package avik.qira_v2.utils;

import com.motorola.g11n.tools.avik.client.android.screenshot.hierarchy.AndroidHierarchy;
import com.motorola.g11n.tools.avik.common.metadata.AvikText;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Normalizer;

import avik.qira.utils.QiraStrings;

public final class QiraV2SlapTextDump {

    private static final Pattern FORMAT_PLACEHOLDER =
            Pattern.compile("%(?:[0-9]+\\$)?[a-zA-Z]");

    public enum AccessibilitySourceKind {
        TEXT,
        DESCRIPTION
    }

    /**
     * One exact owner-package accessibility value with its source semantics.
     * Text and contentDescription must remain distinguishable through metadata
     * reconstruction so linked description containers can be handled without
     * weakening text or icon-only description evidence.
     */
    public static final class AccessibilitySnapshotRecord {
        private final String value;
        private final AccessibilitySourceKind sourceKind;
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;
        private final String nodePath;

        private AccessibilitySnapshotRecord(
                String value,
                AccessibilitySourceKind sourceKind,
                int left,
                int top,
                int right,
                int bottom,
                String nodePath) {
            this.value = value;
            this.sourceKind = sourceKind;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.nodePath = nodePath;
        }

        public String getValue() {
            return value;
        }

        public AccessibilitySourceKind getSourceKind() {
            return sourceKind;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }

        public String getNodePath() {
            return nodePath;
        }

        private AvikText toAvikText() {
            return new AvikText(
                    value,
                    Collections.<String>emptyList(),
                    left,
                    top,
                    right,
                    bottom,
                    Boolean.FALSE);
        }
    }

    private QiraV2SlapTextDump() {
    }

    public static List<AvikText> dumpVisibleText(
            boolean includeDescription,
            Logger logger) {
        boolean effectiveIncludeDescription = true;
        List<AvikText> primary;
        try {
            primary = new AndroidHierarchy(
                    0,
                    effectiveIncludeDescription,
                    Collections.<String>emptySet()).dumpWindowHierarchy();
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 SLAP text dump failed: " + t.getMessage());
            }
            primary = new ArrayList<>();
        }

