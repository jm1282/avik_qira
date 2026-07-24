package avik.SETUP.util;

import android.os.SystemClock;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;


public class Setup {

    private ObjectUtils mUtils;
    private UiDevice mDevice;

    public Setup() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtils = new ObjectUtils();
    }

    public void clickByObjectArea(UiObject2 targetObject, BySelector targetSelector) {

        // Number of rows.
        int horizontalFactor = 25;
        int verticalFactor = 10;

        // Direction: bottom-up;
        for (int y = targetObject.getVisibleBounds().bottom - 5; y >= targetObject.getVisibleBounds().top; y -= verticalFactor) {
            for (int x = targetObject.getVisibleBounds().right; x >= targetObject.getVisibleBounds().left; x -= horizontalFactor) {

                AvikLogger.info(String.format("Clicking on %d, %d.", x, y));
                mDevice.click(x, y);
                mUtils.sleep(Constants.TWO_SECONDS);

                if (mDevice.hasObject(targetSelector)) {
                    AvikLogger.info("Found!");

                    return;
                }

            }
        }

    }

    public void goToWelcomeScreen() {
        while (!mDevice.hasObject(By.res("com.google.android.setupwizard:id/welcome_title"))) {
            mDevice.pressBack();
            mUtils.sleep(Constants.TWO_SECONDS);
        }
    }

}
