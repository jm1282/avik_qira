package avik.qira_v2.utils;

import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.common.metadata.AvikText;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraOnboardingPage;
import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira.utils.QiraUiDumper;
import avik.qira_v2.pages.QiraV2OnboardingStartPage;

public final class QiraV2HomeOnboardingFlow {

    public static final String SCREEN_PREFIX = "MotorolaQiraHome";
    public static final String SCREEN_START = SCREEN_PREFIX + "_Onboarding_Start";
    public static final String SCREEN_INTRO_ARROW = SCREEN_PREFIX + "_Onboarding_IntroArrow";
    public static final String SCREEN_DEVICE_ASSURANCE =
            SCREEN_PREFIX + "_Onboarding_DeviceAssurance";
    public static final String SCREEN_PRODUCTIVITY_ARROW =
            SCREEN_PREFIX + "_Onboarding_ProductivityArrow";
    public static final String SCREEN_LANGUAGE = SCREEN_PREFIX + "_Onboarding_Language";
    public static final String SCREEN_CONTINUE_AS = SCREEN_PREFIX + "_Onboarding_ContinueAs";
    public static final String SCREEN_ACKNOWLEDGE = SCREEN_PREFIX + "_Onboarding_Acknowledge";
    public static final String SCREEN_ACKNOWLEDGE_SCROLLED =
            SCREEN_PREFIX + "_Onboarding_Acknowledge_Scrolled";
    public static final String SCREEN_PERMISSIONS = SCREEN_PREFIX + "_Onboarding_Permissions";
    public static final String SCREEN_PERMISSIONS_BANNER =
            SCREEN_PREFIX + "_Onboarding_PermissionsBanner";
    public static final String SCREEN_PERMISSIONS_TOGGLE =
            SCREEN_PREFIX + "_Onboarding_PermissionsToggle";
    public static final String SCREEN_PERMISSIONS_TOGGLE_ENABLED =
            SCREEN_PREFIX + "_Onboarding_PermissionsToggleEnabled";
    public static final String SCREEN_PERMISSIONS_SCROLLED =
            SCREEN_PREFIX + "_Onboarding_PermissionsScrolled";
    public static final String SCREEN_CONTEXTUAL_READING_PERMISSION =
            SCREEN_PREFIX + "_Onboarding_ContextualReadingPermission";
    public static final String SCREEN_CONTEXTUAL_READING_PERMISSION_SCROLLED =
            SCREEN_PREFIX + "_Onboarding_ContextualReadingPermissionScrolled";
    public static final String SCREEN_CONTEXTUAL_READING_PERMISSION_ACCEPT =
            SCREEN_PREFIX + "_Onboarding_ContextualReadingPermissionAccept";
    public static final String SCREEN_CONTEXTUAL_READING_ACTION_CORE_ENABLE =
            SCREEN_PREFIX + "_Onboarding_ContextualReadingActionCoreEnable";
    public static final String SCREEN_HOTWORD_SETUP = SCREEN_PREFIX + "_Onboarding_HotwordSetup";
    public static final String SCREEN_EXPLORE_START = SCREEN_PREFIX + "_Onboarding_ExploreStart";
    public static final String SCREEN_ANDROID_LOCATION_PERMISSION =
            SCREEN_PREFIX + "_Onboarding_AndroidLocationPermission";
    public static final String SCREEN_ANDROID_LOCATION_PERMISSION_PRECISE =
            SCREEN_PREFIX + "_Onboarding_AndroidLocationPermissionPrecise";
    public static final String SCREEN_ANDROID_SYSTEM_PERMISSION =
            SCREEN_PREFIX + "_Onboarding_AndroidSystemPermission";
    public static final String SCREEN_HOME = SCREEN_PREFIX + "_Home";

    // Fixed number of scrolled-variant screenshots emitted for the scrollable
    // contextual-reading permission dialog, applied identically to EVERY locale.
    // The dialog's copy length is locale-dependent (verbose / RTL locales scroll
    // further before the Enable CTA appears), so a "scroll until Enable" loop
    // produced a different Scrolled{1..N} count per locale and left en-XM - the
    // string-linking reference - short of the screens other locales produced.
    // A fixed count keeps the en-XM reference complete and maps every locale's
    // Scrolled{n} 1:1. Sized to cover the longest dialog; on short (e.g. en-XM)
    // dialogs the trailing passes simply re-capture the bottom, which is
    // harmless and still linkable.
    private static final int CONTEXTUAL_READING_SCROLL_CAPTURES = 3;

    private static final String[] NEXT_CTA_STRING_ANCHORS = {"Next"};
    private static final String[] CONTINUE_AS_STRING_ANCHORS = {"Continue as"};
    private static final String[] I_ACKNOWLEDGE_STRING_ANCHORS = {"I acknowledge"};
    private static final String[] I_AGREE_STRING_ANCHORS = {"I agree"};
    private static final String[] SKIP_THIS_STEP_STRING_ANCHORS = {"Skip this step"};
    private static final String[] SKIP_FOR_NOW_STRING_ANCHORS = {"Skip for now"};
    private static final String[] START_CTA_STRING_ANCHORS = {"Start"};
    private static final String[] PERMISSION_VIEWPORT_RESOURCE_IDS = {
            "all_permission_title",
            "permission_prompt_title",
            "permission_reading_title",
            "permission_personalization_title",
            "permission_task_title",
            "synchronization_title_card",
            "analytics_tracking_title",
            "permission_notification_title",
            "i_agree"
    };

    private static final long POST_TRANSITION_SETTLE_MS = 2500L;
    private static final long POST_ONBOARDING_HOME_TIMEOUT_MS = 60000L;

    private static final String PERMISSION_CONTROLLER_PACKAGE =
            "com.android.permissioncontroller";
    private static final String PERMISSION_CONTROLLER_GOOGLE_PACKAGE =
            "com.google.android.permissioncontroller";
    private static final String[] PERMISSION_LOCATION_FINE_RESOURCES = {
            "com.android.permissioncontroller:id/permission_location_accuracy_radio_fine",
            "com.google.android.permissioncontroller:id/permission_location_accuracy_radio_fine"
    };
    private static final String[] PERMISSION_LOCATION_COARSE_RESOURCES = {
            "com.android.permissioncontroller:id/permission_location_accuracy_radio_coarse",
            "com.google.android.permissioncontroller:id/permission_location_accuracy_radio_coarse"
    };
    private static final String[] PERMISSION_ALLOW_FOREGROUND_RESOURCES = {
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.google.android.permissioncontroller:id/permission_allow_foreground_only_button"
    };
    private static final String[] PERMISSION_ALLOW_RESOURCES = {
            "com.android.permissioncontroller:id/permission_allow_all_button",
            "com.google.android.permissioncontroller:id/permission_allow_all_button",
            "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.google.android.permissioncontroller:id/permission_allow_foreground_only_button",
            "com.android.permissioncontroller:id/permission_allow_button",
            "com.google.android.permissioncontroller:id/permission_allow_button",
            "com.android.permissioncontroller:id/permission_allow_always_button",
            "com.google.android.permissioncontroller:id/permission_allow_always_button"
    };

    private QiraV2HomeOnboardingFlow() {
    }

