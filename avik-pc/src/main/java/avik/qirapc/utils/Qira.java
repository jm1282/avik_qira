package avik.qirapc.utils;

import com.motorola.g11n.tools.avik.client.win.Application;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import avik.qirapc.pages.ActionBarPage;

public class Qira {

    public static final String APP_PATH = "C:\\Program Files\\Lenovo\\Lenovo Qira\\Lenovo Qira.Launcher.exe";
    public static final String WINDOW = "Qira";
    public static final String LAUNCHER_EXE = "Lenovo Qira.Launcher.exe";
    public static final String CORE_EXE = "LenovoQiraCore.exe";
    public static final String HUB_EXE = "QuantumAI.Hub.exe";

    private static final int MAX_LAUNCH_ATTEMPTS = 2;
    private static final long OVERLAY_SETTLE_WAIT_MS = 5000;
    private static final long POST_FOCUS_ZONE_WAIT_MS = 35000;
    private static final long POLL_INTERVAL_MS = 1000;
    private static final long PROCESS_STOP_TIMEOUT_MS = 15000;
    private static final long PROCESS_START_TIMEOUT_MS = 15000;
    private static final long LAUNCHER_BOOT_WAIT_MS = 30000;
    private static final long RETRY_COOLDOWN_MS = 2000;

    public static void restartQira(Application app, Rectangle desktopRect) throws Exception {
        Exception lastError = null;

        for (int attempt = 1; attempt <= MAX_LAUNCH_ATTEMPTS; attempt++) {
            try {
                stopExistingQira();
                launchQira();
                waitForLauncherOverlay();

                if (!waitForMainWindow(app, 3000)) {
                    openFocusZone(app, desktopRect);
                }

                waitForMainWindowToSettle(app);
                return;
            } catch (Exception e) {
                lastError = e;
                stopExistingQiraQuietly();
                Thread.sleep(RETRY_COOLDOWN_MS);
            }
        }

        throw new IllegalStateException("Unable to launch Lenovo Qira after " + MAX_LAUNCH_ATTEMPTS + " attempts.", lastError);
    }

    public static void killQiraWinProcesses() throws Exception {
        killProcessByName(LAUNCHER_EXE);
        killProcessByName(CORE_EXE);
        killProcessByName(HUB_EXE);
    }

    public static void launchQira() throws Exception {
        if (!Files.exists(Path.of(APP_PATH))) {
            throw new IllegalStateException("Qira launcher not found at: " + APP_PATH);
        }

        new ProcessBuilder(APP_PATH).start();
    }

    public static void waitForLauncherOverlay() throws Exception {
        waitForProcessToStart(LAUNCHER_EXE, PROCESS_START_TIMEOUT_MS);
        Thread.sleep(LAUNCHER_BOOT_WAIT_MS);
        Thread.sleep(OVERLAY_SETTLE_WAIT_MS);
    }

    public static void openFocusZone(Application app, Rectangle desktopRect) throws Exception {
        ActionBarPage actionBarPage = new ActionBarPage(desktopRect);

        actionBarPage.tapFocusZone();

        if (waitForMainWindow(app, POST_FOCUS_ZONE_WAIT_MS)) {
            return;
        }

        throw new IllegalStateException("Focus zone click did not open the Qira main window.");
    }

    public static void waitForMainWindowToSettle(Application app) throws Exception {
        if (!waitForMainWindow(app, POST_FOCUS_ZONE_WAIT_MS)) {
            throw new IllegalStateException("Qira main window did not become ready after opening Focus zone.");
        }

        Thread.sleep(3000);
    }

    public static Rectangle getMainWindowRectangle(Application app) throws Exception {
        Rectangle windowRectangle = getMainWindowRectangleByExe(app, CORE_EXE);

        if (windowRectangle != null) {
            return windowRectangle;
        }

        windowRectangle = getMainWindowRectangleByExe(app, HUB_EXE);
        if (windowRectangle != null) {
            return windowRectangle;
        }

        windowRectangle = getMainWindowRectangleByTitle(app);
        if (windowRectangle != null) {
            return windowRectangle;
        }

        throw new IllegalStateException("Unable to resolve the Qira main window rectangle.");
    }

    private static void stopExistingQira() throws Exception {
        killQiraWinProcesses();
        waitForProcessesToStop(PROCESS_STOP_TIMEOUT_MS, LAUNCHER_EXE, CORE_EXE);
        waitForProcessesToStopQuietly(5000, HUB_EXE);
        Thread.sleep(1500);
    }

    private static void stopExistingQiraQuietly() {
        try {
            stopExistingQira();
        } catch (Exception ignored) {
            // A stale background process should not mask the original launch failure.
        }
    }

    private static void killProcessByName(String executableFile) throws Exception {
        Process process = new ProcessBuilder("taskkill", "/F", "/T", "/IM", executableFile)
                .redirectErrorStream(true)
                .start();
        readProcessOutput(process);
        process.waitFor();
    }

    private static void waitForProcessToStart(String executableFile, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            if (isProcessRunning(executableFile)) {
                return;
            }

            Thread.sleep(POLL_INTERVAL_MS);
        }

        throw new IllegalStateException("Qira process did not start: " + executableFile);
    }

    private static void waitForProcessesToStop(long timeoutMs, String... executableFiles) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            if (!isAnyProcessRunning(executableFiles)) {
                return;
            }

            Thread.sleep(POLL_INTERVAL_MS);
        }

        throw new IllegalStateException("Timed out waiting for Qira processes to stop.");
    }

    private static void waitForProcessesToStopQuietly(long timeoutMs, String... executableFiles) {
        try {
            waitForProcessesToStop(timeoutMs, executableFiles);
        } catch (Exception ignored) {
            // QuantumAI.Hub can linger in the background and should not block a relaunch.
        }
    }

    private static boolean isAnyProcessRunning(String... executableFiles) throws Exception {
        for (String executableFile : executableFiles) {
            if (isProcessRunning(executableFile)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isProcessRunning(String executableFile) throws Exception {
        Process process = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + executableFile, "/FO", "CSV", "/NH")
                .redirectErrorStream(true)
                .start();
        String output = readProcessOutput(process);
        process.waitFor();

        return output.toLowerCase(Locale.ROOT).contains(executableFile.toLowerCase(Locale.ROOT));
    }

    private static String readProcessOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        return output.toString();
    }

    private static boolean waitForMainWindow(Application app, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            if (focusMainWindow(app)) {
                return true;
            }

            Thread.sleep(POLL_INTERVAL_MS);
        }

        return false;
    }

    private static boolean focusMainWindow(Application app) {
        return focusMainWindowByExe(app, CORE_EXE)
                || focusMainWindowByExe(app, HUB_EXE)
                || focusMainWindowByTitle(app);
    }

    private static boolean focusMainWindowByExe(Application app, String executableFile) {
        try {
            app.focusWindowByExeFile(executableFile);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean focusMainWindowByTitle(Application app) {
        try {
            app.focusWindowByTitle(WINDOW);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Rectangle getMainWindowRectangleByExe(Application app, String executableFile) {
        try {
            return app.getWindowRectangleByExeFile(executableFile);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Rectangle getMainWindowRectangleByTitle(Application app) {
        try {
            return app.getWindowRectangleByTitle(WINDOW);
        } catch (Exception ignored) {
            return null;
        }
    }
}
