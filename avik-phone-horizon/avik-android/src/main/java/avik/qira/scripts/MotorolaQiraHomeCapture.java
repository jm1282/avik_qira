package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2HomeOnboardingFlow;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;
import avik.qira_v2.utils.QiraV2ScreenshotSink;

@RunWith(AndroidJUnit4.class)
public class MotorolaQiraHomeCapture extends BaseQiraCaptureScript {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    protected String getScreenPrefix() {
        return QiraV2HomeOnboardingFlow.SCREEN_PREFIX;
    }

    @Override
    public void captureScreens() throws Exception {
        logger.info("MotorolaQiraHomeCapture compatibility shim:"
                + " delegating to qira_v2 home onboarding capture.");
        QiraV2HomeOnboardingFlow.capture(
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
        logger.info("MotorolaQiraHomeCapture compatibility shim complete;"
                + " leaving Qira visible for SLAP validation.");
    }

    @Test
    public void testMain() throws Exception {
        captureScreens();
    }
}
