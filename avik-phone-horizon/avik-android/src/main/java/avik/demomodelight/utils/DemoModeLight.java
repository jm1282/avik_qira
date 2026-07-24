package avik.demomodelight.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.utils.Constants;

import avik.demomode.utils.CommonMethods;
import avik.demomodelight.pages.MainPage;

public class DemoModeLight extends BaseLibrary {
    private final String APP_PACKAGE = "com.motorola.demo";
    private final String APP_ACTIVITY = "com.motorola.demo/.light.ui.LightActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void fixExecutionLanguage() throws Exception {
        new CommonMethods().fixExecutionLanguage();
    }

    public MainPage callApp() throws Exception{
        mUtils.startActivity(APP_ACTIVITY);
        sleep(Constants.TWO_SECONDS);
        return new MainPage(mDevice);
    }

    public void forceCloseApp() throws Exception {
        mUtils.forceCloseApp(mUtils.getApplicationName(APP_PACKAGE), APP_PACKAGE);
    }

    public void clearApp() throws Exception {
        mUtils.clearApp(mUtils.getApplicationName(APP_PACKAGE), APP_PACKAGE);
    }
}
