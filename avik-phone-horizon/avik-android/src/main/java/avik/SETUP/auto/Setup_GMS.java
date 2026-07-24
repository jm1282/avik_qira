package avik.SETUP.auto;

import com.motorola.g11n.tools.avik.screenshot.action.AvikScreenshotAction;
import com.motorola.g11n.tools.avik.client.android.screenshot.action.AndroidAvikScreenshotAction;

/**
 * <pre>
 * Screen count: 22 Execution time: ~4m
 *
 * Initial Setup:
 * 1. Go through the Setup flow first and:
 * 1.1. Set up the Wi-Fi;
 * 1.2. Configure a Google Account (i.e. cinauto2014@gmail.com);
 * 1.3. Skip the Voice Assistant screen.
 * 2. Install the InvisibleIME app and set its keyboard as the main input;
 * 3. Go back to the first screen (language selection);
 * 4. Run the script for all target locales, since this setup is only required once
 *
 * </pre>
 */


public class Setup_GMS {

    // capture this block
    private AvikScreenshotAction Setup_EmergencyCall_Numbers_pureGMS;
    private AvikScreenshotAction Setup_EmergencyInformation_pureGMS;
    // for Venus only
    private AvikScreenshotAction Setup_ConnectToMobileNetwork_eSIM_PureGMS;
    private AvikScreenshotAction Setup_SkipNetwork_Dialog_pureGMS;
    private AvikScreenshotAction Setup_ConnectToWifi_pureGMS;
    private AvikScreenshotAction Setup_SeeAllWifi_pureGMS;
    private AvikScreenshotAction Setup_UseMobileNetwork_Dialog_pureGMS;
    private AvikScreenshotAction Setup_GettingYourPhoneReady_pureGMS;
    private AvikScreenshotAction Setup_CopyAppsAndData_pureGMS;
    private AvikScreenshotAction Setup_SelectYourOldPhone_pureGMS;
    private AvikScreenshotAction Setup_TransferPhotos_Dialog_pureGMS;
    private AvikScreenshotAction Setup_OpenGoogleApp_pureGMS;
    private AvikScreenshotAction Setup_GooglePreInstalled_Dialog_pureGMS;
    private AvikScreenshotAction Setup_KeepGoogleAppOpen_pureGMS;
    private AvikScreenshotAction Setup_TypeInSearchBox_Dialog_pureGMS;
    private AvikScreenshotAction Setup_VerifyMyPhone_pureGMS;
    private AvikScreenshotAction Setup_CopyAnotherWay_Dialog_pureGMS;
    private AvikScreenshotAction Setup_UseYourOldPhone_pureGMS;
    private AvikScreenshotAction Setup_OnOtherDevice_pureGMS;
    private AvikScreenshotAction Setup_CopyYourDataIOS_pureGMS;
    

    private AvikScreenshotAction Setup_CreateAccountOptions_pureGMS;
    private AvikScreenshotAction Setup_EnterEmail_pureGMS;
    private AvikScreenshotAction Setup_EnterValidEmail_pureGMS;
    private AvikScreenshotAction Setup_CreateAccountForChild_pureGMS;
    private AvikScreenshotAction Setup_CreateAccountForMe_pureGMS;
    private AvikScreenshotAction Setup_FindYourEmail_pureGMS;
    private AvikScreenshotAction Setup_YourDeviceWorksBetter_Dialog_pureGMS;
    private AvikScreenshotAction Setup_SkipAccount_Dialog_pureGMS;
    private AvikScreenshotAction Setup_EnterYourPassword_pureGMS;
    private AvikScreenshotAction Setup_EnterAPassword_pureGMS;
    private AvikScreenshotAction Setup_WrongPassword_pureGMS;
    private AvikScreenshotAction Setup_AccountRecovery_pureGMS;
    private AvikScreenshotAction Setup_WhoWillUse_pureGMS;
    private AvikScreenshotAction Setup_Welcome_pureGMS;
    
    
    private AvikScreenshotAction Setup_AccountAdded_pureGMS;
    private AvikScreenshotAction Setup_RestoreFromBackup_pureGMS;
    private AvikScreenshotAction Setup_GoogleServices_pureGMS;
    private AvikScreenshotAction Setup_ContinueSetup_pureGMS;
    private AvikScreenshotAction Setup_AccessYourAssistant_pureGMS;
    private AvikScreenshotAction Setup_TeachYourAssistant_pureGMS;
    private AvikScreenshotAction Setup_SayOkGoogle_pureGMS;
    private AvikScreenshotAction Setup_AccessWithoutUlocking_pureGMS;
    private AvikScreenshotAction Setup_YoutubeMusic_pureGMS;
    private AvikScreenshotAction Setup_ReviewAdditionalApps_pureGMS;
    
    
    
    
    private AvikScreenshotAction Setup_GooglePay_pureGMS; 
    private AvikScreenshotAction Setup_AnythingElse_pureGMS;
    private AvikScreenshotAction Setup_AnythingElseDoneForNow_pureGMS;

    
    
    
    
    
    
    
    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch;
    private AvikScreenshotAction Setup_Motorola_StayInTheKnow;
    private AvikScreenshotAction Setup_Motorola_OptOut_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterEmail;
    private AvikScreenshotAction Setup_Motorola_EnterValidEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_OptOutEmail_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterCPF;
    private AvikScreenshotAction Setup_Motorola_UseOfCPF_Dialog;
    private AvikScreenshotAction Setup_Motorola_EnterValidCPF_Dialog;
    
//    private AvikScreenshotAction Setup_Motorola_LetsStayInTouch_UseThisEmail_Dialog;
//    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere;
//    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_UseOfCPF_Dialog;
//    private AvikScreenshotAction Setup_Motorola_YoureAlmostThere_InsertAValidCPFNumber_Tooltip;
//    private AvikScreenshotAction Setup_Motorola_AreYouSure_Dialog;
    private AvikScreenshotAction Setup_Motorola_YoureAllSet;
    private AvikScreenshotAction Setup_ChooseYourTheme_Light;
    private AvikScreenshotAction Setup_ChooseYourTheme_Dark;
    private AvikScreenshotAction Setup_ChooseNavigation_Gestures;
    private AvikScreenshotAction Setup_ChooseNavigation_Buttons;
    

}
