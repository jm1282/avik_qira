package avik.motonote.auto;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class MotoNoteOrion {
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
    private AvikUtility mUtils;
    private Stylus mStylus;

    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtils = AvikUtility.getInstance();
        mUtil = new Util();
        mObjectUtils = new ObjectUtils();
        mUtils.pressBackKeySeveralTimes(3);
        mStylus = new Stylus();
        mStylus.forceCloseApp();
//        moreOption = mDevice.findObject(mStylus.moreOptionsButton);
    }

    @After
    public void tearDown() throws Exception {
        mStylus.forceCloseApp();
    }

    public void recordMaxAudio() {
        for (int i = 0; i < 4; i++) {
            Point recButtonLocation = mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).getVisibleCenter();
            mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).click();
            mUtils.sleep(Constants.TEN_SECONDS);
            mDevice.click(recButtonLocation.x, recButtonLocation.y); // stop recording
            mUtils.sleep(Constants.FIVE_SECONDS);
        }
        mDevice.findObject(By.res("com.motorola.stylus:id/mainFab")).click();
        mUtils.sleep(Constants.HALF_SECOND);
    }

    @DeltaMethod
    @Test
    //Preconditions: put moto note app in home screen and change the position(x, y) in function holdAppIconOnHomeScreen
    public void captureScreensOfMotoNoteMain() throws Exception {
        mStylus.clearApp();
        mDevice.pressHome();
        mUtils.sleep(Constants.FIVE_SECONDS);

//        mUtil.holdAppIconOnHomeScreen();
        String motoNoteAppname = mUtils.getResourceByPackAndStringKey("com.motorola.stylus", "application_name");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(motoNoteAppname)), Constants.THREE_SECONDS).click(2000);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_IconShortcuts");
//        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(0).longClick();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_IconShortcuts_pauseAPP");
//        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(1).longClick();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_IconShortcuts_APPInfo");
//        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(2).longClick();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_IconShortcuts_installInSecureFolder");
//
//        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(0).click();
//        mUtils.sleep(Constants.FIVE_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_IconShortcuts_pauseAPP_Notification_GMS");
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(By.res("android:id/button2")).click();

        mStylus.openApp();
//        mDevice.takeAvikScreenshot("MotoStylus_PermissionStatement_Scrolling1");
//
//        mDevice.swipe(500, 2200, 500, 1400, 50);
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_PermissionStatement_Scrolling2");
//        mUtils.clickByResourceId("Agree", "android:id/button1");
        mDevice.takeAvikScreenshot("MotoStylus_Tutorial_getStarted");

        mDevice.findObject(mStylus.getStartedButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        if (mDevice.hasObject(mUtil.a2)) {
            AvikLogger.info("IGNORING NOTIFICATION PERMISSION");
            mDevice.takeAvikScreenshot("MotoStylus_NotificationPermission_GMS");
            mDevice.findObject(mUtil.a2).click();
        }
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_noNotes");

        //click "More Options"

        mUtils.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).longClick();
