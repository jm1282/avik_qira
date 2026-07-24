package avik.motohealth.utils;

import android.annotation.SuppressLint;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.Until;

import com.motorola.frevoutils.code.definitions.BaseLibrary;
import com.motorola.frevoutils.code.utils.Constants;
import avik.motohealth.utils.MotoWatchConstants.ACTIVITY_TRACKER;
import avik.motohealth.utils.MotoWatchConstants.DATE_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class MotoWatch extends BaseLibrary {
    Assertions mAssertions;

    public MotoWatch(){
        this.mAssertions = new Assertions();
    }

    /**
     * Class that will contain all assertions to make sure all code is run in specific screens/
     * situations
     */
    public class Assertions {
        public boolean currentPageIsHealth(){
            BySelector healthSettingsIcon = By.res("com.motorola.watch:id/health_settings");
            return mDevice.wait(Until.hasObject(healthSettingsIcon),Constants.FIVE_SECONDS);
        }
        public boolean currentActivityIsExpected(String expectedActivity){
            return Objects.equals(mUtils.getCurrentActivityName(), expectedActivity);
        }
        public boolean polarInfoIsShown(){
            BySelector hasBottomSheet = By.res(Pattern.compile(".*:id/design_bottom_sheet"));
            return mDevice.wait(Until.hasObject(hasBottomSheet),Constants.TWO_SECONDS);
        }
        public boolean currentPageIsMyPage(){
            BySelector fitnessGoalBtn = By.res(Pattern.compile(".*:id/daily_goal"));
            return mDevice.wait(Until.hasObject(fitnessGoalBtn),Constants.TWO_SECONDS);
        }
        public boolean currentPageIsWatch(){
            BySelector watchFaceIcon = By.res(Pattern.compile(".*:id/rl_watch_face"));
            return mDevice.wait(Until.hasObject(watchFaceIcon),Constants.TWO_SECONDS);
        }
        public boolean currentActivityHasDateSelection(){
            BySelector dateTypeSelector = By.clazz(HorizontalScrollView.class.getName());
            return mDevice.wait(Until.hasObject(dateTypeSelector),Constants.THREE_SECONDS);
        }
        public boolean aboutScreenHasExpandableArrows(){
            BySelector arrowSelector = By.res("com.motorola.watch:id/narrow");
            return mDevice.wait(Until.hasObject(arrowSelector),Constants.THREE_SECONDS);
        }
    }

    public void launchApp() {
        mUtils.launchApp(MotoWatchConstants.AppInfo.APP_NAME, MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
    }

    public void forceStop() throws Exception {
        mUtils.forceCloseApp(MotoWatchConstants.AppInfo.APP_NAME, MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
    }

    public void openPanels() {
        assert mAssertions.currentPageIsWatch();

        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/panels")),Constants.TWO_SECONDS).click();
    }

    public void openAccountDetails() throws Exception {
        assert mAssertions.currentPageIsMyPage();

        mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/edit_profile")),Constants.THREE_SECONDS).click();
    }

    public void openWatchSettings() throws Exception{
        assert mAssertions.currentPageIsWatch();

        mUtils.launchApp("Watch Settings", MotoWatchConstants.Activity.Watch.WATCH_SETTINGS);
    }

    public void changeTab(MotoWatchConstants.APP_TABS tab){
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);

        UiObject2 appTabParent = mDevice.wait(Until.findObject(By.res("com.motorola.watch:id/tab_layout")),Constants.FIVE_SECONDS);
        List<UiObject2> appTab = appTabParent.getChildren().get(0).getChildren();
        UiObject2 requestedTab = appTab.get(tab.position);
        requestedTab.click();
        sleep(Constants.ONE_SECOND);
        requestedTab.click();
    }

    public void openPolar(){
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
        assert mAssertions.currentPageIsHealth();

        mUtils.launchApp("Polar Feature", MotoWatchConstants.Activity.Health.POLAR);
    }

    public void openPolarInfo() {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.POLAR);

        BySelector infoBtnRes = By.res(Pattern.compile(".*:id/btn_info"));
        mDevice.wait(Until.findObject(infoBtnRes),Constants.FIVE_SECONDS).click();
    }

    public void openDailyChart() {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
        assert mAssertions.currentPageIsHealth();

        mUtils.launchApp("Daily Chart", MotoWatchConstants.Activity.Health.DAILY_CHART);
    }

    public void openActiveCalories() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.DAILY_CHART);

        UiScrollable mScroll = mUtils.createScrollable();
        mScroll.scrollToEnd(3);
        sleep(Constants.ONE_SECOND);

        BySelector caloriesChart = By.res("com.motorola.watch:id/chart_calorie");
        mDevice.wait(Until.findObject(caloriesChart),Constants.THREE_SECONDS).click();

    }

    public void openLearnMorePolar(){
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.POLAR);
        assert mAssertions.polarInfoIsShown();

        mUtils.launchApp("About Polar", MotoWatchConstants.Activity.Health.ABOUT_POLAR);
    }

    public void openTrackerTip(ACTIVITY_TRACKER activity){
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.POLAR);

        mDevice.wait(Until.findObject(activity.barSelector),Constants.FIVE_SECONDS).click();
    }

    public void openFitnessGoals() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
        assert mAssertions.currentPageIsMyPage();

        BySelector fitnessGoalBtn = By.res(Pattern.compile(".*:id/daily_goal"));
        mDevice.wait(Until.findObject(fitnessGoalBtn),Constants.TWO_SECONDS).click();
    }

    public void changeDateType(DATE_TYPE wantedDateType){
        assert mAssertions.currentActivityHasDateSelection();

        UiObject2 dateTypeScroll = mDevice.wait(Until.findObject(By.clazz(HorizontalScrollView.class.getName())),Constants.THREE_SECONDS);
        UiObject2 dateTypeParent = dateTypeScroll.getChildren().get(0);
        dateTypeParent.getChildren().get(wantedDateType.position).click();

    }

    public void openNightlyRecharge() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);

        mUtils.launchApp("Nightly Recharge", MotoWatchConstants.Activity.Health.NIGHTLY_RECHARGE);
    }

    public void openAboutNightlyRecharge() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.NIGHTLY_RECHARGE);

        mUtils.launchApp("Nightly Recharge", MotoWatchConstants.Activity.Health.ABOUT_NIGHTLY_RECHARGE);
    }

    public void openStress() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
        assert mAssertions.currentPageIsHealth();

        mUtils.launchApp("Stress", MotoWatchConstants.Activity.Health.STRESS);
    }

    public void openBloodOxygen() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.MainUI.MAIN_PAGE);
        assert mAssertions.currentPageIsHealth();

        mUtils.launchApp("Blood Oxygen", MotoWatchConstants.Activity.Health.BLOOD_OXYGEN);
    }

    public void openAboutBloodOxygen() throws Exception {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.BLOOD_OXYGEN);

        mUtils.launchApp("About Blood Oxygen", MotoWatchConstants.Activity.Health.ABOUT_BLOOD_OXYGEN);
    }

    @SuppressLint("NewAPI")
    public void expandTextsOnAboutNightlyRecharge() throws UiObjectNotFoundException {
        assert mAssertions.currentActivityIsExpected(MotoWatchConstants.Activity.Health.ABOUT_NIGHTLY_RECHARGE);

        mUtils.createScrollable().scrollToEnd(5);
        sleep(Constants.ONE_SECOND);

        BySelector listSelector = By.res(Pattern.compile(".*:id/recycler_view"));
        UiObject2 parentList = mDevice.wait(Until.findObject(listSelector),Constants.FIVE_SECONDS);
        List<UiObject2> subItems = parentList.getChildren();
        // Reverse list to click subItems from Last to First
        List<UiObject2> reversedItemsList = subItems.reversed();
        for (UiObject2 item: reversedItemsList) {
            if(item.getChildren().size() < 3) {
                item.click();
            }
            sleep(Constants.ONE_SECOND);
        }

        // Scroll backwards (we probably only need to do this once) and click on nArrows again
        mUtils.createScrollable().scrollBackward(150);
        sleep(Constants.THREE_SECONDS);
        parentList = mDevice.wait(Until.findObject(listSelector),Constants.FIVE_SECONDS);
        subItems = parentList.getChildren();
        reversedItemsList = subItems.reversed();
        for (UiObject2 item: reversedItemsList) {
            if(item.getChildren().size() < 3) {
                item.click();
            }
            sleep(Constants.ONE_SECOND);
        }
    }
    @SuppressLint("NewAPI")
    public void expandArrowsLearnMore() throws UiObjectNotFoundException {
        assert mAssertions.aboutScreenHasExpandableArrows();

        try {
            mUtils.createScrollable().scrollToEnd(5);
        }catch (Exception ignore){}
        sleep(Constants.ONE_SECOND);

        BySelector downArrowRes = By.res(Pattern.compile(".*:id/narrow")).selected(false);
        List<UiObject2> downArrows = mDevice.wait(Until.findObjects(downArrowRes),Constants.FIVE_SECONDS);
        // Reverse list to click the arrow buttons from Last to First
        List<UiObject2> reversedDownArrowsList = downArrows.reversed();
        for (UiObject2 downArrow: reversedDownArrowsList) {
            downArrow.click();
            sleep(Constants.ONE_SECOND);
        }

        // Scroll backwards (we probably only need to do this once) and click on nArrows again
        try {
            mUtils.createScrollable().scrollBackward();
        }catch (Exception ignore){}
        downArrows = mDevice.wait(Until.findObjects(downArrowRes),Constants.FIVE_SECONDS);
        if(downArrows == null){
            return;
        }
        reversedDownArrowsList = downArrows.reversed();
        for (UiObject2 downArrow: reversedDownArrowsList) {
            downArrow.click();
            sleep(Constants.ONE_SECOND);
        }
    }
}