package avik.motohealth.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.ContextFactory;
import com.motorola.g11n.avik.uiautomatoradapter.ResourcesWrapper;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestUtils {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    UiDevice mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    AvikUiDevice avikUiDevice = AvikUiDevice.getInstance();
    int screenshotedCounter = 0;
    ArrayList<String> lTRLocale = new ArrayList<String>();

    public TestUtils() {
    }

    public void writeLog(String comments) {
        AvikLogger.info(comments);
    }

    public void openMotoWatch() throws InterruptedException, IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand("am start com.motorola.watch/com.motorola.watch.ui.activity.MainPageActivity");
        }
        sleep(AvikConstants.SHORTWAIT);
    }
    public void executeCmd(String cmd) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand(cmd);
        }
    }
    public void sleep(long t) throws InterruptedException {
        SystemClock.sleep(t);
    }

    public void clickByPoint(String comments, int x, int y) throws InterruptedException {
        writeLog(comments);
        sleep(1000);
        mUiDevice.click(x, y);
    }

    public void setSettingsMicro() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand("am start com.google.android.permissioncontroller/" +
                    "com.android.permissioncontroller.safetycenter.ui.SafetyCenterActivity");
        }
        getListView().scrollToEnd(100);
        clickByObj("Privacy", "android:id/summary", 5);
        clickByObj("Permission manager", "android:id/title", 0);
        getListView().scrollToEnd(100);
        clickByObj("Microphone", "android:id/summary", 1);
        getListView().scrollToEnd(100);
        clickByObj("FM Radio", "android:id/title", 0);
        clickByObj("Don't allow", "android.widget.RadioButton", 2);
        pressBack(4);
    }

    public UiObject2 findObjFromListByResViaClazz(String comments, String res, String clazz, int index) {
        writeLog(comments);
        try{
            UiObject2 obj2 = null;
            obj2 = mUiDevice.wait(Until.findObject(By.res(res)), 2000);
            obj2 = mUiDevice.findObject(By.res(res));
//            writeLog("==== Obj2 res: " + obj2.getResourceName());
            for (UiObject2 objTemp : obj2.findObjects(By.clazz(clazz))) {
                writeLog("==== ClassName: " + objTemp.getClassName());
            }
            obj2 = obj2.findObjects(By.clazz(clazz)).get(index);
            return obj2;
        } catch (Exception e) {
            writeLog("====== the object is null");
            return null;
        }
    }
    public int getInstanceViaText(String str,String res) throws Exception{

        String targetText = str;
        List<UiObject2> items = mUiDevice.findObjects(By.res(res));
        int instance = -1;
        writeLog("=====total " + items.size() + " " + res);
        for (int i = 0; i < items.size(); i++) {
            UiObject2 item = items.get(i);
            if (targetText.equals(item.getText())) {
                instance = i;
                break;
            }
        }
        writeLog(str + " is " + instance + " of " + res + ".");
        return  instance;
    }

    public UiObject2 findObjByResViaClazzFromBottom(String comments, String res, String clazz, int index) {
        writeLog(comments);
        try{
            UiObject2 obj2 = null;
            sleep(1000);
//            obj2 =  mUiDevice.findObject(new UiSelector().resourceId(res)).waitForExists(2000);
            obj2 = mUiDevice.wait(Until.findObject(By.res(res)), 2000);
            obj2 = mUiDevice.findObject(By.res(res));
            int mIndex = obj2.getChildCount();
//            writeLog("child count total " + mIndex + " Resource Name:" + obj2.getResourceName());
            for (UiObject2 objTemp : obj2.findObjects(By.clazz(clazz))) {
                writeLog("==== ClassName: " + objTemp.getClassName());
            }
            obj2 = obj2.findObjects(By.clazz(clazz)).get(mIndex - index - 1);
            return obj2;
        } catch (Exception e) {
            writeLog("====== the object is null");
            return null;
        }
    }

    public void launchMotoX(String appName) throws Exception {
//        com.motorola.moto/com.motorola.moto.motofive.feature.mainactivity.MainActivityMotoFive
//        mUiDevice.executeShellCommand(
//                "am start com.motorola.moto/com.motorola.moto.motofour.feature.mainactivity.MotoFourActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand(
                    "am start com.motorola.moto/com.motorola.moto.motofive.feature.mainactivity.MainActivityMotoFive");
        }
        waitFor10S(3, " ====== Waiting of the device is translating =======");