    public static void capture(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink) throws Exception {
        CanonicalCaptureGuard captureGuard =
                new CanonicalCaptureGuard(screenshotSink);
        screenshotSink = captureGuard;
        QiraV2InstrumentationDefaults.logEffectiveConfig(config, logger);
        QiraStrings.getInstance().enableRuntimeResourceScan();

        QiraV2App app = new QiraV2App(device, utils, config);
        app.clearDataAndLaunch();

        QiraOnboardingPage legacyOnboarding = new QiraOnboardingPage(device, config);

        captureStart(device, utils, config, logger, screenshotSink);

        if (waitForIntroPill(device, logger, 10000L)
                || legacyOnboarding.waitForIntroBanner(1000L)) {
            waitForTransitionSettle(device, utils);
            captureStep(
                    device,
                    config,
                    logger,
                    screenshotSink,
                    SCREEN_INTRO_ARROW,
                    QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT);
            clickIntroFooterNext(device, config, logger);
            settle(device, utils);
        }

        capturePostIntroMilestones(
                device,
                utils,
                config,
                logger,
                screenshotSink,
                legacyOnboarding,
                captureGuard,
                30000L);

        boolean signInVisible = isAnySignInSurfaceVisible(legacyOnboarding);
        if (signInVisible) {
            captureAndDismissSignInSurface(
                    device,
                    utils,
                    config,
                    logger,
                    screenshotSink,
                    legacyOnboarding);
        } else {
            logger.info("QiraV2 Moto account sign-in dialog was not shown; continuing.");
        }

        boolean acknowledgeVisible =
                isAcknowledgeSurfaceVisibleBeforePermissions(legacyOnboarding);
        if (acknowledgeVisible) {
            waitForTransitionSettle(device, utils);
            captureStep(
                    device,
                    config,
                    logger,
                    screenshotSink,
                    SCREEN_ACKNOWLEDGE,
                    QiraV2SlapCatalog.DISCLAIMER_SCREEN_TITLE,
                    QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA);
            if (legacyOnboarding.scrollAcknowledgeDialogForCapture()) {
                screenshotSink.capture(SCREEN_ACKNOWLEDGE_SCROLLED);
            } else {
                screenshotSink.capture(SCREEN_ACKNOWLEDGE_SCROLLED);
            }
            enableVisibleAcknowledgeToggles(device, utils, logger);
            enableAcknowledgeToggleByResourceAnchor(device, logger);
            clickAcknowledgeOrAgree(device, config, logger);
            settle(device, utils);
            if (legacyOnboarding.isAcknowledgeDialogVisible()) {
                legacyOnboarding.acknowledge();
            }
        }

        dismissAcknowledgeIfStillVisible(
                device,
                utils,
                config,
                logger,
                screenshotSink,
                legacyOnboarding);

        try {
            legacyOnboarding.waitForPermissionBanner(6000L);
        } catch (Throwable t) {
            logger.info("QiraV2 waitForPermissionBanner failed (continuing): " + t.getMessage());
        }
        settle(device, utils);

        boolean permissionPanelVisible = legacyOnboarding.isPermissionPanelVisible()
                || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE);
        if (!permissionPanelVisible
                && !legacyOnboarding.isFeatureGridVisible()
                && !legacyOnboarding.isExploreStartVisible()
                && !legacyOnboarding.isHotwordSetupVisible()
                && !isQiraV2HotwordSetupVisible()
                && !isCatalogEntryVisible(QiraV2SlapCatalog.START_CTA, null)
                && !isAnySignInSurfaceVisible(legacyOnboarding)) {
            try {
                legacyOnboarding.waitForPermissionPanel();
            } catch (IllegalStateException ignored) {
            }
            permissionPanelVisible = legacyOnboarding.isPermissionPanelVisible()
                    || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE);
        }

        if (!permissionPanelVisible
                && isAcknowledgeSurfaceVisibleBeforePermissions(legacyOnboarding)) {
            dismissAcknowledgeIfStillVisible(
                    device,
                    utils,
                    config,
                    logger,
                    screenshotSink,
                    legacyOnboarding);
            try {
                legacyOnboarding.waitForPermissionBanner(6000L);
            } catch (Throwable t) {
                logger.info("QiraV2 waitForPermissionBanner after delayed acknowledge"
                        + " failed (continuing): " + t.getMessage());
            }
            settle(device, utils);
            permissionPanelVisible = legacyOnboarding.isPermissionPanelVisible()
                    || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE);
        }

        if (permissionPanelVisible) {
            handlePermissionPanel(
                    device,
                    utils,
                    logger,
                    screenshotSink,
                    config,
                    legacyOnboarding,
                    new QiraFocusZonePage(device, config));
        }

        if (!legacyOnboarding.isFeatureGridVisible()
                && !legacyOnboarding.isExploreStartVisible()
                && !legacyOnboarding.isHotwordSetupVisible()
                && !isQiraV2HotwordSetupVisible()
                && !isCatalogEntryVisible(QiraV2SlapCatalog.START_CTA, null)) {
            QiraV2SlapTextDump.logVisibleTextSummaries(
                    "QiraV2 post-permission unknown surface",
                    false,
                    logger);
            if (QiraV2SlapTextDump.findByResolvedQiraStringResource(
                    QiraV2SlapCatalog.DISCLAIMER_SCREEN_TITLE.getStringId(),
                    false,
                    logger) != null) {
                logger.info("QiraV2 delayed acknowledgement detected immediately before"
                        + " fallback; dismissing by ID-backed selectors.");
                forceDismissAcknowledge(
                        device,
                        utils,
                        config,
                        logger,
                        screenshotSink,
                        legacyOnboarding);
                try {
                    legacyOnboarding.waitForPermissionBanner(6000L);
                } catch (Throwable t) {
                    logger.info("QiraV2 waitForPermissionBanner after final delayed"
                            + " acknowledge failed (continuing): " + t.getMessage());
                }
                settle(device, utils);
                if (legacyOnboarding.isPermissionPanelVisible()
                        || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)) {
                    handlePermissionPanel(
                            device,
                            utils,
                            logger,
                            screenshotSink,
                            config,
                            legacyOnboarding,
                            new QiraFocusZonePage(device, config));
                }
            }
        }

        if (!legacyOnboarding.isFeatureGridVisible()) {
            advanceOnboardingToHomeBySlap(
                    device,
                    utils,
                    config,
                    logger,
                    screenshotSink,
                    legacyOnboarding,
                    new QiraFocusZonePage(device, config),
                    150000L);
        }
        requireEnXmOnboardingMilestones(
                device, config, captureGuard);

        handlePostOnboardingSystemPermissionsAndCaptureHome(
                device,
                utils,
                config,
                logger,
                screenshotSink,
                legacyOnboarding);
    }

    /**
     * Brings an already-launched Qira instance to the home feature grid without
     * emitting onboarding screenshots. Zone scripts use this only after their
     * legacy entry path cannot read a textless Compose onboarding surface.
     */
    public static void advanceToHomeWithoutCaptures(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            long timeoutMs) throws Exception {
        if (device == null || config == null) {
            throw new IllegalArgumentException(
                    "Qira v2 onboarding recovery requires a device and Qira configuration.");
        }

        QiraStrings.getInstance().enableRuntimeResourceScan();
        QiraOnboardingPage legacyOnboarding = new QiraOnboardingPage(device, config);
        if (legacyOnboarding.isFeatureGridVisible()) {
            return;
        }

        if (logger != null) {
            logger.info("QiraV2 onboarding recovery: advancing the current Qira"
                    + " surface to home without duplicate screenshot capture.");
        }
        QiraV2ScreenshotSink noCapture = new QiraV2ScreenshotSink() {
            @Override
            public void capture(String screenName) {
                // The caller is a zone capture. Its own flow owns artifacts.
            }
        };
        advanceOnboardingToHomeBySlap(
                device,
                utils,
                config,
                logger,
                noCapture,
                legacyOnboarding,
                new QiraFocusZonePage(device, config),
                Math.max(1000L, timeoutMs));
    }

    private static final class DuplicateCanonicalCaptureException
            extends IllegalStateException {
        private DuplicateCanonicalCaptureException(String screenName) {
            super("Duplicate canonical onboarding capture attempted for "
                    + screenName
                    + "; refusing to hide state-machine misclassification.");
        }
    }

    private static final class CanonicalCaptureGuard
            implements QiraV2ScreenshotSink {
        private final QiraV2ScreenshotSink delegate;
        private final Set<String> emitted = new LinkedHashSet<>();

        private CanonicalCaptureGuard(QiraV2ScreenshotSink delegate) {
            this.delegate = delegate;
        }

        @Override
        public void capture(String screenName) throws Exception {
            boolean canonical = screenName != null
                    && (screenName.startsWith(SCREEN_PREFIX + "_Onboarding_")
                    || SCREEN_HOME.equals(screenName));
            if (canonical && !emitted.add(screenName)) {
                throw new DuplicateCanonicalCaptureException(screenName);
            }
            delegate.capture(screenName);
        }

        private boolean wasCaptured(String screenName) {
            return emitted.contains(screenName);
        }
    }

    private enum PostIntroSurface {
        INTRO,
        RESPONSE_LANGUAGE,
        DEVICE_ASSURANCE,
        DOWNSTREAM,
        UNKNOWN_NEXT,
        UNKNOWN
    }

    private static void capturePostIntroMilestones(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage legacyOnboarding,
            CanonicalCaptureGuard captureGuard,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int introTransitionAttempts = 0;
        while (System.currentTimeMillis() < deadline) {
            PostIntroSurface surface = classifyPostIntroSurface(
                    legacyOnboarding, logger);
            switch (surface) {
                case INTRO:
                    if (introTransitionAttempts >= 3) {
                        failPostIntroClassification(
                                device,
                                config,
                                SCREEN_INTRO_ARROW + "_transition_stuck",
                                "Intro identity remained visible after "
                                        + introTransitionAttempts
                                        + " stable Next transition attempts.");
                    }
                    introTransitionAttempts++;
                    logger.info("QiraV2 post-intro classifier: Intro still visible;"
                            + " retrying stable Next transition without capture"
                            + " (attempt=" + introTransitionAttempts + "/3).");
                    clickIntroFooterNext(device, config, logger);
                    waitForCatalogIdentityToDisappear(
                            QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT,
                            utils,
                            6000L);
                    continue;
                case RESPONSE_LANGUAGE:
                    logger.info("QiraV2 post-intro classifier: response-language"
                            + " identity proven by stringId="
                            + QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT.getStringId()
                            + ", messageId="
                            + QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT.getMessageId()
                            + ".");
                    captureStep(
                            device,
                            config,
                            logger,
                            screenshotSink,
                            SCREEN_LANGUAGE,
                            QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT);
                    clickNext(device, config, logger, SCREEN_LANGUAGE);
                    if (!waitForCatalogIdentityToDisappear(
                            QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT,
                            utils,
                            8000L)) {
                        failPostIntroClassification(
                                device,
                                config,
                                SCREEN_LANGUAGE + "_transition_stuck",
                                "Response-language identity remained visible after"
                                        + " stable Next activation.");
                    }
                    continue;
                case DEVICE_ASSURANCE:
                    logger.info("QiraV2 post-intro classifier: Device Assurance"
                            + " identity proven by stringId="
                            + QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT
                            .getStringId()
                            + ", messageId="
                            + QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT
                            .getMessageId()
                            + ".");
                    captureStep(
                            device,
                            config,
                            logger,
                            screenshotSink,
                            SCREEN_DEVICE_ASSURANCE,
                            QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT);
                    clickNext(device, config, logger, SCREEN_DEVICE_ASSURANCE);
                    if (!waitForCatalogIdentityToDisappear(
                            QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT,
                            utils,
                            8000L)) {
                        failPostIntroClassification(
                                device,
                                config,
                                SCREEN_DEVICE_ASSURANCE + "_transition_stuck",
                                "Device Assurance identity remained visible after"
                                        + " stable Next activation.");
                    }
                    continue;
                case DOWNSTREAM:
                    requirePostIntroMilestonesBeforeDownstream(
                            device, config, captureGuard);
                    return;
                case UNKNOWN_NEXT:
                    QiraV2SlapTextDump.logVisibleTextSummaries(
                            "QiraV2 unknown Next-only post-intro surface",
                            false,
                            logger);
                    failPostIntroClassification(
                            device,
                            config,
                            SCREEN_PREFIX + "_Onboarding_unknown_next_surface",
                            "A Next-gated onboarding surface had no Intro,"
                                    + " response-language, Device Assurance, or"
                                    + " known downstream catalog identity.");
                    break;
                case UNKNOWN:
                default:
                    sleep(utils, 250L);
                    break;
            }
        }
        failPostIntroClassification(
                device,
                config,
                SCREEN_PREFIX + "_Onboarding_post_intro_classification_timeout",
                "No authoritative post-intro catalog identity appeared within "
                        + timeoutMs + " ms.");
    }

    private static PostIntroSurface classifyPostIntroSurface(
            QiraOnboardingPage legacyOnboarding,
            Logger logger) {
        if (isOwnedCatalogIdentityVisible(
                QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT, logger)) {
            return PostIntroSurface.INTRO;
        }
        if (isOwnedCatalogIdentityVisible(
                QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT, logger)) {
            return PostIntroSurface.RESPONSE_LANGUAGE;
        }
        if (isOwnedCatalogIdentityVisible(
                QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT, logger)) {
            return PostIntroSurface.DEVICE_ASSURANCE;
        }
        if (isKnownDownstreamOnboardingSurface(legacyOnboarding)) {
            return PostIntroSurface.DOWNSTREAM;
        }
        if (isSlapMessageVisible(QiraV2SlapCatalog.NEXT_CTA)) {
            return PostIntroSurface.UNKNOWN_NEXT;
        }
        return PostIntroSurface.UNKNOWN;
    }

    private static boolean isKnownDownstreamOnboardingSurface(
            QiraOnboardingPage legacyOnboarding) {
        return isAnySignInSurfaceVisible(legacyOnboarding)
                || isAcknowledgeSurfaceVisibleBeforePermissions(legacyOnboarding)
                || isCatalogEntryVisible(
                        QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE, null)
                || isCatalogEntryVisible(
                        QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE, null)
                || legacyOnboarding.isPermissionPanelVisible()
                || legacyOnboarding.isHotwordSetupVisible()
                || isQiraV2HotwordSetupVisible()
                || legacyOnboarding.isExploreStartVisible()
                || isCatalogEntryVisible(QiraV2SlapCatalog.START_CTA, null)
                || legacyOnboarding.isFeatureGridVisible();
    }

    private static boolean isOwnedCatalogIdentityVisible(
            QiraV2SlapCatalog.SlapString entry,
            Logger logger) {
        if (entry == null) {
            return false;
        }
        AvikText resolved =
                QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                        entry.getStringId(), false, logger);
        if (resolved == null) {
            return false;
        }
        String expected = QiraStrings.stripBidiControls(resolved.getText());
        if (expected == null || expected.isEmpty()) {
            return false;
        }
        for (AvikText ownerText
                : QiraV2SlapTextDump.dumpVisibleAccessibilityTextForPackage(
                        QiraStrings.DEFAULT_QIRA_PACKAGE, null)) {
            if (ownerText == null
                    || !expected.equals(
                    QiraStrings.stripBidiControls(ownerText.getText()))) {
                continue;
            }
            if (resolved.getLeft() == ownerText.getLeft()
                    && resolved.getTop() == ownerText.getTop()
                    && resolved.getRight() == ownerText.getRight()
                    && resolved.getBottom() == ownerText.getBottom()) {
                return true;
            }
        }
        return false;
    }

    private static boolean waitForCatalogIdentityToDisappear(
            QiraV2SlapCatalog.SlapString entry,
            AvikUtility utils,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isOwnedCatalogIdentityVisible(entry, null)) {
                return true;
            }
            sleep(utils, 250L);
        }
        return false;
    }

    private static void requirePostIntroMilestonesBeforeDownstream(
            UiDevice device,
            QiraConfig config,
            CanonicalCaptureGuard captureGuard) {
        if (!isEnXmContractPath(config)) {
            return;
        }
        if (!captureGuard.wasCaptured(SCREEN_LANGUAGE)) {
            failPostIntroClassification(
                    device,
                    config,
                    SCREEN_LANGUAGE + "_required_capture_missing",
                    "The en-XM contract reached a downstream surface before the"
                            + " authoritative response-language capture.");
        }
        if (!captureGuard.wasCaptured(SCREEN_DEVICE_ASSURANCE)) {
            failPostIntroClassification(
                    device,
                    config,
                    SCREEN_DEVICE_ASSURANCE + "_required_capture_missing",
                    "The en-XM contract reached a downstream surface before the"
                            + " authoritative Device Assurance capture.");
        }
    }

    private static void requireEnXmOnboardingMilestones(
            UiDevice device,
            QiraConfig config,
            CanonicalCaptureGuard captureGuard) {
        requirePostIntroMilestonesBeforeDownstream(
                device, config, captureGuard);
    }

    private static boolean isEnXmContractPath(QiraConfig config) {
        return config != null
                && QiraV2InstrumentationDefaults.SLAP_DISCOVERY_LOCALE
                .equalsIgnoreCase(config.getLocale());
    }

    private static void failPostIntroClassification(
            UiDevice device,
            QiraConfig config,
            String dumpTag,
            String reason) {
        QiraUiDumper.dump(
                device,
                config == null ? QiraStrings.DEFAULT_QIRA_PACKAGE
                        : config.getPackageName(),
                dumpTag,
                reason);
        throw new IllegalStateException(reason);
    }

    private static void captureStart(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink) throws Exception {
        QiraV2OnboardingStartPage page =
                new QiraV2OnboardingStartPage(device, utils, config, logger);
        // A fresh install raises the notification runtime-permission dialog over
        // the start card; clear it before AND after the start-surface settle so
        // the Start CTA is the foreground window when we probe its selector.
        dismissInitialSystemPermissionDialogs(device, utils, logger);
        page.waitForLoaded(30000L);
        dismissInitialSystemPermissionDialogs(device, utils, logger);
        page.logSelectorEvidence(SCREEN_START);
        page.requireStartCtaIdBackedEvidence();
        screenshotSink.capture(SCREEN_START);
        try {
            page.clickStartCtaByIdBackedSelector();
        } catch (IllegalStateException noSelector) {
            // The start card exposed no selectable node (textless Compose canvas
            // with no text/desc/message-ID/R.string/clickable node - observed in
            // the ar-EG render). Fall back to locale-independent non-coordinate
            // activation (semantic ACTION_CLICK / key activation / a11y-focus).
            // Never taps a fixed coordinate; rethrows (fail loud with dump) when
            // no route advances onboarding.
            if (!page.activateStartWithoutSelectableNode()) {
                throw noSelector;
            }
        }
        settle(device, utils);
    }

    private static void captureStep(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            String screenName,
            QiraV2SlapCatalog.SlapString... catalogEntries) throws Exception {
        captureStepInternal(
                device,
                config,
                logger,
                screenshotSink,
                screenName,
                true,
                catalogEntries);
    }

    private static void captureStepBestEffort(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            String screenName,
            QiraV2SlapCatalog.SlapString... catalogEntries) throws Exception {
        captureStepInternal(
                device,
                config,
                logger,
                screenshotSink,
                screenName,
                false,
                catalogEntries);
    }

    /**
     * Captures a system (permissioncontroller) dialog by screen name without
     * aborting the flow for ordinary capture failures. System permission prompts have no Qira catalog
     * entry and occasionally expose no SLAP text at capture time; the SLAP sink
     * throws in that case. These captures are supplementary parity screens, so
     * a failure is logged and swallowed rather than failing onboarding. A
     * duplicate canonical-tag assertion is always rethrown.
     */
    private static void captureSystemDialogBestEffort(
            QiraV2ScreenshotSink screenshotSink, Logger logger, String screenName)
            throws Exception {
        try {
            screenshotSink.capture(screenName);
        } catch (DuplicateCanonicalCaptureException duplicate) {
            throw duplicate;
        } catch (Throwable t) {
            if (logger != null) {
                logger.info("QiraV2 SLAP-advance: best-effort capture of " + screenName
                        + " skipped: " + t.getMessage());
            }
        }
    }

    private static void captureStepInternal(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            String screenName,
            boolean requireEvidence,
            QiraV2SlapCatalog.SlapString... catalogEntries) throws Exception {
        QiraV2SlapActions.logCatalogEvidence(
                device,
                config,
                logger,
                screenName,
                catalogEntries);
        if (requireEvidence) {
            for (QiraV2SlapCatalog.SlapString entry : catalogEntries) {
                if (entry != null) {
                    QiraV2SlapActions.requireCatalogEvidence(
                            device,
                            config,
                            logger,
                            screenName,
                            entry,
                            englishAnchorsFor(entry));
                }
            }
        }
        screenshotSink.capture(screenName);
    }

    /**
     * SLAP-driven onboarding advance used as the qira_v2 fallback when the flow's
     * fixed-order steps did not land on the home feature grid.
     *
     * <p>Late onboarding surfaces on this build (response-language picker, Moto
     * sign-in, acknowledgement, permission review) render as Compose sheets whose
     * semantics are not exposed to UiAutomator, and they can appear in a different
     * order than the linear flow expects. The legacy
     * {@code advanceThroughOnboardingToHome} is accessibility-node based and cannot
     * see them (and uses coordinate fallbacks we forbid). Avik SLAP does expose
     * each surface, so this loop detects whichever surface is present by SLAP
     * message ID and advances it with the existing ID-backed handlers until the
     * home feature grid appears. Android system permission dialogs (which ARE
     * exposed to UiAutomator) are handled by their stable permissioncontroller
     * resource IDs. Bounded by {@code timeoutMs}; fails loud with a dump rather
     * than guessing.
     */
    private static void advanceOnboardingToHomeBySlap(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage legacyOnboarding,
            QiraFocusZonePage focusZonePage,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        long lastDiag = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isOnboardingHome(legacyOnboarding, focusZonePage)) {
                settle(device, utils);
                return;
            }
            if (isPermissionControllerSurface(device)) {
                // Capture the Android permission dialogs here too. The parallel
                // late-onboarding advance captures these, but this loop
                // previously advanced them WITHOUT a capture - so a locale routed
                // through here was missing AndroidLocationPermission /
                // AndroidSystemPermission that the other path captured. Captures
                // are best-effort: a system-dialog screenshot must never abort
                // onboarding, and these surfaces are also device-state gated
                // (already-granted permissions simply don't prompt).
                if (isLocationPermissionPromptVisible(device)) {
                    captureSystemDialogBestEffort(
                            screenshotSink, logger, SCREEN_ANDROID_LOCATION_PERMISSION);
                    selectPreciseLocationIfAvailable(device, utils, logger);
                    captureSystemDialogBestEffort(
                            screenshotSink, logger, SCREEN_ANDROID_LOCATION_PERMISSION_PRECISE);
                    clickSystemPermissionResource(
                            device, utils, PERMISSION_ALLOW_FOREGROUND_RESOURCES);
                } else {
                    captureSystemDialogBestEffort(
                            screenshotSink, logger, SCREEN_ANDROID_SYSTEM_PERMISSION);
                    clickSystemPermissionResource(device, utils, PERMISSION_ALLOW_RESOURCES);
                }
                settle(device, utils);
                continue;
            }
            if (isQuitSetupSurfaceVisible(logger)) {
                // This dialog is not an onboarding milestone. It can appear if
                // Android/Qira navigation receives a stray back event while a
                // previous surface is transitioning. Stay in setup so an
                // unexpected dialog cannot silently abandon the capture flow.
                logger.info("QiraV2 SLAP-advance: unexpected Quit setup dialog;"
                        + " dismissing with the resource-backed Stay action.");
                clickQuitSetupStay(device, config, logger);
                settle(device, utils);
                continue;
            }
            PostIntroSurface classifiedSurface = classifyPostIntroSurface(
                    legacyOnboarding, logger);
            if (classifiedSurface == PostIntroSurface.INTRO) {
                logger.info("QiraV2 SLAP-advance: Intro identity still visible;"
                        + " retrying stable Next without emitting another tag.");
                clickIntroFooterNext(device, config, logger);
                if (!waitForCatalogIdentityToDisappear(
                        QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT,
                        utils,
                        6000L)) {
                    failPostIntroClassification(
                            device,
                            config,
                            SCREEN_INTRO_ARROW + "_recovery_transition_stuck",
                            "Intro identity remained visible after the recovery"
                                    + " stable Next transition.");
                }
                continue;
            }
            if (classifiedSurface == PostIntroSurface.RESPONSE_LANGUAGE) {
                logger.info("QiraV2 SLAP-advance: authoritative response-language"
                        + " identity visible; capturing Language exactly once.");
                captureStep(
                        device,
                        config,
                        logger,
                        screenshotSink,
                        SCREEN_LANGUAGE,
                        QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT);
                clickNext(device, config, logger, SCREEN_LANGUAGE);
                if (!waitForCatalogIdentityToDisappear(
                        QiraV2SlapCatalog.LANGUAGE_SCREEN_PILL_TEXT,
                        utils,
                        8000L)) {
                    failPostIntroClassification(
                            device,
                            config,
                            SCREEN_LANGUAGE + "_recovery_transition_stuck",
                            "Response-language identity remained visible after"
                                    + " recovery Next activation.");
                }
                continue;
            }
            if (classifiedSurface == PostIntroSurface.DEVICE_ASSURANCE) {
                logger.info("QiraV2 SLAP-advance: authoritative Device Assurance"
                        + " identity visible; capturing DeviceAssurance exactly once.");
                captureStep(
                        device,
                        config,
                        logger,
                        screenshotSink,
                        SCREEN_DEVICE_ASSURANCE,
                        QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT);
                clickNext(device, config, logger, SCREEN_DEVICE_ASSURANCE);
                if (!waitForCatalogIdentityToDisappear(
                        QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT,
                        utils,
                        8000L)) {
                    failPostIntroClassification(
                            device,
                            config,
                            SCREEN_DEVICE_ASSURANCE + "_recovery_transition_stuck",
                            "Device Assurance identity remained visible after"
                                    + " recovery Next activation.");
                }
                continue;
            }
            if (classifiedSurface == PostIntroSurface.UNKNOWN_NEXT) {
                QiraV2SlapTextDump.logVisibleTextSummaries(
                        "QiraV2 SLAP-advance unknown Next-only surface",
                        false,
                        logger);
                failPostIntroClassification(
                        device,
                        config,
                        SCREEN_PREFIX + "_Onboarding_unknown_next_surface",
                        "A Next-gated recovery surface had no authoritative"
                                + " onboarding catalog identity.");
            }
            if (isAnySignInSurfaceVisible(legacyOnboarding)) {
                logger.info("QiraV2 SLAP-advance: dismissing the stable "
                        + "Continue-as or localized Login/Cancel sign-in sheet.");
                captureAndDismissSignInSurface(
                        device,
                        utils,
                        config,
                        logger,
                        screenshotSink,
                        legacyOnboarding);
                continue;
            }
            if (isSlapMessageVisible(QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA)
                    || isSlapMessageVisible(QiraV2SlapCatalog.I_AGREE_CTA)) {
                if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                        || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)) {
                    handlePermissionPanel(device, utils, logger, screenshotSink, config,
                            legacyOnboarding, focusZonePage);
                } else {
                    logger.info("QiraV2 SLAP-advance: acknowledgement sheet;"
                            + " confirming by message ID.");
                    captureStepBestEffort(device, config, logger, screenshotSink,
                            SCREEN_ACKNOWLEDGE,
                            QiraV2SlapCatalog.DISCLAIMER_SCREEN_TITLE,
                            QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA);
                    screenshotSink.capture(SCREEN_ACKNOWLEDGE_SCROLLED);
                    clickAcknowledgeOrAgree(device, config, logger);
                    settle(device, utils);
                }
                continue;
            }
            if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                    || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)) {
                handlePermissionPanel(device, utils, logger, screenshotSink, config,
                        legacyOnboarding, focusZonePage);
                continue;
            }
            if (isQiraV2HotwordSetupVisible() || legacyOnboarding.isHotwordSetupVisible()) {
                logger.info("QiraV2 SLAP-advance: hotword setup; skipping by message ID.");
                clickSkipThisStep(device, config, logger);
                settle(device, utils);
                continue;
            }
            if (isSlapMessageVisible(QiraV2SlapCatalog.START_CTA)
                    || legacyOnboarding.isExploreStartVisible()) {
                logger.info("QiraV2 SLAP-advance: explore start; starting by message ID.");
                clickStartExplore(device, config, logger);
                settle(device, utils);
                continue;
            }
            long now = System.currentTimeMillis();
            if (now - lastDiag > 5000L) {
                QiraV2SlapTextDump.logVisibleTextSummaries(
                        "QiraV2 advanceOnboardingToHomeBySlap waiting", false, logger);
                logger.info("QiraV2 advanceOnboardingToHomeBySlap currentPackage="
                        + safeCurrentPackage(device));
                lastDiag = now;
            }
            sleep(utils, 400L);
        }
        QiraUiDumper.dump(device, config.getPackageName(),
                SCREEN_HOME + "_slap_advance_timeout",
                "Qira v2 SLAP-driven onboarding advance could not reach the home feature grid");
        throw new IllegalStateException("Qira v2 SLAP-driven onboarding advance could not"
                + " reach the home feature grid within " + timeoutMs + " ms.");
    }

    private static void clickNext(UiDevice device, QiraConfig config, Logger logger, String screen) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                screen,
                QiraV2SlapCatalog.NEXT_CTA,
                NEXT_CTA_STRING_ANCHORS);
    }

    private static void clickIntroFooterNext(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_INTRO_ARROW,
                QiraV2SlapCatalog.NEXT_CTA,
                NEXT_CTA_STRING_ANCHORS);
        logger.info("QiraV2 clicked intro footer through the stable Next"
                + " Compose/message-ID selector.");
    }

    private static void dismissAcknowledgeIfStillVisible(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage legacyOnboarding) throws Exception {
        if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)
                || legacyOnboarding.isPermissionPanelVisible()) {
            return;
        }

        boolean acknowledgeStillVisible =
                isAcknowledgeSurfaceVisibleBeforePermissions(legacyOnboarding);
        if (!acknowledgeStillVisible) {
            return;
        }

        logger.info("QiraV2 acknowledgement sheet is still visible before permissions;"
                + " dismissing it again with ID-backed selectors.");
        for (int pass = 0; pass < 6; pass++) {
            if (pass > 0) {
                if (!legacyOnboarding.scrollAcknowledgeDialogForCapture()) {
                    scrollAcknowledgeByGeometry(device, utils, logger);
                }
            }
            clickAcknowledgeOrAgree(device, config, logger);
            settle(device, utils);
            if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                    || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)
                    || legacyOnboarding.isPermissionPanelVisible()) {
                return;
            }
            if (!isAcknowledgeSurfaceVisibleBeforePermissions(legacyOnboarding)) {
                return;
            }
        }

        if (legacyOnboarding.isAcknowledgeDialogVisible()) {
            legacyOnboarding.acknowledge();
            settle(device, utils);
        }
    }

    private static void forceDismissAcknowledge(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage legacyOnboarding) throws Exception {
        for (int pass = 0; pass < 6; pass++) {
            if (pass > 0) {
                if (!legacyOnboarding.scrollAcknowledgeDialogForCapture()) {
                    scrollAcknowledgeByGeometry(device, utils, logger);
                }
            }
            enableVisibleAcknowledgeToggles(device, utils, logger);
            enableAcknowledgeToggleByResourceAnchor(device, logger);
            clickAcknowledgeOrAgree(device, config, logger);
            settle(device, utils);
            if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                    || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)
                    || legacyOnboarding.isPermissionPanelVisible()) {
                return;
            }
            if (QiraV2SlapTextDump.findByResolvedQiraStringResource(
                    QiraV2SlapCatalog.DISCLAIMER_SCREEN_TITLE.getStringId(),
                    false,
                    logger) == null
                    && !legacyOnboarding.isAcknowledgeDialogVisible()) {
                return;
            }
        }
    }

    private static void enableVisibleAcknowledgeToggles(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        if (device == null) {
            return;
        }
        List<UiObject2> toggles = device.findObjects(By.pkg("com.lenovo.qira").checkable(true));
        int width = device.getDisplayWidth();
        int height = device.getDisplayHeight();
        int clicked = 0;
        for (UiObject2 toggle : toggles) {
            try {
                Rect bounds = toggle.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 80) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 45) / 100) {
                    continue;
                }
                if (toggle.isChecked()) {
                    continue;
                }
                toggle.click();
                clicked++;
                settle(device, utils);
            } catch (StaleObjectException ignored) {
            }
        }
        if (clicked > 0) {
            logger.info("QiraV2 enabled " + clicked
                    + " visible acknowledgement toggle(s) before CTA click.");
        }
    }

    private static void enableAcknowledgeToggleByResourceAnchor(
            UiDevice device,
            Logger logger) {
        if (QiraV2SlapTextDump.clickLogicalEndOfResourceRow(
                device,
                false,
                logger,
                "personalized_results_title")) {
            logger.info("QiraV2 clicked acknowledgement personalized-results toggle"
                    + " by Qira string resource row anchor personalized_results_title.");
        }
    }

    private static boolean isAcknowledgeSurfaceVisibleBeforePermissions(
            QiraOnboardingPage legacyOnboarding) {
        if (isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)
                || legacyOnboarding.isPermissionPanelVisible()) {
            return false;
        }
        return legacyOnboarding.isAcknowledgeDialogVisible()
                || isSlapMessageVisible(QiraV2SlapCatalog.DISCLAIMER_SCREEN_TITLE)
                || isSlapMessageVisible(QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA);
    }

    private static void handlePermissionPanel(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraConfig config,
            QiraOnboardingPage legacyOnboarding,
            QiraFocusZonePage focusZonePage) throws Exception {
        waitForTransitionSettle(device, utils);
        captureStep(
                device,
                config,
                logger,
                screenshotSink,
                SCREEN_PERMISSIONS,
                QiraV2SlapCatalog.I_AGREE_CTA,
                QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE,
                QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE);
        if (!enablePermissionMasterToggleByAccessibility(device, utils, logger)) {
            throw new IllegalStateException(
                    "Qira v2 permission master toggle could not be enabled.");
        }
        if (waitForContextualReadingDialogVisible(utils, logger, legacyOnboarding, 2500L)) {
            logger.info("QiraV2 contextual-reading dialog detected after master toggle;"
                    + " deferring capture to contextual-reading handler.");
        }
        handleContextualReadingPermissionIfShown(
                device,
                utils,
                logger,
                screenshotSink,
                legacyOnboarding,
                focusZonePage);

        // The master cascade can be interrupted by the contextual-reading
        // sheet. Re-walk and verify every checkable row after returning, then
        // capture the enabled state from a known top position in every locale.
        ensureAllPermissionTogglesEnabledByAccessibility(device, utils, logger);
        screenshotSink.capture(SCREEN_PERMISSIONS_TOGGLE_ENABLED);
        captureDistinctPermissionScrollStates(
                device, config, logger, screenshotSink);

        sleep(utils, 1200L);
        clickAgree(device, config, logger);
        waitForPostPermissionTransition(device, utils, logger, legacyOnboarding, 12000L);
        if (legacyOnboarding.isPermissionPanelVisible()) {
            legacyOnboarding.agreeToPermissions();
            waitForPostPermissionTransition(device, utils, logger, legacyOnboarding, 12000L);
        }
    }

    private static boolean enablePermissionMasterToggleByAccessibility(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        List<AccessibilityNodeInfo> toggles = permissionAccessibilityToggles(device);
        if (toggles.isEmpty()) {
            return false;
        }
        AccessibilityNodeInfo master = toggles.get(0);
        Rect bounds = new Rect();
        master.getBoundsInScreen(bounds);
        if (master.isChecked()) {
            logger.info("QiraV2 permission master toggle already checked; bounds="
                    + bounds.toShortString());
            return true;
        }
        boolean performed = master.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        settle(device, utils);
        logger.info("QiraV2 permission master ACTION_CLICK: performed="
                + performed + ", bounds=" + bounds.toShortString());
        return performed;
    }

    private static void ensureAllPermissionTogglesEnabledByAccessibility(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        int observed = 0;
        int changed = 0;
        for (int pass = 0; pass < 16; pass++) {
            List<AccessibilityNodeInfo> toggles = permissionAccessibilityToggles(device);
            observed += toggles.size();
            for (AccessibilityNodeInfo toggle : toggles) {
                if (toggle.isChecked() || !toggle.isEnabled()) {
                    continue;
                }
                if (!toggle.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    Rect bounds = new Rect();
                    toggle.getBoundsInScreen(bounds);
                    throw new IllegalStateException(
                            "Permission toggle ACTION_CLICK failed at "
                                    + bounds.toShortString());
                }
                changed++;
                settle(device, utils);
                if (isSlapMessageVisible(
                        QiraV2SlapCatalog.CONTEXTUAL_READING_PERMISSION_TITLE)) {
                    throw new IllegalStateException(
                            "Contextual-reading dialog reopened during final toggle verification.");
                }
            }
            if (!performPermissionAccessibilityScroll(
                    device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                break;
            }
        }
        if (observed == 0) {
            throw new IllegalStateException(
                    "Direct accessibility exposed no permission toggle nodes.");
        }

        verifyAllPermissionTogglesCheckedByAccessibility(device);
        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        logger.info("QiraV2 permission toggle verification complete: observed="
                + observed + ", changed=" + changed);
    }

    private static void verifyAllPermissionTogglesCheckedByAccessibility(
            UiDevice device) {
        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        int observed = 0;
        for (int pass = 0; pass < 16; pass++) {
            List<AccessibilityNodeInfo> toggles = permissionAccessibilityToggles(device);
            observed += toggles.size();
            for (AccessibilityNodeInfo toggle : toggles) {
                if (!toggle.isChecked()) {
                    Rect bounds = new Rect();
                    toggle.getBoundsInScreen(bounds);
                    throw new IllegalStateException(
                            "Permission toggle remained unchecked after enable pass; bounds="
                                    + bounds.toShortString());
                }
            }
            if (!performPermissionAccessibilityScroll(
                    device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                break;
            }
        }
        if (observed == 0) {
            throw new IllegalStateException(
                    "Permission state verification observed no checkable nodes.");
        }
    }

    private static List<AccessibilityNodeInfo> permissionAccessibilityToggles(
            UiDevice device) {
        List<AccessibilityNodeInfo> out = new ArrayList<>();
        UiAutomation automation = rtlOnboardingUiAutomation();
        AccessibilityNodeInfo root = automation == null
                ? null
                : automation.getRootInActiveWindow();
        if (root == null || device == null) {
            return out;
        }
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            if (isPermissionAccessibilityToggle(node, device)) {
                out.add(node);
            }
            for (int index = 0; index < node.getChildCount(); index++) {
                AccessibilityNodeInfo child = node.getChild(index);
                if (child != null) {
                    queue.add(child);
                }
            }
        }
        java.util.Collections.sort(
                out,
                new java.util.Comparator<AccessibilityNodeInfo>() {
                    @Override
                    public int compare(
                            AccessibilityNodeInfo left,
                            AccessibilityNodeInfo right) {
                        Rect leftBounds = new Rect();
                        Rect rightBounds = new Rect();
                        left.getBoundsInScreen(leftBounds);
                        right.getBoundsInScreen(rightBounds);
                        return leftBounds.top - rightBounds.top;
                    }
                });
        return out;
    }

    private static boolean isPermissionAccessibilityToggle(
            AccessibilityNodeInfo node,
            UiDevice device) {
        if (!node.isCheckable()) {
            return false;
        }
        CharSequence packageName = node.getPackageName();
        if (packageName == null || !"com.lenovo.qira".contentEquals(packageName)) {
            return false;
        }
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        if (bounds.isEmpty()
                || bounds.top < (height * 12) / 100
                || bounds.bottom > (height * 82) / 100
                || bounds.width() < (width * 3) / 100
                || bounds.width() > (width * 22) / 100
                || bounds.height() < (height * 1) / 100
                || bounds.height() > (height * 10) / 100) {
            return false;
        }
        int centerX = bounds.centerX();
        return isRtlOnboardingLocale()
                ? centerX <= (width * 55) / 100
                : centerX >= (width * 45) / 100;
    }

    private static boolean performPermissionAccessibilityScroll(
            UiDevice device,
            int action) {
        AccessibilityNodeInfo scrollable =
                findPermissionAccessibilityScrollable(device);
        if (scrollable == null) {
            return false;
        }
        boolean performed = scrollable.performAction(action);
        try {
            device.waitForIdle(1000L);
        } catch (Throwable ignored) {
        }
        return performed;
    }

    private static void scrollPermissionAccessibilityToEdge(
            UiDevice device,
            int action) {
        for (int pass = 0; pass < 16; pass++) {
            if (!performPermissionAccessibilityScroll(device, action)) {
                return;
            }
        }
    }

    private static AccessibilityNodeInfo findPermissionAccessibilityScrollable(
            UiDevice device) {
        UiAutomation automation = rtlOnboardingUiAutomation();
        AccessibilityNodeInfo root = automation == null
                ? null
                : automation.getRootInActiveWindow();
        if (root == null || device == null) {
            return null;
        }
        int displayWidth = Math.max(1, device.getDisplayWidth());
        int displayHeight = Math.max(1, device.getDisplayHeight());
        AccessibilityNodeInfo best = null;
        int bestArea = 0;
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            if (node.isScrollable()
                    && node.getPackageName() != null
                    && "com.lenovo.qira".contentEquals(node.getPackageName())) {
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                if (!bounds.isEmpty()
                        && bounds.top >= (displayHeight * 10) / 100
                        && bounds.bottom <= (displayHeight * 88) / 100
                        && bounds.width() >= (displayWidth * 35) / 100) {
                    int area = bounds.width() * bounds.height();
                    if (area > bestArea) {
                        best = node;
                        bestArea = area;
                    }
                }
            }
            for (int index = 0; index < node.getChildCount(); index++) {
                AccessibilityNodeInfo child = node.getChild(index);
                if (child != null) {
                    queue.add(child);
                }
            }
        }
        return best;
    }

    private static boolean performPermissionGranularAccessibilityScroll(
            UiDevice device,
            float amount) {
        AccessibilityNodeInfo scrollable =
                findPermissionAccessibilityScrollable(device);
        if (scrollable == null) {
            return false;
        }
        try {
            Bundle arguments = new Bundle();
            arguments.putFloat(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SCROLL_AMOUNT_FLOAT,
                    amount);
            boolean performed = scrollable.performAction(
                    AccessibilityNodeInfo.ACTION_SCROLL_FORWARD,
                    arguments);
            device.waitForIdle(1000L);
            return performed;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Performs a bounded partial scroll inside the exact package-scoped
     * accessibility scrollable. Coordinates are derived from the live semantic
     * node bounds; no fixed screen coordinate or translated text is used.
     */
    private static boolean performPermissionBoundsDerivedPartialScroll(
            UiDevice device,
            float amount) {
        AccessibilityNodeInfo scrollable =
                findPermissionAccessibilityScrollable(device);
        if (scrollable == null || device == null) {
            return false;
        }
        Rect bounds = new Rect();
        scrollable.getBoundsInScreen(bounds);
        if (bounds.isEmpty() || amount <= 0f || amount >= 1f) {
            return false;
        }
        int x = bounds.centerX();
        int inset = Math.max(8, bounds.height() / 12);
        int startY = bounds.bottom - inset;
        int distance = Math.max(1, Math.round(bounds.height() * amount));
        int endY = Math.max(bounds.top + inset, startY - distance);
        if (endY >= startY) {
            return false;
        }
        try {
            boolean performed = device.swipe(x, startY, x, endY, 20);
            device.waitForIdle(1000L);
            return performed;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void captureDistinctPermissionScrollStates(
            UiDevice device,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink) throws Exception {
        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        String topSignature = permissionViewportSignature(logger);
        if (topSignature.isEmpty()) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled1_no_top_signature",
                    "No resource-backed permission row was visible at the panel top.");
        }

        if (!performPermissionAccessibilityScroll(
                device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled1_not_distinct",
                    "The first permission scroll did not change any resource-backed row bounds.");
        }
        String firstFullSignature = permissionViewportSignature(logger);
        if (firstFullSignature.isEmpty()
                || firstFullSignature.equals(topSignature)) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled1_not_distinct",
                    "The first permission scroll did not change any resource-backed row bounds.");
        }

        performPermissionAccessibilityScroll(
                device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        String secondFullSignature = permissionViewportSignature(logger);
        boolean oneFullStepToEdge =
                secondFullSignature.isEmpty()
                        || secondFullSignature.equals(firstFullSignature);

        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        String restoredTopSignature = permissionViewportSignature(logger);
        if (!topSignature.equals(restoredTopSignature)) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled_top_restore_failed",
                    "Permission viewport did not return to its proven top state.");
        }

        String middleSignature;
        if (!oneFullStepToEdge) {
            if (!performPermissionAccessibilityScroll(
                    device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                throwPermissionViewportFailure(
                        device,
                        config,
                        "PermissionsScrolled1_replay_failed",
                        "The proven first permission scroll could not be replayed.");
            }
            middleSignature = permissionViewportSignature(logger);
        } else {
            middleSignature = findAdaptivePermissionMiddleSignature(
                    device,
                    topSignature,
                    firstFullSignature,
                    logger);
        }
        if (middleSignature.isEmpty()
                || middleSignature.equals(topSignature)
                || (oneFullStepToEdge
                && middleSignature.equals(firstFullSignature))) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled1_no_adaptive_middle",
                    "No distinct resource-backed intermediate permission"
                            + " viewport could be established.");
        }
        screenshotSink.capture(SCREEN_PERMISSIONS_SCROLLED + "1");

        scrollPermissionAccessibilityToEdge(
                device, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        String finalSignature = permissionViewportSignature(logger);
        if (finalSignature.isEmpty()
                || finalSignature.equals(middleSignature)
                || finalSignature.equals(topSignature)) {
            throwPermissionViewportFailure(
                    device,
                    config,
                    "PermissionsScrolled_not_distinct",
                    "The final permission viewport was not distinct from both"
                            + " the top and PermissionsScrolled1 states.");
        }
        screenshotSink.capture(SCREEN_PERMISSIONS_SCROLLED);
    }

    private static String findAdaptivePermissionMiddleSignature(
            UiDevice device,
            String topSignature,
            String edgeSignature,
            Logger logger) {
        float[] amounts = {0.25f, 0.40f, 0.55f};
        for (float amount : amounts) {
            scrollPermissionAccessibilityToEdge(
                    device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            if (!performPermissionGranularAccessibilityScroll(
                    device, amount)) {
                continue;
            }
            String candidate = permissionViewportSignature(logger);
            if (!candidate.isEmpty()
                    && !candidate.equals(topSignature)
                    && !candidate.equals(edgeSignature)) {
                if (logger != null) {
                    logger.info("QiraV2 permission adaptive middle viewport:"
                            + " accessibilityScrollAmount=" + amount
                            + ", signature=" + candidate);
                }
                return candidate;
            }
        }
        for (float amount : amounts) {
            scrollPermissionAccessibilityToEdge(
                    device, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            if (!performPermissionBoundsDerivedPartialScroll(
                    device, amount)) {
                continue;
            }
            String candidate = permissionViewportSignature(logger);
            if (!candidate.isEmpty()
                    && !candidate.equals(topSignature)
                    && !candidate.equals(edgeSignature)) {
                if (logger != null) {
                    logger.info("QiraV2 permission adaptive middle viewport:"
                            + " semanticBoundsScrollAmount=" + amount
                            + ", signature=" + candidate);
                }
                return candidate;
            }
        }
        return "";
    }

    private static String permissionViewportSignature(Logger logger) {
        StringBuilder signature = new StringBuilder();
        for (String resourceId : PERMISSION_VIEWPORT_RESOURCE_IDS) {
            AvikText evidence = QiraV2SlapTextDump.findByResolvedQiraStringResource(
                    resourceId, false, null);
            if (evidence == null) {
                continue;
            }
            if (signature.length() > 0) {
                signature.append('|');
            }
            signature.append(resourceId)
                    .append('@')
                    .append(evidence.getTop())
                    .append(':')
                    .append(evidence.getBottom());
        }
        if (logger != null) {
            logger.info("QiraV2 permission viewport signature: " + signature);
        }
        return signature.toString();
    }

    private static void throwPermissionViewportFailure(
            UiDevice device,
            QiraConfig config,
            String tag,
            String reason) {
        QiraUiDumper.dump(device, config.getPackageName(), tag, reason);
        throw new IllegalStateException(reason);
    }

    private static void handleContextualReadingPermissionIfShown(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZonePage) throws Exception {
        settle(device, utils);
        if (!waitForContextualReadingDialogVisible(utils, logger, onboardingPage, 8000L)) {
            return;
        }

        logger.info("QiraV2 contextual-reading permission dialog detected after"
                + " master permission toggle; capturing and enabling permission.");
        screenshotSink.capture(SCREEN_CONTEXTUAL_READING_PERMISSION);
        QiraV2SlapTextDump.logVisibleTextSummaries(
                "QiraV2 contextual-reading dialog initial SLAP inventory",
                false,
                logger);

        // Deterministic scrolled-variant captures: emit a FIXED number of
        // Scrolled{1..N} screens for EVERY locale, decoupled from the Enable
        // search below. The previous loop stopped capturing the moment Enable
        // was found, so verbose / RTL locales (which scroll further before
        // Enable appears) produced MORE Scrolled screens than en-XM - leaving
        // those extra locale screens with no en-XM reference to string-link
        // against. A fixed count keeps the en-XM reference complete.
        for (int pass = 1; pass <= CONTEXTUAL_READING_SCROLL_CAPTURES; pass++) {
            if (!onboardingPage.scrollContextualReadingPermissionDialogDown()) {
                scrollContextualDialogByGeometry(device, utils, logger);
            }
            screenshotSink.capture(SCREEN_CONTEXTUAL_READING_PERMISSION_SCROLLED + pass);
            QiraV2SlapTextDump.logVisibleTextSummaries(
                    "QiraV2 contextual-reading dialog scrolled SLAP inventory pass " + pass,
                    false,
                    logger);
        }

        // Enable click is separate and does NOT vary the deterministic count
        // above: captureAndClickContextualReadingEnableBySlap only captures the
        // single Accept screen once, when Enable is actually visible. After the
        // fixed scrolls we are at the bottom where Enable lives; a bounded retry
        // covers any locale that needs one more nudge, WITHOUT emitting extra
        // Scrolled variants (preserving the previous Enable-progression and
        // failure semantics).
        boolean tappedEnable = captureAndClickContextualReadingEnableBySlap(
                device, logger, screenshotSink);
        for (int retry = 0; retry < 6 && !tappedEnable; retry++) {
            if (!onboardingPage.scrollContextualReadingPermissionDialogDown()) {
                scrollContextualDialogByGeometry(device, utils, logger);
            }
            tappedEnable = captureAndClickContextualReadingEnableBySlap(
                    device, logger, screenshotSink);
        }

        if (!tappedEnable) {
            QiraV2SlapTextDump.logVisibleTextSummaries(
                    "QiraV2 contextual-reading enable failed SLAP inventory",
                    false,
                    logger);
            throw new IllegalStateException("Qira v2 contextual-reading permission"
                    + " dialog was shown, but Enable permission could not be tapped.");
        }

        settle(device, utils);
        if (waitForPermissionPanelOrQiraReturn(device, utils, logger, 6000L)) {
            logger.info("QiraV2 contextual-reading Enable permission returned to"
                    + " the Qira permission sheet; continuing with remaining toggles.");
            return;
        }

        if (focusZonePage.waitForMotoActionCoreEnableScreen(10000L)) {
            screenshotSink.capture(SCREEN_CONTEXTUAL_READING_ACTION_CORE_ENABLE);
            if (!focusZonePage.tapMotoActionCoreEnable()) {
                throw new IllegalStateException("Moto Action Core enable screen was shown,"
                        + " but its Enable action could not be tapped.");
            }
            focusZonePage.acceptPermissionPrompts(3, 5000L);
            settle(device, utils);
        } else {
            throw new IllegalStateException("Qira v2 contextual-reading Enable permission"
                    + " did not return to the Qira permission sheet or open the Moto"
                    + " Action Core enable screen.");
        }

        waitForQiraPackage(device, utils, logger, 15000L);
    }

    private static boolean waitForPermissionPanelOrQiraReturn(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if ("com.lenovo.qira".equals(safeCurrentPackage(device))
                    && hasPermissionSheetEvidence(logger)) {
                settle(device, utils);
                return true;
            }
            sleep(utils, 250L);
        }
        if (logger != null) {
            logger.info("QiraV2 did not observe Qira permission sheet after contextual"
                    + " Enable within " + timeoutMs + " ms; currentPackage="
                    + safeCurrentPackage(device));
        }
        return false;
    }

    private static boolean hasPermissionSheetEvidence(Logger logger) {
        return isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)
                || isSlapMessageVisible(QiraV2SlapCatalog.I_AGREE_CTA)
                || isSlapMessageVisible(QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE)
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE.getStringId(),
                        false,
                        logger) != null
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        QiraV2SlapCatalog.I_AGREE_CTA.getStringId(),
                        false,
                        logger) != null
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        QiraV2SlapCatalog.PERMISSION_MASTER_TOGGLE_TITLE.getStringId(),
                        false,
                        logger) != null;
    }

    private static boolean captureAndClickContextualReadingEnableBySlap(
            UiDevice device,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink) throws Exception {
        if (!isContextualReadingEnableVisible(logger)) {
            return false;
        }
        screenshotSink.capture(SCREEN_CONTEXTUAL_READING_PERMISSION_ACCEPT);
        return clickContextualReadingEnableBySlap(device, logger);
    }

    private static boolean clickContextualReadingEnableBySlap(
            UiDevice device,
            Logger logger) {
        if (!isContextualReadingEnableVisible(logger)) {
            return false;
        }
        if (isSlapMessageVisible(QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION)
                && QiraV2SlapTextDump.shellClickByMessageId(
                device,
                QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getMessageId(),
                false,
                logger)) {
            logger.info("QiraV2 clicked contextual reading Enable permission by SLAP"
                    + " message ID "
                    + QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getMessageId()
                    + " (no hard-coded text or static coordinates).");
            return true;
        }
        if (QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                device,
                QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getStringId(),
                false,
                logger)) {
            logger.info("QiraV2 clicked contextual reading Enable permission by"
                    + " Qira string resource entry "
                    + QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getStringId()
                    + " (no hard-coded text or static coordinates).");
            return true;
        }
        return false;
    }

    private static boolean isContextualReadingEnableVisible(Logger logger) {
        return isSlapMessageVisible(QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION)
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getStringId(),
                        false,
                        logger) != null;
    }

    private static boolean isLikelyContextualReadingDialogVisible(
            QiraOnboardingPage onboardingPage) {
        try {
            if (onboardingPage.waitForContextualReadingPermissionDialog(1200L)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        return isSlapMessageVisible(QiraV2SlapCatalog.CONTEXTUAL_READING_PERMISSION_TITLE)
                || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                        QiraV2SlapCatalog.CONTEXTUAL_READING_PERMISSION_TITLE.getStringId(),
                        false,
                        null) != null;
    }

    private static boolean waitForContextualReadingDialogVisible(
            AvikUtility utils,
            Logger logger,
            QiraOnboardingPage onboardingPage,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isLikelyContextualReadingDialogVisible(onboardingPage)
                    || QiraV2SlapTextDump.findByResolvedQiraStringResource(
                    QiraV2SlapCatalog.CONTEXTUAL_READING_ENABLE_PERMISSION.getStringId(),
                    false,
                    logger) != null) {
                return true;
            }
            sleep(utils, 300L);
        }
        return false;
    }

    private static void scrollContextualDialogByGeometry(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        int width = device.getDisplayWidth();
        int height = device.getDisplayHeight();
        int x = width / 2;
        int startY = (height * 73) / 100;
        int endY = (height * 42) / 100;
        logger.info("QiraV2 contextual-reading dialog geometry scroll: x="
                + x + ", startY=" + startY + ", endY=" + endY);
        try {
            device.executeShellCommand("input swipe "
                    + x + " "
                    + startY + " "
                    + x + " "
                    + endY + " 450");
        } catch (Throwable shellFailure) {
            device.swipe(x, startY, x, endY, 30);
        }
        settle(device, utils);
    }

    private static void scrollAcknowledgeByGeometry(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        int width = device.getDisplayWidth();
        int height = device.getDisplayHeight();
        int x = width / 2;
        int startY = (height * 46) / 100;
        int endY = (height * 14) / 100;
        logger.info("QiraV2 acknowledgement geometry scroll: x="
                + x + ", startY=" + startY + ", endY=" + endY);
        try {
            device.executeShellCommand("input swipe "
                    + x + " "
                    + startY + " "
                    + x + " "
                    + endY + " 550");
        } catch (Throwable shellFailure) {
            device.swipe(x, startY, x, endY, 32);
        }
        settle(device, utils);
    }

    private static void waitForQiraPackage(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String pkg = safeCurrentPackage(device);
            if ("com.lenovo.qira".equals(pkg)) {
                settle(device, utils);
                return;
            }
            sleep(utils, 250L);
        }
        logger.info("QiraV2 contextual-reading flow did not return to Qira within "
                + timeoutMs + " ms; currentPackage=" + safeCurrentPackage(device));
    }

    private static String safeCurrentPackage(UiDevice device) {
        try {
            return device == null ? "" : device.getCurrentPackageName();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static void handlePostOnboardingSystemPermissionsAndCaptureHome(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage onboardingPage) throws Exception {
        QiraFocusZonePage homeProbe = new QiraFocusZonePage(device, config);
        long deadline = System.currentTimeMillis() + POST_ONBOARDING_HOME_TIMEOUT_MS;
        long lastDiagLog = 0L;
        while (System.currentTimeMillis() < deadline) {
            if (isOnboardingHome(onboardingPage, homeProbe)) {
                waitForTransitionSettle(device, utils);
                screenshotSink.capture(SCREEN_HOME);
                return;
            }

            if (onboardingPage.isHotwordSetupVisible() || isQiraV2HotwordSetupVisible()) {
                captureStepBestEffort(
                        device,
                        config,
                        logger,
                        screenshotSink,
                        SCREEN_HOTWORD_SETUP,
                        QiraV2SlapCatalog.VOICE_ACTIVATION_TITLE,
                        QiraV2SlapCatalog.VOICE_ACTIVATION_SKIP_FOR_NOW_CTA,
                        QiraV2SlapCatalog.SKIP_THIS_STEP_CTA);
                clickSkipThisStep(device, config, logger);
                settle(device, utils);
                continue;
            }

            if (onboardingPage.isExploreStartVisible()
                    || isSlapMessageVisible(QiraV2SlapCatalog.START_CTA)) {
                captureStepBestEffort(
                        device,
                        config,
                        logger,
                        screenshotSink,
                        SCREEN_EXPLORE_START,
                        QiraV2SlapCatalog.START_CTA);
                clickStartExplore(device, config, logger);
                settle(device, utils);
                continue;
            }

            if (isPermissionControllerSurface(device)) {
                if (isLocationPermissionPromptVisible(device)) {
                    screenshotSink.capture(SCREEN_ANDROID_LOCATION_PERMISSION);
                    selectPreciseLocationIfAvailable(device, utils, logger);
                    screenshotSink.capture(SCREEN_ANDROID_LOCATION_PERMISSION_PRECISE);
                    if (!clickSystemPermissionResource(device, utils,
                            PERMISSION_ALLOW_FOREGROUND_RESOURCES)) {
                        throw new IllegalStateException("Qira v2 Android location"
                                + " permission prompt exposed no resource-ID backed"
                                + " While-using action.");
                    }
                    settle(device, utils);
                    continue;
                }

                if (findByAnyResource(device, PERMISSION_ALLOW_RESOURCES) != null) {
                    screenshotSink.capture(SCREEN_ANDROID_SYSTEM_PERMISSION);
                    if (!clickSystemPermissionResource(device, utils, PERMISSION_ALLOW_RESOURCES)) {
                        throw new IllegalStateException("Qira v2 Android permission"
                                + " prompt exposed no resource-ID backed allow action.");
                    }
                    settle(device, utils);
                    continue;
                }
            }

            long now = System.currentTimeMillis();
            if (now - lastDiagLog > 8000L) {
                QiraV2SlapTextDump.logVisibleTextSummaries(
                        "QiraV2 waiting for post-onboarding home",
                        false,
                        logger);
                logger.info("QiraV2 post-onboarding home wait currentPackage="
                        + safeCurrentPackage(device));
                lastDiagLog = now;
            }
            sleep(utils, 350L);
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                SCREEN_HOME + "_not_reached",
                "Qira v2 onboarding finished but home feature grid was not detected");
        throw new IllegalStateException("Qira v2 did not reach the home feature grid"
                + " after onboarding and Android permission handling.");
    }

    private static boolean isPermissionControllerSurface(UiDevice device) {
        String currentPackage = safeCurrentPackage(device);
        return PERMISSION_CONTROLLER_PACKAGE.equals(currentPackage)
                || PERMISSION_CONTROLLER_GOOGLE_PACKAGE.equals(currentPackage);
    }

    private static boolean isLocationPermissionPromptVisible(UiDevice device) {
        return isPermissionControllerSurface(device)
                && findByAnyResource(device, PERMISSION_ALLOW_FOREGROUND_RESOURCES) != null
                && (findByAnyResource(device, PERMISSION_LOCATION_FINE_RESOURCES) != null
                        || findByAnyResource(device, PERMISSION_LOCATION_COARSE_RESOURCES) != null);
    }

    private static void selectPreciseLocationIfAvailable(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        UiObject2 precise = findByAnyResource(device, PERMISSION_LOCATION_FINE_RESOURCES);
        if (precise == null) {
            logger.info("QiraV2 Android location permission prompt did not expose"
                    + " a precise radio resource; continuing to foreground allow.");
            return;
        }
        if (precise.isChecked()) {
            return;
        }
        clickObject(device, precise);
        settle(device, utils);
    }

    // ------------------------------------------------------------------
    // APPROVED positional / ACTION_CLICK onboarding fallback - ar-EG / RTL
    // textless-Compose render ONLY (documented exception).
    //
    // Qira's onboarding renders as textless Compose canvases in the Arabic
    // locale: no text / content-desc / message ID / R.string / Compose-string
    // node resolves for the per-screen CTAs, so the normal SLAP/Compose flow
    // and the legacy accessibility navigation stall. This driver advances the
    // onboarding to Home for that case only. It is STRICTLY gated on
    // isCurrentLocaleRtl(), so en-XM and every LTR/normal locale are
    // byte-identical and NEVER take this path (logged). Per screen it prefers
    // the semantic AccessibilityNodeInfo.ACTION_CLICK on the primary clickable
    // node (as already proven for Start) and only falls back to a
    // proportional, RTL-mirrored coordinate tap when the surface exposes no
    // clickable node at all.
    // ------------------------------------------------------------------

    private static boolean isRtlOnboardingLocale() {
        try {
            return QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean isOnboardingHome(
            QiraOnboardingPage legacyOnboarding, QiraFocusZonePage focusZone) {
        try {
            if (legacyOnboarding.isFeatureGridVisible()) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        try {
            return focusZone.isBubbleBarVisible();
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * Drives ar-EG / RTL textless onboarding to Home. Returns {@code true} once
     * Home (feature grid / bubble bar) is reached. No-op (returns false) in any
     * non-RTL locale so the coordinate path can never fire in en-XM / LTR.
     */
    private static boolean advanceRtlTextlessOnboardingToHome(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink sink,
            QiraOnboardingPage legacyOnboarding,
            QiraFocusZonePage focusZone,
            long timeoutMs) throws Exception {
        if (!isRtlOnboardingLocale()) {
            logger.info("QiraV2 onboarding: positional RTL fallback NOT engaged"
                    + " (locale is not RTL); coordinate path fires 0 times here.");
            return false;
        }
        logger.info("QiraV2 onboarding: RTL textless render; engaging APPROVED"
                + " positional/ACTION_CLICK fallback (ar-EG documented exception).");
        String[] sequence = {
                SCREEN_DEVICE_ASSURANCE,
                SCREEN_CONTINUE_AS,
                SCREEN_ACKNOWLEDGE,
                SCREEN_PERMISSIONS,
                SCREEN_CONTEXTUAL_READING_PERMISSION,
                SCREEN_HOTWORD_SETUP,
                SCREEN_EXPLORE_START
        };
        // Known primary-CTA English anchors per step. Resolved to the current
        // locale via the Compose bridge and clicked by SLAP first, so on
        // text-bearing ar screens (e.g. Acknowledge "أُقرّ") the CORRECT button
        // is targeted rather than a blind geometry tap that could hit "Cancel".
        String[][] ctaAnchors = {
                {"Next", "Continue"},
                {"Continue as", "Continue", "Get started"},
                {"I acknowledge", "I agree", "Acknowledge", "Agree"},
                {"I agree", "Agree", "Continue", "Next"},
                {"Enable permission", "Enable", "Continue"},
                {"Skip this step", "Skip for now", "Skip", "Not now"},
                {"Start", "Get started", "Explore"}
        };
        long deadline = System.currentTimeMillis() + timeoutMs;
        int coordinateTaps = 0;
        for (int i = 0; i < sequence.length && System.currentTimeMillis() < deadline; i++) {
            drainSystemPermissionDialogsQuietly(device, utils);
            if (isOnboardingHome(legacyOnboarding, focusZone)) {
                logger.info("QiraV2 RTL onboarding: reached Home before step " + sequence[i]);
                break;
            }
            // ar Compose surfaces render their content (and a11y semantics)
            // a beat after the transition; wait for a settled, non-empty
            // surface so we act on the real screen, not a textless frame.
            waitForRtlOnboardingSurfaceReady(device, config, 6000L, utils);
            settle(device, utils);
            try {
                sink.capture(sequence[i]);
            } catch (Throwable ignored) {
            }
            // Per-screen enablement so the primary CTA is actionable.
            if (SCREEN_ACKNOWLEDGE.equals(sequence[i])) {
                enableVisibleAcknowledgeToggles(device, utils, logger);
                enableAcknowledgeToggleByResourceAnchor(device, logger);
                try {
                    legacyOnboarding.scrollAcknowledgeDialogForCapture();
                } catch (Throwable ignored) {
                }
                settle(device, utils);
            } else if (SCREEN_PERMISSIONS.equals(sequence[i])) {
                coordinateTaps += enableRtlPermissionMasterToggle(device, utils, config, logger);
            }
            String before = surfaceFingerprint(device, config);
            // Advance, verifying each attempt against a re-stabilized surface
            // (ar Compose surfaces render asynchronously, so a bare settle is
            // not enough): Compose-resolved CTA -> semantic ACTION_CLICK ->
            // proportional trailing-bottom coordinate -> full-width bottom.
            boolean advanced = false;
            if (QiraV2SlapTextDump.clickByEnglishAnchorCompose(
                    device, config.getPackageName(), false, logger, ctaAnchors[i])) {
                advanced = rtlSurfaceChanged(device, config, utils, before);
            }
            if (!advanced && actionClickPrimaryClickable(device, logger)) {
                advanced = rtlSurfaceChanged(device, config, utils, before);
            }
            // Coordinate candidates (proportional, RTL-mirrored). The trailing
            // CTA sits deeper into the mirrored corner than a naive mirror
            // predicts, so we sweep a few trailing X positions plus the
            // full-width-bottom centre. Each is verified against a re-stabilized
            // surface; we stop at the first that advances.
            float[][] candidates = {
                    {0.77f, 0.84f}, {0.85f, 0.82f}, {0.5f, 0.90f}, {0.35f, 0.84f}
            };
            for (int c = 0; c < candidates.length && !advanced; c++) {
                tapProportional(device, logger, candidates[c][0], candidates[c][1],
                        "primary CTA candidate " + c);
                coordinateTaps++;
                advanced = rtlSurfaceChanged(device, config, utils, before);
            }
            if (!advanced) {
                logger.info("QiraV2 RTL onboarding: step " + sequence[i]
                        + " did not visibly advance (surface unchanged).");
            }
            drainSystemPermissionDialogsQuietly(device, utils);
        }
        boolean home = isOnboardingHome(legacyOnboarding, focusZone);
        long homeDeadline = System.currentTimeMillis() + 10000L;
        while (!home && System.currentTimeMillis() < homeDeadline) {
            drainSystemPermissionDialogsQuietly(device, utils);
            utils.sleep(400L);
            home = isOnboardingHome(legacyOnboarding, focusZone);
        }
        logger.info("QiraV2 RTL positional onboarding finished; home=" + home
                + ", coordinateTaps=" + coordinateTaps + " (ar-EG approved exception).");
        return home;
    }

    private static void drainSystemPermissionDialogsQuietly(UiDevice device, AvikUtility utils) {
        try {
            for (int i = 0; i < 4; i++) {
                if (!isPermissionControllerSurface(device)
                        && findByAnyResource(device, PERMISSION_ALLOW_RESOURCES) == null) {
                    return;
                }
                if (!clickSystemPermissionResource(device, utils, PERMISSION_ALLOW_RESOURCES)) {
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static UiAutomation rtlOnboardingUiAutomation() {
        try {
            return InstrumentationRegistry.getInstrumentation().getUiAutomation();
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Semantic ACTION_CLICK on the primary CTA node: the trailing-most clickable
     * in the bottom CTA band (right under LTR, left under RTL - avoids the
     * leading "Cancel"). Returns {@code true} if a clickable node was found and
     * ACTION_CLICK was performed (no coordinate involved).
     */
    private static boolean actionClickPrimaryClickable(UiDevice device, Logger logger) {
        UiAutomation automation = rtlOnboardingUiAutomation();
        if (automation == null) {
            return false;
        }
        AccessibilityNodeInfo root = automation.getRootInActiveWindow();
        if (root == null) {
            return false;
        }
        int height = Math.max(1, device.getDisplayHeight());
        int topBand = (height * 55) / 100;
        int bottomBand = (height * 94) / 100;
        List<AccessibilityNodeInfo> clickables = new ArrayList<>();
        collectClickableNodes(root, clickables);
        boolean rtl = isRtlOnboardingLocale();
        AccessibilityNodeInfo best = null;
        int bestEdge = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int bestBottom = -1;
        Rect bounds = new Rect();
        for (AccessibilityNodeInfo node : clickables) {
            node.getBoundsInScreen(bounds);
            int cy = bounds.centerY();
            if (cy < topBand || cy > bottomBand) {
                continue;
            }
            // Prefer the lowest CTA row; within it, the trailing edge.
            if (bounds.bottom < bestBottom - (height / 40)) {
                continue;
            }
            int edge = rtl ? bounds.left : bounds.right;
            boolean better = bounds.bottom > bestBottom + (height / 40)
                    || (rtl ? edge < bestEdge : edge > bestEdge);
            if (best == null || better) {
                best = node;
                bestEdge = edge;
                bestBottom = bounds.bottom;
            }
        }
        if (best == null) {
            return false;
        }
        best.getBoundsInScreen(bounds);
        boolean performed = best.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (logger != null) {
            logger.info("QiraV2 RTL onboarding ACTION_CLICK primary CTA node: performed="
                    + performed + ", rtl=" + rtl + ", bounds=" + bounds.toShortString());
        }
        return performed;
    }

    private static void collectClickableNodes(
            AccessibilityNodeInfo root, List<AccessibilityNodeInfo> out) {
        Deque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            if (node.isClickable()) {
                out.add(node);
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    queue.add(child);
                }
            }
        }
    }

    /**
     * Enables the permission-screen master toggle (top-trailing) via a checkable
     * node ACTION_CLICK, or a proportional coordinate tap when no node is
     * exposed. Returns the number of coordinate taps used (0 when semantic).
     */
    private static int enableRtlPermissionMasterToggle(
            UiDevice device, AvikUtility utils, QiraConfig config, Logger logger) throws Exception {
        try {
            List<UiObject2> checkables = device.findObjects(By.checkable(true));
            UiObject2 master = null;
            for (UiObject2 view : checkables) {
                Rect b = view.getVisibleBounds();
                if (b == null) {
                    continue;
                }
                if (master == null || b.centerY() < master.getVisibleBounds().centerY()) {
                    master = view;
                }
            }
            if (master != null) {
                boolean already = false;
                try {
                    already = master.isChecked();
                } catch (Throwable ignored) {
                }
                if (!already) {
                    clickObject(device, master);
                    settle(device, utils);
                    logger.info("QiraV2 RTL onboarding: enabled permission master toggle by"
                            + " checkable node (no coordinate).");
                }
                return 0;
            }
        } catch (Throwable ignored) {
        }
        // No checkable node exposed: proportional top-trailing tap (mirrored).
        tapProportional(device, logger, 0.84f, 0.205f, "permissions master toggle");
        settle(device, utils);
        return 1;
    }

    /**
     * Taps a proportional screen position. {@code xFraction} is expressed for a
     * LTR layout and mirrored automatically under RTL, so the same call targets
     * the trailing edge in both directions.
     */
    private static void tapProportional(
            UiDevice device, Logger logger, float xFraction, float yFraction, String label) {
        int width = Math.max(1, device.getDisplayWidth());
        int height = Math.max(1, device.getDisplayHeight());
        float effectiveX = isRtlOnboardingLocale() ? (1f - xFraction) : xFraction;
        int x = Math.round(width * effectiveX);
        int y = Math.round(height * yFraction);
        x = Math.max(8, Math.min(width - 8, x));
        y = Math.max(8, Math.min(height - 8, y));
        if (logger != null) {
            logger.info("QiraV2 RTL onboarding APPROVED coordinate tap: " + label
                    + " at (" + x + "," + y + ") = (" + effectiveX + "," + yFraction
                    + ") of " + width + "x" + height + " [ar-EG exception]");
        }
        device.click(x, y);
    }

    /**
     * Waits for the ar/RTL onboarding Compose surface to render a settled,
     * non-empty screen (a clickable node or a Qira text/desc node, stable across
     * two samples) so per-step actions do not fire on a textless transitional
     * frame. Bounded; returns as soon as the surface is ready.
     */
    private static void waitForRtlOnboardingSurfaceReady(
            UiDevice device, QiraConfig config, long timeoutMs, AvikUtility utils)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        String last = null;
        int stable = 0;
        while (System.currentTimeMillis() < deadline) {
            boolean hasClickable;
            try {
                hasClickable = !device.findObjects(
                        By.pkg(config.getPackageName()).clickable(true)).isEmpty();
            } catch (Throwable t) {
                hasClickable = false;
            }
            String fp = surfaceFingerprint(device, config);
            boolean hasContent = hasClickable || fp.indexOf('|') >= 0 || fp.indexOf('#') >= 0;
            if (hasContent && fp.equals(last)) {
                if (++stable >= 2) {
                    return;
                }
            } else {
                stable = 0;
            }
            last = fp;
            utils.sleep(400L);
        }
    }

    private static boolean rtlSurfaceChanged(
            UiDevice device, QiraConfig config, AvikUtility utils, String before)
            throws Exception {
        waitForRtlOnboardingSurfaceReady(device, config, 4000L, utils);
        return !surfaceFingerprint(device, config).equals(before);
    }

    private static String surfaceFingerprint(UiDevice device, QiraConfig config) {
        StringBuilder sb = new StringBuilder();
        try {
            List<UiObject2> nodes = device.findObjects(By.pkg(config.getPackageName()));
            sb.append(nodes.size());
            for (UiObject2 node : nodes) {
                try {
                    String text = node.getText();
                    if (text != null && !text.isEmpty()) {
                        sb.append('|').append(text);
                        continue;
                    }
                    String desc = node.getContentDescription();
                    if (desc != null && !desc.isEmpty()) {
                        sb.append('#').append(desc);
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
        return sb.toString();
    }

    private static boolean clickSystemPermissionResource(
            UiDevice device,
            AvikUtility utils,
            String... resources) throws Exception {
        UiObject2 target = findByAnyResource(device, resources);
        if (target == null) {
            return false;
        }
        clickObject(device, target);
        settle(device, utils);
        return true;
    }

    /**
     * Dismisses any Android runtime-permission dialog that is raised OVER the
     * Qira onboarding start card on a fresh install (notably the Android 13+
     * notification permission prompt). While that dialog is up, the
     * permissioncontroller window is in the foreground and the Qira start card
     * is a background window, so the Start CTA is not present in the foreground
     * SLAP hierarchy and no stable selector can resolve it - this was the
     * ar-EG onboarding blocker. Dismissal uses the stable
     * {@code permissioncontroller} Allow resource IDs (locale independent) and
     * is a no-op when no dialog is present, so en-XM and every other locale are
     * unaffected.
     */
    private static void dismissInitialSystemPermissionDialogs(
            UiDevice device,
            AvikUtility utils,
            Logger logger) throws Exception {
        for (int i = 0; i < 4; i++) {
            if (!isPermissionControllerSurface(device)
                    && findByAnyResource(device, PERMISSION_ALLOW_RESOURCES) == null) {
                return;
            }
            if (!clickSystemPermissionResource(device, utils, PERMISSION_ALLOW_RESOURCES)) {
                return;
            }
            if (logger != null) {
                logger.info("QiraV2 onboarding: dismissed an initial system permission"
                        + " dialog covering the start card via permissioncontroller"
                        + " Allow resource ID (locale-independent).");
            }
            settle(device, utils);
        }
    }

    private static UiObject2 findByAnyResource(UiDevice device, String... resources) {
        if (device == null || resources == null) {
            return null;
        }
        for (String resource : resources) {
            if (resource == null || resource.isEmpty()) {
                continue;
            }
            UiObject2 object = device.findObject(By.res(resource));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private static void clickObject(UiDevice device, UiObject2 object) throws Exception {
        UiObject2 clickableTarget = object;
        while (clickableTarget != null && !clickableTarget.isClickable()) {
            clickableTarget = clickableTarget.getParent();
        }
        Rect bounds = clickableTarget != null
                ? clickableTarget.getVisibleBounds()
                : object.getVisibleBounds();
        device.click(bounds.centerX(), bounds.centerY());
    }

    private static void clickContinueAs(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_CONTINUE_AS,
                QiraV2SlapCatalog.CONTINUE_AS_CTA,
                CONTINUE_AS_STRING_ANCHORS);
    }

    private static void clickQuitSetupStay(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_CONTINUE_AS,
                QiraV2SlapCatalog.QUIT_SETUP_STAY_CTA);
    }

    private static boolean isContinueAsSignInSurfaceVisible(
            QiraOnboardingPage legacyOnboarding) {
        if (isCatalogEntryVisible(QiraV2SlapCatalog.CONTINUE_AS_CTA, null)) {
            return true;
        }
        try {
            return legacyOnboarding != null && legacyOnboarding.isSignInDialogVisible();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isLocalizedLoginSurfaceVisible() {
        // The dismiss row is rendered independently and can use a different
        // Compose string ID across Qira's Login, Moto-account and Lenovo-ID
        // variants. Guard on the distinctive resource-backed title first; use
        // an action-row pair only as a fallback, never as an AND-gate.
        if (isCatalogEntryVisible(QiraV2SlapCatalog.LOGIN_SCREEN_TITLE, null)
                || isCatalogEntryVisible(QiraV2SlapCatalog.SIGN_IN_WITH_MOTO_TITLE, null)
                || isCatalogEntryVisible(QiraV2SlapCatalog.TAB_LOGIN_SCREEN_TITLE, null)
                || isCatalogEntryVisible(
                        QiraV2SlapCatalog.WELCOME_PILL_LOGIN_WITH_LENOVO_TITLE, null)) {
            return true;
        }
        boolean hasPrimaryAction = isCatalogEntryVisible(QiraV2SlapCatalog.LOGIN_CTA, null)
                || isCatalogEntryVisible(QiraV2SlapCatalog.SIGN_IN_CTA, null)
                || isCatalogEntryVisible(QiraV2SlapCatalog.LOGIN_ACCESSIBILITY_CTA, null);
        return hasPrimaryAction
                && isCatalogEntryVisible(QiraV2SlapCatalog.LOGIN_CANCEL_CTA, null);
    }

    private static boolean isQuitSetupSurfaceVisible() {
        return isQuitSetupSurfaceVisible(null);
    }

    private static boolean isQuitSetupSurfaceVisible(Logger logger) {
        // Both actions are required so that an unrelated localized "Exit" or
        // "Stay" string cannot be mistaken for this confirmation dialog.
        AvikText title = QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                QiraV2SlapCatalog.QUIT_SETUP_TITLE.getStringId(), false, null);
        AvikText exit = QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                QiraV2SlapCatalog.QUIT_SETUP_EXIT_CTA.getStringId(), false, null);
        AvikText stay = QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                QiraV2SlapCatalog.QUIT_SETUP_STAY_CTA.getStringId(), false, null);
        if (logger != null && (title != null || exit != null || stay != null)) {
            logger.info("QiraV2 Quit setup probe: title="
                    + QiraV2SlapTextDump.summarize(title)
                    + ", exit="
                    + QiraV2SlapTextDump.summarize(exit)
                    + ", stay="
                    + QiraV2SlapTextDump.summarize(stay));
        }
        return title != null && exit != null && stay != null;
    }

    private static boolean isAnySignInSurfaceVisible(QiraOnboardingPage legacyOnboarding) {
        return isContinueAsSignInSurfaceVisible(legacyOnboarding)
                || isLocalizedLoginSurfaceVisible();
    }

    /**
     * Handles both account-backed Continue-as and anonymous localized Login /
     * Cancel sheets. Tier 2 locales use the latter, which has no Continue-as
     * message ID but does expose stable Compose resource IDs.
     */
    private static void captureAndDismissSignInSurface(
            UiDevice device,
            AvikUtility utils,
            QiraConfig config,
            Logger logger,
            QiraV2ScreenshotSink screenshotSink,
            QiraOnboardingPage legacyOnboarding) throws Exception {
        // The account-backed Continue-as sheet shares the generic
        // sign_in_with_moto title with the anonymous Login/Cancel sheet.
        // Prefer its unique Continue-as CTA before classifying the surface as
        // account-gated, otherwise a valid signed-in account is misrouted to
        // failForAnonymousLogin.
        boolean continueAs = isContinueAsSignInSurfaceVisible(legacyOnboarding);
        boolean localizedLogin = !continueAs
                && isLocalizedLoginSurfaceVisible();
        if (!continueAs && !localizedLogin) {
            return;
        }

        waitForTransitionSettle(device, utils);
        if (continueAs) {
            captureStepBestEffort(
                    device,
                    config,
                    logger,
                    screenshotSink,
                    SCREEN_CONTINUE_AS,
                    QiraV2SlapCatalog.CONTINUE_AS_CTA);
            clickContinueAs(device, config, logger);
        } else {
            captureStepBestEffort(
                    device,
                    config,
                    logger,
                    screenshotSink,
                    SCREEN_CONTINUE_AS,
                    QiraV2SlapCatalog.LOGIN_SCREEN_TITLE,
                    QiraV2SlapCatalog.SIGN_IN_WITH_MOTO_TITLE,
                    QiraV2SlapCatalog.TAB_LOGIN_SCREEN_TITLE,
                    QiraV2SlapCatalog.WELCOME_PILL_LOGIN_WITH_LENOVO_TITLE,
                    QiraV2SlapCatalog.LOGIN_CTA,
                    QiraV2SlapCatalog.SIGN_IN_CTA,
                    QiraV2SlapCatalog.LOGIN_ACCESSIBILITY_CTA,
                    QiraV2SlapCatalog.LOGIN_CANCEL_CTA);
            failForAnonymousLogin(device, config);
        }
        settle(device, utils);

        long deadline = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < deadline) {
            if (!isAnySignInSurfaceVisible(legacyOnboarding)
                    && !isQuitSetupSurfaceVisible()) {
                return;
            }
            sleep(utils, 250L);
        }

        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                SCREEN_CONTINUE_AS + "_dismiss_not_observed",
                "Stable sign-in dismissal did not leave the known sign-in surface");
        throw new IllegalStateException(
                "Qira v2 sign-in surface remained after its stable resource-backed action.");
    }

    /**
     * An anonymous Login/Cancel sheet is an authentication gate, not an
     * onboarding variation that can be skipped. Its resource-backed Cancel
     * action opens {@code leave_onboarding}; Leave closes Qira to the launcher
     * and Stay returns to this same sheet. Do not fake a successful capture by
     * bouncing between those two states.
     */
    private static void failForAnonymousLogin(
            UiDevice device,
            QiraConfig config) {
        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                SCREEN_CONTINUE_AS + "_anonymous_login_account_required",
                "Anonymous Login/Cancel is account-gated: Cancel -> Quit setup ->"
                        + " launcher; Stay returns to Login/Cancel");
        throw new IllegalStateException(
                "Qira v2 exposed an anonymous Login/Cancel sheet. The only"
                        + " resource-backed cancel path is Quit setup -> Leave"
                        + " (launcher) or Stay (same login sheet), so a"
                        + " provisioned Moto account is required to reach Home"
                        + " without fabricating an onboarding transition.");
    }

    private static void clickAcknowledge(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_ACKNOWLEDGE,
                QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA,
                I_ACKNOWLEDGE_STRING_ANCHORS);
    }

    private static void clickAcknowledgeOrAgree(
            UiDevice device,
            QiraConfig config,
            Logger logger) throws Exception {
        long deadline = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < deadline) {
            if (isCatalogEntryVisible(QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA, logger)) {
                clickAcknowledge(device, config, logger);
                return;
            }
            if (isCatalogEntryVisible(QiraV2SlapCatalog.I_AGREE_CTA, logger)) {
                clickAgree(device, config, logger);
                return;
            }
            Thread.sleep(500L);
        }
        QiraUiDumper.dump(
                device,
                config.getPackageName(),
                SCREEN_ACKNOWLEDGE + "_no_acknowledge_or_agree_selector",
                "No ID-backed acknowledge/agree selector was exposed");
        throw new IllegalStateException("Qira v2 acknowledgement surface exposed"
                + " neither i_acknowledge nor i_agree by message ID or Qira"
                + " string resource entry.");
    }

    private static boolean isCatalogEntryVisible(
            QiraV2SlapCatalog.SlapString entry,
            Logger logger) {
        return entry != null
                && ((entry.getMessageId() != null
                        && !entry.getMessageId().isEmpty()
                        && QiraV2SlapTextDump.findByMessageId(
                                entry.getMessageId(),
                                false,
                                logger) != null)
                || QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                        entry.getStringId(),
                        false,
                        logger) != null);
    }

    private static void clickAgree(UiDevice device, QiraConfig config, Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_PERMISSIONS,
                QiraV2SlapCatalog.I_AGREE_CTA,
                I_AGREE_STRING_ANCHORS);
    }

    private static void waitForPostPermissionTransition(
            UiDevice device,
            AvikUtility utils,
            Logger logger,
            QiraOnboardingPage onboardingPage,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (onboardingPage.isHotwordSetupVisible()
                    || isQiraV2HotwordSetupVisible()
                    || onboardingPage.isExploreStartVisible()
                    || onboardingPage.isFeatureGridVisible()) {
                waitForTransitionSettle(device, utils);
                return;
            }
            if (!isSlapMessageVisible(QiraV2SlapCatalog.I_AGREE_CTA)
                    && !isSlapMessageVisible(QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE)) {
                waitForTransitionSettle(device, utils);
                return;
            }
            sleep(utils, 300L);
        }
        if (logger != null) {
            logger.info("QiraV2 post-permission transition wait timed out after "
                    + timeoutMs + " ms; continuing with current surface.");
        }
        settle(device, utils);
    }

    private static void clickSkipThisStep(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        if (isSlapMessageVisible(QiraV2SlapCatalog.VOICE_ACTIVATION_SKIP_FOR_NOW_CTA)) {
            QiraV2SlapActions.clickCatalogEntry(
                    device,
                    config,
                    logger,
                    SCREEN_HOTWORD_SETUP,
                    QiraV2SlapCatalog.VOICE_ACTIVATION_SKIP_FOR_NOW_CTA,
                    SKIP_FOR_NOW_STRING_ANCHORS);
            return;
        }
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_HOTWORD_SETUP,
                QiraV2SlapCatalog.SKIP_THIS_STEP_CTA,
                SKIP_THIS_STEP_STRING_ANCHORS);
    }

    private static void clickStartExplore(
            UiDevice device,
            QiraConfig config,
            Logger logger) {
        QiraV2SlapActions.clickCatalogEntry(
                device,
                config,
                logger,
                SCREEN_EXPLORE_START,
                QiraV2SlapCatalog.START_CTA,
                START_CTA_STRING_ANCHORS);
    }

    private static String[] englishAnchorsFor(QiraV2SlapCatalog.SlapString entry) {
        if (entry == QiraV2SlapCatalog.NEXT_CTA) {
            return NEXT_CTA_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT
                || entry == QiraV2SlapCatalog.INTRO_SCREEN_BRAND_TEXT) {
            return new String[] {"Hi, I\u2019m Motorola Qira, your personal intelligence"};
        }
        if (entry == QiraV2SlapCatalog.DEVICE_ASSURANCE_SCREEN_PILL_TEXT) {
            return new String[] {
                    "I can help you stay productive, spark ideas, and work across"
                            + " your Lenovo & Motorola Qira devices"
            };
        }
        if (entry == QiraV2SlapCatalog.CONTINUE_AS_CTA) {
            return CONTINUE_AS_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.I_ACKNOWLEDGE_CTA) {
            return I_ACKNOWLEDGE_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.I_AGREE_CTA) {
            return I_AGREE_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.SKIP_THIS_STEP_CTA) {
            return SKIP_THIS_STEP_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.VOICE_ACTIVATION_SKIP_FOR_NOW_CTA) {
            return SKIP_FOR_NOW_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.START_CTA) {
            return START_CTA_STRING_ANCHORS;
        }
        if (entry == QiraV2SlapCatalog.PERMISSIONS_SCREEN_TITLE) {
            return new String[] {"Let's review a few permissions"};
        }
        if (entry == QiraV2SlapCatalog.VOICE_ACTIVATION_TITLE) {
            return new String[] {"Voice Activation"};
        }
        return new String[0];
    }

    private static boolean isQiraV2HotwordSetupVisible() {
        return isSlapMessageVisible(QiraV2SlapCatalog.VOICE_ACTIVATION_TITLE)
                || isSlapMessageVisible(QiraV2SlapCatalog.VOICE_ACTIVATION_SKIP_FOR_NOW_CTA);
    }

    private static boolean waitForIntroPill(
            UiDevice device,
            Logger logger,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isOwnedCatalogIdentityVisible(
                    QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT, null)) {
                if (logger != null) {
                    logger.info("QiraV2 intro pill visible by owner-backed"
                            + " Compose catalog identity.");
                }
                return true;
            }
            if (QiraV2SlapTextDump.findByAnyResolvedQiraStringResource(
                    false,
                    logger,
                    QiraV2SlapCatalog.INTRO_SCREEN_PILL_TEXT.getStringId(),
                    QiraV2SlapCatalog.INTRO_SCREEN_BRAND_TEXT.getStringId()) != null) {
                if (logger != null) {
                    logger.info("QiraV2 intro pill visible by Qira Compose string ID.");
                }
                return true;
            }
            Thread.sleep(250L);
        }
        if (logger != null) {
            logger.info("QiraV2 intro pill not visible by SLAP message ID after "
                    + timeoutMs
                    + " ms.");
        }
        return false;
    }

    private static boolean isSlapMessageVisible(QiraV2SlapCatalog.SlapString entry) {
        return entry != null
                && ((entry.getMessageId() != null
                        && !entry.getMessageId().isEmpty()
                        && QiraV2SlapTextDump.findByMessageId(
                                entry.getMessageId(),
                                false,
                                null) != null)
                || QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                        entry.getStringId(),
                        false,
                        null) != null);
    }

    private static void waitForTransitionSettle(UiDevice device, AvikUtility utils)
            throws Exception {
        settle(device, utils);
        try {
            if (device != null) {
                device.waitForIdle(1500L);
            }
        } catch (Throwable ignored) {
        }
        sleep(utils, POST_TRANSITION_SETTLE_MS);
    }

    private static void settle(UiDevice device, AvikUtility utils) throws Exception {
        try {
            if (device != null) {
                device.waitForIdle(800L);
            }
        } catch (Throwable ignored) {
        }
        sleep(utils, 600L);
    }

    private static void sleep(AvikUtility utils, long millis) throws Exception {
        if (utils != null) {
            utils.sleep(millis);
        } else {
            Thread.sleep(millis);
        }
    }
}

