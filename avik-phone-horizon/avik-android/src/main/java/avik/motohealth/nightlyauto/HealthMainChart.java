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
public class HealthMainChart {
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
        mUtil.takeAvikScreenshot("MotoHealth_Health_Main");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Main2");
        mUtil.getListView().scrollToBeginning(100);
        mUtil.waitFor10S(5,"===============================");
        mUtil.clickByObj("chart","com.motorola.watch:id/click_area",0);
        mUtil.clickByObj("calendar","com.motorola.watch:id/calendar",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_ChartCalendar");
        mUtil.pressBack(1);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Scrolling3");

        mUtil.getListView().scrollToBeginning(50);
        mUtil.waitFor10S(5,"===============================");
        mUtil.clickByObj("calorie","com.motorola.watch:id/calorie_layout",0);
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout",
                "android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Day");
        mUtil.waitFor10S(5,"===== Manual to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_ActiveTime");
        mUtil.waitFor10S(3,"===== Disappear to active time");
        mUtil.clickByObj("","com.motorola.watch:id/edit",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_EditCalories");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Chart_Year");
        mUtil.pressBack(1);
        mUtil.getListView().scrollToEnd(50);
        mUtil.waitFor10S(5,"===============================");
        mUtil.clickByObj("step","com.motorola.watch:id/step_layout",0);
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout",
                "android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_Day");
        mUtil.waitFor10S(5,"===== Manual to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_ActiveTime");
        mUtil.waitFor10S(3,"===== Disappear to active time");
        mUtil.clickByObj("","com.motorola.watch:id/edit",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_EditCalories");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Step_Year");
        mUtil.pressBack(1);
        mUtil.waitFor10S(5,"===============================");
        mUtil.clickByObj("exercise","com.motorola.watch:id/exercise_time_layout",0);
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout",
                "android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_Day");
        mUtil.waitFor10S(5,"===== Manual to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_ActiveTime");
        mUtil.waitFor10S(3,"===== Disappear to active time");
        mUtil.clickByObj("","com.motorola.watch:id/edit",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_EditCalories");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/cancel_btn",0);
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Exercise_Year");
        mUtil.pressBack(1);
        mUtil.pressBack(1);
        mUtil.findObjFromListByResViaClazz("Workout records",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",0).click();
//        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Day");
        mUtil.clickByText("jump_rope","com.motorola.watch","jump_rope");
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_DetailJump_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_DetailJump_Scrolling2");

        mUtil.pressBack(1);
        mUtil.clickByText("indoor_walking","com.motorola.watch","indoor_walking");
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_DetailIndoorWalking_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_DetailIndoorWalking_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_DetailIndoorWalking_Scrolling3");
        mUtil.pressBack(1);
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Year");
//        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Calendar");
//        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.findObjFromListByResViaClazz("Heart rate",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",1).click();
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Day");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Day_Scrolling2");
        mUtil.waitFor10S(5,"===== Manual to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_ActiveTime");
        mUtil.waitFor10S(3,"===== Disappear to active time");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Year");
//        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Calendar");
//        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.clickByObj("sleep","com.motorola.watch:id/sleep_length",0);
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout",
                "android.widget.LinearLayout",0).click();
        mUtil.clickByObj("","com.motorola.watch:id/edit",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_SleepGoal");
        mUtil.pressBack(1);
//        mUtil.clickByObj("","com.motorola.watch:id/cancel_btn",0);

        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Day_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Day_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Day_Scrolling3");



        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();

        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Week_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Week_Scrolling2");


        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();

        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Year_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Year_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Year_Scrolling3");




//        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Calendar");
//        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
//        mUtil.clickByObj("Stress","com.motorola.watch:id/average",0);
        mUtil.clickByText("label_stress","com.motorola.watch","label_stress");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.waitFor10S(5,"===== Manual to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_ActiveTime");
        mUtil.waitFor10S(3,"===== Disappear to active time");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Day_Scrolling1");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Day_Scrolling2");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Year");
//        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress_Calendar");
//        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
//        mUtil.clickByObj("Blood Oxygen","com.motorola.watch:id/tv_value",0);
        mUtil.clickByText("label_blood_oxygen","com.motorola.watch","label_blood_oxygen");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen_Day");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen_Year");
//        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen_Calendar");
//        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
    }
    public void captureOfHealth() throws Exception{

    }
    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");
        mUtil.pressBack(5);
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
