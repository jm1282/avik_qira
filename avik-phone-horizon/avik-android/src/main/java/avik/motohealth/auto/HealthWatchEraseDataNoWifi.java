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
public class HealthWatchEraseDataNoWifi {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {

        mUtil.waitFor10S(5,"remove notification");
        mUtil.clickByText("My page","com.motorola.watch","my_page_title");
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByObj("Sync data to cloud","com.motorola.watch:id/sync_data_item",0);
        mUtil.clickByText("Erase data on cloud","com.motorola.watch","erase_data_title");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_EraseCloudDataNoWifi");
        mUtil.clickByObj("","com.motorola.watch:id/go_btn",0);
        mUtil.clickByObj("Erase","com.motorola.watch:id/bt_done",0);
        mUtil.takeAvikScreenshot(" MotoHealth_Settings_EraseCloudDataFailedNoWifi");
        mUtil.clickByObj("Got it","com.motorola.watch:id/bt_done",0);
    }

    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");

        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }
    @After
    public void tearDown() throws InterruptedException {
        mUtil.pressBack(5);
        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {

            captureOfHealth();

    }
}
