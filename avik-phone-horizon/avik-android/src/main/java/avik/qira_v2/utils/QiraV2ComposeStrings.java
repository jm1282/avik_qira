package avik.qira_v2.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import avik.qira.utils.QiraStrings;

public final class QiraV2ComposeStrings {

    private static final String QTPROMPT_RESOURCE_ROOT =
            "composeResources/qtprompt.app.generated.resources/";
    private static final String STRINGS_FILE = "/strings.commonMain.cvr";
    private static final String DEFAULT_FOLDER = "values";
    private static final Map<String, Map<String, String>> CACHE = new HashMap<>();

    /**
     * Reverse index built from the English {@code values/} catalog:
     * {@code normalize(englishValue) -> [stringId, ...]}. Used to translate a
     * page-object English anchor into the Compose {@code stringId}(s) that
     * carry that anchor so the current-locale value(s) can be resolved. The
     * mapping is locale independent (English is the pivot), so a single build
     * per process serves every locale. Multiple stringIds may share the same
     * English value (e.g. several "Next" buttons) - all are kept so callers get
     * every locale variant as a candidate alias.
     */
    private static final Map<String, List<String>> ENGLISH_REVERSE = new HashMap<>();
    private static volatile boolean englishReverseLoaded = false;

    private QiraV2ComposeStrings() {
    }

    public static String resolve(
            String packageName,
            String stringId,
            Logger logger) {
        return resolveFromFolders(packageName, stringId, localeFolders(), logger);
    }

    public static String resolveDefault(
            String packageName,
            String stringId,
            Logger logger) {
        List<String> folders = new ArrayList<>();
        folders.add("values");
        return resolveFromFolders(packageName, stringId, folders, logger);
    }

    /**
     * Returns a defensive copy of one shipped Compose string folder.
     *
     * <p>This package-private hook exists for the SLAP message-ID index, which
     * decodes the markers embedded in Qira's {@code values-en-rXM} catalog.
     * Callers cannot mutate the shared folder cache.</p>
     */
    static Map<String, String> loadFolderSnapshot(
            String packageName,
            String folder,
            Logger logger) {
        if (packageName == null || packageName.isEmpty()
                || folder == null || folder.isEmpty()) {
            return new HashMap<>();
        }
        Context qiraContext = createQiraContext(packageName, logger);
        if (qiraContext == null) {
            return new HashMap<>();
        }
        return new HashMap<>(loadFolder(qiraContext, folder, logger));
    }

    static Map<String, String> loadCurrentStringsSnapshot(
            String packageName,
            Logger logger) {
        Context qiraContext = createQiraContext(packageName, logger);
        if (qiraContext == null) {
            return new HashMap<>();
        }
        Map<String, String> merged = new HashMap<>();
        for (String folder : localeFolders()) {
            for (Map.Entry<String, String> entry
                    : loadFolder(qiraContext, folder, logger).entrySet()) {
                if (!merged.containsKey(entry.getKey())) {
                    merged.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return merged;
    }

    /**
     * Reverse resolution: given an <b>English</b> page-object anchor, return the
     * current-locale Compose value(s) for every {@code stringId} whose English
     * ({@code values/}) value equals the anchor (case-insensitive, after
     * {@link QiraStrings#stripBidiControls} and punctuation/whitespace
     * normalization). This is the qira_v2 bridge that lets the v1 English-anchor
     * page objects resolve Creator Zone / Knowledge / Chat History / Settings
     * labels that live in Qira's Compose catalog (not in Android {@code
     * R.string}). Locale independent by construction: the English catalog is the
     * pivot and the returned values are read from the current locale's folder.
     *
     * <p>Returns an empty array (never {@code null}) when the anchor is not an
     * English Compose value or the package/assets cannot be read.
     */
    public static String[] localizeEnglishAnchor(
            String packageName,
            String englishAnchor,
            Logger logger) {
        if (packageName == null || packageName.isEmpty()
                || englishAnchor == null || englishAnchor.isEmpty()) {
            return new String[0];
        }
        Context qiraContext = createQiraContext(packageName, logger);
        if (qiraContext == null) {
            return new String[0];
        }
        List<String> stringIds = englishReverseLookup(qiraContext, englishAnchor, logger);
        if (stringIds.isEmpty()) {
            return new String[0];
        }
        List<String> folders = localeFolders();
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String stringId : stringIds) {
            for (String folder : folders) {
                Map<String, String> strings = loadFolder(qiraContext, folder, logger);
                String value = strings.get(stringId);
                if (value != null && !value.isEmpty()) {
                    out.add(value);
                    // Most specific folder wins for this stringId.
                    break;
                }
            }
        }
        return out.toArray(new String[0]);
    }

    /**
     * Builds (once) and queries the {@code normalize(english) -> [stringId]}
     * reverse index from the English {@code values/} catalog.
     */
    private static List<String> englishReverseLookup(
            Context qiraContext, String englishAnchor, Logger logger) {
        ensureEnglishReverse(qiraContext, logger);
        String key = normalizeAnchor(englishAnchor);
        if (key.isEmpty()) {
            return new ArrayList<>();
        }
        synchronized (ENGLISH_REVERSE) {
            List<String> ids = ENGLISH_REVERSE.get(key);
            return ids == null ? new ArrayList<>() : new ArrayList<>(ids);
        }
    }

    private static void ensureEnglishReverse(Context qiraContext, Logger logger) {
        if (englishReverseLoaded) {
            return;
        }
        synchronized (ENGLISH_REVERSE) {
            if (englishReverseLoaded) {
                return;
            }
            Map<String, String> english = loadFolder(qiraContext, DEFAULT_FOLDER, logger);
            int indexed = 0;
            for (Map.Entry<String, String> entry : english.entrySet()) {
                String normalized = normalizeAnchor(entry.getValue());
                if (normalized.isEmpty()) {
                    continue;
                }
                List<String> ids = ENGLISH_REVERSE.get(normalized);
                if (ids == null) {
                    ids = new ArrayList<>(1);
                    ENGLISH_REVERSE.put(normalized, ids);
                }
                if (!ids.contains(entry.getKey())) {
                    ids.add(entry.getKey());
                    indexed++;
                }
            }
            englishReverseLoaded = true;
            if (logger != null) {
                logger.info("QiraV2 Compose reverse index built: englishKeys="
                        + ENGLISH_REVERSE.size() + ", stringIds=" + indexed);
            }
        }
    }

    /**
     * Normalizes an English anchor / catalog value for reverse-index keying:
     * bidi-stripped, lower-cased ({@link Locale#ROOT}), trailing punctuation
     * removed, and internal whitespace collapsed. Mirrors the semantics of
     * {@code QiraStrings.normalize} so an anchor and its catalog entry key
     * identically.
     */
    private static String normalizeAnchor(String value) {
        String stripped = QiraStrings.stripBidiControls(value);
        if (stripped == null) {
            return "";
        }
        String t = stripped.toLowerCase(Locale.ROOT).trim();
        if (t.isEmpty()) {
            return t;
        }
        int end = t.length();
        while (end > 0) {
            char c = t.charAt(end - 1);
            if (c == '.' || c == ',' || c == ';' || c == ':' || c == '!' || c == '?'
                    || c == '\u3002' || c == '\uFF0C' || c == '\uFF01' || c == '\uFF1F') {
                end--;
            } else {
                break;
            }
        }
        if (end != t.length()) {
            t = t.substring(0, end);
        }
        StringBuilder sb = new StringBuilder(t.length());
        boolean lastWasSpace = false;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            boolean isWs = Character.isWhitespace(c) || c == '\u00a0';
            if (isWs) {
                if (!lastWasSpace) {
                    sb.append(' ');
                    lastWasSpace = true;
                }
            } else {
                sb.append(c);
                lastWasSpace = false;
            }
        }
        return sb.toString().trim();
    }

    private static Context createQiraContext(String packageName, Logger logger) {
        try {
            Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
            return testContext.createPackageContext(
                    packageName,
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            if (logger != null) {
                logger.info("QiraV2 Compose string resolve failed; package not found: "
                        + packageName);
            }
            return null;
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 Compose string resolve failed creating package context: "
                        + t.getMessage());
            }
            return null;
        }
    }

