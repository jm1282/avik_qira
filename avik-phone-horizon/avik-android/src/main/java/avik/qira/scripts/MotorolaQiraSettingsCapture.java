package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.pages.QiraDrawerPage;
import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraOnboardingPage;
import avik.qira.pages.QiraSettingsPage;
import avik.qira.utils.QiraUiDumper;

/**
 * Captures the Motorola Qira "Settings" surface reached from the top-left
 * navigation drawer. The script must run <em>after</em>
 * {@link MotorolaQiraHomeCapture} so the app is already signed in and past the
 * primary onboarding; app data is never cleared here (see
 * {@link BaseQiraCaptureScript#setUp()} which respects
 * {@code QiraConfig.shouldClearData()}).
 *
 * <p>The flow:
 * <ol>
 *     <li>Launch Qira and wait for the Focus Zone bubble bar.</li>
 *     <li>Tap the Focus Zone App Icon bubble to return to the Qira home grid.</li>
 *     <li>Tap the top-left "Menu" icon (hamburger) to open the navigation
 *         drawer and capture it.</li>
 *     <li>Tap "Settings" to reach the master-detail Settings surface and
 *         capture the default view (Account detail shown first).</li>
 *     <li>Tap each left-hand option in sequence and capture its right-pane
 *         detail:
 *         <ul>
 *             <li><b>Personal</b>: Account, Devices, Smart Connect.</li>
 *             <li><b>General</b>: Language, Launch Options, Voice,
 *                 Lock-Screen Display.</li>
 *             <li><b>Data Control</b>: Sync Data.</li>
 *             <li><b>Personalization</b>: Personalized Answers, Catch Me Up,
 *                 Connectors.</li>
 *             <li><b>About</b>: About, Support Page, Legal Notices.</li>
 *             <li><b>Feedback</b>: Feedback.</li>
 *         </ul>
 *     </li>
 * </ol>
 *
 * <p>UI matching is primarily text / content-description based (see
 * {@link QiraSettingsPage}), with a left-pane index fallback for locales
 * whose labels drift from known anchors. No destructive actions (Sign Out,
 * Delete, Disconnect, …) are performed.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraSettingsCapture extends BaseQiraCaptureScript {

    private static final long DRAWER_TIMEOUT_MS = 8000L;
    // Trimmed from 1200 -> 800ms: takeScreenshot() now prepends a
    // waitForIdle(1000ms) universally, so the previous fixed cushion is
    // mostly redundant. 800ms still covers the worst-case Compose
    // settle observed when navigating between Settings detail panes.
    private static final long DETAIL_SETTLE_MS = 800L;
    // Trimmed from 800 -> 500ms for the same reason. Each settings
    // option capture saved ~300ms; across the 15 options that totals
    // ~4.5s per locale.
    private static final long SETTLE_MS = 500L;

    // Left-pane Settings option order, top-to-bottom. Matches
    // QiraSettingsPage.OPTION_LABELS.
    private static final int OPTION_INDEX_ACCOUNT = 0;
    private static final int OPTION_INDEX_DEVICES = 1;
    private static final int OPTION_INDEX_SMART_CONNECT = 2;
    private static final int OPTION_INDEX_LANGUAGE = 3;
    private static final int OPTION_INDEX_LAUNCH_OPTIONS = 4;
    private static final int OPTION_INDEX_VOICE = 5;
    private static final int OPTION_INDEX_LOCK_SCREEN_DISPLAY = 6;
    private static final int OPTION_INDEX_SYNC_DATA = 7;
    private static final int OPTION_INDEX_PERSONALIZED_ANSWERS = 8;
    private static final int OPTION_INDEX_CATCH_ME_UP = 9;
    private static final int OPTION_INDEX_CONNECTORS = 10;
    private static final int OPTION_INDEX_ABOUT = 11;
    private static final int OPTION_INDEX_SUPPORT_PAGE = 12;
    private static final int OPTION_INDEX_LEGAL_NOTICES = 13;
    private static final int OPTION_INDEX_FEEDBACK = 14;

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraSettings";
    }

    protected QiraSettingsPage createSettingsPage(
            QiraSettingsPage openedPage) throws Exception {
        return openedPage;
    }

    protected boolean requireVerifiedCanonicalSurfaces() {
        return false;
    }

    public void captureScreens() throws Exception {
        logger.info("Launching Motorola Qira without clearing data for the Settings capture.");

        QiraOnboardingPage onboardingPage = new QiraOnboardingPage(mDevice, mConfig);
        onboardingPage.ensureDeviceUnlocked();
        onboardingPage.disableAutoRotate();

        // Hard reset: kill leftover Focus Zone bubble bar + dismiss the
        // IME from the previous sub-flow before opening the drawer.
        // See BaseQiraCaptureScript.ensureCleanQiraEntry for the rationale.
        ensureCleanQiraEntry(onboardingPage);

        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        QiraHomePage home = new QiraHomePage(mDevice, mConfig);
        navigateToQiraHome(onboardingPage, focusZone, home);
        // Home_TileGrid was added after the en-XM canonical run; the
        // strict baseline rule says we must NOT introduce screen names
        // that en-XM does not have. We navigate to the home tile grid
        // for the subsequent drawer-open step but emit no screenshot
        // under MotorolaQiraSettings_Home_TileGrid.

        QiraDrawerPage drawer = openDrawer(onboardingPage, focusZone, home);
        takeScreenshot("Drawer_Menu");

        QiraSettingsPage settings = drawer.openSettings();
        if (settings == null) {
            throw new IllegalStateException(
                    "Unable to open the Qira Settings surface from the drawer");
        }
        settings = createSettingsPage(settings);
        mUtils.sleep(DETAIL_SETTLE_MS);
        takeScreenshot("Settings_Default");

        // Force a single scrollToTop() before the first captureOption()
        // call so the master pane is at a known anchor; subsequent
        // captures rely on the previous tap leaving the next-row
        // selection naturally below the visible window.
        mNeedsTopScrollOnNextOption = true;

        // Personal
        captureAccount(settings);
        captureDevices(settings);
        captureSmartConnect(settings);

        // General
        captureLanguage(settings);
        captureLaunchOptions(settings);
        captureVoice(settings);
        captureLockScreenDisplay(settings);

        // Data Control
        captureSyncData(settings);

        // Personalization
        capturePersonalizedAnswers(settings);
        captureCatchMeUp(settings);
        captureConnectors(settings);

        // About
        captureAbout(settings);
        captureSupportPage(settings);
        captureLegalNotices(settings);

        // Feedback
        captureFeedback(settings);
    }

    // ---------------------------------------------------------------------
    // Personal section
    // ---------------------------------------------------------------------

    private void captureAccount(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_ACCOUNT,
                QiraSettingsPage.ACCOUNT_OPTION_LABELS, "Account",
                "Account", "Manage Lenovo Id", "Sign Out");
    }

    private void captureDevices(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_DEVICES,
                QiraSettingsPage.DEVICES_OPTION_LABELS, "Devices",
                "Devices", "Refresh Devices");
    }

    private void captureSmartConnect(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_SMART_CONNECT,
                QiraSettingsPage.SMART_CONNECT_OPTION_LABELS, "SmartConnect",
                "Smart Connect", "Connected Devices", "Manage Devices", "Cross-Device Actions");
    }

    // ---------------------------------------------------------------------
    // General section
    // ---------------------------------------------------------------------

    private void captureLanguage(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_LANGUAGE,
                QiraSettingsPage.LANGUAGE_OPTION_LABELS, "Language",
                "Language");
    }

    private void captureLaunchOptions(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_LAUNCH_OPTIONS,
                QiraSettingsPage.LAUNCH_OPTIONS_OPTION_LABELS, "LaunchOptions",
                "Launch Options", "Floating Bubble", "AI Key", "Approach");
    }

    private void captureVoice(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_VOICE,
                QiraSettingsPage.VOICE_OPTION_LABELS, "Voice",
                "Voice", "Voice Control", "TalkBack", "Adaptive Playback");
    }

    private void captureLockScreenDisplay(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_LOCK_SCREEN_DISPLAY,
                QiraSettingsPage.LOCK_SCREEN_DISPLAY_OPTION_LABELS,
                "LockScreenDisplay",
                "Lock-Screen Display");
    }

    // ---------------------------------------------------------------------
    // Data Control section
    // ---------------------------------------------------------------------

    private void captureSyncData(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_SYNC_DATA,
                QiraSettingsPage.SYNC_DATA_OPTION_LABELS, "SyncData",
                "Sync Data");
    }

    // ---------------------------------------------------------------------
    // Personalization section
    // ---------------------------------------------------------------------

    private void capturePersonalizedAnswers(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_PERSONALIZED_ANSWERS,
                QiraSettingsPage.PERSONALIZED_ANSWERS_OPTION_LABELS,
                "PersonalizedAnswers",
                "Personalized Answers");
    }

    private void captureCatchMeUp(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_CATCH_ME_UP,
                QiraSettingsPage.CATCH_ME_UP_OPTION_LABELS, "CatchMeUp",
                "Catch Me Up", "Update me", "Clear summarized notifications");
    }

    private void captureConnectors(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_CONNECTORS,
                QiraSettingsPage.CONNECTORS_OPTION_LABELS, "Connectors",
                "Connectors");
    }

    // ---------------------------------------------------------------------
    // About section
    // ---------------------------------------------------------------------

    private void captureAbout(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_ABOUT,
                QiraSettingsPage.ABOUT_OPTION_LABELS, "About",
                "About", "Info");
    }

    private void captureSupportPage(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_SUPPORT_PAGE,
                QiraSettingsPage.SUPPORT_PAGE_OPTION_LABELS, "SupportPage",
                "Support Page", "Support page", "Support-Seite",
                "Get additional support", "Visit Support Page");
    }

    private void captureLegalNotices(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_LEGAL_NOTICES,
                QiraSettingsPage.LEGAL_NOTICES_OPTION_LABELS, "LegalNotices",
                "Legal Notices");
    }

    // ---------------------------------------------------------------------
    // Feedback section
    // ---------------------------------------------------------------------

    private void captureFeedback(QiraSettingsPage settings) throws Exception {
        captureOption(settings, OPTION_INDEX_FEEDBACK,
                QiraSettingsPage.FEEDBACK_OPTION_LABELS, "Feedback",
                "Feedback");
    }

    // ---------------------------------------------------------------------
    // Shared helpers
    // ---------------------------------------------------------------------

    /**
     * Taps the given left-pane option, waits for the Compose master-detail
     * swap to settle and captures the right-pane detail. If the option label
     * cannot be found the step is logged and skipped so a single missing
     * entry on a new Qira build does not abort the rest of the capture.
     *
     * @param settings       the Settings page object
     * @param optionIndex    0-based row index in the left master list
     *                       (locale-agnostic fallback path)
     * @param optionLabels   known aliases for the option as it appears in the
     *                       left-hand master list
     * @param screenSuffix   short token appended to the screenshot name
     *                       (e.g. {@code "SmartConnect"}); the final name
     *                       is {@code "MotorolaQiraSettings_Settings_&lt;suffix&gt;"}
     * @param detailAnchors  localized-safe anchors expected on the right-hand
     *                       detail pane once the option has been opened
     */
    private void captureOption(QiraSettingsPage settings, int optionIndex, String[] optionLabels,
            String screenSuffix, String... detailAnchors) throws Exception {
        String optionLabel = optionLabels != null && optionLabels.length > 0
                ? optionLabels[0]
                : screenSuffix;
        logger.info("Capturing Settings option: " + optionLabel);

        // BEFORE: every option called settings.scrollToTop() unconditionally,
        // which paid a full 4-8 swipe roundtrip per option (15 options =
        // ~30+ wasted swipes per locale). Captures iterate top -> bottom by
        // canonical OPTION_INDEX_*, so we only need to reset to the top
        // once before the FIRST option. Every subsequent option is just
        // below the previous one in DOM order, so the existing
        // selectOption() probe (which first checks the visible window
        // before falling back to scrollToTop) finds it in 0 or 1 forward
        // swipes.
        //
        // openOptionDetailByPosition() will still call scrollToTop() as a
        // fallback if the direct row probe misses, so missing-option
        // recovery still works for every locale that ever needed it.
        if (mNeedsTopScrollOnNextOption) {
            settings.scrollToTop();
            mNeedsTopScrollOnNextOption = false;
        }
        if (settings.openOptionDetailByPosition(optionIndex, optionLabels, detailAnchors) == null) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException("Settings option \"" + optionLabel
                        + "\" could not be opened by its stable resource selector.");
            }
            logger.info("Settings option \"" + optionLabel
                    + "\" was not found; skipping capture for " + screenSuffix);
            // Unconditionally dump the current UI so we can see what
            // surface Qira is actually showing when an option misses. This
            // bypasses the qira.dumpUi flag because the Settings master
            // pane is the area of the app we still need to harden the most.
            try {
                QiraUiDumper.dump(mDevice, mConfig,
                        getScreenPrefix() + "_Missing_" + screenSuffix);
            } catch (Throwable t) {
                logger.info("Diagnostic UI dump failed for " + screenSuffix
                        + " (continuing): " + t.getMessage());
            }
            // A miss usually means scroll position is now unknown - reset
            // before the next option so we do not chain misses.
            mNeedsTopScrollOnNextOption = true;
            takeScreenshot("Settings_" + screenSuffix);
            return;
        }
        // takeScreenshot already prepends a waitForIdle(1000ms) since the
        // anti-jitter pass, so the Compose master-detail swap is allowed to
        // settle before the snapshot. The DETAIL_SETTLE_MS sleep below is
        // kept (now trimmed to 800ms) only as a small belt-and-braces
        // cushion for builds where the right-pane recomposition fires a
        // brief follow-up animation right after the master swap.
        mUtils.sleep(DETAIL_SETTLE_MS);

        // Body-title alignment guard. Repeats once if the right pane does
        // not show the expected localized header. Catches the row-misalignment
        // class of bug observed on Spanish in early May 2026 where a tap
        // silently selected the previous option and every subsequent
        // capture was offset by one. UNKNOWN_LOCALE is treated as a no-op
        // so locales without a dictionary entry degrade gracefully —
        // row alignment trumps perfect content (existing pattern), so we
        // never abort on MISMATCH; we just log a warning and capture
        // whatever is on screen.
        verifyBodyAlignmentWithGentleRetry(settings, optionIndex, optionLabels, detailAnchors,
                screenSuffix);

        if ("CatchMeUp".equals(screenSuffix)
                && !settings.scrollCatchMeUpDetailToTop()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Catch Me Up detail could not be positioned at its verified top.");
            }
            logger.info("Catch Me Up detail did not confirm its top position; "
                    + "capturing the best available state.");
        }

        takeScreenshot("Settings_" + screenSuffix);
        mUtils.sleep(SETTLE_MS);
        if (!settings.returnToSettingsListIfNeeded(4000L)) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException("Settings option \"" + optionLabel
                        + "\" did not return to the resource-verified Settings list.");
            }
            logger.info("Settings option \"" + optionLabel
                    + "\" did not return to the Settings list after capture.");
            mNeedsTopScrollOnNextOption = true;
        }
    }

    /**
     * Runs {@link QiraSettingsPage#verifyBodyTitleAlignment(String[])} and,
     * on {@link QiraSettingsPage.BodyTitleCheck#MISMATCH MISMATCH}, re-taps
     * the option once before re-checking. {@link
     * QiraSettingsPage.BodyTitleCheck#UNKNOWN_LOCALE} is a no-op so locales
     * without a dictionary entry never block the capture. A surviving
     * mismatch logs a warning and falls through to the screenshot, per the
     * "row alignment trumps perfect content" pattern used throughout this
     * script.
     */
    private void verifyBodyAlignmentWithGentleRetry(QiraSettingsPage settings, int optionIndex,
            String[] optionLabels, String[] detailAnchors, String screenSuffix) throws Exception {
        QiraSettingsPage.BodyTitleCheck check = settings.verifyBodyTitleAlignment(optionLabels);
        if (check != QiraSettingsPage.BodyTitleCheck.MISMATCH) {
            return;
        }
        logger.info("Body-title alignment MISMATCH for " + screenSuffix
                + "; right pane does not contain any localized form of "
                + (optionLabels != null && optionLabels.length > 0 ? optionLabels[0] : screenSuffix)
                + ". Retrying once.");

        if (settings.openOptionDetailByPosition(optionIndex, optionLabels, detailAnchors) == null) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException("Body-title retry for " + screenSuffix
                        + " could not reopen the stable Settings option.");
            }
            logger.info("Body-title retry for " + screenSuffix
                    + " could not re-open the option; capturing whatever is on screen"
                    + " (row alignment trumps perfect content).");
            return;
        }
        mUtils.sleep(DETAIL_SETTLE_MS);

        check = settings.verifyBodyTitleAlignment(optionLabels);
        if (check == QiraSettingsPage.BodyTitleCheck.MISMATCH) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException("Settings body-title alignment remained"
                        + " mismatched for " + screenSuffix + " after retry.");
            }
            logger.info("Body-title alignment STILL MISMATCH for " + screenSuffix
                    + " after one retry; capturing whatever is on screen"
                    + " (row alignment trumps perfect content).");
        }
    }

    /**
     * Tracks whether the next captureOption() call must reset the master
     * pane to the top of the list. True at suite start (before the very
     * first option) and after any option that ended up missing or failing,
     * so a misfire never chains. Captures iterate in canonical top -> bottom
     * order (OPTION_INDEX_ACCOUNT through OPTION_INDEX_FEEDBACK), so on a
     * happy path this flag is true exactly once per Settings sub-flow run.
     */
    private boolean mNeedsTopScrollOnNextOption = true;

    /**
     * Ensures the Qira home tile grid (Creator Zone / Knowledge / Chat
     * History / Help &amp; Support) is the active surface.
     *
     * <p>Earlier versions of this method blindly tapped the bubble bar's
     * App Icon and then issued three unconditional {@code pressBack()}
     * calls. The unconditional back-presses were the actual root cause of
     * the long-standing "Unable to open the Qira navigation drawer"
     * failure: once the App Icon tap had already returned us to the tile
     * grid, the next back-press dismissed Qira entirely and dropped us on
     * the launcher, so the hamburger was never on screen when
     * {@link #openDrawer} fired. We now mirror the
     * {@link MotorolaQiraKnowledgeCapture} pattern and short-circuit the
     * moment {@link QiraHomePage#isDiscoverVisible()} reports the tile
     * grid is up.
     */
    private void navigateToQiraHome(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home) throws Exception {
        for (int pass = 0; pass < 2; pass++) {
            if (isSettingsEntryGridVisible(onboardingPage, home)) {
                return;
            }
            for (int i = 0; i < 6; i++) {
                if (focusZone.isBubbleBarVisible()) {
                    try {
                        focusZone.tapFocusZoneAppIcon();
                    } catch (IllegalStateException ignored) {
                        // Fall back to back-navigation below.
                    }
                    mUtils.sleep(1200L);
                    if (isSettingsEntryGridVisible(onboardingPage, home)) {
                        return;
                    }
                }
                mDevice.pressBack();
                mUtils.sleep(800L);
                if (isSettingsEntryGridVisible(onboardingPage, home)) {
                    return;
                }
            }

            if (pass == 0) {
                mDevice.pressHome();
                mUtils.sleep(1000L);
                onboardingPage.launchQiraApp();
                onboardingPage.advanceThroughOnboardingToHome(60000L);
                if (isSettingsEntryGridVisible(onboardingPage, home)) {
                    return;
                }
                focusZone = new QiraFocusZonePage(mDevice, mConfig);
            }
        }

        mDevice.pressHome();
        mUtils.sleep(1000L);
        onboardingPage.launchQiraApp();
        onboardingPage.advanceThroughOnboardingToHome(60000L);
        try {
            onboardingPage.waitForFeatureGrid();
        } catch (IllegalStateException ignored) {
            // Final visibility check below decides whether we can proceed.
        }

        if (!isSettingsEntryGridVisible(onboardingPage, home)) {
            throw new IllegalStateException(
                    "Unable to reach the Qira home tile grid to start the Settings capture");
        }
    }

    private boolean isSettingsEntryGridVisible(QiraOnboardingPage onboardingPage,
            QiraHomePage home) {
        return home.isDiscoverVisible()
                || onboardingPage.isFeatureGridVisible();
    }

    /**
     * Opens the side drawer by tapping the top-left "Menu" icon (hamburger).
     * Relies on {@link QiraHomePage#openDrawer()} which matches the icon by
     * content-description ("Open navigation drawer" / "Menu") and falls back
     * to the nearest top-left clickable.
     *
     * <p>Before each tap attempt we re-confirm Qira is in the foreground.
     * If something has dropped us on the launcher (e.g. a stray back-press
     * during the previous capture), we re-launch Qira and walk back to the
     * tile grid so the hamburger is actually on screen when we click.
     */
    private QiraDrawerPage openDrawer(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone, QiraHomePage home) throws Exception {
        long deadline = System.currentTimeMillis() + DRAWER_TIMEOUT_MS;
        QiraDrawerPage drawer = null;
        while (drawer == null && System.currentTimeMillis() < deadline) {
            ensureQiraForegroundOnHome(onboardingPage, focusZone, home);
            try {
                drawer = home.openDrawer();
            } catch (IllegalStateException ignored) {
                // Drawer animation swallowed the tap; retry below.
            }
            if (drawer == null) {
                mUtils.sleep(400L);
            }
        }
        if (drawer == null) {
            // Snapshot the surface we are stuck on so the next iteration
            // has the actual UI hierarchy to work from instead of a
            // best-guess assumption about what's behind the failure.
            try {
                QiraUiDumper.dump(mDevice, mConfig,
                        getScreenPrefix() + "_DrawerOpen_Failed");
            } catch (Throwable ignored) {
            }
            throw new IllegalStateException("Unable to open the Qira navigation drawer");
        }
        return drawer;
    }

    /**
     * If Qira has somehow lost foreground, re-launch it and walk back to
     * the home tile grid; this guarantees the "Menu" hamburger
     * ({@code content-desc="Menu"} on the top-left ImageView) is present
     * when {@link QiraHomePage#openDrawer()} fires.
     */
    private void ensureQiraForegroundOnHome(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone, QiraHomePage home) throws Exception {
        String currentPkg = mDevice.getCurrentPackageName();
        boolean qiraForeground = mConfig.getPackageName().equals(currentPkg);
        if (qiraForeground && isSettingsEntryGridVisible(onboardingPage, home)) {
            return;
        }
        if (!qiraForeground) {
            logger.info("Qira is not foreground (current=" + currentPkg
                    + "); re-launching for drawer open.");
            onboardingPage.launchQiraApp();
            try {
                focusZone.waitForBubbleBar();
            } catch (Throwable ignored) {
                // Even if the bubble bar isn't recognised, keep going;
                // navigateToQiraHome will throw if the tile grid never
                // reaches us.
            }
        }
        navigateToQiraHome(onboardingPage, focusZone, home);
    }

    @Test
    public void testMain() {
        try {
            captureScreens();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}
