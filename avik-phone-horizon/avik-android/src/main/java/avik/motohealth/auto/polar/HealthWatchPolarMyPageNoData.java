package avik.motohealth.auto.polar;

import static android.os.SystemClock.sleep;

import android.widget.ScrollView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.motohealth.utils.MotoWatch;
import avik.motohealth.utils.MotoWatchConstants;
import avik.motohealth.utils.MotoWatchConstants.ACTIVITY_TRACKER;
import avik.motohealth.utils.MotoWatchConstants.DATE_TYPE;

/**
 * Moto Health execution for Moto Watch R3
 * This one requires no Data, only a watch that needs to be connected
 *
 * <p>
 * PRE-STEPS:
 * <ul>
 * <li> Launch the Moto Watch app and accept all permissions</p>
 * <li> Close and Open the app twice, then move from each tab, go to Health, Watch & My Page</p>
 * <li> Reopen the app and on the "Health" tab choose to "Set up Health Connect" & "Sync data"</p>
 * <li> Make sure that you're using a account without exercise data</p>
 * </ul>
 *
 */

@RunWith(AndroidJUnit4.class)
public class HealthWatchPolarMyPageNoData {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public final Logger mLogger = AvikLoggerFactory.INSTANCE.getInstance();
    public AvikUiDevice mAvikDevice = AvikUiDevice.getInstance();
    public AvikUtility mUtils = AvikUtility.getInstance();
    public ObjectUtils mObjectUtils = new ObjectUtils();

    public UiDevice mDevice;
    public MotoWatch mMotoWatch;

    @Before
    public void setup() throws Exception{
        mLogger.info("=== Locale: " + mAvikDevice.getLocale());
        mLogger.info("=== Start of Execution ===");
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mMotoWatch = new MotoWatch();
        mMotoWatch.forceStop();
        sleep(Constants.THREE_SECONDS);
    }

    @After
    public void tearDown() throws InterruptedException {
        mUtils.pressBackKeySeveralTimes(4);
        mLogger.info("=== End Of Execution ===");
    }

    public void captureMyPage() throws Exception {
        mMotoWatch.changeTab(MotoWatchConstants.APP_TABS.MYPAGE);
        sleep(Constants.THREE_SECONDS);
        UiScrollable mScroll = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Settings_Main", mScroll, 2, 150);
        mScroll.scrollToBeginning(3);
        sleep(Constants.THREE_SECONDS);

        captureAccountDetails();
        captureFitnessGoal();
        captureReportIssue();
    }

    public void captureReportIssue() throws Exception {
        sleep(Constants.THREE_SECONDS);
        UiScrollable mScroll = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));
        mScroll.scrollToEnd(2);
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/b2g_item")),Constants.ONE_SECOND).click();
        sleep(Constants.THREE_SECONDS);
        avikHandler.takeScreenshot("MotoHealth_Settings_Main_ReportIssue");
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/next_btn")),Constants.ONE_SECOND).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Settings_Main_ReportIssue_Transmission_Dialog");

    }

    public void captureAccountDetails() throws Exception {
        mMotoWatch.openAccountDetails();
        sleep(Constants.ONE_SECOND);
        UiScrollable mScroll = mUtils.createScrollable();
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Settings_Main_AccountAndProfile", mScroll, 2, 150);
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/training_background")),Constants.ONE_SECOND).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Settings_Main_AccountAndProfile_TrainingBackground_Dialog");
        mUtils.pressBackKeySeveralTimes(2);
        sleep(Constants.ONE_SECOND);
    }

    public void captureFitnessGoal() throws Exception {
        mMotoWatch.openFitnessGoals();
        sleep(Constants.ONE_SECOND);
        UiScrollable mScroll = mUtils.createScrollable();
        try {
            avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_Polar_DailyFitnessGoal", mScroll, 2, 200);
        }catch (Exception e){
            mLogger.info("Erro sem Scroll");
            avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyFitnessGoal_Scrolling1");
        }
        sleep(Constants.TWO_SECONDS);
        String activeTime = mUtils.getResourceByStringOnCurrentAppPack("activity_duration");
        mDevice.wait(Until.findObject(By.text(activeTime)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_ActiveTime_Dialog");
        mDevice.pressBack();
        sleep(Constants.HALF_SECOND);
        String steps = mUtils.getResourceByStringOnCurrentAppPack("steps");
        mDevice.wait(Until.findObject(By.text(steps)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_Steps_Dialog");
        mDevice.pressBack();
        sleep(Constants.HALF_SECOND);
        String calories = mUtils.getResourceByStringOnCurrentAppPack("Calorie");
        mDevice.wait(Until.findObject(By.text(calories)),Constants.TWO_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_Calories_Dialog");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        try {
            mUtils.createScrollable().scrollBackward();
        } catch (Exception ignore){

        }
        sleep(Constants.ONE_SECOND);
        List<UiObject2> options = mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/recycler_view"))),Constants.THREE_SECONDS).getChildren();
        options.get(0).click();
        sleep(Constants.ONE_SECOND);
        mScroll = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_Level1", mScroll, 2, 200);
        mScroll.scrollBackward();
        sleep(Constants.ONE_SECOND);
        UiObject2 horizontalLevel = mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/tab_layout")),Constants.TWO_SECONDS);
        UiObject2 level2 = horizontalLevel.getChildren().get(0).getChildren().get(1);
        level2.click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_Level2");
        UiObject2 level3 = horizontalLevel.getChildren().get(0).getChildren().get(2);
        level3.click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_DailyActivityGoal_Level3");
        mUtils.pressBackKeySeveralTimes(2);
    }

    public void capturePolar() throws Exception {
        mMotoWatch.launchApp();
        sleep(Constants.FIVE_SECONDS);

        captureMyPage();
    }
    @Test
    public void testMain() throws Exception {
        try {
            capturePolar();
        } catch (Exception e){
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}