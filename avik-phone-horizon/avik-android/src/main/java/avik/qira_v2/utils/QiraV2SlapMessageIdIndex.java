package avik.qira_v2.utils;

import com.motorola.g11n.tools.avik.common.metadata.MessageIdParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Runtime index of Qira Compose string IDs to source SLAP message IDs.
 *
 * <p>Qira ships the authoritative markers in
 * {@code values-en-rXM/strings.commonMain.cvr}. Production locales expose the
 * localized plain text but omit those inline markers from accessibility. This
 * index decodes the shipped pseudo-locale once, then lets screenshot metadata
 * associate the same stable string ID with its localized value.</p>
 */
public final class QiraV2SlapMessageIdIndex {

    private static final String EN_XM_FOLDER = "values-en-rXM";

    private static volatile Snapshot snapshot;

    private QiraV2SlapMessageIdIndex() {
    }

    public static Map<String, List<String>> get(
            String packageName,
            Logger logger) {
        Snapshot current = snapshot;
        if (current != null && current.packageName.equals(packageName)) {
            return current.messageIdsByStringId;
        }
        synchronized (QiraV2SlapMessageIdIndex.class) {
            current = snapshot;
            if (current != null && current.packageName.equals(packageName)) {
                return current.messageIdsByStringId;
            }
            Map<String, String> enXm = QiraV2ComposeStrings.loadFolderSnapshot(
                    packageName, EN_XM_FOLDER, logger);
            Map<String, List<String>> decoded = new LinkedHashMap<>();
            int malformedRows = 0;
            for (Map.Entry<String, String> entry : enXm.entrySet()) {
                try {
                    List<String> parsed = MessageIdParser.INSTANCE.decodeMessageId(
                            entry.getValue());
                    if (parsed == null || parsed.isEmpty()) {
                        continue;
                    }
                    LinkedHashSet<String> unique = new LinkedHashSet<>();
                    for (String messageId : parsed) {
                        if (messageId != null && !messageId.isEmpty()) {
                            unique.add(messageId);
                        }
                    }
                    if (!unique.isEmpty()) {
                        decoded.put(
                                entry.getKey(),
                                Collections.unmodifiableList(new ArrayList<>(unique)));
                    }
                } catch (Throwable malformedMarker) {
                    malformedRows++;
                }
            }
            Map<String, List<String>> immutable =
                    Collections.unmodifiableMap(decoded);
            snapshot = new Snapshot(packageName, immutable);
            if (logger != null) {
                logger.info("QiraV2 SLAP message-ID index built: catalogRows="
                        + enXm.size()
                        + ", linkedStringIds="
                        + decoded.size()
                        + ", malformedRows="
                        + malformedRows);
            }
            return immutable;
        }
    }

    private static final class Snapshot {
        private final String packageName;
        private final Map<String, List<String>> messageIdsByStringId;

        private Snapshot(
                String packageName,
                Map<String, List<String>> messageIdsByStringId) {
            this.packageName = packageName == null ? "" : packageName;
            this.messageIdsByStringId = messageIdsByStringId;
        }
    }
}
