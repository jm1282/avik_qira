package avik.qira_v2.voicevalidation.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Accumulates one result per scenario and writes the run manifest to
 * {@code /sdcard/avik/voicevalidation/output/&lt;tab&gt;_manifest.json}.
 *
 * <p>The manifest is the authoritative scenario &rarr; folder binding the host
 * side ({@code vv_fill_results.py}) consumes to know which traceability folder
 * (and therefore which SSML/audio) belongs to which workbook row. On-screen
 * response text is captured too, as a cross-check for the SSML-derived output.
 */
public final class VoiceValidationManifest {

    public static final String DEFAULT_OUTPUT_DIR = "/sdcard/avik/voicevalidation/output";

    private final String tab;
    private final String outputDir;
    private final Logger logger;
    private final JSONObject root = new JSONObject();
    private final JSONArray results = new JSONArray();

    public VoiceValidationManifest(String tab, String outputDir, VoiceValidationInput input,
            String deviceInfo, Logger logger) {
        this.tab = tab;
        this.outputDir = outputDir == null ? DEFAULT_OUTPUT_DIR : outputDir;
        this.logger = logger;
        try {
            root.put("tab", tab);
            root.put("responseLanguage", input.getResponseLanguage());
            root.put("appLocale", input.getAppLocale());
            root.put("voiceLocale", input.getVoiceLocale());
            root.put("voiceModelDir", input.getVoiceModelDir());
            root.put("traceLocaleDir", input.getTraceLocaleDir());
            root.put("deviceInfo", deviceInfo == null ? "" : deviceInfo);
            root.put("results", results);
        } catch (Throwable ignored) {
        }
    }

    public void addResult(
            int row,
            String useCase,
            String inputText,
            boolean inputVerified,
            String responseText,
            boolean counterQuestion,
            boolean played,
            long playEpochMs,
            String traceFolder,
            String traceFiles,
            String ssml,
            String ssmlText,
            String status,
            String error) {
        JSONObject r = new JSONObject();
        try {
            r.put("row", row);
            r.put("useCase", useCase);
            r.put("inputText", inputText);
            r.put("inputVerified", inputVerified);
            r.put("responseText", responseText == null ? "" : responseText);
            r.put("counterQuestion", counterQuestion);
            r.put("played", played);
            r.put("playEpochMs", playEpochMs);
            r.put("traceFolder", traceFolder == null ? "" : traceFolder);
            r.put("traceFiles", traceFiles == null ? "" : traceFiles);
            r.put("ssml", ssml == null ? "" : ssml);
            r.put("ssmlText", ssmlText == null ? "" : ssmlText);
            r.put("status", status == null ? "" : status);
            r.put("error", error == null ? "" : error);
        } catch (Throwable ignored) {
        }
        results.put(r);
        // Persist after every scenario so a mid-run failure still leaves a
        // usable partial manifest for the host side to pull.
        write();
    }

    public void write() {
        try {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, tab + "_manifest.json");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(root.toString(2).getBytes(StandardCharsets.UTF_8));
            }
            if (logger != null) {
                logger.info("VoiceValidation: manifest written -> " + file.getAbsolutePath());
            }
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("VoiceValidation: manifest write failed: " + t.getMessage());
            }
        }
    }
}
