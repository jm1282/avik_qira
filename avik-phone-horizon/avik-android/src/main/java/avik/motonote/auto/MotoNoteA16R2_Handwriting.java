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
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
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
public class MotoNoteA16R2_Handwriting {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_CircleText = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_CircleText", LocaleEnum.AR_EG.toLocale(), LocaleEnum.FR_FR.toLocale(), LocaleEnum.PT_BR.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_EasyLineBreaks = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_EasyLineBreaks", LocaleEnum.AR_EG.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_HandwritingTools = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_HandwritingTools", LocaleEnum.EN_GB.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_JoinOrSeparate = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_JoinOrSeparate", LocaleEnum.AR_EG.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_RemoveWords = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_RemoveWords", LocaleEnum.AR_EG.toLocale(), LocaleEnum.ES_ES.toLocale(), LocaleEnum.JA_JP.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_WriteItOut = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_WriteItOut", LocaleEnum.AR_EG.toLocale(), LocaleEnum.JA_JP.toLocale(), LocaleEnum.RO_RO.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_WriteWithStylus = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_WriteWithStylus", LocaleEnum.PT_BR.toLocale(), LocaleEnum.RO_RO.toLocale(), LocaleEnum.ZH_CN.toLocale());
    private final AvikScreenshotAction ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage", LocaleEnum.AR_EG.toLocale());
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_CircleText = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_CircleText", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_EasyLineBreaks = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_EasyLineBreaks", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_JoinOrSeparate = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_JoinOrSeparate", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_RemoveWords = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_RemoveWords", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_WriteItOut = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_WriteItOut", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_Tutorial_HandwritingTools = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_Tutorial_HandwritingTools", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_WriteWithStylus = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_WriteWithStylus", true);
    //private AvikScreenshotAction ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage", true);
    private final AvikScreenshotAction ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage_PackCannotBeDeleted = new AndroidAvikScreenshotAction("ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage_PackCannotBeDeleted", true);
    public UiObject2 moreOption;
    private Util mUtil;
    private AvikUiDevice mDevice;
    Thread pressListGridButton = new Thread(new Runnable() {
        public void run() {
            UiObject2 listGridButton = mDevice.findObject(By.res("com.motorola.stylus:id/display_style"));
            Rect position = listGridButton.getVisibleBounds();
            mDevice.swipe(position.centerX(), position.centerY(), position.centerX(), position.centerY(), 400);
        }
    });
    // ADD THE REMOVE LANGUAGE THINGY
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
    public void captureScreensOfHandwriting() throws Exception {
        mStylus.clearApp();
        mDevice.pressHome();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.runShellCommand("am start -n com.motorola.handwriting/com.motorola.handwriting.settings.HandwritingSettingsActivity");
        mUtility.sleep(Constants.THREE_SECONDS);
        mDevice.wait(Until.hasObject(By.res("com.motorola.handwriting:id/operate_text")), Constants.FIVE_SECONDS);

        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_WriteItOut);
        mUtility.sleep(Constants.ONE_SECOND);
        BySelector nextButton = By.res("com.motorola.handwriting:id/next");

        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_RemoveWords);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_CircleText);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_JoinOrSeparate);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_EasyLineBreaks);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(nextButton), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mObjectUtils.createScrollable().scrollForward();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_Tutorial_HandwritingTools);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.handwriting:id/btn_ok")), Constants.THREE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.hasObject(By.res("com.motorola.handwriting:id/switch_text")), Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_WriteWithStylus);
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.handwriting:id/recycler_view")), Constants.THREE_SECONDS).getChildren().get(1).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage);
        mDevice.findObject(By.res("com.motorola.handwriting:id/button_download")).click();
        mUtility.sleep(Constants.TEN_SECONDS);
        mDevice.findObjects(By.res("com.motorola.handwriting:id/radio_button_select")).get(1).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.motorola.handwriting:id/button_delete")).click();
        mUtility.sleep(Constants.ONE_SECOND);
        //mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Handwriting_WriteWithStylus_HandwritingLanguage_PackCannotBeDeleted);

        mUtility.sleep(Constants.ONE_SECOND);
        mUtility.pressBackKeySeveralTimes(5);
        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(0).longClick();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_IconShortcuts_pauseAPP);
