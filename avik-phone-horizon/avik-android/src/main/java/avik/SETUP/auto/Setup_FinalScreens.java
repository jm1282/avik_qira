package avik.SETUP.auto;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import android.widget.Switch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;
import java.util.regex.Pattern;

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
public class Setup_FinalScreens {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    // 3 Ladin (PIN screens)
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

    // 6 ladin
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch_AreYouSure_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip;
    private AvikScreenshotAction Setup_Motorola_AreYouSure_Dialog;

    //private AvikScreenshotAction Setup_Motorola_YoureAllSet_Buttons;
    private AvikScreenshotAction Setup_Motorola_YoureAllSet_Gestures;
    private AvikScreenshotAction Setup_ChooseYourTheme_Light;
    private AvikScreenshotAction Setup_ChooseYourTheme_Dark;
    //2
    private AvikScreenshotAction Setup_ChooseNavigation_Gestures;
    private AvikScreenshotAction Setup_ChooseNavigation_Buttons;
    
    private AvikScreenshotAction Setup_AccountAdded_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountAdded_pureGMS", true);
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreFromBackup_pureGMS", true);
    private AvikScreenshotAction Setup_GoogleServices_pureGMS;
    private AvikScreenshotAction Setup_ScreenLockOptions_Dialog; 
    private AvikScreenshotAction Setup_ContinueSetup_pureGMS;
    private AvikScreenshotAction Setup_YoutubeMusic_pureGMS;
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS_Scrolling1", false);
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS_Scrolling2", false);
    private AvikScreenshotAction Setup_GooglePay_pureGMS;
    private AvikScreenshotAction Setup_GooglePay_InsertCreditCard_pureGMS = new AndroidAvikScreenshotAction("Setup_GooglePay_InsertCreditCard_pureGMS", true);
    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS;
    private AvikScreenshotAction Setup_AnythingElse_pureGMS;
    private AvikScreenshotAction Setup_ReviewAdditionalApps_pureGMS = new AndroidAvikScreenshotAction("Setup_ReviewAdditionalApps_pureGMS", false);
    private AvikScreenshotAction Setup_AnythingElseDoneForNow_pureGMS;
    
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS;
    
    private AvikScreenshotAction Setup_DigitalSecure_VZW_Scrolling1 = new AndroidAvikScreenshotAction("Setup_DigitalSecure_VZW_Scrolling1", true);
    private AvikScreenshotAction Setup_VerizonCloud_VZW = new AndroidAvikScreenshotAction("Setup_VerizonCloud_VZW", true);

    private AvikScreenshotAction Setup_GoogleBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_GoogleBackup_pureGMS", true);
    private AvikScreenshotAction Setup_GoogleBackup_HowProtected_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_GoogleBackup_HowProtected_Dialog_pureGMS", true);

    private AvikScreenshotAction Setup_NearbyShare_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_NearbyShare_pureGMS_Scrolling1", true);
    private AvikScreenshotAction Setup_NearbyShare_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_NearbyShare_pureGMS_Scrolling2", true);
    private AvikScreenshotAction Setup_NearbyShare_LearnMore_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_NearbyShare_LearnMore_Dialog_pureGMS", true);

    private AvikScreenshotAction Setup_ChatWithGemini_PureGMS = new AndroidAvikScreenshotAction("Setup_ChatWithGemini_PureGMS", true);
    private AvikScreenshotAction Setup_MeetGemini_PureGMS = new AndroidAvikScreenshotAction("Setup_MeetGemini_PureGMS", true);
    private AvikScreenshotAction Setup_HandsFreeGemini_PureGMS = new AndroidAvikScreenshotAction("Setup_HandsFreeGemini_PureGMS", true);
    private AvikScreenshotAction Setup_HandsFreeGemini_Expanded_PureGMS = new AndroidAvikScreenshotAction("Setup_HandsFreeGemini_Expanded_PureGMS", true);

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
        Setup_GooglePay_pureGMS = new AndroidAvikScreenshotAction("Setup_GooglePay_pureGMS", true);
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
        //Setup_Motorola_YoureAllSet_Buttons = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAllSet_Buttons", true);
        Setup_TeachYourAssistant_pureGMS = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS", false);
        
