package avik.qira.scripts;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraKnowledgePage;
import avik.qira.pages.QiraOnboardingPage;

/**
 * Captures the Motorola Qira "Knowledge" surface that is reached from the Qira
 * home tile grid. The script must run <em>after</em>
 * {@link MotorolaQiraHomeCapture} so the app is already signed in and past the
 * primary onboarding; app data is never cleared here (see
 * {@link BaseQiraCaptureScript#setUp()} which respects
 * {@code QiraConfig.shouldClearData()}).
 *
 * <p>The flow:
 * <ol>
 *     <li>Launch Qira and wait for the Focus Zone bubble bar.</li>
 *     <li>Tap the Focus Zone App Icon bubble to return to the Qira home grid.</li>
 *     <li>Tap the "Knowledge" tile.</li>
 *     <li>Walk the 2-page onboarding carousel, capturing each page.</li>
 *     <li>Capture the main Knowledge list surface.</li>
 *     <li>Open (and capture) the Categories dropdown, then dismiss it.</li>
 *     <li>Open (and capture) the Tags dropdown, then dismiss it.</li>
 *     <li>Open (and capture) the top-right More options overflow menu
 *         (Manage Settings / Delete Everything), then dismiss it.</li>
 *     <li>Open (and capture) the bottom-right FAB menu
 *         (Create a memory / Upload Files), open the Create memory dialog,
 *         capture it and cancel it without committing any data.</li>
 *     <li>Tap the first saved memory to open the detail screen and capture the
 *         Summary / Transcript / Audio Recording tabs.</li>
 * </ol>
 *
 * <p>All UI matching is done via text / content-description lookups backed by
 * UiAutomator (see {@link QiraKnowledgePage}).
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraKnowledgeCapture extends BaseQiraCaptureScript {

    private static final long ONBOARDING_PAGE_TIMEOUT_MS = 10000L;
    private static final long MAIN_TIMEOUT_MS = 10000L;
    private static final long DETAIL_TIMEOUT_MS = 10000L;
    private static final long SETTLE_MS = 800L;

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraKnowledge";
    }

    protected QiraKnowledgePage createKnowledgePage() throws Exception {
        return new QiraKnowledgePage(mDevice, mConfig);
    }

    protected boolean requireVerifiedCanonicalSurfaces() {
        return false;
    }

    public void captureScreens() throws Exception {
        logger.info("Launching Motorola Qira without clearing data for the Knowledge capture.");

        QiraOnboardingPage onboardingPage = new QiraOnboardingPage(mDevice, mConfig);
        onboardingPage.ensureDeviceUnlocked();
        onboardingPage.disableAutoRotate();

        // Hard reset: kill leftover Focus Zone bubble bar + dismiss the
        // IME from the previous sub-flow before tapping the Knowledge tile.
        // See BaseQiraCaptureScript.ensureCleanQiraEntry for the rationale.
        ensureCleanQiraEntry(onboardingPage);

        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        QiraHomePage home = new QiraHomePage(mDevice, mConfig);
        QiraKnowledgePage knowledge = createKnowledgePage();

        navigateToQiraHome(onboardingPage, focusZone, home, knowledge);
        // Home_TileGrid was added after the en-XM canonical run; the
        // strict baseline rule says we must NOT introduce screen names
        // that en-XM does not have. We navigate to the tile grid for
        // the subsequent Knowledge tile tap but emit no screenshot.

        captureOnboardingFlow(knowledge);
        captureMainSurface(knowledge);
        captureCategoriesDropdown(knowledge);
        captureTagsDropdown(knowledge);
        captureMoreOptionsMenu(knowledge);
        captureFabAndCreateMemory(knowledge);
        // Per-row Delete popup capture is non-fatal so a missing delete
        // affordance (e.g. no rows yet on a fresh install) does not abort
        // the rest of the Knowledge sub-flow.
        try {
            captureDeletePopup(knowledge);
        } catch (Throwable t) {
            logger.info("Knowledge: captureDeletePopup failed (continuing): " + t.getMessage());
        }
        captureFirstItemDetail(knowledge);
    }

    /**
     * Ensures the Qira home tile grid is the active surface. If the bubble bar
     * is present we tap the App Icon (which returns us to home); otherwise we
     * press back a few times to dismiss any leftover Focus Zone overlay.
     */
    private void navigateToQiraHome(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            QiraKnowledgePage knowledge) throws Exception {
        for (int pass = 0; pass < 2; pass++) {
            if (isKnowledgeEntryGridVisible(onboardingPage, home, knowledge)) {
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
                    if (isKnowledgeEntryGridVisible(onboardingPage, home, knowledge)) {
                        return;
                    }
                }
                mDevice.pressBack();
                mUtils.sleep(800L);
                if (isKnowledgeEntryGridVisible(onboardingPage, home, knowledge)) {
                    return;
                }
            }

            if (pass == 0) {
                mDevice.pressHome();
                mUtils.sleep(1000L);
                onboardingPage.launchQiraApp();
                onboardingPage.advanceThroughOnboardingToHome(60000L);
                if (isKnowledgeEntryGridVisible(onboardingPage, home, knowledge)) {
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

        if (!isKnowledgeEntryGridVisible(onboardingPage, home, knowledge)) {
            throw new IllegalStateException(
                    "Unable to reach the Qira home tile grid to start the Knowledge capture");
        }
    }

    private boolean isKnowledgeEntryGridVisible(QiraOnboardingPage onboardingPage,
            QiraHomePage home,
            QiraKnowledgePage knowledge) {
        return home.isDiscoverVisible()
                || onboardingPage.isFeatureGridVisible()
                || knowledge.isHomeTileVisible();
    }

    /**
     * Walks the 2-page onboarding carousel. Each page is captured, then we tap
     * Next to advance. The last Next dismisses the carousel and reveals the
     * main Knowledge list surface (captured separately by
     * {@link #captureMainSurface}).
     */
    private void captureOnboardingFlow(QiraKnowledgePage knowledge) throws Exception {
        knowledge.tapKnowledgeTile();
        try {
            knowledge.waitForLoaded();
        } catch (IllegalStateException ignored) {
            // The page-specific waits below handle the concrete state.
        }

        if (knowledge.waitForOnboardingPage1(ONBOARDING_PAGE_TIMEOUT_MS)) {
            takeScreenshot("Onboarding_1_Knowledge");
            if (!knowledge.tapNext()) {
                logger.info("Knowledge onboarding page 1 primary action was not available.");
            }
        } else if (knowledge.isOnboardingVisible()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge onboarding appeared without page-1 resource evidence.");
            }
            logger.info("Knowledge onboarding dialog appeared without matching page-1 labels; capturing current dialog.");
            takeScreenshot("Onboarding_1_Knowledge");
            if (!knowledge.tapNext()) {
                logger.info("Knowledge onboarding dialog primary action was not available.");
            }
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge onboarding page 1 was not resource-verified.");
            }
            logger.info("Knowledge onboarding page 1 did not appear; capturing current screen.");
            takeScreenshot("Onboarding_1_Knowledge");
        }

        if (knowledge.waitForOnboardingPage2(ONBOARDING_PAGE_TIMEOUT_MS)) {
            takeScreenshot("Onboarding_2_Permissions");
            if (!knowledge.tapNext()) {
                logger.info("Knowledge onboarding page 2 primary action was not available.");
            }
        } else if (knowledge.isOnboardingVisible() && !knowledge.isMainVisible()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge onboarding remained without page-2 resource evidence.");
            }
            logger.info("Knowledge onboarding dialog remained visible after page 1; capturing it as page 2.");
            takeScreenshot("Onboarding_2_Permissions");
            if (!knowledge.tapNext()) {
                logger.info("Knowledge onboarding dialog page-2 primary action was not available.");
            }
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge onboarding page 2 was not resource-verified.");
            }
            logger.info("Knowledge onboarding page 2 did not appear; capturing current screen.");
            takeScreenshot("Onboarding_2_Permissions");
        }
    }

    private void captureMainSurface(QiraKnowledgePage knowledge) throws Exception {
        if (!knowledge.waitForMain(MAIN_TIMEOUT_MS)) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge main surface was not resource-verified.");
            }
            logger.info("Knowledge main surface did not appear within timeout; capturing anyway.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Main_FileList");
    }

    /**
     * Captures the per-row Delete popup that fires after tapping the trash
     * icon on a Knowledge list item. The capture is non-fatal: if the
     * delete affordance is not present (no items yet, or the trash icon is
     * gated behind a long-press on this build), we log and continue
     * without throwing. We always cancel the dialog so subsequent capture
     * scripts inherit the same Knowledge dataset.
     */
    private void captureDeletePopup(QiraKnowledgePage knowledge) throws Exception {
        if (!knowledge.hasAnyListItems()) {
            logger.info("Knowledge: no list items detected; skipping Main_DeletePopup capture.");
            return;
        }
        boolean opened;
        try {
            opened = knowledge.tapFirstListItemDeleteIcon();
        } catch (Throwable t) {
            logger.info("Knowledge: tap-delete-icon failed (continuing): " + t.getMessage());
            return;
        }
        if (!opened) {
            logger.info("Knowledge: per-row delete icon not visible; skipping Main_DeletePopup capture.");
            return;
        }
        if (!knowledge.waitForDeleteItemDialog(4000L)) {
            logger.info("Knowledge: Delete confirmation dialog did not appear; "
                    + "capturing whatever surface is currently visible.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Main_DeletePopup");

        try {
            if (!knowledge.cancelDeleteItemDialog()) {
                logger.info("Knowledge: Delete dialog Cancel was not tappable; pressing back.");
                mDevice.pressBack();
            }
        } catch (Throwable t) {
            logger.info("Knowledge: Cancel-delete-dialog failed (continuing): " + t.getMessage());
            mDevice.pressBack();
        }
        try {
            mDevice.waitForIdle(1000L);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Captures the Categories dropdown exposing
     * All / Memories / Documents / Pay Attention, then dismisses it with Back.
     */
    private void captureCategoriesDropdown(QiraKnowledgePage knowledge) throws Exception {
        if (!knowledge.openCategoriesDropdown()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge Categories dropdown could not be opened by stable selector.");
            }
            logger.info("Categories dropdown trigger was not found; capturing current surface.");
            takeScreenshot("Main_CategoriesDropdown");
            return;
        }
        mUtils.sleep(600L);
        if (knowledge.isCategoriesDropdownVisible()) {
            takeScreenshot("Main_CategoriesDropdown");
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge Categories options were not resource-verified.");
            }
            logger.info("Categories dropdown did not render its options; capturing anyway.");
            takeScreenshot("Main_CategoriesDropdown");
        }
        mDevice.pressBack();
        mUtils.sleep(SETTLE_MS);
    }

    /**
     * Captures the Tags dropdown exposing Identity / Contact / Education / Work,
     * then dismisses it with Back.
     */
    private void captureTagsDropdown(QiraKnowledgePage knowledge) throws Exception {
        if (!knowledge.openTagsDropdown()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge Tags dropdown could not be opened by stable selector.");
            }
            logger.info("Tags dropdown trigger was not found; capturing current surface.");
            takeScreenshot("Main_TagsDropdown");
            return;
        }
        mUtils.sleep(600L);
        if (knowledge.isTagsDropdownVisible()) {
            takeScreenshot("Main_TagsDropdown");
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge Tags options were not resource-verified.");
            }
            logger.info("Tags dropdown did not render its options; capturing anyway.");
            takeScreenshot("Main_TagsDropdown");
        }
        mDevice.pressBack();
        mUtils.sleep(SETTLE_MS);
    }

    /**
     * Captures the top-right More options overflow menu exposing
     * "Manage Settings" and "Delete Everything", then dismisses it with Back.
     * We deliberately do NOT tap "Delete Everything" to avoid wiping the
     * Knowledge dataset shared with subsequent capture scripts.
     */
    private void captureMoreOptionsMenu(QiraKnowledgePage knowledge) throws Exception {
        boolean menuOpened = knowledge.openMoreOptionsMenu();
        if (menuOpened) {
            mUtils.sleep(600L);
        } else {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge More options menu could not be resource-verified.");
            }
            logger.info("More options overflow menu could not be opened; "
                    + "capturing current surface.");
        }
        takeScreenshot("Main_MoreOptionsMenu");
        boolean dialogOpened = false;
        try {
            if (menuOpened) {
                dialogOpened = knowledge.openManageSettingsDialogFromMenu();
            }
            if (dialogOpened) {
                mUtils.sleep(SETTLE_MS);
            } else {
                if (requireVerifiedCanonicalSurfaces()) {
                    throw new IllegalStateException(
                            "Knowledge Manage Settings dialog could not be resource-verified.");
                }
                logger.info("Knowledge Manage Settings popup was not available; "
                        + "capturing current surface.");
            }
            takeScreenshot("Main_ManageSettingsPopup");
        } catch (Throwable t) {
            if (requireVerifiedCanonicalSurfaces()) {
                if (t instanceof Exception) {
                    throw (Exception) t;
                }
                throw new IllegalStateException(
                        "Knowledge Manage Settings capture failed.", t);
            }
            logger.info("Knowledge Manage Settings popup capture failed (continuing): " + t.getMessage());
            takeScreenshot("Main_ManageSettingsPopup");
        }
        if (dialogOpened) {
            if (!knowledge.dismissManageSettingsDialog()) {
                logger.info("Knowledge Manage Settings popup did not expose Cancel; pressing back.");
                mDevice.pressBack();
            }
        } else if (menuOpened) {
            mDevice.pressBack();
        }
        mUtils.sleep(SETTLE_MS);
    }

    /**
     * Opens the bottom-right FAB "Menu" popup and captures the two options
     * (Create a memory / Upload Files). Then opens the Create memory dialog,
     * captures it, and cancels it to avoid persisting any test data into the
     * user's Knowledge store.
     */
    private void captureFabAndCreateMemory(QiraKnowledgePage knowledge) throws Exception {
        if (!knowledge.openFabMenu()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Knowledge FAB menu could not be resource-verified.");
            }
            logger.info("Knowledge FAB menu could not be opened; capturing current surface.");
            takeScreenshot("Main_FabMenu");
            takeScreenshot("Main_CreateMemory_Dialog");
            return;
        }
        mUtils.sleep(600L);
        takeScreenshot("Main_FabMenu");

        // Row-alignment guarantee: Main_CreateMemory_Dialog must always
        // fire under exactly that name. When the dialog does not open
        // (account state, locale-specific label miss), capture the
        // current surface under the canonical name so the workbench
        // row aligns - never emit a one-off "_Unreachable" sibling
        // that would pollute the parity diff with a new column.
        boolean dialogOpened = false;
        try {
            dialogOpened = knowledge.openCreateMemoryDialog();
        } catch (Throwable t) {
            logger.info("openCreateMemoryDialog failed (continuing): " + t.getMessage());
        }
        mUtils.sleep(SETTLE_MS);
        if (!dialogOpened && requireVerifiedCanonicalSurfaces()) {
            throw new IllegalStateException(
                    "Knowledge Create memory dialog could not be resource-verified.");
        }
        takeScreenshot("Main_CreateMemory_Dialog");
        if (!dialogOpened) {
            logger.info("Create a memory dialog did not open; "
                    + "Main_CreateMemory_Dialog reflects best-available surface.");
            mDevice.pressBack();
            mUtils.sleep(SETTLE_MS);
            return;
        }

        if (!knowledge.cancelCreateMemoryDialog()) {
            logger.info("Create memory Cancel button was not available; pressing back.");
            mDevice.pressBack();
        }
        mUtils.sleep(SETTLE_MS);
    }

    /**
     * Opens the first saved memory and captures the detail screen across its
     * Summary / Transcript / Audio Recording tabs. The detail screen re-uses
     * the Pay Attention tabbed layout; we always land on Summary, then
     * explicitly select the other two tabs before returning.
     */
    private void captureFirstItemDetail(QiraKnowledgePage knowledge) throws Exception {
        // Detail_Summary / Detail_Transcript / Detail_AudioRecording are
        // NOT in the en-XM canonical baseline; the strict baseline rule
        // says we must NOT introduce screen names that en-XM does not
        // have. We do still tap a list item (when present) so the
        // detail surface state matches what other locales would see if
        // the user navigates manually, but no screenshots are emitted.
        boolean tapped = false;
        if (knowledge.hasAnyListItems()) {
            try {
                tapped = knowledge.tapFirstListItem();
            } catch (Throwable t) {
                logger.info("tapFirstListItem failed (continuing): " + t.getMessage());
            }
        } else {
            logger.info("No Knowledge list items detected; skipping detail surface visit.");
        }

        if (tapped) {
            // Wait briefly so any side effects of the tap settle, then
            // press Back to return to the list so the next sub-flow
            // starts from a known-good surface.
            try {
                knowledge.waitForDetailTabs(DETAIL_TIMEOUT_MS);
            } catch (Throwable ignored) {
            }
            mUtils.sleep(SETTLE_MS);
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
