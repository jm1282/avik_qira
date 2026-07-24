package avik.qira_v2.utils;

import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.screenshot.service.AndroidScreenProcessService;
import com.motorola.g11n.tools.avik.client.android.util.AvikProperties;
import com.motorola.g11n.tools.avik.common.metadata.AvikText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira.utils.QiraUiDumper;

public final class QiraV2CaptureArtifacts {

    private static final String QIRA_PACKAGE = "com.lenovo.qira";
    private static final String GOOGLE_PERMISSION_CONTROLLER =
            "com.google.android.permissioncontroller";
    private static final String PLATFORM_PERMISSION_CONTROLLER =
            "com.android.permissioncontroller";
    private static final String ACTION_CORE_PACKAGE = "com.motorola.actioncore";
    private static final String ACCESSIBILITY_SOURCE_KIND_FIELD =
            "qiraAccessibilitySourceKind";
    private static final String ACCESSIBILITY_NODE_PATH_FIELD =
            "qiraAccessibilityNodePath";
    private static final double NEAR_FULL_SCREEN_AREA_RATIO = 0.80d;
    private static final Pattern FOCUSED_COMPONENT_PATTERN =
            Pattern.compile("([A-Za-z0-9_.$]+/[A-Za-z0-9_.$]+)");

    private QiraV2CaptureArtifacts() {
    }

    public static void captureSlapScreenshot(
            AvikHandler handler,
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            String screenName) throws Exception {
        OwnerPolicy ownerPolicy = OwnerPolicy.forTag(screenName);
        String locale = config == null ? "" : config.getLocale();
        if (logger != null) {
            logger.info("Capture Screen: " + screenName);
            logger.info("QiraV2 screenshot flags: includeText=true,"
                    + " includeDescription=true, dumpUi=true, expectedOwner="
                    + ownerPolicy.describe());
        }

        try {
            if (device != null) {
                device.waitForIdle(1000L);
            }
        } catch (Throwable ignored) {
        }

        String beforePackage = currentPackage(device);
        requireExpectedOwner(
                device,
                utils,
                logger,
                screenName,
                locale,
                ownerPolicy,
                beforePackage,
                null,
                "before_capture");

        Throwable last = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                String attemptPackage = currentPackage(device);
                requireExpectedOwner(
                        device,
                        utils,
                        logger,
                        screenName,
                        locale,
                        ownerPolicy,
                        attemptPackage,
                        null,
                        "before_attempt_" + attempt);
                beforePackage = attemptPackage;
                handler.takeScreenshot(screenName, true, true);
                last = null;
                break;
            } catch (Throwable t) {
                last = t;
                if (logger != null) {
                    logger.info("QiraV2 screenshot failed for " + screenName
                            + " (attempt " + attempt + "/2): " + t.getMessage());
                }
                try {
                    if (utils != null) {
                        utils.sleep(400L);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        if (last != null) {
            throw new IllegalStateException(
                    "Unable to capture SLAP screenshot for " + screenName, last);
        }

        String afterPackage = currentPackage(device);
        requireExpectedOwner(
                device,
                utils,
                logger,
                screenName,
                locale,
                ownerPolicy,
                afterPackage,
                null,
                "after_capture");
        if (!beforePackage.equals(afterPackage)) {
            failOwnerCheck(
                    device,
                    utils,
                    logger,
                    screenName,
                    locale,
                    ownerPolicy,
                    beforePackage + " -> " + afterPackage,
                    null,
                    "package_changed_during_capture");
        }

        MetadataLinkResult metadataLinks;
        try {
            metadataLinks = scopeAndEnrichMetadata(
                    screenName,
                    ownerPolicy,
                    afterPackage,
                    logger);
        } catch (OwnerMismatchException mismatch) {
            failOwnerCheck(
                    device,
                    utils,
                    logger,
                    screenName,
                    locale,
                    ownerPolicy,
                    mismatch.actualPackage,
                    mismatch.activity,
                    "metadata_owner");
            return;
        } catch (Exception correctionFailure) {
            String activity = currentActivity(device);
            String evidencePath = dumpDiagnosticEvidence(
                    device,
                    screenName + "_metadata_scope_failure",
                    afterPackage,
                    "tag=" + screenName
                            + "; expectedOwner=" + ownerPolicy.describe()
                            + "; actualPackage=" + afterPackage
                            + "; activity=" + activity
                            + "; locale=" + locale
                            + "; error=" + safeLogValue(correctionFailure.getMessage()));
            String quarantineStatus =
                    quarantineLatestCapture(evidencePath, logger);
            if (logger != null) {
                logger.info("QiraV2 metadata correction failure:"
                        + " tag=" + screenName
                        + ", expectedOwner=" + ownerPolicy.describe()
                        + ", actualPackage=" + afterPackage
                        + ", activity=" + activity
                        + ", locale=" + locale
                        + ", evidenceDumpPath=" + evidencePath
                        + ", quarantineStatus=" + quarantineStatus
                        + ", errorType=" + correctionFailure.getClass().getName()
                        + ", error=" + safeLogValue(correctionFailure.getMessage()));
            }
            throw new IllegalStateException(
                    "Owner-scoped metadata correction failed for " + screenName
                            + "; capture rejected. Evidence: " + evidencePath
                            + "; quarantine=" + quarantineStatus,
                    correctionFailure);
        }

        if (logger != null) {
            logger.info("QiraV2 owner-scoped capture:"
                    + " tag=" + screenName
                    + ", locale=" + locale
                    + ", expectedOwner=" + ownerPolicy.describe()
                    + ", actualPackage=" + afterPackage
                    + ", originalCount=" + metadataLinks.originalCount
                    + ", retainedCount=" + metadataLinks.retainedCount
                    + ", foreignRemovedCount=" + metadataLinks.foreignRemovedCount
                    + ", appendedCount=" + metadataLinks.accessibilityNodesAdded
                    + ", finalCount=" + metadataLinks.finalCount
                    + ", linkedNodeCount=" + metadataLinks.linkedNodes
                    + ", idsAdded=" + metadataLinks.messageIdsAdded
                    + ", descriptionContainerIdsSuppressed="
                    + metadataLinks.descriptionContainerIdsSuppressed
                    + ", nearFullCandidates=" + metadataLinks.nearFullCandidates
                    + ", containerCandidates=" + metadataLinks.containerCandidates
                    + ", geometryOnlyOverlapCandidates="
                    + metadataLinks.geometryOnlyOverlapCandidates
                    + ", remainingCandidateDetails="
                    + metadataLinks.candidateDetails);
        }

        try {
            if (device != null) {
                QiraUiDumper.dump(
                        device,
                        afterPackage,
                        screenName,
                        "owner-scoped capture; expectedOwner="
                                + ownerPolicy.describe());
            }
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 UI dump failed for " + screenName
                        + ": " + t.getMessage());
            }
        }
    }

