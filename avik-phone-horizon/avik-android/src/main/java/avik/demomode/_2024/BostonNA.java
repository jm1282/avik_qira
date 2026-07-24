package avik.demomode._2024;

import com.motorola.frevoutils.code.libraries.camera.CameraT;
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
import avik.demomode.pages.DesignPage;
import avik.demomode.pages.EntertainmentPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class BostonNA {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "BostonNA";
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
        takeScreenshot("DemoModeExperiences_BostonNA_Widget");
        HomePage homePage = mDemoMode.callApp();
        takeScreenshot("DemoModeExperiences_BostonNA_Discover");
        DesignPage designPage = homePage.openExperiences(DesignPage.class);
        designPage.openMenu();
        takeScreenshot("DemoModeExperiences_BostonNA_Menu");
        designPage.goBack();
        takeScreenshot("DemoModeExperiences_BostonNA_Design");
        BaseDialog designDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Design_Dialog_Scrolling1");
        designDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Design_Dialog_Scrolling2");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Design_Perfect");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Design_InControl");

        EntertainmentPage entertainmentPage = designPage.nextExperience(EntertainmentPage.class);
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment");
        BaseDialog entertainmentDialog = entertainmentPage.openDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment_Dialog_Scrolling1");
        entertainmentDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment_Dialog_Scrolling2");
        entertainmentDialog.closeDialog();
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment_Display");
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment_RefreshRate");
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Entertainment_DolbyAtmos");

        CameraPage cameraPage = entertainmentPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_BostonNA_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Main");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Ultrawide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_MacroVision");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_Selfie");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_TestCamera");
        CameraT mCameraApp = cameraPage.openTestCamera();
        takeScreenshot("DemoModeExperiences_BostonNA_Camera_GoBackOverlay");
        cameraPage.clickGoBackOverlay();

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
