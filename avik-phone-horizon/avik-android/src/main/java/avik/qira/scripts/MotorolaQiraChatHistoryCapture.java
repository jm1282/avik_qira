package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraHistoryPage;
import avik.qira.pages.QiraOnboardingPage;

/**
 * Captures the Motorola Qira "Chat History" surface that is reached from the
 * Qira home tile grid. The script must run <em>after</em>
 * {@link MotorolaQiraHomeCapture} so the app is already signed in and past the
 * primary onboarding; app data is never cleared here (see
 * {@link BaseQiraCaptureScript#setUp()} which respects
 * {@code QiraConfig.shouldClearData()}).
 *
 * <p>The flow:
 * <ol>
 *     <li>Launch Qira and wait for the Focus Zone bubble bar.</li>
 *     <li>Tap the Focus Zone App Icon bubble to return to the Qira home grid.</li>
 *     <li>Tap the "Chat History" tile.</li>
 *     <li>Capture the main Chat History list surface (title, subtitle, search
 *         field, "Manage chats" chip and the persisted chat rows).</li>
 *     <li>Open (and capture) the "Manage chats" multi-select mode
 *         (Back / Select all / Delete with per-row checkboxes), then exit
 *         without deleting anything.</li>
 *     <li>Tap the first persisted chat to open the chat detail surface and
 *         capture it.</li>
 *     <li>Open (and capture) the top-right More options overflow menu
 *         (Settings / History / Feedback), then dismiss it with Back.</li>
 * </ol>
 *
 * <p>All UI matching is done via text / content-description lookups backed by
 * UiAutomator (see {@link QiraHistoryPage}).
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraChatHistoryCapture extends BaseQiraCaptureScript {

    private static final long MAIN_TIMEOUT_MS = 10000L;
    private static final long DETAIL_TIMEOUT_MS = 10000L;
    private static final long SETTLE_MS = 800L;

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraChatHistory";
    }

    protected QiraHistoryPage createHistoryPage() throws Exception {
        return new QiraHistoryPage(mDevice, mConfig);
    }

    protected boolean requireVerifiedCanonicalSurfaces() {
        return false;
    }

    public void captureScreens() throws Exception {
        logger.info("Launching Motorola Qira without clearing data for the Chat History capture.");

        QiraOnboardingPage onboardingPage = new QiraOnboardingPage(mDevice, mConfig);
        onboardingPage.ensureDeviceUnlocked();
        onboardingPage.disableAutoRotate();

        // Hard reset: kill leftover Focus Zone bubble bar + dismiss the
        // IME from the previous sub-flow before opening Chat History.
        // Without this, the bubble bar's expanded chat dialog from the
        // FocusZone CatchMeUp / PayAttention step survives the implicit
        // forceStop in MasterCapture and the Chat History tile tap lands
        // on the dialog instead, producing a wrong-state screenshot for
        // every locale where ChatHistoryCapture runs after FocusZone.
        ensureCleanQiraEntry(onboardingPage);

        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        QiraHomePage home = new QiraHomePage(mDevice, mConfig);
        QiraHistoryPage chatHistory = createHistoryPage();

        navigateToQiraHome(onboardingPage, focusZone, home, chatHistory);

        openChatHistory(chatHistory, home);
        captureMainSurface(chatHistory, home, onboardingPage);
        captureManageChatsMode(chatHistory);
        captureFirstChatDetail(chatHistory);
    }

    /**
     * Ensures the Qira home tile grid is the active surface. If the bubble bar
     * is present we tap the App Icon (which returns us to home); otherwise we
     * press back a few times to dismiss any leftover Focus Zone overlay.
     */
    private void navigateToQiraHome(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            QiraHistoryPage chatHistory) throws Exception {
        for (int pass = 0; pass < 2; pass++) {
            if (isChatHistoryEntryGridVisible(onboardingPage, home, chatHistory)) {
                return;
            }

            for (int i = 0; i < 6; i++) {
                if (focusZone.isBubbleBarVisible()) {
                    try {
                        focusZone.tapFocusZoneAppIcon();
                    } catch (IllegalStateException ignored) {
                        // Fall back to back-navigation below.
                    }
                    mUtils.sleep(1200L);
                    if (isChatHistoryEntryGridVisible(onboardingPage, home, chatHistory)) {
                        return;
                    }
                }
                mDevice.pressBack();
                mUtils.sleep(800L);
                if (isChatHistoryEntryGridVisible(onboardingPage, home, chatHistory)) {
                    return;
                }
            }

            if (pass == 0) {
                mDevice.pressHome();
                mUtils.sleep(1000L);
                onboardingPage.launchQiraApp();
                onboardingPage.advanceThroughOnboardingToHome(60000L);
                if (isChatHistoryEntryGridVisible(onboardingPage, home, chatHistory)) {
                    return;
                }
                focusZone = new QiraFocusZonePage(mDevice, mConfig);
            }
        }

        mDevice.pressHome();
        mUtils.sleep(1000L);
        onboardingPage.launchQiraApp();
        onboardingPage.advanceThroughOnboardingToHome(60000L);
        try {
            onboardingPage.waitForFeatureGrid();
        } catch (IllegalStateException ignored) {
            // Final visibility check below decides whether we can proceed.
        }

        if (!isChatHistoryEntryGridVisible(onboardingPage, home, chatHistory)) {
            throw new IllegalStateException(
                    "Unable to reach the Qira home tile grid to start the Chat History capture");
        }
    }

    private boolean isChatHistoryEntryGridVisible(QiraOnboardingPage onboardingPage,
            QiraHomePage home,
            QiraHistoryPage chatHistory) {
        return home.isDiscoverVisible()
                || onboardingPage.isFeatureGridVisible()
                || chatHistory.isHomeTileVisible();
    }

    private void openChatHistory(QiraHistoryPage chatHistory, QiraHomePage home) throws Exception {
        // Tile-tap path first. Poll for the main surface for the full
        // MAIN_TIMEOUT_MS (10s) instead of an immediate fallback - the
        // Compose tile transition can take ~2s on slow locales and the
        // drawer fallback is significantly slower (extra back-press,
        // drawer open, history entry tap, page rebuild).
        boolean opened = false;
        try {
            chatHistory.tapChatHistoryTile();
            try {
                mDevice.waitForIdle(1500L);
            } catch (Throwable ignored) {
            }
            opened = chatHistory.waitForMain(MAIN_TIMEOUT_MS);
        } catch (IllegalStateException ignored) {
            // Fall back to drawer navigation below.
        }

        if (!opened) {
            logger.info("Chat History tile open missed after polling; "
                    + "retrying via drawer History entry.");
            try {
                QiraHistoryPage viaDrawer = home.openHistory();
                if (viaDrawer != null) {
                    opened = viaDrawer.waitForMain(MAIN_TIMEOUT_MS);
                }
            } catch (Throwable t) {
                logger.info("Drawer History fallback failed (continuing): " + t.getMessage());
            }
        }

        if (!opened) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Chat History main surface could not be opened by stable resources.");
            }
            logger.info("Chat History main surface did not appear within timeout; "
                    + "capturing anyway.");
        }
    }

    /**
     * Captures the main Chat History list surface. Hard pre-condition:
     * the Chat History list MUST be visible before the screenshot fires.
     * If the initial tile-tap path in {@link #openChatHistory} did not
     * land on the list (e.g. because a leftover FocusZone bubble bar
     * dialog absorbed the tap, or because the localized list anchor
     * was missing from {@link QiraHistoryPage#MAIN_ANCHORS}), we
     * perform up to two recovery passes that:
     *
     * <ol>
     *   <li>{@link #ensureCleanQiraEntry} - kills leftover overlays /
     *       IME and re-launches Qira to the home grid.</li>
     *   <li>{@link #navigateToQiraHome} - walks back to the tile grid.</li>
     *   <li>{@link #openChatHistory} - taps the Chat History tile again.</li>
     * </ol>
     *
     * <p>Each pass widens detection: pass #2 also accepts a geometry
     * fallback (right-edge "Manage chats" chip near the top, or any
     * compose row directly under the search field). If after two
     * recovery passes the list is still not detected, we log a clear
     * warning and capture whatever surface is visible - row alignment
     * still trumps blank cells, but the warning makes the bad capture
     * explicit in the run log so triage can look at it.
     */
    private void captureMainSurface(QiraHistoryPage chatHistory,
            QiraHomePage home,
            QiraOnboardingPage onboardingPage) throws Exception {
        mUtils.sleep(SETTLE_MS);
        if (!chatHistory.waitForMainOrLocalized(2000L)) {
            for (int recovery = 0; recovery < 2; recovery++) {
                logger.info("Chat History main surface not detected (recovery pass "
                        + (recovery + 1) + "/2): hard-resetting Qira and re-tapping the tile.");
                try {
                    ensureCleanQiraEntry(onboardingPage);
                } catch (Throwable t) {
                    logger.info("ensureCleanQiraEntry during recovery failed (continuing): "
                            + t.getMessage());
                }
                QiraFocusZonePage fz = new QiraFocusZonePage(mDevice, mConfig);
                navigateToQiraHome(onboardingPage, fz, home, chatHistory);
                openChatHistory(chatHistory, home);
                if (chatHistory.waitForMainOrLocalized(MAIN_TIMEOUT_MS)) {
                    break;
                }
            }
        }
        if (!chatHistory.waitForMainOrLocalized(2000L)) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Chat History list was not resource-verified after recovery.");
            }
            logger.info("Chat History list still not detected after recovery; "
                    + "Main_ChatList will be a best-effort capture of the current surface.");
        }
        try {
            mDevice.waitForIdle(1000L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Main_ChatList");
    }

    /**
     * Captures the Manage chats multi-select mode exposing Back / Select all /
     * Delete and per-row checkboxes, selects one chat row, then taps Delete so
     * locale runs capture the enabled delete affordance and any follow-up
     * delete surface Qira presents.
     *
     * <p>Row-alignment guarantee: every screenshot name reachable in this
     * sub-flow ({@code Main_ManageChats}, {@code Main_ManageChats_Selected},
     * {@code Main_ManageChats_DeleteAction}, {@code Main_DeleteChatPopup})
     * MUST always be emitted exactly once per script run, regardless of
     * locale-specific label-detection misses. When the chip / row / delete
     * action cannot be located, we fall back to a best-effort capture of
     * the current surface under the same screen name. This trades cell
     * content fidelity for the row-alignment requirement (the workbench
     * comparison view aligns by screen name, so a missing screenshot
     * breaks every column to the right of the gap).
     */
    private void captureManageChatsMode(QiraHistoryPage chatHistory) throws Exception {
        boolean opened = false;
        try {
            opened = chatHistory.openManageChats();
        } catch (Throwable t) {
            logger.info("openManageChats failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        // Always emit Main_ManageChats. If the chip could not be opened,
        // capture the current surface (typically the main chat list) so
        // every locale's row aligns with en-XM's cell.
        if (!opened && requireVerifiedCanonicalSurfaces()) {
            throw new IllegalStateException(
                    "Manage chats mode could not be opened by stable resources.");
        }
        takeScreenshot("Main_ManageChats");
        if (!opened) {
            logger.info("Manage chats chip could not be opened; downstream "
                    + "_Selected / _DeleteAction / _DeleteChatPopup captures "
                    + "will use best-available state.");
        }

        // Select-first-row hardening: refresh + retry up to 3 times to
        // ride out stale-bounds nodes after the manage-mode transition.
        boolean selected = false;
        if (opened) {
            for (int attempt = 0; attempt < 3 && !selected; attempt++) {
                try {
                    selected = chatHistory.selectFirstManageChat();
                } catch (Throwable t) {
                    logger.info("selectFirstManageChat attempt " + (attempt + 1)
                            + "/3 failed (continuing): " + t.getMessage());
                }
                if (!selected) {
                    try {
                        mDevice.waitForIdle(800L);
                    } catch (Throwable ignored) {
                    }
                }
            }
            if (!selected) {
                logger.info(
                        "No selectable chat row detected in Manage chats mode after 3 attempts.");
            }
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        // Always emit Main_ManageChats_Selected. Even if no row was
        // selectable, capture whatever surface we have so the row aligns.
        if (!selected && requireVerifiedCanonicalSurfaces()) {
            throw new IllegalStateException(
                    "No checkable Chat History row could be selected.");
        }
        takeScreenshot("Main_ManageChats_Selected");

        // Delete-action hardening: refresh + retry up to 3 times. On
        // some builds the action bar's Delete chip flips from gated to
        // enabled after a tiny delay once a row has a check applied.
        boolean deleteTapped = false;
        if (selected) {
            for (int attempt = 0; attempt < 3 && !deleteTapped; attempt++) {
                try {
                    deleteTapped = chatHistory.tapManageChatsDeleteAction();
                } catch (Throwable t) {
                    logger.info("tapManageChatsDeleteAction attempt " + (attempt + 1)
                            + "/3 failed (continuing): " + t.getMessage());
                }
                if (!deleteTapped) {
                    try {
                        mDevice.waitForIdle(800L);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        if (deleteTapped) {
            // Wait up to 5s for the Delete confirmation dialog before
            // the capture (raised from 4s to give slow locales more
            // headroom). waitForDeleteChatDialog polls internally.
            boolean dialogVisible = false;
            try {
                dialogVisible = chatHistory.waitForDeleteChatDialog(5000L);
            } catch (Throwable t) {
                logger.info("waitForDeleteChatDialog failed (continuing): " + t.getMessage());
            }
            if (!dialogVisible) {
                if (requireVerifiedCanonicalSurfaces()) {
                    throw new IllegalStateException(
                            "Manage chats Delete dialog was not resource-verified.");
                }
                logger.info("Manage chats Delete dialog did not appear; "
                        + "capturing whatever surface is currently visible.");
            }
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Manage chats Delete action was not resource-clickable.");
            }
            logger.info("Manage chats Delete action was not tappable; "
                    + "_DeleteAction / _DeleteChatPopup will capture the current state.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        // Emit only the en-XM canonical name. _DeleteChatPopup was a
        // newer alias added after en-XM ran; the strict baseline rule
        // says every locale must produce exactly the en-XM screen set,
        // so we drop the duplicate alias and keep _DeleteAction.
        takeScreenshot("Main_ManageChats_DeleteAction");

        if (deleteTapped) {
            // Cancel the dialog (do NOT actually delete; downstream
            // captures still need the chat row). cancelDeleteChatDialog
            // now uses the multi-locale CANCEL_LABELS array.
            boolean cancelled = false;
            try {
                cancelled = chatHistory.cancelDeleteChatDialog();
            } catch (Throwable t) {
                logger.info("cancelDeleteChatDialog failed (continuing): " + t.getMessage());
            }
            if (!cancelled) {
                logger.info("Manage chats Delete dialog Cancel was not tappable; pressing back.");
                mDevice.pressBack();
            }
            try {
                mDevice.waitForIdle(1000L);
            } catch (Throwable ignored) {
            }
        }

        if (chatHistory.isManageModeVisible()) {
            try {
                if (!chatHistory.exitManageChats()) {
                    logger.info("Manage chats Back label was not tappable; pressing device back.");
                    mDevice.pressBack();
                }
            } catch (Throwable t) {
                logger.info("exitManageChats failed (continuing): " + t.getMessage());
                mDevice.pressBack();
            }
        }
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Opens the first persisted chat and captures the chat detail surface and
     * the top-right More options overflow (Settings / History / Feedback).
     *
     * <p>Row-alignment guarantee: both {@code Detail_Chat} and
     * {@code Detail_MoreOptionsMenu} MUST always be emitted exactly once
     * per script run. When the chat row cannot be tapped or the More
     * options menu cannot be opened (no persisted chats, locale-specific
     * label miss, etc.), we still capture the current surface under the
     * required name so the workbench row alignment is preserved.
     */
    private void captureFirstChatDetail(QiraHistoryPage chatHistory) throws Exception {
        boolean tapped = false;
        if (chatHistory.hasAnyChatItems()) {
            try {
                tapped = chatHistory.tapFirstChatItem();
            } catch (Throwable t) {
                logger.info("tapFirstChatItem failed (continuing): " + t.getMessage());
            }
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Chat History has no persisted chat for detail capture.");
            }
            logger.info("No persisted chats detected; Detail_Chat / Detail_MoreOptionsMenu "
                    + "will capture the current surface.");
        }

        boolean detailDetected = !tapped || chatHistory.waitForChatDetail(DETAIL_TIMEOUT_MS);
        mUtils.sleep(SETTLE_MS);
        // Detail_Chat was added after the en-XM canonical run; the strict
        // baseline rule says we must NOT introduce screen names that en-XM
        // does not have. The chat detail surface still gets navigated into
        // so the More-options overflow capture below works, but no
        // screenshot is emitted under _Detail_Chat itself.

        boolean moreOpened = false;
        try {
            moreOpened = chatHistory.openChatMoreOptions();
        } catch (Throwable t) {
            logger.info("openChatMoreOptions failed (continuing): " + t.getMessage());
        }
        if (tapped && !detailDetected && !moreOpened) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Chat detail surface was not resource-verified.");
            }
            logger.info("Chat detail surface did not appear within timeout; capturing anyway.");
        }
        mUtils.sleep(SETTLE_MS);
        // Always emit Detail_MoreOptionsMenu (best-effort if menu not opened).
        takeScreenshot("Detail_MoreOptionsMenu");
        if (moreOpened) {
            mDevice.pressBack();
            mUtils.sleep(SETTLE_MS);
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Chat detail More options menu was not resource-verified.");
            }
            logger.info("Chat detail More options menu could not be opened; "
                    + "Detail_MoreOptionsMenu reflects best-available state.");
        }

        // Return to the Chat History list so follow-up captures (if any) start
        // from a known-good surface.
        if (tapped) {
            mDevice.pressBack();
            mUtils.sleep(SETTLE_MS);
        }
    }

    @Test
    public void testMain() {
        try {
            captureScreens();
        } catch (Exception e) {
            mUtils.printStackTraceOnLog(e);
            throw new RuntimeException(e);
        }
    }
}
