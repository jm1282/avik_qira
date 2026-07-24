package avik.SECUREFOLDER.SecureFolder5.A15R2;


import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.Until;

import com.motorola.avikscripts.R;
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

import java.util.logging.Logger;

import avik.SECUREFOLDER.utils.SecureFolderLib;


/**
 * Screen Number: 36 Execution Time: .
 *
 * <PRE>
 * - Security lock set to none
 * - Add a google account on the device and update the variable EMAIL.
 * <p>
 * there is a lot of mDevice.hasObject around that looks useless. But that's the only workaround I found to avoid null object error
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class SecureFolder5 {

    @Rule
    //public AvikHandler mAvik = new AvikHandler();
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String IMAGE_DIRECTORY = "AVIK_SECUREFOLDER";
    private final String IMAGE_PATH = Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY;
    private final String EMAIL = "cinauto2014@gmail.com";
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    AvikScreenshotAction SecureFolder_Settings_NotConfigured = new AndroidAvikScreenshotAction("SecureFolder_Settings_NotConfigured", true);
    AvikScreenshotAction SecureFolder_Intro_SecureFolder = new AndroidAvikScreenshotAction("SecureFolder_Intro_SecureFolder", true);
    AvikScreenshotAction SecureFolder_Intro_ProtectedContent = new AndroidAvikScreenshotAction("SecureFolder_Intro_ProtectedContent", true);
    AvikScreenshotAction SecureFolder_Intro_DisguiseIcons = new AndroidAvikScreenshotAction("SecureFolder_Intro_DisguiseIcons", true);
    AvikScreenshotAction SecureFolder_Intro_GoogleAccount = new AndroidAvikScreenshotAction("SecureFolder_Intro_GoogleAccount", true);
    AvikScreenshotAction SecureFolder_Intro_GoogleAccount_Dialog = new AndroidAvikScreenshotAction("SecureFolder_Intro_GoogleAccount_Dialog", true);
    AvikScreenshotAction SecureFolder_Start_ChooseALocker = new AndroidAvikScreenshotAction("SecureFolder_Start_ChooseALocker", true);
    AvikScreenshotAction SecureFolder_Start_ChooseALocker_SetPattern = new AndroidAvikScreenshotAction("SecureFolder_Start_ChooseALocker_SetPattern", true);
    AvikScreenshotAction SecureFolder_Start_ChooseALocker_SetPIN = new AndroidAvikScreenshotAction("SecureFolder_Start_ChooseALocker_SetPIN", true);
    AvikScreenshotAction SecureFolder_Start_ChooseALocker_SetPassword = new AndroidAvikScreenshotAction("SecureFolder_Start_ChooseALocker_SetPassword", true);
    AvikScreenshotAction SecureFolder_FingerprintUnlock_Dialog = new AndroidAvikScreenshotAction("SecureFolder_FingerprintUnlock_Dialog", true);
    AvikScreenshotAction SecureFolder_Main = new AndroidAvikScreenshotAction("SecureFolder_Main", true);
    AvikScreenshotAction SecureFolder_StealthMode = new AndroidAvikScreenshotAction("SecureFolder_StealthMode", true);
    AvikScreenshotAction SecureFolder_StealthMode_EnableStealthModeDialog = new AndroidAvikScreenshotAction("SecureFolder_StealthMode_EnableStealthModeDialog", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_FingerprintAuth_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_FingerprintAuth_Dialog", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_FaceUnlock_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_FaceUnlock_Dialog", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_PatternAuth_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_PatternAuth_Dialog", true);
    AvikScreenshotAction SecureFolder_Main_Menu = new AndroidAvikScreenshotAction("SecureFolder_Main_Menu", true);
    AvikScreenshotAction SecureFolder_About = new AndroidAvikScreenshotAction("SecureFolder_About", true);
    AvikScreenshotAction SecureFolder_MoreSettings = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings", true);
    AvikScreenshotAction SecureFolder_MoreSettings_SecureFolderTimeout = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_SecureFolderTimeout", true);
    AvikScreenshotAction SecureFolder_MoreSettings_NotificationManager = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_NotificationManager", true);
    AvikScreenshotAction SecureFolder_MoreSettings_WifiSecurity = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_WifiSecurity", true);
    AvikScreenshotAction SecureFolder_MoreSettings_USBTransfer = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_USBTransfer", true);
    AvikScreenshotAction SecureFolder_ManageFiles = new AndroidAvikScreenshotAction("SecureFolder_ManageFiles", true);
    AvikScreenshotAction SecureFolder_DisguisedIcons = new AndroidAvikScreenshotAction("SecureFolder_DisguisedIcons", true);
    AvikScreenshotAction SecureFolder_DisguisedIcons_NameIsRequired_Toast = new AndroidAvikScreenshotAction("SecureFolder_DisguisedIcons_NameIsRequired_Toast", true);
    AvikScreenshotAction SecureFolder_AddApps_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AddApps_Dialog", true);
    AvikScreenshotAction SecureFolder_AddApps = new AndroidAvikScreenshotAction("SecureFolder_AddApps", true);
    AvikScreenshotAction SecureFolder_AddApps_AppAdded = new AndroidAvikScreenshotAction("SecureFolder_AddApps_AppAdded", true);
    AvikScreenshotAction SecureFolder_AppOptions = new AndroidAvikScreenshotAction("SecureFolder_AppOptions", true);
    AvikScreenshotAction SecureFolder_DeletionAlert = new AndroidAvikScreenshotAction("SecureFolder_DeletionAlert", true);
    AvikScreenshotAction SecureFolder_AddType = new AndroidAvikScreenshotAction("SecureFolder_AddType", true);
    AvikScreenshotAction SecureFolder_Settings_Enabled = new AndroidAvikScreenshotAction("SecureFolder_Settings_Enabled", true);
    AvikScreenshotAction SecureFolder_Settings_SecureFolder = new AndroidAvikScreenshotAction("SecureFolder_Settings_SecureFolder", true);
    AvikScreenshotAction SecureFolder_ManageFiles_MoveOrCopy = new AndroidAvikScreenshotAction("SecureFolder_ManageFiles_MoveOrCopy", true);
    AvikScreenshotAction SecureFolder_ManageFiles_MoveOrCopy_CopyComplete_Toast = new AndroidAvikScreenshotAction("SecureFolder_ManageFiles_MoveOrCopy_CopyComplete_Toast", true);
    AvikScreenshotAction SecureFolder_ManageFiles_MoveOrCopy_MoveComplete_Toast = new AndroidAvikScreenshotAction("SecureFolder_ManageFiles_MoveOrCopy_MoveComplete_Toast", true);
    AvikScreenshotAction SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles", true);
    AvikScreenshotAction SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles_BackupFailed = new AndroidAvikScreenshotAction("SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles_BackupFailed", true);
    AvikScreenshotAction SecureFolder_ActivateSecureFolderRecovery_Dialog = new AndroidAvikScreenshotAction("SecureFolder_ActivateSecureFolderRecovery_Dialog", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_VerifyWithMotoAccount = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_VerifyWithMotoAccount", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_SecureFolderRecovery = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_SecureFolderRecovery", true);
    AvikScreenshotAction SecureFolder_AuthenticationSettings_SecureFolderRecovery_FingerprintRequired_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AuthenticationSettings_SecureFolderRecovery_FingerprintRequired_Dialog", true);
    AvikScreenshotAction SecureFolder_SetSecureFolderTimeout_Dialog = new AndroidAvikScreenshotAction("SecureFolder_SetSecureFolderTimeout_Dialog", true);
    private AvikUtility mUtility;
    private SecureFolderLib mFolder;

    @Before
    public void setUp() throws Exception {
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mFolder = new SecureFolderLib();
        mUtility = AvikUtility.getInstance();
        mFolder.clearApp();
        mFolder.forceCloseApp();

        mUtils.saveImage(
                R.raw.i18n_256,
                Constants.NON_ASCII_IMAGE_FILENAME,
                IMAGE_PATH,
                "image/jpg");
    }

    @After
    public void tearDown() throws Exception {
        mDevice.pressHome();
        mFolder.forceCloseApp();
        mFolder.deleteIfEnabled();
        mUtils.removeAllFilesFromFolder("/storage/emulated/0/" + IMAGE_PATH);
        mDevice.pressHome();
    }

    public void testMain() throws Exception {
        mUtils.startActivityWithAction(
                "com.android.settings", "com.android.settings.security.SECURITY_ADVANCED_SETTINGS");
        UiScrollable list = mUtils.createScrollablebyResourceID("com.android.settings:id/content_parent");
        list.scrollTextIntoView(mFolder.getAppName());
//        mUtils.createScrollable().scrollTextIntoView(mFolder.getAppName());
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Settings_NotConfigured);

        mFolder.callApp();

        if (mDevice.hasObject(By.res("com.android.permissioncontroller:id/permission_allow_button"))) {
            mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_button")).click();
        }

        //Screens showing features
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Intro_SecureFolder);

        String continueText = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "onboarding_continue");
        mDevice.findObject(By.text(continueText)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Intro_ProtectedContent);

        mFolder.clickNextBtn();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Intro_DisguiseIcons);

        mFolder.clickNextBtn();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Intro_GoogleAccount);

        String logIn = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "onboarding_fourth_page_finish_button");
        mDevice.hasObject(By.text(logIn));
        mDevice.findObject(By.text(logIn)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Intro_GoogleAccount_Dialog);

        mDevice.pressBack();
        String skipLogin = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "skip");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(skipLogin)).click();

//        //login
//        mDevice.wait(Until.findObject(By.text(EMAIL)), Constants.FIVE_SECONDS).click();
//        mUtils.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(By.clazz(Button.class)).getParent().click();


        //Screens from security locker method
        String pattern = mUtils.getResourceByPackAndStringKey("com.android.settings", "unlock_set_unlock_pattern_title");
        String pin = mUtils.getResourceByPackAndStringKey("com.android.settings", "unlock_set_unlock_pin_title");
        String password = mUtils.getResourceByPackAndStringKey("com.android.settings", "unlock_set_unlock_password_title");
        mDevice.wait(Until.hasObject(By.text(pattern)), Constants.THREE_MINUTES);
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Start_ChooseALocker);
        //Pattern
        mDevice.findObject(By.text(pattern)).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Start_ChooseALocker_SetPattern);
        mDevice.pressBack();
        //Pin
        mDevice.wait(Until.findObject(By.text(pin)), Constants.TWO_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Start_ChooseALocker_SetPIN);
        mDevice.pressBack();
        mUtility.sleep(Constants.FIVE_SECONDS);
        //Password
        mDevice.wait(Until.findObject(By.text(password)), Constants.TWO_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Start_ChooseALocker_SetPassword);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);


        //Setting up a pin code to proceed
        mDevice.findObject(By.text(pin)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        BySelector passwordSelector = By.res("com.android.settings:id/password_entry");
        mDevice.findObject(passwordSelector).setText(SecureFolderLib.PIN_CODE);
        mDevice.pressEnter();
        mDevice.findObject(passwordSelector).setText(SecureFolderLib.PIN_CODE);
        mDevice.pressEnter();

        //Activate Secure folder recovery dialog (New A15.R2)
        String titleDialog = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "password_reset_requirements_dialog_title");
        String maybeLaterText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "stealth_mode_dialog_dismiss");
        mDevice.wait(Until.findObject(By.text(titleDialog)), Constants.THREE_MINUTES);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_ActivateSecureFolderRecovery_Dialog);
        mDevice.findObject(By.text(maybeLaterText)).click();

        //Fingerprint unlock dialog
        String maybeLater = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "modal_maybe_later");
        mDevice.wait(Until.hasObject(By.text(maybeLater)), Constants.ONE_MINUTE);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_FingerprintUnlock_Dialog);
        mDevice.hasObject(By.text(maybeLater));
        mDevice.findObject(By.text(maybeLater)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        String searchText = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "main_screen_search_hint");
        mDevice.wait(Until.hasObject(By.text(searchText)), Constants.ONE_MINUTE);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Main);

        //Open side menu
        mUtils.sleep(Constants.TWO_SECONDS);
        mFolder.showMenu();
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Main_Menu);
        mUtils.sleep(Constants.TWO_SECONDS);

        //Opening one of the menu options than returning to main to trigger the timeout dialog
        String setTimeoutText = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "inactivity_time_modal_title");
        mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mDevice.wait(Until.findObject(By.text(setTimeoutText)), Constants.TEN_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_SetSecureFolderTimeout_Dialog);
        mDevice.pressBack();

        //Menu > Stealth mode
        mUtils.sleep(Constants.TWO_SECONDS);
        mFolder.showMenu();
        String stealthMode = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "stealth_mode");
        mUtils.sleep(Constants.TWO_SECONDS);
        //mDevice.findObject(By.text(stealthMode)).click(); //n ta indo
        mDevice.hasObject(By.clazz(ScrollView.class)); //tem q botar isso pra essa bença aparecer
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_StealthMode);

        //Turning stealth mode on to trigger dialog
        String stealthModeText = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "hide_secure_vault_toggle_title");
        mDevice.hasObject(By.text(stealthModeText));
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(stealthModeText)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_StealthMode_EnableStealthModeDialog);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mFolder.showMenu();

        //Authentication settings
        String authSettings = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "lock_settings");
        mDevice.hasObject(By.text(authSettings));
        mDevice.findObject(By.text(authSettings)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings);

        //Authentication settings > Use fingerprint
        String useFingerPrintText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "enable_fingerprint_unlock_title");
        mDevice.findObject(By.text(useFingerPrintText)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_FingerprintAuth_Dialog);
        mDevice.pressBack();

//        //Authentication settings > Use face unlock
//        String useFaceunlockText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "enable_face_unlock_text");
//        mDevice.hasObject(By.text(useFaceunlockText));
//        mDevice.findObject(By.text(useFaceunlockText)).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_FaceUnlock_Dialog);
//        mDevice.pressBack();

        //Authentication settings > make pattern invisible
        String makePatternVisibleText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "make_pattern_visible_title");
        mDevice.hasObject(By.text(makePatternVisibleText));
        mDevice.findObject(By.text(makePatternVisibleText)).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_PatternAuth_Dialog);
        mDevice.pressBack();

        //Secure folder recovery
        String secureFolderRecoveryText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "password_reset");
        String signInText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "moto_login_screen_confirm_button");

        mDevice.hasObject(By.text(secureFolderRecoveryText));
        mDevice.wait(Until.findObject(By.text(secureFolderRecoveryText)), Constants.ONE_MINUTE).click();

//        mDevice.hasObject(By.text(signInText));
//        mDevice.wait(Until.findObject(By.text(signInText)), Constants.ONE_MINUTE);
//        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_VerifyWithMotoAccount);
//        mDevice.findObject(By.text(signInText)).click();
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mDevice.findObject(By.res(Pattern.compile(".*:id/re_login_third_google"))).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.wait(Until.findObject(By.text(EMAIL)), Constants.FIVE_SECONDS).click();
//        mUtils.sleep(Constants.HALF_SECOND);
//        mUtility.skipAndroidButton1();

        String useRecovery = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "password_reset_screen_switch");
        mDevice.wait(Until.findObject(By.text(useRecovery)), Constants.ONE_MINUTE);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_SecureFolderRecovery);

        mDevice.hasObject(By.text(useRecovery));
        mDevice.findObject(By.text(useRecovery)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AuthenticationSettings_SecureFolderRecovery_FingerprintRequired_Dialog);
        mUtility.pressBackKeySeveralTimes(3);
        mUtility.sleep(Constants.TWO_SECONDS);


        //Menu > About
        mDevice.hasObject(By.res("android:id/content"));
        mFolder.showMenu();
        String about = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "about");
        mDevice.hasObject(By.text(about));
        mDevice.findObject(By.text(about)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_About);
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);

        //Menu > More Settings
        mFolder.showMenu();
        String moreSettings = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "more_settings_screen_title");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.hasObject(By.text(moreSettings));
        mDevice.findObject(By.text(moreSettings)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings);
        mUtils.sleep(Constants.TWO_SECONDS);

        //Menu > More Settings > Secure folder timeout
        String timeoutText = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "inactivity_time_title");
        mDevice.hasObject(By.text(timeoutText));
        mDevice.findObject(By.text(timeoutText)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_SecureFolderTimeout);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        //Menu > More Settings > Notification Manager
        String notificationManager = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "notification_manager");
        mDevice.hasObject(By.text(notificationManager));
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(notificationManager)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_NotificationManager);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        //Menu > More Settings > Wi-fi security
        String wifiSecurity = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "block_apps_unsecure_network");
        mDevice.click(0, 0);
        mDevice.hasObject(By.text(wifiSecurity));
        mDevice.findObject(By.text(wifiSecurity)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_WifiSecurity);
        mDevice.pressBack();

        //Menu > More Settings > USB transfer
        String usbTransfer = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "usb_options");
        mDevice.hasObject(By.text(usbTransfer));
        mDevice.findObject(By.text(usbTransfer)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_USBTransfer);
        mDevice.pressBack();

        //Menu > More Settings > Delete Secure folder
        String delete = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "delete_secure_vault");
        String deleteBtn = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "delete_profile_modal_affirmative");
        String backup = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "delete_profile_backup_modal_start_confirm");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.hasObject(By.text(delete));
        mDevice.findObject(By.text(delete)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.android.systemui:id/lockPassword")).setText(SecureFolderLib.PIN_CODE);
        mDevice.pressEnter();
        mDevice.wait(Until.findObject(By.text(deleteBtn)), Constants.TEN_SECONDS).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles);
//        mDevice.findObject(By.text(backup)).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(SecureFolder_MoreSettings_DeleteSecureFolder_BackupFiles_BackupFailed);
        mUtility.pressBackKeySeveralTimes(3);


        //Menu > Disguised icons
        mUtils.sleep(Constants.TWO_SECONDS);
        mFolder.showMenu();
        String disguisedIcons = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "custom_icon_and_name_option");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.click(0, 0);
        mDevice.hasObject(By.text(disguisedIcons));
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(disguisedIcons)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.click(0, 0);
        mDevice.hasObject(By.clazz(ScrollView.class));
        mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(3).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_DisguisedIcons);
        mDevice.hasObject(By.clazz(Button.class));
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtils.sleep(300);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_DisguisedIcons_NameIsRequired_Toast);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        //Add apps
        String addApp = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "add_apps_option");
        String gotIt = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "got_it");

        // + add button
        mUtils.clickOnScreenCenter();
        mDevice.hasObject(By.clazz(Button.class));
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddType);

        mDevice.hasObject(By.text(addApp));
        mDevice.findObject(By.text(addApp)).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddApps_Dialog);
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddApps);
        //Adding one app
        mDevice.findObjects(By.clazz(CheckBox.class)).get(0).click();
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddApps_AppAdded);
        mDevice.findObject(By.text(gotIt)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        //Showing app options, then removing it
        String removeApp = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "remove_app");
        UiObject2 app = mDevice.findObject(By.res("android:id/content"))
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(3)
                .getChildren().get(0);
        mDevice.swipe(app.getVisibleCenter().x, app.getVisibleCenter().y, app.getVisibleCenter().x, app.getVisibleCenter().y, 100);
        mUtils.sleep(Constants.TWO_SECONDS);

        //criando um loop para caso as opcoes nao aparecam
        while (!mDevice.hasObject(By.text(removeApp))) {
            mDevice.swipe(app.getVisibleCenter().x, app.getVisibleCenter().y, app.getVisibleCenter().x, app.getVisibleCenter().y, 100);
            mUtils.sleep(Constants.TWO_SECONDS);
        }
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AppOptions);
        mDevice.findObject(By.text(removeApp)).click();


        String yes = mUtils.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "delete_profile_modal_affirmative");
        mUtils.startActivityWithAction(
                "com.android.settings", "com.android.settings.security.SECURITY_ADVANCED_SETTINGS");
        mUtils.createScrollable().scrollTextIntoView(mFolder.getAppName());
        //mUtils.createScrollable().scrollToEnd(2);
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Settings_Enabled);

        mDevice.findObject(By.text(mFolder.APP_NAME)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_Settings_SecureFolder);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(delete)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_DeletionAlert);
        mDevice.findObject(By.text(yes)).click();

    }

    @Test
    public void main() throws Exception {
        try {
            testMain();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw e;
        }

    }
}