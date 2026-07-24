package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import java.util.List;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

public class BaseQiraPage {
    protected static final String COMPOSE_VIEW_CLASS =
            "androidx.compose.ui.platform.AndroidComposeView";
    private static final String PERMISSION_CONTROLLER_PACKAGE = "com.android.permissioncontroller";
    private static final String PERMISSION_CONTROLLER_GOOGLE_PACKAGE =
            "com.google.android.permissioncontroller";
    private static final String[] PERMISSION_ALLOW_RESOURCES = {
            "com.android.permissioncontroller:id/permission_allow_all_button",
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.android.permissioncontroller:id/permission_allow_button",
            "com.android.permissioncontroller:id/permission_allow_always_button",
            "com.android.permissioncontroller:id/permission_allow_one_time_button"
    };

    protected final UiDevice mDevice;
    protected final AvikUtility mUtils;
    protected final QiraConfig mConfig;

    public BaseQiraPage(UiDevice device, QiraConfig config) throws Exception {
        mDevice = device;
        mUtils = AvikUtility.getInstance();
        mConfig = config;
    }

    /**
     * Inter-token glue accepting all whitespace plus the common Unicode
     * bidirectional-formatting control characters. Qira's Compose/Text
     * renderer wraps LTR product names ("Motorola Qira") embedded in
     * RTL copy with U+2066/U+2067/U+2068 (…isolate starts) and U+2069
     * (pop directional isolate), and sometimes injects U+200E/U+200F
     * (LRM/RLM) at language-direction transitions. These characters
     * are invisible on screen but appear in view.getText() and break
     * naive substring regex matches. Allowing them between tokens
     * keeps literal English / rendered-template aliases matchable in
     * RTL locales.
     */
    private static final String BIDI_WHITESPACE_CLASS =
            "[\\s\\u00A0\\u200E\\u200F\\u202A-\\u202E\\u2066-\\u2069]+";

    /**
     * Same character set as {@link #BIDI_WHITESPACE_CLASS} but matched
     * zero-or-more times, used for padding at the ends of an exact
     * pattern (leading / trailing whitespace or bidi isolate markers
     * injected by the renderer).
     */
    private static final String BIDI_WHITESPACE_CLASS_OPT =
            "[\\s\\u00A0\\u200E\\u200F\\u202A-\\u202E\\u2066-\\u2069]*";

    // (?is) = case-insensitive + DOTALL. DOTALL is required because Qira
    // banner copy frequently contains embedded newlines in non-English
    // locales (e.g. pt-BR splits the intro banner as "Olá, meu nome é
    // Qira da Motorola\n\n. Sou sua assistente de inteligência
    // artificial pessoal."). UiAutomator's By.text(Pattern) / By.desc(Pattern)
    // call Matcher.matches(), which demands the WHOLE string matches the
    // regex; without DOTALL the leading / trailing `.*` cannot cross
    // newlines, so a substring that sits between two `\n`s can never match
    // even when it is genuinely present in the text.
    protected Pattern patternForLabel(String label) {
        return Pattern.compile("(?is).*" + buildTokenizedLiteral(label) + ".*");
    }

    protected Pattern exactPatternForLabel(String label) {
        return Pattern.compile("(?is)^"
                + BIDI_WHITESPACE_CLASS_OPT
                + buildTokenizedLiteral(label)
                + BIDI_WHITESPACE_CLASS_OPT
                + "$");
    }

