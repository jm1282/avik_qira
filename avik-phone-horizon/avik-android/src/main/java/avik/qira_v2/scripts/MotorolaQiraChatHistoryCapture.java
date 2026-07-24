package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import avik.qira.pages.QiraHistoryPage;
import avik.qira_v2.pages.QiraV2HistoryPage;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/**
 * qira_v2 entry point for the Chat History capture suite.
 *
 * <p>The Chat History page/script in {@code avik.qira} owns the tested flow and
 * locale-safe selectors. This wrapper applies qira_v2 SLAP defaults and routes
 * every screenshot through the v2 artifact path so string/message links are
 * validated for each screen.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraChatHistoryCapture
        extends avik.qira.scripts.MotorolaQiraChatHistoryCapture {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    public void captureScreens() throws Exception {
        QiraV2InstrumentationDefaults.logEffectiveConfig(mConfig, logger);
        logger.info("MotorolaQiraChatHistoryCapture qira_v2:"
                + " running Chat History coverage with qira_v2 SLAP defaults.");
        super.captureScreens();
    }

    @Override
    protected QiraHistoryPage createHistoryPage() throws Exception {
        return new QiraV2HistoryPage(mDevice, mConfig);
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
        logger.info("QiraV2 Chat History capture complete; leaving Qira visible"
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
