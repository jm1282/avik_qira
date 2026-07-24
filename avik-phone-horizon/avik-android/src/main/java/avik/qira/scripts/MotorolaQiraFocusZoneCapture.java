package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraOnboardingPage;

/**
 * Captures the first-run flow of the Motorola Qira "Focus Zone" floating
 * bubble bar. This script must run <em>after</em> {@link MotorolaQiraHomeCapture}
 * so the app is already signed in and past the main onboarding; app data is
 * never cleared by this script.
 *
 * <p>The flow walks each bubble entry point and captures the relevant screens:
 * <ul>
 *     <li>Chat: onboarding intro, Try it composer, thinking state, answer</li>
 *     <li>Live: onboarding intro, agreement, active Live with Camera</li>
 *     <li>Catch me up: onboarding intro, agreement, processing states, summary</li>
 *     <li>Pay Attention: onboarding intro, agreement, By proceeding dialog,
 *         recording bubble, Summary / Transcript / Audio Recording tabs</li>
 *     <li>Focus Zone hero carousel: every paginated slide on the Qira home
 *         surface reached by tapping the App Icon bubble</li>
 * </ul>
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraFocusZoneCapture extends BaseQiraCaptureScript {

    private static final long THINKING_TIMEOUT_MS = 8000L;
    private static final long ANSWER_TIMEOUT_MS = 20000L;
    private static final long CATCH_ME_UP_PROCESSING_TIMEOUT_MS = 60000L;
    private static final long PAY_ATTENTION_PROCESSING_TIMEOUT_MS = 60000L;
    private static final int FOCUS_ZONE_SLIDE_MAX_ATTEMPTS = 8;
    private static final long FOCUS_ZONE_SLIDE_SETTLE_MS = 600L;
    private static final int LIVE_EXIT_MAX_ATTEMPTS = 3;

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraFocusZone";
    }

    public void captureScreens() throws Exception {
        logger.info("Launching Motorola Qira without clearing data for the Focus Zone capture.");

        QiraOnboardingPage onboardingPage = new QiraOnboardingPage(mDevice, mConfig);
        onboardingPage.ensureDeviceUnlocked();
        onboardingPage.disableAutoRotate();

        // Hard reset: kill any leftover overlay / IME from the previous
        // sub-flow before driving the bubble bar. See
        // BaseQiraCaptureScript.ensureCleanQiraEntry for the rationale.
        ensureCleanQiraEntry(onboardingPage);
        ensureQiraReadyForFeatureEntry(onboardingPage, 60000L);

        // BubbleBar was a screenshot added after the en-XM canonical run;
        // the strict baseline rule says we must NOT introduce screen
        // names that en-XM does not have. We still wait for the bubble
        // bar so downstream sub-flows can find their entry tiles, but
        // no screenshot is emitted under MotorolaQiraFocusZone_BubbleBar.
        // The page object is captured into an effectively-final local so
        // the lambda-based runSubFlow() helpers below can reference it.
        QiraFocusZonePage builtFocusZone;
        try {
            builtFocusZone = new QiraFocusZonePage(mDevice, mConfig).waitForBubbleBar();
        } catch (Throwable t) {
            logger.info("Focus Zone bubble bar was not detected (continuing): "
                    + t.getMessage());
            builtFocusZone = new QiraFocusZonePage(mDevice, mConfig);
        }
        final QiraFocusZonePage focusZone = builtFocusZone;
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }

        // Each sub-flow is independent and all of them start by tapping a
        // bubble on the bar; isolating their failures means a bug in one
        // flow (e.g. Live's exit toggle) does not cost us the screenshots
        // from every later flow. Any failure is logged and rethrown only
        // after the remaining flows have had a chance to run.
        IllegalStateException firstFailure = null;
        firstFailure = runSubFlow("Chat", firstFailure,
                () -> captureChatFlow(focusZone));
        firstFailure = runSubFlow("Live", firstFailure,
                () -> captureLiveFlow(focusZone));
        firstFailure = runSubFlow("CatchMeUp", firstFailure,
                () -> captureCatchMeUpFlow(focusZone));
        firstFailure = runSubFlow("PayAttention", firstFailure,
                () -> capturePayAttentionFlow(focusZone));
        firstFailure = runSubFlow("FocusZoneSlides", firstFailure,
                () -> captureFocusZoneSlides(focusZone));

        if (firstFailure != null) {
            throw firstFailure;
        }
    }
    @FunctionalInterface
    private interface FocusZoneSubFlow {
        void run() throws Exception;
    }

    /**
     * Runs a Focus Zone sub-flow and keeps going even if it fails. Before
     * running a sub-flow that follows a previous failure we attempt to
     * restore the bubble bar (via Back presses); if we still cannot see
     * the bar, the sub-flow is skipped outright so it does not cascade.
     * The first failure observed is remembered and returned so the caller
     * can re-raise it at the end, preserving the original JUnit failure
     * signal while still collecting every screenshot we reasonably can.
     */
    private IllegalStateException runSubFlow(String label,
                                             IllegalStateException carriedFailure,
                                             FocusZoneSubFlow action) {
        if (carriedFailure != null && !recoverToBubbleBarOrRelaunch()) {
            logger.info("Skipping Focus Zone sub-flow '" + label
                    + "' because the bubble bar could not be recovered.");
            return carriedFailure;
        }
        if (carriedFailure == null && !recoverToBubbleBarOrRelaunch()) {
            IllegalStateException failure = new IllegalStateException(
                    "Unable to recover Focus Zone bubble bar before sub-flow '" + label + "'");
            logger.info(failure.getMessage());
            return failure;
        }
        try {
            action.run();
            return carriedFailure;
        } catch (Throwable t) {
            String message = (t.getMessage() != null && !t.getMessage().isEmpty())
                    ? t.getMessage()
                    : t.getClass().getSimpleName();
            logger.info("Focus Zone sub-flow '" + label + "' FAILED: " + message);
            if (t instanceof Exception) {
                try {
                    mUtils.printStackTraceOnLog((Exception) t);
                } catch (Throwable ignored) {
                }
            }
            if (carriedFailure != null) {
                return carriedFailure;
            }
            return (t instanceof IllegalStateException)
                    ? (IllegalStateException) t
                    : new IllegalStateException(label + ": " + message, t);
        }
    }

    /**
     * Polls the Live intro or agreement surface for up to {@code timeoutMs}.
     * Returns true as soon as either is visible. 200ms poll interval keeps
     * the early-exit cheap while still capping the worst-case wait.
     */
    private boolean waitForLiveIntroOrAgreement(QiraFocusZonePage focusZone, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (focusZone.isLiveIntroVisible() || focusZone.isLiveAgreementVisible()) {
                    return true;
                }
            } catch (Throwable ignored) {
                // Stale Compose nodes can throw transiently; retry next tick.
            }
            try {
                mUtils.sleep(200L);
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    /**
     * Polls just the Live agreement surface (after the intro Next has been
     * tapped). Returns true once the agreement card has rendered.
     */
    private boolean waitForLiveAgreement(QiraFocusZonePage focusZone, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (focusZone.isLiveAgreementVisible()) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
            try {
                mUtils.sleep(200L);
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private boolean tryRecoverToBubbleBar() {
        for (int i = 0; i < 3; i++) {
            try {
                QiraFocusZonePage fz = new QiraFocusZonePage(mDevice, mConfig);
                if (fz.isBubbleBarVisible()) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
            try {
                mDevice.pressBack();
                mUtils.sleep(500L);
            } catch (Throwable ignored) {
            }
        }
        try {
            return new QiraFocusZonePage(mDevice, mConfig).isBubbleBarVisible();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean recoverToBubbleBarOrRelaunch() {
        if (tryRecoverToBubbleBar()) {
            return true;
        }
        try {
            QiraOnboardingPage onboarding = new QiraOnboardingPage(mDevice, mConfig);
            onboarding.launchQiraApp();
            new QiraFocusZonePage(mDevice, mConfig).waitForBubbleBar();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void captureChatFlow(QiraFocusZonePage focusZone) throws Exception {
        focusZone.tapChatBubble();
        takeScreenshot("Chat_Onboarding");

        focusZone.tapChatTryIt();
        takeScreenshot("Chat_Composer");

        // Keep prompt ASCII-only and punctuation-light for reliable adb input
        // fallback on localized keyboards/IME layouts.
        focusZone.askChatQuestion("What is the weather in Bangalore?");
        if (focusZone.waitForChatThinking(THINKING_TIMEOUT_MS)) {
            takeScreenshot("Chat_Thinking");
        } else {
            logger.info("Chat thinking indicator did not appear within timeout; capturing anyway.");
            takeScreenshot("Chat_Thinking");
        }

        focusZone.waitForChatAnswer(ANSWER_TIMEOUT_MS);
        takeScreenshot("Chat_Answer");
    }

    private void captureLiveFlow(QiraFocusZonePage focusZone) throws Exception {
        focusZone.tapLiveBubble();
        // Poll up to 5s for the Live intro/agreement to render before
        // deciding to retry. waitForLiveIntro/Agreement use 200ms polls
        // internally and exit as soon as the surface is visible, so the
        // common (fast) path costs ~200ms instead of the previous 1.2s
        // fixed sleep that was used as a retry cushion.
        if (!waitForLiveIntroOrAgreement(focusZone, 5000L)) {
            logger.info("Live intro did not appear immediately; retrying Live tap without bubble-bar reset.");
            try {
                focusZone.tapLiveBubble();
            } catch (Throwable t) {
                logger.info("Live retry tap failed (continuing): " + t.getMessage());
            }
            waitForLiveIntroOrAgreement(focusZone, 3000L);
        }
        // One extra waitForIdle so the screenshot lands on a stable frame
        // (the intro card animates in over ~250ms on Compose; capturing
        // mid-animation produces a half-rendered frame). 1.5s upper bound
        // returns immediately on a quiet tree.
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Live_Onboarding");

        if (!focusZone.isLiveShareScreenPromptVisible()) {
            if (!focusZone.tapNextIfPresent()) {
                logger.info("Live intro Next action was not visible; continuing to agreement check.");
            } else {
                // Poll for the agreement card instead of sleeping a flat 1s -
                // it usually renders in <300ms once Next has fired.
                waitForLiveAgreement(focusZone, 3000L);
            }
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        if (focusZone.waitForLiveShareScreenPrompt(3000L)) {
            takeScreenshot("Live_Agreement");
            takeScreenshot("Live_ShareScreen");
            if (!focusZone.tapStartLiveIfPresent()) {
                logger.info("Live Share Screen Start Live action was not visible; continuing.");
            }
        } else {
            takeScreenshot("Live_Agreement");
            focusZone.tapIAgree();
        }
        focusZone.acceptPermissionPrompts(3, 6000L);
        if (focusZone.waitForLiveEnablePermissionPrompt(4000L)) {
            takeScreenshot("Live_EnablePermission");
            if (!focusZone.tapLiveEnablePermissionPrompt()) {
                logger.info("Live Enable permission action could not be tapped; continuing.");
            } else if (focusZone.waitForMotoActionCoreEnableScreen(8000L)) {
                takeScreenshot("Live_Enable");
                if (!focusZone.tapMotoActionCoreEnable()) {
                    logger.info("Moto Action Core Enable action could not be tapped; continuing.");
                }
            } else {
                logger.info("Moto Action Core Enable screen did not appear after "
                        + "tapping Enable permission.");
            }
            focusZone.acceptPermissionPrompts(3, 3000L);
        } else {
            logger.info("Live Enable permission card did not appear; continuing to active Live.");
        }
        // Poll up to 3s for Live to actually become active before the
        // screenshot. The previous fixed 1.5s sleep wasted time on locales
        // where activation completed in 500ms and was too short on
        // locales (zh-CN, ja-JP) where the agreement -> active transition
        // can take 2s+.
        long activeDeadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < activeDeadline && !focusZone.isLiveActive()) {
            mUtils.sleep(200L);
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Live_Active");

        // Live_Camera was a screenshot added after the en-XM canonical
        // run; the strict baseline rule says we must NOT introduce
        // screen names that en-XM does not have. We still tap the
        // camera bubble (when present) so Live mode reaches its full
        // active state and the subsequent exit-Live cleanup works
        // reliably, but no screenshot is emitted.
        if (focusZone.isLiveActive()) {
            try {
                focusZone.tapLiveCamera();
                focusZone.acceptPermissionPrompts(2, 6000L);
            } catch (Throwable t) {
                logger.info("Live camera tap failed (continuing): " + t.getMessage());
            }
            try {
                mDevice.waitForIdle(1500L);
            } catch (Throwable ignored) {
            }
        } else {
            logger.info("Live did not activate (no Camera bubble present); "
                    + "skipping camera-bubble interaction.");
        }

        // Live can take more than one tap to toggle off on some builds (the
        // camera view intercepts the first tap, then the bubble needs a
        // second one). We retry up to LIVE_EXIT_MAX_ATTEMPTS, with a Back
        // press as a final fallback, before giving up. A missed exit here
        // used to abort the entire Focus Zone capture and block every
        // remaining flow (Catch me up, Pay Attention, Focus Zone slides).
        //
        // If Live never became active in the first place (e.g. the
        // agreement card auto-dismissed without an explicit click on a
        // locale where our agreement labels miss), there is nothing to
        // exit. Just make sure the bubble bar is back so the next flow
        // can run; press Back / re-launch if needed.
        if (focusZone.isLiveActive()) {
            if (!tryExitLive(focusZone)) {
                logger.info("Unable to restore the bubble bar after exiting Live; "
                        + "pressing Back and attempting to continue.");
                try {
                    mDevice.pressBack();
                    mUtils.sleep(1000L);
                } catch (Throwable ignored) {
                }
                try {
                    focusZone.waitForBubbleBar();
                } catch (Throwable t) {
                    logger.info("Bubble bar still not visible after Live cleanup: "
                            + t.getMessage() + "; relying on next-flow recovery.");
                }
            }
        } else {
            logger.info("Live was not active at end of capture; "
                    + "ensuring bubble bar is reachable before next flow.");
            try {
                if (!focusZone.isBubbleBarVisible()) {
                    mDevice.pressBack();
                    mUtils.sleep(800L);
                }
                if (!focusZone.isBubbleBarVisible()) {
                    QiraOnboardingPage onboarding =
                            new QiraOnboardingPage(mDevice, mConfig);
                    onboarding.launchQiraApp();
                    mUtils.sleep(1000L);
                }
                focusZone.waitForBubbleBar();
            } catch (Throwable t) {
                logger.info("Bubble bar recovery after skipped Live failed: "
                        + t.getMessage() + "; next sub-flow will retry.");
            }
        }
    }

    private boolean tryExitLive(QiraFocusZonePage focusZone) {
        for (int attempt = 1; attempt <= LIVE_EXIT_MAX_ATTEMPTS; attempt++) {
            try {
                focusZone.exitLive();
                // waitForBubbleBar polls internally and exits early; skip
                // the previous flat 800ms cushion.
                try {
                    mDevice.waitForIdle(800L);
                } catch (Throwable ignored) {
                }
                focusZone.waitForBubbleBar();
                return true;
            } catch (Throwable t) {
                logger.info("Live exit attempt " + attempt + "/"
                        + LIVE_EXIT_MAX_ATTEMPTS + " failed: " + t.getMessage());
                try {
                    mUtils.sleep(400L);
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private void captureCatchMeUpFlow(QiraFocusZonePage focusZone) throws Exception {
        focusZone.tapCatchMeUpBubble();
        // Wait up to 5s for the Catch me up intro to render before deciding
        // whether the bubble tap "missed". A premature retry here re-taps
        // the bubble while the intro is still animating in, dismissing it
        // back to the bubble bar - the visible jitter/flicker the user
        // sees on slower locales.
        if (!focusZone.waitForCatchMeUpIntro(5000L)) {
            logger.info("Catch me up intro not visible after 5s; retrying once.");
            try {
                focusZone.waitForBubbleBar();
                focusZone.tapCatchMeUpBubble();
                focusZone.waitForCatchMeUpIntro(5000L);
            } catch (Throwable t) {
                logger.info("Catch me up retry tap failed (continuing): " + t.getMessage());
            }
        }
        // Hold for one stable frame before screenshotting (the intro
        // typically settles in ~300ms on Compose).
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("CatchMeUp_Onboarding");

        if (!focusZone.tapNextIfPresent()) {
            logger.info("Catch me up intro Next not available; continuing to agreement check.");
        }
        // Poll for the agreement card with early exit instead of a flat
        // 1s sleep. Worst case still bounded at 3s to cover slow locales.
        long agreementDeadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < agreementDeadline
                && !focusZone.isCatchMeUpAgreementVisible()) {
            mUtils.sleep(200L);
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("CatchMeUp_Agreement");

        try {
            focusZone.tapIAgree();
        } catch (Throwable t) {
            logger.info("Catch me up tapIAgree did not complete cleanly (continuing): "
                    + t.getMessage());
        }
        focusZone.acceptPermissionPrompts(3, 6000L);

        // Branch on whichever surface comes after the agreement card.
        // The Manage apps screen renders on first-run for some accounts;
        // builds with a previously-saved app selection skip it and go
        // straight to processing or summary.
        QiraFocusZonePage.CatchMeUpAfterAgreementSurface next;
        try {
            next = focusZone.waitForCatchMeUpAfterAgreement(8000L);
        } catch (Throwable t) {
            logger.info("Catch me up after-agreement detection failed (continuing): "
                    + t.getMessage());
            next = QiraFocusZonePage.CatchMeUpAfterAgreementSurface.OTHER;
        }
        logger.info("Catch me up after-agreement surface: " + next);

        // Row-alignment guarantee: both CatchMeUp_ManageApps AND
        // CatchMeUp_ManageApps_Stuck must always fire so the workbench
        // rows align across every locale. _ManageApps captures the
        // first-run app-selection surface; _ManageApps_Stuck is a
        // diagnostic best-effort capture of the surface immediately
        // after the primary CTA tap (regardless of whether the screen
        // actually advanced). For account states that bypass manage-apps
        // (SUMMARY/OTHER), both captures fall back to the current
        // surface so the row still aligns with en-XM.
        boolean cmuTapped = false;
        if (next == QiraFocusZonePage.CatchMeUpAfterAgreementSurface.MANAGE_APPS) {
            // Wait one stable frame so the screenshot lands cleanly,
            // capture, then tap the primary CTA. NEVER call
            // tapCatchMeUpNow without first idling - that's where the
            // previous flicker originated (the wide CTA bounds shift
            // by a few px during the manage-apps fade-in).
            try {
                mDevice.waitForIdle(1500L);
            } catch (Throwable ignored) {
            }
            takeScreenshot("CatchMeUp_ManageApps");

            try {
                if (focusZone.enableCatchMeUpAllOtherAppsToggleIfPresent()) {
                    logger.info("Catch me up: enabled All other apps toggle.");
                    mDevice.waitForIdle(800L);
                }
            } catch (Throwable t) {
                logger.info("Catch me up All other apps toggle failed (continuing): "
                        + t.getMessage());
            }
            try {
                cmuTapped = focusZone.tapCatchMeUpNowIfPresent();
            } catch (Throwable t) {
                logger.info("Catch me up tapCatchMeUpNowIfPresent failed (continuing): "
                        + t.getMessage());
            }
            if (!cmuTapped) {
                logger.info("Catch me up Manage Apps primary CTA not matched on first attempt; "
                        + "settling and retrying once.");
                try {
                    mDevice.waitForIdle(1500L);
                } catch (Throwable ignored) {
                }
                try {
                    cmuTapped = focusZone.tapCatchMeUpNowIfPresent();
                } catch (Throwable t) {
                    logger.info("Catch me up retry tapCatchMeUpNowIfPresent failed (continuing): "
                            + t.getMessage());
                }
            }

            // Wait for the manage-apps screen to advance into processing
            // / summary. Capture a diagnostic screenshot if it never
            // does, then continue (do not block the rest of the master
            // suite on this).
            long advanceDeadline = System.currentTimeMillis() + 8000L;
            while (System.currentTimeMillis() < advanceDeadline) {
                try {
                    if (focusZone.isCatchMeUpProcessing()
                            || focusZone.isCatchMeUpSummaryReady()) {
                        break;
                    }
                } catch (Throwable ignored) {
                }
                mUtils.sleep(250L);
            }
        } else {
            // Bypass-manage-apps account state. Emit _ManageApps as a
            // best-effort capture of the current after-agreement surface
            // so the workbench row aligns with en-XM.
            try {
                mDevice.waitForIdle(1500L);
            } catch (Throwable ignored) {
            }
            takeScreenshot("CatchMeUp_ManageApps");
            if (next == QiraFocusZonePage.CatchMeUpAfterAgreementSurface.SUMMARY) {
                logger.info("Catch me up jumped straight to summary "
                        + "(account state already past Manage apps); "
                        + "_ManageApps reflects best-available surface.");
            } else {
                logger.info("Catch me up after-agreement surface was OTHER; "
                        + "_ManageApps reflects best-available surface.");
            }
        }
        // _CatchMeUp_ManageApps_Stuck was added after the en-XM canonical
        // run as a diagnostic; the strict baseline rule says we must NOT
        // introduce screen names that en-XM does not have. The diagnostic
        // signal is preserved in the run log via the
        // logger.info("...did not advance after tap...") line above.
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }

        captureCatchMeUpProcessingStages(focusZone);

        boolean summaryReady = focusZone.waitForCatchMeUpSummary(CATCH_ME_UP_PROCESSING_TIMEOUT_MS);
        if (!summaryReady) {
            logger.info("Catch me up summary did not appear within timeout; capturing anyway.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("CatchMeUp_Summary");
    }

    private void captureCatchMeUpProcessingStages(QiraFocusZonePage focusZone) throws Exception {
        // The en-XM canonical baseline does NOT contain any per-stage
        // CatchMeUp_<Stage> screenshots (e.g. _Gathering_latest_notifications).
        // The strict baseline rule says we must NOT introduce screen
        // names that en-XM does not have, so we only POLL the
        // processing-stage detector to know when to stop blocking the
        // overall capture; we never emit a screenshot per stage.
        long deadline = System.currentTimeMillis() + CATCH_ME_UP_PROCESSING_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (!focusZone.isCatchMeUpProcessing()) {
                return;
            }
            mUtils.sleep(400L);
        }
    }

    private void capturePayAttentionFlow(QiraFocusZonePage focusZone) throws Exception {
        // Single Record-bubble tap + WAIT for the intro; do not blindly
        // re-tap the bubble (that flicker is what the user is seeing,
        // and it is what makes the intro vanish on slow locales because
        // the second tap dismisses the half-rendered intro). Up to two
        // additional tap-then-wait retries if the intro never lands.
        focusZone.tapRecordBubble();
        boolean introShown = focusZone.waitForPayAttentionIntro(8000L);
        for (int attempt = 0; attempt < 2 && !introShown; attempt++) {
            logger.info("Pay Attention intro not visible yet; retrying tap (attempt "
                    + (attempt + 2) + "/3).");
            try {
                focusZone.waitForBubbleBar();
                focusZone.tapRecordBubble();
            } catch (Throwable t) {
                logger.info("Pay Attention bubble tap failed: " + t.getMessage());
            }
            introShown = focusZone.waitForPayAttentionIntro(8000L);
        }
        // Account state survives pm clear for some Lenovo IDs - the
        // Record tap then jumps straight to Recording / Agreement /
        // ByProceeding. waitForPayAttentionIntro accepts those as "intro
        // skipped" and still returns true. If even the downstream
        // surfaces never appeared, fall back to a best-available capture.
        boolean introCardActuallyVisible = false;
        try {
            introCardActuallyVisible = focusZone.isPayAttentionIntroVisible();
        } catch (Throwable ignored) {
        }
        if (!introShown) {
            logger.info("Unable to open the Pay Attention intro card; "
                    + "capturing current state and continuing.");
        } else if (!introCardActuallyVisible) {
            logger.info("Pay Attention intro skipped (account state already past it); "
                    + "capturing the current downstream surface as PayAttention_Onboarding.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_Onboarding");

        // Use the non-throwing variant so a missing Next button (intro
        // skipped because the account already accepted by-proceeding on a
        // prior run) does not abort the entire PayAttention sub-flow.
        // tapNextIfPresent returns false when no candidate label is found
        // and the geometry fallback misses; in that case the agreement
        // card may already be on screen.
        if (!focusZone.tapNextIfPresent()) {
            logger.info("Pay Attention intro Next not available; "
                    + "agreement card may already be visible.");
        }
        // Poll for the agreement card; cap at 3s.
        long paAgreementDeadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < paAgreementDeadline
                && !focusZone.isPayAttentionAgreementVisible()) {
            mUtils.sleep(200L);
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_Agreement");

        try {
            focusZone.tapIAgree();
        } catch (Throwable t) {
            logger.info("Pay Attention tapIAgree did not complete cleanly (continuing): "
                    + t.getMessage());
        }
        focusZone.acceptPermissionPrompts(3, 6000L);

        // Row-alignment guarantee: PayAttention_ByProceeding must always
        // fire. Try the long wait first; fall back to a short late-arrival
        // poll; finally capture whatever is on screen under the same name
        // so the row aligns even on locales where the dialog never appears
        // for this account state.
        boolean byProceedingShown = false;
        try {
            byProceedingShown = focusZone.waitForByProceedingDialog(12000L);
        } catch (Throwable t) {
            logger.info("waitForByProceedingDialog failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_ByProceeding");
        if (byProceedingShown) {
            try {
                focusZone.tapByProceedingAccept();
            } catch (Throwable t) {
                logger.info("tapByProceedingAccept failed (continuing): " + t.getMessage());
            }
        } else {
            try {
                if (focusZone.acceptByProceedingIfPresent(6000L)) {
                    logger.info(
                            "By proceeding dialog appeared late; accepted before recording capture.");
                } else {
                    logger.info("By proceeding dialog did not appear; "
                            + "PayAttention_ByProceeding reflects best-available state.");
                }
            } catch (Throwable t) {
                logger.info("acceptByProceedingIfPresent failed (continuing): " + t.getMessage());
            }
        }
        // Hold for stable frame before Recording capture - the screen
        // composition (record dot + waveform) settles around ~1s on most
        // builds; cap at 1.5s.
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_Recording");

        if (focusZone.acceptByProceedingIfPresent(3000L)) {
            logger.info("By proceeding dialog re-appeared; accepted before expanding tabs.");
            try {
                mDevice.waitForIdle(1000L);
            } catch (Throwable ignored) {
            }
        }

        // Tap Record bubble to expand Pay Attention tabs. The bubble bar
        // can be hidden on Compose surfaces (e.g. when the Recording state
        // takes over the foreground), so swallow tap failures and rely on
        // the next waitForPayAttentionTabs to confirm the surface state.
        try {
            focusZone.tapRecordBubble();
        } catch (Throwable t) {
            logger.info("Pay Attention Record bubble tap failed (continuing): " + t.getMessage());
        }
        if (!focusZone.waitForPayAttentionTabs(5000L)) {
            try {
                focusZone.tapRecordBubble();
            } catch (Throwable t) {
                logger.info("Pay Attention Record bubble retry tap failed (continuing): " + t.getMessage());
            }
            try {
                mDevice.waitForIdle(1000L);
            } catch (Throwable ignored) {
            }
        }
        if (!focusZone.waitForPayAttentionTabs(5000L)) {
            logger.info("Pay Attention tabs did not appear after expanding; capturing anyway.");
        }
        capturePayAttentionProcessingStages(focusZone);

        // Explicitly select the Summary tab first so the screenshot is
        // not at the mercy of whatever default the Pay Attention card
        // happens to land on (some firmware lands on Transcript first).
        // selectPayAttentionTabAndVerify uses a multi-locale label
        // dictionary + a geometry fallback (Nth pill from the left)
        // and waits up to 3s for the body headline to confirm the
        // tap actually changed the active tab.
        boolean summarySelected = false;
        try {
            summarySelected = focusZone.selectPayAttentionTabAndVerify("Summary", 3000L);
        } catch (Throwable t) {
            logger.info("selectPayAttentionTabAndVerify(Summary) failed (continuing): "
                    + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_Summary");
        if (!summarySelected) {
            logger.info("Pay Attention Summary tab not verified active before capture; "
                    + "PayAttention_Summary reflects best-available surface.");
        }

        // Row-alignment guarantee: both _Transcript and _AudioRecording
        // tab captures must always fire. selectPayAttentionTabAndVerify
        // taps the localized pill (by desc/text dictionary or by
        // geometry index) and verifies the tab is actually active by
        // waiting for the matching localized body headline ("Here is
        // the transcript" / "Hier ist die Transkription" / ...). If
        // verification fails we still take the screenshot to keep the
        // workbench row alignment, but the log line below makes the
        // bad capture explicit so triage can revisit it.
        boolean transcriptSelected = false;
        try {
            transcriptSelected = focusZone.selectPayAttentionTabAndVerify(
                    "Transcript", 3000L);
        } catch (Throwable t) {
            logger.info("selectPayAttentionTabAndVerify(Transcript) failed (continuing): "
                    + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_Transcript");
        if (!transcriptSelected) {
            logger.info("Pay Attention Transcript tab not verified active before capture; "
                    + "PayAttention_Transcript reflects best-available surface.");
        }

        boolean audioSelected = false;
        try {
            audioSelected = focusZone.selectPayAttentionTabAndVerify(
                    "Audio Recording", 3000L);
        } catch (Throwable t) {
            logger.info("selectPayAttentionTabAndVerify(Audio Recording) failed (continuing): "
                    + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("PayAttention_AudioRecording");
        if (!audioSelected) {
            logger.info("Pay Attention Audio Recording tab not verified active before capture; "
                    + "PayAttention_AudioRecording reflects best-available surface.");
        }
    }

    private void capturePayAttentionProcessingStages(QiraFocusZonePage focusZone) throws Exception {
        long deadline = System.currentTimeMillis() + PAY_ATTENTION_PROCESSING_TIMEOUT_MS;
        boolean generatingSummaryEmitted = false;
        while (System.currentTimeMillis() < deadline) {
            String stage = focusZone.currentPayAttentionProcessingStage();
            if (stage != null && !generatingSummaryEmitted) {
                // Processing labels are localized and can describe several
                // transient phases. en-XM defines one canonical row for the
                // whole processing state, so never derive a screenshot ID
                // from translated UI text.
                takeScreenshot("PayAttention_Generating_Summary");
                generatingSummaryEmitted = true;
            }
            if (!focusZone.isPayAttentionProcessing()) {
                break;
            }
            mUtils.sleep(400L);
        }
        // Row-alignment guarantee: PayAttention_Generating_Summary must
        // always fire. The processing-stage detector misses it on locales
        // where the localized "Generating Summary" headline is not in
        // PAY_ATTENTION_PROCESSING_LABELS or appears too briefly between
        // 400ms polls. Capture the current state as a best-effort fallback
        // so the workbench row aligns with en-XM.
        if (!generatingSummaryEmitted) {
            try {
                mDevice.waitForIdle(1000L);
            } catch (Throwable ignored) {
            }
            takeScreenshot("PayAttention_Generating_Summary");
        }
    }

    /**
     * Returns to the Qira home via the Focus Zone bubble (App Icon) and captures
     * every page of the hero slide carousel. The number of pages is discovered
     * at runtime from the pager's {@code "Page N of M"} content description.
     */
    private void captureFocusZoneSlides(QiraFocusZonePage focusZone) throws Exception {
        logger.info("Returning to Qira home to capture Focus Zone slide carousel.");
        QiraHomePage home = new QiraHomePage(mDevice, mConfig);

        for (int i = 0; i < 6 && !home.isDiscoverVisible(); i++) {
            if (focusZone.isBubbleBarVisible()) {
                try {
                    focusZone.tapFocusZoneAppIcon();
                } catch (IllegalStateException ignored) {
                    mDevice.pressBack();
                }
            } else {
                mDevice.pressBack();
            }
            // waitForIdle returns as soon as the launcher transition
            // settles; previously we slept a fixed 1.2s every iteration.
            try {
                mDevice.waitForIdle(1200L);
            } catch (Throwable ignored) {
            }
        }

        if (!home.isDiscoverVisible()) {
            // FocusZone_Home is NOT in the en-XM canonical baseline; per
            // the strict baseline rule we must not emit it. The Slide_*
            // captures will still fire if the carousel becomes reachable
            // on a retry inside goToSlide(); otherwise this branch just
            // returns without producing any extra screenshot.
            logger.info("Focus Zone home grid was not reachable after Pay Attention; "
                    + "skipping Slide_* captures.");
            return;
        }

        int total = focusZone.totalSlidePages();
        if (total <= 0) {
            // FocusZone_Home is not in the en-XM baseline; skip the
            // diagnostic screenshot and rely on the log line above.
            logger.info("Focus Zone slide carousel was not detected; "
                    + "skipping Slide_* captures.");
            return;
        }
        logger.info(String.format("Focus Zone slide carousel detected with %d pages.", total));

        focusZone.goToSlide(1, FOCUS_ZONE_SLIDE_MAX_ATTEMPTS);
        for (int page = 1; page <= total; page++) {
            boolean reached = focusZone.goToSlide(page, FOCUS_ZONE_SLIDE_MAX_ATTEMPTS);
            int observed = focusZone.currentSlidePage();
            if (!reached) {
                logger.info("Focus Zone slide " + page + " could not be stabilized (observed " + observed + ").");
            }
            // Hold for a stable frame before snapshotting (the swipe
            // animation overshoots a few px on fast devices); waitForIdle
            // returns immediately on a quiet tree.
            try {
                mDevice.waitForIdle(1000L);
            } catch (Throwable ignored) {
            }
            takeScreenshot("FocusZone_Slide_" + page);
        }
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
