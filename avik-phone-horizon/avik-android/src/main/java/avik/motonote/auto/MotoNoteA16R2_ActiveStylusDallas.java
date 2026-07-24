package avik.motonote.auto;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
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
public class MotoNoteA16R2_ActiveStylusDallas {

    Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiObject2 moreOption;
    private Util mUtil;
    private AvikUiDevice mDevice;
    private ObjectUtils mObjectUtils;
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


    private AvikScreenshotAction ActiveStylus_StylusToolbar = new AndroidAvikScreenshotAction("ActiveStylus_StylusToolbar", true);
    private AvikScreenshotAction ActiveStylus_AddToNote_HighlightContent_Toast = new AndroidAvikScreenshotAction("ActiveStylus_AddToNote_HighlightContent_Toast", true);
    private AvikScreenshotAction ActiveStylus_EndTutorial_Dialog = new AndroidAvikScreenshotAction("ActiveStylus_EndTutorial_Dialog", true);
    private AvikScreenshotAction ActiveStylus_LearnAboutToolbar = new AndroidAvikScreenshotAction("ActiveStylus_LearnAboutToolbar", true);
    private AvikScreenshotAction ActiveStylus_Settings_About_OpenSourceLicense = new AndroidAvikScreenshotAction("ActiveStylus_Settings_About_OpenSourceLicense", true);
    private AvikScreenshotAction ActiveStylus_Settings_LastKnownLocation_PermissionRequired_Dialog = new AndroidAvikScreenshotAction("ActiveStylus_Settings_LastKnownLocation_PermissionRequired_Dialog", true);
    private AvikScreenshotAction ActiveStylus_Settings_Onboarding_ThePerfectToolbar = new AndroidAvikScreenshotAction("ActiveStylus_Settings_Onboarding_ThePerfectToolbar", true);
    private AvikScreenshotAction ActiveStylus_Settings_Onboarding_TheUltimateMotoNoteCompanion = new AndroidAvikScreenshotAction("ActiveStylus_Settings_Onboarding_TheUltimateMotoNoteCompanion", true);
    private AvikScreenshotAction ActiveStylus_Settings_Onboarding_YouAreNowReady = new AndroidAvikScreenshotAction("ActiveStylus_Settings_Onboarding_YouAreNowReady", true);
    private AvikScreenshotAction ActiveStylus_Settings_OpenWhenLocked = new AndroidAvikScreenshotAction("ActiveStylus_Settings_OpenWhenLocked", true);
    private AvikScreenshotAction ActiveStylus_Settings_Scrolling1 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_Scrolling1", true);
    private AvikScreenshotAction ActiveStylus_Settings_Scrolling2 = new AndroidAvikScreenshotAction("ActiveStylus_Settings_Scrolling2", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_DoublePress = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_DoublePress", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusActions_LongPress = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusActions_LongPress", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusOutReminder = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusOutReminder", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusToolbar = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusToolbar", true);
    private AvikScreenshotAction ActiveStylus_Settings_StylusToolbar_YouCanAddUpTo7_Toast = new AndroidAvikScreenshotAction("ActiveStylus_Settings_StylusToolbar_YouCanAddUpTo7_Toast", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_AddContentToYourNote = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_AddContentToYourNote", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_Annotate = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_Annotate", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_CreateANote = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_CreateANote", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_HandwritingCalculator = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_HandwritingCalculator", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_HoverToMagnify = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_HoverToMagnify", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_ScreenRecording = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_ScreenRecording", true);
    private AvikScreenshotAction ActiveStylus_Tutorial_SketchToImage = new AndroidAvikScreenshotAction("ActiveStylus_Tutorial_SketchToImage", true);


    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtility = AvikUtility.getInstance();
        mUtil = new Util();
        mObjectUtils = new ObjectUtils();
        mUtility.pressBackKeySeveralTimes(3);
        mStylus = new Stylus();
        mStylus.forceCloseApp();
//        moreOption = mDevice.findObject(mStylus.moreOptionsButton);
    }

    @After
    public void tearDown() throws Exception {
        mStylus.forceCloseApp();
        mUtility.runShellCommand("sendevent /dev/input/event8 5 15 1");
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.runShellCommand("sendevent /dev/input/event8 0 0 0");
        mUtility.sleep(Constants.ONE_SECOND);
        //mStylus.clearApp();
        logger.info("Coloque a caneta no device");
    }

    public void recordMaxAudio() {
        for (int i = 0; i < 4; i++) {
            Point recButtonLocation = mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).getVisibleCenter();
            mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).click();
            mUtility.sleep(Constants.TEN_SECONDS);
            mDevice.click(recButtonLocation.x, recButtonLocation.y); // stop recording
            mUtility.sleep(Constants.FIVE_SECONDS);
        }
        mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).click();
        mUtility.sleep(Constants.HALF_SECOND);
    }

    @DeltaMethod
    @Test
    //Preconditions: put moto note app in home screen and change the position(x, y) in function holdAppIconOnHomeScreen
    public void captureScreensOfActiveStylus() throws Exception {
        mStylus.clearApp();
        mDevice.pressHome();
        mUtility.sleep(Constants.ONE_SECOND);

        logger.info("Tire a caneta no device");
        mStylus.clearApp();
        //mUtility.runShellCommand("sendevent /dev/input/event8 5 15 0");
        mUtility.sleep(Constants.FIVE_SECONDS);
        //mUtility.runShellCommand("sendevent /dev/input/event8 0 0 0");
        mUtility.sleep(Constants.ONE_SECOND);

//         mUtility.runShellCommand("am start -n com.motorola.activestylus/com.motorola.stylusmanager.activity.StylusToolbarOnboardingActivity");
//        mUtility.sleep(Constants.ONE_SECOND);
        logger.info("Tire e coloque a caneta no device");
        mDevice.wait(Until.hasObject(By.res("com.motorola.activestylus:id/page_title")), Constants.TEN_SECONDS);

        mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_LearnAboutToolbar);
        mUtility.sleep(Constants.ONE_SECOND);
        BySelector nextButton = By.res("com.motorola.activestylus:id/next_btn");
        BySelector endTutorialButton = By.res("com.motorola.activestylus:id/end_tutorial_btn");

        mDevice.wait(Until.findObject(endTutorialButton), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(endTutorialButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_EndTutorial_Dialog);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/resume_tutorial_btn")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/take_a_tour_btn")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_CreateANote);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_AddContentToYourNote);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_SketchToImage);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_Annotate);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_ScreenRecording);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_HandwritingCalculator);
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Tutorial_HoverToMagnify);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/done_btn")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Options); //ta invisivel e o drag to reposition tbm
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(1).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.sleep(Constants.HALF_SECOND);
//        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_AddToNote_HighlightContent_Toast);
//        mUtility.sleep(Constants.ONE_SECOND);

