package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

/**
 * Page object for the "Focus Zone" floating bubble bar that Motorola Qira
 * renders as an overlay after the initial onboarding has completed.
 *
 * <p>The bubble bar exposes four feature entry points (Chat, Live, Catch me up,
 * Record) plus an App Icon. Each entry point opens a first-run onboarding card
 * that must be accepted before the feature becomes active. This class wraps all
 * of those interactions.
 */
public class QiraFocusZonePage extends BaseQiraPage {

    private static final long DEFAULT_TIMEOUT_MS = 15000L;
    private static final String ACTION_CORE_PACKAGE = "com.motorola.actioncore";

    private static final String BUBBLE_APP_ICON = "App Icon";
    private static final String BUBBLE_CHAT = "Chat";
    private static final String BUBBLE_LIVE = "Live";
    private static final String BUBBLE_CATCH_ME_UP = "Catch me up";
    private static final String BUBBLE_RECORD = "Record";
    private static final String BUBBLE_PAY_ATTENTION_ACTIVE = "Pay Attention Active";
    private static final String BUBBLE_EXPAND_PAY_ATTENTION = "Expand Pay Attention";
    private static final String BUBBLE_CAMERA = "Camera";
    private static final String BUBBLE_COLLAPSED_QIRA = "Qira";

    /**
     * Locale-specific aliases for the bubble-bar content descriptions. Some
     * Qira builds localize the desc strings (notably ja-JP and zh-CN), even
     * though English copies remain for "Catch me up" and "Record". The
     * matching code below compares against every alias in the array so the
     * bubble-bar visibility heuristic still finds 3+ anchors on those
     * locales.
     */
    private static final String[] BUBBLE_CHAT_ALIASES = {
            "Chat",
            "チャット",
            "聊天",
            "Chatear",
            "Conversar",
            "\u0645\u062d\u0627\u062f\u062b\u0629"
    };
    private static final String[] BUBBLE_LIVE_ALIASES = {
            "Live",
            "ライブ",
            "直播",
            "En vivo",
            "En direct",
            "Em direto",
            "Em direct",
            "En directo",
            "In diretta",
            "Na żywo",
            "În direct",
            "Live"
    };
    private static final String[] BUBBLE_CATCH_ME_UP_ALIASES = {
            "Catch me up",
            "Catch Me Up",
            "Update me",
            "What's new?",
            "Whats new?",
            "Was gibt es Neues?",
            "Quoi de neuf",
            "Novidades",
            "Novedades",
            "Novità",
            "Co nowego",
            "追いつかせて",
            "近况"
    };
    private static final String[] BUBBLE_RECORD_ALIASES = {
            "Record",
            "Pay Attention Active",
            "Expand Pay Attention",
            "録音",
            "录制",
            "Grabar",
            "Gravar",
            "Enregistrer",
            "Aufnehmen",
            "Registra",
            "Nagrywaj",
            "Înregistrează"
    };

