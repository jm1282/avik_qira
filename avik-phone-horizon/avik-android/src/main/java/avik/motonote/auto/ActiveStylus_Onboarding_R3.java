package avik.motonote.auto;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
public class ActiveStylus_Onboarding_R3 {

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
        //mSettings = new Settings16();
        //mUtils.pressBackKeySeveralTimes(5);
    }

    @After
    public void tearDown() throws Exception {
        //mStylus.forceCloseApp();
    }

    public void sleep(long sleepTime) {
        mUtils.sleep(sleepTime);
    }

    public void captureSmartPenWelcome() throws Exception {
        mUtils.sleep(Constants.ONE_SECOND);
        mUtils.runShellCommand("am start -n com.motorola.activestylus/com.motorola.stylusmanager.activity.TipsActivity");
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("ActiveStylus_BTStylus_Settings_Onboarding_NotesCompanion");
    }

    @Test
    public void testMain() {
        try {
            captureSmartPenWelcome();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw new RuntimeException(e);
        }
    }
}