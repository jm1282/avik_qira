package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

public class QiraSettingsPage extends BaseQiraPage {

    public static final String[] ACCOUNT_OPTION_LABELS = {
            "Account",
            "Konto",
            "Compte",
            "Conta"
    };

    public static final String[] DEVICES_OPTION_LABELS = {
            "Devices",
            "Geräte",
            "Appareils",
            "Dispositivos"
    };

    public static final String[] SMART_CONNECT_OPTION_LABELS = {
            "Smart Connect",
            "SmartConnect",
            "Smart\u00A0Connect"
    };

    public static final String[] LANGUAGE_OPTION_LABELS = {
            "Language",
            "Sprache",
            "Langue",
            "Idioma"
    };

    public static final String[] LAUNCH_OPTIONS_OPTION_LABELS = {
            "Launch Options",
            "Launch options",
            "Startoptionen",
            "Options de lancement",
            "Opções de inicialização",
            // es / es-US: missing entry caused row 79 (LaunchOptions) and 80
            // (Voice) to mis-align on Spanish — the script could not match
            // the row by text and the index fallback drifted by one. Mirrors
            // the verified entry already present in SETTINGS_ANCHORS.
            "Opciones de inicio",
            "Opzioni di avvio",
            "Opcje uruchamiania",
            "Opțiuni de lansare",
            "起動オプション",
            "启动选项"
    };

    public static final String[] VOICE_OPTION_LABELS = {
            "Voice",
            // Qira's own R.string scan resolves "Voice" -> the localized
            // value on every locale that ships a translation, but we keep
            // hardcoded fallbacks so the row tap still works if the scan
            // missed (e.g. Qira shipped the string under a different
            // English value than "Voice"). De-duplication happens later
            // via a LinkedHashSet so co-translated locales (es / pt-BR
            // both render "Voz") cost nothing.
            "Voz",
            "Voix",
            "Voce",
            "Stimme",
            "Głos",
            "音声",
            "语音"
    };

    public static final String[] LOCK_SCREEN_DISPLAY_OPTION_LABELS = {
            "Lock-Screen Display",
            "Lock Screen Display",
            "Lock screen display",
            "Lockscreen display",
            "Sperrbildschirm",
            "Affichage écran verrouillé",
            "Tela de bloqueio"
    };

    public static final String[] SYNC_DATA_OPTION_LABELS = {
            "Sync Data",
            "Sync data",
            "Daten synchronisieren",
            "Synchroniser les données",
            "Sincronizar dados"
    };

    public static final String[] PERSONALIZED_ANSWERS_OPTION_LABELS = {
            "Personalized Answers",
            "Personalised answers",
            "Personalisierte Antworten",
            "Réponses personnalisées",
            "Respostas personalizadas",
            // es / es-US: missing entry caused row 83 to highlight the
            // wrong row (the Spanish capture showed "Sincronizar datos" /
            // "Sincronizo" body, i.e. Sync Data, because the row tap
            // missed and the index fallback drifted). Mirrors the verified
            // entry already present in SETTINGS_ANCHORS.
            "Respuestas personalizadas",
            "Risposte personalizzate",
            "Spersonalizowane odpowiedzi",
            "Răspunsuri personalizate",
            "パーソナライズされた回答",
            "个性化回答"
    };

    public static final String[] CATCH_ME_UP_OPTION_LABELS = {
            "Catch Me Up",
            "Catch me up",
            "Update me",
            "What's new?",
            "Whats new?",
            "Was gibt es Neues?",
            // es / es-US: the user-reported row 84 was offset by one
            // because the Spanish row label was missing here. Use the
            // accent-bearing "día" form Qira itself ships ("Ponerme al
            // día"). Mirrors the verified entry already present in
            // SETTINGS_ANCHORS.
            "Ponerme al día",
            "Quoi de neuf",
            "Quoi de neuf ?",
            "Novidades",
            "Novedades",
            "Novità",
            "Co nowego",
            "新着情報",
            "新功能"
    };

    public static final String[] CONNECTORS_OPTION_LABELS = {
            "Connectors",
            // es / es-US: missing entry caused row 85 to drift to the
            // previous canonical option ("Ponerme al día" / Catch Me Up)
            // body in Spanish. Mirrors the verified entry already present
            // in SETTINGS_ANCHORS.
            "Conectores",
            "Konnektoren",
            "Connecteurs",
            "Connettori",
            "Łączniki",
            "Conectoare",
            "コネクタ",
            "连接器"
    };

    public static final String[] ABOUT_OPTION_LABELS = {
            "About",
            "Info"
    };

    public static final String[] SUPPORT_PAGE_OPTION_LABELS = {
            "Support Page",
            "Support page",
            "Support-Seite"
    };

