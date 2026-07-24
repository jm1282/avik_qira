package avik.demomode.pages;

import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import java.util.List;

public class DesignPage extends BasePage {
    public DesignPage(UiDevice device) throws Exception {
        super(device);
    }

    @Deprecated
    public void captureDeviceColors(AvikHandler handler, String modelName, int colorCount) throws Exception {
        String screenName = String.format("DemoModeExperiences_Design_%s_Color", modelName);

        UiObject2 horizontalScroll = mDevice.wait(Until.findObject(By.clazz(HorizontalScrollView.class.getName())), Constants.TWO_SECONDS);
        List<UiObject2> viewObjects = horizontalScroll
                .getChildren()
                .get(0)
                .findObjects(By.clazz(View.class.getName()));
        for (int i = 0; i < colorCount; i++) {
            viewObjects.get(i).click();
            mUtils.sleep(Constants.ONE_SECOND);
            handler.takeScreenshot(screenName + (i + 1), true, true);
        }
    }
}
