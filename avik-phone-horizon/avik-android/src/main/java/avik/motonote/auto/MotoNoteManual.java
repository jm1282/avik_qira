package avik.motonote.auto;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.libraries.settings.SettingsV;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.motonote.util.Stylus;

//PRE-REQUISITES:

/**
 * <PRE>
 * Screen Number: 32
 * Execution Time: 5 Min.
 * <p>
 * Preconditions:
 * 1)PEN INSIDE DEVICE, NO NEED TO REMOVE
 * </PRE>
 * <p>
 * Manual screen:
 * MotoStylus_InstantNote_SystemGesturesBlocked_Toast - Swipe from bottom up to trigger
 */
// =======================================================

@RunWith(AndroidJUnit4.class)
public class MotoNoteManual {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    final String eventID = "/dev/input/event3";
    //For Global Handwriting, start writing with stylus in a text box and click language button
    private final AvikScreenshotAction MotoStylus_GlobalHandwriting_ChangeLanguage = new AndroidAvikScreenshotAction("MotoStylus_GlobalHandwriting_ChangeLanguage", true);
    //Couldn't trigger these again
    private final AvikScreenshotAction MotoStylus_TurnOnBluetooth = new AndroidAvikScreenshotAction("MotoStylus_TurnOnBluetooth", true);
    private final AvikScreenshotAction MotoStylus_WantsBluetooth = new AndroidAvikScreenshotAction("MotoStylus_WantsBluetooth", true);
    // Add a table on a text note and click the column and row editors
    private final AvikScreenshotAction MotoStylus_TextNote_EditColumns = new AndroidAvikScreenshotAction("MotoStylus_TextNote_EditColumns", true);
    private final AvikScreenshotAction MotoStylus_TextNote_EditRow = new AndroidAvikScreenshotAction("MotoStylus_TextNote_EditRow", true);
    // Make a new checklist note and add an item with a time alert
    private final AvikScreenshotAction MotoStylus_MotoNote_ChecklistNote_Reminder_Notification = new AndroidAvikScreenshotAction("MotoStylus_MotoNote_ChecklistNote_Reminder_Notification", true);
    // Pull out stylus and in the shortcuts, click Live Message icon
    // Click color icon
    private final AvikScreenshotAction MotoStylus_LiveMessage_CustomColor = new AndroidAvikScreenshotAction("MotoStylus_LiveMessage_CustomColor", true);
    // Press checkmark after drawing, then wait for toast
    private final AvikScreenshotAction MotoStylus_LiveMessage_Saved_Toast = new AndroidAvikScreenshotAction("MotoStylus_LiveMessage_Saved_Toast", true);
    // Keep drawing until toast triggers
    private final AvikScreenshotAction MotoStylus_LiveMessage_isFull_Toast = new AndroidAvikScreenshotAction("MotoStylus_LiveMessage_isFull_Toast", true);
    // Click trash can icon
    private final AvikScreenshotAction MotoStylus_LiveMessage_Delete_Dialog = new AndroidAvikScreenshotAction("MotoStylus_LiveMessage_Delete_Dialog", true);
    // Enable gesture navigation, in draw note try doing the swipe up to change app
    private final AvikScreenshotAction MotoStylus_DrawNote_UnlockGesturesInMenu_Toast = new AndroidAvikScreenshotAction("MotoStylus_DrawNote_UnlockGesturesInMenu_Toast", true);
    // Use lasso tool to trigger the popup
    private final AvikScreenshotAction MotoStylus_DrawNote_LassoTool_Tooltip = new AndroidAvikScreenshotAction("MotoStylus_DrawNote_LassoTool_Tooltip", true);
    // Try typing a title over 40 characters on a note
    private final AvikScreenshotAction MotoStylus_TextNote_SetTitle_Over40Characters_Toast = new AndroidAvikScreenshotAction("MotoStylus_TextNote_SetTitle_Over40Characters_Toast", true);
    private final AvikScreenshotAction Settings_Main_SmartPenSettings = new AndroidAvikScreenshotAction("Settings_Main_SmartPenSettings", true);
    private final AvikScreenshotAction ActiveStylus_Settings_LastKnownLocation_LastLocation = new AndroidAvikScreenshotAction("ActiveStylus_Settings_LastKnownLocation_LastLocation", true);
    private UiDevice mDevice;
    private AvikUiDevice mAvikUIDevice;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private Stylus mStylus;

