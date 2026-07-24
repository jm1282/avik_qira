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
public class HealthSettingsRunTrainingCustom {
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
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Moto Watch notifications","com.motorola.watch","label_request_notification_permission");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_MotoWatchNotifications");
        mUtil.pressBack(1);
        mUtil.getListView().scrollToBeginning(100);
        mUtil.clickByText("Run training","com.motorola.watch","run_training");
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingCustomAvik");
        mUtil.clickByObj("Change course","com.motorola.watch:id/btn_change",0);
        mUtil.clickByText("Custom","com.motorola.watch","custom");
        mUtil.clickByObj("rv_custom_training","com.motorola.watch:id/rv_custom_training",0);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingCustomAvik_Scrolling1");
        mUtil.getListView().scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Settings_RunTrainingCustomAvik_Scrolling2");
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