    public static final String[] LEGAL_NOTICES_OPTION_LABELS = {
            "Legal Notices",
            "Legal notices",
            "Rechtliche Hinweise"
    };

    public static final String[] FEEDBACK_OPTION_LABELS = {
            "Feedback"
    };

    /**
     * Labels that only appear on the Settings master-detail surface and not
     * on the drawer that precedes it or the home tile grid. Using a
     * drawer-unique label like "Smart Connect" / "Launch Options" as the
     * anchor avoids false-positive waits where the drawer's "Settings"
     * label is already visible but the Settings surface has not yet
     * replaced it.
     */
    private static final String[] SETTINGS_ANCHORS = {
            "Smart Connect",
            "SmartConnect",
            "Launch Options",
            "Launch options",
            "Startoptionen",
            "Options de lancement",
            "Opções de inicialização",
            "Opciones de inicio",
            "Opzioni di avvio",
            "Opcje uruchamiania",
            "Opțiuni de lansare",
            "起動オプション",
            "启动选项",
            "Lock-Screen Display",
            "Lock Screen Display",
            "Lock screen display",
            "Lockscreen display",
            "Sperrbildschirm",
            "Affichage écran verrouillé",
            "Tela de bloqueio",
            "Pantalla de bloqueo",
            "Schermata di blocco",
            "Ekran blokady",
            "Ecran de blocare",
            "Personalized Answers",
            "Personalised answers",
            "Personalisierte Antworten",
            "Réponses personnalisées",
            "Respostas personalizadas",
            "Respuestas personalizadas",
            "Risposte personalizzate",
            "Spersonalizowane odpowiedzi",
            "Răspunsuri personalizate",
            "Catch Me Up",
            "Catch me up",
            "Update me",
            "What's new?",
            "Whats new?",
            "Was gibt es Neues?",
            "Quoi de neuf",
            "Novidades",
            "Novedades",
            // es: current Qira build renders the Catch Me Up row as
            // "Ponerme al día" rather than "Novedades", so the
            // surface-detection anchor list needs both forms to avoid a
            // false-negative when the Settings surface has just loaded
            // in Spanish.
            "Ponerme al día",
            "Novità",
            "Co nowego",
            "Connectors",
            "Conectores",
            "Konnektoren",
            "Connettori",
            "Łączniki",
            "Conectoare",
            "Support Page",
            "Support page",
            "Support-Seite",
            "Page d'assistance",
            "Página de soporte",
            "Página de suporte",
            "Pagina di supporto",
            "Strona pomocy",
            "Pagina de asistență",
            "Legal Notices",
            "Legal notices",
            "Rechtliche Hinweise",
            "Mentions légales",
            "Avisos legales",
            "Avisos legais",
            "Note legali",
            "Informacje prawne",
            "Note legale",
            "What do you want to search?"
    };

    /**
     * Section headers shown in the left-hand master pane of the Settings
     * surface, in top-to-bottom order.
     */
    public static final String[] SECTION_HEADERS = {
            "Personal",
            "General",
            "Data Control",
            "Personalization",
            "About",
            "Feedback"
    };

    /**
     * Exact left-pane option labels, in the order they appear on the Settings
     * list. Selecting any of them swaps the right-hand detail pane; the
     * {@code Settings} surface itself is not replaced (it is a master-detail
     * layout on this device form factor).
     */
    public static final String[] OPTION_LABELS = {
            "Account",
            "Devices",
            "Smart Connect",
            "Language",
            "Launch Options",
            "Voice",
            "Lock-Screen Display",
            "Sync Data",
            "Personalized Answers",
            "Catch Me Up",
            "Connectors",
            "About",
            "Support Page",
            "Legal Notices",
            "Feedback"
    };

    public QiraSettingsPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraSettingsPage waitForLoaded() throws Exception {
        long deadline = System.currentTimeMillis() + 15000L;
        while (System.currentTimeMillis() < deadline) {
            UiObject2 anchor = findByTextOrDescription(SETTINGS_ANCHORS);
            if (anchor != null
                    || isSettingsSurfaceVisibleByGeometry()
                    || isSettingsSurfaceVisibleByGeometryLenient()) {
                settle();
                return this;
            }
            mUtils.sleep(250L);
        }
        logSettingsDiag("waitForLoaded timeout");
        throw new IllegalStateException("Unable to detect the Qira settings page");
    }

