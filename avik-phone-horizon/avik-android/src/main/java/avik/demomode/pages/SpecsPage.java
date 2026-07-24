package avik.demomode.pages;

import android.widget.ScrollView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import java.util.List;
import java.util.Locale;

public class SpecsPage extends BasePage {
    public SpecsPage(UiDevice device) throws Exception {
        super(device);
    }

    public void captureSpecs(AvikHandler handler, String modelName, int scrollNumber) throws Exception {
        String screenName = String.format("DemoModeExperiences_%s_TechSpecs_Scrolling", modelName);
        int ySize = mDevice.getDisplayHeight();
        int xSize = mDevice.getDisplayWidth();
        String currentScreenName;
        for (int screenNumber = 1; screenNumber <= scrollNumber; screenNumber++) {
            currentScreenName = String.format(Locale.US, "%s%d", screenName, screenNumber);
            handler.takeScreenshot(currentScreenName, true, true);
            mDevice.swipe(xSize / 2, ySize - 500, xSize / 2, 450, 150);
            mUtils.sleep(Constants.HALF_SECOND);
        }
    }

    public void scrollSpecs() throws Exception {
        mUtils.createScrollable().scrollToEnd(10);
    }

    public DisclaimerPage openDisclaimers() throws Exception {
        mUtils.createScrollable().scrollToEnd(10);
        List<UiObject2> scrollChild =
                mDevice.wait(Until.findObject(By.clazz(ScrollView.class.getName())),
                        Constants.FIVE_SECONDS).getChildren();
        UiObject2 expandDisclaimersBtn = scrollChild.get(scrollChild.size() - 1);
        expandDisclaimersBtn.click();
        mUtils.sleep(Constants.ONE_SECOND);

        return new DisclaimerPage(mDevice);
    }
}