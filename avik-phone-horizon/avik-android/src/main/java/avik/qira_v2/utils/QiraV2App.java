package avik.qira_v2.utils;

import android.os.Build;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.Locale;
import java.util.logging.Logger;

import avik.qira.pages.QiraOnboardingPage;
import avik.qira.utils.QiraConfig;

public final class QiraV2App {

    private static final String ACTION_CORE_PACKAGE = "com.motorola.actioncore";

    private final UiDevice device;
    private final AvikUtility utils;
    private final QiraConfig config;
    private final Logger logger;

    public QiraV2App(UiDevice device, AvikUtility utils, QiraConfig config) {
        this.device = device;
        this.utils = utils;
        this.config = config;
        this.logger = AvikLoggerFactory.INSTANCE.getInstance();
    }

    public void clearDataAndLaunch() throws Exception {
        logger.info("QiraV2 launch: clearing " + config.getPackageName());
        runShell(String.format(Locale.US, "pm clear %s", config.getPackageName()));
        clearActionCoreOrFail();
        runShell(String.format(
                Locale.US,
                "pm grant %s android.permission.POST_NOTIFICATIONS",
                config.getPackageName()));
        sleep(1800L);
        launch();
    }

    private void clearActionCoreOrFail() throws Exception {
        String command = String.format(
                Locale.US, "pm clear %s", ACTION_CORE_PACKAGE);
        logger.info("QiraV2 launch: clearing companion package "
                + ACTION_CORE_PACKAGE);
        String output = executeShellForOutput(command);
        String normalized = output == null ? "" : output.trim();
        if (!isSuccessfulPackageClearOutput(normalized)) {
            throw new IllegalStateException(
                    "QiraV2 companion reset failed: command='" + command
                            + "', expected='Success', actual='"
                            + normalized.replace('\n', ' ').replace('\r', ' ')
                            + "'. Refusing a non-deterministic master run.");
        }
        logger.info("QiraV2 launch: companion package reset succeeded:"
                + " package=" + ACTION_CORE_PACKAGE
                + ", output=" + normalized);
    }

    static boolean isSuccessfulPackageClearOutput(String output) {
        return output != null && "Success".equals(output.trim());
    }

    public void launch() throws Exception {
        try {
            // A rooted locale restart can leave NotificationShade focused on
            // the keyguard even though dumpsys does not report the legacy
            // mKeyguardShowing markers. Disable/dismiss before attempting the
            // explicit Qira activity; otherwise every surface wait observes
            // SystemUI and the master fails all child flows.
            device.executeShellCommand("locksettings set-disabled true");
            device.executeShellCommand("wm dismiss-keyguard");
            device.executeShellCommand("input keyevent KEYCODE_WAKEUP");
            device.executeShellCommand("input keyevent 82");
            new QiraOnboardingPage(device, config).ensureDeviceUnlocked();
        } catch (Throwable t) {
            logger.info("QiraV2 launch: unlock preflight failed (continuing): "
                    + t.getMessage());
        }
        device.pressHome();
        sleep(600L);

        if (config.hasLaunchActivity()) {
            runShell(String.format(Locale.US, "am start -W -n %s",
                    buildComponentName(config.getLaunchActivity())));
            if (waitForQiraSurface(12000L)) {
                waitForLaunchedSurfaceIdle(3000L);
                return;
            }
        }

        runShell(String.format(Locale.US,
                "monkey -p %s -c android.intent.category.LAUNCHER 1",
                config.getPackageName()));
        if (!waitForQiraSurface(12000L)) {
            throw new IllegalStateException("Unable to launch Motorola Qira v2 start surface");
        }
        waitForLaunchedSurfaceIdle(3000L);
    }

    /**
     * Bounded, condition-based settle after the Qira surface is detected.
     * Returns as soon as the UI accessibility stream is idle, capped at the same
     * upper bound the previous fixed post-launch sleep used. This preserves
     * animation-settle semantics without an unconditional fixed wait.
     *
     * @param maxWaitMs upper bound for the settle wait
     */
    private void waitForLaunchedSurfaceIdle(long maxWaitMs) throws Exception {
        try {
            device.waitForIdle(maxWaitMs);
        } catch (Throwable ignored) {
        }
    }

    private boolean waitForQiraSurface(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            dismissBlockingOverlayIfVisible();
            String currentPackage = safeCurrentPackage();
            if (config.getPackageName().equals(currentPackage)
                    || device.findObject(By.pkg(config.getPackageName())) != null) {
                return true;
            }
            sleep(250L);
        }
        return false;
    }

    private void dismissBlockingOverlayIfVisible() throws Exception {
        String currentPackage = safeCurrentPackage();
        if (currentPackage != null && currentPackage.startsWith("com.motorola.mobiledesktop")) {
            logger.info("QiraV2 launch: dismissing Motorola mobile desktop overlay");
            device.pressBack();
            sleep(800L);
            return;
        }
        if ("com.android.systemui".equals(currentPackage)) {
            logger.info("QiraV2 launch: collapsing SystemUI overlay");
            try {
                device.executeShellCommand("cmd statusbar collapse");
            } catch (Throwable ignored) {
            }
            // SystemUI can transiently own focus while Qira's overlay is
            // launching. Back is not a safe generic dismissal here: it can
            // dismiss the Qira surface that the router just opened. Collapse
            // the system panel only and let the bounded package/surface wait
            // observe Qira returning foreground.
            sleep(300L);
            return;
        }

        String currentActivity = "";
        try {
            currentActivity = device.executeShellCommand(
                    "dumpsys window | grep -E 'mCurrentFocus|mFocusedApp'");
        } catch (Throwable ignored) {
        }
        if (currentActivity != null && currentActivity.contains("leakcanary")) {
            logger.info("QiraV2 launch: dismissing LeakCanary debug overlay");
            device.pressBack();
            sleep(800L);
        }
    }

    private String buildComponentName(String activity) {
        if (activity.contains("/")) {
            return activity;
        }
        return config.getPackageName() + "/" + activity;
    }

    private String safeCurrentPackage() {
        try {
            return device.getCurrentPackageName();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private void runShell(String command) throws Exception {
        String out = executeShellForOutput(command);
        if (out != null && !out.trim().isEmpty()) {
            logger.info("QiraV2 shell: " + command + " -> " + out.trim());
        }
    }

    private String executeShellForOutput(String command) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new IllegalStateException(
                    "QiraV2 shell command requires Android 5.0+: " + command);
        }
        return device.executeShellCommand(command);
    }

    private void sleep(long millis) throws Exception {
        if (utils != null) {
            utils.sleep(millis);
        } else {
            Thread.sleep(millis);
        }
    }
}
