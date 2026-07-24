package avik.motohealth.auto.polar;

import static android.os.SystemClock.sleep;

import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.motohealth.utils.MotoWatch;
import avik.motohealth.utils.MotoWatchConstants;

/**
 * Moto Health execution for Moto Watch R3
 * This one requires no Data, only a watch that needs to be connected
 *
 * <p>
 * PRE-STEPS:
 * <ul>
 * <li> Launch the Moto Watch app and accept all permissions</p>
 * <li> Close and Open the app twice, then move from each tab, go to Health, Watch & My Page</p>
 * <li> Reopen the app and on the "Health" tab choose to "Set up Health Connect" & "Sync data"</p>
 * <li> Make sure that you're using a account without exercise data</p>
 * </ul>
 *
 */

@RunWith(AndroidJUnit4.class)
public class HealthWatchPolarWatchNoData {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger mLogger = AvikLoggerFactory.INSTANCE.getInstance();
    public AvikUiDevice mAvikDevice = AvikUiDevice.getInstance();
    public AvikUtility mUtils = AvikUtility.getInstance();
    public ObjectUtils mObjectUtils = new ObjectUtils();

    public UiDevice mDevice;
    public MotoWatch mMotoWatch;

    @Before
    public void setup() throws Exception{
        mLogger.info("=== Locale: " + mAvikDevice.getLocale());
        mLogger.info("=== Start of Execution ===");
        mUtils.runShellCommand("pm revoke com.motorola.watch android.permission.READ_CONTACTS");
        sleep(Constants.FIVE_SECONDS);
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mMotoWatch = new MotoWatch();
    }

    @After
    public void tearDown() throws Exception {
        mUtils.pressBackKeySeveralTimes(4);
        mUtils.runShellCommand("pm revoke com.motorola.watch android.permission.READ_CONTACTS");
        mLogger.info("=== End Of Execution ===");
    }

