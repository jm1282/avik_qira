package avik.qira_v2.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.common.metadata.AvikText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira.utils.QiraUiDumper;
import avik.qira_v2.utils.QiraV2ComposeStrings;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/**
 * qira_v2 Focus Zone selector + navigation helper.
 *
 * <p>Mirrors the surface coverage of the legacy
 * {@code avik.qira.pages.QiraFocusZonePage} but replaces its visible-text /
 * geometry selectors with the qira_v2 stable-ID strategy:
 *
 * <ol>
 *   <li>The Focus Zone <b>bubble bar</b> is an accessibility overlay whose
 *       bubbles expose {@code content-desc}s; they are resolved from English
 *       anchors to the Qira string-resource value via
 *       {@link QiraV2Selectors#findByQiraStringIdsForEnglish} (locale
 *       independent) and clicked through UiAutomator.</li>
 *   <li>The Focus Zone <b>content sheets</b> (Chat / Live / Catch me up / Pay
 *       Attention) render as Compose surfaces with no UiAutomator semantics, so
 *       they are detected and clicked through Avik SLAP
 *       ({@link QiraV2SlapTextDump#findByResolvedQiraStringResource} /
 *       {@link QiraV2SlapTextDump#clickByResolvedQiraStringResource}) using the
 *       Qira string-resource IDs resolved from the same English anchors.</li>
 *   <li>Android runtime permission dialogs are handled by their stable
 *       {@code com.android.permissioncontroller:id/...} resource IDs.</li>
 * </ol>
 *
 * <p>No method uses a static coordinate or a hard-coded localized string as the
 * primary selector. When no stable selector resolves, the caller is expected to
 * fail loudly with a {@link QiraUiDumper} dump.
 */
public final class QiraV2FocusZonePage {

    public static final String PERMISSION_CONTROLLER_PACKAGE =
            "com.android.permissioncontroller";
    public static final String PERMISSION_CONTROLLER_GOOGLE_PACKAGE =
            "com.google.android.permissioncontroller";

    private static final String[] PERMISSION_LOCATION_FINE_RESOURCES = {
            "com.android.permissioncontroller:id/permission_location_accuracy_radio_fine",
            "com.google.android.permissioncontroller:id/permission_location_accuracy_radio_fine"
    };
    private static final String[] PERMISSION_ALLOW_FOREGROUND_RESOURCES = {
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.google.android.permissioncontroller:id/permission_allow_foreground_only_button"
    };
    private static final String[] PERMISSION_ALLOW_RESOURCES = {
            "com.android.permissioncontroller:id/permission_allow_button",
            "com.google.android.permissioncontroller:id/permission_allow_button",
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.google.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.android.permissioncontroller:id/permission_allow_one_time_button",
            "com.google.android.permissioncontroller:id/permission_allow_one_time_button",
            "com.android.permissioncontroller:id/permission_allow_all_button",
            "com.google.android.permissioncontroller:id/permission_allow_all_button"
    };

    // Focus Zone bubble-bar entries. The bubbles render as a textless Compose
    // bar (no UiAutomator semantics), but Avik SLAP exposes each with a stable
    // message ID (discovered live in en-XM on this build). English anchors are
    // kept as a secondary, Qira string-resource backed fallback.
    public static final Bubble BUBBLE_APP_ICON =
            new Bubble("App Icon", "6DwUBp1IvZJf9vvxzSsiMi", "cd_app_icon", "App Icon", "Qira");
    public static final Bubble BUBBLE_CHAT =
            new Bubble("Chat", "rBhe3pvtchIrAQ29tn9v6", "chat", "Chat");
    public static final Bubble BUBBLE_LIVE =
            new Bubble("Live", "14v03xM0T0aKPsfGREtus", "live", "Live");
    public static final Bubble BUBBLE_CATCH_ME_UP =
            new Bubble("Catch me up", "5c6tmHP9csDc2vurD0AMTa", "catch_me_up_edu",
                    "Catch me up", "Catch Me Up");
    public static final Bubble BUBBLE_RECORD =
            new Bubble("Record", "25YNpWC61QKVuuOiMRwmpe",
                    new String[] {"record", "cd_record", "recording", "start_recording", "save"},
                    "Record");
    private static final String LIVE_MODEL_DOWNLOAD_NOTICE_STRING_ID =
            "language_model_download_failed";
    private static final String LIVE_MODEL_DOWNLOAD_CLOSE_STRING_ID =
            "download_close";

    /**
     * A Focus Zone bubble. Identified by its stable SLAP message ID (en-XM fast
     * path) and, for real locales where SLAP carries no message IDs, by its Qira
     * Compose string-resource ID ({@code stringId}) which resolves to the bubble
     * label in the current locale. English anchors are a last-resort fallback.
     */
    public static final class Bubble {
        final String label;
        final String messageId;
        final String[] stringIds;
        final String[] englishAnchors;

        Bubble(String label, String messageId, String stringId, String... englishAnchors) {
            this(label, messageId, new String[] {stringId}, englishAnchors);
        }

        Bubble(String label, String messageId, String[] stringIds, String... englishAnchors) {
            this.label = label;
            this.messageId = messageId;
            this.stringIds = stringIds == null ? new String[0] : stringIds;
            this.englishAnchors = englishAnchors;
        }
    }

    private final UiDevice device;
    private final AvikUtility utils;
    private final QiraConfig config;
    private final Logger logger;
    private final String packageName;

    public QiraV2FocusZonePage(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger) {
        this.device = device;
        this.utils = utils;
        this.config = config;
        this.logger = logger;
        this.packageName = config.getPackageName();
    }

