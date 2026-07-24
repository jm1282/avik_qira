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
public class HealthMainNoData {
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
        mUtil.findObjFromListByResViaClazz("Health","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Main");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Main2");
        mUtil.findObjFromListByResViaClazz("Workout records",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Day");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Year");
        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords_Calendar");
        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.findObjFromListByResViaClazz("Heart rate",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Day");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Year");
        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_HeartRate_Calendar");
        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.findObjFromListByResViaClazz("Sleep",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Day");
        mUtil.findObjFromListByResViaClazz("Week","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Week");
        mUtil.findObjFromListByResViaClazz("Month","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Month");
        mUtil.findObjFromListByResViaClazz("Year","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Year");
        mUtil.clickByObj("Calendar","com.motorola.watch:id/calendar",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Sleep_Calendar");
        mUtil.clickByObj("Back","com.motorola.watch:id/base_title_bar_back",0);
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.findObjFromListByResViaClazz("Stress",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",3).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stress");
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);
        mUtil.findObjFromListByResViaClazz("Blood Oxygen",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",4).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_BloodOxygen");
        mUtil.clickByObj("BACK","android.widget.ImageButton",0);


//        mUtil.clickByObj("Settings","com.motorola.watch:id/settings",0);
//        mUtil.takeAvikScreenshot("MotoHealth_Devices_Settings_Main1");
//        mUtil.findObj("Health & fitness control","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",1).click();
//        mUtil.findObj("Display","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",3).click();
//        mUtil.findObj("Notifications","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",4).click();
//        mUtil.findObj("Vibration","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",5).click();
//        mUtil.findObj("Weather","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",6).click();
//        mUtil.findObj("Preference","com.motorola.watch:id/recycler_view",
//                "androidx.appcompat.widget.LinearLayoutCompat",7).click();

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
