package avik.qira_v2.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import avik.qira.pages.QiraOnboardingPage;
import avik.qira.scripts.BaseQiraCaptureScript;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;

/**
 * Master qira_v2 capture suite.
 *
 * <p>Runs the complete qira_v2 pipeline in one instrumentation process so a
 * locale run produces one coherent execution: full home onboarding, Focus
 * Zone, Creator Zone, Knowledge, Chat History and Settings. Each child keeps
 * its own screenshot prefix and routes captures through the qira_v2 SLAP path.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraMasterCapture extends BaseQiraCaptureScript {

    private static final long POST_STEP_FLUSH_MS = 700L;
    private static final long POST_STOP_SETTLE_MS = 900L;
    private static final long INTER_STEP_IDLE_TIMEOUT_MS = 2500L;

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraMasterV2";
    }

    @Override
    public void captureScreens() throws Exception {
        QiraV2InstrumentationDefaults.logEffectiveConfig(mConfig, logger);

        List<BaseQiraCaptureScript> pipeline = new ArrayList<>(Arrays.asList(
                new avik.qira.scripts.MotorolaQiraHome_Onboarding_Start(),
                new MotorolaQiraFocusZoneCapture(),
                new avik.qira.scripts.MotorolaQiraCreatorZoneCaptureV2(),
                new avik.qira.scripts.MotorolaQiraKnowledgeCaptureV2(),
                new avik.qira.scripts.MotorolaQiraChatHistoryCaptureV2(),
                new avik.qira.scripts.MotorolaQiraSettingsCaptureV2()
        ));

        logger.info("Starting qira_v2 master capture suite ("
                + pipeline.size() + " sub-flows).");

        List<String> failures = new ArrayList<>();

        for (int index = 0; index < pipeline.size(); index++) {
            BaseQiraCaptureScript step = pipeline.get(index);
            String stepName = step.getClass().getSimpleName();
            logger.info(String.format(Locale.US,
                    "[qira_v2 master %d/%d] Running %s.",
                    index + 1,
                    pipeline.size(),
                    stepName));

            adoptChild(step);
            if (index > 0) {
                resetBetweenSteps(stepName);
            }

            try {
                step.captureScreens();
                logger.info(String.format(Locale.US,
                        "[qira_v2 master %d/%d] %s completed.",
                        index + 1,
                        pipeline.size(),
                        stepName));
            } catch (Throwable t) {
                String message = t.getMessage() == null || t.getMessage().isEmpty()
                        ? t.getClass().getSimpleName()
                        : t.getMessage();
                failures.add(stepName + ": " + message);
                logger.info(String.format(Locale.US,
                        "[qira_v2 master %d/%d] %s FAILED: %s",
                        index + 1,
                        pipeline.size(),
                        stepName,
                        message));
                if (t instanceof Exception) {
                    try {
                        mUtils.printStackTraceOnLog((Exception) t);
                    } catch (Throwable ignored) {
                    }
                }
            }

            drainPendingAvikWork();
        }

        if (!failures.isEmpty()) {
            StringBuilder summary = new StringBuilder();
            summary.append("qira_v2 master capture suite failed ")
                    .append(failures.size())
                    .append(" / ")
                    .append(pipeline.size())
                    .append(" sub-flows:");
            for (String failure : failures) {
                summary.append("\n  - ").append(failure);
            }
            logger.info(summary.toString());
            throw new IllegalStateException(summary.toString());
        }

        logger.info("qira_v2 master capture suite finished; all sub-flows OK.");
    }

    private void resetBetweenSteps(String nextStepName) throws Exception {
        try {
            mQiraApp.forceStop();
        } catch (Throwable t) {
            logger.info("qira_v2 master force-stop before " + nextStepName
                    + " failed (continuing): " + t.getMessage());
        }
        mUtils.sleep(POST_STOP_SETTLE_MS);
        try {
            new QiraOnboardingPage(mDevice, mConfig).ensureDeviceUnlocked();
        } catch (Throwable t) {
            logger.info("qira_v2 master unlock before " + nextStepName
                    + " failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.pressHome();
            mUtils.sleep(400L);
        } catch (Throwable ignored) {
        }
    }

    private void drainPendingAvikWork() {
        try {
            mDevice.waitForIdle(INTER_STEP_IDLE_TIMEOUT_MS);
        } catch (Throwable ignored) {
        }
        try {
            mUtils.sleep(POST_STEP_FLUSH_MS);
        } catch (Throwable ignored) {
        }
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.info("qira_v2 master complete; leaving Qira visible for SLAP validation.");
    }

    @Test
    public void testMain() {
        try {
            captureScreens();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}

