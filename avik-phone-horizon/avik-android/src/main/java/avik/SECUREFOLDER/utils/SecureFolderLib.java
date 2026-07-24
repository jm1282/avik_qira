package avik.SECUREFOLDER.utils;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;


public class SecureFolderLib {

    public final java.util.logging.Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private AvikUtility mUtility = AvikUtility.getInstance();
    private ObjectUtils mUtils = new ObjectUtils();
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public String PACKAGE_NAME = "com.motorola.securevault";
    public static final String MOTOSECURE_PACKAGE_NAME = "com.motorola.securityhub";
    public String APP_NAME;
    public static final String PIN_CODE = "1111";

    public SecureFolderLib() throws Exception {
        APP_NAME = mUtility.getResourceByPackAndStringKey(PACKAGE_NAME, "app_name");
    }

    public void clearApp() throws Exception {
        mUtility.runShellCommand(String.format("pm clear %s", PACKAGE_NAME));

    }

    public String getAppName() throws Exception {
        return mUtility.getResourceByPackAndStringKey(PACKAGE_NAME, "app_name");
    }

    public void callApp() throws Exception {
//        mUtility.runShellCommand("am start com.motorola.securevault/com.motorola.securevault.MainActivity");
        mUtility.runShellCommand("am start com.motorola.securevault/com.motorola.securevault.PrimaryUserActivity");
        mUtility.sleep(Constants.ONE_SECOND);
    }

    public void forceCloseApp() throws Exception {
        mUtility.runShellCommand(String.format("am force-stop %s", PACKAGE_NAME));
    }

    public void skipIntro() throws Exception {
        String cont = mUtility.getResourceByPackAndStringKey("com.motorola.securevault", "onboarding_continue");
        String next = mUtility.getResourceByPackAndStringKey("com.motorola.securevault", "next_page");
        String start = mUtility.getResourceByPackAndStringKey("com.motorola.securevault", "complete_onboarding_button_text");
        mDevice.findObject(By.text(cont)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.text(next)).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(3).click();
        mUtility.sleep(Constants.ONE_SECOND);
        mDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(3).click();
        mUtility.sleep(Constants.ONE_SECOND);

    }

    public void showMenu() throws Exception {
        mDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).click();
        mUtils.sleep(Constants.ONE_SECOND);
    }


    public void deleteIfEnabled() throws Exception {
        logger.info("Deleting Secure Folder...");
        mUtils.startActivityWithAction(
                "com.android.settings", "com.android.settings.security.SECURITY_ADVANCED_SETTINGS");
        mUtils.sleep(Constants.THREE_SECONDS);
        String enabled = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "enabled_summary");
        String delete = mUtils.getResourceByPackAndStringKey(PACKAGE_NAME, "delete_secure_vault");
        String yesDelete =
                mUtils.getResourceByPackAndStringKey(
                        PACKAGE_NAME, "delete_profile_modal_affirmative");
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.createScrollable().scrollTextIntoView(getAppName());
        //mUtility.createScrollable().scrollToEnd(2);
        mUtils.sleep(Constants.TWO_SECONDS);

        String secureFolderStatus = mDevice.findObject(By.text(getAppName())).getParent().getChildren().get(1).getText();

        if (secureFolderStatus.equals(enabled)) {
            mDevice.findObject(By.text(getAppName())).click();
            mUtils.sleep(Constants.TWO_SECONDS);
            mDevice.findObject(By.text(delete)).click();
            mUtils.sleep(Constants.TWO_SECONDS);
            mDevice.findObject(By.text(yesDelete)).click();
            logger.info("Secure folder deleted.");
        } else {
            logger.info("Secure folder already deleted.");
        }
        mUtils.sleep(Constants.TWO_SECONDS);
        mUtility.pressBackKeySeveralTimes(5);
    }

    public void clickNextBtn() throws Exception {
        mDevice.findObject(By.res("android:id/content"))
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(3)
                .click();
        mUtils.sleep(Constants.ONE_SECOND);
    }
}
