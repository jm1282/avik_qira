package avik.motonote.util;

import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.SystemClock;

import androidx.test.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.avik.uiautomatoradapter.ResourcesWrapper;

import java.io.IOException;
import java.util.List;

public class Util {

    public static final String PRESS_AND_HOLD = "PRESS_AND_HOLD";
    public static final String NO_ACTION = "NO_ACTION";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    private final AvikUiDevice mDevice;
    private final AvikUtility mUtils;
    public BySelector denySel1 = By.res("com.android.permissioncontroller:id/permission_deny_button");
    public BySelector denySel2 = By.res("com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button");
    public BySelector a = By.res("com.android.permissioncontroller:id/permission_allow_always_button");
    public BySelector a1 = By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
    public BySelector a2 = By.res("com.android.permissioncontroller:id/permission_allow_button");
    private UiObject label;
    private ResourcesWrapper resourcesWrapper;

    public Util() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtils = AvikUtility.getInstance();
    }

    public void clickAndWait(String res) {
        clickAndWait(res, AvikConstants.SHORTWAIT);
    }

    public void clickAndWait(String res, int time) {
        mDevice.findObject(By.res(res)).click();
        mUtils.sleep(time);
    }

    public void permissionAllow() {
        UiObject allowObj = mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/permission_allow_always_button|.*:id/permission_allow_foreground_only_button|.*:id/permission_allow_button"));
        if (allowObj.exists()) {
            try {
                allowObj.click();
            } catch (UiObjectNotFoundException e) {
                mUtils.printStackTraceOnLog(e);
            }
        }
    }

    public void permissionDeny() {
        UiObject denyObj = mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/permission_deny_button|.*:id/permission_deny_foreground_only_button|.*:id/permission_deny_and_dont_ask_again_button"));

        if (denyObj.exists()) {
            try {
                denyObj.click();
            } catch (UiObjectNotFoundException e) {
                mUtils.printStackTraceOnLog(e);
            }
        }
    }

    public void selectFromList(int itemPosition, int sizeInOnePage, String keyAction) {
        // System.out.println("Select item located in position: " + position);
        for (int i = 1; i <= sizeInOnePage; i++) {
            mDevice.pressDPadUp();
            SystemClock.sleep(AvikConstants.TINYWAIT);
        }
        for (int j = 1; j < itemPosition; j++) {
            mDevice.pressDPadDown();
            SystemClock.sleep(AvikConstants.TINYWAIT);
        }
        mDevice.pressKeyCode(23);
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public UiObject2 getPermissionObj() {

        if (mDevice.hasObject(a)) {
            return mDevice.findObject(a);
        }
        if (mDevice.hasObject(a1)) {
            return mDevice.findObject(a1);
        }
        if (mDevice.hasObject(a2)) {
            return mDevice.findObject(a2);
        }
        return null;
    }

    public void enableNFC(boolean enableOrDisable) throws UiObjectNotFoundException, RemoteException, InterruptedException, IOException {
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        if (enableOrDisable) {
            uiAutomation.executeShellCommand("svc nfc enable");
            SystemClock.sleep(AvikConstants.SHORTWAIT);
        } else {
            uiAutomation.executeShellCommand("svc nfc disable");
            SystemClock.sleep(AvikConstants.SHORTWAIT);
        }
    }

    public void scrollForwardPage(int time) {
        int drag_x = mDevice.getDisplayWidth();
        int drag_y = mDevice.getDisplayHeight();
        while (time > 0) {
            mDevice.drag(drag_x / 2, drag_y / 2, drag_x / 2, 54, 500);
            time--;
        }
    }

    public void clickOnScreenCenter() {
        int x = this.mDevice.getDisplayWidth() / 2;
        int y = this.mDevice.getDisplayHeight() / 2;
        this.mDevice.click(x, y);
    }

    public void swipeFromCenterToLeft() {
        this.mDevice.swipe(this.mDevice.getDisplayWidth() / 2, this.mDevice.getDisplayHeight() / 2, 0, this.mDevice.getDisplayHeight() / 2, 10);
    }

    public void swipeFromCenterToRight() {
        this.mDevice.swipe(this.mDevice.getDisplayWidth() / 2, this.mDevice.getDisplayHeight() / 2, this.mDevice.getDisplayWidth(), this.mDevice.getDisplayHeight() / 2, 10);
    }

    public void swipeFromCenterToTop() {
        this.mDevice.swipe(this.mDevice.getDisplayWidth() / 2, this.mDevice.getDisplayHeight() / 2, this.mDevice.getDisplayWidth() / 2, 0, 10);
    }

    public void swipeFromCenterToBottom() {
        this.mDevice.swipe(this.mDevice.getDisplayWidth() / 2, this.mDevice.getDisplayHeight() / 2, this.mDevice.getDisplayWidth() / 2, this.mDevice.getDisplayHeight(), 10);
    }

    public void clickAndTakeScreenshotOfToast(String resourceID, String screenName) throws Exception {
        label = mUtils.createObjectByResourceID(resourceID);
        final int X = label.getBounds().centerX();
        final int Y = label.getBounds().centerY();
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(X, Y);
            }
        };
        getToastThread.start();
        mUtils.sleep(300);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void clickAndTakeScreenshotOfToast(String buttonName, String resourceIdName, int instanceNumber, int waitTime, String screenName)
            throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName).instance(instanceNumber));
        final int X = button.getBounds().centerX();
        final int Y = button.getBounds().centerY();
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(X, Y);
            }
        };
        getToastThread.start();
        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void clickAndTakeScreenshotOfToast(String buttonName, String resourceIdName, int instancenmmber, String screenName, int waitTime) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        mUtils.sleep(AvikConstants.SHORTWAIT);
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName).instance(instancenmmber));
        final int X = button.getBounds().centerX();
        final int Y = button.getBounds().centerY();
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(X, Y);
            }
        };
        getToastThread.start();
        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void clickAndTakeScreenshotOfToast(String buttonName, final int locationX, final int locationY, String screenName, int waitTime) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        mUtils.sleep(AvikConstants.SHORTWAIT);
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(locationX, locationY);
            }
        };
        getToastThread.start();
        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void clickAndTakeScreenshotOfToast(String buttonName, int x1, int y1, int waitTime, String screenName) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        final int X = x1;
        final int Y = y1;
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(X, Y);
            }
        };
        getToastThread.start();
        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void swipeUpScreen(int swipeTime) throws Exception {
        int screenSizeX = mDevice.getDisplayWidth() / 2;
        int screenSizeY = mDevice.getDisplayHeight() - 150;

        int screenSize1X = mDevice.getDisplayWidth() / 2;
        int screenSize1Y = mDevice.getDisplayHeight() / 4;
        for (int i = 1; i <= swipeTime; i++) {
            AvikLogger.info("swipe Up Screen: " + i);
            mDevice.swipe(screenSizeX, screenSizeY, screenSize1X, screenSize1Y, 100);
        }
    }

    public void swipeDownScreen(int swipeTime) throws Exception {
        int screenSizeX = mDevice.getDisplayWidth() / 2;
        int screenSizeY = mDevice.getDisplayHeight() / 4;

        int screenSize1X = mDevice.getDisplayWidth() / 2;
        int screenSize1Y = mDevice.getDisplayHeight() - 150;
        for (int i = 1; i <= swipeTime; i++) {
            AvikLogger.info("swipe Down Screen: " + i);
            mDevice.swipe(screenSizeX, screenSizeY, screenSize1X, screenSize1Y, 100);
        }
    }

    public void swipeRightScreen(int swipeTime) throws Exception {
        int screenSizeX = mDevice.getDisplayWidth() / 6;
        int screenSizeY = mDevice.getDisplayHeight() / 2;

        int screenSize1X = mDevice.getDisplayWidth() * 6 / 7;
        int screenSize1Y = mDevice.getDisplayHeight() / 2;
        for (int i = 1; i <= swipeTime; i++) {
            AvikLogger.info("swipe Right Screen: " + i);
            mDevice.swipe(screenSizeX, screenSizeY, screenSize1X, screenSize1Y, 100);
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void swipeLeftScreen(int swipeTime) throws Exception {
        int screenSizeX = mDevice.getDisplayWidth() / 6;
        int screenSizeY = mDevice.getDisplayHeight() / 2;

        int screenSize1X = mDevice.getDisplayWidth() * 6 / 7;
        int screenSize1Y = mDevice.getDisplayHeight() / 2;
        for (int i = 1; i <= swipeTime; i++) {
            AvikLogger.info("swipe Left Screen: " + i);
            mDevice.swipe(screenSize1X, screenSize1Y, screenSizeX, screenSizeY, 100);
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void expandNotification(boolean flag) throws UiObjectNotFoundException, IOException {
        if (flag = true) {
            AvikLogger.info("expand Notification");
            mDevice.executeShellCommand("cmd statusbar expand-notifications");
        } else {
            AvikLogger.info("Close Notification");
            mDevice.executeShellCommand("cmd statusbar collapse");
        }
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void clearNotification() throws UiObjectNotFoundException, IOException {
        mDevice.executeShellCommand("service call notification 1");
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void clickAndTakeScreenshotOfToast(String buttonName, String resourceIdName, String screenName, int waitTime) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        mUtils.sleep(AvikConstants.SHORTWAIT);
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName));
        final int X = button.getBounds().centerX();
        final int Y = button.getBounds().centerY();
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.click(X, Y);
            }
        };
        getToastThread.start();
        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void holdAndTakeScreenshotofToast(String buttonName, String resourceIdName, String screenName, int waitTime) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        mUtils.sleep(AvikConstants.SHORTWAIT);
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName));
        final int X = button.getBounds().centerX();
        final int Y = button.getBounds().centerY();
        Thread getToastThread = new Thread() {
            public void run() {
                mUtils.holdByCoordinate(X, Y, 1000);
            }
        };
        getToastThread.start();
