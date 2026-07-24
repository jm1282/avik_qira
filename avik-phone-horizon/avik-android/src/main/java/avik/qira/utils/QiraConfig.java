package avik.qira.utils;

import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.Locale;

public class QiraConfig {

    public static final String ARG_PACKAGE_NAME = "qira.package";
    public static final String ARG_LAUNCH_ACTIVITY = "qira.activity";
    public static final String ARG_PROMPT_ACTIVITY = "qira.promptActivity";
    public static final String ARG_SCREENSHOT_PREFIX = "qira.screenshotPrefix";
    public static final String ARG_STAGE_PROMPT = "qira.prompt";
    public static final String ARG_CLEAR_DATA = "qira.clearData";
    /**
     * When {@code true}, every call to {@code takeScreenshot} in
     * {@link avik.qira.scripts.BaseQiraCaptureScript} also writes a
     * UiAutomator XML dump + resource-id inventory under
     * {@link QiraUiDumper#DUMP_ROOT} for that surface. This is how we
     * harvest stable, locale-independent selectors for page-object refactoring.
     *
     * <p>Defaults to {@code true} because the dumps are small, cheap to
     * generate, and every real-world debugging session so far has needed
     * them. Pass {@code -e qira.dumpUi false} on the {@code am instrument}
     * command line if you ever need to disable them.
     */
    public static final String ARG_DUMP_UI = "qira.dumpUi";
    /**
     * Whether Avik screenshot metadata should include node text.
     * Defaults to {@code true}. Pass
     * {@code -e qira.includeScreenshotText false} to disable.
     */
    public static final String ARG_INCLUDE_SCREENSHOT_TEXT = "qira.includeScreenshotText";
    /**
     * Whether Avik screenshot metadata should include content-description.
     * Defaults to {@code false} for Qira because Compose surfaces often expose
     * a giant root description that collapses string-linking to one full-screen
     * node. Pass {@code -e qira.includeScreenshotDescription true} to opt in.
     */
    public static final String ARG_INCLUDE_SCREENSHOT_DESCRIPTION =
            "qira.includeScreenshotDescription";

    /**
     * BCP-47 locale tag (e.g. {@code pt-BR}, {@code ar-EG}, {@code de},
     * {@code zh-Hant-TW}) to apply to Motorola Qira for the duration of
     * the capture run. When set, {@code BaseQiraCaptureScript.setUp()}
     * calls {@code cmd locale set-app-locales --user current
     * <qira package> --locales <tag>} (Android 13+) plus a
     * {@code LOCALE_CHANGED} broadcast so Qira re-inflates its resources
     * in the requested language before the very first screenshot. Also
     * applies the locale to the test instrumentation process itself, so
     * {@link QiraStrings#resolve(String)} expands anchors using the
     * requested catalog row even if the device's system locale differs.
     *
     * <p>Pass {@code -e qira.locale pt-BR} on {@code am instrument} to
     * drive the run in Brazilian Portuguese, for example. Leave unset to
     * honour whatever locale the device is already in (normal behaviour).
     */
    public static final String ARG_LOCALE = "qira.locale";

    /**
     * Optional: also attempt to change the <em>device system</em> locale
     * to the value supplied in {@link #ARG_LOCALE}. Defaults to
     * {@code true} whenever {@link #ARG_LOCALE} is provided so the
     * master capture suite always exercises Qira at the requested locale
     * across every sub-flow without the caller having to remember the
     * extra flag. Pass {@code -e qira.applySystemLocale false} to opt
     * out (only useful when running a single sub-flow during page
     * object debugging, where the system locale should stay untouched).
     */
    public static final String ARG_APPLY_SYSTEM_LOCALE = "qira.applySystemLocale";

