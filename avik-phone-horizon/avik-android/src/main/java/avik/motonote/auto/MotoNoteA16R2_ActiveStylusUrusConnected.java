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
 * Execution Time: .
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
public class MotoNoteA16R2_ActiveStylusUrusConnected {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    private final AvikScreenshotAction ActiveStylus_Settings_About = new AndroidAvikScreenshotAction("ActiveStylus_Settings_About", LocaleEnum.DE_DE.toLocale(), LocaleEnum.EN_GB.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.IT_IT.toLocale(), LocaleEnum.PL_PL.toLocale(), LocaleEnum.PT_BR.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_Settings_OTA_CheckForUpdate = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_CheckForUpdate", LocaleEnum.IT_IT.toLocale());
    private final AvikScreenshotAction ActiveStylus_Settings_OTA_Update_Checking = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_Update_Checking", LocaleEnum.IT_IT.toLocale());
    private final AvikScreenshotAction ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling1", true);
    private final AvikScreenshotAction ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling2", true);
    private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling1", LocaleEnum.AR_EG.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.IT_IT.toLocale(), LocaleEnum.JA_JP.toLocale());
    //private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling1", false);
    //private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling2", false);
    //private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling3 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling3", false);
    //private final AvikScreenshotAction ActiveStylus_Settings_OTA_CheckForUpdate = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_CheckForUpdate", false);
    //private final AvikScreenshotAction ActiveStylus_Settings_OTA_Update_Checking = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_Update_Checking", false);
    private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling2", LocaleEnum.AR_EG.toLocale(), LocaleEnum.EN_GB.toLocale(), LocaleEnum.ES_US.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.IT_IT.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_Settings_SmartPen_Scrolling3 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Scrolling3", LocaleEnum.ES_US.toLocale());
    /// /Skip UpToDate screen due to FW update risks
    //private final AvikScreenshotAction ActiveStylus_Settings_OTA_VersionUpToDate = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_VersionUpToDate", false);
    //private final AvikScreenshotAction ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling1", true);
    //private final AvikScreenshotAction ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling2", true);
    //private final AvikScreenshotAction ActiveStylus_Settings_About = new AndroidAvikScreenshotAction("ActiveStylus_Settings_About", false);
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
//        mStylus.clearApp();
        mDevice.pressHome();
        mUtility.sleep(Constants.ONE_SECOND);
//        moreOption = mDevice.findObject(mStylus.moreOptionsButton);
    }

    @After
    public void tearDown() throws Exception {
        mStylus.forceCloseApp();
//        mUtility.runShellCommand("sendevent /dev/input/event8 5 15 1");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.runShellCommand("sendevent /dev/input/event8 0 0 0");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mStylus.clearApp();
//        logger.info("Coloque a caneta no device");
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
        String checkUpdates = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "updates");
        String checkForUpdate = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "check_for_update");
        String versionUpToDate = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "system_up_to_date");
        String doneButton = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "button_done");

        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_SmartPen_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_SmartPen_Scrolling2);
        mUtility.sleep(Constants.HALF_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_SmartPen_Scrolling3);
        mUtility.sleep(Constants.HALF_SECOND);

        mDevice.wait(Until.findObject(By.text(checkUpdates)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_CheckForUpdate);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(checkForUpdate)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(400);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_Update_Checking);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.hasObject(By.text(versionUpToDate)), Constants.TEN_SECONDS);
        mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_VersionUpToDate);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        mUtil.swipeFromCenterToTop();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_UpdateAvailable_Scrolling2);
        mUtility.sleep(Constants.HALF_SECOND);
        mUtil.swipeFromCenterToTop();
        mDevice.pressBack();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.text(about)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_About);
        mUtility.sleep(Constants.HALF_SECOND);
        mUtility.pressBackKeySeveralTimes(4);
        mUtility.sleep(Constants.ONE_SECOND);
//        mObjectUtils.openAppInfoByPack(activeStylusPkg);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(By.clazz("android.widget.ScrollView")), Constants.THREE_SECONDS).getChildren().get(4).click();
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