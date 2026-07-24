package avik.demomodelight.pages;

import android.widget.Button;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import java.util.List;
import java.util.Locale;

public class TechSpecsPage extends BasePage{

    private final int DISCLAIMER_EXPAND_BUTTON_POS = 1;

    public TechSpecsPage(UiDevice device) throws Exception {
        super(device);
    }

    public void captureSpecs(AvikHandler handler, String modelName, int scrollNumber) throws Exception {
        String screenName = String.format("DemoModeExperiencesLight_%s_TechSpecs_Scrolling", modelName);
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

    public DisclaimersPage openDisclaimers() throws Exception {
        mUtils.createScrollable().scrollToEnd(10);
        List<UiObject2> btnObjects = mDevice.wait(Until.findObjects(By.clazz(Button.class.getName())),Constants.THREE_SECONDS);
        UiObject2 disclaimerExpandBtn = btnObjects.get(DISCLAIMER_EXPAND_BUTTON_POS);
        disclaimerExpandBtn.click();

        mUtils.sleep(Constants.ONE_SECOND);

        return new DisclaimersPage(mDevice);
    }
}
