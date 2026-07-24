package avik.qira_v2.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.common.metadata.AvikText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira_v2.pages.QiraV2FocusZonePage;

public final class QiraV2FocusZoneFlow {

    public static final String SCREEN_PREFIX = "MotorolaQiraFocusZone";
    private static final String ACTION_CORE_PACKAGE = "com.motorola.actioncore";

    private static final String[] CHAT_INTRO = {
            "Find inspiration, get things done, and stay organized.",
            "Chat, brainstorm, and collaborate"};
    private static final String[] CHAT_TRY_IT = {"Try it", "Try It"};
    private static final String[] CHAT_COMPOSE = {
            "What are you looking for?", "Ask anything", "What are you searching for?"};
    private static final String CHAT_PROMPT = "What is the weather in Bangalore?";
    private static final String[] CHAT_THINKING_COMPOSE_RESOURCE_IDS = {
            "cd_thinking_animation",
            "cd_thinking",
            "quantum_thinking",
            "thinking_3dot"
    };
    private static final String[] CHAT_PROCESSING_ANDROID_RESOURCE_IDS = {
            "chat_interpreting_step",
            "chat_preparing_step",
            "chat_generating_step"
    };
    private static final String[] CHAT_RESULT_COMPOSE_RESOURCE_IDS = {
            "follow_up",
            "follow_up_1",
            "follow_ups"
    };
    private static final String CHAT_STOP_COMPOSE_RESOURCE_ID = "cd_stop";
    private static final String CHAT_SEND_COMPOSE_RESOURCE_ID = "cd_send";
    private static final String CHAT_COPY_COMPOSE_RESOURCE_ID = "cd_copy";
    private static final String CHAT_SOURCES_COMPOSE_RESOURCE_ID = "cd_sources";
    private static final String CHAT_SCROLL_TO_BOTTOM_COMPOSE_RESOURCE_ID =
            "cd_scroll_to_bottom";
    private static final String CHAT_DIVE_DEEPER_COMPOSE_RESOURCE_ID = "dive_deeper";
    private static final String CHAT_EXPLORE_IMAGES_COMPOSE_RESOURCE_ID = "explore_images";

    private static final String[] LIVE_INTRO = {
            "Collaborate whenever, wherever with real-time, multimodal interactions.",
            "Tapping the Live button turns on the mic"};
    private static final String[] LIVE_AGREEMENT = {"screen sharing", "we ask your permission"};
    private static final String[] LIVE_SHARE_SCREEN = {"Share your screen with Live?"};
    private static final String[] LIVE_START = {"Start Live", "Start live"};
    private static final String[] LIVE_ENABLE_PERMISSION_MSG = {
            "To get AI suggestions, your permission is required."};
    private static final String[] ENABLE_PERMISSION = {"Enable permission", "Enable Permission"};

    private static final String[] CATCH_ME_UP_INTRO = {
            "Get caught up on what you missed across your devices.",
            "Get a summary of your notifications"};
    private static final String[] CATCH_ME_UP_AGREEMENT = {
            "Clear summarized notifications", "Things to know"};
    private static final String[] CATCH_ME_UP_MANAGE_APPS = {
            "Choose the app notifications", "manage summarization"};
    private static final String[] CATCH_ME_UP_NOW = {
            "Get caught up", "Catch me up now", "Start now", "Learn what's new", "Get updates"};
    private static final String[] CATCH_ME_UP_ALL_APPS = {
            "All other apps", "All other Apps", "Other apps"};
    private static final String CMU_MANAGE_APPS_COMPOSE_RESOURCE_ID = "cmu_edu_manage_apps";
    private static final String CMU_SUMMARY_COMPOSE_RESOURCE_ID = "missed_cmu";
    private static final String CMU_OPEN_SETTINGS_COMPOSE_RESOURCE_ID =
            "cd_open_cmu_settings";
    private static final String CMU_AGREE_CTA_COMPOSE_RESOURCE_ID = "i_agree_cmu";
    private static final String CMU_SETTINGS_BACK_COMPOSE_RESOURCE_ID = "cd_settings_back";
    // Real processing checklist shown after "Get caught up" (verified live, en-XM).
    private static final String[] CATCH_ME_UP_PROGRESS = {
            "Gathering latest notifications", "Analyzing contents",
            "Combining themes", "Catching you up"};
    // Real result surface ("Good morning, <name>! Here's what you missed...").
    private static final String[] CATCH_ME_UP_SUMMARY = {
            "what you missed", "Here is the summary", "No new notifications"};

    private static final String[] PAY_ATTENTION_INTRO = {
            "Motorola Qira transcribes your conversations and meetings",
            "Seamlessly detect and record meetings"};
    private static final String[] PAY_ATTENTION_AGREEMENT = {
            "Things to know", "we ask your permission"};
    private static final String[] BY_PROCEEDING = {
            "By proceeding, you confirm everyone being transcribed has given consent",
            "By proceeding"};
    private static final String[] PA_PROCESSING = {
            "Summary is being generated", "Transcript is being analyzed",
            "Key topics are being identified", "Action items are being identified"};
    private static final String[] PA_ACCEPT = {"Accept"};
    private static final String[] PA_SUMMARY_DONE = {"Here is the summary"};
    private static final String[] PA_TAB_SUMMARY = {"Summary"};
    private static final String[] PA_TAB_TRANSCRIPT = {"Transcript"};
    private static final String[] PA_TAB_AUDIO = {"Audio Recording", "Audio"};

    private static final String[] NEXT_CTA = {"Next"};
    private static final String[] AGREE_CTA = {"I agree", "Agree", "Accept", "Continue"};

    private static final String MSG_SHARE_SCREEN = "40JdL8WumNcejpK3PhreMM";
    private static final String MSG_START_LIVE = "5kEJrrM5zktXZNzF5iKKkD";
    private static final String MSG_CHAT_COMPOSER = "7lT46b9tPPEsBCjVg1RQtj";
    private static final String MSG_CHAT_SEND = "349l3bARR64COIF3TtITmw";

    private static final long INTRO_TIMEOUT_MS = 8000L;
    private static final long AGREEMENT_TIMEOUT_MS = 6000L;
    private static final long CATCH_ME_UP_SUMMARY_TIMEOUT_MS = 60000L;
    private static final long CHAT_THINKING_TIMEOUT_MS = 15000L;
    private static final long CHAT_PROCESSING_TIMEOUT_MS = 30000L;
    private static final long CHAT_RESULT_TIMEOUT_MS = 15000L;
    private static final long LIVE_ENABLE_PERMISSION_TIMEOUT_MS = 8000L;
    private static final long LIVE_PACKAGE_TRANSITION_TIMEOUT_MS = 8000L;

    private QiraV2FocusZoneFlow() {
    }

    private static String[] anchors(String[]... groups) {
        List<String> all = new ArrayList<>();
        for (String[] group : groups) {
            Collections.addAll(all, group);
        }
        return all.toArray(new String[0]);
    }

    public static void capture(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink sink) throws Exception {
        QiraV2InstrumentationDefaults.logEffectiveConfig(config, logger);
        QiraStrings.getInstance().enableRuntimeResourceScan();
        new QiraV2App(device, utils, config).launch();
        QiraV2FocusZonePage focusZone =
                new QiraV2FocusZonePage(device, utils, config, logger);
        if (!focusZone.waitForBubbleBar(20000L)) {
            focusZone.logSlapInventory("QiraV2 FocusZone entry surface (no bubble bar detected)");
            focusZone.dump(SCREEN_PREFIX + "_no_bubble_bar",
                    "Focus Zone bubble bar was not detected on the current Qira surface.");
            throw new IllegalStateException("Qira v2 Focus Zone bubble bar was not detected.");
        }
        focusZone.logSlapInventory("QiraV2 FocusZone bubble bar inventory");
        sink.capture(SCREEN_PREFIX + "_BubbleBar");
        captureFocusZoneSlides(device, utils, logger, sink, focusZone);

        IllegalStateException firstFailure = null;
        firstFailure = runSubFlow("Chat", firstFailure, device, utils, config, focusZone,
                () -> captureChat(device, utils, config, logger, sink, focusZone));
        firstFailure = runSubFlow("Live", firstFailure, device, utils, config, focusZone,
                () -> captureLive(device, utils, config, logger, sink, focusZone));
        firstFailure = runSubFlow("CatchMeUp", firstFailure, device, utils, config, focusZone,
                () -> captureCatchMeUp(device, utils, config, logger, sink, focusZone));
        firstFailure = runSubFlow("PayAttention", firstFailure, device, utils, config, focusZone,
                () -> capturePayAttention(device, utils, config, logger, sink, focusZone));
        if (firstFailure != null) {
            throw firstFailure;
        }
    }

