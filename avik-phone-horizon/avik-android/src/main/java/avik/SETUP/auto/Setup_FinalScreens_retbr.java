package avik.SETUP.auto;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 22 Execution time: ~4m
 *
 * Initial Setup:
 * 1. Go through the Setup flow first and:
 * 1.1. Set up the Wi-Fi;
 * 1.2. Configure a Google Account (i.e. cinauto2014@gmail.com);
 * 1.3. Skip the Voice Assistant screen.
 * 2. Install the InvisibleIME app and set its keyboard as the main input;
 * 3. Go back to the first screen (language selection);
 * 4. Run the script for all target locales, since this setup is only required once
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_FinalScreens_retbr {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    private AvikScreenshotAction Setup_GoogleServices_pureGMS;
    private AvikScreenshotAction Setup_ScreenLockOptions_Dialog; 
    private AvikScreenshotAction Setup_SetAPIN; 
    private AvikScreenshotAction Setup_SkipScreenLock_Dialog; 
    private AvikScreenshotAction Setup_ContinueSetup_pureGMS;
    private AvikScreenshotAction Setup_YoutubeMusic_pureGMS; 
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS; 
    private AvikScreenshotAction Setup_GooglePay_pureGMS; 
    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS; 
    private AvikScreenshotAction Setup_AnythingElse_pureGMS;
    private AvikScreenshotAction Setup_AnythingElseDoneForNow_pureGMS;
    
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch;
    // 10 ladin
    private AvikScreenshotAction Setup_Motorola_StayInTheKnow_Scrolling1;
    private AvikScreenshotAction Setup_Motorola_StayInTheKnow_Scrolling2;
    private AvikScreenshotAction Setup_Motorola_OptOut_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterEmail_Scrolling1;
    private AvikScreenshotAction Setup_Motorola_EnterEmail_Scrolling2;
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
    private AvikScreenshotAction Setup_Motorola_YoureAllSet_Gestures;
    private AvikScreenshotAction Setup_ChooseYourTheme_Light;
    private AvikScreenshotAction Setup_ChooseYourTheme_Dark;
    // 2 ladin
    private AvikScreenshotAction Setup_ChooseNavigation_Gestures;
    private AvikScreenshotAction Setup_ChooseNavigation_Buttons;

    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS;
    
    private AvikScreenshotAction Setup_Motorola_UseThisEmail_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_UseThisEmail_Dialog", true);
    private AvikScreenshotAction Setup_Motorola_UseThisEmail_EnterValidEmail_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_UseThisEmail_EnterValidEmail_Dialog", true);
    
    private AvikScreenshotAction Setup_DigitalSecure_VZW_Scrolling1 = new AndroidAvikScreenshotAction("Setup_DigitalSecure_VZW_Scrolling1", true);
    private AvikScreenshotAction Setup_VerizonCloud_VZW = new AndroidAvikScreenshotAction("Setup_VerizonCloud_VZW", true);
    
    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();

        Setup_GoogleServices_pureGMS = new AndroidAvikScreenshotAction("Setup_GoogleServices_pureGMS", true);
        Setup_ScreenLockOptions_Dialog = new AndroidAvikScreenshotAction("Setup_ScreenLockOptions_Dialog", true);
        Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", true);
        Setup_SkipScreenLock_Dialog = new AndroidAvikScreenshotAction("Setup_SkipScreenLock_Dialog", true);
        Setup_ContinueSetup_pureGMS = new AndroidAvikScreenshotAction("Setup_ContinueSetup_pureGMS", false);
        Setup_YoutubeMusic_pureGMS = new AndroidAvikScreenshotAction("Setup_YoutubeMusic_pureGMS", true);
        Setup_AccessWithoutUlocking_pureGMS = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS", true);
        Setup_GooglePay_pureGMS = new AndroidAvikScreenshotAction("Setup_GooglePay_pureGMS", true);
        Setup_AccessYourAssistant_pureGMS = new AndroidAvikScreenshotAction("Setup_AccessYourAssistant_pureGMS", true);
        Setup_AnythingElse_pureGMS = new AndroidAvikScreenshotAction("Setup_AnythingElse_GMS_Scrolling1", true);
        Setup_AnythingElseDoneForNow_pureGMS = new AndroidAvikScreenshotAction("Setup_AnythingElseDoneForNow_pureGMS", true);
        Setup_Motorola_LetsStayInTouch = new AndroidAvikScreenshotAction("Setup_Motorola_LetsStayInTouch", true);
        Setup_Motorola_StayInTheKnow_Scrolling1 = new AndroidAvikScreenshotAction("Setup_Motorola_StayInTheKnow_Scrolling1", true);
        Setup_Motorola_StayInTheKnow_Scrolling2 = new AndroidAvikScreenshotAction("Setup_Motorola_StayInTheKnow_Scrolling2", true);
        Setup_Motorola_OptOut_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_OptOut_Dialog", true);
        Setup_Motorola_EnterEmail_Scrolling1 = new AndroidAvikScreenshotAction("Setup_Motorola_EnterEmail_Scrolling1", true);
        Setup_Motorola_EnterEmail_Scrolling2 = new AndroidAvikScreenshotAction("Setup_Motorola_EnterEmail_Scrolling2", true);
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
        Setup_Motorola_YoureAllSet_Gestures = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAllSet_Gestures", true);
        Setup_TeachYourAssistant_pureGMS = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS", true);
        
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
       mUtils.sleep(Constants.THREE_SECONDS);


       mUtility.scrollListAndCapture(Setup_GoogleServices_pureGMS, mUtils.createScrollable(), 4);
       mUtils.sleep(Constants.HALF_SECOND);


       mDevice.findObject(By.clazz("android.widget.Button")).click();
       mUtils.sleep(Constants.FIVE_SECONDS);


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

       //mDevice.findObject(By.res("android:id/button1")).click();
       //mUtils.sleep(Constants.FIVE_SECONDS);

       // nao aparece caso n tenha conta logada
       mUtility.takeAvikScreenshotWithFlag(Setup_ContinueSetup_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);

       mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
       do{
    	   mUtils.sleep(Constants.ONE_SECOND);
       }while(!mDevice.hasObject(By.clazz("android.widget.Button")));

       mUtility.takeAvikScreenshotWithFlag(Setup_AccessYourAssistant_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);

       mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
       mUtils.sleep(Constants.FIVE_SECONDS);

       if(mDevice.hasObject(By.scrollable(true))){
    	   mUtils.createScrollable().scrollForward();
    	   mUtils.sleep(Constants.FIVE_SECONDS);
       }

       mUtility.takeAvikScreenshotWithFlag(Setup_TeachYourAssistant_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);

       mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
       mUtils.sleep(Constants.THREE_SECONDS);

       mUtility.takeAvikScreenshotWithFlag(Setup_AccessWithoutUlocking_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);


       mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
       mUtils.sleep(Constants.FIVE_SECONDS);

       mUtility.takeAvikScreenshotWithFlag(Setup_GooglePay_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);

       mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
       mUtils.sleep(Constants.FIVE_SECONDS);


       mUtility.takeAvikScreenshotWithFlag(Setup_YoutubeMusic_pureGMS);
       mUtils.sleep(Constants.HALF_SECOND);

       mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
       mUtils.sleep(Constants.ONE_SECOND);

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

    }
    
    private void captureLetsStayInTouchScreens() throws Exception {

        while(!mDevice.hasObject(By.res("com.motorola.setup:id/stay_in_the_know_image"))){
            logger.info("-----------CHECK SCREEN-----------");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }
    	
    	BySelector buttonSelector = By.clazz("android.widget.Button");
    	
    	//mUtility.scrollAndCaptureWithFlag(mUtils.createScrollable(), 2, Setup_Motorola_StayInTheKnow);
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayInTheKnow_Scrolling1);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayInTheKnow_Scrolling2);

        mUtils.sleep(Constants.ONE_SECOND);
		mDevice.findObjects(buttonSelector).get(0).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_OptOut_Dialog);
		
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		//mUtility.scrollAndCaptureWithFlag(mUtils.createScrollable(), 2, Setup_Motorola_EnterEmail);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_EnterEmail_Scrolling1);
        mUtils.swipeFromCenterToTop();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_EnterEmail_Scrolling2);

		mUtils.sleep(Constants.ONE_SECOND);
		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_add_email")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_UseThisEmail_Dialog);
		
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mDevice.findObject(By.res("com.motorola.setup:id/add_email")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_UseThisEmail_EnterValidEmail_Dialog);
		
		mUtils.skipAndroidButton2();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mDevice.findObjects(buttonSelector).get(0).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_OptOutEmail_Dialog);
		
		mDevice.findObject(By.res("com.motorola.setup:id/moto_email_skip_dialog")).click();
		mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_EnterCPF);
		
		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_cpf_info")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_UseOfCPF_Dialog);
		
		mDevice.findObject(By.res("android:id/button1")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mDevice.findObjects(buttonSelector).get(1).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mDevice.findObject(By.res("com.motorola.setup:id/moto_crm_cpf")).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
		mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_EnterValidCPF_Dialog);
		mUtils.sleep(Constants.HALF_SECOND);
		
		
		mDevice.findObjects(buttonSelector).get(0).click();
		mUtils.sleep(Constants.ONE_SECOND);
		
    }

    private void captureFinalScreens() throws Exception{
    	
    	while(!mDevice.hasObject(By.res("com.motorola.coresettingsext:id/animationView"))){
    		logger.info("-----------CHECK SCREEN-----------");
    		mUtils.sleep(Constants.FIVE_SECONDS);
        	
    	}

    	//navigation
    	
    	mUtils.sleep(Constants.ONE_SECOND);
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Buttons);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/secondary_option")).click();
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Gestures);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/primary_option")).click();
    	mUtils.sleep(Constants.FIVE_SECONDS);

    	mDevice.pressBack();
    	/*
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	
    	mDevice.findObject(By.res("com.motorola.coresettingsext:id/next_button")).click();
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	
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

         */
    }
    
    private void expandAll() throws Exception{
    	mDevice.pressBack();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mDevice.pressBack();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_backup")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_location_service")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_wireless_scan_always_mode")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/agree_usage_reporting")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	mUtils.createScrollable().scrollForward();
    	mUtils.sleep(Constants.ONE_SECOND);    	
    	
    	mDevice.findObject(By.res("com.google.android.gms:id/google_services_agreement")).click();
    	mUtils.sleep(Constants.HALF_SECOND);
    	
    	mUtils.createScrollable().scrollBackward(4);
    	
    	
    	//FindObject
    	//CLick
    }
    
    public void captureVZW() throws Exception {
    	mUtils.sleep(Constants.TEN_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_VerizonCloud_VZW);
    }

    public void captureGoogleServ() throws Exception {

        while(!mDevice.hasObject(By.res("com.google.android.gms:id/sud_items_switch"))) {
            logger.info("GO TO SERVICES SCREEN AND EXPAND ITEMS");
            mUtils.sleep(Constants.TWENTY_SECONDS);
        }
        mUtility.scrollListAndCapture(Setup_GoogleServices_pureGMS, mUtils.createScrollable(), 4);

    }

    @Test
    public void testMain() {
        try {
        	captureLetsStayInTouchScreens();
        	//captureFinalScreens();
            //captureGoogleServ();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}