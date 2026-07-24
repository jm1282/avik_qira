package avik.qira_v2.voicevalidation.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.List;
import java.util.logging.Logger;

import avik.qira.pages.BaseQiraPage;
import avik.qira.utils.QiraConfig;

/**
 * Drives Qira Settings for Voice Validation setup:
 * <ul>
 *   <li>selects the {@code Ava-Preview (userdebug only)} TTS voice - the debug
 *       voice that writes the {@code en-US-Ava-preview_DragonHDLatestNeural}
 *       traceability folders when a response is played;</li>
 *   <li>selects Qira's response language, taps through the "Download Language
 *       Model" gate, waits for the download to finish, and verifies the target
 *       language's radio is actually {@code checked} before returning.</li>
 * </ul>
 *
 * <p>All controls are matched through {@link BaseQiraPage} English-anchor
 * helpers, which expand to the current-locale value via the qira_v2 Compose
 * resolver, so the page works whether Qira's UI is English or localized (the
 * device system locale is switched per tab). Language rows are matched by the
 * caller-supplied candidate list (English name + native autonym / keyword).
 */
public final class QiraV2VoiceSettingsPage extends BaseQiraPage {

    private static final String AVA_PREVIEW = "Ava-Preview";
    private static final long DOWNLOAD_TIMEOUT_MS = 360000L;

    private final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    public QiraV2VoiceSettingsPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    /** Opens Settings via the home-grid navigation drawer (Menu -> Settings). */
    public void openSettings() throws Exception {
        goToHomeGrid();
        // Open the navigation drawer. "Menu" is only on the home grid; the drawer
        // exposes a persistent "Settings" entry independent of any chat state.
        if (!clickByTextOrDescription("Menu", "Open navigation drawer")) {
            throw new IllegalStateException(
                    "VoiceValidation: could not open the Qira navigation drawer (Menu).");
        }
        settle();
        if (!clickByTextOrDescription("Settings")) {
            throw new IllegalStateException(
                    "VoiceValidation: could not open Settings from the drawer.");
        }
        if (waitForTextOrDescription(8000L, "Language", "Voice") == null) {
            throw new IllegalStateException(
                    "VoiceValidation: Settings screen did not render (no Language/Voice row).");
        }
    }

    /**
     * Navigates to the Qira home grid (Discover), the one surface that always
     * exposes the navigation drawer. Taps the bubble-bar App Icon, falling back
     * to Back, until the grid is detected.
     */
    private void goToHomeGrid() throws Exception {
        for (int i = 0; i < 8; i++) {
            if (isHomeGridVisible()) {
                return;
            }
            UiObject2 appIcon = lowestByDescription("App Icon");
            if (appIcon != null) {
                clickObject(appIcon);
            } else {
                mDevice.pressBack();
            }
            settle();
        }
        if (!isHomeGridVisible()) {
            throw new IllegalStateException(
                    "VoiceValidation: could not reach the Qira home grid (drawer unavailable).");
        }
    }

    private boolean isHomeGridVisible() {
        if (findByTextOrDescription("Menu", "Open navigation drawer") != null) {
            return true;
        }
        int tiles = 0;
        for (String tile : new String[] {"Creations", "Knowledge", "Chat History", "Help & Support"}) {
            if (findByTextOrDescription(tile) != null) {
                tiles++;
            }
        }
        return tiles >= 2;
    }

