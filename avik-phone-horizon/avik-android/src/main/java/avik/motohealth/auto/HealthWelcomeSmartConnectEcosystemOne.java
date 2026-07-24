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
public class HealthWelcomeSmartConnectEcosystemOne {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        while (!uiDevice.hasObject(By.res("com.motorola.watch:id/btn_confirm"))) {
          mUtil.waitFor10S(10,"Manual to show the smart connect page.");
        }
        mUtil.waitFor10S(5,"waiting for the notification disappear.");
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_SmartConnectEcosystem");
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
