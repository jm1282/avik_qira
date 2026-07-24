package avik.SETUP.auto;

import static android.os.SystemClock.sleep;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.impl.LocaleEnum;
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
 * Screen count: 12 Execution time: ~m
 *
 * Initial Setup:
 * 1. Set desired locale BEFORE execution
 * 2. Go through to the login screen with Wi-Fi connected;
 * 3. Go back to the copy apps and data screen
 * 4. Run the script for all target locales, since this setup is only required once
 *
 * </pre>
 */


public class Setup_Manual {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;


    private AvikScreenshotAction Setup_ChatWithGemini_PureGMS = new AndroidAvikScreenshotAction("Setup_ChatWithGemini_PureGMS", LocaleEnum.EL_GR.toLocale());
    private AvikScreenshotAction Setup_GoogleBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_GoogleBackup_pureGMS", LocaleEnum.AR_EG.toLocale());
    private AvikScreenshotAction Setup_NavigationStyle = new AndroidAvikScreenshotAction("Setup_NavigationStyle", LocaleEnum.PT_BR.toLocale());
    private AvikScreenshotAction Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", LocaleEnum.AR_EG.toLocale());
    private AvikScreenshotAction Setup_SkipScreenLock_Dialog = new AndroidAvikScreenshotAction("Setup_SkipScreenLock_Dialog", LocaleEnum.ES_US.toLocale());

    private AvikScreenshotAction Setup_PrivacyAndSecurity = new AndroidAvikScreenshotAction("Setup_PrivacyAndSecurity", true);

