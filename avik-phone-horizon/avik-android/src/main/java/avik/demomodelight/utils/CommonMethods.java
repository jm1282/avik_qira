package avik.demomodelight.utils;

import static androidx.test.uiautomator.Until.findObject;

import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.util.AvikProperties;

import java.util.Locale;

import avik.demomodelight.pages.BasePage;

public class CommonMethods extends BaseLibrary {

    private final int KSP_CARD_DEPTH = 9;

    public CommonMethods() {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void fixExecutionLanguage() throws Exception {
        Locale targetLocale = Locale.forLanguageTag(AvikProperties.INSTANCE.getLocaleInDevice());
        mUtils.setLocale(targetLocale);
        AvikLoggerFactory.INSTANCE.getInstance().info("Changing Locale to : " + targetLocale);
        mDevice.pressBack();
        sleep(Constants.THREE_SECONDS);
    }

    public void nextExperienceCard() throws Exception {
        BySelector kspCardSelector = By.clazz(View.class.getName()).depth(KSP_CARD_DEPTH);
        UiObject2 kspCardObject = mDevice.wait(Until.findObject(kspCardSelector),Constants.FIVE_SECONDS);
        kspCardObject.fling(Direction.RIGHT);
        sleep(Constants.ONE_SECOND);
    }

    public void previousExperienceCard() throws Exception {
        BySelector kspCardSelector = By.clazz(View.class.getName()).depth(KSP_CARD_DEPTH);
        UiObject2 kspCardObject = mDevice.wait(Until.findObject(kspCardSelector),Constants.FIVE_SECONDS);
        kspCardObject.fling(Direction.LEFT);
        sleep(Constants.TWO_SECONDS);
    }

    public <T extends BasePage> T goBack() throws Exception {
        mDevice.pressBack();
        sleep(Constants.HALF_SECOND);
        return (T) new BasePage(mDevice);
    }

}
