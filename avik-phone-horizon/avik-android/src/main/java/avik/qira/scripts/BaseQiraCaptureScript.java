package avik.qira.scripts;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.Locale;
import java.util.logging.Logger;

import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraOnboardingPage;
import avik.qira.utils.QiraApp;
import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira.utils.QiraUiDumper;
import avik.qira_v2.utils.QiraV2HomeOnboardingFlow;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

public abstract class BaseQiraCaptureScript {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    protected final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    protected UiDevice mDevice;
    protected AvikUtility mUtils;
    protected QiraApp mQiraApp;
    protected QiraConfig mConfig;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtils = AvikUtility.getInstance();
        mConfig = QiraConfig.fromInstrumentation();
        mQiraApp = new QiraApp(mConfig);

        // Keep the device awake and unlocked for the entire capture run.
        // Without this, long-running sub-flows (permission panels, scroll
        // animations, hotword setup) routinely trip the screen-off timer and
        // the next interaction hits the keyguard instead of Qira. Everything
        // here is idempotent and safe to run on every @Before.
        prepareDeviceForCapture();

        // Apply the requested locale (if any) BEFORE clearing Qira's data
        // and BEFORE the first launch, so Qira's first-run onboarding
        // renders in the target language. Applied after
        // prepareDeviceForCapture() so the screen is already on (some
        // vendor firmware ignores locale commands while the display is
        // asleep) but before clearApp() / forceStop() so the app picks up
        // the new locale on its very next launch.
        applyRequestedLocale();

        // Force-stop every common app that could be holding a stale
        // pre-locale-flip Activity on screen. The pt-BR -> en-XM
        // regression observed by the user came from a Lenovo
        // SetupWizard "Sair da configuração?" dialog left over from
        // the previous locale's run; clearApp() of Qira alone did not
        // dismiss it because it lives in a different package. Pressing
        // HOME afterwards returns us to a freshly-localized launcher
        // surface so the next Qira launch starts from a clean slate
        // in the requested locale.
        runShell("am force-stop com.lenovo.setupwizard");
        runShell("am force-stop com.android.settings");
        runShell("input keyevent KEYCODE_HOME");
        try {
            mUtils.sleep(500L);
        } catch (Throwable ignored) {
        }

        if (mConfig.shouldClearData()) {
            mQiraApp.clearApp();
        } else {
            mQiraApp.forceStop();
        }
        mUtils.pressBackKeySeveralTimes(3);

        // Pre-grant runtime permissions that the onboarding permission panel
        // would otherwise prompt for. Failures here are non-fatal - the
        // permission may not be declared on this Qira build, or the device
        // may already have granted it. The panel itself still captures the
        // UI state; this just removes the Android OS prompt that can
        // randomly land on top of Qira.
        preGrantRuntimePermissions();

        // Build the exhaustive Qira R.string index. This is the "unique
        // ID" bridge: we iterate every string resource in Qira's APK,
        // key it by normalised English text, and cache the result at
        // /sdcard/avik/qira-strings-cache/qira-strings-v2-<versionCode>.json.
        // Every subsequent lookup just asks Qira's own Resources for the
        // current-locale value of the matching resource ID - no
        // hand-written translation involved - so the same code handles
        // every locale Qira itself ships. First run takes ~40 seconds on
        // a cold device; every run after that reuses the cached index.
        try {
            QiraStrings.getInstance();
            // applyRequestedLocale() may have flipped the instrumentation
            // process + Qira per-app locale - make QiraStrings rebuild
            // its resolved-alias cache and Qira Resources binding for
            // the NEW locale before anything queries it.
            QiraStrings.getInstance().onLocaleMayHaveChanged();
            QiraStrings.getInstance().enableRuntimeResourceScan();
        } catch (Throwable t) {
            logger.info("QiraStrings bootstrap failed (continuing): " + t.getMessage());
        }

