package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.List;

import avik.qira.utils.QiraConfig;

public class QiraHomePage extends BaseQiraPage {

    private static final String[] HERO_CARD_ANCHORS = {
            "Drop into Chat Mode and continue seamlessly across devices",
            "All in one AI that's all about you",
            "Collaborate whenever, wherever with real-time, multimodal interactions",
            "Get caught up on what you missed across your devices"
    };

    private static final String[] DISCOVER_TILE_ANCHORS = {
            "Creator Zone",
            "Knowledge",
            "Chat History",
            "Help & Support"
    };

    private static final String[] LANGUAGE_SHEET_ANCHORS = {
            "Choose a response language for Motorola Qira"
    };

    private static final String[] SIGN_IN_DIALOG_ANCHORS = {
            "Sign in with Moto account or Lenovo ID",
            "Continue as"
    };

    private static final String[] TOUR_DIALOG_ANCHORS = {
            "Let's explore what Motorola Qira can do",
            "Start the tour and get to know Motorola Qira",
            "Remind me later",
            "Start"
    };

    private static final String[] COMPOSER_ENTRY_LABELS = {
            "Ask Qira",
            "Ask anything",
            "Type a message",
            "Type or speak",
            "Message Qira",
            "Chat",
            "Search"
    };

    private static final String[] CATCH_ME_UP_LABELS = {
            "Catch me up",
            "Summary",
            "Summaries"
    };

    private static final String[] PAY_ATTENTION_LABELS = {
            "Pay attention"
    };

    private static final String[] MEMORY_LABELS = {
            "Remember this",
            "Memory",
            "Memories",
            "Saved",
            "Knowledge"
    };

    private static final String[] HISTORY_LABELS = {
            "Chat History",
            "History",
            "Recents",
            "Past chats",
            "All Chats",
            "Recent chats",
            "Manage chats"
    };

