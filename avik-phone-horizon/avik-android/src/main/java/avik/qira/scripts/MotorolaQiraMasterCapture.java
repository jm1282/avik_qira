package avik.qira.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import avik.qira.pages.QiraOnboardingPage;

/**
 * Master capture script that drives every Motorola Qira sub-flow in a single
 * JUnit run, starting from a fresh installation state and ending after the
 * Settings surface has been explored.
 *
 * <p>Execution order:
 * <ol>
 *     <li>{@link MotorolaQiraHomeCapture} — clears app data, walks the full
 *         first-run onboarding and ends on the Qira home tile grid.</li>
 *     <li>{@link MotorolaQiraFocusZoneCapture} — captures the floating
 *         bubble bar and every bubble entry point (Chat, Live, Catch me up,
 *         Pay Attention, Focus Zone slides).</li>
 *     <li>{@link MotorolaQiraCreatorZoneCapture} — captures the Creator
 *         Zone tile and its feature sub-screens.</li>
 *     <li>{@link MotorolaQiraKnowledgeCapture} — captures the Knowledge
 *         onboarding, main list, dropdowns, More options menu, FAB menu and
 *         a memory detail screen.</li>
 *     <li>{@link MotorolaQiraChatHistoryCapture} — captures the Chat
 *         History list, Manage chats mode and a chat detail surface with
 *         its More options overflow.</li>
 *     <li>{@link MotorolaQiraSettingsCapture} — opens the drawer, enters
 *         Settings and captures every master-detail option (Account,
 *         Devices, Smart Connect, Language, Launch Options, Voice,
 *         Lock-Screen Display, Sync Data, Personalized Answers, Catch Me
 *         Up, Connectors, About, Support Page, Legal Notices, Feedback).</li>
 * </ol>
 *
 * <p>The master shares its instrumentation state ({@code mDevice},
 * {@code mUtils}, {@code mConfig}, {@code mQiraApp}) with each child so the
 * children's own {@code @Before setUp()} does not re-run and accidentally
 * clear the app data populated by {@link MotorolaQiraHomeCapture}. Between
 * children the Qira app is force-stopped so each sub-flow launches from a
 * clean foreground state, while preserving the on-device account / synced
 * data produced by earlier steps.
 *
 * <p>Screenshots keep the prefix of the sub-capture that produced them
 * (e.g. {@code MotorolaQiraHome_*}, {@code MotorolaQiraSettings_*}); the
 * master's own prefix is only used if the master itself ever calls
 * {@code takeScreenshot}, which it currently does not.
 *
 * <p>Execution is wired through wrapper test classes like
 * {@link MotorolaQiraMasterCaptureTest} and
 * {@link MotorolaQiraMasterSmartphoneTest}, whose names match the project's
 * JUnit naming regex.
 */
public class MotorolaQiraMasterCapture extends BaseQiraCaptureScript {

    /**
     * Time we wait after a child's last {@code takeScreenshot()} call before
     * force-stopping Qira. {@code takeScreenshot()} only <em>queues</em> a
     * window-hierarchy dump onto a background Avik worker thread; if we
     * force-stop Qira while that worker is mid-dump, the worker sees a
     * {@code null} root window and hard-crashes the instrumentation process
     * with "getRoot(...) must not be null". 600ms comfortably covers the
     * dump latency observed in practice (the dump itself rarely exceeds
     * ~250ms on this device) once {@link #drainPendingAvikWork} has already
     * called {@code waitForIdle(2s)} - that returns as soon as the tree is
     * quiet, so by the time we land here the worker is almost always done.
     * Halving the previous 1200ms saves ~3.6s across the 6 sub-flows in
     * the master suite without ever observing a "getRoot must not be null"
     * crash on en-XM/de-DE/ja-JP regression runs.
     */
    private static final long POST_STEP_FLUSH_MS = 600L;

    /**
     * Pause after force-stopping Qira, so the launcher is fully foregrounded
     * before the next child issues {@code am start} for Qira.
     */
    private static final long POST_STOP_SETTLE_MS = 800L;

