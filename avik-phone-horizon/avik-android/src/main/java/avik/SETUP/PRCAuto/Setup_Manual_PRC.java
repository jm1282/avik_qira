package avik.SETUP.PRCAuto;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;

import avik.SETUP.util.Setup;

/**
 * <pre>
 * Screen count: 25 Execution time: ~2m 40s
 *
 * Initial Setup:
 * 1. Add a working Wi-Fi network during setup flow
 * 2. Install the InvisibleIME app and set its keyboard as the main input;
 * 3. Go back to the first screen (language selection);
 *
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class Setup_Manual_PRC {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    private UiDevice mDevice;
    private AvikUtility mUtility;
    private ObjectUtils mUtils;
    private Setup mSetup;

    private AvikScreenshotAction Setup_CarrierPrivacyStatement;
    private AvikScreenshotAction Setup_SetAPIN;
    private AvikScreenshotAction Setup_SetAPIN_Skip;
    private AvikScreenshotAction Setup_TermsOfService_Scrolling1;
    private AvikScreenshotAction Setup_TermsOfService_Scrolling2;
    private AvikScreenshotAction Setup_FeatureRecommendation_LocationConsent_Dialog;
    private AvikScreenshotAction Setup_ChooseMode_Standard;
    private AvikScreenshotAction Setup_ChooseMode_Easy;
    private AvikScreenshotAction Setup_SetUpHotWord;
    private AvikScreenshotAction Setup_BackupService_Lenovo;
    private AvikScreenshotAction Setup_BackupService_DataSync_Lenovo;
    private AvikScreenshotAction Setup_LenovoAccount_Login;
    private AvikScreenshotAction Setup_LenovoAccount_Login_PwdLogin;
    private AvikScreenshotAction Setup_LenovoAccount_FormatError_Hint;
    private AvikScreenshotAction Setup_LenovoAccount_WrongPassword_Toast;
    private AvikScreenshotAction Setup_LenovoAccount_RetrievePassword;
    private AvikScreenshotAction Setup_BackupService_RecoverData_Lenovo;

    private AvikScreenshotAction Setup_FeatureRecommendation_Scrolling1 = new AndroidAvikScreenshotAction("Setup_FeatureRecommendation_Scrolling1", true);;
    private AvikScreenshotAction Setup_FeatureRecommendation_Scrolling2 = new AndroidAvikScreenshotAction("Setup_FeatureRecommendation_Scrolling2", true);;
    private AvikScreenshotAction Setup_NavigationStyle = new AndroidAvikScreenshotAction("Setup_NavigationStyle", true);;
    private AvikScreenshotAction Setup_ReadyToGo = new AndroidAvikScreenshotAction("Setup_ReadyToGo", true);;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mSetup = new Setup();
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();

        Setup_CarrierPrivacyStatement = new AndroidAvikScreenshotAction("Setup_CarrierPrivacyStatement", true);
        Setup_BackupService_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_Lenovo", true);
        Setup_LenovoAccount_Login = new AndroidAvikScreenshotAction("Setup_LenovoAccount_Login", true);
        Setup_LenovoAccount_Login_PwdLogin = new AndroidAvikScreenshotAction("Setup_LenovoAccount_Login_PwdLogin", true);
        Setup_LenovoAccount_FormatError_Hint = new AndroidAvikScreenshotAction("Setup_LenovoAccount_FormatError_Hint", true);
        Setup_SetAPIN = new AndroidAvikScreenshotAction("Setup_SetAPIN", true);
        Setup_LenovoAccount_RetrievePassword = new AndroidAvikScreenshotAction("Setup_LenovoAccount_RetrievePassword",true);
        Setup_SetAPIN_Skip = new AndroidAvikScreenshotAction("Setup_SetAPIN_Skip", true);
        Setup_BackupService_RecoverData_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_RecoverData_Lenovo", true);
        Setup_BackupService_DataSync_Lenovo = new AndroidAvikScreenshotAction("Setup_BackupService_DataSync_Lenovo", true);
        Setup_TermsOfService_Scrolling1 = new AndroidAvikScreenshotAction("Setup_TermsOfService_Scrolling1", true);
        Setup_TermsOfService_Scrolling2 = new AndroidAvikScreenshotAction("Setup_TermsOfService_Scrolling2", true);

        Setup_FeatureRecommendation_LocationConsent_Dialog = new AndroidAvikScreenshotAction("Setup_FeatureRecommendation_LocationConsent_Dialog", true);
        Setup_ChooseMode_Standard = new AndroidAvikScreenshotAction("Setup_ChooseMode_Standard", true);
        Setup_ChooseMode_Easy = new AndroidAvikScreenshotAction("Setup_ChooseMode_Easy", true);
        Setup_SetUpHotWord = new AndroidAvikScreenshotAction("Setup_SetUpHotWord", true);

        Setup_LenovoAccount_WrongPassword_Toast = new AndroidAvikScreenshotAction("Setup_LenovoAccount_WrongPassword_Toast", true);
    }
    
    private void captureManual() throws Exception {
        mUtility.takeAvikScreenshotWithFlag(Setup_CarrierPrivacyStatement);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetAPIN_Skip);
        mUtility.takeAvikScreenshotWithFlag(Setup_TermsOfService_Scrolling1);
        mUtility.takeAvikScreenshotWithFlag(Setup_TermsOfService_Scrolling2);
        mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_Scrolling1);
        mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_Scrolling2);
        mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_LocationConsent_Dialog);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseMode_Standard);
        mUtility.takeAvikScreenshotWithFlag(Setup_ChooseMode_Easy);
        mUtility.takeAvikScreenshotWithFlag(Setup_SetUpHotWord);
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_Lenovo);
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_DataSync_Lenovo);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_Login);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_Login_PwdLogin);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_FormatError_Hint);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_WrongPassword_Toast);
        mUtility.takeAvikScreenshotWithFlag(Setup_LenovoAccount_RetrievePassword);
        mUtility.takeAvikScreenshotWithFlag(Setup_BackupService_RecoverData_Lenovo);
        mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        mUtility.takeAvikScreenshotWithFlag(Setup_ReadyToGo);
    }

    public void captureDelta() throws Exception {
        mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_Scrolling1);
        mUtility.takeAvikScreenshotWithFlag(Setup_FeatureRecommendation_Scrolling2);
        mUtility.takeAvikScreenshotWithFlag(Setup_NavigationStyle);
        mUtility.takeAvikScreenshotWithFlag(Setup_ReadyToGo);
    }

    @Test
    public void testMain() {
        try {
            //captureManual();
            captureDelta();
        } catch (Exception e) {
            mUtility.printStackTraceOnLog(e);
        }

    }

}
