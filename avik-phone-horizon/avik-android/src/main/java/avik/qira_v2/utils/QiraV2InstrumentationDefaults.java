package avik.qira_v2.utils;

import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.Locale;
import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

public final class QiraV2InstrumentationDefaults {

    public static final String SLAP_DISCOVERY_LOCALE = "en-XM";
    public static final String WORKBENCH_APP_ID = "qira";

    private static final String[] EMPTY = new String[0];
    private static final String ONBOARDING_FALLBACK_PROPERTY =
            "qira.v2.onboardingFallback";

    private QiraV2InstrumentationDefaults() {
    }

    /**
     * Applies defaults before BaseQiraCaptureScript builds QiraConfig.
     * The qira_v2 start script is an ID-discovery capture, so SLAP text
     * extraction and UI dumps are mandatory.
     */
    public static void apply() {
        try {
            Bundle args = InstrumentationRegistry.getArguments();
            if (args != null) {
                putDefault(args, QiraConfig.ARG_LOCALE, SLAP_DISCOVERY_LOCALE);
                args.putString("app_id", WORKBENCH_APP_ID);
                args.putString(QiraConfig.ARG_DUMP_UI, "true");
                args.putString(QiraConfig.ARG_INCLUDE_SCREENSHOT_TEXT, "true");
                args.putString(QiraConfig.ARG_INCLUDE_SCREENSHOT_DESCRIPTION, "true");
            }
        } catch (Throwable ignored) {
        }

        // Overlay-based Qira surfaces may expose their foreground strings in a
        // non-focused accessibility window. Foreground-only filtering drops
        // those strings from screenshot metadata, so qira_v2 explicitly uses
        // the complete hierarchy for SLAP/string-link capture.
        try {
            System.clearProperty("avik.foregroundWindowOnly");
            System.setProperty(ONBOARDING_FALLBACK_PROPERTY, "true");
        } catch (Throwable ignored) {
        }
        registerComposeSupplementalResolver();
    }

    /**
     * The v1 zone scripts opt in only when they are launched through qira_v2.
     * This keeps legacy Qira v1 entry behavior unchanged.
     */
    public static boolean isOnboardingFallbackEnabled() {
        try {
            return Boolean.parseBoolean(System.getProperty(ONBOARDING_FALLBACK_PROPERTY));
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Bridges the v1 English-anchor -> localized-alias resolver to Qira's
     * Compose string catalog, scoped to qira_v2 only. Registered here (run
     * {@code @BeforeClass} by every qira_v2 script) so the Creator Zone /
     * Knowledge / Chat History / Settings flows - which anchor on English
     * literals that live in Qira's Compose resources, not Android
     * {@code R.string} - localize by a stable Compose {@code stringId} in every
     * locale. Pure-v1 runs never call this, so their behaviour is unchanged.
     */
    private static void registerComposeSupplementalResolver() {
        try {
            final String qiraPackage = resolveQiraPackageName();
            QiraStrings.setSupplementalResolver(new QiraStrings.SupplementalResolver() {
                @Override
                public String[] localize(String englishAnchor) {
                    try {
                        return QiraV2ComposeStrings.localizeEnglishAnchor(
                                qiraPackage, englishAnchor, null);
                    } catch (Throwable t) {
                        return EMPTY;
                    }
                }
            });
        } catch (Throwable ignored) {
        }
    }

    private static String resolveQiraPackageName() {
        try {
            String pkg = QiraConfig.fromInstrumentation().getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                return pkg;
            }
        } catch (Throwable ignored) {
        }
        return QiraStrings.DEFAULT_QIRA_PACKAGE;
    }

    public static void logEffectiveConfig(QiraConfig config, Logger logger) {
        if (config == null || logger == null) {
            return;
        }
        String locale = config.getLocale();
        boolean slapLocale = SLAP_DISCOVERY_LOCALE.equalsIgnoreCase(locale);
        logger.info("QiraV2 SLAP config: locale="
                + (locale == null || locale.isEmpty() ? "<device>" : locale)
                + ", dumpUi=" + config.shouldDumpUi()
                + ", includeScreenshotText=" + config.shouldIncludeScreenshotText()
                + ", includeScreenshotDescription="
                + config.shouldIncludeScreenshotDescription());
        if (!slapLocale) {
            logger.info("QiraV2 SLAP config warning: expected "
                    + SLAP_DISCOVERY_LOCALE
                    + " for string/message ID discovery, got "
                    + (locale == null || locale.isEmpty() ? "<device>" : locale));
        }
        if (!config.shouldDumpUi()
                || !config.shouldIncludeScreenshotText()
                || !config.shouldIncludeScreenshotDescription()) {
            logger.info(String.format(Locale.US,
                    "QiraV2 SLAP config warning: screenshots are forced as"
                            + " dumpUi=true, includeText=true,"
                            + " includeDescription=true for this flow."));
        }
    }

    private static void putDefault(Bundle args, String key, String value) {
        String current = args.getString(key);
        if (current == null || current.trim().isEmpty()) {
            args.putString(key, value);
        }
    }
}