    /**
     * Extra idle wait budget for {@code waitForIdle()} between steps.
     * {@code waitForIdle} returns as soon as the accessibility tree is quiet
     * for ~500ms, so this is a worst-case upper bound and not a fixed
     * sleep.
     */
    private static final long INTER_STEP_IDLE_TIMEOUT_MS = 2000L;

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraMaster";
    }

    public void captureScreens() throws Exception {
        List<BaseQiraCaptureScript> pipeline = new ArrayList<>(Arrays.asList(
                new MotorolaQiraHomeCapture(),
                new MotorolaQiraFocusZoneCapture(),
                new MotorolaQiraCreatorZoneCapture(),
                new MotorolaQiraKnowledgeCapture(),
                new MotorolaQiraChatHistoryCapture(),
                new MotorolaQiraSettingsCapture()
        ));

        logger.info("Starting Motorola Qira master capture suite ("
                + pipeline.size() + " sub-flows).");

        List<String> failures = new ArrayList<>();

        int index = 0;
        for (BaseQiraCaptureScript step : pipeline) {
            index++;
            String stepName = step.getClass().getSimpleName();
            logger.info(String.format(Locale.US, "[%d/%d] Running %s (prefix=%s).",
                    index, pipeline.size(), stepName, step.getScreenPrefix()));

            // Share already-initialized instrumentation so the child does
            // not need its own @Before to fire. MotorolaQiraHomeCapture
            // still clears app data explicitly at the top of its
            // captureScreens(); every other child only force-stops, which
            // we do here on their behalf before invoking them.
            adoptChild(step);

            if (index > 1) {
                // Subsequent sub-flows expect a cold foreground launch.
                // Force-stop the app (without wiping data) so each one
                // reaches its expected launch surface via launchQiraApp(),
                // regardless of what screen the previous (possibly failed)
                // sub-flow left behind.
                try {
                    mQiraApp.forceStop();
                } catch (Throwable t) {
                    logger.info("Force-stop before " + stepName
                            + " failed (continuing): " + t.getMessage());
                }
                mUtils.sleep(POST_STOP_SETTLE_MS);
                // Defensively wake + unlock the device and return to the
                // launcher so the next child's launchQiraApp() call starts
                // from a clean surface even if the previous sub-flow died
                // while a system dialog was on top of Qira or the screen
                // auto-locked during the settle window. QiraOnboardingPage
                // has the most robust unlock logic in the project (wake +
                // dismiss keyguard + disable lock screen with retry), so
                // we reuse it directly.
                try {
                    QiraOnboardingPage onboardingPage =
                            new QiraOnboardingPage(mDevice, mConfig);
                    onboardingPage.ensureDeviceUnlocked();
                } catch (Throwable t) {
                    logger.info("ensureDeviceUnlocked before " + stepName
                            + " failed (continuing): " + t.getMessage());
                }
                try {
                    mDevice.pressHome();
                    mUtils.sleep(400L);
                } catch (Throwable ignored) {
                }
            }

            try {
                step.captureScreens();
                logger.info(String.format(Locale.US, "[%d/%d] %s completed.",
                        index, pipeline.size(), stepName));
            } catch (Throwable t) {
                // Continue-on-failure: a broken sub-flow shouldn't cost
                // the user the remaining ~70% of the capture suite. We
                // collect the failure, log the full stack trace, then
                // move on; the master will re-raise a combined error at
                // the end so the JUnit run still fails loudly.
                String message = (t.getMessage() != null && !t.getMessage().isEmpty())
                        ? t.getMessage()
                        : t.getClass().getSimpleName();
                failures.add(stepName + ": " + message);
                logger.info(String.format(Locale.US, "[%d/%d] %s FAILED: %s",
                        index, pipeline.size(), stepName, message));
                if (t instanceof Exception) {
                    try {
                        mUtils.printStackTraceOnLog((Exception) t);
                    } catch (Throwable ignored) {
                    }
                }
            }

            // Give the async Avik screenshot worker time to finish any
            // dumps queued by the child's final takeScreenshot() call
            // before we move on (or fall out of the loop and let
            // @After tearDown fire). See POST_STEP_FLUSH_MS javadoc.
            drainPendingAvikWork();
        }

        if (failures.isEmpty()) {
            logger.info("Motorola Qira master capture suite finished (all "
                    + pipeline.size() + " sub-flows OK).");
        } else {
            StringBuilder summary = new StringBuilder();
            summary.append("Motorola Qira master capture suite finished with ")
                    .append(failures.size())
                    .append(" / ")
                    .append(pipeline.size())
                    .append(" sub-flows failing:");
            for (String failure : failures) {
                summary.append("\n  - ").append(failure);
            }
            logger.info(summary.toString());
            // Re-raise so the JUnit run still reports failure. Every
            // sub-flow that <em>did</em> succeed has already produced its
            // screenshots, so this is the best of both worlds: max
            // coverage plus an explicit CI signal.
            throw new IllegalStateException(summary.toString());
        }
    }

    /**
     * Best-effort flush of any in-flight UiAutomator / Avik work produced
     * by the just-completed child. The Avik screenshot service queues its
     * hierarchy dumps onto a background worker, and UiAutomator itself
     * batches events; a brief idle wait plus a fixed sleep reliably gives
     * both enough time to settle before we kill Qira or tear down.
     */
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

    public void testMain() {
        try {
            captureScreens();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}