    public QiraHomePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraHomePage waitForLaunchSurface() throws Exception {
        long deadline = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isDiscoverVisible()
                    || isLanguageSheetVisible()
                    || isSignInDialogVisible()
                    || isTourDialogVisible()) {
                settle();
                return this;
            }
            mUtils.sleep(250L);
        }

        throw new IllegalStateException("Unable to detect a Motorola Qira launch surface");
    }

    public QiraHomePage waitForLoaded() throws Exception {
        long deadline = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < deadline) {
            if (handleSystemPermissionPrompt()) {
                continue;
            }
            if (isDiscoverVisible()) {
                settle();
                return this;
            }
            mUtils.sleep(250L);
        }

        throw new IllegalStateException("Unable to detect the Motorola Qira discover home surface");
    }

    public boolean isDiscoverVisible() {
        int visibleTiles = 0;
        for (String anchor : DISCOVER_TILE_ANCHORS) {
            if (hasTextOrDescription(anchor)) {
                visibleTiles++;
            }
        }

        if (visibleTiles >= 3) {
            return true;
        }

        if (visibleTiles >= 2 && hasTextOrDescription(HERO_CARD_ANCHORS)) {
            return true;
        }

        // Locale-safe fallback for builds where tile labels are fully localized
        // and no longer expose stable English text/content-desc hooks.
        return isDiscoverVisibleByGeometry();
    }

    public boolean isLanguageSheetVisible() {
        return hasTextOrDescription(LANGUAGE_SHEET_ANCHORS);
    }

    public boolean isSignInDialogVisible() {
        return hasTextOrDescription(SIGN_IN_DIALOG_ANCHORS);
    }

    public boolean isTourDialogVisible() {
        return hasTextOrDescription(TOUR_DIALOG_ANCHORS);
    }

    public boolean dismissLanguageSheet() throws Exception {
        if (!isLanguageSheetVisible()) {
            return false;
        }
        if (clickByDescription("Close", "Dismiss")) {
            return true;
        }
        mDevice.pressBack();
        settle();
        return true;
    }

    public boolean continueSignInIfPresent() throws Exception {
        if (!isSignInDialogVisible()) {
            return false;
        }
        if (clickByTextOrDescription("Continue as", "Continue")) {
            return true;
        }
        mDevice.pressBack();
        settle();
        return true;
    }

    public boolean dismissTourIfPresent() throws Exception {
        if (!isTourDialogVisible()) {
            return false;
        }
        if (clickByTextOrDescription("Remind me later",
                "Cancel",
                "Not now",
                "Maybe later",
                "Close")) {
            return true;
        }
        mDevice.pressBack();
        settle();
        return true;
    }

    public QiraCreatorZonePage startTourIfPresent() throws Exception {
        if (!isTourDialogVisible()) {
            return null;
        }
        if (!clickByTextOrDescription("Start")) {
            return null;
        }
        return new QiraCreatorZonePage(mDevice, mConfig).waitForLoaded();
    }

    public QiraComposerPage openComposer() throws Exception {
        if (clickByTextOrDescription(COMPOSER_ENTRY_LABELS)) {
            return new QiraComposerPage(mDevice, mConfig).waitForLoaded();
        }

        throw new IllegalStateException("Unable to open the Qira composer");
    }

    public QiraSurfacePage openCatchMeUp() throws Exception {
        return openFeature(CATCH_ME_UP_LABELS);
    }

    public QiraSurfacePage openPayAttention() throws Exception {
        return openFeature(PAY_ATTENTION_LABELS);
    }

    public QiraSurfacePage openRememberThis() throws Exception {
        return openFeature(MEMORY_LABELS);
    }

    public QiraCreatorZonePage openCreatorZone() throws Exception {
        if (!clickByTextOrDescriptionWithScroll("Creator Zone")) {
            return null;
        }
        return new QiraCreatorZonePage(mDevice, mConfig).waitForLoaded();
    }

    public QiraKnowledgePage openKnowledge() throws Exception {
        if (!clickByTextOrDescriptionWithScroll("Knowledge")) {
            return null;
        }
        return new QiraKnowledgePage(mDevice, mConfig).waitForLoaded();
    }

    public QiraDrawerPage openDrawer() throws Exception {
        // The drawer "Menu" affordance carries a localized content-description
        // (e.g. ar "القائمة"), so clickByStableDescription only resolves it on
        // en-XM. Every other locale falls through to the geometric corner tap -
        // which MUST mirror for RTL: on ar-* the home top bar puts Menu at the
        // top-right and the profile avatar at the top-left, so the LTR top-left
        // fallback taps the avatar and opens the Account page instead of the
        // drawer (root cause of the ar-EG "Unable to open the Qira navigation
        // drawer" failures). Pick the correct top corner by layout direction.
        boolean rtl = isCurrentLocaleRtl();
        if (clickByStableDescription("Menu", "Open navigation drawer")
                || (rtl ? clickTopRightClickable() : clickTopLeftClickable())) {
            return new QiraDrawerPage(mDevice, mConfig).waitForLoaded();
        }
        return null;
    }

    private boolean isCurrentLocaleRtl() {
        try {
            return avik.qira.utils.QiraStrings.getInstance().isCurrentLocaleRtl();
        } catch (Throwable t) {
            return false;
        }
    }

    public QiraHistoryPage openHistory() throws Exception {
        if (clickByTextOrDescriptionWithScroll(HISTORY_LABELS)) {
            QiraHistoryPage page = new QiraHistoryPage(mDevice, mConfig);
            try {
                return page.waitForLoaded();
            } catch (IllegalStateException ignored) {
                // Locale variants can translate every visible anchor on the
                // history surface. Return the page object so callers can apply
                // their own locale-safe waits/fallbacks instead of hard-failing
                // at this helper boundary.
                return page;
            }
        }

        if (clickByStableDescription("Menu", "More options", "Open navigation drawer")) {
            if (clickByTextOrDescription(HISTORY_LABELS)) {
                QiraHistoryPage page = new QiraHistoryPage(mDevice, mConfig);
                try {
                    return page.waitForLoaded();
                } catch (IllegalStateException ignored) {
                    return page;
                }
            }
            mDevice.pressBack();
            settle();
        }

        return null;
    }

    private QiraSurfacePage openFeature(String[] labels) throws Exception {
        if (clickByTextOrDescription(labels)) {
            return new QiraSurfacePage(mDevice, mConfig).waitForLoaded(labels);
        }
        return null;
    }

    /**
     * Geometry fallback for the Discover home tile grid.
     *
     * <p>The Discover surface renders a two-column grid of large clickable cards
     * above the bubble bar. Creator Zone, by contrast, renders a 3-column grid.
     * We use this column-layout difference to avoid false positives.
     */
    private boolean isDiscoverVisibleByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();

        int leftColumn = 0;
        int rightColumn = 0;
        int middleColumn = 0;
        int tileCount = 0;

        List<UiObject2> clickables = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true));
        for (UiObject2 object : clickables) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Restrict to large feature cards in the content area.
                if (bounds.top < (height * 10) / 100 || bounds.bottom > (height * 93) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 18) / 100 || bounds.width() > (width * 56) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 9) / 100 || bounds.height() > (height * 34) / 100) {
                    continue;
                }

                tileCount++;
                int cx = bounds.centerX();
                if (cx < (width * 45) / 100) {
                    leftColumn++;
                } else if (cx > (width * 55) / 100) {
                    rightColumn++;
                } else {
                    middleColumn++;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled during scan.
            }
        }

        return tileCount >= 4
                && leftColumn >= 2
                && rightColumn >= 2
                && middleColumn <= 1;
    }
}
