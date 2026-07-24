package avik.SETUP.auto;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.impl.LocaleEnum;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 19 Execution time: 2m 35s
 *
 * Initial Setup:
 * 1. Connect wifi
 * 2. Install invisible IME
 * 3. Set device do first privacy screen
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_CopyAppsData {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private UiDevice mDevice;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private Setup mSetup;

    private AvikScreenshotAction Setup_Motorola_PrivacyAndSecurity = new AndroidAvikScreenshotAction("Setup_Motorola_PrivacyAndSecurity", true);

    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly", true);
    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport", true);
    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts", true);
    private AvikScreenshotAction Setup_Motorola_Privacy = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy", true);
    
    private AvikScreenshotAction Setup_CopyAppsAndData_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyAppsAndData_pureGMS", true);
    private AvikScreenshotAction Setup_RestoreWithoutOldPhone_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreWithoutOldPhone_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_UseYourOldPhone_pureGMS = new AndroidAvikScreenshotAction("Setup_UseYourOldPhone_pureGMS", true);
    private AvikScreenshotAction Setup_CopyAnotherWay_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyAnotherWay_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_FindYourOldPhoneCable_pureGMS = new AndroidAvikScreenshotAction("Setup_FindYourOldPhoneCable_pureGMS", true);
    private AvikScreenshotAction Setup_InsertCableOldPhone_pureGMS = new AndroidAvikScreenshotAction("Setup_InsertCableOldPhone_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectBothPhones_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectBothPhones_pureGMS", true);
    private AvikScreenshotAction Setup_CopyWithoutCable_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyWithoutCable_pureGMS", true);

    private AvikScreenshotAction Setup_TransferDataWirelessly_pureGMS = new AndroidAvikScreenshotAction("Setup_TransferDataWirelessly_pureGMS", true);
    private AvikScreenshotAction Setup_OnYourOldAndroid_pureGMS = new AndroidAvikScreenshotAction("Setup_OnYourOldAndroid_pureGMS", true);
    private AvikScreenshotAction Setup_OnOtherDevice_pureGMS = new AndroidAvikScreenshotAction("Setup_OnOtherDevice_pureGMS", true);
    private AvikScreenshotAction Setup_AllowNearby_pureGMS = new AndroidAvikScreenshotAction("Setup_AllowNearby_pureGMS", true);
    private AvikScreenshotAction Setup_AddYourAccount_pureGMS = new AndroidAvikScreenshotAction("Setup_AddYourAccount_pureGMS", true);
    private AvikScreenshotAction Setup_CopyYourDataIOS_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyYourDataIOS_pureGMS", true);
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreFromBackup_pureGMS", true);

    private AvikScreenshotAction Setup_ConnectToMobileNetwork_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS", true);

    private AvikScreenshotAction Setup_CopyFromiPhone_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromiPhone_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectDevices_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectDevices_pureGMS", true);
    private AvikScreenshotAction Setup_CopyWihthoutCable_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyWihthoutCable_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_CopyFromIphone_ScanQR_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromIphone_ScanQR_pureGMS", true);
    private AvikScreenshotAction Setup_CopyFromIphone_Steps_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromIphone_Steps_pureGMS", true);

    /*
    private AvikScreenshotAction Setup_BringYourDataFrom_pureGMS = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly", false);
    private AvikScreenshotAction Setup_BringPhotosAndVideos_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly", false);
    */

    private AvikScreenshotAction Setup_GettingYourPhoneReady_pureGMS = new AndroidAvikScreenshotAction("Setup_GettingYourPhoneReady_pureGMS", true);

    private AvikScreenshotAction Setup_OpenGoogleApp_pureGMS = new AndroidAvikScreenshotAction("Setup_OpenGoogleApp_pureGMS", true);
    private AvikScreenshotAction Setup_GooglePreInstalled_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_GooglePreInstalled_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_KeepGoogleAppOpen_pureGMS = new AndroidAvikScreenshotAction("Setup_KeepGoogleAppOpen_pureGMS", true);
    private AvikScreenshotAction Setup_TypeInSearchBox_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_TypeInSearchBox_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_VerifyMyPhone_pureGMS = new AndroidAvikScreenshotAction("Setup_VerifyMyPhone_pureGMS", true);
    private AvikScreenshotAction Setup_CopyFromOldAndroid_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromOldAndroid_pureGMS", true);


    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();

        //mSetup.goToWelcomeScreen();
    }

    @After
    public void tearDown() throws Exception {
    }

    private void captureBackupScreens() throws Exception {
        logger.info("===== CAPTURING BACKUP SCREENS =====");
        while(!mDevice.hasObject(By.res("com.google.android.apps.restore:id/sud_layout_subtitle"))){
            logger.info("Wait for screen");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyAppsAndData_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_RestoreFromBackup_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

    }


    private void captureBackupScreensOLD() throws Exception {
        logger.info("===== CAPTURING BACKUP SCREENS =====");
        while(!mDevice.hasObject(By.res("com.google.android.apps.restore:id/sud_layout_subtitle"))){
            logger.info("Wait for screen");
        	mUtils.sleep(Constants.FIVE_SECONDS);
    	}
    
        mUtility.takeAvikScreenshotWithFlag(Setup_CopyAppsAndData_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_UseYourOldPhone_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_RestoreWithoutOldPhone_Dialog_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_FindYourOldPhoneCable_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_CopyAnotherWay_Dialog_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_InsertCableOldPhone_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyWithoutCable_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectBothPhones_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_TransferDataWirelessly_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        //FLOW - ANDROID PHONE
        
        // en-XM

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.TEN_SECONDS*2);
        mUtility.takeAvikScreenshotWithFlag(Setup_OnYourOldAndroid_pureGMS);
        
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_OnOtherDevice_pureGMS);
        
        mUtility.pressBackKeySeveralTimes(2);
        
        // en-US

        /*
		mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
		mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_OpenGoogleApp_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_GooglePreInstalled_Dialog_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.res("android:id/button1")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_KeepGoogleAppOpen_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_TypeInSearchBox_Dialog_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.res("android:id/button1")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_VerifyMyPhone_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObject(By.res("android:id/button2")).click();
        
        
        while(!mDevice.hasObject(By.res("com.google.android.gms:id/body"))){
        	mUtils.sleep(Constants.ONE_SECOND);
        }
        
        mUtility.takeAvikScreenshotWithFlag(Setup_OnYourOldAndroid_pureGMS);
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_OnOtherDevice_pureGMS);
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.pressBackKeySeveralTimes(4);
        */
        //FLOW -  IPHONE DEVICE
        
        mDevice.findObject(By.res("com.google.android.apps.restore:id/fragment_target_ios_wifi_tertiary_button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_AllowNearby_pureGMS);
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_AddYourAccount_pureGMS);
        
        while(!mDevice.hasObject(By.res("com.motorola.setup:id/moto_help_title"))) {
        	mDevice.pressBack();
        	mUtils.sleep(Constants.ONE_SECOND);
        }        
    }
    private void captureBackupiPhone() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyFromiPhone_pureGMS);
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectDevices_pureGMS);
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyWihthoutCable_Dialog_pureGMS);
        mUtils.skipAndroidButton1();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyFromIphone_ScanQR_pureGMS);
        mDevice.findObject(By.res("com.google.android.apps.restore:id/hotspot_qr_code_alternative")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CopyFromIphone_Steps_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

    }

    private void capturePrivacyScreens() throws Exception{
    	logger.info("===== CAPTURING FIRST PRIVACY SCREENS =====");

        mDevice.findObject(By.res("com.google.android.setupwizard:id/start")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(By.res("com.motorola.setup:id/suc_layout_title"))) {
            mDevice.findObject(By.clazz("android.widget.Button")).click();
        }
        if (mDevice.hasObject(By.res("com.google.android.gms:id/suc_layout_title"))) {
            mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        }

        mUtils.sleep(Constants.ONE_SECOND + 700);
        mUtility.takeAvikScreenshotWithFlag(Setup_GettingYourPhoneReady_pureGMS);

        while(!mDevice.hasObject(By.res("com.motorola.setup:id/moto_help_title"))) {
        	mUtils.sleep(Constants.THREE_SECONDS);
        }

        mUtility.scrollListAndCapture(Setup_Motorola_Privacy ,mUtils.createScrollable(), 2);
        //mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy);
        mUtils.sleep(Constants.HALF_SECOND);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollBackward();
        	mUtils.sleep(Constants.HALF_SECOND);
        }
        
        logger.info("dois off");
        
        String shareUsage = mUtils.getResourceByPackAndStringKey("com.motorola.setup", "moto_help_summary");
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        String customizedSupport = mUtils.getResourceByPackAndStringKey("com.motorola.setup", "moto_support_summary");
        mDevice.findObject(By.text(customizedSupport)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollForward();
        }
        
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly);
        mUtils.sleep(Constants.HALF_SECOND);
     
        logger.info("primeiro ON");
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollForward();
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport);
        mUtils.sleep(Constants.HALF_SECOND);
        
        logger.info("segundo ON");
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        mDevice.findObject(By.text(customizedSupport)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollForward();
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
    	
    }

    private void capturePrivacyAndSecurityScreens() throws Exception {
        logger.info("===== CAPTURING FIRST PRIVACY SCREENS =====");

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(By.res("com.motorola.setup:id/suc_layout_title"))) {
            mDevice.findObject(By.clazz("android.widget.Button")).click();
        }
        if (mDevice.hasObject(By.res("com.google.android.gms:id/suc_layout_title"))) {
            mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        }

        mUtils.sleep(Constants.ONE_SECOND + 700);
        mUtility.takeAvikScreenshotWithFlag(Setup_GettingYourPhoneReady_pureGMS);

        while(!mDevice.hasObject(By.res("com.motorola.setup:id/general_policy_summary"))) {
            mUtils.sleep(Constants.THREE_SECONDS);
        }

        mUtility.scrollListAndCapture(Setup_Motorola_PrivacyAndSecurity, mUtils.createScrollable(), 3);
        //mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    private void captureSIMScreens() throws Exception {
        logger.info("===== CAPTURING SIM SCREENS =====");

        /*
        mDevice.findObject(By.res("com.google.android.setupwizard:id/start")).click();
        mUtils.sleep(Constants.ONE_SECOND + 700);

        if (mDevice.hasObject(By.res("com.google.android.gms:id/suc_layout_title"))) {
            mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        }
        while(!mDevice.hasObject(By.res("com.motorola.setup:id/general_policy_summary"))) {
            mUtils.sleep(Constants.THREE_SECONDS);
        }

        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        while(!mDevice.hasObject(By.res("com.google.android.setupwizard:id/sud_layout_subtitle"))) {
            mUtils.sleep(Constants.THREE_SECONDS);
        }

        mDevice.findObject(By.clazz("android.widget.Button")).click();

        while(!mDevice.hasObject(By.res("com.google.android.euicc:id/sim_lottie_illustration"))) {
            mUtils.sleep(Constants.THREE_SECONDS);
        }
        */

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectToMobileNetwork_pureGMS);
        //Manual
        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS);
        mDevice.findObject(By.res("android:id/button2")).click();
        mUtils.sleep(Constants.ONE_SECOND);

    }


    @Test
    public void testMain() {
        try {
        	//capturePrivacyAndSecurityScreens();
            //captureSIMScreens();
            //captureBackupScreens();
            captureBackupiPhone();

        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
