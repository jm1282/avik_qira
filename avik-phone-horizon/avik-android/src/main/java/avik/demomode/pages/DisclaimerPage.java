package avik.demomode.pages;

import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;

public class DisclaimerPage extends BasePage {
	public DisclaimerPage(UiDevice device) throws Exception {
		super(device);
	}

	public void captureDisclaimers(AvikHandler handler, String modelName, int scrollNumber) throws Exception {
		String screenName = String.format("DemoModeExperiences_%s_Disclaimers_Scrolling", modelName);
		mCommonMethods.nextExperience(-150);
		mUtils.sleep(Constants.ONE_SECOND);
		for (int screenNumber = 1; screenNumber <= scrollNumber; screenNumber++) {
			handler.takeScreenshot(screenName + screenNumber, true, true);
			mCommonMethods.nextExperience(-350);
			mUtils.sleep(Constants.HALF_SECOND);
		}
	}
}
