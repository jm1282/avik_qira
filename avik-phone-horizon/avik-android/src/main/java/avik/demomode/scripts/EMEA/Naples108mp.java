package avik.demomode.scripts.EMEA;

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
public class Naples108mp {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String MODEL_NAME = "Naples108MP";
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
        HomePage homePage = mDemoMode.callAppFromAppTray(HAS_SIZZLE);

        CameraPage cameraPage = homePage.openExperiences(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera_UltraResolution");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera_Zoom");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera_Portrait");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera_UltraWide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Camera_Selfie");

        DisplayPage displayPage = cameraPage.nextExperience(DisplayPage.class);
        takeScreenshot("DemoModeExperiences_Naples108MP_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        displayDialog.captureScroll(avikHandler, "Naples108MP_EMEA", "Display");
        displayDialog.closeDialog();
        if (true)
            throw new Exception("EOE");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Display_SuperHD");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Display_AMOLED");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Display_Brightness");

        DesignPage designPage = displayPage.nextExperience(DesignPage.class);
        takeScreenshot("DemoModeExperiences_Naples108MP_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, MODEL_NAME, "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Design_Standards");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Design_Pantone");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Design_Premium");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Naples108MP_Design_Durable");

        SpecsPage specsPage = displayPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, "Naples108MP_EMEA", TOTAL_SCROLLS_TECHSPECS);

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