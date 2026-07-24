package avik;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.motorola.g11n.tools.avik.client.android.AvikHandler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Sample of Avik Android script in Java
 */
@RunWith(AndroidJUnit4.class)
public class SampleScriptJava {

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();

    /**
     * Run the script
     */
    @Test
    public void testMain() {
        avikHandler.takeScreenshot("Testing1");
    }
}