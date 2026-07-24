package avik.qira.utils;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Writes UiAutomator window-hierarchy dumps to the device at
 * {@code /sdcard/avik/qira-ui-dumps/&lt;tag&gt;/&lt;timestamp&gt;.xml} and a
 * matching human-readable resource-id inventory at
 * {@code …/&lt;timestamp&gt;.txt}.
 *
 * <p>Use this to harvest stable, locale-independent
 * {@code resource-id} / {@code content-desc} / {@code class} selectors from
 * every Qira surface. A single call writes two files:
 * <ol>
 *     <li>The raw UiAutomator XML (same format {@code uiautomator dump}
 *         produces). This is the source of truth; it includes every element,
 *         every attribute, and the full view tree.</li>
 *     <li>A tab-separated {@code .txt} index of every node that has a
 *         non-empty {@code resource-id}, {@code content-desc} or
 *         {@code text}. This is what humans grep through to pick selectors.</li>
 * </ol>
 *
 * <p>All I/O is best-effort; failures are logged but never thrown. A dump
 * that cannot be written must never abort the capture flow.
 */
public final class QiraUiDumper {

    private static final Logger LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    /** Root directory on the device where every dump is written. */
    public static final String DUMP_ROOT = "/sdcard/avik/qira-ui-dumps";