    /**
     * Optional: when set together with {@link #ARG_LOCALE}, the locale
     * applier is permitted to soft-reboot the device as a last-resort
     * fallback if the {@code settings put system_locales} +
     * {@code setprop persist.sys.locale} +
     * {@code IActivityManager.updatePersistentConfiguration()} chain
     * all fail to flip the system locale. Defaults to {@code false}
     * because a reboot is disruptive on a developer's interactive
     * session, but the unattended driver script
     * (under {@code .scratch/run-all-locales.bat}) sets this to
     * {@code true} so a wedged locale state can self-heal.
     */
    public static final String ARG_ALLOW_REBOOT = "qira.allowReboot";

    /**
     * Opt-out for the self-healing master promotion in
     * {@link avik.qira.scripts.MotorolaQiraHomeCapture#testMain()}.
     *
     * <p>When the workbench Avik plugin Run config invokes the
     * {@code MotorolaQiraHomeCapture} test class, by default it now
     * self-promotes to the full {@link avik.qira.scripts.MotorolaQiraMasterCapture}
     * pipeline (Home + FocusZone + CreatorZone + Knowledge + ChatHistory
     * + Settings) so every locale produces all 89 baseline screens
     * regardless of which test class the workbench plugin is
     * configured to invoke. This is what reviewers actually want when
     * they hit "Run" in the workbench tool window.
     *
     * <p>Pass {@code -e qira.homeOnly true} on {@code am instrument}
     * (or set it in your IDE Run config) to skip the promotion and
     * run only the Home capture sub-flow. Useful for local Home-only
     * triage, ~5-10 minute runs.
     */
    public static final String ARG_HOME_ONLY = "qira.homeOnly";

    private static final String DEFAULT_PACKAGE_NAME = "com.lenovo.qira";
    private static final String DEFAULT_LAUNCH_ACTIVITY =
            "com.lenovo.quantum.prompt.QuantumLauncherRouterActivity";
    private static final String DEFAULT_PROMPT_ACTIVITY =
            "com.lenovo.quantum.prompt.PromptLaunchActivity";
    private static final String DEFAULT_SCREENSHOT_PREFIX = "MotorolaQira";

    private final String packageName;
    private final String launchActivity;
    private final String promptActivity;
    private final String screenshotPrefix;
    private final String stagedPrompt;
    private final boolean clearData;
    private final boolean dumpUi;
    private final boolean includeScreenshotText;
    private final boolean includeScreenshotDescription;
    private final String locale;
    private final boolean applySystemLocale;
    private final boolean allowReboot;
    private final boolean homeOnly;

    private QiraConfig(
            String packageName,
            String launchActivity,
            String promptActivity,
            String screenshotPrefix,
            String stagedPrompt,
            boolean clearData,
            boolean dumpUi,
            boolean includeScreenshotText,
            boolean includeScreenshotDescription,
            String locale,
            boolean applySystemLocale,
            boolean allowReboot,
            boolean homeOnly) {
        this.packageName = packageName;
        this.launchActivity = launchActivity;
        this.promptActivity = promptActivity;
        this.screenshotPrefix = screenshotPrefix;
        this.stagedPrompt = stagedPrompt;
        this.clearData = clearData;
        this.dumpUi = dumpUi;
        this.includeScreenshotText = includeScreenshotText;
        this.includeScreenshotDescription = includeScreenshotDescription;
        this.locale = locale;
        this.applySystemLocale = applySystemLocale;
        this.allowReboot = allowReboot;
        this.homeOnly = homeOnly;
    }

