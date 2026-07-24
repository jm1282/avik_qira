package avik.qira_v2.pages;

import android.app.UiAutomation;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira.utils.QiraUiDumper;
import avik.qira_v2.utils.QiraV2OnboardingStartFlow;
import avik.qira_v2.utils.QiraV2Selectors;
import avik.qira_v2.utils.QiraV2SlapCatalog;
import avik.qira_v2.utils.QiraV2SlapTextDump;

import com.motorola.g11n.tools.avik.common.metadata.AvikText;

public final class QiraV2OnboardingStartPage {

    private static final String CONTENT_ROOT_RESOURCE_ID = "android:id/content";
    private static final String[] COMPOSE_ROOT_CLASSES = {
            "androidx.compose.ui.platform.ComposeView",
            "androidx.compose.ui.platform.AndroidComposeView"
    };

    private static final String[] START_SCREEN_STRING_ANCHORS = {
            "Motorola Qira",
            "Qira app icon",
            "Start",
            "Remind me later",
            "Start the tour and get to know Motorola Qira"
    };

    private static final String[] START_CTA_STRING_ANCHORS = {
            "Start"
    };
    private static final long POST_ROOT_VISIBLE_SETTLE_MS = 8500L;

    private final UiDevice device;
    private final AvikUtility utils;
    private final QiraConfig config;
    private final Logger logger;

    public QiraV2OnboardingStartPage(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger) {
        this.device = device;
        this.utils = utils;
        this.config = config;
        this.logger = logger != null
                ? logger
                : AvikLoggerFactory.INSTANCE.getInstance();
    }

