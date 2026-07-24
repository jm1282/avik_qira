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
import avik.demomode.pages.CameraPage;
import avik.demomode.pages.DesignPage;
import avik.demomode.pages.DisplayPage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SpecsPage;
import avik.demomode.utils.DemoModeFlutter;

@RunWith(JUnit4.class)
public class ViennaROW {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtils;
    private DemoModeFlutter mDemoMode;
    private final String MODEL_NAME = "Vienna_ROW";
    private final int TOTAL_SCROLLS_TECHSPECS = 12;

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
        HomePage homePage = mDemoMode.callAppFromHomeScreen(false);
        CameraPage cameraPage = homePage.openExperiences(CameraPage.class);
        cameraPage.openMenu();
        takeScreenshot("DemoModeExperiences_Vienna_Menu");
        cameraPage.goBack();
        takeScreenshot("DemoModeExperiences_Vienna_Camera");
        BaseDialog cameraDialog = cameraPage.openDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_Dialog_Scrolling1");
        cameraDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_Dialog_Scrolling2");
        cameraDialog.closeDialog();
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_50MP");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_Telephoto");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_PortraitMode");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_AdaptiveStabilization");
        cameraPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Camera_Selfie");

        DesignPage designPage = cameraPage.nextExperience(DesignPage.class);
        takeScreenshot("DemoModeExperiences_Vienna_Design");
        BaseDialog designDialog = designPage.openDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Design_Dialog_Scrolling1");
        designDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Design_Dialog_Scrolling2");
        designDialog.closeDialog();
        designPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Design_Pantone");

        DisplayPage displayPage = designPage.nextExperience(DisplayPage.class);
        takeScreenshot("DemoModeExperiences_Vienna_ROW_Display");
        BaseDialog displayDialog = displayPage.openDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Display_Dialog_Scrolling1");
        displayDialog.scrollDialog();
        takeScreenshot("DemoModeExperiences_Vienna_Display_Dialog_Scrolling2");
        displayDialog.closeDialog();
        displayPage.nextExperienceCard();
        takeScreenshot("DemoModeExperiences_Vienna_Display_SuperHD");

        SpecsPage specsPage = cameraPage.nextExperience(SpecsPage.class);
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