    public static QiraConfig fromInstrumentation() {
        Bundle arguments = InstrumentationRegistry.getArguments();
        String requestedLocale = getOptionalArgument(arguments, ARG_LOCALE);
        boolean defaultIncludeScreenshotText = isPseudoLocale(requestedLocale) ? false : true;

        return new QiraConfig(
                getRequiredArgument(arguments, ARG_PACKAGE_NAME, DEFAULT_PACKAGE_NAME),
                getRequiredArgument(arguments, ARG_LAUNCH_ACTIVITY, DEFAULT_LAUNCH_ACTIVITY),
                getRequiredArgument(arguments, ARG_PROMPT_ACTIVITY, DEFAULT_PROMPT_ACTIVITY),
                getRequiredArgument(arguments, ARG_SCREENSHOT_PREFIX, DEFAULT_SCREENSHOT_PREFIX),
                getOptionalArgument(arguments, ARG_STAGE_PROMPT),
                getBooleanArgument(arguments, ARG_CLEAR_DATA, false),
                getBooleanArgument(arguments, ARG_DUMP_UI, true),
                getBooleanArgument(arguments,
                        ARG_INCLUDE_SCREENSHOT_TEXT,
                        defaultIncludeScreenshotText),
                getBooleanArgument(arguments, ARG_INCLUDE_SCREENSHOT_DESCRIPTION, false),
                requestedLocale,
                getBooleanArgument(arguments, ARG_APPLY_SYSTEM_LOCALE, true),
                getBooleanArgument(arguments, ARG_ALLOW_REBOOT, false),
                getBooleanArgument(arguments, ARG_HOME_ONLY, false)
        );
    }

    private static boolean isPseudoLocale(String localeTag) {
        if (localeTag == null || localeTag.trim().isEmpty()) {
            return false;
        }
        String lower = localeTag.trim().toLowerCase(Locale.ROOT);
        return "en-xa".equals(lower)
                || "en-xb".equals(lower)
                || "en-xm".equals(lower);
    }

    private static String getRequiredArgument(Bundle arguments, String key, String defaultValue) {
        String value = arguments.getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String getOptionalArgument(Bundle arguments, String key) {
        String value = arguments.getString(key);
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private static boolean getBooleanArgument(Bundle arguments, String key, boolean defaultValue) {
        String value = arguments.getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLaunchActivity() {
        return launchActivity;
    }

    public boolean hasLaunchActivity() {
        return !launchActivity.isEmpty();
    }

    public String getScreenshotPrefix() {
        return screenshotPrefix;
    }

    public String getPromptActivity() {
        return promptActivity;
    }

    public String getStagedPrompt() {
        return stagedPrompt;
    }

    public boolean hasStagedPrompt() {
        return !stagedPrompt.isEmpty();
    }

    public boolean shouldClearData() {
        return clearData;
    }

    public boolean shouldDumpUi() {
        return dumpUi;
    }

    /**
     * Whether screenshot metadata should include text nodes.
     */
    public boolean shouldIncludeScreenshotText() {
        return includeScreenshotText;
    }

    /**
     * Whether screenshot metadata should include content-description nodes.
     */
    public boolean shouldIncludeScreenshotDescription() {
        return includeScreenshotDescription;
    }

    /**
     * Requested BCP-47 locale tag (see {@link #ARG_LOCALE}). Returns an
     * empty string when the caller did not request a specific locale, in
     * which case the capture run honours the device's existing locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * True when the caller explicitly passed {@code -e qira.locale <tag>}.
     */
    public boolean hasLocale() {
        return locale != null && !locale.isEmpty();
    }

    /**
     * Whether the caller asked us to also change the device system locale
     * (see {@link #ARG_APPLY_SYSTEM_LOCALE}). When {@code false} we only
     * change Motorola Qira's per-app locale, leaving the rest of the
     * device unchanged.
     */
    public boolean shouldApplySystemLocale() {
        return applySystemLocale;
    }

    /**
     * Whether the locale applier may soft-reboot as a last-resort
     * fallback when every other system-locale flip mechanism fails.
     * See {@link #ARG_ALLOW_REBOOT} for the rationale.
     */
    public boolean shouldAllowReboot() {
        return allowReboot;
    }

    /**
     * Whether {@link avik.qira.scripts.MotorolaQiraHomeCapture#testMain()}
     * should skip the self-promotion to the full
     * {@link avik.qira.scripts.MotorolaQiraMasterCapture} pipeline and
     * run only the Home capture sub-flow. See {@link #ARG_HOME_ONLY}
     * for the rationale.
     */
    public boolean shouldRunHomeOnly() {
        return homeOnly;
    }
}
