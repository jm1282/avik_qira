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
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 4
 * Execution time: ~50s
 *
 * Initial Setup:
 * 1. Insert a SIM Card;
 * 2. Go back to the first screen (language selection);
 * 3. Run the script for all target locales, since this setup is only required once.
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_ConnectWifi{

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    private AvikScreenshotAction Setup_SkipNetwork_Dialog_pureGMS;
    private AvikScreenshotAction Setup_ConnectToWifi_pureGMS;
    private AvikScreenshotAction Setup_SeeAllWifi_pureGMS;
    private AvikScreenshotAction Setup_UseMobileNetwork_Dialog_pureGMS;
    private AvikScreenshotAction Setup_InsertSIMNow_pureGMS;
    private AvikScreenshotAction Setup_PossibleDataCharges_Dialog_pureGMS;

    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
        
        Setup_SkipNetwork_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_SkipNetwork_Dialog_pureGMS", true);
        Setup_ConnectToWifi_pureGMS = new AndroidAvikScreenshotAction("Setup_ConnectToWifi_pureGMS", true);
        Setup_SeeAllWifi_pureGMS = new AndroidAvikScreenshotAction("Setup_SeeAllWifi_pureGMS", true);
        Setup_UseMobileNetwork_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_UseMobileNetwork_Dialog_pureGMS", true);
        Setup_InsertSIMNow_pureGMS = new AndroidAvikScreenshotAction("Setup_InsertSIMNow_pureGMS", true);
        Setup_PossibleDataCharges_Dialog_pureGMS = new AndroidAvikScreenshotAction("Setup_PossibleDataCharges_Dialog_pureGMS", true);

        mSetup.goToWelcomeScreen();

    }

    @After
    public void tearDown() throws Exception {
        mSetup.goToWelcomeScreen();

    }

    private void captureSetupScreens() throws Exception {
	    logger.info("===== Capturing Connect Wifi Screens =====");
	        
	    mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
	    mUtils.sleep(Constants.FIVE_SECONDS);

        mDevice.findObjects(By.clazz("android.widget.Button")).get(2).click();
        mUtils.sleep(Constants.FIVE_SECONDS);

	    mUtils.swipeFromCenterToTop();
	    mUtils.sleep(Constants.HALF_SECOND);
	       
	    mUtility.takeAvikScreenshotWithFlag(Setup_ConnectToWifi_pureGMS);
	    mUtils.sleep(Constants.HALF_SECOND);
	      
	    mDevice.findObject(By.clazz("android.widget.Button")).click();
	    mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_InsertSIMNow_pureGMS);

        mDevice.findObject(By.clazz("android.widget.Button")).click();
        mUtils.sleep(Constants.ONE_SECOND);

	    mUtility.takeAvikScreenshotWithFlag(Setup_SkipNetwork_Dialog_pureGMS);
	    mUtils.sleep(Constants.ONE_SECOND);
      
      	mDevice.findObject(By.res("android:id/button2")).click();
      	mUtils.sleep(Constants.ONE_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
      
      	while(mDevice.hasObject(By.res("android:id/alertTitle"))) {
    	  mUtils.sleep(Constants.ONE_SECOND);
      	}
      
        mDevice.findObject(By.res("com.google.android.setupwizard:id/network_see_all_wifi")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtils.createScrollable().scrollForward();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtility.takeAvikScreenshotWithFlag(Setup_SeeAllWifi_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);

        /*
        mDevice.pressBack();
        mDevice.findObject(By.res("com.google.android.setupwizard:id/network_use_mobile")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_UseMobileNetwork_Dialog_pureGMS);
        mUtils.sleep(Constants.HALF_SECOND);
        */

        mDevice.pressBack();
        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
    }

    @Test
    public void testMain() {
        try {
            //this.captureSetupScreens();
            mUtility.takeAvikScreenshotWithFlag(Setup_PossibleDataCharges_Dialog_pureGMS);

        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
