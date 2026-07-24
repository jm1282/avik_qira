package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/**
 * Workbench-facing entry point for qira_v2 Creator Zone captures.
 *
 * <p>This class intentionally lives under {@code avik.qira.scripts} so Avik
 * Workbench imports the executions into the existing QIRA appId/tile. The
 * runnable implementation is exposed under {@code avik.qira_v2.scripts} and
 * both paths apply the same qira_v2 SLAP defaults.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraCreatorZoneCaptureV2
        extends avik.qira_v2.scripts.MotorolaQiraCreatorZoneCapture {

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    public void captureScreens() throws Exception {
        logger.info("MotorolaQiraCreatorZoneCaptureV2:"
                + " running qira_v2 Creator Zone flow under qira appId.");
        super.captureScreens();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.info("MotorolaQiraCreatorZoneCaptureV2 complete;"
                + " leaving Qira visible for SLAP validation.");
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