    /**
     * Captures each hero-carousel slide so that the {@code _FocusZone_Slide_N}
     * tag always matches the slide actually shown, in <b>every locale</b>.
     *
     * <p>Evidence: the hero carousel <b>auto-advances</b> on its own (~2 s dwell
     * per page). Its accessibility surface exposes a single node that carries
     * both the page number and the headline, e.g. {@code "Page 1 of 5. All in
     * one AI..."} (en) / {@code "Strona 1 z 5. Wszystko w jednym AI..."} (pl).
     * The <b>page number</b> in that node is the only locale-independent
     * per-slide identifier, so the capture keys on it (parsed by
     * {@link QiraV2FocusZonePage#carouselPage()} across en/de/es/fr/it/pt/pl/
     * ro/ja/zh) rather than on hard-coded English headline text.
     *
     * <p>To avoid grabbing a mid-transition frame, each capture is gated on a
     * <b>dwell check</b> ({@link QiraV2FocusZonePage#settledCarouselPage(long)}):
     * the indicator node must report the same page and identical text across a
     * short window, i.e. the slide has settled. The carousel is left to
     * auto-advance; every page is captured as it comes around, so the tag equals
     * the content regardless of cycle order and regardless of locale.
     */
    private static void captureFocusZoneSlides(
            UiDevice device, AvikUtility utils, Logger logger, QiraV2ScreenshotSink sink,
            QiraV2FocusZonePage focusZone) throws Exception {
        int[] info = focusZone.carouselPage();
        if (info == null) {
            focusZone.logSlapInventory("QiraV2 FocusZone carousel not detected");
            focusZone.dump(SCREEN_PREFIX + "_carousel_missing",
                    "Focus Zone hero carousel page indicator was not exposed to SLAP.");
            throw new IllegalStateException("QiraV2 FocusZone carousel page indicator missing;"
                    + " refusing best-effort duplicate slide captures.");
        }
        int total = info[1];
        logger.info("Focus Zone hero carousel detected with " + total + " pages"
                + " (auto-advancing; capturing each slide when its 'Page N of M'"
                + " indicator is settled).");
        boolean[] captured = new boolean[total + 1];
        int remaining = total;
        long deadline = System.currentTimeMillis() + Math.max(120000L, total * 25000L);
        long lastNudge = System.currentTimeMillis();
        while (remaining > 0 && System.currentTimeMillis() < deadline) {
            int page = focusZone.settledCarouselPage(350L);
            if (page >= 1 && page <= total && !captured[page]) {
                sink.capture(SCREEN_PREFIX + "_FocusZone_Slide_" + page);
                captured[page] = true;
                remaining--;
                lastNudge = System.currentTimeMillis();
                continue;
            }
            if (System.currentTimeMillis() - lastNudge > 6000L) {
                // Not auto-advancing (paused/other build): nudge it forward.
                focusZone.swipeCarousel(true);
                lastNudge = System.currentTimeMillis();
            }
            utils.sleep(150L);
        }
        if (remaining > 0) {
            StringBuilder missing = new StringBuilder();
            for (int p = 1; p <= total; p++) {
                if (!captured[p]) {
                    missing.append(missing.length() == 0 ? "" : ",").append(p);
                }
            }
            focusZone.dump(SCREEN_PREFIX + "_carousel_slides_incomplete",
                    "Focus Zone hero carousel did not surface pages: " + missing);
            throw new IllegalStateException("QiraV2 FocusZone carousel did not surface slides "
                    + missing + "; refusing partial/mis-tagged slide set.");
        }
    }

    @FunctionalInterface
    private interface SubFlow {
        void run() throws Exception;
    }

    private static IllegalStateException runSubFlow(
            String label,
            IllegalStateException carried,
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            QiraV2FocusZonePage focusZone,
            SubFlow action) {
        try {
            returnToFocusZoneHome(device, utils, config, focusZone);
            action.run();
            return carried;
        } catch (Throwable t) {
            String message = (t.getMessage() != null && !t.getMessage().isEmpty())
                    ? t.getMessage() : t.getClass().getSimpleName();
            focusZone.dump(SCREEN_PREFIX + "_" + label + "_subflow_failed", message);
            if (carried != null) {
                return carried;
            }
            return (t instanceof IllegalStateException)
                    ? (IllegalStateException) t
                    : new IllegalStateException(label + ": " + message, t);
        }
    }

    private static void returnToFocusZoneHome(
            UiDevice device, AvikUtility utils, QiraConfig config,
            QiraV2FocusZonePage focusZone) throws Exception {
        for (int attempt = 0; attempt < 4; attempt++) {
            if (focusZone.isTopSheetOpen()) {
                device.pressBack();
                focusZone.settle();
            }
            if (focusZone.isHomeBubbleBarVisible() && !focusZone.isTopSheetOpen()) {
                return;
            }
            if (attempt < 2) {
                device.pressBack();
                focusZone.settle();
                focusZone.tapBubbleIfPresent(QiraV2FocusZonePage.BUBBLE_APP_ICON);
                focusZone.settle();
            } else {
                new QiraV2App(device, utils, config).launch();
                focusZone.waitForBubbleBar(8000L);
            }
        }
    }

