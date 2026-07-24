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
public class HealthMainChartNew {
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
        mUtil.findObjFromListByResViaClazz("Workout records",
                "com.motorola.watch:id/recycler_view","android.view.ViewGroup",0).click();
//        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecords");
        mUtil.findObjFromListByResViaClazz("Day","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",0).click();
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_WorkoutRecordDetails_Scrolling2");
        mUtil.clickByText("Lower body strength","com.motorola.watch","lower_body_strength");
        mUtil.takeAvikScreenshot("MotoHealth_Health_LowerBodyStrength_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_LowerBodyStrength_Scrolling2");
        mUtil.pressBack(1);
        mUtil.clickByText("Flexibility training","com.motorola.watch","flexibility_training");
        mUtil.takeAvikScreenshot("MotoHealth_Health_FlexibilityTraining_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_FlexibilityTraining_Scrolling2");
        mUtil.pressBack(1);
        mUtil.clickByText("Upper limb training","com.motorola.watch","upper_limb_training");
        mUtil.takeAvikScreenshot("MotoHealth_Health_UpperLimbTraining_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_UpperLimbTraining_Scrolling2");
        mUtil.pressBack(1);
        mUtil.pressBack(1);
//        mUtil.clickByText("jump_rope","com.motorola.watch","jump_rope");
//        mUtil.clickByText("","com.motorola.watch","3ql8mR9XsJjDP7u86SoOia");

        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Stamina","com.motorola.watch","stamina");
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_Scrolling2");
        mUtil.clickByObj("","com.motorola.watch:id/training_status",0);
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_Information");
        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_stamina_arrow")).get(0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationStamina");
        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_stamina_arrow")).get(0).click();
        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_running_arrow")).get(0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationRunningIndex_Scrolling1");
        mUtil.getListView().flingForward();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationRunningIndex_Scrolling2");
//        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_running_arrow")).get(0).click();
        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_load_arrow")).get(0).click();
        mUtil.getListView().flingForward();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationTrainingLoad");
//        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_load_arrow")).get(0).click();
        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_vo_arrow")).get(0).click();
        mUtil.getListView().flingForward();
        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationVO2Max");
//        mUtil.getListView().flingForward();
//        mUtil.takeAvikScreenshot("MotoHealth_Health_Stamina_InformationVO2Max_Scrolling2");
//        uiDevice.findObjects(By.res("com.motorola.watch:id/iv_vo_arrow")).get(0).click();

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
