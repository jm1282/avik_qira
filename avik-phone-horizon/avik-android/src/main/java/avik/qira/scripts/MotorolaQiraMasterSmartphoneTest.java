package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * JUnit entrypoint for smartphone/folded-screen capture runs.
 *
 * <p>Uses the same master pipeline as {@link MotorolaQiraMasterCaptureTest},
 * while {@link MotorolaQiraSettingsCapture} auto-switches to Back-navigation
 * between Settings options when the UI is in single-pane mode.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraMasterSmartphoneTest extends MotorolaQiraMasterCapture {

    @Test
    @Override
    public void testMain() {
        super.testMain();
    }
}
