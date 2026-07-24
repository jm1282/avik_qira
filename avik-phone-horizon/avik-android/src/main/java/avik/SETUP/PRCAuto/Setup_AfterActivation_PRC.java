package avik.SETUP.PRCAuto;

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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 25 Execution time: ~2m 40s
 *
 * Initial Setup:
 * 1. Add a working Wi-Fi network during setup flow
 * 2. Install the InvisibleIME app and set its keyboard as the main input;
 * 3. Go back to the first screen (language selection);
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_AfterActivation_PRC {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private UiDevice mDevice;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private Setup mSetup;

    private AvikScreenshotAction Setup_Activation_Completed_Toast;
    private AvikScreenshotAction Setup_BackupService_Lenovo;
    private AvikScreenshotAction Setup_LenovoAccount_Login;
    private AvikScreenshotAction Setup_LenovoAccount_Login_PwdLogin;
    private AvikScreenshotAction Setup_LenovoAccount_FormatError_Hint;
    private AvikScreenshotAction Setup_LenovoAccount_FormatError_Toast;
    private AvikScreenshotAction Setup_LenovoAccount_RetrievePassword;
    private AvikScreenshotAction Setup_LenovoAccount_Loading_Lenovo;
    private AvikScreenshotAction Setup_BackupService_RecoverData_Lenovo;
    private AvikScreenshotAction Setup_BackupService_DataSync_Lenovo;
    private AvikScreenshotAction Setup_SetAPIN_6digit;
    private AvikScreenshotAction Setup_SetAPIN_6digit_Skip;
    private AvikScreenshotAction Setup_Motorola_Privacy;
    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly;
    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport;
    private AvikScreenshotAction Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts;
    private AvikScreenshotAction Setup_RecommendedApps_Lenovo;
    private AvikScreenshotAction Setup_Start_Homescreen_Open;
    private AvikScreenshotAction Setup_Start_Homescreen_AppTray;
    
    private AvikScreenshotAction Setup_ChooseYourTheme_Light;
    private AvikScreenshotAction Setup_ChooseYourTheme_Dark;
    private AvikScreenshotAction Setup_ChooseNavigation_Gestures;
    private AvikScreenshotAction Setup_ChooseNavigation_Buttons;
    
    private AvikScreenshotAction Setup_Motorola_YoureAllSet;
    
    //NEW
    private AvikScreenshotAction Setup_FeatureRecommendation = new AndroidAvikScreenshotAction("Setup_FeatureRecommendation", true);
    private AvikScreenshotAction Setup_FeatureRecommendation_LocationConsent_Dialog = new AndroidAvikScreenshotAction("Setup_FeatureRecommendation_LocationConsent_Dialog", true);
    
    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mSetup = new Setup();
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
        
        Setup_Activation_Completed_Toast = new AndroidAvikScreenshotAction("Setup_Activation_Completed_Toast", true);
        Setup_BackupService_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_Lenovo", true);
        Setup_LenovoAccount_Login = new AndroidAvikScreenshotAction("Setup_LenovoAccount_Login", true);
        Setup_LenovoAccount_Login_PwdLogin = new AndroidAvikScreenshotAction("Setup_LenovoAccount_Login_PwdLogin", true);
        Setup_LenovoAccount_FormatError_Hint = new AndroidAvikScreenshotAction("Setup_LenovoAccount_FormatError_Hint", true);
        Setup_LenovoAccount_FormatError_Toast = new AndroidAvikScreenshotAction("Setup_LenovoAccount_FormatError_Toast", true);
        Setup_LenovoAccount_RetrievePassword = new AndroidAvikScreenshotAction("Setup_LenovoAccount_RetrievePassword",true);
        Setup_LenovoAccount_Loading_Lenovo = new AndroidAvikScreenshotAction("Setup_LenovoAccount_Loading_Lenovo", true);
        Setup_BackupService_RecoverData_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_RecoverData_Lenovo", true);
        Setup_BackupService_DataSync_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_DataSync_Lenovo", true);
        Setup_SetAPIN_6digit = new AndroidAvikScreenshotAction("Setup_SetAPIN_6digit", true);
        Setup_SetAPIN_6digit_Skip = new AndroidAvikScreenshotAction("Setup_SetAPIN_6digit_Skip", true);
        
        Setup_Motorola_Privacy = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy", true);
        Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoToKeepSoftwareUpdatedOnly", true);
        Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupporty", true);
        Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts", true);        
        
        Setup_RecommendedApps_Lenovo = new AndroidAvikScreenshotAction("Setup_RecommendedApps_Lenovo", true);
        Setup_Start_Homescreen_Open = new AndroidAvikScreenshotAction("Setup_Start_Homescreen_Open", true);
        Setup_Start_Homescreen_AppTray = new AndroidAvikScreenshotAction("Setup_Start_Homescreen_AppTray", true);
        
        // en-GB only
        Setup_ChooseYourTheme_Light = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Light", true);
        Setup_ChooseYourTheme_Dark = new AndroidAvikScreenshotAction("Setup_ChooseYourTheme_Dark", true);
        Setup_ChooseNavigation_Gestures = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Gestures", true);
        Setup_ChooseNavigation_Buttons = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Buttons", true);
        
        Setup_Motorola_YoureAllSet = new AndroidAvikScreenshotAction("Setup_Motorola_YoureAllSet", true);
          
    }
    
    private void capturePRCAfterWiFi() throws Exception {
    	logger.info("-----CAPTURING SCREENS-----");
    	
    	mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/start")).click();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mDevice.wait(Until.hasObject(By.res("com.lenovo.leos.cloud.sync:id/right")), Constants.THREE_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_Activation_Completed_Toast);
        mUtils.sleep(Constants.TWO_SECONDS);
        // in chinese, no en-XM available
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_Lenovo);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.res("com.lenovo.leos.cloud.sync:id/right")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_DataSync_Lenovo);
        mDevice.pressBack();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mDevice.pressBack();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/start")).click();
        mUtils.sleep(Constants.TEN_SECONDS);
        mDevice.findObject(By.res("com.lenovo.leos.cloud.sync:id/login")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_Login);
        mDevice.findObject(By.res("com.lenovo.lsf:id/tv_login_pwd")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_Login_PwdLogin);
        
        BySelector accountBox = By.res("com.lenovo.lsf:id/at_account");
        BySelector passwordBox = By.res("com.lenovo.lsf:id/et_password");
        BySelector loginButton = By.res("com.lenovo.lsf:id/b_login");
        
        mDevice.findObject(accountBox).setText("1234");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(passwordBox).setText("1234");
        mUtils.sleep(Constants.ONE_SECOND);
        
        //trigger wrong account hint
        mDevice.findObject(loginButton).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_FormatError_Hint);
        
        //trigger wrong password toast
        mDevice.findObject(accountBox).setText("cinauto2014@gmail.com");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(loginButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_FormatError_Toast);
        
        //forgot password
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.lenovo.lsf:id/b_findPW")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_RetrievePassword);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        //actually logging in
        mDevice.findObject(passwordBox).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(passwordBox).setText("passcinauto2022");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(loginButton).click();
        mDevice.wait(Until.hasObject(By.res("com.lenovo.leos.cloud.sync:id/message")), Constants.TEN_SECONDS * 2);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_Loading_Lenovo);
        
        mDevice.wait(Until.hasObject(By.res(".lenovo.leos.cloud.sync:id/blank_tab_sub_title")), Constants.TEN_SECONDS);
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_RecoverData_Lenovo);
        
        mDevice.findObject(By.res("com.lenovo.leos.cloud.sync:id/btn_continue")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN_6digit);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.HALF_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN_6digit_Skip);
        mUtils.skipAndroidButton1();
        mUtils.sleep(Constants.HALF_SECOND);
        
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.scrollListAndCapture(Setup_Motorola_Privacy, mUtils.createScrollable(), 2);
        
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
     
        logger.info("primeiro ON");
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollForward();
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy_WarningInfoNotSharedForPersonalizedSupport);
        
        logger.info("segundo ON");
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        mDevice.findObject(By.text(customizedSupport)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        if(mDevice.hasObject(By.scrollable(true))){
        	mUtils.createScrollable().scrollForward();
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_Privacy_WarningInfoNotSharedToImproveProducts);
        
        mDevice.findObject(By.text(shareUsage)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        
        mDevice.wait(Until.hasObject(By.res("com.lenovo.leos.appstore:id/checkBox")), Constants.TEN_SECONDS * 2);
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_RecommendedApps_Lenovo);
        mDevice.findObject(By.res("com.lenovo.leos.appstore:id/btn_install")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_Start_Homescreen_Open);
        mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/secondary_option")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Start_Homescreen_AppTray);
        mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/next_button")).click();
        
        BySelector finalScreensNext = By.res("com.motorola.coresettingsext:id/next_button");
        /*
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseYourTheme_Light);
        mDevice.findObject(By.res("com.motorola.coresettingsext:id/secondary_option")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseYourTheme_Dark);
        mDevice.findObject(By.res("com.motorola.coresettingsext:id/primary_option")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.findObject(finalScreensNext).click();
        */
        
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Gestures);
        mDevice.findObject(By.res("com.motorola.coresettingsext:id/secondary_option")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseNavigation_Buttons);
        mDevice.findObject(By.res("com.motorola.coresettingsext:id/primary_option")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.findObject(finalScreensNext).click();
        
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_YoureAllSet);

    }
    
    private void captureFeatureRec() throws Exception {
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation);
    	mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/location_switch")).click();
    	mUtils.sleep(Constants.TWO_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_LocationConsent_Dialog);
    	mDevice.pressBack();
    }
    
    @Test
    public void testMain() {
        try {
        	/*
        	mDevice.pressBack();
        	mUtils.sleep(Constants.ONE_SECOND);
        	mDevice.pressBack();
        	mUtils.sleep(Constants.ONE_SECOND);
        	*/
        	//capturePRCAfterWiFi();
        	captureFeatureRec();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
