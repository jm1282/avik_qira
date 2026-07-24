package avik.demomode.pages;

import android.widget.Button;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;


public class SizzlePage extends BasePage {

	public SizzlePage(UiDevice device) throws Exception {
		super(device);
	}

	public HomePage skipSizzle() throws Exception {
		mDevice.wait(Until.findObject(By.clazz(Button.class.getName())), Constants.TWO_SECONDS)
				.clickAndWait(Until.newWindow(), Constants.TWO_SECONDS);
		mUtils.sleep(Constants.TWO_SECONDS);

		return new HomePage(mDevice);
	}
}
