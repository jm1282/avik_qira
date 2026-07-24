package avik.SETUP.PRCAuto;


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
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 8 Execution time: ~1m 30s
 *
 * Initial Setup:
 * 1. Just FDR device
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_ActivationScreens_PRC {
    
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private UiDevice mDevice;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private Setup mSetup;

    private AvikScreenshotAction Setup_Start;
    private AvikScreenshotAction Setup_Activation;
    private AvikScreenshotAction Setup_Activation_Toast;
    private AvikScreenshotAction Setup_Activation_Failed_Dialog;
    
    private AvikScreenshotAction Setup_EmergencyInformationButton;
    private AvikScreenshotAction Setup_EmergencyInformationTapAgain;
    private AvikScreenshotAction Setup_EmergencyInformation;
    private AvikScreenshotAction Setup_VisionSettings;
    
    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mSetup = new Setup();
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
        
        Setup_Start = new AndroidAvikScreenshotAction("Setup_Start", false);
        Setup_Activation = new AndroidAvikScreenshotAction("Setup_Activation", false);
        Setup_Activation_Toast = new AndroidAvikScreenshotAction("Setup_Activation_Toast", false);
        Setup_Activation_Failed_Dialog = new AndroidAvikScreenshotAction("Setup_Activation_Failed_Dialog", false);
        
        Setup_EmergencyInformationButton = new AndroidAvikScreenshotAction("Setup_EmergencyInformationButton", false);
        Setup_EmergencyInformationTapAgain = new AndroidAvikScreenshotAction("Setup_EmergencyInformationTapAgain", false);
        Setup_EmergencyInformation = new AndroidAvikScreenshotAction("Setup_EmergencyInformation", false);
        Setup_VisionSettings = new AndroidAvikScreenshotAction("Setup_VisionSettings", true);
          
    }

    private void capturePRCActivation() throws Exception {
    	logger.info("-----CAPTURING SCREENS-----");
    	
    	mUtility.takeAvikScreenshotWithFlag(Setup_Start);
        mUtils.sleep(Constants.HALF_SECOND);
        
        mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/welcome_emergency_dial")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyInformationButton);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.findObject(By.res("com.android.phone:id/emergency_info_view")).click();
        mUtils.sleep(Constants.HALF_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyInformationTapAgain);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.findObject(By.res("com.android.phone:id/emergency_info_view")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_EmergencyInformation);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mUtils.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/welcome_accessibility")).click();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(Setup_VisionSettings);
        mUtils.sleep(Constants.HALF_SECOND);

        mDevice.pressBack();
        mUtils.sleep(Constants.ONE_SECOND);
        
        mDevice.findObject(By.res("com.motorola.cn.setupwizard:id/start")).click();
        mUtils.sleep(Constants.FIVE_SECONDS);
        UiScrollable networksList = mUtils.createScrollable();
        networksList.scrollToEnd(5);
        mUtils.sleep(Constants.HALF_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Activation);
        mUtils.sleep(Constants.HALF_SECOND);
        
        //mDevice.findObject(By.res("com.motorola.cn.prcactivation:id/sim_card1")).click();
        mUtils.sleep(Constants.ONE_SECOND);
        mUtility.takeAvikScreenshotWithFlag(Setup_Activation_Toast);
        //mDevice.wait(Until.hasObject(By.res("android:id/button1")), Constants.TEN_SECONDS * 2);
        // Only doable with a SIM with no data or blocked signal
        mUtility.takeAvikScreenshotWithFlag(Setup_Activation_Failed_Dialog);
        
    }
    
    private void captureManual() throws Exception {
    	mUtility.takeAvikScreenshotWithFlag(Setup_Activation);
    	mUtility.takeAvikScreenshotWithFlag(Setup_Activation_Failed_Dialog);
    }
    
    @Test
    public void testMain() {
        try {
        	mDevice.pressBack();
        	mUtils.sleep(Constants.ONE_SECOND);
        	mDevice.pressBack();
        	mUtils.sleep(Constants.ONE_SECOND);
        	capturePRCActivation();
        	//captureManual();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