//        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/bubble")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(3).click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.wait(Until.findObject(By.res("com.motorola.screenshoteditor:id/annotate_bt_save")), Constants.THREE_SECONDS).click();
////        mUtility.sleep(Constants.ONE_SECOND);
//
////        com.motorola.screenshoteditor:id/annotate_loading_progress_bar
////      Saved to Annotate folder
        String activeStylusPkg = "com.motorola.activestylus";
        String openWhenLocked = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "moto_note_when_locked_title");
        String stylusActions = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_actions");
        String doublePress = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "double_press_button");
        String longPress = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "long_press_button");
        String stylusToolbar = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_toolbar");
        String stylusToolbarDesc = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "settings_shortcuts_edit_introduce");
        String stylusOutOfSlot = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_out_of_its_case");
        String lastKnownLocation = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "last_known_location");
        String cancel = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "cancel_msg");
        String stylusTips = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "stylus_tips");
        String getStartedMsg = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "get_started_msg");
        String about = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "about");
        String next = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "next");
        String done = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "done");
        String openSourceLicense = mObjectUtils.getResourceByPackAndStringKey(activeStylusPkg, "open_source_license");
//        mDevice.wait(Until.findObject(By.res("com.motorola.activestylus:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(6).click();
//        mUtility.sleep(Constants.THREE_SECONDS);

        mUtility.runShellCommand("am start -n com.motorola.activestylus/com.motorola.stylusmanager.activity.StylusSettingsSubActivity");
        mDevice.wait(Until.hasObject(By.text(getStartedMsg)), Constants.FIVE_SECONDS);
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Onboarding_YouAreNowReady);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(getStartedMsg)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);

        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Onboarding_ThePerfectToolbar);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.desc(next)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Onboarding_TheUltimateMotoNoteCompanion);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.desc(done)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Scrolling1);
        mUtility.sleep(Constants.HALF_SECOND);
        //mObjectUtils.createScrollable().scrollForward();
        //mUtility.sleep(Constants.TWO_SECONDS);
        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_Scrolling2);
        //mUtility.sleep(Constants.HALF_SECOND);
        //mObjectUtils.createScrollable().scrollBackward();
        //mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.scrollToText(openWhenLocked);
        mUtility.sleep(Constants.TWO_SECONDS);

        //OPEN WHEN LOCKED REMOVED
