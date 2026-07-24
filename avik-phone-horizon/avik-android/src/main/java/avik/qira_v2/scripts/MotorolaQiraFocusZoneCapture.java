package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.scripts.BaseQiraCaptureScript;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2FocusZoneFlow;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;
import avik.qira_v2.utils.QiraV2ScreenshotSink;

/**
 * Original qira_v2 implementation entry point for the Focus Zone capture flow.
 * The Workbench-facing wrapper is
 * {@code avik.qira.scripts.MotorolaQiraFocusZoneCaptureV2}; both delegate to
 * {@link QiraV2FocusZoneFlow}. Requires Qira to already be onboarded.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraFocusZoneCapture extends BaseQiraCaptureScript {

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
        logger.info("QiraV2 Focus Zone capture complete; leaving Qira visible"
                + " for SLAP validation.");
    }

    @Test
    public void testMain() throws Exception {
        captureScreens();
    }
}
