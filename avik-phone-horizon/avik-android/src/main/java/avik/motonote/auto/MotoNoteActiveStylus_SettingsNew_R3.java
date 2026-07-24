package avik.motonote.auto;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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
public class MotoNoteActiveStylus_SettingsNew_R3 {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUiDevice mDevice;
    private ObjectUtils mObjectUtils;
    private AvikUtility mUtils;
    private Stylus mStylus;

    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtils = AvikUtility.getInstance();
        //mUtil = new Util();
        mObjectUtils = new ObjectUtils();
        //mUtils.pressBackKeySeveralTimes(3);
        mStylus = new Stylus();
        //mStylus.forceCloseApp();
    }

    @After
    public void tearDown() throws Exception {
        mStylus.forceCloseApp();
        //mUtils.pressBackKeySeveralTimes(3);
    }

    public void captureViewOnly() throws Exception {
        mStylus.openApp();
        mUtils.sleep(Constants.ONE_SECOND);

        mUtils.runShellCommand("am start -n com.motorola.stylus/com.motorola.stylus.settings.activity.NoteSettingActivity");
        mUtils.scrollListAndCapture("MotoStylus_Settings_SmartPen", 2, 200);

        mUtils.runShellCommand("am start -n com.motorola.stylus/com.motorola.stylus.settings.activity.HandwritingCalculatorSettingsActivity");
        avikHandler.takeScreenshot("MotoStylus_Settings_HandwritingCalculator");

        mUtils.runShellCommand("am start -n com.motorola.stylus/com.motorola.stylus.settings.activity.DefaultPenSettingsActivity");
        avikHandler.takeScreenshot("MotoStylus_Settings_PenPreferences");
    }

    @Test
    public void testMain() {
        try {
            captureViewOnly();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw new RuntimeException(e);
        }
    }
}