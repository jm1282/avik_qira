package avik.qira.pages;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Switch;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

public class QiraOnboardingPage extends BaseQiraPage {

    private static final long DEFAULT_TIMEOUT_MS = 15000L;
    private static final long TEXTLESS_START_SURFACE_MIN_WAIT_MS = 16000L;

    private static final String LAUNCHER_PACKAGE = "com.motorola.launcher3";

    private static final String[] APP_DRAWER_LABELS = {
            "Apps",
            "Newsfeed",
            "Motorola Qira"
    };

    private static final String[] QIRA_LAUNCHER_LABELS = {
            "Motorola Qira"
    };

    private static final String[] START_DIALOG_LABELS = {
            "Start the tour and get to know Motorola Qira",
            "Remind me later",
            "Starte die Tour und lerne Motorola Qira kennen",
            "Comece o tour e conhe\u00e7a a Qira da Motorola",
            "Comece o tour e conhe\u00e7a Motorola Qira",
            "Inicia el instructivo para conocer Motorola Qira",
            "Inicia el tutorial y descubre Motorola Qira"
    };

    private static final String[] START_PRIMARY_LABELS = {
            "Start",
            "D\u00e9marrer",
            "Iniciar",
            "Starten",
            "Avvia",
            "Rozpocznij",
            "\u958b\u59cb",
            "\u5f00\u59cb",
            "\u00cencepe",
            "\u00cencepe\u021bi"
    };

    private static final String[] END_SETUP_DIALOG_LABELS = {
            "End setup",
            "Quit setup?",
            "Quit setup",
            "If you end setup now",
            "If you quit now",
            "you\u2019ll need to start setup from the beginning",
            "you'll need to start setup from the beginning",
            // German.
            "Einrichtung beenden",
            "Einrichtung beenden?",
            "Wenn du die Einrichtung jetzt beendest",
            // French.
            "Quitter la configuration",
            "Quitter la configuration\u202f?",
            "Quitter la configuration ?",
            "Si vous quittez maintenant",
            // Spanish.
            "\u00bfQuieres salir de la configuraci\u00f3n?",
            "Salir de la configuraci\u00f3n",
            "\u00bfSalir de la configuraci\u00f3n?",
            "Si sales ahora",
            "Si sale ahora",
            // Italian.
            "Uscire dalla configurazione?",
            "Esci dalla configurazione",
            "Se esci ora",
            // Portuguese (Brazil).
            "Sair da configura\u00e7\u00e3o?",
            "Sair da configura\u00e7\u00e3o",
            "Se voc\u00ea sair agora",
            "Se sair agora",
            // Polish.
            "Czy chcesz zako\u0144czy\u0107 konfiguracj\u0119?",
            "Zako\u0144czy\u0107 konfiguracj\u0119?",
            "Je\u015bli wyjdziesz teraz",
            // Romanian.
            "Renun\u021ba\u021bi la configurare?",
            "Renun\u021bi la configurare?",
            "P\u0103r\u0103si\u021bi configurarea?",
            "Dac\u0103 renun\u021bi acum",
            "Dac\u0103 ie\u0219i acum",
            // Japanese.
            "\u30bb\u30c3\u30c8\u30a2\u30c3\u30d7\u3092\u7d42\u4e86\u3057\u307e\u3059\u304b",
            "\u30bb\u30c3\u30c8\u30a2\u30c3\u30d7\u3092\u4e2d\u6b62\u3057\u307e\u3059\u304b",
            "\u4eca\u7d42\u4e86\u3059\u308b\u3068",
            // Chinese (Simplified).
            "\u9000\u51fa\u8bbe\u7f6e\uff1f",
            "\u9000\u51fa\u8bbe\u7f6e",
            "\u7ed3\u675f\u8bbe\u7f6e\uff1f",
            "\u73b0\u5728\u9000\u51fa"
    };

    private static final String[] END_SETUP_STAY_LABELS = {
            "Stay",
            "Cancel",
            // German.
            "Bleiben",
            "Abbrechen",
            // French.
            "Rester",
            "Annuler",
            // Spanish.
            "Quedarse",
            "Cancelar",
            "Permanecer",
            // Italian.
            "Rimani",
            "Annulla",
            // Portuguese (Brazil).
            "Ficar",
            "Permanecer",
            // Polish.
            "Zosta\u0144",
            "Anuluj",
            // Romanian.
            "R\u0103m\u00e2i",
            "Anuleaz\u0103",
            // Japanese: 留まる ("Stay"), 維持 ("Keep" - newer build label),
            // キャンセル ("Cancel").
            "\u7559\u307e\u308b",
            "\u7dad\u6301",
            "\u30ad\u30e3\u30f3\u30bb\u30eb",
            // Chinese (Simplified).
            "\u7559\u4e0b",
            "\u4fdd\u7559",
            "\u53d6\u6d88"
    };

    // Anchors used to recognise the "Hi, I'm Motorola Qira, your personal
    // intelligence" intro arrow card. We list several independent fragments
    // because the English phrasing is rewritten per-locale (e.g. pt-BR
    // renders "Olá, meu nome é Qira da Motorola... Sou sua assistente de
    // inteligência artificial pessoal.", where "personal intelligence" is
    // not even a substring). Each entry is also mapped to its localisation
    // in QiraStrings.loadCatalog().
    private static final String[] INTRO_BANNER_LABELS = {
            "I'm Motorola Qira",
            "personal intelligence"
    };

    private static final String[] PRODUCTIVITY_BANNER_LABELS = {
            "stay productive"
    };

    private static final String[] LANGUAGE_LABELS = {
            "Choose a response language for Motorola Qira",
            "Choose a response language",
            "response language"
    };

    private static final String[] LANGUAGE_OPTION_LABELS = {
            "English (United States)",
            "English (United Kingdom)",
            "English (India)",
            "Deutsch (Deutschland)",
            "German (Germany)",
            "Espa\u00f1ol (Espa\u00f1a)",
            "Spanish (Spain)",
            "Espa\u00f1ol (Estados Unidos)",
            "Spanish (United States)",
            "Fran\u00e7ais (France)",
            "French (France)",
            "Italiano (Italia)",
            "Italian (Italy)",
            "Portugu\u00eas (Brasil)",
            "Portuguese (Brazil)",
            "Polski (Polska)",
            "Polish (Poland)",
            "\u65e5\u672c\u8a9e (\u65e5\u672c)",
            "Japanese (Japan)",
            "Rom\u00e2n\u0103",
            "Romanian",
            "\u7b80\u4f53\u4e2d\u6587",
            "\u4e2d\u6587\uff08\u7b80\u4f53\uff09",
            "Chinese (Simplified)",
            "Chinese (China)"
    };

    /**
     * Primary CTA labels used on the response-language picker.
     *
     * <p>Some locales render this as a localized text button (for example
     * "Avançar" on pt-BR) rather than the legacy "Next" content-description.
     * Keep this list focused on short one-word / two-word "advance" actions so
     * exact-match selection stays deterministic.
     */
    private static final String[] LANGUAGE_PICKER_NEXT_LABELS = {
            "Next",
            "Continue",
            "Avançar",
            "Avancar",
            "Suivant",
            "Continuer",
            "Siguiente",
            "Continuar",
            "Weiter",
            "Fortfahren",
            "Avanti",
            "Dalej",
            "Kontynuuj",
            "Nast\u0119pny",
            "\u6b21\u3078",
            "\u7d9a\u884c",
            "\u00cenainte",
            "Continu\u0103",
            "\u4e0b\u4e00\u6b65",
            "\u7ee7\u7eed"
    };

    private static final String[] SIGN_IN_LABELS = {
            "Sign in with Moto account or Lenovo ID",
            "Sign in with Moto account",
            "Sign in with Lenovo ID",
            "Continue as",
            "Fortfahren als",
            "Continuer en tant que",
            "Continuer comme",
            "Continuar como",
            "Continua come",
            "Kontynuuj jako",
            // Japanese: NEVER use bare "\u7d9a\u884c" (= "continue"). Qira's
            // acknowledge legal disclaimer also contains the substring
            // ("\u7d9a\u884c\u3059\u308b\u3053\u3068\u3067" = "by continuing"),
            // and a substring-match on the bare verb falsely flips
            // isSignInDialogVisible() to true on the Acknowledge dialog. The
            // multi-character compound "\u3068\u3057\u3066\u7d9a\u884c" (= "continue as")
            // is unique to the sign-in surface.
            "\u3068\u3057\u3066\u7d9a\u884c",
            // Chinese (Simplified/Traditional): same reason as Japanese - "\u7ee7\u7eed"
            // (= "continue") appears on the acknowledge dialog as well, so we
            // rely on the longer "\u8eab\u4efd\u7ee7\u7eed" (= "continue as identity") and
            // "\u4ee5...\u8eab\u4efd\u7ee7\u7eed" (= "continue as <identity>") variants.
            "\u8eab\u4efd\u7ee7\u7eed",
            "Continu\u0103 ca"
    };

    // Text fallbacks for the Moto-account acknowledgement dialog. Many
    // localized builds do not expose these strings through Qira's resource
    // scan, so the primary detector below uses the dump-proven dialog shape
    // instead of relying on translated labels. Every label here is fed to a
    // substring regex via hasTextOrDescription(); keep each one unique
    // enough that it cannot trigger on any *other* Qira surface. The
    // permission-review panel, for example, embeds "Lenovo AI Acceptable
    // Use Policy" in its body copy, so a bare "Accept" substring here
    // would flip isAcknowledgeDialogVisible() to true on that panel and
    // cause acknowledge() to spin for the full timeout after a successful
    // tap. The real "Accept" button is still clicked via exact-match
    // clickByExactTextOrDescription("Accept") inside acknowledge(), so we
    // do not need to carry the bare word in the detector set.
    private static final String[] ACKNOWLEDGE_LABELS = {
            "A few notes from Motorola Qira",
            "A few notes about Motorola Qira",
            // Legacy labels retained for older Qira builds that still ship
            // the original wording. resolve() returns all of them so
            // waitForSurface() matches whichever is actually rendered.
            "Moto account and Lenovo ID",
            "I acknowledge"
    };

    private static final String[] ACKNOWLEDGE_BACKUP_MEMORY_LABELS = {
            "Back up previous memories",
            "Backup previous memories",
            "Back up your Moto AI memories",
            "Moto AI memories"
    };

    private static final String[] CONTEXTUAL_READING_PERMISSION_DIALOG_LABELS = {
            "Permission needed for contextual reading",
            "Moto Action Core uses Android's Accessibility Service",
            "read content on your screen",
            "does not continuously monitor"
    };

    private static final String[] CONTEXTUAL_READING_ENABLE_LABELS = {
            "Enable permission",
            "Enable Permission"
    };

    private static final String[] PERMISSION_BANNER_LABELS = {
            "review a few permissions"
    };

    private static final String[] PERMISSION_PANEL_LABELS = {
            "Turn on all permissions below",
            "Enable Personalized Answers",
            "Sync data across your devices"
    };

    /**
     * Positive-action CTA labels used on the permission review panel.
     *
     * <p>Qira localizes this button aggressively ("Aceitar", "Ich stimme zu",
     * etc.), and several builds do not map those strings back through
     * QiraStrings' catalog. We therefore keep a compact multilingual list and
     * still fall back to geometry when text lookup misses.
     */
    private static final String[] PERMISSION_AGREE_LABELS = {
            "I agree",
            "Accept",
            "Agree",
            "Aceitar",
            "Ich stimme zu",
            "Accepter",
            "Aceptar",
            "Accetto",
            "Concordo",
            "Zgadzam si\u0119",
            "\u540c\u610f\u3059\u308b",
            "\u540c\u610f",
            "\u63a5\u53d7",
            "Sunt de acord",
            "Accept\u0103"
    };

    /**
     * Breadcrumb text shown at the bottom of the permission-review surface
     * (above the Backward / Qira / Next footer). It appears on no other
     * onboarding surface, so it is the single most reliable signal that
     * we are still on the permission panel.
     *
     * <p>NB: Qira renders the visible breadcrumb "Let[U+2019]s review a
     * few permissions" with a Unicode right-single-quote (U+2019)
     * rather than the ASCII apostrophe U+0027. Both our text anchor
     * matcher (Pattern.quote) and UiAutomator's By.text(Pattern) treat
     * the two as distinct code points, so a "Let's ..." anchor with
     * an ASCII apostrophe can never match the rendered string. We
     * therefore intentionally keep ONLY the apostrophe-free suffix
     * ("review a few permissions") which, combined with the leading
     * ".*" in {@link BaseQiraPage#patternForLabel}, still matches the
     * full breadcrumb via substring regardless of the apostrophe
     * variant.
     */
    private static final String[] PERMISSION_PANEL_BREADCRUMB_LABELS = {
            "review a few permissions"
    };

    private static final String[] HOTWORD_SETUP_LABELS = {
            "Ready to set up",
            "Hey Motorola Qira",
            "Hey Qira",
            "Voice activation",
            "Bereit zum Einrichten",
            "Bereit zum Einrichten von",
            "Sprachsteuerung aktivieren",
            "Sage \u201eHey Qira\u201c"
    };

    private static final String[] MIC_NOT_DETECTED_LABELS = {
            "Mic not detected",
            "Skip this step",
            "Diesen Schritt \u00fcberspringen"
    };

    private static final String[] HOTWORD_SKIP_LABELS = {
            "Skip this step",
            "Diesen Schritt \u00fcberspringen",
            "\u00dcberspringen",
            "Not now",
            "Ignorer cette \u00e9tape",
            "Omitir este paso",
            "Ignora questo passaggio",
            "Pular esta etapa",
            "Pomi\u0144 ten krok",
            "\u3053\u306e\u624b\u9806\u3092\u30b9\u30ad\u30c3\u30d7",
            "\u8df3\u8fc7\u6b64\u6b65\u9aa4",
            "Omite acest pas"
    };

    private static final String[] EXPLORE_START_LABELS = {
            "Let's explore what Motorola Qira can do",
            "Let's explore",
            "explore what Motorola Qira",
            "Start the tour",
            "Finde heraus, was Motorola Qira alles kann.",
            "Vamos descobrir o que a Qira da Motorola pode fazer",
            "Vamos a explorar lo que Motorola Qira puede hacer",
            "Veamos todo lo que puede hacer Motorola Qira"
    };

    private static final String[] EXPLORE_START_PRIMARY_LABELS = {
            "Let's explore what Motorola Qira can do",
            "Let's explore",
            "explore what Motorola Qira",
            "Finde heraus, was Motorola Qira alles kann.",
            "Vamos descobrir o que a Qira da Motorola pode fazer",
            "Vamos a explorar lo que Motorola Qira puede hacer",
            "Veamos todo lo que puede hacer Motorola Qira"
    };

    private static final String[] EXPLORE_CANCEL_LABELS = {
            "Cancel",
            "Abbrechen",
            "Annuler",
            "Cancelar",
            "Annulla",
            "Anuluj",
            "Anuleaz\u0103",
            "\u30ad\u30e3\u30f3\u30bb\u30eb",
            "\u53d6\u6d88"
    };

    private static final String[] NEARBY_DEVICES_PROMPT_LABELS = {
            "find, connect to, and determine the relative position",
            "relative position of nearby devices"
    };

    private static final String PERMISSION_ALLOW_RES =
            "com.android.permissioncontroller:id/permission_allow_button";
    private static final String PERMISSION_ALLOW_FG_RES =
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button";
    private static final String PERMISSION_LOCATION_COARSE_RES =
            "com.android.permissioncontroller:id/permission_location_accuracy_radio_coarse";
    private static final String PERMISSION_LOCATION_FINE_RES =
            "com.android.permissioncontroller:id/permission_location_accuracy_radio_fine";
    private static final String PERMISSION_CONTROLLER_PACKAGE =
            "com.android.permissioncontroller";
    private static final String PERMISSION_CONTROLLER_GOOGLE_PACKAGE =
            "com.google.android.permissioncontroller";

    private static final String[] HOME_TILE_LABELS = {
            "Turn your ideas into images",
            "Talk in real-time with Motorola Qira",
            "See summarized notifications",
            "important to you",
            "Ask anything",
            "Record and summarize meetings",
            // Japanese feature card titles. Several Qira home cards do not
            // expose their headline through the QiraStrings R.string scan
            // (the cards build their text from %s templates), so we keep a
            // small cohort of dump-proven Japanese fragments here. Each
            // phrase is unique to the Qira home grid - none of them ever
            // appears on the launcher or any onboarding surface, so the
            // contains-match below cannot false-positive.
            "\u30a2\u30a4\u30c7\u30a2\u3092\u753b\u50cf",   // "アイデアを画像" (turn ideas into images)
            "\u30ea\u30a2\u30eb\u30bf\u30a4\u30e0\u3067",     // "リアルタイムで" (in real-time)
            "\u8981\u7d04\u3055\u308c\u305f\u901a\u77e5",   // "要約された通知" (summarized notifications)
            "\u4f1a\u8b70\u3092\u9332\u753b",               // "会議を録画" (record meetings)
            "\u8cea\u554f\u3057\u3066\u304f\u3060\u3055\u3044", // "質問してください" (ask anything)
            // Chinese (Simplified) home tile fragments. Same story as
            // Japanese - several Compose-built cards do not surface their
            // localized text through the cached scan, so we anchor on the
            // dump-proven verb phrases.
            "\u8f6c\u5316\u4e3a\u56fe\u50cf",   // "转化为图像" (turn into image)
            "\u5b9e\u65f6\u4ea4\u8c08",         // "实时交谈" (real-time conversation)
            "\u67e5\u770b\u603b\u7ed3\u7684\u901a\u77e5", // "查看总结的通知" (see summarized notifications)
            "\u5f55\u5236\u548c\u603b\u7ed3",   // "录制和总结" (record and summarize)
            "\u63d0\u95ee\u4efb\u4f55\u5185\u5bb9", // "提问任何内容" (ask anything)
            // German tile fragments - keep them short and unique.
            "Verwandle deine Ideen in Bilder",
            "Stelle eine beliebige Frage",
            "Notizen zusammenfassen",
            // French.
            "Transforme tes id\u00e9es en images",
            "Pose n'importe quelle question",
            // Spanish.
            "Transforma tus ideas en im\u00e1genes",
            "Pregunta lo que sea",
            // Italian.
            "Trasforma le tue idee in immagini",
            "Fai qualsiasi domanda",
            // Portuguese (Brazil).
            "Transforme suas ideias em imagens",
            "Pergunte qualquer coisa",
            // Polish.
            "Zamie\u0144 swoje pomys\u0142y w obrazy",
            "Zapytaj o cokolwiek",
            // Romanian.
            "Transform\u0103 ideile \u00een imagini",
            "\u00centreab\u0103 orice"
    };

    private static final String[] HOME_TILE_ICON_DESCS = {
            "Chat",
            "Live",
            "Catch me up",
            "Record",
            // Japanese localised content-descriptions for the bottom tab.
            "\u30c1\u30e3\u30c3\u30c8",  // "チャット" (chat)
            "\u30e9\u30a4\u30d6",        // "ライブ" (live)
            "\u9332\u753b",              // "録画" (record)
            // Chinese (Simplified) localised content-descriptions.
            "\u804a\u5929",              // "聊天" (chat)
            "\u76f4\u64ad",              // "直播" (live)
            "\u5f55\u5236",              // "录制" (record)
            // Polish localised content-descriptions.
            "Czat",                       // "Czat" (chat)
            "Na \u017cywo",              // "Na żywo" (live)
            // Romanian.
            "Conversa\u021bie",           // "Conversație" (chat)
            "\u00cen direct",             // "În direct" (live)
            // German.
            "Aufnehmen",                  // "Aufnehmen" (record)
            // French.
            "En direct",                  // "En direct" (live)
            // Spanish.
            "En vivo",                    // "En vivo" (live)
            // Italian.
            "In diretta"                  // "In diretta" (live)
    };

    /**
     * Localized content-descriptions of the four hero tiles on the Qira home
     * grid (Focus Zone, Creator Zone, Knowledge, Chat History). When the
     * full-text scan misses the home grid we still detect "we are on home"
     * by looking for these tile descriptions in the upper 70% of the
     * display.
     */
    private static final String[] HOME_HERO_TILE_DESCS = {
            "Focus Zone",
            "Creator Zone",
            "Knowledge",
            "Chat History",
            // German.
            "Fokus-Zone", "Kreativbereich", "Wissen", "Chatverlauf",
            // French.
            "Zone de focalisation", "Zone du cr\u00e9ateur", "Connaissances",
            "Historique des discussions", "Historique de discussion",
            // Spanish.
            "Zona de enfoque", "Zona del creador", "Conocimientos",
            "Historial de chat",
            // Italian.
            "Zona Focus", "Zona del creatore", "Conoscenza", "Cronologia chat",
            // Portuguese (Brazil).
            "Zona Foco", "Zona do criador", "Conhecimento",
            "Hist\u00f3rico de bate-papo",
            // Polish.
            "Strefa skupienia", "Strefa twA3rcy", "Strefa tw\u00f3rcy",
            "Wiedza", "Historia czatu",
            // Japanese.
            "\u30d5\u30a9\u30fc\u30ab\u30b9\u30be\u30fc\u30f3", // フォーカスゾーン
            "\u30af\u30ea\u30a8\u30a4\u30bf\u30fc\u30be\u30fc\u30f3", // クリエイターゾーン
            "\u30ca\u30ec\u30c3\u30b8",          // ナレッジ
            "\u30c1\u30e3\u30c3\u30c8\u5c65\u6b74", // チャット履歴
            // Chinese (Simplified).
            "\u4e13\u6ce8\u533a",                  // 专注区
            "\u521b\u4f5c\u533a",                  // 创作区
            "\u77e5\u8bc6",                        // 知识
            "\u804a\u5929\u5386\u53f2",            // 聊天历史
            // Romanian.
            "Zona de focalizare", "Zona creatorului", "Cuno\u015ftin\u021be",
            "Istoricul discu\u021biilor"
    };

