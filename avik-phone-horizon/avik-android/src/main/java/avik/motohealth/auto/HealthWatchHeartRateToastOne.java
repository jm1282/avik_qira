package avik.motohealth.auto;

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
public class HealthWatchHeartRateToastOne {
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
        mAvikDevice.takeAvikScreenshot("MotoHealth_HeartRate_Measure_Toast",false,false);
        mUtil.waitFor10S(10,"waiting for dialog");
        mUtil.takeAvikScreenshot("MotoHealth_HeartRate_MeasureFailed_Dialog");
        mUtil.clickByObj("","com.motorola.watch:id/bt_done",0);
        mUtil.clickByObj("","com.motorola.watch:id/btn_measure",0);
        mUtil.pressBack(2);
    mUtil.sleep(2000);
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
