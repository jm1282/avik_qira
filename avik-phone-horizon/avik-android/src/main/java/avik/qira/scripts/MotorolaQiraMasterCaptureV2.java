package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/**
 * Workbench-facing master entry point for qira_v2 captures.
 *
 * <p>Lives under {@code avik.qira.scripts} so Workbench imports executions
 * into the existing QIRA appId/tile while delegating the implementation to the
 * qira_v2 master suite.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraMasterCaptureV2
        extends avik.qira_v2.scripts.MotorolaQiraMasterCapture {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    public void captureScreens() throws Exception {
        logger.info("MotorolaQiraMasterCaptureV2:"
                + " running qira_v2 master flow under qira appId.");
        super.captureScreens();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.info("MotorolaQiraMasterCaptureV2 complete;"
                + " leaving Qira visible for SLAP validation.");
    }

    @Test
    @Override
    public void testMain() {
        super.testMain();
    }
}