//        clickByObj("Menu", "android.widget.ImageButton", 0);
        clickByObj("Menu", "android.widget.Button", 0);
        sleep(1000);
        switch (appName) {
            case "Personalize":
                clickByObj("Personalize", "com.motorola.moto:id/text", 1);
//                writeLog("Personalize: " + getObjBy("","com.motorola.moto:id/text",1).getText());
                break;
            case "Moto Actions":
//                clickByText("actions", "com.motorola.actions", "actions_container_setting_title");
                findObjFromListByResViaClazz("Moto Actions","com.motorola.moto:id/compose_view","android.widget.TextView",3).click();
                break;
            case "Moto Secure":
                findObjFromListByResViaClazz("Moto Secure", "com.motorola.moto:id/compose_view", "android.widget.TextView", 6).click();
//                clickByObj("Secure","com.motorola.moto:id/text",4);
                break;

            case "Tips":
                findObjFromListByResViaClazz("Razr Tips", "com.motorola.moto:id/compose_view", "android.widget.TextView", 5).click();
//                clickByObj("Tips","com.motorola.moto:id/text",4);
                break;
            case "Moto Display":
                findObjFromListByResViaClazz("Display","com.motorola.moto:id/compose_view","android.widget.TextView",7).click();
//                clickByText("Display","com.motorola.motodisplay","sn_container_title");
                break;
            case "Moto Play":
                findObjFromListByResViaClazz("Play", "com.motorola.moto:id/compose_view", "android.widget.TextView", 4).click();
//                clickByObj("Play","com.motorola.moto:id/text",6);
                break;

            default:
                break;

        }
        waitFor10S(2, " ====== Waiting of Moto App launching =======");
    }

    public void waitFor10S(int s, String comment) throws InterruptedException {
//		writeLog("Waiting for " + comment + " " + s + " seconds.");
        for (int i = 0; i < s; i++) {
            Thread.sleep(1000);
            writeLog((s - i) + " S " + comment);
        }
    }

    public void pressBack(int n) throws InterruptedException {
//        writeLog("=== " + n + " times back.");
        for (int i = 0; i < n; i++) {
            mUiDevice.pressBack();
            sleep(1000);
        }
    }

    public void isExistClass(String cName) throws Exception {
        //            Until.hasObject(By.res(""));
//            uiDevice.findObjects(By.res("")).get(0);
        UiObject obj;
        int counter = 0;
        while (counter < 10) {
            if (mUiDevice.wait(Until.hasObject(By.clazz(cName)), AvikConstants.LONGWAIT)) {
                writeLog("=== Object with class id: " + cName + " is found.");
                counter = 10;
            } else {
                writeLog("=== Object with class id: " + cName + " is not found.");
                counter = counter + 1;
            }

        }
    }

    public void isExistResuorceId(String cId) throws Exception {
        UiObject obj;
        int counter = 0;
        while (counter < 10) {
            if (mUiDevice.wait(Until.hasObject(By.res(cId)), AvikConstants.LONGWAIT)) {
                writeLog("=== Object with resource id: " + cId + " is found.");
                counter = 10;
            } else {
                writeLog("=== Object with resource id: " + cId + " is not found.");
                counter = counter + 1;
            }

        }
    }
    public void skipResButton(String res) throws InterruptedException {
        BySelector button1 = By.res(res);
        if ((Boolean)this.mUiDevice.wait(Until.hasObject(button1), AvikConstants.SHORTWAIT)) {
            this.mUiDevice.findObject(button1).click();
            this.sleep(1000L);
        }
        writeLog("====== Skip Resource ID: " + res + " doesn't exist.");
    }

    public void pressLong(String log, String str, int instance) throws UiObjectNotFoundException {
        UiObject obj;
        if (str.contains(":id/")) {
            mUiDevice.findObject(new UiSelector().resourceIdMatches(".*:id/search_box_collapsed|.*:id/search_box_start_search"));
            if (instance == 0) {
                mUiDevice.findObject(new UiSelector().resourceId(str)).waitForExists(2000);
                obj = mUiDevice.findObject(new UiSelector().resourceId(str));
            } else {
                mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance)).waitForExists(2000);
                obj = mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance));
            }
        } else {
            if (instance == 0) {
                mUiDevice.findObject(new UiSelector().className(str)).waitForExists(2000);
                obj = mUiDevice.findObject(new UiSelector().className(str));
            } else {
                mUiDevice.findObject(new UiSelector().className(str).instance(instance)).waitForExists(2000);
                obj = mUiDevice.findObject(new UiSelector().className(str).instance(instance));
            }
        }
        writeLog(log);
        mUiDevice.swipe(obj.getVisibleBounds().centerX(), obj.getVisibleBounds().centerY(),
                obj.getVisibleBounds().centerX(), obj.getVisibleBounds().centerY(), 100);
    }

    public boolean isLTRLocale(String localeName) {
        lTRLocale = new ArrayList<>(Arrays.asList("ar-EG", "iw-IL"));
        if (lTRLocale.contains(localeName)) {
            writeLog("It is a LTR Locale.");
            return true;
        } else {
            writeLog("It is not a LTR locale.");
            return false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void launchScreenLock() throws Exception {
        mUiDevice.executeShellCommand(" am start -a android.settings.SECURITY_SETTINGS");
        clickByObj("Device unlock","android:id/summary",1);
        clickByObj("Screen lock","android:id/summary",0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void launchFaceAndFingerprintUnlock() throws Exception{
        mUiDevice.executeShellCommand(" am start -a android.settings.SECURITY_SETTINGS");
        clickByObj("Device unlock","android:id/summary",1);
        clickByObj("Screen lock","android:id/summary",0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void launchMoreSecurityAndPrivacy() throws Exception{
        mUiDevice.executeShellCommand(" am start -a android.settings.SECURITY_SETTINGS");
        getListView().scrollToEnd(600);
        clickByObj("More security & privacy","android:id/summary",7);
    }

    public void disabledCheckBtn(String resourceIDorClassId, int index) throws Exception {
        isExistResuorceId(resourceIDorClassId);
        if (Until.checked(true).apply(
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index))) {
            if (avikUiDevice.getLocale().equals("ar-EG")) {
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index).swipe(Direction.LEFT, 1.0f);
            } else {
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index).swipe(Direction.RIGHT, 1.0f);
            }
        }
        writeLog("Switch the " + resourceIDorClassId + " " + index + " status to OFF");
    }
    public void enabledCheckBtn(String resourceIDorClassId, int index) throws Exception {
        writeLog("Swith the " + resourceIDorClassId + " " + index + " status to ON");
        isExistResuorceId(resourceIDorClassId);
        sleep(1000);
        if (Until.checked(false).apply(
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index))) {
            if (avikUiDevice.getLocale().equals("ar-EG")) {
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index).swipe(Direction.LEFT, 1.0f);
            } else {
                mUiDevice.findObjects(By.res(resourceIDorClassId)).get(index).swipe(Direction.RIGHT, 1.0f);
            }
        }
    }

    public UiScrollable mGetListView() throws UiObjectNotFoundException {
        String[] classnames = { //
                "Candroid.widget.ListView", //
                "Candroid.widget.ScrollView", //
                "Candroid.widget.ExpandableListView", //
                "Candroid.widget.GridView", //
                "Candroid:id/list", "android.support.v4.view.ViewPager", "Candroid.support.v7.widget.RecyclerView",
                "Ccom.motorola.moto:id/detail_feature_family_list", "Candroid.view.ViewGroup",
                "Candroidx.recyclerview.widget.RecyclerView", "Ccom.android.settings:id/list",
                "Ccom.android.settings:id/recycler_view",
                "Ccom.motorola.moto:id/family_container",
                "Candroidx.compose.ui.platform.ComposeView"

        };
        UiScrollable listView = null;
        for (String className : classnames) {
            String name = className.substring(1);
            if (className.charAt(0) == 'C') {
                listView = new UiScrollable(new UiSelector().className(name));
            } else {
                listView = new UiScrollable(new UiSelector().resourceId(name));
            }
            if (listView.exists() && listView.getChildCount() != 0) {
                break;
            }
        }
        return listView;
    }

    public void openAppInfoByPack(String packageName) {
        Intent i = new Intent();
        i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + packageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getInstrumentation().getContext().startActivity(i);
        SystemClock.sleep(AvikConstants.NORMALWAIT);
    }

//    public void disableAllPermissions(String packageName) throws Exception {
//        final String settingsPkg = "com.android.settings";
//        mUtils.forceCloseApp("Settings", settingsPkg);
//        mUtils.openAppInfoByPack(packageName);
//
//        String permissionsLabelContent = mUtils.getResourceByPackAndStringKey(settingsPkg, "permissions_label");
//        writeLog("permissionsLabelContent: " + permissionsLabelContent);
//        SystemClock.sleep(AvikConstants.NORMALWAIT);
//
//        UiObject permissionsObject = mUtils.createObjectbyText(permissionsLabelContent);
//
//        if (permissionsObject.exists()) {
//            mUtils.createObjectbyText(permissionsLabelContent).click();
//        }
//
//        waitFor10S(15, "===== disable the permissions.");
//        pressBack(3);
//
//		UiObject switchWidget = mUiDevice.findObject(new UiSelector().className("android.widget.Switch").checked(true));
//		UiObject permission_denial_message = mUtils.createObjectbyResourceID("android:id/message");
//
//		while (switchWidget.exists()) {
//
//			switchWidget.click();
//			SystemClock.sleep(AvikConstants.SHORTWAIT);
//
//			if (permission_denial_message.waitForExists(AvikConstants.SMALLWAIT)) {
//				mUtils.skipAndroidButton1();
//			}
//
//			switchWidget = mUiDevice.findObject(new UiSelector().className("android.widget.Switch").checked(true));
//
//		}
//    }

//    public void activeAllPermissions(String packageName) throws Exception {
//
//        final String settingsPkg = "com.android.settings";
//        mUtils.forceCloseApp("Settings", settingsPkg);
//        mUtils.openAppInfoByPack(packageName);
//
//        String permissionsLabelContent = mUtils.getResourceByPackAndStringKey(settingsPkg, "permissions_label");
//        SystemClock.sleep(AvikConstants.NORMALWAIT);
//
//        UiObject permissionsObject = mUtils.createObjectbyText(permissionsLabelContent);
//
//        if (permissionsObject.exists()) {
//            mUtils.createObjectbyText(permissionsLabelContent).click();
//        }
//
//        UiObject switchWidget = mUiDevice
//                .findObject(new UiSelector().className("android.widget.Switch").checked(false));
//        UiObject permission_denial_message = mUtils.createObjectbyResourceID("android:id/message");
//
//        while (switchWidget.exists()) {
//
//            switchWidget.click();
//            SystemClock.sleep(AvikConstants.SHORTWAIT);
//
//            if (permission_denial_message.waitForExists(AvikConstants.SMALLWAIT)) {
//                mUtils.skipAndroidButton1();
//            }
//
//            switchWidget = mUiDevice.findObject(new UiSelector().className("android.widget.Switch").checked(false));
//
//        }
//    }


    public void dragDownNotification() {
        writeLog("======Try to drag notification down");
        mUiDevice.drag(mUiDevice.getDisplayWidth() / 2, 50, mUiDevice.getDisplayWidth() / 2,
                mUiDevice.getDisplayHeight(), 600);

    }

    public void dragUpNotification() {
        writeLog("======Try to drag notification up");
        mUiDevice.drag(mUiDevice.getDisplayWidth() / 2, mUiDevice.getDisplayHeight()-50, mUiDevice.getDisplayWidth() / 2,
                0, 600);
    }

    public void clickByObj(String log, String str, int instance) throws UiObjectNotFoundException, InterruptedException {
        writeLog(log);

        if (str.contains(":id/")) {
            mUiDevice.wait(Until.hasObject(By.res(str)), AvikConstants.LONGWAIT);
//			mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/search_box_collapsed|.*:id/search_box_start_search"));
            if (instance == 0) {
                mUiDevice.findObject(new UiSelector().resourceId(str)).waitForExists(2000);
                mUiDevice.findObject(new UiSelector().resourceId(str)).click();
            } else {
                mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance)).waitForExists(2000);
                mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance)).click();
            }
        } else {
            if (instance == 0) {
                mUiDevice.wait(Until.hasObject(By.clazz(str)), AvikConstants.LONGWAIT);
                mUiDevice.findObject(new UiSelector().className(str)).waitForExists(2000);
                mUiDevice.findObject(new UiSelector().className(str)).click();
            } else {
                mUiDevice.findObject(new UiSelector().className(str).instance(instance)).waitForExists(2000);
                mUiDevice.findObject(new UiSelector().className(str).instance(instance)).click();
            }
        }
        sleep(500);
    }
    public UiObject getObj(String log, String str, int instance) throws UiObjectNotFoundException, InterruptedException {
        writeLog(log);
        if (str.contains(":id/")) {
            mUiDevice.wait(Until.hasObject(By.res(str)), AvikConstants.LONGWAIT);
//			mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/search_box_collapsed|.*:id/search_box_start_search"));
            if (instance == 0) {
                mUiDevice.findObject(new UiSelector().resourceId(str)).waitForExists(2000);
                return mUiDevice.findObject(new UiSelector().resourceId(str));
            } else {
                mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance)).waitForExists(2000);
                return mUiDevice.findObject(new UiSelector().resourceId(str).instance(instance));
            }
        } else {
            if (instance == 0) {
                mUiDevice.wait(Until.hasObject(By.clazz(str)), AvikConstants.LONGWAIT);
                mUiDevice.findObject(new UiSelector().className(str)).waitForExists(2000);
                return mUiDevice.findObject(new UiSelector().className(str));
            } else {
                mUiDevice.findObject(new UiSelector().className(str).instance(instance)).waitForExists(2000);
                return mUiDevice.findObject(new UiSelector().className(str).instance(instance));
            }
        }
    }



    public void clickByText(String log, String pack, String stringkey) throws Exception {
        String note = getResourceByPackAndStringKey(pack, stringkey);
        writeLog("==== " + log + " and " + note + "should be displayed");
        getListView().scrollTextIntoView(note);
        mUiDevice.findObject(By.text(note)).click();
    }

    public UiObject getObjByText(String pack, String stringkey) throws Exception {
        UiObject mObj;
        String note = getResourceByPackAndStringKey(pack, stringkey);
        writeLog("=== " + note + " is displayed.");
        getListView().scrollTextIntoView(note);
        mObj = mUiDevice.findObject(new UiSelector().text(note));
        return mObj;

    }


    public String getResourceByPackAndStringKey(String pack, String stringKey) {
        try{
            Context context = ContextFactory.getInstance().getContext(pack);
            Resources resources = context.getResources();
            ResourcesWrapper resourcesWrapper = new ResourcesWrapper(resources, pack);

            return resourcesWrapper.getString(stringKey);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    public void startActivityWithComponent(String packageName, String activity) throws Exception {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activity));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getInstrumentation().getTargetContext().startActivity(intent);
        sleep(Constants.THREE_SECONDS);
    }

    public void takeAvikScreenshot(String screenName) throws InterruptedException, IOException {
        screenshotedCounter = screenshotedCounter + 1;
//        mUiDevice.executeShellCommand("adb shell screencap -d 4630947043778501763 /sdcard/" + screenName + ".png");
//        mUiDevice.executeShellCommand("adb pull /sdcard/" + screenName + ".png" + "d/logcat/" + screenName + ".png");
//        writeLog(screenName);
        sleep(1000);
        avikUiDevice.takeAvikScreenshot(screenName);
        writeLog("====== Total screenshot " + screenshotedCounter + " is captured");

    }

    public void pressOkBtn1() throws Exception {
        mUiDevice.findObject(new UiSelector().resourceId("android:id/button1")).click();
        sleep(500);
    }

    public void pressCancelBtn2() throws Exception {
        mUiDevice.findObject(new UiSelector().resourceId("android:id/button2")).click();
//        mUiDevice.findObject(new UiSelector().className("android.widget.ImageButton").instance(0)).click();
        sleep(1000);
    }

    public void launchBattery() throws Exception {
        avikUiDevice.executeShellCommand("am start com.android.settings");
        clickByText("", "com.android.settings", "power_usage_summary_title");
        sleep(AvikConstants.NORMALWAIT);
    }

    public void launchStorage() throws Exception {
        avikUiDevice.executeShellCommand("am start com.android.settings");
        clickByText("", "com.android.settings", "storage_usb_settings");
        sleep(AvikConstants.NORMALWAIT);
    }

    public void forceCloseApp(String packageName) throws IOException {
        writeLog("=== clear " + packageName + " data");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand(String.format("pm clear %s", packageName));
        }
    }

    public void clearApp(String packageName) throws IOException {
        writeLog("=== " + packageName + "force closed.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUiDevice.executeShellCommand(String.format("am force-stop %s", packageName));
        }
    }


    public void takeAvikScreenshotToast(String screenName) throws Exception {
        avikUiDevice.takeAvikScreenshot(screenName);
    }

    public UiScrollable getListView() throws UiObjectNotFoundException {
        SystemClock.sleep(500);
        UiScrollable listView = mGetListView();
        if (!listView.exists()) {
            System.out.println("Wait 1 second for ListView appear.");
            SystemClock.sleep(500);
            listView = mGetListView();
            if (!listView.exists()) {
                System.out.println("Wait 2 second for ListView appear.");
                SystemClock.sleep(500);
                listView = mGetListView();
            }
        }
        // writeLog("class name " + listView.getClassName() + " scrollable is found");
        return listView;
    }

    public ParcelFileDescriptor runCommand(String activity) {
        ParcelFileDescriptor fileDescriptor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fileDescriptor = InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand(activity);
        }
        SystemClock.sleep(2000);
        return fileDescriptor;
    }


}