//        mDevice.findObject(mStylus.tutorialButton).click(300);
//        mUtils.createObjectByDescription("More options").longClick();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_MoreOptions_Tip");
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MoreOptions");

        mDevice.findObject(mStylus.tutorialButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mObjectUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_SketchIntoImage_Dialog");
        mDevice.findObject(mStylus.nextButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mObjectUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_AutomaticShaping_Dialog");
        mDevice.findObject(mStylus.nextButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mObjectUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_EnlargeCanvas_Dialog");
        mDevice.findObject(mStylus.nextButton).click();

        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(mStylus.settingsButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_Scrolling1");
        mObjectUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
//        //ToDo need manually check whether there are one more pages, if there is only one page, then don't need to scroll and capture.
//        UiScrollable container = new UiScrollable(new UiSelector().resourceId("com.motorola.stylus:id/scroll_container"));
//        container.scrollToEnd(2, 55);
//        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_Scrolling2");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.settingsAboutButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_About");
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mObjectUtils.createScrollable().scrollBackward();
        mUtils.sleep(Constants.ONE_SECOND);

////        container.scrollToBeginning(40);
        mDevice.findObject(mStylus.languageSupportButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_LanguageSupport_options");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.OKButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.LanguageSupportPortuguese).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_Settings_Languagesupport_portuguese");
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.languageSupportButton).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.LanguageSupportSpanish).click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_Settings_Languagesupport_spanish");
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.languageSupportButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.LanguageSupportEnglish).click();
//        mUtils.sleep(Constants.ONE_SECOND);

//        //ToDo need manually check whether there is a pen with testing phone, if there is no pen, then Comment out the following code.
        mDevice.findObject(mStylus.penPreferencesButton).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_PenPreferences");
        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_PenPreferences_Colors");
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(mStylus.settingsAutoSync).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSnc_Toast");
        mUtils.sleep(Constants.ONE_SECOND);

//        String stylusSettings = mObjectUtils.getResourceByPackAndStringKey("com.motorola.stylus","stylus_setting");
//        mDevice.wait(Until.findObject(By.text(stylusSettings)),Constants.THREE_SECONDS).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//
//        mDevice.takeAvikScreenshot("MotoStylus_Settings_StylusSettings_Scrolling1");
//        mUtils.sleep(Constants.HALF_SECOND);
//        mObjectUtils.createScrollable().scrollForward();
//        mUtils.sleep(Constants.ONE_SECOND);
//
//        mDevice.takeAvikScreenshot("MotoStylus_Settings_StylusSettings_Scrolling2");
//        mUtils.sleep(Constants.HALF_SECOND);
//
//        mObjectUtils.createScrollable().scrollBackward();
//        mUtils.sleep(Constants.ONE_SECOND);
//
//        stylus_settings_global_handwriting_title


        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.allNotesButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Classification");

        mDevice.findObject(mStylus.uncategorizedButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_emptyCategory");

        mDevice.findObject(mStylus.allNotesButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.editButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ManageCategories");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.addACategoryButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ManageCategories_NewCategory");

        mDevice.findObject(mStylus.newCategoryName).setText("avik");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.doneButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.addACategoryButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.newCategoryName).setText("avik");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.doneButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ManageCategories_NewCategory_exists");
        mDevice.findObject(mStylus.cancelButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(mStylus.categoryList).getChildren().get(3).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ManageCategories_EditCategory");
        mDevice.findObject(mStylus.cancelButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.deleteButton).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.pressBack();
        mDevice.pressBack();

        mDevice.findObject(mStylus.canvasesTab).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_NoNote");


        mDevice.findObject(mStylus.checklistTab).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_NoNote");

//        mUtils.clickByResourceId("MergedNotes", "com.motorola.stylus:id/navigation_notes");
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_MergedNotes_SearchMergedNotes");
//        mUtils.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
//
//        mUtils.clickByResourceId("Canvases", "com.motorola.stylus:id/navigation_canvases");
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_CanvasesNote_SearchCanvasesNote");
//        mUtils.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
//
//        mUtils.clickByResourceId("Checklists", "com.motorola.stylus:id/navigation_checklists");
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
//        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_SearchChecklistNote");
//        mUtils.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
    }

    @DeltaMethod
    @Test
    //preconditions: login with motorola account
    public void captureScreensOfMergedNote() throws Exception {
        mStylus.clearApp();
        mStylus.createNewNote("MergedNote");
        mUtils.sleep(Constants.TWO_SECONDS);


//        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_AutomaticShaping_Dialog");
        mDevice.findObject(mStylus.nextButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_EnlargeCanvas_Dialog");
        mDevice.findObject(mStylus.nextButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_TurnPages_Dialog");
        mDevice.findObject(mStylus.nextButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_SwitchMode_tooltips1");

//        mDevice.findObject(tooltipButton).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
////        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_AISummaryTooltip");
////        mDevice.findObject(tooltipButton).click();

        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_SwitchMode_tooltips2");
        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
//        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_Style");
        mDevice.findObject(By.res("com.motorola.stylus:id/color_palette")).getChildren().get(0).getChildren().get(6).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_Style_CustomColor_Dialog");
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();

        mDevice.findObject(mStylus.recyclerView).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_InsertTable_Dialog");
        mDevice.click(280, 2160);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
        mUtils.sleep(Constants.TWO_SECONDS);


        mStylus.clickScreenMakeEditIconVisible();
        mUtils.createObjectByText("ic_column_handle").click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_EditColumns");
        mUtil.clickOnScreenCenter();

        mUtils.sleep(AvikConstants.SHORTWAIT);
        mStylus.clickScreenMakeEditIconVisible();
        mUtils.createObjectByText("ic_row_handle").click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_EditRow");
        mUtil.clickOnScreenCenter();
        mUtils.sleep(AvikConstants.SHORTWAIT);

        mDevice.findObject(By.clazz("android.widget.EditText")).clear();
//        mUtil.clickOnScreenCenter();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(3).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_AddImage");
        mUtils.sleep(AvikConstants.NORMALWAIT);
        mDevice.findObject(mStylus.sketchToImageButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_login");

        Rect tooltipRect =
                mDevice.wait(
                                Until.findObject(
                                        By.res("com.motorola.stylus:id/snackbar_animation")),
                                Constants.FIVE_SECONDS)
                        .getVisibleBounds();
        mDevice.click(500, 1200); // the masker should disapear after clicking any point.
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.click(500, 1200); // the masker should disapear after clicking any point.
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage");

        mDevice.findObject(mStylus.snackBarCloseButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.selectStylelist).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        UiObject styleContainer = mDevice.findObject(new UiSelector().resourceId("com.motorola.stylus:id/styleContainer"));
        if (!styleContainer.exists()) {
            mDevice.findObject(mStylus.selectStylelist).click();
            mUtils.sleep(Constants.TWO_SECONDS);
        }
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_StyleContainer");
        mDevice.wait(Until.hasObject(By.res("com.motorola.stylus:id/style_selection_container")), Constants.FIFTEEN_SECONDS);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_StyleContainer_Scrolling1");
        mUtils.sleep(Constants.HALF_SECOND);
        UiScrollable obj = new UiScrollable(new UiSelector().className("android.widget.HorizontalScrollView").scrollable(true));
        obj.setAsHorizontalList().scrollForward();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_StyleContainer_Scrolling2");
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.findObject(mStylus.styleContainer).getChildren().get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_Abstract");
//
//        mDevice.findObject(mStylus.selectStylelist).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.styleContainer).getChildren().get(2).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_clean");
//
//        mDevice.findObject(mStylus.selectStylelist).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.styleContainer).getChildren().get(3).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_Realistic");
//
//        mDevice.findObject(mStylus.selectStylelist).click();
//        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.styleContainer).getChildren().get(0).click();
//        mUtils.sleep(Constants.TWO_SECONDS);

        mUtil.clickOnScreenCenter();
        String touchViewResourceID = "com.motorola.stylus:id/drawing_view";
        mObjectUtils.runCommand("input swipe 600 1000 600 1500 3000");
//        mUtil.drawNote(touchViewResourceID);
        mDevice.swipe(
                tooltipRect.left, tooltipRect.top, tooltipRect.right, tooltipRect.bottom, 200);
        mDevice.swipe(
                tooltipRect.left,
                tooltipRect.top,
                tooltipRect.centerX(),
                tooltipRect.centerY(),
                200);
        mDevice.swipe(
                tooltipRect.centerX(),
                tooltipRect.centerY(),
                tooltipRect.right,
                tooltipRect.top,
                200);
        mDevice.swipe(tooltipRect.left, tooltipRect.top, tooltipRect.right, tooltipRect.top, 200);
        AvikLogger.info("+++++++++++ Draw a circle ");
        mUtils.sleep(Constants.TEN_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(1).longClick();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_Undo");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(0).longClick();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_ClearAll");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(1).click();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(2).longClick();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_Redo");
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.findObject(By.res("com.motorola.stylus:id/toolbar")).getChildren().get(2).getChildren().get(2).longClick();
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.findObject(mStylus.generateButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_transformSketchToMasterPiece");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.wait(Until.hasObject(mStylus.AddToNoteButton), Constants.THREE_MINUTES);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SketchToImage_GeneratedSuccessfully");
        mDevice.findObject(mStylus.AddToNoteButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.sleep(Constants.TWO_SECONDS);

        //push the pictures under res.DCIM.Camera to phone
        mDevice.findObject(mStylus.recyclerView).getChildren().get(4).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtils.clickByResourceId("Choose an image ", "com.motorola.stylus:id/album");

//        mDevice.findObject(mStylus.chooseAnImage).click();
        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/camera")), Constants.THREE_SECONDS).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TakePhotosAndVideos_Permission_GMS");
        mUtils.sleep(Constants.ONE_SECOND);
        mObjectUtils.clickIfExists(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button"), Constants.THREE_SECONDS);

        mDevice.wait(Until.findObject(By.res("com.motorola.camera5:id/capture_bar_shutter_button")), Constants.THREE_SECONDS).click();
        mUtils.sleep(Constants.THREE_SECONDS);

        mObjectUtils.runCommand("input keyevent 66"); //enter button


        mDevice.wait(Until.hasObject(By.res("com.motorola.stylus:id/ocr_text")), Constants.SEVEN_SECONDS);
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_OCR_NoText");
        mUtils.sleep(Constants.ONE_SECOND);
//        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/arrow")), Constants.THREE_SECONDS).click();
//        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_AccessPhotos&Radios_Permission_GMS");
//        mDevice.findObject(mStylus.allowAll).click();
//        mUtils.sleep(AvikConstants.NORMALWAIT);
//        mUtil.clickByResourceId("PHOTO WITH NO TEXT ", "com.google.android.providers.media.module:id/icon_thumbnail", 1);
//        mDevice.wait(Until.hasObject(mStylus.OCRCloseButton), AvikConstants.LONGERWAIT * 6);
//        mDevice.takeAvikScreenshot("MotoStylus_TextNote_OCR_NoText");
        mDevice.findObject(mStylus.OCRCloseButton).click();
        mUtils.sleep(AvikConstants.NORMALWAIT);

        AvikLogger.info("TAKE PHOTO WITH TEXT");
        mDevice.findObject(mStylus.recyclerView).getChildren().get(4).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mUtils.clickByResourceId("Choose an image ", "com.motorola.stylus:id/album");
        mDevice.findObject(mStylus.chooseAnImage).click();
        mUtils.sleep(AvikConstants.NORMALWAIT);
        mDevice.takeAvikScreenshot("MotoStylus_AccessPhotos&Radios_Permission_GMS");
        mDevice.findObject(mStylus.allowAll).click();
        mUtils.sleep(AvikConstants.NORMALWAIT);
//        mDevice.click(170, 1500);
        mUtils.sleep(AvikConstants.NORMALWAIT);
        String photoTaken = "फ़ोटो खींचने का समय 1 अग॰ 2025 8:03 pm था";
        //Photo taken on Aug 1, 2025 20:03"
        mDevice.wait(Until.findObject(By.clazz("android.view.View").desc(photoTaken)), Constants.FIVE_SECONDS).click();
//        mUtil.clickByResourceId("PHOTO WITH TEXT ", "com.google.android.providers.media.module:id/icon_thumbnail", 0);
        mDevice.wait(Until.hasObject(mStylus.OCRCloseButton), AvikConstants.LONGERWAIT * 6);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_OCR_TextFound");
        AvikLogger.info("CLICK THE DROP DOWN");
        mUtils.clickByResourceId("DROP DOWN ", "com.motorola.stylus:id/arrow");
        mUtils.sleep(AvikConstants.NORMALWAIT);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_OCR_TextFound_Language");
        mUtils.pressBackKeySeveralTimes(4);
        mUtils.sleep(Constants.TWO_SECONDS);
        mObjectUtils.runCommand("am force-stop com.motorola.stylus");

        mStylus.createNewNote("MergedNote");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(mStylus.recyclerView), Constants.THREE_SECONDS).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        AvikLogger.info("TAKE NOTE WITH Audio");
        mDevice.findObject(mStylus.recyclerView).getChildren().get(5).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button"))) {
            AvikLogger.info("Allow Moto Noto to record audio Only this time");
            mDevice.findObject(By.res("com.android.permissioncontroller:id/permission_allow_one_time_button")).click();
            mUtils.sleep(Constants.TWO_SECONDS);
        }
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_Audio_Recording");
        mUtils.sleep(Constants.TWO_SECONDS);

        AvikLogger.info("CLICK recording button to stop record");
        mUtils.clickByResourceId("Stop Record", "com.motorola.stylus:id/fab");
        mUtils.sleep(AvikConstants.NORMALWAIT);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_Audio_Recorded");
        mUtils.pressBackKeySeveralTimes(1);

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.swipe(500, 420, 500, 420, 100);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(By.clazz("android.widget.Image")).get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_TranscriptioAndSummaryDisclaimer");
        mObjectUtils.clickIfExists(mStylus.agreeButton);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_LanguageSupport_options");
        mDevice.findObject(mStylus.OKButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_AudioTranscribing");
        mDevice.wait(Until.gone(mStylus.cancelButton), AvikConstants.LONGERWAIT * 6);
        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/arrow")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SetTitle");
        mDevice.findObject(By.res("com.motorola.stylus:id/arrow")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        // Needs to be manually captured
        //mUtils.writeNonAsciiText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        mDevice.findObject(By.res("com.motorola.stylus:id/title")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mStylus.inputCharacter(2);
        mDevice.takeAvikScreenshot("MotoStylus_TextNote_SetTitle_Over40Characters_Toast", false, false);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/title")).clear();
        mUtils.pressBackKeySeveralTimes(3);


//        mDevice.pressBack();
        mStylus.createNewNote("MergedNote");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
        mUtils.sleep(Constants.TWO_SECONDS);
//        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_TranscriptioAndSummaryDisclaimer");
        mObjectUtils.clickIfExists(mStylus.agreeButton);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_summarize_NoText");
        mDevice.findObject(mStylus.cancelButton).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtil.clickOnScreenCenter();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mDevice.executeShellCommand("input text WehavetrainedamodelcalledChatGPTwhichinteractsinaconversationalway.");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_MergedNote_GeneratingSummary");
    }


    @DeltaMethod
    @Test
    // Need manually add some circles, please pay attention on the avik log
    public void captureScreensOfDrawNote() throws Exception {
//        mStylus.clearApp();
        mStylus.createNewNote("DrawNote");

        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_Tips");
        mUtils.clickByResourceId("Got it", "com.motorola.stylus:id/next");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_Style");
        mDevice.findObject(By.res("com.motorola.stylus:id/color_palette")).getChildren().get(0).getChildren().get(6).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_Style_CustomColor_Dialog");
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();

        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_ClearAll");
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(3).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_AddImage");
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_withoutInput");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.pressBack();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        String touchViewResourceID = "com.motorola.stylus:id/above_menu_container";
        mUtils.clickByResourceId("touchView", touchViewResourceID);
        mUtil.swipeFromCenterToLeft();
        mUtil.clickOnScreenCenter();
        mUtil.drawNote(touchViewResourceID);
        AvikLogger.info("+++++++++++ Draw something, for example: a circle ?manually? -> "); //todo?
        mUtils.sleep(Constants.TEN_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtils.clickByResourceId("touchView", touchViewResourceID);
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtil.clickOnScreenCenter();
        mUtil.clickOnScreenCenter();
        mUtil.drawNote(touchViewResourceID);
        AvikLogger.info("+++++++++++ Draw a circle ?manually? -> "); //todo?
        mUtils.sleep(Constants.TEN_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_LassoTool_Tooltip");

        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_withInput");
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_PinToShortCuts_Toast");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptionsWithUnpin");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
        //new
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_UnpinFromShortCuts_Toast");
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_SetBackground_Dialog");
        mUtils.sleep(Constants.HALF_SECOND);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        mDevice.findObject(By.res("com.motorola.stylus:id/container_end")).getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(mStylus.moreOptionsTitles).get(2).click();
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DrawNote_MoreOptions_Delete_Dialog");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_negative")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
    }

    @DeltaMethod
    @Test
    //6 screens
    public void captureScreensOfChecklistNote() throws Exception {
//        mStylus.clearApp();
        mStylus.createNewNote("ChecklistNote");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_Empty");
        mDevice.findObject(By.res("com.motorola.stylus:id/add_todo")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.motorola.stylus:id/info")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_AddDetails");
        mDevice.findObject(By.res("com.motorola.stylus:id/time")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_AddDetails_PermissionNeeded_Dialog");
        mUtils.skipAndroidButton1();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
        mUtils.sleep(Constants.HALF_SECOND + 100);
        mDevice.findObject(mStylus.deny).click();
        mUtils.sleep(Constants.HALF_SECOND + 100);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_AddDetails_NeedsPermission_Tooltip");
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.a2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_AddDetails_ReminderTime_Dialog");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/btn_positive")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(By.res("com.motorola.stylus:id/btn_positive"))) {
            mDevice.pressBack();
        }
        mUtils.sleep(Constants.TWO_SECONDS);

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/content")).setText("AViK");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObjects(By.res("com.motorola.stylus:id/icon")).get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_unCompleted_Notification");
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.waitForIdle();
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_unCompleted");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.res("com.motorola.stylus:id/cute_cb")).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtils.clickByResourceId("MoreOptions", "com.motorola.stylus:id/container_end");
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
//        mDevice.findObjects(mStylus.moreOptionsTitles).get(0).click();
        String deleteBtnStr = mUtils.getResourceByPackAndStringKey("com.motorola.stylus", "dialog_button_delete");
        UiObject2 deleteBtn = mDevice.findObject(By.text(deleteBtnStr));
        deleteBtn.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_MoreOptions_DeleteOne_Dialog");
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.clickByResourceId("Cancel", "com.motorola.stylus:id/btn_negative");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_Completed");
        mDevice.findObject(By.res("com.motorola.stylus:id/add_todo")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/todo_edit_text_view").setText("avik");
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.clickByResourceId("MoreOptions", "com.motorola.stylus:id/container_end");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(mStylus.moreOptionsTitles).get(1).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_ChecklistNote_MoreOptions_DeleteAll_Dialog");
        mUtils.clickByResourceId("Cancel", "com.motorola.stylus:id/btn_negative");
        mUtils.pressBackKeySeveralTimes(3);
    }

    @DeltaMethod
    @Test
    //preconditions: there is only every note for every kinds of note without title,and for merged note, there are two notes.
    public void captureScreensOfSearchAndDelete() throws Exception {
        mStylus.openApp();
        mUtils.sleep(Constants.TWO_SECONDS);
        if (mDevice.hasObject(mStylus.getStartedButton)) {
            mDevice.findObject(mStylus.getStartedButton).click();
        }
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/fab")), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.findObject(mStylus.recyclerView).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtil.clickOnScreenCenter();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mDevice.executeShellCommand("input text Avik");
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/container_start")), Constants.FIVE_SECONDS).getChildren().get(0).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DefaultNoteNames_Notes");
        pressListGridButton.run();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Main_List");

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/display_style").click();
        mUtils.sleep(Constants.ONE_SECOND);
        pressListGridButton.run();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Main_Grid");

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/display_style").click();
        mUtils.sleep(Constants.ONE_SECOND);
        pressSearchButton.run();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Main_Search");

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Notes_SearchNote");

        mUtils.createObjectByResourceID("android:id/search_src_text").setText("nothing");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Notes_SearchNote_NotFound");

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);


        mDevice.findObject(mStylus.canvasesTab).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DefaultNoteNames_Canvases");

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Canvases_SearchNote");

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);


        mDevice.findObject(mStylus.checklistTab).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_DefaultNoteNames_Checklists");

        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_Checklists_SearchNote");

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        mUtils.sleep(Constants.THREE_SECONDS);

        mDevice.findObject(mStylus.notesTab).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        UiObject2 note = mDevice.findObjects(By.res("com.motorola.stylus:id/note_title")).get(0);
        Rect position = note.getVisibleBounds();
        mDevice.swipe(position.centerX(), position.centerY(), position.centerX(), position.centerY(), 400);
        mUtils.sleep(Constants.ONE_SECOND);

//        mUtil.holdAndTakeScreenshotofToast("Delete", "com.motorola.stylus:id/display_style", "MotoStylus_OneDelete_Toast", 1000);
        UiObject2 deleteButton = mDevice.findObject(mStylus.toolBar).getChildren().get(1).getChildren().get(2);
        Rect deletePosition = deleteButton.getVisibleBounds();
        mDevice.swipe(deletePosition.centerX(), deletePosition.centerY(), deletePosition.centerX(), deletePosition.centerY(), 400);
        mUtils.sleep(Constants.ONE_SECOND);
        //new
        mDevice.takeAvikScreenshot("MotoStylus_OneDelete_Toast");
        deleteButton.click();
        mUtils.sleep(AvikConstants.NORMALWAIT);
        //new
        mDevice.takeAvikScreenshot("MotoStylus_OneDelete_Dialog");
        mDevice.pressBack();

        UiObject2 shareButton = mDevice.findObject(mStylus.toolBar).getChildren().get(1).getChildren().get(1);
        Rect sharePosition = shareButton.getVisibleBounds();
        mDevice.swipe(sharePosition.centerX(), sharePosition.centerY(), sharePosition.centerX(), sharePosition.centerY(), 400);
        mUtils.sleep(Constants.ONE_SECOND);
        //new
        mDevice.takeAvikScreenshot("MotoStylus_OneShare_Toast");
        shareButton.click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        //new
        mDevice.takeAvikScreenshot("MotoStylus_OneShare_Dialog");
        mDevice.pressBack();

        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.res("com.motorola.stylus:id/note_title")).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        deleteButton.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_ManyDelete_Dialog");
//        mUtils.skipAndroidButton2();
        mDevice.findObject(mStylus.declineButton).click();
        mUtils.sleep(Constants.ONE_SECOND);
        shareButton.click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.takeAvikScreenshot("MotoStylus_ManyShare_Dialog");
        mDevice.pressBack();
        mDevice.findObject(By.res("com.motorola.stylus:id/action_mode_close_button")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtils.pressBackKeySeveralTimes(3);
    }

    @DeltaMethod
    @Test
    //VPN and gmail account logged-in
    public void captureScreensOfMotoNotewithAccountLogin() throws Exception {
        mStylus.clearApp();
        mDevice.pressHome();
        mUtils.sleep(Constants.FIVE_SECONDS);
        mStylus.openStylusSettings();

        mDevice.findObject(mStylus.settingsAutoSync).click();
        mDevice.wait(Until.hasObject(mStylus.accountName), AvikConstants.LONGERWAIT * 6);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSync_ChooseUser_GMS");
        mDevice.findObject(mStylus.accountName).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSync_SignInSuccessfullyToast");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.settingsAutoSync).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.sleep(700);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSync_OFFToast");

        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(mStylus.settingsAutoSync).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.sleep(700);
        mDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSync_ONToast");
    }

    @Test
    public void testMain() {
        try {
            captureScreensOfMotoNoteMain();
            captureScreensOfMergedNote();
            captureScreensOfDrawNote();
            captureScreensOfChecklistNote();
            captureScreensOfSearchAndDelete();
            captureScreensOfMotoNotewithAccountLogin();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
        }
    }
}
