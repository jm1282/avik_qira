package avik.demomodelight.pages;

import static android.os.SystemClock.sleep;

import android.widget.Button;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;

import java.util.List;

public class MainPage extends BasePage{
    private final int SPECS_BUTTON_POS = 1;
    public MainPage(UiDevice device) throws Exception {
        super(device);
    }

    public void nextExperienceCard() throws Exception {
        mCommonMethods.nextExperienceCard();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public TechSpecsPage openSpecs() throws Exception {
        List<UiObject2> buttons = mDevice.wait(Until.findObjects(By.clazz(Button.class.getName())), Constants.FIVE_SECONDS);
        UiObject2 specsButton = buttons.get(SPECS_BUTTON_POS);
        specsButton.click();
        sleep(Constants.TWO_SECONDS);
        return new TechSpecsPage(mDevice);
    }

}
