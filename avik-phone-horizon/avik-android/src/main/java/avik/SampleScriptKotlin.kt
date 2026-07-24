package avik

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.motorola.g11n.tools.avik.client.android.AvikHandler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Sample of Avik Android script in Kotlin
 */
@RunWith(AndroidJUnit4::class)
class SampleScriptKotlin {

    @get:Rule
    val avikHandler = AvikHandler.instance

    /**
     * Run the script
     */
    @Test
    fun testMain() {
        avikHandler.takeScreenshot("TestKotlin")
    }
}
