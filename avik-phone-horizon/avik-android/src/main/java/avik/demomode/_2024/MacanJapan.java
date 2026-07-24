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
import avik.demomode.pages.BatteryPage;
import avik.demomode.pages.CameraPage;
import avik.demomode.pages.DesignPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class MacanJapan {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "MacanJP";
    private final int TOTAL_SCROLLS_TECHSPECS = 9;

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
        // takeScreenshot("DemoModeExperiences_MacanJP_Widget");
        HomePage homePage = mDemoMode.callApp();
        takeScreenshot("DemoModeExperiences_MacanJP_Discover");
        DesignPage designPage = homePage.openExperiences(DesignPage.class);
        designPage.openMenu();
        takeScreenshot("DemoModeExperiences_MacanJP_Menu");
        designPage.goBack();
        takeScreenshot("DemoModeExperiences_MacanJP_Design_Main");
        BaseDialog designDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Design_Dialog_Scrolling1");
        designDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Design_Dialog_Scrolling2");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Design_Comfortable");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Design_UnderWater");

        CameraPage cameraPage = designPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_MacanJP_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_Adaptive");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_Enhance");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_Telephoto");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_LongExposure");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Camera_TestCamera");
//		CameraT mCameraApp = cameraPage.openTestCamera();
//		takeScreenshot("DemoModeExperiences_MacanJP_Camera_GoBackOverlay");
//		cameraPage.clickGoBackOverlay();

        BatteryPage batteryPage = cameraPage.nextExperience(BatteryPage.class);
        takeScreenshot("DemoModeExperiences_MacanJP_Battery_Charging");
        BaseDialog batteryDialog = batteryPage.openDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Battery_Dialog_Scrolling1");
        batteryDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_MacanJP_Battery_Dialog_Scrolling2");
        batteryDialog.closeDialog();
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MacanJP_Battery_ChargeWireless");

        SpecsPage specsPage = batteryPage.nextExperience(SpecsPage.class);
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
