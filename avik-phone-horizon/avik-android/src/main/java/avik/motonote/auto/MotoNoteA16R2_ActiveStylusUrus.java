package avik.motonote.auto;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.impl.LocaleEnum;
import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

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
public class MotoNoteA16R2_ActiveStylusUrus {

    Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiObject2 moreOption;
    private Util mUtil;
    private AvikUiDevice mDevice;
    private ObjectUtils mObjectUtils;

    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_SmartPen = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_SmartPen", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_DoublePress = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_DoublePress", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_LongPress = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_LongPress", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_Notes = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_Notes", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_ScreenshotEditor = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_ScreenshotEditor", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_Camera = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_Camera", true);

    private AvikScreenshotAction ActiveStylus_BTStylus_Settings_Onboarding_ThePerfectToolbar = new AndroidAvikScreenshotAction("ActiveStylus_BTStylus_Settings_Onboarding_ThePerfectToolbar", true);
    //    private AvikScreenshotAction ActiveStylus_Settings_OTA_Update = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_Update", LocaleEnum.AR_EG.toLocale());
    private AvikScreenshotAction ActiveStylus_Settings_OTA_UpdateFailed = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OTA_UpdateFailed", true);
    private AvikScreenshotAction ActiveStylus_Settings_SmartPen_Disconnected = new AndroidAvikScreenshotAction("ActiveStylus_Settings_SmartPen_Disconnected", true);
    private AvikScreenshotAction ActiveStylus_Settings_LocationPermissionIsRequired_Toast = new AndroidAvikScreenshotAction("ActiveStylus_Settings_LocationPermissionIsRequired_Toast", true);
    private AvikScreenshotAction ActiveStylus_Settings_LastStylusLocation_GMS = new AndroidAvikScreenshotAction("ActiveStylus_Settings_LastStylusLocation_GMS", true);


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
        mStylus.clearApp();
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
        String checkUpdates = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "check_for_update");
        String checkForUpdate = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "check_for_update");
        String notes = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "notes");
        String screenEditor = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "screen_editor");
        String camera = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "camera");


        mDevice.wait(Until.hasObject(By.text(getStartedMsg)), Constants.THREE_SECONDS);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(getStartedMsg)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_BTStylus_Settings_Onboarding_ThePerfectToolbar);
        mUtility.sleep(Constants.HALF_SECOND);
        // Not working for sr-RS for some reason
        // mDevice.wait(Until.findObject(By.desc(next)), Constants.THREE_SECONDS).getParent().click();
        mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Onboarding_TheUltimateMotoNoteCompanion);
//        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.desc(done)), Constants.THREE_SECONDS).getParent().click();
        //mDevice.findObjects(By.clazz("android.widget.Button")).get(0).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_SmartPen_Disconnected);
        mUtility.sleep(Constants.HALF_SECOND);

        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(lastKnownLocation)), Constants.THREE_SECONDS).getParent().findObject(By.checkable(true)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(cancel)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_LocationPermissionIsRequired_Toast);
        mUtility.sleep(Constants.HALF_SECOND);
//
//        mDevice.wait(Until.findObject(By.text(lastKnownLocation)), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mDevice.wait(Until.hasObject(By.res("com.google.android.apps.maps:id/business_place_card")), Constants.FIVE_SECONDS);
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_LastStylusLocation_GMS);
//        mUtility.sleep(Constants.HALF_SECOND);
//
//        mUtility.pressBackKeySeveralTimes(2);
//        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(checkUpdates)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_Update);
//        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(checkForUpdate)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OTA_UpdateFailed);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();

        mUtility.sleep(Constants.TWO_SECONDS);
        mUtil.swipeFromCenterToBottom();
        mUtility.sleep(Constants.TWO_SECONDS);

        mDevice.wait(Until.findObject(By.text(stylusActions)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_SmartPen);
        mUtility.sleep(Constants.HALF_SECOND);

        mDevice.wait(Until.findObject(By.text(doublePress)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_DoublePress);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(longPress)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_LongPress);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(notes)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_Notes);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(screenEditor)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_ScreenshotEditor);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(camera)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_Camera);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

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
