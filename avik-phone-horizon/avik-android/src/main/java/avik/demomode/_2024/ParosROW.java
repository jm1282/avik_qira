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
import avik.demomode.pages.DesignPage;
import avik.demomode.pages.EntertainmentPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class ParosROW {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Paros_ROW";
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
        HomePage homePage = mDemoMode.callApp();
        DesignPage designPage = homePage.openExperiences(DesignPage.class);

//		mDemoMode.callAppFromHomeScreen();
//		DesignPage designPage = new HomePage(AvikUiDevice.getInstance(avikHandler).getUiautomatorUiDevice()).openExperiences(DesignPage.class);

        designPage.openMenu();
        takeScreenshot("DemoModeExperiences_Paros_Menu");
        designPage.goBack();
        takeScreenshot("DemoModeExperiences_Paros_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, "Paros", "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Design_Premium");

        CameraPage cameraPage = designPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Paros_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, "Paros", "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Camera_Main");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Camera_NightVision");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Camera_Selfie");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Camera_Portrait");

        EntertainmentPage entertainmentPage = cameraPage.nextExperience(EntertainmentPage.class);
        takeScreenshot("DemoModeExperiences_Paros_ROW_Entertainment");
        BaseDialog entertainmentDialog = entertainmentPage.openDialog();
        entertainmentDialog.captureScroll(avikHandler, "Paros", "Entertainment");
        entertainmentDialog.closeDialog();
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Paros_Entertainment_Elevate");

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
