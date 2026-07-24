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
import avik.demomode.pages.BatteryPage;
import avik.demomode.pages.CameraPage;
import avik.demomode.pages.DisclaimerPage;
import avik.demomode.pages.DurabilityPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class EquatorROW {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String MODEL_NAME = "Equator";
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

        CameraPage cameraPage = homePage.openExperiences(CameraPage.class);
        takeScreenshot("DemoModeExperiences_Equator_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Camera_Perfection");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Camera_Videos");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Camera_Ultrawide");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Camera_FrontCamera");
        cameraPage.nextExperienceCard();

        BatteryPage batteryPage = cameraPage.nextExperience(BatteryPage.class);
        takeScreenshot("DemoModeExperiences_Equator_ROW_Battery");
        BaseDialog batteryDialog = batteryPage.openDialog();
        batteryDialog.captureScroll(avikHandler, MODEL_NAME, "Battery");
        batteryDialog.closeDialog();
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Battery_Playback");
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Battery_Day");
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Battery_Wireless");

        DurabilityPage durabilityPage = cameraPage.nextExperience(DurabilityPage.class);
        takeScreenshot("DemoModeExperiences_Equator_Durability");
        BaseDialog durabilityDialog = durabilityPage.openDialog();
        durabilityDialog.captureScroll(avikHandler, MODEL_NAME, "Durability");
        durabilityDialog.closeDialog();
        durabilityPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Durability_Aluminum");
        durabilityPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Equator_Durability_Better");

        SpecsPage specsPage = durabilityPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, "Equator_ROW", TOTAL_SCROLLS_TECHSPECS);

        DisclaimerPage disclaimerPage = specsPage.openDisclaimers();
        disclaimerPage.captureDisclaimers(avikHandler, "Equator_ROW", TOTAL_SCROLLS_DISCLAIMERS);
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