package avik.motohealth.nightlyauto;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.motohealth.utils.TestUtils;


@RunWith(AndroidJUnit4.class)
public class HealthDeviceSettings {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice = AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility = AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        mUtil.openMotoWatch();
        mUtil.clickByText("Watch", "com.motorola.watch", "watch");
        mUtil.clickByText("Watch settings", "com.motorola.watch", "button_setting");
        mUtil.sleep(1000);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Scrolling1");
        mUtil.findObjFromListByResViaClazz("Health and fitness", "com.motorola.watch:id/recycler_view",
                "androidx.appcompat.widget.LinearLayoutCompat", 1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Health");
        mUtil.clickByObj("Exercise", "android:id/summary", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthExercise");
        mUtil.clickByObj("Auto pause recording", "com.motorola.watch:id/titleTextView", 1);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthExerciseAutoPauseRecording");
        mUtil.clickByObj("", "com.motorola.watch:id/switchStatus", 1);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthExerciseAutoPauseRecordingTurnOff_Dialog");
        mUtil.clickByObj("", "com.motorola.watch:id/bt_cancel", 0);
        mUtil.pressBack(1);
        mUtil.pressBack(1);
        mUtil.clickByText("Heart rate", "com.motorola.watch", "heart_rate");
        mUtil.clickByText("Monitor every 6 minutes", "com.motorola.watch", "heart_rate_measurement_option_auto");
        mUtil.disabledCheckBtn("com.motorola.watch:id/switchStatus",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthHeartRate_OFF");
        // new ui
//        mUtil.clickByText("Heart rate alerts","com.motorola.watch","heart_rate_alert_title");
        mUtil.clickByObj("Heart rate alerts", "com.motorola.watch:id/heart_alert", 0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_Settings_HeartRateAlerts");
        mUtil.clickByText("High HR threshold", "com.motorola.watch", "high_HR_alert_title");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_Settings_HeartRateAlertsHigh_Dialog");
        mUtil.clickByObj("", "com.motorola.watch:id/cancel_btn", 0);
        mUtil.clickByText("Low HR threshold", "com.motorola.watch", "low_HR_alert_title");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_Settings_HeartRateAlertsLow_Dialog");
        mUtil.clickByObj("", "com.motorola.watch:id/cancel_btn", 0);
        mUtil.pressBack(1);
        mUtil.enabledCheckBtn("com.motorola.watch:id/switchStatus",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthHeartRate_ON");
        mUtil.disabledCheckBtn("com.motorola.watch:id/switchStatus",0);
        mUtil.pressBack(1);
        mUtil.clickByText("Stress", "com.motorola.watch", "stress_settings_title");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthStress");
        mUtil.pressBack(1);
//        mUtil.clickByObj("Sleep mode","android:id/title",3);
//        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthSleepMode");
//mUtil.clickByObj("sleep mode schedule","android:id/title",0);
//mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthSleepModeSchedule");
//mUtil.pressBack(1);
//mUtil.pressBack(1);
        mUtil.clickByText("Sedentary reminder", "com.motorola.watch", "sedentary_reminder");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthSedentaryReminder");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthSedentaryReminder_2");
        mUtil.clickByObj("start time", "android:id/summary", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthSedentaryReminderStartTime");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/cancel_btn", 0);
        mUtil.clickByObj("end time", "android:id/summary", 1);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_HealthSedentaryReminderEndtTime");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/cancel_btn", 0);
        mUtil.pressBack(1);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);


        mUtil.findObjFromListByResViaClazz("Display", "com.motorola.watch:id/recycler_view",
                "androidx.appcompat.widget.LinearLayoutCompat", 3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Display_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Display_Scrolling2");

        mUtil.getListView().scrollToBeginning(50);
        mUtil.clickByObj("Screen timeout", "android:id/summary", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_DisplayScreenTimeout_Dialog");
        mUtil.clickByObj("", "com.motorola.watch:id/timeout_30", 0);
        mUtil.pressBack(1);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);


        mUtil.findObjFromListByResViaClazz("Notifications", "com.motorola.watch:id/recycler_view",
                "androidx.appcompat.widget.LinearLayoutCompat", 4).click();
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Notifications_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Notifications_Scrolling2");
        mUtil.getListView().scrollToBeginning(50);
        mUtil.clickByObj("App notifications", "android:id/summary", 0);
        mUtil.sleep(1000);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_NotificationsAppNotifications");
        mUtil.pressBack(1);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);


        mUtil.findObjFromListByResViaClazz("Vibration", "com.motorola.watch:id/recycler_view",
                "androidx.appcompat.widget.LinearLayoutCompat", 5).click();
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Vibration");
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.findObjFromListByResViaClazz("Sleep mode", "com.motorola.watch:id/recycler_view",
                "androidx.appcompat.widget.LinearLayoutCompat", 6).click();
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_SleepModeOff");
        mUtil.clickByObj("","com.motorola.watch:id/switch_title",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_SleepModeOn");
        mUtil.clickByObj("","com.motorola.watch:id/switch_title",0);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);


        mUtil.clickByText("Weather", "com.motorola.watch", "setting_weather_title");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Weather");
        mUtil.clickByObj("", "com.motorola.watch:id/title_refresh", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_WeatherRefresh");
        mUtil.pressBack(1);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);

        mUtil.clickByText("Wrist preference", "com.motorola.watch", "setting_preference_title");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Preference_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Preference_Scrolling2");
//        mUtil.clickByText("Double-press Shortcut", "com.motorola.watch", "double_press_settings_for_juste");


        mUtil.clickByObj("double_press_settings_for_juste","android:id/title",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_PreferenceApps_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_PreferenceApps_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_PreferenceApps_Scrolling3");
        mUtil.pressBack(1);


        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Scrolling2");
        mUtil.clickByText("Advanced", "com.motorola.watch", "setting_advanced_title");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Advanced");
        mUtil.clickByText("Backup data", "com.motorola.watch", "backup_data");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_AdvancedBackup");
        mUtil.pressBack(1);
        mUtil.clickByText("Restore data", "com.motorola.watch", "restore_data");
        mUtil.takeAvikScreenshotToast("MotoHealth_Devices_Settings_AdvancedRestore_Toast");
        mUtil.waitFor10S(3, "toast disappear");
        mUtil.clickByText("Delete backups", "com.motorola.watch", "delete_backups");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_AdvancedDeleteNoData");
        mUtil.pressBack(1);
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.getListView().scrollToEnd(100);

        mUtil.clickByText("About watch", "com.motorola.watch", "about_watch");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_About");
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Safety and emergency", "com.motorola.watch", "safety_emergency");
//        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Safety");
        mUtil.clickByObj("", "com.motorola.watch:id/title_emergency", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_SafetyEmergency");
//mUtil.clickByObj("Use Emergency SOS","com.motorola.watch:id/title",0);
//mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_SafetyEmergency_SetNow_Dialog");
//mUtil.clickByObj("Cancel","com.motorola.watch:id/dialog_cancel_button",0);
        mUtil.pressBack(1);
        mUtil.clickByObj("", "com.motorola.watch:id/title_fall_detection", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_SafetyHardFallDetection");
//        mUtil.clickByObj("Use Fall detection","com.motorola.watch:id/title",0);
//        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_SafetyHardFallDetection_SetNow_Dialog");
//        mUtil.clickByObj("Cancel","com.motorola.watch:id/dialog_cancel_button",0);
        mUtil.pressBack(1);
        mUtil.clickByText("Medical card", "com.motorola.watch", "medical_card");
        mUtil.clickByText("Name", "com.motorola.watch", "name");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Name");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/dialog_cancel_button", 0);
        mUtil.clickByText("Address", "com.motorola.watch", "address_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Address");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/dialog_cancel_button", 0);
        mUtil.clickByText("Blood type", "com.motorola.watch", "blood_type_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_BloodType");
        mUtil.pressBack(1);
        mUtil.clickByText("Allergies", "com.motorola.watch", "allergies_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Allergies");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/dialog_cancel_button", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Medications", "com.motorola.watch", "medications_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Medications");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/dialog_cancel_button", 0);
        mUtil.clickByText("Organ donor", "com.motorola.watch", "organ_donor_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_OrganDonor");
        mUtil.pressBack(1);
        mUtil.clickByText("Medical note", "com.motorola.watch", "medical_note_text");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_MedicalNote");
        mUtil.clickByObj("Cancel", "com.motorola.watch:id/dialog_cancel_button", 0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettings_MedicalCard_Scrolling2");
        mUtil.pressBack(1);
        mUtil.clickByObj("Call message for help", "com.motorola.watch:id/emergency_contact_text", 0);
//        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Safety_AddContact");
        mUtil.clickByText("Add contact", "com.motorola.watch", "add_contact");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Safety_AddContactNoContact");

        mUtil.pressBack(2);


        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Find my watch", "com.motorola.watch", "find_my_watch");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Find");
        mUtil.clickByObj("", "com.motorola.watch:id/ring_btn", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_FindStop");
        mUtil.clickByObj("", "com.motorola.watch:id/ring_btn", 0);

        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Tips", "com.motorola.watch", "setting_tips");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Tips1");
        mUtil.clickByObj("", "com.motorola.watch:id/next_btn_icon", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Tips2");
        mUtil.clickByObj("", "com.motorola.watch:id/next_btn_icon", 0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Tips3");
        mUtil.clickByObj("", "com.motorola.watch:id/next_btn_icon", 0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Firmware update", "com.motorola.watch", "firmware_update");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Firmware");
        mUtil.clickByObj("BACK", "android.widget.ImageButton", 0);
    }
    @Before
    public void setup() throws Exception {
        mUtil.writeLog("setup");
//        mUtil.pressBack(5);
        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }

    @After
    public void tearDown() throws InterruptedException {
        mUtil.pressBack(5);
        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {
        captureOfHealth();
    }
}
