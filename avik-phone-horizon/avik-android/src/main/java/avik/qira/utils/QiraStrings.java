/*
 * Copyright (c) 2026. Motorola Mobility Inc.
 * All Rights Reserved.
 * Motorola Confidential Restricted.
 */
package avik.qira.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;

import androidx.test.platform.app.InstrumentationRegistry;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central locale-aware string resolver for Qira page objects.
 *
 * <p><b>Strategy: Qira R.string IDs ARE the stable "unique IDs".</b> The
 * Motorola Qira app ships a full Android string-resource table (hundreds of
 * entries) and every locale it supports is just a different column of the
 * same table. The {@code R.string.*} integer IDs are therefore perfectly
 * stable across locales - they are exactly the "unique id which is used
 * even when other languages change" that page objects need.
 *
 * <p>Qira's Compose/WebView surface does NOT expose those IDs as
 * view-level {@code resource-id} attributes, so we cannot query them from
 * UiAutomator directly. Instead we <i>bridge</i> them at resolve time:
 *
 * <ol>
 *   <li><b>Exhaustive reverse scan.</b> On first launch after a Qira APK
 *       update we iterate Qira's resource-id range, read every string
 *       resource in both English and the current device locale, and build
 *       an in-memory index
 *       ({@code normalize(englishText) &rarr; resId},
 *        {@code resId &rarr; currentLocaleText}).
 *       Results are cached to {@code /sdcard/avik/qira-strings-cache/} and
 *       reused on every subsequent run until the Qira versionCode changes.</li>
 *   <li><b>Resolver.</b> When a page object asks for the current-locale
 *       aliases of an English anchor, {@link #resolve(String)} normalizes
 *       the anchor, looks up the matching Qira resId, and pulls the
 *       current-locale value from Qira's own {@link Resources}. No
 *       hand-written translation is involved, so the same code works on
 *       every locale Qira itself ships.</li>
 *   <li><b>Static catalog (fallback only).</b> {@link #loadCatalog()} still
 *       carries a few per-locale anchors that are NOT in Qira's resource
 *       table (e.g. Android framework permission-dialog labels like
 *       "Don't allow" which come from the OS, not Qira). Everything that
 *       <i>is</i> in Qira's resources is resolved via the exhaustive scan
 *       and does not need a catalog entry at all.</li>
 *   <li><b>Explicitly registered resource names</b>
 *       ({@link #registerQiraResource(String, String...)}) still work as a
 *       deterministic override for anchors where the English text is
 *       ambiguous or differs from the resource's English value.</li>
 * </ol>
 *
 * <p>Page objects do not have to be rewritten: the {@code BaseQiraPage}
 * find/wait helpers call {@link #expand(String...)} on every label array
 * before matching, so subclasses keep their existing English anchor
 * literals untouched. New anchors automatically become locale-aware as
 * long as the English text matches a Qira R.string value.
 */
public final class QiraStrings {

    private static final Logger LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    /** Default Motorola Qira package name, matches {@link QiraConfig}. */
    public static final String DEFAULT_QIRA_PACKAGE = "com.lenovo.qira";

    /**
     * Optional, pluggable source of <b>supplemental</b> current-locale aliases
     * for an English anchor, merged in by {@link #resolve(String)} <i>after</i>
     * all built-in tiers. Declared here (in v1) but implemented / registered by
     * qira_v2 (see {@code QiraV2InstrumentationDefaults}) so v1 has no compile
     * dependency on qira_v2. When no resolver is registered (pure v1 / en-XM
     * runs) resolution behaviour is byte-identical to before: the hook is only
     * consulted when non-null, and it only ever <i>adds</i> extra aliases, never
     * reorders or removes the English original or the R.string-backed results.
     *
     * <p>The concrete qira_v2 implementation bridges English anchors to Qira's
     * Compose string catalog ({@code strings.commonMain.cvr}) so labels that are
     * NOT in Android {@code R.string} (Creator Zone tiles, Knowledge / Chat
     * History / Settings copy) still localize by a stable Compose {@code
     * stringId} instead of a visible-text or coordinate fallback.
     */
    public interface SupplementalResolver {
        /**
         * Returns current-locale aliases for {@code englishAnchor}, or an empty
         * array when it resolves nothing. Must never return {@code null} and
         * must not throw.
         */
        String[] localize(String englishAnchor);
    }

    /** Registered supplemental resolver, or {@code null} for pure-v1 behaviour. */
    private static volatile SupplementalResolver supplementalResolver;

    /**
     * Registers (or clears, when {@code resolver} is {@code null}) the
     * {@link SupplementalResolver}. Idempotent and thread-safe; clears the
     * resolved-alias cache so anchors resolved before registration pick up the
     * supplemental aliases on their next lookup. Intended to be called once from
     * qira_v2 setup ({@code @BeforeClass}) before any capture navigation runs.
     */
    public static void setSupplementalResolver(SupplementalResolver resolver) {
        supplementalResolver = resolver;
        QiraStrings local = INSTANCE;
        if (local != null) {
            local.resolvedCache.clear();
        }
    }

    /**
     * Android internationalization testing pseudo-locales. The platform wraps
     * every resource string with Unicode bidi isolate markers
     * (U+2066 / U+2069 etc.) when running in one of these locales, even though
     * the underlying language is English (or Arabic for {@code ar-XB}).
     *
     * <p>Motorola's {@code en-XM} variant additionally encodes <b>SLAP
     * string-link markers</b> in those bidi isolates so the screen-capture
     * pipeline can audit which {@code R.string} backed each rendered label.
     * For that reason we never silently fold pseudo-locales to {@code en-US}:
     * doing so would discard the SLAP encoding from the captured UI. We
     * keep Qira running in the device locale and rely on bidi-tolerant
     * matching ({@link avik.qira.pages.BaseQiraPage}'s regex helpers) plus
     * {@link #stripBidiControls(String)} for plain-equality comparisons to
     * make English anchors match the wrapped on-screen text.
     */
    private static final Set<String> PSEUDO_LOCALE_TAGS;

    static {
        Set<String> tags = new HashSet<>();
        tags.add("en-XM");
        tags.add("en-XA");
        tags.add("ar-XB");
        PSEUDO_LOCALE_TAGS = Collections.unmodifiableSet(tags);
    }

    /**
     * Brand tokens we try to fold back into %s / %1$s placeholders
     * during tier-1b template lookup. Longest first so
     * "Motorola Qira" wins over just "Motorola" when both are present.
     */
    private static final String[] TEMPLATE_BRAND_TOKENS = {
            "Motorola Qira",
            "Motorola",
            "Qira"
    };

    /**
     * Placeholder variants used by Android string resources. Checked in
     * descending specificity so explicit positional forms get tried
     * before the plain {@code %s}.
     */
    private static final String[] TEMPLATE_PLACEHOLDERS = {
            "%1$s",
            "%s"
    };

    /** Lower bound of the Qira string resource ID scan window. */
    private static final int SCAN_RANGE_START = 0x7f010000;
    /** Upper bound of the Qira string resource ID scan window. */
    private static final int SCAN_RANGE_END = 0x7f2f0000;

    /** Cache directory on the device under /sdcard/avik/qira-strings-cache/. */
    private static final String CACHE_SUBDIR = "avik/qira-strings-cache";

    private static volatile QiraStrings INSTANCE;

    private final Context hostContext;
    private final String qiraPackage;

    /**
     * Qira's resources in the device's current (live) locale. Mutable so
     * {@link #onLocaleMayHaveChanged()} can rebuild it when
     * {@code BaseQiraCaptureScript} flips the instrumentation-process
     * locale mid-run via {@code cmd locale set-app-locales}.
     */
    private volatile Resources qiraResourcesCurrent;

    /** Qira's resources forced to en-US, used only by the runtime scanner. */
    private final Resources qiraResourcesEnglish;

    /**
     * anchor (English, lower-cased) &rarr; list of Qira
     * <code>R.string.*</code> resource names that carry that anchor.
     */
    private final Map<String, List<String>> anchorToQiraResNames = new ConcurrentHashMap<>();

    /**
     * anchor (English, lower-cased) &rarr; cached current-locale translations.
     * Rebuilt whenever {@link #onLocaleMayHaveChanged()} detects a locale flip.
     */
    private final Map<String, String[]> resolvedCache = new ConcurrentHashMap<>();

    /** Static, compiled-in per-locale catalog: <code>locale tag &rarr; (anchor &rarr; translation)</code>. */
    private final Map<String, Map<String, String>> catalog;

    /** Last locale we computed translations for; used to invalidate {@link #resolvedCache}. */
    private volatile String lastLocaleTag = "";

    /**
     * Exhaustive reverse-map built by the runtime scan:
     * {@code normalize(englishText) &rarr; Qira R.string integer ID}.
     * Populated once per Qira APK version (cached on device) and used by
     * tier 3 of {@link #resolve(String)} to translate any English anchor
     * whose text matches a Qira string resource into the current-locale
     * value of that same resource ID. This map is the concrete realisation
     * of the "unique ID" strategy described in the class javadoc.
     */
    private final Map<String, Integer> scannedEnglishToResId = new ConcurrentHashMap<>();

    /**
     * Qira R.string entry name -> integer resource ID. This is the runtime
     * equivalent of SLAP's stable String ID for labels that are backed by
     * Android resources.
     */
    private final Map<String, Integer> qiraResNameToId = new ConcurrentHashMap<>();

    /** True once {@link #enableRuntimeResourceScan} has completed (or been tried). */
    private volatile boolean runtimeScanCompleted = false;

    private QiraStrings(Context hostContext,
                        String qiraPackage,
                        Resources qiraResourcesCurrent,
                        Resources qiraResourcesEnglish) {
        this.hostContext = hostContext;
        this.qiraPackage = qiraPackage;
        this.qiraResourcesCurrent = qiraResourcesCurrent;
        this.qiraResourcesEnglish = qiraResourcesEnglish;
        this.catalog = loadCatalog();
    }

    /**
     * Returns the shared instance, creating it on first use. Safe to call
     * from any page object; if the Qira package cannot be opened (e.g. not
     * installed) the helper returns a no-op instance that just echoes the
     * English anchors.
     */
    public static QiraStrings getInstance() {
        QiraStrings local = INSTANCE;
        if (local != null) {
            return local;
        }
        synchronized (QiraStrings.class) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = createSafely();
            return INSTANCE;
        }
    }

    /**
     * Returns {@code true} when {@code tag} (BCP-47 language tag, e.g.
     * {@code "en-XM"}) names one of Android's pseudo-localization locales
     * used for i18n stress-testing. {@code en-XM} additionally carries
     * Motorola's SLAP string-link markers, so the runtime intentionally
     * keeps the locale intact and only uses this predicate for diagnostic
     * logging.
     */
    public static boolean isPseudoLocaleTag(String tag) {
        return tag != null && PSEUDO_LOCALE_TAGS.contains(tag);
    }

    /**
     * Returns {@link Locale#US} when {@code locale} is one of the Android
     * pseudo-locales ({@code en-XM} / {@code en-XA} / {@code ar-XB}),
     * otherwise the original locale. Null input is coerced to
     * {@link Locale#US}.
     *
     * <p>Provided as a stand-alone utility for callers that genuinely need
     * to project a pseudo-locale onto plain English (for example, when
     * rendering log messages or when a future caller wants to fall back to
     * {@code en-US} resource lookups). The capture pipeline itself does
     * <b>not</b> apply this fold to Qira because doing so would strip the
     * SLAP markers that {@code en-XM} encodes into every rendered label.
     */
    public static Locale normalizePseudoLocale(Locale locale) {
        if (locale == null) {
            return Locale.US;
        }
        if (isPseudoLocaleTag(locale.toLanguageTag())) {
            return Locale.US;
        }
        return locale;
    }

    /**
     * Strips Unicode bidi control / formatting characters from a string.
     * Pseudo-locales (e.g. {@code en-XM}) wrap every resource value with
     * LRI/PDI isolates (U+2066 / U+2069) and scatter LRM/RLM (U+200E /
     * U+200F) inside, so plain-text comparisons such as
     * {@code "Chat".equals(desc)} fail even though the visible label is
     * still "Chat". This helper returns the pure text so callers that
     * need literal equality (content-desc matching, clipboard
     * comparisons, etc.) remain locale-safe.
     */
    public static String stripBidiControls(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder sb = null;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            boolean isBidi = c == '\u200E' || c == '\u200F'
                    || (c >= '\u202A' && c <= '\u202E')
                    || (c >= '\u2066' && c <= '\u2069');
            if (isBidi) {
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
        String out = sb != null ? sb.toString() : value;
        return out.trim();
    }

    /**
     * Convenience: expand a single English anchor into <em>{anchor,
     * translated-in-current-locale, &hellip;}</em>. Always returns at least the
     * original input.
     */
    public static String[] expand(String englishAnchor) {
        if (englishAnchor == null) {
            return new String[0];
        }
        return getInstance().resolve(englishAnchor);
    }

    /**
     * Expand every entry of {@code englishAnchors} and merge them into a
     * single de-duplicated alias array. Preserves caller order for the
     * original anchors so exact-match lookups still prefer the English form
     * first on en-US devices.
     */
    public static String[] expandAll(String... englishAnchors) {
        if (englishAnchors == null || englishAnchors.length == 0) {
            return new String[0];
        }
        Set<String> merged = new LinkedHashSet<>();
        QiraStrings strings = getInstance();
        for (String anchor : englishAnchors) {
            if (anchor == null) {
                continue;
            }
            String[] expanded = strings.resolve(anchor);
            Collections.addAll(merged, expanded);
        }
        return merged.toArray(new String[0]);
    }

    /**
     * Register one or more Qira <code>R.string.*</code> resource names that
     * are known to carry the given English anchor. Used as the middle lookup
     * tier between the static catalog and the (expensive) runtime scan.
     */
    public void registerQiraResource(String englishAnchor, String... qiraResourceNames) {
        if (englishAnchor == null || qiraResourceNames == null || qiraResourceNames.length == 0) {
            return;
        }
        String key = englishAnchor.toLowerCase(Locale.ROOT);
        anchorToQiraResNames.computeIfAbsent(key, k -> new java.util.ArrayList<>())
                .addAll(Arrays.asList(qiraResourceNames));
        resolvedCache.remove(key);
    }

    /**
     * Resolve stable Qira string IDs / resource entry names directly into the
     * current-locale strings. This avoids using English text as the lookup key.
     */
    public String[] resolveQiraResourceNames(String... qiraResourceNames) {
        if (qiraResourceNames == null || qiraResourceNames.length == 0) {
            return new String[0];
        }
        onLocaleMayHaveChanged();
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String name : qiraResourceNames) {
            String value = resolveQiraResourceName(name);
            if (value != null && !value.isEmpty()) {
                out.add(value);
            }
        }
        return out.toArray(new String[0]);
    }

    public String resolveQiraResourceName(String qiraResourceName) {
        if (qiraResourceName == null || qiraResourceName.isEmpty()
                || qiraResourcesCurrent == null) {
            return null;
        }
        onLocaleMayHaveChanged();
        Integer id = qiraResNameToId.get(qiraResourceName);
        if (id == null) {
            try {
                int found = qiraResourcesCurrent.getIdentifier(
                        qiraResourceName, "string", qiraPackage);
                if (found != 0) {
                    id = found;
                    qiraResNameToId.put(qiraResourceName, found);
                }
            } catch (Throwable ignored) {
            }
        }
        if (id == null || id == 0) {
            return null;
        }
        try {
            return qiraResourcesCurrent.getString(id);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Returns the stable Qira string resource ID that backs an English anchor,
     * when the exhaustive runtime scan can prove the mapping. This is intended
     * for selector diagnostics and SLAP triage: callers can log the concrete
     * {@code R.string} entry name / integer ID without parsing visible text or
     * maintaining locale tables.
     */
    public ResolvedQiraStringId resolveQiraStringIdForEnglish(String englishAnchor) {
        if (englishAnchor == null || englishAnchor.isEmpty()) {
            return null;
        }
        enableRuntimeResourceScan();
        onLocaleMayHaveChanged();
        if (scannedEnglishToResId.isEmpty()) {
            return null;
        }

        Integer id = findScannedResourceIdForAnchor(englishAnchor);
        if (id == null || id == 0) {
            return null;
        }

        String entryName = null;
        for (Map.Entry<String, Integer> entry : qiraResNameToId.entrySet()) {
            if (entry.getValue() != null && entry.getValue().intValue() == id.intValue()) {
                entryName = entry.getKey();
                break;
            }
        }
        if (entryName == null && qiraResourcesCurrent != null) {
            try {
                entryName = qiraResourcesCurrent.getResourceEntryName(id);
            } catch (Throwable ignored) {
            }
        }

        String localizedValue = null;
        if (qiraResourcesCurrent != null) {
            try {
                localizedValue = qiraResourcesCurrent.getString(id);
                localizedValue = renderTemplateForAnchorIfNeeded(localizedValue, englishAnchor);
            } catch (Throwable ignored) {
            }
        }
        return new ResolvedQiraStringId(englishAnchor, entryName, id, localizedValue);
    }

    public ResolvedQiraStringId[] resolveQiraStringIdsForEnglish(String... englishAnchors) {
        if (englishAnchors == null || englishAnchors.length == 0) {
            return new ResolvedQiraStringId[0];
        }
        java.util.ArrayList<ResolvedQiraStringId> out = new java.util.ArrayList<>();
        for (String anchor : englishAnchors) {
            ResolvedQiraStringId info = resolveQiraStringIdForEnglish(anchor);
            if (info != null) {
                out.add(info);
            }
        }
        return out.toArray(new ResolvedQiraStringId[0]);
    }

    private Integer findScannedResourceIdForAnchor(String englishAnchor) {
        Integer direct = scannedEnglishToResId.get(normalize(englishAnchor));
        if (direct != null) {
            return direct;
        }
        for (String brand : TEMPLATE_BRAND_TOKENS) {
            int idx = englishAnchor.indexOf(brand);
            if (idx < 0) {
                continue;
            }
            String prefix = englishAnchor.substring(0, idx);
            String suffix = englishAnchor.substring(idx + brand.length());
            for (String placeholder : TEMPLATE_PLACEHOLDERS) {
                Integer templated = scannedEnglishToResId.get(
                        normalize(prefix + placeholder + suffix));
                if (templated != null) {
                    return templated;
                }
            }
        }
        return null;
    }

    private static String renderTemplateForAnchorIfNeeded(String value, String englishAnchor) {
        if (value == null || englishAnchor == null) {
            return value;
        }
        if (!value.contains("%s") && !value.contains("%1$s")) {
            return value;
        }
        for (String brand : TEMPLATE_BRAND_TOKENS) {
            if (englishAnchor.contains(brand)) {
                return renderTemplate(value, brand);
            }
        }
        return value;
    }

    public static final class ResolvedQiraStringId {
        private final String englishAnchor;
        private final String entryName;
        private final int resourceId;
        private final String localizedValue;

        private ResolvedQiraStringId(
                String englishAnchor,
                String entryName,
                int resourceId,
                String localizedValue) {
            this.englishAnchor = englishAnchor;
            this.entryName = entryName;
            this.resourceId = resourceId;
            this.localizedValue = localizedValue;
        }

        public String getEnglishAnchor() {
            return englishAnchor;
        }

        public String getEntryName() {
            return entryName;
        }

        public int getResourceId() {
            return resourceId;
        }

        public String getHexResourceId() {
            return "0x" + Integer.toHexString(resourceId);
        }

        public String getLocalizedValue() {
            return localizedValue;
        }

        public String toLogString() {
            return "anchor='" + englishAnchor + "'"
                    + " entry=" + (entryName == null ? "<unknown>" : entryName)
                    + " id=" + getHexResourceId()
                    + " localized='"
                    + QiraStrings.stripBidiControls(localizedValue == null ? "" : localizedValue)
                    + "'";
        }
    }

    /**
     * Kick off an <b>exhaustive</b> scan of Qira's string resources. This
     * walks every integer ID in Qira's resource range, reads the English
     * text for each string resource, normalises it and indexes it back to
     * the resource ID. Results are cached on device (keyed by Qira's
     * versionCode) and reused on every subsequent run.
     *
     * <p>The exhaustive scan is the core of the "unique ID" strategy:
     * instead of matching on-screen text against hand-written translations
     * per locale, we look up the Qira R.string ID whose English value
     * equals the anchor and then ask Qira's own {@link Resources} for the
     * current-locale value of that ID. Because the ID is stable across
     * locales, every language Qira itself ships Just Works.
     *
     * <p>The {@code knownEnglishAnchors} parameter is accepted for
     * backwards compatibility only and is <b>ignored</b> - the scan always
     * processes the full resource range. If the scan fails for any reason
     * (e.g. Qira's resources cannot be opened) the helper keeps working
     * through the catalog and resource-name paths, so callers can safely
     * invoke this and ignore failures.
     */
    public void enableRuntimeResourceScan(String... knownEnglishAnchors) {
        if (runtimeScanCompleted) {
            return;
        }
        runtimeScanCompleted = true;

        LOGGER.info("QiraStrings: enableRuntimeResourceScan called"
                + " (englishRes=" + (qiraResourcesEnglish != null)
                + ", currentRes=" + (qiraResourcesCurrent != null)
                + ", pkg=" + qiraPackage
                + ", locale=" + currentLocaleTag() + ")");

        if (qiraResourcesEnglish == null) {
            LOGGER.info("QiraStrings: scan skipped; Qira English resources unavailable"
                    + " (createPackageContext may have failed)");
            return;
        }

        try {
            if (loadScanCache()) {
                LOGGER.info("QiraStrings: scan skipped; loaded from on-device cache ("
                        + scannedEnglishToResId.size() + " entries).");
                return;
            }

            LOGGER.info("QiraStrings: exhaustive scan of Qira string resources in range 0x"
                    + Integer.toHexString(SCAN_RANGE_START) + "-0x"
                    + Integer.toHexString(SCAN_RANGE_END)
                    + " (runs once per Qira APK version, cached thereafter)");
            long started = System.currentTimeMillis();
            int indexed = 0;
            int collisions = 0;
            int stringsSeen = 0;
            int notFoundCount = 0;
            for (int id = SCAN_RANGE_START; id < SCAN_RANGE_END; id++) {
                CharSequence cs;
                try {
                    // Cheap existence check: getResourceTypeName throws for
                    // unassigned IDs but is much faster than getValue.
                    String type = qiraResourcesEnglish.getResourceTypeName(id);
                    if (!"string".equals(type)) {
                        continue;
                    }
                    cs = qiraResourcesEnglish.getText(id);
                } catch (Resources.NotFoundException nnf) {
                    notFoundCount++;
                    continue;
                } catch (Throwable t) {
                    continue;
                }
                if (cs == null) {
                    continue;
                }
                String value = cs.toString();
                if (value.isEmpty()) {
                    continue;
                }
                stringsSeen++;
                try {
                    String name = qiraResourcesEnglish.getResourceEntryName(id);
                    if (name != null && !name.isEmpty()) {
                        qiraResNameToId.putIfAbsent(name, id);
                    }
                } catch (Throwable ignored) {
                }
                String normalized = normalize(value);
                if (normalized.isEmpty()) {
                    continue;
                }
                // If two different Qira resources ship the same English
                // text we keep the first one we see (the map is
                // populated in ascending-id order). This is rare and
                // never harmful: both IDs carry the same localisation,
                // so either pick resolves to an acceptable alias.
                Integer previous = scannedEnglishToResId.putIfAbsent(normalized, id);
                if (previous == null) {
                    indexed++;
                } else {
                    collisions++;
                }
            }
            LOGGER.info(String.format(Locale.US,
                    "QiraStrings: exhaustive scan done in %d ms, indexed=%d, collisions=%d,"
                            + " strings seen=%d, unassigned=%d",
                    System.currentTimeMillis() - started, indexed, collisions,
                    stringsSeen, notFoundCount));
            // Drop any stale resolved aliases so page objects start
            // picking up fresh scan-backed translations on the very
            // next resolve() call.
            resolvedCache.clear();
            saveScanCache();
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "QiraStrings: runtime resource scan failed (continuing)", t);
        }
    }

    /**
     * Core resolver: returns <em>at least</em> the English anchor itself,
     * followed by every translation we can discover for the current device
     * locale via the exhaustive scan, explicitly registered resource names,
     * and the static catalog.
     *
     * <p>Lookup order is tuned so the most precise source wins first:
     * <ol>
     *   <li>Runtime scan: normalised English &rarr; Qira R.string ID
     *       &rarr; current-locale value. Covers every label Qira itself
     *       ships, automatically, in every Qira-supported locale.</li>
     *   <li>Explicitly registered resource names (override for ambiguous
     *       English anchors).</li>
     *   <li>Static catalog (Android framework labels and anything Qira
     *       does not own).</li>
     * </ol>
     */
    public String[] resolve(String englishAnchor) {
        if (englishAnchor == null || englishAnchor.isEmpty()) {
            return new String[0];
        }
        onLocaleMayHaveChanged();
        String key = englishAnchor.toLowerCase(Locale.ROOT);
        String[] cached = resolvedCache.get(key);
        if (cached != null) {
            return cached;
        }
        LinkedHashSet<String> out = new LinkedHashSet<>();
        out.add(englishAnchor);

        // Tier 1 – runtime resource scan (primary "unique ID" path).
        // The scan maps normalized English text to a Qira R.string ID;
        // we then ask Qira's own Resources for the current-locale value.
        boolean scanHit = false;
        if (!scannedEnglishToResId.isEmpty() && qiraResourcesCurrent != null) {
            Integer resId = scannedEnglishToResId.get(normalize(englishAnchor));
            if (resId != null) {
                try {
                    String value = qiraResourcesCurrent.getString(resId);
                    if (value != null && !value.isEmpty()) {
                        out.add(value);
                        scanHit = true;
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        // Tier 1b – template-substitution lookup. Many Qira strings are
        // format templates containing %s / %1$s placeholders (e.g.
        // "Choose a response language for %s") that render to a
        // concrete brand name at runtime. Tier 1 misses because
        // normalize("…for motorola qira") != normalize("…for %s"). Try
        // substituting each common brand token with a placeholder and
        // looking up the template; if it hits, format the current-
        // locale template back with the brand to produce the rendered
        // alias. Returned aliases are added in addition to the English
        // original so exact-match lookups still prefer the literal.
        if (!scanHit && !scannedEnglishToResId.isEmpty() && qiraResourcesCurrent != null) {
            for (String brand : TEMPLATE_BRAND_TOKENS) {
                int idx = englishAnchor.indexOf(brand);
                if (idx < 0) {
                    continue;
                }
                String prefix = englishAnchor.substring(0, idx);
                String suffix = englishAnchor.substring(idx + brand.length());
                boolean matched = false;
                for (String placeholder : TEMPLATE_PLACEHOLDERS) {
                    String candidate = prefix + placeholder + suffix;
                    Integer resId = scannedEnglishToResId.get(normalize(candidate));
                    if (resId == null) {
                        continue;
                    }
                    try {
                        String template = qiraResourcesCurrent.getString(resId);
                        if (template == null || template.isEmpty()) {
                            continue;
                        }
                        String rendered = renderTemplate(template, brand);
                        if (rendered != null && !rendered.isEmpty()) {
                            out.add(rendered);
                            scanHit = true;
                            matched = true;
                            break;
                        }
                    } catch (Throwable ignored) {
                    }
                }
                if (matched) {
                    break;
                }
            }
        }

        // Tier 2 – explicitly registered Qira resource names (deterministic
        // override for anchors where English text alone is ambiguous).
        List<String> resourceNames = anchorToQiraResNames.get(key);
        if (resourceNames != null && qiraResourcesCurrent != null) {
            for (String name : resourceNames) {
                String value = tryGetStringByResName(name);
                if (value != null && !value.isEmpty()) {
                    out.add(value);
                    scanHit = true;
                }
            }
        }

        // Tier 3 – static catalog. Intentionally empty in the current code
        // base; retained so callers that still consult it keep working, and
        // so framework-level strings can be re-added behind a single
        // lookupCatalog() call if absolutely necessary.
        String catalogHit = lookupCatalog(englishAnchor);
        if (catalogHit != null && !catalogHit.isEmpty()) {
            out.add(catalogHit);
        }

        // Tier 4 – supplemental resolver (qira_v2 Compose-string bridge).
        // Additive only: appended after every built-in tier so the English
        // original and R.string-backed aliases keep their priority (en-XM
        // exact-match ordering is preserved). Only consulted when registered,
        // so pure-v1 runs are unaffected. A hit here means the anchor WAS
        // resolved to a stable Qira Compose stringId, so it also clears the
        // tier-1 MISS diagnostic below.
        SupplementalResolver supplemental = supplementalResolver;
        if (supplemental != null) {
            try {
                String[] extra = supplemental.localize(englishAnchor);
                if (extra != null) {
                    StringBuilder added = null;
                    for (String alias : extra) {
                        if (alias != null && !alias.isEmpty() && out.add(alias)) {
                            scanHit = true;
                            if (added == null) {
                                added = new StringBuilder();
                            } else {
                                added.append(" | ");
                            }
                            added.append(stripBidiControls(alias));
                        }
                    }
                    // One-shot proof (cached per anchor below): shows the
                    // Compose-backed localization applied on a non-English
                    // device. Suppressed for en/en-* so en-XM stays quiet.
                    if (added != null && !currentLocaleTag().toLowerCase(Locale.ROOT)
                            .startsWith("en")) {
                        LOGGER.info("QiraStrings: supplemental (Compose) resolved anchor \""
                                + englishAnchor + "\" in locale " + currentLocaleTag()
                                + " -> '" + added + "'");
                    }
                }
            } catch (Throwable t) {
                LOGGER.log(Level.INFO,
                        "QiraStrings: supplemental resolver threw for anchor \""
                                + englishAnchor + "\" (continuing)", t);
            }
        }

        // Diagnostic: flag anchors that can't be resolved via any Qira-
        // owned unique ID on a non-English device. This is a one-shot log
        // per missed anchor (cached below) and makes it obvious which
        // labels still need a registerQiraResource override.
        if (!scanHit && !"en".equalsIgnoreCase(currentLocaleTag())
                && !currentLocaleTag().toLowerCase(Locale.ROOT).startsWith("en")) {
            LOGGER.info("QiraStrings: tier-1 MISS for anchor \"" + englishAnchor
                    + "\" in locale " + currentLocaleTag()
                    + " (no matching Qira R.string or Compose stringId; add"
                    + " registerQiraResource if this fires consistently)");
        }

        String[] result = out.toArray(new String[0]);
        resolvedCache.put(key, result);
        return result;
    }

    /**
     * Normalise a string for tier-1 lookup: lower-case, collapse internal
     * whitespace to single spaces, strip surrounding whitespace and
     * trailing punctuation ({@code . , ; : ! ?} and their full-width
     * analogues).
     *
     * <p>This makes anchors like {@code "Accept."} match a Qira resource
     * that ships {@code "Accept"}, {@code "I Agree "} match {@code "I
     * agree"}, and so on - without losing specificity: we never compare
     * across word boundaries or strip interior punctuation.
     */
    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String t = value.toLowerCase(Locale.ROOT).trim();
        if (t.isEmpty()) {
            return t;
        }
        // Strip trailing punctuation that varies between the anchor form
        // and the on-disk resource form.
        int end = t.length();
        while (end > 0) {
            char c = t.charAt(end - 1);
            if (c == '.' || c == ',' || c == ';' || c == ':' || c == '!' || c == '?'
                    || c == '\u3002' /* CJK full stop */
                    || c == '\uFF0C' /* CJK comma */
                    || c == '\uFF01' /* CJK exclamation */
                    || c == '\uFF1F' /* CJK question */) {
                end--;
            } else {
                break;
            }
        }
        if (end != t.length()) {
            t = t.substring(0, end);
        }
        // Collapse runs of whitespace to single ASCII spaces so "I  agree"
        // and "I\nagree" both key the same entry.
        if (t.indexOf('\n') >= 0 || t.indexOf('\t') >= 0 || t.indexOf("  ") >= 0
                || t.indexOf('\u00a0') >= 0) {
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
            t = sb.toString().trim();
        }
        return t;
    }

    private String lookupCatalog(String englishAnchor) {
        String tag = currentLocaleTag();
        Map<String, String> primary = catalog.get(tag);
        String key = englishAnchor.toLowerCase(Locale.ROOT);
        if (primary != null) {
            String hit = primary.get(key);
            if (hit != null) {
                return hit;
            }
        }
        // Fall back to language-only tag (e.g. "pt" when "pt-BR" is missing).
        int dash = tag.indexOf('-');
        if (dash > 0) {
            Map<String, String> lang = catalog.get(tag.substring(0, dash));
            if (lang != null) {
                String hit = lang.get(key);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return null;
    }

    /**
     * Renders a Qira string template (e.g. {@code "Choose a response language for %s"}
     * or {@code "Hi, I'm %1$s"}) by substituting {@code brand} for the
     * first placeholder it finds. Falls back to literal replacement if
     * {@link String#format(String, Object...)} throws on an unusual
     * template.
     */
    private static String renderTemplate(String template, String brand) {
        if (template == null || template.isEmpty() || brand == null) {
            return template;
        }
        try {
            if (template.contains("%1$s") || template.contains("%s")) {
                return String.format(template, brand);
            }
        } catch (Throwable ignored) {
        }
        String rendered = template;
        if (rendered.contains("%1$s")) {
            rendered = rendered.replace("%1$s", brand);
        }
        if (rendered.contains("%s")) {
            rendered = rendered.replace("%s", brand);
        }
        return rendered;
    }

    private String tryGetStringByResName(String qiraResName) {
        if (qiraResourcesCurrent == null) {
            return null;
        }
        try {
            int id = qiraResourcesCurrent.getIdentifier(qiraResName, "string", qiraPackage);
            if (id == 0) {
                return null;
            }
            return qiraResourcesCurrent.getString(id);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Called whenever the caller suspects the device or instrumentation
     * process locale may have changed. Drops the resolved-alias cache so
     * subsequent {@link #resolve(String)} calls pick up translations for
     * the new locale, and reopens Qira's Resources bound to the new
     * locale so runtime-scan lookups return localized values instead of
     * the en-US baseline they were created with.
     *
     * <p>Invoked from {@link #resolve(String)} on every lookup (so the
     * cache self-heals on the first request after a flip) and also
     * explicitly from capture scripts that actively change the locale at
     * setup time, to invalidate translations cached before the flip.
     */
    public void onLocaleMayHaveChanged() {
        String current = currentLocaleTag();
        if (!Objects.equals(current, lastLocaleTag)) {
            resolvedCache.clear();
            lastLocaleTag = current;
            reopenQiraResourcesForCurrentLocale();
        }
    }

    /**
     * Recreates {@link #qiraResourcesCurrent} bound to whatever locale
     * the host context is in right now. Required so tier-3 lookups
     * (runtime scan cache) return the <em>current</em> locale's
     * translation of a resource id, not the locale the context was
     * originally opened in.
     */
    private void reopenQiraResourcesForCurrentLocale() {
        if (hostContext == null || qiraPackage == null) {
            return;
        }
        try {
            Locale requested = hostContext.getResources().getConfiguration().getLocales().get(0);
            if (requested == null) {
                requested = Locale.getDefault();
            }
            // Honour the device locale verbatim, including pseudo-locales
            // like en-XM. en-XM carries SLAP string-link markers in every
            // resource value and we want the runtime scan / resolve path
            // to see exactly what the rendered UI shows. Bidi-tolerant
            // matching in BaseQiraPage (and stripBidiControls() for direct
            // equality checks) absorbs the markers at compare time.
            Configuration cfg = new Configuration(hostContext.getResources().getConfiguration());
            cfg.setLocale(requested);
            Context qiraCtx = hostContext.createPackageContext(qiraPackage, 0)
                    .createConfigurationContext(cfg);
            qiraResourcesCurrent = qiraCtx.getResources();
            LOGGER.info("QiraStrings: reopened Qira resources for locale "
                    + requested.toLanguageTag());
        } catch (Throwable t) {
            LOGGER.log(Level.INFO,
                    "QiraStrings: failed to reopen Qira resources for new locale", t);
        }
    }

    private String currentLocaleTag() {
        Locale locale = getCurrentLocale();
        return locale == null ? "" : locale.toLanguageTag();
    }

    /**
     * Returns the device's current UI locale (i.e. whatever the Qira /
     * host resource configuration is bound to right now). Page objects
     * use this to decide which row of the Qira language picker to
     * select — honouring the system language is the whole point of the
     * response-language choice, and reading it off the host resources
     * stays in sync with the master flow's {@code -e qira.locale} arg
     * and with {@link #onLocaleMayHaveChanged()}.
     *
     * <p>Pseudo-locales such as {@code en-XM} are returned <i>verbatim</i>
     * here — they carry SLAP string-link markers that the screen-capture
     * pipeline depends on, so we never silently downgrade them to
     * {@code en-US}. Callers that need to compare anchor text against
     * pseudo-localized values use {@link #stripBidiControls(String)} or
     * the bidi-tolerant regex helpers in {@code BaseQiraPage}.
     *
     * <p>Falls back to {@link Locale#getDefault()} if the host context
     * is unavailable (e.g. very early in instrumentation setup) and
     * finally to {@link Locale#US} so callers never have to null-check.
     */
    public Locale getCurrentLocale() {
        Locale locale = null;
        try {
            if (hostContext != null) {
                locale = hostContext.getResources().getConfiguration().getLocales().get(0);
            }
        } catch (Throwable ignored) {
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale == null) {
            locale = Locale.US;
        }
        return locale;
    }

    /**
     * Returns {@code true} when the active UI locale is right-to-left
     * (Arabic, Hebrew, Persian, Urdu, ...). Unlike
     * {@code TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())},
     * which reflects the instrumentation process's default locale, this
     * helper consults the <em>device</em> locale via the host / Qira
     * Resources configuration — the same signal the Qira UI actually
     * uses to lay itself out. The process-level default lags behind the
     * device locale when a capture runs without an explicit
     * {@code -e qira.locale} arg (e.g. the master flow in common mode),
     * which is why every geometry heuristic that needs to know "is the
     * primary action on the left or on the right?" must call this
     * helper instead of {@link Locale#getDefault()}.
     */
    public boolean isCurrentLocaleRtl() {
        Locale locale = null;
        try {
            if (qiraResourcesCurrent != null) {
                Configuration cfg = qiraResourcesCurrent.getConfiguration();
                if (cfg != null && cfg.getLocales() != null && !cfg.getLocales().isEmpty()) {
                    locale = cfg.getLocales().get(0);
                }
            }
        } catch (Throwable ignored) {
        }
        if (locale == null) {
            try {
                if (hostContext != null) {
                    locale = hostContext.getResources().getConfiguration().getLocales().get(0);
                }
            } catch (Throwable ignored) {
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale == null) {
            return false;
        }
        return android.text.TextUtils.getLayoutDirectionFromLocale(locale)
                == android.view.View.LAYOUT_DIRECTION_RTL;
    }

    // ---------------------------------------------------------------------
    // Scan cache persistence
    // ---------------------------------------------------------------------

    private File scanCacheFile() {
        if (hostContext == null) {
            return null;
        }
        try {
            long versionCode = 0L;
            try {
                versionCode = hostContext.getPackageManager()
                        .getPackageInfo(qiraPackage, 0).getLongVersionCode();
            } catch (Throwable ignored) {
            }
            File dir = new File(Environment.getExternalStorageDirectory(), CACHE_SUBDIR);
            if (!dir.exists() && !dir.mkdirs()) {
                dir = hostContext.getFilesDir();
            }
            if (dir == null) {
                return null;
            }
            // Bumped to v2 when the scan switched from anchor-list
            // matching to an exhaustive normalized-english index. Older
            // caches lack most entries and would force every run to miss.
            return new File(dir, "qira-strings-v2-" + versionCode + ".json");
        } catch (Throwable t) {
            return null;
        }
    }

    private boolean loadScanCache() {
        File file = scanCacheFile();
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            JSONObject json = new JSONObject(new String(raw, StandardCharsets.UTF_8));
            int count = 0;
            JSONObject names = json.optJSONObject("_qiraResNameToId");
            if (names != null) {
                for (java.util.Iterator<String> it = names.keys(); it.hasNext(); ) {
                    String name = it.next();
                    int id = names.optInt(name, 0);
                    if (id != 0) {
                        qiraResNameToId.put(name, id);
                    }
                }
            }
            for (java.util.Iterator<String> it = json.keys(); it.hasNext(); ) {
                String key = it.next();
                if (key.startsWith("_")) {
                    continue;
                }
                int id = json.optInt(key, 0);
                if (id != 0) {
                    scannedEnglishToResId.put(key, id);
                    count++;
                }
            }
            LOGGER.info("QiraStrings: loaded " + count + " anchors from scan cache " + file);
            return count > 0;
        } catch (Throwable t) {
            LOGGER.log(Level.INFO, "QiraStrings: scan cache unreadable, will rescan", t);
            return false;
        }
    }

    private void saveScanCache() {
        File file = scanCacheFile();
        if (file == null || scannedEnglishToResId.isEmpty()) {
            return;
        }
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, Integer> e : scannedEnglishToResId.entrySet()) {
                json.put(e.getKey(), e.getValue());
            }
            JSONObject names = new JSONObject();
            for (Map.Entry<String, Integer> e : qiraResNameToId.entrySet()) {
                names.put(e.getKey(), e.getValue());
            }
            json.put("_qiraResNameToId", names);
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.INFO, "QiraStrings: failed to persist scan cache", t);
        }
    }

    // ---------------------------------------------------------------------
    // Bootstrap
    // ---------------------------------------------------------------------

    private static QiraStrings createSafely() {
        Context host;
        try {
            host = InstrumentationRegistry.getInstrumentation().getTargetContext();
        } catch (Throwable t) {
            try {
                host = InstrumentationRegistry.getInstrumentation().getContext();
            } catch (Throwable ignored) {
                // Cannot resolve a Context at all; return an empty helper that
                // always echoes the English input.
                return new QiraStrings(null, DEFAULT_QIRA_PACKAGE, null, null);
            }
        }
        String pkg;
        try {
            pkg = QiraConfig.fromInstrumentation().getPackageName();
        } catch (Throwable t) {
            pkg = DEFAULT_QIRA_PACKAGE;
        }

        Resources current = null;
        Resources english = null;
        // Try progressively weaker flag combinations. Some devices/ROMs deny
        // CONTEXT_IGNORE_SECURITY for non-system callers; plain flags=0 works
        // in most cases because we only need resources, not code.
        int[] flagCombos = new int[] {
                0,
                Context.CONTEXT_RESTRICTED,
                Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_RESTRICTED,
                Context.CONTEXT_IGNORE_SECURITY
        };
        Throwable lastFailure = null;
        for (int flags : flagCombos) {
            try {
                Context qiraCtx = host.createPackageContext(pkg, flags);
                // Bind to the device locale verbatim, including pseudo-
                // locales like en-XM that carry SLAP string-link markers.
                // The bidi markers are absorbed at compare time (regex
                // BIDI_WHITESPACE_CLASS in BaseQiraPage and
                // stripBidiControls() for plain equality), so we never
                // need to silently downgrade the locale here.
                current = qiraCtx.getResources();

                Configuration enCfg = new Configuration(current.getConfiguration());
                enCfg.setLocale(Locale.US);
                english = host.createPackageContext(pkg, flags)
                        .createConfigurationContext(enCfg)
                        .getResources();
                Locale deviceLocale = current.getConfiguration().getLocales().get(0);
                LOGGER.info("QiraStrings: opened Qira resources for '" + pkg
                        + "' using flags=" + flags
                        + " (locale=" + (deviceLocale != null
                                ? deviceLocale.toLanguageTag() : "<unknown>")
                        + (deviceLocale != null
                                && isPseudoLocaleTag(deviceLocale.toLanguageTag())
                                ? "; pseudo-locale: SLAP markers preserved"
                                : "")
                        + ")");
                lastFailure = null;
                break;
            } catch (PackageManager.NameNotFoundException nnfe) {
                LOGGER.info("QiraStrings: Qira package '" + pkg
                        + "' not installed; locale expansion disabled");
                lastFailure = nnfe;
                break;
            } catch (Throwable t) {
                lastFailure = t;
                current = null;
                english = null;
            }
        }
        if (lastFailure != null && !(lastFailure instanceof PackageManager.NameNotFoundException)) {
            LOGGER.info("QiraStrings: cannot open Qira resources: "
                    + lastFailure.getClass().getSimpleName() + ": " + lastFailure.getMessage());
        }
        return new QiraStrings(host, pkg, current, english);
    }

    // ---------------------------------------------------------------------
    // Static per-locale catalog (intentionally empty)
    // ---------------------------------------------------------------------

    /**
     * Returns the (intentionally empty) static translation catalog.
     *
     * <p>Historical context: this class used to ship hand-written per-locale
     * translation tables (&gt;600 entries across 15 locales) that covered the
     * labels each Qira page object needed. That approach drifted out of sync
     * with Qira's resource table every time the app re-worded a string, and
     * produced silent false-positive matches when a stale translation
     * happened to appear elsewhere on screen.
     *
     * <p>Translations now come <b>exclusively</b> from Qira's own
     * {@code R.string} table via {@link #enableRuntimeResourceScan()}: we
     * build a {@code normalize(english) -&gt; resId} index once per APK
     * version (cached to /sdcard/avik/qira-strings-cache/), then ask Qira's
     * {@link Resources} for the current-locale value at resolve time. The
     * {@code R.string} integer IDs are the stable "unique IDs" - they do not
     * change when the device locale changes, so the same code path works on
     * every locale Qira itself ships.
     *
     * <p>Android framework strings (permission dialog labels like
     * {@code Allow} / {@code Don\u2019t allow} / {@code Only this time} /
     * {@code Allow only while using the app}) are clicked via their stable
     * {@code com.android.permissioncontroller:id/...} resource IDs in
     * {@code BaseQiraPage.handleSystemPermissionPrompt}, so they need no
     * text translation either.
     *
     * <p>If a new anchor ever misses tier 1 (the scan) in some locale,
     * prefer {@link #registerQiraResource(String, String...)} over hand-
     * writing translations - that keeps the lookup driven by Qira's unique
     * resource IDs instead of fragile text tables.
     */
    private static Map<String, Map<String, String>> loadCatalog() {
        // Intentionally empty.
        //
        // Every Qira-owned string is resolved through the exhaustive R.string
        // scan in enableRuntimeResourceScan() (normalize(english) -> resId ->
        // qiraResourcesCurrent.getString(resId)). Android framework strings
        // on the system permission dialog (Allow / Don’t allow / Only
        // this time / Allow only while using the app) are clicked via their
        // stable permissioncontroller resource IDs in
        // BaseQiraPage.handleSystemPermissionPrompt, which is locale-agnostic.
        //
        // If a specific anchor ever misses tier 1 (the Qira scan) in a new
        // locale, prefer registerQiraResource(anchor, resName) over hand-
        // written translations so we remain driven by Qira's own unique IDs.
        return Collections.emptyMap();
    }
}