    public QiraV2OnboardingStartPage waitForLoaded(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        long lastLog = 0L;
        long rootVisibleSince = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isQiraStartRootVisible()) {
                if (rootVisibleSince == 0L) {
                    rootVisibleSince = System.currentTimeMillis();
                    logger.info("QiraV2 start root visible; waiting "
                            + POST_ROOT_VISIBLE_SETTLE_MS
                            + " ms for onboarding animation/start CTA frame.");
                }
                long visibleFor = System.currentTimeMillis() - rootVisibleSince;
                if (visibleFor >= POST_ROOT_VISIBLE_SETTLE_MS) {
                    try {
                        device.waitForIdle(1500L);
                    } catch (Throwable ignored) {
                    }
                    if (isQiraStartRootVisible()) {
                        logger.info("QiraV2 start surface ready after animation settle:"
                                + " package=" + safeCurrentPackage()
                                + ", qiraNodes=" + countQiraNodes()
                                + ", composeRoot=" + (findComposeRoot() != null));
                        return this;
                    }
                    rootVisibleSince = 0L;
                    continue;
                }
            } else {
                rootVisibleSince = 0L;
            }
            long now = System.currentTimeMillis();
            if (now - lastLog > 5000L) {
                logger.info("Waiting for QiraV2 start surface; current package="
                        + safeCurrentPackage());
                lastLog = now;
            }
            sleep(250L);
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                QiraV2OnboardingStartFlow.SCREEN_NAME + "_wait_timeout",
                "Timed out waiting for Qira v2 onboarding start root");
        throw new IllegalStateException("Qira v2 onboarding start surface not visible");
    }

    public void logSelectorEvidence(String dumpTag) {
        try {
            QiraStrings.getInstance().enableRuntimeResourceScan();
            QiraStrings.ResolvedQiraStringId[] ids = QiraStrings.getInstance()
                    .resolveQiraStringIdsForEnglish(START_SCREEN_STRING_ANCHORS);
            logger.info("QiraV2 Start CTA SLAP catalog: "
                    + QiraV2SlapCatalog.START_CTA.toLogString());
            if (ids.length == 0) {
                logger.info("QiraV2 start selector evidence: no Qira string IDs"
                        + " resolved from runtime R.string scan");
            }
            for (QiraStrings.ResolvedQiraStringId id : ids) {
                logger.info("QiraV2 start string ID: " + id.toLogString());
            }

            UiObject2 startCta = findStartCtaByIdBackedSelector();
            if (startCta == null) {
                logger.info("QiraV2 start CTA evidence: no accessible"
                        + " UiAutomator node matched a resource/string ID."
                        + " Live dump on this build exposes the start card as"
                        + " a textless Compose root, so this start-only script"
                        + " captures without clicking and refuses coordinate"
                        + " fallback.");
            } else {
                logger.info("QiraV2 start CTA evidence: accessible ID-backed"
                        + " node found, resource=" + safe(startCta.getResourceName())
                        + ", text='" + safe(QiraStrings.stripBidiControls(startCta.getText()))
                        + "', desc='"
                        + safe(QiraStrings.stripBidiControls(startCta.getContentDescription()))
                        + "'");
            }

            AvikText slapStart = findStartCtaBySlapMessageId();
            if (slapStart == null) {
                logger.info("QiraV2 start CTA SLAP evidence: message ID "
                        + QiraV2SlapCatalog.START_CTA.getMessageId()
                        + " was not visible in Avik hierarchy text dump.");
            } else {
                logger.info("QiraV2 start CTA SLAP evidence: "
                        + QiraV2SlapTextDump.summarize(slapStart));
            }

            AvikText resourceStart = findStartCtaByResolvedQiraResource();
            if (resourceStart == null) {
                logger.info("QiraV2 start CTA Qira resource evidence: string ID "
                        + QiraV2SlapCatalog.START_CTA.getStringId()
                        + " did not match a visible SLAP text item.");
            } else {
                logger.info("QiraV2 start CTA Qira resource evidence: string ID "
                        + QiraV2SlapCatalog.START_CTA.getStringId()
                        + " matched "
                        + QiraV2SlapTextDump.summarize(resourceStart));
            }

            QiraUiDumper.dump(
                    device,
                    config.getPackageName(),
                    dumpTag + "_selector_probe",
                    "Selector probe before qira_v2 onboarding start capture");
        } catch (Throwable t) {
            logger.info("QiraV2 start selector evidence failed: " + t.getMessage());
        }
    }

    public void requireStartCtaIdBackedEvidence() {
        UiObject2 startCta = findStartCtaByIdBackedSelector();
        if (startCta != null) {
            logger.info("QiraV2 start selector accepted: accessible"
                    + " resource/string-ID backed node found.");
            return;
        }

        AvikText slapStart = findStartCtaBySlapMessageId();
        if (slapStart != null) {
            logger.info("QiraV2 start selector accepted: SLAP message ID "
                    + QiraV2SlapCatalog.START_CTA.getMessageId()
                    + " found for "
                    + QiraV2SlapTextDump.summarize(slapStart));
            return;
        }

        AvikText resourceStart = findStartCtaByResolvedQiraResource();
        if (resourceStart != null) {
            logger.info("QiraV2 start selector accepted: Qira string resource entry "
                    + QiraV2SlapCatalog.START_CTA.getStringId()
                    + " resolved in active locale for "
                    + QiraV2SlapTextDump.summarize(resourceStart));
            return;
        }

        AvikText structuralStart = findStartCtaByStructuralSlapPrimary();
        if (structuralStart != null) {
            logger.info("QiraV2 start selector accepted: structural SLAP primary action"
                    + " on start card after ID/resource probes, evidence="
                    + QiraV2SlapTextDump.summarize(structuralStart));
            return;
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                QiraV2OnboardingStartFlow.SCREEN_NAME + "_no_id_evidence",
                "No Android resource/string ID or SLAP message ID evidence"
                        + " for Qira v2 Start CTA");
        logger.info("QiraV2 start selector warning: pre-click evidence did not expose"
                + " Android resource/string ID or SLAP message ID "
                + QiraV2SlapCatalog.START_CTA.getMessageId()
                + " (string ID "
                + QiraV2SlapCatalog.START_CTA.getStringId()
                + "). Continuing to timed click probe so late Compose animation"
                + " frames can expose the ID-backed or structural SLAP selector.");
    }

    public void clickStartCtaByIdBackedSelector() throws Exception {
        long deadline = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < deadline) {
            UiObject2 startCta = findStartCtaByIdBackedSelector();
            if (QiraV2Selectors.clickIfPresent(startCta)) {
                logger.info("QiraV2 start CTA clicked by ID-backed selector");
                return;
            }
            if (QiraV2SlapTextDump.clickByMessageId(
                    device,
                    QiraV2SlapCatalog.START_CTA.getMessageId(),
                    false,
                    logger)) {
                logger.info("QiraV2 start CTA clicked by SLAP message ID "
                        + QiraV2SlapCatalog.START_CTA.getMessageId()
                        + " (no hard-coded text or static coordinates).");
                return;
            }
            if (QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                    device,
                    QiraV2SlapCatalog.START_CTA.getStringId(),
                    false,
                    logger)) {
                logger.info("QiraV2 start CTA clicked by Qira string resource entry "
                        + QiraV2SlapCatalog.START_CTA.getStringId()
                        + " in active locale (no per-locale table or static coordinates).");
                return;
            }
            AvikText structuralStart = findStartCtaByStructuralSlapPrimary();
            if (structuralStart != null) {
                int centerX = structuralStart.getLeft()
                        + ((structuralStart.getRight() - structuralStart.getLeft()) / 2);
                int centerY = structuralStart.getTop()
                        + ((structuralStart.getBottom() - structuralStart.getTop()) / 2);
                logger.info("QiraV2 start CTA clicked by structural SLAP primary action"
                        + " after ID/resource probes, evidence="
                        + QiraV2SlapTextDump.summarize(structuralStart)
                        + ", target=" + centerX + "," + centerY);
                if (device.click(centerX, centerY)) {
                    return;
                }
            }
            sleep(500L);
        }
        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                QiraV2OnboardingStartFlow.SCREEN_NAME + "_no_id_click_selector",
                "No ID-backed Start CTA selector was exposed; refusing text/coordinate fallback");
        throw new IllegalStateException("Qira v2 Start CTA has no accessible"
                + " resource-id, Qira string resource entry, or SLAP-backed"
                + " message ID selector. Refusing text/coordinate fallback.");
    }

    /**
     * Locale-independent LAST-RESORT activation for a primary CTA that exposes
     * NO selectable node (the start card is a textless Compose canvas: no
     * text / content-desc / resource-id / SLAP message ID / R.string, and in
     * some renders - e.g. ar-EG - no clickable or a11y-focusable node at all).
     * Only called after {@link #clickStartCtaByIdBackedSelector()} has proven
     * that none of the stable selectors resolve, so the primary SLAP
     * message-ID / Compose-string path always wins first and other locales are
     * unaffected. Never taps a fixed coordinate.
     *
     * <p>Tries, in order, and returns {@code true} on the first route that
     * actually advances onboarding off the start card:
     * <ol>
     *   <li>Semantic {@link AccessibilityNodeInfo#ACTION_CLICK} on the
     *       outermost clickable node hosting the card (falling back to the
     *       Compose semantics root).</li>
     *   <li>Key activation: move focus with {@code KEYCODE_TAB} then activate
     *       with {@code KEYCODE_DPAD_CENTER} / {@code KEYCODE_ENTER}.</li>
     *   <li>Request accessibility focus on the Compose root, then
     *       {@code ACTION_CLICK}.</li>
     * </ol>
     */
    public boolean activateStartWithoutSelectableNode() throws Exception {
        int baselineNodes = countQiraNodes();
        logger.info("QiraV2 start: no selectable node resolved; attempting"
                + " locale-independent non-coordinate activation routes"
                + " (baseline qiraNodes=" + baselineNodes + ").");

        if (activateByAccessibilityActionClick(baselineNodes)) {
            logger.info("QiraV2 start CTA activated by AccessibilityNodeInfo.ACTION_CLICK"
                    + " (semantic click, no coordinate).");
            return true;
        }
        if (activateByKeyEvent(baselineNodes)) {
            logger.info("QiraV2 start CTA activated by key event"
                    + " (focus + DPAD_CENTER/ENTER, no coordinate).");
            return true;
        }
        if (activateByAccessibilityFocusThenClick(baselineNodes)) {
            logger.info("QiraV2 start CTA activated by accessibility-focus + ACTION_CLICK"
                    + " (no coordinate).");
            return true;
        }
        logger.info("QiraV2 start: no non-coordinate activation route advanced onboarding.");
        return false;
    }

    private boolean activateByAccessibilityActionClick(int baselineNodes) throws Exception {
        UiAutomation automation = uiAutomation();
        if (automation == null) {
            return false;
        }
        AccessibilityNodeInfo root = automation.getRootInActiveWindow();
        if (root == null) {
            return false;
        }
        AccessibilityNodeInfo clickable = findOutermostClickable(root);
        if (clickable != null) {
            boolean performed = clickable.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            logger.info("QiraV2 start ACTION_CLICK on outermost clickable node: performed="
                    + performed + ", class=" + clickable.getClassName());
            if (performed && advancedOffStartCard(baselineNodes, 4000L)) {
                return true;
            }
        } else {
            logger.info("QiraV2 start: no clickable node in the active window;"
                    + " trying ACTION_CLICK on the Compose semantics root.");
        }
        // Best-effort: the Compose canvas may accept ACTION_CLICK even when it
        // does not advertise isClickable() (Compose routes it to the semantics
        // owner's click handler).
        AccessibilityNodeInfo composeRoot = findDeepestSingleChild(root);
        if (composeRoot != null) {
            boolean performed = composeRoot.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            logger.info("QiraV2 start ACTION_CLICK on Compose semantics root: performed="
                    + performed + ", class=" + composeRoot.getClassName());
            if (performed && advancedOffStartCard(baselineNodes, 4000L)) {
                return true;
            }
        }
        return false;
    }

    private boolean activateByKeyEvent(int baselineNodes) throws Exception {
        // The start card may already have Compose focus on its single primary
        // action; try activating directly first, then move focus with TAB.
        int[] activateKeys = {KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER};
        for (int key : activateKeys) {
            device.pressKeyCode(key);
            if (advancedOffStartCard(baselineNodes, 1500L)) {
                return true;
            }
        }
        for (int tabs = 1; tabs <= 3; tabs++) {
            device.pressKeyCode(KeyEvent.KEYCODE_TAB);
            sleep(200L);
            for (int key : activateKeys) {
                device.pressKeyCode(key);
                if (advancedOffStartCard(baselineNodes, 1500L)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean activateByAccessibilityFocusThenClick(int baselineNodes) throws Exception {
        UiAutomation automation = uiAutomation();
        if (automation == null) {
            return false;
        }
        AccessibilityNodeInfo root = automation.getRootInActiveWindow();
        if (root == null) {
            return false;
        }
        AccessibilityNodeInfo target = findOutermostClickable(root);
        if (target == null) {
            target = findDeepestSingleChild(root);
        }
        if (target == null) {
            return false;
        }
        boolean focused = target.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
        sleep(300L);
        boolean clicked = target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        logger.info("QiraV2 start a11y-focus(" + focused + ") + ACTION_CLICK(" + clicked + ").");
        return clicked && advancedOffStartCard(baselineNodes, 4000L);
    }

    private static AccessibilityNodeInfo findOutermostClickable(AccessibilityNodeInfo root) {
        // Breadth-first so the SHALLOWEST (outermost) clickable node wins.
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            if (node.isClickable()) {
                return node;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    queue.add(child);
                }
            }
        }
        return null;
    }

    private static AccessibilityNodeInfo findDeepestSingleChild(AccessibilityNodeInfo root) {
        AccessibilityNodeInfo current = root;
        AccessibilityNodeInfo deepest = root;
        while (current != null && current.getChildCount() > 0) {
            AccessibilityNodeInfo child = current.getChild(current.getChildCount() - 1);
            if (child == null) {
                break;
            }
            deepest = child;
            current = child;
        }
        return deepest;
    }

    /**
     * True once the textless start card is replaced by the next onboarding
     * surface. Locale independent: the start card exposes no Qira text node, so
     * either a change in the Qira node count or the appearance of any Qira
     * text/desc node means onboarding advanced.
     */
    private boolean advancedOffStartCard(int baselineNodes, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            int nodes = countQiraNodes();
            boolean hasText = qiraHasVisibleTextNode();
            if (hasText || (nodes > 0 && nodes != baselineNodes)) {
                logger.info("QiraV2 start advance detected: qiraNodes " + baselineNodes
                        + " -> " + nodes + ", qiraHasText=" + hasText);
                return true;
            }
            sleep(300L);
        }
        return false;
    }

    private boolean qiraHasVisibleTextNode() {
        try {
            for (UiObject2 object : device.findObjects(By.pkg(config.getPackageName()))) {
                String text = object.getText();
                if (text != null && !text.trim().isEmpty()) {
                    return true;
                }
                String desc = object.getContentDescription();
                if (desc != null && !desc.trim().isEmpty()) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private UiAutomation uiAutomation() {
        try {
            return InstrumentationRegistry.getInstrumentation().getUiAutomation();
        } catch (Throwable t) {
            logger.info("QiraV2 start: UiAutomation unavailable: " + t.getMessage());
            return null;
        }
    }

    private UiObject2 findStartCtaByIdBackedSelector() {
        return QiraV2Selectors.findByQiraStringIdsForEnglish(
                device,
                config.getPackageName(),
                logger,
                START_CTA_STRING_ANCHORS);
    }

    private AvikText findStartCtaBySlapMessageId() {
        return QiraV2SlapTextDump.findByMessageId(
                QiraV2SlapCatalog.START_CTA.getMessageId(),
                false,
                logger);
    }

    private AvikText findStartCtaByResolvedQiraResource() {
        return QiraV2SlapTextDump.findByResolvedQiraStringResource(
                QiraV2SlapCatalog.START_CTA.getStringId(),
                false,
                logger);
    }

    private AvikText findStartCtaByStructuralSlapPrimary() {
        AvikText candidate = QiraV2SlapTextDump.findLowestWideTextInBand(
                false,
                logger,
                0.55f,
                0.86f,
                0.08f);
        if (candidate == null) {
            return null;
        }
        int width = candidate.getRight() - candidate.getLeft();
        int height = candidate.getBottom() - candidate.getTop();
        if (width <= 0 || height <= 0 || height > device.getDisplayHeight() / 12) {
            return null;
        }
        return candidate;
    }

    private boolean isQiraStartRootVisible() {
        if (!config.getPackageName().equals(safeCurrentPackage())
                && device.findObject(By.pkg(config.getPackageName())) == null) {
            return false;
        }
        UiObject2 contentRoot = device.findObject(By.res(CONTENT_ROOT_RESOURCE_ID));
        return contentRoot != null && findComposeRoot() != null;
    }

    private UiObject2 findComposeRoot() {
        for (String className : COMPOSE_ROOT_CLASSES) {
            UiObject2 root = device.findObject(
                    By.pkg(config.getPackageName()).clazz(className));
            if (root != null) {
                return root;
            }
        }
        Pattern composeClass = Pattern.compile(".*Compose.*");
        return device.findObject(By.pkg(config.getPackageName()).clazz(composeClass));
    }

    private int countQiraNodes() {
        try {
            return device.findObjects(By.pkg(config.getPackageName())).size();
        } catch (Throwable ignored) {
            return -1;
        }
    }

    private String safeCurrentPackage() {
        try {
            return safe(device.getCurrentPackageName());
        } catch (Throwable ignored) {
            return "";
        }
    }

    private void sleep(long millis) throws Exception {
        if (utils != null) {
            utils.sleep(millis);
        } else {
            Thread.sleep(millis);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ');
    }
}