//        mDevice.wait(Until.findObject(By.text(openWhenLocked)), Constants.THREE_SECONDS).getParent().click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_OpenWhenLocked);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mDevice.wait(Until.findObject(By.text(stylusActions)), Constants.THREE_SECONDS).getParent().click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions);
//        mUtility.sleep(Constants.HALF_SECOND);
//
//        mDevice.wait(Until.findObject(By.text(doublePress)), Constants.THREE_SECONDS).getParent().click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_DoublePress);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mDevice.wait(Until.findObject(By.text(longPress)), Constants.THREE_SECONDS).getParent().click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusActions_LongPress);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.scrollToText(stylusToolbar);
        mUtility.sleep(Constants.TWO_SECONDS);

        mDevice.wait(Until.findObject(By.text(stylusToolbar)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusToolbar);
        mUtility.sleep(Constants.HALF_SECOND);

        UiObject2 parentView = mDevice.wait(Until.findObject(By.text(stylusToolbarDesc)), Constants.THREE_SECONDS).getParent();
        int childSize = parentView.getChildren().size();
        parentView = mDevice.wait(Until.findObject(By.text(stylusToolbarDesc)), Constants.THREE_SECONDS).getParent();
        parentView.getChildren().get(childSize - 1).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        parentView = mDevice.wait(Until.findObject(By.text(stylusToolbarDesc)), Constants.THREE_SECONDS).getParent();
        parentView.getChildren().get(childSize - 2).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusToolbar_YouCanAddUpTo7_Toast);
        mUtility.sleep(Constants.HALF_SECOND);

        int removeIconSize = mDevice.wait(Until.findObjects(By.desc("Remove")), Constants.THREE_SECONDS).size();
        mDevice.wait(Until.findObjects(By.desc("Remove")), Constants.THREE_SECONDS).get(removeIconSize - 1).click();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.TWO_SECONDS);

        mDevice.wait(Until.findObject(By.text(stylusOutOfSlot)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_StylusOutReminder);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(lastKnownLocation)), Constants.THREE_SECONDS).getParent().findObject(By.checkable(true)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_LastKnownLocation_PermissionRequired_Dialog);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(cancel)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);

//        mDevice.wait(Until.findObject(By.text(stylusTips)), Constants.THREE_SECONDS).getParent().click();
//        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.text(about)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(about)), Constants.THREE_SECONDS).getParent().click();
        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_About);
        mUtility.sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.text(openSourceLicense)), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_About_OpenSourceLicense);
        mUtility.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mDevice.pressBack();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.pressBackKeySeveralTimes(4);
        mUtility.sleep(Constants.ONE_SECOND);


    }
