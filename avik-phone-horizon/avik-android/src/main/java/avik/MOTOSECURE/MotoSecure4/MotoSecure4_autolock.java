package avik.MOTOSECURE.MotoSecure4;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Switch;

import androidx.appcompat.widget.SwitchCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.libraries.settings.SettingsV;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
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
import java.util.regex.Pattern;

import avik.MOTOSECURE.utils.MotoSecureLib;


/**
 * Screen Number: 16 Time: 102.976.
 *
 * <PRE>
 * Device security set to .
 * Internet available
 * Moto Secure 4.0 installed.
 * Location enabled.
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure4_autolock {

    private static final String AVIK_PLACE = "AViK";
    private static final String AVIK_PLACE2 = "AViK2";

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    private SettingsV mSettings;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    AvikScreenshotAction MotoSecure_AutoLock_Dialog_1 = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_Dialog_1", true);
    AvikScreenshotAction MotoSecure_AutoLock_Dialog_2 = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_Dialog_2", true);
    AvikScreenshotAction MotoSecure_AutoLock_Dialog_3 = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_Dialog_3", true);
    AvikScreenshotAction MotoSecure_AutoLock = new AndroidAvikScreenshotAction("MotoSecure_AutoLock", true);
    AvikScreenshotAction MotoSecure_AutoLock_AddPlaceOrDeviceFirst = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_AddPlaceOrDeviceFirst", true);
    AvikScreenshotAction MotoSecure_AutoLock_AddTrustedPlaceOrWifiNetwork = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_AddTrustedPlaceOrWifiNetwork", true);

    AvikScreenshotAction MotoSecure_AutoLock_TrustedSettings = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedSettings", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces_LocationPermissions = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces_LocationPermissions", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedDevices = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedDevices", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedDevices_ConnectedDevices = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedDevices_ConnectedDevices", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedWifi_Permission = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedWifi_Permission", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedWifi = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedWifi", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedWifi_SavedNetworks = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedWifi_SavedNetworks", true);

    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_SaveLocation = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_SaveLocation", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_RenameLocation = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_RenameLocation", true);
    AvikScreenshotAction MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_AlreadyExists_Toast = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_AlreadyExists_Toast", true);
    AvikScreenshotAction MotoSecure_AutoLock_SetUpAScreenLock_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_SetUpAScreenLock_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_SetUpDoubleAuth_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_SetUpDoubleAuth_Dialog", true);




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
    }

    public void testMain() throws Exception {

        mSecure.callApp();
        mSecure.skipOnboard();

        //Auto lock
        String autoLock = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "main_screen_tip_title_auto_lock");
        String explore = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "btn_explore");
        mUtility.createScrollable().scrollTextIntoView(autoLock);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(autoLock)), Constants.FIVE_SECONDS).click();

        //New dialog
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_Dialog_1);
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_Dialog_2);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_Dialog_3);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(explore)).click();

        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock);

        // Auto lock > Trusted places
        // No Longer needed
//        String useAutoLock = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "use_auto_lock");
//        mDevice.findObject(By.text(useAutoLock)).getParent().getChildren().get(4).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_AddPlaceOrDeviceFirst);
//        mDevice.pressBack();
//        mUtils.sleep(Constants.TWO_SECONDS);
//
//
//        //Auto lock > Use two-factor authentication
//        String use2fa = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "new_use_2fa");
//        mDevice.findObject(By.text(use2fa)).getParent().getChildren().get(7).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_AddTrustedPlaceOrWifiNetwork);
//        mDevice.pressBack();


        //Trusted settings
        //String trustedSettings = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_settings");
        //mDevice.findObject(By.text(trustedSettings)).click();
        //mUtils.sleep(Constants.ONE_SECOND);
        //mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedSettings);

        String trustedPlaces = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_places");
        mDevice.wait(Until.findObject(By.text(trustedPlaces)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces);

        String addTrustedPlace = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "add_trusted_place");
        mDevice.wait(Until.findObject(By.text(addTrustedPlace)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces_LocationPermissions);

        String whileUsingApp = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "allow_use_precise_location_title");
        String allowAllTheTime = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "allow_background_permission");

        Point myLocationPosition = new Point(0, 0);

        mDevice.wait(Until.findObject(By.text(whileUsingApp)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.text(allowAllTheTime)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.android.permissioncontroller:id/allow_always_radio_button")).click();
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);

        if (mDevice.hasObject(By.res("android:id/button1"))){
            mUtility.skipAndroidButton1();
            mUtils.sleep(Constants.TWO_SECONDS);
        }

        mUtils.sleep(Constants.THREE_SECONDS);
        UiObject2 myLocationIcon = mDevice.findObject(By.clazz("androidx.compose.ui.viewinterop.ViewFactoryHolder"))
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(1)
                .getChildren().get(0);
        myLocationPosition = myLocationIcon.getVisibleCenter();


        mDevice.click(myLocationPosition.x, myLocationPosition.y);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace);

        String saveLocation = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "location_save_button");
        mDevice.wait(Until.findObject(By.text(saveLocation)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_SaveLocation);

        mDevice.findObject(By.clazz("android.widget.EditText")).setText(AVIK_PLACE);
        mUtils.clickOnResourceByPackAndName("location_save_ok");
        mUtils.sleep(Constants.TWO_SECONDS);
        UiObject2 editButton = mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(1);
        UiObject2 deleteButton = mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(2);
        editButton.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_RenameLocation);
        mDevice.findObject(By.clazz("android.widget.EditText")).setText(AVIK_PLACE2);
        mUtils.clickOnResourceByPackAndName("location_save_ok");

        mDevice.wait(Until.findObject(By.text(addTrustedPlace)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.click(myLocationPosition.x, myLocationPosition.y);
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.wait(Until.findObject(By.text(saveLocation)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.clazz("android.widget.EditText")).setText(AVIK_PLACE);
        mUtils.clickOnResourceByPackAndName("location_save_ok");
        mUtils.sleep(800);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedPlaces_AddTrustedPlace_AlreadyExists_Toast);
        mUtility.pressBackKeySeveralTimes(2);

        //Auto lock > Trusted devices
        String trustedDevices = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_devices");
        mDevice.wait(Until.findObject(By.text(trustedDevices)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/permission_allow_button"))),Constants.TWO_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedDevices);

//        String addTrustedDevice = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "pair_new_device");
//        mDevice.wait(Until.findObject(By.text(addTrustedDevice)), Constants.FIVE_SECONDS).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_button")).click();
//        mDevice.wait(Until.findObject(By.text(addTrustedDevice)), Constants.FIVE_SECONDS).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedDevices_ConnectedDevices);
//        mUtility.pressBackKeySeveralTimes(2);
        mDevice.pressBack();

        //Auto lock > trusted wifi
        String trustedWifi = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_wifi");
        String addNetwork = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "add_trusted_wifi");

        mDevice.wait(Until.findObject(By.text(trustedWifi)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);

        final int WIFI_SWITCH_POS = 3;
        UiObject2 scrollView = mDevice.wait(Until.findObject(By.clazz(ScrollView.class.getName())),Constants.TWO_SECONDS);
        UiObject2 switchButton = scrollView.getChildren().get(WIFI_SWITCH_POS);
        switchButton.click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedWifi);

        mDevice.wait(Until.findObject(By.text(addNetwork)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
//
//        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedWifi_Permission);
//        ////Auto lock > trusted wifi > permissions
//        mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(3).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(5).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("com.android.permissioncontroller:id/allow_always_radio_button")).click();
//        mDevice.pressBack();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_TrustedWifi_SavedNetworks);
        //mUtility.pressBackKeySeveralTimes(3);
        mDevice.pressBack();

        //Dialogs for when there is no lock mode and fingerprint
        final int AUTOLOCK_SWITCH_POS = 4;
        UiObject2 useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(AUTOLOCK_SWITCH_POS);

        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_SetUpAScreenLock_Dialog);
        mDevice.pressBack();

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
