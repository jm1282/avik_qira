package avik.motonote.auto;

import android.os.Build;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.libraries.settings.Settings16;
import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.motonote.util.Stylus;

//PRE-REQUISITES:

/**
 * <PRE>
 * Screen Number:
 * Execution Time: .
 * <p>
 * Preconditions:
 * 1) PEN INSIDE DEVICE, NO NEED TO REMOVE
 * 2) ENABLE GESTURE NAVIGATION
 * 3) Put app icon to home screen
 * 4) Hold app icon, then add all types to home screen, and hold the app icon at the first place.
 * 5) push the pictures under res.DCIM.Camera to phone
 * *
 *
 * <p>
 * Manual screens:
 */
// =======================================================

@RunWith(AndroidJUnit4.class)
public class ActiveStylus_OTA_R3 {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUiDevice mDevice;
    private ObjectUtils mObjectUtils;
    private AvikUtility mUtils;
    private Settings16 mSettings;
    private Stylus mStylus;

    @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtils = AvikUtility.getInstance();
        mObjectUtils = new ObjectUtils();
        mStylus = new Stylus();
        mSettings = new Settings16();
        mUtils.runShellCommand("am start com.motorola.activestylus/com.motorola.stylusmanager.activity.FirmwareUpdateActivity");
    }

    @After
    public void tearDown() throws Exception {
    }

    public void sleep(long sleepTime){
        mUtils.sleep(sleepTime);
    }

    public void capture() throws Exception {
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("ActiveStylus_Settings_OTA_Update");
        mDevice.wait(Until.findObject(By.clazz(Button.class.getName())),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("ActiveStylus_Settings_OTA_Update_NoConnection_Dialog");
        mDevice.pressBack();
    }

    @Test
    public void testMain() {
        try {
            capture();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw new RuntimeException(e);
        }
    }
}