        Setup_ChooseYourTheme_Light = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Light", true);
        Setup_ChooseYourTheme_Dark = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Dark", true);
        Setup_ChooseNavigation_Gestures = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Gestures", true);
        Setup_ChooseNavigation_Buttons = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Buttons", true);
       
        
        
    }

    @After
    public void tearDown() throws Exception {

    }

    private void captureBackup() throws Exception {

        while(!mDevice.hasObject(By.res("com.google.android.setupwizard:id/sud_layout_subtitle"))) {
            mDevice.pressBack();
            mUtils.sleep(Constants.FIVE_SECONDS);
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_AccountAdded_pureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();

        mUtils.sleep(Constants.TWENTY_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_RestoreFromBackup_pureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
    }

    private void captureSetupScreens() throws Exception {
        logger.info("===== Capturing Setup Screens =====");

        /*
        while (!mDevice.hasObject(By.res("com.google.android.gms:id/sud_items_title"))) {
            logger.info("==== GO TO CORRECT SCREEN ====");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }

        mUtils.sleep(Constants.TEN_SECONDS);
        //expandAll();
       
        //mUtility.scrollAndCaptureWithFlag(mUtils.createScrollable(), 5, Setup_GoogleServices_pureGMS );
        mUtils.sleep(Constants.HALF_SECOND);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.HALF_SECOND);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.HALF_SECOND);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.HALF_SECOND);
        */

        while (!mDevice.hasObject(By.res("com.android.settings:id/screen_lock_options"))) {
            logger.info("==== GO TO CORRECT SCREEN ====");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }
       
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObject(By.res("com.android.settings:id/screen_lock_options")).click();
        mUtils.sleep(Constants.ONE_SECOND);
       
        mUtility.takeAvikScreenshotWithFlag(Setup_ScreenLockOptions_Dialog);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.pressBack();
        mUtils.sleep(Constants.FIVE_SECONDS);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
       
        mUtility.takeAvikScreenshotWithFlag(Setup_SkipScreenLock_Dialog);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);
        //mDevice.pressBack();
        //mUtils.sleep(Constants.THREE_SECONDS);
        /*
        mDevice.findObject(By.res("android:id/button1")).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        // nao aparece caso n tenha conta logada
        mUtility.takeAvikScreenshotWithFlag(Setup_ContinueSetup_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        do{
    	    mUtils.sleep(Constants.ONE_SECOND);
        }while(!mDevice.hasObject(By.clazz("android.widget.Button")));
       

        mUtility.scrollAndCaptureWithFlag(mUtils.createScrollable(), 2, Setup_AccessYourAssistant_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
      
        // only in select locales (not available in en-XM)
       
        mUtility.scrollAndCaptureWithFlag(mUtils.createScrollable(), 2, Setup_TeachYourAssistant_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.THREE_SECONDS);
       
        mUtility.takeAvikScreenshotWithFlag(Setup_AccessWithoutUlocking_pureGMS_Scrolling1);
        mUtils.sleep(Constants.HALF_SECOND);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.HALF_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_AccessWithoutUlocking_pureGMS_Scrolling2);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        */
        /*
        mUtility.takeAvikScreenshotWithFlag(Setup_GooglePay_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        */

        /*
        mUtility.takeAvikScreenshotWithFlag(Setup_YoutubeMusic_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_ReviewAdditionalApps_pureGMS);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.TEN_SECONDS);
        */
        /*
        mUtility.takeAvikScreenshotWithFlag(Setup_AnythingElse_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObjects(By.res("com.google.android.setupwizard:id/suggested_item")).get(3).click();
        mUtils.sleep(Constants.ONE_SECOND);
       
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
       
        mUtility.takeAvikScreenshotWithFlag(Setup_AnythingElseDoneForNow_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
       
        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        */
    }
    
    private void captureLetsStayInTouchScreens() throws Exception {

        while (!mDevice.hasObject(By.res("com.motorola.setup:id/moto_crm_tailored"))) {
            mUtils.sleep(Constants.TEN_SECONDS);
            logger.info("------SWITCH TO CORRECT SCREEN------");
        }

    	logger.info("------CHECK SWITCHES------");
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	
    	
		
		mUtility.scrollListAndCapture(Setup_Motorola_LetsStayInTouch, mUtils.createScrollable(), 2);

		if (mDevice.hasObject(By.clazz(Switch.class.getName()).checked(true))) {
			mDevice.findObject(By.clazz(Switch.class.getName())).click();
			mUtils.sleep(Constants.ONE_SECOND);
		}
		
		mUtils.sleep(Constants.ONE_SECOND);
		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_add_email")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		mDevice.findObject(By.res("com.motorola.setup:id/add_email")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog);
		mUtils.sleep(Constants.HALF_SECOND);
		
		mDevice.findObject(By.res("android:id/button2")).click();
		mUtils.sleep(Constants.ONE_SECOND);

		String nextButton = mUtils.getResourceByPackAndStringKey("com.motorola.setup", "next");
		mDevice.findObject(By.text(nextButton)).click();
		mUtils.sleep(Constants.ONE_SECOND);
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_LetsStayInTouch_AreYouSure_Dialog);
		
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);		
		mDevice.findObject(By.text(nextButton)).click();
		mUtils.sleep(Constants.ONE_SECOND);
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAlmostThere);
		mUtils.sleep(Constants.HALF_SECOND);

		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_cpf_info")).click();
		mUtils.sleep(Constants.ONE_SECOND);

		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog);
		
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);

		mDevice.findObject(By.res("com.motorola.setup:id/next_button")).click();
		mUtils.sleep(Constants.ONE_SECOND);

		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_cpf")).click();
		mUtils.sleep(Constants.ONE_SECOND);

		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip);
		mUtils.sleep(Constants.HALF_SECOND);
		
		mDevice.findObject(By.res("com.motorola.setup:id/skip_button")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_AreYouSure_Dialog);
		mUtils.sleep(Constants.HALF_SECOND);
		
		//mDevice.findObject(By.res("android:id/button1")).click();
		//mUtils.sleep(Constants.ONE_SECOND);

	}

    private void captureFinalScreens() throws Exception{
    	
    	while(!mDevice.hasObject(By.res("com.motorola.coresettingsext:id/animationView"))){
    		logger.info("-----------CHECK SCREEN-----------");
    		mUtils.sleep(Constants.FIVE_SECONDS);
        	
    	}
    	if(mDevice.hasObject(By.text("Avik Client"))) {
    		mDevice.pressBack();
    		mUtils.sleep(Constants.THREE_SECONDS);
    	}
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseYourTheme_Light);
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/secondary_option")).click();
    	mUtils.sleep(Constants.THREE_SECONDS);
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseYourTheme_Dark);
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/primary_option")).click();
    	mUtils.sleep(Constants.THREE_SECONDS);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/next_button")).click();
    	mUtils.sleep(Constants.THREE_SECONDS);
    	
    	//navigation
    	
    	/*
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Gestures);
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/secondary_option")).click();
    	mUtils.sleep(Constants.THREE_SECONDS);
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Buttons);
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/primary_option")).click();
    	mUtils.sleep(Constants.THREE_SECONDS);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/next_button")).click();
    	mUtils.sleep(Constants.ONE_SECOND);
    	*/
    	
    	//mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAllSet_Buttons); //CAPTURAR TODOS EXCETO EN_GB E ZH-CN
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    }
    
    private void captureFinal() throws Exception{
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/next_button")).click();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	while(mDevice.getCurrentPackageName().matches("com.android.settings")) {
    		logger.info("-----------CHECK SCREEN VA PARA O ALL SET-----------");
    		mUtils.sleep(Constants.FIVE_SECONDS);
    	}
    	while(!mDevice.hasObject(By.res("com.motorola.coresettingsext:id/header_device_setup_all_set"))){
    		logger.info("-----------CHECK SCREEN E VA PARA O ALL SET-----------");
    		mUtils.sleep(Constants.FIVE_SECONDS);
        	
    	}
		mDevice.wait(Until.gone(By.res("com.android.settings:id/usb_bottom_close")), Constants.FIVE_SECONDS);
    	//com.motorola.coresettingsext/.setup.DeviceSetupAllSetActivity
    	//mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAllSet_Buttons); //CAPTURAR TODOS EXCETO EN_GB E ZH-CN
    	mUtils.sleep(Constants.HALF_SECOND);
    	mDevice.pressHome();
    }
    
    private void expandAll() throws Exception {
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_backup")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_location_service")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_wireless_scan_always_mode")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_usage_reporting")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);    	
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/google_services_agreement")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mUtils.createScrollable().scrollBackward(5);
    }
    
    public void captureVZW() throws Exception {
    	mUtils.sleep(Constants.TEN_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_VerizonCloud_VZW);
    }

    public void captureNearbyShare() throws Exception {
        while (!mDevice.hasObject(By.res("com.google.android.gms:id/sud_account_name"))) {
            logger.info("GO TO NEARBY SHARE");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_NearbyShare_pureGMS_Scrolling1);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_NearbyShare_pureGMS_Scrolling2);
        mUtils.swipeFromCenterToBottom();
        mUtility.takeAvikScreenshotWithFlag(Setup_NearbyShare_LearnMore_Dialog_pureGMS);
    }

    public void captureGooglePay() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);

        while (!mDevice.hasObject(By.text(Pattern.compile(".*(Google Pay|Google Pay).*")))) {
            logger.info("GO TO GOOGLE PAY SCREEN");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }
        mUtility.takeAvikScreenshotWithFlag(Setup_GooglePay_pureGMS);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        while (!mDevice.hasObject(By.res("com.google.android.gms:id/card_images"))) {
            logger.info("Waiting for credit card screen");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_GooglePay_InsertCreditCard_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.FIVE_SECONDS);

    }

    public void captureGoogleBackup() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.click(100, 200);
        mUtility.takeAvikScreenshotWithFlag(Setup_GoogleBackup_pureGMS);
        mUtility.takeAvikScreenshotWithFlag(Setup_GoogleBackup_HowProtected_Dialog_pureGMS);
    }

    public void captureGemini() throws Exception {
        // For devices before A15.R2, Setup_ChatWithGemini_PureGMS may be present instead
        //mUtility.takeAvikScreenshotWithFlag(Setup_ChatWithGemini_PureGMS);

        while (!mDevice.hasObject(By.res("com.google.android.googlequicksearchbox:id/assistant_robin_suw_intro_animation_1"))) {
            mUtils.sleep(Constants.FIVE_SECONDS);
            logger.info("--- GO TO TARGET SCREEN ---");
        }

        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_MeetGemini_PureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_HandsFreeGemini_PureGMS);

        mDevice.findObject(By.res("com.google.android.googlequicksearchbox:id/assistant_robin_gemini_tos_item_arrow_container")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_HandsFreeGemini_Expanded_PureGMS);

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
    }
    @Test
    public void testMain() {
        try {
            //captureBackup();
            //captureSetupScreens();
        	//captureLetsStayInTouchScreens();
            //captureNearbyShare();
            //captureGooglePay();
            //captureGoogleBackup();
            captureGemini();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
