package avik.MOTOSECURE.MotoSecure4;

import static com.motorola.frevoutils.code.utils.Constants.TWO_SECONDS;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ScrollView;

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

import avik.MOTOSECURE.utils.MotoSecureLib;


/**
 * Screen Number: 4 Time: 137.181
 *
 * <PRE>
 * Pair a bluetooth device
 * Setup screen lock to PIN = 1234
 * Setup a fingerprint
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure4_autolock_activated {

    private static final String AVIK_PLACE = "AViK";

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    private SettingsV mSettings;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    private AvikUiDevice mDevice2;

    AvikScreenshotAction MotoSecure_AutoLock_2fAOff_Active_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_2fAOff_Active_Dialog ", true);
    AvikScreenshotAction MotoSecure_AutoLock_2fAOff_Fingerprint_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_2fAOff_Fingerprint_Dialog ", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveLocation_AutoLockOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveLocation_AutoLockOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveLocation_AutoLockAnd2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveLocation_AutoLockAnd2fAOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveLocation_2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveLocation_2fAOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveDevice_AutoLockOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveDevice_AutoLockOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveDevice_AutoLockAnd2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveDevice_AutoLockAnd2fAOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveDevice_2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveDevice_2fAOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveWifi_AutoLockOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveWifi_AutoLockOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveWifi_2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveWifi_2fAOff_Dialog", true);
    AvikScreenshotAction MotoSecure_AutoLock_RemoveWifi_AutoLockAnd2fAOff_Dialog = new AndroidAvikScreenshotAction("MotoSecure_AutoLock_RemoveWifi_AutoLockAnd2fAOff_Dialog", true);


    @Before
    public void setUp() throws Exception {
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


    public void clickNetworkSwitch() throws Exception {
        UiObject2 switchButton = mDevice.wait(Until.findObject(By
                .hasParent(By.clazz(ScrollView.class.getName()))
                .clazz(View.class.getName())
                .clickable(true)), Constants.TWO_SECONDS);
        switchButton.click();
        mUtils.sleep(Constants.ONE_SECOND);
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
        mDevice.findObject(By.clazz(Button.class)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(explore)).click();
        mUtils.sleep(Constants.ONE_SECOND);

        UiObject2 useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(4);
        UiObject2 use2faSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(7);
        String trustedSettings = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_settings");
        String trustedPlaces = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_places");

        ///Adding a trust place
        mDevice.wait(Until.findObject(By.text(trustedPlaces)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        String addTrustedPlace = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "add_trusted_place");
        mDevice.wait(Until.findObject(By.text(addTrustedPlace)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);

        String whileUsingApp = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "allow_use_precise_location_title");
        String allowAllTheTime = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "allow_background_permission");

        Point myLocationPosition = new Point(0, 0);

        mDevice.wait(Until.findObject(By.text(whileUsingApp)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")).click();
        mUtils.sleep(TWO_SECONDS);
        mDevice.wait(Until.findObject(By.text(allowAllTheTime)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(TWO_SECONDS);
        mDevice.findObject(By.res("com.android.permissioncontroller:id/allow_always_radio_button")).click();
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);

        if (mDevice.hasObject(By.res("android:id/button1"))) {
            mUtility.skipAndroidButton1();
            mUtils.sleep(TWO_SECONDS);
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

        String saveLocation = mUtils.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "location_save_button");
        mDevice.wait(Until.findObject(By.text(saveLocation)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.clazz("android.widget.EditText")).setText(AVIK_PLACE);
        mUtils.clickOnResourceByPackAndName("location_save_ok");
        mUtils.sleep(TWO_SECONDS);
        mDevice.pressBack();
        //Finished adding a trust place

        //"Use Auto lock" switch on
        mUtility.sleep(TWO_SECONDS);
        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.text(trustedPlaces)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveLocation_AutoLockOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

        //2fa switch on
        mUtils.sleep(TWO_SECONDS);
        useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(4);
        use2faSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(7);
        useAutoLockSwitch.click();
        use2faSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_2fAOff_Active_Dialog);
        mDevice.wait(Until.findObject(By.clazz(Button.class.getName())), TWO_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_2fAOff_Fingerprint_Dialog);
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(trustedPlaces)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveLocation_2fAOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

        //Both switches on
        mUtils.sleep(Constants.ONE_SECOND);
        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(trustedPlaces)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveLocation_AutoLockAnd2fAOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

        //Removing trusted place
        mUtils.sleep(Constants.ONE_SECOND);
        use2faSwitch.click();
        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(trustedPlaces)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(AVIK_PLACE)).getParent().getChildren().get(2).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();

        //Adding trusted wifi
        String trustedWifi = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "trusted_wifi");

        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(trustedWifi)).click();
        clickNetworkSwitch();
        mDevice.pressBack();
        //Finished adding trusted wifi

        //Auto lock switch on
        mUtils.sleep(Constants.ONE_SECOND);
        useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(4);
        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.findObject(By.text(trustedWifi)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        clickNetworkSwitch();
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveWifi_AutoLockOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

        //2fA switch on
        mUtils.sleep(Constants.ONE_SECOND);
        useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(4);
        use2faSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(7);
        useAutoLockSwitch.click();
        use2faSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.FIVE_SECONDS);
        mDevice.findObject(By.text(trustedWifi)).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        clickNetworkSwitch();
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveWifi_2fAOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

        //Both switches on
        mUtils.sleep(Constants.ONE_SECOND);
        useAutoLockSwitch = mDevice.findObject(By.clazz(ScrollView.class)).getChildren().get(4);
        useAutoLockSwitch.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(trustedWifi)).click();
        mUtils.sleep(Constants.ONE_SECOND);
        clickNetworkSwitch();
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_AutoLock_RemoveWifi_AutoLockAnd2fAOff_Dialog);
        mUtility.pressBackKeySeveralTimes(2);

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
