package avik.SECUREFOLDER.SecureFolder5.A15R2;


import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
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
import org.w3c.dom.Text;

import java.util.List;
import java.util.logging.Logger;

import avik.SECUREFOLDER.utils.SecureFolderLib;

/**
 * Screen Number: 36 Execution Time: .
 *
 * <PRE>
 *
 *     Set secure folder pin and fingerprint
 *
 *
 *     there is a lot of .hasObject() around that looks useless, but that's the only workaround I found to avoid null object breaking everything :(
 * </PRE>
 */

@RunWith(AndroidJUnit4.class)
public class SecureFolder5_Fingerprint {



    @Rule
    //public AvikHandler mAvik = new AvikHandler();
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public UiDevice mDevice;
    public ObjectUtils mUtils;
    private AvikUtility mUtility;
    private SecureFolderLib mFolder;
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private final String IMAGE_DIRECTORY = "AVIK_SECUREFOLDER";
    private final String IMAGE_PATH = Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY;
    private final String EMAIL = "cinauto2014@gmail.com";
    private final String PASSWORD = "Passcinauto2022!";

    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess", false);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_ConfirmIdentityWithFingerprint = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_ConfirmIdentityWithFingerprint", false);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_FingerprintNotRecognized = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_FingerprintNotRecognized", true);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_FingerprintRecognized = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_FingerprintRecognized", true);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_VerifyWithMotoAccount = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_VerifyWithMotoAccount", true);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_VerifyWithMotoAccount_Failed = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_VerifyWithMotoAccount_Failed", true);
    private final AndroidAvikScreenshotAction SecureFolder_RecoverAccess_VerifyWithMotoAccount_Successful = new AndroidAvikScreenshotAction("SecureFolder_RecoverAccess_VerifyWithMotoAccount_Successful", true);


    @Before
    public void setUp() throws Exception{
        mUtils = new ObjectUtils();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mFolder = new SecureFolderLib();
        mUtility = AvikUtility.getInstance();
        mFolder.forceCloseApp();
        mDevice.pressHome();
    }

    @After
    public void tearDown() throws Exception {
        mFolder.forceCloseApp();
        mUtility.pressBackKeySeveralTimes(3);
        mDevice.pressHome();
    }

    public void captureScreens() throws Exception {
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.launcher3:id/workspace"))
//                .getChildren().get(0)
//                .getChildren().get(0)
//                .getChildren().get(0)
//                .click();
//
        BySelector lockPassword = By.res("com.android.systemui:id/lockPassword");
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        //Enter wrong password two times
//        mDevice.findObject(lockPassword).setText("1111");
//        mDevice.pressEnter();
//        mDevice.findObject(lockPassword).setText("1111");
//        mDevice.pressEnter();
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess);
//        mDevice.findObject(By.res("com.android.systemui:id/recover")).click();

        mUtility.runShellCommand("am start -n com.motorola.securevault/com.motorola.securevault.CheckAccountActivity");

        mUtility.sleep(Constants.THREE_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_ConfirmIdentityWithFingerprint);

        //Click on cancel to trigger error screen
        mDevice.findObject(By.res("com.android.systemui:id/button_negative")).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_FingerprintNotRecognized);

        String tryAgain = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "check_account_try_again_button");
        mDevice.findObject(By.text(tryAgain)).click();

        String continueText = mUtility.getResourceByPackAndStringKey(mFolder.PACKAGE_NAME, "check_account_confirm_button");
        logger.info("====== Place fingerprint on sensor... ======");
        mDevice.wait(Until.findObject(By.text(continueText)), Constants.ONE_MINUTE);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_FingerprintRecognized);
        mUtility.sleep(Constants.TWO_SECONDS);
        mDevice.findObject(By.text(continueText)).click();

        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_VerifyWithMotoAccount);
        mUtility.sleep(Constants.TWO_SECONDS);


        mDevice.click(0,0);
        UiObject2 emailField = mDevice.findObjects(By.clazz(EditText.class)).get(0);
        mUtility.sleep(Constants.HALF_SECOND);
        UiObject2 passwordField = mDevice.findObjects(By.clazz(EditText.class)).get(1);

        emailField.setText("aaaa");
        passwordField.setText("aaaa");
        mUtility.sleep(Constants.ONE_SECOND);


        mDevice.findObject(By.text(continueText)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_VerifyWithMotoAccount_Failed);

        mDevice.findObject(By.text(tryAgain)).click();
        mUtility.sleep(Constants.THREE_SECONDS);

        List<UiObject2> texts = mDevice.findObjects(By.clazz(Text.class));

        for (int i = 0 ; i < texts.size() ; i++ ){
            logger.info("Text: " + texts.get(i).getText());
        }

        UiObject2 emailField2 = mDevice.findObjects(By.clazz(EditText.class)).get(0);
        UiObject2 passwordField2 = mDevice.findObjects(By.clazz(EditText.class)).get(1);

        mDevice.hasObject(By.clazz(EditText.class));

        emailField2.clear();
        emailField2.setText(EMAIL);
        mUtility.sleep(Constants.ONE_SECOND);

        passwordField2.clear();
        passwordField2.setText(PASSWORD);
        mUtility.sleep(Constants.ONE_SECOND);


        mDevice.findObject(By.text(continueText)).click();
        mUtility.sleep(Constants.TWO_SECONDS);
        mUtility.takeAvikScreenshotWithFlag(SecureFolder_RecoverAccess_VerifyWithMotoAccount_Successful);

//
//        mDevice.pressHome();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.findObject(By.res("com.motorola.launcher3:id/workspace"))
//                .getChildren().get(0)
//                .getChildren().get(0)
//                .getChildren().get(0)
//                .click();
//
//        lockPassword = By.res("com.android.systemui:id/lockPassword");
//
//        mUtility.sleep(Constants.TWO_SECONDS);
//        //Enter wrong password two times
//        mDevice.findObject(lockPassword).setText("1234");
//        mDevice.pressEnter();
//        mUtility.sleep(Constants.TWO_SECONDS);
//        mDevice.pressBack();

    }




    @Test
    public void main() throws Exception {
        try {
            captureScreens();
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            logger.severe(stackTrace);
            throw e;
        }

    }
}
