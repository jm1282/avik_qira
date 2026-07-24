package avik.demomode._2024;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.logging.Logger;

import avik.demomode.dialogs.BaseDialog;
import avik.demomode.pages.CameraPage;
import avik.demomode.pages.DisplayPage;
import avik.demomode.pages.FlexViewPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class GloryEMEA {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Glory_EMEA";
    private final int TOTAL_SCROLLS_TECHSPECS = 10;

    @Before
    public void setUp() throws Exception {
        mUtils = AvikUtility.getInstance();
        mDemoMode = new DemoModeFlutter();

        mDemoMode.clearApp();
        mUtils.pressBackKeySeveralTimes(5);
    }

    @After
    public void tearDown() throws Exception {
        mDemoMode.forceCloseApp();
        mUtils.pressBackKeySeveralTimes(5);
    }

    public void takeScreenshot(String screenName) throws Exception {
        avikHandler.takeScreenshot(screenName, true, true);
    }

    public void captureScreens() throws Exception {
        // takeScreenshot("DemoModeExperiences_Glory_Widget");
        HomePage homePage = mDemoMode.callApp();
        takeScreenshot("DemoModeExperiences_Glory_Discover");
        DisplayPage displayPage = homePage.openExperiences(DisplayPage.class);
        displayPage.openMenu();
        takeScreenshot("DemoModeExperiences_Glory_Menu");
        displayPage.goBack();
        takeScreenshot("DemoModeExperiences_Glory_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        takeScreenshot("DemoModeExperiences_Glory_EMEA_Display_Dialog_Scrolling1");
        displayDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Glory_EMEA_Display_Dialog_Scrolling2");
        displayDialog.closeDialog();
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Display_ExternalDisplay");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Display_MadeForMusic");

        FlexViewPage flexViewPage = displayPage.nextExperience(FlexViewPage.class);
        takeScreenshot("DemoModeExperiences_Glory_FlexView");
        BaseDialog flexViewDialog = flexViewPage.openDialog();
        takeScreenshot("DemoModeExperiences_Glory_FlexView_Dialog_Scrolling1");
        flexViewDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Glory_FlexView_Dialog_Scrolling2");
        flexViewDialog.closeDialog();
        flexViewPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_FlexView_Camcorder");
        flexViewPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_FlexView_HandsFree");
        flexViewPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_FlexView_DeskDisplay");

        CameraPage cameraPage = flexViewPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Glory_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_Glory_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Glory_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_TelephotoLens");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_AdaptiveStabilization");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_ActionShot");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_LongExposure");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Glory_Camera_TestCamera");
//		CameraT mCameraApp = cameraPage.openTestCamera();
//		takeScreenshot("DemoModeExperiences_Glory_Camera_GoBackOverlay");
//		cameraPage.clickGoBackOverlay();

        // Go Back to previous KSP to Avoid Error when scrolling on TestCamera Page
        cameraPage.previousExperienceCard();
        SpecsPage specsPage = cameraPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, MODEL_NAME, TOTAL_SCROLLS_TECHSPECS);
    }

    @Test
    public void testMain() {
        try {
            this.captureScreens();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }

}
