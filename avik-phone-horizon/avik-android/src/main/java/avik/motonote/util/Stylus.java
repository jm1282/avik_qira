package avik.motonote.util;

import android.app.UiAutomation;
import android.os.SystemClock;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import java.util.Locale;

public class Stylus {

    final String hCalcPackage = "com.motorola.handwritingcalculator";
    private final UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private final AvikUtility mUtility = AvikUtility.getInstance();
    private final ObjectUtils mUtils = new ObjectUtils();
    public BySelector tooltipButton = By.res("com.motorola.stylus:id/button");
    public BySelector createNote = By.res("com.motorola.stylus:id/fab");
    public BySelector fabItem = By.res("com.motorola.stylus:id/fab_item");
    public BySelector recyclerView = By.res("com.motorola.stylus:id/recycler_view");
    public BySelector moreOptionsTitles = By.res("com.motorola.stylus:id/title");
    public BySelector getStartedButton = By.res("com.motorola.stylus:id/startBtn");
    //    public BySelector moreOptionsButton = By.res("com.motorola.stylus:id/toolbar");
    public BySelector toolBar = By.res("com.motorola.stylus:id/toolbar");
    public BySelector a = By.res("com.android.permissioncontroller:id/permission_allow_always_button");
    public BySelector a1 = By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
    public BySelector a2 = By.res("com.android.permissioncontroller:id/permission_allow_button");
    public BySelector deny = By.res("com.android.permissioncontroller:id/permission_deny_button");
    public BySelector allowAll = By.res("com.android.permissioncontroller:id/permission_allow_all_button");
    public BySelector tutorialButton;
    public BySelector settingsButton;
    public BySelector settingsAboutButton;
    public BySelector penPreferencesButton;
    public BySelector languageSupportButton;
    public BySelector nextButton = By.res("com.motorola.stylus:id/next");
    public BySelector LanguageSupportEnglish = By.res("com.motorola.stylus:id/english");
    public BySelector LanguageSupportPortuguese = By.res("com.motorola.stylus:id/portuguese");
    public BySelector LanguageSupportSpanish = By.res("com.motorola.stylus:id/spanish");
    public BySelector OKButton = By.res("com.motorola.stylus:id/continue_button");
    public BySelector settingsAutoSync;
    public BySelector moreOptionsButton;

    //All Notes page
    public BySelector allNotesButton = By.res("com.motorola.stylus:id/clickable_layout");
    public BySelector uncategorizedButton = By.res("com.motorola.stylus:id/drawer_uncategorized");
    public BySelector editButton = By.res("com.motorola.stylus:id/category_edit");
    public BySelector addACategoryButton = By.res("com.motorola.stylus:id/add_new_category");
    public BySelector newCategoryName = By.res("com.motorola.stylus:id/contentEditor");
    public BySelector doneButton = By.res("com.motorola.stylus:id/btnDone");
    public BySelector cancelButton = By.res("com.motorola.stylus:id/btnCancel");
    public BySelector categoryList = By.res("com.motorola.stylus:id/category_list");
    public BySelector deleteButton = By.res("com.motorola.stylus:id/deleteButton");

    public BySelector notesTab = By.res("com.motorola.stylus:id/navigation_notes");
    public BySelector canvasesTab = By.res("com.motorola.stylus:id/navigation_canvases");
    public BySelector checklistTab = By.res("com.motorola.stylus:id/navigation_checklists");
    public BySelector chooseAnImage;
    public BySelector OCRCloseButton = By.res("com.motorola.stylus:id/ocr_close");
    public BySelector copyToNoteButton = By.res("com.motorola.stylus:id/ocr_copy");
    public BySelector accountName = By.res("com.google.android.gms:id/account_name");

