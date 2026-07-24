package avik.qira_v2.voicevalidation.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsed per-locale Voice Validation input, produced on the host by
 * {@code vv_export_inputs.py} from the QA Excel workbook and pushed to the
 * device at {@code /sdcard/avik/voicevalidation/input/&lt;tab&gt;.json}.
 *
 * <p>The workbook is the single source of truth for the localized prompt text.
 * Keeping the whole locale contract (response-language label, TTS voice locale,
 * counter-question affirmative, and the exact scenario rows) in one JSON keeps
 * the instrumentation free of hard-coded, locale-specific string tables.
 */
public final class VoiceValidationInput {

    /** Default TTS model directory produced by the Ava-Preview debug voice. */
    public static final String DEFAULT_VOICE_MODEL_DIR =
            "en-US-Ava-preview_DragonHDLatestNeural";

    /** Default on-device root the debug voice writes traceability folders to. */
    public static final String DEFAULT_TRACE_BASE = "/sdcard/Download/Voice Traceability";

    private final String tab;
    private final String responseLanguage;
    private final String appLocale;
    private final String voiceLocale;
    private final String affirmative;
    private final String voiceModelDir;
    private final String traceBase;
    private final List<String> languageCandidates;
    private final List<Scenario> scenarios;

    private VoiceValidationInput(
            String tab,
            String responseLanguage,
            String appLocale,
            String voiceLocale,
            String affirmative,
            String voiceModelDir,
            String traceBase,
            List<String> languageCandidates,
            List<Scenario> scenarios) {
        this.tab = tab;
        this.responseLanguage = responseLanguage;
        this.appLocale = appLocale;
        this.voiceLocale = voiceLocale;
        this.affirmative = affirmative;
        this.voiceModelDir = voiceModelDir;
        this.traceBase = traceBase;
        this.languageCandidates = languageCandidates;
        this.scenarios = scenarios;
    }

    public static VoiceValidationInput fromFile(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalStateException("Voice Validation input not found: " + path
                    + ". Run vv_export_inputs.py and push it to the device first.");
        }
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream in = new FileInputStream(file)) {
            int read = 0;
            while (read < bytes.length) {
                int n = in.read(bytes, read, bytes.length - read);
                if (n < 0) {
                    break;
                }
                read += n;
            }
        }
        return fromJson(new String(bytes, StandardCharsets.UTF_8));
    }

    public static VoiceValidationInput fromJson(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        String tab = root.optString("tab", "");
        String responseLanguage = root.optString("responseLanguage", "");
        String appLocale = root.optString("appLocale", "");
        String voiceLocale = root.optString("voiceLocale", appLocale);
        String affirmative = root.optString("affirmative", "yes");
        String voiceModelDir = root.optString("voiceModelDir", DEFAULT_VOICE_MODEL_DIR);
        String traceBase = root.optString("traceBase", DEFAULT_TRACE_BASE);

        // Candidate labels used to find/verify the response-language row across
        // both an English UI (English name) and a localized UI (native autonym /
        // keyword). Falls back to the English responseLanguage label.
        List<String> languageCandidates = new ArrayList<>();
        JSONArray cand = root.optJSONArray("languageCandidates");
        if (cand != null) {
            for (int i = 0; i < cand.length(); i++) {
                String c = cand.optString(i, "").trim();
                if (!c.isEmpty()) {
                    languageCandidates.add(c);
                }
            }
        }
        if (languageCandidates.isEmpty() && !responseLanguage.trim().isEmpty()) {
            languageCandidates.add(responseLanguage.trim());
        }

        List<Scenario> scenarios = new ArrayList<>();
        JSONArray arr = root.optJSONArray("scenarios");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject s = arr.getJSONObject(i);
                String inputText = s.optString("inputText", "");
                if (inputText.trim().isEmpty()) {
                    // A scenario with no localized prompt cannot drive a chat;
                    // skip it rather than sending an empty query.
                    continue;
                }
                scenarios.add(new Scenario(
                        s.optInt("row", -1),
                        s.optString("useCase", ""),
                        inputText,
                        s.optString("criteria", "")));
            }
        }
        if (scenarios.isEmpty()) {
            throw new IllegalStateException(
                    "Voice Validation input for tab '" + tab + "' has no usable scenarios.");
        }
        return new VoiceValidationInput(tab, responseLanguage, appLocale, voiceLocale,
                affirmative, voiceModelDir, traceBase, languageCandidates, scenarios);
    }

    public String getTab() {
        return tab;
    }

    public String getResponseLanguage() {
        return responseLanguage;
    }

    public String getAppLocale() {
        return appLocale;
    }

    public String getVoiceLocale() {
        return voiceLocale;
    }

    public String getAffirmative() {
        return affirmative;
    }

    /** Ordered candidate labels/keywords to match the response-language row. */
    public List<String> getLanguageCandidates() {
        return languageCandidates;
    }

    public String getVoiceModelDir() {
        return voiceModelDir;
    }

    public String getTraceBase() {
        return traceBase;
    }

    /** Absolute device path of the folder the debug voice fills for this locale. */
    public String getTraceLocaleDir() {
        return traceBase + "/" + voiceModelDir + "/" + voiceLocale;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    /** A single QA workbook row: one use case with one localized prompt. */
    public static final class Scenario {
        private final int row;
        private final String useCase;
        private final String inputText;
        private final String criteria;

        Scenario(int row, String useCase, String inputText, String criteria) {
            this.row = row;
            this.useCase = useCase;
            this.inputText = inputText;
            this.criteria = criteria;
        }

        public int getRow() {
            return row;
        }

        public String getUseCase() {
            return useCase;
        }

        public String getInputText() {
            return inputText;
        }

        public String getCriteria() {
            return criteria;
        }
    }
}