//        mUtils.sleep(waitTime);
        mDevice.takeAvikScreenshot(screenName, false, false);
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void clickAndCapture(String packName, String stringKey, String screenName) throws Exception {
        String stringsName = mUtils.getResourceByPackAndStringKey(packName, stringKey);
        mUtils.getListView().scrollTextIntoView(stringsName);
        UiObject appNameq = mUtils.createObjectByText(stringsName);
        appNameq.click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mDevice.takeAvikScreenshot(screenName);
    }

    public void scrollToViewClickOnResourceByPackAndName(String packName, String stringKey) throws Exception {
        String stringsName = mUtils.getResourceByPackAndStringKey(packName, stringKey);
        mUtils.getListView().scrollTextIntoView(stringsName);
        UiObject itemName = mUtils.createObjectByText(stringsName);
        itemName.click();
        mUtils.sleep(AvikConstants.SHORTWAIT);

    }

    public void londClickOnResourceByResourceIDAndCapture(String resourceIdName, String screenName) throws Exception {
        UiObject button = mUtils.createObjectByResourceID(resourceIdName);
        mUtils.pressLong(button);
        mUtils.sleep(250);
        mDevice.takeAvikScreenshot(screenName);
    }

    public void setTextByResourceId(String text, String resourceIdName) throws Exception {
        AvikLogger.info("Input " + text);
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mUtils.createObjectByResourceID(resourceIdName).setText(text);
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public void clickByResourceId(String buttonName, String resourceIdName, int instance) throws Exception {
        AvikLogger.info("Click " + buttonName + " button");
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName).instance(instance));
        button.click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    public UiObject createObjectByResourceID(String buttonName, String resourceIdName, int instance) throws Exception {
        UiObject button = mDevice.findObject(new UiSelector().resourceId(resourceIdName).instance(instance));
        return button;
    }

    public UiObject createObjectByClassName(String buttonName, String className, int instance) throws Exception {
        UiObject button = mDevice.findObject(new UiSelector().className(className).instance(instance));
        return button;

    }

    public void holdAppIconOnHomeScreen(String appName) throws Exception {
        if (mUtils.createObjectByTextContains(appName).exists()) {
            mUtils.createObjectByTextContains(appName).longClick();
        } else {
            mUtils.holdByCoordinate(405, 100);// camera icon at home screen
        }
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    public void holdAppIconOnHomeScreen() throws Exception {
//        if (mUtils.createObjectByClassName("android.widget.TextView").exists()) {
//            mUtils.createObjectByClassName("android.widget.TextView").longClick();
//        } else {
            mUtils.holdByCoordinate(480, 1650);// APP icon at home screen
//        }
        mUtils.sleep(AvikConstants.NORMALWAIT);
    }

    int dip2px(float dpValue) {
        Context context = InstrumentationRegistry.getTargetContext();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void drawNote(String touchViewResourceID) {
        int TOP_RECT_COUNT = 9;
        int MIN_MARGIN_TOP = dip2px(4);
//        UiObject2 touchView = mDevice.findObject(By.res("com.motorola.genie:id/bordertouchview"));
//        UiObject2 touchView = mDevice.findObject(By.res("com.motorola.stylus:id/above_menu_container"));
        UiObject2 touchView = mDevice.findObject(By.res(touchViewResourceID));
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        AvikLogger.info("device width: " + width);
        AvikLogger.info("device height: " + height);
        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();
        AvikLogger.info("touchView w: " + w);
        AvikLogger.info("touchView h: " + h);
        int length = w / TOP_RECT_COUNT - 1;
        AvikLogger.info("length: " + length);
        int paddingLeft = (w % (length + 1)) / 2;
        AvikLogger.info("paddingLeft: " + paddingLeft);
        int paddingTop = (h % (length + 1)) / 2;
        if (paddingTop < MIN_MARGIN_TOP) {
            paddingTop += length / 2;
        }
        AvikLogger.info("paddingTop: " + paddingTop);
        // int leftCount = (h - 2 * paddingTop) / (length + 1);
        int viewPadingTop = (height - h) / 2;
        int viewPadingLeft = (width - w) / 2;
        AvikLogger.info("viewPadingTop: " + viewPadingTop);
        AvikLogger.info("viewPadingLeft: " + viewPadingLeft);
        int leftX = paddingLeft + length / 2 + viewPadingLeft;
        int rightX = width - viewPadingLeft - paddingLeft - length / 2;
        int topY = paddingTop + length / 2 + viewPadingTop;
        // Here is something confused: the bottomY should be viewPadingTop + h -
        // paddingTop - length / 2
        int bottomY = viewPadingTop + h + length;
        // int bottomY = viewPadingTop + h - paddingTop - length / 2;
        Point topLeft = new Point(leftX + 300, topY + 800);
        Point topRight = new Point(rightX - 300, topY + 800);
        Point bottomLeft = new Point(leftX + 300, bottomY - 800);
        Point bottomRight = new Point(rightX - 300, bottomY - 800);
        AvikLogger.info("touchView top: " + touchView.getVisibleBounds().top);
        AvikLogger.info("touchView bottom: " + touchView.getVisibleBounds().bottom);
        AvikLogger.info("touchView left: " + touchView.getVisibleBounds().left);
        AvikLogger.info("touchView right: " + touchView.getVisibleBounds().right);
        AvikLogger.info("topLeft: " + topLeft);
        AvikLogger.info("  topRight: " + topRight);
        AvikLogger.info("  bottomLeft: " + bottomLeft);
        AvikLogger.info("  bottomRight: " + bottomRight);
// swipe >
        mDevice.swipe(new Point[]{bottomLeft, bottomRight}, 100);
// swipe ^
        mDevice.swipe(new Point[]{bottomRight, topRight}, 100);
// swipe <
        mDevice.swipe(new Point[]{topRight, topLeft}, 100);
// Swipe down
        mDevice.swipe(new Point[]{topLeft, bottomLeft}, 100);

    }

    public void clickLeftOrNo() throws Exception {
        BySelector noSel = By.res("android:id/button2");
        BySelector leftSel = By.res("com.motorola.genie:id/tv_left");
        BySelector tvnoSel = By.res("com.motorola.genie:id/tv_no");
        if (mDevice.hasObject(noSel)) {
            mDevice.findObject(noSel).click();
        } else if (mDevice.hasObject(tvnoSel)) {
            mDevice.findObject(tvnoSel).click();
        } else if (mDevice.hasObject(leftSel)) {
            mDevice.findObject(leftSel).click();
        } else {
            mDevice.pressBack();
        }
    }

    public void clickOkOrYes() throws Exception {
        BySelector yesSel = By.res("android:id/button1");
        BySelector rightSel = By.res("com.motorola.genie:id/tv_right");
        BySelector tvyesSel = By.res("com.motorola.genie:id/tv_yes");
        BySelector uninstallSel = By.res("com.android.packageinstaller:id/uninsall_comfirm_btn");
        if (mDevice.hasObject(yesSel)) {
            mDevice.findObject(yesSel).click();
        } else if (mDevice.hasObject(tvyesSel)) {
            mDevice.findObject(tvyesSel).click();
        } else if (mDevice.hasObject(uninstallSel)) {
            mDevice.findObject(uninstallSel).click();
        } else if (mDevice.hasObject(rightSel)) {
            mDevice.findObject(rightSel).click();
        }
    }

    public void clickCancelOrNo() throws Exception {
        BySelector yesSel = By.res("android:id/button2");
        BySelector rightSel = By.res("com.motorola.genie:id/tv_left");
        BySelector tvyesSel = By.res("com.motorola.genie:id/tv_no");
        BySelector uninstallSel = By.res("com.android.packageinstaller:id/uninsall_comfirm_btn");
        if (mDevice.hasObject(yesSel)) {
            mDevice.findObject(yesSel).click();
        } else if (mDevice.hasObject(tvyesSel)) {
            mDevice.findObject(tvyesSel).click();
        } else if (mDevice.hasObject(uninstallSel)) {
            mDevice.findObject(uninstallSel).click();
        } else {
            mDevice.findObject(rightSel).click();
        }
    }

    public void clickAllowPermission() {
        BySelector allowSel = By.res("com.android.permissioncontroller:id/permission_allow_button");
        BySelector usingSel = By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button");

        if (mDevice.hasObject(allowSel)) {
            mDevice.findObject(allowSel).click();
        } else if (mDevice.hasObject(usingSel)) {
            mDevice.findObject(usingSel).click();
        }
        SystemClock.sleep(3000);
    }

    public void clickDenyPermission() {

        if (mDevice.hasObject(denySel1)) {
            mDevice.findObject(denySel1).click();
        } else if (mDevice.hasObject(denySel2)) {
            mDevice.findObject(denySel2).click();
        }
        SystemClock.sleep(3000);
    }

    public void longClickPointsAndTakeScreenshot(final Point[] ps, String avikscreenshot) {
        Thread getToastThread = new Thread() {
            public void run() {
                mDevice.swipe(ps, 100);
            }
        };
        getToastThread.start();
        SystemClock.sleep(1000);
        mDevice.takeAvikScreenshot(avikscreenshot, false, false);
    }

    public void inputTextByID(String resourceID, String text) {
        UiObject2 firstName = mDevice.findObject(By.res(resourceID));
        firstName.setText("avik_" + text);
        mDevice.waitForIdle(AvikConstants.SHORTWAIT);
    }

    public void clearTextByID(String resourceID) {
        UiObject2 firstName = mDevice.findObject(By.res(resourceID));
        firstName.clear();
        mDevice.waitForIdle(AvikConstants.SHORTWAIT);
    }

    public void callApp(String PACKAGE_NAME) throws IOException {
        mUtils.callAppByIntent(PACKAGE_NAME);
        mDevice.waitForWindowUpdate(PACKAGE_NAME, AvikConstants.SHORTWAIT);
    }

    public void clearApp(String PACKAGE_NAME) throws Exception {
        mDevice.executeShellCommand("pm clear " + PACKAGE_NAME);
    }

    public void enableBluetooth(boolean isenable) throws IOException {
        if (isenable) {
            mDevice.executeShellCommand("am start -a android.bluetooth.adapter.action.REQUEST_ENABLE");
            SystemClock.sleep(AvikConstants.LONGWAIT);
            if (mDevice.hasObject(By.res("android:id/button1"))) {
                mDevice.findObject(By.res("android:id/button1")).click();
            }
            SystemClock.sleep(AvikConstants.NORMALWAIT);
        } else {
            mDevice.executeShellCommand("am start -a android.bluetooth.adapter.action.REQUEST_DISABLE");
            SystemClock.sleep(AvikConstants.LONGWAIT);
            if (mDevice.hasObject(By.res("android:id/button1"))) {
                mDevice.findObject(By.res("android:id/button1")).click();
            }

            SystemClock.sleep(AvikConstants.NORMALWAIT);
        }

    }

    public void selectItemFromListTop(int index) {
        UiObject2 list = mDevice.findObject(By.clazz(android.widget.ListView.class));
        if (list == null & mDevice.hasObject(By.clazz("android.support.v7.widget.RecyclerView"))) {
            list = mDevice.findObject(By.clazz("android.support.v7.widget.RecyclerView"));
        }
        if (list.isScrollable()) {
            list.fling(Direction.UP);
        }
        List<UiObject2> items = list.getChildren();
        if (!items.isEmpty()) {
            items.get(index).click();
        }
        SystemClock.sleep(2000);
    }

    public void forceCloseApp(String APP_NAME, String PACKAGE_NAME) throws Exception {
        mUtils.forceCloseApp(APP_NAME, PACKAGE_NAME);
    }

    public UiObject2 getUiObjectByStringKey(String string_key) throws Exception {
        String text = mUtils.getResourceByStringOnCurrentAppPack(string_key);
        UiObject2 obj = null;
        if (mDevice.hasObject(By.text(text))) {
            obj = mDevice.findObject(By.text(text));
        } else {
            AvikLogger.info(string_key + " obj cannot find");
        }
        return obj;
    }

    public boolean waitForExist(String resourceID) {
        BySelector yesBtnSel = By.res(resourceID);
        boolean isexist = mDevice.wait(Until.hasObject(yesBtnSel), AvikConstants.LONGERWAIT * 3);
        return isexist;
    }

    /**
     * wait until the object exists or wait_time is timeout.
     */

    public boolean waitForExist(String resourceID, long wait_time) {
        BySelector yesBtnSel = By.res(resourceID);
        boolean isexist = mDevice.wait(Until.hasObject(yesBtnSel), wait_time);
        return isexist;
    }

    public void removeAllWidgetsFromHomeScreen() throws Exception {
        System.out.println("Removing all the widgets from home screen...");
        mDevice.pressHome();
        UiObject destObject;
        String destObjectResourceID = null;

        if (mUtils.createObjectByResourceID("com.android.launcher3:id/search_drop_target_bar").waitForExists(AvikConstants.NORMALWAIT)) {
            destObjectResourceID = "com.android.launcher3:id/search_drop_target_bar";
        } else if (mUtils.createObjectByResourceID("com.android.launcher3:id/search_button_container").waitForExists(AvikConstants.NORMALWAIT)) {
            destObjectResourceID = "com.android.launcher3:id/search_button_container";
        } else if (mUtils.createObjectByResourceID("com.motorola.motlauncher:id/qsb_search_bar").waitForExists(AvikConstants.NORMALWAIT)) {
            destObjectResourceID = "com.motorola.motlauncher:id/qsb_search_bar";
        } else if (mUtils.createObjectByResourceID("com.android.launcher:id/search_button_container").waitForExists(AvikConstants.NORMALWAIT)) {
            destObjectResourceID = "com.android.launcher:id/search_button_container";
        } else if (mUtils.createObjectByResourceID("com.google.android.googlequicksearchbox:id/search_plate_container").waitForExists(AvikConstants.NORMALWAIT)) {
            destObjectResourceID = "com.google.android.googlequicksearchbox:id/search_plate_container";
        }

        UiObject widget1 = mDevice.findObject(new UiSelector().className("android.widget.TextView").longClickable(true).textMatches(".*"));
        UiObject widget2 = mDevice.findObject(new UiSelector().className("android.widget.FrameLayout").longClickable(true));
        UiObject widget3 = mDevice.findObject(new UiSelector().className("android.widget.LinearLayout").longClickable(true));
        UiObject widget4 = mDevice.findObject(new UiSelector().className("android.appwidget.AppWidgetHostView").longClickable(true));
        UiObject widget5 = mDevice.findObject(new UiSelector().className("android.widget.GridView").longClickable(true));

        destObject = mUtils.createObjectByResourceID(destObjectResourceID);
        Rect rectDestObject = destObject.getBounds();
        int steps = 70;

        while (widget1.waitForExists(AvikConstants.NORMALWAIT) || widget2.waitForExists(AvikConstants.NORMALWAIT) || widget3.waitForExists(AvikConstants.NORMALWAIT) || widget4.waitForExists(AvikConstants.NORMALWAIT) || widget5.waitForExists(AvikConstants.NORMALWAIT)) {

            if (widget1.waitForExists(AvikConstants.NORMALWAIT)) {
                widget1 = mDevice.findObject(new UiSelector().className("android.widget.TextView").longClickable(true).textMatches(".*"));
                widget1.dragTo(rectDestObject.centerX() - rectDestObject.centerX() / 4, rectDestObject.centerY(), steps);

            }

            if (widget2.waitForExists(AvikConstants.NORMALWAIT)) {
                widget2 = mDevice.findObject(new UiSelector().className("android.widget.FrameLayout").longClickable(true));
                widget2.dragTo(rectDestObject.centerX() - rectDestObject.centerX() / 4, rectDestObject.centerY(), steps);
            }

            if (widget3.waitForExists(AvikConstants.NORMALWAIT)) {
                widget3 = mDevice.findObject(new UiSelector().className("android.widget.LinearLayout").longClickable(true));
                widget3.dragTo(rectDestObject.centerX() - rectDestObject.centerX() / 4, rectDestObject.centerY(), steps);
            }

            if (widget4.waitForExists(AvikConstants.NORMALWAIT)) {
                widget4 = mDevice.findObject(new UiSelector().className("android.appwidget.AppWidgetHostView").longClickable(true));
                widget4.dragTo(rectDestObject.centerX() - rectDestObject.centerX() / 4, rectDestObject.centerY(), steps);
            }

            if (widget5.waitForExists(AvikConstants.NORMALWAIT)) {
                widget5 = mDevice.findObject(new UiSelector().className("android.widget.GridView").longClickable(true));
                widget5.dragTo(rectDestObject.centerX() - rectDestObject.centerX() / 4, rectDestObject.centerY(), steps);
            }
        }

    }

    public void callAppTray() throws Exception {

        System.out.println("=== Launching App Tray === ");
        mDevice.pressHome();
        UiObject appTrayObject = null;

        resourcesWrapper = mUtils.getResources(mDevice.getCurrentPackageName());
        String appTrayTitle = resourcesWrapper.getString("all_apps_button_label");
        appTrayObject = mUtils.createObjectByDescriptionContains(appTrayTitle);
        appTrayObject.clickAndWaitForNewWindow();
        mUtils.sleep(AvikConstants.NORMALWAIT);

        resourcesWrapper = mUtils.getResources(mDevice.getCurrentPackageName());
        if (mUtils.createObjectByText(resourcesWrapper.getString("all_apps_button_label")).waitForExists(AvikConstants.NORMALWAIT)) {
            mUtils.createObjectByText(resourcesWrapper.getString("all_apps_button_label")).click();
        }
    }

    public boolean addAppShortcutToHomeScreen(String appName) throws Exception {

        int homeScreenItemsNumber;
        boolean isItemFound = false;
        UiObject desiredWidgetObject = null;
        int pos_x = mDevice.getDisplayWidth();
        int pos_y = mDevice.getDisplayHeight();

        mDevice.pressHome();
        UiObject homeScreenChild1 = mDevice.findObject(new UiSelector().className("android.view.View").index(0).longClickable(false));
        if (homeScreenChild1.exists()) {
            homeScreenItemsNumber = homeScreenChild1.getChildCount();
        } else {
            homeScreenChild1 = mDevice.findObject(new UiSelector().className("android.view.ViewGroup").index(0).longClickable(false));
            homeScreenItemsNumber = homeScreenChild1.getChildCount();
        }

        System.out.println(String.format("Items at Home Screen: %s", homeScreenItemsNumber));

        callAppTray();

        UiObject searchBoxObject = mUtils.createObjectByResourceID("com.google.android.googlequicksearchbox:id/search_box_proxy");
        if (!searchBoxObject.exists()) {
            searchBoxObject = mUtils.createObjectByResourceID("com.android.launcher3:id/search_box_container");
        }

        if (searchBoxObject.exists()) {
            searchBoxObject.click();
            mUtils.writeNonAsciiText(appName);
            desiredWidgetObject = mDevice.findObject(new UiSelector().className("android.widget.TextView").text(appName));
            if (homeScreenItemsNumber != 16) {
                desiredWidgetObject.dragTo((pos_x / 2), (pos_y / 2), 40);
            } else {
                desiredWidgetObject.dragTo((pos_x), (pos_y / 2), 40);
            }
            isItemFound = true;
            return isItemFound;
        }

        UiScrollable appsListView = new UiScrollable(new UiSelector().resourceId("com.google.android.googlequicksearchbox:id/apps_list_view"));
        if (!appsListView.exists()) {
            appsListView = new UiScrollable(new UiSelector().resourceId("com.android.launcher3:id/apps_list_view"));
        }
        appsListView.setAsVerticalList();
        System.out.println("Scrolling to the beginning ...");
        appsListView.scrollToBeginning(appsListView.getMaxSearchSwipes());

        String message = String.format("Searching by %s to add in the home screen...", appName);
        System.out.println(message);

        for (int i = 1; i <= appsListView.getMaxSearchSwipes(); i++) {
            if (mUtils.createObjectByText(appName).exists()) {
                desiredWidgetObject = mUtils.createObjectByText(appName);
                if (homeScreenItemsNumber != 16) {
                    desiredWidgetObject.dragTo((pos_x / 2), (pos_y / 2), 40);
                } else {
                    desiredWidgetObject.dragTo((pos_x), (pos_y / 2), 40);
                }
                isItemFound = true;
                break;
            }
            System.out.println("Scrolling down ...");
            appsListView.scrollForward();
        }
        return isItemFound;
    }

    public void enableMobileData(boolean isEnabled) throws IOException {
        if (isEnabled) {
            mDevice.executeShellCommand("adb shell svc data enable");
        } else {
            mDevice.executeShellCommand("adb shell svc data disable");
        }
        mDevice.waitForIdle();
    }

    public void enableWiFiConnection(boolean isEnabled) {
        UiObject2 bar = mDevice.findObject(By.res("android:id/statusBarBackground"));
        bar.scroll(Direction.DOWN, 100);
        mUtils.sleep(AvikConstants.LONGERWAIT);
        List<UiObject2> allItems = mDevice.findObject(By.res("com.android.systemui:id/tile_page")).findObjects(By.clazz(android.widget.Button.class));
        if (isEnabled) {
            if (allItems.get(0).isEnabled()) {

            } else {
                allItems.get(0).click();
            }
        } else {
            if (allItems.get(0).isEnabled()) {
                allItems.get(0).click();
            }
        }
    }

    public String getDeviceCarrier() {
        String carrier = null;
        try {
            carrier = mDevice.executeShellCommand("getprop ro.carrier");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carrier.trim();
    }

    public boolean isPRCProduct() {
        return "retcn".equals(getDeviceCarrier());
    }
}
