package avik.demomode.pages;

import android.graphics.Point;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.libraries.camera.CameraT;
import com.motorola.frevoutils.code.utils.Constants;

import java.util.regex.Pattern;

import javax.net.ssl.CertPathTrustManagerParameters;

import avik.demomode.utils.DemoModeFlutter;

public class CameraPage extends BasePage {
    private final BySelector filterSelector =
            By.res(Pattern.compile(".*:id/toggle_bar_button"));
    public CameraPage(UiDevice device) throws Exception {
        super(device);
    }

    public CameraT openTestCamera() throws Exception {
        mDevice.wait(Until.findObject(By.clazz(HorizontalScrollView.class.getName())),Constants.THREE_SECONDS)
                .findObject(By.clazz(Button.class.getName()))
                .clickAndWait(Until.newWindow(), Constants.FIVE_SECONDS);
        mUtils.sleep(Constants.ONE_SECOND);
        return new CameraT();
    }
    public CameraPage clickGoBackOverlay() throws Exception {
        if (mObjectUtils.isCurrentLocaleRTL()) {
            //TODO Fix this
            mUtils.sleep(Constants.THREE_SECONDS);
            mDevice.click(97, 1488);
        } else {
            Point filterButtonPosition = mDevice.
                    wait(Until.findObject(filterSelector), Constants.TEN_SECONDS).getVisibleCenter();
            Point goBackButtonPosition =
                    new Point(filterButtonPosition.x, filterButtonPosition.y - 250);
            mDevice.click(goBackButtonPosition.x, goBackButtonPosition.y);
        }
        mUtils.sleep(Constants.THREE_SECONDS);
        new DemoModeFlutter().callApp(); // without this line the avik handler will fail to realize we are on the Demo Mode App and crash the avik client, do not remove
        return new CameraPage(mDevice);
    }
}