        // Workbench SLAP-parser bidi/message-ID recovery (RTL only).
        //
        // The workbench AndroidHierarchy runs every visible string through
        // StringLinkageUtils.decipherMessageId(). RTL text whose natural bidi
        // control marks happen to look like a SLAP marker (reproducibly, any
        // Arabic string that embeds a Latin run such as "Motorola") makes
        // decipherMessageId throw NumberFormatException; AndroidHierarchy
        // catches that PER WINDOW and drops the ENTIRE window's text. On ar-*
        // that silently removes every Qira onboarding string (Start CTA,
        // toggles, "I acknowledge", ...), so no stable Qira string-ID selector
        // can resolve and the flow is forced onto brittle key/coordinate
        // routes. Evidence: 136 "Can not dump strings" warnings in an ar-EG
        // run vs 0 in en-XM/de-DE/fr-FR/ja-JP.
        //
        // LTR locales never trigger the exception, so this recovery is gated on
        // RTL and LTR tier-1 behaviour is byte-identical (primary returned as
        // is). For RTL we read the accessibility tree directly - without ever
        // calling the throwing decipher - and merge back any text the parser
        // dropped, so the existing resolved-Qira-string selectors work exactly
        // as they do for en-XM.
        if (!isRtl()) {
            return primary;
        }
        List<AvikText> supplemental = dumpAccessibilityTextsDirect(logger);
        if (supplemental.isEmpty()) {
            return primary;
        }
        Set<String> seen = new HashSet<>();
        for (AvikText text : primary) {
            seen.add(dedupKey(text));
        }
        List<AvikText> merged = new ArrayList<>(primary);
        int recovered = 0;
        for (AvikText text : supplemental) {
            if (seen.add(dedupKey(text))) {
                merged.add(text);
                recovered++;
            }
        }
        if (logger != null && recovered > 0) {
            logger.info("QiraV2 SLAP RTL recovery: primary=" + primary.size()
                    + ", recovered=" + recovered + " text node(s) dropped by the"
                    + " workbench SLAP parser bidi/message-ID decipher exception.");
        }
        return merged;
    }

    /**
     * Returns direct accessibility text and content descriptions from all
     * currently visible windows. Capture code uses this only to fill metadata
     * nodes omitted by AndroidHierarchy; selectors continue to prefer the
     * normal hierarchy and resource-backed matching paths.
     */
    public static List<AvikText> dumpVisibleAccessibilityText(Logger logger) {
        return dumpAccessibilityTextsDirect(logger);
    }

    public static List<AvikText> dumpVisibleAccessibilityTextForPackage(
            String packageName,
            Logger logger) {
        return dumpAccessibilityTextsDirect(logger, packageName);
    }

    public static List<AccessibilitySnapshotRecord>
            dumpVisibleAccessibilitySnapshotForPackage(
                    String packageName,
                    Logger logger) {
        return dumpAccessibilitySnapshotDirect(logger, packageName);
    }

    /**
     * Links visible accessibility strings to message IDs decoded from Qira's
     * shipped en-XM Compose catalog. Qira's production-locale Compose nodes
     * expose localized text but omit the embedded SLAP marker consumed by the
     * stock Android hierarchy parser. The stable string ID bridges the decoded
     * en-XM marker to the current-locale value; no locale table or guessed
     * message ID is involved.
     */
    public static Map<String, List<String>> resolveCatalogMessageIdsForVisibleTexts(
            List<String> visibleTexts,
            Logger logger) {
        Map<String, List<String>> links = new LinkedHashMap<>();
        if (visibleTexts == null || visibleTexts.isEmpty()) {
            return links;
        }

        Map<String, List<String>> messageIdsByStringId =
                QiraV2SlapMessageIdIndex.get("com.lenovo.qira", logger);
        Map<String, String> currentStrings =
                QiraV2ComposeStrings.loadCurrentStringsSnapshot(
                        "com.lenovo.qira", null);
        Map<String, List<String>> resolved = new LinkedHashMap<>();
        int skippedPlaceholderOnlyEntries = 0;
        for (Map.Entry<String, List<String>> entry
                : messageIdsByStringId.entrySet()) {
            String value = currentStrings.get(entry.getKey());
            if (value != null && !value.isEmpty()) {
                if (isPlaceholderOnlyResourceValue(value)) {
                    skippedPlaceholderOnlyEntries++;
                    continue;
                }
                String normalized = stripDiacritics(
                        QiraStrings.stripBidiControls(value));
                List<String> ids = resolved.get(normalized);
                if (ids == null) {
                    ids = new ArrayList<>();
                    resolved.put(normalized, ids);
                }
                for (String messageId : entry.getValue()) {
                    if (!ids.contains(messageId)) {
                        ids.add(messageId);
                    }
                }
            }
        }

        int linkedNodeCount = 0;
        for (String rawVisible : visibleTexts) {
            if (rawVisible == null || rawVisible.trim().isEmpty()) {
                continue;
            }
            String visible = stripDiacritics(QiraStrings.stripBidiControls(rawVisible));
            LinkedHashSet<String> messageIds = new LinkedHashSet<>();
            for (Map.Entry<String, List<String>> candidate
                    : resolved.entrySet()) {
                if (matchesResolvedResourceValue(candidate.getKey(), visible)) {
                    messageIds.addAll(candidate.getValue());
                }
            }
            if (!messageIds.isEmpty()) {
                links.put(rawVisible, new ArrayList<>(messageIds));
                linkedNodeCount++;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 Compose SLAP linkage: visibleStrings="
                    + visibleTexts.size()
                    + ", resolvedCatalogEntries="
                    + resolved.size()
                    + ", skippedPlaceholderOnlyEntries="
                    + skippedPlaceholderOnlyEntries
                    + ", linkedNodes="
                    + linkedNodeCount);
        }
        return links;
    }

    /**
     * Reads visible text + on-screen bounds straight from the accessibility
     * tree (all interactive windows), bypassing the workbench SLAP parser's
     * {@code decipherMessageId} call that throws - and drops the whole window -
     * on RTL text. Message IDs are intentionally left empty: this path is only
     * used to recover text for RTL production locales (ar-*), which carry no
     * SLAP message IDs anyway; en-XM/pseudo-locale message IDs still come from
     * the primary {@link AndroidHierarchy} dump.
     */
    private static List<AvikText> dumpAccessibilityTextsDirect(Logger logger) {
        return dumpAccessibilityTextsDirect(logger, null);
    }

    private static List<AvikText> dumpAccessibilityTextsDirect(
            Logger logger,
            String packageNameFilter) {
        List<AvikText> out = new ArrayList<>();
        for (AccessibilitySnapshotRecord record
                : dumpAccessibilitySnapshotDirect(logger, packageNameFilter)) {
            out.add(record.toAvikText());
        }
        return out;
    }

    private static List<AccessibilitySnapshotRecord> dumpAccessibilitySnapshotDirect(
            Logger logger,
            String packageNameFilter) {
        List<AccessibilitySnapshotRecord> out = new ArrayList<>();
        UiAutomation automation;
        try {
            automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        } catch (Throwable t) {
            return out;
        }
        if (automation == null) {
            return out;
        }
        try {
            AccessibilityServiceInfo info = automation.getServiceInfo();
            if (info != null
                    && (info.flags & AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS)
                            == 0) {
                info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
                automation.setServiceInfo(info);
            }
        } catch (Throwable ignored) {
        }
        Map<String, AccessibilityNodeInfo> roots = new LinkedHashMap<>();
        try {
            List<AccessibilityWindowInfo> windows = automation.getWindows();
            if (windows != null) {
                for (AccessibilityWindowInfo window : windows) {
                    if (window == null) {
                        continue;
                    }
                    try {
                        AccessibilityNodeInfo root = window.getRoot();
                        if (root != null) {
                            roots.put("window-" + window.getId(), root);
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        try {
            AccessibilityNodeInfo active = automation.getRootInActiveWindow();
            if (active != null) {
                boolean alreadyPresent = false;
                for (AccessibilityNodeInfo root : roots.values()) {
                    if (active.equals(root)
                            || active.getWindowId() == root.getWindowId()) {
                        alreadyPresent = true;
                        break;
                    }
                }
                if (!alreadyPresent) {
                    roots.put("active", active);
                }
            }
        } catch (Throwable ignored) {
        }
        for (Map.Entry<String, AccessibilityNodeInfo> root : roots.entrySet()) {
            collectAccessibilityRecords(
                    root.getValue(),
                    root.getKey(),
                    out,
                    packageNameFilter);
        }
        return out;
    }

    private static void collectAccessibilityRecords(
            AccessibilityNodeInfo root,
            String rootPath,
            List<AccessibilitySnapshotRecord> out,
            String packageNameFilter) {
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        Deque<String> paths = new ArrayDeque<>();
        queue.add(root);
        paths.add(rootPath);
        Rect bounds = new Rect();
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            String nodePath = paths.poll();
            if (node == null) {
                continue;
            }
            try {
                CharSequence rawPackage = node.getPackageName();
                boolean packageMatches = packageNameFilter == null
                        || (rawPackage != null
                        && packageNameFilter.contentEquals(rawPackage));
                CharSequence rawText = node.getText();
                String text = rawText == null ? null : rawText.toString();
                boolean hasText = packageMatches
                        && emitNodeString(
                        node,
                        bounds,
                        text,
                        AccessibilitySourceKind.TEXT,
                        nodePath,
                        out);
                // Compose icon controls (Focus Zone bubble bar, the Settings
                // navigation-drawer "Menu" affordance, the Knowledge
                // Categories/Tags dropdown triggers, ...) carry their only
                // stable label in content-description with an EMPTY text node.
                // The workbench AndroidHierarchy dump surfaces those labels via
                // includeDescription=true in LTR, but on RTL that whole dump is
                // discarded by the decipherMessageId exception, so this direct
                // accessibility recovery must emit the content-description too -
                // otherwise ar-* loses the bubble bar, the drawer menu and the
                // Knowledge dropdowns even though they render on screen.
                CharSequence rawDesc = node.getContentDescription();
                String desc = rawDesc == null ? null : rawDesc.toString();
                if (packageMatches
                        && desc != null && !desc.trim().isEmpty()
                        && (!hasText || !desc.trim().equals(text == null ? "" : text.trim()))) {
                    emitNodeString(
                            node,
                            bounds,
                            desc,
                            AccessibilitySourceKind.DESCRIPTION,
                            nodePath,
                            out);
                }
                for (int i = 0; i < node.getChildCount(); i++) {
                    AccessibilityNodeInfo child = node.getChild(i);
                    if (child != null) {
                        queue.add(child);
                        paths.add(nodePath + "/" + i);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static boolean emitNodeString(
            AccessibilityNodeInfo node,
            Rect bounds,
            String value,
            AccessibilitySourceKind sourceKind,
            String nodePath,
            List<AccessibilitySnapshotRecord> out) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        node.getBoundsInScreen(bounds);
        if (bounds.right <= bounds.left || bounds.bottom <= bounds.top) {
            return false;
        }
        out.add(new AccessibilitySnapshotRecord(
                value,
                sourceKind,
                bounds.left,
                bounds.top,
                bounds.right,
                bounds.bottom,
                nodePath));
        return true;
    }

    private static String dedupKey(AvikText text) {
        String normalized = QiraStrings.stripBidiControls(text.getText());
        if (normalized != null) {
            normalized = normalized.trim();
        }
        int centerX = (text.getLeft() + text.getRight()) / 2;
        int centerY = (text.getTop() + text.getBottom()) / 2;
        return normalized + "@" + (centerX / 24) + "," + (centerY / 24);
    }

    public static AvikText findByMessageId(
            String messageId,
            boolean includeDescription,
            Logger logger) {
        if (messageId == null || messageId.isEmpty()) {
            return null;
        }
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        if (logger != null) {
            logger.info("QiraV2 SLAP text dump count=" + texts.size()
                    + ", includeDescription=" + includeDescription);
        }
        for (AvikText text : texts) {
            List<String> ids = text.getMessageIds();
            if (ids != null && ids.contains(messageId)) {
                return text;
            }
        }
        return null;
    }

    public static AvikText findByAnyMessageId(
            boolean includeDescription,
            Logger logger,
            String... messageIds) {
        if (messageIds == null || messageIds.length == 0) {
            return null;
        }
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        if (logger != null) {
            logger.info("QiraV2 SLAP text dump count=" + texts.size()
                    + ", includeDescription=" + includeDescription);
        }
        for (AvikText text : texts) {
            List<String> ids = text.getMessageIds();
            if (ids == null) {
                continue;
            }
            for (String messageId : messageIds) {
                if (messageId != null && !messageId.isEmpty() && ids.contains(messageId)) {
                    return text;
                }
            }
        }
        return null;
    }

    public static boolean clickByMessageId(
            UiDevice device,
            String messageId,
            boolean includeDescription,
            Logger logger) {
        if (device == null) {
            return false;
        }
        AvikText text = findByMessageId(messageId, includeDescription, logger);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP message-ID click refused; invalid bounds for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP message-ID click: messageId="
                    + messageId
                    + ", center="
                    + centerX
                    + ","
                    + centerY
                    + ", evidence="
                    + summarize(text));
        }
        return device.click(centerX, centerY);
    }

    public static boolean shellClickByMessageId(
            UiDevice device,
            String messageId,
            boolean includeDescription,
            Logger logger) {
        if (device == null) {
            return false;
        }
        AvikText text = findByMessageId(messageId, includeDescription, logger);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP shell message-ID click refused;"
                        + " invalid bounds for " + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP shell message-ID click: messageId="
                    + messageId
                    + ", center="
                    + centerX
                    + ","
                    + centerY
                    + ", evidence="
                    + summarize(text));
        }
        try {
            device.executeShellCommand("input tap " + centerX + " " + centerY);
            return true;
        } catch (Throwable shellFailure) {
            if (logger != null) {
                logger.info("QiraV2 SLAP shell message-ID click failed; falling back"
                        + " to UiDevice.click: " + shellFailure.getMessage());
            }
            return device.click(centerX, centerY);
        }
    }

    public static boolean clickByResolvedQiraStringResource(
            UiDevice device,
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }
        AvikText text = findByResolvedQiraStringResource(
                resourceName,
                includeDescription,
                logger);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP resource click refused; invalid bounds for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP resource click: resourceName="
                    + resourceName
                    + ", center="
                    + centerX
                    + ","
                    + centerY
                    + ", evidence="
                    + summarize(text));
        }
        return device.click(centerX, centerY);
    }

    /**
     * Activates exact resource-backed evidence through Android's input command.
     * This is used only after semantic ACTION_CLICK is unavailable; coordinates
     * come from the current resolved accessibility/SLAP node, never constants.
     */
    public static boolean shellClickByResolvedQiraStringResource(
            UiDevice device,
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }
        AvikText text = findByResolvedQiraStringResource(
                resourceName,
                includeDescription,
                logger);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 shell resource click: resourceName="
                    + resourceName
                    + ", center=" + centerX + "," + centerY
                    + ", evidence=" + summarize(text));
        }
        try {
            device.executeShellCommand("input tap " + centerX + " " + centerY);
            return true;
        } catch (Throwable shellFailure) {
            if (logger != null) {
                logger.info("QiraV2 shell resource click failed: resourceName="
                        + resourceName + ", error=" + shellFailure.getMessage());
            }
            return false;
        }
    }

    /**
     * Clicks a Qira Compose control through a value resolved from the Compose
     * catalog only. Use this when a Compose string ID is generic enough to
     * collide with an Android resource name (for example {@code exit}).
     */
    public static boolean clickByResolvedQiraComposeStringResource(
            UiDevice device,
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }
        AvikText text = findByResolvedQiraComposeStringResource(
                resourceName,
                includeDescription,
                logger);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP Compose resource click refused; invalid bounds for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP Compose resource click: resourceName="
                    + resourceName
                    + ", center="
                    + centerX
                    + ","
                    + centerY
                    + ", evidence="
                    + summarize(text));
        }
        return device.click(centerX, centerY);
    }

    /**
     * Clicks the clickable accessibility ancestor of a visible Compose string
     * resolved by its stable Qira resource ID. This is preferred over tapping
     * the text bounds because Compose commonly exposes a text child inside a
     * larger button node. The resource-backed evidence and overlapping bounds
     * keep the action tied to the intended foreground control.
     */
    public static boolean clickClickableAncestorByResolvedQiraStringResource(
            UiDevice device,
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }

        AvikText evidence = findByResolvedQiraStringResource(
                resourceName,
                includeDescription,
                logger);
        if (evidence == null) {
            return false;
        }
        return clickClickableAncestorForEvidence(device, resourceName, evidence, logger);
    }

    /**
     * Invokes the accessibility click action on the clickable ancestor of a
     * resource-backed Qira node. Some large Creator tiles only react through
     * their Compose semantics action; injecting a touch at the label/parent
     * center can be accepted by Android without invoking the tile.
     */
    public static boolean performAccessibilityClickByResolvedQiraStringResource(
            String resourceName,
            Logger logger) {
        if (resourceName == null || resourceName.isEmpty()) {
            return false;
        }
        String value = QiraStrings.getInstance().resolveQiraResourceName(resourceName);
        if (value == null || value.isEmpty()) {
            value = QiraV2ComposeStrings.resolve(
                    "com.lenovo.qira", resourceName, logger);
        }
        if (value == null || value.isEmpty()) {
            return false;
        }
        String defaultValue = QiraV2ComposeStrings.resolveDefault(
                "com.lenovo.qira", resourceName, logger);
        try {
            UiAutomation automation =
                    InstrumentationRegistry.getInstrumentation().getUiAutomation();
            if (automation == null) {
                return false;
            }
            Set<AccessibilityNodeInfo> roots = new LinkedHashSet<>();
            AccessibilityNodeInfo active = automation.getRootInActiveWindow();
            if (active != null) {
                roots.add(active);
            }
            List<AccessibilityWindowInfo> windows = automation.getWindows();
            if (windows != null) {
                for (AccessibilityWindowInfo window : windows) {
                    if (window == null) {
                        continue;
                    }
                    AccessibilityNodeInfo root = window.getRoot();
                    if (root != null) {
                        roots.add(root);
                    }
                }
            }
            for (AccessibilityNodeInfo root : roots) {
                if (performResolvedAccessibilityClick(
                        root, resourceName, value, defaultValue, logger)) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * Approved Style Sync exception: the Qira tile reports a clickable
     * accessibility ancestor, but both ACTION_CLICK and a center activation are
     * no-ops. Resolve the tile by its stable resource, then activate the icon
     * region at one quarter of that ancestor's height.
     */
    public static boolean clickApprovedUpperRegionByResolvedQiraStringResource(
            UiDevice device,
            String resourceName,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }
        AvikText evidence = findByResolvedQiraStringResource(
                resourceName, true, logger);
        if (evidence == null) {
            return false;
        }
        String expected = stripDiacritics(
                QiraStrings.stripBidiControls(evidence.getText()));
        try {
            for (UiObject2 node : device.findObjects(By.pkg("com.lenovo.qira"))) {
                Rect nodeBounds = node.getVisibleBounds();
                if (!overlapsEvidence(nodeBounds, evidence)
                        || (!matchesAccessibilityString(expected, node.getText())
                        && !matchesAccessibilityString(
                        expected, node.getContentDescription()))) {
                    continue;
                }
                UiObject2 tile = node;
                while (tile != null && !tile.isClickable()) {
                    tile = tile.getParent();
                }
                if (tile == null) {
                    continue;
                }
                Rect bounds = tile.getVisibleBounds();
                int targetX = bounds.centerX();
                int targetY = bounds.top + Math.max(1, bounds.height() / 4);
                if (logger != null) {
                    logger.info("QiraV2 approved Style Sync resource-anchored tap:"
                            + " resourceName=" + resourceName
                            + ", tileBounds=" + bounds
                            + ", target=" + targetX + "," + targetY);
                }
                return device.click(targetX, targetY);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean performResolvedAccessibilityClick(
            AccessibilityNodeInfo root,
            String resourceName,
            String value,
            String defaultValue,
            Logger logger) {
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            try {
                CharSequence packageName = node.getPackageName();
                if (packageName != null
                        && "com.lenovo.qira".contentEquals(packageName)
                        && (matchesNodeResolvedValue(node, value)
                        || matchesNodeResolvedValue(node, defaultValue))) {
                    AccessibilityNodeInfo clickable = node;
                    while (clickable != null && !clickable.isClickable()) {
                        clickable = clickable.getParent();
                    }
                    if (clickable != null
                            && clickable.performAction(
                            AccessibilityNodeInfo.ACTION_CLICK)) {
                        if (logger != null) {
                            logger.info("QiraV2 accessibility semantic click: resourceName="
                                    + resourceName);
                        }
                        return true;
                    }
                }
                for (int index = 0; index < node.getChildCount(); index++) {
                    AccessibilityNodeInfo child = node.getChild(index);
                    if (child != null) {
                        queue.add(child);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private static boolean matchesNodeResolvedValue(
            AccessibilityNodeInfo node,
            String resolvedValue) {
        if (resolvedValue == null || resolvedValue.isEmpty()) {
            return false;
        }
        CharSequence text = node.getText();
        CharSequence description = node.getContentDescription();
        return matchesResolvedResourceValue(
                QiraStrings.stripBidiControls(resolvedValue),
                QiraStrings.stripBidiControls(text == null ? null : text.toString()))
                || matchesResolvedResourceValue(
                QiraStrings.stripBidiControls(resolvedValue),
                QiraStrings.stripBidiControls(
                        description == null ? null : description.toString()));
    }

    /**
     * Same semantic click as {@link #clickClickableAncestorByResolvedQiraStringResource},
     * but resolves strictly from Qira's Compose catalog. Catalog resource IDs
     * such as {@code exit} can collide with unrelated Android {@code R.string}
     * names, so Compose-backed Qira controls must not be resolved through the
     * Android-resource-first lookup.
     */
    public static boolean clickClickableAncestorByResolvedQiraComposeStringResource(
            UiDevice device,
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (device == null || resourceName == null || resourceName.isEmpty()) {
            return false;
        }

        AvikText evidence = findByResolvedQiraComposeStringResource(
                resourceName,
                includeDescription,
                logger);
        if (evidence == null) {
            return false;
        }
        return clickClickableAncestorForEvidence(device, resourceName, evidence, logger);
    }

    private static boolean clickClickableAncestorForEvidence(
            UiDevice device,
            String resourceName,
            AvikText evidence,
            Logger logger) {
        String expected = stripDiacritics(QiraStrings.stripBidiControls(evidence.getText()));
        if (expected == null || expected.isEmpty()) {
            return false;
        }

        List<UiObject2> nodes;
        try {
            nodes = device.findObjects(By.pkg("com.lenovo.qira"));
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 resource semantic click could not enumerate Qira nodes for "
                        + resourceName + ": " + t.getMessage());
            }
            return false;
        }

        for (UiObject2 node : nodes) {
            try {
                Rect bounds = node.getVisibleBounds();
                if (!overlapsEvidence(bounds, evidence)) {
                    continue;
                }
                if (!matchesAccessibilityString(expected, node.getText())
                        && !matchesAccessibilityString(expected, node.getContentDescription())) {
                    continue;
                }

                UiObject2 clickable = node;
                while (clickable != null && !clickable.isClickable()) {
                    clickable = clickable.getParent();
                }
                if (clickable == null) {
                    continue;
                }

                Rect targetBounds = clickable.getVisibleBounds();
                clickable.click();
                if (logger != null) {
                    logger.info("QiraV2 resource semantic click: resourceName="
                            + resourceName
                            + ", evidence="
                            + summarize(evidence)
                            + ", targetBounds="
                            + targetBounds);
                }
                return true;
            } catch (Throwable ignored) {
                // Compose can replace a node while its transition is in flight.
                // Continue looking for the same stable resource-backed control.
            }
        }

        if (logger != null) {
            logger.info("QiraV2 resource semantic click unavailable: resourceName="
                    + resourceName
                    + ", evidence="
                    + summarize(evidence));
        }
        return false;
    }

    /**
     * Resolves every requested Qira string ID, then verifies all values against
     * one immutable hierarchy snapshot. This prevents strict multi-resource
     * postconditions from being assembled across different UI frames.
     */
    public static boolean areResolvedQiraStringResourcesVisibleInSingleSnapshot(
            boolean includeDescription,
            Logger logger,
            String... resourceNames) {
        if (resourceNames == null || resourceNames.length == 0) {
            return false;
        }
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        for (String resourceName : resourceNames) {
            if (resourceName == null || resourceName.isEmpty()) {
                return false;
            }
            String value = QiraStrings.getInstance().resolveQiraResourceName(resourceName);
            if (value == null || value.isEmpty()) {
                value = QiraV2ComposeStrings.resolve(
                        "com.lenovo.qira", resourceName, logger);
            }
            if (value == null || value.isEmpty()) {
                if (logger != null) {
                    logger.info("QiraV2 atomic resource evidence miss: resourceName="
                            + resourceName + " did not resolve.");
                }
                return false;
            }

            String normalizedValue = QiraStrings.stripBidiControls(value);
            String defaultValue = QiraV2ComposeStrings.resolveDefault(
                    "com.lenovo.qira", resourceName, logger);
            String normalizedDefaultValue = QiraStrings.stripBidiControls(defaultValue);
            boolean found = false;
            for (AvikText text : texts) {
                String visible = QiraStrings.stripBidiControls(text.getText());
                if (matchesResolvedResourceValue(normalizedValue, visible)
                        || (normalizedDefaultValue != null
                        && !normalizedDefaultValue.isEmpty()
                        && !normalizedDefaultValue.equals(normalizedValue)
                        && matchesResolvedResourceValue(
                                normalizedDefaultValue, visible))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (logger != null) {
                    logger.info("QiraV2 atomic resource evidence miss: resourceName="
                            + resourceName + ", snapshotCount=" + texts.size() + ".");
                }
                return false;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 atomic resource evidence matched "
                    + resourceNames.length + " resource(s) in one snapshot.");
        }
        return true;
    }

    /**
     * Verifies all requested resource values in one direct, package-filtered
     * accessibility snapshot. This is the RTL-safe fallback for windows that
     * AndroidHierarchy drops after a bidi/message-marker parse exception.
     */
    public static boolean areResolvedQiraStringResourcesVisibleInAccessibilitySnapshot(
            String packageName,
            Logger logger,
            String... resourceNames) {
        if (packageName == null || packageName.isEmpty()
                || resourceNames == null || resourceNames.length == 0) {
            return false;
        }
        List<AvikText> texts =
                dumpVisibleAccessibilityTextForPackage(packageName, logger);
        if (texts.isEmpty()) {
            return false;
        }
        for (String resourceName : resourceNames) {
            if (resourceName == null || resourceName.isEmpty()) {
                return false;
            }
            String value =
                    QiraStrings.getInstance().resolveQiraResourceName(resourceName);
            if (value == null || value.isEmpty()) {
                value = QiraV2ComposeStrings.resolve(
                        "com.lenovo.qira", resourceName, logger);
            }
            if (value == null || value.isEmpty()) {
                return false;
            }
            String normalizedValue = QiraStrings.stripBidiControls(value);
            String defaultValue = QiraV2ComposeStrings.resolveDefault(
                    "com.lenovo.qira", resourceName, logger);
            String normalizedDefaultValue =
                    QiraStrings.stripBidiControls(defaultValue);
            boolean found = false;
            for (AvikText text : texts) {
                String visible =
                        QiraStrings.stripBidiControls(text.getText());
                if (matchesResolvedResourceValue(normalizedValue, visible)
                        || (normalizedDefaultValue != null
                        && !normalizedDefaultValue.isEmpty()
                        && !normalizedDefaultValue.equals(normalizedValue)
                        && matchesResolvedResourceValue(
                                normalizedDefaultValue, visible))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (logger != null) {
                    logger.info("QiraV2 direct accessibility resource miss:"
                            + " package=" + packageName
                            + ", resourceName=" + resourceName
                            + ", snapshotCount=" + texts.size() + ".");
                }
                return false;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 direct accessibility resource evidence matched "
                    + resourceNames.length + " resource(s) for package="
                    + packageName + ".");
        }
        return true;
    }

    public static AvikText findByResolvedQiraStringResource(
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (resourceName == null || resourceName.isEmpty()) {
            return null;
        }
        String value = QiraStrings.getInstance().resolveQiraResourceName(resourceName);
        if ((value == null || value.isEmpty()) && logger != null) {
            logger.info("QiraV2 Android string resource miss for resourceName="
                    + resourceName
                    + "; trying Qira Compose resources.");
        }
        if (value == null || value.isEmpty()) {
            value = QiraV2ComposeStrings.resolve("com.lenovo.qira", resourceName, logger);
        }
        if (value == null || value.isEmpty()) {
            if (logger != null) {
                logger.info("QiraV2 SLAP resource evidence miss: resourceName="
                        + resourceName + " did not resolve");
            }
            return null;
        }
        String normalizedValue = QiraStrings.stripBidiControls(value);
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        if (logger != null) {
            logger.info("QiraV2 SLAP resource evidence text dump count=" + texts.size()
                    + ", resourceName=" + resourceName
                    + ", includeDescription=" + includeDescription);
        }
        for (AvikText text : texts) {
            String visible = QiraStrings.stripBidiControls(text.getText());
            if (!matchesResolvedResourceValue(normalizedValue, visible)) {
                continue;
            }
            if (logger != null) {
                logger.info("QiraV2 SLAP resource evidence: resourceName="
                        + resourceName
                        + ", evidence="
                        + summarize(text));
            }
            return text;
        }
        String defaultValue = QiraV2ComposeStrings.resolveDefault(
                "com.lenovo.qira",
                resourceName,
                logger);
        String normalizedDefaultValue = QiraStrings.stripBidiControls(defaultValue);
        if (normalizedDefaultValue != null
                && !normalizedDefaultValue.isEmpty()
                && !normalizedDefaultValue.equals(normalizedValue)) {
            for (AvikText text : texts) {
                String visible = QiraStrings.stripBidiControls(text.getText());
                if (!matchesResolvedResourceValue(normalizedDefaultValue, visible)) {
                    continue;
                }
                if (logger != null) {
                    logger.info("QiraV2 SLAP resource evidence: resourceName="
                            + resourceName
                            + ", defaultFallbackValue='"
                            + normalizedDefaultValue
                            + "', evidence="
                            + summarize(text));
                }
                return text;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP resource evidence miss: resourceName="
                    + resourceName + ", resolvedValue='" + normalizedValue + "'");
        }
        return null;
    }

    /**
     * Resolves {@code resourceName} directly from Qira's Compose catalog,
     * bypassing Android {@code R.string}. The Qira catalog deliberately has
     * concise IDs such as {@code exit} and {@code stay}; an Android resource
     * with the same name can be unrelated and must not shadow it.
     */
    public static AvikText findByResolvedQiraComposeStringResource(
            String resourceName,
            boolean includeDescription,
            Logger logger) {
        if (resourceName == null || resourceName.isEmpty()) {
            return null;
        }
        String value = QiraV2ComposeStrings.resolve(
                "com.lenovo.qira", resourceName, logger);
        if (value == null || value.isEmpty()) {
            if (logger != null) {
                logger.info("QiraV2 Compose resource evidence miss: resourceName="
                        + resourceName + " did not resolve");
            }
            return null;
        }

        String normalizedValue = QiraStrings.stripBidiControls(value);
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        if (logger != null) {
            logger.info("QiraV2 Compose resource evidence text dump count=" + texts.size()
                    + ", resourceName=" + resourceName
                    + ", includeDescription=" + includeDescription);
        }
        for (AvikText text : texts) {
            String visible = QiraStrings.stripBidiControls(text.getText());
            if (!matchesResolvedResourceValue(normalizedValue, visible)) {
                continue;
            }
            if (logger != null) {
                logger.info("QiraV2 Compose resource evidence: resourceName="
                        + resourceName
                        + ", evidence="
                        + summarize(text));
            }
            return text;
        }

        String defaultValue = QiraV2ComposeStrings.resolveDefault(
                "com.lenovo.qira",
                resourceName,
                logger);
        String normalizedDefaultValue = QiraStrings.stripBidiControls(defaultValue);
        if (normalizedDefaultValue != null
                && !normalizedDefaultValue.isEmpty()
                && !normalizedDefaultValue.equals(normalizedValue)) {
            for (AvikText text : texts) {
                String visible = QiraStrings.stripBidiControls(text.getText());
                if (!matchesResolvedResourceValue(normalizedDefaultValue, visible)) {
                    continue;
                }
                if (logger != null) {
                    logger.info("QiraV2 Compose resource evidence: resourceName="
                            + resourceName
                            + ", defaultFallbackValue='"
                            + normalizedDefaultValue
                            + "', evidence="
                            + summarize(text));
                }
                return text;
            }
        }
        // AndroidHierarchy currently omits some Compose icon-only
        // content-descriptions on LTR builds. Those descriptions are the only
        // stable identifier for actions such as Chat's Copy/Sources row. Use
        // the direct accessibility tree only after the ordinary hierarchy has
        // missed, and only when descriptions were requested, so normal capture
        // behavior remains unchanged while selector evidence stays
        // resource-backed across every locale.
        if (includeDescription) {
            List<AvikText> directTexts = dumpAccessibilityTextsDirect(
                    logger, "com.lenovo.qira");
            for (AvikText text : directTexts) {
                String visible = QiraStrings.stripBidiControls(text.getText());
                if (matchesResolvedResourceValue(normalizedValue, visible)) {
                    if (logger != null) {
                        logger.info("QiraV2 Compose resource accessibility evidence: resourceName="
                                + resourceName
                                + ", evidence="
                                + summarize(text));
                    }
                    return text;
                }
            }
            if (normalizedDefaultValue != null
                    && !normalizedDefaultValue.isEmpty()
                    && !normalizedDefaultValue.equals(normalizedValue)) {
                for (AvikText text : directTexts) {
                    String visible = QiraStrings.stripBidiControls(text.getText());
                    if (matchesResolvedResourceValue(normalizedDefaultValue, visible)) {
                        if (logger != null) {
                            logger.info("QiraV2 Compose resource accessibility evidence: resourceName="
                                    + resourceName
                                    + ", defaultFallbackValue='"
                                    + normalizedDefaultValue
                                    + "', evidence="
                                    + summarize(text));
                        }
                        return text;
                    }
                }
            }
        }
        if (logger != null) {
            logger.info("QiraV2 Compose resource evidence miss: resourceName="
                    + resourceName + ", resolvedValue='" + normalizedValue + "'");
        }
        return null;
    }

    public static AvikText findByAnyResolvedQiraStringResource(
            boolean includeDescription,
            Logger logger,
            String... resourceNames) {
        if (resourceNames == null || resourceNames.length == 0) {
            return null;
        }
        for (String resourceName : resourceNames) {
            AvikText text = findByResolvedQiraStringResource(
                    resourceName,
                    includeDescription,
                    logger);
            if (text != null) {
                return text;
            }
        }
        return null;
    }

    /**
     * Finds a SLAP text node whose value matches the <b>current-locale Compose
     * value</b> of one of the given English anchors. This is the qira_v2
     * bridge for the Focus Zone content sheets: their onboarding / CTA copy
     * lives in Qira's Compose catalog (not Android {@code R.string}), so the
     * message-ID / R.string-scan paths miss it in real locales. Resolving the
     * anchor through {@link QiraV2ComposeStrings#localizeEnglishAnchor} makes
     * these surfaces detectable/clickable by a stable Compose {@code stringId}
     * in every locale, with no visible-text or coordinate fallback.
     */
    public static AvikText findByEnglishAnchorCompose(
            String packageName,
            boolean includeDescription,
            Logger logger,
            String... englishAnchors) {
        if (packageName == null || packageName.isEmpty()
                || englishAnchors == null || englishAnchors.length == 0) {
            return null;
        }
        List<AvikText> texts = null;
        for (String anchor : englishAnchors) {
            if (anchor == null || anchor.isEmpty()) {
                continue;
            }
            String[] values = QiraV2ComposeStrings.localizeEnglishAnchor(
                    packageName, anchor, logger);
            if (values == null || values.length == 0) {
                continue;
            }
            if (texts == null) {
                texts = dumpVisibleText(includeDescription, logger);
            }
            for (String value : values) {
                String normalizedValue = stripDiacritics(QiraStrings.stripBidiControls(value));
                if (normalizedValue == null || normalizedValue.isEmpty()) {
                    continue;
                }
                for (AvikText text : texts) {
                    String visible = stripDiacritics(QiraStrings.stripBidiControls(text.getText()));
                    if (matchesResolvedResourceValue(normalizedValue, visible)) {
                        if (logger != null) {
                            logger.info("QiraV2 SLAP compose-anchor evidence: anchor='"
                                    + anchor + "', value='" + normalizedValue
                                    + "', evidence=" + summarize(text));
                        }
                        return text;
                    }
                }
            }
        }
        return null;
    }

    public static boolean clickByEnglishAnchorCompose(
            UiDevice device,
            String packageName,
            boolean includeDescription,
            Logger logger,
            String... englishAnchors) {
        if (device == null) {
            return false;
        }
        AvikText text = findByEnglishAnchorCompose(
                packageName, includeDescription, logger, englishAnchors);
        if (text == null) {
            return false;
        }
        int centerX = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        if (centerX <= 0 || centerY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP compose-anchor click refused; invalid bounds for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP compose-anchor click: center=" + centerX + "," + centerY
                    + ", evidence=" + summarize(text));
        }
        return device.click(centerX, centerY);
    }

    public static boolean clickLogicalEndOfMessageRow(
            UiDevice device,
            boolean includeDescription,
            Logger logger,
            String... anchorMessageIds) {
        if (device == null) {
            return false;
        }
        AvikText text = findByAnyMessageId(includeDescription, logger, anchorMessageIds);
        if (text == null) {
            return false;
        }
        int displayWidth = device.getDisplayWidth();
        if (displayWidth <= 0) {
            return false;
        }
        int rowHeight = Math.max(1, text.getBottom() - text.getTop());
        int targetX = trailingEdgeX(device, text, Math.max(72, rowHeight), 24);
        int targetY = text.getBottom() - Math.max(4, rowHeight / 8);
        if (targetX <= 0 || targetY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP row-end click refused; invalid target for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP row-end click: target="
                    + targetX
                    + ","
                    + targetY
                    + ", anchor="
                    + summarize(text));
        }
        return device.click(targetX, targetY);
    }

    public static boolean clickLogicalEndOfResourceRow(
            UiDevice device,
            boolean includeDescription,
            Logger logger,
            String... resourceNames) {
        if (device == null) {
            return false;
        }
        AvikText text = findByAnyResolvedQiraStringResource(
                includeDescription,
                logger,
                resourceNames);
        if (text == null) {
            return false;
        }
        int displayWidth = device.getDisplayWidth();
        if (displayWidth <= 0) {
            return false;
        }
        int rowHeight = Math.max(1, text.getBottom() - text.getTop());
        int targetX = trailingEdgeX(device, text, Math.max(72, rowHeight), 24);
        int targetY = text.getBottom() - Math.max(4, rowHeight / 8);
        if (targetX <= 0 || targetY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP resource row-end click refused; invalid target for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP resource row-end click: target="
                    + targetX
                    + ","
                    + targetY
                    + ", anchor="
                    + summarize(text));
        }
        return device.click(targetX, targetY);
    }

    public static boolean clickTrailingControlForMessageRow(
            UiDevice device,
            boolean includeDescription,
            Logger logger,
            String... anchorMessageIds) {
        if (device == null) {
            return false;
        }
        AvikText text = findByAnyMessageId(includeDescription, logger, anchorMessageIds);
        if (text == null) {
            return false;
        }
        int displayWidth = device.getDisplayWidth();
        if (displayWidth <= 0) {
            return false;
        }
        int rowHeight = Math.max(1, text.getBottom() - text.getTop());
        int targetX = trailingEdgeX(device, text, Math.max(72, rowHeight), 24);
        int targetY = text.getBottom() - Math.max(4, rowHeight / 8);
        if (targetX <= 0 || targetY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP trailing-control click refused; invalid target for "
                        + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP trailing-control click: target="
                    + targetX
                    + ","
                    + targetY
                    + ", anchor="
                    + summarize(text));
        }
        return device.click(targetX, targetY);
    }

    public static boolean clickTrailingControlForResourceRow(
            UiDevice device,
            boolean includeDescription,
            Logger logger,
            String... resourceNames) {
        if (device == null) {
            return false;
        }
        AvikText text = findByAnyResolvedQiraStringResource(
                includeDescription,
                logger,
                resourceNames);
        if (text == null) {
            return false;
        }
        int displayWidth = device.getDisplayWidth();
        if (displayWidth <= 0) {
            return false;
        }
        int rowHeight = Math.max(1, text.getBottom() - text.getTop());
        int targetX = trailingEdgeX(device, text, Math.max(96, rowHeight * 2), 32);
        int targetY = text.getTop() + (rowHeight / 2);
        if (targetX <= 0 || targetY <= 0) {
            if (logger != null) {
                logger.info("QiraV2 SLAP resource trailing-control click refused;"
                        + " invalid target for " + summarize(text));
            }
            return false;
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP resource trailing-control click: target="
                    + targetX
                    + ","
                    + targetY
                    + ", anchor="
                    + summarize(text));
        }
        return device.click(targetX, targetY);
    }

    public static AvikText findLargestTextInBand(
            boolean includeDescription,
            Logger logger,
            float topFraction,
            float bottomFraction) {
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        int displayBottom = maxBottom(texts);
        if (displayBottom <= 0) {
            return null;
        }
        int top = (int) (displayBottom * topFraction);
        int bottom = (int) (displayBottom * bottomFraction);
        AvikText best = null;
        int bestArea = 0;
        for (AvikText text : texts) {
            int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
            if (centerY < top || centerY > bottom) {
                continue;
            }
            int width = Math.max(0, text.getRight() - text.getLeft());
            int height = Math.max(0, text.getBottom() - text.getTop());
            int area = width * height;
            if (area > bestArea) {
                best = text;
                bestArea = area;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP largest text in band "
                    + topFraction + "-" + bottomFraction + ": " + summarize(best));
        }
        return best;
    }

    public static AvikText findLowestWideTextInBand(
            boolean includeDescription,
            Logger logger,
            float topFraction,
            float bottomFraction,
            float minWidthFraction) {
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        int displayBottom = maxBottom(texts);
        int displayRight = maxRight(texts);
        if (displayBottom <= 0 || displayRight <= 0) {
            return null;
        }
        int top = (int) (displayBottom * topFraction);
        int bottom = (int) (displayBottom * bottomFraction);
        int minWidth = (int) (displayRight * minWidthFraction);
        AvikText best = null;
        for (AvikText text : texts) {
            int centerY = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
            int width = text.getRight() - text.getLeft();
            int height = text.getBottom() - text.getTop();
            if (centerY < top || centerY > bottom || width < minWidth
                    || height <= 0 || height > displayBottom / 6) {
                continue;
            }
            if (best == null || text.getBottom() > best.getBottom()) {
                best = text;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP lowest wide text in band "
                    + topFraction + "-" + bottomFraction + ": " + summarize(best));
        }
        return best;
    }

    public static void logVisibleTextSummaries(
            String label,
            boolean includeDescription,
            Logger logger) {
        if (logger == null) {
            return;
        }
        List<AvikText> texts = dumpVisibleText(includeDescription, logger);
        logger.info(label + ": count=" + texts.size());
        for (AvikText text : texts) {
            logger.info(label + ": " + summarize(text));
        }
    }

    public static String summarize(AvikText text) {
        if (text == null) {
            return "<none>";
        }
        return "text='" + safe(QiraStrings.stripBidiControls(text.getText())) + "'"
                + ", messageIds=" + text.getMessageIds()
                + ", bounds=" + text.getLeft()
                + "," + text.getTop()
                + "," + text.getRight()
                + "," + text.getBottom();
    }

    private static String safe(String value) {
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ');
    }

    private static int maxBottom(List<AvikText> texts) {
        int max = 0;
        for (AvikText text : texts) {
            max = Math.max(max, text.getBottom());
        }
        return max;
    }

    private static int maxRight(List<AvikText> texts) {
        int max = 0;
        for (AvikText text : texts) {
            max = Math.max(max, text.getRight());
        }
        return max;
    }

    /**
     * True when the active Qira UI locale lays out right-to-left. Row-end /
     * trailing-control targets are computed off the anchor's own bounds and
     * this flag, so the "logical end of the row" is to the right in LTR and to
     * the left in RTL - never a hard-coded side.
     */
    private static boolean isRtl() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * X coordinate just past the trailing edge of {@code text}'s row: to the
     * right of {@code getRight()} in LTR, to the left of {@code getLeft()} in
     * RTL. {@code gap} is the nominal offset past the edge; the result is
     * clamped inside {@code [clampMargin, displayWidth - clampMargin]}.
     */
    private static int trailingEdgeX(UiDevice device, AvikText text, int gap, int clampMargin) {
        int displayWidth = device.getDisplayWidth();
        if (isRtl()) {
            return Math.max(clampMargin, text.getLeft() - gap);
        }
        return Math.min(displayWidth - clampMargin, text.getRight() + gap);
    }

    /**
     * Removes Arabic tashkeel / combining diacritics (harakat, superscript
     * alef, tatweel) so a resolved Compose value like {@code "أُقرّ"} matches
     * an on-screen node that renders the same word with different / no
     * diacritics. No-op for Latin/CJK text.
     */
    private static String stripDiacritics(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder sb = null;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            boolean diacritic = (c >= '\u064B' && c <= '\u065F')
                    || c == '\u0640' /* tatweel */
                    || c == '\u0670'
                    || (c >= '\u06D6' && c <= '\u06ED');
            if (diacritic) {
                if (sb == null) {
                    sb = new StringBuilder(value.length());
                    sb.append(value, 0, i);
                }
                continue;
            }
            if (sb != null) {
                sb.append(c);
            }
        }
        return sb != null ? sb.toString() : value;
    }

    private static boolean overlapsEvidence(Rect bounds, AvikText evidence) {
        if (bounds == null || bounds.isEmpty() || evidence == null) {
            return false;
        }
        return bounds.left < evidence.getRight()
                && bounds.right > evidence.getLeft()
                && bounds.top < evidence.getBottom()
                && bounds.bottom > evidence.getTop();
    }

    private static boolean matchesAccessibilityString(String expected, CharSequence candidate) {
        if (candidate == null) {
            return false;
        }
        String visible = stripDiacritics(QiraStrings.stripBidiControls(candidate.toString()));
        return matchesResolvedResourceValue(expected, visible);
    }

    private static boolean matchesResolvedResourceValue(String expected, String visible) {
        expected = normalizeResourceMatchValue(expected);
        visible = normalizeResourceMatchValue(visible);
        if (expected == null || visible == null) {
            return false;
        }
        // A value such as "%1$s" has no literal identity. Turning it into
        // ".+?" makes every non-empty accessibility value look like the same
        // resource and attaches that resource's Message ID to unrelated,
        // dynamic text. Parameterized values with a real literal prefix or
        // suffix remain eligible, including reordered numbered placeholders.
        if (isPlaceholderOnlyResourceValue(expected)) {
            return false;
        }
        if (expected.equals(visible)
                || expected.equalsIgnoreCase(visible)) {
            return true;
        }
        if (!expected.contains("%")) {
            return false;
        }
        String trailingPlaceholderPrefix = expected
                .replaceFirst("\\s*%[0-9]*\\$?s\\s*$", "")
                .trim();
        if (!trailingPlaceholderPrefix.equals(expected)
                && trailingPlaceholderPrefix.equalsIgnoreCase(visible.trim())) {
            return true;
        }
        String pattern = buildPlaceholderPattern(expected, false);
        int patternFlags = Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        if (Pattern.compile("^" + pattern + "$", patternFlags).matcher(visible).matches()) {
            return true;
        }

        String optionalTrailingPlaceholderPattern = buildPlaceholderPattern(expected, true);
        return Pattern.compile(
                "^" + optionalTrailingPlaceholderPattern.trim() + "$",
                patternFlags).matcher(visible).matches();
    }

    private static String buildPlaceholderPattern(String value, boolean allowEmptyPlaceholders) {
        Matcher matcher = FORMAT_PLACEHOLDER.matcher(value);
        StringBuilder pattern = new StringBuilder(value.length() + 16);
        int cursor = 0;
        while (matcher.find()) {
            pattern.append(Pattern.quote(value.substring(cursor, matcher.start())));
            pattern.append(allowEmptyPlaceholders ? ".*?" : ".+?");
            cursor = matcher.end();
        }
        pattern.append(Pattern.quote(value.substring(cursor)));
        return pattern.toString();
    }

    private static boolean isPlaceholderOnlyResourceValue(String value) {
        String normalized = normalizeResourceMatchValue(value);
        if (normalized == null || normalized.isEmpty()) {
            return false;
        }
        Matcher matcher = FORMAT_PLACEHOLDER.matcher(normalized);
        int cursor = 0;
        boolean foundPlaceholder = false;
        while (matcher.find()) {
            foundPlaceholder = true;
            if (hasLiteralIdentity(normalized, cursor, matcher.start())) {
                return false;
            }
            cursor = matcher.end();
        }
        return foundPlaceholder
                && !hasLiteralIdentity(normalized, cursor, normalized.length());
    }

    private static boolean hasLiteralIdentity(String value, int start, int end) {
        for (int offset = start; offset < end;) {
            int codePoint = value.codePointAt(offset);
            if (Character.isLetterOrDigit(codePoint)) {
                return true;
            }
            offset += Character.charCount(codePoint);
        }
        return false;
    }

    /**
     * Accessibility and Compose can represent the same localized string with
     * canonical-equivalent Unicode, non-breaking spaces, or invisible format
     * marks. Normalize only those presentation differences before comparing a
     * runtime-resolved Qira resource to its visible Avik text. This keeps the
     * selector resource-backed while avoiding locale-specific string variants.
     */
    private static String normalizeResourceMatchValue(String value) {
        if (value == null) {
            return null;
        }
        String canonical = Normalizer.normalize(value, Normalizer.Form.NFC);
        StringBuilder normalized = new StringBuilder(canonical.length());
        boolean pendingSpace = false;
        for (int i = 0; i < canonical.length(); i++) {
            char c = canonical.charAt(i);
            if (Character.getType(c) == Character.FORMAT) {
                continue;
            }
            if (c == '\u2026') {
                if (pendingSpace) {
                    normalized.append(' ');
                    pendingSpace = false;
                }
                normalized.append("...");
                continue;
            }
            if (c == '\u2018' || c == '\u2019' || c == '\u201B') {
                c = '\'';
            } else if (c == '\u201C' || c == '\u201D') {
                c = '"';
            } else if ((c >= '\u2010' && c <= '\u2015') || c == '\u2212') {
                c = '-';
            }
            boolean whitespace = Character.isWhitespace(c)
                    || c == '\u00A0'
                    || c == '\u2007'
                    || c == '\u202F';
            if (whitespace) {
                pendingSpace = normalized.length() > 0;
                continue;
            }
            if (pendingSpace) {
                normalized.append(' ');
                pendingSpace = false;
            }
            normalized.append(c);
        }
        return normalized.toString().trim();
    }
}
