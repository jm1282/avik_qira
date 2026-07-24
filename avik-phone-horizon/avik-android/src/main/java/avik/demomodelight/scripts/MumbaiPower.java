package avik.demomodelight.scripts;

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

import avik.demomodelight.pages.DisclaimersPage;
import avik.demomodelight.pages.MainPage;
import avik.demomodelight.pages.TechSpecsPage;
import avik.demomodelight.utils.DemoModeLight;

@RunWith(JUnit4.class)
public class MumbaiPower {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeLight mDemoModeLight;
    private final String MODEL_NAME = "MumbaiPower";
    private final int TOTAL_SCROLLS_TECHSPECS = 3;
    private final int TOTAL_SCROLLS_DISCLAIMERS = 2;

    @Before
    public void setUp() throws Exception {
        mUtils = AvikUtility.getInstance();
        mDemoModeLight = new DemoModeLight();

        mDemoModeLight.clearApp();
        mUtils.pressBackKeySeveralTimes(5);
    }

    @After
    public void tearDown() throws Exception {
        mDemoModeLight.forceCloseApp();
        mUtils.pressBackKeySeveralTimes(5);
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

    public void captureAvikScreen(String screeName) {
        avikHandler.takeScreenshot(screeName, true, true);
    }

    public void captureScreens() throws Exception {
        MainPage mainPage = mDemoModeLight.callApp();
        captureAvikScreen("DemoModeExperiencesLight_MumbaiPower_Battery");
        mainPage.nextExperienceCard();
        captureAvikScreen("DemoModeExperiencesLight_MumbaiPower_Camera");
        mainPage.nextExperienceCard();
        captureAvikScreen("DemoModeExperiencesLight_MumbaiPower_Durability");

        TechSpecsPage techSpecsPage = mainPage.openSpecs();
        techSpecsPage.captureSpecs(avikHandler, MODEL_NAME, TOTAL_SCROLLS_TECHSPECS);

        DisclaimersPage disclaimersPage = techSpecsPage.openDisclaimers();
        disclaimersPage.captureDisclaimers(avikHandler, MODEL_NAME, TOTAL_SCROLLS_DISCLAIMERS);
    }

}

