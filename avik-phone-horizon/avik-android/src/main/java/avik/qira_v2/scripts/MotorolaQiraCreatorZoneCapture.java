package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import avik.qira.pages.QiraCreatorZonePage;
import avik.qira_v2.pages.QiraV2CreatorZonePage;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/**
 * qira_v2 entry point for the Creator Zone capture suite.
 *
 * <p>The legacy Creator Zone page object already contains the full surface
 * matrix and cross-locale hardening for the current Qira Creator experience.
 * This v2 entry point keeps that proven coverage while applying qira_v2
 * instrumentation defaults: appId=qira, UI dumps on, screenshot text on, and
 * screenshot descriptions on for SLAP/string-link capture.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraCreatorZoneCapture
        extends avik.qira.scripts.MotorolaQiraCreatorZoneCapture {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    public void captureScreens() throws Exception {
        QiraV2InstrumentationDefaults.logEffectiveConfig(mConfig, logger);
        logger.info("MotorolaQiraCreatorZoneCapture qira_v2:"
                + " running Creator Zone coverage with qira_v2 SLAP defaults.");
        super.captureScreens();
    }

    @Override
    protected QiraCreatorZonePage createCreatorZonePage() throws Exception {
        return new QiraV2CreatorZonePage(mDevice, mConfig);
    }

    @Override
    protected boolean requireVerifiedCanonicalSurfaces() {
        return true;
    }

    /**
     * Corrected Creator Zone onboarding for the current qira_v2 build.
     *
     * <p>Proven live (en-XM, fresh onboarding): opening Creator Zone shows the
     * 3-page onboarding carousel, but the daily-quota <b>Information</b> dialog
     * auto-appears <em>over page&nbsp;1</em> and intercepts the "Next" taps. The
     * inherited v1 sequence therefore never advanced past page&nbsp;1 - it
     * mis-captured the dialog as Onboarding_2/Onboarding_3 and the stuck intro as
     * CreatorHome_Grid. Here we dismiss the dialog first (capturing it as
     * Onboarding_InformationQuota), then walk the carousel page-by-page so every
     * screenshot matches its tag:
     *
     * <ol>
     *   <li>Onboarding_InformationQuota - the auto-raised daily-quota dialog.</li>
     *   <li>Onboarding_1_CreatorZone - page 1 intro ("Creator Zone").</li>
     *   <li>Onboarding_2_ImaginationRunFree - page 2 ("Let your imagination run free").</li>
     *   <li>Onboarding_3_MakeItYourOwn - page 3 ("Make it your own", CTA "I agree").</li>
     *   <li>CreatorHome_Grid - the Creator Zone feature grid.</li>
     * </ol>
     */
    @Override
    protected void captureCreatorOnboardingSequence(QiraCreatorZonePage creator) throws Exception {
        final long pageTimeoutMs = 10000L;
        final long infoTimeoutMs = 8000L;
        final long homeTimeoutMs = 10000L;

        creator.tapCreatorZoneTile();
        if (!creator.waitForOnboardingPage1(pageTimeoutMs)) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page 1 was not resource-verified.");
        }

        // Daily-quota Information dialog auto-appears over page 1 on first open.
        // Capture and dismiss it so the carousel underneath becomes tappable.
        if (creator.waitForInformationDialog(infoTimeoutMs)) {
            waitIdle(800L);
            takeScreenshot("Onboarding_InformationQuota");
            if (!creator.tapGotIt()) {
                logger.info("Creator Zone qira_v2: Information 'Got It' was not tappable;"
                        + " pressing back to dismiss.");
                mDevice.pressBack();
            }
            // Reveal the clean page-1 intro underneath the dismissed dialog.
            creator.waitForOnboardingPage1(pageTimeoutMs);
        } else {
            logger.info("Creator Zone qira_v2: quota Information dialog did not auto-appear"
                    + " over onboarding page 1; capturing current surface as InformationQuota.");
            waitIdle(800L);
            takeScreenshot("Onboarding_InformationQuota");
        }

        waitIdle(800L);
        takeScreenshot("Onboarding_1_CreatorZone");

        // Page 2: "Let your imagination run free".
        if (!creator.tapNext()) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page-1 Next was not resource-clickable.");
        }
        if (!creator.waitForOnboardingPage2(pageTimeoutMs)) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page 2 was not resource-verified.");
        }
        waitIdle(800L);
        takeScreenshot("Onboarding_2_ImaginationRunFree");

        // Page 3: "Make it your own".
        if (!creator.tapNext()) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page-2 Next was not resource-clickable.");
        }
        if (!creator.waitForOnboardingPage3(pageTimeoutMs)) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page 3 was not resource-verified.");
        }
        waitIdle(800L);
        takeScreenshot("Onboarding_3_MakeItYourOwn");

        // Page 3 primary CTA is "I agree" (handled by tapNext's label set) ->
        // Creator Home feature grid.
        if (!creator.tapNext()) {
            throw new IllegalStateException(
                    "Creator Zone onboarding page-3 agreement was not resource-clickable.");
        }
        if (!creator.waitForCreatorHome(homeTimeoutMs)) {
            throw new IllegalStateException(
                    "Creator Zone home grid did not expose its resource-backed tiles"
                            + " after onboarding.");
        }
        waitIdle(1500L);
        takeScreenshot("CreatorHome_Grid");
    }

    private void waitIdle(long ms) {
        try {
            mDevice.waitForIdle(ms);
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected void takeScreenshot(String suffix) throws Exception {
        String screenName = String.format(Locale.US, "%s_%s", getScreenPrefix(), suffix);
        QiraV2CaptureArtifacts.captureSlapScreenshot(
                avikHandler,
                mDevice,
                mUtils,
                mConfig,
                logger,
                screenName);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.info("QiraV2 Creator Zone capture complete; leaving Qira visible"
                + " for SLAP validation.");
    }

    @Test
    @Override
    public void testMain() {
        try {
            captureScreens();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
