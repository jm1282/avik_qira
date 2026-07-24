package avik.Juste;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.SystemClock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.motorola.g11n.avik.uiautomatoradapter.AvikConstants;
import com.motorola.g11n.avik.uiautomatoradapter.AvikLogger;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <pre>
 * Screen Number: 13
 * Execution Time:  Time: 292.335
 *
 * Pre-conditions:
 *     1) Connect the watch
 * </pre>
 */

@RunWith(AndroidJUnit4.class)
public class End {
    private AvikUiDevice mDevice;
    private AvikUtility mUtils;
    //map<screenName from avik review app, screen path description in watch>
    private Map<String, String> map;
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();

    @Before
    public void setUp() throws Exception {
        mDevice = AvikUiDevice.getInstance();
        mUtils = AvikUtility.getInstance();
        map = new LinkedHashMap<>();

        map.put("stamina-1_Scrolling1", "First screen of full day stamina");
        map.put("stamina-1_Scrolling2", "Scroll if needed, to show truncations/overlaps et cetera");
        map.put("imperial-13", "auto");

        deleteScreensInMobile();
        deleteScreensInMobile();
    }

    public void runCommandBeforeCapture(String command){
        UiObject2 snapShotCommObj = mDevice.findObject(By.res("win.lioil.bluetooth:id/ac_instruct"));
        snapShotCommObj.setText(command);
        mUtils.sleep(500L);
        mDevice.pressBack();
        mUtils.sleep(500L);
        mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/btn_sendMsg"))),1000L).click();
        mUtils.sleep(500L);
    }

    @Test
    public void testMain() {
        captureScreens();
    }

