package avik.motohealth.auto.polar;

import static android.os.SystemClock.sleep;

import android.widget.ScrollView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import avik.motohealth.utils.MotoWatchConstants.ACTIVITY_TRACKER;
import avik.motohealth.utils.MotoWatchConstants.DATE_TYPE;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.motohealth.utils.MotoWatch;
import avik.motohealth.utils.MotoWatchConstants;

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
public class HealthWatchPolarHealthNoData{
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
    }

    @After
    public void tearDown() throws InterruptedException {
        mUtils.pressBackKeySeveralTimes(4);
        mLogger.info("=== End Of Execution ===");
    }

    public void captureActivityRing() throws Exception {
        mMotoWatch.openPolar();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Scrolling1");
        UiScrollable mScroll = mUtils.createScrollable();
        mScroll.scrollForward();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Scrolling2");
        mScroll.scrollBackward();
        sleep(Constants.ONE_SECOND);

        mMotoWatch.openPolarInfo();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_ActivityRing");

        mMotoWatch.openLearnMorePolar();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.expandArrowsLearnMore();
        sleep(Constants.ONE_SECOND);

        UiScrollable scrollObj = new UiScrollable(new UiSelector().resourceIdMatches(".*:id/content_layout"));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_Polar_AboutActivityTracking",scrollObj, 30, 150);

        mUtils.pressBackKeySeveralTimes(2);
        sleep(Constants.THREE_SECONDS);

        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.NOT_WORN);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_NotWorn_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.RESTING);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Resting_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.SITTING);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Sitting_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.LOW_INTENSITY);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_LowIntensity_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.MEDIUM_INTENSITY);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_MediumIntensity_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openTrackerTip(ACTIVITY_TRACKER.HIGH_INTENSITY);
        sleep(Constants.HALF_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_HighIntensity_Tip");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);

        mScroll = mUtils.createScrollable();
        mScroll.scrollForward();
        sleep(Constants.ONE_SECOND);
        BySelector dailyActivityGoalRes = By.res(Pattern.compile(".*:id/btn_progress_info"));
        mDevice.wait(Until.findObject(dailyActivityGoalRes),Constants.THREE_SECONDS).click();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_ReachYouGoal_Tip");

        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);

        mMotoWatch.changeDateType(DATE_TYPE.YEAR);
        sleep(Constants.ONE_SECOND);
        mScroll = new UiScrollable(new UiSelector().className(ScrollView.class));
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Year_Scrolling1");
        mScroll.scrollForward();
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Polar_Year_Scrolling2");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
    }

    public void captureNightlyRecharge() throws Exception {
        mMotoWatch.openNightlyRecharge();
        sleep(Constants.ONE_SECOND);
        UiScrollable mScroll = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_NightlyRecharge_Empty",mScroll, 2, 200);
        mMotoWatch.openAboutNightlyRecharge();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.expandTextsOnAboutNightlyRecharge();
        mScroll = mUtils.createScrollable();
        mScroll.scrollToBeginning(5);
        sleep(Constants.ONE_SECOND);
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_NightlyRecharge_About", mScroll, 17, 200);
        mUtils.pressBackKeySeveralTimes(2);
        sleep(Constants.THREE_SECONDS);
    }

    public void captureStress() throws Exception {
        mMotoWatch.openStress();
        sleep(Constants.ONE_SECOND);
        UiScrollable mScroll = new UiScrollable(new UiSelector().resourceId("com.motorola.watch:id/view_pager"));
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_Stress_Day", mScroll, 3, 150);
        mMotoWatch.changeDateType(DATE_TYPE.WEEK);
        sleep(Constants.ONE_SECOND);
        mUtils.createScrollable().scrollToBeginning(3);
        sleep(Constants.ONE_SECOND);
        avikHandler.takeScreenshot("MotoHealth_Health_Stress_Week");
        mDevice.pressBack();
        sleep(Constants.ONE_SECOND);
    }

    public void captureBloodOxygen() throws Exception {
        mMotoWatch.openBloodOxygen();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.openAboutBloodOxygen();
        sleep(Constants.ONE_SECOND);
        mMotoWatch.expandArrowsLearnMore();
        UiScrollable mScroll = new UiScrollable(new UiSelector().resourceId("com.motorola.watch:id/scroll_view"));
        mScroll.scrollToBeginning(5);
        sleep(Constants.ONE_SECOND);
        avikHandler.scrollAndTakeScreenshot("MotoHealth_Health_BloodOxygen_About", mScroll, 9, 150);
        mUtils.pressBackKeySeveralTimes(2);

    }

    public void captureDailyChart() throws Exception {
        mMotoWatch.openDailyChart();
        sleep(Constants.TWO_SECONDS);
        mMotoWatch.openActiveCalories();
        sleep(Constants.ONE_SECOND);
        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/prev_btn")),Constants.ONE_SECOND).click();
        sleep(Constants.TWO_SECONDS);
        avikHandler.takeScreenshot("MotoHealth_Health_Chart_Day");
        mMotoWatch.changeDateType(DATE_TYPE.WEEK);
        sleep(Constants.ONE_SECOND);
        BySelector unmarkedBox = By.res("com.motorola.watch:id/bmr_check_box").checked(false);
        mDevice.wait(Until.findObject(unmarkedBox), Constants.ONE_SECOND).click();
        sleep(Constants.ONE_SECOND);
        mLogger.severe("===== BELOW SCREEN NEEDS TO BE REPLACED MANUALLY =====");
        avikHandler.takeScreenshot("MotoHealth_Health_Chart_ActiveTime");
        mUtils.pressBackKeySeveralTimes(2);
    }

    public void capturePolar() throws Exception {
        mMotoWatch.launchApp();
        sleep(Constants.FIVE_SECONDS);

        mMotoWatch.changeTab(MotoWatchConstants.APP_TABS.HEALTH);
        sleep(Constants.ONE_SECOND);
        //avikHandler.takeScreenshot("MotoHealth_Health_Main_Scrolling1");
        UiScrollable mScroll = new UiScrollable(new UiSelector().className(ScrollView.class));
        mScroll.scrollForward();
        sleep(Constants.ONE_SECOND);
        //avikHandler.takeScreenshot("MotoHealth_Health_Main_Scrolling2");

        //captureActivityRing();
        //captureDailyChart();
        captureNightlyRecharge();
        captureStress();
        captureBloodOxygen();
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