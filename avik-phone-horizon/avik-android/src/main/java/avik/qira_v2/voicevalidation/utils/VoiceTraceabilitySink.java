package avik.qira_v2.voicevalidation.utils;

import androidx.test.uiautomator.UiDevice;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Detects the per-play traceability folder the Ava-Preview debug voice writes
 * under {@code &lt;traceBase&gt;/&lt;voiceModelDir&gt;/&lt;voiceLocale&gt;/&lt;timestamp&gt;/}
 * (each timestamp folder holds one Log, one SSML and one Audio file).
 *
 * <p>Folder names are timestamps only, so the reliable way to bind a folder to
 * the scenario that produced it is to snapshot the locale directory immediately
 * before tapping Play, then poll for the single new entry that appears after.
 * We list through {@code adb shell ls} (the shell user always has read access to
 * shared {@code Download} storage, unlike the scoped-storage view of the
 * instrumentation process).
 */
public final class VoiceTraceabilitySink {

    private final UiDevice device;
    private final Logger logger;
    private final String localeDir;

    public VoiceTraceabilitySink(UiDevice device, Logger logger, String localeDir) {
        this.device = device;
        this.logger = logger;
        this.localeDir = localeDir;
    }

    /** Names of the timestamp folders currently present for this locale. */
    public Set<String> snapshot() {
        Set<String> names = new LinkedHashSet<>();
        String out = runShell(String.format(Locale.US, "ls -1 \"%s\" 2>/dev/null", localeDir));
        if (out == null) {
            return names;
        }
        for (String line : out.split("\\r?\\n")) {
            String name = line.trim();
            if (!name.isEmpty() && !name.startsWith("ls:")) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Polls for a folder that appears after {@code before} was captured, up to
     * {@code timeoutMs}. Returns the absolute device path of the newest new
     * folder, or {@code null} if none appears (e.g. Play produced no audio).
     */
    public String detectNewFolder(Set<String> before, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        String newest = null;
        while (System.currentTimeMillis() < deadline) {
            Set<String> now = snapshot();
            now.removeAll(before);
            if (!now.isEmpty()) {
                // Timestamped names sort lexicographically in creation order.
                for (String name : now) {
                    if (newest == null || name.compareTo(newest) > 0) {
                        newest = name;
                    }
                }
                // Give the audio/SSML/log writer a moment to flush all three
                // files before we report the folder as complete.
                sleep(1500L);
                String path = localeDir + "/" + newest;
                if (logger != null) {
                    logger.info("VoiceValidation: new traceability folder -> " + path
                            + " (files: " + listFiles(path) + ")");
                }
                return path;
            }
            sleep(1000L);
        }
        return null;
    }

    /** Comma-joined file names inside a traceability folder, for logging. */
    public String listFiles(String folderPath) {
        String out = runShell(String.format(Locale.US, "ls -1 \"%s\" 2>/dev/null", folderPath));
        if (out == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String line : out.split("\\r?\\n")) {
            String name = line.trim();
            if (!name.isEmpty() && !name.startsWith("ls:")) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(name);
            }
        }
        return sb.toString();
    }

    private String runShell(String cmd) {
        try {
            return device.executeShellCommand(cmd);
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("VoiceValidation: shell failed (" + cmd + "): " + t.getMessage());
            }
            return null;
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