    /**
     * The published AViK client flattens every visible accessibility window,
     * and its metadata records do not retain source-package identity. Build a
     * fresh, single-frame owner-package snapshot after the synchronous AViK
     * call, then retain only metadata records whose exact normalized
     * text/description and bounds occur in that owner snapshot.
     */
    private static MetadataLinkResult scopeAndEnrichMetadata(
            String screenName,
            OwnerPolicy ownerPolicy,
            String actualPackage,
            Logger logger) throws Exception {
        String screenHash = AndroidScreenProcessService.INSTANCE
                .getScreenshotService()
                .getScreenHash();
        if (screenHash == null || screenHash.isEmpty()) {
            throw new IllegalStateException(
                    "Avik did not expose a screenshot hash for owner-scoped metadata.");
        }

        File metadataFile = new File(
                AvikProperties.INSTANCE.getJSONFilePath(screenHash));
        if (!metadataFile.isFile()) {
            throw new IllegalStateException(
                    "Avik screenshot metadata was not written for hash " + screenHash + ".");
        }

        JSONObject metadata = new JSONObject(new String(
                Files.readAllBytes(metadataFile.toPath()), StandardCharsets.UTF_8));
        String metadataName = metadata.optString("name", "");
        String metadataHash = metadata.optString("hash", "");
        if (!screenName.equals(metadataName) || !screenHash.equals(metadataHash)) {
            throw new IllegalStateException(
                    "Latest AViK metadata identity mismatch: requestedTag=" + screenName
                            + ", metadataTag=" + metadataName
                            + ", requestedHash=" + screenHash
                            + ", metadataHash=" + metadataHash + ".");
        }

        String metadataPackage = metadata.optString("packageName", "");
        String metadataActivity = metadata.optString("activity", "");
        if (!ownerPolicy.accepts(metadataPackage)
                || !actualPackage.equals(metadataPackage)) {
            throw new OwnerMismatchException(metadataPackage, metadataActivity);
        }

        int width = metadata.optInt("width", 0);
        int height = metadata.optInt("height", 0);
        if (width <= 0 || height <= 0) {
            throw new IllegalStateException(
                    "Avik metadata has invalid dimensions for hash " + screenHash
                            + ": " + width + "x" + height + ".");
        }

        JSONArray originalTexts = metadata.optJSONArray("avikTexts");
        if (originalTexts == null) {
            throw new IllegalStateException(
                    "Avik metadata for hash " + screenHash + " has no avikTexts array.");
        }

        List<QiraV2SlapTextDump.AccessibilitySnapshotRecord> accessibilityTexts =
                QiraV2SlapTextDump.dumpVisibleAccessibilitySnapshotForPackage(
                        actualPackage, logger);
        Map<String, AccessibilityRecord> ownerRecords =
                normalizedAccessibilityRecords(accessibilityTexts, width, height);
        if (ownerRecords.isEmpty()) {
            throw new IllegalStateException(
                    "No accessibility text or description evidence remained for owner "
                            + actualPackage + " after capture.");
        }

        int originalCount = originalTexts.length();
        JSONArray retainedTexts = new JSONArray();
        Set<String> retainedKeys = new LinkedHashSet<>();
        Map<String, List<AccessibilityRecord>> ownerRecordsByBaseKey =
                accessibilityRecordsByBaseKey(ownerRecords);
        int sourceKindAnnotationsAdded = 0;
        for (int index = 0; index < originalTexts.length(); index++) {
            JSONObject text = originalTexts.optJSONObject(index);
            if (text == null) {
                continue;
            }
            String baseKey = metadataNodeBaseKey(
                    text.optString("text", ""),
                    text.optInt("left"),
                    text.optInt("top"),
                    text.optInt("right"),
                    text.optInt("bottom"));
            AccessibilityRecord ownerRecord = selectOwnerRecord(
                    ownerRecordsByBaseKey.get(baseKey), text, retainedKeys);
            if (ownerRecord == null) {
                continue;
            }
            if (!ownerRecord.sourceKind.name().equals(
                    text.optString(ACCESSIBILITY_SOURCE_KIND_FIELD, ""))) {
                text.put(
                        ACCESSIBILITY_SOURCE_KIND_FIELD,
                        ownerRecord.sourceKind.name());
                sourceKindAnnotationsAdded++;
            }
            if (!ownerRecord.nodePath.equals(
                    text.optString(ACCESSIBILITY_NODE_PATH_FIELD, ""))) {
                text.put(ACCESSIBILITY_NODE_PATH_FIELD, ownerRecord.nodePath);
                sourceKindAnnotationsAdded++;
            }
            retainedTexts.put(text);
            retainedKeys.add(ownerRecord.key);
        }
        int retainedCount = retainedTexts.length();
        int foreignRemovedCount = originalCount - retainedCount;

        // AndroidHierarchy can drop an entire RTL window when its marker parser
        // mistakes natural bidi controls for a SLAP marker. Recover missing
        // records for every already-verified owner, not only Qira. The direct
        // snapshot is package-filtered to actualPackage, and system-owner
        // records retain empty Message IDs rather than inventing Qira linkage.
        int appendedCount = 0;
        for (Map.Entry<String, AccessibilityRecord> entry
                : ownerRecords.entrySet()) {
            if (!retainedKeys.add(entry.getKey())) {
                continue;
            }
            AccessibilityRecord record = entry.getValue();
            JSONObject node = new JSONObject();
            node.put("text", record.text);
            node.put("messageIds", new JSONArray());
            node.put("left", record.left);
            node.put("top", record.top);
            node.put("right", record.right);
            node.put("bottom", record.bottom);
            node.put("isMarquee", false);
            node.put(
                    ACCESSIBILITY_SOURCE_KIND_FIELD,
                    record.sourceKind.name());
            node.put(ACCESSIBILITY_NODE_PATH_FIELD, record.nodePath);
            retainedTexts.put(node);
            appendedCount++;
        }

        if (retainedTexts.length() == 0) {
            throw new IllegalStateException(
                    "No owner-scoped AViK text or description evidence remained for "
                            + actualPackage + "; refusing to retain an unowned capture.");
        }
        metadata.put("avikTexts", retainedTexts);

        int messageIdsAdded = 0;
        if (ownerPolicy.isQira()) {
            List<String> visibleTexts = new ArrayList<>();
            for (int index = 0; index < retainedTexts.length(); index++) {
                JSONObject text = retainedTexts.optJSONObject(index);
                if (text == null) {
                    continue;
                }
                String value = text.optString("text", "");
                if (!value.trim().isEmpty()) {
                    visibleTexts.add(value);
                }
            }

            Map<String, List<String>> messageIdsByText =
                    QiraV2SlapTextDump.resolveCatalogMessageIdsForVisibleTexts(
                            visibleTexts, logger);
            for (int index = 0; index < retainedTexts.length(); index++) {
                JSONObject text = retainedTexts.optJSONObject(index);
                if (text == null) {
                    continue;
                }
                List<String> resolvedIds =
                        messageIdsByText.get(text.optString("text", ""));
                if (resolvedIds == null || resolvedIds.isEmpty()) {
                    continue;
                }

                LinkedHashSet<String> messageIds = new LinkedHashSet<>();
                JSONArray existing = text.optJSONArray("messageIds");
                if (existing != null) {
                    for (int idIndex = 0; idIndex < existing.length(); idIndex++) {
                        String existingId = existing.optString(idIndex, "");
                        if (!existingId.isEmpty()) {
                            messageIds.add(existingId);
                        }
                    }
                }
                int before = messageIds.size();
                messageIds.addAll(resolvedIds);
                if (messageIds.size() == before) {
                    continue;
                }

                JSONArray linkedIds = new JSONArray();
                for (String messageId : messageIds) {
                    linkedIds.put(messageId);
                }
                text.put("messageIds", linkedIds);
                messageIdsAdded += messageIds.size() - before;
            }
        }

        int descriptionContainerIdsSuppressed = ownerPolicy.isQira()
                ? suppressLinkedDescriptionContainers(retainedTexts) : 0;
        int finalLinkedNodes = countLinkedRecords(retainedTexts);
        CandidateSummary candidates =
                analyzeRemainingCandidates(retainedTexts, width, height);
        boolean metadataChanged = foreignRemovedCount > 0
                || appendedCount > 0
                || messageIdsAdded > 0
                || sourceKindAnnotationsAdded > 0
                || descriptionContainerIdsSuppressed > 0;
        if (metadataChanged) {
            replaceMetadataAtomically(metadataFile, metadata, logger);
        }
        return new MetadataLinkResult(
                originalCount,
                retainedCount,
                foreignRemovedCount,
                retainedTexts.length(),
                finalLinkedNodes,
                messageIdsAdded,
                appendedCount,
                descriptionContainerIdsSuppressed,
                candidates.nearFullCount,
                candidates.containerCount,
                candidates.geometryOnlyOverlapCount,
                candidates.details);
    }

