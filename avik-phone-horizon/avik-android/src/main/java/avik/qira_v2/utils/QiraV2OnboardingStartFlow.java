package avik.qira_v2.utils;

import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;

/**
 * Compatibility wrapper for the start-only flow name. Delegates to the full
 * qira_v2 home onboarding capture pipeline.
 */
public final class QiraV2OnboardingStartFlow {

    public static final String SCREEN_PREFIX = QiraV2HomeOnboardingFlow.SCREEN_PREFIX;
    public static final String SCREEN_SUFFIX = "Onboarding_Start";
    public static final String SCREEN_NAME = SCREEN_PREFIX + "_" + SCREEN_SUFFIX;

    private QiraV2OnboardingStartFlow() {
    }

    public static void capture(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink) throws Exception {
        QiraV2HomeOnboardingFlow.capture(
                device,
                utils,
                config,
                logger,
                screenshotSink);
    }
}
