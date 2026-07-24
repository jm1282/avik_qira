package avik.motohealth.auto;

import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import avik.motohealth.utils.TestUtils;


@RunWith(AndroidJUnit4.class)
public class HealthMainInit {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfHealth() throws Exception {
        mUtil.dragDownNotification();
        mUtil.getObjByText("com.android.systemui","quick_settings_bluetooth_label").click();
        while (!uiDevice.hasObject(By.res("com.android.systemui:id/bluetooth_tile_dialog_title"))) {
            mUtil.waitFor10S(10,"Manual to show the smart connect page.");
        }
        mUtil.disabledCheckBtn("com.android.systemui:id/bluetooth_toggle",0);
        mUtil.clickByObj("Done","com.android.systemui:id/done_button",0);
        mUtil.pressBack(2);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_WelcomeToMotoWatch");
        mUtil.clickByObj("","com.motorola.watch:id/lets_enjoy_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_UserNotice");
        mUtil.clickByObj("","com.motorola.watch:id/mc_selected",0);
        mUtil.clickByObj("","com.motorola.watch:id/mc_selected",1);
        mUtil.clickByObj("","com.motorola.watch:id/mc_selected",2);
        mUtil.clickByObj("","com.motorola.watch:id/mc_selected",3);
        UiScrollable list = new UiScrollable(new UiSelector().resourceId("com.motorola.watch:id/content_layout"));
        list.scrollToEnd(100);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_HelpUsImproveMotoWatch");
        mUtil.clickByObj("Agree & continue","com.motorola.watch:id/btn_agree",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_AppPermissions");
        mUtil.clickByObj("Notifications","com.motorola.watch:id/iv_granted",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_EnableMotoWatchAppPermission");
        mUtil.clickByObj("Settings","com.motorola.watch:id/btn_layout",0);
        try{
            mUtil.clickByText("Moto Watch","com.motorola.watch","app_name");
        }catch (UiObjectNotFoundException e){
            mUtil.writeLog(" Object not found");
            mUtil.clickByObj("Moto Watch in de-DE","android:id/title",9);
        }
        mUtil.enabledCheckBtn("com.android.settings:id/switchWidget",0);
        mUtil.clickByObj("Allow","com.android.settings:id/allow_button",0);
//        com.android.settings:id/deny_button
        mUtil.clickByObj("Back","android.widget.ImageButton",0);
        mUtil.pressBack(1);
        mUtil.clickByObj("Bluetooth","com.motorola.watch:id/iv_granted",1);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_AllowFindConnect");
        mUtil.clickByObj("Allow","com.android.permissioncontroller:id/permission_allow_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_BluetoothIsOff");
        mUtil.clickByObj("Turn on Bluetooth","com.motorola.watch:id/turn_on_button",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_TurnOnBluetooth_Dialog");
        uiDevice.findObject(new UiSelector().resourceId("android:id/button1")).click();
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_TurnOnBluetooth_Toast");
//        mUtil.waitFor10S(3,"loading the bluetooth");
        mUtil.clickByObj("Location","com.motorola.watch:id/iv_granted",2);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_PermissionLocation1_Dialog");
        mUtil.clickByObj("Agree","com.motorola.watch:id/btn_agree",0);
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_PermissionLocation_Dialog");
        mUtil.clickByObj("While using the app","com.android.permissioncontroller:id/permission_allow_foreground_only_button",0);
       mUtil.clickByObj("Allow all the time","com.android.permissioncontroller:id/allow_always_radio_button",0);
        mUtil.clickByObj("Back","android.widget.ImageButton",0);
        mUtil.clickByObj("NEXT","com.motorola.watch:id/next_button",0);
        mUtil.findObjFromListByResViaClazz("Health profile","com.motorola.watch:id/tab",
                "androidx.appcompat.widget.LinearLayoutCompat",0).click();
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_HealthNoAccount");
        mUtil.findObjFromListByResViaClazz("Watch","com.motorola.watch:id/tab",
                "androidx.appcompat.widget.LinearLayoutCompat",1).click();
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_WatchNoAccount");
        mUtil.findObjFromListByResViaClazz("MyPage","com.motorola.watch:id/tab",
                "androidx.appcompat.widget.LinearLayoutCompat",2).click();
        mUtil.takeAvikScreenshot("MotoHealth_Welcome_MyPageNoAccount");
    }

    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");
        mUtil.pressBack(5);
        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uiDevice.executeShellCommand("pm clear com.motorola.watch");
        }
    }
    @After
    public void tearDown() throws InterruptedException, IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uiDevice.executeShellCommand("pm clear com.motorola.watch");
        }
        mUtil.pressBack(5);
        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {
            captureOfHealth();

    }
}
