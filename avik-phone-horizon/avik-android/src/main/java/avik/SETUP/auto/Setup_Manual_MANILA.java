package avik.SETUP.auto;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.impl.LocaleEnum;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

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


public class Setup_Manual_MANILA {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

//    private AvikScreenshotAction Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS = new AndroidAvikScreenshotAction(avikHandler, "Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS", LocaleEnum.ES_US, LocaleEnum.FR_FR);
//    private AvikScreenshotAction Setup_YoureAllSetUp = new AndroidAvikScreenshotAction(avikHandler, "Setup_YoureAllSetUp", LocaleEnum.IT_IT);
//    private AvikScreenshotAction Setup_TakeCareOfYourPhone = new AndroidAvikScreenshotAction(avikHandler, "Setup_TakeCareOfYourPhone", LocaleEnum.FR_FR, LocaleEnum.ES_ES, LocaleEnum.ES_US, LocaleEnum.IT_IT, LocaleEnum.JA_JP, LocaleEnum.PT_BR);
//    private AvikScreenshotAction Setup_TakeCareOfYourPhone_Scrolling1 = new AndroidAvikScreenshotAction(avikHandler, "Setup_TakeCareOfYourPhone_Scrolling1", LocaleEnum.EN_GB, LocaleEnum.FR_FR, LocaleEnum.IT_IT, LocaleEnum.JA_JP);

