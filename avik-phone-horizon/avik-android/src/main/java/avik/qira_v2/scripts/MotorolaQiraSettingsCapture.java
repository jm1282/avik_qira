package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import avik.qira.pages.QiraSettingsPage;
import avik.qira_v2.pages.QiraV2SettingsPage;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/** qira_v2 Settings entry point with stable resource-backed navigation. */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraSettingsCapture
        extends avik.qira.scripts.MotorolaQiraSettingsCapture {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    public void captureScreens() throws Exception {
        QiraV2InstrumentationDefaults.logEffectiveConfig(mConfig, logger);
        logger.info("MotorolaQiraSettingsCapture qira_v2:"
                + " running strict resource-backed Settings coverage.");
        super.captureScreens();
    }

    @Override
    protected QiraSettingsPage createSettingsPage(
            QiraSettingsPage openedPage) throws Exception {
        return new QiraV2SettingsPage(mDevice, mConfig);
    }

    @Override
    protected boolean requireVerifiedCanonicalSurfaces() {
        return true;
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
        logger.info("QiraV2 Settings capture complete; leaving Qira visible"
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
