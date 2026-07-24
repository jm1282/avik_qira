package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2FocusZoneFlow;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;
import avik.qira_v2.utils.QiraV2ScreenshotSink;

/**
 * Workbench-facing entry point for the qira_v2 Focus Zone capture flow.
 *
 * <p>Lives in the {@code avik.qira.scripts} package and applies
 * {@link QiraV2InstrumentationDefaults} so the produced screenshots import under
 * the existing Workbench {@code appId=qira} module (not a separate
 * {@code qira_v2} tile), in en-XM with SLAP capture forced on. The flow itself
 * is implemented under {@code avik.qira_v2.*}; this wrapper only delegates.
 *
 * <p>Requires Qira to already be onboarded (run
 * {@code avik.qira.scripts.MotorolaQiraHome_Onboarding_Start} first). This
 * script does not clear app data ({@code qira.clearData} defaults to false).
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraFocusZoneCaptureV2 extends BaseQiraCaptureScript {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    protected String getScreenPrefix() {
        return QiraV2FocusZoneFlow.SCREEN_PREFIX;
    }

    @Override
    public void captureScreens() throws Exception {
        logger.info("MotorolaQiraFocusZoneCaptureV2:"
                + " running qira_v2 Focus Zone flow under qira appId.");
        QiraV2FocusZoneFlow.capture(
                mDevice,
                mUtils,
                mConfig,
                logger,
                new QiraV2ScreenshotSink() {
                    @Override
                    public void capture(String screenName) throws Exception {
                        QiraV2CaptureArtifacts.captureSlapScreenshot(
                                avikHandler,
                                mDevice,
                                mUtils,
                                mConfig,
                                logger,
                                screenName);
                    }
                });
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.info("MotorolaQiraFocusZoneCaptureV2 complete;"
                + " leaving Qira visible for SLAP validation.");
    }

    @Test
    public void testMain() throws Exception {
        captureScreens();
    }
}
