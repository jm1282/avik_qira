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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 2
 *		1. add PIN/Pattern/Password;
 *		2. Reach fingerprint screen;
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_Fingerprint {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private Setup mSetup;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private UiDevice mDevice;

    //2?3 Ladin

    private AvikScreenshotAction Setup_Fingerprint = new AndroidAvikScreenshotAction("Setup_Fingerprint", true);
    private AvikScreenshotAction Setup_Fingerprint_Skip_Dialog = new AndroidAvikScreenshotAction("Setup_Fingerprint_Skip_Dialog", true);

    private AvikScreenshotAction Setup_Fingerprint_AlmostThere = new AndroidAvikScreenshotAction("Setup_Fingerprint_AlmostThere", true);

    @Before
    public void setUp() throws Exception {
        mSetup = new Setup();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
        mSetup = new Setup();
    }
    
    private void captureFinalScreens() throws Exception{
    	mUtils.sleep(Constants.TEN_SECONDS);
    	mUtility.takeAvikScreenshotWithFlag(Setup_Fingerprint);
    	mDevice.findObjects(By.clazz("android.widget.Button")).get(1).click();
    	mUtils.sleep(Constants.TWO_SECONDS);
    	mDevice.findObject(By.clazz("android.widget.Button")).click();
    	mUtils.sleep(Constants.TWO_SECONDS);
    	mDevice.findObject(By.clazz("android.widget.Button")).click();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mUtility.takeAvikScreenshotWithFlag(Setup_Fingerprint_Skip_Dialog);
    	mDevice.pressBack();
    	mUtils.sleep(Constants.ONE_SECOND);
    	mDevice.pressBack();
    }

    private void captureAlmostThere() throws Exception {
        mUtils.sleep(Constants.FIVE_SECONDS);
        //mDevice.click(353,364);
        mUtility.takeAvikScreenshotWithFlag(Setup_Fingerprint_AlmostThere);
    }
    
    @Test
    public void testMain() {
        try {
            captureFinalScreens();
            //captureAlmostThere();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
