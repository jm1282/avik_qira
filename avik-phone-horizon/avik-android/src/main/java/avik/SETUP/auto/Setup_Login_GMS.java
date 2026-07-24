package avik.SETUP.auto;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
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

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 15 Execution time: ~m
 *
 * Initial Setup:
 * 1. Set desired locale BEFORE execution
 * 2. Go through to the login screen with Wi-Fi connected;
 * 3. Go back to the copy apps and data screen
 * 4. Run the script for all target locales, since this setup is only required once
 *
 * </pre>
 */


public class Setup_Login_GMS {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    // New screen: Setup_SetupChild_pureGMS
    // triggered by trying to create account for child

    private AvikScreenshotAction Setup_ConnectToMobileNetwork_eSIM_PureGMS;
    

    private AvikScreenshotAction Setup_CreateAccountOptions_pureGMS = new AndroidAvikScreenshotAction("Setup_CreateAccountOptions_pureGMS", true);
    private AvikScreenshotAction Setup_EnterEmail_pureGMS = new AndroidAvikScreenshotAction("Setup_EnterEmail_pureGMS", true);
    private AvikScreenshotAction Setup_EnterValidEmail_pureGMS = new AndroidAvikScreenshotAction("Setup_EnterValidEmail_pureGMS", true);
    private AvikScreenshotAction Setup_AdventureStartsHere_pureGMS = new AndroidAvikScreenshotAction("Setup_AdventureStartsHere_pureGMS", true);
    private AvikScreenshotAction Setup_CreateAccountForMe_pureGMS = new AndroidAvikScreenshotAction("Setup_CreateAccountForMe_pureGMS", true);
    private AvikScreenshotAction Setup_FindYourEmail_pureGMS = new AndroidAvikScreenshotAction("Setup_FindYourEmail_pureGMS", true);
    private AvikScreenshotAction Setup_YourDeviceWorksBetter_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_YourDeviceWorksBetter_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_SkipAccount_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_SkipAccount_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_EnterYourPassword_pureGMS = new AndroidAvikScreenshotAction("Setup_EnterYourPassword_pureGMS", true);
    private AvikScreenshotAction Setup_EnterAPassword_pureGMS = new AndroidAvikScreenshotAction("Setup_EnterAPassword_pureGMS", true);
    private AvikScreenshotAction Setup_WrongPassword_pureGMS = new AndroidAvikScreenshotAction("Setup_WrongPassword_pureGMS", true);
    private AvikScreenshotAction Setup_AccountRecovery_CheckYourPhone_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountRecovery_CheckYourPhone_pureGMS", true);
    private AvikScreenshotAction Setup_AccountRecovery_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountRecovery_pureGMS", true);
    private AvikScreenshotAction Setup_WhoWillUse_pureGMS = new AndroidAvikScreenshotAction("Setup_WhoWillUse_pureGMS", true);
    private AvikScreenshotAction Setup_Welcome_pureGMS = new AndroidAvikScreenshotAction("Setup_Welcome_pureGMS", true);
    
    
    private AvikScreenshotAction Setup_GooglePay_pureGMS; 
    private AvikScreenshotAction Setup_AnythingElse_pureGMS;
    private AvikScreenshotAction Setup_AnythingElseDoneForNow_pureGMS;


    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
    }

    @After
    public void tearDown() throws Exception {

    }

    private void captureSetupScreens() throws Exception {
        logger.info("===== Capturing Account Screens =====");
        mUtils.sleep(Constants.TWO_SECONDS);

        while (!mDevice.hasObject(By.clazz("android.widget.TextView"))) {
            logger.info("!!!!! GO TO LOGIN SCREEN !!!!!");
            mUtils.sleep(Constants.TWO_SECONDS);
        }

        mUtility.takeAvikScreenshotWithFlag(Setup_EnterEmail_pureGMS);

        mDevice.findObject(By.clazz("android.widget.EditText")).click();
        mUtils.sleep(Constants.HALF_SECOND);
        mUtils.writeNonAsciiText("@/@");
        mUtils.sleep(Constants.ONE_SECOND);
        // NEXT button
        mDevice.findObjects(By.clazz("android.widget.Button")).get(3).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EnterValidEmail_pureGMS);

        mDevice.findObject(By.res("learnMore")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_YourDeviceWorksBetter_Dialog_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        //SKIP button
        mDevice.findObjects(By.clazz("android.widget.Button")).get(4).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_SkipAccount_Dialog_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_FindYourEmail_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.THREE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CreateAccountOptions_pureGMS);

        mDevice.click(315,1830);
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_CreateAccountForMe_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.click(315,1960);
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_AdventureStartsHere_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);

        mDevice.findObject(By.clazz("android.widget.EditText")).setText("cinauto2014");
        mUtils.sleep(Constants.ONE_SECOND);

        // NEXT button
        mDevice.findObjects(By.clazz("android.widget.Button")).get(3).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_EnterYourPassword_pureGMS);

        mDevice.findObject(By.res("passwordNext")).click();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_EnterAPassword_pureGMS);

        mDevice.findObject(By.clazz("android.widget.EditText")).setText("wronglollmao");
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.res("passwordNext")).click();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_WrongPassword_pureGMS);

        mDevice.findObject(By.res("forgotPassword")).click();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_AccountRecovery_CheckYourPhone_pureGMS);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtils.sleep(Constants.TEN_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_AccountRecovery_pureGMS);

        // MANUAL
        mUtility.takeAvikScreenshotWithFlag(Setup_WhoWillUse_pureGMS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Welcome_pureGMS);
    }

    public void captureAfterCredentials() throws Exception{
        while(mDevice.hasObject(By.res("passwordNext"))) {
            mDevice.pressBack();
            mUtils.sleep(Constants.THREE_SECONDS);
        }

        mDevice.findObject(By.clazz("android.widget.EditText")).setText("passcinauto2022");
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.findObject(By.res("passwordNext")).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_WhoWillUse_pureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_Welcome_pureGMS);

        mDevice.pressBack();
        mDevice.pressBack();

    }

    private void captureDelta() throws Exception {
        logger.info("===== Capturing Account Screens =====");
        mUtils.sleep(Constants.FIVE_SECONDS);

        while (!mDevice.hasObject(By.clazz("android.widget.EditText"))) {
            logger.info("!!!!! GO TO LOGIN SCREEN !!!!!");
            mUtils.sleep(Constants.FIVE_SECONDS);
        }

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);

        //Probably need to click by coordinates
        mDevice.click(340,1969);
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_AdventureStartsHere_pureGMS);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    @Test
    public void testMain() {
        try {
            this.captureSetupScreens();
            //this.captureAfterCredentials();
            //captureDelta();

        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
