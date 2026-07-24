package avik.demomodelight.pages;

import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

public class DisclaimersPage extends BasePage{
    public DisclaimersPage(UiDevice device) throws Exception {
        super(device);
    }
    public void captureDisclaimers(AvikHandler handler, String modelName, int scrollNumber) throws Exception {
        String screenName = String.format("DemoModeExperiencesLight_%s_Disclaimers_Scrolling", modelName);
        int ySize = mDevice.getDisplayHeight();
        int xSize = mDevice.getDisplayWidth();

        mUtils.sleep(Constants.ONE_SECOND);
        for (int screenNumber = 1; screenNumber <= scrollNumber; screenNumber++) {
            handler.takeScreenshot(screenName + screenNumber, true, true);
            mDevice.swipe(xSize / 2, ySize - 500, xSize / 2, 450, 150);
            mUtils.sleep(Constants.HALF_SECOND);
        }
    }
}