    private AvikScreenshotAction Setup_Motorola_PrivacyAndUpdates = new AndroidAvikScreenshotAction("Setup_Motorola_PrivacyAndUpdates", true);
    private AvikScreenshotAction Setup_Motorola_Privacy_Scrolling1 = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_Scrolling1", true);
    private AvikScreenshotAction Setup_Motorola_Privacy_Scrolling2 = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_Scrolling2", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS", true);
    //private AvikScreenshotAction Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreFromBackup_pureGMS", true);
    //private AvikScreenshotAction Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", true);
    //private AvikScreenshotAction Setup_SkipScreenLock_Dialog = new AndroidAvikScreenshotAction("Setup_SkipScreenLock_Dialog", true);
    private AvikScreenshotAction Setup_Fingerprint = new AndroidAvikScreenshotAction("Setup_Fingerprint", true);
    private AvikScreenshotAction Setup_Fingerprint_Skip_Dialog = new AndroidAvikScreenshotAction("Setup_Fingerprint_Skip_Dialog", true);
    private AvikScreenshotAction Setup_Fingerprint_AlmostThere = new AndroidAvikScreenshotAction("Setup_Fingerprint_AlmostThere", true);
    private AvikScreenshotAction Setup_LookingForUpdates_pureGMS = new AndroidAvikScreenshotAction("Setup_LookingForUpdates_pureGMS", true);
    private AvikScreenshotAction Setup_CopyAppsAndData_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyAppsAndData_pureGMS", true);

    private AvikScreenshotAction Setup_WhoWillUse_pureGMS = new AndroidAvikScreenshotAction("Setup_WhoWillUse_pureGMS", true);
    private AvikScreenshotAction Setup_Welcome_pureGMS = new AndroidAvikScreenshotAction("Setup_Welcome_pureGMS", true);
    private AvikScreenshotAction Setup_AccountAdded_pureGMS = new AndroidAvikScreenshotAction("Setup_AccountAdded_pureGMS", true);

    private AvikScreenshotAction Setup_ContinueSetup_pureGMS = new AndroidAvikScreenshotAction("Setup_ContinueSetup_pureGMS", true);

    //private AvikScreenshotAction Setup_ChooseNavigation_Buttons = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Buttons", true);
    //private AvikScreenshotAction Setup_Complete = new AndroidAvikScreenshotAction("Setup_Complete", true);
    private AvikScreenshotAction Setup_CopyFromiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_ConnectiPhoneWithCable_PureGMS = new AndroidAvikScreenshotAction("Setup_ConnectiPhoneWithCable_PureGMS", true);
    private AvikScreenshotAction Setup_ScanQRCodeWithiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_ScanQRCodeWithiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_FollowTheseStepsOniPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_FollowTheseStepsOniPhone_PureGMS", true);

    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_AccessYourAssistant_pureGMS_Scrolling1", true);
    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_AccessYourAssistant_pureGMS_Scrolling2", true);
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS_Scrolling1", true);
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS_Scrolling2", true);
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS = new AndroidAvikScreenshotAction("Setup_AccessWithoutUlocking_pureGMS", true);
    private AvikScreenshotAction Setup_ReviewAdditionalApps_pureGMS = new AndroidAvikScreenshotAction("Setup_ReviewAdditionalApps_pureGMS", true);

    private AvikScreenshotAction Setup_Motorola_StayInTouch = new AndroidAvikScreenshotAction("Setup_Motorola_StayInTouch", true);

    private AvikScreenshotAction Setup_ScreenProtectorAlert = new AndroidAvikScreenshotAction("Setup_ScreenProtectorAlert", true);
    private AvikScreenshotAction Setup_TakeCareOfYourPhone = new AndroidAvikScreenshotAction("Setup_TakeCareOfYourPhone", true);

    //private AvikScreenshotAction Setup_NavigationStyle = new AndroidAvikScreenshotAction("Setup_NavigationStyle", true);
    private AvikScreenshotAction Setup_HelloMoto = new AndroidAvikScreenshotAction("Setup_HelloMoto", true);
    private AvikScreenshotAction Setup_Theme = new AndroidAvikScreenshotAction("Setup_Theme", true);
    private AvikScreenshotAction Setup_LetsExplore = new AndroidAvikScreenshotAction("Setup_LetsExplore", true);
    //private AvikScreenshotAction Setup_ChatWithGemini_PureGMS = new AndroidAvikScreenshotAction("Setup_ChatWithGemini_PureGMS", true);

    private AvikScreenshotAction Setup_Motorola_StayUpToDate_PleaseInputAddress = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate_PleaseInputAddress", true);
    private AvikScreenshotAction Setup_Motorola_StayUpToDate_Skip = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate_Skip", true);

    private AvikScreenshotAction Setup_Motorola_ProtectYourPhone = new AndroidAvikScreenshotAction("Setup_Motorola_ProtectYourPhone", true);

    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
    }

    public void captureManual() throws Exception{
        mUtility.takeAvikScreenshotWithFlag(Setup_WhoWillUse_pureGMS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Welcome_pureGMS);

    }

    public void captureTemp() throws Exception{
        mUtils.clickOnScreenCenter();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ReviewAdditionalApps_pureGMS);
    }

    public void captureCLI() throws Exception{
        mUtils.clickOnScreenCenter();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ScreenProtectorAlert);
        mUtility.takeAvikScreenshotWithFlag(Setup_TakeCareOfYourPhone);
    }

    public void captureNewSetupEnd() throws Exception {
        mDevice.click(503,531);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        mUtility.takeAvikScreenshotWithFlag(Setup_Theme);
        mUtility.takeAvikScreenshotWithFlag(Setup_HelloMoto);
        mUtility.takeAvikScreenshotWithFlag(Setup_LetsExplore);
    }

    public void capturePrivacy() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.scrollListAndCapture(Setup_Motorola_PrivacyAndUpdates, mUtils.createScrollable(), 3);

        mUtils.swipeFromCenterToBottom();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.swipeFromCenterToBottom();
        mUtils.sleep(Constants.ONE_SECOND);
    }
    public void captureNew() throws Exception {
        mDevice.click(503,531);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayInTouch);
    }

    public void captureDelta() throws Exception {
        mDevice.click(503,123);
        mUtils.sleep(Constants.TEN_SECONDS);
        //mUtility.takeAvikScreenshotWithFlag(Setup_ScreenProtectorAlert);
        //mUtility.takeAvikScreenshotWithFlag(Setup_TakeCareOfYourPhone);
        //mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        //mUtility.takeAvikScreenshotWithFlag(Setup_HelloMoto);
        //mUtility.takeAvikScreenshotWithFlag(Setup_Theme);
        mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN);
        mUtility.takeAvikScreenshotWithFlag(Setup_SkipScreenLock_Dialog);
        //mUtility.takeAvikScreenshotWithFlag(Setup_SkipScreenLock_Dialog);
        //mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayInTouch);
        //mUtility.takeAvikScreenshotWithFlag(Setup_ReviewAdditionalApps_pureGMS);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChatWithGemini_PureGMS);
        mUtility.takeAvikScreenshotWithFlag(Setup_GoogleBackup_pureGMS);

    }

    public void captureProtectYourPhone() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_ProtectYourPhone);
    }
    public void captureDelta2() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);

        mUtility.scrollListAndCapture(Setup_Motorola_PrivacyAndUpdates, mUtils.createScrollable(), 3);

        mUtils.swipeFromCenterToBottom();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.swipeFromCenterToBottom();
    }

    public void captureStayUpToDate() throws Exception {

        mDevice.wait(Until.findObjects(By.clazz(Button.class.getName())),Constants.ONE_SECOND).get(1).click();
        sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_PleaseInputAddress);

        mDevice.wait(Until.findObjects(By.clazz(Button.class.getName())),Constants.ONE_SECOND).get(0).click();
        sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_Skip);
    }

    @Test
    public void testMain() {
        try {
            //this.captureTemp();
            //captureNewSetupEnd();
            //this.captureManual();
            //captureCLI();
            //captureDelta();
            //captureDelta2();
            //capturePrivacy();
            //captureStayUpToDate();
            captureProtectYourPhone();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}