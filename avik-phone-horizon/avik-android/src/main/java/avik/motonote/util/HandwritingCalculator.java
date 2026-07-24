package avik.motonote.util;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

public class HandwritingCalculator {
    final String hCalcPackage = "com.motorola.handwritingcalculator";
    private final UiDevice mDevice;
    private final AvikUtility mUtility;
    private final ObjectUtils mUtils;
    public BySelector tooltipButton = By.res("com.motorola.stylus:id/button");

    public HandwritingCalculator() {

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUtility = AvikUtility.getInstance();
        mUtils = new ObjectUtils();
    }

    public void clearApp() throws Exception {
        mUtility.runShellCommand("pm clear " + hCalcPackage);
        mUtils.sleep(Constants.TWO_SECONDS);

    }

    public void forceCloseApp() throws Exception {
        System.out.println("=== Closing the Handwriting Calculator app ===");
        mUtility.runShellCommand(String.format("am force-stop %s", hCalcPackage));
    }

    public void callApp() throws Exception {
        System.out.println("=== Opening the Handwriting Calculator app ===");
        mUtility.runShellCommand("am start -n com.motorola.handwritingcalculator/com.motorola.handwritingcalculator.ui.HandwritingActivity");
    }
}
