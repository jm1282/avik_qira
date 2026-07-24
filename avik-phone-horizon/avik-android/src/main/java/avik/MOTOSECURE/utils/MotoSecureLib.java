package avik.MOTOSECURE.utils;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;


public class MotoSecureLib {

    private AvikUtility mUtils = AvikUtility.getInstance();
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private AvikUtility mUtility;

    public String PACKAGE_NAME = "com.motorola.securityhub";
    public String SECURECORE_PACKAGE = "com.motorola.motosecurecore";
    public String CORESETTINGSEXT_PACKAGE = "com.motorola.coresettingsext";

    public String APP_NAME;

    private final String MENU_DESC = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "navigation_drawer_menu_description");

    public MotoSecureLib() throws Exception {
        APP_NAME = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "app_name");
        mUtility = AvikUtility.getInstance();

    }

    public void clearApp() throws Exception {
        mUtils.runShellCommand(String.format("pm clear %s", PACKAGE_NAME));
        mUtils.runShellCommand(String.format("pm clear %s", CORESETTINGSEXT_PACKAGE));
        mUtils.runShellCommand(String.format("pm clear %s", SECURECORE_PACKAGE));
    }

    public void callApp() throws Exception {
        mUtils.runShellCommand("am start com.motorola.securityhub/com.motorola.securityhub.ui.view.MainActivity");

    }

    public void forceCloseApp() throws Exception {
        mUtils.runShellCommand(String.format("am force-stop %s", PACKAGE_NAME));
        mUtils.runShellCommand(String.format("am force-stop com.motorola.motosecurecore"));

    }

    public void closePopup() throws Exception {
        String closeIcon = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "onboarding_modal_icon_close_description");
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.descContains(closeIcon)).click();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void closeProtips() throws Exception {
        String closeContDesc = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "onboarding_modal_icon_description");
        mDevice.findObjects(By.desc(closeContDesc)).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.desc(closeContDesc)).get(1).click();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void openMenu() throws Exception {
        mUtils.sleep(Constants.ONE_SECOND);
        //String menu = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "navigation_drawer_menu_description");
        mDevice.findObject(By.descContains(MENU_DESC)).click();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void skipOnboard() throws Exception {
        BySelector content = By.res("android:id/content");
        String getStarted = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "btn_get_started");
        String startBtn = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "btn_start");
        BySelector allowSelector = By.res("com.android.permissioncontroller:id/permission_allow_button");
        String later = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "btn_maybe_later");

        mDevice.wait(Until.findObject(By.text(getStarted)), Constants.TEN_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(content).getChildren().get(0).getChildren().get(0).getChildren().get(6).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(content).getChildren().get(0).getChildren().get(0).getChildren().get(6).click();
        mUtils.sleep(Constants.THREE_SECONDS);
        mDevice.findObject(By.text(startBtn)).click();
//        mDevice.wait(Until.findObject(allowSelector), Constants.FIVE_SECONDS).click();
        mDevice.wait(Until.findObject(By.text(later)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
    }

    public void openAutoLock() throws Exception{
        //Auto lock
        String autoLock = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "main_screen_tip_title_auto_lock");
        mDevice.wait(Until.findObject(By.text(autoLock)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
    }

    public void openAppLock() throws Exception{
        //App lock
        String appLock = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "app_lock");
        mDevice.wait(Until.findObject(By.text(appLock)), Constants.FIVE_SECONDS).click();
        mUtils.sleep(Constants.TWO_SECONDS);
    }

}
