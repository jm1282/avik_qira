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
public class HealthWatchAddWatchSteps1Once {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        mUtil.isExistResuorceId("com.motorola.watch:id/set_up_button");
        mUtil.sleep(5000);
        mAvikDevice.takeAvikScreenshot("MotoHealth_Devices_AddDevice_Step1");
        mUtil.clickByObj("","com.motorola.watch:id/set_up_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_DoMoreWithMotoAccount");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/exit_set_up_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Devices_ExitSetup_Dialog");
        mUtil.clickByObj("","com.motorola.watch:id/btn_decline",0);


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