//
//    @DeltaMethod
//    @Test
//    //preconditions: login with motorola account
//    public void captureScreensOfMergedNote() throws Exception {
//        mStylus.clearApp();
//        mStylus.createNewNote("MergedNote");
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_AutomaticShaping_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_EnlargeCanvas_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_TurnPages_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_SwitchMode_tooltips1);
//
////        mDevice.findObject(tooltipButton).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
//////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_AISummaryTooltip);
//////        mDevice.findObject(tooltipButton).click();
//
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_SwitchMode_tooltips2);
//        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
////        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_Style);
//        mDevice.findObject(By.res("com.motorola.stylus:id/color_palette")).getChildren().get(0).getChildren().get(6).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_Style_CustomColor_Dialog);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_InsertTable_Dialog);
//        mDevice.click(280, 2160);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//
//        mStylus.clickScreenMakeEditIconVisible();
//        mUtility.createObjectByText("ic_column_handle").click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_EditColumns);
//        mUtil.clickOnScreenCenter();
//
//        mUtility.sleep(AvikConstants.SHORTWAIT);
//        mStylus.clickScreenMakeEditIconVisible();
//        mUtility.createObjectByText("ic_row_handle").click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_EditRow);
//        mUtil.clickOnScreenCenter();
//        mUtility.sleep(AvikConstants.SHORTWAIT);
//
//        mDevice.findObject(By.clazz("android.widget.EditText")).clear();
////        mUtil.clickOnScreenCenter();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(3).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_AddImage);
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        mDevice.findObject(mStylus.sketchToImageButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_login);
//
//        Rect tooltipRect =
//                mDevice.wait(
//                                Until.findObject(
//                                        By.res("com.motorola.stylus:id/snackbar_animation")),
//                                Constants.FIVE_SECONDS)
//                        .getVisibleBounds();
//        mDevice.click(500, 1200); // the masker should disapear after clicking any point.
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mDevice.click(500, 1200); // the masker should disapear after clicking any point.
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage);
//
//        mDevice.findObject(mStylus.snackBarCloseButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.selectStylelist).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        UiObject styleContainer = mDevice.findObject(new UiSelector().resourceId("com.motorola.stylus:id/styleContainer"));
//        if (!styleContainer.exists()) {
//            mDevice.findObject(mStylus.selectStylelist).click();
//            mUtility.sleep(Constants.TWO_SECONDS);
//        }
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_StyleContainer);
//        mDevice.wait(Until.hasObject(By.res("com.motorola.stylus:id/style_selection_container")), Constants.FIFTEEN_SECONDS);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_StyleContainer_Scrolling1);
//        mUtility.sleep(Constants.HALF_SECOND);
//        UiScrollable obj = new UiScrollable(new UiSelector().className("android.widget.HorizontalScrollView").scrollable(true));
//        obj.setAsHorizontalList().scrollForward();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_StyleContainer_Scrolling2);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(mStylus.styleContainer).getChildren().get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mDevice.pressBack();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_Abstract);
////
////        mDevice.findObject(mStylus.selectStylelist).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mDevice.findObject(mStylus.styleContainer).getChildren().get(2).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_clean);
////
////        mDevice.findObject(mStylus.selectStylelist).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mDevice.findObject(mStylus.styleContainer).getChildren().get(3).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_Realistic);
////
////        mDevice.findObject(mStylus.selectStylelist).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mDevice.findObject(mStylus.styleContainer).getChildren().get(0).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mUtil.clickOnScreenCenter();
//        String touchViewResourceID = "com.motorola.stylus:id/drawing_view";
//        mObjectUtils.runCommand("input swipe 600 1000 600 1500 3000");
////        mUtil.drawNote(touchViewResourceID);
//        mDevice.swipe(
//                tooltipRect.left, tooltipRect.top, tooltipRect.right, tooltipRect.bottom, 200);
//        mDevice.swipe(
//                tooltipRect.left,
//                tooltipRect.top,
//                tooltipRect.centerX(),
//                tooltipRect.centerY(),
//                200);
//        mDevice.swipe(
//                tooltipRect.centerX(),
//                tooltipRect.centerY(),
//                tooltipRect.right,
//                tooltipRect.top,
//                200);
//        mDevice.swipe(tooltipRect.left, tooltipRect.top, tooltipRect.right, tooltipRect.top, 200);
//        AvikLogger.info("+++++++++++ Draw a circle ");
//        mUtility.sleep(Constants.TEN_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(1).longClick();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_Undo);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(0).longClick();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_ClearAll);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(1).click();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(2).longClick();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_Redo);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(2).longClick();
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(mStylus.generateButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_transformSketchToMasterPiece);
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mDevice.wait(Until.hasObject(mStylus.AddToNoteButton), Constants.THREE_MINUTES);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SketchToImage_GeneratedSuccessfully);
//        mDevice.findObject(mStylus.AddToNoteButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        //push the pictures under res.DCIM.Camera to phone
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(4).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.clickByResourceId("Choose an image ", "com.motorola.stylus:id/album");
//
////        mDevice.findObject(mStylus.chooseAnImage).click();
//        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/camera")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TakePhotosAndVideos_Permission_GMS);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mObjectUtils.clickIfExists(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button"), Constants.THREE_SECONDS);
//
//        mDevice.wait(Until.findObject(By.res("com.motorola.camera5:id/capture_bar_shutter_button")), Constants.THREE_SECONDS).click();
//        mUtility.sleep(Constants.THREE_SECONDS);
//
//        mObjectUtils.runCommand("input keyevent 66"); //enter button
//
//
//        mDevice.wait(Until.hasObject(By.res("com.motorola.stylus:id/ocr_text")), Constants.SEVEN_SECONDS);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_OCR_NoText);
//        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/arrow")), Constants.THREE_SECONDS).click();
////        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_AccessPhotos&Radios_Permission_GMS);
////        mDevice.findObject(mStylus.allowAll).click();
////        mUtility.sleep(AvikConstants.NORMALWAIT);
////        mUtil.clickByResourceId("PHOTO WITH NO TEXT ", "com.google.android.providers.media.module:id/icon_thumbnail", 1);
////        mDevice.wait(Until.hasObject(mStylus.OCRCloseButton), AvikConstants.LONGERWAIT * 6);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_OCR_NoText);
//        mDevice.findObject(mStylus.OCRCloseButton).click();
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//
//        AvikLogger.info("TAKE PHOTO WITH TEXT");
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(4).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.clickByResourceId("Choose an image ", "com.motorola.stylus:id/album");
//        mDevice.findObject(mStylus.chooseAnImage).click();
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_AccessPhotos&Radios_Permission_GMS);
//        mDevice.findObject(mStylus.allowAll).click();
//        mUtility.sleep(AvikConstants.NORMALWAIT);
////        mDevice.click(170, 1500);
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        String photoTaken = "फ़ोटो खींचने का समय 1 अग॰ 2025 8:03 pm था";
//        //Photo taken on Aug 1, 2025 20:03"
//        mDevice.wait(Until.findObject(By.clazz("android.view.View").desc(photoTaken)), Constants.FIVE_SECONDS).click();
////        mUtil.clickByResourceId("PHOTO WITH TEXT ", "com.google.android.providers.media.module:id/icon_thumbnail", 0);
//        mDevice.wait(Until.hasObject(mStylus.OCRCloseButton), AvikConstants.LONGERWAIT * 6);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_OCR_TextFound);
//        AvikLogger.info("CLICK THE DROP DOWN");
//        mUtility.clickByResourceId("DROP DOWN ", "com.motorola.stylus:id/arrow");
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_OCR_TextFound_Language);
//        mUtility.pressBackKeySeveralTimes(4);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mObjectUtils.runCommand("am force-stop com.motorola.stylus");
//
//        mStylus.createNewNote("MergedNote");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.wait(Until.findObject(mStylus.recyclerView), Constants.THREE_SECONDS).getChildren().get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        AvikLogger.info("TAKE NOTE WITH Audio");
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(5).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        if (mDevice.hasObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button"))) {
//            AvikLogger.info("Allow Moto Noto to record audio Only this time");
//            mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_one_time_button")).click();
//            mUtility.sleep(Constants.TWO_SECONDS);
//        }
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_Audio_Recording);
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        AvikLogger.info("CLICK recording button to stop record");
//        mUtility.clickByResourceId("Stop Record", "com.motorola.stylus:id/fab");
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_Audio_Recorded);
//        mUtility.pressBackKeySeveralTimes(1);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.swipe(500, 420, 500, 420, 100);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObjects(By.clazz("android.widget.Image")).get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_TranscriptioAndSummaryDisclaimer);
//        mObjectUtils.clickIfExists(mStylus.agreeButton);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_LanguageSupport_options);
//        mDevice.findObject(mStylus.OKButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_AudioTranscribing);
//        mDevice.wait(Until.gone(mStylus.cancelButton), AvikConstants.LONGERWAIT * 6);
//        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/arrow")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SetTitle);
//        mDevice.findObject(By.res("com.motorola.stylus:id/arrow")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        // Needs to be manually captured
//        //mUtility.writeNonAsciiText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mStylus.inputCharacter(2);
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SetTitle_Over40Characters_Toast", false, false);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/title")).clear();
//        mUtility.pressBackKeySeveralTimes(3);
//
//
////        mDevice.pressBack();
//        mStylus.createNewNote("MergedNote");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_TranscriptioAndSummaryDisclaimer);
//        mObjectUtils.clickIfExists(mStylus.agreeButton);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_summarize_NoText);
//        mDevice.findObject(mStylus.cancelButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtil.clickOnScreenCenter();
//        mUtility.sleep(AvikConstants.SHORTWAIT);
//        mDevice.executeShellCommand("input text WehavetrainedamodelcalledChatGPTwhichinteractsinaconversationalway.");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNote_GeneratingSummary);
//    }
//
//
//    @DeltaMethod
//    @Test
//    // Need manually add some circles, please pay attention on the avik log
//    public void captureScreensOfDrawNote() throws Exception {
////        mStylus.clearApp();
//        mStylus.createNewNote("DrawNote");
//
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_Tips);
//        mUtility.clickByResourceId("Got it", "com.motorola.stylus:id/next");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_Style);
//        mDevice.findObject(By.res("com.motorola.stylus:id/color_palette")).getChildren().get(0).getChildren().get(6).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_Style_CustomColor_Dialog);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_ClearAll);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(3).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_AddImage);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_withoutInput);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        String touchViewResourceID = "com.motorola.stylus:id/above_menu_container";
//        mUtility.clickByResourceId("touchView", touchViewResourceID);
//        mUtil.swipeFromCenterToLeft();
//        mUtil.clickOnScreenCenter();
//        mUtil.drawNote(touchViewResourceID);
//        AvikLogger.info("+++++++++++ Draw something, for example: a circle ?manually? -> "); //todo?
//        mUtility.sleep(Constants.TEN_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.clickByResourceId("touchView", touchViewResourceID);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtil.clickOnScreenCenter();
//        mUtil.clickOnScreenCenter();
//        mUtil.drawNote(touchViewResourceID);
//        AvikLogger.info("+++++++++++ Draw a circle ?manually? -> "); //todo?
//        mUtility.sleep(Constants.TEN_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_LassoTool_Tooltip);
//
//        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_withInput);
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_PinToShortCuts_Toast);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptionsWithUnpin);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        //new
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_UnpinFromShortCuts_Toast);
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
////        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_SetBackground_Dialog);
//        mUtility.sleep(Constants.HALF_SECOND);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(2).click();
////        mDevice.findObjects(mStylus.moreOptionsTitles).get(2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_MoreOptions_Delete_Dialog);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//    }
//
//    @DeltaMethod
//    @Test
//    //6 screens
//    public void captureScreensOfChecklistNote() throws Exception {
////        mStylus.clearApp();
//        mStylus.createNewNote("ChecklistNote");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_Empty);
//        mDevice.findObject(By.res("com.motorola.stylus:id/add_todo")).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("com.motorola.stylus:id/info")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_AddDetails);
//        mDevice.findObject(By.res("com.motorola.stylus:id/time")).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_AddDetails_PermissionNeeded_Dialog);
//        mUtility.skipAndroidButton1();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
//        mUtility.sleep(Constants.HALF_SECOND + 100);
//        mDevice.findObject(mStylus.deny).click();
//        mUtility.sleep(Constants.HALF_SECOND + 100);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_AddDetails_NeedsPermission_Tooltip);
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.a2).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_AddDetails_ReminderTime_Dialog);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        if (mDevice.hasObject(By.res("com.motorola.stylus:id/btn_positive"))) {
//            mDevice.pressBack();
//        }
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/content")).setText("AViK");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObjects(By.res("com.motorola.stylus:id/icon")).get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_unCompleted_Notification);
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.waitForIdle();
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_unCompleted);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.stylus:id/cute_cb")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.clickByResourceId("MoreOptions", "com.motorola.stylus:id/container_end");
////        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
////        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
//        String deleteBtnStr = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "dialog_button_delete");
//        UiObject2 deleteBtn = mDevice.findObject(By.text(deleteBtnStr));
//        deleteBtn.click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_MoreOptions_DeleteOne_Dialog);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.clickByResourceId("Cancel", "com.motorola.stylus:id/btn_negative");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_Completed);
//        mDevice.findObject(By.res("com.motorola.stylus:id/add_todo")).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/todo_edit_text_view").setText("avik");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.clickByResourceId("MoreOptions", "com.motorola.stylus:id/container_end");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_MoreOptions_DeleteAll_Dialog);
//        mUtility.clickByResourceId("Cancel", "com.motorola.stylus:id/btn_negative");
//        mUtility.pressBackKeySeveralTimes(3);
//    }
//
//    @DeltaMethod
//    @Test
//    //preconditions: there is only every note for every kinds of note without title,and for merged note, there are two notes.
//    public void captureScreensOfSearchAndDelete() throws Exception {
//        mStylus.openApp();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        if (mDevice.hasObject(mStylus.getStartedButton)) {
//            mDevice.findObject(mStylus.getStartedButton).click();
//        }
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/fab")), Constants.FIVE_SECONDS).click();
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtil.clickOnScreenCenter();
//        mUtility.sleep(AvikConstants.SHORTWAIT);
//        mDevice.executeShellCommand("input text Avik");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/container_start")), Constants.FIVE_SECONDS).getChildren().get(0).click();
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DefaultNoteNames_Notes);
//        pressListGridButton.run();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Main_List);
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/display_style").click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        pressListGridButton.run();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Main_Grid);
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/display_style").click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        pressSearchButton.run();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Main_Search);
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Notes_SearchNote);
//
//        mUtility.createObjectByResourceID("android:id/search_src_text").setText("nothing");
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Notes_SearchNote_NotFound);
//
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.THREE_SECONDS);
//
//
//        mDevice.findObject(mStylus.canvasesTab).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DefaultNoteNames_Canvases);
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Canvases_SearchNote);
//
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.THREE_SECONDS);
//
//
//        mDevice.findObject(mStylus.checklistTab).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DefaultNoteNames_Checklists);
//
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Checklists_SearchNote);
//
//        mDevice.pressBack();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.THREE_SECONDS);
//
//        mDevice.findObject(mStylus.notesTab).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        UiObject2 note = mDevice.findObjects(By.res("com.motorola.stylus:id/note_title")).get(0);
//        Rect position = note.getVisibleBounds();
//        mDevice.swipe(position.centerX(), position.centerY(), position.centerX(), position.centerY(), 400);
//        mUtility.sleep(Constants.ONE_SECOND);
//
////        mUtil.holdAndTakeScreenshotofToast("Delete", "com.motorola.stylus:id/display_style", "MotoStylus_OneDelete_Toast", 1000);
//        UiObject2 deleteButton = mDevice.findObject(mStylus.toolBar).getChildren().get(1).getChildren().get(2);
//        Rect deletePosition = deleteButton.getVisibleBounds();
//        mDevice.swipe(deletePosition.centerX(), deletePosition.centerY(), deletePosition.centerX(), deletePosition.centerY(), 400);
//        mUtility.sleep(Constants.ONE_SECOND);
//        //new
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_OneDelete_Toast);
//        deleteButton.click();
//        mUtility.sleep(AvikConstants.NORMALWAIT);
//        //new
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_OneDelete_Dialog);
//        mDevice.pressBack();
//
//        UiObject2 shareButton = mDevice.findObject(mStylus.toolBar).getChildren().get(1).getChildren().get(1);
//        Rect sharePosition = shareButton.getVisibleBounds();
//        mDevice.swipe(sharePosition.centerX(), sharePosition.centerY(), sharePosition.centerX(), sharePosition.centerY(), 400);
//        mUtility.sleep(Constants.ONE_SECOND);
//        //new
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_OneShare_Toast);
//        shareButton.click();
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        //new
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_OneShare_Dialog);
//        mDevice.pressBack();
//
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObjects(By.res("com.motorola.stylus:id/note_title")).get(1).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        deleteButton.click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManyDelete_Dialog);
////        mUtility.skipAndroidButton2();
//        mDevice.findObject(mStylus.declineButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        shareButton.click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManyShare_Dialog);
//        mDevice.pressBack();
//        mDevice.findObject(By.res("com.motorola.stylus:id/action_mode_close_button")).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mUtility.pressBackKeySeveralTimes(3);
//    }
//
//    @DeltaMethod
//    @Test
//    //VPN and gmail account logged-in
//    public void captureScreensOfMotoNotewithAccountLogin() throws Exception {
//        mStylus.clearApp();
//        mDevice.pressHome();
//        mUtility.sleep(Constants.FIVE_SECONDS);
//        mStylus.openStylusSettings();
//
//        mDevice.findObject(mStylus.settingsAutoSync).click();
//        mDevice.wait(Until.hasObject(mStylus.accountName), AvikConstants.LONGERWAIT * 6);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_AutoSync_ChooseUser_GMS);
//        mDevice.findObject(mStylus.accountName).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_AutoSync_SignInSuccessfullyToast);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.settingsAutoSync).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.sleep(700);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_AutoSync_OFFToast);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.settingsAutoSync).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.sleep(700);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_AutoSync_ONToast);
//    }

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
            //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_StylusToolbar);

        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }
    }
}
