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
import avik.demomode.pages.EntertainmentPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.PerformancePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class LionLite {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;

    private final String MODEL_NAME = "LionLite";
    private final int TOTAL_SCROLLS_TECHSPECS = 8;

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
        //takeScreenshot("DemoModeExperiences_LionLite_Widget");
        HomePage homePage = mDemoMode.callApp();
        takeScreenshot("DemoModeExperiences_LionLite_Discover");
        EntertainmentPage entertainmentPage = homePage.openExperiences(EntertainmentPage.class);
        entertainmentPage.openMenu();
        takeScreenshot("DemoModeExperiences_LionLite_Menu");
        entertainmentPage.goBack();
        takeScreenshot("DemoModeExperiences_LionLite_Entertainment_EMEA");
        BaseDialog entertainmentDialog = entertainmentPage.openDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Entertainment_Dialog_Scrolling1");
        entertainmentDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Entertainment_Dialog_Scrolling2");
        entertainmentDialog.closeDialog();
        entertainmentPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LionLite_Entertainment_FastRefresh");

        PerformancePage performancePage = entertainmentPage.nextExperience(PerformancePage.class);
        takeScreenshot("DemoModeExperiences_LionLite_Performance_EMEA");
        BaseDialog performanceDialog = performancePage.openDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Performance_Dialog_Scrolling1");
        performanceDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Performance_Dialog_Scrolling2");
        performanceDialog.closeDialog();

        BatteryPage designPage = performancePage.nextExperience(BatteryPage.class);
        takeScreenshot("DemoModeExperiences_LionLite_Design");
        BaseDialog designDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Design_Dialog_Scrolling1");
        designDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_LionLite_Design_Dialog_Scrolling2");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LionLite_Design_GorillaGlass");
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_LionLite_Design_IP52");

        SpecsPage specsPage = performancePage.nextExperience(SpecsPage.class);
        specsPage.captureSpecs(avikHandler, MODEL_NAME + "_EMEA", TOTAL_SCROLLS_TECHSPECS);
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
