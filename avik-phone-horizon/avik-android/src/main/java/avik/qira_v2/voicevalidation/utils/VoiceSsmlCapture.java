package avik.qira_v2.voicevalidation.utils;

import androidx.test.uiautomator.UiDevice;

import java.util.logging.Logger;

/**
 * Captures the SSML that Qira sends to the cloud TTS when a response is played.
 *
 * <p>The debug build logs the request as, e.g.:
 * <pre>
 * QC.ModelCallHandler MCH input: {"modelName":"cloudtts",...,"prompt":
 *   "&lt;speak ...&gt;&lt;voice name=\"en-US-Ava-preview:DragonHDLatestNeural\"&gt;
 *    &lt;lang xml:lang=\"zh-CN\"&gt;OUTPUT TEXT&lt;/lang&gt;&lt;/voice&gt;&lt;/speak&gt;",
 *   "isSSML":true}
 * </pre>
 *
 * <p>This is the authoritative source of the spoken "Output Text" and is
 * available whether or not the on-device traceability folder-writing is enabled.
 * Callers clear logcat immediately before tapping Play, then capture here.
 */
public final class VoiceSsmlCapture {

    private static final String SPEAK_OPEN = "<speak";
    private static final String SPEAK_CLOSE = "</speak>";

    private VoiceSsmlCapture() {
    }

    private static final String LOGCAT_DUMP_PATH = "/sdcard/avik/vv_logcat.txt";

    /** Clears the logcat buffer so only post-Play synthesis lines are captured. */
    public static void clear(UiDevice device) {
        try {
            device.executeShellCommand("logcat -c");
        } catch (Throwable ignored) {
        }
        try {
            device.executeShellCommand("rm -f " + LOGCAT_DUMP_PATH);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Polls logcat until an SSML {@code <speak>...</speak>} appears (the latest
     * one, matching the most recent Play), or the timeout elapses.
     *
     * @return the unescaped SSML string, or {@code null} if none was logged.
     */
    public static String capture(UiDevice device, long timeoutMs, Logger logger) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String log = readLogcat(device);
            String ssml = extractLastSpeak(log);
            if (ssml != null) {
                if (logger != null) {
                    logger.info("VoiceValidation: captured SSML (" + ssml.length() + " chars).");
                }
                return ssml;
            }
            sleep(1500L);
        }
        if (logger != null) {
            logger.info("VoiceValidation: no SSML found in logcat within timeout.");
        }
        return null;
    }

    private static String readLogcat(UiDevice device) {
        // Dump to a file and read it as UTF-8. executeShellCommand's own string
        // decoding can corrupt a multi-byte char at a read-buffer boundary; a
        // whole-file UTF-8 decode is exact (important for CJK/Arabic SSML text).
        try {
            device.executeShellCommand("rm -f " + LOGCAT_DUMP_PATH);
            device.executeShellCommand("logcat -d -f " + LOGCAT_DUMP_PATH);
            java.io.File f = new java.io.File(LOGCAT_DUMP_PATH);
            if (f.exists() && f.length() > 0) {
                byte[] bytes = new byte[(int) f.length()];
                try (java.io.FileInputStream in = new java.io.FileInputStream(f)) {
                    int read = 0;
                    while (read < bytes.length) {
                        int n = in.read(bytes, read, bytes.length - read);
                        if (n < 0) {
                            break;
                        }
                        read += n;
                    }
                }
                return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Throwable ignored) {
        }
        try {
            return device.executeShellCommand("logcat -d");
        } catch (Throwable t) {
            return null;
        }
    }

    /** Extracts and unescapes the last {@code <speak>...</speak>} block in a log. */
    static String extractLastSpeak(String log) {
        if (log == null || log.isEmpty()) {
            return null;
        }
        int end = log.lastIndexOf(SPEAK_CLOSE);
        if (end < 0) {
            return null;
        }
        int start = log.lastIndexOf(SPEAK_OPEN, end);
        if (start < 0) {
            return null;
        }
        String raw = log.substring(start, end + SPEAK_CLOSE.length());
        // The SSML is embedded in a JSON string, so quotes/slashes are escaped.
        return raw.replace("\\\"", "\"").replace("\\/", "/").trim();
    }

    /** Plain spoken text from an SSML string (tags + XML entities stripped). */
    public static String extractText(String ssml) {
        if (ssml == null || ssml.isEmpty()) {
            return "";
        }
        String noTags = ssml.replaceAll("<[^>]+>", " ");
        noTags = noTags
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'");
        return noTags.replaceAll("[ \\t]+", " ").trim();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
