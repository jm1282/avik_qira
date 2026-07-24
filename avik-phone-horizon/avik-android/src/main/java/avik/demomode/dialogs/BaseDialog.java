package avik.demomode.dialogs;

import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import java.util.Locale;

import avik.demomode.pages.BasePage;

public class BaseDialog extends BaseLibrary {
    private final int SCROLL_VIEW_DEPTH = 11;

    public BaseDialog() {
        super();
    }

    public <T extends BasePage> T closeDialog() throws Exception {
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mDevice.click(500, 200);
        sleep(Constants.TWO_SECONDS);
        return (T) new BasePage(mDevice);
    }

    public void captureScroll(@NonNull AvikHandler handler, String modelName, String ksp) throws Exception {
        String screenName = String.format
                (Locale.US, "DemoModeExperiences_%s_%s_Dialog_Scrolling", modelName, ksp);
        boolean isLanguageEnXM =
                mUtils.getCurrentLocale().equals(new Locale("en", "XM"));

        String firstScreenName = String.format(Locale.US, "%s%d", screenName, 1);
        String secondScreenName = String.format(Locale.US, "%s%d", screenName, 2);

        boolean dialogHasScroll = mDevice.wait(Until.hasObject(
                By.clazz(View.class.getName())
                        .scrollable(true)
                        .depth(SCROLL_VIEW_DEPTH)), Constants.THREE_SECONDS);
        handler.takeScreenshot(firstScreenName, true, true);

        // Verify if it needs to capture the second screenshot and then scrolls + captures it
        if (isLanguageEnXM || dialogHasScroll) {
            try {
                mDevice.wait(Until.findObject(
                                By.clazz(View.class.getName())
                                        .scrollable(true)
                                        .depth(SCROLL_VIEW_DEPTH)), Constants.THREE_SECONDS)
                        .scroll(Direction.DOWN, 80);
            } catch (NullPointerException e) {
                //No scroll Found
            }
            handler.takeScreenshot(secondScreenName, true, true);
        }

    }

    public void scrollDialog() throws Exception {
        BySelector scrollableSelector = By.clazz(ScrollView.class.getName());
        if (mDevice.wait(Until.hasObject(scrollableSelector), Constants.TWO_SECONDS)) {
            mDevice.wait(Until.findObject(scrollableSelector), Constants.ONE_SECOND)
                    .scroll(Direction.DOWN, 80);
        }
        sleep(Constants.ONE_SECOND);
    }
}
