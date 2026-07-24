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
import avik.demomode.pages.DisclaimerPage;
import avik.demomode.pages.DisplayPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.MotoAIPage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class OrionNA {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "OrionNA";
    private final int TOTAL_SCROLLS_TECHSPECS = 7;
    private final int TOTAL_SCROLLS_DISCLAIMERS = 6;

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
        takeScreenshot("DemoModeExperiences_OrionNA_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        displayDialog.captureScroll(avikHandler, MODEL_NAME, "Display");
        displayDialog.closeDialog();
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Display_Reinforced");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Display_Interactive");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Display_Access");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Display_Panels");
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Display_First");

        CameraPage cameraPage = displayPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_OrionNA_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Camera_Quickly");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Camera_HighRes");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Camera_Selfies");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Camera_Stabilization");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_OrionNA_Camera_Camcorder");

        MotoAIPage motoaiPage = cameraPage.nextExperience(MotoAIPage.class);
        takeScreenshot("DemoModeExperiences_OrionNA_MotoAI");
        BaseDialog motoaiDialog = motoaiPage.openDialog();
        motoaiDialog.captureScroll(avikHandler, MODEL_NAME, "MotoAI");
        motoaiDialog.closeDialog();


        SpecsPage specsPage = motoaiPage.nextExperience(SpecsPage.class);
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
