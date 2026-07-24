package avik.demomode.pages;

import android.widget.Button;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;


public class HomePage extends BasePage {

    public HomePage(UiDevice device) throws Exception {
        super(device);
    }

    public <T extends BasePage> T openExperiences(Class<T> pageType) throws Exception {
        mDevice.wait(Until.findObject(By.clazz(Button.class.getName())), Constants.TWO_SECONDS)
                .clickAndWait(Until.newWindow(), Constants.TWO_SECONDS);
        mUtils.sleep(Constants.ONE_SECOND);
        return pageType.getDeclaredConstructor(UiDevice.class).newInstance(mDevice);
    }

    public SpecsPage openSpecs() throws Exception {
        mDevice.wait(Until.findObjects(By.clazz(Button.class.getName())), Constants.TWO_SECONDS)
                .get(1).clickAndWait(Until.newWindow(), Constants.TWO_SECONDS);

        mUtils.sleep(Constants.TWO_SECONDS);
        return new SpecsPage(mDevice);
    }

}