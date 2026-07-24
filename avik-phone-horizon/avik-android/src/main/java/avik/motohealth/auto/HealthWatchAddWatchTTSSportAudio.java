package avik.motohealth.auto;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.motohealth.utils.TestUtils;


//PRE-REQUISITES:

/**
 * <pre>
 * 1) This script is only applicable to devices wit..
 * </pre>
 */
@RunWith(AndroidJUnit4.class)
public class HealthWatchAddWatchTTSSportAudio {
    TestUtils mUtil = new TestUtils();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();

    @Before
    public void setUp() throws Exception {
        mUtil.writeLog("setup");
        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }
    @After
    public void tearDown() throws InterruptedException {
        mUtil.pressBack(3);
        mUtil.writeLog("tearDown");
    }

    @Test
    @DeltaMethod
    public void captureScreensOfTTS() throws Exception {
        AvikLogger.info("===== Starting Script =====");
        mUtil.clickByObj("Moto Watch","android.widget.TextView",0);
        mUtil.sleep(2000);

        /**
         * Scope: To ensure the word "and" correctly shifts between the hour and minute, hour and second or minute and second when the exercise data lacks the hour / minute / second value.
         * Audio readout: Great job! You’ve reached X miles in XX hour(s) XX minute(s) and XX second(s) workout. >
         * “Great job! You’ve reached 5 miles in 1 hour 55 minutes and 10 seconds workout.”
         */

        //1. hr/min = Great job! You’ve reached 2 miles in 1 hour and 5 minutes workout.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_EnsureTheWordAnd_hr_min");

        //2. hr/sec = Great job! You’ve reached 2 miles in 1 hour and 30 seconds workout.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_EnsureTheWordAnd_hr_sec");

        //3. min/sec = Great job! You’ve reached 1 mile in 5 minutes and 30 seconds workout.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_EnsureTheWordAnd_min_sec");

        /**
         * Scope: Testing singular and plural form:
         * Audio readout: Keep going! You’ve walked %s , with the last kilometer completed in %s. >
         * “Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 1 hour, 30 minutes and 10 seconds.”
         */

        //1. Only in Singular Form = Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 1 hour, 1 minute and 1 second.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_OnlyInSingular");

        //2. Only in Plural Form = Keep going! You’ve walked 2 kilometers, with the last kilometer completed in 2 hours, 2 minutes and 2 seconds.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_OnlyInSingularPlural");

        //3. 3 Singular + 1 Plural Form = Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 1 hour, 1 minute and 2 seconds.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_3Singular1Plural");

        //4. 2 Singular + 2 Plural Form = Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 1 hour, 2 minutes and 2 seconds.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_2Singular2Plural");

        //5. 1 Singular + 3 Plural Form = Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 2 hours, 2 minutes and 2 seconds.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_1Singular3Plural");

        //6. 1 Plural + 3 Singular Form = Keep going! You’ve walked 2 kilometers, with the last kilometer completed in 1 hour, 1 minute and 1 second.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_1Plural3Singular");

        //7. 2 Plural + 2 Singular Form = Keep going! You’ve walked 2 kilometers, with the last kilometer completed in 2 hours, 1 minute and 1 second.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_2Plural2Singular");

        //8. 3 Plural + 1 Singular Form = Keep going! You’ve walked 2 kilometers, with the last kilometer completed in 2 hours, 2 minutes and 1 second.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_3Plural1Singular");

        //9. Mixture of 2 Singular + 2 Plural Form = Keep going! You’ve walked 1 kilometer, with the last kilometer completed in 2 hours, 1 minute and 2 seconds.
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_MixtureOf2Singular2Plural_1");

        //10. Mixture of 2 Singular + 2 Plural Form
        mUtil.takeAvikScreenshot("MotoHealth_TTS_SingularAndPluralForm_MixtureOf2Singular2Plural_2");
    }
    @Test
    public void testMain() throws Exception {

        captureScreensOfTTS();
    }
}