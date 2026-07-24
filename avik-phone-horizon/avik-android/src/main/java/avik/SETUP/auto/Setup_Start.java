package avik.SETUP.auto;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;

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
 * Screen count: 9
 * Execution time: ~50s
 * 
 * This script will capture start, emergency and vision screens
 *
 * 1. FDR device 
 * 2. Install Invisible IME
 * 3. Remove simcard
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_Start {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    private AvikScreenshotAction Setup_EmergencyCall_Numbers_pureGMS = new AndroidAvikScreenshotAction("Setup_EmergencyCall_Numbers_pureGMS", true);
    private AvikScreenshotAction Setup_EmergencyCall_Dialer = new AndroidAvikScreenshotAction("Setup_EmergencyCall_Dialer", true);
    private AvikScreenshotAction Setup_EmergencyInformationTapAgain = new AndroidAvikScreenshotAction("Setup_EmergencyInformationTapAgain", true);
    private AvikScreenshotAction Setup_EmergencyCall_Dialer_CantCall = new AndroidAvikScreenshotAction("Setup_EmergencyCall_Dialer_CantCall", true);
    private AvikScreenshotAction Setup_VisionSettings = new AndroidAvikScreenshotAction("Setup_VisionSettings", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork", true);
    
    private AvikScreenshotAction Setup_Start_pureGMS = new AndroidAvikScreenshotAction("Setup_Start_pureGMS", true);
    private AvikScreenshotAction Setup_ChooseLanguage_pureGMS = new AndroidAvikScreenshotAction("Setup_ChooseLanguage_pureGMS", true);
    private AvikScreenshotAction Setup_EmergencyInformation_pureGMS = new AndroidAvikScreenshotAction("Setup_EmergencyInformation_pureGMS", true);
    private AvikScreenshotAction Setup_PhoneActivation_SIMCard_VZW = new AndroidAvikScreenshotAction("Setup_PhoneActivation_SIMCard_VZW", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_pureGMS", true);

    private AvikScreenshotAction Setup_UsingAnotherDevice_PureGMS = new AndroidAvikScreenshotAction("Setup_UsingAnotherDevice_PureGMS", true);
    private AvikScreenshotAction Setup_UsingAnotherDevice_ScanQRCode_PureGMS = new AndroidAvikScreenshotAction("Setup_UsingAnotherDevice_ScanQRCode_PureGMS", true);

    private AvikScreenshotAction Setup_CopyFromiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_ScanQRCodeWithiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_ScanQRCodeWithiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_ConnectiPhoneWithCable_PureGMS = new AndroidAvikScreenshotAction("Setup_ConnectiPhoneWithCable_PureGMS", true);

    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();

        mSetup.goToWelcomeScreen();
    }

    @After
    public void tearDown() throws Exception {
        mSetup.goToWelcomeScreen();
    }

    private void captureStartScreen() throws Exception {
        logger.info("======= Capturing Setup Screens ========");
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_Start_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);

        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseLanguage_pureGMS);


        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        captureNewCopyGMS();
        /*
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ConnectToMobileNetwork);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.pressBack();


         */
    }
    
    private void captureEmergencyAndVisionScreens() throws Exception {
        logger.info("======= Capturing Emergency Information Screens ========");
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        BySelector dialerButton = By.res("com.google.android.setupwizard:id/welcome_emergency_dial");
        
        if (mDevice.hasObject(dialerButton)) {
        	mDevice.findObject(dialerButton).click();
        	mUtils.sleep(Constants.TWO_SECONDS);
        }

        // May need to insert SIM to capture
        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyCall_Numbers_pureGMS);
        //mUtils.sleep(Constants.HALF_SECOND);

        //mDevice.findObject(By.res("com.google.android.apps.safetyhub:id/floating_action_button_dialpad")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyCall_Dialer);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.findObject(By.res("com.android.phone:id/floating_action_button")).click();
        mUtils.sleep(Constants.HALF_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyCall_Dialer_CantCall);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObject(By.res("com.android.phone:id/emergency_info_name")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyInformationTapAgain);

        mDevice.findObject(By.res("com.android.phone:id/emergency_info_name")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.scrollListAndCapture(Setup_EmergencyInformation_pureGMS, mUtility.createScrollable(), 2);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.res("com.google.android.setupwizard:id/welcome_accessibility")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_VisionSettings);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
    }
    
    private void captureVZWStart() throws Exception {
    	mDevice.findObject(By.res("com.google.android.setupwizard:id/start")).click();
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_PhoneActivation_SIMCard_VZW);
    }

    private void captureNewCopyGMS() throws Exception {

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_UsingAnotherDevice_PureGMS);
        mDevice.findObject(By.res("com.google.android.gms:id/android_option_button")).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_UsingAnotherDevice_ScanQRCode_PureGMS);
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.pressBack();
    }

    @Test
    public void testMain() {
        try {
            captureStartScreen();
            captureEmergencyAndVisionScreens();
        	//captureVZWStart();
            //captureNewCopyGMS();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
