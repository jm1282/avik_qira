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
import avik.qirapc.pages.CustomImagePage;
import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.pages.GalleryPage;
import avik.qirapc.pages.ScribblePage;
import avik.qirapc.pages.StickerPage;
import avik.qirapc.utils.Qira;


/**
 * Sample of Avik Windows script in Java
 */
@AvikWinScript
public class TestCreatorZonePage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_";
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
            StackTraceElement[] error = e.getStackTrace();
            for (StackTraceElement stackTraceElement : error) {
                mLogger.warning(stackTraceElement.toString());
            }
            throw e;
        }
    }

    public void main() throws Exception {
        Thread.sleep(5000);

        CreatorZonePage creatorZonePage = new CreatorZonePage(mRect);
        CustomImagePage customImagePage = new CustomImagePage(mRect);
        StickerPage stickerPage = new StickerPage(mRect);
        ScribblePage scribblePage = new ScribblePage(mRect);
        GalleryPage galleryPage = new GalleryPage(mRect);

        openCreatorZoneAndDismissOnboardingIfNeeded(creatorZonePage);

        captureCustomImageFlow(creatorZonePage, customImagePage);
        captureStickerFlow(creatorZonePage, stickerPage);
        captureScribbleFlow(creatorZonePage, scribblePage);
        captureGalleryFlow(creatorZonePage, galleryPage);
        captureInpaintFlow(creatorZonePage);
        captureEraseFlow(creatorZonePage);
        captureChangeBackgroundFlow(creatorZonePage);
    }

    private void openCreatorZoneAndDismissOnboardingIfNeeded(CreatorZonePage creatorZonePage) throws Exception {
        creatorZonePage.tapCreatorZone();

        if (!creatorZonePage.isOnboardingTooltipVisible()) {
            capture("CreatorZone_creator_zone");
            return;
        }

        for (int step = 1; step <= MAX_TOOLTIP_STEPS; step++) {
            capture("CreatorZone_onboarding_step" + step);
            creatorZonePage.tapTooltipAdvance();

            if (!creatorZonePage.isOnboardingTooltipVisible()) {
                capture("CreatorZone_creator_zone");
                return;
            }
        }

        throw new IllegalStateException("Creator Zone onboarding tooltip remained visible after " + MAX_TOOLTIP_STEPS + " steps.");
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }

    private void captureCustomImageFlow(CreatorZonePage creatorZonePage, CustomImagePage customImagePage) throws Exception {
        customImagePage.tapCustomImage();
        capture("CustomImage_custom_image");

        customImagePage.hoverOverReferenceImage();
        capture("CustomImage_hover_reference_image");

        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureStickerFlow(CreatorZonePage creatorZonePage, StickerPage stickerPage) throws Exception {
        stickerPage.tapSticker();
        capture("Sticker_sticker");

        stickerPage.hoverOverDailyLimitBadge();
        capture("Sticker_hover_daily_limit");

        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureScribbleFlow(CreatorZonePage creatorZonePage, ScribblePage scribblePage) throws Exception {
        scribblePage.tapScribble();
        capture("Scribble_scribble");

        scribblePage.hoverOverScribbleHere();
        capture("Scribble_hover_scribble_here");

        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureGalleryFlow(CreatorZonePage creatorZonePage, GalleryPage galleryPage) throws Exception {
        galleryPage.tapGallery();
        capture("Gallery_gallery");

        galleryPage.hoverOverSync();
        capture("Gallery_hover_sync");

        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureInpaintFlow(CreatorZonePage creatorZonePage) throws Exception {
        openEditImageSample(creatorZonePage, "Inpaint");
        creatorZonePage.tapInpaint();
        capture("Inpaint_inpaint");
        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureEraseFlow(CreatorZonePage creatorZonePage) throws Exception {
        openEditImageSample(creatorZonePage, "Erase");
        creatorZonePage.tapErase();
        capture("Erase_erase");
        returnToCreatorZoneHome(creatorZonePage);
    }

    private void captureChangeBackgroundFlow(CreatorZonePage creatorZonePage) throws Exception {
        openEditImageSample(creatorZonePage, "ChangeBackground");
        creatorZonePage.tapChangeBackground();
        capture("ChangeBackground_change_background");
        returnToCreatorZoneHome(creatorZonePage);
    }

    private void openEditImageSample(CreatorZonePage creatorZonePage, String screenPrefix) throws Exception {
        creatorZonePage.tapEditImage();
        capture(screenPrefix + "_edit_image");

        creatorZonePage.tapClickToTry();
        capture(screenPrefix + "_click_to_try");
    }

    private void returnToCreatorZoneHome(CreatorZonePage creatorZonePage) throws Exception {
        creatorZonePage.tapBackArrow();
        creatorZonePage.tapCreatorZone();
        Thread.sleep(2000);
    }
}
