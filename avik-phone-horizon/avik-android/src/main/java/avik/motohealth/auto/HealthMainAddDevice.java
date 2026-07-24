package avik.motohealth.auto;

import android.os.Build;

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
public class HealthMainAddDevice {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uiDevice.executeShellCommand("am start com.motorola.watch/com.motorola.watch.ui.activity.MainPageActivity");
        }
        mUtil.findObjFromListByResViaClazz("Device","com.motorola.watch:id/tab_layout","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Device_AddDevice");
        mUtil.clickByObj("Add Device","com.motorola.watch:id/ok_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Device_SetupDevice");
        mAvikDevice.findObject(By.res("com.motorola.watch:id/name")).click();
        mAvikDevice.takeAvikScreenshot("MotoHealth_Device_SetupDevice_Searching_Toast");
        mUtil.waitFor10S(2,"==== Loading");
        mUtil.takeAvikScreenshot("MotoHealth_Device_SetupDevice_Searching");
        mAvikDevice.findObject(By.res("com.motorola.watch:id/name")).click();
        mUtil.takeAvikScreenshot("MotoHealth_Device_SetupDevice_Pair_Dialog");
        mAvikDevice.findObject(By.res("android:id/button2")).click();
        mUtil.takeAvikScreenshot("MotoHealth_Device_SetupDevice_Pair_Toast");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_AddDevicePaired_Toast");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_AddDevicePaired");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_MyDevices");
        mUtil.takeAvikScreenshot("MotoHealth_AddWatch_Searching_Toast");
        mUtil.takeAvikScreenshot("MotoHealth_Devices_MyDevices_RemoveDevice");
        mUtil.takeAvikScreenshot("MotoHealth_AddWatch_Searching_Toast");
        mUtil.pressBack(1);
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
        // 包括,添加,配对,删除


            captureOfHealth();

    }
}
