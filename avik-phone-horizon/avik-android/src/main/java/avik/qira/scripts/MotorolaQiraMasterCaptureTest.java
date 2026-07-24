package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * JUnit entrypoint for the Motorola Qira master capture flow.
 *
 * <p>The implementation lives in {@link MotorolaQiraMasterCapture}; this
 * wrapper exists so the test class name complies with the team's naming
 * convention regex.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraMasterCaptureTest extends MotorolaQiraMasterCapture {

    @Test
    @Override
    public void testMain() {
        super.testMain();
    }
}