    private AvikScreenshotAction Setup_Motorola_Privacy_Scrolling1 = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_Scrolling1", true);
    private AvikScreenshotAction Setup_Motorola_Privacy_Scrolling2 = new AndroidAvikScreenshotAction("Setup_Motorola_Privacy_Scrolling2", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_HowTo_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToMobileNetwork_Skip_Dialog_pureGMS", true);
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS = new AndroidAvikScreenshotAction("Setup_RestoreFromBackup_pureGMS", true);
    private AvikScreenshotAction Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", true);
    private AvikScreenshotAction Setup_SkipScreenLock_Dialog = new AndroidAvikScreenshotAction("Setup_SkipScreenLock_Dialog", true);
    private AvikScreenshotAction Setup_Fingerprint = new AndroidAvikScreenshotAction("Setup_Fingerprint", true);
    private AvikScreenshotAction Setup_Fingerprint_Skip_Dialog = new AndroidAvikScreenshotAction("Setup_Fingerprint_Skip_Dialog", true);
    private AvikScreenshotAction Setup_Fingerprint_AlmostThere = new AndroidAvikScreenshotAction("Setup_Fingerprint_AlmostThere", true);
    private AvikScreenshotAction Setup_LookingForUpdates_pureGMS = new AndroidAvikScreenshotAction("Setup_LookingForUpdates_pureGMS", true);
    private AvikScreenshotAction Setup_CopyAppsAndData_pureGMS = new AndroidAvikScreenshotAction("Setup_CopyAppsAndData_pureGMS", true);

    private AvikScreenshotAction Setup_WhoWillUse_pureGMS = new AndroidAvikScreenshotAction("Setup_WhoWillUse_pureGMS", true);
    private AvikScreenshotAction Setup_Welcome_pureGMS = new AndroidAvikScreenshotAction("Setup_Welcome_pureGMS", true);

    private AvikScreenshotAction Setup_ChooseNavigation_Buttons = new AndroidAvikScreenshotAction("Setup_ChooseNavigation_Buttons", true);
    private AvikScreenshotAction Setup_Complete = new AndroidAvikScreenshotAction("Setup_Complete", true);
    private AvikScreenshotAction Setup_CopyFromiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_CopyFromiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_ConnectiPhoneWithCable_PureGMS = new AndroidAvikScreenshotAction("Setup_ConnectiPhoneWithCable_PureGMS", true);
    private AvikScreenshotAction Setup_ScanQRCodeWithiPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_ScanQRCodeWithiPhone_PureGMS", true);
    private AvikScreenshotAction Setup_FollowTheseStepsOniPhone_PureGMS = new AndroidAvikScreenshotAction("Setup_FollowTheseStepsOniPhone_PureGMS", true);

    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS_Scrolling1 = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS_Scrolling1", true);
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS_Scrolling2 = new AndroidAvikScreenshotAction("Setup_TeachYourAssistant_pureGMS_Scrolling2", true);
    private AvikScreenshotAction Setup_ReviewAdditionalApps_pureGMS = new AndroidAvikScreenshotAction("Setup_ReviewAdditionalApps_pureGMS", true);

    private AvikScreenshotAction Setup_Motorola_StayInTouch = new AndroidAvikScreenshotAction("Setup_Motorola_StayInTouch", true);

    private AvikScreenshotAction Setup_ScreenProtectorAlert = new AndroidAvikScreenshotAction("Setup_ScreenProtectorAlert", true);
    private AvikScreenshotAction Setup_TakeCareOfYourPhone = new AndroidAvikScreenshotAction("Setup_TakeCareOfYourPhone", true);

    private AvikScreenshotAction Setup_NavigationStyle = new AndroidAvikScreenshotAction("Setup_NavigationStyle", true);
    private AvikScreenshotAction Setup_YoureAllSetUp = new AndroidAvikScreenshotAction("Setup_YoureAllSetUp", true);
    private AvikScreenshotAction Setup_Theme = new AndroidAvikScreenshotAction("Setup_Theme", true);
    private AvikScreenshotAction Setup_LetsExplore = new AndroidAvikScreenshotAction("Setup_LetsExplore", true);

    private AvikScreenshotAction Setup_EmergencyCall_Dialer = new AndroidAvikScreenshotAction("Setup_EmergencyCall_Dialer", true);
    private AvikScreenshotAction Setup_EmergencyInformationTapAgain = new AndroidAvikScreenshotAction("Setup_EmergencyInformationTapAgain", true);
    private AvikScreenshotAction Setup_EmergencyCall_Dialer_CantCall = new AndroidAvikScreenshotAction("Setup_EmergencyCall_Dialer_CantCall", false);
    private AvikScreenshotAction Setup_VisionSettings = new AndroidAvikScreenshotAction("Setup_VisionSettings", true);

    private AvikScreenshotAction Setup_Motorola_PrivacyAndSecurity_Scrolling1 = new AndroidAvikScreenshotAction("Setup_Motorola_PrivacyAndSecurity_Scrolling1", true);
    private AvikScreenshotAction Setup_Motorola_PrivacyAndSecurity_Scrolling2 = new AndroidAvikScreenshotAction("Setup_Motorola_PrivacyAndSecurity_Scrolling2", true);
    private AvikScreenshotAction Setup_Motorola_PrivacyAndSecurity_Scrolling3 = new AndroidAvikScreenshotAction("Setup_Motorola_PrivacyAndSecurity_Scrolling3", true);

    private AvikScreenshotAction Setup_SetAPIN_Skip_Dialog = new AndroidAvikScreenshotAction("Setup_SetAPIN_Skip_Dialog", true);
    private AvikScreenshotAction Setup_ScreenLockOptions_Dialog = new AndroidAvikScreenshotAction("Setup_ScreenLockOptions_Dialog", true);

    private AvikScreenshotAction Setup_Motorola_StayUpToDate = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate", true);
    private AvikScreenshotAction Setup_Motorola_StayUpToDate_Dropdown = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate_Dropdown", true);
    private AvikScreenshotAction Setup_Motorola_StayUpToDate_Skip_Dialog = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate_Skip_Dialog", false);
    private AvikScreenshotAction Setup_Motorola_StayUpToDate_PleaseInputAddress = new AndroidAvikScreenshotAction("Setup_Motorola_StayUpToDate_PleaseInputAddress", true);
    //private AvikScreenshotAction Setup_NavigationStyle = new AndroidAvikScreenshotAction("Setup_NavigationStyle", true);
    private AvikScreenshotAction Setup_YoureAllSet = new AndroidAvikScreenshotAction("Setup_YoureAllSet", true);


    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
    }


