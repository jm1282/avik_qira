package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.scripts.BaseQiraCaptureScript;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;
import avik.qira_v2.utils.QiraV2OnboardingStartFlow;
import avik.qira_v2.utils.QiraV2ScreenshotSink;

@RunWith(AndroidJUnit4.class)
public class MotorolaQiraHome_Onboarding_Start extends BaseQiraCaptureScript {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    protected String getScreenPrefix() {
        return QiraV2OnboardingStartFlow.SCREEN_PREFIX;
    }

    @Override
    public void captureScreens() throws Exception {
        QiraV2OnboardingStartFlow.capture(
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
        logger.info("QiraV2 home onboarding capture complete; leaving Qira visible"
                + " for SLAP string/message ID validation.");
    }

    @Test
    public void testMain() throws Exception {
        captureScreens();
    }
}