    private static boolean matchesAny(String value, String[] aliases) {
        if (value == null) {
            return false;
        }
        for (String alias : aliases) {
            if (alias.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static final String[] CHAT_INTRO_LABELS = {
            "Find inspiration, get things done, and stay organized.",
            "Chat, brainstorm, and collaborate"
    };

    private static final String[] CHAT_TRY_IT_LABELS = {
            "Try it",
            "Try It",
            "Ausprobieren",
            "Essayer",
            "Probar",
            "Experimentar",
            "Prova",
            "\u062c\u0631\u0651\u0628",
            "\u062c\u0631\u0651\u0628\u0647\u0627",
            "\u062c\u0631\u0628\u0647\u0627"
    };

    private static final String[] CHAT_COMPOSE_LABELS = {
            "What are you looking for?",
            "Ask anything"
    };

    private static final String[] CHAT_THINKING_LABELS = {
            "Thinking",
            "Generating",
            "Looking that up"
    };

    private static final String[] LIVE_INTRO_LABELS = {
            "Collaborate whenever, wherever with real-time, multimodal interactions.",
            "Tapping the Live button turns on the mic",
            // German
            "Arbeiten Sie zusammen",
            // French
            "Collaborez",
            // Spanish
            "Colabora",
            // Italian
            "Collabora",
            // Portuguese
            "Colabore",
            // Polish
            "Wsp\u00f3\u0142pracuj",
            // Romanian
            "Colaboreaz\u0103",
            // Japanese: ライブボタン / リアルタイム
            "\u30e9\u30a4\u30d6\u30dc\u30bf\u30f3",
            "\u30ea\u30a2\u30eb\u30bf\u30a4\u30e0",
            // Chinese (Simplified): 实时 / 直播按钮
            "\u5b9e\u65f6",
            "\u76f4\u64ad\u6309\u94ae"
    };

    private static final String[] LIVE_AGREEMENT_LABELS = {
            "screen sharing",
            "Screen sharing",
            "mic",
            "we ask your permission",
            // German
            "Bildschirm",
            "Mikrofon",
            "Berechtigung",
            // French
            "partage d'\u00e9cran",
            "partage d\u2019\u00e9cran",
            "microphone",
            "autorisation",
            // Spanish
            "compartir pantalla",
            "micr\u00f3fono",
            "permiso",
            // Italian
            "condivisione schermo",
            "microfono",
            "autorizzazione",
            // Portuguese
            "compartilhamento de tela",
            "microfone",
            "permiss\u00e3o",
            // Polish
            "udost\u0119pnianie ekranu",
            "mikrofon",
            "uprawnienie",
            // Romanian
            "partajarea ecranului",
            "microfon",
            "permisiune",
            // Japanese: 画面共有, マイク, 許可をお願い
            "\u753b\u9762\u5171\u6709",
            "\u30de\u30a4\u30af",
            "\u8a31\u53ef\u3092\u304a\u9858\u3044",
            "\u8a31\u53ef",
            // Chinese (Simplified): 屏幕共享, 麦克风, 我们需要您的权限
            "\u5c4f\u5e55\u5171\u4eab",
            "\u9ea6\u514b\u98ce",
            "\u9700\u8981\u60a8\u7684\u6743\u9650"
    };

    private static final String[] LIVE_SHARE_SCREEN_LABELS = {
            "Share your screen with Live?",
            "While Live is active",
            "Avoid sharing sensitive information"
    };

    private static final String[] LIVE_START_LABELS = {
            "Start Live",
            "Start live"
    };

    private static final String[] LIVE_ENABLE_PERMISSION_MESSAGE_LABELS = {
            "To get AI suggestions, your permission is required.",
            "To get AI suggestions, your permission is required",
            "AI suggestions"
    };

    private static final String[] LIVE_ENABLE_PERMISSION_ACTION_LABELS = {
            "Enable permission",
            "Enable Permission"
    };

    private static final String[] ACTION_CORE_ENABLE_SCREEN_LABELS = {
            "Get personalized AI content",
            "Moto Screen Assistant uses Accessibility Services",
            "Moto Action Core"
    };

    private static final String[] ACTION_CORE_ENABLE_ACTION_LABELS = {
            "Enable"
    };

    private static final String[] CATCH_ME_UP_INTRO_LABELS = {
            "Get caught up on what you missed across your devices.",
            "Get a summary of your notifications",
            // Japanese: "Catch up on what you missed across devices"
            "デバイス間で見逃したものを",
            "デバイス間",
            "見逃した",
            // Chinese (zh-CN): "Catch up on what you missed across devices"
            "了解您在设备",
            "错过的内容",
            "通知摘要",
            // Spanish: "Get caught up..."
            "Ponte al día",
            "Ponte al dia",
            "resumen de tus notificaciones",
            // Portuguese: "Atualize-se sobre..."
            "Atualize-se",
            "resumo das suas notificações",
            "resumo das notificações",
            // French
            "Faites le point",
            "résumé de vos notifications",
            "Mettez-vous au courant",
            // German
            "Bleiben Sie auf dem Laufenden",
            "Zusammenfassung Ihrer Benachrichtigungen",
            // Italian
            "Aggiornati su quello",
            "riepilogo delle tue notifiche",
            // Polish
            "Bądź na bieżąco",
            "podsumowanie Twoich powiadomień",
            // Romanian
            "Pune-te la curent",
            "rezumat al notificărilor"
    };

    private static final String[] CATCH_ME_UP_AGREEMENT_LABELS = {
            "Clear summarized notifications",
            "Things to know",
            // German
            "Wichtige Informationen",
            "Hinweise",
            // French
            "\u00c0 savoir",
            "Informations importantes",
            // Spanish
            "Aspectos a tener en cuenta",
            "Cosas que debes saber",
            // Italian
            "Cose da sapere",
            // Portuguese
            "O que voc\u00ea precisa saber",
            "Coisas a saber",
            // Polish
            "Co warto wiedzie\u0107",
            // Romanian
            "Lucruri de \u015ftiut",
            // Japanese: 知っておくこと / お知らせ
            "\u77e5\u3063\u3066\u304a\u304f\u3053\u3068",
            "\u304a\u77e5\u3089\u305b",
            // Chinese (Simplified): 须知 / 注意事项
            "\u987b\u77e5",
            "\u6ce8\u610f\u4e8b\u9879"
    };

    private static final String[] CATCH_ME_UP_PROCESSING_LABELS = {
            "Gathering latest notifications",
            "Analyzing contents",
            "Combining themes",
            "Catching you up",
            // Japanese
            "\u6700\u65b0\u306e\u901a\u77e5\u3092\u53d6\u5f97",  // 最新の通知を取得
            "\u30b3\u30f3\u30c6\u30f3\u30c4\u3092\u5206\u6790",    // コンテンツを分析
            // Chinese (Simplified)
            "\u83b7\u53d6\u6700\u65b0\u901a\u77e5",                // 获取最新通知
            "\u5206\u6790\u5185\u5bb9"                              // 分析内容
    };

    private static final String[] CATCH_ME_UP_SUMMARY_LABELS = {
            "Here is the summary",
            "Summary",
            "No new notifications",
            "No notifications to summarize",
            // German
            "Hier ist die Zusammenfassung", "Zusammenfassung",
            "Keine neuen Benachrichtigungen",
            // French
            "Voici le r\u00e9sum\u00e9", "R\u00e9sum\u00e9",
            "Aucune nouvelle notification",
            // Spanish
            "Aqu\u00ed est\u00e1 el resumen", "Resumen",
            "Sin notificaciones nuevas",
            // Italian
            "Ecco il riepilogo", "Riepilogo",
            "Nessuna nuova notifica",
            // Portuguese (Brazil)
            "Aqui est\u00e1 o resumo", "Resumo",
            "Sem novas notifica\u00e7\u00f5es",
            // Polish
            "Oto podsumowanie", "Podsumowanie",
            "Brak nowych powiadomie\u0144",
            // Romanian
            "Iat\u0103 rezumatul", "Rezumat",
            "Nicio notificare nou\u0103",
            // Japanese: 要約はこちら / 要約 / 新しい通知はありません
            "\u8981\u7d04\u306f\u3053\u3061\u3089",
            "\u8981\u7d04",
            "\u65b0\u3057\u3044\u901a\u77e5\u306f\u3042\u308a\u307e\u305b\u3093",
            // Chinese (Simplified): 这是摘要 / 摘要 / 没有新通知
            "\u8fd9\u662f\u6458\u8981",
            "\u6458\u8981",
            "\u6ca1\u6709\u65b0\u901a\u77e5"
    };

    private static final String[] PAY_ATTENTION_INTRO_LABELS = {
            "Motorola Qira transcribes your conversations and meetings",
            "Seamlessly detect and record meetings",
            // German
            "transkribiert",
            // French
            "transcrit",
            // Spanish
            "transcribe",
            // Italian
            "trascrive",
            // Portuguese
            "transcreve",
            // Polish
            "transkrybuje",
            // Romanian
            "transcrie",
            // Japanese: 会話と会議を文字起こし
            "\u4f1a\u8a71\u3068\u4f1a\u8b70",
            "\u6587\u5b57\u8d77\u3053\u3057",
            // Chinese (Simplified): 转录您的对话和会议
            "\u8f6c\u5f55\u60a8\u7684\u5bf9\u8bdd",
            "\u5bf9\u8bdd\u548c\u4f1a\u8bae"
    };

    private static final String[] PAY_ATTENTION_AGREEMENT_LABELS = {
            "Things to know",
            "cloud",
            "we ask your permission",
            // German
            "Wichtige Informationen",
            "Cloud",
            "Berechtigung",
            // French
            "\u00c0 savoir",
            "cloud",
            "autorisation",
            // Spanish
            "Aspectos a tener en cuenta",
            "nube",
            "permiso",
            // Italian
            "Cose da sapere",
            "cloud",
            "autorizzazione",
            // Portuguese
            "O que voc\u00ea precisa saber",
            "nuvem",
            "permiss\u00e3o",
            // Polish
            "Co warto wiedzie\u0107",
            "chmura",
            "uprawnienie",
            // Romanian
            "Lucruri de \u015ftiut",
            "cloud",
            "permisiune",
            // Japanese: 知っておくこと / クラウド / 許可をお願い
            "\u77e5\u3063\u3066\u304a\u304f\u3053\u3068",
            "\u30af\u30e9\u30a6\u30c9",
            "\u8a31\u53ef\u3092\u304a\u9858\u3044",
            // Chinese (Simplified): 须知 / 云端 / 我们需要您的权限
            "\u987b\u77e5",
            "\u4e91\u7aef",
            "\u9700\u8981\u60a8\u7684\u6743\u9650"
    };

    private static final String[] BY_PROCEEDING_LABELS = {
            "By proceeding, you confirm everyone being transcribed has given consent",
            "By proceeding",
            // German: "Indem Sie fortfahren..."
            "Indem Sie fortfahren",
            // French: "En continuant..."
            "En continuant",
            // Spanish: "Al continuar..."
            "Al continuar",
            // Italian: "Procedendo..."
            "Procedendo",
            // Portuguese (Brazil): "Ao continuar..."
            "Ao continuar",
            // Polish: "Kontynuując..."
            "Kontynuuj\u0105c",
            // Romanian: "Continuând..."
            "Continu\u00e2nd",
            // Japanese: 続行することで, 続行することにより
            "\u7d9a\u884c\u3059\u308b\u3053\u3068\u3067",
            "\u7d9a\u884c\u3059\u308b\u3053\u3068\u306b\u3088\u308a",
            // Chinese (Simplified): 继续操作, 继续即表示
            "\u7ee7\u7eed\u64cd\u4f5c",
            "\u7ee7\u7eed\u5373\u8868\u793a"
    };

    private static final String[] AGREEMENT_PRIMARY_ACTION_LABELS = {
            "I agree",
            "Agree",
            "Accept",
            "Continue",
            // German.
            "Ich stimme zu",
            "Zustimmen",
            "Akzeptieren",
            "Fortfahren",
            "Weiter",
            // French.
            "J'accepte",
            "J\u2019accepte",
            "Accepter",
            "Continuer",
            // Spanish.
            "Acepto",
            "Aceptar",
            "Estoy de acuerdo",
            "Continuar",
            // Italian.
            "Accetto",
            "Accetta",
            "Continua",
            // Portuguese (Brazil).
            "Concordo",
            "Aceitar",
            "Aceito",
            "Continuar",
            // Polish.
            "Zgadzam si\u0119",
            "Akceptuj\u0119",
            "Akceptuj",
            "Kontynuuj",
            "Dalej",
            // Romanian.
            "Sunt de acord",
            "Accept",
            "Continu\u0103",
            // Japanese.
            "\u540c\u610f\u3057\u307e\u3059",
            "\u540c\u610f\u3059\u308b",
            "\u7d9a\u884c",
            // Chinese (Simplified).
            "\u540c\u610f",
            "\u6211\u540c\u610f",
            "\u7ee7\u7eed",
            "\u63a5\u53d7"
    };

    private static final String[] PAY_ATTENTION_PROCESSING_LABELS = {
            "Generating Summary",
            "Analyzing the transcript",
            "Identifying main topics",
            "Identifying action items",
            "Formatting summary",
            // Japanese
            "\u8981\u7d04\u3092\u751f\u6210",     // 要約を生成 (Generating Summary)
            "\u30c8\u30e9\u30f3\u30b9\u30af\u30ea\u30d7\u30c8\u3092\u5206\u6790", // トランスクリプトを分析
            "\u4e3b\u8981\u30c8\u30d4\u30c3\u30af",  // 主要トピック
            // Chinese (Simplified)
            "\u751f\u6210\u6458\u8981",            // 生成摘要
            "\u5206\u6790\u8f6c\u5f55"             // 分析转录
    };

    /**
     * Localized labels per tab. The Pay Attention card has three tabs in
     * a fixed left-to-right order: Summary, Transcript, Audio Recording.
     * Each constant below holds every locale variant we have observed
     * in the wild, including Compose-truncated forms (e.g. "Zusammenfa..."
     * appears when the pill is too narrow for the full word). The
     * truncated forms are intentionally kept in this list so a startsWith
     * / contains-style match still picks the right pill on small folds.
     *
     * <p>{@link #selectPayAttentionTabAndVerify(String, long)} walks
     * each list desc-first then text-first. If every label misses, the
     * helper falls back to a geometry tap (1st / 2nd / 3rd pill from
     * the left of the tab strip) - tab order is the same in every
     * locale so this is a sound locale-independent fallback.
     */
    private static final String[] PAY_ATTENTION_TAB_LABELS_SUMMARY = {
            "Summary", "Summary\u2026", "Summary...",
            // German (de / de-DE).
            "Zusammenfassung", "Zusammenfa\u2026", "Zusammenfa...",
            // French (fr / fr-FR).
            "R\u00e9sum\u00e9", "R\u00e9sum\u00e9\u2026",
            // Spanish (es / es-ES / es-US).
            "Resumen", "Resumen\u2026",
            // Italian (it / it-IT).
            "Riepilogo", "Riepilogo\u2026",
            // Portuguese (pt-BR).
            "Resumo", "Resumo\u2026",
            // Polish (pl / pl-PL).
            "Podsumowanie", "Podsumowa\u2026",
            // Romanian (ro / ro-RO).
            "Rezumat", "Rezumat\u2026",
            // Japanese (ja-JP).
            "\u8981\u7d04",        // 要約
            // Chinese Simplified (zh-CN).
            "\u6458\u8981"         // 摘要
    };

    private static final String[] PAY_ATTENTION_TAB_LABELS_TRANSCRIPT = {
            "Transcript", "Transcript\u2026", "Transcript...",
            // German - the live de-DE UI shows "Transkription" (not the
            // older "Transkript"); keep both so older builds still match.
            "Transkription", "Transkriptionen", "Transkripti\u2026",
            "Transkript", "Transkript\u2026",
            // French.
            "Transcription", "Transcription\u2026",
            // Spanish.
            "Transcripci\u00f3n", "Transcripci\u00f3n\u2026",
            "Transcripcion", "Transcripcion\u2026",
            // Italian.
            "Trascrizione", "Trascrizione\u2026",
            // Portuguese.
            "Transcri\u00e7\u00e3o", "Transcri\u00e7\u00e3o\u2026",
            "Transcricao", "Transcricao\u2026",
            // Polish.
            "Transkrypcja", "Transkrypcja\u2026",
            // Romanian.
            "Transcriere", "Transcriere\u2026",
            // Japanese.
            "\u30c8\u30e9\u30f3\u30b9\u30af\u30ea\u30d7\u30c8",   // トランスクリプト
            "\u8a18\u9332",                                       // 記録 (alternative label)
            // Chinese Simplified.
            "\u8f6c\u5f55"                                        // 转录
    };

    private static final String[] PAY_ATTENTION_TAB_LABELS_AUDIO_RECORDING = {
            "Audio Recording", "Audio Recordi\u2026", "Audio Recordi...",
            // German - the live de-DE UI shows "Audioaufnahme" (not the
            // older "Audioaufzeichnung"); keep both.
            "Audioaufnahme", "Audioaufnah\u2026",
            "Audioaufzeichnung", "Audioaufzei\u2026",
            // French.
            "Enregistrement audio", "Enregistrement\u2026",
            // Spanish.
            "Grabaci\u00f3n de audio", "Grabaci\u00f3n\u2026",
            "Grabacion de audio", "Grabacion\u2026",
            // Italian.
            "Registrazione audio", "Registrazione\u2026",
            // Portuguese.
            "Grava\u00e7\u00e3o de \u00e1udio", "Grava\u00e7\u00e3o\u2026",
            "Gravacao de audio", "Gravacao\u2026",
            // Polish.
            "Nagranie audio", "Nagranie\u2026",
            // Romanian.
            "\u00cenregistrare audio", "\u00cenregistrare\u2026",
            "Inregistrare audio", "Inregistrare\u2026",
            // Japanese.
            "\u97f3\u58f0\u9332\u97f3",        // 音声録音
            // Chinese Simplified.
            "\u5f55\u97f3"                     // 录音
    };

    /**
     * Union of all three tab dictionaries above. Used by
     * {@link #arePayAttentionTabsVisible()} to confirm the tab strip
     * is on screen before tapping any tab.
     */
    private static final String[] PAY_ATTENTION_TAB_LABELS;
    static {
        java.util.ArrayList<String> all = new java.util.ArrayList<>(
                PAY_ATTENTION_TAB_LABELS_SUMMARY.length
                + PAY_ATTENTION_TAB_LABELS_TRANSCRIPT.length
                + PAY_ATTENTION_TAB_LABELS_AUDIO_RECORDING.length);
        java.util.Collections.addAll(all, PAY_ATTENTION_TAB_LABELS_SUMMARY);
        java.util.Collections.addAll(all, PAY_ATTENTION_TAB_LABELS_TRANSCRIPT);
        java.util.Collections.addAll(all, PAY_ATTENTION_TAB_LABELS_AUDIO_RECORDING);
        PAY_ATTENTION_TAB_LABELS = all.toArray(new String[0]);
    }

    /**
     * Localized headline text that appears IMMEDIATELY under the tab
     * strip when the matching tab is selected. We use these to verify
     * the tap actually changed the active tab (Compose tab pills do
     * not expose AccessibilityNodeInfo.selected reliably, so the body
     * headline is a more reliable signal). Indexed in lock-step with
     * the per-tab label arrays above:
     * 0 = Summary headline, 1 = Transcript headline, 2 = Audio Recording.
     */
    private static final String[][] PAY_ATTENTION_BODY_HEADLINES = {
            // Summary headline ("Here is the summary").
            {
                    "Here is the summary",
                    "Hier ist die Zusammenfassung",
                    "Voici le r\u00e9sum\u00e9",
                    "Aqu\u00ed est\u00e1 el resumen",
                    "Ecco il riepilogo",
                    "Aqui est\u00e1 o resumo",
                    "Oto podsumowanie",
                    "Iat\u0103 rezumatul",
                    "\u8981\u7d04\u306f\u4ee5\u4e0b\u306e\u3068\u304a\u308a\u3067\u3059",
                    "\u4ee5\u4e0b\u662f\u6458\u8981"
            },
            // Transcript headline ("Here is the transcript").
            {
                    "Here is the transcript",
                    "Hier ist die Transkription",
                    "Hier ist das Transkript",
                    "Voici la transcription",
                    "Aqu\u00ed est\u00e1 la transcripci\u00f3n",
                    "Aqui est\u00e1 a transcri\u00e7\u00e3o",
                    "Ecco la trascrizione",
                    "Oto transkrypcja",
                    "Iat\u0103 transcrierea",
                    "\u30c8\u30e9\u30f3\u30b9\u30af\u30ea\u30d7\u30c8\u306f\u4ee5\u4e0b\u306e\u3068\u304a\u308a\u3067\u3059",
                    "\u4ee5\u4e0b\u662f\u8f6c\u5f55"
            },
            // Audio Recording headline ("Here is the audio recording").
            {
                    "Here is the audio recording",
                    "Hier ist die Audioaufnahme",
                    "Hier ist die Audioaufzeichnung",
                    "Voici l\u2019enregistrement audio",
                    "Voici l'enregistrement audio",
                    "Aqu\u00ed est\u00e1 la grabaci\u00f3n de audio",
                    "Aqui est\u00e1 a grava\u00e7\u00e3o de \u00e1udio",
                    "Ecco la registrazione audio",
                    "Oto nagranie audio",
                    "Iat\u0103 \u00eenregistrarea audio",
                    "\u97f3\u58f0\u9332\u97f3\u306f\u4ee5\u4e0b\u306e\u3068\u304a\u308a\u3067\u3059",
                    "\u4ee5\u4e0b\u662f\u5f55\u97f3"
            }
    };

    private static final String[] UNEXPECTED_ERROR_LABELS = {
            "Unexpected Error",
            "An unexpected issue has occurred while processing your request"
    };

    /**
     * Pattern matched against the hero carousel's root content description on
     * the Qira home surface. The carousel renders a single view per page with a
     * {@code content-desc} of "Page N of M"; we parse the two numbers to
     * navigate across the Focus Zone introduction slides.
     */
    private static final Pattern SLIDE_PAGE_PATTERN =
            Pattern.compile("Page (\\d+) of (\\d+)");

    private static final String NOTIFICATION_ALLOW_RES =
            "com.android.permissioncontroller:id/permission_allow_button";
    private static final String PERMISSION_ALLOW_FG_RES =
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button";
    private static final String PERMISSION_ALLOW_ONE_TIME_RES =
            "com.android.permissioncontroller:id/permission_allow_one_time_button";

    private static final Logger FOCUS_ZONE_LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    public QiraFocusZonePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    // ---------------------------------------------------------------------
    // Bubble bar
    // ---------------------------------------------------------------------

    public QiraFocusZonePage waitForBubbleBar() throws Exception {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isBubbleBarVisible()) {
                settle();
                return this;
            }
            if (expandCollapsedBubbleBarIfPresent()) {
                continue;
            }
            mUtils.sleep(250L);
        }
        logVisibleLabels("waitForBubbleBar timeout");
        throw new IllegalStateException("Unable to detect the Motorola Qira Focus Zone bubble bar");
    }

    public boolean isBubbleBarVisible() {
        try {
            List<UiObject2> objects = mDevice.findObjects(By.pkg(mConfig.getPackageName()));
            boolean chat = false;
            boolean live = false;
            boolean catchMeUp = false;
            boolean record = false;
            int minY = (mDevice.getDisplayHeight() * 3) / 4;
            for (UiObject2 obj : objects) {
                try {
                    String d = obj.getContentDescription();
                    if (d == null) {
                        continue;
                    }
                    Rect bounds = obj.getVisibleBounds();
                    if (bounds == null || bounds.isEmpty() || bounds.centerY() < minY) {
                        continue;
                    }
                    // Strip Unicode bidi control characters so pseudo-locales
                    // (en-XM / en-XA / ar-XB) that wrap every content-desc
                    // with LRI/PDI isolates still match the plain English
                    // anchor literals below.
                    String clean = QiraStrings.stripBidiControls(d);
                    if (matchesAny(clean, BUBBLE_CHAT_ALIASES)) {
                        chat = true;
                    } else if (matchesAny(clean, BUBBLE_LIVE_ALIASES)) {
                        live = true;
                    } else if (matchesAny(clean, BUBBLE_CATCH_ME_UP_ALIASES)) {
                        catchMeUp = true;
                    } else if (matchesAny(clean, BUBBLE_RECORD_ALIASES)) {
                        record = true;
                    }
                } catch (StaleObjectException ignored) {
                    // Node was recycled while we were iterating; skip it.
                }
            }
            int anchors = (chat ? 1 : 0) + (live ? 1 : 0)
                    + (catchMeUp ? 1 : 0) + (record ? 1 : 0);
            return anchors >= 3;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Finds the bubble-bar icon whose content description matches exactly.
     * The bubble bar is anchored to the bottom of the Qira surface; other
     * elements (for example an "App Icon" node inside the in-app app bar)
     * share the same description at the top of the screen, so we restrict
     * matches to the bottom quarter and return the lowest match.
     *
     * <p>Content descriptions are compared after stripping Unicode bidi
     * control characters so pseudo-locales ({@code en-XM} / {@code en-XA}
     * / {@code ar-XB}) that wrap every string with LRI/PDI isolates still
     * match the plain English {@code description} argument.
     */
    private UiObject2 findBubbleIconByDesc(String description) {
        List<UiObject2> objects = mDevice.findObjects(By.pkg(mConfig.getPackageName()));
        UiObject2 best = null;
        int bestY = Integer.MIN_VALUE;
        int minY = (mDevice.getDisplayHeight() * 3) / 4;
        for (UiObject2 obj : objects) {
            try {
                String d = obj.getContentDescription();
                if (d == null) {
                    continue;
                }
                String clean = QiraStrings.stripBidiControls(d);
                if (!description.equals(clean)) {
                    continue;
                }
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.centerY() < minY) {
                    continue;
                }
                if (bounds.centerY() > bestY) {
                    bestY = bounds.centerY();
                    best = obj;
                }
            } catch (StaleObjectException ignored) {
                // Node was recycled; skip it.
            }
        }
        return best;
    }

    private boolean clickBubbleDescription(String description) throws Exception {
        UiObject2 icon = findBubbleIconByDesc(description);
        if (icon == null) {
            icon = findByStableDescription(description);
        }
        if (icon == null) {
            // Locale-aware fallback: walk known aliases for the requested
            // bubble (so e.g. "Chat" matches the ja-JP "チャット" desc).
            String[] aliases = aliasesFor(description);
            if (aliases != null) {
                for (String alias : aliases) {
                    if (alias.equals(description)) continue;
                    icon = findBubbleIconByDesc(alias);
                    if (icon != null) break;
                    icon = findByStableDescription(alias);
                    if (icon != null) break;
                }
            }
        }
        if (icon == null) {
            return false;
        }
        clickObject(icon);
        return true;
    }

    private String[] aliasesFor(String description) {
        if (BUBBLE_CHAT.equals(description)) return BUBBLE_CHAT_ALIASES;
        if (BUBBLE_LIVE.equals(description)) return BUBBLE_LIVE_ALIASES;
        if (BUBBLE_CATCH_ME_UP.equals(description)) return BUBBLE_CATCH_ME_UP_ALIASES;
        if (BUBBLE_RECORD.equals(description)
                || BUBBLE_PAY_ATTENTION_ACTIVE.equals(description)
                || BUBBLE_EXPAND_PAY_ATTENTION.equals(description)) {
            return BUBBLE_RECORD_ALIASES;
        }
        return null;
    }

    /**
     * Locale-aware variant of {@link #findBubbleIconByDesc(String)} that walks
     * every known alias for the requested bubble. Used by visibility heuristics
     * (e.g. {@code isLiveBubbleVisible}) so they can find the right icon on
     * locales where the desc is translated but the layout is unchanged.
     */
    private UiObject2 findBubbleIconByAnyAlias(String canonical) {
        UiObject2 hit = findBubbleIconByDesc(canonical);
        if (hit != null) {
            return hit;
        }
        String[] aliases = aliasesFor(canonical);
        if (aliases == null) {
            return null;
        }
        for (String alias : aliases) {
            if (alias.equals(canonical)) continue;
            UiObject2 obj = findBubbleIconByDesc(alias);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Locale-safe fallback for Focus Zone bubbles whose labels/descriptions are
     * translated or absent. The bar layout is stable at the bottom edge:
     * AppIcon, Chat, Live, CatchMeUp, Record. Some builds omit AppIcon in this
     * row; callers provide both index variants.
     */
    private boolean clickBubbleBySlot(int slotWithAppIcon, int slotWithoutAppIcon) throws Exception {
        UiObject2 bubble = findBubbleBySlot(slotWithAppIcon, slotWithoutAppIcon);
        if (bubble == null) {
            return false;
        }
        clickObject(bubble);
        return true;
    }

    private UiObject2 findBubbleBySlot(int slotWithAppIcon, int slotWithoutAppIcon) {
        List<UiObject2> row = findBubbleRowCandidates();
        if (row.size() < 4) {
            return null;
        }
        int slot = row.size() >= 5 ? slotWithAppIcon : slotWithoutAppIcon;
        if (slot < 0) {
            slot = 0;
        }
        if (slot >= row.size()) {
            slot = row.size() - 1;
        }
        int physicalSlot = isRtlLayout() ? row.size() - 1 - slot : slot;
        return row.get(physicalSlot);
    }

    private List<UiObject2> findBubbleRowCandidates() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> raw = new ArrayList<>();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (!isBubbleRowCandidateBounds(bounds, width, height)) {
                    continue;
                }
                raw.add(object);
            } catch (StaleObjectException ignored) {
                // Node recycled mid-scan.
            }
        }
        raw.sort((a, b) -> {
            Rect ab = safeVisibleBounds(a);
            Rect bb = safeVisibleBounds(b);
            int ax = ab != null ? ab.centerX() : Integer.MAX_VALUE;
            int bx = bb != null ? bb.centerX() : Integer.MAX_VALUE;
            return Integer.compare(ax, bx);
        });

        List<UiObject2> deduped = new ArrayList<>();
        int xTolerance = Math.max(24, (width * 3) / 100);
        for (UiObject2 candidate : raw) {
            Rect cb = safeVisibleBounds(candidate);
            if (cb == null || cb.isEmpty()) {
                continue;
            }
            if (deduped.isEmpty()) {
                deduped.add(candidate);
                continue;
            }
            UiObject2 last = deduped.get(deduped.size() - 1);
            Rect lb = safeVisibleBounds(last);
            if (lb != null && Math.abs(cb.centerX() - lb.centerX()) <= xTolerance) {
                int cArea = cb.width() * cb.height();
                int lArea = lb.width() * lb.height();
                if (cArea > lArea) {
                    deduped.set(deduped.size() - 1, candidate);
                }
            } else {
                deduped.add(candidate);
            }
        }
        return deduped;
    }

    private Rect safeVisibleBounds(UiObject2 object) {
        try {
            return object.getVisibleBounds();
        } catch (StaleObjectException stale) {
            return null;
        }
    }

    private boolean isBubbleRowCandidateBounds(Rect bounds, int width, int height) {
        if (bounds == null || bounds.isEmpty()) {
            return false;
        }
        if (bounds.top < (height * 84) / 100 || bounds.bottom > (height * 99) / 100) {
            return false;
        }
        if (bounds.width() < (width * 2) / 100 || bounds.width() > (width * 20) / 100) {
            return false;
        }
        if (bounds.height() < (height * 2) / 100 || bounds.height() > (height * 14) / 100) {
            return false;
        }
        return bounds.centerX() > (width * 2) / 100
                && bounds.centerX() < (width * 98) / 100;
    }

    /**
     * Some builds show the Focus Zone as a collapsed single "Qira" bubble
     * (floating near the horizontal edge) before the full bar is expanded.
     * Tap that trigger proactively so waitForBubbleBar() can proceed.
     */
    private boolean expandCollapsedBubbleBarIfPresent() throws Exception {
        UiObject2 collapsed = findCollapsedQiraBubble();
        if (collapsed == null) {
            return false;
        }
        clickObject(collapsed);
        settle();
        mUtils.sleep(350L);
        return true;
    }

    private UiObject2 findCollapsedQiraBubble() {
        UiObject2 best = null;
        int bestEdgeDistance = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 obj : mDevice.findObjects(By.pkg(mConfig.getPackageName())
                .desc(exactPatternForLabel(BUBBLE_COLLAPSED_QIRA)))) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                int centerY = bounds.centerY();
                if (centerY < (height * 20) / 100 || centerY > (height * 85) / 100) {
                    continue;
                }
                if (bounds.width() > (width * 12) / 100 || bounds.height() > (height * 15) / 100) {
                    continue;
                }
                int edgeDistance = Math.min(bounds.left, width - bounds.right);
                if (edgeDistance > (width * 12) / 100) {
                    continue;
                }
                if (edgeDistance < bestEdgeDistance) {
                    bestEdgeDistance = edgeDistance;
                    best = obj;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled mid-scan.
            }
        }
        return best;
    }

    // ---------------------------------------------------------------------
    // Focus Zone slide carousel
    // ---------------------------------------------------------------------

    /**
     * Taps the Focus Zone entry on the bubble bar (the left-most icon carrying
     * the Qira "App Icon" content description). This returns to the Qira home
     * surface where the paginated Focus Zone slide carousel is shown.
     */
    public void tapFocusZoneAppIcon() throws Exception {
        if (!clickBubbleDescription(BUBBLE_APP_ICON)
                && !clickBubbleBySlot(0, 0)) {
            throw new IllegalStateException("Unable to tap the Focus Zone App Icon bubble");
        }
        settle();
    }

    /**
     * Locates the hero slide carousel on the Qira home screen. The carousel is
     * exposed by the app with a {@code content-desc} like "Page N of M" on the
     * outer view, so we search for the largest element matching that pattern to
     * avoid transient child views that may share the description.
     */
    public UiObject2 findSlidePager() {
        // Rule-set #2: pair the pattern match with a pkg filter. Otherwise a
        // system overlay (e.g. an accessibility tutorial that also surfaces
        // "Page N of M") could win the first round-trip before we fall back
        // to the pkg-scoped scan below.
        UiObject2 direct = mDevice.findObject(
                By.pkg(mConfig.getPackageName()).desc(SLIDE_PAGE_PATTERN));
        if (direct != null) {
            try {
                Rect bounds = direct.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    return direct;
                }
            } catch (StaleObjectException ignored) {
                // Fall through to the manual scan below.
            }
        }

        UiObject2 best = null;
        int bestArea = 0;
        List<UiObject2> objects = mDevice.findObjects(By.pkg(mConfig.getPackageName()));
        for (UiObject2 obj : objects) {
            try {
                String desc = obj.getContentDescription();
                if (desc == null) {
                    continue;
                }
                if (!SLIDE_PAGE_PATTERN.matcher(desc).find()) {
                    continue;
                }
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = obj;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled mid-scan; skip it.
            }
        }
        return best;
    }

    public int currentSlidePage() {
        return readSlidePageComponent(1);
    }

    public int totalSlidePages() {
        return readSlidePageComponent(2);
    }

    private int readSlidePageComponent(int group) {
        UiObject2 pager = findSlidePager();
        if (pager == null) {
            return -1;
        }
        try {
            String desc = pager.getContentDescription();
            if (desc == null) {
                return -1;
            }
            Matcher m = SLIDE_PAGE_PATTERN.matcher(desc);
            if (m.find()) {
                return Integer.parseInt(m.group(group));
            }
        } catch (StaleObjectException ignored) {
            // Node invalidated; caller will retry.
        }
        return -1;
    }

    /**
     * Drags horizontally across the hero carousel to advance (or rewind) a single
     * slide. Uses {@link UiDevice#swipe(int, int, int, int, int)} with a moderate
     * step count so the pager registers a controlled page change rather than a
     * multi-page fling.
     */
    public boolean swipeSlidePager(boolean forward) {
        UiObject2 pager = findSlidePager();
        if (pager == null) {
            return false;
        }
        Rect bounds;
        try {
            bounds = pager.getVisibleBounds();
        } catch (StaleObjectException stale) {
            return false;
        }
        if (bounds == null || bounds.isEmpty()) {
            return false;
        }
        int y = bounds.centerY();
        int margin = Math.max(80, bounds.width() / 8);
        int startX;
        int endX;
        boolean swipeLeft = forward != isRtlLayout();
        if (swipeLeft) {
            startX = bounds.right - margin;
            endX = bounds.left + margin;
        } else {
            startX = bounds.left + margin;
            endX = bounds.right - margin;
        }
        mDevice.swipe(startX, y, endX, y, 25);
        return true;
    }

    /**
     * Navigates the Focus Zone hero carousel to the requested 1-based page
     * number by issuing a series of controlled swipes. The carousel on some
     * devices advances automatically; the loop still lands on the requested
     * page within {@code maxAttempts} swipes.
     */
    public boolean goToSlide(int targetPage, int maxAttempts) throws Exception {
        for (int attempt = 0; attempt < Math.max(1, maxAttempts); attempt++) {
            int current = currentSlidePage();
            if (current == targetPage) {
                settle();
                return true;
            }
            if (current < 0) {
                mUtils.sleep(500L);
                continue;
            }
            swipeSlidePager(current < targetPage);
            mUtils.sleep(1200L);
        }
        return currentSlidePage() == targetPage;
    }

    // ---------------------------------------------------------------------
    // Chat flow
    // ---------------------------------------------------------------------

    public void tapChatBubble() throws Exception {
        if (!clickBubbleDescription(BUBBLE_CHAT)
                && !clickBubbleBySlot(1, 0)) {
            throw new IllegalStateException("Unable to tap the Chat bubble");
        }
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isChatIntroVisible()) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }
        throw new IllegalStateException("Unable to detect the Chat intro");
    }

    public boolean isChatIntroVisible() {
        return hasTextOrDescription(CHAT_INTRO_LABELS)
                || (hasIntroActionPairByGeometry()
                        && findBubbleIconByAnyAlias(BUBBLE_CHAT) != null);
    }

    public void tapChatTryIt() throws Exception {
        if (!clickByExactTextOrDescription(CHAT_TRY_IT_LABELS)) {
            UiObject2 primary = findIntroPrimaryActionByGeometry();
            if (primary == null) {
                primary = findIntroPrimaryActionByGeometryLenient();
            }
            if (primary == null) {
                throw new IllegalStateException("Unable to tap Try it on the Chat intro");
            }
            clickObject(primary);
        }
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isChatComposerVisible()) {
                settle();
                return;
            }
            mUtils.sleep(250L);
        }
        throw new IllegalStateException("Unable to detect the Chat composer");
    }

    public boolean isChatComposerVisible() {
        // Locale-safe primary detection: compose row structure is stable
        // across translations even when placeholder text changes.
        if (isChatComposerVisibleByStructure()) {
            return true;
        }
        return hasTextOrDescription(CHAT_COMPOSE_LABELS);
    }

    public void askChatQuestion(String prompt) throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            focusChatComposerField();

            if (trySetChatText(prompt)) {
                settle();
            } else {
                String escaped = shellSafeInputText(prompt);
                if (!escaped.isEmpty()) {
                    mDevice.executeShellCommand(String.format(Locale.US, "input text %s", escaped));
                    settle();
                }
            }

            if (isChatSendEnabled()) {
                break;
            }
            FOCUS_ZONE_LOGGER.info("askChatQuestion: send still disabled after attempt "
                    + (attempt + 1) + "/3");
            mUtils.sleep(250L);
        }

        if (!isChatSendEnabled()) {
            throw new IllegalStateException("Unable to enter text into Chat composer");
        }

        if (!tapEnabledChatSendAction()) {
            mDevice.executeShellCommand("input keyevent 66");
        }
        settle();
    }

    private boolean isChatComposerVisibleByStructure() {
        // The composer row is identified by attach + microphone + send controls.
        return mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Attach")) != null
                && mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Microphone")) != null
                && mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Send")) != null;
    }

    private void focusChatComposerField() throws Exception {
        UiObject2 textField = findChatTextFieldByGeometry();
        if (textField != null) {
            forceTapCenter(textField);
            mUtils.sleep(120L);
            forceTapCenter(textField);
            settle();
            return;
        }

        Rect inferred = inferChatInputTapBounds();
        if (inferred != null) {
            int offset = Math.max(24, inferred.width() / 4);
            int x = isRtlLayout() ? inferred.right - offset : inferred.left + offset;
            int y = inferred.centerY();
            mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d", x, y));
            mUtils.sleep(120L);
            mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d", x, y));
            settle();
            return;
        }

        UiObject2 container = findChatComposerContainerByGeometry();
        if (container == null) {
            return;
        }
        Rect bounds = container.getVisibleBounds();
        if (bounds == null || bounds.isEmpty()) {
            forceTapCenter(container);
            settle();
            return;
        }
        // Tap toward the logical start where the text cursor appears.
        int offset = Math.max(24, bounds.width() / 4);
        int x = isRtlLayout() ? bounds.right - offset : bounds.left + offset;
        int y = bounds.centerY();
        mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d", x, y));
        mUtils.sleep(120L);
        mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d", x, y));
        settle();
    }

    private boolean trySetChatText(String prompt) {
        UiObject2 input = findChatTextFieldByGeometry();
        if (input == null) {
            input = findChatComposerInputByGeometry();
        }
        if (input == null) {
            return false;
        }
        try {
            input.setText(prompt);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isChatSendEnabled() {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 sendIcon = findChatSendIcon();
                if (sendIcon != null) {
                    UiObject2 clickable = findClickableAncestor(sendIcon);
                    if (isEnabledSafely(clickable) || isEnabledSafely(sendIcon)) {
                        return true;
                    }
                }
            } catch (StaleObjectException stale) {
                sleepQuietly(100L);
            }
        }
        return false;
    }

    private boolean tapEnabledChatSendAction() throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 sendIcon = findChatSendIcon();
                if (sendIcon != null) {
                    UiObject2 clickable = findClickableAncestor(sendIcon);
                    if (tapIfEnabled(clickable) || tapIfEnabled(sendIcon)) {
                        return true;
                    }
                }
            } catch (StaleObjectException stale) {
                sleepQuietly(100L);
            }
            if (tapEnabledChatSendActionByGeometry()) {
                return true;
            }
        }
        return false;
    }

    private UiObject2 findChatSendIcon() {
        return mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Send"));
    }

    private boolean isEnabledSafely(UiObject2 object) {
        if (object == null) {
            return false;
        }
        try {
            return object.isEnabled();
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean tapIfEnabled(UiObject2 object) throws Exception {
        if (!isEnabledSafely(object)) {
            return false;
        }
        try {
            forceTapCenter(object);
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private boolean tapEnabledChatSendActionByGeometry() throws Exception {
        Rect bounds = findEnabledChatSendActionBoundsByGeometry();
        if (bounds == null) {
            return false;
        }
        mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d",
                bounds.centerX(), bounds.centerY()));
        return true;
    }

    private Rect findEnabledChatSendActionBoundsByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        Rect best = null;
        int bestArea = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (!isAtLogicalEnd(bounds.centerX(), width, 74)) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100 || bounds.width() > (width * 18) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                if (!object.isEnabled()) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = new Rect(bounds);
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private Rect inferChatInputTapBounds() {
        UiObject2 attach = mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Attach"));
        UiObject2 microphone = mDevice.findObject(By.pkg(mConfig.getPackageName()).desc("Microphone"));
        if (attach == null || microphone == null) {
            return null;
        }
        try {
            Rect a = attach.getVisibleBounds();
            Rect m = microphone.getVisibleBounds();
            if (a == null || m == null || a.isEmpty() || m.isEmpty()) {
                return null;
            }
            int left = Math.max(0, a.left - 80);
            int right = Math.min(mDevice.getDisplayWidth(), m.left - 40);
            int top = Math.max(0, a.top - 170);
            int bottom = Math.min(mDevice.getDisplayHeight(), a.bottom + 70);
            if (right <= left || bottom <= top) {
                return null;
            }
            return new Rect(left, top, right, bottom);
        } catch (StaleObjectException stale) {
            return null;
        }
    }

    private UiObject2 findChatTextFieldByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestArea = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.ScrollView"))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 80) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 25) / 100 || bounds.width() > (width * 70) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 25) / 100 || bounds.centerX() > (width * 75) / 100) {
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

    private UiObject2 findChatComposerInputByGeometry() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                String clazz = object.getClassName();
                boolean isEditText = "android.widget.EditText".equals(clazz);
                if (!isEditText && !object.isFocusable()) {
                    continue;
                }
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 35) / 100 || bounds.width() > (width * 90) / 100) {
                    continue;
                }
                if ((!isRtlLayout() && bounds.centerX() > (width * 76) / 100)
                        || (isRtlLayout() && bounds.centerX() < (width * 24) / 100)) {
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

    private UiObject2 findChatComposerContainerByGeometry() {
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
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 35) / 100 || bounds.width() > (width * 75) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 4) / 100 || bounds.height() > (height * 20) / 100) {
                    continue;
                }
                if ((!isRtlLayout() && bounds.centerX() > (width * 78) / 100)
                        || (isRtlLayout() && bounds.centerX() < (width * 22) / 100)) {
                    continue;
                }
                if (!object.isEnabled()) {
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

    private UiObject2 findChatSendButtonByGeometry() {
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
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (!isAtLogicalEnd(bounds.centerX(), width, 76)) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100 || bounds.width() > (width * 20) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 2) / 100 || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                if (!object.isEnabled()) {
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

    private String shellSafeInputText(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(raw.length() * 2);
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')) {
                out.append(c);
            } else if (c == ' ') {
                out.append("%s");
            } else if (c == '?') {
                out.append("%3F");
            } else if (c == '\'') {
                // Apostrophes are often dropped by adb input; skip safely.
            }
        }
        return out.toString();
    }

    private void sleepQuietly(long millis) {
        try {
            mUtils.sleep(millis);
        } catch (Exception ignored) {
        }
    }

    private void forceTapCenter(UiObject2 object) throws Exception {
        Rect bounds = object.getVisibleBounds();
        if (bounds == null || bounds.isEmpty()) {
            clickObject(object);
            return;
        }
        int x = bounds.centerX();
        int y = bounds.centerY();
        mDevice.executeShellCommand(String.format(Locale.US, "input tap %d %d", x, y));
    }

    public boolean waitForChatThinking(long timeoutMs) throws Exception {
        return waitForTextOrDescriptionNoAuto(timeoutMs, CHAT_THINKING_LABELS) != null;
    }

    /**
     * Waits for the thinking indicator to clear (answer is now populated). When
     * the indicator never appeared, returns immediately after a short sleep.
     */
    public void waitForChatAnswer(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!hasTextOrDescription(CHAT_THINKING_LABELS)) {
                settle();
                return;
            }
            mUtils.sleep(500L);
        }
    }

    // ---------------------------------------------------------------------
    // Live flow
    // ---------------------------------------------------------------------

    public void tapLiveBubble() throws Exception {
        if (!clickBubbleDescription(BUBBLE_LIVE)
                && !clickBubbleBySlot(2, 1)) {
            logVisibleLabels("tapLiveBubble failed");
            throw new IllegalStateException("Unable to tap the Live bubble");
        }
        settle();
    }

    public boolean isLiveIntroVisible() {
        if (hasTextOrDescription(LIVE_INTRO_LABELS)) {
            return true;
        }
        // Locale-safe fallback: translated intro copy can miss our anchor list.
        return hasIntroActionPairByGeometry()
                && findBubbleIconByAnyAlias(BUBBLE_LIVE) != null
                && findBubbleIconByDesc(BUBBLE_CAMERA) != null;
    }

    public boolean isLiveAgreementVisible() {
        return hasTextOrDescription(LIVE_AGREEMENT_LABELS)
                && (findByExactTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS) != null
                || findAgreementPrimaryActionByGeometry() != null);
    }

    public boolean waitForLiveShareScreenPrompt(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isLiveShareScreenPromptVisible()) {
                settle();
                return true;
            }
            if (isLiveAgreementVisible() || isLiveActive()) {
                return false;
            }
            mUtils.sleep(250L);
        }
        return isLiveShareScreenPromptVisible();
    }

    public boolean isLiveShareScreenPromptVisible() {
        return hasTextOrDescription(LIVE_SHARE_SCREEN_LABELS)
                && (findByExactTextOrDescription(LIVE_START_LABELS) != null
                || findLiveStartActionByGeometry() != null);
    }

    public boolean tapStartLiveIfPresent() throws Exception {
        if (!isLiveShareScreenPromptVisible()) {
            return false;
        }
        if (clickByExactTextOrDescription(LIVE_START_LABELS)) {
            return true;
        }
        UiObject2 action = findLiveStartActionByGeometry();
        if (action == null) {
            return false;
        }
        clickObject(action);
        return true;
    }

    public boolean waitForLiveEnablePermissionPrompt(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isLiveEnablePermissionPromptVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return isLiveEnablePermissionPromptVisible();
    }

    public boolean isLiveEnablePermissionPromptVisible() {
        return hasTextOrDescription(LIVE_ENABLE_PERMISSION_MESSAGE_LABELS)
                && findByExactTextOrDescription(LIVE_ENABLE_PERMISSION_ACTION_LABELS) != null;
    }

    public boolean tapLiveEnablePermissionPrompt() throws Exception {
        if (!isLiveEnablePermissionPromptVisible()) {
            return false;
        }
        if (clickByExactTextOrDescriptionNoAuto(LIVE_ENABLE_PERMISSION_ACTION_LABELS)) {
            return true;
        }

        UiObject2 enablePermission = findLiveEnablePermissionActionByGeometry();
        if (enablePermission == null) {
            return false;
        }
        clickObject(enablePermission);
        return true;
    }

    public boolean waitForMotoActionCoreEnableScreen(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isMotoActionCoreEnableScreenVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return isMotoActionCoreEnableScreenVisible();
    }

    public boolean isMotoActionCoreEnableScreenVisible() {
        if (!ACTION_CORE_PACKAGE.equals(mDevice.getCurrentPackageName())) {
            return false;
        }
        return (findActionCoreExactTextOrDescription(ACTION_CORE_ENABLE_ACTION_LABELS) != null
                || findMotoActionCoreEnableActionByGeometry() != null)
                && (findActionCoreTextOrDescription(ACTION_CORE_ENABLE_SCREEN_LABELS) != null
                || findMotoActionCoreEnableActionByGeometry() != null);
    }

    public boolean tapMotoActionCoreEnable() throws Exception {
        if (!isMotoActionCoreEnableScreenVisible()) {
            return false;
        }
        UiObject2 enable = findActionCoreExactTextOrDescription(
                ACTION_CORE_ENABLE_ACTION_LABELS);
        if (enable != null) {
            UiObject2 clickable = findClickableAncestor(enable);
            clickObject(clickable != null ? clickable : enable);
        } else {
            enable = findMotoActionCoreEnableActionByGeometry();
            if (enable == null) {
                return false;
            }
            clickObject(enable);
        }

        long deadline = System.currentTimeMillis() + 8000L;
        while (System.currentTimeMillis() < deadline) {
            if (!ACTION_CORE_PACKAGE.equals(mDevice.getCurrentPackageName())) {
                settle();
                return true;
            }
            mUtils.sleep(200L);
        }
        return true;
    }

    private UiObject2 findActionCoreTextOrDescription(String... labels) {
        for (String label : labels) {
            UiObject2 object = mDevice.findObject(
                    By.pkg(ACTION_CORE_PACKAGE).desc(patternForLabel(label)));
            if (object != null) {
                return object;
            }
            object = mDevice.findObject(
                    By.pkg(ACTION_CORE_PACKAGE).text(patternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private UiObject2 findActionCoreExactTextOrDescription(String... labels) {
        for (String label : labels) {
            UiObject2 object = mDevice.findObject(
                    By.pkg(ACTION_CORE_PACKAGE).desc(exactPatternForLabel(label)));
            if (object != null) {
                return object;
            }
            object = mDevice.findObject(
                    By.pkg(ACTION_CORE_PACKAGE).text(exactPatternForLabel(label)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private UiObject2 findLiveEnablePermissionActionByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        boolean rtl = isRtlLayout();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 72) / 100
                        || bounds.bottom > (height * 91) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 14) / 100
                        || bounds.width() > (width * 42) / 100) {
                    continue;
                }
                boolean atLogicalEnd = rtl
                        ? bounds.centerX() <= (width * 50) / 100
                        : bounds.centerX() >= (width * 50) / 100;
                if (!atLogicalEnd) {
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

    private UiObject2 findMotoActionCoreEnableActionByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(ACTION_CORE_PACKAGE).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 78) / 100
                        || bounds.bottom > (height * 96) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 65) / 100
                        || bounds.width() > (width * 96) / 100) {
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

    private UiObject2 findLiveStartActionByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!object.isEnabled()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 70) / 100
                        || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 55) / 100
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
    public void tapNext() throws Exception {
        if (!tapNextIfPresent()) {
            throw new IllegalStateException("Unable to tap Next on the current intro card");
        }
    }

    public boolean tapNextIfPresent() throws Exception {
        if (clickByExactTextOrDescription(INTRO_NEXT_LABELS)) {
            return true;
        }
        if (clickByTextOrDescription(INTRO_NEXT_LABELS)) {
            return true;
        }
        // Locale-safe fallback: intro primary action is the upper centered
        // CTA ("Next"/"Weiter"/equivalent), above the "Not now" action.
        UiObject2 next = findIntroPrimaryActionByGeometry();
        if (next != null) {
            clickObject(next);
            return true;
        }
        // Lenient geometry fallback: some locales render the primary "Next"
        // CTA as a small text-only button (e.g. ja-JP, where the visible
        // text is short and the button auto-shrinks to fit). The strict
        // geometry filter above requires width >= 25% of display, which
        // those small buttons do not satisfy. Pair the small upper centered
        // clickable with a small lower centered clickable (the secondary
        // "Not now"/equivalent CTA) to identify a CMU/Live/PayAttention
        // intro action band even when the labels are localized strings we
        // do not have explicit translations for yet.
        UiObject2 lenient = findIntroPrimaryActionByGeometryLenient();
        if (lenient != null) {
            clickObject(lenient);
            return true;
        }
        return false;
    }

    /**
     * Localized variants of the "Next" CTA on Focus Zone intro cards. The
     * upper centered button advances Live / Catch me up / Pay Attention
     * intro overlays into their agreement card. Compose buttons frequently
     * expose the visible label as the only stable anchor (no resource id),
     * so we keep an exhaustive locale list here. Order matters only if a
     * locale's localized "Next" happens to substring-match another label
     * on a different surface; the entries below are scoped to the bottom
     * action band already, via {@link #findIntroPrimaryActionByGeometry()}
     * for the geometry fallback.
     */
    private static final String[] INTRO_NEXT_LABELS = {
            "Next",
            // German
            "Weiter",
            // French
            "Suivant",
            // Spanish (es-ES, es-US)
            "Siguiente",
            // Italian
            "Avanti",
            // Portuguese (Brazil)
            "Avan\u00e7ar",
            "Pr\u00f3ximo",
            // Polish
            "Dalej",
            "Nast\u0119pny",
            // Romanian
            "\u00cenainte",
            "Continu\u0103",
            // Japanese: 次へ ("Next") and common alternates the CMU/PayAttention
            // intro cards have been observed to use in place of plain "Next":
            // 次, 続ける ("Continue"), 続行 ("Continue"), 進む ("Advance"),
            // 始める / はじめる ("Start"), 開始 ("Start"), 確認 ("Confirm"),
            // 了解 ("Understood"), OK / はい. Some of these intentionally
            // overlap with intro-card primary CTAs in other locales; the
            // geometry fallback already restricts taps to the intro action
            // band, so spurious matches in other surfaces are rare.
            "\u6b21\u3078",
            "\u6b21\u306b",
            "\u6b21",
            "\u7d9a\u3051\u308b",
            "\u7d9a\u884c",
            "\u9032\u3080",
            "\u59cb\u3081\u308b",
            "\u306f\u3058\u3081\u308b",
            "\u958b\u59cb",
            "\u78ba\u8a8d",
            "\u4e86\u89e3",
            "\u306f\u3044",
            // Chinese (Simplified): 下一步 ("Next") and 继续 ("Continue")
            "\u4e0b\u4e00\u6b65",
            "\u7ee7\u7eed",
            "\u5f00\u59cb",
            "\u786e\u8ba4"
    };

    public void tapIAgree() throws Exception {
        long deadline = System.currentTimeMillis() + 12000L;
        while (System.currentTimeMillis() < deadline) {
            if (isFocusZoneUnexpectedErrorVisible() && !isAnyAgreementCardVisible()) {
                FOCUS_ZONE_LOGGER.info("tapIAgree: agreement card replaced by Unexpected Error surface; skipping agreement tap.");
                return;
            }
            if (!isAnyAgreementCardVisible() && isPostAgreementSurfaceVisible()) {
                // The flow already progressed past the agreement card (for
                // example Catch me up jumps straight to summary on some runs).
                return;
            }
            UiObject2 primaryAction = findAgreementPrimaryActionByGeometry();
            if (primaryAction != null) {
                try {
                    clickObject(primaryAction);
                } catch (StaleObjectException stale) {
                    continue;
                }

                long postClick = System.currentTimeMillis() + 3000L;
                while (System.currentTimeMillis() < postClick) {
                    if (isPermissionPromptVisible()) {
                        return;
                    }
                    if (!isAnyAgreementCardVisible()) {
                        return;
                    }
                    mUtils.sleep(200L);
                }
            }

            UiObject2 agree = waitForExactTextOrDescriptionNoAuto(1200L,
                    AGREEMENT_PRIMARY_ACTION_LABELS);
            if (agree == null) {
                // Fallback when the CTA is rendered with extra copy around the
                // core label (for example "I agree to continue").
                agree = findByTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS);
            }
            if (agree != null) {
                try {
                    UiObject2 clickable = findClickableAncestor(agree);
                    clickObject(clickable != null ? clickable : agree);
                } catch (StaleObjectException stale) {
                    // Agreement node recycled between lookup and click; retry.
                    continue;
                }

                long postClick = System.currentTimeMillis() + 3000L;
                while (System.currentTimeMillis() < postClick) {
                    if (isPermissionPromptVisible()) {
                        return;
                    }
                    if (!isAnyAgreementCardVisible()) {
                        return;
                    }
                    mUtils.sleep(200L);
                }
            }
            if (isPermissionPromptVisible()) {
                return;
            }
            if (isFocusZoneUnexpectedErrorVisible() && !isAnyAgreementCardVisible()) {
                FOCUS_ZONE_LOGGER.info("tapIAgree: agreement card no longer visible; Unexpected Error is on screen.");
                return;
            }
            mUtils.sleep(250L);
        }
        if (isFocusZoneUnexpectedErrorVisible() && !isAnyAgreementCardVisible()) {
            FOCUS_ZONE_LOGGER.info("tapIAgree timeout: Unexpected Error visible and no agreement card; not failing flow.");
            return;
        }
        if (!isAnyAgreementCardVisible() && isPostAgreementSurfaceVisible()) {
            return;
        }
        // If the agreement card is no longer visible at all (regardless of
        // whether we recognise the post-agreement surface), the click may
        // have already taken effect or the card was self-dismissed by Qira.
        // Log and continue rather than aborting the entire sub-flow - the
        // master test prefers to capture downstream screenshots than fail
        // hard on a transient agreement-card detection miss.
        if (!isAnyAgreementCardVisible()) {
            FOCUS_ZONE_LOGGER.info(
                    "tapIAgree timeout: agreement card no longer visible; "
                            + "assuming agreement was already dismissed and continuing.");
            return;
        }
        logVisibleLabels("tapIAgree timeout");
        // Last-ditch: try a coarse pixel tap on the lower-center band (where
        // every locale's "I agree" sits regardless of geometry quirks). This
        // covers builds where the localized button is wider/narrower than
        // our normal band but still occupies the bottom half of the
        // agreement sheet.
        try {
            int width = mDevice.getDisplayWidth();
            int height = mDevice.getDisplayHeight();
            mDevice.executeShellCommand(String.format(java.util.Locale.US,
                    "input tap %d %d", width / 2, (height * 78) / 100));
            settle();
            mUtils.sleep(800L);
            if (!isAnyAgreementCardVisible()) {
                FOCUS_ZONE_LOGGER.info(
                        "tapIAgree last-ditch coarse tap dismissed the agreement card.");
                return;
            }
        } catch (Throwable ignored) {
        }
        FOCUS_ZONE_LOGGER.info(
                "tapIAgree: unable to dismiss the agreement card after retries; "
                        + "logging and continuing without throwing.");
    }

    /**
     * Handles Android runtime permission dialogs that appear after tapping I
     * agree on Live/Catch me up/Pay Attention. Accepts up to {@code max}
     * prompts, preferring Allow/foreground answers.
     */
    public int acceptPermissionPrompts(int max, long perPromptTimeoutMs) throws Exception {
        int accepted = 0;
        for (int i = 0; i < max; i++) {
            if (!waitForPermissionPrompt(perPromptTimeoutMs)) {
                break;
            }
            if (acceptCurrentPermissionPrompt()) {
                accepted++;
                settle();
            } else {
                break;
            }
        }
        return accepted;
    }

    private boolean waitForPermissionPrompt(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isPermissionPromptVisible()) {
                return true;
            }
            if (isLiveEnablePermissionPromptVisible()) {
                return false;
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private boolean isPermissionPromptVisible() {
        String pkg = mDevice.getCurrentPackageName();
        if ("com.android.permissioncontroller".equals(pkg)
                || "com.google.android.permissioncontroller".equals(pkg)) {
            return true;
        }
        return findByResource(
                NOTIFICATION_ALLOW_RES,
                PERMISSION_ALLOW_FG_RES,
                PERMISSION_ALLOW_ONE_TIME_RES) != null;
    }

    private UiObject2 findAgreementPrimaryActionByGeometry() {
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
                // Agreement CTA rows are wide chips in the lower-middle band.
                // We pick the TOP-most wide row ("I agree"), not the lower
                // "Back" row that uses the same shape. Locales with longer
                // localized labels (e.g. de-DE "Ich stimme zu") can render
                // the row a few percent higher or wider, so allow a slightly
                // broader band than the strict en-XM ranges.
                if (bounds.top < (height * 64) / 100
                        || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 25) / 100
                        || bounds.width() > (width * 80) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 25) / 100
                        || bounds.centerX() > (width * 75) / 100) {
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

    private boolean isAnyAgreementCardVisible() {
        return hasTextOrDescription(LIVE_AGREEMENT_LABELS)
                || hasTextOrDescription(CATCH_ME_UP_AGREEMENT_LABELS)
                || hasTextOrDescription(PAY_ATTENTION_AGREEMENT_LABELS);
    }

    private boolean isPostAgreementSurfaceVisible() {
        if (isPermissionPromptVisible()) {
            return true;
        }
        if (isLiveActive()) {
            return true;
        }
        if (isChatComposerVisibleByStructure()) {
            return true;
        }
        if (arePayAttentionTabsVisible() || isByProceedingDialogVisible()) {
            return true;
        }
        if (hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
            return true;
        }
        return hasCatchMeUpSummaryControls();
    }

    private boolean hasCatchMeUpSummaryControls() {
        return mDevice.findObject(By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel("Open CMU settings"))) != null
                || mDevice.findObject(By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel("Share"))) != null
                || mDevice.findObject(By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel("Play"))) != null;
    }

    private boolean isCatchMeUpManageAppsVisible() {
        return countBottomCenteredActionsByGeometry(10) >= 1
                && !isCatchMeUpProcessing()
                && !hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS);
    }

    private int countBottomCenteredActionsByGeometry(int minWidthPercent) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int count = 0;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 68) / 100 || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * minWidthPercent) / 100
                        || bounds.width() > (width * 72) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private boolean isFocusZoneUnexpectedErrorVisible() {
        return hasTextOrDescription(UNEXPECTED_ERROR_LABELS);
    }

    private boolean acceptCurrentPermissionPrompt() throws Exception {
        if (clickByResource(
                PERMISSION_ALLOW_FG_RES,
                NOTIFICATION_ALLOW_RES,
                PERMISSION_ALLOW_ONE_TIME_RES)) {
            return true;
        }
        return clickByExactTextOrDescriptionNoAuto(
                "While using the app",
                "Allow only while using the app",
                "Only this time",
                "Allow",
                "Start now");
    }

    public boolean isLiveActive() {
        // In Live mode the bubble bar exposes a Camera icon next to the Live
        // pill; on other Focus Zone surfaces the Camera icon is not present.
        return findBubbleIconByDesc(BUBBLE_CAMERA) != null
                && findBubbleIconByAnyAlias(BUBBLE_LIVE) != null;
    }

    public void tapLiveCamera() throws Exception {
        if (!clickBubbleDescription(BUBBLE_CAMERA)) {
            throw new IllegalStateException("Unable to tap the Camera bubble next to Live");
        }
        settle();
    }

    public void exitLive() throws Exception {
        // Tapping Live a second time toggles Live off.
        tapLiveBubble();
    }

    // ---------------------------------------------------------------------
    // Catch me up flow
    // ---------------------------------------------------------------------

    public void tapCatchMeUpBubble() throws Exception {
        if (!clickBubbleDescription(BUBBLE_CATCH_ME_UP)
                && !clickBubbleBySlot(3, 2)) {
            throw new IllegalStateException("Unable to tap the Catch me up bubble");
        }
        settle();
    }

    public boolean isCatchMeUpIntroVisible() {
        if (hasTextOrDescription(CATCH_ME_UP_INTRO_LABELS)) {
            return true;
        }
        return hasIntroActionPairByGeometry()
                && findBubbleIconByAnyAlias(BUBBLE_CATCH_ME_UP) != null;
    }

    /**
     * Polls until the Catch me up intro card has rendered (or timeout).
     * Used by callers that just tapped the Catch me up bubble — gives the
     * intro a chance to animate in before they decide whether to retry,
     * which avoids the visible "tap-bubble → close-intro → re-tap-bubble"
     * jitter that otherwise occurs on slower locales.
     */
    public boolean waitForCatchMeUpIntro(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isCatchMeUpIntroVisible()) {
                settle();
                return true;
            }
            // Already past the intro? Treat that as success too — the user
            // ended up on the agreement / processing surface directly.
            if (isCatchMeUpAgreementVisible()
                    || isCatchMeUpProcessing()
                    || hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
                return true;
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    public boolean isCatchMeUpAgreementVisible() {
        return hasTextOrDescription(CATCH_ME_UP_AGREEMENT_LABELS)
                && (findByExactTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS) != null
                || findAgreementPrimaryActionByGeometry() != null);
    }

    public boolean isCatchMeUpProcessing() {
        return hasTextOrDescription(CATCH_ME_UP_PROCESSING_LABELS);
    }

    public String currentCatchMeUpProcessingStage() {
        for (String label : CATCH_ME_UP_PROCESSING_LABELS) {
            if (hasTextOrDescription(label)) {
                return label;
            }
        }
        return null;
    }

    public boolean waitForCatchMeUpSummary(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isCatchMeUpProcessing() && hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
                settle();
                return true;
            }
            mUtils.sleep(500L);
        }
        return false;
    }

    public boolean waitForCatchMeUpManageApps(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isCatchMeUpManageAppsVisible()) {
                settle();
                return true;
            }
            if (isCatchMeUpProcessing() || hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
                return false;
            }
            mUtils.sleep(250L);
        }
        return isCatchMeUpManageAppsVisible();
    }

    /**
     * Non-throwing tap on the Catch me up "Now" / primary CTA. Returns
     * {@code true} when a tap was issued, {@code false} when no candidate
     * label/geometry was matched. The previous throwing variant
     * ({@link #tapCatchMeUpNow()}) is preserved as a thin wrapper for
     * back-compat callers.
     */
    public boolean tapCatchMeUpNowIfPresent() throws Exception {
        if (clickByExactTextOrDescription(CATCH_ME_UP_NOW_LABELS)) {
            return true;
        }
        if (clickByTextOrDescription(CATCH_ME_UP_NOW_LABELS)) {
            return true;
        }
        UiObject2 action = findByProceedingPrimaryActionByGeometry();
        if (action == null) {
            action = findAgreementPrimaryActionByGeometry();
        }
        if (action == null) {
            // Locale-agnostic fallback: any clickable in the bottom action
            // band (78-88% top), width 38-92% of display, centerX 25-75%.
            // This is the layout fingerprint of the wide primary CTA on
            // the Catch me up "Manage apps" / agreement-equivalent screens
            // even when the localized label is missing from our list.
            action = findCatchMeUpManageAppsPrimaryActionByGeometry();
        }
        if (action == null) {
            return false;
        }
        try {
            clickObject(action);
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    public boolean enableCatchMeUpAllOtherAppsToggleIfPresent() throws Exception {
        UiObject2 toggle = findCatchMeUpAllOtherAppsToggle();
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
                mUtils.sleep(300L);
            }
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findCatchMeUpAllOtherAppsToggle() {
        UiObject2 label = findByExactTextOrDescription("All other apps");
        if (label == null) {
            label = findByTextOrDescription("All other apps");
        }
        if (label != null) {
            UiObject2 toggle = findToggleInAncestor(label);
            if (toggle != null) {
                return toggle;
            }
        }
        return findCatchMeUpAllOtherAppsToggleByGeometry();
    }

    private UiObject2 findToggleInAncestor(UiObject2 anchor) {
        UiObject2 current = anchor;
        for (int depth = 0; current != null && depth < 6; depth++) {
            try {
                UiObject2 best = null;
                boolean rtl = isRtlLayout();
                int bestX = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                for (UiObject2 toggle : current.findObjects(By.checkable(true))) {
                    Rect bounds = toggle.getVisibleBounds();
                    if (bounds == null || bounds.isEmpty()) {
                        continue;
                    }
                    if ((!rtl && bounds.centerX() > bestX)
                            || (rtl && bounds.centerX() < bestX)) {
                        bestX = bounds.centerX();
                        best = toggle;
                    }
                }
                if (best != null) {
                    return best;
                }
                current = current.getParent();
            } catch (StaleObjectException stale) {
                return null;
            }
        }
        return null;
    }

    private UiObject2 findCatchMeUpAllOtherAppsToggleByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 34) / 100
                        || bounds.bottom > (height * 66) / 100) {
                    continue;
                }
                if (!isInLogicalEndBand(bounds.centerX(), width, 62, 94)) {
                    continue;
                }
                if (bounds.top > bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    public void tapCatchMeUpNow() throws Exception {
        if (!tapCatchMeUpNowIfPresent()) {
            throw new IllegalStateException("Unable to tap the Catch me up primary action");
        }
    }

    /**
     * Locale-agnostic fingerprint of the Catch me up "Manage apps" / wide
     * primary-CTA surface: a clickable in the lower action band whose
     * footprint matches the visible "Catch me up now" / "Catch up now"
     * button. Used as a final fallback by
     * {@link #tapCatchMeUpNowIfPresent()} so locales we have not yet
     * mapped a label for still advance.
     */
    private UiObject2 findCatchMeUpManageAppsPrimaryActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!object.isEnabled()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 78) / 100
                        || bounds.bottom > (height * 92) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 38) / 100
                        || bounds.width() > (width * 92) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 25) / 100
                        || bounds.centerX() > (width * 75) / 100) {
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

    /**
     * Result enum for {@link #waitForCatchMeUpAfterAgreement(long)}. Lets
     * the capture script branch on whichever post-Agreement surface
     * actually rendered without re-running the full visibility probe set
     * for each branch.
     */
    public enum CatchMeUpAfterAgreementSurface {
        MANAGE_APPS,
        PROCESSING,
        SUMMARY,
        OTHER
    }

    /**
     * After the agreement card is dismissed (tapIAgree), polls for
     * whichever surface comes next. Returns as soon as one of
     * MANAGE_APPS / PROCESSING / SUMMARY is observed; falls through to
     * OTHER on timeout. Polls at 250ms with an early-exit on the first
     * observed surface.
     */
    /**
     * Convenience wrapper used by capture scripts to check whether the
     * Catch me up summary surface has rendered. Equivalent to
     * {@code hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)} but
     * exposed as a public API so the script does not have to reference
     * the private label array.
     */
    public boolean isCatchMeUpSummaryReady() {
        return hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS);
    }

    public CatchMeUpAfterAgreementSurface waitForCatchMeUpAfterAgreement(long timeoutMs)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
                    return CatchMeUpAfterAgreementSurface.SUMMARY;
                }
                if (isCatchMeUpProcessing()) {
                    return CatchMeUpAfterAgreementSurface.PROCESSING;
                }
                if (isCatchMeUpManageAppsVisible()) {
                    return CatchMeUpAfterAgreementSurface.MANAGE_APPS;
                }
            } catch (Throwable ignored) {
                // Stale Compose nodes can throw transiently; retry next tick.
            }
            mUtils.sleep(250L);
        }
        try {
            if (hasTextOrDescription(CATCH_ME_UP_SUMMARY_LABELS)) {
                return CatchMeUpAfterAgreementSurface.SUMMARY;
            }
            if (isCatchMeUpProcessing()) {
                return CatchMeUpAfterAgreementSurface.PROCESSING;
            }
            if (isCatchMeUpManageAppsVisible()) {
                return CatchMeUpAfterAgreementSurface.MANAGE_APPS;
            }
        } catch (Throwable ignored) {
        }
        return CatchMeUpAfterAgreementSurface.OTHER;
    }

    private static final String[] CATCH_ME_UP_NOW_LABELS = {
            "Now",
            "Continue",
            "Start now",
            "Get caught up",
            "Get Caught up",
            "Catch me up now",
            "Catch up now",
            // German
            "Jetzt", "Fortfahren", "Jetzt starten", "Catch me up jetzt",
            "Mich jetzt informieren", "Mich auf den neuesten Stand bringen",
            // French
            "Maintenant", "Continuer", "Commencer maintenant",
            "Me mettre au courant maintenant", "Faites le point maintenant",
            // Spanish
            "Ahora", "Continuar", "Comenzar ahora", "Empezar ahora",
            "Ponerme al d\u00eda ahora", "Ponme al d\u00eda ahora",
            // Italian
            "Ora", "Continua", "Inizia ora",
            "Mettimi in pari ora", "Aggiornami ora",
            // Portuguese (Brazil)
            "Agora", "Continuar", "Come\u00e7ar agora",
            "Me atualize agora", "Atualize-me agora",
            // Polish
            "Teraz", "Kontynuuj", "Rozpocznij teraz",
            "Nadrabiaj teraz", "Nadr\u00f3b teraz",
            // Romanian
            "Acum", "Continu\u0103", "\u00cencepe acum",
            "Pune-m\u0103 la curent acum", "Pune-m\u0103 la curent",
            // Japanese: 今すぐ / 続行 / 今すぐ始める / 今すぐキャッチアップ
            "\u4eca\u3059\u3050",
            "\u7d9a\u884c",
            "\u4eca\u3059\u3050\u59cb\u3081\u308b",
            "\u4eca\u3059\u3050\u30ad\u30e3\u30c3\u30c1\u30a2\u30c3\u30d7",
            // Chinese (Simplified): 现在 / 继续 / 立即开始 / 立即追赶
            "\u73b0\u5728",
            "\u7ee7\u7eed",
            "\u7acb\u5373\u5f00\u59cb",
            "\u7acb\u5373\u8ffd\u8d76"
    };

    // ---------------------------------------------------------------------
    // Pay Attention flow
    // ---------------------------------------------------------------------

    public void tapRecordBubble() throws Exception {
        if (clickBubbleDescription(BUBBLE_RECORD)) {
            settle();
            return;
        }
        if (clickBubbleDescription(BUBBLE_PAY_ATTENTION_ACTIVE)) {
            settle();
            return;
        }
        if (clickBubbleDescription(BUBBLE_EXPAND_PAY_ATTENTION)) {
            settle();
            return;
        }
        UiObject2 rightMostBubble = findRightMostFeatureBubbleByGeometry();
        if (rightMostBubble != null) {
            clickObject(rightMostBubble);
            settle();
            return;
        }
        throw new IllegalStateException("Unable to tap the Record / Pay Attention bubble");
    }

    public boolean isPayAttentionIntroVisible() {
        if (hasTextOrDescription(PAY_ATTENTION_INTRO_LABELS)) {
            return true;
        }
        return hasIntroActionPairByGeometry()
                && findBubbleIconByAnyAlias(BUBBLE_RECORD) != null;
    }

    /**
     * Polls the Pay Attention intro card for up to {@code timeoutMs}.
     * Returns true as soon as either the intro card itself is visible OR
     * we observe one of the down-stream surfaces (agreement, by-proceeding
     * dialog, recording, summary) - in which case the intro was either
     * skipped (e.g. account state already accepted by-proceeding on a
     * prior run, taking us straight past the intro) or already animated
     * past faster than our poll cadence. Polling at 200ms keeps the early
     * exit cheap while bounding the worst case.
     *
     * <p>Returning true on a "downstream surface" sentinel is required so
     * the capture script can take its {@code PayAttention_Onboarding}
     * screenshot against the best-available state instead of stalling on
     * an intro that is never going to render.
     */
    public boolean waitForPayAttentionIntro(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (isPayAttentionIntroVisible()) {
                    return true;
                }
                // Downstream surfaces count as "intro already passed".
                if (isPayAttentionAgreementVisible()
                        || isByProceedingDialogVisible()
                        || arePayAttentionTabsVisible()
                        || hasTextOrDescription(PAY_ATTENTION_TAB_LABELS)) {
                    return true;
                }
            } catch (Throwable ignored) {
                // Stale Compose nodes can throw transiently during a
                // cross-fade; retry next tick.
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private boolean hasIntroActionPairByGeometry() {
        if (findIntroPrimaryActionByGeometry() != null
                && findIntroSecondaryActionByGeometry() != null) {
            return true;
        }
        // Lenient fallback for locales where the intro CTAs render as small
        // text-only buttons that fall outside the strict 25-65% width band.
        return findIntroPrimaryActionByGeometryLenient() != null;
    }

    private UiObject2 findIntroPrimaryActionByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!object.isEnabled()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 72) / 100 || bounds.bottom > (height * 83) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 25) / 100 || bounds.width() > (width * 94) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100 || bounds.centerX() > (width * 65) / 100) {
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

    private UiObject2 findIntroSecondaryActionByGeometry() {
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
                if (bounds.top < (height * 80) / 100 || bounds.bottom > (height * 90) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 25) / 100 || bounds.width() > (width * 94) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100 || bounds.centerX() > (width * 65) / 100) {
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

    /**
     * Lenient variant of {@link #findIntroPrimaryActionByGeometry()} that
     * matches small text-only primary CTAs (e.g. ja-JP intro cards where
     * the localized "Next" string is short enough that the button auto-
     * shrinks below the standard 25% min width). Requires a corresponding
     * small secondary CTA below it, so we still only fire on intro/dialog
     * surfaces with a primary+secondary action pair.
     */
    private UiObject2 findIntroPrimaryActionByGeometryLenient() {
        UiObject2 primary = findIntroPrimaryActionByGeometryLenient(
                /* topPct= */ 72, /* bottomPct= */ 83);
        if (primary == null) {
            return null;
        }
        UiObject2 secondary = findIntroPrimaryActionByGeometryLenient(
                /* topPct= */ 80, /* bottomPct= */ 92);
        if (secondary == null) {
            return null;
        }
        try {
            Rect primaryBounds = primary.getVisibleBounds();
            Rect secondaryBounds = secondary.getVisibleBounds();
            if (primaryBounds == null || secondaryBounds == null) {
                return null;
            }
            if (secondaryBounds.top <= primaryBounds.bottom) {
                return null;
            }
            return primary;
        } catch (StaleObjectException ignored) {
            return null;
        }
    }

    private UiObject2 findIntroPrimaryActionByGeometryLenient(int topPct, int bottomPct) {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                if (!object.isEnabled()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * topPct) / 100
                        || bounds.bottom > (height * bottomPct) / 100) {
                    continue;
                }
                // Reject elements that span ~the full screen (root containers,
                // page overlays). The intro CTA is a button-shaped element.
                if (bounds.width() > (width * 94) / 100) {
                    continue;
                }
                if (bounds.height() > (height * 12) / 100) {
                    continue;
                }
                // Require horizontal centering so we do not mis-tap a side
                // chrome element.
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

    public boolean isPayAttentionAgreementVisible() {
        return hasTextOrDescription(PAY_ATTENTION_AGREEMENT_LABELS)
                && (findByExactTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS) != null
                || findAgreementPrimaryActionByGeometry() != null);
    }

    public boolean isByProceedingDialogVisible() {
        return hasTextOrDescription(BY_PROCEEDING_LABELS);
    }

    public boolean waitForByProceedingDialog(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isByProceedingDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public void tapByProceedingAccept() throws Exception {
        if (clickByExactTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS)) {
            return;
        }
        UiObject2 primary = findByProceedingPrimaryActionByGeometry();
        if (primary != null) {
            clickObject(primary);
            return;
        }
        throw new IllegalStateException("Unable to tap Accept on the By proceeding dialog");
    }

    public boolean acceptByProceedingIfPresent(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isByProceedingDialogVisible()
                    || findByExactTextOrDescription(AGREEMENT_PRIMARY_ACTION_LABELS) != null
                    || findByProceedingPrimaryActionByGeometry() != null) {
                tapByProceedingAccept();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    private UiObject2 findByProceedingPrimaryActionByGeometry() {
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
                if (bounds.top < (height * 76) / 100 || bounds.bottom > (height * 86) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 24) / 100 || bounds.width() > (width * 72) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 30) / 100 || bounds.centerX() > (width * 70) / 100) {
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

    public boolean isPayAttentionProcessing() {
        return hasTextOrDescription(PAY_ATTENTION_PROCESSING_LABELS);
    }

    public String currentPayAttentionProcessingStage() {
        for (String label : PAY_ATTENTION_PROCESSING_LABELS) {
            if (hasTextOrDescription(label)) {
                return label;
            }
        }
        return null;
    }

    public boolean arePayAttentionTabsVisible() {
        int tabs = 0;
        for (String label : PAY_ATTENTION_TAB_LABELS) {
            if (hasTextOrDescription(label)) {
                tabs++;
            }
        }
        return tabs >= 2;
    }

    public boolean waitForPayAttentionTabs(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (arePayAttentionTabsVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(300L);
        }
        return false;
    }

    public boolean waitForPayAttentionRecordingActive(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (findBubbleIconByAnyAlias(BUBBLE_RECORD) != null
                    || arePayAttentionTabsVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return findBubbleIconByAnyAlias(BUBBLE_RECORD) != null
                || arePayAttentionTabsVisible();
    }

    /**
     * Legacy single-locale tap. Kept for backward compatibility with
     * any direct caller; new capture scripts should use the
     * verified variant {@link #selectPayAttentionTabAndVerify(String, long)}
     * which guarantees the tap actually changed the active tab.
     */
    public boolean selectPayAttentionTab(String tab) throws Exception {
        return selectPayAttentionTabAndVerify(tab, 0L);
    }

    /**
     * Locale-robust Pay Attention tab tap with verification.
     *
     * <p>Resolution chain:
     * <ol>
     *   <li>Map the English tab name ("Summary" / "Transcript" /
     *       "Audio Recording") to the matching multi-locale label list
     *       in {@link #PAY_ATTENTION_TAB_LABELS_SUMMARY} et al. and
     *       try desc-then-text exact match for every label in the
     *       list. Most locales hit on the first localized label.</li>
     *   <li>If no label matched, locate the three tab pills by
     *       geometry (rounded, clickable Compose Views in a horizontal
     *       strip near the top of the Pay Attention card) and tap the
     *       pill at index 0/1/2 by tab order. Tab order is fixed
     *       across every locale.</li>
     *   <li>After the tap, wait up to {@code verifyTimeoutMs} for one
     *       of the matching {@link #PAY_ATTENTION_BODY_HEADLINES}
     *       entries to be visible on screen. The Pay Attention card
     *       always renders a "Here is the &lt;tab&gt;" headline under
     *       the tab strip when a tab is selected; using that as the
     *       success signal sidesteps the fact that Compose tab pills
     *       do NOT expose AccessibilityNodeInfo.selected on this
     *       build.</li>
     * </ol>
     *
     * <p>Returns true iff the tab was tapped AND the body headline
     * matching the requested tab was observed within the verification
     * window. {@code verifyTimeoutMs == 0} skips verification (legacy
     * behavior) and returns true as long as a tap fired.
     */
    public boolean selectPayAttentionTabAndVerify(String englishTab, long verifyTimeoutMs)
            throws Exception {
        int tabIndex = englishTabIndex(englishTab);
        if (tabIndex < 0) {
            return false;
        }
        String[] labels = PAY_ATTENTION_TAB_LABELS_FOR_INDEX[tabIndex];

        // Rule-set #1: desc before text per label - Compose builds expose
        // the localized label as either text or desc depending on the
        // accessibility wiring. The first hit wins.
        UiObject2 target = null;
        for (String label : labels) {
            target = findByExactDescription(label);
            if (target != null) {
                break;
            }
        }
        if (target == null) {
            for (String label : labels) {
                target = findByExactText(label);
                if (target != null) {
                    break;
                }
            }
        }

        boolean tapped = false;
        if (target != null) {
            UiObject2 clickable = findClickableAncestor(target);
            clickObject(clickable != null ? clickable : target);
            tapped = true;
        } else {
            // Geometry fallback: tap the Nth pill from the left of the
            // Pay Attention tab strip. Pill order is the same in every
            // locale, so this works even when our label dictionary is
            // missing the locale-specific translation.
            UiObject2 pill = findPayAttentionTabPillByIndex(tabIndex);
            if (pill != null) {
                try {
                    clickObject(pill);
                    tapped = true;
                } catch (Throwable ignored) {
                }
            }
        }
        if (!tapped) {
            return false;
        }

        if (verifyTimeoutMs <= 0L) {
            return true;
        }

        // Verify the body headline matches the tab we asked for. This
        // is the only reliable "tab is now active" signal on Compose.
        String[] expectedHeadlines = PAY_ATTENTION_BODY_HEADLINES[tabIndex];
        long deadline = System.currentTimeMillis() + verifyTimeoutMs;
        while (System.currentTimeMillis() < deadline) {
            for (String headline : expectedHeadlines) {
                if (findByExactText(headline) != null
                        || findByExactDescription(headline) != null) {
                    settle();
                    return true;
                }
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    /**
     * Returns 0/1/2 for "Summary"/"Transcript"/"Audio Recording" (any
     * case) and -1 for any other input. The English name is the API
     * the capture scripts use because the script source has to read
     * naturally to a non-localized maintainer; the per-tab label
     * dictionaries above expand the English name to every locale at
     * lookup time.
     */
    private static int englishTabIndex(String englishTab) {
        if (englishTab == null) {
            return -1;
        }
        String normalized = englishTab.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "summary":
                return 0;
            case "transcript":
                return 1;
            case "audio recording":
            case "audiorecording":
                return 2;
            default:
                return -1;
        }
    }

    private static final String[][] PAY_ATTENTION_TAB_LABELS_FOR_INDEX = {
            PAY_ATTENTION_TAB_LABELS_SUMMARY,
            PAY_ATTENTION_TAB_LABELS_TRANSCRIPT,
            PAY_ATTENTION_TAB_LABELS_AUDIO_RECORDING
    };

    /**
     * Locates the Nth Pay Attention tab pill from the left. The pill
     * strip is a {@code HorizontalScrollView} near the top of the Pay
     * Attention card; each pill is a clickable Compose View whose
     * direct parent is the scroll view. The geometry filter accepts
     * any clickable View whose bounds:
     *
     * <ul>
     *   <li>start in the top quarter of the display (the tab strip
     *       sits above the body card),</li>
     *   <li>are wider than 80px and shorter than 200px (the pills are
     *       horizontally laid out and roughly equal-sized),</li>
     *   <li>and whose package matches Qira (filters out launcher /
     *       SystemUI clickables behind the half-transparent card).</li>
     * </ul>
     *
     * <p>Pills are then sorted by left-edge X coordinate and the Nth
     * pill is returned. Index 0 = Summary, 1 = Transcript, 2 = Audio
     * Recording. Returns null when fewer than {@code index + 1}
     * candidate pills are visible.
     */
    private UiObject2 findPayAttentionTabPillByIndex(int index) {
        java.util.List<UiObject2> pills = new java.util.ArrayList<>();
        int displayHeight = mDevice.getDisplayHeight();
        for (UiObject2 obj : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top > displayHeight / 4) {
                    continue;
                }
                int width = bounds.width();
                int height = bounds.height();
                if (width < 80 || width > 600 || height < 50 || height > 200) {
                    continue;
                }
                pills.add(obj);
            } catch (StaleObjectException ignored) {
            }
        }
        if (pills.size() < index + 1) {
            return null;
        }
        java.util.Collections.sort(pills, new java.util.Comparator<UiObject2>() {
            @Override
            public int compare(UiObject2 a, UiObject2 b) {
                try {
                    return a.getVisibleBounds().left - b.getVisibleBounds().left;
                } catch (StaleObjectException stale) {
                    return 0;
                }
            }
        });
        int physicalIndex = isRtlLayout() ? pills.size() - 1 - index : index;
        return pills.get(physicalIndex);
    }

    private UiObject2 findRightMostFeatureBubbleByGeometry() {
        UiObject2 best = null;
        boolean rtl = isRtlLayout();
        int bestX = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 85) / 100 || bounds.bottom > (height * 98) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 3) / 100 || bounds.width() > (width * 12) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 10) / 100) {
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

    private boolean isRtlLayout() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isAtLogicalEnd(int centerX, int width, int ltrMinPct) {
        return isRtlLayout()
                ? centerX <= (width * (100 - ltrMinPct)) / 100
                : centerX >= (width * ltrMinPct) / 100;
    }

    private boolean isInLogicalEndBand(
            int centerX, int width, int ltrMinPct, int ltrMaxPct) {
        if (isRtlLayout()) {
            return centerX >= (width * (100 - ltrMaxPct)) / 100
                    && centerX <= (width * (100 - ltrMinPct)) / 100;
        }
        return centerX >= (width * ltrMinPct) / 100
                && centerX <= (width * ltrMaxPct) / 100;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private UiObject2 findClickableAncestor(UiObject2 object) {
        UiObject2 current = object;
        for (int depth = 0; current != null && depth < 6; depth++) {
            try {
                if (current.isClickable()) {
                    return current;
                }
                current = current.getParent();
            } catch (StaleObjectException stale) {
                return null;
            }
        }
        return null;
    }

    private void logVisibleLabels(String marker) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[diag ").append(marker).append("] visible text/desc: ");
            List<UiObject2> all = mDevice.findObjects(By.pkg(mConfig.getPackageName()));
            int count = 0;
            for (UiObject2 obj : all) {
                if (count >= 30) {
                    break;
                }
                String t = obj.getText();
                String d = obj.getContentDescription();
                if ((t != null && !t.isEmpty()) || (d != null && !d.isEmpty())) {
                    Rect bounds = obj.getVisibleBounds();
                    sb.append("{t=").append(t).append(",d=").append(d)
                            .append(",b=").append(bounds).append("} ");
                    count++;
                }
            }
            FOCUS_ZONE_LOGGER.info(sb.toString());
        } catch (Throwable ignored) {
        }
    }
}
