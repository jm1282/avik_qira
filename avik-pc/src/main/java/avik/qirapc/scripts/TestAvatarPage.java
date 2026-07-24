package avik.qirapc.scripts;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.awt.Rectangle;

import avik.qirapc.pages.AvatarPage;
import avik.qirapc.pages.CreatorZonePage;
import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.utils.Qira;

@AvikWinScript
public class TestAvatarPage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_AvatarPage_";
    private static final int MAX_TOOLTIP_STEPS = 4;

    private static Application mApp;
    private static Rectangle mRect;
    private static boolean mQiraLaunched;
    protected AvikLogger mLogger = AvikLogger.INSTANCE;

    @BeforeAll
    public static void setup() throws Exception {
        mApp = Application.getInstance();
        Rectangle desktopRect = Utils.INSTANCE.getDesktopRect();
        mQiraLaunched = false;
        Qira.restartQira(mApp, desktopRect);
        mRect = Qira.getMainWindowRectangle(mApp);
        mQiraLaunched = true;
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (mRect != null && mQiraLaunched) {
            try {
                new ExplorerPage(mRect).tapReturnToBasePage();
            } catch (Exception ignored) {
                // Avoid a stray desktop click if launch/navigation fails before the main window is ready.
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
        Thread.sleep(5000);

        CreatorZonePage creatorZonePage = new CreatorZonePage(mRect);
        AvatarPage avatarPage = new AvatarPage(mRect);

        openCreatorZoneAndDismissOnboardingIfNeeded(creatorZonePage);
        avatarPage.tapAvatar();
        capture("avatar_screen");
    }

    private void openCreatorZoneAndDismissOnboardingIfNeeded(CreatorZonePage creatorZonePage) throws Exception {
        creatorZonePage.tapCreatorZone();

        for (int step = 1; step <= MAX_TOOLTIP_STEPS && creatorZonePage.isOnboardingTooltipVisible(); step++) {
            creatorZonePage.tapTooltipAdvance();
        }

        if (creatorZonePage.isOnboardingTooltipVisible()) {
            throw new IllegalStateException(
                    "Creator Zone onboarding tooltip remained visible after " + MAX_TOOLTIP_STEPS + " steps."
            );
        }
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }
}
