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
public class MalmoROW {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Malmo_ROW";
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
        takeScreenshot("DemoModeExperiences_Malmo_Discover");
        DesignPage designPage = homePage.openExperiences(DesignPage.class);
        designPage.openMenu();
        takeScreenshot("DemoModeExperiences_Malmo_Menu");
        designPage.goBack();
        takeScreenshot("DemoModeExperiences_Malmo_Design");
        BaseDialog displayDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_Malmo_Design_Dialog_Scrolling1");
        displayDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Malmo_Design_Dialog_Scrolling2");
        displayDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Design_PremiumDesign");

        EntertainmentPage entertainmentPage = designPage.nextExperience(EntertainmentPage.class);
        takeScreenshot("DemoModeExperiences_Malmo_Entertainment");
        BaseDialog flexViewDialog = entertainmentPage.openDialog();
        takeScreenshot("DemoModeExperiences_Malmo_ROW_Entertainment_Dialog_Scrolling1");
        flexViewDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Malmo_ROW_Entertainment_Dialog_Scrolling2");
        flexViewDialog.closeDialog();
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Entertainment_Ultrawide");
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Entertainment_DolbyAtmos");

        CameraPage cameraPage = entertainmentPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Malmo_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_NightVision");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_GoogleAutoEnhance");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_Selfie");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_PhotoBooth");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Malmo_Camera_TestCamera");

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
