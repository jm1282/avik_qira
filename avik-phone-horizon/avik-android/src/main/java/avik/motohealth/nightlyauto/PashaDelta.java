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
public class PashaDelta {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfPaShaDelta() throws Exception {
        mUtil.openMotoWatch();
        // WatchSettings->Health&fitness->BloodOxygen,default is OFF
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Watch settings","com.motorola.watch","button_setting");
        mUtil.clickByObj("Health & fitness","android:id/title",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness");
        mUtil.clickByText("Blood oxygen","com.motorola.watch","spo_setting_title");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness_BloodOxygen_Scrolling1");
        mUtil.clickByObj("Full day","com.motorola.watch:id/radio_btn",0);
        mUtil.clickByObj("Low blood oxygen alert","com.motorola.watch:id/alert",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness_LowBloodOxygenAlert_Dialog");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        mUtil.clickByObj("Off","com.motorola.watch:id/radio_btn",2);
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness_BloodOxygen_Scrolling2");
        mUtil.pressBack(1);
        mUtil.clickByText("Health reminders","com.motorola.watch","health_reminders_setting_title");
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness_HealthReminders");
        mUtil.clickByObj("Sedentary reminders","com.motorola.watch:id/titleTextView",0);
        mUtil.enabledCheckBtn("com.motorola.watch:id/sedentary_switch",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchSettingsP_HealthFitness_SedentaryReminders");
        mUtil.disabledCheckBtn("com.motorola.watch:id/sedentary_switch",0);
    }
    public void captureHealth()throws Exception{
        mUtil.clickByText("Health","com.motorola.watch","health_title");
    };
    public void capturePanels()throws Exception{
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.clickByObj("Panels","com.motorola.watch:id/panels",0);
    };
    public void captureWatchSettings()throws Exception{

    }
    public void captureQuickSettings() throws Exception{
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.clickByText("Quick Settings","com.motorola.watch","btn_quick_setting");
    }
    public void captureMusic()throws Exception{
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.clickByText("Music","com.motorola.watch","btn_music_manager");
    }
    public void captureApps()throws Exception{
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.clickByText("Apps","com.motorola.watch","btn_apps_screen");
    }
    public void MyPage() throws Exception{
        mUtil.clickByText("My page","com.motorola.watch","my_page_title");


    }

    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");
//        mUtil.pressBack(5);
        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }
    @After
    public void tearDown() throws InterruptedException {
//        mUtil.pressBack(5);
        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {

            captureOfPaShaDelta();

    }
}
