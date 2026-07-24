package avik.motohealth.auto;

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
public class HealthAnotherMeasureToastOnce {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        mUtil.waitFor10S(5,"=====Remove notification");
        mUtil.clickByObj("","com.motorola.watch:id/tv_max_heart_rate",0);
        mUtil.clickByObj("","com.motorola.watch:id/btn_measure",0);
        mUtil.pressBack(1);
        mUtil.clickByText("label_blood_oxygen","com.motorola.watch","label_blood_oxygen");
        uiDevice.findObject(By.res("com.motorola.watch:id/btn_measure")).click();
        mUtil.takeAvikScreenshot("MotoHealth_HeartRate_AnotherMeasure_Toast");
        mUtil.pressBack(1);
    }

    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");

        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }
    @After
    public void tearDown() throws InterruptedException {

        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {

            captureOfHealth();

    }
}
