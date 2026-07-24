package avik.demomode.utils;

import android.os.Build;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.libraries.homescreen.HomeScreenV;
import com.motorola.frevoutils.code.utils.Constants;

import java.util.regex.Pattern;

import avik.demomode.pages.BasePage;
import avik.demomode.pages.HomePage;
import avik.demomode.pages.SizzlePage;

public class DemoModeFlutter extends BaseLibrary {
    public final static String FLUTTER_APP_PACKAGE = "com.motorola.demo.flutter";
    public final static String CORE_APP_PACKAGE = "com.motorola.demo";
    public final static String APP_ACTIVITY = "com.motorola.demo.flutter/com.motorola.demo" +
            ".flutter.MainActivity";

    public DemoModeFlutter() {
        super();
        mDevice.pressHome();
    }

    public void fixExecutionLanguage() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new CommonMethods().fixExecutionLanguage();
        }
    }

    /**
     * Flutter team has implemented a new logic do decide if ROW or EMEA strings should be shown,
     * and opening the app using the "mUtils.startActivity()" function will always open the app
     * displaying ROW Strings. This should only be used if you want to force all strings on the app
     * to show their ROW Version
     */
    @Deprecated
    public HomePage callApp() throws Exception {
        mUtils.startActivity(APP_ACTIVITY);
        sleep(Constants.FIVE_SECONDS);
        return new HomePage(mDevice);
    }

    public <T extends BasePage> T callAppFromHomeScreen(boolean hasSizzle) throws Exception {
        mDevice.pressHome();
        sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/bt_ksp_widget"))),
                Constants.THREE_SECONDS).click();
        sleep(Constants.FIVE_SECONDS);
        if (hasSizzle) return (T) new SizzlePage(mDevice);
        return (T) new HomePage(mDevice);
    }

    public <T extends BasePage> T callAppFromAppTray(boolean hasSizzle) throws Exception {
        String demomodeAppName = mUtils.getResourceByPackAndStringKey(CORE_APP_PACKAGE, "demo_mode_reseller");
        new HomeScreenV().launchAnApplicationByAppTray(demomodeAppName);
        sleep(Constants.ONE_SECOND);
        return (T) new HomePage(mDevice);
    }

    public void forceCloseApp() throws Exception {
        mUtils.forceCloseApp(mUtils.getApplicationName(FLUTTER_APP_PACKAGE), FLUTTER_APP_PACKAGE);
    }

    public void clearApp() throws Exception {
        mUtils.clearApp(mUtils.getApplicationName(FLUTTER_APP_PACKAGE), FLUTTER_APP_PACKAGE);
    }
}