package avik.SETUP.auto;

import android.widget.Switch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 24 Execution time: ~4m
 *
 * Initial Setup:
 * 1. Go through the Setup flow first and:
 * 1.1. Set up the Wi-Fi;
 * 1.2. Configure a Google Account (i.e. cinauto2014@gmail.com);
 * 2. Install the InvisibleIME app and set its keyboard as the main input;
 * 3. Go back to Google Services screen and expand all items
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_FinalScreens_Delta {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    // In current scope: 24-28
    private AvikScreenshotAction Setup_SetAPIN;
    private AvikScreenshotAction Setup_SkipScreenLock_Dialog;
    private AvikScreenshotAction Setup_Fingerprint_Skip_Dialog;
    
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch;
    private AvikScreenshotAction Setup_Motorola_StayInTheKnow;
    private AvikScreenshotAction Setup_Motorola_OptOut_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterEmail;
    private AvikScreenshotAction Setup_Motorola_EnterValidEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_OptOutEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterCPF;
    private AvikScreenshotAction Setup_Motorola_UseOfCPF_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterValidCPF_Dialog;
    
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch_AreYouSure_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip;
    private AvikScreenshotAction Setup_Motorola_AreYouSure_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAllSet_Buttons;
    private AvikScreenshotAction Setup_Motorola_YoureAllSet_Gestures;
    private AvikScreenshotAction Setup_ChooseYourTheme_Light;
    private AvikScreenshotAction Setup_ChooseYourTheme_Dark;
    private AvikScreenshotAction Setup_ChooseNavigation_Gestures;
    private AvikScreenshotAction Setup_ChooseNavigation_Buttons;
    
    private AvikScreenshotAction Setup_AccountAdded_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountAdded_pureGMS", false);
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreFromBackup_pureGMS", false);
    private AvikScreenshotAction Setup_GoogleServices_pureGMS;
    private AvikScreenshotAction Setup_ScreenLockOptions_Dialog; 
    private AvikScreenshotAction Setup_ContinueSetup_pureGMS;
    private AvikScreenshotAction Setup_YoutubeMusic_pureGMS;
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS_Scrolling1", false);
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS_Scrolling2", false);
    private AvikScreenshotAction Setup_GooglePay_pureGMS; 
    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS; 
    private AvikScreenshotAction Setup_AnythingElse_pureGMS;
    private AvikScreenshotAction Setup_ReviewAdditionalApps_pureGMS = new AndroidAvikScreenshotAction("Setup_ReviewAdditionalApps_pureGMS", true);
    private AvikScreenshotAction Setup_AnythingElseDoneForNow_pureGMS;
    
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS;
    
    private AvikScreenshotAction Setup_DigitalSecure_VZW_Scrolling1 = new AndroidAvikScreenshotAction("Setup_DigitalSecure_VZW_Scrolling1", true);
    private AvikScreenshotAction Setup_VerizonCloud_VZW = new AndroidAvikScreenshotAction("Setup_VerizonCloud_VZW", true);

    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();

        Setup_AccountAdded_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountAdded_pureGMS", false);
        Setup_GoogleServices_pureGMS = new AndroidAvikScreenshotAction("Setup_GoogleServices_pureGMS", false);
        Setup_ScreenLockOptions_Dialog = new AndroidAvikScreenshotAction("Setup_ScreenLockOptions_Dialog", true);
        Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", true);
        Setup_SkipScreenLock_Dialog = new AndroidAvikScreenshotAction("Setup_SkipScreenLock_Dialog", true);
        Setup_ContinueSetup_pureGMS = new AndroidAvikScreenshotAction("Setup_ContinueSetup_pureGMS", false);
        Setup_YoutubeMusic_pureGMS = new AndroidAvikScreenshotAction("Setup_YoutubeMusic_pureGMS", false);
        Setup_GooglePay_pureGMS = new AndroidAvikScreenshotAction("Setup_GooglePay_pureGMS", false);
        Setup_AccessYourAssistant_pureGMS = new AndroidAvikScreenshotAction("Setup_AccessYourAssistant_pureGMS", false);
        Setup_AnythingElse_pureGMS = new AndroidAvikScreenshotAction("Setup_AnythingElse_GMS_Scrolling1", true);
        Setup_AnythingElseDoneForNow_pureGMS = new AndroidAvikScreenshotAction("Setup_AnythingElseDoneForNow_pureGMS", false);
        Setup_Motorola_LetsStayInTouch = new AndroidAvikScreenshotAction("Setup_Motorola_LetsStayInTouch", true);
        Setup_Motorola_StayInTheKnow = new AndroidAvikScreenshotAction("Setup_Motorola_StayInTheKnow", true);
        Setup_Motorola_OptOut_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_OptOut_Dialog", true);
        Setup_Motorola_EnterEmail = new AndroidAvikScreenshotAction("Setup_Motorola_EnterEmail", true);
        Setup_Motorola_EnterValidEmail_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_EnterValidEmail_Dialog", true);
        Setup_Motorola_OptOutEmail_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_OptOutEmail_Dialog", true);
        Setup_Motorola_EnterCPF = new AndroidAvikScreenshotAction("Setup_Motorola_EnterCPF", true);
        Setup_Motorola_UseOfCPF_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_UseOfCPF_Dialog", true);
        Setup_Motorola_EnterValidCPF_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_EnterValidCPF_Dialog", true);
        
        Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog", true);
        Setup_Motorola_LetsStayInTouch_AreYouSure_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_LetsStayInTouch_AreYouSure_Dialog", true);
        Setup_Motorola_YoureAlmostThere = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAlmostThere", true);
        Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog", true);
        Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip", true);
        Setup_Motorola_AreYouSure_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_AreYouSure_Dialog", true);
        Setup_Motorola_YoureAllSet_Buttons = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAllSet_Buttons", true);
        Setup_TeachYourAssistant_pureGMS = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS", false);
        
        Setup_ChooseYourTheme_Light = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Light", true);
        Setup_ChooseYourTheme_Dark = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Dark", true);
        Setup_ChooseNavigation_Gestures = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Gestures", true);
        Setup_ChooseNavigation_Buttons = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Buttons", true);
       
        
        
    }

    @After
    public void tearDown() throws Exception {

    }



    private void captureSetupScreens() throws Exception {
        logger.info("===== Capturing Setup Screens =====");

        mUtils.sleep(Constants.FIVE_SECONDS);
       
        mUtility.takeAvikScreenshotWithFlag(Setup_ReviewAdditionalApps_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);

    }

    @Test
    public void testMain() {
        try {
            //captureBackup();
            captureSetupScreens();
        	//captureLetsStayInTouchScreens();

        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
