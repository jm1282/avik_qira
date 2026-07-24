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
import avik.demomode.pages.HomePage;
import avik.demomode.pages.PerformancePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class CuscoPlus {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "CuscoPlus";
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
        takeScreenshot("DemoModeExperiences_CuscoPlus_Widget");
        HomePage homePage = mDemoMode.callApp();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Discover");
        DesignPage designPage = homePage.openExperiences(DesignPage.class);
        designPage.openMenu();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Menu");
        designPage.goBack();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Design_Main");
        BaseDialog designDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Design_Dialog_Scrolling1");
        designDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Design_Dialog_Scrolling2");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Design_Durability");

        CameraPage cameraPage = designPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_Main");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_LowLight");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_VideoHorizonLock");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_UltraWide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_Selfie");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_TestCamera");
        CameraT mCameraApp = cameraPage.openTestCamera();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Camera_GoBackOverlay");
        cameraPage.clickGoBackOverlay();

        PerformancePage performancePage = cameraPage.nextExperience(PerformancePage.class);
        takeScreenshot("DemoModeExperiences_CuscoPlus_Performance");
        BaseDialog performanceDialog = performancePage.openDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Performance_Dialog_Scrolling1");
        performanceDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Performance_Dialog_Scrolling2");
        performanceDialog.closeDialog();
        performancePage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_CuscoPlus_Performance_Engineered");

        SpecsPage specsPage = performancePage.nextExperience(SpecsPage.class);
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
