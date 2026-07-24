package avik.qira_v2.voicevalidation.scripts;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira_v2.utils.QiraV2App;
import avik.qira_v2.utils.QiraV2CaptureArtifacts;
import avik.qira_v2.utils.QiraV2InstrumentationDefaults;
import avik.qira_v2.voicevalidation.pages.QiraV2ChatPage;
import avik.qira_v2.voicevalidation.pages.QiraV2VoiceSettingsPage;
import avik.qira_v2.voicevalidation.utils.VoiceSsmlCapture;
import avik.qira_v2.voicevalidation.utils.VoiceTraceabilitySink;
import avik.qira_v2.voicevalidation.utils.VoiceValidationInput;
import avik.qira_v2.voicevalidation.utils.VoiceValidationManifest;

/**
 * Voice Validation driver for one locale tab of the QA workbook.
 *
 * <p>For the tab named by {@code -e vv.tab &lt;tab&gt;} it:
 * <ol>
 *   <li>reads the localized prompts pushed to
 *       {@code /sdcard/avik/voicevalidation/input/&lt;tab&gt;.json};</li>
 *   <li>selects the {@code Ava-Preview} debug voice and Qira's response language
 *       (handling the model-download gate and verifying selection);</li>
 *   <li>for every scenario: starts a new chat, sends the exact prompt, answers a
 *       counter-question if asked, taps Play, and binds the freshly-created
 *       traceability folder (Log + SSML + Audio) to that row;</li>
 *   <li>captures a qira_v2 SLAP screenshot per response (appId=qira);</li>
 *   <li>writes a manifest the host uses to fill the workbook's Output Text.</li>
 * </ol>
 *
 * <p>The device system locale is switched to the tab locale by the host driver
 * (reboot required) before this runs; this script does not clear Qira data so
 * the onboarded state and settings survive.
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraVoiceValidation {

    private static final long RESPONSE_TIMEOUT_MS = 150000L;
    private static final long SSML_TIMEOUT_MS = 25000L;
    // Qira gates tool calls (weather/catch-me-up) behind chained confirmations;
    // reply affirmatively up to this many times to reach the spoken answer.
    private static final int MAX_COUNTER_REPLIES = 3;
    // Time to wait for the trace folder DIR to appear after Play. The files
    // inside can take minutes to finish; that completeness wait is done host-side
    // (vv_fill_results). Override with -e vv.folderTimeoutMs <ms>.
    private static final long FOLDER_TIMEOUT_MS = 45000L;

    private long folderTimeoutMs = FOLDER_TIMEOUT_MS;

    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();

    protected final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();
    private UiDevice device;
    private AvikUtility utils;
    private QiraConfig config;
    private QiraV2App app;

    @BeforeClass
    public static void applyQiraV2Defaults() {
        QiraV2InstrumentationDefaults.apply();
    }

    @Before
    public void setUp() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        utils = AvikUtility.getInstance();
        config = QiraConfig.fromInstrumentation();
        app = new QiraV2App(device, utils, config);

        runShell("input keyevent KEYCODE_WAKEUP");
        runShell("wm dismiss-keyguard");
        runShell("svc power stayon true");

        try {
            QiraStrings.getInstance();
            QiraStrings.getInstance().onLocaleMayHaveChanged();
            QiraStrings.getInstance().enableRuntimeResourceScan();
        } catch (Throwable t) {
            logger.info("VoiceValidation: QiraStrings bootstrap failed (continuing): "
                    + t.getMessage());
        }
    }

    @Test
    public void testMain() {
        try {
            runVoiceValidation();
        } catch (Exception e) {
            utils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }

    private void runVoiceValidation() throws Exception {
        String tab = arg("vv.tab", "zh_CN");
        String inputDir = arg("vv.inputDir", "/sdcard/avik/voicevalidation/input");
        String outputDir = arg("vv.outputDir", VoiceValidationManifest.DEFAULT_OUTPUT_DIR);
        folderTimeoutMs = argLong("vv.folderTimeoutMs", FOLDER_TIMEOUT_MS);

        VoiceValidationInput input = VoiceValidationInput.fromFile(inputDir + "/" + tab + ".json");
        logger.info("VoiceValidation: tab=" + tab
                + " responseLanguage=" + input.getResponseLanguage()
                + " voiceLocale=" + input.getVoiceLocale()
                + " scenarios=" + input.getScenarios().size()
                + " traceDir=" + input.getTraceLocaleDir());

        // Force-stop first so Qira starts from a clean top-level surface instead
        // of restoring whatever deep screen it was last on.
        runShell("am force-stop " + config.getPackageName());
        utils.sleep(1200L);
        app.launch();
        try {
            QiraStrings.getInstance().onLocaleMayHaveChanged();
        } catch (Throwable ignored) {
        }

        // --- One-time setup: voice + response language (with download gate) ---
        QiraV2VoiceSettingsPage settings = new QiraV2VoiceSettingsPage(device, config);
        settings.openSettings();
        settings.setVoiceAvaPreview();
        settings.setResponseLanguage(input.getLanguageCandidates());
        // Relaunch to a clean chat entry surface. Settings has no bubble bar to
        // navigate home from, so restart Qira to reach the composer reliably.
        runShell("am force-stop " + config.getPackageName());
        utils.sleep(1200L);
        app.launch();
        try {
            QiraStrings.getInstance().onLocaleMayHaveChanged();
        } catch (Throwable ignored) {
        }

        String deviceInfo = readDeviceInfo();
        VoiceValidationManifest manifest =
                new VoiceValidationManifest(tab, outputDir, input, deviceInfo, logger);
        VoiceTraceabilitySink sink =
                new VoiceTraceabilitySink(device, logger, input.getTraceLocaleDir());
        QiraV2ChatPage chat = new QiraV2ChatPage(device, config);

        for (VoiceValidationInput.Scenario scenario : input.getScenarios()) {
            runScenario(tab, input, chat, sink, manifest, scenario);
        }

        manifest.write();
        logger.info("VoiceValidation: completed tab=" + tab
                + " (" + input.getScenarios().size() + " scenarios).");
    }

    private void runScenario(
            String tab,
            VoiceValidationInput input,
            QiraV2ChatPage chat,
            VoiceTraceabilitySink sink,
            VoiceValidationManifest manifest,
            VoiceValidationInput.Scenario scenario) {
        int row = scenario.getRow();
        String useCase = scenario.getUseCase();
        String inputText = scenario.getInputText();
        logger.info("VoiceValidation: scenario row=" + row + " useCase='" + useCase
                + "' input='" + preview(inputText) + "'");
        try {
            chat.openChatAndStartNew();
            chat.enterQueryAndSend(inputText);
            // Verify the exact prompt (special characters/symbols included) rendered.
            boolean inputVerified = chat.isTextDisplayed(inputText);
            if (!inputVerified) {
                logger.info("VoiceValidation: WARNING exact input not found on screen for row "
                        + row + " (paste may have altered it).");
            }

            QiraV2ChatPage.ResponseState state = chat.waitForResponseComplete(RESPONSE_TIMEOUT_MS);
            boolean counterQuestion = false;
            for (int reply = 0; reply < MAX_COUNTER_REPLIES
                    && state == QiraV2ChatPage.ResponseState.COMPLETE
                    && chat.isCounterQuestion(); reply++) {
                counterQuestion = true;
                logger.info("VoiceValidation: counter-question detected; replying '"
                        + input.getAffirmative() + "' (" + (reply + 1) + "/"
                        + MAX_COUNTER_REPLIES + ").");
                chat.replyAndSend(input.getAffirmative());
                state = chat.waitForResponseComplete(RESPONSE_TIMEOUT_MS);
            }

            String responseText = chat.getResponseBlob();

            if (state == QiraV2ChatPage.ResponseState.ERROR) {
                manifest.addResult(row, useCase, inputText, inputVerified, responseText,
                        counterQuestion, false, 0L, null, "", "", "", "Error", "response error card");
                return;
            }
            if (state == QiraV2ChatPage.ResponseState.TIMEOUT) {
                manifest.addResult(row, useCase, inputText, inputVerified, responseText,
                        counterQuestion, false, 0L, null, "", "", "", "Timeout",
                        "response did not complete in time");
                return;
            }

            // Clear logcat, tap Play, then capture the SSML the debug build emits
            // for the read-aloud request (authoritative Output Text source).
            VoiceSsmlCapture.clear(device);
            Set<String> before = sink.snapshot();
            long playTs = System.currentTimeMillis();
            boolean played = chat.tapPlayLatest();
            String ssml = played ? VoiceSsmlCapture.capture(device, SSML_TIMEOUT_MS, logger) : null;
            String ssmlText = VoiceSsmlCapture.extractText(ssml);
            // Bind the trace folder (files inside can take minutes; the host does
            // the completeness wait). Only present when the trace-write flag is on.
            String folder = played ? sink.detectNewFolder(before, folderTimeoutMs) : null;
            String files = folder != null ? sink.listFiles(folder) : "";

            try {
                QiraV2CaptureArtifacts.captureSlapScreenshot(
                        avikHandler, device, utils, config, logger, screenName(tab, row, useCase));
            } catch (Throwable t) {
                logger.info("VoiceValidation: SLAP screenshot failed for row " + row
                        + " (continuing): " + t.getMessage());
            }

            boolean gotOutput = ssmlText != null && !ssmlText.isEmpty();
            String status = gotOutput ? "Pass" : (played ? "NoSsml" : "NoPlay");
            String error = gotOutput ? ""
                    : (played ? "Play started but no SSML captured from logcat"
                    : "Play control not found on the response");
            manifest.addResult(row, useCase, inputText, inputVerified, responseText,
                    counterQuestion, played, played ? playTs : 0L, folder, files, ssml, ssmlText,
                    status, error);
        } catch (Throwable t) {
            logger.info("VoiceValidation: scenario row=" + row + " failed: " + t.getMessage());
            manifest.addResult(row, useCase, inputText, false, "", false, false, 0L, null, "", "",
                    "", "Fail", String.valueOf(t.getMessage()));
        }
    }

    @After
    public void tearDown() {
        logger.info("VoiceValidation: run complete; leaving Qira visible for inspection.");
    }

    private String screenName(String tab, int row, String useCase) {
        return String.format(Locale.US, "MotorolaQiraVoiceValidation_%s_row%d_%s",
                tab, row, sanitize(useCase));
    }

    private static String sanitize(String s) {
        if (s == null || s.isEmpty()) {
            return "scenario";
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
            sb.append(ok ? c : '_');
        }
        return sb.toString();
    }

    private static String preview(String s) {
        if (s == null) {
            return "";
        }
        String t = s.replace('\n', ' ');
        return t.length() > 40 ? t.substring(0, 40) + "..." : t;
    }

    private String readDeviceInfo() {
        String model = readShell("getprop ro.product.model");
        String release = readShell("getprop ro.build.version.release");
        String version = readShell("getprop ro.vendor.build.version.incremental");
        if (version == null || version.isEmpty()) {
            version = readShell("getprop ro.build.display.id");
        }
        return "Device- " + safe(model) + " // Release - " + safe(release)
                + " // Version- " + safe(version);
    }

    private String arg(String key, String def) {
        Bundle args = InstrumentationRegistry.getArguments();
        if (args == null) {
            return def;
        }
        String value = args.getString(key);
        return (value == null || value.trim().isEmpty()) ? def : value.trim();
    }

    private long argLong(String key, long def) {
        try {
            String v = arg(key, "");
            return v.isEmpty() ? def : Long.parseLong(v);
        } catch (Throwable t) {
            return def;
        }
    }

    private void runShell(String cmd) {
        try {
            device.executeShellCommand(cmd);
        } catch (Throwable ignored) {
        }
    }

    private String readShell(String cmd) {
        try {
            String out = device.executeShellCommand(cmd);
            return out == null ? "" : out.trim();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
