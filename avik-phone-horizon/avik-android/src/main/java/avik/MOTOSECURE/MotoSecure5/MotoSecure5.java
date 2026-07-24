package avik.MOTOSECURE.MotoSecure5;

import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.libraries.settings.SettingsV;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.MOTOSECURE.utils.MotoSecureLib;


/**
 * Screen Number:
 *
 * <PRE>
 * Device security set to none.
 * Internet available
 * Moto Secure 5 installed.
 * Location enabled.
 *
 * There is a problem for slap in some screens, so after run for en-XM you should verify if slaps are correctly placed and manually fix them if needed.
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure5 {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    private SettingsV mSettings;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    AvikScreenshotAction MotoSecure_Intro_MotoSecure = new AndroidAvikScreenshotAction("MotoSecure_Intro_MotoSecure", true);
    AvikScreenshotAction MotoSecure_Intro_SimplifySecurityManagement = new AndroidAvikScreenshotAction("MotoSecure_Intro_SimplifySecurityManagement", true);
    AvikScreenshotAction MotoSecure_Intro_BoostYourProtection = new AndroidAvikScreenshotAction("MotoSecure_Intro_BoostYourProtection", true);
    AvikScreenshotAction MotoSecure_Intro_MakeSaferConnections = new AndroidAvikScreenshotAction("MotoSecure_Intro_MakeSaferConnections", true);
    AvikScreenshotAction MotoSecure_Welcome_1 = new AndroidAvikScreenshotAction("MotoSecure_Welcome_1", true);
    AvikScreenshotAction MotoSecure_Welcome_2 = new AndroidAvikScreenshotAction("MotoSecure_Welcome_2", true);
    AvikScreenshotAction MotoSecure_Welcome_3 = new AndroidAvikScreenshotAction("MotoSecure_Welcome_3", true);
    AvikScreenshotAction MotoSecure_Welcome_4 = new AndroidAvikScreenshotAction("MotoSecure_Welcome_4", true);
    AvikScreenshotAction MotoSecure_Main = new AndroidAvikScreenshotAction("MotoSecure_Main", true);
    AvikScreenshotAction MotoSecure_Menu = new AndroidAvikScreenshotAction("MotoSecure_Menu", true);
    AvikScreenshotAction MotoSecure_Menu_About = new AndroidAvikScreenshotAction("MotoSecure_Menu_About", true);
    AvikScreenshotAction MotoSecure_Menu_ProtectedByThinkshield = new AndroidAvikScreenshotAction("MotoSecure_Menu_ProtectedByThinkshield", true);
    AvikScreenshotAction MotoSecure_ProtectIfStolen_LockNetworkAndSecurity_Dialog = new AndroidAvikScreenshotAction("MotoSecure_ProtectIfStolen_LockNetworkAndSecurity_Dialog", true);
    AvikScreenshotAction MotoSecure_PINpadScramble_ChangeScreenlockDialog = new AndroidAvikScreenshotAction("MotoSecure_PINpadScramble_ChangeScreenlockDialog", true);
    AvikScreenshotAction MotoSecure_CameraAndMicShield_Dialog = new AndroidAvikScreenshotAction("MotoSecure_CameraAndMicShield_Dialog", true);
    AvikScreenshotAction MotoSecure_CameraAndMicShield = new AndroidAvikScreenshotAction("MotoSecure_CameraAndMicShield", true);
    AvikScreenshotAction MotoSecure_ProtectFromOnlineScammers_Dialog = new AndroidAvikScreenshotAction("MotoSecure_ProtectFromOnlineScammers_Dialog", true);
    AvikScreenshotAction MotoSecure_ProtectFromOnlineScammers_TrustSites_Dialog = new AndroidAvikScreenshotAction("MotoSecure_ProtectFromOnlineScammers_TrustSites_Dialog", true);
    AvikScreenshotAction MotoSecure_NetworkProtection_Dialog = new AndroidAvikScreenshotAction("MotoSecure_NetworkProtection_Dialog", true);
    AvikScreenshotAction MotoSecure_ProtectFromOnlineScammers_AllowList = new AndroidAvikScreenshotAction("MotoSecure_ProtectFromOnlineScammers_AllowList", true);
    AvikScreenshotAction MotoSecure_ScanNow_Scanning = new AndroidAvikScreenshotAction("MotoSecure_ScanNow_Scanning", true);
    AvikScreenshotAction MotoSecure_ScanNow_ScanFinished_Essential = new AndroidAvikScreenshotAction("MotoSecure_ScanNow_ScanFinished_Essential", true);
    AvikScreenshotAction MotoSecure_ScanNow_ScanFinished_Advanced = new AndroidAvikScreenshotAction("MotoSecure_ScanNow_ScanFinished_Advanced", true);
    AvikScreenshotAction MotoSecure_ScanNow_ScanFinished_ViewReport = new AndroidAvikScreenshotAction("MotoSecure_ScanNow_ScanFinished_ViewReport", true);

    //A16.R1
    AvikScreenshotAction MotoSecure_SecurePowerOff_Explore = new AndroidAvikScreenshotAction("MotoSecure_SecurePowerOff_Explore", true);
    AvikScreenshotAction MotoSecure_SecurePowerOff = new AndroidAvikScreenshotAction("MotoSecure_SecurePowerOff", true);
    AvikScreenshotAction MotoSecure_SecurePowerOff_Hint = new AndroidAvikScreenshotAction("MotoSecure_SecurePowerOff_Hint", true);
    AvikScreenshotAction MotoSecure_SecurePowerOff_SetUpAuthentication = new AndroidAvikScreenshotAction("MotoSecure_SecurePowerOff_SetUpAuthentication", true);
    AvikScreenshotAction MotoSecure_SecurePowerOff_RemoveProtect = new AndroidAvikScreenshotAction("MotoSecure_SecurePowerOff_RemoveProtect", true);


    @Before
    public void setUp() throws Exception{
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mSecure = new MotoSecureLib();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            mSettings = new SettingsV();
        }
        mUtility = AvikUtility.getInstance();
        mSecure.clearApp();
        mSecure.forceCloseApp();
        mSettings.turnOnLocation();

        mUtils.grantPermission(mSecure.PACKAGE_NAME, "android.permission.POST_NOTIFICATIONS");
        mUtils.grantPermission(mSecure.SECURECORE_PACKAGE, "android.permission.POST_NOTIFICATIONS");

    }

    @After
    public void tearDown() throws Exception {
        mSecure.forceCloseApp();
        mSettings.forceCloseApp();
        mSettings.clearApp();
        mSecure.clearApp();
        mSettings.removeScreenLock();
        mDevice.pressHome();
    }

    public void captureScreens() throws Exception {
        mSecure.callApp();

        captureOnboarding();
        captureWelcome();
        captureMain();
        captureProtectIfStolen();
        capturePinPadScramble();
        //captureCameraMicShield();
        captureProtectFromOnlineScammers();
        captureNetworkProtection();
        captureSecurePowerOff();

        captureScan();

    }

    @SuppressLint("NewApi")
    private void captureSecurePowerOff() throws Exception {
        String securePowerOff = mUtility.getResourceByStringOnCurrentAppPack("secure_turnoff");

        mUtility.createScrollable().scrollToBeginning(3);
        sleep(Constants.ONE_SECOND);
        mUtility.createScrollable().scrollTextIntoView(securePowerOff);
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(securePowerOff)), Constants.TWO_SECONDS).click();
        sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_SecurePowerOff_Explore);

        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/doneButton"))), Constants.TWO_SECONDS).click();
        sleep(Constants.HALF_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_SecurePowerOff);

        UiObject2 toolbar = mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/collapsing_toolbar"))), Constants.ONE_SECOND);
        UiObject2 topBar = toolbar.findObject(By.clazz(LinearLayout.class.getName()));
        topBar.getChildren().get(topBar.getChildren().size() - 1).click();
        sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_SecurePowerOff_Hint);

        mDevice.pressBack();
        UiObject2 switchButton = mDevice.wait(Until.findObject(By.clazz(Switch.class.getName())),Constants.TWO_SECONDS);
        if(switchButton.isChecked()){
           mSettings.removeScreenLock();
        }

        // To avoid staleObjectException reinstantiate the switch Object
        switchButton = mDevice.wait(Until.findObject(By.clazz(Switch.class.getName())),Constants.TWO_SECONDS);
        switchButton.click();
        sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_SecurePowerOff_SetUpAuthentication);
        mDevice.pressBack();
        sleep(Constants.HALF_SECOND);

        mSettings.setScreenLock("PIN");
        mDevice.pressBack();

        mSettings.launchScreenLockSuggestionSettingsScreen();
        sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/password_entry"))), Constants.TWO_SECONDS).setText("1234");
        mDevice.pressEnter();
        sleep(Constants.HALF_SECOND);

        mUtility.clickOnResourceByPackAndName("com.android.settings", "unlock_set_unlock_off_title");
        sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_SecurePowerOff_RemoveProtect);

        mUtility.pressBackKeySeveralTimes(5);
        mSecure.callApp();
    }

    private void captureScan() throws Exception {
        String scanNow = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "scan_now");
        String essentialProt = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "section_essential_protection");
        String advancedProt = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "section_advanced_protection");


        mUtility.createScrollable().scrollToBeginning(3);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.text(scanNow)), Constants.FIVE_SECONDS).getParent().click();

        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_ScanNow_Scanning);

        mDevice.wait(Until.findObject(By.text(essentialProt)), Constants.ONE_MINUTE).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_ScanNow_ScanFinished_Essential);

        mDevice.pressBack();
        sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.text(scanNow)), Constants.FIVE_SECONDS).click();
        sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(advancedProt)), Constants.ONE_MINUTE).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_ScanNow_ScanFinished_Advanced);

        String viewReportText = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "view_report");
        mDevice.wait(Until.findObject(By.text(viewReportText)), Constants.FIVE_SECONDS).click();
        sleep(Constants.TWO_SECONDS);
        avikHandler.scrollAndTakeScreenshot(MotoSecure_ScanNow_ScanFinished_ViewReport, mUtility.createScrollable(), 2, 2);
    }

    private void captureOnboarding() throws Exception {
        BySelector content = By.res("android:id/content");
        String getStarted = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_get_started");
        String startBtn = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_start");


        mDevice.wait(Until.hasObject(By.text(getStarted)), Constants.TEN_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Intro_MotoSecure);
        mDevice.findObject(By.text(getStarted)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Intro_SimplifySecurityManagement);
        mDevice.findObject(content).getChildren().get(0).getChildren().get(0).getChildren().get(6).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Intro_BoostYourProtection);
        mDevice.findObject(content).getChildren().get(0).getChildren().get(0).getChildren().get(6).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Intro_MakeSaferConnections);
        mDevice.findObject(By.text(startBtn)).click();
        mUtility.sleep(Constants.THREE_SECONDS);
    }

    private void captureWelcome() throws Exception {
        BySelector allowSelector = By.res("com.android.permissioncontroller:id/permission_allow_button");
        String getStarted = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_get_started");
        BySelector button = By.clazz(Button.class);

        //mDevice.wait(Until.findObject(allowSelector), Constants.FIVE_SECONDS).click();

        //Onboard dialog cards. It shows some of the new features of the current version
        //The amount of cards may vary depending on the version

        mUtility.sleep(Constants.TWO_SECONDS);

        avikHandler.scrollAndTakeScreenshot(MotoSecure_Welcome_1, mUtility.createScrollable(), 2, 2);

        mDevice.findObject(By.text(getStarted)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        avikHandler.scrollAndTakeScreenshot(MotoSecure_Welcome_2, mUtility.createScrollable(), 2, 2);

        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.pressEnter(); //this was added to avoid error (was throwing StaleObjectException)
        avikHandler.scrollAndTakeScreenshot(MotoSecure_Welcome_3, mUtility.createScrollable(), 2, 2);

        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.pressEnter();
        avikHandler.scrollAndTakeScreenshot(MotoSecure_Welcome_4, mUtility.createScrollable(), 2, 2);

        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
    }

    private void captureMain() throws Exception{
        String about = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "nav_drawer_item_title_about");
        String protectedBy = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "main_screen_subtitle");

        mUtility.createScrollable().scrollToBeginning(3);
        //Main screen
        avikHandler.scrollAndTakeScreenshot(MotoSecure_Main, mUtility.createScrollable(), 3, 3);
        mUtility.createScrollable().scrollToBeginning(3);

        //Side menu
        mSecure.openMenu();
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Menu);

        //Side menu > About
        mDevice.wait(Until.findObject(By.text(about)), Constants.ONE_SECOND).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Menu_About);
        mDevice.pressBack();
    }

    private void captureProtectIfStolen() throws Exception{
        String protectIfStolen = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "feature_lock_network_security");
        String gotIt = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "pin_dialog_button1");
        mUtility.createScrollable().scrollTextIntoView(protectIfStolen);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(protectIfStolen)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        checkIfScrollableAndCapture(By.scrollable(true),MotoSecure_ProtectIfStolen_LockNetworkAndSecurity_Dialog);
        mDevice.findObject(By.clazz(Button.class)).click();
        mDevice.pressBack();
        mUtility.sleep(Constants.TWO_SECONDS);

    }

    private void capturePinPadScramble() throws Exception{
        String pinPadScramble = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "security_feature_pin_pad_scramble");
        String gotIt = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "pin_dialog_button1");
        BySelector doneBtn = By.res("com.motorola.coresettingsext:id/doneButton");
        mUtility.createScrollable().scrollTextIntoView(pinPadScramble);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(pinPadScramble)).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_PINpadScramble_ChangeScreenlockDialog);
        mDevice.findObject(By.text(gotIt)).click();
    }

    private void captureCameraMicShield() throws Exception{
        String cameraMicShield = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "security_feature_screen_privacy");

        mUtility.createScrollable().scrollTextIntoView(cameraMicShield);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(cameraMicShield)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_CameraAndMicShield_Dialog);
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        //removing bc its a coresettingsext screen
        //mUtility.takeAvikScreenshotWithFlag(MotoSecure_CameraAndMicShield);
        mDevice.pressBack();
        mUtility.sleep(Constants.TWO_SECONDS);
    }

    private void captureProtectFromOnlineScammers() throws Exception{
        String phishing = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "security_feature_phishing_detection");
        String allowList = mUtility.getResourceByPackAndStringKey("com.motorola.coresettingsext", "phishing_detection_allow_list_title");

        mUtility.createScrollable().scrollTextIntoView(phishing);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.click(0,0);
        mDevice.wait(Until.findObject(By.text(phishing)), Constants.FIVE_SECONDS).getParent().click();
        mUtility.sleep(Constants.TWO_SECONDS);
        avikHandler.scrollAndTakeScreenshot(MotoSecure_ProtectFromOnlineScammers_Dialog, mUtility.createScrollable(), 2, 2);
        mDevice.findObject(By.clazz(ImageButton.class)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_ProtectFromOnlineScammers_TrustSites_Dialog);
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(allowList)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_ProtectFromOnlineScammers_AllowList);
        mUtility.pressBackKeySeveralTimes(2);
        mUtility.sleep(Constants.TWO_SECONDS);

    }

    private void captureNetworkProtection() throws Exception{
        String networkProtection = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "security_feature_network_protection");

        mUtility.createScrollable().scrollTextIntoView(networkProtection);
        List<UiObject2> networkProtectionObjects = mDevice.wait(Until.findObjects(By.text(networkProtection)),Constants.FIVE_SECONDS);
        UiObject2 networkProtectionButton = networkProtectionObjects.get(networkProtectionObjects.size() - 1);
        networkProtectionButton.click();

        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_NetworkProtection_Dialog);
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtility.sleep(Constants.TWO_SECONDS);
    }

    private void checkIfScrollableAndCapture(BySelector scroll, AvikScreenshotAction screenName) throws Exception{
        UiObject2 scrollObj = mDevice.findObject(scroll);
        if (scrollObj == null)
                return;

        if (scrollObj.isScrollable()){
            avikHandler.scrollAndTakeScreenshot(screenName ,mUtility.createScrollable(), 2, 2);
        } else {
            avikHandler.takeScreenshot(screenName);
        }
    }

    @Test
    public void main() throws Exception {
        try {
            captureScreens();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw e;
        }

    }


}
