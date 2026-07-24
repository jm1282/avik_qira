package avik.qirapc.scripts;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import java.awt.Rectangle;

import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.pages.SettingsPage;
import avik.qirapc.utils.Qira;

@AvikWinScript
public class TestSettingsPage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_SettingsPage_";

    private static Application mApp;
    private static Rectangle mRect;
    private static boolean mQiraLaunched;
    protected AvikLogger mLogger = AvikLogger.INSTANCE;

    @BeforeAll
    public static void setup() throws Exception {
        mApp = Application.getInstance();
        mRect = Utils.INSTANCE.getDesktopRect();
        mQiraLaunched = false;
        Qira.restartQira(mApp, mRect);
        mQiraLaunched = true;
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (mRect != null && mQiraLaunched) {
            try {
                new ExplorerPage(mRect).tapReturnToBasePage();
            } catch (Exception ignored) {
                // The test may fail before Qira is ready; avoid a stray desktop click in teardown.
            }
        }
    }

    @AvikWinMain
    public void executeCapture() throws Exception {
        try {
            main();
        } catch (Exception e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                mLogger.warning(stackTraceElement.toString());
            }
            throw e;
        }
    }

    public void main() throws Exception {
        Thread.sleep(4000);

        new ExplorerPage(mRect).tapSettings();
        capture("personal");

        SettingsPage settingsPage = new SettingsPage(mRect);

        settingsPage.tapAccount();
        capture("account");

        settingsPage.tapDevices();
        capture("devices");

        settingsPage.tapRefresh();
        capture("refresh_devices");
        Thread.sleep(5000);

        settingsPage.tapLanguage();
        capture("language");

        settingsPage.tapLanguageOptions();
        capture("language");

        settingsPage.tapLaunchOptions();
        capture("launch_options");

        settingsPage.tapVoice();
        capture("voice");

        settingsPage.tapSyncData();
        capture("sync_data");

        settingsPage.tapProcessingMode();
        capture("processing_mode");

        settingsPage.scrollSideBar();
        capture("sidebar_scrolled");
        Thread.sleep(3000);

        settingsPage.tapPersonalizedAnswers();
        capture("personalised_answers");

        settingsPage.tapConnectors();
        capture("connectors");

        settingsPage.tapUpdateMe();
        capture("update_me");

        settingsPage.tapCloseBehaviour();
        capture("close_behaviour");

        settingsPage.tapAbout();
        capture("about");

        settingsPage.tapSupportPage();
        capture("support_page");

        settingsPage.tapLegalNotices();
        capture("legal_notices");

        settingsPage.tapFeedback();
        capture("feedback");
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }
}