    public void captureWatch() throws Exception {
        mMotoWatch.launchApp();
        sleep(Constants.FIVE_SECONDS);

        mMotoWatch.openPanels();
        sleep(Constants.ONE_SECOND);
        UiScrollable mScroll = mUtils.createScrollable();
        avikHandler.scrollAndTakeScreenshot("MotoHealth_DevicesP_Panel", mScroll, 2, 150);
        mDevice.pressBack();
        mMotoWatch.openWatchSettings();
        sleep(Constants.ONE_SECOND);
        mScroll = mUtils.createScrollable();
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Devices_Settings", mScroll, 2, 150);
        mScroll.scrollToBeginning(2);

        String wristPreference = mUtils.getResourceByStringOnCurrentAppPack("setting_preference_title");
        mScroll.scrollTextIntoView(wristPreference);
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(wristPreference)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        mScroll = mUtils.createScrollable();
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Devices_Settings_Preference", mScroll,2,150);
        sleep(Constants.ONE_SECOND);
        String doublePress = mUtils.getResourceByStringOnCurrentAppPack("double_press_settings_for_pasha");
        mDevice.wait(Until.findObject(By.text(doublePress)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Preference_DoublePress");
        mUtils.pressBackKeySeveralTimes(2);

        String phone = mUtils.getResourceByStringOnCurrentAppPack("setting_phone_title");
        mUtils.createScrollable().scrollTextIntoView(phone);
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.text(phone)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone");
        mDevice.wait(Until.findObject(By.clazz("androidx.appcompat.widget.LinearLayoutCompat")),Constants.ONE_SECOND).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_FavoriteContacts_Empty");
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/favorite_contacts_add_btn")),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_FavoriteContacts_Permission_Dialog");
        mDevice.pressBack();
        mUtils.runShellCommand("pm grant com.motorola.watch android.permission.READ_CONTACTS");
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/favorite_contacts_add_btn")),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_FavoriteContacts_SelectContacts");
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/contact_checkbox")),Constants.TWO_SECONDS).click();
        sleep(Constants.HALF_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/favorite_contacts_add_btn")),Constants.TWO_SECONDS).click();
        sleep(Constants.TWO_SECONDS);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_FavoriteContacts");
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/ic_delete")),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_FavoriteContacts_Delete");
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/dialog_confirm_button")),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        mDevice.pressBack();
        String quickReply = mUtils.getResourceByStringOnCurrentAppPack("quick_reply_title");
        mDevice.wait(Until.findObject(By.text(quickReply)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_QuickReply_Empty");
        mDevice.wait(Until.findObject(By.clazz(Switch.class.getName())),Constants.ONE_SECOND).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Phone_QuickReply_Permission_Dialog");
        mUtils.pressBackKeySeveralTimes(3);
        sleep(Constants.ONE_SECOND);
        mUtils.createScrollable().scrollToBeginning(3);
        sleep(Constants.ONE_SECOND);
        String healthAndFitness = mUtils.getResourceByStringOnCurrentAppPack("health_fitness");
        mDevice.wait(Until.findObject(By.text(healthAndFitness).res("android:id/title")),Constants.TWO_SECONDS).click();
        sleep(Constants.THREE_SECONDS);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Health");
        String exercise = mUtils.getResourceByStringOnCurrentAppPack("exercise_setting_title");
        mDevice.wait(Until.findObject(By.text(exercise)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_HealthExercise");
        mDevice.pressBack();
        String bloodOxygen = mUtils.getResourceByStringOnCurrentAppPack("spo_setting_title");
        mDevice.wait(Until.findObject(By.text(bloodOxygen)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.clazz(RadioButton.class.getName())).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Devices_Settings_Health_BloodOxygen",2,150);
        sleep(Constants.ONE_SECOND);
        String lowBloodAlert = mUtils.getResourceByStringOnCurrentAppPack("spo_low_alert_setting_title");
        mDevice.findObject(By.text(lowBloodAlert)).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Health_BloodOxygen_LowBlood_Dialog");
        mUtils.pressBackKeySeveralTimes(2);
        sleep(Constants.ONE_SECOND);
        String healthReminders = mUtils.getResourceByStringOnCurrentAppPack("health_reminders_setting_title");
        mDevice.findObject(By.text(healthReminders)).click();
        sleep(Constants.ONE_SECOND);
        List<UiObject2> switchesToTouch = mDevice.wait(Until.findObjects(By.clazz(Switch.class.getName()).checked(false)),Constants.FIVE_SECONDS);
        if(switchesToTouch != null) {
            for (UiObject2 s : switchesToTouch) {
                s.click();
                sleep(Constants.ONE_SECOND);
            }
        }
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Health_Reminders");
        mDevice.findObject(By.res("com.motorola.watch:id/drink_water")).click();
        sleep(Constants.ONE_SECOND);
        mScroll = new UiScrollable(new UiSelector().className(ScrollView.class));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Devices_Settings_Health_Reminders_DrinkWater",mScroll, 2,150);
        mDevice.pressBack();
        sleep(Constants.THREE_SECONDS);
        mDevice.findObject(By.res("com.motorola.watch:id/wash_hand")).click();
        sleep(Constants.ONE_SECOND);
        mScroll = new UiScrollable(new UiSelector().className(ScrollView.class));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Devices_Settings_Health_Reminders_WashHands",mScroll, 2,150);
        mDevice.pressBack();
        sleep(Constants.THREE_SECONDS);
        mDevice.findObject(By.res("com.motorola.watch:id/medicine")).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Health_Reminders_TakeMedicine");
        mDevice.findObject(By.res("com.motorola.watch:id/interval")).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Devices_Settings_Health_Reminders_TakeMedicine_Interval_Dialog");
    }

    @Test
    public void testMain() throws Exception {
        try {
            captureWatch();
        } catch (Exception e){
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}