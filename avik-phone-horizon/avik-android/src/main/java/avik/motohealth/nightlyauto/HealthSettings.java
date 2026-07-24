package avik.motohealth.nightlyauto;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
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
public class HealthSettings {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealthMain() throws Exception {
        mUtil.openMotoWatch();
        mUtil.clickByText("My page","com.motorola.watch","my_page_title");
        mUtil.clickByObj("","com.motorola.watch:id/user_info",0);
        mUtil.sleep(2000);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Account");
        mUtil.clickByObj("Back","com.android.chrome:id/close_button",0);
        mUtil.clickByObj("","com.motorola.watch:id/health_profile",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile");
        mUtil.clickByObj("","com.motorola.watch:id/gender",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_Gender");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/height",0);
        uiDevice.findObject(By.res("com.motorola.watch:id/tab_layout")).
                findObjects(By.clazz("android.widget.TextView")).get(0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_Height");
        uiDevice.findObject(By.res("com.motorola.watch:id/tab_layout")).
                findObjects(By.clazz("android.widget.TextView")).get(1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_Height_Right");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/weight",0);
        uiDevice.findObject(By.res("com.motorola.watch:id/tab_layout")).
                findObjects(By.clazz("android.widget.TextView")).get(0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_Weight");
        uiDevice.findObject(By.res("com.motorola.watch:id/tab_layout")).
                findObjects(By.clazz("android.widget.TextView")).get(1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_Weight_Right");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/birth",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_DateOfBirth");
        mUtil.pressBack(1);

        mUtil.clickByObj("Back","android.widget.ImageButton",0);

        mUtil.clickByObj("Daily activity goals","com.motorola.watch:id/daily_goal",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_DailyActivityGoals");
        mUtil.findObjFromListByResViaClazz("Steps", "com.motorola.watch:id/nav_host_fragment",
                "androidx.appcompat.widget.LinearLayoutCompat",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_DailyActivityGoalsSteps");
        mUtil.pressBack(1);

        mUtil.findObjFromListByResViaClazz("Calorie", "com.motorola.watch:id/nav_host_fragment",
                "androidx.appcompat.widget.LinearLayoutCompat",1).click();;
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_DailyActivityGoalsCalorie");
        mUtil.pressBack(1);

        mUtil.findObjFromListByResViaClazz("Activity duration", "com.motorola.watch:id/nav_host_fragment",
                "androidx.appcompat.widget.LinearLayoutCompat",2).click();;
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthProfile_DailyActivityActivityDuration");
        mUtil.pressBack(1);
        mUtil.clickByObj("Back","android.widget.ImageButton",0);

        mUtil.clickByObj("Sleep goal","com.motorola.watch:id/sleep_goal_item",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_SleepDurationGoal");
        mUtil.pressBack(1);
        mUtil.clickByText("Wind-down reminder","com.motorola.watch","wind_down_reminder_setting_title");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_WindDownReminder");
        mUtil.pressBack(1);
        mUtil.clickByText("Run training","com.motorola.watch","run_training");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining");
//        mUtil.clickByText("Beginner Run-Walk","com.motorola.watch","beginner_run_walk");
        mUtil.clickByObj("","com.motorola.watch:id/tv_name",0);

        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_BeginnerRunWalk_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_BeginnerRunWalk_Scrolling2");





        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Training reminder","com.motorola.watch","training_reminder");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_BeginnerRunWalk_TrainingReminder");
        mUtil.clickByObj("","android.widget.ImageButton",0);
        mUtil.pressBack(1);
        mUtil.clickByObj("Change cours","com.motorola.watch:id/btn_change",0);

        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_Preset_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_Preset_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTraining_Preset_Scrolling3");
        mUtil.getListView().scrollForward(600);


        mUtil.pressBack(1);
        mUtil.pressBack(1);
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Health Connect","com.motorola.watch","my_health_connect_title");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_HealthConnect_Off");
        mUtil.pressBack(1);
        mUtil.clickByText("Units","com.motorola.watch","unit_settings_title");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Units");
        mUtil.clickByObj("Height","android:id/title",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Units_Height");
        mUtil.pressBack(1);
        mUtil.clickByObj("weight","android:id/title",1);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Units_Weight");
        mUtil.pressBack(1);
        mUtil.clickByObj("Temperature","android:id/title",2);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Units_Temperature");
        mUtil.pressBack(1);
        mUtil.clickByObj("Distance","android:id/title",3);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Units_Distance");
        mUtil.pressBack(1);
        mUtil.pressBack(1);
        mUtil.clickByText("Moto Watch notifications","com.motorola.watch","label_request_notification_permission");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_MotoWatchNotifications_Off");
        mUtil.pressBack(1);
        mUtil.clickByObj("Sync data to cloud","com.motorola.watch:id/sync_data_item",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_SyncDataToCloud");
        mUtil.enabledCheckBtn("com.motorola.watch:id/switchStatus",0);
        mUtil.clickByText("Erase data on cloud","com.motorola.watch","erase_data_title");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_EraseCloudData");
        mUtil.clickByObj("","com.motorola.watch:id/go_btn",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_EraseCloudData_Dialog");
        mUtil.clickByObj("Erase","com.motorola.watch:id/bt_done",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_EraseCloudData_Toast");
        mUtil.sleep(2000);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_EraseCloudData_Successfully");
        mUtil.clickByObj("Got it","com.motorola.watch:id/bt_done",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Settings_EraseCloudDataFailed");
        mUtil.pressBack(1);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_SyncDataToCloud_On");
        mUtil.disabledCheckBtn("com.motorola.watch:id/switchStatus",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_SyncDataToCloud_Off_Dialog");
        mUtil.clickByObj("","com.motorola.watch:id/set_btn",0);
        mUtil.pressBack(1);
        mUtil.sleep(2000);
        mUtil.getListView().scrollToEnd(50);
//        mUtil.clickByObj("Moto Watch notifications","com.motorola.watch:id/notification_item",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Settings_MotoWatchNotifications");
//        mUtil.pressBack(1);
        mUtil.clickByObj("About App","com.motorola.watch:id/about_item",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_AboutApp");
        mUtil.pressBack(1);

        mUtil.clickByObj("","com.motorola.watch:id/send_feedback_item",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_SendFeedback");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/b2g_item",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_ReportIssue");
        mUtil.pressBack(1);
        mUtil.getListView().scrollToBeginning(50);

        mUtil.takeAvikScreenshot("MotoHealth_Settings_Main_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_Main_Scrolling2");
    }
    @Before
    public void setup() throws Exception{
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

            captureOfHealthMain();

    }
}