    //Sketch to image page
    public BySelector sketchToImageButton = By.res("com.motorola.stylus:id/sketch_to_image");
    public BySelector sketchPenButton = By.res("com.motorola.stylus:id/pen_button");
    public BySelector selectStylelist = By.res("com.motorola.stylus:id/acb_select_style");
    public BySelector generateButton = By.res("com.motorola.stylus:id/acb_generate");
    public BySelector styleContainer = By.res("com.motorola.stylus:id/styleContainer");

    public BySelector sketchDeleteButton;
    public BySelector sketchUndoButton;
    public BySelector sketchRedoButton;
    public BySelector AddToNoteButton = By.res("com.motorola.stylus:id/atn_add_btn");
    public BySelector snackBarCloseButton = By.res("com.motorola.stylus:id/snackbar_close");


    public BySelector agreeButton = By.res("com.motorola.stylus:id/btn_positive");
    public BySelector declineButton = By.res("com.motorola.stylus:id/btn_negative");


    {
        try {
            String tutorialText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "menu_title_tutorial");
            tutorialButton = By.text(tutorialText);
            String settingsText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "note_settings_title");
            settingsButton = By.text(settingsText);
            String settingsAboutText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "settings_about");
            settingsAboutButton = By.text(settingsAboutText);
            String penPrefString = mUtils.getResourceByPackAndStringKey("com.motorola.stylus", "settings_pen_default_title");
            penPreferencesButton = By.text(penPrefString);
            String languageSupportText = mUtils.getResourceByPackAndStringKey("com.motorola.stylus", "transcription_language_pref_title");
            languageSupportButton = By.text(languageSupportText);
            String settingsAutoSyncText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "settings_backup_notes_title");
            settingsAutoSync = By.text(settingsAutoSyncText);
            String chooseAnImageText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "choose_a_photo");
            chooseAnImage = By.text(chooseAnImageText);

            String moreOptionsText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "action_menu_overflow_description");
            moreOptionsButton = By.text(moreOptionsText);

            sketchDeleteButton = By.text(mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "paint_clear_all"));
            String sketchUndoText = mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "paint_undo");
            sketchUndoButton = By.text(sketchUndoText);
            sketchRedoButton = By.text(mUtility.getResourceByPackAndStringKey("com.motorola.stylus", "paint_redo"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    UiObject2 tutorialButton = mDevice.hasObject(By.text(tutorialText));

//    public Stylus() {
//        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//        mUtility = AvikUtility.getInstance();
//        mUtils = new ObjectUtils();
//    }

    public void clearApp() throws Exception {
        mUtils.runCommand("pm clear com.motorola.stylus");
        mUtils.runCommand("pm clear com.motorola.activestylus");
        mUtils.runCommand("pm clear com.motorola.handwriting");
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void forceCloseApp() throws Exception {
        System.out.println("=== Closing the Stylus app ===");
        mUtils.runCommand(String.format("am force-stop %s", "com.motorola.stylus"));
        mUtils.runCommand(String.format("am force-stop %s", "com.motorola.activestylus"));
        mUtils.runCommand(String.format("am force-stop %s", "com.motorola.handwriting"));
    }

    public void openApp() throws Exception {
        mUtility.pressBackKeySeveralTimes(3);
        mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.manager.NoteManagerActivity");
        mUtils.sleep(Constants.TWO_SECONDS);
    }

    public void clickScreenMakeEditIconVisible() throws Exception {
        AvikLogger.info("@@@@@@@@@@@Click screen, make Edit icon visible");
        UiObject columnHandle = mUtils.createObjectbyText("ic_column_handle");
        for (int i = 1; i <= 5; i++) {
            mUtils.clickOnScreenCenter();
            mUtils.sleep(AvikConstants.SHORTWAIT);
            mDevice.click(200, 440);
            mUtility.waitForObjAppear("ic_column_handle", columnHandle, AvikConstants.LONGWAIT);
            mUtils.sleep(Constants.FIVE_SECONDS);
            AvikLogger.info("@@@@@@@@@@@ Edit icon is visible");
            break;
        }
    }

    public void inputCharacter(int times) throws Exception {
        for (int i = 1; i <= times; i++) {
            AvikLogger.info("Input Character: " + 20 * i);
            mUtility.runShellCommand("input text aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
    }

    public void openStylusSettings() throws Exception {
//        mUtility.runShellCommand("am start -n com.motorola.stylus/com.motorola.stylus.settings.activity.StylusSettingsActivity");
        mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.settings.activity.NoteSettingActivity");
        mUtils.sleep(Constants.TWO_SECONDS);
    }

    public UiObject2 getPermissionObj() {

        if (mDevice.hasObject(a)) {
            return mDevice.findObject(a);
        }
        if (mDevice.hasObject(a1)) {
            return mDevice.findObject(a1);
        }
        if (mDevice.hasObject(a2)) {
            return mDevice.findObject(a2);
        }
        return null;
    }

    public void clickAllowPermission() {

        if (mDevice.hasObject(a2)) {
            mDevice.findObject(a2).click();
        } else if (mDevice.hasObject(a1)) {
            mDevice.findObject(a1).click();
        }
        SystemClock.sleep(3000);
    }

    public void createNewNote1(String createNewItem) throws Exception {
        openApp();
        for (int i = 1; i <= 3; i++) {
            if (getPermissionObj() != null) {
                clickAllowPermission();
            }
        }
        mDevice.findObject(moreOptionsButton).getChildren().get(0).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text("avik")).click();
        mUtils.sleep(Constants.TWO_SECONDS);

        AvikLogger.info("+++++++++++ Create New -> " + createNewItem);
        mUtility.clickByResourceId("Add note ", "com.motorola.stylus:id/fab");
        for (int i = 1; i <= 5; i++) {
            if (mUtility.createObjectByResourceID("com.motorola.stylus:id/button").exists()) {
                mUtility.createObjectByResourceID("com.motorola.stylus:id/button").click();
            }
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
        if (createNewItem.equals("TextNote")) {
            mUtility.clickByResourceId("TextNote ", "com.motorola.stylus:id/fab_item", 0);
        } else if (createNewItem.equals("DrawNote")) {
            mUtility.clickByResourceId("DrawNote ", "com.motorola.stylus:id/fab_item", 1);
        } else if (createNewItem.equals("AudioNote")) {
            mUtility.clickByResourceId("AudioNote ", "com.motorola.stylus:id/fab_item", 2);
        } else if (createNewItem.equals("ChecklistNote")) {
            mUtility.clickByResourceId("ChecklistNote ", "com.motorola.stylus:id/fab_item", 3);
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void createNewNote(String createNewItem) throws Exception {
        AvikLogger.info("+++++++++++ Create New -> " + createNewItem);

        if (createNewItem.equals("TextNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.note.text.TextNoteActivity");
        } else if (createNewItem.equals("DrawNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.UnlimitedCanvasActivity");
        } else if (createNewItem.equals("AudioNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.note.audio.my.AudioNoteActivity");
        } else if (createNewItem.equals("ChecklistNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.note.checklist.ChecklistNoteActivity");
        } else if (createNewItem.equals("MergedNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.MergedNotesActivity");
        } else if (createNewItem.equals("JournalNote")) {
            mUtility.runShellCommand("am start com.motorola.stylus/com.motorola.stylus.note.text.JournalNoteActivity");
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void drawn(String eventID) throws Exception {
        int touchId = 14;
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 1 330 1", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 57 %d", eventID, touchId));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 48 5", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 58 45", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 1 325 1", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 53 %d", eventID, 500));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 54 %d", eventID, 1800));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 0 0 0", eventID));

        for (int i = 1900; i < 2101; i = i + 50) {

            uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 53 %d", eventID, 500));
            uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 54 %d", eventID, i));
            uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 0 0 0", eventID));
        }

        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 3 57 -1", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 1 330 0", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 1 325 0", eventID));
        uiAutomation.executeShellCommand(String.format(Locale.ENGLISH, "sendevent %s 0 0 0", eventID));
    }

}