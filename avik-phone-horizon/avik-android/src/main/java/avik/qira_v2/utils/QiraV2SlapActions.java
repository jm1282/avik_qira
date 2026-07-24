package avik.qira_v2.utils;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.common.metadata.AvikText;

import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraUiDumper;

public final class QiraV2SlapActions {

    private QiraV2SlapActions() {
    }

    public static boolean waitForMessageId(
            String messageId,
            long timeoutMs,
            Logger logger) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            AvikText match = QiraV2SlapTextDump.findByMessageId(messageId, false, null);
            if (match != null) {
                if (logger != null) {
                    logger.info("QiraV2 SLAP wait satisfied: messageId="
                            + messageId
                            + ", evidence="
                            + QiraV2SlapTextDump.summarize(match));
                }
                return true;
            }
            Thread.sleep(250L);
        }
        if (logger != null) {
            logger.info("QiraV2 SLAP wait timed out for messageId=" + messageId);
        }
        return false;
    }

    public static void logCatalogEvidence(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            String dumpTag,
            QiraV2SlapCatalog.SlapString... entries) {
        if (entries == null) {
            return;
        }
        for (QiraV2SlapCatalog.SlapString entry : entries) {
            if (entry == null) {
                continue;
            }
            logger.info("QiraV2 SLAP catalog: " + entry.toLogString());
            AvikText slapMatch = QiraV2SlapTextDump.findByMessageId(
                    entry.getMessageId(),
                    false,
                    logger);
            if (slapMatch == null) {
                logger.info("QiraV2 SLAP evidence miss: messageId="
                        + entry.getMessageId()
                        + ", stringId="
                        + entry.getStringId());
                QiraV2SlapTextDump.logVisibleTextSummaries(
                        "QiraV2 SLAP visible text after miss for " + entry.getStringId(),
                        false,
                        logger);
            } else {
                logger.info("QiraV2 SLAP evidence: "
                        + QiraV2SlapTextDump.summarize(slapMatch));
            }
        }
        try {
            QiraUiDumper.dump(
                    device,
                    config.getPackageName(),
                    dumpTag + "_selector_probe",
                    "Selector probe before qira_v2 capture");
        } catch (Throwable t) {
            logger.info("QiraV2 selector evidence dump failed: " + t.getMessage());
        }
    }

    public static void requireCatalogEvidence(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            String screenName,
            QiraV2SlapCatalog.SlapString entry,
            String... englishAnchors) {
        UiObject2 idBacked = findIdBacked(device, config, logger, englishAnchors);
        if (idBacked != null) {
            logger.info("QiraV2 selector accepted for " + entry.getStringId()
                    + ": accessible resource/string-ID backed node found.");
            return;
        }

        AvikText slapMatch = QiraV2SlapTextDump.findByMessageId(
                entry.getMessageId(),
                false,
                logger);
        if (slapMatch != null) {
            logger.info("QiraV2 selector accepted for " + entry.getStringId()
                    + ": SLAP message ID "
                    + entry.getMessageId()
                    + " found for "
                    + QiraV2SlapTextDump.summarize(slapMatch));
            return;
        }

        AvikText resourceMatch = QiraV2SlapTextDump.findByResolvedQiraStringResource(
                entry.getStringId(),
                false,
                logger);
        if (resourceMatch != null) {
            logger.info("QiraV2 selector accepted for " + entry.getStringId()
                    + ": Qira string resource entry resolved in active locale for "
                    + QiraV2SlapTextDump.summarize(resourceMatch));
            return;
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                screenName + "_no_id_evidence",
                "No Android resource/string ID or SLAP message ID evidence for "
                        + entry.getStringId());
        throw new IllegalStateException("Qira v2 control "
                + entry.getStringId()
                + " has no accessible Android resource/string ID and Avik SLAP"
                + " text dump did not expose message ID "
                + entry.getMessageId()
                + ". Refusing text/coordinate fallback.");
    }

    public static void clickCatalogEntry(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            String screenName,
            QiraV2SlapCatalog.SlapString entry,
            String... englishAnchors) {
        UiObject2 idBacked = findIdBacked(device, config, logger, englishAnchors);
        if (QiraV2Selectors.clickIfPresent(idBacked)) {
            logger.info("QiraV2 clicked " + entry.getStringId() + " by ID-backed selector");
            return;
        }
        if (QiraV2SlapTextDump.clickClickableAncestorByResolvedQiraComposeStringResource(
                device,
                entry.getStringId(),
                false,
                logger)) {
            logger.info("QiraV2 clicked " + entry.getStringId()
                    + " through its Compose-resource-backed clickable ancestor.");
            return;
        }
        if (QiraV2SlapTextDump.clickByMessageId(
                device,
                entry.getMessageId(),
                false,
                logger)) {
            logger.info("QiraV2 clicked " + entry.getStringId()
                    + " by SLAP message ID "
                    + entry.getMessageId()
                    + " (no hard-coded text or static coordinates).");
            return;
        }
        if (QiraV2SlapTextDump.clickByResolvedQiraComposeStringResource(
                device,
                entry.getStringId(),
                false,
                logger)) {
            logger.info("QiraV2 clicked " + entry.getStringId()
                    + " by Qira Compose string resource entry in active locale"
                    + " (no per-locale table or static coordinates).");
            return;
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                screenName + "_no_id_click_selector",
                "No ID-backed selector for " + entry.getStringId()
                        + "; refusing text/coordinate fallback");
        throw new IllegalStateException("Qira v2 control "
                + entry.getStringId()
                + " has no accessible resource-id, Qira string resource entry,"
                + " or SLAP-backed message ID selector. Refusing text/coordinate"
                + " fallback.");
    }

    private static UiObject2 findIdBacked(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            String... englishAnchors) {
        if (englishAnchors == null || englishAnchors.length == 0) {
            return null;
        }
        return QiraV2Selectors.findByQiraStringIdsForEnglish(
                device,
                config.getPackageName(),
                logger,
                englishAnchors);
    }
}