    /**
     * Lenient geometry check used as a final fallback when neither the
     * localized text anchors nor the strict master-detail layout heuristic
     * match. Locales like es-US can render Settings with wider option labels
     * (e.g. "Configuración") that fall outside the 55%-of-display width cap
     * used by {@link #isSettingsSurfaceVisibleByGeometry()}, and some builds
     * collapse the right-detail pane to a non-clickable container that the
     * strict heuristic no longer counts. This relaxed pass requires only 3
     * left-pane clickable rows in roughly the same vertical band.
     */
    private boolean isSettingsSurfaceVisibleByGeometryLenient() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int leftPaneRows = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 12) / 100 || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100 || bounds.width() > (width * 65) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 2) / 100 || bounds.height() > (height * 16) / 100) {
                    continue;
                }
                if (bounds.centerX() <= (width * 50) / 100) {
                    leftPaneRows++;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return leftPaneRows >= 3;
    }

    private void logSettingsDiag(String marker) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[diag QiraSettingsPage ").append(marker).append("] display=")
                    .append(mDevice.getDisplayWidth()).append("x")
                    .append(mDevice.getDisplayHeight())
                    .append(" visible text/desc: ");
            int count = 0;
            for (UiObject2 obj : mDevice.findObjects(
                    By.pkg(mConfig.getPackageName()))) {
                if (count >= 25) break;
                try {
                    String t = obj.getText();
                    String d = obj.getContentDescription();
                    if ((t != null && !t.isEmpty()) || (d != null && !d.isEmpty())) {
                        Rect b = obj.getVisibleBounds();
                        sb.append("{t=").append(t).append(",d=").append(d);
                        if (b != null) {
                            sb.append(",b=").append(b);
                        }
                        sb.append("} ");
                        count++;
                    }
                } catch (StaleObjectException ignored) {
                }
            }
            SETTINGS_LOGGER.info(sb.toString());
        } catch (Throwable ignored) {
        }
    }

    private static final Logger SETTINGS_LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    public boolean isMasterDetailLayout() {
        return isSettingsSurfaceVisibleByGeometry();
    }

    public boolean waitForOptionList(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isSettingsOptionListVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return isSettingsOptionListVisible();
    }

    public boolean returnToSettingsListIfNeeded(long timeoutMs) throws Exception {
        if (waitForOptionList(800L)) {
            return true;
        }
        if (!tapSettingsDetailBackByGeometry()) {
            mDevice.pressBack();
            settle();
        }
        return waitForOptionList(timeoutMs);
    }

    private boolean isSettingsSurfaceVisibleByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int leftPaneRows = 0;
        int rightPaneRows = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 14) / 100 || bounds.bottom > (height * 94) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 10) / 100 || bounds.width() > (width * 55) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 14) / 100) {
                    continue;
                }
                if (bounds.centerX() <= (width * 48) / 100) {
                    leftPaneRows++;
                } else if (bounds.centerX() >= (width * 58) / 100) {
                    rightPaneRows++;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return leftPaneRows >= 4 && rightPaneRows >= 1;
    }

    public QiraSettingsDetailPage openAccount() throws Exception {
        return openOptionDetail(
                ACCOUNT_OPTION_LABELS,
                "Account",
                "Manage Lenovo Id",
                "Sign Out");
    }

    public QiraSettingsDetailPage openSmartConnect() throws Exception {
        return openOptionDetail(
                SMART_CONNECT_OPTION_LABELS,
                "Smart Connect",
                "Connected Devices",
                "Manage Devices",
                "Cross-Device Actions");
    }

    public QiraSettingsDetailPage openLaunchOptions() throws Exception {
        return openOptionDetail(
                LAUNCH_OPTIONS_OPTION_LABELS,
                "Launch Options",
                "Launch options",
                "Floating Bubble",
                "AI Key",
                "Approach");
    }

    public QiraSettingsDetailPage openCatchMeUpSettings() throws Exception {
        return openOptionDetail(
                CATCH_ME_UP_OPTION_LABELS,
                "Catch Me Up",
                "Catch me up",
                "Update me",
                "What's new?",
                "Whats new?",
                "Clear summarized notifications",
                "Summarization app list",
                "Enable all");
    }

    public boolean scrollToDataControl() throws Exception {
        return selectOption(SYNC_DATA_OPTION_LABELS)
                || selectOption(CATCH_ME_UP_OPTION_LABELS)
                || selectOption(CONNECTORS_OPTION_LABELS);
    }

    /**
     * Maximum number of swipes attempted by {@link #scrollToTop()} or
     * {@link #selectOption(String...)}. The full Settings list contains 15
     * options across 6 sections; in the worst case a swipe reveals roughly
     * half the visible list, so 8 swipes covers the full list with margin
     * even on small phone form factors.
     */
    private static final int MAX_MASTER_SWIPES = 8;

    /**
     * Scrolls the left-hand Settings master pane back to the top by issuing
     * downward swipes restricted to the left half of the display. We do not
     * use {@code UiScrollable.scrollToBeginning} because the Compose
     * master-detail layout exposes multiple scrollables (master pane,
     * detail pane, outer container) and {@code UiScrollable} picks the
     * first one it finds — which is often the wrong pane. Manual swipes
     * give us deterministic control over which pane is moved.
     */
    public boolean scrollToTop() throws Exception {
        for (int i = 0; i < MAX_MASTER_SWIPES; i++) {
            if (isAtTopOfMasterList()) {
                return true;
            }
            if (!swipeMasterPane(false)) {
                return false;
            }
        }
        return isAtTopOfMasterList();
    }

    public boolean scrollCatchMeUpDetailToTop() throws Exception {
        for (int i = 0; i < MAX_MASTER_SWIPES; i++) {
            if (isCatchMeUpDetailAtTop()) {
                return true;
            }
            if (!swipeCatchMeUpDetailTowardTop()) {
                return false;
            }
        }
        return isCatchMeUpDetailAtTop();
    }

    private boolean isCatchMeUpDetailAtTop() {
        UiObject2 title = findByExactText(CATCH_ME_UP_OPTION_LABELS);
        UiObject2 clearNotifications =
                findByExactText("Clear summarized notifications");
        if (title == null || clearNotifications == null) {
            return false;
        }
        try {
            int height = mDevice.getDisplayHeight();
            Rect titleBounds = title.getVisibleBounds();
            Rect clearBounds = clearNotifications.getVisibleBounds();
            return titleBounds != null
                    && !titleBounds.isEmpty()
                    && clearBounds != null
                    && !clearBounds.isEmpty()
                    && titleBounds.top <= (height * 25) / 100
                    && clearBounds.top <= (height * 40) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean swipeCatchMeUpDetailTowardTop() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        Rect swipeBounds = null;
        int bestArea = 0;
        for (UiObject2 scrollable : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).scrollable(true))) {
            try {
                Rect bounds = scrollable.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.height() < (height * 40) / 100) {
                    continue;
                }
                if (isMasterDetailLayout()
                        && bounds.centerX() < (width * 52) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    swipeBounds = new Rect(bounds);
                }
            } catch (StaleObjectException ignored) {
            }
        }

        if (swipeBounds == null) {
            int left = isMasterDetailLayout() ? width / 2 : 0;
            swipeBounds = new Rect(left, (height * 8) / 100,
                    width, (height * 95) / 100);
        }

        int x = swipeBounds.centerX();
        int startY = swipeBounds.top + (swipeBounds.height() * 25) / 100;
        int endY = swipeBounds.top + (swipeBounds.height() * 82) / 100;
        if (startY >= endY) {
            return false;
        }
        mDevice.swipe(x, startY, x, endY, 30);
        settle();
        return true;
    }

    /**
     * Returns {@code true} when the master pane is showing the top of the
     * list. We keep this geometry-only because several localized builds
     * expose section headers that do not resolve cleanly through Qira's
     * string table, while the row geometry is stable across locales.
     */
    private boolean isAtTopOfMasterList() {
        List<UiObject2> rows = findVisibleLeftPaneOptionRows();
        if (rows.isEmpty()) {
            return false;
        }
        try {
            int height = mDevice.getDisplayHeight();
            Rect topRow = rows.get(0).getVisibleBounds();
            return topRow != null
                    && !topRow.isEmpty()
                    && topRow.top >= (height * 30) / 100
                    && topRow.top <= (height * 45) / 100;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Selects an option from the left-hand Settings list, scrolling the list
     * into view if necessary. Returns {@code true} when the option is tapped
     * and the right-hand detail pane re-renders; returns {@code false} if the
     * label cannot be located after exhausting {@link #MAX_MASTER_SWIPES}
     * forward swipes from the top of the master list.
     *
     * <p>This implementation deliberately avoids
     * {@code UiScrollable.scrollTextIntoView} because the Compose
     * master-detail layout exposes multiple scrollables and the legacy
     * helper picked an inconsistent target — observable in the diagnostic
     * dumps where {@code scrollTextIntoView} would either fail outright
     * (Feedback) or succeed at scrolling but report failure (Sync Data).
     */
    public boolean selectOption(String... labels) throws Exception {
        UiObject2 target = findLeftPaneOption(labels);
        if (target != null) {
            clickObject(target);
            return true;
        }

        // Start from a known anchor (top of list) so we always sweep the
        // full master pane in one consistent direction.
        scrollToTop();

        for (int i = 0; i < MAX_MASTER_SWIPES; i++) {
            target = findLeftPaneOption(labels);
            if (target != null) {
                clickObject(target);
                return true;
            }
            if (!swipeMasterPane(true)) {
                break;
            }
        }

        // Last lookup after the final swipe in case the target is at the
        // very bottom of the list.
        target = findLeftPaneOption(labels);
        if (target == null) {
            return false;
        }
        clickObject(target);
        return true;
    }

    public QiraSettingsDetailPage openOptionDetail(String[] optionLabels) throws Exception {
        return openOptionDetail(optionLabels, optionLabels);
    }

    public QiraSettingsDetailPage openOptionDetail(String[] optionLabels,
            String... detailAnchors) throws Exception {
        String[] effectiveAnchors = mergeDetailAnchors(optionLabels, detailAnchors);
        QiraSettingsDetailPage detail = trySelectOptionAndWait(optionLabels, effectiveAnchors);
        if (detail != null) {
            return detail;
        }
        return scanVisibleRowsForDetail(effectiveAnchors);
    }

    /**
     * Hybrid variant that first attempts direct label selection, then falls
     * back to selecting by top-to-bottom position in the left pane (see
     * {@link #OPTION_LABELS} for canonical ordering).
     *
     * <p>This is resilient on locales where option labels are reworded (or no
     * longer map to Qira's {@code R.string} table), which otherwise causes
     * a direct label tap to fail
     * even though the option row is visibly present.
     *
     * @param optionIndex 0-based index in the left master list (fallback path).
     * @param optionLabels known aliases for this option (used by primary path and
     *                     by detail-anchor merge).
     * @param detailAnchors expected right-pane anchors; treated as best-effort.
     */
    public QiraSettingsDetailPage openOptionDetailByPosition(int optionIndex,
            String[] optionLabels, String... detailAnchors) throws Exception {
        String[] effectiveAnchors = mergeDetailAnchors(optionLabels, detailAnchors);

        // Keep a direct label route first so known locales still validate
        // against semantic anchors instead of pure position.
        if (selectOption(optionLabels)) {
            QiraSettingsDetailPage detailByLabel = waitForDetail(effectiveAnchors, 2400L);
            if (detailByLabel != null) {
                return detailByLabel;
            }
            // The row tap succeeded, but detail anchors did not resolve in
            // this locale. Return a lenient detail wrapper so captures still
            // proceed.
            settle();
            return new QiraSettingsDetailPage(mDevice, mConfig);
        }

        if (optionIndex < 0) {
            return null;
        }

        UiObject2 rowByIndex = findLeftPaneOptionByIndex(optionIndex);
        if (rowByIndex != null) {
            clickObject(rowByIndex);
            QiraSettingsDetailPage detail = waitForDetail(effectiveAnchors, 2400L);
            if (detail != null) {
                return detail;
            }
            // On some locales the detail text is fully reworded and does not
            // match any anchor yet. We still return a detail wrapper so the
            // caller can capture the rendered pane instead of skipping.
            settle();
            return new QiraSettingsDetailPage(mDevice, mConfig);
        }

        // Index-based fallback also failed (unexpected layout drift).
        return null;
    }

    /**
     * Result of {@link #verifyBodyTitleAlignment(String[])}. Tri-state so the
     * caller can distinguish between "we have no localized form to check
     * against in this locale" (degrade gracefully) and "we expected the
     * body title to be one of these and it isn't" (worth a single retry).
     */
    public enum BodyTitleCheck {
        /** A localized expected body alias was found in the right pane. */
        MATCH,
        /**
         * Localized aliases exist but none appear in the right pane.
         * Caller should consider one gentle retry, then proceed regardless.
         */
        MISMATCH,
        /**
         * No localized aliases beyond the caller-supplied English literals.
         * Treated as a no-op so unknown locales degrade gracefully — we
         * have nothing to compare against, so we should not fail.
         */
        UNKNOWN_LOCALE
    }

    /**
     * Best-effort guard against the row-misalignment class of bug observed
     * on Spanish in early May 2026, where the left-pane row tap silently
     * picked the wrong row and every subsequent capture was offset by one.
     *
     * <p>After tapping an option we expect the right-pane body to render
     * a header that matches the localized form of {@code expectedAliases}
     * (which are typically the same option labels passed to
     * {@link #openOptionDetailByPosition(int, String[], String...)}). This
     * method scans the right half of the display for any text/desc that
     * exactly matches one of the localized aliases and returns:
     *
     * <ul>
     *   <li>{@link BodyTitleCheck#MATCH} if a match is found</li>
     *   <li>{@link BodyTitleCheck#MISMATCH} if localized aliases exist but
     *       none appear in the right pane (caller should retry once)</li>
     *   <li>{@link BodyTitleCheck#UNKNOWN_LOCALE} if expansion produced no
     *       new aliases beyond the English literals (treat as a no-op so
     *       the capture still proceeds on locales without a dictionary
     *       entry)</li>
     * </ul>
     *
     * <p>The method intentionally never throws and never aborts the run;
     * the caller decides how to react to the result. Per the existing
     * capture pattern, "row alignment trumps perfect content", so a
     * {@code MISMATCH} that survives one retry should still fall through
     * to a screenshot of whatever is currently on screen.
     */
    public BodyTitleCheck verifyBodyTitleAlignment(String[] expectedAliases) {
        if (expectedAliases == null || expectedAliases.length == 0) {
            return BodyTitleCheck.UNKNOWN_LOCALE;
        }
        String[] localized = localizeLabels(expectedAliases);
        if (localized == null || localized.length == 0) {
            return BodyTitleCheck.UNKNOWN_LOCALE;
        }

        // If expansion produced nothing beyond the original English
        // literals, the current locale has no dictionary entry for these
        // labels (Qira's R.string scan missed AND we have no hardcoded
        // fallback). Degrade gracefully so unknown locales never fail
        // the check just because we have nothing to compare to.
        boolean hasLocalizedAlias = false;
        Set<String> originalSet = new HashSet<>();
        for (String original : expectedAliases) {
            if (original != null && !original.isEmpty()) {
                originalSet.add(original);
            }
        }
        for (String alias : localized) {
            if (alias != null && !alias.isEmpty() && !originalSet.contains(alias)) {
                hasLocalizedAlias = true;
                break;
            }
        }
        // On English-family locales the localized expansion will equal the
        // English literals; treat that as MATCH-eligible so we still catch
        // a wrong-row tap on en-XM / en-US / en-GB.
        if (!hasLocalizedAlias && !isEnglishFamilyLocale()) {
            return BodyTitleCheck.UNKNOWN_LOCALE;
        }

        return findRightPaneTextMatch(localized) != null
                ? BodyTitleCheck.MATCH
                : BodyTitleCheck.MISMATCH;
    }

    /**
     * Returns true when the active UI locale is one of the en-* variants
     * we capture (en-XM, en-US, en-GB, ...). Used by
     * {@link #verifyBodyTitleAlignment(String[])} to decide whether the
     * (English-only) expansion is meaningful for the body-title check
     * or whether we should treat it as "unknown locale".
     */
    private boolean isEnglishFamilyLocale() {
        try {
            String tag = QiraStrings.getInstance().getCurrentLocale().toLanguageTag();
            return tag != null && tag.toLowerCase(Locale.ROOT).startsWith("en");
        } catch (Throwable t) {
            // If we cannot determine the locale, default to "yes English"
            // so we still attempt the check rather than silently skipping.
            return true;
        }
    }

    /**
     * Searches the right half of the display for any element whose text
     * (or content-description) exactly matches one of {@code aliases}.
     * Returns the matching object or {@code null} when nothing matches.
     */
    private UiObject2 findRightPaneTextMatch(String[] aliases) {
        if (aliases == null || aliases.length == 0) {
            return null;
        }
        int width = mDevice.getDisplayWidth();
        int rightHalfX = width / 2;
        for (String alias : aliases) {
            if (alias == null || alias.isEmpty()) {
                continue;
            }
            try {
                List<UiObject2> textHits = mDevice.findObjects(
                        By.pkg(mConfig.getPackageName()).text(exactPatternForLabel(alias)));
                for (UiObject2 hit : textHits) {
                    try {
                        Rect bounds = hit.getVisibleBounds();
                        if (bounds != null && !bounds.isEmpty()
                                && bounds.centerX() > rightHalfX) {
                            return hit;
                        }
                    } catch (StaleObjectException ignored) {
                    }
                }
                List<UiObject2> descHits = mDevice.findObjects(
                        By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(alias)));
                for (UiObject2 hit : descHits) {
                    try {
                        Rect bounds = hit.getVisibleBounds();
                        if (bounds != null && !bounds.isEmpty()
                                && bounds.centerX() > rightHalfX) {
                            return hit;
                        }
                    } catch (StaleObjectException ignored) {
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * Issues a swipe inside the left-hand Settings master pane.
     *
     * @param forward {@code true} to swipe up (revealing later items),
     *                {@code false} to swipe down (revealing earlier items).
     * @return {@code true} if the gesture was issued; {@code false} only if
     *         we could not compute a usable swipe rectangle.
     */
    private boolean swipeMasterPane(boolean forward) throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int x = width / 4;
        int top = (height * 35) / 100;
        int bottom = (height * 80) / 100;
        if (top >= bottom) {
            return false;
        }
        int startY = forward ? bottom : top;
        int endY = forward ? top : bottom;
        mDevice.swipe(x, startY, x, endY, 25);
        settle();
        return true;
    }

    /**
     * Locates a left-pane Settings <em>option</em> (text matched exactly,
     * restricted to the left half of the display, and excluding section
     * headers which sit at the very left edge with their own indentation).
     * Returns {@code null} when no matching option is currently in view.
     *
     * <p>Section headers ("Personal", "General", "Data Control", ...) are
     * filtered out because their text is also rendered as a section title
     * at {@code x ≈ 42} while real options are indented at {@code x ≈ 176}.
     * "About" in particular is both a section header and an option, so we
     * have to be specific about which one we click.
     */
    private UiObject2 findLeftPaneOption(String... labels) {
        for (UiObject2 row : findVisibleLeftPaneOptionRows()) {
            if (rowContainsAnyLabel(row, labels)) {
                return row;
            }
        }

        int maxX = mDevice.getDisplayWidth() / 2;
        // Options live indented from the section gutter; require a small
        // left margin so we never accidentally click the section header.
        int minX = 100;
        // Try every alias (English + translated forms) so non-English
        // locales match the localized option label without needing a
        // separate code path.
        for (String alias : localizeLabels(labels)) {
            List<UiObject2> candidates = mDevice.findObjects(
                    By.pkg(mConfig.getPackageName()).text(exactPatternForLabel(alias)));
            for (UiObject2 obj : candidates) {
                try {
                    Rect bounds = obj.getVisibleBounds();
                    if (bounds == null || bounds.isEmpty()) {
                        continue;
                    }
                    int centerX = bounds.centerX();
                    if (centerX <= maxX && bounds.left >= minX) {
                        return obj;
                    }
                } catch (StaleObjectException ignored) {
                    // Node recycled; skip.
                }
            }
        }
        return null;
    }

    /**
     * Locates any left-pane text (option <em>or</em> section header)
     * matching the given label exactly. Used by the top-of-list detector,
     * which legitimately needs to see the section header.
     */
    private UiObject2 findLeftPaneText(String... labels) {
        int maxX = mDevice.getDisplayWidth() / 2;
        for (String alias : localizeLabels(labels)) {
            List<UiObject2> candidates = mDevice.findObjects(
                    By.pkg(mConfig.getPackageName()).text(exactPatternForLabel(alias)));
            for (UiObject2 obj : candidates) {
                try {
                    Rect bounds = obj.getVisibleBounds();
                    if (bounds == null || bounds.isEmpty()) {
                        continue;
                    }
                    if (bounds.centerX() <= maxX) {
                        return obj;
                    }
                } catch (StaleObjectException ignored) {
                    // Node recycled; skip.
                }
            }
        }
        return null;
    }

    private boolean rowContainsAnyLabel(UiObject2 row, String... labels) {
        for (String alias : localizeLabels(labels)) {
            try {
                if (row.findObject(By.text(exactPatternForLabel(alias))) != null
                        || row.findObject(By.desc(exactPatternForLabel(alias))) != null) {
                    return true;
                }
            } catch (Throwable ignored) {
                // Row recycled between scans.
            }
        }
        return false;
    }

    private List<UiObject2> findVisibleLeftPaneOptionRows() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> rows = new ArrayList<>();
        Set<String> seenBounds = new HashSet<>();

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                boolean fullWidthPhoneRow = bounds.left <= (width * 10) / 100
                        && bounds.right >= (width * 80) / 100
                        && object.findObject(By.desc(exactPatternForLabel("Go to details"))) != null;
                if (!fullWidthPhoneRow) {
                    if (bounds.left > (width * 10) / 100 || bounds.right > (width * 52) / 100) {
                        continue;
                    }
                    if (bounds.right < (width * 40) / 100) {
                        continue;
                    }
                }
                if (bounds.top < (height * 8) / 100 || bounds.bottom > (height * 95) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 4) / 100 || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (!isLikelyLeftPaneOptionRow(object)) {
                    continue;
                }
                String key = bounds.flattenToString();
                if (seenBounds.add(key)) {
                    rows.add(object);
                }
            } catch (Throwable ignored) {
                // Candidate recycled while scanning.
            }
        }

        Collections.sort(rows, new Comparator<UiObject2>() {
            @Override
            public int compare(UiObject2 left, UiObject2 right) {
                Rect lb = left.getVisibleBounds();
                Rect rb = right.getVisibleBounds();
                int byTop = lb.top - rb.top;
                if (Math.abs(byTop) > 6) {
                    return byTop;
                }
                return lb.left - rb.left;
            }
        });
        return rows;
    }

    private boolean isSettingsOptionListVisible() {
        List<UiObject2> rows = findVisibleLeftPaneOptionRows();
        if (rows.size() < 2) {
            return false;
        }
        int settingsRows = 0;
        for (UiObject2 row : rows) {
            if (rowContainsAnyLabel(row, OPTION_LABELS)) {
                settingsRows++;
            }
        }
        return settingsRows > 0;
    }

    private boolean tapSettingsDetailBackByGeometry() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestScore = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            if (bounds.left > (width * 18) / 100 || bounds.top > (height * 16) / 100) {
                continue;
            }
            if (bounds.width() < (width * 5) / 100 || bounds.width() > (width * 20) / 100) {
                continue;
            }
            if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 10) / 100) {
                continue;
            }
            String desc = object.getContentDescription();
            if (desc == null || !desc.toLowerCase().contains("back")) {
                continue;
            }
            int score = bounds.left + bounds.top;
            if (score < bestScore) {
                bestScore = score;
                best = object;
            }
        }
        if (best == null) {
            return false;
        }
        clickObject(best);
        return true;
    }

    private boolean isLikelyLeftPaneOptionRow(UiObject2 object) {
        try {
            if (object.findObject(By.desc(exactPatternForLabel("Go to details"))) != null) {
                return true;
            }
            List<UiObject2> textChildren = object.findObjects(By.clazz("android.widget.TextView"));
            int nonEmpty = 0;
            for (UiObject2 text : textChildren) {
                String value = text.getText();
                if (value == null) {
                    continue;
                }
                value = value.trim();
                if (value.isEmpty()) {
                    continue;
                }
                nonEmpty++;
            }
            return nonEmpty >= 1;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private QiraSettingsDetailPage trySelectOptionAndWait(String[] optionLabels,
            String... detailAnchors) throws Exception {
        if (!selectOption(optionLabels)) {
            return null;
        }
        return waitForDetail(detailAnchors, 2600L);
    }

    private QiraSettingsDetailPage scanVisibleRowsForDetail(String... detailAnchors)
            throws Exception {
        scrollToTop();

        Set<String> attemptedRows = new HashSet<>();
        for (int pass = 0; pass <= MAX_MASTER_SWIPES; pass++) {
            List<UiObject2> rows = findVisibleLeftPaneOptionRows();
            for (UiObject2 row : rows) {
                String signature = buildRowSignature(row);
                if (!attemptedRows.add(signature)) {
                    continue;
                }
                clickObject(row);
                QiraSettingsDetailPage detail = waitForDetail(detailAnchors, 2200L);
                if (detail != null) {
                    return detail;
                }
            }
            if (!swipeMasterPane(true)) {
                break;
            }
        }
        return null;
    }

    /**
     * Returns the Nth unique left-pane option row (0-based) after sweeping the
     * master list from top to bottom.
     */
    private UiObject2 findLeftPaneOptionByIndex(int targetIndex) throws Exception {
        scrollToTop();
        if (targetIndex < 0) {
            return null;
        }

        int seenCount = 0;
        Set<String> seenRows = new HashSet<>();
        for (int pass = 0; pass <= MAX_MASTER_SWIPES; pass++) {
            List<UiObject2> rows = findVisibleLeftPaneOptionRows();
            for (UiObject2 row : rows) {
                String signature = buildRowSignature(row);
                if (!seenRows.add(signature)) {
                    continue;
                }
                if (seenCount == targetIndex) {
                    return row;
                }
                seenCount++;
            }
            if (!swipeMasterPane(true)) {
                break;
            }
        }
        return null;
    }

    private String[] mergeDetailAnchors(String[] optionLabels, String... detailAnchors) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (detailAnchors != null) {
            Collections.addAll(merged, detailAnchors);
        }
        if (optionLabels != null) {
            Collections.addAll(merged, optionLabels);
        }
        return merged.toArray(new String[0]);
    }

    private QiraSettingsDetailPage waitForDetail(String[] detailAnchors, long timeoutMs)
            throws Exception {
        try {
            return new QiraSettingsDetailPage(mDevice, mConfig)
                    .waitForLoaded(timeoutMs, detailAnchors);
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private String buildRowSignature(UiObject2 row) {
        StringBuilder signature = new StringBuilder();
        try {
            for (UiObject2 text : row.findObjects(By.clazz("android.widget.TextView"))) {
                String value = text.getText();
                if (value == null) {
                    continue;
                }
                value = value.trim();
                if (value.isEmpty()) {
                    continue;
                }
                if (signature.length() > 0) {
                    signature.append('|');
                }
                signature.append(value);
            }
        } catch (Throwable ignored) {
            // Row recycled while building its signature.
        }
        if (signature.length() > 0) {
            return signature.toString();
        }
        try {
            Rect bounds = row.getVisibleBounds();
            if (bounds != null) {
                return bounds.flattenToString();
            }
        } catch (Throwable ignored) {
        }
        return Integer.toHexString(System.identityHashCode(row));
    }
}
