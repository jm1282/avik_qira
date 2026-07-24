package avik.MOTOSECURE;

import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.MOTOSECURE.utils.MotoSecureLib;


/**
 * Screen Number:  Execution Time: .
 *
 * <PRE>
 *     Add a phishing url to allowed list (ex. http://ww3w.g00gle.com.ru)
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure_Phishing_Added {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    AndroidAvikScreenshotAction MotoSecure_Phishing_RemoveFromList = new AndroidAvikScreenshotAction("MotoSecure_Phishing_RemoveFromList", true);

    @Before
    public void setUp() throws Exception{
        mSecure = new MotoSecureLib();
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        mUtility.pressBackKeySeveralTimes(3);
        mDevice.pressHome();
    }

    public void testMain() throws Exception {
        String phishing = mUtility.getResourceByPackAndStringKey(mSecure.PACKAGE_NAME, "security_feature_phishing_detection");

        mSecure.callApp();
        mDevice.findObjects(By.clazz(Button.class)).get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.createScrollable().scrollTextIntoView(phishing);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(phishing)).click();
        mDevice.wait(Until.findObject(By.res("com.motorola.coresettingsext:id/iconSelected")), Constants.FIVE_SECONDS).click();

        BySelector checkbox = By.clazz(CheckBox.class);

        mDevice.wait(Until.findObject(checkbox), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.ONE_SECOND);


        mDevice.findObject(checkbox).getParent().getParent().getChildren().get(0).getChildren().get(1).click();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Phishing_RemoveFromList);

        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObjects(By.clazz(Button.class)).get(1).click();



    }


    @Test
    public void main() throws Exception {
        try {
            testMain();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw e;
        }

    }
}
