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
public class HealthWatchAddWatchSteps5Once {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
mUtil.isExistResuorceId("com.motorola.watch:id/next_btn");
        mAvikDevice.takeAvikScreenshot("MotoHealth_Devices_AddDevice_Step5");
        mUtil.clickByObj("","com.motorola.watch:id/next_btn",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_AddDevice_Step5OptimizeYourSleep");
        mUtil.clickByObj("","com.motorola.watch:id/next_btn",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_AddDevice_Step5PersonalizeYouWatchFace");
        mUtil.clickByObj("","com.motorola.watch:id/last_btn",0);
        mUtil.sleep(2000);
        mUtil.clickByObj("","com.motorola.watch:id/last_btn",0);
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
