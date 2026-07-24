package avik.motonote.auto;

import android.graphics.Rect;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.impl.LocaleEnum;
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

import avik.motonote.util.Stylus;
import avik.motonote.util.Util;

//PRE-REQUISITES:

/**
 * <PRE>
 * Screen Number:
 * Execution Time: 105 seconds per locale ~ 3045 seconds or ~51 minutes
 * <p>
 * Preconditions:
 * 1) PEN INSIDE DEVICE, NO NEED TO REMOVE
 * 2) ENABLE GESTURE NAVIGATION
 * 3) Put app icon to home screen
 * 4) Hold app icon, then add all types to home screen, and hold the app icon at the first place.
 * 5) push the pictures under res.DCIM.Camera to phone
 * *
 *
 * <p>
 * Manual screens:
 */
// =======================================================

@RunWith(AndroidJUnit4.class)
public class MotoNoteA16R2_ActiveStylusUrusNotificationChannel {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    private final AvikScreenshotAction ActiveStylus_NotificationCategories = new AndroidAvikScreenshotAction("ActiveStylus_NotificationCategories", LocaleEnum.AR_EG.toLocale(), LocaleEnum.DE_DE.toLocale(), LocaleEnum.EN_GB.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.IT_IT.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_StylusChargingError_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_StylusChargingError_NotificationChannel_Scrolling1", LocaleEnum.IT_IT.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale());
    private final AvikScreenshotAction ActiveStylus_StylusChargingError_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_StylusChargingError_NotificationChannel_Scrolling2", LocaleEnum.DE_DE.toLocale(), LocaleEnum.IT_IT.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_StylusRemoved_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_StylusRemoved_NotificationChannel_Scrolling1", LocaleEnum.FR_FR.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale());
    private final AvikScreenshotAction ActiveStylus_StylusRemoved_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_StylusRemoved_NotificationChannel_Scrolling2", LocaleEnum.DE_DE.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.ZH_CN.toLocale());
    //private AvikScreenshotAction ActiveStylus_NotificationCategories = new AndroidAvikScreenshotAction("ActiveStylus_NotificationCategories", true);
    //private AvikScreenshotAction ActiveStylus_StylusChargingError_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_StylusChargingError_NotificationChannel_Scrolling1", true);
    //private AvikScreenshotAction ActiveStylus_StylusChargingError_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_StylusChargingError_NotificationChannel_Scrolling2", true);
    //private AvikScreenshotAction ActiveStylus_TimeToCharge_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_TimeToCharge_NotificationChannel_Scrolling1", true);
    //private AvikScreenshotAction ActiveStylus_TimeToCharge_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_TimeToCharge_NotificationChannel_Scrolling2", true);
    //private AvikScreenshotAction ActiveStylus_StylusRemoved_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_StylusRemoved_NotificationChannel_Scrolling1", true);
    //private AvikScreenshotAction ActiveStylus_StylusRemoved_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_StylusRemoved_NotificationChannel_Scrolling2", true);
    private final AvikScreenshotAction ActiveStylus_TimeToCharge_NotificationChannel_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_TimeToCharge_NotificationChannel_Scrolling1", LocaleEnum.EN_GB.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale());
    private final AvikScreenshotAction ActiveStylus_TimeToCharge_NotificationChannel_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_TimeToCharge_NotificationChannel_Scrolling2", LocaleEnum.DE_DE.toLocale(), LocaleEnum.EN_GB.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.JA_JP.toLocale());
    public UiObject2 moreOption;
    Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private Util mUtil;
    private AvikUiDevice mDevice;
    Thread pressListGridButton = new Thread(new Runnable() {
        public void run() {
            UiObject2 listGridButton = mDevice.findObject(By.res("com.motorola.stylus:id/display_style"));
            Rect position = listGridButton.getVisibleBounds();
            mDevice.swipe(position.centerX(), position.centerY(), position.centerX(), position.centerY(), 400);
        }
    });
    Thread pressSearchButton = new Thread(new Runnable() {
        public void run() {
            UiObject2 listGridButton = mDevice.findObject(By.res("com.motorola.stylus:id/action_search"));
            Rect position = listGridButton.getVisibleBounds();
            mDevice.swipe(position.centerX(), position.centerY(), position.centerX(), position.centerY(), 400);
        }
    });
    private ObjectUtils mObjectUtils;
    private AvikUtility mUtility;
    private Stylus mStylus;

    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtility = AvikUtility.getInstance();
        mUtil = new Util();
        mObjectUtils = new ObjectUtils();
        mUtility.pressBackKeySeveralTimes(3);
        mStylus = new Stylus();
        mStylus.forceCloseApp();
        mStylus.clearApp();
        mDevice.pressHome();
        mUtility.sleep(Constants.ONE_SECOND);
    }

    @After
    public void tearDown() throws Exception {
        mStylus.forceCloseApp();
        //mStylus.clearApp();
    }

    //Preconditions: put moto note app in home screen and change the position(x, y) in function holdAppIconOnHomeScreen
    public void captureScreensOfActiveStylus() throws Exception {
        mObjectUtils.runCommand("am start com.motorola.activestylus/com.motorola.stylusmanager.MainActivity");
        mUtility.sleep(Constants.ONE_SECOND);
        String activeStylusPkg = "com.motorola.activestylus";
        String openWhenLocked = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "moto_note_when_locked_title");
        String stylusActions = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_actions");
        String doublePress = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "double_press_button");
        String longPress = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "long_press_button");
        String stylusToolbar = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_toolbar");
        String stylusToolbarDesc = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "settings_shortcuts_edit_introduce");
        String stylusOutOfSlot = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_out_of_its_case_passive");
        String lastKnownLocation = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "last_known_location");
        String cancel = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "cancel_msg");
        String stylusTips = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_tips");
        String getStartedMsg = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "get_started_msg");
        String about = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "about");
        String next = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "next");
        String done = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "done");
        String openSourceLicense = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "open_source_license");
        String checkUpdates = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "check_updates");
        String checkForUpdate = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "check_for_update");

        mDevice.wait(Until.hasObject(By.text(getStartedMsg)), Constants.THREE_SECONDS);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(getStartedMsg)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.desc(next)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.desc(done)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.pressBackKeySeveralTimes(4);
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.openAppInfoByPack(activeStylusPkg);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.clazz("android.widget.ScrollView")), Constants.THREE_SECONDS).getChildren().get(4).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.android.settings:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(4).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_NotificationCategories);
        mUtility.sleep(Constants.HALF_SECOND);
        String stylusChargingError = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "notification_charging_error_title");
        String timeToCharge = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "notification_low_battery_title");
        String stylusRemoved = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "title_active_stylus_loss");

        mDevice.wait(Until.findObject(By.text(stylusChargingError)), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_StylusChargingError_NotificationChannel_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_StylusChargingError_NotificationChannel_Scrolling2);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.res("com.android.settings:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(4).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(timeToCharge)), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_TimeToCharge_NotificationChannel_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_TimeToCharge_NotificationChannel_Scrolling2);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.res("com.android.settings:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(4).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(stylusRemoved)), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_StylusRemoved_NotificationChannel_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_StylusRemoved_NotificationChannel_Scrolling2);
        mUtility.sleep(Constants.HALF_SECOND);
        mUtility.pressBackKeySeveralTimes(4);
        mUtility.sleep(Constants.ONE_SECOND);
    }

    @Test
    public void testMain() {
        try {
            captureScreensOfActiveStylus();
////           captureScreensOfMotoNoteMain();
//            captureScreensOfMergedNote();
//            captureScreensOfDrawNote();
//            captureScreensOfChecklistNote();
//            captureScreensOfSearchAndDelete();
//            captureScreensOfMotoNotewithAccountLogin();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }
    }
}