package avik.qira.utils;

import android.os.Build;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import java.util.Locale;

import avik.qira.pages.QiraComposerPage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraSurfacePage;

public class QiraApp {

    private static final String ACTION_CATCH_ME_UP = "com.lenovo.qira.action.CATCH_ME_UP";
    private static final String ACTION_QUANTUM_QUICK_ACTION =
            "com.lenovo.qira.action.QUANTUM_QUICK_ACTION";
    private static final String EXTRA_LAUNCH_WITH_ACTION = "launch_with_action";
    private static final String QUICK_ACTION_PAY_ATTENTION = "pay_attention";
    private static final String ACTION_MOTO_AI_HERO = "com.lenovo.qira.action.MOTO_AI_HERO_ACTION";

    private final UiDevice mDevice;
    private final AvikUtility mUtils;
    private final QiraConfig mConfig;

    public QiraApp(QiraConfig config) throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtils = AvikUtility.getInstance();
        mConfig = config;
    }

    public void clearApp() throws Exception {
        runShellCommand(String.format(Locale.US, "pm clear %s", mConfig.getPackageName()));
        mUtils.sleep(2000L);
    }

    public void forceStop() throws Exception {
        runShellCommand(String.format(Locale.US, "am force-stop %s", mConfig.getPackageName()));
        mUtils.sleep(1000L);
    }

    public QiraHomePage launch() throws Exception {
        mDevice.pressHome();
        mUtils.sleep(1000L);

        startExplicitActivity(mConfig.getLaunchActivity());
        return new QiraHomePage(mDevice, mConfig);
    }

    public QiraComposerPage launchHeroAction() throws Exception {
        runShellCommand(String.format(Locale.US, "am start -n %s -a %s",
                buildComponentName(mConfig.getPromptActivity()), ACTION_MOTO_AI_HERO));
        waitForForegroundOrThrow("Motorola Qira composer");
        return new QiraComposerPage(mDevice, mConfig);
    }

    public QiraSurfacePage launchCatchMeUp() throws Exception {
        runShellCommand(String.format(Locale.US, "am start -n %s -a %s",
                buildComponentName(mConfig.getPromptActivity()), ACTION_CATCH_ME_UP));
        waitForForegroundOrThrow("Motorola Qira Catch Me Up");
        return new QiraSurfacePage(mDevice, mConfig);
    }

    public QiraSurfacePage launchPayAttention() throws Exception {
        runShellCommand(String.format(Locale.US, "am start -n %s -a %s --es %s %s",
                buildComponentName(mConfig.getPromptActivity()),
                ACTION_QUANTUM_QUICK_ACTION,
                EXTRA_LAUNCH_WITH_ACTION,
                QUICK_ACTION_PAY_ATTENTION));
        waitForForegroundOrThrow("Motorola Qira Pay Attention");
        return new QiraSurfacePage(mDevice, mConfig);
    }

    private void startExplicitActivity(String activity) throws Exception {
        if (mConfig.hasLaunchActivity()) {
            runShellCommand(String.format(Locale.US, "am start -W -n %s",
                    buildComponentName(activity)));
            if (waitForForeground(10000L)) {
                return;
            }
        }

        runShellCommand(String.format(Locale.US,
                "monkey -p %s -c android.intent.category.LAUNCHER 1",
                mConfig.getPackageName()));
        if (!waitForForeground(10000L)) {
            throw new IllegalStateException("Unable to launch Motorola Qira home");
        }
    }

    private String buildComponentName(String activity) {
        if (activity.contains("/")) {
            return activity;
        }
        return String.format(Locale.US, "%s/%s",
                mConfig.getPackageName(), activity);
    }

    private void runShellCommand(String command) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDevice.executeShellCommand(command);
        }
    }

    private boolean waitForForeground(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String currentPackage = mDevice.getCurrentPackageName();
            if (mConfig.getPackageName().equals(currentPackage)) {
                mUtils.sleep(1500L);
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    private void waitForForegroundOrThrow(String surfaceName) throws Exception {
        if (!waitForForeground(10000L)) {
            throw new IllegalStateException("Unable to bring " + surfaceName + " to foreground");
        }
    }
}