        // Announce whether UI dumping is active for this run. This makes it
        // trivial to tell - from the logs alone - whether qira.dumpUi was
        // actually received by the test process (there have been cases where
        // the -e flag on `am instrument` appeared to be swallowed and the
        // feature was silently disabled).
        logger.info("QiraConfig: shouldDumpUi=" + mConfig.shouldDumpUi()
                + " (pass '-e qira.dumpUi true' on am instrument to enable)");
        logger.info("QiraCapture: Avik screenshot string-link extraction enabled"
                + " (includeText=" + mConfig.shouldIncludeScreenshotText()
                + ", includeDescription=" + mConfig.shouldIncludeScreenshotDescription()
                + "). Override with -e "
                + QiraConfig.ARG_INCLUDE_SCREENSHOT_TEXT + " <true|false> and -e "
                + QiraConfig.ARG_INCLUDE_SCREENSHOT_DESCRIPTION + " <true|false>.");
    }

    @After
    public void tearDown() throws Exception {
        if (mQiraApp != null) {
            mQiraApp.forceStop();
        }
        if (mUtils != null) {
            mUtils.pressBackKeySeveralTimes(3);
        }
    }

    protected abstract String getScreenPrefix();

    /**
     * Runs the capture flow for this script. Sub-classes override this with
     * their full sequence of taps / waits / screenshots; the concrete
     * {@code @Test} methods typically just delegate to this.
     *
     * <p>Declared on the base so that orchestration helpers like
     * {@link MotorolaQiraMasterCaptureTest} can invoke each child's flow through
     * the base reference without casting to each concrete type.
     */
    public abstract void captureScreens() throws Exception;

    /**
     * Copies this script's already-initialized instrumentation state
     * ({@link #mDevice}, {@link #mUtils}, {@link #mConfig},
     * {@link #mQiraApp}) into a freshly-instantiated peer capture
     * script. Lets one capture script invoke another in-process without
     * triggering the peer's own {@code @Before} setUp (which would
     * re-{@code clearApp()} / re-{@code forceStop()} the Qira app and
     * undo the state built up by the current run).
     *
     * <p>Used by {@link MotorolaQiraMasterCapture#captureScreens()} to
     * iterate the full pipeline, and by
     * {@link MotorolaQiraHomeCapture#testMain()} when it self-promotes
     * to the master pipeline because the workbench Run config invoked
     * the Home test class.
     */
    protected final void adoptChild(BaseQiraCaptureScript child) {
        child.mDevice = mDevice;
        child.mUtils = mUtils;
        child.mConfig = mConfig;
        child.mQiraApp = mQiraApp;
    }

    protected void takeScreenshot(String suffix) throws Exception {
        String screenName = String.format(Locale.US, "%s_%s", getScreenPrefix(), suffix);
        // Universal anti-jitter guard: hold the screenshot until the
        // accessibility tree has been quiet for ~500ms (UiAutomator's
        // waitForIdle returns immediately when no events fire). This makes
        // every per-screen capture a "stable frame" capture and eliminates
        // the entire class of mid-transition / half-rendered screenshots.
        // We bound the wait at 1000ms so a permanently-busy surface (e.g.
        // an animated progress indicator that never goes idle) cannot
        // stall a capture indefinitely; the screenshot still fires after
        // the bound elapses.
        try {
            mDevice.waitForIdle(1000L);
        } catch (Throwable ignored) {
            // waitForIdle is advisory; never let an idle-wait failure
            // prevent the screenshot from being taken.
        }
        // Keep Avik string extraction enabled so Image Strings / SLAP links
        // are emitted for every locale (including en-XM pseudo-locale runs).
        //
        // IMPORTANT: default to text-only extraction
        // (includeText=true, includeDescription=false). On Compose-heavy Qira
        // surfaces, includeDescription=true often produces one giant
        // whole-screen string-link node instead of per-text/per-field/per-
        // button links. The behavior is configurable via QiraConfig args.
        //
        // QiraUiDumper below still writes the authoritative XML/text inventory
        // for selector triage; both outputs are needed.
        Throwable screenshotError = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                boolean includeText = mConfig == null || mConfig.shouldIncludeScreenshotText();
                boolean includeDescription = mConfig != null
                        && mConfig.shouldIncludeScreenshotDescription();
                avikHandler.takeScreenshot(screenName, includeText, includeDescription);
                screenshotError = null;
                break;
            } catch (Throwable t) {
                screenshotError = t;
                logger.info("takeScreenshot failed for " + screenName
                        + " (attempt " + attempt + "/2): " + t.getMessage());
                try {
                    mUtils.sleep(400L);
                } catch (Throwable ignored) {
                }
            }
        }
        if (screenshotError != null) {
            // Never abort an entire locale run because one image open/write
            // call failed in the host screenshot pipeline. We still dump the
            // UiAutomator hierarchy below so triage has per-surface evidence.
            logger.info("takeScreenshot continuing without image for " + screenName
                    + " after retries: " + screenshotError.getMessage());
        }
        if (mConfig != null && mConfig.shouldDumpUi()) {
            // Capture a UiAutomator XML + resource-id inventory next to every
            // screenshot. These files are what we use to refactor page
            // objects to stable, locale-independent By.res(...) selectors.
            try {
                QiraUiDumper.dump(mDevice, mConfig, screenName);
            } catch (Throwable t) {
                logger.info("QiraUiDumper.dump failed for " + screenName
                        + " (continuing): " + t.getMessage());
            }
        }
    }

    /**
     * Hard reset to a known-good Qira top-level surface before a sub-flow's
     * first screenshot. Bug class this guards against:
     *
     * <ul>
     *   <li>The previous sub-flow left the Focus Zone bubble bar's expanded
     *       chat dialog visible (the bubble bar floats above Qira and is
     *       NOT killed by {@code am force-stop com.lenovo.qira} on this
     *       device).</li>
     *   <li>The previous sub-flow left an EditText focused so the IME
     *       (system process, separate from Qira) is still on screen
     *       and obscures the lower half of any subsequent capture.</li>
     *   <li>The launcher / Lenovo SetupWizard rendered a confirmation
     *       dialog ("Sair da configuração?" / "Exit setup?") on top of
     *       the launcher, blocking the next Qira launch from reaching
     *       the home tile grid.</li>
     * </ul>
     *
     * <p>Fix: explicitly hide the IME, press HOME (so the launcher takes
     * over and any system overlay is dismissed), force-stop Qira (kills
     * the bubble bar service), wait briefly, then re-launch Qira and
     * advance through onboarding to the home tile grid. Verify a known
     * top-level Qira surface is actually visible before returning;
     * raise {@link IllegalStateException} otherwise so the sub-flow's
     * outer try/catch can mark it as failed instead of silently
     * screenshotting whatever leftover surface is on screen.
     */
    protected void ensureCleanQiraEntry(QiraOnboardingPage onboardingPage) throws Exception {
        // 1) Hide the IME if any EditText kept it pinned.
        hideIme();

        // 2) Press HOME so the launcher takes the foreground; this also
        //    dismisses any modal/system dialog that was on top of Qira.
        try {
            mDevice.pressHome();
            mUtils.sleep(600L);
        } catch (Throwable ignored) {
        }

        // 3) Kill Qira including the bubble bar service. Without this the
        //    floating overlay survives and subsequent screenshots include
        //    the bubble bar's expanded chat dialog from the previous flow.
        try {
            mQiraApp.forceStop();
            mUtils.sleep(800L);
        } catch (Throwable t) {
            logger.info("ensureCleanQiraEntry: forceStop failed (continuing): " + t.getMessage());
        }

        // 4) Re-launch Qira fresh. launchQiraApp() issues an explicit
        //    am start so we always come up on Qira's primary task.
        try {
            mDevice.pressHome();
            mUtils.sleep(400L);
        } catch (Throwable ignored) {
        }
        onboardingPage.launchQiraApp();

        // 5) Advance through onboarding (idempotent on already-onboarded
        //    accounts) so we land on the home tile grid.
        try {
            onboardingPage.advanceThroughOnboardingToHome(60000L);
        } catch (Throwable t) {
            logger.info("ensureCleanQiraEntry: advanceThroughOnboardingToHome failed (continuing): "
                    + t.getMessage());
        }

        // 6) Make sure the IME did not pop back up while Qira inflated
        //    its initial composer surface.
        hideIme();

        // 7) Final verification - the home feature grid OR a Focus Zone
        //    bubble bar surface MUST be visible. Both are valid Qira
        //    top-level surfaces from which sub-flows can navigate to
        //    their target tile.
        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        if (!onboardingPage.isFeatureGridVisible()
                && !focusZone.isBubbleBarVisible()
                && QiraV2InstrumentationDefaults.isOnboardingFallbackEnabled()) {
            try {
                logger.info("ensureCleanQiraEntry: legacy onboarding entry did not expose "
                        + "the Qira top-level surface; running qira_v2 stable "
                        + "resource-backed recovery.");
                QiraV2HomeOnboardingFlow.advanceToHomeWithoutCaptures(
                        mDevice,
                        mUtils,
                        mConfig,
                        logger,
                        150000L);
            } catch (Throwable t) {
                logger.info("ensureCleanQiraEntry: qira_v2 onboarding recovery failed "
                        + "(continuing to final verification): " + t.getMessage());
            }
        }
        if (!onboardingPage.isFeatureGridVisible()
                && !focusZone.isBubbleBarVisible()) {
            // One last best-effort: a few back presses might dismiss any
            // leftover modal that survived the HOME press.
            for (int i = 0; i < 4; i++) {
                if (onboardingPage.isFeatureGridVisible()
                        || focusZone.isBubbleBarVisible()) {
                    return;
                }
                try {
                    mDevice.pressBack();
                    mUtils.sleep(400L);
                } catch (Throwable ignored) {
                    break;
                }
            }
            if (!onboardingPage.isFeatureGridVisible()
                    && !focusZone.isBubbleBarVisible()) {
                throw new IllegalStateException(
                        "ensureCleanQiraEntry: Qira top-level surface not visible after relaunch");
            }
        }
    }

    /**
     * Best-effort IME hide. The IME is a separate system process so
     * {@code am force-stop com.lenovo.qira} does NOT close it. The
     * commands below cover every Android version we target:
     *
     * <ol>
     *   <li>{@code ime hide} (Android 14+ shell verb)</li>
     *   <li>{@code input keyevent KEYCODE_BACK} (collapses any visible
     *       IME on every Android version; on most devices it consumes
     *       the BACK only when the IME is the active receiver, so the
     *       call is a no-op when the IME is already hidden)</li>
     * </ol>
     */
    private void hideIme() {
        try {
            mDevice.executeShellCommand("ime hide");
        } catch (Throwable ignored) {
        }
        // Reading dumpsys is far cheaper than a guessing a back press, so
        // we only press BACK when we actually see "mInputShown=true".
        try {
            String dump = mDevice.executeShellCommand("dumpsys input_method");
            if (dump != null && dump.contains("mInputShown=true")) {
                mDevice.pressBack();
                mUtils.sleep(300L);
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * Brings Qira to a stable post-launch state that downstream capture
     * scripts can work from: either the home feature grid / Discover surface
     * or the Focus Zone bubble bar. This lets locale runs recover when Qira
     * unexpectedly re-enters onboarding instead of assuming the prior capture
     * already left the app on the expected screen.
     */
    protected void ensureQiraReadyForFeatureEntry(QiraOnboardingPage onboardingPage,
            long timeoutMs) throws Exception {
        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        long deadline = System.currentTimeMillis() + timeoutMs;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (onboardingPage.isFeatureGridVisible()
                    || focusZone.isBubbleBarVisible()) {
                return;
            }
            if (onboardingPage.advanceOnboardingOnce()) {
                mUtils.sleep(500L);
                continue;
            }
            String currentPkg = mDevice.getCurrentPackageName();
            if (!mConfig.getPackageName().equals(currentPkg)) {
                onboardingPage.launchQiraApp();
                mUtils.sleep(800L);
                continue;
            }
            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 5000L) {
                logger.info("Qira entry surface not ready yet; current package=" + currentPkg);
                lastDiagLog = now;
            }
            mUtils.sleep(350L);
        }
        throw new IllegalStateException(
                "Unable to reach a ready Qira entry surface (home grid or bubble bar)");
    }

    /**
     * Wakes the device, dismisses the keyguard, disables the lock screen,
     * forces the screen to stay on while plugged in (default on emulators
     * and on a USB-tethered phone), and freezes rotation to portrait. Every
     * step is best-effort and swallows failures; this method must never
     * abort the test setup.
     */
    private void prepareDeviceForCapture() {
        try {
            mDevice.executeShellCommand("input keyevent KEYCODE_WAKEUP");
        } catch (Throwable ignored) {
        }
        try {
            // Keep screen on while charging (all three: AC, USB, wireless).
            mDevice.executeShellCommand("svc power stayon true");
        } catch (Throwable ignored) {
        }
        try {
            // Stretch the screen-off timeout to 30 minutes as a belt-and-
            // braces fallback for devices where stayon doesn't cover the
            // battery path.
            mDevice.executeShellCommand("settings put system screen_off_timeout 1800000");
        } catch (Throwable ignored) {
        }
        try {
            // Disable the lock screen so we don't have to dismiss keyguard
            // between sub-flows.
            mDevice.executeShellCommand("locksettings set-disabled true");
        } catch (Throwable ignored) {
        }
        try {
            mDevice.executeShellCommand("settings put system accelerometer_rotation 0");
            mDevice.executeShellCommand("settings put system user_rotation 0");
        } catch (Throwable ignored) {
        }

        // Make sure we enter the run with Qira-capable conditions. Reuse the
        // hardened unlock logic already implemented for onboarding.
        try {
            new QiraOnboardingPage(mDevice, mConfig).ensureDeviceUnlocked();
        } catch (Throwable t) {
            logger.info("ensureDeviceUnlocked during setUp failed (continuing): " + t.getMessage());
        }
    }

    /**
     * Strict pre-flight verifier for the device system locale.
     *
     * <p>Earlier revisions of this method tried to switch the device
     * system locale from inside the test process. That approach is
     * fundamentally unreliable on this device: every avenue we have
     * from instrumentation ({@code setprop}, {@code settings put},
     * {@code IActivityManager.updatePersistentConfiguration}) either
     * does not apply to already-running Activities (launcher / SystemUI)
     * or is denied with {@code SecurityException: requires
     * WRITE_SETTINGS}. The only path that actually swings the entire
     * device system locale on a Motorola userdebug build is a Zygote
     * soft-restart ({@code adb shell stop} + {@code adb shell start})
     * driven from outside the test process.
     *
     * <p>The test process applies the requested locale where it can:
     * the per-app override on the Qira APK ({@code cmd locale
     * set-app-locales}), and the host instrumentation process default
     * locale (so {@link QiraStrings#resolve(String)} expands anchors
     * using the matching catalog row).
     *
     * <p>The DEVICE SYSTEM LOCALE switch is performed by the workbench
     * Avik plugin's pre-flight {@code com.motorola.iqa.frevosetup
     * .code.scripts.DeviceLocale} step BEFORE this test process
     * starts. When invoking through the workbench Avik plugin Run
     * config, that pre-flight path is the one that sets {@code persist.sys.locale}
     * via {@code IActivityManager.updatePersistentConfiguration(...)}
     * (which requires {@code WRITE_SETTINGS} - granted to Frevo Setup
     * via {@code .scratch\preflight-workbench.bat}).
     *
     * <p>Steps performed here, in order:
     * <ol>
     *   <li>Read {@code persist.sys.locale}, {@code settings get system
     *       system_locales}, and {@link Locale#getDefault()}.</li>
     *   <li>Apply the requested locale to Qira via
     *       {@code cmd locale set-app-locales} - idempotent / harmless.</li>
     *   <li>Apply the requested locale to the host instrumentation
     *       process so {@link QiraStrings#resolve(String)} expands
     *       anchors using the matching catalog row.</li>
     *   <li>Log a {@code [locale-check]} line summarising whether the
     *       device system locale matches the requested tag. A mismatch
     *       is logged as a {@code WARNING} but does NOT abort the run -
     *       the test continues using whatever device locale was
     *       actually applied (workbench dialog selection, Frevo, or
     *       any out-of-process driver). The instrumentation-process
     *       locale flip in step 3 keeps {@link QiraStrings} aligned
     *       with the requested catalog so anchor resolution still
     *       works even when the device's system locale lags.</li>
     * </ol>
     *
     * <p>No-op when {@code qira.locale} is empty (logs the device's
     * current locale via a {@code [locale-check]} line so the absence
     * of a switch is still auditable).
     *
     * <p><b>Pseudo-locales (e.g. {@code en-XM}) are intentionally NOT
     * auto-folded to {@code en-US} here.</b> {@code en-XM} carries the
     * SLAP string-link markers (Unicode bidi isolates wrapping each
     * resource value) that the screen-capture pipeline needs to audit
     * which {@code R.string} backed each rendered label.
     */
    private void applyRequestedLocale() {
        if (mConfig == null || !mConfig.hasLocale()) {
            String deviceTag = Locale.getDefault().toLanguageTag();
            String persist = readShell("getprop persist.sys.locale");
            String systemLocales = readShell("settings get system system_locales");
            logger.info("QiraLocale: no qira.locale argument; honouring device locale.");
            logger.info("[locale-check] requested=<none> persist.sys.locale=" + persist
                    + " system_locales=" + systemLocales
                    + " Locale.getDefault()=" + deviceTag);
            return;
        }
        String tag = mConfig.getLocale();
        String startPersist = readShell("getprop persist.sys.locale");
        String startSystemLocales = readShell("settings get system system_locales");
        String startDefault = Locale.getDefault().toLanguageTag();
        logger.info("[locale-check] requested=" + tag
                + " (start) persist.sys.locale=" + startPersist
                + " system_locales=" + startSystemLocales
                + " Locale.getDefault()=" + startDefault);

        // 1) Per-app locale override on the Qira APK (Android 13+).
        //    Idempotent and safe to apply every run; it only affects
        //    Qira's own resources, never the rest of the device.
        try {
            String cmd = String.format(Locale.US,
                    "cmd locale set-app-locales %s --user current --locales %s",
                    mConfig.getPackageName(), tag);
            String out = mDevice.executeShellCommand(cmd);
            logger.info("QiraLocale: set-app-locales result: "
                    + (out == null ? "" : out.trim()));
        } catch (Throwable t) {
            logger.info("QiraLocale: cmd locale set-app-locales failed (continuing): "
                    + t.getMessage());
        }

        // 2) Instrumentation process locale - so QiraStrings catalog
        //    expansion lines up with the device locale.
        try {
            Locale requested = bcp47ToLocale(tag);
            Locale.setDefault(requested);
            android.content.res.Resources resources = InstrumentationRegistry
                    .getInstrumentation().getTargetContext().getResources();
            android.content.res.Configuration config =
                    new android.content.res.Configuration(resources.getConfiguration());
            config.setLocale(requested);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } catch (Throwable t) {
            logger.info("QiraLocale: instrumentation-process locale flip failed (continuing): "
                    + t.getMessage());
        }

        // 3) Soft verification. The device system locale switch is
        //    performed by the workbench plugin's pre-flight DeviceLocale
        //    step (or by the .scratch\run-locale.bat driver when running
        //    out-of-band). If for any reason the device system locale
        //    does NOT match the requested tag, we log a WARNING and
        //    continue with the in-process locale already applied above:
        //    Qira itself runs with the requested locale via the per-app
        //    override, and the host instrumentation process has its
        //    default Locale flipped so QiraStrings catalog expansion
        //    still resolves anchors correctly. We deliberately do NOT
        //    throw here, because the workbench Avik plugin's "Execute
        //    Avik scripts" dialog drives the device locale switch out
        //    of the test process and we want the script to honour
        //    whatever locale the dialog ended up applying - even if
        //    persist.sys.locale lags behind the requested tag for a
        //    moment after the Frevo DeviceLocale step.
        String endPersist = readShell("getprop persist.sys.locale");
        String endSystemLocales = readShell("settings get system system_locales");
        String endDefault = Locale.getDefault().toLanguageTag();
        boolean systemMatch = localeTagMatches(tag, endPersist);
        boolean defaultMatch = localeTagMatches(tag, endDefault);
        logger.info("[locale-check] requested=" + tag
                + " (end) persist.sys.locale=" + endPersist
                + " system_locales=" + endSystemLocales
                + " Locale.getDefault()=" + endDefault
                + " systemMatch=" + systemMatch
                + " defaultMatch=" + defaultMatch);

        if (!systemMatch) {
            logger.info("[locale-check] WARNING: device persist.sys.locale='"
                    + endPersist + "' does NOT match requested qira.locale='"
                    + tag + "'. Continuing with in-process locale flip"
                    + " (Qira app + instrumentation default Locale already"
                    + " set above). If you need a strict device-wide system"
                    + " locale switch, ensure the workbench plugin's"
                    + " DeviceLocale pre-flight is granted WRITE_SETTINGS"
                    + " via .\\.scratch\\preflight-workbench.bat, or run"
                    + " .\\.scratch\\run-locale.bat " + tag
                    + " out-of-band.");
            return;
        }
        logger.info("[locale-check] requested=" + tag + " applied successfully");
    }

    private boolean localeTagMatches(String expected, String actual) {
        return matchesLocaleTag(expected, actual);
    }

    private static boolean matchesLocaleTag(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        String e = expected.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        String a = actual.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        if (e.isEmpty() || a.isEmpty()) {
            return false;
        }
        return e.equals(a);
    }

    private String readShell(String cmd) {
        try {
            String out = mDevice.executeShellCommand(cmd);
            return out == null ? "" : out.trim();
        } catch (Throwable t) {
            return "";
        }
    }

    private void runShell(String cmd) {
        try {
            mDevice.executeShellCommand(cmd);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Parses a BCP-47 tag (e.g. {@code pt-BR}, {@code zh-Hant-TW}) into a
     * {@link Locale}. Falls back to {@link Locale#forLanguageTag(String)}
     * on API levels that expose it (21+), which covers every device we
     * target. The try/catch path handles grossly malformed tags by
     * returning the default locale so we never crash setUp.
     */
    private static Locale bcp47ToLocale(String tag) {
        try {
            Locale parsed = Locale.forLanguageTag(tag);
            if (parsed == null || parsed.getLanguage().isEmpty()) {
                return Locale.getDefault();
            }
            return parsed;
        } catch (Throwable t) {
            return Locale.getDefault();
        }
    }

    /**
     * Fires {@code pm grant} for every runtime permission the onboarding
     * flow normally asks the user to accept. Each grant is independent; a
     * failure on one (because Qira does not declare it, or because the
     * device hardened the permission) must not block the rest.
     *
     * <p>Location in particular is an Android 12+ "Precise/Approximate"
     * radio prompt that the current page object had trouble dismissing on
     * some devices; pre-granting it suppresses the prompt entirely so the
     * onboarding flow can proceed.
     */
    private void preGrantRuntimePermissions() {
        String[] perms = new String[] {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.RECORD_AUDIO",
                "android.permission.CAMERA",
                "android.permission.READ_CONTACTS",
                "android.permission.READ_CALENDAR",
                "android.permission.WRITE_CALENDAR",
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.READ_MEDIA_VIDEO",
                "android.permission.READ_MEDIA_AUDIO",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.BLUETOOTH_CONNECT",
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.NEARBY_WIFI_DEVICES"
        };
        for (String perm : perms) {
            try {
                mDevice.executeShellCommand(String.format(Locale.US,
                        "pm grant %s %s",
                        mConfig.getPackageName(),
                        perm));
            } catch (Throwable ignored) {
                // Permission may not be declared, or may already be granted.
            }
        }
    }

    protected QiraHomePage launchToHome(boolean captureLaunchPrompts) throws Exception {
        QiraHomePage homePage = mQiraApp.launch().waitForLaunchSurface();
        boolean handledLanguageSheet = false;
        boolean handledSignInDialog = false;
        boolean handledTourDialog = false;

        while (true) {
            if (homePage.isLanguageSheetVisible()) {
                if (handledLanguageSheet) {
                    throw new IllegalStateException(
                            "Motorola Qira launch did not progress past the language picker");
                }
                if (captureLaunchPrompts) {
                    takeScreenshot("Launch_Language");
                }
                handledLanguageSheet = true;
                homePage.dismissLanguageSheet();
                homePage.waitForLaunchSurface();
                continue;
            }

            if (homePage.isSignInDialogVisible()) {
                if (handledSignInDialog) {
                    throw new IllegalStateException(
                            "Motorola Qira launch did not progress past the sign-in dialog");
                }
                if (captureLaunchPrompts) {
                    takeScreenshot("Launch_SignIn");
                }
                handledSignInDialog = true;
                homePage.continueSignInIfPresent();
                homePage.waitForLaunchSurface();
                continue;
            }

            if (homePage.isTourDialogVisible()) {
                if (handledTourDialog) {
                    throw new IllegalStateException(
                            "Motorola Qira launch did not progress past onboarding");
                }
                if (captureLaunchPrompts) {
                    takeScreenshot("Launch_Tour");
                }
                handledTourDialog = true;
                homePage.dismissTourIfPresent();
                homePage.waitForLaunchSurface();
                continue;
            }

            return homePage.waitForLoaded();
        }
    }
}