    /**
     * Quote {@code label} for use inside a regex while allowing bidi
     * formatting characters and extra whitespace between whitespace-
     * separated tokens. Single-token labels are quoted verbatim — the
     * relaxed join only kicks in when the caller-supplied anchor
     * already spans multiple words.
     */
    private static String buildTokenizedLiteral(String label) {
        if (label == null || label.isEmpty()) {
            return Pattern.quote("");
        }
        String[] tokens = label.split("\\s+");
        StringBuilder sb = new StringBuilder(label.length() + 32);
        boolean first = true;
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (!first) {
                sb.append(BIDI_WHITESPACE_CLASS);
            }
            sb.append(Pattern.quote(token));
            first = false;
        }
        if (first) {
            return Pattern.quote(label);
        }
        return sb.toString();
    }

    /**
     * Expand each caller-supplied English anchor into its locale-aware alias
     * set (English original + current-locale translation[s]) using
     * {@link QiraStrings}. The returned array is what all {@code findBy*}
     * helpers here match against, so every subclass automatically picks up
     * locale support without changing its hardcoded label arrays.
     */
    protected String[] localizeLabels(String... labels) {
        if (labels == null || labels.length == 0) {
            return new String[0];
        }
        try {
            return QiraStrings.expandAll(labels);
        } catch (Throwable t) {
            return labels;
        }
    }

    protected String[] localizeStringIds(String... stringIds) {
        if (stringIds == null || stringIds.length == 0) {
            return new String[0];
        }
        try {
            return QiraStrings.getInstance().resolveQiraResourceNames(stringIds);
        } catch (Throwable t) {
            return new String[0];
        }
    }

    protected UiObject2 findByText(String... labels) {
        for (String label : localizeLabels(labels)) {
            // Scope every raw text/desc lookup to the Qira package so a
            // stray system toast or permission dialog hosted in another
            // package can never win the match. See the locator-strategy
            // rule set in docs — pkg + clazz/selector beats a naked By.text.
            UiObject2 object = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).text(patternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    protected UiObject2 findByDescription(String... labels) {
        for (String label : localizeLabels(labels)) {
            UiObject2 object = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).desc(patternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    protected UiObject2 findByExactText(String... labels) {
        for (String label : localizeLabels(labels)) {
            UiObject2 object = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).text(exactPatternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    protected UiObject2 findByExactDescription(String... labels) {
        for (String label : localizeLabels(labels)) {
            UiObject2 object = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * Rule-set order: prefer {@link By#desc} over {@link By#text} for
     * multilingual apps. Many Qira widgets surface a stable, English
     * content-description even while their visible label flips per
     * locale, so trying description first short-circuits most locale-
     * dependent text mismatches and avoids spurious text scans when we
     * have a direct accessibility anchor.
     */
    protected UiObject2 findByTextOrDescription(String... labels) {
        UiObject2 object = findByDescription(labels);
        if (object != null) {
            return object;
        }
        return findByText(labels);
    }

    protected UiObject2 findByExactTextOrDescription(String... labels) {
        UiObject2 object = findByExactDescription(labels);
        if (object != null) {
            return object;
        }
        return findByExactText(labels);
    }

    protected UiObject2 findByStringId(String... stringIds) {
        String[] labels = localizeStringIds(stringIds);
        if (labels.length == 0) {
            return null;
        }
        return findByTextOrDescription(labels);
    }

    protected UiObject2 findByExactStringId(String... stringIds) {
        String[] labels = localizeStringIds(stringIds);
        if (labels.length == 0) {
            return null;
        }
        return findByExactTextOrDescription(labels);
    }

    protected boolean hasTextOrDescription(String... labels) {
        return findByTextOrDescription(labels) != null;
    }

    protected UiObject2 waitForTextOrDescription(long timeoutMs, String... labels) throws Exception {
        return waitForTextOrDescription(timeoutMs, true, false, labels);
    }

    protected UiObject2 waitForTextOrDescriptionNoAuto(long timeoutMs, String... labels)
            throws Exception {
        return waitForTextOrDescription(timeoutMs, false, false, labels);
    }

    protected UiObject2 waitForExactTextOrDescription(long timeoutMs, String... labels)
            throws Exception {
        return waitForTextOrDescription(timeoutMs, true, true, labels);
    }

    protected UiObject2 waitForExactTextOrDescriptionNoAuto(long timeoutMs, String... labels)
            throws Exception {
        return waitForTextOrDescription(timeoutMs, false, true, labels);
    }

    private UiObject2 waitForTextOrDescription(long timeoutMs,
            boolean autoHandlePermissionPrompt,
            boolean exactMatch,
            String... labels) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        UiObject2 object;
        while (System.currentTimeMillis() < deadline) {
            if (autoHandlePermissionPrompt && handleSystemPermissionPrompt()) {
                continue;
            }
            object = exactMatch
                    ? findByExactTextOrDescription(labels)
                    : findByTextOrDescription(labels);
            if (object != null) {
                return object;
            }
            mUtils.sleep(250L);
        }
        return null;
    }

    protected UiObject2 waitForClass(String className, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        UiObject2 object;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            // pkg + clazz, not naked clazz — keeps us from accidentally
            // grabbing a class match inside a system surface layered
            // above the Qira window.
            object = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).clazz(className));
            if (object != null) {
                return object;
            }
            mUtils.sleep(250L);
        }
        return null;
    }

    protected boolean clickByTextOrDescription(String... labels) throws Exception {
        UiObject2 object = waitForTextOrDescription(3000L, labels);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByExactTextOrDescription(String... labels) throws Exception {
        UiObject2 object = waitForExactTextOrDescription(3000L, labels);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByStringId(String... stringIds) throws Exception {
        UiObject2 object = findByStringId(stringIds);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByExactStringId(String... stringIds) throws Exception {
        UiObject2 object = findByExactStringId(stringIds);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByExactTextOrDescriptionNoAuto(String... labels) throws Exception {
        UiObject2 object = waitForExactTextOrDescriptionNoAuto(3000L, labels);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByDescription(String... labels) throws Exception {
        long deadline = System.currentTimeMillis() + 3000L;
        UiObject2 object;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            object = findByDescription(labels);
            if (object != null) {
                clickObject(object);
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    protected boolean scrollTextIntoView(String... labels) throws Exception {
        for (String label : localizeLabels(labels)) {
            try {
                if (mUtils.createScrollable().scrollTextIntoView(label)) {
                    settle();
                    return true;
                }
            } catch (Exception ignored) {
                // Some surfaces are not UiScrollable-backed; fall back to direct lookup only.
            }
        }
        return false;
    }

    protected boolean clickByTextOrDescriptionWithScroll(String... labels) throws Exception {
        if (clickByTextOrDescription(labels)) {
            return true;
        }

        if (!scrollTextIntoView(labels)) {
            return false;
        }

        return clickByTextOrDescription(labels);
    }

    protected UiObject2 findByResource(String... resourceIds) {
        for (String resourceId : resourceIds) {
            UiObject2 object = mDevice.findObject(By.res(resourceId));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * Matches dump-proven semantic content descriptions exactly, without
     * locale expansion. Use this only for accessibility anchors that behave
     * like stable IDs across Qira locales (for example icon controls such as
     * Menu, Send, More options, and Add image).
     */
    protected UiObject2 findByStableDescription(String... descriptions) {
        if (descriptions == null || descriptions.length == 0) {
            return null;
        }
        for (String description : descriptions) {
            if (description == null || description.isEmpty()) {
                continue;
            }
            UiObject2 object = mDevice.findObject(By.pkg(mConfig.getPackageName())
                    .desc(exactPatternForLabel(description)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    protected boolean clickByStableDescription(String... descriptions) throws Exception {
        UiObject2 object = findByStableDescription(descriptions);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickByResource(String... resourceIds) throws Exception {
        UiObject2 object = findByResource(resourceIds);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    protected boolean clickTopLeftClickable() throws Exception {
        // Rule-set #2: never leave selectors unscoped. Callers of this helper
        // are always inside the Qira surface (e.g. openDrawer fallback when the
        // stable "Menu" description is missing), so restricting the candidate
        // pool to the Qira package keeps us from grabbing an overlaying
        // launcher / status-bar clickable that happens to live in the same
        // geometric band.
        List<UiObject2> objects = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true));
        UiObject2 bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        int maxX = mDevice.getDisplayWidth() / 3;
        int maxY = mDevice.getDisplayHeight() / 4;

        for (UiObject2 object : objects) {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            if (centerX > maxX || centerY > maxY) {
                continue;
            }

            int score = centerX + centerY;
            if (score < bestScore) {
                bestScore = score;
                bestMatch = object;
            }
        }

        if (bestMatch == null) {
            return false;
        }

        clickObject(bestMatch);
        return true;
    }

    /**
     * RTL mirror of {@link #clickTopLeftClickable()}: taps the top-<b>right</b>
     * Qira clickable. On RTL locales the home top bar mirrors, so the
     * navigation-drawer "Menu" affordance sits at the top-right while the
     * profile avatar takes the top-left. The LTR top-left fallback therefore
     * taps the avatar (opening Account) instead of the drawer; callers on RTL
     * must use this mirror. Kept package-scoped like the LTR variant so system
     * chrome cannot hijack the match.
     */
    protected boolean clickTopRightClickable() throws Exception {
        List<UiObject2> objects = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true));
        UiObject2 bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        int displayWidth = mDevice.getDisplayWidth();
        int minX = (displayWidth * 2) / 3;
        int maxY = mDevice.getDisplayHeight() / 4;

        for (UiObject2 object : objects) {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            if (centerX < minX || centerY > maxY) {
                continue;
            }

            // Closest to the top-right corner (displayWidth, 0).
            int score = (displayWidth - centerX) + centerY;
            if (score < bestScore) {
                bestScore = score;
                bestMatch = object;
            }
        }

        if (bestMatch == null) {
            return false;
        }

        clickObject(bestMatch);
        return true;
    }

    protected boolean clickBottomRightClickable() throws Exception {
        // Rule-set #2: mirror clickTopLeftClickable() and keep the geometric
        // fan-out inside the Qira package so system chrome (nav-bar gesture
        // handles, floating system toasts) cannot hijack the match.
        List<UiObject2> objects = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true));
        UiObject2 bestMatch = null;
        int bestScore = Integer.MIN_VALUE;
        int minX = (mDevice.getDisplayWidth() * 2) / 3;
        int minY = (mDevice.getDisplayHeight() * 2) / 3;

        for (UiObject2 object : objects) {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            if (centerX < minX || centerY < minY) {
                continue;
            }

            int score = centerX + centerY;
            if (score > bestScore) {
                bestScore = score;
                bestMatch = object;
            }
        }

        if (bestMatch == null) {
            return false;
        }

        clickObject(bestMatch);
        return true;
    }

    protected void clickObject(UiObject2 object) throws Exception {
        UiObject2 clickableTarget = object;
        while (clickableTarget != null && !clickableTarget.isClickable()) {
            clickableTarget = clickableTarget.getParent();
        }

        Rect bounds = clickableTarget != null
                ? clickableTarget.getVisibleBounds()
                : object.getVisibleBounds();
        mDevice.click(bounds.centerX(), bounds.centerY());
        settle();
    }

    protected void tap(int x, int y) throws Exception {
        mDevice.click(x, y);
        settle();
    }

    protected boolean handleSystemPermissionPrompt() throws Exception {
        String currentPackage = mDevice.getCurrentPackageName();
        if (!PERMISSION_CONTROLLER_PACKAGE.equals(currentPackage)
                && !PERMISSION_CONTROLLER_GOOGLE_PACKAGE.equals(currentPackage)
                && findByResource(PERMISSION_ALLOW_RESOURCES) == null) {
            return false;
        }

        UiObject2 allowButton = findByResource(PERMISSION_ALLOW_RESOURCES);
        if (allowButton == null) {
            allowButton = findByExactTextOrDescription(
                    "Allow all",
                    "While using the app",
                    "Allow only while using the app",
                    "Only this time",
                    "Allow",
                    "OK",
                    "Start now");
        }

        if (allowButton == null) {
            return false;
        }

        allowButton.click();
        settle();

        long deadline = System.currentTimeMillis() + 5000L;
        while (System.currentTimeMillis() < deadline) {
            String resumedPackage = mDevice.getCurrentPackageName();
            if (mConfig.getPackageName().equals(resumedPackage)) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }

        settle();
        return true;
    }

    protected void settle() throws Exception {
        mUtils.sleep(1000L);
    }
}