    private static String resolveFromFolders(
            String packageName,
            String stringId,
            List<String> folders,
            Logger logger) {
        if (packageName == null || packageName.isEmpty()
                || stringId == null || stringId.isEmpty()) {
            return null;
        }
        Context qiraContext = createQiraContext(packageName, logger);
        if (qiraContext == null) {
            return null;
        }

        for (String folder : folders) {
            Map<String, String> strings = loadFolder(qiraContext, folder, logger);
            String value = strings.get(stringId);
            if (value != null && !value.isEmpty()) {
                if (logger != null) {
                    logger.info("QiraV2 Compose string resolved: stringId="
                            + stringId
                            + ", folder="
                            + folder
                            + ", value='"
                            + QiraStrings.stripBidiControls(value)
                            + "'");
                }
                return value;
            }
        }
        if (logger != null) {
            logger.info("QiraV2 Compose string resolve miss: stringId=" + stringId);
        }
        return null;
    }

    private static List<String> localeFolders() {
        Locale locale = QiraStrings.getInstance().getCurrentLocale();
        String language = locale == null ? "" : safeLower(locale.getLanguage());
        String region = locale == null ? "" : safeUpper(locale.getCountry());
        List<String> folders = new ArrayList<>();
        if (!language.isEmpty() && !region.isEmpty()) {
            folders.add("values-" + language + "-r" + region);
        }
        if (!language.isEmpty()) {
            folders.add("values-" + language);
        }
        folders.add("values");
        return folders;
    }

    private static Map<String, String> loadFolder(
            Context qiraContext,
            String folder,
            Logger logger) {
        synchronized (CACHE) {
            Map<String, String> cached = CACHE.get(folder);
            if (cached != null) {
                return cached;
            }
        }

        Map<String, String> parsed = new HashMap<>();
        String path = QTPROMPT_RESOURCE_ROOT + folder + STRINGS_FILE;
        try (InputStream input = qiraContext.getAssets().open(path);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("string|")) {
                    continue;
                }
                String[] parts = line.split("\\|", 3);
                if (parts.length != 3 || parts[1].isEmpty() || parts[2].isEmpty()) {
                    continue;
                }
                try {
                    parsed.put(
                            parts[1],
                            new String(Base64.getDecoder().decode(parts[2]),
                                    StandardCharsets.UTF_8));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 Compose string folder unavailable: "
                        + path
                        + " ("
                        + t.getMessage()
                        + ")");
            }
        }

        synchronized (CACHE) {
            CACHE.put(folder, parsed);
        }
        return parsed;
    }

    private static String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.US);
    }

    private static String safeUpper(String value) {
        return value == null ? "" : value.toUpperCase(Locale.US);
    }
}