    private static Map<String, AccessibilityRecord> normalizedAccessibilityRecords(
            List<QiraV2SlapTextDump.AccessibilitySnapshotRecord> texts,
            int width,
            int height) {
        Map<String, AccessibilityRecord> records = new LinkedHashMap<>();
        if (texts == null) {
            return records;
        }
        for (QiraV2SlapTextDump.AccessibilitySnapshotRecord text : texts) {
            if (text == null || text.getValue() == null
                    || text.getValue().trim().isEmpty()) {
                continue;
            }
            int left = Math.max(0, Math.min(width, text.getLeft()));
            int top = Math.max(0, Math.min(height, text.getTop()));
            int right = Math.max(0, Math.min(width, text.getRight()));
            int bottom = Math.max(0, Math.min(height, text.getBottom()));
            if (right <= left || bottom <= top) {
                continue;
            }
            AccessibilityRecord record = new AccessibilityRecord(
                    text.getValue(),
                    text.getSourceKind(),
                    text.getNodePath(),
                    left,
                    top,
                    right,
                    bottom);
            if (!records.containsKey(record.key)) {
                records.put(record.key, record);
            }
        }
        return records;
    }

    private static Map<String, List<AccessibilityRecord>>
            accessibilityRecordsByBaseKey(
                    Map<String, AccessibilityRecord> records) {
        Map<String, List<AccessibilityRecord>> byBase = new LinkedHashMap<>();
        for (AccessibilityRecord record : records.values()) {
            List<AccessibilityRecord> matches = byBase.get(record.baseKey);
            if (matches == null) {
                matches = new ArrayList<>();
                byBase.put(record.baseKey, matches);
            }
            matches.add(record);
        }
        return byBase;
    }

