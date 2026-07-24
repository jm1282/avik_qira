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
import avik.demomode.pages.DisplayPage;
import avik.demomode.pages.CameraPage;
import avik.demomode.pages.DisclaimerPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.PerformancePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class NevadaNA {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String MODEL_NAME = "NevadaNA";
    private final int TOTAL_SCROLLS_TECHSPECS = 7;
    private final int TOTAL_SCROLLS_DISCLAIMERS = 7;
    private final boolean HAS_SIZZLE = false;
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;

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
        HomePage homePage = mDemoMode.callAppFromHomeScreen(HAS_SIZZLE);

        DisplayPage displayPage = homePage.openExperiences(DisplayPage.class);
        takeScreenshot("DemoModeExperiences_NevadaNA_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        displayDialog.captureScroll(avikHandler, MODEL_NAME, "Display");
        displayDialog.closeDialog();
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Display_Bright");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Display_RefreshRate");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Display_Immersive");

        PerformancePage performancePage = displayPage.nextExperience(PerformancePage.class);
        takeScreenshot("DemoModeExperiences_NevadaNA_Performance");
        BaseDialog performanceDialog = performancePage.openDialog();
        performanceDialog.captureScroll(avikHandler, MODEL_NAME, "Performance");
        performanceDialog.closeDialog();
        performancePage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Performance_Seamless");
        performancePage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Performance_Powerful");
        performancePage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Performance_Battery");

        CameraPage cameraPage = homePage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_NevadaNA_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Camera_NightVision");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_NevadaNA_Camera_PhotoBooth");
        cameraPage.nextExperienceCard();


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