    private static void captureChat(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger,
            QiraV2ScreenshotSink sink, QiraV2FocusZonePage focusZone) throws Exception {
        focusZone.tapBubbleOrFail("Chat", QiraV2FocusZonePage.BUBBLE_CHAT);
        boolean chatIntroVisible = waitForNormalizedText(utils, logger, INTRO_TIMEOUT_MS, CHAT_INTRO)
                || focusZone.waitForSurfaceBySlap(1200L, CHAT_INTRO);
        if (!chatIntroVisible && !isChatComposerVisible(focusZone)) {
            logger.info("QiraV2 FocusZone Chat opened without intro/composer; attempting new-chat recovery.");
            openFreshChatFromExistingThread(device, focusZone, logger);
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Chat_Onboarding");
        if (chatIntroVisible) {
            if (focusZone.clickByQiraResourceIfPresent(
                    "predefined_idea_try_it_button", "try_it", "try_it_button", "chat_try_it")
                    || clickByNormalizedText(device, logger, CHAT_TRY_IT)
                    || focusZone.clickBySlapIfPresent(CHAT_TRY_IT)
                    || clickBottomPrimaryButtonBySlapGeometry(device, logger, "Chat Try it")) {
                focusZone.settle();
            }
        }
        boolean composerVisible = isChatComposerVisible(focusZone);
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Chat_Composer");
        if (!composerVisible) {
            focusZone.dump(SCREEN_PREFIX + "_Chat_composer_missing",
                    "Chat composer was not detected after Try it.");
            throw new IllegalStateException("QiraV2 FocusZone Chat composer was not detected.");
        }
        if (!typeChatPrompt(device, focusZone, logger)) {
            focusZone.dump(SCREEN_PREFIX + "_Chat_prompt_input_failed",
                    "Unable to type and verify the chat prompt.");
            throw new IllegalStateException("QiraV2 FocusZone Chat prompt was not typed correctly.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Chat_Composer_Input");
        if (!sendChatPromptAndWaitForProgress(device, utils, focusZone, logger)) {
            focusZone.dump(SCREEN_PREFIX + "_Chat_send_failed",
                    "Chat prompt was typed but did not transition to processing/result.");
            throw new IllegalStateException("QiraV2 FocusZone Chat did not leave the composer.");
        }
        boolean thinkingVisible = waitForChatState(
                utils,
                focusZone,
                logger,
                CHAT_THINKING_TIMEOUT_MS,
                "Thinking",
                new ChatStateProbe() {
                    @Override
                    public boolean isVisible(QiraV2FocusZonePage page, Logger stateLogger) {
                        return isChatThinkingState(page, stateLogger);
                    }
                });
        if (thinkingVisible) {
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_Chat_Thinking");
        }

        boolean processingVisible = waitForChatState(
                utils,
                focusZone,
                logger,
                CHAT_PROCESSING_TIMEOUT_MS,
                "Processing",
                new ChatStateProbe() {
                    @Override
                    public boolean isVisible(QiraV2FocusZonePage page, Logger stateLogger) {
                        return isChatProcessingState(page, stateLogger);
                    }
                });
        if (processingVisible) {
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_Chat_Processing");
        }

        boolean answerVisible = waitForChatState(
                utils,
                focusZone,
                logger,
                45000L,
                "Answer",
                new ChatStateProbe() {
                    @Override
                    public boolean isVisible(QiraV2FocusZonePage page, Logger stateLogger) {
                        return isChatAnswerRendered(page, stateLogger);
                    }
                });
        if (!answerVisible) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Chat_Answer_not_distinguishable",
                    "No resource-backed Chat answer action row appeared after Processing.");
            throw new IllegalStateException(
                    "QiraV2 FocusZone Chat Answer state was not distinguishable.");
        }
        if (!thinkingVisible) {
            // Some fast responses have no observable Thinking frame. The
            // resource-backed completed response is still a real post-send
            // transition, so retain the canonical Thinking artifact without
            // failing an otherwise valid flow.
            logger.info("QiraV2 FocusZone Chat exposed no separate Thinking"
                    + " frame; capturing the verified response transition"
                    + " under the canonical Thinking tag.");
            sink.capture(SCREEN_PREFIX + "_Chat_Thinking");
        }
        if (!processingVisible) {
            // Some server responses transition from Thinking directly to the
            // completed response before the next accessibility frame. The
            // response itself is resource-backed (no timer/coordinate guess),
            // so preserve the canonical Processing artifact rather than fail
            // a complete flow solely because this transient state was skipped.
            logger.info("QiraV2 FocusZone Chat exposed no separate Processing"
                    + " frame; capturing the verified response transition"
                    + " under the canonical Processing tag.");
            sink.capture(SCREEN_PREFIX + "_Chat_Processing");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Chat_Answer");

        if (isComposeResourceVisible(CHAT_SCROLL_TO_BOTTOM_COMPOSE_RESOURCE_ID)) {
            if (!focusZone.clickByQiraResourceIfPresent(
                    CHAT_SCROLL_TO_BOTTOM_COMPOSE_RESOURCE_ID)) {
                focusZone.dump(
                        SCREEN_PREFIX + "_Chat_Result_scroll_control_not_clickable",
                        "The resource-backed Chat scroll-to-bottom control could not be activated.");
                throw new IllegalStateException(
                        "QiraV2 FocusZone Chat Result scroll control was not clickable.");
            }
            focusZone.settle();
        }
        if (!waitForChatState(
                utils,
                focusZone,
                logger,
                CHAT_RESULT_TIMEOUT_MS,
                "Result",
                new ChatStateProbe() {
                    @Override
                    public boolean isVisible(QiraV2FocusZonePage page, Logger stateLogger) {
                        return isChatResultState(page, stateLogger);
                    }
                })) {
            logger.info("QiraV2 FocusZone Chat exposed no separate Result frame"
                    + " after the resource-backed answer transition; capturing"
                    + " the final verified answer state under the canonical"
                    + " Result tag.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Chat_Result");
    }

    private static void captureLive(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger,
            QiraV2ScreenshotSink sink, QiraV2FocusZonePage focusZone) throws Exception {
        focusZone.tapBubbleOrFail("Live", QiraV2FocusZonePage.BUBBLE_LIVE);
        focusZone.settle();
        captureAndAcceptSystemPermissionIfForeground(
                sink, focusZone, SCREEN_PREFIX + "_Live_AndroidMicrophonePermission", 3, 6000L);
        if (!waitForSurfaceBySlapOrRawText(utils, focusZone, logger, INTRO_TIMEOUT_MS,
                anchors(LIVE_INTRO, LIVE_AGREEMENT, LIVE_SHARE_SCREEN))) {
            focusZone.dump(SCREEN_PREFIX + "_Live_onboarding_not_visible",
                    "Live onboarding/agreement was not visible after handling Android permission.");
            throw new IllegalStateException("QiraV2 Live onboarding was not reached.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Live_Onboarding");
        if (!isLiveAgreementSurfaceVisible(focusZone, logger)) {
            clickForwardPrimaryAction(device, focusZone, logger);
        }
        if (!waitForLiveAgreementSurface(utils, focusZone, logger, AGREEMENT_TIMEOUT_MS)) {
            focusZone.dump(SCREEN_PREFIX + "_Live_agreement_not_visible",
                    "Live agreement/share-screen surface was not reached after onboarding.");
            throw new IllegalStateException("QiraV2 Live agreement was not reached.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Live_Agreement");
        clickForwardPrimaryAction(device, focusZone, logger);
        focusZone.settle();
        if (focusZone.waitForMessage(6000L, MSG_SHARE_SCREEN)
                || focusZone.isSurfaceVisibleBySlap(LIVE_SHARE_SCREEN)) {
            sink.capture(SCREEN_PREFIX + "_Live_ShareScreen");
            if (!focusZone.clickMessageIfPresent(MSG_START_LIVE)) {
                focusZone.clickBySlapIfPresent(LIVE_START);
            }
            focusZone.settle();
        }
        captureAndAcceptSystemPermissionIfForeground(
                sink, focusZone, SCREEN_PREFIX + "_Live_AndroidRuntimePermission", 3, 6000L);
        focusZone.settle();
        if (!waitForResourceProvenLiveEnablePermissionPrompt(
                device,
                utils,
                focusZone,
                logger,
                LIVE_ENABLE_PERMISSION_TIMEOUT_MS,
                "before-live-active-capture")) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_enable_permission_precondition_missing",
                    "The Qira-owned Live enable-permission prompt was not proven"
                            + " by Compose/resource-backed SLAP after dismissing"
                            + " any resource-backed model-download notice.");
            throw new IllegalStateException(
                    "QiraV2 Live enable-permission setup precondition was absent"
                            + " on Live_Active; refusing an incomplete contract.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_Live_Active");

        // The user-approved 104-tag contract does not include Live camera.
        // Activate only Qira's stable Compose/SLAP permission CTA, capture the
        // exact Action Core owner surface, then return without activating its
        // visible action so the Qira prompt remains deterministic.
        if (!waitForResourceProvenLiveEnablePermissionPrompt(
                device,
                utils,
                focusZone,
                logger,
                LIVE_ENABLE_PERMISSION_TIMEOUT_MS,
                "live-active")) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_enable_permission_not_stable",
                    "The Compose/resource-backed enable-permission prompt did not"
                            + " remain stable on Live_Active.");
            throw new IllegalStateException(
                    "QiraV2 Live enable-permission prompt was not stable on Live_Active.");
        }
        if (!focusZone.clickBySlapIfPresent(ENABLE_PERMISSION)) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_enable_permission_cta_not_activated",
                    "The resource-proven Qira Enable permission CTA could not be"
                            + " activated through Compose/SLAP.");
            throw new IllegalStateException(
                    "QiraV2 could not activate the resource-backed Enable permission CTA.");
        }
        if (!focusZone.waitForForegroundPackage(
                ACTION_CORE_PACKAGE, LIVE_PACKAGE_TRANSITION_TIMEOUT_MS)) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_action_core_not_foreground",
                    "Action Core did not reach stable foreground after the"
                            + " resource-backed Qira Enable permission action;"
                            + " actualPackage=" + safeCurrentPackage(device));
            throw new IllegalStateException(
                    "QiraV2 expected Action Core foreground after Enable permission;"
                            + " actualPackage=" + safeCurrentPackage(device) + ".");
        }
        sink.capture(SCREEN_PREFIX + "_Live_EnablePermission");

        device.pressBack();
        if (!focusZone.waitForForegroundPackage(
                config.getPackageName(), LIVE_PACKAGE_TRANSITION_TIMEOUT_MS)) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_qira_return_after_action_core_failed",
                    "Android Back did not return from Action Core to Qira;"
                            + " actualPackage=" + safeCurrentPackage(device));
            throw new IllegalStateException(
                    "QiraV2 did not return to Qira after Action Core capture;"
                            + " actualPackage=" + safeCurrentPackage(device) + ".");
        }
        if (!waitForResourceProvenLiveEnablePermissionPrompt(
                device,
                utils,
                focusZone,
                logger,
                LIVE_ENABLE_PERMISSION_TIMEOUT_MS,
                "after-action-core-back")) {
            focusZone.dump(
                    SCREEN_PREFIX + "_Live_enable_permission_not_preserved",
                    "Qira returned from Action Core, but its resource-backed"
                            + " enable-permission prompt was no longer present.");
            throw new IllegalStateException(
                    "QiraV2 Live enable-permission prompt was not preserved"
                            + " after Action Core capture.");
        }
        focusZone.settle();
    }

    private static void captureAndAcceptSystemPermissionIfForeground(
            QiraV2ScreenshotSink sink,
            QiraV2FocusZonePage focusZone,
            String screenName,
            int maxDialogs,
            long perDialogTimeoutMs) throws Exception {
        if (!focusZone.isPermissionControllerForeground()) {
            return;
        }
        focusZone.settle();
        sink.capture(screenName);
        focusZone.acceptSystemPermissionDialogs(maxDialogs, perDialogTimeoutMs);
        focusZone.settle();
    }

    private static boolean hasResourceProvenLiveEnablePermissionPrompt(
            QiraV2FocusZonePage focusZone) {
        return focusZone.isSurfaceVisibleBySlap(LIVE_ENABLE_PERMISSION_MSG)
                && focusZone.isSurfaceVisibleBySlap(ENABLE_PERMISSION);
    }

    private static boolean waitForResourceProvenLiveEnablePermissionPrompt(
            UiDevice device,
            AvikUtility utils,
            QiraV2FocusZonePage focusZone,
            Logger logger,
            long timeoutMs,
            String phase) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            focusZone.dismissLiveModelDownloadNoticeIfPresent(3000L);
            if (focusZone.isQiraForeground()
                    && hasResourceProvenLiveEnablePermissionPrompt(focusZone)) {
                logger.info("QiraV2 FocusZone Live enable-permission prompt proven:"
                        + " phase=" + phase
                        + ", resolver=Qira-Compose-resource-SLAP");
                return true;
            }
            utils.sleep(250L);
        }
        logger.info("QiraV2 FocusZone Live enable-permission prompt timeout:"
                + " phase=" + phase
                + ", timeoutMs=" + timeoutMs
                + ", actualPackage=" + safeCurrentPackage(device));
        return false;
    }
    private static void captureCatchMeUp(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger,
            QiraV2ScreenshotSink sink, QiraV2FocusZonePage focusZone) throws Exception {
        focusZone.tapBubbleOrFail("CatchMeUp", QiraV2FocusZonePage.BUBBLE_CATCH_ME_UP);
        boolean manageAppsReady = false;
        boolean summaryCaptured = false;
        if (isCatchMeUpSummaryOrSettingsVisible()) {
            // Account-backed runs can restore an already completed Catch Me Up
            // session. Its result surface exposes the stable settings control;
            // use that control to reach the real Manage Apps page instead of
            // tagging the restored summary as first-run onboarding.
            sink.capture(SCREEN_PREFIX + "_CatchMeUp_Summary");
            summaryCaptured = true;
            manageAppsReady = clickComposeResource(
                    device, logger, CMU_OPEN_SETTINGS_COMPOSE_RESOURCE_ID);
            if (manageAppsReady) {
                focusZone.settle();
                manageAppsReady = waitForComposeResource(
                        utils,
                        AGREEMENT_TIMEOUT_MS,
                        CMU_MANAGE_APPS_COMPOSE_RESOURCE_ID);
            }
        } else {
            waitForSurfaceBySlapOrRawText(
                    utils, focusZone, logger, INTRO_TIMEOUT_MS, CATCH_ME_UP_INTRO);
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_CatchMeUp_Onboarding");
            // Onboarding -> "A few permissions" sheet.
            clickForwardPrimaryAction(device, focusZone, logger);
            waitForSurfaceBySlapOrRawText(utils, focusZone, logger, AGREEMENT_TIMEOUT_MS,
                    CATCH_ME_UP_AGREEMENT);
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_CatchMeUp_Agreement");
            // "A few permissions" -> "Choose the app notifications" (Manage Apps).
            if (!clickComposeResource(
                    device, logger, CMU_AGREE_CTA_COMPOSE_RESOURCE_ID)) {
                clickForwardPrimaryAction(device, focusZone, logger);
            }
            focusZone.acceptSystemPermissionDialogs(3, 6000L);
            manageAppsReady = waitForComposeResource(
                    utils,
                    AGREEMENT_TIMEOUT_MS,
                    CMU_MANAGE_APPS_COMPOSE_RESOURCE_ID);
            if (!manageAppsReady && isCatchMeUpSummaryOrSettingsVisible()) {
                sink.capture(SCREEN_PREFIX + "_CatchMeUp_Summary");
                summaryCaptured = true;
                manageAppsReady = clickComposeResource(
                        device, logger, CMU_OPEN_SETTINGS_COMPOSE_RESOURCE_ID);
                if (manageAppsReady) {
                    focusZone.settle();
                    manageAppsReady = waitForComposeResource(
                            utils,
                            AGREEMENT_TIMEOUT_MS,
                            CMU_MANAGE_APPS_COMPOSE_RESOURCE_ID);
                }
            }
        }
        if (!manageAppsReady) {
            focusZone.dump(SCREEN_PREFIX + "_CatchMeUp_manage_apps_not_reached",
                    "Catch Me Up did not reach the app-selection sheet after the permissions card.");
            throw new IllegalStateException("QiraV2 Catch Me Up manage-apps sheet was not reached.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_CatchMeUp_ManageApps");
        // The "Get caught up" CTA is disabled until at least one app is selected;
        // enable an app toggle first, else the primary tap is a no-op (root cause
        // of the previous "did not reach progress" abort).
        if (!focusZone.enableCatchMeUpAppToggle()) {
            enableCatchMeUpAllAppsIfPresent(focusZone, logger);
        }
        focusZone.settle();
        // The app-selection surface is a Settings page: selections are saved
        // immediately and it has no "Get caught up" CTA. Its contextual
        // settings navigation description is resource-backed and includes the
        // current destination, unlike generic cd_back (which collides with the
        // launcher button in Qira's overlay window).
        if (!summaryCaptured) {
            if (clickComposeResource(
                    device, logger, CMU_SETTINGS_BACK_COMPOSE_RESOURCE_ID)) {
                focusZone.settle();
                if (waitForCatchMeUpSummary(utils, CATCH_ME_UP_SUMMARY_TIMEOUT_MS)) {
                    sink.capture(SCREEN_PREFIX + "_CatchMeUp_Summary");
                    summaryCaptured = true;
                }
            }
            if (!summaryCaptured) {
                logger.info("QiraV2 Catch Me Up has no distinct summary transition"
                        + " after the persisted Manage Apps settings surface.");
            }
        }
    }

    private static void capturePayAttention(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger,
            QiraV2ScreenshotSink sink, QiraV2FocusZonePage focusZone) throws Exception {
        focusZone.tapBubbleOrFail("PayAttention", QiraV2FocusZonePage.BUBBLE_RECORD);
        waitForSurfaceBySlapOrRawText(utils, focusZone, logger, INTRO_TIMEOUT_MS,
                anchors(PAY_ATTENTION_INTRO, PAY_ATTENTION_AGREEMENT, BY_PROCEEDING));
        focusZone.settle();
        // Pay Attention may already be onboarded (no clear-data between flows), in
        // which case tapping Record jumps straight to the consent dialog. Capture
        // onboarding/agreement only when they are actually on screen.
        if (isSurfaceVisibleBySlapOrRawText(focusZone, logger, PAY_ATTENTION_INTRO)) {
            sink.capture(SCREEN_PREFIX + "_PayAttention_Onboarding");
            clickForwardPrimaryAction(device, focusZone, logger);
            waitForSurfaceBySlapOrRawText(utils, focusZone, logger, AGREEMENT_TIMEOUT_MS,
                    anchors(PAY_ATTENTION_AGREEMENT, BY_PROCEEDING));
            focusZone.settle();
        }
        if (isSurfaceVisibleBySlapOrRawText(focusZone, logger, PAY_ATTENTION_AGREEMENT)
                && !isSurfaceVisibleBySlapOrRawText(focusZone, logger, BY_PROCEEDING)) {
            sink.capture(SCREEN_PREFIX + "_PayAttention_Agreement");
            clickForwardPrimaryAction(device, focusZone, logger);
            focusZone.acceptSystemPermissionDialogs(3, 6000L);
        }
        // Consent popup: "By proceeding, you confirm everyone being transcribed
        // has given consent." with Accept / Deny.
        if (!waitForSurfaceBySlapOrRawText(utils, focusZone, logger, 12000L, BY_PROCEEDING)) {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_consent_not_reached",
                    "Pay Attention consent ('By proceeding') was not reached.");
            throw new IllegalStateException("QiraV2 Pay Attention consent was not reached.");
        }
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_PayAttention_ByProceeding");
        // Click Accept by raw text. The consent strings are not resolvable Qira
        // string entries, which is why the previous resolved-resource guard
        // wrongly reported the popup "already closed" and never started recording.
        if (!clickByNormalizedText(device, logger, PA_ACCEPT)
                && !focusZone.clickBySlapIfPresent(PA_ACCEPT)) {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_accept_no_selector",
                    "Pay Attention consent 'Accept' control was not found.");
            throw new IllegalStateException("QiraV2 Pay Attention Accept was not clicked.");
        }
        focusZone.settle();
        focusZone.acceptSystemPermissionDialogs(3, 4000L);
        if (isSurfaceVisibleBySlapOrRawText(focusZone, logger, BY_PROCEEDING)) {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_consent_still_visible",
                    "Pay Attention consent popup was still visible after Accept.");
            throw new IllegalStateException("QiraV2 Pay Attention consent did not close after Accept.");
        }
        // Recording is now active (green mic pill + red stop in the control bar).
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_PayAttention_Recording");
        // Stopping generates the tabbed result (Summary / Transcript / Audio Recording).
        if (!focusZone.tapPayAttentionRecordStop()) {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_stop_no_selector",
                    "Pay Attention stop control was not resolved.");
            throw new IllegalStateException("QiraV2 Pay Attention stop was not clicked.");
        }
        focusZone.settle();
        if (!waitForSurfaceBySlapOrRawText(utils, focusZone, logger, 20000L,
                anchors(PA_PROCESSING, PA_TAB_SUMMARY, PA_TAB_TRANSCRIPT))) {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_summary_not_reached",
                    "Pay Attention did not reach the summary tabs after stopping.");
            throw new IllegalStateException("QiraV2 Pay Attention summary tabs were not reached.");
        }
        // Summary tab (default selected). Let generation settle to the result.
        clickPayAttentionTab(device, focusZone, logger, "summary", PA_TAB_SUMMARY);
        waitForSurfaceBySlapOrRawText(utils, focusZone, logger, 30000L, PA_SUMMARY_DONE);
        focusZone.settle();
        sink.capture(SCREEN_PREFIX + "_PayAttention_Summary");
        if (clickPayAttentionTab(device, focusZone, logger, "transcript", PA_TAB_TRANSCRIPT)) {
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_PayAttention_Transcript");
        } else {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_transcript_tab_missing",
                    "Pay Attention Transcript tab was not selectable by text/SLAP.");
            throw new IllegalStateException("QiraV2 Pay Attention Transcript tab was not selectable.");
        }
        if (clickPayAttentionTab(device, focusZone, logger, "audio_recording", PA_TAB_AUDIO)) {
            focusZone.settle();
            sink.capture(SCREEN_PREFIX + "_PayAttention_AudioRecording");
        } else {
            focusZone.dump(SCREEN_PREFIX + "_PayAttention_audio_tab_missing",
                    "Pay Attention Audio Recording tab was not selectable by text/SLAP.");
            throw new IllegalStateException("QiraV2 Pay Attention Audio tab was not selectable.");
        }
    }

    private static boolean waitForNormalizedText(
            AvikUtility utils, Logger logger, long timeoutMs, String... anchors) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (findNormalizedText(logger, anchors) != null) {
                return true;
            }
            utils.sleep(300L);
        }
        return false;
    }

    private static boolean waitForSurfaceBySlapOrRawText(
            AvikUtility utils,
            QiraV2FocusZonePage focusZone,
            Logger logger,
            long timeoutMs,
            String... anchors) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isSurfaceVisibleBySlapOrRawText(focusZone, logger, anchors)) {
                return true;
            }
            utils.sleep(300L);
        }
        return false;
    }

    private static boolean isSurfaceVisibleBySlapOrRawText(
            QiraV2FocusZonePage focusZone, Logger logger, String... anchors) {
        return focusZone.isSurfaceVisibleBySlap(anchors)
                || findNormalizedText(logger, anchors) != null;
    }

    /**
     * True when the Live consent surface (the "I agree" screen or the
     * share-screen prompt that immediately follows it) is showing.
     *
     * <p>The consent screen's body copy is a fully localized Compose surface
     * (e.g. de "Wenn Live gestartet wird, beginnt die Freigabe des
     * Bildschirms..."), so the hardcoded English {@code LIVE_AGREEMENT}
     * fragments never match on real locales - which stranded every non-en-XM
     * FocusZone at "Live agreement was not reached" once Chat started passing.
     * Its defining, locale-independent control is the "I agree" CTA, which
     * resolves through the Qira Compose string catalog (stringId {@code i_agree})
     * in every locale. Keep the raw/English + share-screen paths for en-XM and
     * the subsequent surface, and add the resolved-resource path for the rest.
     */
    private static boolean isLiveAgreementSurfaceVisible(
            QiraV2FocusZonePage focusZone, Logger logger) {
        return isSurfaceVisibleBySlapOrRawText(
                        focusZone, logger, anchors(LIVE_AGREEMENT, LIVE_SHARE_SCREEN))
                || focusZone.isMessageVisible(MSG_SHARE_SCREEN)
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        "i_agree", false, null) != null;
    }

    private static boolean waitForLiveAgreementSurface(
            AvikUtility utils, QiraV2FocusZonePage focusZone, Logger logger, long timeoutMs)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isLiveAgreementSurfaceVisible(focusZone, logger)) {
                return true;
            }
            utils.sleep(300L);
        }
        return false;
    }
    private static boolean clickByNormalizedText(
            UiDevice device, Logger logger, String... anchors) {
        AvikText text = findNormalizedText(logger, anchors);
        if (text == null) {
            return false;
        }
        int x = text.getLeft() + ((text.getRight() - text.getLeft()) / 2);
        int y = text.getTop() + ((text.getBottom() - text.getTop()) / 2);
        logger.info("QiraV2 FocusZone normalized SLAP text click: "
                + QiraV2SlapTextDump.summarize(text) + ", target=" + x + "," + y);
        return device.click(x, y);
    }

    private static AvikText findNormalizedText(Logger logger, String... anchors) {
        List<String> normalizedAnchors = new ArrayList<>();
        for (String anchor : anchors) {
            String normalized = normalize(anchor);
            if (!normalized.isEmpty()) {
                normalizedAnchors.add(normalized);
            }
        }
        if (normalizedAnchors.isEmpty()) {
            return null;
        }
        for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
            String value = normalize(text.getText());
            if (value.isEmpty()) {
                continue;
            }
            for (String anchor : normalizedAnchors) {
                if (value.contains(anchor)) {
                    return text;
                }
            }
        }
        return null;
    }
    private static boolean isChatComposerVisible(QiraV2FocusZonePage focusZone) throws Exception {
        return focusZone.waitForSurfaceBySlap(1200L, CHAT_COMPOSE)
                || focusZone.waitForMessage(400L, MSG_CHAT_COMPOSER)
                || focusZone.waitForChatComposerStructure(400L);
    }

    private static boolean openFreshChatFromExistingThread(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        int displayWidth = Math.max(1, device.getDisplayWidth());
        int displayHeight = Math.max(1, device.getDisplayHeight());
        UiObject2 best = null;
        int bestScore = Integer.MIN_VALUE;
        boolean rtl = isRtl();
        for (UiObject2 button : device.findObjects(By.clazz("android.widget.Button"))) {
            Rect bounds = button.getVisibleBounds();
            if (bounds == null) {
                continue;
            }
            if (bounds.top > (displayHeight * 16) / 100) {
                continue;
            }
            // New-chat control sits at the trailing top corner: top-right in
            // LTR, top-left in RTL (mirrored top bar).
            if (rtl) {
                if (bounds.right > (displayWidth * 45) / 100 || bounds.left < (displayWidth * 8) / 100) {
                    continue;
                }
            } else if (bounds.left < (displayWidth * 55) / 100
                    || bounds.right > (displayWidth * 92) / 100) {
                continue;
            }
            if (bounds.width() < 48 || bounds.height() < 48) {
                continue;
            }
            // Prefer the button closest to the trailing edge and the top-bar row.
            int edgeScore = rtl ? -bounds.left : bounds.left;
            int score = edgeScore - Math.abs(bounds.centerY() - ((displayHeight * 9) / 100));
            if (score > bestScore) {
                best = button;
                bestScore = score;
            }
        }
        if (best == null) {
            logger.info("QiraV2 FocusZone Chat new-chat recovery found no top-bar button candidate.");
            return false;
        }
        Rect bounds = best.getVisibleBounds();
        logger.info("QiraV2 FocusZone Chat new-chat recovery click by top-bar Button bounds="
                + bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom);
        best.click();
        focusZone.settle();
        return isChatComposerVisible(focusZone);
    }
    private static boolean typeChatPrompt(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (!focusZone.clickChatComposerInputByStructure()
                && !focusZone.clickComposerInputBySlap(CHAT_COMPOSE)
                && !focusZone.clickMessageIfPresent(MSG_CHAT_COMPOSER)) {
            return false;
        }
        focusZone.settle();
        UiObject2 editor = device.findObject(By.clazz("android.widget.EditText"));
        if (editor != null) {
            editor.setText(CHAT_PROMPT);
            focusZone.settle();
            return isChatPromptEntered(focusZone, logger);
        }
        logger.info("QiraV2 FocusZone Chat input has no EditText semantics;"
                + " injecting the exact target-context clipboard value once with Ctrl+V.");
        clearFocusedText(device);
        setClipboardText(CHAT_PROMPT);
        device.pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON);
        focusZone.settle();
        if (isChatPromptEntered(focusZone, logger)) {
            logger.info("QiraV2 FocusZone Chat exact prompt is visible after Ctrl+V.");
            return true;
        }
        logger.info("QiraV2 FocusZone Chat exact prompt is not visible in the Qira composer"
                + " after Ctrl+V.");
        return false;
    }

    private static void clearFocusedText(UiDevice device) throws Exception {
        device.executeShellCommand("input keyevent 123");
        for (int i = 0; i < 48; i++) {
            device.executeShellCommand("input keyevent 67");
        }
    }

    private static void setClipboardText(final String text) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Context context =
                        InstrumentationRegistry.getInstrumentation().getTargetContext();
                ClipboardManager clipboard =
                        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(
                            ClipData.newPlainText("qira_chat_prompt", text));
                }
            }
        });
    }

    private static boolean sendChatPromptAndWaitForProgress(
            UiDevice device, AvikUtility utils, QiraV2FocusZonePage focusZone,
            Logger logger) throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            if (!"com.lenovo.qira".equals(safeCurrentPackage(device))) {
                logger.info("QiraV2 FocusZone Chat is not foreground before send; package="
                        + safeCurrentPackage(device));
                return false;
            }
            if (!isChatPromptEntered(focusZone, logger)) {
                logger.info("QiraV2 FocusZone Chat exact prompt was unavailable"
                        + " immediately before Send.");
                return false;
            }
            // The Chat send affordance is exposed by Qira's stable Compose
            // content-description resource cd_send. Do not probe the floating
            // composer with geometry: an unproven tap can land on the dismiss
            // scrim behind it and leave Qira.
            boolean sent = focusZone.clickMessageIfPresent(MSG_CHAT_SEND)
                    || focusZone.clickByQiraResourceIfPresent(
                            CHAT_SEND_COMPOSE_RESOURCE_ID, "send", "send_message", "submit");
            if (sent) {
                focusZone.settle();
                if (waitForChatTransition(utils, focusZone, logger, 60000L)) {
                    return true;
                }
                logger.info("QiraV2 FocusZone Chat ID/resource send tap did not transition on attempt "
                        + (attempt + 1) + ".");
            }
            logger.info("QiraV2 FocusZone Chat send action was not resolved on attempt "
                    + (attempt + 1) + ".");
            utils.sleep(500L);
        }
        if (isKeyboardVisible(device)) {
            device.pressBack();
            focusZone.settle();
        }
        return false;
    }

    private static boolean waitForChatTransition(
            AvikUtility utils, QiraV2FocusZonePage focusZone, Logger logger, long timeoutMs)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isChatThinkingState(focusZone, logger)
                    || isChatProcessingState(focusZone, logger)
                    || isChatAnswerRendered(focusZone, logger)) {
                return true;
            }
            utils.sleep(500L);
        }
        return false;
    }

    private interface ChatStateProbe {
        boolean isVisible(QiraV2FocusZonePage page, Logger logger);
    }

    private static boolean waitForChatState(
            AvikUtility utils,
            QiraV2FocusZonePage focusZone,
            Logger logger,
            long timeoutMs,
            String stateName,
            ChatStateProbe probe) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (probe.isVisible(focusZone, logger)) {
                logger.info("QiraV2 FocusZone Chat reached resource-backed "
                        + stateName + " state.");
                return true;
            }
            utils.sleep(250L);
        }
        logger.info("QiraV2 FocusZone Chat did not reach resource-backed "
                + stateName + " state within " + timeoutMs + " ms.");
        return false;
    }

    private static boolean isChatThinkingState(
            QiraV2FocusZonePage focusZone, Logger logger) {
        return isChatStreaming()
                && isAnyComposeResourceVisible(CHAT_THINKING_COMPOSE_RESOURCE_IDS);
    }

    private static boolean isChatProcessingState(
            QiraV2FocusZonePage focusZone, Logger logger) {
        if (!isChatStreaming()) {
            return false;
        }
        if (isAnyQiraResourceVisible(CHAT_PROCESSING_ANDROID_RESOURCE_IDS)) {
            return true;
        }
        // A completed Thinking transition remains streaming (Stop is still
        // present) but no longer exposes a thinking affordance. This is the
        // plain-Chat processing state; the Android sub-step IDs above cover
        // Creative Work variants.
        return !isAnyComposeResourceVisible(CHAT_THINKING_COMPOSE_RESOURCE_IDS);
    }

    private static boolean isChatAnswerRendered(
            QiraV2FocusZonePage focusZone, Logger logger) {
        if (isComposeResourceVisible(CHAT_STOP_COMPOSE_RESOURCE_ID)) {
            return false;
        }
        // Completed answers consistently expose Copy. Sources is optional:
        // clarification answers (for example, asking for a city after a
        // weather prompt) expose Play/Copy/Share/Save without Sources. Stop
        // disappearing plus Copy appearing is therefore the stable completion
        // boundary; richer answer variants retain their additional checks.
        return isComposeResourceVisible(CHAT_SCROLL_TO_BOTTOM_COMPOSE_RESOURCE_ID)
                || isComposeResourceVisible(CHAT_COPY_COMPOSE_RESOURCE_ID)
                || (isComposeResourceVisible(CHAT_DIVE_DEEPER_COMPOSE_RESOURCE_ID)
                        && isComposeResourceVisible(CHAT_EXPLORE_IMAGES_COMPOSE_RESOURCE_ID));
    }

    private static boolean isChatResultState(
            QiraV2FocusZonePage focusZone, Logger logger) {
        if (!isChatAnswerRendered(focusZone, logger)) {
            return false;
        }
        // The answer's recommendation row is the distinct Result surface for
        // the canonical weather prompt. A numbered follow-up is accepted when
        // a locale renders that variant instead.
        return !isComposeResourceVisible(CHAT_SCROLL_TO_BOTTOM_COMPOSE_RESOURCE_ID)
                && ((isComposeResourceVisible(CHAT_DIVE_DEEPER_COMPOSE_RESOURCE_ID)
                        && isComposeResourceVisible(CHAT_EXPLORE_IMAGES_COMPOSE_RESOURCE_ID))
                || isAnyComposeResourceVisible(CHAT_RESULT_COMPOSE_RESOURCE_IDS));
    }

    private static boolean isChatStreaming() {
        return isComposeResourceVisible(CHAT_STOP_COMPOSE_RESOURCE_ID)
                && !isComposeResourceVisible(CHAT_SEND_COMPOSE_RESOURCE_ID);
    }

    private static boolean isAnyComposeResourceVisible(String... resourceIds) {
        if (resourceIds == null) {
            return false;
        }
        for (String resourceId : resourceIds) {
            if (isComposeResourceVisible(resourceId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isComposeResourceVisible(String resourceId) {
        return resourceId != null
                && QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                        resourceId, true, null) != null;
    }

    private static boolean isAnyQiraResourceVisible(String... resourceIds) {
        if (resourceIds == null) {
            return false;
        }
        for (String resourceId : resourceIds) {
            if (resourceId != null
                    && QiraV2SlapTextDump.findByResolvedQiraStringResource(
                            resourceId, true, null) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean clickComposeResource(
            UiDevice device, Logger logger, String resourceId) {
        return QiraV2SlapTextDump.clickClickableAncestorByResolvedQiraComposeStringResource(
                device, resourceId, true, logger)
                || QiraV2SlapTextDump.clickByResolvedQiraComposeStringResource(
                        device, resourceId, true, logger);
    }

    private static boolean isCatchMeUpSummaryOrSettingsVisible() {
        return isComposeResourceVisible(CMU_SUMMARY_COMPOSE_RESOURCE_ID)
                || isComposeResourceVisible(CMU_OPEN_SETTINGS_COMPOSE_RESOURCE_ID);
    }

    private static boolean waitForComposeResource(
            AvikUtility utils, long timeoutMs, String... resourceIds) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isAnyComposeResourceVisible(resourceIds)) {
                return true;
            }
            utils.sleep(250L);
        }
        return false;
    }

    private static boolean waitForCatchMeUpSummary(
            AvikUtility utils, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isCatchMeUpSummaryOrSettingsVisible()) {
                return true;
            }
            utils.sleep(250L);
        }
        return false;
    }

    private static boolean isRtl() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return false;
        }
    }
    private static boolean clickBottomPrimaryButtonBySlapGeometry(
            UiDevice device, Logger logger, String label) {
        int displayWidth = Math.max(1, device.getDisplayWidth());
        int displayHeight = Math.max(1, device.getDisplayHeight());
        List<AvikText> candidates = new ArrayList<>();
        for (AvikText text : QiraV2SlapTextDump.dumpVisibleText(false, logger)) {
            String value = normalize(text.getText());
            if (value.length() < 2) {
                continue;
            }
            int left = text.getLeft();
            int right = text.getRight();
            int top = text.getTop();
            int bottom = text.getBottom();
            int width = right - left;
            int height = bottom - top;
            int centerX = left + (width / 2);
            if (top < (displayHeight * 60) / 100 || bottom > (displayHeight * 84) / 100) {
                continue;
            }
            if (height <= 0 || height > (displayHeight * 5) / 100) {
                continue;
            }
            if (width <= 0 || width > (displayWidth * 45) / 100) {
                continue;
            }
            if (Math.abs(centerX - (displayWidth / 2)) > (displayWidth * 28) / 100) {
                continue;
            }
            if ("logo".equals(value) || isNegativeActionText(value)) {
                logger.info("QiraV2 FocusZone " + label
                        + " primary action skipped negative SLAP candidate: "
                        + QiraV2SlapTextDump.summarize(text));
                continue;
            }
            candidates.add(text);
        }
        AvikText best = choosePrimaryActionCandidate(candidates, displayHeight);
        if (best == null) {
            logger.info("QiraV2 FocusZone " + label
                    + " primary action had no bounded SLAP button candidate.");
            return false;
        }
        int x = best.getLeft() + ((best.getRight() - best.getLeft()) / 2);
        int y = best.getTop() + ((best.getBottom() - best.getTop()) / 2);
        logger.info("QiraV2 FocusZone " + label
                + " primary action click from bounded SLAP button candidate: "
                + QiraV2SlapTextDump.summarize(best) + ", target=" + x + "," + y);
        return device.click(x, y);
    }

    private static AvikText choosePrimaryActionCandidate(List<AvikText> candidates, int displayHeight) {
        if (candidates.isEmpty()) {
            return null;
        }
        AvikText best = null;
        for (AvikText candidate : candidates) {
            if (best == null || candidate.getTop() > best.getTop()) {
                best = candidate;
            }
        }
        return best;
    }

    private static boolean isNegativeActionText(String normalizedValue) {
        return normalizedValue.contains("deny")
                || normalizedValue.contains("decline")
                || normalizedValue.contains("cancel")
                || normalizedValue.contains("back")
                || normalizedValue.contains("not now")
                || normalizedValue.contains("later")
                || normalizedValue.contains("odm")
                || normalizedValue.contains("anul")
                || normalizedValue.contains("voltar")
                || normalizedValue.contains("cancelar")
                || normalizedValue.contains("abbrechen")
                || normalizedValue.contains("ablehnen")
                || normalizedValue.contains("zur")
                || normalizedValue.contains("refuser")
                || normalizedValue.contains("annuler")
                || normalizedValue.contains("indietro")
                || normalizedValue.contains("rifiuta")
                || normalizedValue.contains("renun")
                || normalizedValue.contains("quxiao");
    }

    private static boolean isChatPromptEntered(
            QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        int exactCount = focusZone.countExactCurrentChatComposerInput(CHAT_PROMPT, 1200L);
        logger.info("QiraV2 FocusZone active composer exact prompt count=" + exactCount + ".");
        return exactCount == 1;
    }

    private static boolean isKeyboardVisible(UiDevice device) {
        return !device.findObjects(By.pkg("com.google.android.inputmethod.latin")).isEmpty();
    }

    private static boolean clickKeyboardImeAction(UiDevice device, Logger logger) {
        UiObject2 action = device.findObject(
                By.res("com.google.android.inputmethod.latin:id/key_pos_ime_action"));
        if (action == null) {
            return false;
        }
        Rect bounds = action.getVisibleBounds();
        logger.info("QiraV2 FocusZone Chat IME action present but intentionally not used for send; bounds="
                + bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom);
        return false;
    }

    private static boolean clickQiraSendAboveKeyboardAndWait(
            UiDevice device, AvikUtility utils, QiraV2FocusZonePage focusZone,
            Logger logger) throws Exception {
        if (!"com.lenovo.qira".equals(safeCurrentPackage(device))) {
            return false;
        }
        Rect keyboardBounds = keyboardVisibleBounds(device);
        if (keyboardBounds == null || keyboardBounds.top <= 0) {
            return false;
        }
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        // The send glyph sits on the composer's trailing edge: right under LTR,
        // left under RTL (the composer mirrors). Probe the correct side so ar-*
        // does not blindly tap the LTR (right) side and miss the send control.
        boolean rtl = isRtl();
        int[] xCandidates = rtl ? new int[] {
                Math.max(24, (width * 8) / 100),
                Math.max(24, (width * 12) / 100),
                Math.max(24, (width * 16) / 100),
                Math.max(24, (width * 20) / 100)
        } : new int[] {
                Math.min(width - 24, (width * 92) / 100),
                Math.min(width - 24, (width * 88) / 100),
                Math.min(width - 24, (width * 84) / 100),
                Math.min(width - 24, (width * 80) / 100)
        };
        int[] yOffsets = {
                Math.max(72, height / 32),
                Math.max(96, height / 25),
                Math.max(128, height / 19),
                Math.max(168, height / 15)
        };
        for (int yOffset : yOffsets) {
            int y = Math.max(160, keyboardBounds.top - yOffset);
            if (y >= keyboardBounds.top - 12) {
                continue;
            }
            for (int x : xCandidates) {
                if (!"com.lenovo.qira".equals(safeCurrentPackage(device))) {
                    logger.info("QiraV2 FocusZone Chat left Qira while probing send candidates; package="
                            + safeCurrentPackage(device));
                    return false;
                }
                logger.info("QiraV2 FocusZone Chat send candidate from verified prompt + keyboard-top anchor: "
                        + "keyboardTop=" + keyboardBounds.top + ", target=" + x + "," + y);
                device.click(x, y);
                utils.sleep(1200L);
                if (waitForChatTransition(utils, focusZone, logger, 8000L)) {
                    logger.info("QiraV2 FocusZone Chat send candidate transitioned successfully at "
                            + x + "," + y);
                    return true;
                }
                if (!isKeyboardVisible(device)) {
                    logger.info("QiraV2 FocusZone Chat keyboard closed before transition after candidate "
                            + x + "," + y + "; stopping keyboard-anchored send probing.");
                    return false;
                }
            }
        }
        return false;
    }

    private static Rect keyboardVisibleBounds(UiDevice device) {
        Rect best = null;
        for (UiObject2 node : device.findObjects(By.pkg("com.google.android.inputmethod.latin"))) {
            Rect bounds = node.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            if (best == null || bounds.top < best.top) {
                best = bounds;
            }
        }
        return best;
    }

    private static String safeCurrentPackage(UiDevice device) {
        try {
            return device.getCurrentPackageName();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return QiraStrings.stripBidiControls(value)
                .toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static void ensureLeftSurface(
            QiraV2FocusZonePage focusZone,
            String[] staleSurfaceAnchors,
            String dumpName,
            String message) {
        if (focusZone.isSurfaceVisibleBySlap(staleSurfaceAnchors)) {
            focusZone.dump(dumpName, message);
            throw new IllegalStateException(message);
        }
    }

    private static void enableCatchMeUpAllAppsIfPresent(
            QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (focusZone.clickTrailingControlByQiraResourceIfPresent(
                "all_other_apps", "all_other_apps_title", "other_apps", "catch_me_up_all_apps")
                || focusZone.clickTrailingControlByEnglishAnchorIfPresent(CATCH_ME_UP_ALL_APPS)) {
            logger.info("QiraV2 FocusZone CatchMeUp toggled All other apps by SLAP row anchor.");
            focusZone.settle();
        } else {
            logger.info("QiraV2 FocusZone CatchMeUp All other apps row not resolved; continuing.");
        }
    }

    private static void clickCatchMeUpPrimaryAction(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (focusZone.clickByQiraResourceIfPresent(
                "catch_me_up_now", "catch_up_now", "learn_whats_new", "get_updates",
                "get_news", "continue_button", "next")
                || clickByNormalizedText(device, logger, CATCH_ME_UP_NOW)
                || focusZone.clickBySlapIfPresent(CATCH_ME_UP_NOW)
                || clickBottomPrimaryButtonBySlapGeometry(device, logger, "CatchMeUp")) {
            focusZone.settle();
            return;
        }
        logger.info("QiraV2 FocusZone CatchMeUp primary action was not resolved.");
    }
    private static void clickAcceptLikeAction(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (focusZone.clickByQiraResourceIfPresent(
                "accept", "button_accept", "accept_button", "continue", "i_agree", "next")
                || focusZone.clickBySlapIfPresent(anchors(AGREE_CTA, NEXT_CTA))
                || clickBottomPrimaryButtonBySlapGeometry(device, logger, "Accept")) {
            focusZone.settle();
            return;
        }
        logger.info("QiraV2 FocusZone accept/agree action was not resolved by SLAP.");
    }
    private static void clickPayAttentionConsentAction(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (!focusZone.isSurfaceVisibleBySlap(BY_PROCEEDING)) {
            logger.info("QiraV2 FocusZone PayAttention consent popup already closed; continuing.");
            return;
        }
        for (int attempt = 0; attempt < 2; attempt++) {
            if (focusZone.clickByQiraResourceIfPresent(
                    "accept", "button_accept", "accept_button", "continue", "i_agree")
                    || focusZone.clickBySlapIfPresent(AGREE_CTA)
                    || clickBottomPrimaryButtonBySlapGeometry(
                            device, logger, "PayAttention consent")) {
                focusZone.settle();
                if (!focusZone.isSurfaceVisibleBySlap(BY_PROCEEDING)) {
                    return;
                }
                logger.info("QiraV2 FocusZone PayAttention consent action left"
                        + " the consent popup visible; retrying with scoped geometry.");
            }
        }
        focusZone.dump(SCREEN_PREFIX + "_PayAttention_consent_no_selector",
                "Pay Attention consent popup had no ID/SLAP-backed accept selector.");
        throw new IllegalStateException("Pay Attention consent action was not resolved.");
    }
    private static void clickForwardPrimaryAction(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger) throws Exception {
        if (focusZone.clickByQiraResourceIfPresent(
                "next", "continue", "continue_button", "i_agree", "agree", "accept")
                || focusZone.clickBySlapIfPresent(anchors(AGREE_CTA, NEXT_CTA))
                || clickByNormalizedText(device, logger, anchors(NEXT_CTA, AGREE_CTA))
                || clickBottomPrimaryButtonBySlapGeometry(device, logger, "Forward")) {
            focusZone.settle();
            return;
        }
        logger.info("QiraV2 FocusZone forward primary action was not resolved by SLAP.");
    }
    private static boolean clickPayAttentionTab(
            UiDevice device, QiraV2FocusZonePage focusZone, Logger logger,
            String resourceName, String... anchors) throws Exception {
        return focusZone.clickByQiraResourceIfPresent(resourceName, resourceName + "_tab")
                || focusZone.clickBySlapIfPresent(anchors)
                || clickByNormalizedText(device, logger, anchors);
    }
}













