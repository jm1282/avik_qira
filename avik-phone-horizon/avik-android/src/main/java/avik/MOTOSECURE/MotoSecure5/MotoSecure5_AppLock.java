package avik.MOTOSECURE.MotoSecure5;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.libraries.settings.SettingsV;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.impl.LocaleEnum;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.MOTOSECURE.utils.MotoSecureLib;

/**
 * Screen Number:
 *
 * <PRE>
 *     Install external app (ie Facebook) and place its shortcut on home screen
 *     Make sure there is only the app installed in the list (Moto Secure > App lock)
 *     Set Lock Screen to None
 * </PRE>
 */

public class MotoSecure5_AppLock {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    private SettingsV mSettings;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    AvikScreenshotAction MotoSecure_AppLock_Dialog1 = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Dialog1", true);
    AvikScreenshotAction MotoSecure_AppLock_Dialog2 = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Dialog2", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod", true);
    AvikScreenshotAction MotoSecure_AppLock = new AndroidAvikScreenshotAction("MotoSecure_AppLock", true);
    AvikScreenshotAction MotoSecure_AppLock_Settings_AlreadyLockedApps = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Settings_AlreadyLockedApps", true);
    AvikScreenshotAction MotoSecure_AppLock_Settings = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Settings", true);
    AvikScreenshotAction MotoSecure_AppLock_Settings_FingerprintRequired = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Settings_FingerprintRequired", true);
    AvikScreenshotAction MotoSecure_AppLock_Settings_FaceUnlockRequired = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Settings_FaceUnlockRequired", true);
    AvikScreenshotAction MotoSecure_AppLock_Settings_AreYouSureDialog = new AndroidAvikScreenshotAction("MotoSecure_AppLock_Settings_AreYouSureDialog", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_ReleaseFingerWhenCompleted = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_ReleaseFingerWhenCompleted", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_MinimumConnectedPoints = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_MinimumConnectedPoints", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_TryAgain = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_TryAgain", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_PatternRecorded = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_PatternRecorded", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_DrawYourPatternAgain = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_DrawYourPatternAgain", true);
    AvikScreenshotAction MotoSecure_AppLock_EnterYourPattern = new AndroidAvikScreenshotAction("MotoSecure_AppLock_EnterYourPattern", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPassword = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPassword", true);
    AvikScreenshotAction MotoSecure_AppLock_EnterYourPassword = new AndroidAvikScreenshotAction("MotoSecure_AppLock_EnterYourPassword", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN_ChooseAStrongPIN = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN_ChooseAStrongPIN", true);
    AvikScreenshotAction MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPIN = new AndroidAvikScreenshotAction("MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPIN", true);
    AvikScreenshotAction MotoSecure_AppLock_EnterYourPIN = new AndroidAvikScreenshotAction("MotoSecure_AppLock_EnterYourPIN", true);
    AvikScreenshotAction MotoSecure_AppLock_OpenApp_EnterPattern = new AndroidAvikScreenshotAction("MotoSecure_AppLock_OpenApp_EnterPattern", true);
    AvikScreenshotAction MotoSecure_AppLock_OpenApp_EnterPassword = new AndroidAvikScreenshotAction("MotoSecure_AppLock_OpenApp_EnterPassword", true);
    AvikScreenshotAction MotoSecure_AppLock_OpenApp_EnterPIN = new AndroidAvikScreenshotAction("MotoSecure_AppLock_OpenApp_EnterPIN", true);


    public BySelector NEXT_BTN = By.res(Pattern.compile(".*:id/nextButton"));
    public BySelector CONFIRM_BTN = By.res(Pattern.compile(".*:id/confirmButton"));
    public BySelector EXTERNAL_APP_NAME = By.res(Pattern.compile(".*:id/app_name"));
    public BySelector EXTERNAL_APP_SWITCH = By.res(Pattern.compile(".*:id/app_state"));


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
        mUtility.runShellCommand("pm clear com.motorola.motosecurecore");
        mSecure.forceCloseApp();
        mUtils.grantPermission(mSecure.PACKAGE_NAME, "android.permission.POST_NOTIFICATIONS");
        mUtils.grantPermission(mSecure.SECURECORE_PACKAGE, "android.permission.POST_NOTIFICATIONS");


    }

    @After
    public void tearDown() throws Exception {
        mSecure.forceCloseApp();
        mUtility.pressBackKeySeveralTimes(5);
        mDevice.pressHome();
    }

    public void captureScreens() throws Exception {

        mSecure.callApp();
        mSecure.skipOnboard();
        mSecure.openAppLock();

        captureAppLock();
        captureAppLockPattern();
        captureAppLockPassword();
        captureAppLockPIN();
    }
    private void captureAppLock() throws Exception{
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Dialog1);

        mDevice.findObject(By.res(Pattern.compile(".*:id/on_board_next_button"))).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Dialog2);

        mDevice.findObject(By.res(Pattern.compile(".*:id/doneButton"))).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod);

        //Setup pin method
        BySelector lockMethodsSelector = By.res("com.motorola.motosecurecore:id/app_lock_password_type");
        UiObject2 pin = mDevice.wait(Until.findObject(lockMethodsSelector), Constants.THREE_SECONDS).getChildren().get(1);
        pin.click();

        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.motosecurecore:id/password_entry")).setText(SettingsV.PIN_CODE);
        mDevice.pressEnter();
        mDevice.findObject(By.res("com.motorola.motosecurecore:id/password_entry")).setText(SettingsV.PIN_CODE);
        mDevice.pressEnter();


        //App lock main screen
        mUtility.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock);

        //activating app on the list
        UiObject2 appSwitch = mDevice.findObject(By.res("com.motorola.motosecurecore:id/app_state"));
        if (!appSwitch.isChecked()) {
            appSwitch.click();
        }
        mDevice.findObject(By.res("com.motorola.motosecurecore:id/btn_app_lock_settings")).click();
        mUtility.sleep(Constants.TWO_SECONDS);

        //mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Settings_AlreadyLockedApps);
        mDevice.pressBack();
        //deactivating
        appSwitch.click();

        //App lock settings
        mDevice.findObject(By.res("com.motorola.motosecurecore:id/btn_app_lock_settings")).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Settings);

        BySelector fingerprintSwitch = By.res("com.motorola.motosecurecore:id/fingerprint_state");
        BySelector faceSwitch = By.res("com.motorola.motosecurecore:id/face_state");
        BySelector closeAppLock = By.res("com.motorola.motosecurecore:id/rl_close_app_locker");
        BySelector resetBtn = By.res("com.motorola.motosecurecore:id/button_ok");

        mDevice.findObject(fingerprintSwitch).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Settings_FingerprintRequired);
        mDevice.pressBack();

        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(faceSwitch).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Settings_FaceUnlockRequired);
        mDevice.pressBack();

        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(closeAppLock).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_Settings_AreYouSureDialog);
        mDevice.wait(Until.findObject(resetBtn), Constants.FIVE_SECONDS).click();
        mSecure.forceCloseApp();
    }

    private void captureAppLockPattern() throws Exception{
        String later = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_maybe_later");
        mSecure.callApp();
        mDevice.wait(Until.findObject(By.text(later)), Constants.FIVE_SECONDS).click();
        mSecure.openAppLock();

        String pattern = mUtility.getResourceByPackAndStringKey(mSecure.SECURECORE_PACKAGE,"app_locker_dialog_pattern");


        mDevice.findObject(By.text(pattern)).click();
        mUtility.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern);

        BySelector lockPatternSelector = By.res("com.motorola.motosecurecore:id/lockPattern");
        UiObject2 patternView = mDevice.findObject(lockPatternSelector);
        Rect patternBounds = patternView.getVisibleBounds();

        capturePatternIncomplete(patternBounds);

        mSettings.drawPattern(patternBounds);
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_PatternRecorded);

        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(NEXT_BTN).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_DrawYourPatternAgain);

        //updating patternBounds position (position changes sometimes because of the strings length)
        patternView = mDevice.findObject(lockPatternSelector);
        patternBounds = patternView.getVisibleBounds();

        mSettings.drawPattern(patternBounds);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(CONFIRM_BTN).click();
        mUtility.sleep(Constants.THREE_SECONDS);

        if (!mDevice.findObject(EXTERNAL_APP_SWITCH).isChecked()){
            mDevice.findObject(EXTERNAL_APP_SWITCH).click();
        }
        String appName = mDevice.findObject(EXTERNAL_APP_NAME).getText();

        mDevice.pressBack();
        mSecure.openAppLock();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_EnterYourPattern);
        mSecure.forceCloseApp();

        mDevice.pressHome();
        mDevice.wait(Until.findObject(By.text(appName)), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_OpenApp_EnterPattern);
        mDevice.pressBack();

        deleteLockMethod();
    }

    private void captureAppLockPassword() throws Exception{
        String later = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_maybe_later");
        mSecure.callApp();
        mDevice.wait(Until.findObject(By.text(later)), Constants.FIVE_SECONDS).click();
        mSecure.openAppLock();

        String password = mUtility.getResourceByPackAndStringKey(mSecure.SECURECORE_PACKAGE,"app_locker_dialog_password");
        String pin = mUtility.getResourceByPackAndStringKey(mSecure.SECURECORE_PACKAGE,"app_locker_dialog_pin");


        mDevice.findObject(By.text(password)).click();
        mUtility.sleep(Constants.TWO_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword);

        BySelector passwordEntrySelector = By.res(Pattern.compile(".*:id/password_entry"));

        mDevice.findObject(passwordEntrySelector).setText(SettingsV.PASSWORD_CODE);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(NEXT_BTN).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPassword);

        mDevice.findObject(passwordEntrySelector).setText(SettingsV.PASSWORD_CODE);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(CONFIRM_BTN).click();
        mUtility.sleep(Constants.ONE_SECOND);

        if (!mDevice.findObject(EXTERNAL_APP_SWITCH).isChecked()){
            mDevice.findObject(EXTERNAL_APP_SWITCH).click();
        }
        String appName = mDevice.findObject(EXTERNAL_APP_NAME).getText();

        mDevice.pressBack();
        mSecure.openAppLock();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_EnterYourPassword);
        mSecure.forceCloseApp();

        mDevice.pressHome();
        mDevice.wait(Until.findObject(By.text(appName)), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_OpenApp_EnterPassword);
        mDevice.pressBack();
        mDevice.pressHome();
        mUtility.sleep(Constants.TWO_SECONDS);

        deleteLockMethod();
    }

    private void captureAppLockPIN() throws Exception{
        String later = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_maybe_later");
        mSecure.callApp();
        mUtility.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(By.text(later))){
            mDevice.wait(Until.findObject(By.text(later)), Constants.TEN_SECONDS).click();
        }
        mSecure.openAppLock();

        String pin = mUtility.getResourceByPackAndStringKey(mSecure.SECURECORE_PACKAGE,"app_locker_dialog_pin");


        mDevice.findObject(By.text(pin)).click();
        mUtility.sleep(Constants.FIVE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN);

        BySelector pinEntrySelector = By.res(Pattern.compile(".*:id/password_entry"));

        mDevice.findObject(pinEntrySelector).setText(SettingsV.PIN_CODE);
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPIN_ChooseAStrongPIN);
        mDevice.findObject(NEXT_BTN).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPassword_ReenterPIN);

        mDevice.findObject(pinEntrySelector).setText(SettingsV.PIN_CODE);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(CONFIRM_BTN).click();
        mUtility.sleep(Constants.ONE_SECOND);

        if (!mDevice.findObject(EXTERNAL_APP_SWITCH).isChecked()){
            mDevice.findObject(EXTERNAL_APP_SWITCH).click();
        }
        String appName = mDevice.findObject(EXTERNAL_APP_NAME).getText();

        mDevice.pressBack();
        mSecure.openAppLock();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_EnterYourPIN);
        mSecure.forceCloseApp();

        mDevice.pressHome();
        mDevice.wait(Until.findObject(By.text(appName)), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_OpenApp_EnterPIN);
        mDevice.pressBack();

        deleteLockMethod();
    }

    private void capturePatternIncomplete(Rect patternBounds) throws Exception {
        Thread t = new Thread(){
            public void run() {
                String releaseFinger;
                try {
                    releaseFinger = mUtility.getResourceByPackAndStringKey(mSecure.SECURECORE_PACKAGE,"app_locker_set_pattern_hint2");
                    mDevice.wait(Until.hasObject(By.text(releaseFinger)), Constants.TEN_SECONDS);
                } catch (Exception e) {
                }
                try {
                    mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_ReleaseFingerWhenCompleted);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        t.start();
        mDevice.drag(patternBounds.centerX(), patternBounds.centerY(), patternBounds.left, patternBounds.centerY(), 100);
        t.join();
        mUtility.sleep(Constants.HALF_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AppLock_ChooseAnAppLockMethod_SetAnAppLockPattern_TryAgain);
    }

    private void deleteLockMethod() throws Exception {
        mUtility.runShellCommand("am start -n com.motorola.motosecurecore/com.motorola.motosecurecore.activity.AppLockerSettingsActivity");
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/rl_close_app_locker"))), Constants.THREE_SECONDS).click();
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/button_ok"))), Constants.THREE_SECONDS).click();
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
