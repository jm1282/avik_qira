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
import avik.demomode.pages.DisclaimerPage;
import avik.demomode.pages.EntertainmentPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SizzlePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class LamuLite {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "LamuLite";
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

        SizzlePage sizzlePage = mDemoMode.callAppFromHomeScreen(true);
        // takeScreenshot("DemoModeExperiences_LamuLite_Sizzle");
        HomePage homePage = sizzlePage.skipSizzle();

        EntertainmentPage entertainmentPage = homePage.openExperiences(EntertainmentPage.class);
//		entertainmentPage.openMenu();
//		takeScreenshot("DemoModeExperiences_LamuLite_Menu");
//		entertainmentPage.goBack();
        takeScreenshot("DemoModeExperiences_LamuLite_Entertainment");
        BaseDialog entertainmentDialog = entertainmentPage.openDialog();
        entertainmentDialog.captureScroll(avikHandler, MODEL_NAME, "Entertainment");
        entertainmentDialog.closeDialog();
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Entertainment_Cinematic");

        CameraPage cameraPage = entertainmentPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_LamuLite_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Camera_Main");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Camera_AI");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Camera_NightVision");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Camera_Selfie");

        DesignPage designPage = cameraPage.nextExperience(DesignPage.class);
        takeScreenshot("DemoModeExperiences_LamuLite_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, MODEL_NAME, "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LamuLite_Design_Sleek");

        SpecsPage specsPage = cameraPage.nextExperience(SpecsPage.class);
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
