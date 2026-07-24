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
public class TimerOnPhoneS {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfTimer() throws Exception {
        mUtil.isExistResuorceId("com.motorola.watch:id/iv_no_sequence");
        mUtil.takeAvikScreenshot("MotoHealth_AddTimer_MultiTimers");
        mUtil.clickByObj("","com.motorola.watch:id/btn_timer",0);// Multi-timers page to begin
        uiDevice.findObject(By.res("com.motorola.watch:id/et_name")).setText("avik");
        mUtil.clickByObj("Add timer","com.motorola.watch:id/tv_timer_name",0);
        uiDevice.findObject(By.res("com.motorola.watch:id/et_timer_name")).setText("aaaa");
        mUtil.waitFor10S(10,"Waiting for select the time 2, 2, 2");
        mUtil.takeAvikScreenshot("MotoHealth_AddTimer_MultiTimers_Hours");
        mUtil.clickByObj("Save","com.motorola.watch:id/btn_timer",0);
        mUtil.clickByObj("save","com.motorola.watch:id/bt_done",0);
        mUtil.clickByObj("Edit","com.motorola.watch:id/timer_edit",0);
        mUtil.clickByObj("Delete Checkbox","com.motorola.watch:id/cb_delete",0);
        mUtil.clickByObj("Delete btn","com.motorola.watch:id/btn_timer",0);
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

            captureOfTimer();

    }
}
