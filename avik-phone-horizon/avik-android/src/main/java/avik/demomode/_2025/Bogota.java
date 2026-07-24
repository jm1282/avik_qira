package avik.demomode._2025;

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
import avik.demomode.pages.DisclaimerPage;
import avik.demomode.pages.EntertainmentPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class Bogota {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Bogota";
    private final int TOTAL_SCROLLS_TECHSPECS = 6;
    private final int TOTAL_SCROLLS_DISCLAIMERS = 5;

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

        mDemoMode.fixExecutionLanguage();
        HomePage homePage = mDemoMode.callApp();

        DesignPage designPage = homePage.openExperiences(DesignPage.class);
        takeScreenshot("DemoModeExperiences_Bogota_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, MODEL_NAME, "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Design_Luxury");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Design_Underwater");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Design_Durability");

        EntertainmentPage entertainmentPage = designPage.nextExperience(EntertainmentPage.class);
        takeScreenshot("DemoModeExperiences_Bogota_Entertainment");
        BaseDialog entertainmentDialog = entertainmentPage.openDialog();
        entertainmentDialog.captureScroll(avikHandler, MODEL_NAME, "Entertainment");
        entertainmentDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Entertainment_Visibility");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Entertainment_Immersive");

        CameraPage cameraPage = designPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Bogota_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Camera_50MP");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Camera_Lens");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Camera_HighRes");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Camera_Snap");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Bogota_Camera_Stunning");

        SpecsPage specsPage = entertainmentPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, MODEL_NAME, TOTAL_SCROLLS_TECHSPECS);

        DisclaimerPage disclaimerPage = specsPage.openDisclaimers();
        disclaimerPage.captureDisclaimers(avikHandler, MODEL_NAME, TOTAL_SCROLLS_DISCLAIMERS);

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