    private void captureEmergencyAndVisionScreens() throws Exception {
        logger.info("======= Capturing Emergency Information Screens ========");
        mUtils.sleep(Constants.ONE_SECOND);

        BySelector dialerButton = By.res("com.google.android.apps.safetyhub:id/floating_action_button_dialpad");

        mDevice.findObject(By.res("com.google.android.setupwizard:id/welcome_accessibility_text_view")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        if (mDevice.hasObject(dialerButton)) {
            mDevice.findObject(dialerButton).click();
            mUtils.sleep(Constants.TWO_SECONDS);
        }

        // May need to insert SIM to capture
        //mUtils.sleep(Constants.HALF_SECOND);

        //mDevice.findObject(By.res("com.google.android.apps.safetyhub:id/floating_action_button_dialpad")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_VisionSettings);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void captureManual() throws Exception{
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_YoureAllSet);
    }

    public void capturePrivacy() throws Exception{
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.swipeFromCenterToBottom();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_PrivacyAndSecurity_Scrolling1);
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.createScrollable().scrollForward();
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_PrivacyAndSecurity_Scrolling2);
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.createScrollable().scrollForward();
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_PrivacyAndSecurity_Scrolling3);
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.swipeFromCenterToBottom();

    }

    public void capturePIN() throws Exception {
        String skip = mUtils.getResourceByPackAndStringKey("com.android.settings", "skip_label");

        mUtils.sleep(Constants.THREE_SECONDS);
        //mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN);
        mDevice.findObject(By.text(skip)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN_Skip_Dialog);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.android.settings:id/screen_lock_options")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_ScreenLockOptions_Dialog);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

    }

    private void captureFingerprint() throws Exception {
        mUtils.sleep(Constants.TEN_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Fingerprint);
        mDevice.findObject(By.res("com.android.settings:id/primary_btn")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.android.settings:id/second_btn")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Fingerprint_Skip_Dialog);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
    }

    public void captureNewSetupEnd() throws Exception {
        mDevice.click(503,531);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        mUtility.takeAvikScreenshotWithFlag(Setup_YoureAllSetUp);
        mUtility.takeAvikScreenshotWithFlag(Setup_LetsExplore);
    }

    public void captureFinal() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate);
        mDevice.findObject(By.res("com.ontim.moto.setupwizard:id/dropdown_menu")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_Dropdown);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.ontim.moto.setupwizard:id/second_btn")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_PleaseInputAddress);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.ontim.moto.setupwizard:id/first_btn")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_Skip_Dialog);
        mDevice.findObject(By.res("com.ontim.moto.setupwizard:id/enable_btn")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

    }

    public void captureFinalLamu() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate);
        mDevice.findObject(By.res("com.ape.setupwizard:id/spinner")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_Dropdown);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.ape.setupwizard:id/edt")).setText("");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.ape.setupwizard:id/suw_navbar_next")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_StayUpToDate_PleaseInputAddress);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.ape.setupwizard:id/edt")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtils.inputAsciiText("cinauto2014@gmail.com");
        mUtils.sleep(Constants.TWO_SECONDS);

    }

    public void captureEnd() throws Exception {
        mDevice.click(363,12);
        mUtils.sleep(Constants.FIVE_SECONDS);

        //mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        //mUtility.takeAvikScreenshotWithFlag(Setup_ScreenLockOptions_Dialog);
        mUtility.takeAvikScreenshotWithFlag(Setup_YoureAllSet);
    }
    @Test
    public void testMain() {
        try {
            //this.captureTemp();
            //captureNewSetupEnd();
            //this.captureManual();
            //captureCLI();
            //captureEmergencyAndVisionScreens();
            //capturePrivacy();
            //capturePIN();
            //captureFingerprint();
            //captureFinal();
            //captureFinalLamu();
            captureEnd();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}