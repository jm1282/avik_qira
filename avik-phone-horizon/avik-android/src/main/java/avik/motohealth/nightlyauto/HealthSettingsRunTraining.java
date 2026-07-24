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
public class HealthSettingsRunTraining {
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
        mUtil.clickByText("Run training","com.motorola.watch","run_training");
        mUtil.clickByObj("Change course","com.motorola.watch:id/btn_change",0);
        mUtil.clickByText("Custom","com.motorola.watch","custom");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingCustom");
        mUtil.clickByObj("","com.motorola.watch:id/tv_timer_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditCourse");
        mUtil.clickByObj("","com.motorola.watch:id/tv_repeat_day",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingTrainingReminder");
        mUtil.pressBack(1);
        mUtil.clickByObj("Add a step","com.motorola.watch:id/tv_timer_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_Scrolling1");
        mUtil.clickByObj("tv_type","com.motorola.watch:id/tv_type",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_WarmUp");
        mUtil.clickByObj("tv_warm_up","com.motorola.watch:id/tv_warm_up",0);
        // time
        mUtil.clickByObj("Goal","com.motorola.watch:id/tv_goal_type",0);
        mUtil.clickByObj("Time","com.motorola.watch:id/tv_time",0);
        mUtil.clickByObj("Goal Value","com.motorola.watch:id/tv_goal_value",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_GoalTime");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/bt_cancel",0);
        // distance
        mUtil.clickByObj("Goal","com.motorola.watch:id/tv_goal_type",0);
        mUtil.clickByObj("Distance","com.motorola.watch:id/tv_distance",0);
        mUtil.clickByObj("Goal Value","com.motorola.watch:id/tv_goal_value",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_GoalDistance");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        // calorie
        mUtil.clickByObj("Goal","com.motorola.watch:id/tv_goal_type",0);
        mUtil.clickByObj("tv_calorie","com.motorola.watch:id/tv_calorie",0);
        mUtil.clickByObj("Goal Value","com.motorola.watch:id/tv_goal_value",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_GoalCalorie");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        // Heart rate
        mUtil.clickByObj("Goal","com.motorola.watch:id/tv_goal_type",0);
        mUtil.clickByObj("tv_heart_rate","com.motorola.watch:id/tv_heart_rate",0);
        mUtil.clickByObj("Goal Value","com.motorola.watch:id/tv_goal_value",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_GoalHeartRate");
        mUtil.clickByText("Percentage","com.motorola.watch","percentage");
        mUtil.takeAvikScreenshotToast("MotoHealth_Settings_RunTrainingEditSteps_GoalHeartRatePercentage");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        //
        mUtil.clickByObj("Goal","com.motorola.watch:id/tv_goal_type",0);
        mUtil.clickByObj("tv_none","com.motorola.watch:id/tv_none",0);
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_Scrolling2");
        //tv_heart_rate_zone
        mUtil.clickByObj("tv_intensity_type","com.motorola.watch:id/tv_intensity_type",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityList");
        mUtil.clickByObj("tv_heart_rate_zone","com.motorola.watch:id/tv_heart_rate_zone",0);
        mUtil.clickByObj("tv_intensity_value","com.motorola.watch:id/tv_intensity_value",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueWarmUp");
        mUtil.waitFor10S(5,"===Manual to Next");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueFatBurning");
        mUtil.waitFor10S(5,"===Manual to Next");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueAerobic");
        mUtil.waitFor10S(5,"===Manual to Next");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueLactate");
        mUtil.waitFor10S(5,"===Manual to Next");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueMaximum");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
//tv_pace_zone
        mUtil.clickByObj("tv_intensity_type","com.motorola.watch:id/tv_intensity_type",0);
        mUtil.clickByObj("tv_pace_zone","com.motorola.watch:id/tv_pace_zone",0);
        mUtil.clickByObj("tv_intensity_value","com.motorola.watch:id/tv_intensity_value",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValuePaceZone");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        //tv_pace
        mUtil.clickByObj("","com.motorola.watch:id/tv_intensity_type",0);
        mUtil.clickByObj("tv_pace","com.motorola.watch:id/tv_pace",0);
        mUtil.clickByObj("tv_intensity_value","com.motorola.watch:id/tv_intensity_value",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValuePace");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
//        tv_cadence
        mUtil.clickByObj("","com.motorola.watch:id/tv_intensity_type",0);
        mUtil.clickByObj("tv_cadence","com.motorola.watch:id/tv_cadence",0);
        mUtil.clickByObj("tv_intensity_value","com.motorola.watch:id/tv_intensity_value",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueCadence");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
//        tv_heart_rate_range
        mUtil.clickByObj("","com.motorola.watch:id/tv_intensity_type",0);
        mUtil.clickByObj("tv_heart_rate_range","com.motorola.watch:id/tv_heart_rate_range",0);
        mUtil.clickByObj("tv_intensity_value","com.motorola.watch:id/tv_intensity_value",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueHeartRateRange");
        mUtil.clickByText("Percentage","com.motorola.watch","percentage");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingEditSteps_IntensityValueHeartRateRangePercentage");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
//        tv_none
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
