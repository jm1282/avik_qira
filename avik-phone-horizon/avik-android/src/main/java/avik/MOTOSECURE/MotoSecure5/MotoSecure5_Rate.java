package avik.MOTOSECURE.MotoSecure5;

import android.os.Build;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.libraries.settings.SettingsV;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.MOTOSECURE.utils.MotoSecureLib;

/**
 *
 * <PRE>
 *     Install an old version of app
 *     Open and use the app a little
 *     Install an update
 *     Open again and check if it shows a dialog saying "Do you like this app?"
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class MotoSecure5_Rate {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private MotoSecureLib mSecure;
    private SettingsV mSettings;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility;

    private AvikUiDevice mDevice2;

    AvikScreenshotAction MotoSecure_DoYouLikeThisApp = new AndroidAvikScreenshotAction("MotoSecure_DoYouLikeThisApp", true);



    @Before
    public void setUp() throws Exception{
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mSecure = new MotoSecureLib();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            mSettings = new SettingsV();
        }
        mUtility = AvikUtility.getInstance();
        mSecure.forceCloseApp();

    }

    @After
    public void tearDown() throws Exception {
        mSecure.forceCloseApp();
    }

    public void testMain() throws Exception {
        mSecure.callApp();
        mUtility.sleep(Constants.ONE_SECOND);

        mUtility.runShellCommand("am start com.motorola.securityhub/com.motorola.securityhub.ui.view.RatingActivity");

        mUtility.takeAvikScreenshotWithFlag(MotoSecure_DoYouLikeThisApp);

        mDevice.pressHome();


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