    /**
     * Resolves the Qira string-resource entry names for the given English
     * anchors. The mapping is locale independent: Qira's own string catalog is
     * scanned for entries whose English value matches the anchor.
     */
    private List<String> resolveEntryNames(String... englishAnchors) {
        List<String> names = new ArrayList<>();
        try {
            QiraStrings.ResolvedQiraStringId[] ids =
                    QiraStrings.getInstance().resolveQiraStringIdsForEnglish(englishAnchors);
            for (QiraStrings.ResolvedQiraStringId id : ids) {
                if (id.getEntryName() != null && !id.getEntryName().isEmpty()) {
                    names.add(id.getEntryName());
                }
            }
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 FocusZone resolveEntryNames failed for "
                        + java.util.Arrays.toString(englishAnchors) + ": " + t.getMessage());
            }
        }
        return names;
    }

    /**
     * True when any of the supplied English anchors resolves to a Qira
     * string-resource value visible in the current Avik SLAP dump. Works on the
     * textless Compose content sheets because SLAP reads the rendering
     * hierarchy, not the UiAutomator tree.
     */
    public boolean isSurfaceVisibleBySlap(String... englishAnchors) {
        for (String entryName : resolveEntryNames(englishAnchors)) {
            if (QiraV2SlapTextDump.findByResolvedQiraStringResource(entryName, false, null) != null) {
                return true;
            }
        }
        // Compose bridge: Focus Zone content-sheet copy lives in Qira's Compose
        // catalog, not Android R.string, so the entry-name path above misses it
        // in real locales. Resolve the anchor to its current-locale Compose
        // value and match that on the SLAP surface (locale independent).
        return QiraV2SlapTextDump.findByEnglishAnchorCompose(
                packageName, false, null, englishAnchors) != null;
    }

    /**
     * Polls {@link #isSurfaceVisibleBySlap} until visible or the timeout
     * elapses.
     */
    public boolean waitForSurfaceBySlap(long timeoutMs, String... englishAnchors)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isSurfaceVisibleBySlap(englishAnchors)) {
                return true;
            }
            sleep(250L);
        }
        return false;
    }

    /**
     * Clicks the first English anchor that resolves to a Qira string-resource
     * value clickable in the SLAP dump. Returns {@code false} when none
     * resolves (caller decides whether that is fatal).
     */
    public boolean clickBySlapIfPresent(String... englishAnchors) {
        for (String entryName : resolveEntryNames(englishAnchors)) {
            if (QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                    device, entryName, false, logger)) {
                if (logger != null) {
                    logger.info("QiraV2 FocusZone clicked Qira string resource '" + entryName
                            + "' by SLAP (resolved from English anchor).");
                }
                return true;
            }
        }
        // Compose bridge for Focus Zone content-sheet CTAs (Try it, Enable
        // permission, Start Live, Accept, ...) whose text is a Compose stringId
        // rather than an Android R.string entry - resolve to the current-locale
        // Compose value and click that SLAP node.
        if (QiraV2SlapTextDump.clickByEnglishAnchorCompose(
                device, packageName, false, logger, englishAnchors)) {
            if (logger != null) {
                logger.info("QiraV2 FocusZone clicked Focus Zone CTA by Compose-resolved"
                        + " anchor (locale-independent).");
            }
            return true;
        }
        return false;
    }

    public boolean clickByQiraResourceIfPresent(String... resourceNames) {
        if (resourceNames == null) {
            return false;
        }
        for (String resourceName : resourceNames) {
            if (QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                    device, resourceName, false, logger)) {
                if (logger != null) {
                    logger.info("QiraV2 FocusZone clicked direct Qira string resource '"
                            + resourceName + "' by SLAP.");
                }
                return true;
            }
        }
        return false;
    }

    public boolean clickTrailingControlByQiraResourceIfPresent(String... resourceNames) {
        return QiraV2SlapTextDump.clickTrailingControlForResourceRow(
                device, false, logger, resourceNames);
    }

    public boolean clickTrailingControlByEnglishAnchorIfPresent(String... englishAnchors) {
        List<String> entryNames = resolveEntryNames(englishAnchors);
        if (entryNames.isEmpty()) {
            return false;
        }
        return clickTrailingControlByQiraResourceIfPresent(entryNames.toArray(new String[0]));
    }

    /**
     * Clicks an English-anchored CTA on a Compose content sheet via SLAP, or
     * fails loudly with a UI dump. Use for required CTAs (e.g. I agree, Next).
     */
    public void clickBySlapOrFail(String screenLabel, String... englishAnchors)
            throws Exception {
        if (clickBySlapIfPresent(englishAnchors)) {
            return;
        }
        QiraUiDumper.dump(device, packageName, screenLabel + "_no_slap_selector",
                "No Qira string-resource SLAP selector resolved for anchors "
                        + java.util.Arrays.toString(englishAnchors));
        throw new IllegalStateException("Qira v2 Focus Zone control for "
                + screenLabel + " could not be anchored to a Qira string-resource"
                + " SLAP selector " + java.util.Arrays.toString(englishAnchors)
                + ". Refusing text/coordinate fallback.");
    }

    /**
     * Taps a Focus Zone bubble by its stable SLAP message ID (the bubble bar is
     * a textless Compose surface, so this is the durable path), falling back to
     * the bubble's Qira string-resource value resolved from English anchors.
     */
    public boolean tapBubbleIfPresent(Bubble bubble) {
        if (bubble.messageId != null && !bubble.messageId.isEmpty()
                && QiraV2SlapTextDump.clickByMessageId(device, bubble.messageId, false, logger)) {
            if (logger != null) {
                logger.info("QiraV2 FocusZone tapped '" + bubble.label
                        + "' bubble by SLAP message ID " + bubble.messageId + ".");
            }
            return true;
        }
        for (String stringId : bubble.stringIds) {
            if (stringId != null && !stringId.isEmpty()
                    && QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                            device, stringId, false, logger)) {
                if (logger != null) {
                    logger.info("QiraV2 FocusZone tapped '" + bubble.label
                            + "' bubble by Qira Compose string resource " + stringId
                            + " (locale-independent).");
                }
                return true;
            }
        }
        if (clickBySlapIfPresent(bubble.englishAnchors)) {
            return true;
        }
        if (bubble == BUBBLE_RECORD) {
            return tapRightmostBubbleBarSlot("Record");
        }
        return false;
    }

    public void tapBubbleOrFail(String screenLabel, Bubble bubble) throws Exception {
        if (tapBubbleIfPresent(bubble)) {
            return;
        }
        logSlapInventory("QiraV2 FocusZone bubble miss for " + screenLabel);
        QiraUiDumper.dump(device, packageName, screenLabel + "_no_bubble_selector",
                "Focus Zone bubble '" + bubble.label + "' not found by SLAP message ID "
                        + bubble.messageId + " or Qira string resource "
                        + java.util.Arrays.toString(bubble.englishAnchors));
        throw new IllegalStateException("Qira v2 Focus Zone bubble for "
                + screenLabel + " was not found by SLAP message ID " + bubble.messageId
                + " or Qira string resource / SLAP bar slot fallback.");
    }

    private boolean isBubbleVisible(Bubble bubble) {
        if (bubble.messageId != null && !bubble.messageId.isEmpty()
                && QiraV2SlapTextDump.findByMessageId(bubble.messageId, false, null) != null) {
            return true;
        }
        for (String stringId : bubble.stringIds) {
            if (stringId != null && !stringId.isEmpty()
                    && QiraV2SlapTextDump.findByResolvedQiraStringResource(
                            stringId, false, null) != null) {
                return true;
            }
        }
        if (isSurfaceVisibleBySlap(bubble.englishAnchors)) {
            return true;
        }
        return bubble == BUBBLE_RECORD && findBubbleBarSlot(4) != null;
    }

    /**
     * True when the Focus Zone <b>home</b> bubble bar is showing, identified by
     * the entries that only exist on home (Catch me up + Record). When an active
     * mode (e.g. Live) takes over, the bar swaps those out for Camera/Live, so
     * {@link #isBubbleBarVisible()} alone is not sufficient between sub-flows.
     */
    public boolean isHomeBubbleBarVisible() {
        return isBubbleVisible(BUBBLE_CATCH_ME_UP) && isBubbleVisible(BUBBLE_RECORD);
    }

    /**
     * True when at least two Focus Zone bubbles are visible by SLAP message ID.
     */
    public boolean isBubbleBarVisible() {
        int seen = 0;
        if (isBubbleVisible(BUBBLE_CHAT)) {
            seen++;
        }
        if (isBubbleVisible(BUBBLE_LIVE)) {
            seen++;
        }
        if (isBubbleVisible(BUBBLE_CATCH_ME_UP)) {
            seen++;
        }
        if (isBubbleVisible(BUBBLE_RECORD)) {
            seen++;
        }
        if (isBubbleVisible(BUBBLE_APP_ICON)) {
            seen++;
        }
        if (seen >= 2) {
            return true;
        }
        // Structural fallback. In production locales whose Qira Compose string
        // catalog does not resolve the bubble labels (notably RTL ar-*, where
        // only the language-neutral "Live" matches), the per-bubble string /
        // message-ID lookups above under-count even though the bar is on screen.
        // Evidence (ar-EG FocusZone dump): the bottom bar renders all five
        // entries - App icon [798,2261], محادثة/Chat [648,2278], ‏Live
        // [492,2274], إبقائي على اطلاع/Catch me up [346,2274], تسجيل/Record
        // [207,2274] - each carrying a stable content-description that the SLAP
        // RTL recovery emits. bubbleBarCandidates() keeps only compact
        // bottom-band entries (launcher-dock icons are taller and excluded), so
        // requiring >=3 confirms the Qira bubble bar without matching dock chrome.
        int structural = bubbleBarCandidates().size();
        if (structural >= 3) {
            if (logger != null) {
                logger.info("QiraV2 FocusZone bubble bar detected structurally"
                        + " (compact bottom-band entries=" + structural
                        + "); Compose string labels did not resolve for this locale.");
            }
            return true;
        }
        return false;
    }

    public boolean waitForBubbleBar(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isBubbleBarVisible()) {
                return true;
            }
            sleep(300L);
        }
        return false;
    }

    /**
     * Handles whichever Android runtime permission dialog is currently in the
     * foreground using stable permissioncontroller resource IDs (precise +
     * while-using preferred). Returns {@code true} if a dialog was handled.
     */
    public boolean handleSystemPermissionDialogIfPresent() throws Exception {
        if (!isPermissionControllerForeground()) {
            return false;
        }
        UiObject2 fine = findByAnyResource(PERMISSION_LOCATION_FINE_RESOURCES);
        if (fine != null && !fine.isChecked()) {
            tapClickable(fine);
            settle();
        }
        if (clickResource(PERMISSION_ALLOW_FOREGROUND_RESOURCES)
                || clickResource(PERMISSION_ALLOW_RESOURCES)) {
            settle();
            return true;
        }
        return false;
    }

    /**
     * Drains up to {@code maxDialogs} chained Android permission dialogs.
     */
    public void acceptSystemPermissionDialogs(int maxDialogs, long perDialogTimeoutMs)
            throws Exception {
        for (int i = 0; i < maxDialogs; i++) {
            long deadline = System.currentTimeMillis() + perDialogTimeoutMs;
            boolean handled = false;
            while (System.currentTimeMillis() < deadline) {
                if (handleSystemPermissionDialogIfPresent()) {
                    handled = true;
                    break;
                }
                sleep(250L);
            }
            if (!handled) {
                return;
            }
        }
    }

    public boolean isPermissionControllerForeground() {
        String pkg = safeCurrentPackage();
        return PERMISSION_CONTROLLER_PACKAGE.equals(pkg)
                || PERMISSION_CONTROLLER_GOOGLE_PACKAGE.equals(pkg);
    }

    public boolean isQiraForeground() {
        return packageName.equals(safeCurrentPackage());
    }

    /**
     * Dismisses the Qira-owned model-download notice that can temporarily occupy
     * the same Live content slot as the Action Core enable-permission prompt.
     * Both detection and activation use exact shipped Compose string IDs.
     */
    public boolean dismissLiveModelDownloadNoticeIfPresent(long timeoutMs)
            throws Exception {
        AvikText notice =
                QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                        LIVE_MODEL_DOWNLOAD_NOTICE_STRING_ID,
                        false,
                        logger);
        if (notice == null) {
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 FocusZone Live model-download notice proven:"
                    + " stringId=" + LIVE_MODEL_DOWNLOAD_NOTICE_STRING_ID
                    + ", evidence=" + QiraV2SlapTextDump.summarize(notice));
        }
        if (!QiraV2SlapTextDump
                .clickClickableAncestorByResolvedQiraComposeStringResource(
                        device,
                        LIVE_MODEL_DOWNLOAD_CLOSE_STRING_ID,
                        true,
                        logger)) {
            dump(
                    "MotorolaQiraFocusZone_Live_model_download_notice_close_missing",
                    "The resource-backed Live model-download notice was visible,"
                            + " but its download_close Compose control could not"
                            + " be activated.");
            throw new IllegalStateException(
                    "QiraV2 Live model-download notice exposed no clickable"
                            + " download_close resource control.");
        }

        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                    LIVE_MODEL_DOWNLOAD_NOTICE_STRING_ID,
                    false,
                    null) == null) {
                if (logger != null) {
                    logger.info("QiraV2 FocusZone Live model-download notice dismissed"
                            + " through Compose stringId="
                            + LIVE_MODEL_DOWNLOAD_CLOSE_STRING_ID + ".");
                }
                return true;
            }
            sleep(200L);
        }
        dump(
                "MotorolaQiraFocusZone_Live_model_download_notice_not_dismissed",
                "The resource-backed model-download notice remained after"
                        + " activating download_close.");
        throw new IllegalStateException(
                "QiraV2 Live model-download notice remained after its stable"
                        + " resource-backed close action.");
    }

    /**
     * Waits for one exact foreground package over two consecutive polls. This
     * prevents a transient package handoff from being treated as a settled
     * Action Core/Qira surface.
     */
    public boolean waitForForegroundPackage(String expectedPackage, long timeoutMs)
            throws Exception {
        if (expectedPackage == null || expectedPackage.isEmpty()) {
            return false;
        }
        long deadline = System.currentTimeMillis() + timeoutMs;
        int stablePolls = 0;
        String lastPackage = "";
        while (System.currentTimeMillis() < deadline) {
            lastPackage = safeCurrentPackage();
            if (expectedPackage.equals(lastPackage)) {
                stablePolls++;
                if (stablePolls >= 2) {
                    if (logger != null) {
                        logger.info("QiraV2 FocusZone foreground package proven:"
                                + " expectedPackage=" + expectedPackage
                                + ", actualPackage=" + lastPackage
                                + ", stablePolls=" + stablePolls);
                    }
                    return true;
                }
            } else {
                stablePolls = 0;
            }
            sleep(250L);
        }
        if (logger != null) {
            logger.info("QiraV2 FocusZone foreground package timeout:"
                    + " expectedPackage=" + expectedPackage
                    + ", actualPackage=" + lastPackage
                    + ", stablePolls=" + stablePolls
                    + ", timeoutMs=" + timeoutMs);
        }
        return false;
    }

    public boolean isTopSheetOpen() {
        if (!isQiraForeground()) {
            return false;
        }
        UiObject2 back = safeFind(By.desc("Back"));
        if (back == null) {
            for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
                String value = QiraStrings.stripBidiControls(text.getText());
                if ("Back".equals(value)
                        && text.getTop() < Math.max(420, device.getDisplayHeight() / 5)
                        && text.getLeft() < device.getDisplayWidth() / 4) {
                    return true;
                }
            }
            return false;
        }
        Rect bounds = back.getVisibleBounds();
        return bounds != null && bounds.top < Math.max(360, device.getDisplayHeight() / 5);
    }

    public void logSlapInventory(String label) {
        QiraV2SlapTextDump.logVisibleTextSummaries(label, false, logger);
    }

    // --- SLAP message-ID surface helpers (for textless Compose CTAs) ---

    public boolean isMessageVisible(String messageId) {
        return messageId != null && !messageId.isEmpty()
                && QiraV2SlapTextDump.findByMessageId(messageId, false, null) != null;
    }

    public boolean clickMessageIfPresent(String messageId) {
        return messageId != null && !messageId.isEmpty()
                && QiraV2SlapTextDump.clickByMessageId(device, messageId, false, logger);
    }

    public boolean waitForMessage(long timeoutMs, String messageId) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isMessageVisible(messageId)) {
                return true;
            }
            sleep(250L);
        }
        return false;
    }

    public boolean clickComposerInputBySlap(String... englishAnchors) {
        if (clickBySlapIfPresent(englishAnchors)) {
            return true;
        }
        return clickChatComposerInputByStructure();
    }

    public boolean waitForChatComposerStructure(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isChatComposerVisibleByStructure()) {
                return true;
            }
            sleep(250L);
        }
        return false;
    }

    public boolean isChatComposerVisibleByStructure() {
        return findChatComposerPromptCandidate() != null
                && findChatComposerSendCandidate() != null;
    }

    public boolean clickChatComposerInputByStructure() {
        AvikText prompt = findChatComposerPromptCandidate();
        if (prompt == null) {
            return false;
        }
        int width = prompt.getRight() - prompt.getLeft();
        int x = prompt.getLeft() + Math.max(32, width / 4);
        int y = prompt.getTop() + ((prompt.getBottom() - prompt.getTop()) / 2);
        if (logger != null) {
            logger.info("QiraV2 FocusZone composer input click from SLAP structure: "
                    + QiraV2SlapTextDump.summarize(prompt) + ", target=" + x + "," + y);
        }
        return device.click(x, y);
    }

    public boolean clickComposerSendBySlap(String... englishAnchors) {
        List<String> entryNames = resolveEntryNames(englishAnchors);
        if (!entryNames.isEmpty()
                && QiraV2SlapTextDump.clickLogicalEndOfResourceRow(
                        device, false, logger, entryNames.toArray(new String[0]))) {
            return true;
        }
        return clickChatComposerSendByStructure();
    }

    public boolean clickChatComposerSendByStructure() {
        AvikText send = findChatComposerSendCandidate();
        if (send == null) {
            return false;
        }
        int x = send.getLeft() + ((send.getRight() - send.getLeft()) / 2);
        int y = send.getTop() + ((send.getBottom() - send.getTop()) / 2);
        if (logger != null) {
            logger.info("QiraV2 FocusZone composer send click from SLAP structure: "
                    + QiraV2SlapTextDump.summarize(send) + ", target=" + x + "," + y);
        }
        return device.click(x, y);
    }

    /**
     * Clicks the chat composer "send" affordance by anchoring to the composer
     * card container, not to any text row.
     *
     * <p>Evidence (this build, UI dumps across pl/ro/de/zh): once the soft
     * keyboard is up, the chat composer renders as a centered popover card
     * (a bounded {@code com.lenovo.qira} node with side gutters, e.g.
     * {@code [21,1037][1059,1506]}) floating just above the keyboard, drawn on
     * top of a full-screen dismiss scrim ({@code [0,0][1080,1187]} +
     * {@code [0,1187][1080,2520]}). The send control is a textless Compose glyph
     * on the trailing edge of the card's bottom action row - it exposes no
     * text, no content-desc and no SLAP message ID, so neither an ID/message
     * selector nor a text-node structural scan can resolve it. The previous
     * prompt-text-row heuristic tapped the row centre (~y1168), which lands on
     * the dismiss scrim above the card body and closed Qira to the launcher.
     *
     * <p>Anchoring inside the card bounds guarantees the tap lands on the card
     * content (the send glyph), never the scrim behind it, and mirrors for RTL
     * (send on the leading/left edge). Returns {@code true} if a tap was issued.
     */
    public boolean clickChatComposerSendByCard() {
        Rect card = findComposerCardBounds();
        if (card == null) {
            return false;
        }
        boolean rtl = isRtl();
        int cardW = card.width();
        int cardH = card.height();
        // The send control is the trailing icon of the card's BOTTOM action row.
        // Anchor to the bottom-trailing corner (mirrored for RTL), inset enough
        // to stay on the row's live controls (send / mic / input field) and off
        // the card padding - a tap on the padding falls through the non-clickable
        // Compose card to the dismiss scrim behind it. The previous y
        // (bottom - 32% of height) landed in the text area, refocusing the field
        // instead of sending.
        int inset = Math.max(64, Math.round(cardW * 0.08f));
        int x = rtl ? card.left + inset : card.right - inset;
        int y = card.bottom - Math.max(52, Math.round(cardH * 0.12f));
        if (y <= card.top || y >= card.bottom) {
            y = card.centerY();
        }
        if (logger != null) {
            logger.info("QiraV2 FocusZone chat send by composer-card action row (rtl=" + rtl
                    + "); card=" + card.left + "," + card.top + "," + card.right + "," + card.bottom
                    + ", target=" + x + "," + y);
        }
        return device.click(x, y);
    }

    /**
     * Counts exact copies of {@code expected} in the current composer input
     * band. Only rendered SLAP text fully contained in the upper half of the
     * keyboard-anchored composer card is eligible; historical chat bubbles,
     * lower action/disclaimer rows, and Gboard suggestions are excluded.
     */
    public int countExactCurrentChatComposerInput(String expected, long timeoutMs)
            throws Exception {
        String normalizedExpected = normalizeComposerInputValue(expected);
        if (normalizedExpected.isEmpty()) {
            return 0;
        }
        long deadline = System.currentTimeMillis() + Math.max(1L, timeoutMs);
        int staleScans = 0;
        do {
            try {
                Rect card = findComposerCardBounds();
                if (card != null) {
                    int matches =
                            countExactInputInUpperCardBand(card, normalizedExpected);
                    if (matches > 0) {
                        if (logger != null) {
                            logger.info("QiraV2 FocusZone composer-card exact input matches="
                                    + matches + ", card=" + card + ".");
                        }
                        return matches;
                    }
                }
            } catch (StaleObjectException stale) {
                staleScans++;
                if (logger != null) {
                    logger.info("QiraV2 FocusZone composer readback hit a stale node;"
                            + " reacquiring scan " + staleScans + ".");
                }
            }
            if (System.currentTimeMillis() < deadline) {
                sleep(100L);
            }
        } while (System.currentTimeMillis() < deadline);
        if (logger != null) {
            logger.info("QiraV2 FocusZone composer-card exact input matches=0"
                    + " after bounded readback; staleScans=" + staleScans + ".");
        }
        return 0;
    }

    private int countExactInputInUpperCardBand(
            Rect card,
            String normalizedExpected) {
        List<AvikText> snapshot =
                QiraV2SlapTextDump.dumpVisibleText(false, logger);
        int inputBandBottom = card.top + (card.height() / 2);
        int matches = 0;
        for (AvikText text : snapshot) {
            if (!normalizedExpected.equals(
                    normalizeComposerInputValue(text.getText()))) {
                continue;
            }
            if (text.getLeft() < card.left
                    || text.getTop() < card.top
                    || text.getRight() > card.right
                    || text.getBottom() > inputBandBottom) {
                continue;
            }
            matches++;
        }
        return matches;
    }

    private static String normalizeComposerInputValue(String value) {
        String stripped = QiraStrings.stripBidiControls(value);
        if (stripped == null) {
            return "";
        }
        StringBuilder normalized = new StringBuilder(stripped.length());
        boolean pendingSpace = false;
        for (int i = 0; i < stripped.length(); i++) {
            char c = stripped.charAt(i);
            if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
                pendingSpace = normalized.length() > 0;
                continue;
            }
            if (pendingSpace) {
                normalized.append(' ');
                pendingSpace = false;
            }
            normalized.append(c);
        }
        return normalized.toString();
    }

    /**
     * Resolves the bounds of the floating chat composer card: a bounded
     * {@code com.lenovo.qira} view with left/right gutters (so it excludes the
     * full-width dismiss scrims and the launcher behind), wide and short, seated
     * in the middle band above the soft keyboard. Returns {@code null} when no
     * such card is present (e.g. the composer is not open).
     */
    private Rect findComposerCardBounds() {
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        int keyboardTop = keyboardTopEdge();
        if (keyboardTop >= height) {
            return null;
        }
        Rect best = null;
        for (UiObject2 view : device.findObjects(By.pkg(packageName))) {
            Rect b = view.getVisibleBounds();
            if (b == null) {
                continue;
            }
            // Side gutters exclude the full-width dismiss scrims (left=0,
            // right=width) while keeping the inset popover card.
            if (b.left < 8 || b.right > width - 8) {
                continue;
            }
            int w = b.width();
            int h = b.height();
            if (w < (width * 55) / 100) {
                continue;
            }
            if (h < (height * 8) / 100 || h > (height * 45) / 100) {
                continue;
            }
            if (b.top < (height * 18) / 100) {
                continue;
            }
            // The composer floats above the keyboard; ignore any node that
            // begins at/below the keyboard top. Gate on the card's TOP, not its
            // bottom: the card's bottom action row can sit a few px above (or
            // flush with) the keyboard, and a bottom-based cutoff was dropping
            // the real card whenever keyboard detection was even slightly off.
            if (b.top >= keyboardTop) {
                continue;
            }
            int keyboardGap = keyboardTop - b.bottom;
            if (keyboardGap < 0 || keyboardGap > Math.max(1, h / 3)) {
                continue;
            }
            if (best == null || b.bottom > best.bottom
                    || (b.bottom == best.bottom && b.height() > best.height())) {
                best = new Rect(b);
            }
        }
        return best;
    }

    private int keyboardTopEdge() {
        int top = Math.max(1, device.getDisplayHeight());
        for (UiObject2 node : device.findObjects(
                By.pkg("com.google.android.inputmethod.latin"))) {
            Rect b = node.getVisibleBounds();
            if (b != null && !b.isEmpty() && b.top < top) {
                top = b.top;
            }
        }
        return top;
    }

    public boolean clickLowestPrimaryActionBySlap() {
        AvikText action = QiraV2SlapTextDump.findLowestWideTextInBand(
                false, logger, 0.45f, 0.90f, 0.36f);
        if (action == null) {
            return false;
        }
        int x = action.getLeft() + ((action.getRight() - action.getLeft()) / 2);
        int y = action.getTop() + ((action.getBottom() - action.getTop()) / 2);
        if (logger != null) {
            logger.info("QiraV2 FocusZone lowest primary action click from SLAP bounds: "
                    + QiraV2SlapTextDump.summarize(action) + ", target=" + x + "," + y);
        }
        return device.click(x, y);
    }

    public boolean tapRecordStopBySlap() {
        if (tapBubbleIfPresent(BUBBLE_RECORD)) {
            return true;
        }
        return tapRightmostBubbleBarSlot("Record/Stop");
    }

    /**
     * Enables an app in the Catch Me Up "Choose the app notifications" sheet so
     * the otherwise-disabled "Get caught up" CTA becomes actionable.
     *
     * <p>Evidence (en-XM, this build): each app row carries a Compose toggle that
     * IS exposed to UiAutomator as a {@code checkable="true" clickable="true"}
     * {@code android.view.View} (no text / resource-id), while the "Get caught
     * up" button wrapper reports {@code enabled="false"} until at least one app
     * is on. This targets the bottom-most toggle ("All other apps"), which yields
     * a deterministic, non-empty summary, selecting by the checkable
     * accessibility role (a stable semantic) rather than a static coordinate.
     * Returns {@code true} once a toggle is (or already was) checked.
     */
    public boolean enableCatchMeUpAppToggle() throws Exception {
        int height = Math.max(1, device.getDisplayHeight());
        List<UiObject2> toggles = new ArrayList<>();
        for (UiObject2 view : device.findObjects(
                By.clazz("android.view.View").checkable(true).clickable(true))) {
            Rect bounds = view.getVisibleBounds();
            if (bounds == null) {
                continue;
            }
            int cy = bounds.centerY();
            // Keep only toggles inside the app-list band (below the sheet title,
            // above the bottom CTA buttons), excluding any stray checkable.
            if (cy < (height * 22) / 100 || cy > (height * 68) / 100) {
                continue;
            }
            toggles.add(view);
        }
        if (toggles.isEmpty()) {
            if (logger != null) {
                logger.info("QiraV2 FocusZone CatchMeUp found no checkable app toggle in the list band.");
            }
            return anyCheckableChecked();
        }
        if (anyCheckableChecked()) {
            return true;
        }
        UiObject2 target = null;
        for (UiObject2 toggle : toggles) {
            if (target == null
                    || toggle.getVisibleBounds().centerY() > target.getVisibleBounds().centerY()) {
                target = toggle;
            }
        }
        Rect bounds = target.getVisibleBounds();
        if (logger != null) {
            logger.info("QiraV2 FocusZone CatchMeUp enabling bottom-most app toggle (All other apps)"
                    + " by checkable role; bounds=" + bounds.left + "," + bounds.top + ","
                    + bounds.right + "," + bounds.bottom);
        }
        target.click();
        settle();
        return anyCheckableChecked();
    }

    private boolean anyCheckableChecked() {
        for (UiObject2 view : device.findObjects(By.clazz("android.view.View").checkable(true))) {
            try {
                if (view.isChecked()) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    /**
     * Stops an active Pay Attention recording. The recording control bar renders
     * its stop button as a textless Compose view (no content-desc / message ID),
     * so this first tries the SLAP record/stop bubble, then falls back to the
     * stop slot resolved structurally from the live bottom control bar (the
     * second-from-right clickable slot), which adapts to the rendered layout
     * rather than relying on a hard-coded coordinate. Returns {@code true} if
     * a control was tapped.
     */
    public boolean tapPayAttentionRecordStop() {
        List<UiObject2> slots = bottomBarClickableSlots();
        if (slots.size() >= 3) {
            // slots are sorted visually left-to-right. The stop control sits
            // second from the trailing edge of the control bar: second-from-
            // right under LTR, second-from-left under RTL (the bar mirrors).
            int index = isRtl() ? 1 : slots.size() - 2;
            UiObject2 stop = slots.get(index);
            Rect bounds = stop.getVisibleBounds();
            if (logger != null) {
                logger.info("QiraV2 FocusZone PayAttention stop by structural bottom-bar slot "
                        + index + "/" + slots.size() + " (rtl=" + isRtl() + "); bounds="
                        + bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom);
            }
            return device.click(bounds.centerX(), bounds.centerY());
        }
        return tapRecordStopBySlap();
    }

    private List<UiObject2> bottomBarClickableSlots() {
        int height = Math.max(1, device.getDisplayHeight());
        int maxWidth = device.getDisplayWidth() / 4;
        List<UiObject2> slots = new ArrayList<>();
        for (UiObject2 view : device.findObjects(
                By.pkg(packageName).clazz("android.view.View").clickable(true))) {
            Rect bounds = view.getVisibleBounds();
            if (bounds == null || bounds.centerY() < (height * 88) / 100) {
                continue;
            }
            int w = bounds.width();
            int h = bounds.height();
            if (w <= 0 || h <= 0 || w > maxWidth) {
                continue;
            }
            slots.add(view);
        }
        slots.sort((a, b) -> Integer.compare(
                a.getVisibleBounds().centerX(), b.getVisibleBounds().centerX()));
        return slots;
    }

    private AvikText findChatComposerPromptCandidate() {
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        AvikText best = null;
        int bestArea = 0;
        for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
            int top = text.getTop();
            int bottom = text.getBottom();
            int left = text.getLeft();
            int right = text.getRight();
            int w = right - left;
            int h = bottom - top;
            if (w <= 0 || h <= 0) {
                continue;
            }
            if (!isChatComposerPromptText(text)) {
                continue;
            }
            if (top < (height * 66) / 100 || bottom > (height * 80) / 100) {
                continue;
            }
            if (left < width / 8 || left > (width * 45) / 100) {
                continue;
            }
            if (right < (width * 60) / 100 || w < (width * 42) / 100) {
                continue;
            }
            if (h > (height * 8) / 100) {
                continue;
            }
            int area = w * h;
            if (area > bestArea) {
                bestArea = area;
                best = text;
            }
        }
        return best;
    }

    private boolean isChatComposerPromptText(AvikText text) {
        String value = QiraStrings.stripBidiControls(text.getText())
                .toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return value.contains("what are you looking for")
                || value.contains("what are you searching for")
                || value.contains("ask anything")
                || (value.contains("looking") && value.contains("for"))
                || (value.contains("search") && value.contains("for"));
    }

    private AvikText findChatComposerSendCandidate() {
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        boolean rtl = isRtl();
        AvikText best = null;
        int bestEdge = rtl ? Integer.MAX_VALUE : 0;
        for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
            int top = text.getTop();
            int bottom = text.getBottom();
            int left = text.getLeft();
            int right = text.getRight();
            int w = right - left;
            int h = bottom - top;
            if (w <= 0 || h <= 0) {
                continue;
            }
            if (top < (height * 74) / 100 || bottom > (height * 84) / 100) {
                continue;
            }
            // The send affordance sits on the trailing edge of the composer:
            // right side under LTR, left side under RTL (mirrored layout).
            if (rtl) {
                if (right > (width * 28) / 100 || left < (width * 4) / 100) {
                    continue;
                }
            } else {
                if (left < (width * 72) / 100 || right > (width * 96) / 100) {
                    continue;
                }
            }
            if (w > (width * 12) / 100 || h > (height * 6) / 100) {
                continue;
            }
            if (rtl) {
                if (left < bestEdge) {
                    bestEdge = left;
                    best = text;
                }
            } else if (right > bestEdge) {
                bestEdge = right;
                best = text;
            }
        }
        return best;
    }

    // --- Focus Zone hero carousel ("Page N of M") ---

    // All patterns run against a digit-normalized copy of the SLAP text (see
    // normalizeUnicodeDigits): Arabic-Indic (٠-٩ / ۰-۹), CJK full-width and
    // other Unicode decimal digits are folded to ASCII 0-9 first, so a single
    // ASCII \d class matches every locale's numerals. These PRECISE indicators
    // key on a localized "page …/… total" structure - covering en/de/es/fr/it/
    // pt/pl/ro (word forms), ar (صفحة … من …, Arabic-Indic digits), ja and
    // zh-TW (slash), and zh-CN (第 N 页（共 M 页）). A precise match is required
    // for node selection (see findCarouselText) so a bare number pair elsewhere
    // on screen - e.g. the status-bar clock "16:45" - can never be mistaken for
    // the carousel indicator.
    private static final Pattern[] CAROUSEL_PAGE_PATTERNS = {
            Pattern.compile(
                    "(?:Page|Seite|Strona|Página|Pagina|\u0635\u0641\u062d\u0629)\\s+(\\d+)\\s+"
                            + "(?:of|von|z|de|di|din|sur|\u0645\u0646)\\s+(\\d+)",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("(\\d+)\\s*/\\s*(\\d+)"),
            Pattern.compile(
                    "第\\s*(\\d+)\\s*页[^\\d]{0,4}?(\\d+)\\s*页?",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile(
                    "(\\d+)\\s*(?:ページ|頁)\\s*/?\\s*(\\d+)\\s*(?:ページ|頁)?",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    };

    // Strict, word-agnostic last-resort net for any future locale whose page
    // indicator is not covered above. Deliberately excludes ':' '.' ',' '/'
    // separators so it can never match a status-bar clock ("16:45"), a decimal
    // or a ratio, and it is only consulted (findCarouselText) when NO node
    // matched a precise indicator. Combined with the short-standalone-node and
    // exactly-two-number-runs guards in parseCarouselPageGeneric this keeps the
    // "digit- and word-agnostic" requirement without re-introducing the clock
    // false-positive.
    private static final Pattern CAROUSEL_PAGE_GENERIC =
            Pattern.compile("(\\d{1,2})[^\\d:.,/\\r\\n]{1,12}?(\\d{1,2})");
    private static final String CAROUSEL_PAGE_INDICATOR_RESOURCE_ID = "page_indicator";
    private static final Pattern PAGE_INDICATOR_PLACEHOLDER =
            Pattern.compile("%(?:[0-9]+\\$)?d");

    private AvikText findCarouselText() {
        List<AvikText> texts = QiraV2SlapTextDump.dumpVisibleText(false, null);
        // Pass 0: resolve Qira's own page_indicator resource in the active
        // locale. The accessibility node often appends the carousel headline
        // after "Page N of M", so hard-coded language patterns and the short
        // two-number fallback both fail for new locales (for example Czech
        // "Strana 1 z 5. <headline>"). This derives the exact localized
        // indicator grammar from the shipped Compose resource instead.
        for (AvikText text : texts) {
            if (parseCarouselPageFromResource(text.getText()) != null) {
                return text;
            }
        }
        // Pass 1: a node whose text matches a PRECISE localized page indicator.
        // This is what makes selection robust - the status-bar clock, launcher
        // page dots, temperatures, etc. never match a precise indicator, so the
        // real Qira carousel node wins even though it is not first in the dump.
        for (AvikText text : texts) {
            if (parseCarouselPagePrecise(text.getText()) != null) {
                return text;
            }
        }
        // Pass 2: strict, word-agnostic net for locales not covered above.
        for (AvikText text : texts) {
            if (parseCarouselPageGeneric(text.getText()) != null) {
                return text;
            }
        }
        return null;
    }

    /**
     * Returns {@code {currentPage, totalPages}} parsed from the hero carousel's
     * SLAP page-count text, or {@code null} when the carousel is not present.
     */
    public int[] carouselPage() {
        AvikText carousel = findCarouselText();
        if (carousel == null) {
            return null;
        }
        int[] resourcePage = parseCarouselPageFromResource(carousel.getText());
        return resourcePage != null ? resourcePage : parseCarouselPage(carousel.getText());
    }

    /**
     * Returns the 1-based hero-carousel page number once the carousel has
     * <b>settled</b> on it, or {@code -1} while it is mid-transition / absent.
     *
     * <p>Locale-independent: it keys purely on the carousel's own "Page N of M"
     * indicator node (parsed across en/de/es/fr/it/pt/pl/ro/ja/zh by
     * {@link #parseCarouselPage}) and treats the page as settled only when both
     * the parsed page number and the raw indicator text are identical across the
     * {@code dwellMs} window - i.e. the auto-advancing carousel is dwelling on
     * this page, not sliding. No hard-coded headline text or per-locale table.
     */
    public int settledCarouselPage(long dwellMs) throws Exception {
        AvikText first = findCarouselText();
        int[] p1 = (first == null) ? null : carouselPageOf(first);
        if (p1 == null) {
            return -1;
        }
        String t1 = QiraStrings.stripBidiControls(first.getText());
        sleep(dwellMs);
        AvikText second = findCarouselText();
        int[] p2 = (second == null) ? null : carouselPageOf(second);
        if (p2 == null || p1[0] != p2[0]) {
            return -1;
        }
        String t2 = QiraStrings.stripBidiControls(second.getText());
        if (t1 != null && !t1.isEmpty() && t1.equals(t2)) {
            return p1[0];
        }
        return -1;
    }

    private int[] carouselPageOf(AvikText text) {
        int[] resourcePage = parseCarouselPageFromResource(text.getText());
        return resourcePage != null ? resourcePage : parseCarouselPage(text.getText());
    }

    private int[] parseCarouselPageFromResource(String rawValue) {
        String template = QiraV2ComposeStrings.resolve(
                packageName, CAROUSEL_PAGE_INDICATOR_RESOURCE_ID, null);
        String value = normalizeCarouselText(rawValue);
        String normalizedTemplate = normalizeCarouselText(template);
        if (value == null || normalizedTemplate == null) {
            return null;
        }
        Matcher placeholders = PAGE_INDICATOR_PLACEHOLDER.matcher(normalizedTemplate);
        StringBuilder expression = new StringBuilder(normalizedTemplate.length() + 16);
        int cursor = 0;
        int placeholderCount = 0;
        int currentGroup = -1;
        int totalGroup = -1;
        while (placeholders.find()) {
            expression.append(Pattern.quote(
                    normalizedTemplate.substring(cursor, placeholders.start())));
            expression.append("(\\d+)");
            cursor = placeholders.end();
            placeholderCount++;
            String placeholder = placeholders.group();
            if (placeholder.startsWith("%1$")) {
                currentGroup = placeholderCount;
            } else if (placeholder.startsWith("%2$")) {
                totalGroup = placeholderCount;
            }
        }
        if (placeholderCount != 2) {
            return null;
        }
        expression.append(Pattern.quote(normalizedTemplate.substring(cursor)));
        Matcher match = Pattern.compile(
                expression.toString(),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(value);
        if (!match.find()) {
            return null;
        }
        if (currentGroup < 0) {
            currentGroup = 1;
        }
        if (totalGroup < 0) {
            totalGroup = currentGroup == 1 ? 2 : 1;
        }
        try {
            int current = Integer.parseInt(match.group(currentGroup));
            int total = Integer.parseInt(match.group(totalGroup));
            return current > 0 && total >= current && total <= 99
                    ? new int[] {current, total}
                    : null;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static int[] parseCarouselPage(String rawValue) {
        int[] precise = parseCarouselPagePrecise(rawValue);
        if (precise != null) {
            return precise;
        }
        return parseCarouselPageGeneric(rawValue);
    }

    /**
     * Matches a precise, localized "page N of M" indicator (see
     * {@link #CAROUSEL_PAGE_PATTERNS}). Digits are folded to ASCII first so
     * ar-EG (Arabic-Indic) and any non-Latin numeral script parse with a single
     * ASCII {@code \d} class.
     */
    private static int[] parseCarouselPagePrecise(String rawValue) {
        String normalized = normalizeCarouselText(rawValue);
        if (normalized == null) {
            return null;
        }
        for (Pattern pattern : CAROUSEL_PAGE_PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                int[] page = toCarouselPage(matcher, 99);
                if (page != null) {
                    return page;
                }
            }
        }
        return null;
    }

    /**
     * Strict, word-agnostic fallback: only a SHORT standalone node with EXACTLY
     * two number runs (after digit folding) and a non-clock/date/ratio
     * separator can be a page counter. Guards against the status-bar clock,
     * dates, decimals and long headlines that merely contain two numbers.
     */
    private static int[] parseCarouselPageGeneric(String rawValue) {
        String normalized = normalizeCarouselText(rawValue);
        if (normalized == null) {
            return null;
        }
        String trimmed = normalized.trim();
        if (trimmed.length() > 28 || countDigitRuns(trimmed) != 2) {
            return null;
        }
        Matcher matcher = CAROUSEL_PAGE_GENERIC.matcher(trimmed);
        if (matcher.find()) {
            return toCarouselPage(matcher, 20);
        }
        return null;
    }

    private static String normalizeCarouselText(String rawValue) {
        String value = QiraStrings.stripBidiControls(rawValue);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return normalizeUnicodeDigits(value);
    }

    private static int[] toCarouselPage(Matcher matcher, int maxTotal) {
        try {
            int current = Integer.parseInt(matcher.group(1));
            int total = Integer.parseInt(matcher.group(2));
            if (current > 0 && total >= current && total <= maxTotal) {
                return new int[] {current, total};
            }
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    /** Counts maximal runs of ASCII digits in {@code value}. */
    private static int countDigitRuns(String value) {
        int runs = 0;
        boolean inRun = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') {
                if (!inRun) {
                    runs++;
                    inRun = true;
                }
            } else {
                inRun = false;
            }
        }
        return runs;
    }

    /**
     * Returns {@code value} with every Unicode decimal digit folded to its
     * ASCII 0-9 equivalent, leaving all other characters untouched. Covers
     * Arabic-Indic (U+0660-U+0669), Extended Arabic-Indic (U+06F0-U+06F9),
     * full-width (U+FF10-U+FF19) and every other {@code DECIMAL_DIGIT_NUMBER}
     * so the carousel parser is numeral-script independent.
     */
    private static String normalizeUnicodeDigits(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder sb = null;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                int digit = Character.digit(c, 10);
                if (digit >= 0 && digit <= 9
                        && Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER) {
                    if (sb == null) {
                        sb = new StringBuilder(value.length());
                        sb.append(value, 0, i);
                    }
                    sb.append((char) ('0' + digit));
                    continue;
                }
            }
            if (sb != null) {
                sb.append(c);
            }
        }
        return sb != null ? sb.toString() : value;
    }

    /**
     * Swipes the hero carousel one page forward/backward. The swipe endpoints
     * are derived from the carousel element's own SLAP bounds (not hard-coded
     * screen coordinates), so this is a content-anchored paging gesture.
     */
    public boolean swipeCarousel(boolean forward) throws Exception {
        AvikText carousel = findCarouselText();
        if (carousel == null) {
            return false;
        }
        int width = carousel.getRight() - carousel.getLeft();
        if (width <= 0) {
            return false;
        }
        int margin = Math.max(24, width / 8);
        int y = (carousel.getTop() + carousel.getBottom()) / 2;
        // The hero pager mirrors under RTL: advancing to the next (higher-index)
        // page is a right-to-left swipe in LTR but a left-to-right swipe in RTL.
        // Anchor endpoints to the element's own bounds and pick the direction
        // from the layout direction, never a hard-coded side.
        boolean swipeRightToLeft = forward != isRtl();
        int startX = swipeRightToLeft ? carousel.getRight() - margin : carousel.getLeft() + margin;
        int endX = swipeRightToLeft ? carousel.getLeft() + margin : carousel.getRight() - margin;
        device.swipe(startX, y, endX, y, 24);
        settle();
        return true;
    }

    /**
     * True when the active Qira UI locale is right-to-left. Used to mirror the
     * few genuinely directional gestures (hero paging, row-trailing controls)
     * so they stay correct in ar-EG without a hard-coded left/right side.
     */
    private boolean isRtl() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Best-effort hero paging for locales/builds where the page-count text is
     * not exposed. The gesture is still derived from SLAP bounds in the upper
     * hero area, not from a fixed device coordinate.
     */
    public boolean swipeHeroSurfaceForwardBestEffort() throws Exception {
        AvikText anchor = QiraV2SlapTextDump.findLargestTextInBand(
                false, logger, 0.10f, 0.72f);
        if (anchor == null) {
            return false;
        }
        int width = anchor.getRight() - anchor.getLeft();
        int height = anchor.getBottom() - anchor.getTop();
        if (width <= 0 || height <= 0) {
            return false;
        }
        int margin = Math.max(32, width / 6);
        int y = anchor.getTop() + Math.max(24, Math.min(height - 24, height / 2));
        // Forward paging mirrors under RTL (see swipeCarousel).
        boolean swipeRightToLeft = !isRtl();
        int startX = swipeRightToLeft ? anchor.getRight() - margin : anchor.getLeft() + margin;
        int endX = swipeRightToLeft ? anchor.getLeft() + margin : anchor.getRight() - margin;
        if (startX == endX) {
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 FocusZone hero swipe from SLAP upper-surface anchor: "
                    + QiraV2SlapTextDump.summarize(anchor)
                    + ", start=" + startX + "," + y + ", end=" + endX + "," + y);
        }
        device.swipe(startX, y, endX, y, 24);
        settle();
        return true;
    }

    /**
     * Pages the hero carousel to {@code targetPage} (1-based), confirming the
     * "Page N of M" SLAP text advanced. Returns true once reached.
     */
    public boolean goToCarouselPage(int targetPage, int maxSwipes) throws Exception {
        for (int i = 0; i < maxSwipes; i++) {
            int[] info = carouselPage();
            if (info == null) {
                return false;
            }
            if (info[0] == targetPage) {
                return true;
            }
            if (!swipeCarousel(info[0] < targetPage)) {
                return false;
            }
        }
        int[] info = carouselPage();
        return info != null && info[0] == targetPage;
    }

    public void dump(String tag, String reason) {
        try {
            QiraUiDumper.dump(device, packageName, tag, reason);
        } catch (Throwable ignored) {
        }
    }

    private boolean clickResource(String... resources) throws Exception {
        UiObject2 target = findByAnyResource(resources);
        if (target == null) {
            return false;
        }
        tapClickable(target);
        return true;
    }

    private UiObject2 findByAnyResource(String... resources) {
        for (String resource : resources) {
            if (resource == null || resource.isEmpty()) {
                continue;
            }
            UiObject2 object = safeFind(By.res(resource));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private void tapClickable(UiObject2 object) {
        UiObject2 clickable = object;
        while (clickable != null && !clickable.isClickable()) {
            clickable = clickable.getParent();
        }
        Rect bounds = (clickable != null ? clickable : object).getVisibleBounds();
        device.click(bounds.centerX(), bounds.centerY());
    }

    private boolean tapBubbleBarSlot(String label, int slotIndex) {
        AvikText slot = findBubbleBarSlot(slotIndex);
        if (slot == null) {
            return false;
        }
        int centerX = slot.getLeft() + ((slot.getRight() - slot.getLeft()) / 2);
        int centerY = slot.getTop() + ((slot.getBottom() - slot.getTop()) / 2);
        if (logger != null) {
            logger.info("QiraV2 FocusZone tapped " + label
                    + " using SLAP bubble-bar slot " + slotIndex
                    + " evidence=" + QiraV2SlapTextDump.summarize(slot)
                    + ", target=" + centerX + "," + centerY);
        }
        return device.click(centerX, centerY);
    }

    private boolean tapRightmostBubbleBarSlot(String label) {
        AvikText slot = findTrailingBubbleBarSlot();
        if (slot == null) {
            return false;
        }
        int centerX = slot.getLeft() + ((slot.getRight() - slot.getLeft()) / 2);
        int centerY = slot.getTop() + ((slot.getBottom() - slot.getTop()) / 2);
        if (logger != null) {
            logger.info("QiraV2 FocusZone tapped " + label
                    + " using trailing SLAP bubble-bar slot (rtl=" + isRtl() + ") evidence="
                    + QiraV2SlapTextDump.summarize(slot)
                    + ", target=" + centerX + "," + centerY);
        }
        return device.click(centerX, centerY);
    }

    private AvikText findBubbleBarSlot(int slotIndex) {
        List<AvikText> candidates = bubbleBarCandidates();
        if (candidates.size() <= slotIndex) {
            return null;
        }
        return candidates.get(slotIndex);
    }

    /**
     * Returns the <b>logically trailing</b> bubble-bar slot - the last entry in
     * reading order. {@link #bubbleBarCandidates()} sorts visually left-to-right,
     * so the trailing slot is the rightmost under LTR and the leftmost under RTL
     * (the bar mirrors). Used as the last-resort locator for the Record/Stop
     * bubble, which is the final entry of the home bar in both directions.
     */
    private AvikText findTrailingBubbleBarSlot() {
        List<AvikText> candidates = bubbleBarCandidates();
        if (candidates.isEmpty()) {
            return null;
        }
        return isRtl() ? candidates.get(0) : candidates.get(candidates.size() - 1);
    }

    private List<AvikText> bubbleBarCandidates() {
        List<AvikText> candidates = new ArrayList<>();
        int height = Math.max(1, device.getDisplayHeight());
        int top = (int) (height * 0.82f);
        int bottom = (int) (height * 0.95f);
        for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
            int cy = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
            int w = text.getRight() - text.getLeft();
            int h = text.getBottom() - text.getTop();
            if (cy < top || cy > bottom || w <= 0 || h <= 0) {
                continue;
            }
            // Bubble labels/icons are compact. This excludes navigation labels
            // and full-width launcher/home content while keeping translated
            // one- or two-word bubble labels like "Was gibt es Neues?".
            if (w > device.getDisplayWidth() / 4 || h > height / 12) {
                continue;
            }
            candidates.add(text);
        }
        candidates.sort((a, b) -> Integer.compare(a.getLeft(), b.getLeft()));
        return candidates;
    }

    private UiObject2 safeFind(androidx.test.uiautomator.BySelector by) {
        try {
            return device.findObject(by);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private String safeCurrentPackage() {
        try {
            return device.getCurrentPackageName();
        } catch (Throwable ignored) {
            return "";
        }
    }

    public void settle() throws Exception {
        try {
            device.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
        sleep(500L);
    }

    private void sleep(long millis) throws Exception {
        if (utils != null) {
            utils.sleep(millis);
        } else {
            Thread.sleep(millis);
        }
    }
}
