package avik.motohealth.utils;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;

import com.motorola.frevoutils.code.definitions.BaseLibrary;

import java.util.regex.Pattern;

public class MotoWatchConstants extends BaseLibrary {
    public class AppInfo {
        public static final String APP_PACKAGE = "com.motorola.watch";
        public static final String APP_NAME = "Moto Watch";
    }

    public class Activity {
        public class MainUI {
            public static final String AUTHORIZATION = "com.motorola.watch/com.motorola.watch.ui.activity.UserNoticeActivity";
            public static final String APP_PERMISSION = "com.motorola.watch/com.motorola.watch.ui.activity.UserNoticeActivity";
            public static final String MAIN_PAGE = "com.motorola.watch/com.motorola.watch.ui.activity.MainPageActivity";
            public static final String HEALTH_SETTINGS = "com.motorola.watch/com.motorola.watch.ui.activity.settings.HealthSettingsActivity";
        }

        public class Watch {
            public static final String WATCH_SETTINGS = "com.motorola.watch/com.motorola.watch.ui.activity.BandDetailedInfoActivity";
        }


        public class Health {
            public static final String POLAR = "com.motorola.watch/com.motorola.watch.ui.activity.PolarActivity";
            public static final String ABOUT_POLAR = "com.motorola.watch/com.motorola.watch.ui.activity.AboutActivityTrackingActivity";
            public static final String DAILY_CHART = "com.motorola.watch/com.motorola.watch.ui.activity.SportDashboardActivity";
            public static final String SLEEP = "com.motorola.watch/com.motorola.watch.ui.activity.SleepDetailActivity";
            public static final String ABOUT_SLEEP = "com.motorola.watch/com.motorola.watch.ui.activity.SleepMoreInfoActivity";
            public static final String NIGHTLY_RECHARGE = "com.motorola.watch/com.motorola.watch.ui.activity.NightlyRechargeDetailActivity";
            public static final String ABOUT_NIGHTLY_RECHARGE = "com.motorola.watch/com.motorola.watch.ui.activity.NightlyRechargeEduActivity";
            public static final String HEART_RATE = "com.motorola.watch/com.motorola.watch.ui.activity.HeartRateActivity";
            public static final String STRESS = "com.motorola.watch/com.motorola.watch.ui.activity.StressActivity";
            public static final String BLOOD_OXYGEN = "com.motorola.watch/com.motorola.watch.ui.activity.BloodOxygenActivity";
            public static final String ABOUT_BLOOD_OXYGEN = "com.motorola.watch/com.motorola.watch.ui.activity.BloodOxygenMoreInfoActivity";
            public static final String WORKOUT_RECORD = "com.motorola.watch/com.motorola.watch.ui.activity.HealthRecordActivity";
        }
    }

    public enum APP_TABS {
        HEALTH(0),
        WATCH(1),
        MYPAGE(2);

        public int position;
        APP_TABS(int position) {
            this.position = position;
        }
    }

    public enum ACTIVITY_TRACKER {
        NOT_WORN(By.res(Pattern.compile(".*:id/tv_no_worn"))),
        RESTING(By.res(Pattern.compile(".*:id/tv_resting"))),
        SITTING(By.res(Pattern.compile(".*:id/tv_sitting"))),
        LOW_INTENSITY(By.res(Pattern.compile(".*:id/tv_low_intensity"))),
        MEDIUM_INTENSITY(By.res(Pattern.compile(".*:id/tv_medium_intensity"))),
        HIGH_INTENSITY(By.res(Pattern.compile(".*:id/tv_high_intensity")));

        public BySelector barSelector;
        ACTIVITY_TRACKER(BySelector barSelector){
            this.barSelector = barSelector;
        }
    }

    public enum DATE_TYPE {
        DAY(0),
        WEEK(1),
        MONTH(2),
        YEAR(3);

        public int position;
        DATE_TYPE(int position){
            this.position = position;
        }
    }
}
