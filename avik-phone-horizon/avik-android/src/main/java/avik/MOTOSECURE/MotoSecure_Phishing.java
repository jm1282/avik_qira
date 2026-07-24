package avik.MOTOSECURE;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

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

/**
 * Screen Number:  Execution Time: .
 *
 * <PRE>
 * Send a SMS to the device containing a phishing link (ex. http://ww3w.g00gle.com.ru)
 * Clean all other sms
 * Make sure Phishing detection is ON inside Moto Secure
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure_Phishing {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private final String PHISHING_LINK = "http://ww3w.g00gle.com.ru";
    private final String MESSAGES_PKG = "com.google.android.apps.messaging";
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    AndroidAvikScreenshotAction MotoSecure_Phishing_RiskyWebsite = new AndroidAvikScreenshotAction("MotoSecure_Phishing_RiskyWebsite", true);
    private AvikUtility mUtility;

    @Before
    public void setUp() throws Exception {
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
        logger.info("Opening Messages app...");
        mUtility.sleep(Constants.TWO_SECONDS);
        openPhishingSms();

        mDevice.findObject(By.text(PHISHING_LINK)).click();
        mUtility.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(MotoSecure_Phishing_RiskyWebsite);
    }

    public void openPhishingSms() throws Exception {
        mUtility.runShellCommand("am start -n com.google.android.apps.messaging/com.google.android.apps.messaging.ui.ConversationListActivity");
        mDevice.findObject(By.res("android:id/list")).getChildren().get(0).click();
        mUtility.sleep(Constants.TWO_SECONDS);
    }

    public void closeMessagesApp() throws Exception {
        mUtils.forceCloseApp(mUtils.getResourceByPackAndStringKey(MESSAGES_PKG, "app_name"), MESSAGES_PKG);
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