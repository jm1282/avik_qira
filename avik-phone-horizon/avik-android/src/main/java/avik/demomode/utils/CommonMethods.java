package avik.demomode.utils;

import static androidx.test.uiautomator.Until.findObject;

import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.util.AvikProperties;

import java.util.List;
import java.util.Locale;

import avik.demomode.pages.BasePage;

public class CommonMethods extends BaseLibrary {

    public CommonMethods() {
        super();
    }

    public void fixExecutionLanguage() throws Exception {
        Locale targetLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            targetLocale = Locale.forLanguageTag(AvikProperties.INSTANCE.getLocaleInDevice());
        }
        mUtils.setLocale(targetLocale);
        AvikLoggerFactory.INSTANCE.getInstance().info("Changing Locale to : " + targetLocale);
        mDevice.pressBack();
        sleep(Constants.THREE_SECONDS);
    }

    public void openMenu() throws Exception {
        if (!mDevice.hasObject(By.clazz(ScrollView.class.getName()))) {
            throw new NullPointerException("Menu button not available on current page");
        }
        List<UiObject2> viewButtons = mDevice.
                wait(Until.findObjects(By.clazz(View.class.getName()).clickable(true))
                        , Constants.THREE_SECONDS);
        if (mUtils.isCurrentLocaleRTL())
            viewButtons.get(1).click();
        else
            viewButtons.get(0).click();
    }

    public void clickButton() {
        UiObject2 horizontalScroll = mDevice.wait(findObject(By.clazz(HorizontalScrollView.class.getName())), Constants.FIVE_SECONDS);
        if (horizontalScroll != null) {
            horizontalScroll.findObject(By.clazz(Button.class.getName()))
                    .clickAndWait(Until.newWindow(), Constants.ONE_SECOND);
        } else {
            mDevice.wait(findObject(By.clazz(Button.class.getName())), Constants.TWO_SECONDS)
                    .clickAndWait(Until.newWindow(), Constants.ONE_SECOND);
        }
    }

    public void nextExperienceCard() throws Exception {
        sleep(1500L);
        if (!mUtils.isCurrentLocaleRTL())
            mDevice.swipe((mDevice.getDisplayWidth() / 2) + 100, mDevice.getDisplayHeight() / 2, 50, mDevice.getDisplayHeight() / 2, 50);
        else
            mDevice.swipe((mDevice.getDisplayWidth() / 2) - 100, this.mDevice.getDisplayHeight() / 2, mDevice.getDisplayWidth(), this.mDevice.getDisplayHeight() / 2, 50);
    }

    public void previousExperienceCard() throws Exception {
        sleep(1500L);
        if (!mUtils.isCurrentLocaleRTL())
            mDevice.swipe((mDevice.getDisplayWidth() / 2) - 50, this.mDevice.getDisplayHeight() / 2, mDevice.getDisplayWidth(), this.mDevice.getDisplayHeight() / 2, 25);
        else
            mDevice.swipe((mDevice.getDisplayWidth() / 2) + 50, mDevice.getDisplayHeight() / 2, 50, mDevice.getDisplayHeight() / 2, 25);
    }

    public void nextExperience(int offset) throws Exception {
        Point startPoint =
                new Point(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() - 200);
        Point endPoint =
                new Point(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() / 2);
        mDevice.swipe(startPoint.x, startPoint.y, endPoint.x, endPoint.y + offset, 100);
        sleep(Constants.ONE_SECOND);
    }

    public void nextExperience() throws Exception {
        Point startPoint =
                new Point(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() - 200);
        Point endPoint =
                new Point(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() / 2);
        mDevice.swipe(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 100);
        sleep(Constants.TWO_SECONDS);
    }

    public <T extends BasePage> T goBack() throws Exception {
        mDevice.pressBack();
        sleep(Constants.HALF_SECOND);
        return (T) new BasePage(mDevice);
    }

}