    private static AccessibilityRecord selectOwnerRecord(
            List<AccessibilityRecord> candidates,
            JSONObject metadataRecord,
            Set<String> retainedKeys) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        String existingSource = metadataRecord.optString(
                ACCESSIBILITY_SOURCE_KIND_FIELD, "");
        String existingPath = metadataRecord.optString(
                ACCESSIBILITY_NODE_PATH_FIELD, "");
        if (!existingSource.isEmpty() || !existingPath.isEmpty()) {
            for (AccessibilityRecord candidate : candidates) {
                boolean sourceMatches = existingSource.isEmpty()
                        || candidate.sourceKind.name().equals(existingSource);
                boolean pathMatches = existingPath.isEmpty()
                        || candidate.nodePath.equals(existingPath);
                if (sourceMatches
                        && pathMatches
                        && !retainedKeys.contains(candidate.key)) {
                    return candidate;
                }
            }
        }
        if (candidates.size() == 1
                && !retainedKeys.contains(candidates.get(0).key)) {
            return candidates.get(0);
        }
        // If distinct accessibility nodes expose the same value and bounds,
        // conservatively treat the retained AViK record as text-owned whenever
        // a TEXT candidate exists. This prevents an unprovable description
        // classification from clearing a legitimate text link; the independent
        // DESCRIPTION record is appended with its exact source key.
        for (AccessibilityRecord candidate : candidates) {
            if (candidate.sourceKind
                    == QiraV2SlapTextDump.AccessibilitySourceKind.TEXT
                    && !retainedKeys.contains(candidate.key)) {
                return candidate;
            }
        }
        for (AccessibilityRecord candidate : candidates) {
            if (!retainedKeys.contains(candidate.key)) {
                return candidate;
            }
        }
        return null;
    }

    private static void replaceMetadataAtomically(
            File metadataFile,
            JSONObject metadata,
            Logger logger) throws Exception {
        Path target = metadataFile.toPath();
        Path temp = new File(
                metadataFile.getParentFile(),
                metadataFile.getName() + ".qira-v2.tmp").toPath();
        byte[] serialized = metadata.toString().getBytes(StandardCharsets.UTF_8);
        Files.write(temp, serialized);

        // Parse the staged bytes before replacement. A truncated or malformed
        // temporary file must never replace the AViK metadata.
        JSONObject staged = new JSONObject(new String(
                Files.readAllBytes(temp), StandardCharsets.UTF_8));
        if (!metadata.optString("name", "").equals(staged.optString("name", ""))
                || !metadata.optString("hash", "").equals(staged.optString("hash", ""))) {
            throw new IllegalStateException(
                    "Staged AViK metadata changed tag/hash identity; original file retained at "
                            + target + ", staged file=" + temp + ".");
        }

        try {
            Files.move(
                    temp,
                    target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException unsupported) {
            if (logger != null) {
                logger.info("QiraV2 metadata atomic move unsupported for "
                        + metadataFile.getName()
                        + "; using same-directory REPLACE_EXISTING fallback.");
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception fallbackFailure) {
                fallbackFailure.addSuppressed(unsupported);
                throw new IllegalStateException(
                        "AViK metadata replacement failed; capture must be rejected."
                                + " Target=" + target + ", stagedFile=" + temp + ".",
                        fallbackFailure);
            }
        } catch (Exception atomicFailure) {
            throw new IllegalStateException(
                    "Atomic AViK metadata replacement failed; capture must be rejected."
                            + " Target=" + target + ", stagedFile=" + temp + ".",
                    atomicFailure);
        }
    }

    private static int suppressLinkedDescriptionContainers(JSONArray texts)
            throws Exception {
        List<CandidateRecord> records = candidateRecords(texts);
        int suppressedIds = 0;
        for (CandidateRecord parent : records) {
            if (!parent.linked
                    || parent.sourceKind
                    != QiraV2SlapTextDump.AccessibilitySourceKind.DESCRIPTION) {
                continue;
            }
            boolean containsLinkedDescendant = false;
            for (CandidateRecord child : records) {
                if (parent == child
                        || !child.linked
                        || !isStrictAncestorNodePath(
                        parent.nodePath, child.nodePath)) {
                    continue;
                }
                containsLinkedDescendant = true;
                break;
            }
            if (!containsLinkedDescendant) {
                continue;
            }
            JSONArray messageIds = parent.json.optJSONArray("messageIds");
            if (messageIds == null || messageIds.length() == 0) {
                continue;
            }
            suppressedIds += messageIds.length();
            parent.json.put("messageIds", new JSONArray());
            parent.linked = false;
        }
        return suppressedIds;
    }

    private static boolean isStrictAncestorNodePath(
            String parentPath,
            String childPath) {
        return parentPath != null
                && childPath != null
                && !parentPath.isEmpty()
                && !childPath.isEmpty()
                && childPath.startsWith(parentPath + "/");
    }

    private static int countLinkedRecords(JSONArray texts) {
        int linked = 0;
        for (int index = 0; index < texts.length(); index++) {
            JSONObject text = texts.optJSONObject(index);
            JSONArray messageIds = text == null
                    ? null : text.optJSONArray("messageIds");
            if (messageIds != null && messageIds.length() > 0) {
                linked++;
            }
        }
        return linked;
    }

    private static List<CandidateRecord> candidateRecords(JSONArray texts) {
        List<CandidateRecord> records = new ArrayList<>();
        for (int index = 0; index < texts.length(); index++) {
            JSONObject text = texts.optJSONObject(index);
            if (text == null) {
                continue;
            }
            CandidateRecord record = CandidateRecord.from(text);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    private static CandidateSummary analyzeRemainingCandidates(
            JSONArray texts,
            int width,
            int height) {
        List<CandidateRecord> records = candidateRecords(texts);

        long screenArea = (long) width * (long) height;
        int nearFullCount = 0;
        int containerCount = 0;
        int geometryOnlyOverlapCount = 0;
        List<String> details = new ArrayList<>();
        for (int index = 0; index < records.size(); index++) {
            CandidateRecord record = records.get(index);
            boolean nearFull = record.linked
                    && screenArea > 0
                    && ((double) record.area / (double) screenArea)
                    >= NEAR_FULL_SCREEN_AREA_RATIO;
            boolean container = false;
            boolean geometryOnlyOverlap = false;
            for (int childIndex = 0; childIndex < records.size(); childIndex++) {
                if (index == childIndex) {
                    continue;
                }
                CandidateRecord child = records.get(childIndex);
                if (record.linked
                        && child.linked
                        && isStrictAncestorNodePath(
                        record.nodePath, child.nodePath)) {
                    container = true;
                } else if (record.linked
                        && child.linked
                        && record.area > child.area
                        && record.left <= child.left
                        && record.top <= child.top
                        && record.right >= child.right
                        && record.bottom >= child.bottom) {
                    geometryOnlyOverlap = true;
                }
            }
            if (nearFull) {
                nearFullCount++;
            }
            if (container) {
                containerCount++;
            }
            if (geometryOnlyOverlap && !container) {
                geometryOnlyOverlapCount++;
            }
            if ((nearFull || container || geometryOnlyOverlap)
                    && details.size() < 12) {
                String kind = nearFull ? "near-full"
                        : (container ? "ancestry-container"
                        : "geometry-only-overlap");
                details.add(kind
                        + "{text=" + safeLogValue(record.text)
                        + ", bounds=[" + record.left + "," + record.top
                        + "][" + record.right + "," + record.bottom + "]"
                        + ", linked=" + record.linked
                        + ", nodePath=" + safeLogValue(record.nodePath)
                        + "}");
            }
        }
        return new CandidateSummary(
                nearFullCount,
                containerCount,
                geometryOnlyOverlapCount,
                details.isEmpty() ? "<none>" : details.toString());
    }

    private static String metadataNodeBaseKey(
            String text,
            int left,
            int top,
            int right,
            int bottom) {
        String normalized = QiraStrings.stripBidiControls(text);
        if (normalized == null) {
            normalized = "";
        } else {
            normalized = Normalizer.normalize(normalized, Normalizer.Form.NFC);
        }
        return normalized
                + "\u0001" + left
                + "\u0001" + top
                + "\u0001" + right
                + "\u0001" + bottom;
    }

    private static String metadataNodeKey(
            String text,
            QiraV2SlapTextDump.AccessibilitySourceKind sourceKind,
            String nodePath,
            int left,
            int top,
            int right,
            int bottom) {
        return (sourceKind == null ? "<unknown>" : sourceKind.name())
                + "\u0001"
                + (nodePath == null ? "" : nodePath)
                + "\u0001"
                + metadataNodeBaseKey(text, left, top, right, bottom);
    }

    private static void requireExpectedOwner(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            String screenName,
            String locale,
            OwnerPolicy ownerPolicy,
            String actualPackage,
            String activity,
            String phase) {
        if (ownerPolicy.accepts(actualPackage)) {
            return;
        }
        failOwnerCheck(
                device,
                utils,
                logger,
                screenName,
                locale,
                ownerPolicy,
                actualPackage,
                activity,
                phase);
    }

    private static void failOwnerCheck(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            String screenName,
            String locale,
            OwnerPolicy ownerPolicy,
            String actualPackage,
            String activity,
            String phase) {
        String resolvedActivity = activity == null || activity.isEmpty()
                ? currentActivity(device) : activity;
        String evidenceTag = screenName + "_owner_mismatch_" + phase;
        String evidencePath = dumpDiagnosticEvidence(
                device,
                evidenceTag,
                ownerPolicy.primaryPackage(),
                "tag=" + screenName
                        + "; phase=" + phase
                        + "; expectedOwner=" + ownerPolicy.describe()
                        + "; actualPackage=" + actualPackage
                        + "; activity=" + resolvedActivity
                        + "; locale=" + locale);
        String quarantineStatus = shouldQuarantineLatestCapture(phase)
                ? quarantineLatestCapture(evidencePath, logger)
                : "not-applicable-before-capture";
        if (logger != null) {
            logger.info("QiraV2 owner mismatch:"
                    + " tag=" + screenName
                    + ", phase=" + phase
                    + ", expectedOwner=" + ownerPolicy.describe()
                    + ", actualPackage=" + safeLogValue(actualPackage)
                    + ", activity=" + safeLogValue(resolvedActivity)
                    + ", locale=" + safeLogValue(locale)
                    + ", evidenceDumpPath=" + evidencePath
                    + ", quarantineStatus=" + quarantineStatus);
        }
        throw new IllegalStateException(
                "Capture owner mismatch for " + screenName
                        + ": expected " + ownerPolicy.describe()
                        + ", actual " + safeLogValue(actualPackage)
                        + "; capture rejected. Evidence: " + evidencePath
                        + "; quarantine=" + quarantineStatus + ".");
    }

    private static String dumpDiagnosticEvidence(
            UiDevice device,
            String evidenceTag,
            String packageFilter,
            String note) {
        String evidencePath = QiraUiDumper.DUMP_ROOT + "/" + evidenceTag;
        try {
            QiraUiDumper.dump(device, packageFilter, evidenceTag, note);
        } catch (Throwable ignored) {
            // The structured path is still emitted so a missing dump is
            // explicit evidence rather than a silently accepted capture.
        }
        return evidencePath;
    }

    private static boolean shouldQuarantineLatestCapture(String phase) {
        return "after_capture".equals(phase)
                || "package_changed_during_capture".equals(phase)
                || "metadata_owner".equals(phase);
    }

    /**
     * Keep a rejected screenshot for diagnosis while moving it out of AViK's
     * importable {@code .png}/{@code .json} namespace. If the diagnostic
     * directory cannot be used, a same-directory {@code .rejected} suffix is
     * the safe fallback; Workbench will not treat either form as a screen pair.
     */
    private static String quarantineLatestCapture(
            String evidencePath,
            Logger logger) {
        String screenHash;
        try {
            screenHash = AndroidScreenProcessService.INSTANCE
                    .getScreenshotService()
                    .getScreenHash();
        } catch (Throwable failure) {
            return "failed:no-screen-hash:" + safeLogValue(failure.getMessage());
        }
        if (screenHash == null || screenHash.isEmpty()) {
            return "failed:no-screen-hash";
        }

        File metadataFile;
        try {
            metadataFile = new File(
                    AvikProperties.INSTANCE.getJSONFilePath(screenHash));
        } catch (Throwable failure) {
            return "failed:no-metadata-path:" + safeLogValue(failure.getMessage());
        }
        File screenshotFile = new File(
                metadataFile.getParentFile(), screenHash + ".png");
        File tempMetadataFile = new File(
                metadataFile.getParentFile(),
                metadataFile.getName() + ".qira-v2.tmp");
        File quarantineDirectory = new File(evidencePath, "rejected-capture");
        boolean quarantineDirectoryReady =
                quarantineDirectory.isDirectory() || quarantineDirectory.mkdirs();

        int moved = 0;
        List<String> failures = new ArrayList<>();
        for (File source : Arrays.asList(
                metadataFile, screenshotFile, tempMetadataFile)) {
            if (!source.exists()) {
                continue;
            }
            File preferred = quarantineDirectoryReady
                    ? new File(
                    quarantineDirectory, source.getName() + ".rejected")
                    : new File(
                    source.getParentFile(), source.getName() + ".rejected");
            try {
                Files.move(
                        source.toPath(),
                        preferred.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                moved++;
                continue;
            } catch (Exception preferredFailure) {
                File localFallback = new File(
                        source.getParentFile(), source.getName() + ".rejected");
                try {
                    Files.move(
                            source.toPath(),
                            localFallback.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    moved++;
                    continue;
                } catch (Exception fallbackFailure) {
                    fallbackFailure.addSuppressed(preferredFailure);
                    failures.add(source.getName() + ":"
                            + safeLogValue(fallbackFailure.getMessage()));
                }
            }
        }
        String status = failures.isEmpty()
                ? "moved=" + moved + ", destination="
                + (quarantineDirectoryReady
                ? quarantineDirectory.getAbsolutePath()
                : "same-directory-.rejected")
                : "failed:moved=" + moved + ", errors=" + failures;
        if (logger != null) {
            logger.info("QiraV2 rejected capture quarantine: hash="
                    + screenHash + ", " + status);
        }
        return status;
    }

    private static String currentPackage(UiDevice device) {
        try {
            String packageName = device == null ? null : device.getCurrentPackageName();
            return packageName == null ? "" : packageName;
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String currentActivity(UiDevice device) {
        try {
            if (device == null) {
                return "";
            }
            String windowDump = device.executeShellCommand("dumpsys window windows");
            return parseFocusedActivity(windowDump);
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String parseFocusedActivity(String windowDump) {
        if (windowDump == null || windowDump.isEmpty()) {
            return "";
        }
        String focusedApp = "";
        for (String rawLine : windowDump.split("\\r?\\n")) {
            String line = rawLine == null ? "" : rawLine.trim();
            boolean currentFocus = line.contains("mCurrentFocus");
            boolean appFocus = line.contains("mFocusedApp");
            if (!currentFocus && !appFocus) {
                continue;
            }
            Matcher component = FOCUSED_COMPONENT_PATTERN.matcher(line);
            if (!component.find()) {
                continue;
            }
            String value = component.group(1);
            if (currentFocus) {
                return value;
            }
            if (focusedApp.isEmpty()) {
                focusedApp = value;
            }
        }
        return focusedApp;
    }

    private static String safeLogValue(String value) {
        if (value == null) {
            return "";
        }
        String safe = value.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
        return safe.length() <= 180 ? safe : safe.substring(0, 177) + "...";
    }

    private static final class OwnerPolicy {
        private final boolean qira;
        private final Set<String> packages;

        private OwnerPolicy(boolean qira, String... packages) {
            this.qira = qira;
            this.packages = new LinkedHashSet<>(Arrays.asList(packages));
        }

        private static OwnerPolicy forTag(String tag) {
            if ((tag != null
                    && tag.startsWith("MotorolaQiraHome_Onboarding_Android"))
                    || "MotorolaQiraFocusZone_Live_AndroidMicrophonePermission"
                    .equals(tag)) {
                // Current-device evidence uses the Google module package. The
                // platform package is the equivalent AOSP PermissionController
                // identity already accepted by the existing Qira permission
                // path and is scoped only to these canonical Android tags.
                return new OwnerPolicy(
                        false,
                        GOOGLE_PERMISSION_CONTROLLER,
                        PLATFORM_PERMISSION_CONTROLLER);
            }
            if ("MotorolaQiraFocusZone_Live_EnablePermission".equals(tag)) {
                return new OwnerPolicy(false, ACTION_CORE_PACKAGE);
            }
            return new OwnerPolicy(true, QIRA_PACKAGE);
        }

        private boolean accepts(String packageName) {
            return packageName != null && packages.contains(packageName);
        }

        private boolean isQira() {
            return qira;
        }

        private String primaryPackage() {
            return packages.iterator().next();
        }

        private String describe() {
            StringBuilder value = new StringBuilder();
            for (String packageName : packages) {
                if (value.length() > 0) {
                    value.append('|');
                }
                value.append(packageName);
            }
            return value.toString();
        }
    }

    private static final class OwnerMismatchException extends Exception {
        private final String actualPackage;
        private final String activity;

        private OwnerMismatchException(String actualPackage, String activity) {
            super("Metadata owner mismatch: " + actualPackage);
            this.actualPackage = actualPackage;
            this.activity = activity;
        }
    }

    private static final class AccessibilityRecord {
        private final String text;
        private final QiraV2SlapTextDump.AccessibilitySourceKind sourceKind;
        private final String nodePath;
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;
        private final String baseKey;
        private final String key;

        private AccessibilityRecord(
                String text,
                QiraV2SlapTextDump.AccessibilitySourceKind sourceKind,
                String nodePath,
                int left,
                int top,
                int right,
                int bottom) {
            this.text = text;
            this.sourceKind = sourceKind;
            this.nodePath = nodePath == null ? "" : nodePath;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.baseKey = metadataNodeBaseKey(text, left, top, right, bottom);
            this.key = metadataNodeKey(
                    text, sourceKind, this.nodePath, left, top, right, bottom);
        }
    }

    private static final class CandidateRecord {
        private final JSONObject json;
        private final String text;
        private final QiraV2SlapTextDump.AccessibilitySourceKind sourceKind;
        private final String nodePath;
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;
        private final long area;
        private boolean linked;

        private CandidateRecord(
                JSONObject json,
                String text,
                QiraV2SlapTextDump.AccessibilitySourceKind sourceKind,
                String nodePath,
                int left,
                int top,
                int right,
                int bottom,
                boolean linked) {
            this.json = json;
            this.text = text;
            this.sourceKind = sourceKind;
            this.nodePath = nodePath == null ? "" : nodePath;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.area = (long) (right - left) * (long) (bottom - top);
            this.linked = linked;
        }

        private static CandidateRecord from(JSONObject text) {
            int left = text.optInt("left");
            int top = text.optInt("top");
            int right = text.optInt("right");
            int bottom = text.optInt("bottom");
            if (right <= left || bottom <= top) {
                return null;
            }
            JSONArray messageIds = text.optJSONArray("messageIds");
            QiraV2SlapTextDump.AccessibilitySourceKind sourceKind = null;
            try {
                sourceKind = QiraV2SlapTextDump.AccessibilitySourceKind.valueOf(
                        text.optString(ACCESSIBILITY_SOURCE_KIND_FIELD, ""));
            } catch (IllegalArgumentException ignored) {
            }
            return new CandidateRecord(
                    text,
                    text.optString("text", ""),
                    sourceKind,
                    text.optString(ACCESSIBILITY_NODE_PATH_FIELD, ""),
                    left,
                    top,
                    right,
                    bottom,
                    messageIds != null && messageIds.length() > 0);
        }
    }

    private static final class CandidateSummary {
        private final int nearFullCount;
        private final int containerCount;
        private final int geometryOnlyOverlapCount;
        private final String details;

        private CandidateSummary(
                int nearFullCount,
                int containerCount,
                int geometryOnlyOverlapCount,
                String details) {
            this.nearFullCount = nearFullCount;
            this.containerCount = containerCount;
            this.geometryOnlyOverlapCount = geometryOnlyOverlapCount;
            this.details = details;
        }
    }

    private static final class MetadataLinkResult {
        private final int originalCount;
        private final int retainedCount;
        private final int foreignRemovedCount;
        private final int finalCount;
        private final int linkedNodes;
        private final int messageIdsAdded;
        private final int accessibilityNodesAdded;
        private final int descriptionContainerIdsSuppressed;
        private final int nearFullCandidates;
        private final int containerCandidates;
        private final int geometryOnlyOverlapCandidates;
        private final String candidateDetails;

        private MetadataLinkResult(
                int originalCount,
                int retainedCount,
                int foreignRemovedCount,
                int finalCount,
                int linkedNodes,
                int messageIdsAdded,
                int accessibilityNodesAdded,
                int descriptionContainerIdsSuppressed,
                int nearFullCandidates,
                int containerCandidates,
                int geometryOnlyOverlapCandidates,
                String candidateDetails) {
            this.originalCount = originalCount;
            this.retainedCount = retainedCount;
            this.foreignRemovedCount = foreignRemovedCount;
            this.finalCount = finalCount;
            this.linkedNodes = linkedNodes;
            this.messageIdsAdded = messageIdsAdded;
            this.accessibilityNodesAdded = accessibilityNodesAdded;
            this.descriptionContainerIdsSuppressed =
                    descriptionContainerIdsSuppressed;
            this.nearFullCandidates = nearFullCandidates;
            this.containerCandidates = containerCandidates;
            this.geometryOnlyOverlapCandidates =
                    geometryOnlyOverlapCandidates;
            this.candidateDetails = candidateDetails;
        }
    }
}