    private static final SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);

    private QiraUiDumper() {
    }

    /**
     * Convenience entry point used by capture scripts.
     * Calls {@link #dump(UiDevice, String, String)} with no per-call extra
     * context. The Qira package is used to scope the text index so the file
     * stays focused on the app under test.
     */
    public static void dump(UiDevice device, QiraConfig config, String tag) {
        dump(device, config != null ? config.getPackageName() : null, tag, null);
    }

    /**
     * Dumps the current UiAutomator window hierarchy into
     * {@code /sdcard/avik/qira-ui-dumps/&lt;tag&gt;/&lt;timestamp&gt;.xml} and
     * writes a text index of every {@code resource-id} / {@code content-desc}
     * / {@code text} node at the same base path with a {@code .txt}
     * extension.
     *
     * @param device the {@link UiDevice} owned by the current capture
     * @param packageFilter if non-null, the text index is restricted to
     *     nodes in this package (recommended: the Qira package). The raw
     *     XML always contains every node regardless of this filter.
     * @param tag short human-friendly label for the surface being dumped,
     *     e.g. {@code "onboarding_start_dialog"}. Used as the sub-directory
     *     name. Illegal filesystem characters are replaced with {@code _}.
     * @param note optional free-form note appended to the top of the text
     *     index (e.g. what the tester just did before the dump). Null is
     *     ignored.
     */
    public static void dump(UiDevice device, String packageFilter, String tag, String note) {
        if (device == null) {
            return;
        }

        String sanitizedTag = sanitize(tag);
        String timestamp;
        synchronized (TIMESTAMP_FORMAT) {
            timestamp = TIMESTAMP_FORMAT.format(new Date());
        }
        File dir = new File(DUMP_ROOT, sanitizedTag);
        if (!dir.exists() && !dir.mkdirs()) {
            LOGGER.info("[QiraUiDumper] Failed to create dump dir: " + dir.getAbsolutePath());
            // We still try to write the files; mkdirs can race with other processes.
        }

        File xmlFile = new File(dir, timestamp + ".xml");
        File txtFile = new File(dir, timestamp + ".txt");

        try {
            device.dumpWindowHierarchy(xmlFile);
        } catch (IOException t) {
            LOGGER.info("[QiraUiDumper] dumpWindowHierarchy failed: " + t.getMessage());
        }

        try (FileOutputStream out = new FileOutputStream(txtFile)) {
            StringBuilder sb = new StringBuilder();
            sb.append("# Qira UI dump\n");
            sb.append("# tag: ").append(sanitizedTag).append('\n');
            sb.append("# timestamp: ").append(timestamp).append('\n');
            sb.append("# package_filter: ")
                    .append(packageFilter == null ? "<none>" : packageFilter)
                    .append('\n');
            sb.append("# current_package: ").append(safeCurrentPackage(device)).append('\n');
            sb.append("# display: ").append(device.getDisplayWidth())
                    .append('x').append(device.getDisplayHeight())
                    .append(" rot=").append(device.getDisplayRotation()).append('\n');
            if (note != null && !note.isEmpty()) {
                sb.append("# note: ").append(note.replace('\n', ' ')).append('\n');
            }
            sb.append('\n');
            sb.append("# columns: resource-id\tclass\tcontent-desc\ttext\tclickable\tbounds\tpackage\n");
            appendInventory(sb, device, packageFilter);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Throwable t) {
            LOGGER.info("[QiraUiDumper] write inventory failed: " + t.getMessage());
        }

        LOGGER.info("[QiraUiDumper] wrote " + xmlFile.getAbsolutePath()
                + " (+ inventory.txt). Pull with: adb pull " + DUMP_ROOT);
    }

    private static void appendInventory(StringBuilder sb, UiDevice device, String packageFilter) {
        // Class names on a real device are always non-empty, so a regex that
        // matches "one or more characters" is a safe way to enumerate every
        // node when no package filter is available.
        java.util.regex.Pattern anyClass = java.util.regex.Pattern.compile(".+");
        List<UiObject2> objects;
        try {
            objects = packageFilter != null && !packageFilter.isEmpty()
                    ? device.findObjects(By.pkg(packageFilter))
                    : device.findObjects(By.clazz(anyClass));
        } catch (Throwable ignored) {
            objects = null;
        }

        if (objects == null || objects.isEmpty()) {
            try {
                objects = device.findObjects(By.clazz(anyClass));
            } catch (Throwable ignored) {
                return;
            }
        }

        if (objects == null) {
            return;
        }

        int written = 0;
        for (UiObject2 obj : objects) {
            if (obj == null) {
                continue;
            }
            try {
                String resId = safe(obj.getResourceName());
                String cls = safe(obj.getClassName());
                String desc = safe(obj.getContentDescription());
                String text = safe(obj.getText());
                if (resId.isEmpty() && desc.isEmpty() && text.isEmpty()) {
                    continue;
                }
                Rect bounds;
                try {
                    bounds = obj.getVisibleBounds();
                } catch (StaleObjectException stale) {
                    continue;
                }
                String boundsStr = bounds != null
                        ? bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom
                        : "";
                String pkg = safe(obj.getApplicationPackage());
                sb.append(resId).append('\t')
                        .append(cls).append('\t')
                        .append(desc).append('\t')
                        .append(text).append('\t')
                        .append(obj.isClickable()).append('\t')
                        .append(boundsStr).append('\t')
                        .append(pkg).append('\n');
                written++;
                if (written > 2000) {
                    sb.append("# truncated after 2000 rows\n");
                    break;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while we were iterating; skip it.
            } catch (Throwable ignored) {
                // Best-effort inventory; never abort the capture flow.
            }
        }
    }

    private static String safeCurrentPackage(UiDevice device) {
        try {
            return safe(device.getCurrentPackageName());
        } catch (Throwable ignored) {
            return "<unknown>";
        }
    }

    private static String safe(String value) {
        if (value == null) {
            return "";
        }
        // Flatten tabs / newlines so the tab-separated inventory stays parseable.
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private static String sanitize(String tag) {
        if (tag == null || tag.isEmpty()) {
            return "unknown";
        }
        StringBuilder out = new StringBuilder(tag.length());
        for (int i = 0; i < tag.length(); i++) {
            char c = tag.charAt(i);
            boolean isLetter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            boolean isDigit = c >= '0' && c <= '9';
            if (isLetter || isDigit || c == '-' || c == '_' || c == '.') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }
}
