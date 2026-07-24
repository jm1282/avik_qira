package avik.SETUP.auto;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 1
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_YoureAllSet {

	@Rule
	public final AvikHandler avikHandler = AvikHandler.getInstance();
	public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

	private Setup mSetup;
	private AvikUtility mUtility;
	private ObjectUtils mUtils;
	private UiDevice mDevice;

    // 2 ladin
    private AvikScreenshotAction Setup_LetsExplore = new AndroidAvikScreenshotAction("Setup_LetsExplore", true);
    private AvikScreenshotAction Setup_Motorola_ContinueSetup = new AndroidAvikScreenshotAction("Setup_Motorola_ContinueSetup", true);

	@Before
	public void setUp() throws Exception {
		mSetup = new Setup();
		mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		mUtility = AvikUtility.getInstance();
		mUtils = new ObjectUtils();
		mSetup = new Setup();
	}
	
    private void captureFinalScreens() throws Exception{
    	logger.info("------CAPTURING FINAL SCREEN-----");
    	mUtils.sleep(Constants.TWO_SECONDS);

		/*
    	mDevice.executeShellCommand("am start -n com.motorola.setup/com.motorola.setup.SplashDialogActivity");
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_LetsExplore);
    	mDevice.pressBack();
		*/
    	mUtils.sleep(Constants.TWO_SECONDS);
    	mDevice.executeShellCommand("am start -n com.motorola.setup/com.motorola.setup.DeferredWelcome");
    	mUtils.sleep(Constants.FIVE_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_Motorola_ContinueSetup);
    	mDevice.pressBack();
    }

    @Test
    public void testMain() {
        try {
            captureFinalScreens();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
