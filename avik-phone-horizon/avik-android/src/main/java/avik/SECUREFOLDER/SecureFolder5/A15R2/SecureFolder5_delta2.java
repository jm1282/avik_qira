package avik.SECUREFOLDER.SecureFolder5.A15R2;


import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
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

import avik.SECUREFOLDER.utils.SecureFolderLib;

/**
 * Screen Number:  Execution Time: .
 *
 * <PRE>

 *     there is a lot of mDevice.hasObject around that looks useless. But that's the only workaround I found to avoid null object :(
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class SecureFolder5_delta2 {



    @Rule
    public final AvikHandler avikHandler=AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private AvikUtility mUtility;
    private SecureFolderLib mFolder;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private final String IMAGE_DIRECTORY = "AVIK_SECUREFOLDER";
    private final String IMAGE_PATH = Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY;
    private final String EMAIL = "ruicintester@gmail.com";



    AvikScreenshotAction SecureFolder_AddAppToSecureFolder_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AddAppToSecureFolder_Dialog", true);
    AvikScreenshotAction SecureFolder_AddAppToSecureFolder_AppAdded_Dialog = new AndroidAvikScreenshotAction("SecureFolder_AddAppToSecureFolder_AppAdded_Dialog", true);
    AvikScreenshotAction SecureFolder_UninstallOriginalApp_Dialog = new AndroidAvikScreenshotAction("SecureFolder_UninstallOriginalApp_Dialog", true);



    @Before
    public void setUp() throws Exception{
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mFolder = new SecureFolderLib();
        mUtility = AvikUtility.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    public void addApp() throws Exception {

        String recorderName = mUtility.getResourceByPackAndStringKey("com.motorola.audiorecorder", "app_name");
        String installSecureFolder = mUtility.getResourceByPackAndStringKey("com.motorola.launcher3", "install_in_secure_folder");
        String addApp = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "move_app_dialog_embedded_add_app_button");
        String gotIt = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "got_it");
        String removeApp = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "remove_app");

        mDevice.pressHome();


        int x = 530;
        int y = 2350;

        mDevice.swipe(x,y,x,y,100);


        mDevice.wait(Until.findObject(By.text(installSecureFolder)), Constants.FIVE_SECONDS).click();

        mDevice.wait(Until.findObject(By.text(addApp)), Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddAppToSecureFolder_Dialog);
        mDevice.wait(Until.findObject(By.text(addApp)), Constants.FIVE_SECONDS).click();


        mDevice.wait(Until.findObject(By.res("com.android.systemui:id/lockPassword")), Constants.FIVE_SECONDS).setText("1111");
        mDevice.pressEnter();

        mDevice.wait(Until.hasObject(By.text(gotIt)), Constants.TEN_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_AddAppToSecureFolder_AppAdded_Dialog);
        mDevice.wait(Until.hasObject(By.text(gotIt)), Constants.FIVE_SECONDS);

        mDevice.findObject(By.text(gotIt)).click();
        mUtility.sleep(Constants.TWO_SECONDS);

        Point point = mDevice.findObject(By.text(recorderName)).getVisibleCenter();
        while (!mDevice.hasObject(By.text(removeApp))){
            mDevice.swipe(point.x, point.y, point.x, point.y, 100);
            mUtils.sleep(Constants.TWO_SECONDS);
        }
        mDevice.wait(Until.findObject(By.text(removeApp)), Constants.FIVE_SECONDS).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.pressHome();

    }

    public void uninstallOriginalApp() throws Exception {

        String recorderName = mUtility.getResourceByPackAndStringKey("com.motorola.audiorecorder", "app_name");
        String installSecureFolder = mUtility.getResourceByPackAndStringKey("com.motorola.launcher3", "install_in_secure_folder");
        String addApp = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "move_app_dialog_embedded_add_app_button");
        String gotIt = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "got_it");
        String removeApp = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "remove_app");

        mDevice.pressHome();


        int x = 530;
        int y = 2350;

        mDevice.swipe(x,y,x,y,100);


        mDevice.wait(Until.findObject(By.text(installSecureFolder)), Constants.FIVE_SECONDS).click();

        mDevice.wait(Until.findObject(By.text(addApp)), Constants.FIVE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_UninstallOriginalApp_Dialog);

    }


    @Test
    public void main() throws Exception {
        try {
            //addApp();
            uninstallOriginalApp();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw e;
        }

    }
}
