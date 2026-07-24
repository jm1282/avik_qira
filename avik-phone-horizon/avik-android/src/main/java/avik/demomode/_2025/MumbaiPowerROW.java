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
public class MumbaiPowerROW {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String MODEL_NAME = "MumbaiPower";
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

        BatteryPage batteryPage = homePage.openExperiences(BatteryPage.class);
        takeScreenshot("DemoModeExperiences_MumbaiPower_Battery");
        BaseDialog batteryDialog = batteryPage.openDialog();
        batteryDialog.captureScroll(avikHandler, MODEL_NAME, "Battery");
        batteryDialog.closeDialog();
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Battery_Music");
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Battery_Video");
        batteryPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Battery_Power");

        CameraPage cameraPage = batteryPage.nextExperience(CameraPage.class);
        takeScreenshot("DemoModeExperiences_MumbaiPower_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        cameraDialog.captureScroll(avikHandler, MODEL_NAME, "Camera");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Camera_Night");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Camera_Portraits");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Camera_PhotoBooth");
        cameraPage.nextExperienceCard();

        DurabilityPage durabilityPage = cameraPage.nextExperience(DurabilityPage.class);
        takeScreenshot("DemoModeExperiences_MumbaiPower_Durability");
        BaseDialog durabilityDialog = durabilityPage.openDialog();
        durabilityDialog.captureScroll(avikHandler, MODEL_NAME, "Durability");
        durabilityDialog.closeDialog();
        durabilityPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Durability_WetFingers");
        durabilityPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Durability_Touch");
        durabilityPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_MumbaiPower_Durability_Pantone");

        SpecsPage specsPage = cameraPage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, "MumbaiPower_ROW", TOTAL_SCROLLS_TECHSPECS);

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