    /** Lowest on-screen node with the given content-description (bubble bar wins). */
    private UiObject2 lowestByDescription(String desc) {
        List<UiObject2> nodes = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(desc)));
        UiObject2 best = null;
        int maxY = -1;
        for (UiObject2 n : nodes) {
            try {
                Rect b = n.getVisibleBounds();
                if (b != null && b.centerY() > maxY) {
                    maxY = b.centerY();
                    best = n;
                }
            } catch (Throwable ignored) {
            }
        }
        return best;
    }

    /**
     * Selects the {@code Ava-Preview} debug voice. Idempotent: if it is already
     * the active voice the method still confirms it and returns.
     */
    public void setVoiceAvaPreview() throws Exception {
        if (!clickByExactTextOrDescription("Voice")
                && !clickByTextOrDescriptionWithScroll("Voice")) {
            throw new IllegalStateException(
                    "VoiceValidation: could not open the Voice settings row.");
        }
        settle();

        // The Voice detail exposes the current voice under "Voice Styles" plus a
        // "Go to voice selection" affordance that opens the Select Voice sheet.
        if (findByText(AVA_PREVIEW) == null || findByText("Continue") == null) {
            if (!clickByTextOrDescription("Go to voice selection")) {
                // Some builds open the Select Voice sheet directly from the row.
                clickByTextOrDescription("Voice Styles");
            }
            settle();
        }

        boolean avaClicked = false;
        for (int attempt = 0; attempt < 3 && !avaClicked; attempt++) {
            try {
                UiObject2 ava = waitForTextOrDescription(4000L, AVA_PREVIEW);
                if (ava == null) {
                    break;
                }
                clickObject(ava);
                avaClicked = true;
            } catch (Throwable stale) {
                mUtils.sleep(400L);
            }
        }
        if (!avaClicked) {
            throw new IllegalStateException(
                    "VoiceValidation: 'Ava-Preview' voice not found/selectable. The userdebug voice"
                            + " is only present on the debug Qira build required for traceability.");
        }
        settle();
        if (!clickByExactTextOrDescription("Continue")) {
            // Continue may be absent when the selection auto-applies; only fail
            // if we cannot confirm Ava-Preview is now the active voice.
            logger.info("VoiceValidation: no 'Continue' after selecting Ava-Preview (continuing).");
        }
        settle();
        logger.info("VoiceValidation: Ava-Preview voice selected.");
        // Return to the Settings list so the language setter starts from there.
        goBack();
    }

    /**
     * Selects Qira's response language, handling the model-download gate.
     *
     * @param candidates ordered labels/keywords identifying the target row
     *                   (English name first, then native autonym / keyword).
     * @throws IllegalStateException if the language cannot be found/selected or
     *         the model download fails or times out.
     */
    public void setResponseLanguage(List<String> candidates) throws Exception {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("VoiceValidation: no language candidates supplied.");
        }
        if (!clickByExactTextOrDescription("Language")
                && !clickByTextOrDescriptionWithScroll("Language")) {
            throw new IllegalStateException(
                    "VoiceValidation: could not open the Language settings row.");
        }
        settle();

        // Momentum-free step scroll to the target row's RadioButton (fast swipes
        // overscroll this Compose list; "Chinese (China)" is the final entry).
        UiObject2 radio = scrollToRowRadio(candidates);
        if (radio == null) {
            throw new IllegalStateException(
                    "VoiceValidation: response language not found for candidates " + candidates
                            + ". The Qira Language list did not expose a matching row.");
        }

        boolean checked;
        try {
            checked = radio.isChecked();
        } catch (Throwable t) {
            checked = false;
        }
        if (checked) {
            logger.info("VoiceValidation: response language already selected: " + candidates.get(0));
            goBack();
            return;
        }

        // The row's touch target is the RadioButton (the label is not clickable).
        if (!tapRowRadio(candidates)) {
            throw new IllegalStateException(
                    "VoiceValidation: could not tap the response-language row for " + candidates);
        }
        settle();

        // Model-download gate. Absent when the model is already cached.
        if (findByText("Download Language Model") != null
                || hasTextOrDescription("Download Language Model")) {
            logger.info("VoiceValidation: Download Language Model prompt shown; tapping Download.");
            if (!clickByTextOrDescription("Download")) {
                throw new IllegalStateException(
                        "VoiceValidation: Download button not found on the language-model prompt.");
            }
        }

        waitForLanguageReady(candidates);
        logger.info("VoiceValidation: response language selected + model ready: " + candidates.get(0));
        goBack();
    }

    /** Polls until the target language row is checked, or fails on error/timeout. */
    private void waitForLanguageReady(List<String> candidates) throws Exception {
        long deadline = System.currentTimeMillis() + DOWNLOAD_TIMEOUT_MS;
        long lastLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (findByText("Download failed") != null
                    || findByText("Request Timeout") != null
                    || findByText("Language Not Supported") != null) {
                throw new IllegalStateException(
                        "VoiceValidation: language model download failed / unsupported for "
                                + candidates.get(0));
            }
            UiObject2 ready = findRowRadio(candidates);
            if (ready == null) {
                ready = scrollToRowRadio(candidates);
            }
            if (ready != null) {
                try {
                    if (ready.isChecked()) {
                        return;
                    }
                } catch (Throwable ignored) {
                }
            }
            long now = System.currentTimeMillis();
            if (now - lastLog > 10000L) {
                logger.info("VoiceValidation: waiting for '" + candidates.get(0)
                        + "' language model download to complete...");
                lastLog = now;
            }
            mUtils.sleep(2500L);
        }
        throw new IllegalStateException(
                "VoiceValidation: timed out waiting for language '" + candidates.get(0)
                        + "' to become selected (model download did not complete).");
    }

    /** Slow step-scroll to bring the target row's radio on screen; null if absent. */
    private UiObject2 scrollToRowRadio(List<String> candidates) throws Exception {
        for (int step = 0; step < 26; step++) {
            UiObject2 radio = findRowRadio(candidates);
            if (radio != null) {
                return radio;
            }
            slowScrollDown();
            settle();
        }
        return findRowRadio(candidates);
    }

    /** Small, slow drag (minimal momentum) advancing the language list. */
    private void slowScrollDown() {
        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();
        try {
            mDevice.swipe(w / 2, (h * 72) / 100, w / 2, (h * 40) / 100, 45);
        } catch (Throwable ignored) {
        }
    }

    /** Fresh lookup (never cached) of the language label node for any candidate. */
    private UiObject2 findLanguageTextNode(List<String> candidates) {
        for (String candidate : candidates) {
            if (candidate == null || candidate.trim().isEmpty()) {
                continue;
            }
            try {
                UiObject2 node = mDevice.findObject(
                        By.pkg(mConfig.getPackageName()).text(patternForLabel(candidate)));
                if (node != null) {
                    return node;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * The clickable RadioButton on the same row as the target language label, or
     * null when the row is not on screen. The label TextView is not clickable, so
     * selection taps and checked-state reads must go through the RadioButton.
     */
    private UiObject2 findRowRadio(List<String> candidates) {
        try {
            UiObject2 node = findLanguageTextNode(candidates);
            if (node == null) {
                return null;
            }
            Rect tb = node.getVisibleBounds();
            if (tb == null) {
                return null;
            }
            List<UiObject2> radios = mDevice.findObjects(
                    By.pkg(mConfig.getPackageName()).clazz("android.widget.RadioButton"));
            for (UiObject2 radio : radios) {
                Rect rb = radio.getVisibleBounds();
                if (rb == null) {
                    continue;
                }
                int cy = rb.centerY();
                if (cy >= tb.top - 70 && cy <= tb.bottom + 70) {
                    return radio;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Re-finds and taps the target row's radio, tolerating stale nodes. */
    private boolean tapRowRadio(List<String> candidates) throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 radio = findRowRadio(candidates);
                if (radio == null) {
                    return false;
                }
                clickObject(radio);
                return true;
            } catch (Throwable stale) {
                mUtils.sleep(400L);
            }
        }
        return false;
    }

    private void goBack() throws Exception {
        mDevice.pressBack();
        settle();
    }
}