    @DeltaMethod
    private void captureScreens() {
        try {
            mUtils.sleep(AvikConstants.SHORTWAIT);
            if (!mDevice.hasObject(By.res("win.lioil.bluetooth:id/btn_exportFile"))) {
                mDevice.executeShellCommand("am start win.lioil.bluetooth/win.lioil.bluetooth.bt226.BtClientActivity");
                mUtils.sleep(AvikConstants.SHORTWAIT);
            }
            UiObject tipsObj = mDevice.findObject(new UiSelector().resourceId("win.lioil.bluetooth:id/tv_tips"));
            UiObject2 ready;
            int times = 0;
            while (true) {
                AvikLogger.info("Error!!! Please connect first");
                mUtils.sleep(AvikConstants.SHORTWAIT);
                try{
                    ready = mDevice.findObject(By.res("win.lioil.bluetooth:id/ac_instruct"));
                    if(ready.getText().equals("ready") && times <=50){
                        break;
                    }else
                        continue;
                } catch(NullPointerException ignored){
                }
                times++;
            }
            if (!tipsObj.exists() || tipsObj.getText().length() < 10) {
                AvikLogger.info("Wait too long. Error end!!! Please connect first");
                return;
            }
            deleteScreensInWatch();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                AvikLogger.info("-----To take screenshot: " + entry.getValue());
                UiObject2 snapShotCommObj = mDevice.findObject(By.res("win.lioil.bluetooth:id/ac_instruct"));
                snapShotCommObj.clear();

                if(entry.getKey().equals("imperial-13")){
                    snapShotCommObj.setText("AT^SPT=remind[km:10]");
                    mUtils.sleep(500L);
                    mDevice.pressBack();
                    mUtils.sleep(500L);
                    mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/btn_sendMsg"))),1000L).click();
                    mUtils.sleep(500L);
                    snapShotCommObj.setText("00AT^SNAPSHOT");
                    mUtils.sleep(500L);
                    mDevice.pressBack();
                    mUtils.sleep(500L);
                    mDevice.wait(Until.findObject(By.res(Pattern.compile(".*:id/btn_sendMsg"))),1000L).click();
                    mUtils.sleep(1000L);
                    mDevice.findObject(By.res("win.lioil.bluetooth:id/btn_clearLog")).click();
                    continue;
                }
                // switch (entry.getKey()){
                //     case "stamina-3" -> runCommandBeforeCapture("AT^ALGOCTL=1=25");
                //     case "stamina-4" -> runCommandBeforeCapture("AT^ALGOCTL=2=25");
                //     case "stamina-5" -> runCommandBeforeCapture("AT^ALGOCTL=3=25");

                //     case "stamina-6" -> runCommandBeforeCapture("AT^ALGOCTL=5=21");
                //     case "stamina-7" -> runCommandBeforeCapture("AT^ALGOCTL=40=21");
                //     case "stamina-8" -> runCommandBeforeCapture("AT^ALGOCTL=45=21");
                //     case "stamina-9" -> runCommandBeforeCapture("AT^ALGOCTL=50=21");
                //     case "stamina-10" -> runCommandBeforeCapture("AT^ALGOCTL=60=21");
                //     case "stamina-11" -> runCommandBeforeCapture("AT^ALGOCTL=70=21");

                //     case "stamina-12" -> runCommandBeforeCapture("AT^ALGOCTL=1=23");
                //     case "stamina-13" -> runCommandBeforeCapture("AT^ALGOCTL=120=23");
                //     case "stamina-14" -> runCommandBeforeCapture("AT^ALGOCTL=3000=23");
                //     case "stamina-15" -> runCommandBeforeCapture("AT^ALGOCTL=6000=23");

                //     case "stamina-16" -> runCommandBeforeCapture("AT^ALGOCTL=5=22");
                //     case "stamina-17" -> runCommandBeforeCapture("AT^ALGOCTL=20=22");
                //     case "stamina-18" -> runCommandBeforeCapture("AT^ALGOCTL=35=22");
                //     case "stamina-19" -> runCommandBeforeCapture("AT^ALGOCTL=50=22");
                //     case "stamina-20" -> runCommandBeforeCapture("AT^ALGOCTL=70=22");
                // }
                snapShotCommObj.setText("00AT^SNAPSHOT");
                //SendCommand
                AvikLogger.info("Wait for clicking SendMes button.......");
                waitTimeOut("SNAPSHOT OK");
            }

        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
        } finally {
            exportFiles();
            saveScreens();

            //delete file
            deleteScreensInWatch();
            deleteScreensInMobile();
        }

    }

    private void deleteScreensInWatch() {
        //delete file
        mDevice.findObject(By.res("win.lioil.bluetooth:id/btn_deleteFile")).click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        //Delete file
        mDevice.findObjects(By.res("android:id/text1")).get(1).click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mDevice.findObject(By.res("win.lioil.bluetooth:id/ac_delete_custom_userInput")).setText("user/snapshot/");
        mDevice.findObject(By.res("win.lioil.bluetooth:id/bt_delete_custom_sure")).click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
    }

    /**
     * Delete screen in mobile device
     */
    private void deleteScreensInMobile() {
        String path = Environment.getExternalStorageDirectory() + "/log/GearLog/Juste/";
        File folder = new File(path);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        } else {
            AvikLogger.info("Fold not exit");
        }
    }

    /**
     * Export screens from watch to mobile device
     *
     * @throws UiObjectNotFoundException
     */
    private void exportFiles() {
        AvikLogger.info("Exporting.......");
        //export file
        mDevice.findObject(By.res("win.lioil.bluetooth:id/btn_exportFile")).click();
        mUtils.sleep(AvikConstants.TINYWAIT);
        //export dir
        mDevice.findObjects(By.res("android:id/text1")).get(1).click();
        mUtils.sleep(AvikConstants.SHORTWAIT);
        mDevice.findObject(By.res("win.lioil.bluetooth:id/ac_export_custom_userInput")).setText("user/snapshot");
        mDevice.findObject(By.res("win.lioil.bluetooth:id/bt_export_custom_sure")).click();
        mUtils.sleep(AvikConstants.TINYWAIT);
        try {
            waitTimeOut("---");
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
        }
    }

    /**
     * Save the screens into database
     */
    private void saveScreens() {
        String path = Environment.getExternalStorageDirectory() + "/log/GearLog/Juste/";
        File folder = new File(path);
        if (folder.exists()) {
            int i = 0;
            File[] files = folder.listFiles();
            List<File> fileList = sort(files);
            int length = fileList.size();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String screenPath = path + fileList.get(length - i - 1).getName();
                Bitmap bitmap = BitmapFactory.decodeFile(screenPath);
                mDevice.takeAvikScreenshot(entry.getKey(), bitmap);
                i++;
            }

        } else {
            AvikLogger.info("Fold not exit");
        }
    }

    /**
     * Sort the screens
     *
     * @param files
     * @return
     */
    private List<File> sort(File[] files) {
        return Arrays.stream(files)
                .sorted(Comparator.comparing(file -> {
                    try {
                        return Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }))
                .collect(Collectors.toList());
    }

    private void waitTimeOut(String strExp) throws UiObjectNotFoundException {
        //clear log
        mDevice.findObject(By.res("win.lioil.bluetooth:id/btn_clearLog")).click();
        mUtils.sleep(AvikConstants.TINYWAIT);
        UiObject tvlog = mDevice.findObject(new UiSelector().resourceId("win.lioil.bluetooth:id/tv_log"));
        long startTime = SystemClock.currentThreadTimeMillis();
        long endTime = startTime;
        long waitTime = 2 * 60 * 1000L;
        if (strExp.equals("---"))
            waitTime = 4 * 60 * 1000L;
        while (endTime - startTime < waitTime && (tvlog.getText() == null || !tvlog.getText().contains(strExp))) {
            mUtils.sleep(AvikConstants.SHORTWAIT);
            endTime = SystemClock.currentThreadTimeMillis();
        }
        //clear log
        mDevice.findObject(By.res("win.lioil.bluetooth:id/btn_clearLog")).click();
        mUtils.sleep(AvikConstants.TINYWAIT);
    }
}