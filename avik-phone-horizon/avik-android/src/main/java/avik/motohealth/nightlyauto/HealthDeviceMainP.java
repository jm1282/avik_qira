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
public class HealthDeviceMainP {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        mUtil.openMotoWatch();
      mUtil.takeAvikScreenshotToast("MotoHealth_DevicesP_Watch");
        mUtil.clickByObj("","com.motorola.watch:id/iv_custom",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_MyDevices");
        mUtil.clickByObj("","com.motorola.watch:id/add_device",0);
        mUtil.takeAvikScreenshot("MotoHealth_AddWatchP_Searching_Toast");
        mUtil.pressBack(1);
        mUtil.clickByObj("","com.motorola.watch:id/remove",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_MyDevices_RemoveDevice");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/bt_cancel",0);
        mUtil.pressBack(1);
//        captureDevicePanel();
        mUtil.clickByObj("Panels","com.motorola.watch:id/panels",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Panel_None");
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Panel_Scrolling1");
        mUtil.waitFor10S(6,"Manual to scroll to send page");
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Panel_Scrolling2");
        mUtil.pressBack(1);
//        captureDeviceApps();
        mUtil.clickByObj("Apps","com.motorola.watch:id/app_screen",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Apps_Scrolling1");
        mUtil.getListView().scrollForward(600);

        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Apps_Scrolling2");
        mUtil.getListView().scrollForward(600);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_Apps_Scrolling3");

        mUtil.pressBack(1);
//        captureDeviceQuickSettings();
        mUtil.clickByObj("Quick Settings","com.motorola.watch:id/quick_settings",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_QuickSettings__Scrolling1");
//        mUtil.waitFor10S(10,"Manual to drap 4 items");
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_QuickSettings__Scrolling2");
//        mUtil.waitFor10S(10,"Manual to drap 4 items");
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_QuickSettings__Scrolling3");
//        mUtil.waitFor10S(20,"Manual to remove them");
        mUtil.pressBack(1);
//        captureDeviceClockFaces();
        mUtil.clickByObj("Clock Faces","com.motorola.watch:id/album_title",0);
        mUtil.findObjFromListByResViaClazz("My faces","com.motorola.watch:id/tab_faces","android.widget.LinearLayout",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_ClockFaceMyFaces");
        mUtil.clickByObj("","com.motorola.watch:id/mcl_scale",1);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_ClockFaceMyFaces_SetAsCurrent");
//        mUtil.clickByObj("","com.motorola.watch:id/siv_face",7);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_ClockFaceMyFaces_Customize");
        mUtil.sleep(1000);
//        mUtil.clickByObj("","com.motorola.watch:id/tv_set_as_current",0);
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_ClockFaceMyFaces_CustomizePage");
//        mUtil.pressBack(1);
        mUtil.findObjFromListByResViaClazz("More Faces","com.motorola.watch:id/tab_faces","android.widget.TextView",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_ClockFaceMoreFaces");
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
        mUtil.takeAvikScreenshot("MotoHealth_DevicesP_WatchSettings_ExerciseAudioFeedback");
        captureOfHealth();
    }
}
