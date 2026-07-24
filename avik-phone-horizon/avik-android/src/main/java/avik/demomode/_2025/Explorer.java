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
import avik.demomode.pages.HomePage;
import avik.demomode.pages.MotoAIPage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class Explorer {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Explorer";
    private final int TOTAL_SCROLLS_TECHSPECS = 8;
    private final int TOTAL_SCROLLS_DISCLAIMERS = 7;

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
        takeScreenshot("DemoModeExperiences_Explorer_Design");
        BaseDialog designDialog = designPage.openDialog();
        designDialog.captureScroll(avikHandler, MODEL_NAME, "Design");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Design_Perfection");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Design_Materials");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Design_Curated");

        CameraPage cameraPage = designPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Explorer_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Camera_Sensor");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Camera_Ultrawide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Camera_SuperZoom");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Camera_HighRes");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Explorer_Camera_Validated");

        MotoAIPage motoaiPage = cameraPage.nextExperience(MotoAIPage.class);
        takeScreenshot("DemoModeExperiences_Explorer_MotoAI");
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
