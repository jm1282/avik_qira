package avik.motonote.auto;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

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
public class MotoNoteActiveStylus_R3 {

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
    }

    public void captureViewOnly() throws Exception {
        mStylus.openApp();
        mUtils.sleep(Constants.ONE_SECOND);

        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/note_title").text("AVIK NOTE")),Constants.THREE_SECONDS).click();
        UiObject2 container = mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/container_end")),Constants.TWO_SECONDS);
        container.getChildren().get(2).click();
        mUtils.sleep(Constants.TWO_SECONDS);
        avikHandler.takeScreenshot("MotoStylus_Note_Options_Unpin");
        mUtils.pressBackKeySeveralTimes(2);
        mUtils.sleep(Constants.TWO_SECONDS);
        mDevice.wait(Until.findObject(By.res("com.motorola.stylus:id/note_title").text("Notă")),Constants.THREE_SECONDS).click();
        mUtils.sleep(1500L);
        avikHandler.takeScreenshot("MotoStylus_Note_ViewOnly_Toast");
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