    private SettingsV mSettings;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
        mStylus = new Stylus();
        mSettings = new SettingsV();
        mAvikUIDevice = AvikUiDevice.getInstance();
    }

    public void captureCharging() throws Exception {
        logger.info("OPEN STYLUS SETTINGS");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_UnlockGesturesInMenu_Toast);
    }

    public void captureBluetoothRequest() throws Exception {
        logger.info("CLEAR APP, TURN OFF BLUETOOTH AND PULL STYLUS, CLICK NOT NOW AND DO IT AGAIN");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TurnOnBluetooth);
    }

    public void captureWantsBluetooth() throws Exception {
        logger.info("TURN OFF BLUETOOTH AND PULL STYLUS");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_WantsBluetooth);
    }

    public void captureHandwritingLanguage() throws Exception {
        //RECAPTURE MANUALLY
        logger.info("WRITE WITH STYLUS AND CLICK [EN]");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_GlobalHandwriting_ChangeLanguage);
    }

    public void captureLiveMessage() throws Exception {
        logger.info("OPEN LIVE MESSAGE COLOR MENU");
        mUtils.sleep(Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_CustomColor);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_isFull_Toast);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_Saved_Toast);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_Delete_Dialog);
    }

    public void captureNoteScreens() throws Exception {
//        mAvikUIDevice.pressBack();
//        mAvikUIDevice.pressHome();
//        mUtils.sleep(Constants.FIVE_SECONDS);
//        mAvikUIDevice.takeAvikScreenshot("MotoStylus_Icons");
//        AvikLogger.info("Manually go to 'select all' page");
//        mStylus.createNewNote("MergedNote");
//        mUtils.sleep(Constants.TEN_SECONDS);
//        mUtils.sleep(Constants.FIVE_SECONDS);
//        mAvikUIDevice.takeAvikScreenshot("MotoStylus_Notes_SelectAll");

        mAvikUIDevice.takeAvikScreenshot("MotoStylus_Settings_AutoSync_SignIN");
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_EditColumns);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_EditRow);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_TextNote_SetTitle_Over40Characters_Toast);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_UnlockGesturesInMenu_Toast);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_DrawNote_LassoTool_Tooltip);
//        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MotoNote_ChecklistNote_Reminder_Notification);
    }

    public void captureAllChangeLater() throws Exception {
        //mUtility.takeAvikScreenshotWithFlag(MotoStylus_Settings_Charging);
        //mUtility.takeAvikScreenshotWithFlag(MotoStylus_InstantNote_Welcome);
        // Problems with recapturing, do it with the script
        //mUtility.takeAvikScreenshotWithFlag(MotoStylus_TurnOnBluetooth);
        //mUtility.takeAvikScreenshotWithFlag(MotoStylus_WantsBluetooth);
        //mUtility.takeAvikScreenshotWithFlag(MotoStylus_GlobalHandwriting_ChangeLanguage);
        // Check which locales need this
        AvikLogger.info("please prepare external display++++++");
        mUtils.sleep(Constants.TEN_SECONDS);
        mAvikUIDevice.takeAvikScreenshot("hahaExternalDisplay1", "4627039422300187651");
        mUtility.runShellCommand("am start com.motorola.camera3/com.motorola.camera.cli.camera.CliCameraActivity");
        mUtils.sleep(Constants.TWO_SECONDS);
        mAvikUIDevice.takeAvikScreenshot("openCamera", "4627039422300187651");
        /*
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_MotoNote_ChecklistNote_Reminder_Notification);
    	mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_CustomColor);
    	mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_isFull_Toast);
        mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_Delete_Dialog);
    	mUtility.takeAvikScreenshotWithFlag(MotoStylus_LiveMessage_Saved_Toast);
        */
    }

    public void captureSettings() throws Exception {
        mSettings.forceCloseApp();
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtils.launchPkgByIntent(mSettings.SETTINGS_PACKAGE);
        mUtils.sleep(Constants.THREE_SECONDS);

        String activePenSettings = mUtils.getResourceByPackAndStringKey("com.motorola.activestylus", "stylus_active");
        mUtils.scrollToText(activePenSettings);
        mUtils.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(Settings_Main_SmartPenSettings);
        mUtils.sleep(Constants.HALF_SECOND);
        mSettings.forceCloseApp();
    }

    public void captureLocationData() throws Exception {
        mUtils.sleep(Constants.THREE_SECONDS);

        mUtility.takeAvikScreenshotWithFlag(ActiveStylus_Settings_LastKnownLocation_LastLocation);
        mUtils.sleep(Constants.ONE_SECOND);

    }
    @Test
    public void testMain() {
        try {
            //captureNoteScreens();
//            captureCharging();
//            captureBluetoothRequest();
//            captureWantsBluetooth();
//            captureHandwritingLanguage();
//            captureLiveMessage();
//            captureAllChangeLater();
            //captureSettings();
            captureLocationData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
