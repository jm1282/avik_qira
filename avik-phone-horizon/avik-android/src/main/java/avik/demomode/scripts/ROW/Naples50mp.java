package avik.demomode.scripts.ROW;

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
import avik.demomode.pages.DisplayPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class Naples50mp {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String MODEL_NAME = "Naples50MP";
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
        HomePage homePage = mDemoMode.callApp();

        DisplayPage displayPage = homePage.openExperiences(DisplayPage.class);
        takeScreenshot("DemoModeExperiences_Naples50MP_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        displayDialog.captureScroll(avikHandler, "Naples50MP_ROW", "Display");
        displayDialog.closeDialog();
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Display_SuperHD");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Display_AMOLED");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Display_Brightness");

        CameraPage cameraPage = displayPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera_50MP");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera_LowLight");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera_UltraWide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Camera_Selfie");

        DesignPage designPage = displayPage.nextExperience(DesignPage.class);
        takeScreenshot("DemoModeExperiences_Naples50MP_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, MODEL_NAME, "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Design_Slim");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Design_Premium");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Design_IP64");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples50MP_Design_GorillaGlass");

        SpecsPage specsPage = displayPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, "Naples50MP_ROW", TOTAL_SCROLLS_TECHSPECS);

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