    private static final String[] LOCATION_PROMPT_LABELS = {
            "Allow Motorola Qira to access this device's location",
            "While using the app"
    };

    private static final int ACKNOWLEDGE_MIN_CONTENT_LABELS = 8;

    private static final String[] PERMISSION_TOGGLE_LABELS = {
            "Keep Motorola Qira always visible",
            "Get more personalized answers",
            "Automate tasks",
            "Allow contextual reading",
            "Sync data across your devices"
    };

    public QiraOnboardingPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public void waitForAppDrawer() throws Exception {
        waitForSurface("the launcher Apps drawer", APP_DRAWER_LABELS);
    }

    public void launchFromAppDrawer() throws Exception {
        if (!clickLauncherQiraIcon()) {
            throw new IllegalStateException("Unable to launch Motorola Qira from the app drawer");
        }
    }

    public void launchQiraApp() throws Exception {
        ensureDeviceUnlocked();

        if (isQiraLaunchPermissionSurface()) {
            return;
        }

        // Always issue an explicit launch intent even when UiAutomator reports
        // Qira as the current package. On some builds the floating Focus Zone
        // overlay keeps the package foreground while the user is effectively on
        // launcher/overlay chrome, which breaks subsequent "go to home tile grid"
        // navigation. Re-launching normalizes us back onto the primary Qira task.

        if (mConfig.hasLaunchActivity()) {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "am start -W -n %s/%s",
                    mConfig.getPackageName(),
                    mConfig.getLaunchActivity()));
            if (waitForPackageForeground(10000L)) {
                return;
            }
        }

        mDevice.executeShellCommand(String.format(Locale.US,
                "monkey -p %s -c android.intent.category.LAUNCHER 1",
                mConfig.getPackageName()));
        if (!waitForPackageForeground(10000L)) {
            // Maybe the device re-locked between wake and launch - try once more.
            ensureDeviceUnlocked();
            mDevice.executeShellCommand(String.format(Locale.US,
                    "monkey -p %s -c android.intent.category.LAUNCHER 1",
                    mConfig.getPackageName()));
            if (!waitForPackageForeground(10000L)
                    && !mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                    && !isQiraLaunchPermissionSurface()) {
                if (mConfig.hasLaunchActivity()) {
                    try {
                        mDevice.executeShellCommand(String.format(Locale.US,
                                "am start -W -S -n %s/%s",
                                mConfig.getPackageName(),
                                mConfig.getLaunchActivity()));
                    } catch (Throwable ignored) {
                    }
                }
                if (waitForPackageForeground(10000L)
                        || mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                        || isQiraLaunchPermissionSurface()) {
                    return;
                }
                throw new IllegalStateException("Unable to launch Motorola Qira");
            }
        }
    }

    /**
     * Wakes the device, dismisses the keyguard with a swipe-up, and disables the
     * lock screen. No-op if the device is already unlocked.
     */
    public void ensureDeviceUnlocked() throws Exception {
        try {
            mDevice.wakeUp();
        } catch (Throwable ignored) {
        }

        for (int attempt = 0; attempt < 4; attempt++) {
            if (!isDeviceLocked()) {
                return;
            }
            try {
                mDevice.executeShellCommand("input keyevent KEYCODE_WAKEUP");
            } catch (Throwable ignored) {
            }
            try {
                mDevice.executeShellCommand("input keyevent 82");
            } catch (Throwable ignored) {
            }
            int w = mDevice.getDisplayWidth();
            int h = mDevice.getDisplayHeight();
            mDevice.swipe(w / 2, (h * 85) / 100, w / 2, (h * 15) / 100, 20);
            mUtils.sleep(800L);
        }

        if (isDeviceLocked()) {
            // Last-ditch: disable lockscreen entirely so downstream runs don't hit it.
            try {
                mDevice.executeShellCommand("locksettings set-disabled true");
            } catch (Throwable ignored) {
            }
            try {
                mDevice.executeShellCommand("input keyevent KEYCODE_WAKEUP");
            } catch (Throwable ignored) {
            }
            int w = mDevice.getDisplayWidth();
            int h = mDevice.getDisplayHeight();
            mDevice.swipe(w / 2, (h * 85) / 100, w / 2, (h * 15) / 100, 20);
            mUtils.sleep(800L);
        }
    }

    private boolean isDeviceLocked() throws Exception {
        String out = mDevice.executeShellCommand("dumpsys window");
        if (out == null) {
            return false;
        }
        // Common markers across vendors/versions.
        return out.contains("mDreamingLockscreen=true")
                || out.contains("mKeyguardShowing=true")
                || out.contains("mShowingLockscreen=true");
    }

    private boolean waitForPackageForeground(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                    || isQiraLaunchPermissionSurface()) {
                mUtils.sleep(1500L);
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    private boolean isQiraLaunchPermissionSurface() {
        if (!isPermissionControllerSurface()) {
            return false;
        }
        if (findByResource(PERMISSION_ALLOW_FG_RES,
                PERMISSION_ALLOW_RES,
                PERMISSION_LOCATION_FINE_RES,
                PERMISSION_LOCATION_COARSE_RES) != null) {
            return true;
        }
        // Defensive fallback for OEM permission UIs where IDs drift but
        // the dialog body still names the requesting app.
        return findSystemTextOrDescription("Motorola Qira", mConfig.getPackageName()) != null;
    }

    public QiraOnboardingPage waitForStartDialog() throws Exception {
        long startedAt = System.currentTimeMillis();
        long deadline = startedAt + 30000L;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            long now = System.currentTimeMillis();
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isStartDialogVisible()) {
                settle();
                return this;
            }
            if (isComposeStartSurfaceAfterAnimation(startedAt)) {
                ONBOARDING_LOGGER.info("Qira Start anchors are not exposed by Compose; using timed first-run surface fallback.");
                settle();
                return this;
            }
            if (now - lastDiagLog > 8000L) {
                ONBOARDING_LOGGER.info("waitForStartDialog currentPackage="
                        + mDevice.getCurrentPackageName());
                logVisibleLabelsForDiagnostics("waitForStartDialog polling");
                lastDiagLog = now;
            }
            mUtils.sleep(250L);
        }
        logVisibleLabelsForDiagnostics("waitForSurface the Qira onboarding start screen timeout");
        throw new IllegalStateException("Unable to detect the Qira onboarding start screen");
    }

    public boolean isStartDialogVisible() {
        // The response-language picker reuses the same footer chrome and
        // a bottom-edge primary CTA, so without this exclusion the start
        // screen geometry can false-positive on the picker and starve the
        // real picker branch in advanceOnboardingOnce().
        if (countLanguagePickerRadioButtons() >= 2
                || countLooseLanguagePickerOptionTexts() >= 4) {
            return false;
        }
        if (isLauncherBackedStartDialogVisible()) {
            return true;
        }
        UiObject2 primary = findStartDialogPrimaryButtonByGeometry();
        if (primary != null && countStartDialogButtonsByGeometry() >= 2) {
            if (findStartDialogQiraFooter() != null || hasQiraPackageSurface()) {
                return true;
            }
        }
        return hasTextOrDescription(START_DIALOG_LABELS)
                && findByExactTextOrDescription(START_PRIMARY_LABELS) != null;
    }

    public void tapStartDialog() throws Exception {
        if (clickGlobalExactTextOrDescription(START_PRIMARY_LABELS)) {
            return;
        }
        UiObject2 startButton = findStartDialogPrimaryButtonByGeometry();
        if (startButton != null) {
            clickObject(startButton);
            return;
        }
        if (clickByExactTextOrDescription(START_PRIMARY_LABELS)) {
            return;
        }
        if (tapStartDialogPrimaryButtonByCoordinates()) {
            return;
        }
        if (tapStartDialogByKeyboardFocus()) {
            return;
        }
        throw new IllegalStateException("Unable to tap the Qira onboarding Start button");
    }

    private void shellTapPercent(int xp, int yp) throws Exception {
        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();
        int x = (w * xp) / 100;
        int y = (h * yp) / 100;
        mDevice.executeShellCommand("input tap " + x + " " + y);
        mUtils.sleep(1800L);
    }

    /**
     * Deterministic onboarding bypass for dev builds where Compose does not
     * expose accessibility text/geometry. Uses shell {@code input tap} only.
     */
    public void completeQiraTextlessOnboardingFast() throws Exception {
        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();

        ONBOARDING_LOGGER.info("[QiraTextless] Start");
        shellTapPercent(50, 77);

        ONBOARDING_LOGGER.info("[QiraTextless] Intro next arrow");
        shellTapPercent(94, 91);

        ONBOARDING_LOGGER.info("[QiraTextless] Language Next");
        shellTapPercent(79, 82);

        ONBOARDING_LOGGER.info("[QiraTextless] Continue As");
        shellTapPercent(50, 90);

        ONBOARDING_LOGGER.info("[QiraTextless] Acknowledge scroll");
        for (int i = 0; i < 12; i++) {
            mDevice.swipe((w * 66) / 100, (h * 53) / 100,
                    (w * 66) / 100, (h * 29) / 100, 25);
            mUtils.sleep(250L);
        }

        ONBOARDING_LOGGER.info("[QiraTextless] Acknowledge accept");
        shellTapPercent(72, 82);

        ONBOARDING_LOGGER.info("[QiraTextless] Permissions master toggle");
        shellTapPercent(84, 22);

        ONBOARDING_LOGGER.info("[QiraTextless] Permissions agree");
        shellTapPercent(72, 82);

        ONBOARDING_LOGGER.info("[QiraTextless] Hotword skip");
        shellTapPercent(50, 82);

        ONBOARDING_LOGGER.info("[QiraTextless] Explore start");
        shellTapPercent(50, 79);
    }

    private boolean isComposeStartSurfaceAfterAnimation(long launchStartedAt) {
        long elapsed = System.currentTimeMillis() - launchStartedAt;
        if (elapsed < TEXTLESS_START_SURFACE_MIN_WAIT_MS) {
            return false;
        }
        if (!hasQiraPackageSurface()
                && !isQiraFocusedWindow()
                && !(isLauncherForeground() && isQiraProcessRunning())) {
            return false;
        }
        if (isIntroBannerVisible()
                || isLanguagePickerVisible()
                || isSignInDialogVisible()
                || isProductivityBannerVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionBannerVisible()
                || isPermissionPanelVisible()) {
            return false;
        }
        return hasNoVisibleQiraAccessibilityLabels()
                || (isLauncherForeground() && isQiraProcessRunning());
    }

    private boolean tapStartDialogByKeyboardFocus() throws Exception {
        int[] tabAttempts = {1, 2, 3};
        for (int tabs : tabAttempts) {
            for (int i = 0; i < tabs; i++) {
                mDevice.pressKeyCode(KeyEvent.KEYCODE_TAB);
                mUtils.sleep(200L);
            }
            mDevice.pressKeyCode(KeyEvent.KEYCODE_ENTER);
            if (waitForStartDialogAdvanced(3500L)) {
                return true;
            }
        }

        mDevice.pressKeyCode(KeyEvent.KEYCODE_DPAD_CENTER);
        return waitForStartDialogAdvanced(2500L);
    }

    private boolean waitForStartDialogAdvanced(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isIntroBannerVisible()
                    || isLanguagePickerVisible()
                    || isSignInDialogVisible()
                    || isProductivityBannerVisible()
                    || isAcknowledgeDialogVisible()
                    || isPermissionBannerVisible()
                    || isPermissionPanelVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    private boolean tapStartDialogPrimaryButtonByCoordinates() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        mDevice.click(width / 2, (height * 77) / 100);
        settle();
        return true;
    }

    private boolean hasQiraPackageSurface() {
        try {
            if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
                return true;
            }
            if (mDevice.findObject(By.pkg(mConfig.getPackageName()).clazz(COMPOSE_VIEW_CLASS)) != null) {
                return true;
            }
            return !mDevice.findObjects(By.pkg(mConfig.getPackageName())).isEmpty();
        } catch (StaleObjectException stale) {
            return true;
        }
    }

    private boolean forceLaunchQiraForTextlessStartSurface() throws Exception {
        if (mConfig.hasLaunchActivity()) {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "am start -W -n %s/%s",
                    mConfig.getPackageName(),
                    mConfig.getLaunchActivity()));
        } else {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "monkey -p %s -c android.intent.category.LAUNCHER 1",
                    mConfig.getPackageName()));
        }
        mUtils.sleep(2500L);
        return hasQiraPackageSurface() || isQiraFocusedWindow();
    }

    private boolean isQiraFocusedWindow() {
        try {
            String out = mDevice.executeShellCommand("dumpsys window");
            return out != null
                    && out.contains("mCurrentFocus=Window")
                    && out.contains(mConfig.getPackageName());
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isLauncherForeground() {
        try {
            if (LAUNCHER_PACKAGE.equals(mDevice.getCurrentPackageName())) {
                return true;
            }
            String out = mDevice.executeShellCommand("dumpsys window");
            return out != null && out.contains(LAUNCHER_PACKAGE);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isQiraProcessRunning() {
        try {
            String out = mDevice.executeShellCommand(
                    String.format(Locale.US, "pidof %s", mConfig.getPackageName()));
            return out != null && !out.trim().isEmpty();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean launchQiraFromLauncherOrDrawer() throws Exception {
        if (clickLauncherQiraIcon()) {
            return true;
        }
        openLauncherAppDrawer();
        if (clickLauncherQiraIcon()) {
            return true;
        }
        return scrollAppDrawerToAndClickQiraIcon();
    }

    private void openLauncherAppDrawer() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        mDevice.swipe(width / 2, (height * 88) / 100, width / 2, (height * 28) / 100, 24);
        settle();
        mUtils.sleep(800L);
    }

    private boolean clickLauncherQiraIcon() throws Exception {
        UiObject2 icon = findLauncherQiraIcon();
        if (icon == null) {
            return false;
        }
        clickObject(icon);
        settle();
        return true;
    }

    private boolean scrollAppDrawerToAndClickQiraIcon() throws Exception {
        for (int attempt = 0; attempt < 8; attempt++) {
            if (clickLauncherQiraIcon()) {
                return true;
            }
            UiObject2 list = mDevice.findObject(By.res(LAUNCHER_PACKAGE + ":id/apps_list_view"));
            if (list == null) {
                return false;
            }
            Rect bounds;
            try {
                bounds = list.getVisibleBounds();
            } catch (StaleObjectException stale) {
                return false;
            }
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            mDevice.swipe(bounds.centerX(),
                    Math.max(bounds.top + 100, bounds.bottom - 180),
                    bounds.centerX(),
                    Math.min(bounds.bottom - 100, bounds.top + 260),
                    24);
            settle();
            mUtils.sleep(500L);
        }
        return clickLauncherQiraIcon();
    }

    private UiObject2 findLauncherQiraIcon() {
        UiObject2 exact = findLauncherExactTextOrDescription(QIRA_LAUNCHER_LABELS);
        if (exact != null) {
            return exact;
        }
        return findLauncherTextOrDescription(QIRA_LAUNCHER_LABELS);
    }

    private UiObject2 findLauncherTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        for (String label : labels) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            UiObject2 byDesc = mDevice.findObject(
                    By.pkg(LAUNCHER_PACKAGE).desc(patternForLabel(label)));
            if (byDesc != null) {
                return byDesc;
            }
            UiObject2 byText = mDevice.findObject(
                    By.pkg(LAUNCHER_PACKAGE).text(patternForLabel(label)));
            if (byText != null) {
                return byText;
            }
        }
        return null;
    }

    private UiObject2 findLauncherExactTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        for (String label : labels) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            UiObject2 byDesc = mDevice.findObject(
                    By.pkg(LAUNCHER_PACKAGE).desc(exactPatternForLabel(label)));
            if (byDesc != null) {
                return byDesc;
            }
            UiObject2 byText = mDevice.findObject(
                    By.pkg(LAUNCHER_PACKAGE).text(exactPatternForLabel(label)));
            if (byText != null) {
                return byText;
            }
        }
        return null;
    }

    private boolean hasNoVisibleQiraAccessibilityLabels() {
        boolean sawQiraObject = false;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                sawQiraObject = true;
                if (!TextUtils.isEmpty(object.getText())
                        || !TextUtils.isEmpty(object.getContentDescription())) {
                    return false;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return !sawQiraObject;
    }


    private boolean isLauncherBackedStartDialogVisible() {
        return findGlobalExactTextOrDescription(START_PRIMARY_LABELS) != null
                && (findGlobalTextOrDescription(START_DIALOG_LABELS) != null
                        || findGlobalExactTextOrDescription("Remind me later") != null);
    }

    private boolean clickGlobalExactTextOrDescription(String... labels) throws Exception {
        UiObject2 object = findGlobalExactTextOrDescription(labels);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    private UiObject2 findGlobalTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        for (String label : localizeLabels(labels)) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            UiObject2 byDesc = mDevice.findObject(By.desc(patternForLabel(label)));
            if (byDesc != null) {
                return byDesc;
            }
            UiObject2 byText = mDevice.findObject(By.text(patternForLabel(label)));
            if (byText != null) {
                return byText;
            }
        }
        return null;
    }

    private UiObject2 findGlobalExactTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        for (String label : localizeLabels(labels)) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            UiObject2 byDesc = mDevice.findObject(By.desc(exactPatternForLabel(label)));
            if (byDesc != null) {
                return byDesc;
            }
            UiObject2 byText = mDevice.findObject(By.text(exactPatternForLabel(label)));
            if (byText != null) {
                return byText;
            }
        }
        return null;
    }
    private int countStartDialogButtonsByGeometry() {
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            if (isStartDialogButtonCandidate(object)) {
                count++;
            }
        }
        return count;
    }

    private UiObject2 findStartDialogPrimaryButtonByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!isStartDialogButtonCandidate(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isStartDialogButtonCandidate(UiObject2 object) {
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            int centerX = bounds.centerX();
            return bounds.top >= (height * 68) / 100
                    && bounds.bottom <= (height * 86) / 100
                    // Localized "Start" actions can render as short text chips
                    // (e.g. "Starten"), so allow narrow widths while still
                    // constraining to centered controls near the bottom band.
                    && bounds.width() >= (width * 4) / 100
                    && bounds.width() <= (width * 65) / 100
                    && bounds.height() <= (height * 8) / 100
                    && centerX >= (width * 30) / 100
                    && centerX <= (width * 70) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findStartDialogQiraFooter() {
        UiObject2 qira = null;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String description = sanitizeQiraDescription(object.getContentDescription());
                if (!"qira".equalsIgnoreCase(description)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                int height = mDevice.getDisplayHeight();
                if (bounds.top >= (height * 85) / 100) {
                    return object;
                }
                if (qira == null) {
                    qira = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        if (qira == null) {
            return null;
        }
        try {
            Rect bounds = qira.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return null;
            }
            int height = mDevice.getDisplayHeight();
            return bounds.top >= (height * 85) / 100 ? qira : null;
        } catch (StaleObjectException stale) {
            return null;
        }
    }

    private String sanitizeQiraDescription(String description) {
        if (description == null) {
            return "";
        }
        return description
                .replace("\u200E", "")
                .replace("\u200F", "")
                .replace("\u202A", "")
                .replace("\u202B", "")
                .replace("\u202C", "")
                .replace("\u202D", "")
                .replace("\u202E", "")
                .replace("\u2066", "")
                .replace("\u2067", "")
                .replace("\u2068", "")
                .replace("\u2069", "")
                .trim();
    }

    public boolean waitForIntroBanner(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isIntroBannerVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isIntroBannerVisible() {
        return hasTextOrDescription(INTRO_BANNER_LABELS)
                || isOnboardingFooterArrowCardVisible();
    }

    public void tapIntroBannerArrow() throws Exception {
        if (!tapOnboardingFooterNextArrow()) {
            tapBannerArrow(INTRO_BANNER_LABELS);
        }
    }

    public boolean waitForLanguagePicker(long timeoutMs) throws Exception {
        return waitForTextOrDescription(timeoutMs, LANGUAGE_LABELS) != null;
    }

    public void waitForLanguagePicker() throws Exception {
        waitForSurface("the response language picker", LANGUAGE_LABELS);
    }

    /**
     * Waits for whichever onboarding screen actually appears after the intro arrow.
     * On devices where a Moto/Lenovo account is already signed in, Qira often auto-
     * selects a language and jumps straight from the intro arrow to the sign-in
     * ("Continue as ...") dialog. This method returns a label describing what came
     * next so the caller can decide which flow to run.
     *
     * @return one of "language", "productivity", "signin", or null on timeout.
     */
    public String waitForLanguagePickerOrSignIn(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int introTaps = 0;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isLanguagePickerVisible()) {
                settle();
                return "language";
            }
            if (isSignInDialogVisible()) {
                settle();
                return "signin";
            }
            if (isProductivityBannerVisible()) {
                settle();
                return "productivity";
            }
            if (isStartDialogVisible()) {
                tapStartDialog();
                mUtils.sleep(1200L);
                continue;
            }
            if (isIntroBannerVisible() && introTaps < 2) {
                tapIntroBannerArrow();
                introTaps++;
                mUtils.sleep(1500L);
                continue;
            }
            if (isUnknownQiraOnboardingSurface() && introTaps < 3) {
                ONBOARDING_LOGGER.info(
                        "Qira onboarding intro/arrow controls are not exposed by Compose; "
                                + "using footer-arrow coordinate fallback.");
                tapOnboardingFooterNextArrowByCoordinates();
                introTaps++;
                mUtils.sleep(1500L);
                continue;
            }

            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 5000L) {
                logVisibleLabelsForDiagnostics("waitForLanguagePickerOrSignIn stuck");
                lastDiagLog = now;
            }
            mUtils.sleep(300L);
        }

        logVisibleLabelsForDiagnostics("waitForLanguagePickerOrSignIn timeout");
        return null;
    }

    public void advanceToLanguagePicker() throws Exception {
        String screen = waitForLanguagePickerOrSignIn(30000L);
        if (!"language".equals(screen)) {
            throw new IllegalStateException(
                    "Unable to detect the response language picker (saw: " + screen + ")");
        }
    }

    public boolean isLanguagePickerVisible() {
        // Structural detection comes FIRST because it is completely
        // locale-independent: only the language picker shows a vertical
        // list of RadioButton rows inside the Qira package, paired with
        // a bottom-edge Next button. Every text anchor below relies on
        // QiraStrings expanding an English label via the Qira R.string
        // scan, but many of Qira's strings are %s templates (e.g.
        // "Choose a response language for %s") that never normalise to
        // the rendered text, so text-only detection silently fails on
        // non-English locales. The radios + Next geometry do not care
        // about language at all.
        if (countLanguagePickerRadioButtons() >= 2
                && findLanguagePickerNextButtonByGeometry() != null) {
            return true;
        }
        if (countLanguagePickerOptionTexts() >= 4
                && findLanguagePickerNextButtonByGeometry() != null
                && findStartDialogQiraFooter() != null) {
            return true;
        }
        if (countLanguagePickerOptionTexts() >= 4
                && findByExactTextOrDescription(LANGUAGE_PICKER_NEXT_LABELS) != null) {
            return true;
        }
        if (isLooseLanguagePickerVisible()) {
            return true;
        }

        // Text fallback: only reachable when structural detection misses
        // (partial render, stale layout). Guard against subsequent onboarding
        // screens whose dense label count or string-link aliases can otherwise
        // collide with the picker's text heuristics.
        if (isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionPanelVisible()
                || isPermissionBannerVisible()
                || isProductivityBannerVisible()) {
            return false;
        }
        if (hasTextOrDescription(LANGUAGE_LABELS)) {
            return true;
        }
        int optionsSeen = 0;
        for (String option : LANGUAGE_OPTION_LABELS) {
            if (findByTextOrDescription(option) != null) {
                optionsSeen++;
            }
        }
        return optionsSeen >= 2;
    }

    /**
     * Selects a response language on the Qira language picker based on
     * the device's current locale and advances past the picker.
     *
     * <p>Resolution order (the rule the master flow expects):
     * <ol>
     *   <li><b>Device-locale match.</b> Try to pick a row whose label
     *       corresponds to the device's full BCP-47 locale, e.g.
     *       {@code pt-BR &rarr;} "Portuguese (Brazil)" / "Português
     *       (Brasil)". Both the English and native display names are
     *       tried because Qira's picker can render either depending on
     *       the UI locale at the time the row was inflated.</li>
     *   <li><b>Language-only match.</b> If the exact country variant
     *       row is missing, accept any row that starts with the
     *       device's language display name, e.g. {@code pt-BR} falls
     *       through to any "Portuguese" / "Português" row.</li>
     *   <li><b>English country fallback.</b> When the device language
     *       itself is English but the specific country variant is
     *       absent, prefer "English (&lt;country&gt;)" for the device's
     *       region before dropping to the catch-all English default.</li>
     *   <li><b>English (United States).</b> Final translation-aware
     *       fallback that works on every Qira build shipped to date —
     *       this is the row Qira renders first in the list.</li>
     *   <li><b>Topmost row.</b> Absolute last resort so the onboarding
     *       flow can always progress; the dump-proven row order still
     *       puts an English option at the top.</li>
     * </ol>
     */
    public void chooseResponseLanguage() throws Exception {
        if (isLooseLanguagePickerVisible()) {
            chooseResponseLanguageLoosely();
            return;
        }
        ensureResponseLanguageSelected();
        advanceFromLanguagePicker();
    }

    /**
     * Back-compat alias: older capture scripts still call
     * {@code chooseEnglishUnitedStates()} because that was the only
     * option before the device-locale-aware flow landed. The new
     * behaviour still picks English (United States) as the final
     * fallback, so the semantics of existing scripts are preserved —
     * they simply now honour the device locale first.
     */
    public void chooseEnglishUnitedStates() throws Exception {
        chooseResponseLanguage();
    }

    private void ensureResponseLanguageSelected() throws Exception {
        Locale device = QiraStrings.getInstance().getCurrentLocale();

        // Tier 1: try a row that matches the device's current locale.
        if (selectLanguageRowForLocale(device)) {
            return;
        }

        // Tier 2: fall back to "English (United States)" via the
        // locale-aware helper; QiraStrings expands it via the per-locale
        // catalog (e.g. "Inglês (Estados Unidos)" on pt-BR, "Englisch
        // (Vereinigte Staaten)" on de, etc.).
        if (selectLanguageRowByLabels("English (United States)")) {
            return;
        }

        // Tier 3: accept any row that starts with the localized word
        // for "English". The three English variants always lead the
        // picker, so the topmost one is English (United States).
        UiObject2 englishLabel = findFirstRowStartingWith("English");
        if (englishLabel != null) {
            clickLanguageRowFor(englishLabel);
            return;
        }

        // Tier 4: absolute last resort. The dump-proven row order is
        // [en-US, en-GB, ...], so the topmost radio button is almost
        // always English (United States).
        UiObject2 firstRow = findFirstLanguagePickerRow();
        if (firstRow != null) {
            UiObject2 firstRadio = findAssociatedRadioButton(firstRow);
            if (firstRadio == null && isRadioButton(firstRow)) {
                firstRadio = firstRow;
            }
            if (firstRadio != null && firstRadio.isChecked()) {
                return;
            }
            clickObject(firstRow);
            return;
        }

        logVisibleLabelsForDiagnostics("ensureResponseLanguageSelected stuck");
        throw new IllegalStateException(
                "Unable to choose a response language (device locale="
                        + (device == null ? "?" : device.toLanguageTag()) + ")");
    }

    /**
     * Attempts to pick the language picker row that corresponds to
     * {@code device}. Returns {@code true} when a row was clicked (or
     * was already selected).
     */
    private boolean selectLanguageRowForLocale(Locale device) throws Exception {
        if (device == null || device.getLanguage() == null
                || device.getLanguage().isEmpty()) {
            return false;
        }

        List<String> candidates = buildLanguageRowCandidates(device);
        for (String anchor : candidates) {
            if (selectLanguageRowByLabels(anchor)) {
                return true;
            }
        }

        // Final sweep: any row whose label starts with the language
        // display name (in either the current locale or English).
        for (String startAnchor : buildLanguageStartAnchors(device)) {
            UiObject2 label = findFirstRowStartingWith(startAnchor);
            if (label != null) {
                clickLanguageRowFor(label);
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the ordered list of English anchors we ask
     * {@link #findByExactTextOrDescription} / {@link #findByTextOrDescription}
     * to expand when looking for the device-locale row. Anchors are
     * expressed in English because {@link QiraStrings#expandAll(String...)}
     * maps them to the current-locale translation via Qira's own string
     * table.
     */
    private List<String> buildLanguageRowCandidates(Locale device) {
        LinkedHashSet<String> out = new LinkedHashSet<>();

        // English "Language (Country)" display, e.g. "Portuguese (Brazil)".
        String enFull = device.getDisplayName(Locale.ENGLISH);
        if (enFull != null && !enFull.isEmpty()) {
            out.add(enFull);
        }

        // Native "Language (Country)" display, e.g. "Português (Brasil)"
        // — added as-is because Qira's picker typically renders native
        // names once a locale is active.
        String nativeFull = device.getDisplayName(device);
        if (nativeFull != null && !nativeFull.isEmpty()) {
            out.add(nativeFull);
        }

        // English language name only, e.g. "Portuguese".
        String enLang = device.getDisplayLanguage(Locale.ENGLISH);
        if (enLang != null && !enLang.isEmpty()) {
            out.add(enLang);
        }

        // Native language name only, e.g. "Português" / "中文" / "Русский".
        String nativeLang = device.getDisplayLanguage(device);
        if (nativeLang != null && !nativeLang.isEmpty()) {
            out.add(nativeLang);
        }

        addTierOneLocaleLanguageCandidates(device, out);

        // Explicit English-country preference so en-GB / en-AU / etc.
        // select their own row when available, and en-US otherwise.
        if ("en".equalsIgnoreCase(device.getLanguage())) {
            String country = device.getCountry();
            if (country != null && !country.isEmpty()) {
                String countryName = new Locale("en", country).getDisplayCountry(Locale.ENGLISH);
                if (countryName != null && !countryName.isEmpty()) {
                    out.add("English (" + countryName + ")");
                }
            }
        }

        return new ArrayList<>(out);
    }

    private void addTierOneLocaleLanguageCandidates(Locale device, LinkedHashSet<String> out) {
        String language = device.getLanguage();
        String country = device.getCountry();
        if ("en".equalsIgnoreCase(language)) {
            out.add("English (United Kingdom)");
            out.add("English (India)");
            out.add("English (United States)");
        } else if ("de".equalsIgnoreCase(language)) {
            out.add("German (Germany)");
            out.add("Deutsch (Deutschland)");
            out.add("Deutsch");
        } else if ("es".equalsIgnoreCase(language)) {
            if ("US".equalsIgnoreCase(country)) {
                out.add("Spanish (United States)");
                out.add("Espa\u00f1ol (Estados Unidos)");
            }
            out.add("Spanish (Spain)");
            out.add("Espa\u00f1ol (Espa\u00f1a)");
            out.add("Espa\u00f1ol");
        } else if ("fr".equalsIgnoreCase(language)) {
            out.add("French (France)");
            out.add("Fran\u00e7ais (France)");
            out.add("Fran\u00e7ais");
        } else if ("it".equalsIgnoreCase(language)) {
            out.add("Italian (Italy)");
            out.add("Italiano (Italia)");
            out.add("Italiano");
        } else if ("ja".equalsIgnoreCase(language)) {
            out.add("Japanese (Japan)");
            out.add("\u65e5\u672c\u8a9e (\u65e5\u672c)");
            out.add("\u65e5\u672c\u8a9e");
        } else if ("pl".equalsIgnoreCase(language)) {
            out.add("Polish (Poland)");
            out.add("Polski (Polska)");
            out.add("polski");
            out.add("Polski");
        } else if ("pt".equalsIgnoreCase(language)) {
            out.add("Portuguese (Brazil)");
            out.add("Portugu\u00eas (Brasil)");
            out.add("Portugu\u00eas");
        } else if ("ro".equalsIgnoreCase(language)) {
            out.add("Romanian");
            out.add("Rom\u00e2n\u0103");
            out.add("rom\u00e2n\u0103");
        } else if ("zh".equalsIgnoreCase(language)) {
            out.add("Chinese (Simplified)");
            out.add("Chinese (China)");
            out.add("\u7b80\u4f53\u4e2d\u6587");
            out.add("\u4e2d\u6587\uff08\u7b80\u4f53\uff09");
            out.add("\u4e2d\u6587 (\u7b80\u4f53)");
            out.add("\u4e2d\u6587");
        }
    }

    /** Same set trimmed down for the contains-based row search. */
    private List<String> buildLanguageStartAnchors(Locale device) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        String enLang = device.getDisplayLanguage(Locale.ENGLISH);
        if (enLang != null && !enLang.isEmpty()) {
            out.add(enLang);
        }
        String nativeLang = device.getDisplayLanguage(device);
        if (nativeLang != null && !nativeLang.isEmpty()) {
            out.add(nativeLang);
        }
        addTierOneLocaleLanguageCandidates(device, out);
        return new ArrayList<>(out);
    }

    /**
     * Exact-text / exact-desc row click. Deliberately avoids the
     * contains-based variant because several Qira builds list multiple
     * rows that share a language prefix ("English (United States)",
     * "English (United Kingdom)", ...) — a loose contains-match would
     * non-deterministically snap to whichever row UiAutomator happened
     * to index first. The caller is expected to fall back to
     * {@link #findFirstRowStartingWith(String)} for the containment
     * case, where we pick the topmost match instead.
     */
    private boolean selectLanguageRowByLabels(String... labels) throws Exception {
        UiObject2 hit = findByExactTextOrDescription(labels);
        if (hit == null) {
            return false;
        }
        clickLanguageRowFor(hit);
        return true;
    }

    private void clickLanguageRowFor(UiObject2 label) throws Exception {
        UiObject2 row = findClickableAncestor(label);
        if (row == null) {
            row = label.getParent();
        }
        UiObject2 radio = findAssociatedRadioButton(row != null ? row : label);
        if (radio != null && radio.isChecked()) {
            return;
        }
        clickObject(row != null ? row : label);
    }

    private void advanceFromLanguagePicker() throws Exception {
        long deadline = System.currentTimeMillis() + 20000L;
        int attempts = 0;
        while (System.currentTimeMillis() < deadline) {
            if (!isLanguagePickerVisible()) {
                settle();
                return;
            }
            if (attempts >= 5) {
                break;
            }
            boolean clicked = tapNextButtonOnLanguagePicker();
            attempts++;
            if (!clicked) {
                mUtils.sleep(350L);
                continue;
            }
            long perAttemptDeadline = System.currentTimeMillis() + 3000L;
            while (System.currentTimeMillis() < perAttemptDeadline) {
                if (!isLanguagePickerVisible()) {
                    settle();
                    return;
                }
                mUtils.sleep(250L);
            }
        }
        if (isLanguagePickerVisible()) {
            logVisibleLabelsForDiagnostics("chooseResponseLanguage stuck");
            throw new IllegalStateException("Unable to continue from the language picker");
        }
    }

    /**
     * Returns the topmost on-screen text/description whose value starts
     * with (or contains) any locale-expanded alias for {@code anchor}.
     * Used as a defensive fallback when the exact row label the test
     * expects does not match because a locale formats the entry
     * slightly differently than the static catalog predicted. "Topmost"
     * means smallest {@code bounds.top}, matching the natural reading
     * order of the Qira language picker.
     */
    private UiObject2 findFirstRowStartingWith(String anchor) {
        String[] aliases = localizeLabels(anchor);
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (String alias : aliases) {
            if (alias == null || alias.isEmpty()) {
                continue;
            }
            // Scope to Qira pkg and prefer description-first per the
            // multilingual locator rule set — an accidental system-toast
            // or launcher widget that mentions the alias substring would
            // otherwise outrank the real Qira label here.
            String pkg = mConfig.getPackageName();
            List<UiObject2> descMatches = mDevice.findObjects(
                    By.pkg(pkg).descContains(alias));
            List<UiObject2> textMatches = mDevice.findObjects(
                    By.pkg(pkg).textContains(alias));
            for (UiObject2 obj : descMatches) {
                try {
                    Rect r = obj.getVisibleBounds();
                    if (r != null && r.top < bestTop) {
                        best = obj;
                        bestTop = r.top;
                    }
                } catch (StaleObjectException ignored) {
                }
            }
            for (UiObject2 obj : textMatches) {
                try {
                    Rect r = obj.getVisibleBounds();
                    if (r != null && r.top < bestTop) {
                        best = obj;
                        bestTop = r.top;
                    }
                } catch (StaleObjectException ignored) {
                }
            }
        }
        return best;
    }

    private int countLanguagePickerRadioButtons() {
        int count = 0;
        for (UiObject2 radio : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.RadioButton"))) {
            if (isLanguagePickerRadio(radio)) {
                count++;
            }
        }
        return count;
    }

    private UiObject2 findFirstLanguagePickerRow() {
        UiObject2 radio = findTopmostLanguagePickerRadioButton();
        if (radio == null) {
            return null;
        }
        UiObject2 row = findClickableAncestor(radio);
        if (row == null) {
            row = radio.getParent();
        }
        return row != null ? row : radio;
    }

    private UiObject2 findTopmostLanguagePickerRadioButton() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 radio : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.RadioButton"))) {
            try {
                if (!isLanguagePickerRadio(radio)) {
                    continue;
                }
                Rect bounds = radio.getVisibleBounds();
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = radio;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isLanguagePickerRadio(UiObject2 object) {
        if (object == null || !isRadioButton(object)) {
            return false;
        }
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int height = mDevice.getDisplayHeight();
            return bounds.top >= (height * 20) / 100
                    && bounds.bottom <= (height * 88) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean isRadioButton(UiObject2 object) {
        try {
            String clazz = object.getClassName();
            return clazz != null && clazz.endsWith("RadioButton");
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findAssociatedRadioButton(UiObject2 root) {
        if (root == null) {
            return null;
        }
        for (UiObject2 child : root.getChildren()) {
            String clazz = child.getClassName();
            if (clazz != null && clazz.endsWith("RadioButton")) {
                return child;
            }
            UiObject2 nested = findAssociatedRadioButton(child);
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private boolean tapNextButtonOnLanguagePicker() throws Exception {
        // Prefer explicit next labels first (desc, then multilingual text).
        UiObject2 nextLabel = findByExactDescription("Next");
        if (nextLabel == null) {
            nextLabel = findByExactTextOrDescription(LANGUAGE_PICKER_NEXT_LABELS);
        }
        if (nextLabel != null) {
            UiObject2 clickable = findClickableAncestor(nextLabel);
            Rect labelBounds = nextLabel.getVisibleBounds();
            if (clickable != null) {
                Rect clickableBounds = clickable.getVisibleBounds();
                if (clickableBounds != null
                        && !clickableBounds.isEmpty()
                        && clickableBounds.width() <= labelBounds.width() * 6
                        && clickableBounds.height() <= labelBounds.height() * 6) {
                    clickObject(clickable);
                    return true;
                }
            }
            if (labelBounds != null && !labelBounds.isEmpty()) {
                mDevice.executeShellCommand(String.format(Locale.US,
                        "input tap %d %d", labelBounds.centerX(), labelBounds.centerY()));
                settle();
                return true;
            }
            clickObject(nextLabel);
            return true;
        }

        UiObject2 nextButton = findLanguagePickerNextButtonByGeometry();
        if (nextButton == null) {
            UiObject2 looseNext = findVisibleExactTextAnywhere(LANGUAGE_PICKER_NEXT_LABELS);
            if (looseNext != null && tapObjectBounds(looseNext)) {
                return true;
            }
            // Some builds reuse the footer arrow control instead of an inline
            // language-picker CTA button.
            return tapOnboardingFooterNextArrow();
        }
        clickObject(nextButton);
        return true;
    }

    private boolean isLooseLanguagePickerVisible() {
        if (isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionPanelVisible()
                || isHotwordSetupVisible()) {
            return false;
        }
        return countLooseLanguagePickerOptionTexts() >= 4
                && (findVisibleExactTextAnywhere(LANGUAGE_PICKER_NEXT_LABELS) != null
                || findVisibleTextContainsAnywhere("Motorola Qira") != null);
    }

    private void chooseResponseLanguageLoosely() throws Exception {
        Locale device = QiraStrings.getInstance().getCurrentLocale();
        if (!tapLooseLanguageRowForLocale(device)) {
            UiObject2 firstOption = findTopmostLooseLanguageOption();
            if (firstOption == null || !tapObjectBounds(firstOption)) {
                logVisibleLabelsForDiagnostics("chooseResponseLanguageLoosely stuck");
                throw new IllegalStateException("Unable to choose a response language from the visible picker");
            }
        }

        long deadline = System.currentTimeMillis() + 12000L;
        while (System.currentTimeMillis() < deadline) {
            if (!isLooseLanguagePickerVisible() && !isLanguagePickerVisible()) {
                settle();
                return;
            }
            UiObject2 next = findVisibleExactTextAnywhere(LANGUAGE_PICKER_NEXT_LABELS);
            if (next != null && tapObjectBounds(next)) {
                if (waitForLanguagePickerToDismiss(3000L)) {
                    return;
                }
            } else if (tapNextButtonOnLanguagePicker() && waitForLanguagePickerToDismiss(3000L)) {
                return;
            }
            mUtils.sleep(300L);
        }
        logVisibleLabelsForDiagnostics("chooseResponseLanguageLoosely timeout");
        throw new IllegalStateException("Unable to continue from the visible response language picker");
    }

    private boolean waitForLanguagePickerToDismiss(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isLooseLanguagePickerVisible() && !isLanguagePickerVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private boolean tapLooseLanguageRowForLocale(Locale device) throws Exception {
        for (String candidate : buildLanguageRowCandidates(device)) {
            UiObject2 option = findVisibleExactTextAnywhere(candidate);
            if (option != null && tapObjectBounds(option)) {
                return true;
            }
        }
        return false;
    }

    private UiObject2 findTopmostLooseLanguageOption() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                if (!isLooseLanguageOptionText(text)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (!isLooseLanguageOptionBounds(bounds)) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private int countLooseLanguagePickerOptionTexts() {
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                if (!isLooseLanguageOptionText(text)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (isLooseLanguageOptionBounds(bounds)) {
                    count++;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private boolean isLooseLanguageOptionText(String text) {
        if (text == null) {
            return false;
        }
        String clean = text.trim();
        if (clean.isEmpty()) {
            return false;
        }
        for (String label : LANGUAGE_OPTION_LABELS) {
            if (label != null && label.equalsIgnoreCase(clean)) {
                return true;
            }
        }
        return clean.contains("(") || clean.contains("（");
    }

    private boolean isLooseLanguageOptionBounds(Rect bounds) {
        if (bounds == null || bounds.isEmpty()) {
            return false;
        }
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        return bounds.top >= (height * 18) / 100
                && bounds.bottom <= (height * 80) / 100
                && bounds.width() >= (width * 20) / 100
                && bounds.centerX() >= (width * 20) / 100
                && bounds.centerX() <= (width * 80) / 100;
    }

    private UiObject2 findVisibleExactTextAnywhere(String... labels) {
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                String desc = sanitizeQiraDescription(object.getContentDescription());
                for (String label : labels) {
                    if (label == null || label.isEmpty()) {
                        continue;
                    }
                    if (label.equals(text) || label.equals(desc)) {
                        return object;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return null;
    }

    private UiObject2 findVisibleTextContainsAnywhere(String label) {
        if (label == null || label.isEmpty()) {
            return null;
        }
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                if (text != null && text.contains(label)) {
                    return object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return null;
    }

    private int countLanguagePickerOptionTexts() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int minTop = (height * 20) / 100;
        int maxBottom = (height * 78) / 100;
        int minWidth = (width * 22) / 100;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                if (object == null || object.getText() == null || object.getText().trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < minTop || bounds.bottom > maxBottom) {
                    continue;
                }
                if (bounds.width() < minWidth) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private UiObject2 findLanguagePickerNextButtonByGeometry() {
        UiObject2 best = null;
        int bestScore = Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int minTop = (height * 74) / 100;
        int maxBottom = (height * 92) / 100;
        int minRightHalfCenter = (width * 50) / 100;
        int minWidth = (width * 4) / 100;
        int maxWidth = (width * 55) / 100;
        int maxHeight = (height * 12) / 100;
        int targetCenterY = (height * 82) / 100;

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < minTop || bounds.bottom > maxBottom) {
                    continue;
                }
                if (bounds.centerX() < minRightHalfCenter) {
                    continue;
                }
                if (bounds.width() < minWidth || bounds.width() > maxWidth
                        || bounds.height() > maxHeight) {
                    continue;
                }
                // Exclude locale rows and radio controls from the candidate
                // set; otherwise the right-aligned radio button can outrank
                // the actual "Next/Continue" CTA on localized pickers.
                if (object.isCheckable()
                        || isRadioButton(object)
                        || hasDescendantClass(object, "android.widget.RadioButton", 2)) {
                    continue;
                }
                int score = bounds.centerY() * 10
                        + bounds.centerX()
                        - Math.abs(bounds.centerY() - targetCenterY);
                if (score > bestScore) {
                    bestScore = score;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    public boolean waitForProductivityBanner(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (hasTextOrDescription(PRODUCTIVITY_BANNER_LABELS)
                    || isOnboardingFooterArrowCardVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isProductivityBannerVisible() {
        return hasTextOrDescription(PRODUCTIVITY_BANNER_LABELS);
    }

    public void tapProductivityBannerArrow() throws Exception {
        if (!tapOnboardingFooterNextArrow()) {
            tapBannerArrow(PRODUCTIVITY_BANNER_LABELS);
        }
    }

    public void advanceToSignInDialog() throws Exception {
        long deadline = System.currentTimeMillis() + 30000L;
        int bannerTaps = 0;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isSignInDialogVisible()) {
                settle();
                return;
            }
            // Some locales / account states skip the explicit sign-in dialog and
            // advance directly to later onboarding surfaces or home tiles.
            if (isAcknowledgeDialogVisible()
                    || isPermissionPanelVisible()
                    || isHotwordSetupVisible()
                    || isExploreStartVisible()
                    || isFeatureGridVisible()) {
                settle();
                return;
            }
            if (isStartDialogVisible()) {
                tapStartDialog();
                mUtils.sleep(1200L);
                continue;
            }
            if (isProductivityBannerVisible()) {
                bannerTaps++;
                try {
                    tapProductivityBannerArrow();
                } catch (IllegalStateException bannerGone) {
                    if (isSignInDialogVisible()
                            || isStartDialogVisible()
                            || isAcknowledgeDialogVisible()
                            || isPermissionPanelVisible()
                            || isHotwordSetupVisible()
                            || isExploreStartVisible()
                            || isFeatureGridVisible()) {
                        mUtils.sleep(600L);
                        continue;
                    }
                    throw bannerGone;
                }
                mUtils.sleep(1500L);
                continue;
            }
            if (isOnboardingFooterArrowCardVisible()) {
                if (tapOnboardingFooterNextArrow()) {
                    bannerTaps++;
                    mUtils.sleep(1500L);
                    continue;
                }
            }
            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 5000L) {
                logVisibleLabelsForDiagnostics("advanceToSignInDialog stuck");
                lastDiagLog = now;
            }
            mUtils.sleep(300L);
        }
        if (isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionPanelVisible()
                || isHotwordSetupVisible()
                || isExploreStartVisible()
                || isFeatureGridVisible()) {
            settle();
            return;
        }
        logVisibleLabelsForDiagnostics("advanceToSignInDialog timeout");
        throw new IllegalStateException("Unable to detect the Moto account sign-in dialog");
    }

    public void waitForSignInDialog() throws Exception {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isSignInDialogVisible()) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }
        logVisibleLabelsForDiagnostics("waitForSurface the Moto account sign-in dialog timeout");
        throw new IllegalStateException("Unable to detect the Moto account sign-in dialog");
    }

    public boolean isSignInDialogVisible() {
        if (hasTextOrDescription(SIGN_IN_LABELS)) {
            return true;
        }
        return findByStableDescription("Profile Picture") != null
                && findSignInEmailText() != null
                && findContinueAsActionByGeometry() != null;
    }

    public void continueAs() throws Exception {
        long deadline = System.currentTimeMillis() + 20000L;
        while (System.currentTimeMillis() < deadline) {
            if (!isSignInDialogVisible()) {
                if (isAcknowledgeDialogVisible()
                        || isPermissionBannerVisible()
                        || isPermissionPanelVisible()) {
                    settle();
                    return;
                }
            }
            UiObject2 button = findContinueAsButton();
            if (button != null) {
                // Prefer the clickable ancestor when the text node sits on a
                // non-clickable Compose Text inside a clickable Card / Row.
                UiObject2 clickable = findClickableAncestor(button);
                UiObject2 target = clickable != null ? clickable : button;
                if (tapObjectBounds(target)) {
                    if (waitForSignInDialogToDismiss(1500L)) {
                        return;
                    }
                }
            }
            if (tapContinueAsByCoordinates() && waitForSignInDialogToDismiss(5000L)) {
                return;
            }
            // Last-ditch coarse pixel tap for locales where the action row
            // bounds escape every geometric heuristic above (long localized
            // CTA labels like "Fortfahren als <name>" can render the row at
            // unusual widths). The Sign-In primary CTA always lives in the
            // bottom 8-12% band centered horizontally.
            try {
                int width = mDevice.getDisplayWidth();
                int height = mDevice.getDisplayHeight();
                mDevice.click(width / 2, (height * 90) / 100);
                settle();
                if (waitForSignInDialogToDismiss(2000L)) {
                    return;
                }
            } catch (Throwable ignored) {
            }
            mUtils.sleep(400L);
        }
        logVisibleLabelsForDiagnostics("continueAs stuck");
        throw new IllegalStateException("Unable to continue with the Moto account dialog");
    }

    private UiObject2 findContinueAsButton() {
        UiObject2 label = findContinueAsLabelBelowEmailByGeometry();
        if (label != null) {
            return label;
        }
        // Locale-specific fallbacks for the Sign-In dialog primary CTA.
        // The English anchor "Continue as" is missing from the runtime
        // resource cache for several locales, so we explicitly enumerate
        // the localized button labels seen in production builds. Each
        // entry is matched by substring against text + content-desc, so
        // trailing-space variants ("Fortfahren als ") still hit.
        UiObject2 button = findByTextOrDescription(
                "Continue as",
                // German.
                "Fortfahren als",
                // French.
                "Continuer en tant que",
                "Continuer comme",
                // Spanish.
                "Continuar como",
                // Italian.
                "Continua come",
                // Portuguese (Brazil).
                "Continuar como",
                // Polish.
                "Kontynuuj jako",
                // Romanian.
                "Continu\u0103 ca",
                "Continua\u021b ca",
                // Japanese.
                "\u3068\u3057\u3066\u7d9a\u884c",
                // Chinese (Simplified).
                "\u7ee7\u7eed\u4f7f\u7528");
        if (button != null) {
            return button;
        }
        return findContinueAsActionByGeometry();
    }

    private boolean waitForSignInDialogToDismiss(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isSignInDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    private UiObject2 findSignInEmailText() {
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || !text.contains("@")) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                int height = mDevice.getDisplayHeight();
                if (bounds.top >= (height * 60) / 100
                        && bounds.bottom <= (height * 92) / 100) {
                    return object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return null;
    }

    private UiObject2 findContinueAsActionByGeometry() {
        UiObject2 label = findContinueAsLabelBelowEmailByGeometry();
        if (label != null) {
            return label;
        }
        UiObject2 button = findContinueAsButtonByGeometry();
        if (button != null) {
            return button;
        }
        return null;
    }

    private UiObject2 findContinueAsButtonByGeometry() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 78) / 100
                        || bounds.bottom > (height * 92) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 25) / 100
                        || bounds.width() > (width * 70) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 30) / 100
                        || bounds.centerX() > (width * 70) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findContinueAsLabelBelowEmailByGeometry() {
        UiObject2 email = findSignInEmailText();
        if (email == null) {
            return null;
        }
        Rect emailBounds;
        try {
            emailBounds = email.getVisibleBounds();
        } catch (StaleObjectException stale) {
            return null;
        }
        if (emailBounds == null || emailBounds.isEmpty()) {
            return null;
        }

        UiObject2 best = null;
        int bestY = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty() || text.contains("@")) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top <= emailBounds.bottom
                        || bounds.top < (height * 76) / 100
                        || bounds.bottom > (height * 95) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 25) / 100
                        || bounds.centerX() > (width * 75) / 100) {
                    continue;
                }
                if (bounds.top < bestY) {
                    bestY = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean tapContinueAsByCoordinates() throws Exception {
        UiObject2 email = findSignInEmailText();
        if (email == null) {
            return false;
        }
        try {
            Rect emailBounds = email.getVisibleBounds();
            if (emailBounds == null || emailBounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            int y = Math.max((height * 89) / 100, emailBounds.bottom + ((height * 5) / 100));
            y = Math.min(y, (height * 93) / 100);
            mDevice.click(width / 2, y);
            settle();
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean tapObjectBounds(UiObject2 object) throws Exception {
        if (object == null) {
            return false;
        }
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            mDevice.click(bounds.centerX(), bounds.centerY());
            settle();
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    public void waitForAcknowledgeDialog() throws Exception {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isAcknowledgeDialogVisible()) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }
        logAcknowledgeDialogShapeDiagnostics();
        logVisibleLabelsForDiagnostics("waitForSurface the Moto account acknowledgement dialog timeout");
        throw new IllegalStateException("Unable to detect the Moto account acknowledgement dialog");
    }

    public boolean isAcknowledgeDialogVisible() {
        // NEGATIVE EXCLUSION FIRST.
        //
        // Every other onboarding surface has at least one unique text
        // marker that NEVER appears on the Moto-account acknowledge
        // dialog. Returning false up front as soon as one of those
        // markers is visible guarantees that
        // waitForAcknowledgeDialogToDismiss() promptly returns true when
        // the dialog transitions to the next surface, regardless of
        // what ACKNOWLEDGE_LABELS's substring match happens to pick up
        // on the new screen.
        //
        // The language picker is the RadioButton-backed surface
        // immediately preceding acknowledge. Any radio on screen is
        // proof we are NOT on the acknowledge dialog.
        if (countLanguagePickerRadioButtons() > 0) {
            return false;
        }
        // The permission-review panel that follows the acknowledge tap
        // shares the Qira footer + dense label count that the geometry
        // heuristic below uses, so without this check the heuristic
        // would keep reporting the acknowledge dialog as visible while
        // the permission panel is actually on screen. Use the dedicated
        // permission-panel detector here (text + structural fallback)
        // so non-English locales are handled as well.
        if (isPermissionPanelVisible()) {
            return false;
        }
        // POSITIVE DETECTION.
        if (hasTextOrDescription(ACKNOWLEDGE_LABELS)) {
            return true;
        }
        int contentLabels = countAcknowledgeContentLabelsByGeometry();
        int denseLabels = countAcknowledgeDenseLabelsByGeometry();
        return (findStartDialogQiraFooter() != null || hasAcknowledgeFooterTitleByGeometry())
                && (contentLabels >= ACKNOWLEDGE_MIN_CONTENT_LABELS || denseLabels >= 12);
    }

    public boolean enableBackupPreviousMemoriesIfPresent() throws Exception {
        if (!isAcknowledgeDialogVisible()) {
            return false;
        }
        UiObject2 toggle = findAcknowledgeBackupMemoryToggle();
        if (toggle == null) {
            return false;
        }
        try {
            if (!toggle.isEnabled()) {
                return false;
            }
            if (!toggle.isChecked()) {
                clickObject(toggle);
                settle();
            }
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findAcknowledgeBackupMemoryToggle() {
        UiObject2 label = findByTextOrDescription(ACKNOWLEDGE_BACKUP_MEMORY_LABELS);
        UiObject2 toggle = label != null ? findAssociatedToggle(label) : null;
        if (toggle != null) {
            return toggle;
        }
        return findAcknowledgeBackupMemoryToggleByGeometry();
    }

    private UiObject2 findAcknowledgeBackupMemoryToggleByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 42) / 100
                        || bounds.bottom > (height * 68) / 100) {
                    continue;
                }
                if (!isLogicalEndX(bounds.centerX(), width, 62, 94)) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }
    public void acknowledge() throws Exception {
        // Some locales keep the legal-notes body tall enough that the
        // primary action stays functionally disabled until we scroll near
        // the end. Give this step a larger budget than generic surfaces.
        long deadline = System.currentTimeMillis() + 60000L;
        // Pre-scroll several pages first so the CTA can become interactable
        // before we spend time on tap retries. We deliberately do NOT
        // short-circuit on the first failed enablement check — on some
        // locales/builds the enablement signal is delayed and we need
        // the notes pane fully paginated before tapping. 12 pre-scrolls
        // with a small pause between each keeps the gesture budget under
        // ~5s while still giving slow Compose builds time to react.
        //
        // REVERT NOTE: an attempt to early-exit on
        // isAcknowledgeConfirmActionEnabled() and trim the loop count
        // regressed de-DE; the longer legal text appears to satisfy the
        // enabled-check sooner than the actual scroll completes, leading
        // to a stale anchor and a stray tap that ended up on "Quit
        // setup?". Restoring the deterministic 12 scrolls × 150ms wait.
        for (int i = 0; i < 12 && System.currentTimeMillis() < deadline; i++) {
            if (!isAcknowledgeDialogVisible()) {
                settle();
                return;
            }
            if (!scrollAcknowledgeDialogDown(findAcknowledgeConfirmButtonByGeometry())) {
                break;
            }
            mUtils.sleep(150L);
        }
        while (System.currentTimeMillis() < deadline) {
            if (!isAcknowledgeDialogVisible()) {
                settle();
                return;
            }
            if (tapAcknowledgeConfirmOnce(1500L)) {
                return;
            }

            UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
            if (scrollAcknowledgeDialogDown(confirmButton)) {
                continue;
            }
            mUtils.sleep(300L);
        }
        logAcknowledgeDialogShapeDiagnostics();
        throw new IllegalStateException("Unable to acknowledge the Moto account notes");
    }

    private boolean tapAcknowledgeConfirmOnce(long dismissWaitMs) throws Exception {
        UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
        if (confirmButton != null) {
            try {
                clickObject(confirmButton);
                if (waitForAcknowledgeDialogToDismiss(dismissWaitMs)) {
                    return true;
                }
            } catch (StaleObjectException | IllegalStateException ignored) {
                // Re-query below and retry from an alternate anchor.
            }
            try {
                Rect bounds = confirmButton.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), bounds.centerY()));
                    settle();
                    if (waitForAcknowledgeDialogToDismiss(dismissWaitMs)) {
                        return true;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }

        UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
        if (confirmLabel != null) {
            try {
                Rect bounds = confirmLabel.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), bounds.centerY()));
                    settle();
                    if (waitForAcknowledgeDialogToDismiss(dismissWaitMs)) {
                        return true;
                    }
                    int pad = Math.max(12, bounds.height() / 2);
                    int y = Math.min(mDevice.getDisplayHeight() - 4, bounds.bottom + pad);
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), y));
                    settle();
                    if (waitForAcknowledgeDialogToDismiss(dismissWaitMs)) {
                        return true;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }

        UiObject2 exactLabel = findByExactTextOrDescription("Ich stimme zu");
        if (exactLabel == null) {
            exactLabel = findByExactTextOrDescription("I acknowledge", "Accept");
        }
        if (exactLabel != null) {
            try {
                Rect bounds = exactLabel.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), bounds.centerY()));
                    settle();
                    if (waitForAcknowledgeDialogToDismiss(dismissWaitMs)) {
                        return true;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return false;
    }

    /**
     * Public helper for capture scripts that need an explicit
     * "Acknowledge_Scrolled" screenshot. Scrolls the notes pane down a few
     * times (or until the confirm action becomes enabled), then returns
     * {@code true} when at least one scroll occurred so callers know to take
     * the post-scroll screenshot. Returns {@code false} if the dialog is
     * already past acknowledge (no work to do).
     */
    public boolean scrollAcknowledgeDialogForCapture() throws Exception {
        if (!isAcknowledgeDialogVisible()) {
            return false;
        }
        boolean scrolled = false;
        for (int attempt = 0; attempt < 4; attempt++) {
            if (isAcknowledgeConfirmActionEnabled()) {
                break;
            }
            if (!scrollAcknowledgeDialogDown(findAcknowledgeConfirmButtonByGeometry())) {
                break;
            }
            scrolled = true;
            mUtils.sleep(300L);
        }
        // Always do at least one scroll so the screenshot reflects the
        // scrolled-down state even on fast devices where the button is
        // already enabled by the time we arrive here.
        if (!scrolled) {
            scrolled = scrollAcknowledgeDialogDown(findAcknowledgeConfirmButtonByGeometry());
            mUtils.sleep(200L);
        }
        return scrolled;
    }

    private boolean isAcknowledgeConfirmActionEnabled() {
        try {
            UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
            if (confirmButton != null) {
                return confirmButton.isEnabled();
            }
            UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
            if (confirmLabel != null) {
                UiObject2 clickable = findClickableAncestor(confirmLabel);
                return clickable != null ? clickable.isEnabled() : confirmLabel.isEnabled();
            }
        } catch (StaleObjectException ignored) {
        }
        return false;
    }

    /**
     * Scrolls the Moto-account acknowledge notes pane down so the confirm
     * action becomes enabled. The gesture is constrained to the dialog's
     * upper notes band — between the title/header and the legal-disclaimer
     * divider — because that is the only region of the dialog that
     * paginates the bullet list (a swipe started inside the legal text or
     * the bottom action row never advances the underlying scroll state).
     *
     * <p>The previously-working anchors for this dialog are:
     * <ul>
     *   <li>{@code x} = dialog center (≈ 66% of display width on the Razr
     *       fold; mirrors the confirm button's centerX so we stay on the
     *       dialog instead of the screen midpoint)</li>
     *   <li>{@code startY} ≈ 53% of display height (just above the legal
     *       divider at ~60%)</li>
     *   <li>{@code endY} ≈ 29% of display height (top of the bullet list
     *       area, well below the dialog header)</li>
     *   <li>swipe duration ≈ 250ms via {@code adb shell input swipe}</li>
     * </ul>
     * Those values are restored as the defaults; any legal/action anchors
     * we can detect on the live dialog further clamp {@code startY} so the
     * gesture never crosses into the disclaimer or button row.
     */
    private boolean scrollAcknowledgeDialogDown(UiObject2 anchor) throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();

        int legalTop = findAcknowledgeLegalSectionTop();
        int actionRowTop = findAcknowledgeActionRowTop();

        // Anchor x to the DIALOG center, NOT the screen center. The
        // acknowledge dialog on the Razr fold sits offset to the right of
        // the display midpoint, and a screen-center swipe lands on a
        // non-scrollable column and never paginates the notes pane.
        // Preference order:
        //   1. anchor (caller-supplied, typically confirm button) centerX
        //   2. confirm button geometry centerX
        //   3. confirm label centerX
        //   4. Qira footer centerX (bottom-of-dialog title chip)
        //   5. previously-working default of 66% of display width
        int x = (width * 66) / 100;
        int dialogCenterX = -1;
        try {
            if (anchor != null) {
                Rect bounds = anchor.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    dialogCenterX = bounds.centerX();
                }
            }
            if (dialogCenterX < 0) {
                UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
                if (confirmButton != null) {
                    Rect bounds = confirmButton.getVisibleBounds();
                    if (bounds != null && !bounds.isEmpty()) {
                        dialogCenterX = bounds.centerX();
                    }
                }
            }
            if (dialogCenterX < 0) {
                UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
                if (confirmLabel != null) {
                    Rect bounds = confirmLabel.getVisibleBounds();
                    if (bounds != null && !bounds.isEmpty()) {
                        dialogCenterX = bounds.centerX();
                    }
                }
            }
            if (dialogCenterX < 0) {
                UiObject2 footer = findStartDialogQiraFooter();
                if (footer != null) {
                    Rect bounds = footer.getVisibleBounds();
                    if (bounds != null && !bounds.isEmpty()) {
                        dialogCenterX = bounds.centerX();
                    }
                }
            }
        } catch (StaleObjectException ignored) {
        }
        if (dialogCenterX > 0) {
            x = dialogCenterX;
        }

        // Default swipe band: previously-working values for this dialog.
        // startY = 53% of height, endY = 29% of height. Both are well
        // inside the upper notes pane (above the legal divider at ~60%
        // and well below the dialog header at ~10%).
        int startY = (height * 53) / 100;
        int endY = (height * 29) / 100;

        // Clamp startY above the legal divider whenever we can detect it
        // (the user explicitly called this out: never include the legal
        // section in the gesture). The pad is large enough to absorb
        // rendering jitter on the divider line.
        if (legalTop > 0) {
            int legalPad = Math.max(40, height / 50);
            startY = Math.min(startY, legalTop - legalPad);
        }
        // Clamp startY above the action row whenever we can detect it.
        if (actionRowTop > 0) {
            int actionPad = Math.max(40, height / 30);
            startY = Math.min(startY, actionRowTop - actionPad);
        }

        // Structural fallback: if we never found a legal anchor BUT we
        // know where the action row is, infer the legal divider as a
        // fixed offset above the action row. This keeps us scrolling
        // above the divider on locales where the legal text isn't
        // recognised by keyword detection.
        if (legalTop <= 0 && actionRowTop > 0) {
            int inferredLegalTop = actionRowTop - (actionRowTop * 18) / 100;
            startY = Math.min(startY, inferredLegalTop - Math.max(40, height / 50));
        }

        // Final safety: ensure we have a meaningful gesture distance.
        // If clamping crushed the band, restore the previously-working
        // defaults rather than emitting a degenerate (zero-distance)
        // swipe that fails to paginate. Re-apply the legal/action clamps
        // so we still respect the divider/action boundaries.
        if (startY - endY < (height * 12) / 100) {
            startY = (height * 53) / 100;
            endY = (height * 29) / 100;
            if (legalTop > 0) {
                int legalPad = Math.max(40, height / 50);
                startY = Math.min(startY, legalTop - legalPad);
            }
            if (actionRowTop > 0) {
                int actionPad = Math.max(40, height / 30);
                startY = Math.min(startY, actionRowTop - actionPad);
            }
        }

        ONBOARDING_LOGGER.info(String.format(Locale.US,
                "Acknowledge: swipe marked area (x=%d,startY=%d,endY=%d,legalTop=%d,actionTop=%d)",
                x, startY, endY, legalTop, actionRowTop));

        try {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "input swipe %d %d %d %d 250", x, startY, x, endY));
        } catch (Throwable shellFailure) {
            mDevice.swipe(x, startY, x, endY, 25);
        }
        settle();
        mUtils.sleep(220L);
        return true;
    }

    /**
     * Locates the scrollable notes container on the Moto-account acknowledge
     * dialog. Unlike the permission panel, the acknowledge notes pane sits
     * higher on the screen (typically 12-65% vertical band) and is exposed
     * by Compose with {@code scrollable=true}. Picks the largest such
     * container in the upper-middle band.
     */
    private UiObject2 findAcknowledgeScrollableContainer() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).scrollable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Acknowledge notes pane sits well above the action row.
                if (bounds.top < (height * 8) / 100
                        || bounds.bottom > (height * 80) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 18) / 100
                        || bounds.centerX() > (width * 82) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 15) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    /**
     * Returns the topmost y of the bottom action row (Cancel + Accept) on the
     * acknowledge dialog. Used so {@link #scrollAcknowledgeDialogDown()} never
     * swipes from inside the buttons (which doesn't paginate the notes
     * scrollview).
     */
    private int findAcknowledgeActionRowTop() {
        int top = -1;
        UiObject2 confirm = findAcknowledgeConfirmButtonByGeometry();
        if (confirm != null) {
            try {
                Rect bounds = confirm.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    top = bounds.top;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
        if (confirmLabel != null) {
            try {
                Rect bounds = confirmLabel.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    top = top < 0 ? bounds.top : Math.min(top, bounds.top);
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return top;
    }

    /**
     * Returns the topmost y-coordinate of the legal/disclaimer text divider on
     * the acknowledge dialog. Used to keep scrolling above the legal section
     * so we do not paginate into the action row. Returns -1 when no legal
     * divider can be detected.
     *
     * <p>Detection strategy (in order):
     * <ol>
     *   <li>Keyword match against known legal/policy phrases in many
     *       locales — including the German "Mit dem Fortfahren erkläre
     *       ich mich damit einverstanden" preamble. The German variant
     *       was previously not detected, which made the structural
     *       fallback below pick a too-low anchor and crowded the swipe
     *       into the legal/action band.</li>
     *   <li>Structural fallback: a long, multi-line text block whose top
     *       sits below ~50% of the dialog height. The acknowledge dialog
     *       always has the legal disclaimer immediately above the action
     *       row, so the lowest long text block in the body is the
     *       disclaimer regardless of language.</li>
     * </ol>
     */
    private int findAcknowledgeLegalSectionTop() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int keywordBestTop = -1;
        int structuralBestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 30) / 100 || bounds.bottom > (height * 92) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 10) / 100
                        || bounds.centerX() > (width * 90) / 100) {
                    continue;
                }
                String trimmed = text.trim();
                String clean = sanitizeQiraDescription(trimmed).toLowerCase(Locale.ROOT);
                if (clean.length() < 24) {
                    continue;
                }
                boolean keywordMatch = clean.contains("privacy")
                        || clean.contains("legal")
                        || clean.contains("terms")
                        || clean.contains("notice")
                        || clean.contains("policy")
                        || clean.contains("rechtlich")
                        || clean.contains("datenschutz")
                        || clean.contains("conditions")
                        || clean.contains("politique")
                        // German preamble that precedes the legal links on
                        // de-DE builds: "Mit dem Fortfahren erkläre ich mich
                        // damit einverstanden". Substrings chosen to survive
                        // umlaut handling (kläre vs. erklare) and bidi marks.
                        || clean.contains("fortfahren")
                        || clean.contains("einverstanden")
                        || clean.contains("nutzungsbedingungen")
                        // Romance/Iberian variants seen on pt-BR / it-IT /
                        // fr-FR / es-ES legal preambles.
                        || clean.contains("aceito")
                        || clean.contains("aceitar os termos")
                        || clean.contains("accetto")
                        || clean.contains("acepto")
                        || clean.contains("j'accepte")
                        || clean.contains("politica")
                        || clean.contains("informativ");
                if (keywordMatch) {
                    if (keywordBestTop < 0 || bounds.top < keywordBestTop) {
                        keywordBestTop = bounds.top;
                    }
                    continue;
                }
                // Structural fallback: long text block sitting below the
                // mid-line of the dialog body. The disclaimer is always
                // a multi-line paragraph that wraps across most of the
                // dialog width; bullet copy is shorter and not wrapped
                // with multiple lines this far down.
                int wrappedHeight = bounds.height();
                int spannedWidth = bounds.width();
                if (spannedWidth < (width * 35) / 100) {
                    continue;
                }
                if (wrappedHeight < (height * 5) / 100) {
                    continue;
                }
                if (trimmed.length() < 50) {
                    continue;
                }
                if (bounds.top < (height * 50) / 100) {
                    continue;
                }
                if (bounds.top < structuralBestTop) {
                    structuralBestTop = bounds.top;
                }
            } catch (StaleObjectException ignored) {
                // Compose node recycled during scan.
            }
        }
        if (keywordBestTop > 0) {
            return keywordBestTop;
        }
        return structuralBestTop == Integer.MAX_VALUE ? -1 : structuralBestTop;
    }

    private boolean waitForAcknowledgeDialogToDismiss(long timeoutMs) throws Exception {
        long postClick = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < postClick) {
            if (!isAcknowledgeDialogVisible() || isPostAcknowledgeSurfaceVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private boolean isPostAcknowledgeSurfaceVisible() {
        return isPermissionPanelVisible()
                || isHotwordSetupVisible()
                || isExploreStartVisible()
                || isFeatureGridVisible();
    }

    private boolean tapAcknowledgeByCoordinates() throws Exception {
        UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
        if (confirmButton != null) {
            try {
                Rect bounds = confirmButton.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.click(bounds.centerX(), bounds.centerY());
                    settle();
                    return true;
                }
            } catch (StaleObjectException stale) {
                return false;
            }
        }

        UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
        if (confirmLabel != null) {
            return tapObjectBounds(confirmLabel);
        }
        return false;
    }

    private boolean tapAcknowledgeByShellCoordinates() throws Exception {
        UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
        if (confirmButton != null) {
            try {
                Rect bounds = confirmButton.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), bounds.centerY()));
                    settle();
                    return true;
                }
            } catch (StaleObjectException stale) {
                return false;
            }
        }

        UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
        if (confirmLabel != null) {
            try {
                Rect bounds = confirmLabel.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US,
                            "input tap %d %d", bounds.centerX(), bounds.centerY()));
                    settle();
                    return true;
                }
            } catch (StaleObjectException stale) {
                return false;
            }
        }
        return false;
    }

    private int countAcknowledgeContentLabelsByGeometry() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String text = object.getText();
                String description = sanitizeQiraDescription(object.getContentDescription());
                if ((text == null || text.trim().isEmpty()) && description.isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 20) / 100
                        || bounds.bottom > (height * 78) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 20) / 100
                        || bounds.centerX() > (width * 80) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private int countAcknowledgeDenseLabelsByGeometry() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                if (!hasVisibleTextOrDescription(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 10) / 100
                        || bounds.bottom > (height * 92) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 10) / 100
                        || bounds.centerX() > (width * 90) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private int countAcknowledgeActionButtonsByGeometry() {
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            if (isAcknowledgeActionButtonCandidate(object)) {
                count++;
            }
        }
        return count;
    }

    private int countAcknowledgeActionLabelsByGeometry() {
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            if (isAcknowledgeActionLabelCandidate(object)) {
                count++;
            }
        }
        return count;
    }

    private UiObject2 findAcknowledgeConfirmButtonByGeometry() {
        UiObject2 bestStyledPrimary = null;
        UiObject2 bestEdgeButton = null;
        // IMPORTANT: we use the device UI layout direction (read off Qira's
        // own Resources via QiraStrings), NOT Locale.getDefault(). The
        // master capture flow runs without -e qira.locale, which means
        // the instrumentation process's default Locale stays en-US even
        // though the device is in ar-EG. Reading RTL off Locale.getDefault()
        // therefore picks the wrong edge in Arabic and clicks the back
        // arrow instead of the primary action.
        boolean rtl = isDeviceRtlLayout();
        int bestStyledEdge = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int bestEdge = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!isAcknowledgeActionButtonCandidate(object)) {
                    continue;
                }
                if (containsAcknowledgeCancelText(object, 3)) {
                    continue;
                }
                if (isAcknowledgeBackAffordance(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                int edge = bounds.centerX();
                if (isBetterAcknowledgeEdge(edge, bestEdge, rtl)) {
                    bestEdge = edge;
                    bestEdgeButton = object;
                }

                // In the Compose dumps, the secondary cancel action contains
                // a decorative Button child while the primary action is the
                // plain trailing clickable. Prefer that signal, then fall back
                // to the layout-direction edge if the structure changes.
                if (!hasDescendantClass(object, "android.widget.Button", 2)
                        && isBetterAcknowledgeEdge(edge, bestStyledEdge, rtl)) {
                    bestStyledEdge = edge;
                    bestStyledPrimary = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return bestStyledPrimary != null ? bestStyledPrimary : bestEdgeButton;
    }

    private UiObject2 findAcknowledgeConfirmLabelByGeometry() {
        UiObject2 best = null;
        boolean rtl = isDeviceRtlLayout();
        int bestEdge = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                if (!isAcknowledgeActionLabelCandidate(object)) {
                    continue;
                }
                if (containsAcknowledgeCancelText(object, 1)) {
                    continue;
                }
                if (isAcknowledgeBackAffordance(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                int edge = bounds.centerX();
                if (isBetterAcknowledgeEdge(edge, bestEdge, rtl)) {
                    bestEdge = edge;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    /**
     * Reads the effective layout direction from the device's current UI
     * locale rather than the test process's default Locale. See the
     * comment in {@link #findAcknowledgeConfirmButtonByGeometry()} for
     * why the distinction matters.
     */
    private boolean isDeviceRtlLayout() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())
                    == View.LAYOUT_DIRECTION_RTL;
        }
    }

    /**
     * Returns {@code true} when {@code object} (or any direct child) is
     * a back/close navigation affordance rather than the primary accept
     * action. The acknowledge dialog has a footer row whose right-side
     * (or left-side, depending on layout direction) hosts a ‹back› arrow
     * with content-desc "Backward" in LTR builds and the localised
     * equivalent in RTL builds. Without this guard, the geometry pass
     * picks that arrow as the "trailing" button in Arabic because it is
     * the leftmost clickable in the band.
     */
    private boolean isAcknowledgeBackAffordance(UiObject2 object) {
        if (object == null) {
            return false;
        }
        try {
            if (isBackAffordanceText(object.getContentDescription())
                    || isBackAffordanceText(object.getText())) {
                return true;
            }
            for (UiObject2 child : object.getChildren()) {
                try {
                    if (isBackAffordanceText(child.getContentDescription())
                            || isBackAffordanceText(child.getText())) {
                        return true;
                    }
                } catch (StaleObjectException ignored) {
                }
            }
        } catch (StaleObjectException ignored) {
        }
        return false;
    }

    private boolean isBackAffordanceText(String value) {
        String clean = sanitizeQiraDescription(value).toLowerCase(Locale.ROOT).trim();
        if (clean.isEmpty()) {
            return false;
        }
        // English content-descriptions Qira ships for navigation widgets
        // plus the common Arabic "back" renderings observed on-device
        // ("إلى الخلف" / "رجوع" / "العودة"). These are locale-dependent
        // on purpose — Qira localises the content-desc — so we accept
        // the known variants.
        return clean.equals("backward")
                || clean.equals("back")
                || clean.equals("close")
                || clean.equals("\u0625\u0644\u0649 \u0627\u0644\u062e\u0644\u0641") // إلى الخلف
                || clean.equals("\u0631\u062c\u0648\u0639")                         // رجوع
                || clean.equals("\u0627\u0644\u0639\u0648\u062f\u0629");            // العودة
    }

    private boolean isAcknowledgeActionButtonCandidate(UiObject2 object) {
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            // The action button row sits in roughly the 78-84% vertical
            // band on Qira's acknowledge dialog (Cancel + Accept chips).
            // The previous upper bound of 92% was too permissive: it let
            // the footer navigation row (centered at ~89-92%, containing
            // the Back arrow and the "A few notes from Motorola Qira"
            // breadcrumb title) qualify as action candidates. In RTL
            // builds the back arrow is at the leftmost edge of that
            // footer and would then be picked as the primary action,
            // sending us back to the sign-in screen instead of
            // acknowledging. Clamping the band to end at 87% cleanly
            // separates the two rows across every device we test on.
            return bounds.top >= (height * 68) / 100
                    && bounds.bottom <= (height * 87) / 100
                    && bounds.width() >= Math.max(20, width / 100)
                    && bounds.width() <= (width * 35) / 100
                    && bounds.height() >= (height * 2) / 100
                    && bounds.height() <= (height * 10) / 100
                    && bounds.centerX() >= (width * 10) / 100
                    && bounds.centerX() <= (width * 90) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean isAcknowledgeActionLabelCandidate(UiObject2 object) {
        try {
            String text = object.getText();
            String description = sanitizeQiraDescription(object.getContentDescription());
            if ((text == null || text.trim().isEmpty()) && description.isEmpty()) {
                return false;
            }
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            // Keep the label band tied to the same 68-87% window as the
            // button candidate check — see the comment on
            // isAcknowledgeActionButtonCandidate for why the previous
            // 92% ceiling pulled the footer's back-arrow label into the
            // primary-action pool.
            return bounds.top >= (height * 68) / 100
                    && bounds.bottom <= (height * 87) / 100
                    && bounds.width() >= Math.max(20, width / 100)
                    && bounds.width() <= (width * 35) / 100
                    && bounds.centerX() >= (width * 10) / 100
                    && bounds.centerX() <= (width * 90) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean isBetterAcknowledgeEdge(int candidate, int current, boolean rtl) {
        return rtl ? candidate < current : candidate > current;
    }

    private boolean containsAcknowledgeCancelText(UiObject2 object, int maxDepth) {
        if (object == null || maxDepth < 0) {
            return false;
        }
        try {
            // Rule-set order: check content-description before visible
            // text so multilingual cancel buttons exposed via a stable
            // content-desc get filtered even when the rendered label has
            // drifted.
            if (isAcknowledgeCancelText(object.getContentDescription())
                    || isAcknowledgeCancelText(object.getText())) {
                return true;
            }
            if (maxDepth == 0) {
                return false;
            }
            for (UiObject2 child : object.getChildren()) {
                if (containsAcknowledgeCancelText(child, maxDepth - 1)) {
                    return true;
                }
            }
        } catch (StaleObjectException ignored) {
        }
        return false;
    }

    private boolean isAcknowledgeCancelText(String value) {
        String clean = sanitizeQiraDescription(value).toLowerCase(Locale.ROOT).trim();
        if (clean.isEmpty()) {
            return false;
        }
        return "cancel".equals(clean)
                || "cancelar".equals(clean)
                || "abbrechen".equals(clean)
                || "annulla".equals(clean)
                || "annuler".equals(clean)
                // Polish: "Anuluj" lowercases to "anuluj".
                || "anuluj".equals(clean)
                // Romanian: "Anuleaz\u0103" lowercases to "anuleaz\u0103".
                || "anuleaz\u0103".equals(clean)
                || "anula".equals(clean)
                // Japanese: \u30ad\u30e3\u30f3\u30bb\u30eb (cancel).
                || "\u30ad\u30e3\u30f3\u30bb\u30eb".equals(clean)
                // Chinese (Simplified): \u53d6\u6d88 (cancel).
                || "\u53d6\u6d88".equals(clean)
                // Arabic cancel variants: إلغاء (standard), ألغِ (imperative),
                // إلغاء الأمر. We compare against the normalised form
                // because the renderer may inject bidi markers that our
                // sanitiser strips.
                || "\u0625\u0644\u063a\u0627\u0621".equals(clean)
                || "\u0623\u0644\u063a\u0650".equals(clean)
                || "\u0625\u0644\u063a\u0627\u0621 \u0627\u0644\u0623\u0645\u0631".equals(clean);
    }

    private boolean hasAcknowledgeFooterTitleByGeometry() {
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 84) / 100
                        || bounds.bottom > (height * 98) / 100) {
                    continue;
                }
                String clean = sanitizeQiraDescription(text).toLowerCase(Locale.ROOT);
                if (clean.contains("motorola") && clean.contains("qira")) {
                    return true;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return false;
    }

    private boolean hasVisibleTextOrDescription(UiObject2 object) {
        String text = object.getText();
        String description = sanitizeQiraDescription(object.getContentDescription());
        return (text != null && !text.trim().isEmpty()) || !description.isEmpty();
    }

    private void logAcknowledgeDialogShapeDiagnostics() {
        try {
            UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
            UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
            ONBOARDING_LOGGER.info(String.format(Locale.US,
                    "[diag acknowledge shape] rtl=%s footer=%s footerTitle=%s "
                            + "contentLabels=%d denseLabels=%d actionButtons=%d "
                            + "actionLabels=%d confirmButton=%s confirmLabel=%s",
                    isDeviceRtlLayout(),
                    findStartDialogQiraFooter() != null,
                    hasAcknowledgeFooterTitleByGeometry(),
                    countAcknowledgeContentLabelsByGeometry(),
                    countAcknowledgeDenseLabelsByGeometry(),
                    countAcknowledgeActionButtonsByGeometry(),
                    countAcknowledgeActionLabelsByGeometry(),
                    describeAcknowledgeTarget(confirmButton),
                    describeAcknowledgeTarget(confirmLabel)));
        } catch (Throwable ignored) {
        }
    }

    private String describeAcknowledgeTarget(UiObject2 object) {
        if (object == null) {
            return "null";
        }
        try {
            Rect bounds = object.getVisibleBounds();
            String desc = sanitizeQiraDescription(object.getContentDescription());
            String text = object.getText();
            String label;
            if (text != null && !text.trim().isEmpty()) {
                label = text.trim();
            } else if (!desc.isEmpty()) {
                label = desc;
            } else {
                label = "";
            }
            if (label.length() > 24) {
                label = label.substring(0, 24) + "…";
            }
            return String.format(Locale.US, "{t=%s,cx=%d,cy=%d,w=%d,h=%d}",
                    label,
                    bounds != null ? bounds.centerX() : -1,
                    bounds != null ? bounds.centerY() : -1,
                    bounds != null ? bounds.width() : -1,
                    bounds != null ? bounds.height() : -1);
        } catch (Throwable t) {
            return "err";
        }
    }

    private boolean hasDescendantClass(UiObject2 object, String className, int maxDepth) {
        if (object == null || maxDepth <= 0) {
            return false;
        }
        try {
            for (UiObject2 child : object.getChildren()) {
                if (className.equals(child.getClassName())) {
                    return true;
                }
                if (hasDescendantClass(child, className, maxDepth - 1)) {
                    return true;
                }
            }
        } catch (StaleObjectException ignored) {
        }
        return false;
    }

    public boolean waitForPermissionBanner(long timeoutMs) throws Exception {
        return waitForTextOrDescription(timeoutMs, PERMISSION_BANNER_LABELS) != null;
    }

    public boolean isPermissionBannerVisible() {
        return hasTextOrDescription(PERMISSION_BANNER_LABELS);
    }

    public void waitForPermissionPanel() throws Exception {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isPermissionPanelVisible()) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }
        logVisibleLabelsForDiagnostics("waitForSurface the Qira permission review panel timeout");
        throw new IllegalStateException("Unable to detect the Qira permission review panel");
    }

    public boolean isPermissionPanelVisible() {
        // Prefer the unambiguous breadcrumb signal when it is present; fall
        // back to the header / I-agree pair for surfaces where the
        // breadcrumb hasn't rendered yet (e.g. permission panel during an
        // initial fade-in).
        if (hasTextOrDescription(PERMISSION_PANEL_BREADCRUMB_LABELS)) {
            return true;
        }
        if (hasTextOrDescription(PERMISSION_PANEL_LABELS)) {
            return true;
        }
        // Locale-agnostic fallback: the permission panel is the only
        // onboarding surface that combines a bottom acknowledge row with
        // multiple logical-end checkable toggle chips in the main body.
        // This guards non-English builds where the panel labels are fully
        // translated and our English anchors legitimately miss.
        return countPermissionPanelToggleCandidatesByGeometry() >= 2
                && (findStartDialogQiraFooter() != null || hasAcknowledgeFooterTitleByGeometry());
    }

    private int countPermissionPanelToggleCandidatesByGeometry() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Permission toggles sit at the logical end of the content
                // list (right in LTR, left in RTL), above the action row.
                if (bounds.top < (height * 16) / 100
                        || bounds.bottom > (height * 78) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100
                        || bounds.width() > (width * 20) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 2) / 100
                        || bounds.height() > (height * 9) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    /**
     * True while the breadcrumb footer of the permission review is still
     * visible. Used as the hard stop in {@link #agreeToPermissions()} so we
     * never interpret Compose residue on the next surface as success.
     */
    private boolean isPermissionPanelBreadcrumbVisible() {
        // Keep this helper locale-agnostic: the breadcrumb string is fully
        // translated on non-English builds, so relying on the English anchor
        // alone can miss a still-visible permission panel.
        return isPermissionPanelVisible();
    }

    public void enableAllPermissions() throws Exception {
        // Step 1: scroll the permission list to the TOP so the master toggle
        // and the first sub-rows are visible. Some locales (de-DE, pl-PL,
        // pt-BR, etc.) have long row descriptions that push the bottom rows
        // below the initial viewport; the master cascade only applies to
        // currently-rendered toggles in some Compose builds, so any rows
        // that were never rendered would remain disabled and keep the
        // "I agree" CTA disabled. Scrolling up first guarantees we start
        // from a known position with the master in view.
        scrollPermissionPanelToTop();
        settle();

        boolean masterOn = clickMasterPermissionToggle();
        settle();
        if (isContextualReadingPermissionDialogVisible()) {
            return;
        }

        // Step 2: walk the entire scrollable list top -> bottom, ensuring
        // every visible sub-toggle is checked. Repeat until either every
        // toggle in the list is checked OR we cannot scroll any further.
        ensureEveryPermissionToggleChecked();

        if (isContextualReadingPermissionDialogVisible()) {
            return;
        }

        // Step 3: bring the action row into view for the post-scroll
        // screenshot. The next step (agreeToPermissions) will deal with
        // the actual click + verification.
        scrollPermissionPanelDown();
        settle();
    }

    /**
     * Walks the permission scrollable list from top to bottom and turns on
     * every sub-toggle that is not already checked. Locale-safe: relies on
     * geometry only (logical-end checkable nodes in the permission band).
     * Stops when an entire scroll attempt yields no further movement (we
     * are at the bottom of the list).
     */
    private void ensureEveryPermissionToggleChecked() throws Exception {
        // REVERT NOTE: trimming the iteration counts and adding
        // isAgreeButtonEnabled() short-circuits regressed de-DE - the
        // 5-toggle long-description layout was leaving sub-toggles
        // unchecked when we exited early. Restoring deterministic 12 + 6
        // pass loop that worked before.
        for (int pass = 0; pass < 12; pass++) {
            // First, flip every visible unchecked sub-toggle.
            int toggledThisPass = enableEveryVisibleSubToggle();

            // Then scroll down to expose the next chunk.
            int scrollY = panelScrollTopY();
            scrollPermissionPanelDown();
            settle();
            int newScrollY = panelScrollTopY();

            // If the panel didn't move (we're at the bottom) AND nothing
            // new toggled, we're done.
            boolean reachedBottom = (scrollY >= 0 && newScrollY >= 0
                    && Math.abs(scrollY - newScrollY) < 12);
            if (reachedBottom && toggledThisPass == 0) {
                break;
            }
        }

        // Final upward sweep to catch any toggles that may still be
        // unchecked at the top of the list (we may have scrolled past them
        // before flipping them).
        scrollPermissionPanelToTop();
        settle();
        for (int pass = 0; pass < 6; pass++) {
            int toggledThisPass = enableEveryVisibleSubToggle();
            int scrollY = panelScrollTopY();
            scrollPermissionPanelDown();
            settle();
            int newScrollY = panelScrollTopY();
            boolean reachedBottom = (scrollY >= 0 && newScrollY >= 0
                    && Math.abs(scrollY - newScrollY) < 12);
            if (reachedBottom && toggledThisPass == 0) {
                break;
            }
        }
    }

    /**
     * Returns a y coordinate that approximates the current scroll offset of
     * the permission scrollable container, by reading the top of the
     * top-most checkable sub-toggle in view. Used to detect "we cannot
     * scroll further". Returns -1 when no toggle is in view.
     */
    private int panelScrollTopY() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int topMost = -1;
        for (UiObject2 toggle : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = toggle.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 82) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (topMost < 0 || bounds.top < topMost) {
                    topMost = bounds.top;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return topMost;
    }

    /**
     * Turns on every visible permission sub-toggle (NOT the master at the
     * top of the panel) that is currently unchecked. Returns the number
     * of toggles that were flipped on this pass.
     */
    private int enableEveryVisibleSubToggle() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int flipped = 0;

        // Identify the master toggle so we don't double-flip it (clicking
        // master a second time would un-cascade everything).
        UiObject2 master = findPermissionPanelHeaderToggleByGeometry();
        Rect masterBounds = null;
        if (master != null) {
            try {
                masterBounds = master.getVisibleBounds();
            } catch (StaleObjectException ignored) {
                masterBounds = null;
            }
        }

        for (UiObject2 toggle : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = toggle.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Skip the master row (top of panel).
                if (masterBounds != null
                        && Math.abs(bounds.top - masterBounds.top) < 20
                        && Math.abs(bounds.centerX() - masterBounds.centerX()) < 20) {
                    continue;
                }
                // Permission list band. Use 12-82% to include the bottom
                // sub-toggle on long-description locales.
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 82) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (toggle.isChecked() || !toggle.isEnabled()) {
                    continue;
                }
                clickObject(toggle);
                flipped++;
                mUtils.sleep(200L);
                if (isContextualReadingPermissionDialogVisible()) {
                    return flipped;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return flipped;
    }

    /**
     * Scrolls the permission scrollable list all the way to the top, by
     * issuing repeated downward swipes (which paginate the list upward).
     * Stops as soon as the scroll position no longer changes.
     */
    private void scrollPermissionPanelToTop() throws Exception {
        for (int pass = 0; pass < 8; pass++) {
            int beforeY = panelScrollTopY();
            scrollPermissionPanelUp();
            settle();
            int afterY = panelScrollTopY();
            // Stop when the panel can no longer be scrolled further up.
            if (beforeY >= 0 && afterY >= 0 && Math.abs(beforeY - afterY) < 12) {
                break;
            }
        }
    }

    private void scrollPermissionPanelUp() throws Exception {
        int x = mDevice.getDisplayWidth() / 2;
        int startY = (mDevice.getDisplayHeight() * 30) / 100;
        int endY = (mDevice.getDisplayHeight() * 70) / 100;
        UiObject2 list = findLargestPermissionPanelScrollableContainer();
        if (list != null) {
            try {
                Rect listBounds = list.getVisibleBounds();
                if (listBounds != null && !listBounds.isEmpty()) {
                    x = listBounds.centerX();
                    int pad = Math.max(24, listBounds.height() / 6);
                    startY = Math.max(listBounds.top + 6, listBounds.top + pad);
                    endY = Math.min(listBounds.bottom - 6, listBounds.bottom - pad);
                }
            } catch (StaleObjectException ignored) {
            }
        }
        try {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "input swipe %d %d %d %d 250", x, startY, x, endY));
        } catch (Throwable shellFailure) {
            mDevice.swipe(x, startY, x, endY, 25);
        }
        settle();
    }

    private boolean clickMasterPermissionToggle() throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 header = findByTextOrDescription("Turn on all permissions below");
                UiObject2 toggle = header != null
                        ? findAssociatedToggle(header)
                        : null;
                if (toggle == null) {
                    toggle = findPermissionPanelHeaderToggleByGeometry();
                }
                if (toggle == null) {
                    toggle = findTopMostPermissionPanelToggleByGeometry();
                }
                if (toggle == null) {
                    mUtils.sleep(300L);
                    continue;
                }
                if (!toggle.isChecked()) {
                    clickObject(toggle);
                    settle();
                }
                return toggle.isChecked();
            } catch (StaleObjectException stale) {
                mUtils.sleep(300L);
            }
        }
        return false;
    }

    private UiObject2 findPermissionPanelHeaderToggleByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 30) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100
                        || bounds.width() > (width * 20) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 2) / 100
                        || bounds.height() > (height * 9) / 100) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private boolean enableVisiblePermissionTogglesByGeometry() throws Exception {
        boolean changed = false;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 toggle : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = toggle.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Include the master toggle above the scroll container.
                // Bottom bound is intentionally 82% rather than 78%: on
                // locales with longer permission descriptions (de-DE,
                // pl-PL, pt-BR) the bottom-most permission toggle gets
                // pushed below the 78% threshold while remaining clearly
                // above the action row at 87%, so the pre-agree
                // enableVisible pass missed it and the agree CTA stayed
                // disabled. 82% keeps a safe gap from the action row
                // band (68-87%) so we never mistake an Accept/Cancel
                // chip for a permission toggle.
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 82) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (!toggle.isEnabled() || toggle.isChecked()) {
                    continue;
                }
                clickObject(toggle);
                changed = true;
                mUtils.sleep(120L);
            } catch (StaleObjectException ignored) {
            }
        }
        return changed;
    }

    public boolean waitForContextualReadingPermissionDialog(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isContextualReadingPermissionDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return isContextualReadingPermissionDialogVisible();
    }

    public boolean isContextualReadingPermissionDialogVisible() {
        return hasTextOrDescription(CONTEXTUAL_READING_PERMISSION_DIALOG_LABELS)
                && findContextualReadingEnableAction() != null;
    }

    public boolean isContextualReadingEnablePermissionEnabled() {
        UiObject2 action = findContextualReadingEnableAction();
        if (action == null) {
            return false;
        }
        try {
            UiObject2 clickable = findClickableAncestor(action);
            return clickable != null ? clickable.isEnabled() : action.isEnabled();
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    public boolean scrollContextualReadingPermissionDialogDown() throws Exception {
        if (!isContextualReadingPermissionDialogVisible()) {
            return false;
        }
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int x = width / 2;
        int startY = (height * 80) / 100;
        int endY = (height * 45) / 100;
        UiObject2 scrollable = findContextualReadingScrollableContainer();
        if (scrollable != null) {
            try {
                Rect bounds = scrollable.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    x = bounds.centerX();
                    startY = Math.min(bounds.bottom - 20, bounds.top + (bounds.height() * 82) / 100);
                    endY = Math.max(bounds.top + 20, bounds.top + (bounds.height() * 20) / 100);
                }
            } catch (StaleObjectException ignored) {
            }
        }
        try {
            mDevice.executeShellCommand(String.format(Locale.US,
                    "input swipe %d %d %d %d 300", x, startY, x, endY));
        } catch (Throwable shellFailure) {
            mDevice.swipe(x, startY, x, endY, 30);
        }
        settle();
        mUtils.sleep(250L);
        return true;
    }

    public boolean tapContextualReadingEnablePermission() throws Exception {
        if (!isContextualReadingPermissionDialogVisible()) {
            return false;
        }
        if (!isContextualReadingEnablePermissionEnabled()) {
            return false;
        }
        UiObject2 action = findContextualReadingEnableAction();
        if (action == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(action);
        clickObject(clickable != null ? clickable : action);
        long deadline = System.currentTimeMillis() + 8000L;
        while (System.currentTimeMillis() < deadline) {
            if (!isContextualReadingPermissionDialogVisible()
                    || isPermissionPanelVisible()
                    || isHotwordSetupVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return true;
    }

    private UiObject2 findContextualReadingEnableAction() {
        UiObject2 label = findByExactTextOrDescription(CONTEXTUAL_READING_ENABLE_LABELS);
        if (label != null) {
            UiObject2 clickable = findClickableAncestor(label);
            return clickable != null ? clickable : label;
        }
        return findContextualReadingEnableActionByGeometry();
    }

    private UiObject2 findContextualReadingEnableActionByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 78) / 100
                        || bounds.bottom > (height * 91) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 58) / 100
                        || bounds.width() > (width * 94) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100
                        || bounds.centerX() > (width * 65) / 100) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findContextualReadingScrollableContainer() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).scrollable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 32) / 100
                        || bounds.bottom > (height * 86) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 20) / 100
                        || bounds.centerX() > (width * 80) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }
    public void agreeToPermissions() throws Exception {
        long deadline = System.currentTimeMillis() + 45000L;
        int clicks = 0;
        int retogglePasses = 0;
        while (System.currentTimeMillis() < deadline) {
            // If a stray BACK from a misfiring tap pushed us onto the
            // "Quit setup?" / "Einrichtung beenden?" confirmation, dismiss
            // it immediately and resume - otherwise the dialog blocks
            // every subsequent click on the permission panel underneath.
            if (isEndSetupDialogVisible()) {
                ONBOARDING_LOGGER.info(
                        "agreeToPermissions: end-setup dialog detected; tapping Stay");
                try {
                    stayInSetup();
                } catch (Throwable ignored) {
                    // Dialog may have already auto-dismissed or its label
                    // is missing from our list; continue polling.
                }
                settle();
                continue;
            }

            if (!isPermissionPanelVisible()) {
                settle();
                return;
            }

            UiObject2 agreeAction = findPermissionAgreeAction();
            if (agreeAction == null) {
                scrollPermissionPanelDown();
                mUtils.sleep(400L);
                continue;
            }

            Rect textBounds;
            try {
                textBounds = agreeAction.getVisibleBounds();
            } catch (StaleObjectException stale) {
                continue;
            }

            // First try the clickable target (standard UiAutomator path).
            try {
                clickObject(agreeAction);
                settle();
                clicks++;
            } catch (StaleObjectException stale) {
                continue;
            }
            if (waitForPermissionPanelToDismiss(5000L)) {
                return;
            }

            // Compose buttons frequently expose their clickable on the
            // parent Card / Box at a level the 6-level ancestor walk
            // doesn't reach. Fall back to a raw pixel tap on the visible
            // text bounds (that is where the user would touch).
            if (textBounds != null && !textBounds.isEmpty()) {
                mDevice.executeShellCommand(String.format(Locale.US,
                        "input tap %d %d", textBounds.centerX(), textBounds.centerY()));
                settle();
                clicks++;
                if (waitForPermissionPanelToDismiss(5000L)) {
                    return;
                }
            }

            // Belt-and-braces: some rollouts only accept a tap slightly
            // below the TextView (the Compose clickable Card often extends
            // a few dp below the text baseline).
            if (textBounds != null && !textBounds.isEmpty()) {
                int pad = Math.max(12, textBounds.height() / 2);
                int y = Math.min(mDevice.getDisplayHeight() - 4,
                        textBounds.bottom + pad);
                mDevice.executeShellCommand(String.format(Locale.US,
                        "input tap %d %d", textBounds.centerX(), y));
                settle();
                clicks++;
                if (waitForPermissionPanelToDismiss(5000L)) {
                    return;
                }
            }

            // The click landed on a Compose Button whose onClick is gated
            // on EVERY sub-toggle being checked. If we're still here after
            // a couple of click attempts, some sub-toggle is still off;
            // walk the entire list top -> bottom one more time and retry.
            if (clicks >= 3 && retogglePasses < 3) {
                retogglePasses++;
                ONBOARDING_LOGGER.info(
                        "agreeToPermissions: agree click ineffective; "
                                + "performing retoggle pass #" + retogglePasses);
                scrollPermissionPanelToTop();
                settle();
                ensureEveryPermissionToggleChecked();
                clicks = 0;
                continue;
            }

            mUtils.sleep(400L);
        }

        logVisibleLabelsForDiagnostics("agreeToPermissions stuck");
        throw new IllegalStateException("The Qira permission review did not dismiss after I agree");
    }

    /**
     * Polls for up to {@code timeoutMs} milliseconds and returns true once
     * the permission review has genuinely advanced (panel hidden, or next
     * onboarding surface visible). Requiring a concrete state change avoids
     * Compose residue / stale-alias false positives where permission text can
     * linger for a few frames after the transition starts.
     */
    private boolean waitForPermissionPanelToDismiss(long timeoutMs) throws Exception {
        long postClick = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < postClick) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (!isPermissionPanelVisible()
                    || isHotwordSetupVisible()
                    || isExploreStartVisibleByStructure()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private UiObject2 findPermissionAgreeAction() {
        UiObject2 agreeText = findByExactTextOrDescription(PERMISSION_AGREE_LABELS);
        if (agreeText != null && !containsAcknowledgeCancelText(agreeText, 1)) {
            UiObject2 clickable = findClickableAncestor(agreeText);
            return clickable != null ? clickable : agreeText;
        }

        UiObject2 agreeByGeometry = findAcknowledgeConfirmButtonByGeometry();
        if (agreeByGeometry != null && !containsAcknowledgeCancelText(agreeByGeometry, 2)) {
            return agreeByGeometry;
        }

        UiObject2 agreeLabelByGeometry = findAcknowledgeConfirmLabelByGeometry();
        if (agreeLabelByGeometry != null && !containsAcknowledgeCancelText(agreeLabelByGeometry, 1)) {
            UiObject2 clickable = findClickableAncestor(agreeLabelByGeometry);
            return clickable != null ? clickable : agreeLabelByGeometry;
        }
        return null;
    }

    private UiObject2 findTopMostPermissionPanelToggleByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // 82% (not 78%) — see enableVisiblePermissionTogglesByGeometry
                // for why the bottom toggle on long-description locales
                // can land below 78%.
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 82) / 100) {
                    continue;
                }
                if (!isPermissionToggleLogicalEnd(bounds, width)) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    public boolean waitForHotwordSetup(long timeoutMs) throws Exception {
        return waitForTextOrDescriptionNoAuto(timeoutMs, HOTWORD_SETUP_LABELS) != null;
    }

    public boolean isHotwordSetupVisible() {
        if (hasTextOrDescription(HOTWORD_SETUP_LABELS)) {
            return true;
        }
        // Locale-safe structural fallback: hotword setup renders a two-button
        // CTA stack plus the footer back icon + Qira label.
        if (isHotwordSetupVisibleByGeometry()) {
            return true;
        }
        // Locale-safe fallback: some builds render only the skip CTA +
        // footer copy ("Ready to set up ...") while omitting the exact
        // anchor strings above from the accessibility tree.
        return hasTextOrDescription(HOTWORD_SKIP_LABELS)
                && findStartDialogQiraFooter() != null;
    }

    private boolean isHotwordSetupVisibleByGeometry() {
        return findHotwordPrimaryButtonByGeometry() != null
                && findHotwordSkipButtonByGeometry() != null
                && findStartDialogQiraFooter() != null
                && findOnboardingFooterMessage() != null
                && findOnboardingFooterBackButtonByGeometry() != null;
    }

    public void skipHotwordSetup() throws Exception {
        if (!clickByTextOrDescription(HOTWORD_SKIP_LABELS)) {
            UiObject2 skip = findHotwordSkipButtonByGeometry();
            if (skip != null) {
                clickObject(skip);
            } else {
                throw new IllegalStateException("Unable to skip the Hey Motorola Qira setup");
            }
        }
        // Wait for the hotword card to actually disappear before re-tapping.
        // Some locales (notably en-GB) take several seconds to transition off
        // the hotword screen; rapid re-tapping during the transition lands on
        // the next surface (Explore Start) and dismisses it inadvertently.
        if (!waitForHotwordDismissed(8000L)) {
            // Card still visible after the grace period — try once more.
            if (clickByTextOrDescription(HOTWORD_SKIP_LABELS)) {
                settle();
            } else {
                UiObject2 skip = findHotwordSkipButtonByGeometry();
                if (skip != null) {
                    clickObject(skip);
                }
            }
            waitForHotwordDismissed(8000L);
        }
        if (hasTextOrDescription(MIC_NOT_DETECTED_LABELS)) {
            clickByTextOrDescription(HOTWORD_SKIP_LABELS);
            waitForHotwordDismissed(4000L);
        }
    }

    private boolean waitForHotwordDismissed(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isHotwordSetupVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(300L);
        }
        return false;
    }

    private UiObject2 findHotwordPrimaryButtonByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!isHotwordBottomActionCandidate(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findHotwordSkipButtonByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!isHotwordBottomActionCandidate(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds.top > bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isHotwordBottomActionCandidate(UiObject2 object) {
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            return bounds.top >= (height * 70) / 100
                    && bounds.bottom <= (height * 88) / 100
                    && bounds.width() >= (width * 25) / 100
                    // Phone layouts use an approximately 69%-wide CTA,
                    // while large-screen layouts keep the same button near 40%.
                    && bounds.width() <= (width * 80) / 100
                    && bounds.height() >= (height * 3) / 100
                    && bounds.height() <= (height * 10) / 100
                    && bounds.centerX() >= (width * 30) / 100
                    && bounds.centerX() <= (width * 70) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findOnboardingFooterBackButtonByGeometry() {
        UiObject2 best = null;
        int bestX = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 86) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100
                        || bounds.width() > (width * 12) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 18) / 100
                        || bounds.centerX() > (width * 35) / 100) {
                    continue;
                }
                if (bounds.centerX() < bestX) {
                    bestX = bounds.centerX();
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    public void skipHotwordSetupIfPresent() throws Exception {
        long deadline = System.currentTimeMillis() + 8000L;
        while (System.currentTimeMillis() < deadline) {
            if (isHotwordSetupVisible()) {
                skipHotwordSetup();
                settle();
                return;
            }
            if (isExploreStartVisible()) {
                return;
            }
            mUtils.sleep(250L);
        }
    }

    public void waitForExploreStart() throws Exception {
        waitForSurface("the Qira start-tour overlay", EXPLORE_START_LABELS);
    }

    public boolean isExploreStartVisible() {
        // Hotword setup ("Hey Qira") can show a similar bottom CTA pair
        // (Start / Skip) and used to be misclassified as the start-tour
        // overlay, causing us to tap "Start" and enter voice setup.
        if (isHotwordSetupVisible()) {
            return false;
        }
        // Defensive guards: every other onboarding surface has at least one
        // anchor text that overlaps the explore-start anchors via the shared
        // alias machinery (notably "Let's review a few permissions" on the
        // permission panel vs. "Let's explore what Motorola Qira can do"
        // here). Requiring the permission panel / sign-in / acknowledge
        // overlays to be gone blocks the premature-advance bug that would
        // otherwise send us into startExploration() while still on the
        // permission panel.
        if (isIntroBannerVisible()
                || isProductivityBannerVisible()
                || isLanguagePickerVisible()
                || isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionBannerVisible()
                || isPermissionPanelVisible()) {
            return false;
        }
        return hasTextOrDescription(EXPLORE_START_PRIMARY_LABELS)
                || isExploreStartVisibleByStructure();
    }

    public void startExploration() throws Exception {
        if (!isExploreStartVisible()) {
            return;
        }
        UiObject2 startButton = findExploreStartPrimaryButtonByGeometry();
        if (startButton != null) {
            clickObject(startButton);
            return;
        }
        if (clickByExactTextOrDescription("Start")) {
            return;
        }
        if (clickByTextOrDescription("Start")) {
            return;
        }
        if (clickByExactTextOrDescription(START_PRIMARY_LABELS)) {
            return;
        }
        if (clickByTextOrDescription(START_PRIMARY_LABELS)) {
            return;
        }
        throw new IllegalStateException("Unable to start the Qira home experience");
    }

    private boolean isExploreStartVisibleByStructure() {
        return findExploreStartPrimaryButtonByGeometry() != null
                && countExploreStartButtonsByGeometry() >= 2
                && findStartDialogQiraFooter() != null
                && (findOnboardingFooterMessage() != null
                        || hasTextOrDescription(EXPLORE_CANCEL_LABELS));
    }

    private int countExploreStartButtonsByGeometry() {
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            if (isExploreStartButtonCandidate(object)) {
                count++;
            }
        }
        return count;
    }

    private UiObject2 findExploreStartPrimaryButtonByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!isExploreStartButtonCandidate(object)) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isExploreStartButtonCandidate(UiObject2 object) {
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            return bounds.top >= (height * 72) / 100
                    && bounds.bottom <= (height * 85) / 100
                    && bounds.width() >= (width * 30) / 100
                    // Phone layouts use an approximately 69%-wide CTA,
                    // while large-screen layouts keep the same button near 40%.
                    && bounds.width() <= (width * 80) / 100
                    && bounds.height() <= (height * 7) / 100
                    && bounds.centerX() >= (width * 35) / 100
                    && bounds.centerX() <= (width * 65) / 100;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    public boolean isFeatureGridVisible() {
        // Home screen never shows onboarding surfaces; if any of them is visible
        // we are definitively not on the feature grid yet.
        if (isStartDialogVisible()
                || isIntroBannerVisible()
                || isProductivityBannerVisible()
                || isLanguagePickerVisible()
                || isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionBannerVisible()
                || isPermissionPanelVisible()
                || isHotwordSetupVisible()
                || isExploreStartVisible()) {
            return false;
        }
        int visibleTiles = 0;
        for (String label : HOME_TILE_LABELS) {
            if (hasTextOrDescription(label)) {
                visibleTiles++;
            }
        }
        if (visibleTiles >= 2) {
            return true;
        }
        // Locale-agnostic fallback: home bottom bar icon descriptions stay
        // stable even when card headlines are fully translated.
        int iconCount = 0;
        for (String iconDesc : HOME_TILE_ICON_DESCS) {
            UiObject2 icon = mDevice.findObject(By.pkg(mConfig.getPackageName()).desc(iconDesc));
            if (icon != null) {
                iconCount++;
            }
        }
        if (iconCount >= 3) {
            return true;
        }

        // Hero-tile content descriptions: Focus Zone / Creator Zone /
        // Knowledge / Chat History each ship a localized content-description
        // even when the visible TextView is translated. Three such
        // descriptions visible in the upper 70% of the screen is a strong
        // signal that we are on the Qira home grid (no other onboarding
        // surface renders four hero cards).
        int heroCount = 0;
        for (String tileDesc : HOME_HERO_TILE_DESCS) {
            UiObject2 tile = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).desc(tileDesc));
            if (tile != null) {
                heroCount++;
            }
        }
        if (heroCount >= 2) {
            return true;
        }
        // Fallback: we're in the Qira package, no onboarding overlays, and we
        // can see the main input affordance.
        if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
            if (hasTextOrDescription("Ask anything")
                    || hasTextOrDescription("Ask Motorola Qira")
                    || hasTextOrDescription("Type a message")
                    || hasTextOrDescription("What can I help")) {
                return true;
            }
        }

        // Locale-agnostic structural fallback: the Qira home screen always
        // ends with a bottom navigation bar of 3-4 evenly-spaced clickables
        // (Chat / Live / Catch me up / Record). Counting those clickables in
        // the very bottom band of the display is a stable signal that does
        // not require any text / description matching at all, so it works
        // for the locales whose home tile headlines do not normalise back
        // to one of the static labels above.
        if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                && countHomeBottomNavButtons() >= 3) {
            return true;
        }
        return false;
    }

    /**
     * Counts the bottom-tab clickable items on the Qira home screen. The bar
     * is a horizontal row of 3-4 icons living in roughly the bottom 6-12% of
     * the display, with each item centered around its own column. We look
     * for clickable Qira-package nodes whose vertical band is the home tab
     * bar, then de-duplicate by horizontal column to avoid counting nested
     * icons multiple times.
     */
    private int countHomeBottomNavButtons() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        if (width <= 0 || height <= 0) {
            return 0;
        }
        int top = (height * 86) / 100;
        int bottom = (height * 99) / 100;
        java.util.TreeSet<Integer> columns = new java.util.TreeSet<>();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < top || bounds.bottom > bottom) {
                    continue;
                }
                if (bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 6) / 100
                        || bounds.width() > (width * 35) / 100) {
                    continue;
                }
                int colKey = bounds.centerX() / Math.max(1, width / 8);
                columns.add(colKey);
            } catch (StaleObjectException ignored) {
            }
        }
        if (columns.size() >= 3) {
            return columns.size();
        }
        // Locale fallback: some Compose builds expose the bottom nav as
        // non-clickable content-description Views (the clickable ancestor
        // is the entire bar). Count icon descriptions in the bottom band
        // instead.
        java.util.TreeSet<Integer> descColumns = new java.util.TreeSet<>();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()))) {
            try {
                String desc = object.getContentDescription();
                if (desc == null || desc.isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < top || bounds.bottom > bottom) {
                    continue;
                }
                if (bounds.height() > (height * 8) / 100
                        || bounds.height() < (height / 80)) {
                    continue;
                }
                if (bounds.width() > (width * 12) / 100
                        || bounds.width() < (width / 80)) {
                    continue;
                }
                int colKey = bounds.centerX() / Math.max(1, width / 8);
                descColumns.add(colKey);
            } catch (StaleObjectException ignored) {
            }
        }
        return descColumns.size();
    }

    public void waitForFeatureGrid() throws Exception {
        long deadline = System.currentTimeMillis() + 45000L;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (relaunchQiraIfBackgrounded()) {
                continue;
            }
            if (isHotwordSetupVisible()) {
                skipHotwordSetup();
                settle();
                continue;
            }
            if (isExploreStartVisible()) {
                startExploration();
                settle();
                continue;
            }
            if (isLocationPermissionPromptVisible()) {
                allowPreciseLocationWhileUsing();
                settle();
                continue;
            }
            if (isNearbyDevicesPromptVisible()) {
                allowNearbyDevicesPrompt();
                settle();
                continue;
            }
            if (isFeatureGridVisible()) {
                settle();
                return;
            }
            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 8000L) {
                logVisibleLabelsForDiagnostics("waitForFeatureGrid polling");
                lastDiagLog = now;
            }
            mUtils.sleep(300L);
        }
        logVisibleLabelsForDiagnostics("waitForFeatureGrid timeout");
        throw new IllegalStateException("Unable to detect the Qira home feature grid");
    }

    public boolean waitForLocationPrompt(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isLocationPermissionPromptVisible()) {
                settle();
                return true;
            }
            if (!mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
                mUtils.sleep(250L);
                continue;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isLocationPermissionPromptVisible() {
        if (!isPermissionControllerSurface()) {
            return false;
        }
        // Prefer stable system resources; they are locale-agnostic.
        if (findByResource(PERMISSION_LOCATION_FINE_RES,
                PERMISSION_LOCATION_COARSE_RES,
                PERMISSION_ALLOW_FG_RES) != null) {
            return true;
        }
        // Fallback for OEM layouts where IDs drift.
        return findSystemTextOrDescription(LOCATION_PROMPT_LABELS) != null;
    }

    /**
     * Grants the Android runtime location permission with the
     * <em>approximate</em> accuracy radio selected. Kept for callers that
     * explicitly want coarse location; new callers should prefer
     * {@link #allowPreciseLocationWhileUsing()}.
     */
    public void allowApproximateLocationWhileUsing() throws Exception {
        allowLocationWhileUsing(/* precise */ false);
    }

    /**
     * Grants the Android runtime location permission with the
     * <em>precise</em> accuracy radio selected. This is the default choice
     * for Motorola Qira capture scripts so generated screenshots reflect
     * the higher-accuracy state users are expected to grant.
     */
    public void allowPreciseLocationWhileUsing() throws Exception {
        allowLocationWhileUsing(/* precise */ true);
    }

    /**
     * Core handler for the Android runtime location-permission prompt.
     * Selects the requested accuracy radio (Precise vs Approximate) if it
     * exists and is not already checked, taps "While using the app", and
     * also handles a chained Nearby Devices prompt if one appears on the
     * way out.
     */
    private void allowLocationWhileUsing(boolean precise) throws Exception {
        long deadline = System.currentTimeMillis() + 10000L;
        boolean handledLocation = false;
        boolean handledPermissionPrompt = false;

        String primaryRes = precise ? PERMISSION_LOCATION_FINE_RES
                                    : PERMISSION_LOCATION_COARSE_RES;
        String primaryLabel = precise ? "Precise" : "Approximate";

        while (System.currentTimeMillis() < deadline) {
            if (isLocationPermissionPromptVisible()) {
                UiObject2 accuracy = findByResource(primaryRes);
                if (accuracy == null) {
                    accuracy = findSystemExactTextOrDescription(primaryLabel);
                }
                if (accuracy != null && !accuracy.isChecked()) {
                    clickObject(accuracy);
                    mUtils.sleep(500L);
                }

                if (clickByResource(PERMISSION_ALLOW_FG_RES)) {
                    handledLocation = true;
                    handledPermissionPrompt = true;
                } else if (clickSystemExactTextOrDescription("While using the app",
                        "Allow only while using the app")) {
                    handledLocation = true;
                    handledPermissionPrompt = true;
                }
                settle();
                continue;
            }

            if (isNearbyDevicesPromptVisible()) {
                allowNearbyDevicesPrompt();
                handledPermissionPrompt = true;
                settle();
                continue;
            }

            if (handledPermissionPrompt && mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }

        if (handledLocation || handledPermissionPrompt) {
            return;
        }
        throw new IllegalStateException("The Android location permission prompt did not close");
    }

    public boolean isNearbyDevicesPromptVisible() {
        if (!isPermissionControllerSurface()) {
            return false;
        }
        // Nearby-devices prompt uses the generic "Allow" button, while the
        // location prompt exposes the dedicated foreground-only action.
        if (findByResource(PERMISSION_ALLOW_RES) != null
                && findByResource(PERMISSION_ALLOW_FG_RES) == null
                && findByResource(PERMISSION_LOCATION_FINE_RES, PERMISSION_LOCATION_COARSE_RES) == null) {
            return true;
        }
        return findSystemTextOrDescription(NEARBY_DEVICES_PROMPT_LABELS) != null;
    }

    public boolean waitForNearbyDevicesPrompt(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isNearbyDevicesPromptVisible()) {
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public void allowNearbyDevicesPrompt() throws Exception {
        if (clickByResource(PERMISSION_ALLOW_RES)) {
            return;
        }
        if (!clickSystemExactTextOrDescription("Allow")) {
            throw new IllegalStateException("Unable to accept the nearby-devices permission prompt");
        }
    }

    private boolean isPermissionControllerSurface() {
        String currentPackage = mDevice.getCurrentPackageName();
        return PERMISSION_CONTROLLER_PACKAGE.equals(currentPackage)
                || PERMISSION_CONTROLLER_GOOGLE_PACKAGE.equals(currentPackage);
    }

    private UiObject2 findSystemTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        String[] systemPackages = {
                PERMISSION_CONTROLLER_PACKAGE,
                PERMISSION_CONTROLLER_GOOGLE_PACKAGE
        };
        for (String label : labels) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            for (String systemPkg : systemPackages) {
                UiObject2 byDesc = mDevice.findObject(By.pkg(systemPkg)
                        .desc(patternForLabel(label)));
                if (byDesc != null) {
                    return byDesc;
                }
                UiObject2 byText = mDevice.findObject(By.pkg(systemPkg)
                        .text(patternForLabel(label)));
                if (byText != null) {
                    return byText;
                }
            }
        }
        return null;
    }

    private UiObject2 findSystemExactTextOrDescription(String... labels) {
        if (labels == null || labels.length == 0) {
            return null;
        }
        String[] systemPackages = {
                PERMISSION_CONTROLLER_PACKAGE,
                PERMISSION_CONTROLLER_GOOGLE_PACKAGE
        };
        for (String label : labels) {
            if (label == null || label.isEmpty()) {
                continue;
            }
            for (String systemPkg : systemPackages) {
                UiObject2 byDesc = mDevice.findObject(By.pkg(systemPkg)
                        .desc(exactPatternForLabel(label)));
                if (byDesc != null) {
                    return byDesc;
                }
                UiObject2 byText = mDevice.findObject(By.pkg(systemPkg)
                        .text(exactPatternForLabel(label)));
                if (byText != null) {
                    return byText;
                }
            }
        }
        return null;
    }

    private boolean clickSystemExactTextOrDescription(String... labels) throws Exception {
        UiObject2 object = findSystemExactTextOrDescription(labels);
        if (object == null) {
            return false;
        }
        clickObject(object);
        return true;
    }

    private UiObject2 findByExactTextOrDescriptionNoAuto(long timeoutMs, String... labels)
            throws Exception {
        return waitForExactTextOrDescriptionNoAuto(timeoutMs, labels);
    }

    private void waitForSurface(String surfaceName, String... labels) throws Exception {
        if (waitForTextOrDescription(DEFAULT_TIMEOUT_MS, labels) == null) {
            logVisibleLabelsForDiagnostics("waitForSurface " + surfaceName + " timeout");
            throw new IllegalStateException("Unable to detect " + surfaceName);
        }
        settle();
    }

    public void disableAutoRotate() throws Exception {
        try {
            mDevice.executeShellCommand("settings put system accelerometer_rotation 0");
            mDevice.executeShellCommand("settings put system user_rotation 0");
        } catch (Throwable ignored) {
        }
        try {
            mDevice.setOrientationNatural();
            mDevice.freezeRotation();
        } catch (Throwable ignored) {
        }
    }

    public void advanceToExploreStart() throws Exception {
        long deadline = System.currentTimeMillis() + 90000L;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (relaunchQiraIfBackgrounded()) {
                continue;
            }
            if (isExploreStartVisible()) {
                settle();
                return;
            }
            if (isPermissionPanelVisible()) {
                // Locale / rollout drift can leave us on the permissions
                // review even after the earlier onboarding step attempted to
                // finish it (for example if the localized "I agree" state
                // check missed once). Self-heal here instead of timing out.
                enableAllPermissions();
                agreeToPermissions();
                settle();
                mUtils.sleep(600L);
                continue;
            }
            if (isHotwordSetupVisible()) {
                skipHotwordSetup();
                settle();
                mUtils.sleep(800L);
                continue;
            }
            if (isLocationPermissionPromptVisible() || isNearbyDevicesPromptVisible()) {
                allowPreciseLocationWhileUsing();
                settle();
                continue;
            }
            if (isFeatureGridVisible()) {
                return;
            }
            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 5000L) {
                logVisibleLabelsForDiagnostics("advanceToExploreStart stuck");
                lastDiagLog = now;
            }
            mUtils.sleep(400L);
        }
        logVisibleLabelsForDiagnostics("advanceToExploreStart timeout");
        throw new IllegalStateException("Unable to detect the Qira start-tour overlay");
    }

    public void advanceThroughOnboardingToHome(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isFeatureGridVisible()) {
                settle();
                return;
            }
            if (advanceOnboardingOnce()) {
                mUtils.sleep(500L);
                continue;
            }
            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 5000L) {
                logVisibleLabelsForDiagnostics("advanceThroughOnboardingToHome stuck");
                lastDiagLog = now;
            }
            mUtils.sleep(350L);
        }
        logVisibleLabelsForDiagnostics("advanceThroughOnboardingToHome timeout");
        throw new IllegalStateException("Unable to advance Qira onboarding to home");
    }

    public boolean advanceOnboardingOnce() throws Exception {
        if (handleSystemPermissionPrompt()) {
            return true;
        }
        if (isEndSetupDialogVisible()) {
            stayInSetup();
            settle();
            return true;
        }
        if (relaunchQiraIfBackgrounded()) {
            return true;
        }
        if (isUnknownQiraOnboardingSurface()) {
            ONBOARDING_LOGGER.info(
                    "Qira onboarding surface has no matched accessibility state; "
                            + "using footer-arrow coordinate fallback.");
            tapOnboardingFooterNextArrowByCoordinates();
            settle();
            return true;
        }
        if (isLocationPermissionPromptVisible() || isNearbyDevicesPromptVisible()) {
            allowPreciseLocationWhileUsing();
            settle();
            return true;
        }
        if (isStartDialogVisible()) {
            tapStartDialog();
            settle();
            return true;
        }
        if (isSignInDialogVisible()) {
            continueAs();
            settle();
            return true;
        }
        if (isAcknowledgeDialogVisible()) {
            acknowledge();
            settle();
            return true;
        }
        if (isPermissionPanelVisible()) {
            enableAllPermissions();
            agreeToPermissions();
            settle();
            return true;
        }
        if (isLanguagePickerVisible()) {
            chooseResponseLanguage();
            settle();
            return true;
        }
        if (isLooseLanguagePickerVisible()) {
            chooseResponseLanguageLoosely();
            settle();
            return true;
        }
        if (isHotwordSetupVisible()) {
            skipHotwordSetup();
            settle();
            return true;
        }
        if (isExploreStartVisible()) {
            startExploration();
            settle();
            return true;
        }
        if (isIntroBannerVisible()
                || isProductivityBannerVisible()
                || isOnboardingFooterArrowCardVisible()
                || isUnknownQiraOnboardingSurface()) {
            if (tapOnboardingFooterNextArrow()) {
                settle();
                return true;
            }
        }
        if (tapBottomOnboardingActionByGeometry()) {
            settle();
            return true;
        }
        return false;
    }

    private boolean isEndSetupDialogVisible() {
        return hasTextOrDescription(END_SETUP_DIALOG_LABELS)
                && findByExactTextOrDescription(END_SETUP_STAY_LABELS) != null;
    }

    private void stayInSetup() throws Exception {
        if (clickByExactTextOrDescription(END_SETUP_STAY_LABELS)) {
            return;
        }
        UiObject2 stay = findByTextOrDescription(END_SETUP_STAY_LABELS);
        if (stay != null && tapObjectBounds(stay)) {
            return;
        }
        throw new IllegalStateException("Unable to stay in the Qira setup flow");
    }

    private boolean tapBottomOnboardingActionByGeometry() throws Exception {
        UiObject2 best = null;
        int bestScore = Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 64) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 4) / 100
                        || bounds.width() > (width * 78) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 2) / 100
                        || bounds.height() > (height * 14) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 18) / 100
                        || bounds.centerX() > (width * 88) / 100) {
                    continue;
                }
                if (object.isCheckable()
                        || hasDescendantClass(object, "android.widget.RadioButton", 2)) {
                    continue;
                }
                int score = bounds.centerY() * 10;
                if (bounds.centerX() >= (width * 45) / 100
                        && bounds.centerX() <= (width * 85) / 100) {
                    score += height;
                }
                if (score > bestScore) {
                    bestScore = score;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        if (best == null) {
            return false;
        }
        return tapObjectBounds(best);
    }

    /**
     * Some locale flows briefly drop us onto launcher chrome (while Qira is
     * still expected to continue onboarding). If Qira loses foreground and we
     * are not in a system permission controller surface, relaunch it and let
     * the caller continue polling.
     */
    private boolean relaunchQiraIfBackgrounded() throws Exception {
        if (isLauncherBackedStartDialogVisible()) {
            return false;
        }
        String currentPkg = mDevice.getCurrentPackageName();
        if (mConfig.getPackageName().equals(currentPkg) || isPermissionControllerSurface()) {
            return false;
        }
        try {
            mDevice.pressBack();
            mUtils.sleep(300L);
        } catch (Throwable ignored) {
        }
        try {
            mDevice.pressHome();
            mUtils.sleep(300L);
        } catch (Throwable ignored) {
        }
        try {
            launchQiraApp();
        } catch (Throwable t) {
            ONBOARDING_LOGGER.info("Qira background relaunch failed once (continuing poll): "
                    + t.getMessage());
            return true;
        }
        settle();
        mUtils.sleep(400L);
        return true;
    }

    private void tapBannerArrow(String... labels) throws Exception {
        UiObject2 bannerText = waitForTextOrDescription(8000L, labels);
        if (bannerText == null) {
            if (tapOnboardingFooterNextArrow()) {
                return;
            }
            throw new IllegalStateException("Unable to detect the Qira onboarding arrow card");
        }

        // The "Next" content-description is translated on non-English locales,
        // so we can't rely on a text/desc match as the primary selector. The
        // arrow is always positioned along the right edge at roughly the same
        // vertical center as the banner copy, so tap there first and only
        // fall through to description/position heuristics as a safety net.
        Rect bounds = bannerText.getVisibleBounds();
        int targetX = mDevice.getDisplayWidth() - Math.max(80, mDevice.getDisplayWidth() / 12);
        int targetY = bounds != null && !bounds.isEmpty()
                ? Math.min(
                        Math.max(bounds.centerY(), (mDevice.getDisplayHeight() * 60) / 100),
                        mDevice.getDisplayHeight() - 120)
                : (mDevice.getDisplayHeight() * 3) / 4;
        mDevice.click(targetX, targetY);
        settle();

        // If that tap already advanced us past the banner we're done.
        if (!hasTextOrDescription(labels)) {
            return;
        }

        if (tapNextArrowByDescription()) {
            return;
        }

        if (clickBottomRightClickable()) {
            settle();
            return;
        }

        int fallbackX = (mDevice.getDisplayWidth() * 3) / 4;
        int minY = (mDevice.getDisplayHeight() * 3) / 4;
        int maxY = mDevice.getDisplayHeight() - 60;
        int fallbackY = bounds != null && !bounds.isEmpty()
                ? Math.max(minY, Math.min(bounds.centerY(), maxY))
                : minY;
        mDevice.executeShellCommand(String.format(Locale.US,
                "input tap %d %d", fallbackX, fallbackY));
        settle();
    }

    private boolean isOnboardingFooterArrowCardVisible() {
        return findOnboardingFooterNextArrow() != null
                && (findStartDialogQiraFooter() != null
                || findOnboardingFooterMessage() != null);
    }

    private boolean tapOnboardingFooterNextArrow() throws Exception {
        UiObject2 next = findOnboardingFooterNextArrow();
        if (next == null) {
            return tapOnboardingFooterNextArrowByCoordinates();
        }
        Rect bounds = next.getVisibleBounds();
        if (bounds != null && !bounds.isEmpty()) {
            mDevice.click(bounds.centerX(), bounds.centerY());
            settle();
            return true;
        }
        clickObject(next);
        return true;
    }

    private boolean isUnknownQiraOnboardingSurface() {
        if (!isQiraForegroundOrRecentlyLaunched()) {
            return false;
        }
        if (!hasNoVisibleQiraAccessibilityLabels() && !isOnboardingFooterArrowCardVisible()) {
            return false;
        }
        if (isFeatureGridVisible()
                || isStartDialogVisible()
                || isLanguagePickerVisible()
                || isSignInDialogVisible()
                || isAcknowledgeDialogVisible()
                || isPermissionPanelVisible()
                || isHotwordSetupVisible()
                || isExploreStartVisible()) {
            return false;
        }
        return true;
    }

    private boolean isQiraForegroundOrRecentlyLaunched() {
        return mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                || isQiraFocusedWindow();
    }

    private boolean tapOnboardingFooterNextArrowByCoordinates() throws Exception {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        if (!isQiraForegroundOrRecentlyLaunched()) {
            return false;
        }
        int x = (int) (width * (isDeviceRtlLayout() ? 0.07f : 0.93f));
        int y = (int) (height * 0.91f);
        ONBOARDING_LOGGER.info(String.format(Locale.US,
                "Tapping Qira onboarding footer-arrow coordinate fallback at x=%d y=%d",
                x, y));
        mDevice.click(x, y);
        settle();
        mUtils.sleep(1200L);
        return true;
    }

    private UiObject2 findOnboardingFooterNextArrow() {
        UiObject2 best = null;
        boolean rtl = isDeviceRtlLayout();
        int bestX = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        // First pass: explicit localized "next" labels/descriptions in the
        // footer band. Some locales expose a non-clickable icon with a
        // localized content-desc (for example "Suivant"), so scanning only
        // clickable nodes can miss it.
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 86) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (!rtl && bounds.centerX() < (width * 60) / 100) {
                    continue;
                }
                if (rtl && bounds.centerX() > (width * 40) / 100) {
                    continue;
                }
                if (bounds.width() > (width * 18) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                String text = object.getText();
                String desc = object.getContentDescription();
                if (!containsAnyLabel(text, LANGUAGE_PICKER_NEXT_LABELS)
                        && !containsAnyLabel(desc, LANGUAGE_PICKER_NEXT_LABELS)) {
                    continue;
                }
                UiObject2 target = object;
                UiObject2 clickable = findClickableAncestor(object);
                if (clickable != null) {
                    Rect clickableBounds = clickable.getVisibleBounds();
                    int objectArea = bounds.width() * bounds.height();
                    int clickableArea = clickableBounds != null && !clickableBounds.isEmpty()
                            ? clickableBounds.width() * clickableBounds.height()
                            : Integer.MAX_VALUE;
                    // Avoid replacing the footer arrow node with a huge page
                    // container; tap the compact arrow node directly in that case.
                    if (clickableBounds != null
                            && !clickableBounds.isEmpty()
                            && clickableArea <= objectArea * 6
                            && clickableBounds.top >= (height * 84) / 100
                            && clickableBounds.bottom <= (height * 98) / 100) {
                        target = clickable;
                    }
                }
                Rect targetBounds = target.getVisibleBounds();
                int edgeX = targetBounds != null && !targetBounds.isEmpty()
                        ? targetBounds.centerX()
                        : bounds.centerX();
                if ((!rtl && edgeX > bestX) || (rtl && edgeX < bestX)) {
                    bestX = edgeX;
                    best = target;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        if (best != null) {
            return best;
        }

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 80) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (!rtl && bounds.centerX() < (width * 65) / 100) {
                    continue;
                }
                if (rtl && bounds.centerX() > (width * 35) / 100) {
                    continue;
                }
                if (bounds.width() > (width * 12) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if ((!rtl && bounds.centerX() > bestX)
                        || (rtl && bounds.centerX() < bestX)) {
                    bestX = bounds.centerX();
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findOnboardingFooterMessage() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 86) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100
                        || bounds.centerX() > (width * 75) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean tapNextArrowByDescription() throws Exception {
        UiObject2 nextArrow = findByExactDescription("Next");
        if (nextArrow == null) {
            // pkg-scoped fallback: the literal "Next" content-desc is a
            // stable English test hook Qira ships across locales, but
            // other on-screen packages (launcher, system tutorials) may
            // expose the same substring — restrict to Qira.
            nextArrow = mDevice.findObject(
                    By.pkg(mConfig.getPackageName()).descContains("Next"));
        }
        if (nextArrow == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(nextArrow);
        UiObject2 target = clickable != null ? clickable : nextArrow;
        Rect bounds = target.getVisibleBounds();
        if (bounds == null || bounds.isEmpty()) {
            return false;
        }
        // Reject a clickable ancestor that spans most of the screen (likely the page background)
        if (bounds.width() > (mDevice.getDisplayWidth() * 2) / 3
                && bounds.height() > (mDevice.getDisplayHeight() * 2) / 3) {
            // Fall back to tapping the content-desc element directly in that case
            Rect innerBounds = nextArrow.getVisibleBounds();
            if (innerBounds == null || innerBounds.isEmpty()) {
                return false;
            }
            mDevice.click(innerBounds.centerX(), innerBounds.centerY());
            settle();
            return true;
        }
        clickObject(target);
        return true;
    }

    private boolean swipeBannerToNext() throws Exception {
        UiObject2 bannerText = findByTextOrDescription(INTRO_BANNER_LABELS);
        if (bannerText == null) {
            bannerText = findByTextOrDescription(PRODUCTIVITY_BANNER_LABELS);
        }
        if (bannerText == null) {
            return false;
        }
        Rect bounds = bannerText.getVisibleBounds();
        int y = bounds.centerY();
        int startX = (mDevice.getDisplayWidth() * 85) / 100;
        int endX = (mDevice.getDisplayWidth() * 15) / 100;
        mDevice.swipe(startX, y, endX, y, 20);
        settle();
        return true;
    }

    private static final Logger ONBOARDING_LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    private void logVisibleLabelsForDiagnostics(String marker) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[diag ").append(marker).append("] display=")
                    .append(mDevice.getDisplayWidth()).append("x")
                    .append(mDevice.getDisplayHeight())
                    .append(" rot=").append(mDevice.getDisplayRotation())
                    .append(" visible text/desc: ");
            List<UiObject2> all = mDevice.findObjects(By.pkg(mConfig.getPackageName()));
            int count = 0;
            for (UiObject2 obj : all) {
                if (count >= 30) {
                    break;
                }
                String t = obj.getText();
                String d = obj.getContentDescription();
                if ((t != null && !t.isEmpty()) || (d != null && !d.isEmpty())) {
                    sb.append("{t=").append(t).append(",d=").append(d).append("} ");
                    count++;
                }
            }
            ONBOARDING_LOGGER.info(sb.toString());
        } catch (Throwable ignored) {
        }
    }

    private UiObject2 findClickableAncestor(UiObject2 object) {
        UiObject2 current = object;
        for (int depth = 0; current != null && depth < 6; depth++) {
            try {
                if (current.isClickable()) {
                    return current;
                }
                current = current.getParent();
            } catch (StaleObjectException ignored) {
                return null;
            }
        }
        return null;
    }

    private boolean containsAnyLabel(String value, String[] labels) {
        if (value == null) {
            return false;
        }
        String cleanValue = sanitizeQiraDescription(value).toLowerCase(Locale.ROOT);
        for (String label : labels) {
            if (label == null || label.trim().isEmpty()) {
                continue;
            }
            String cleanLabel = sanitizeQiraDescription(label).toLowerCase(Locale.ROOT);
            if (cleanValue.contains(cleanLabel)) {
                return true;
            }
        }
        return false;
    }

    private boolean enableToggleForLabelIfVisible(String label) throws Exception {
        // Retry on StaleObjectException: Compose re-lays out during scrolling, which
        // can invalidate a UiObject2 between the time we look it up and the time we
        // interact with its switch. Re-query from scratch and try again.
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 labelObject = findByTextOrDescription(label);
                if (labelObject == null) {
                    return false;
                }

                UiObject2 toggle = findAssociatedToggle(labelObject);
                if (toggle == null) {
                    // Label is visible but no switch found yet (still laying out). Retry.
                    mUtils.sleep(300L);
                    continue;
                }

                if (!toggle.isChecked()) {
                    clickObject(toggle);
                }

                return true;
            } catch (StaleObjectException stale) {
                mUtils.sleep(300L);
            }
        }
        return false;
    }

    private UiObject2 findAssociatedToggle(UiObject2 anchor) {
        UiObject2 current = anchor;
        for (int depth = 0; current != null && depth < 6; depth++) {
            try {
                UiObject2 toggle = findLogicalEndToggle(
                        current.findObjects(By.clazz(Switch.class.getName())));
                if (toggle != null) {
                    return toggle;
                }

                toggle = findLogicalEndToggle(current.findObjects(By.checkable(true)));
                if (toggle != null) {
                    return toggle;
                }

                current = current.getParent();
            } catch (StaleObjectException stale) {
                return null;
            }
        }
        return null;
    }

    private UiObject2 findLogicalEndToggle(List<UiObject2> objects) {
        UiObject2 bestMatch = null;
        boolean rtl = isDeviceRtlLayout();
        int bestX = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (UiObject2 object : objects) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if ((!rtl && bounds.centerX() > bestX)
                        || (rtl && bounds.centerX() < bestX)) {
                    bestX = bounds.centerX();
                    bestMatch = object;
                }
            } catch (StaleObjectException stale) {
                // Node vanished during the scroll animation - skip it.
            }
        }
        return bestMatch;
    }

    private boolean isPermissionToggleLogicalEnd(Rect bounds, int width) {
        return bounds != null && isLogicalEndX(bounds.centerX(), width, 60, 92);
    }

    private boolean isLogicalEndX(int centerX, int width, int ltrMinPct, int ltrMaxPct) {
        if (isDeviceRtlLayout()) {
            return centerX >= (width * (100 - ltrMaxPct)) / 100
                    && centerX <= (width * (100 - ltrMinPct)) / 100;
        }
        return centerX >= (width * ltrMinPct) / 100
                && centerX <= (width * ltrMaxPct) / 100;
    }

    private boolean isAgreeButtonEnabled() {
        try {
            UiObject2 agreeButton = findByExactTextOrDescription(PERMISSION_AGREE_LABELS);
            if (agreeButton != null) {
                UiObject2 clickable = findClickableAncestor(agreeButton);
                return clickable != null ? clickable.isEnabled() : agreeButton.isEnabled();
            }
            // Locale-agnostic fallback: resolve the bottom-right action by
            // geometry (same "I agree"/confirm row shape used across locales).
            UiObject2 confirmButton = findAcknowledgeConfirmButtonByGeometry();
            if (confirmButton != null) {
                return confirmButton.isEnabled();
            }
            UiObject2 confirmLabel = findAcknowledgeConfirmLabelByGeometry();
            if (confirmLabel != null) {
                UiObject2 clickable = findClickableAncestor(confirmLabel);
                return clickable != null ? clickable.isEnabled() : confirmLabel.isEnabled();
            }
            return false;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private void scrollPermissionPanelDown() throws Exception {
        // Locale-agnostic primary path: swipe INSIDE the actual scrollable
        // permission list container (Compose marks it scrollable=true).
        int x = mDevice.getDisplayWidth() / 2;
        int startY = (mDevice.getDisplayHeight() * 70) / 100;
        int endY = (mDevice.getDisplayHeight() * 30) / 100;
        UiObject2 list = findLargestPermissionPanelScrollableContainer();
        if (list != null) {
            Rect listBounds = list.getVisibleBounds();
            if (listBounds != null && !listBounds.isEmpty()) {
                x = listBounds.centerX();
                int pad = Math.max(24, listBounds.height() / 6);
                startY = Math.min(listBounds.bottom - 6, listBounds.bottom - pad);
                endY = Math.max(listBounds.top + 6, listBounds.top + pad);
            }
        } else {
            // Fallback for builds that do not expose scrollable=true.
            UiObject2 anchor = findByTextOrDescription("Keep Motorola Qira always visible");
            if (anchor == null) {
                anchor = findByTextOrDescription("Turn on all permissions below");
            }
            if (anchor != null) {
                Rect bounds = anchor.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    x = bounds.centerX();
                    int anchorY = bounds.centerY();
                    startY = Math.max(anchorY, (mDevice.getDisplayHeight() * 55) / 100);
                    endY = Math.max(anchorY - (mDevice.getDisplayHeight() / 3),
                            (mDevice.getDisplayHeight() * 15) / 100);
                }
            }
        }

        mDevice.swipe(x, startY, x, endY, 25);
        settle();
    }

    private UiObject2 findLargestPermissionPanelScrollableContainer() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).scrollable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Keep to the permission-panel content band.
                if (bounds.top < (height * 18) / 100
                        || bounds.bottom > (height * 80) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 20) / 100
                        || bounds.centerX() > (width * 80) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }
}
