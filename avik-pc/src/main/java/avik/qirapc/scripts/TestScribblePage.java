package avik.qirapc.scripts;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.awt.Rectangle;

import avik.qirapc.pages.CreatorZonePage;
import avik.qirapc.pages.ScribblePage;
import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.utils.Qira;

@AvikWinScript
public class TestScribblePage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_Scribble_";
    private static final int MAX_TOOLTIP_STEPS = 4;
    private static final long APP_READY_WAIT_MS = 5000L;
    private static final long RETURN_HOME_WAIT_MS = 2000L;

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
            }
        }
    }

    @AvikWinMain
    public void execute() throws Exception {
        try {
            main();
        } catch (Exception e) {
            for (StackTraceElement el : e.getStackTrace()) {
                mLogger.warning(el.toString());
            }
            throw e;
        }
    }

    public void main() throws Exception {
        Thread.sleep(APP_READY_WAIT_MS);

        CreatorZonePage creatorZonePage = new CreatorZonePage(mRect);
        ScribblePage scribblePage = new ScribblePage(mRect);

        openCreatorZoneAndDismissOnboardingIfNeeded(creatorZonePage);

        // ---- Scribble flow using coordinate-only actions ----
        scribblePage.tapScribble();
        capture("01_open_module");

        scribblePage.tapClickHereToDraw();
        capture("02_draw_mode_opened");

        scribblePage.drawLineByCoordinates();
        scribblePage.tapRightIconAtCoordinate();
        capture("03_line_drawn_and_right_icon_clicked");

        scribblePage.tapConfirm();
        capture("04_confirmed");

        returnToCreatorZoneHome(creatorZonePage, scribblePage);
    }

    private void openCreatorZoneAndDismissOnboardingIfNeeded(CreatorZonePage creatorZonePage) throws Exception {
        creatorZonePage.tapCreatorZone();

        if (!creatorZonePage.isOnboardingTooltipVisible()) {
            capture("CreatorZone_home");
            return;
        }

        for (int i = 1; i <= MAX_TOOLTIP_STEPS; i++) {
            capture("CreatorZone_onboarding_" + i);
            creatorZonePage.tapTooltipAdvance();

            if (!creatorZonePage.isOnboardingTooltipVisible()) {
                capture("CreatorZone_home");
                return;
            }
        }

        throw new IllegalStateException("Tooltip did not disappear");
    }

    private void capture(String name) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + name);
    }

    private void returnToCreatorZoneHome(CreatorZonePage creatorZonePage, ScribblePage scribblePage) throws Exception {
        scribblePage.tapBack();
        creatorZonePage.tapCreatorZone();
        Thread.sleep(RETURN_HOME_WAIT_MS);
    }
}
