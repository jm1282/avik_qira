package avik.qirapc.scripts;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.awt.Rectangle;

import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.pages.LaunchFlowPage;
import avik.qirapc.utils.Qira;

@AvikWinScript
public class TestLaunchFlowPage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_LaunchFlowPage_";

    private static Application mApp;
    private static Rectangle mRect;
    private static boolean mQiraLaunched;
    protected AvikLogger mLogger = AvikLogger.INSTANCE;

    @BeforeAll
    public static void setup() throws Exception {
        mApp = Application.getInstance();
        mRect = Utils.INSTANCE.getDesktopRect();
        mQiraLaunched = false;

        Qira.killQiraWinProcesses();
        Thread.sleep(3000);
        Qira.launchQira();
        mQiraLaunched = true;
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (mRect != null && mQiraLaunched) {
            try {
                new ExplorerPage(mRect).tapReturnToBasePage();
            } catch (Exception ignored) {
                // The launch flow can finish on desktop overlays where the return action is unavailable.
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
        LaunchFlowPage launchFlowPage = new LaunchFlowPage(mRect);

        launchFlowPage.waitForLauncherOverlay();
        capture("launching_overlay");

        launchFlowPage.waitForBubble();
        capture("bubble");

        launchFlowPage.hoverOverChat();
        capture("chat_hover");

        launchFlowPage.tapChat();
        capture("chat_clicked");

        launchFlowPage.dismissOverlay();
        launchFlowPage.hoverOverLive();
        capture("live_hover");

        launchFlowPage.tapLive();
        capture("live_intro");

        launchFlowPage.tapNext();
        capture("live_intro_next");

        launchFlowPage.tapAgree();
        capture("live_agree");

        launchFlowPage.tapLive();
        launchFlowPage.waitForThinkingState();
        capture("thinking");

        launchFlowPage.tapCamera();
        capture("camera");

        launchFlowPage.dismissOverlay();
        launchFlowPage.hoverOverUpdateMe();
        capture("update_me_hover");

        launchFlowPage.tapUpdateMe();
        capture("update_me_intro");

        launchFlowPage.tapNext();
        capture("update_me_intro_next");

        launchFlowPage.tapAgree();
        launchFlowPage.waitForAttachedState();
        capture("attached");

        launchFlowPage.tapBubble();
        launchFlowPage.hoverOverFocusZone();
        capture("focus_zone");
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }
}