////        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(1).longClick();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_IconShortcuts_APPInfo);
////        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(2).longClick();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_IconShortcuts_installInSecureFolder);
////
////        mDevice.findObject(By.res("com.motorola.launcher3:id/system_shortcuts_container")).getChildren().get(0).click();
////        mUtility.sleep(Constants.FIVE_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_IconShortcuts_pauseAPP_Notification_GMS);
////        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(By.res("android:id/button2")).click();
//
//        mStylus.openApp();
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_PermissionStatement_Scrolling1);
////
////        mDevice.swipe(500, 2200, 500, 1400, 50);
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_PermissionStatement_Scrolling2);
////        mUtility.clickByResourceId("Agree", "android:id/button1");
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Tutorial_getStarted);
//
//        mDevice.findObject(mStylus.getStartedButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        if (mDevice.hasObject(mUtil.a2)) {
//            AvikLogger.info("IGNORING NOTIFICATION PERMISSION");
//            mUtility.takeAvikScreenshotWithFlag(MotoStylus_NotificationPermission_GMS);
//            mDevice.findObject(mUtil.a2).click();
//        }
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_noNotes);
//
//        //click "More Options"
//
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).longClick();
////        mDevice.findObject(mStylus.tutorialButton).click(300);
////        mUtility.createObjectByDescription("More options").longClick();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MoreOptions_Tip);
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MoreOptions);
//
//        mDevice.findObject(mStylus.tutorialButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mObjectUtils.createScrollable().scrollForward();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_SketchIntoImage_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mObjectUtils.createScrollable().scrollForward();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_AutomaticShaping_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mObjectUtils.createScrollable().scrollForward();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_EnlargeCanvas_Dialog);
//        mDevice.findObject(mStylus.nextButton).click();
//
//        mUtility.sleep(Constants.ONE_SECOND);
//        mUtility.createObjectByResourceID("com.motorola.stylus:id/toolbar").getChild(new UiSelector().className("androidx.appcompat.widget.LinearLayoutCompat")).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(mStylus.settingsButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_Scrolling1);
//        mObjectUtils.createScrollable().scrollForward();
//        mUtility.sleep(Constants.ONE_SECOND);
////        //ToDo need manually check whether there are one more pages, if there is only one page, then don't need to scroll and capture.
////        UiScrollable container = new UiScrollable(new UiSelector().resourceId("com.motorola.stylus:id/scroll_container"));
////        container.scrollToEnd(2, 55);
////        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_Scrolling2);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.settingsAboutButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_About);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mObjectUtils.createScrollable().scrollBackward();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//////        container.scrollToBeginning(40);
//        mDevice.findObject(mStylus.languageSupportButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_LanguageSupport_options);
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.OKButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
////        mDevice.findObject(mStylus.LanguageSupportPortuguese).click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_Languagesupport_portuguese);
////        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(mStylus.languageSupportButton).click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(mStylus.LanguageSupportSpanish).click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_Languagesupport_spanish);
////        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(mStylus.languageSupportButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
////        mDevice.findObject(mStylus.LanguageSupportEnglish).click();
////        mUtility.sleep(Constants.ONE_SECOND);
//
////        //ToDo need manually check whether there is a pen with testing phone, if there is no pen, then Comment out the following code.
//        mDevice.findObject(mStylus.penPreferencesButton).click();
//        mUtility.sleep(Constants.THREE_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_PenPreferences);
//        mDevice.findObject(mStylus.recyclerView).getChildren().get(6).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_PenPreferences_Colors);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(mStylus.settingsAutoSync).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_AutoSnc_Toast);
//        mUtility.sleep(Constants.ONE_SECOND);
//
////        String stylusSettings = mObjectUtils.getResourceByPackAndStringKey("com.motorola.stylus","stylus_setting");
////        mDevice.wait(Until.findObject(By.text(stylusSettings)),Constants.THREE_SECONDS).click();
////        mUtility.sleep(Constants.TWO_SECONDS);
////
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_StylusSettings_Scrolling1);
////        mUtility.sleep(Constants.HALF_SECOND);
////        mObjectUtils.createScrollable().scrollForward();
////        mUtility.sleep(Constants.ONE_SECOND);
////
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_StylusSettings_Scrolling2);
////        mUtility.sleep(Constants.HALF_SECOND);
////
////        mObjectUtils.createScrollable().scrollBackward();
////        mUtility.sleep(Constants.ONE_SECOND);
////
////        stylus_settings_global_handwriting_title
//
//
//        mDevice.pressBack();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.allNotesButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_Classification);
//
//        mDevice.findObject(mStylus.uncategorizedButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_emptyCategory);
//
//        mDevice.findObject(mStylus.allNotesButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.editButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManageCategories);
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.addACategoryButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManageCategories_NewCategory);
//
//        mDevice.findObject(mStylus.newCategoryName).setText("avik");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.doneButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.addACategoryButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.newCategoryName).setText("avik");
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(mStylus.doneButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManageCategories_NewCategory_exists);
//        mDevice.findObject(mStylus.cancelButton).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//
//        mDevice.findObject(mStylus.categoryList).getChildren().get(3).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ManageCategories_EditCategory);
//        mDevice.findObject(mStylus.cancelButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//        mDevice.findObject(mStylus.deleteButton).click();
//        mUtility.sleep(Constants.ONE_SECOND);
//
//        mDevice.pressBack();
//        mDevice.pressBack();
//
//        mDevice.findObject(mStylus.canvasesTab).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_NoNote);
//
//
//        mDevice.findObject(mStylus.checklistTab).click();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_NoNote);
//
////        mUtility.clickByResourceId("MergedNotes", "com.motorola.stylus:id/navigation_notes");
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MergedNotes_SearchMergedNotes);
////        mUtility.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
////
////        mUtility.clickByResourceId("Canvases", "com.motorola.stylus:id/navigation_canvases");
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_CanvasesNote_SearchCanvasesNote);
////        mUtility.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
////
////        mUtility.clickByResourceId("Checklists", "com.motorola.stylus:id/navigation_checklists");
////        mUtility.sleep(Constants.TWO_SECONDS);
////        mUtility.createObjectByResourceID("com.motorola.stylus:id/action_search").click();
////        mUtility.sleep(Constants.ONE_SECOND);
////        mUtility.takeAvikScreenshotWithFlag(MotoStylus_ChecklistNote_SearchChecklistNote);
////        mUtility.clickByResourceId("ReturnSearch", "com.motorola.stylus:id/search_mag_icon");
    }

    @Test
    public void testMain() {
        try {
            captureScreensOfHandwriting();
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