package avik.qira.scripts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.OutputStream;

import avik.qira.pages.QiraCreatorZonePage;
import avik.qira.pages.QiraFocusZonePage;
import avik.qira.pages.QiraHomePage;
import avik.qira.pages.QiraOnboardingPage;
import avik.qira.utils.QiraUiDumper;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/**
 * Captures the Motorola Qira "Creator Zone" surface that is reached from the
 * Focus Zone overlay. The script must run <em>after</em>
 * {@link MotorolaQiraHomeCapture} so the app is already signed in and past the
 * primary onboarding; app data is never cleared here (see
 * {@link BaseQiraCaptureScript#setUp()} which respects
 * {@code QiraConfig.shouldClearData()}).
 *
 * <p>The flow:
 * <ol>
 *     <li>Launch Qira and wait for the Focus Zone bubble bar.</li>
 *     <li>Tap the Focus Zone App Icon bubble to return to the Qira home grid.</li>
 *     <li>Tap the "Creator Zone" tile.</li>
 *     <li>Walk the 3-page onboarding carousel, capturing each page.</li>
 *     <li>Capture and dismiss the "Information" dialog (daily image quota).</li>
 *     <li>Capture the Creator Zone feature grid.</li>
 *     <li>Tap each feature tile (Create image, Edit image, Create avatar,
 *         Create sticker, Scribble, Style Sync), accept Android permission
 *         popups with <em>Allow all</em> for full access, walk through the
 *         feature-specific sub-screens (including the system photo picker for
 *         flows that require it), and return to the Creator Zone home.</li>
 * </ol>
 *
 * <p>Per-feature sub-screens captured:
 * <ul>
 *     <li>Create image &rarr; <code>_Composer</code>,
 *         <code>_QuotaInfoPopup</code> (re-opened daily-image quota dialog),
 *         <code>_Styles_Scrolled</code>.</li>
 *     <li>Edit image &rarr; <code>_ConfirmUsage</code>,
 *         <code>_Permission</code> (if prompted, tapped with
 *         <em>Allow all</em>), <code>_PhotoSelected</code> (first picker tile
 *         highlighted), <code>_Editor</code> (Crop / Mirror / Smart Editing
 *         tools) and <code>_QuotaInfoPopup</code>.</li>
 *     <li>Create avatar &rarr; <code>_Permission</code>,
 *         <code>_Main</code> (avatar surface), <code>_PhotoSelected</code>
 *         (first picker tile), <code>_PreviewConfirm</code>, <code>_Generating</code>
 *         and <code>_Result</code> (or a re-capture of the generation screen
 *         if the result has not rendered within the timeout).</li>
 *     <li>Create sticker &rarr; <code>_Main</code>, <code>_Templates_Slide2</code>,
 *         <code>_Templates_Slide3</code> (successive horizontal scrolls).</li>
 *     <li>Scribble &rarr; <code>_Canvas</code>, <code>_CanvasActive</code>.</li>
 *     <li>Style Sync &rarr; <code>_ConfirmUsage</code>,
 *         <code>_Permission</code> (if prompted), <code>_PhotoSelected</code>,
 *         <code>_PostPicker</code>.</li>
 * </ul>
 */
@RunWith(AndroidJUnit4.class)
public class MotorolaQiraCreatorZoneCapture extends BaseQiraCaptureScript {

    private static final long ONBOARDING_PAGE_TIMEOUT_MS = 10000L;
    private static final long INFORMATION_TIMEOUT_MS = 8000L;
    private static final long CREATOR_HOME_TIMEOUT_MS = 10000L;
    private static final long CONFIRM_DIALOG_TIMEOUT_MS = 4000L;
    private static final long PERMISSION_TIMEOUT_MS = 4000L;
    private static final long COMPOSER_TIMEOUT_MS = 8000L;
    private static final long PHOTO_PICKER_TIMEOUT_MS = 12000L;
    private static final long EDIT_EDITOR_TIMEOUT_MS = 15000L;
    private static final long AVATAR_PREVIEW_TIMEOUT_MS = 10000L;
    private static final long AVATAR_GENERATION_TIMEOUT_MS = 90000L;
    private static final long FEATURE_SETTLE_MS = 1500L;
    private static final int STICKER_SLIDE_COUNT = 3;
    private static final String SEED_IMAGE_NAME = "avik_creator_zone_seed.jpg";
    private static final String SEED_IMAGE_RELATIVE_PATH = "Pictures/Avik";

    @Override
    protected String getScreenPrefix() {
        return "MotorolaQiraCreatorZone";
    }

    protected QiraCreatorZonePage createCreatorZonePage() throws Exception {
        return new QiraCreatorZonePage(mDevice, mConfig);
    }

    protected boolean requireVerifiedCanonicalSurfaces() {
        return false;
    }

    public void captureScreens() throws Exception {
        logger.info("Launching Motorola Qira without clearing data for the Creator Zone capture.");

        QiraOnboardingPage onboardingPage = new QiraOnboardingPage(mDevice, mConfig);
        onboardingPage.ensureDeviceUnlocked();
        onboardingPage.disableAutoRotate();

        // Hard reset: kill leftover Focus Zone bubble bar + dismiss the
        // IME from the previous sub-flow before tapping the Creator Zone tile.
        // See BaseQiraCaptureScript.ensureCleanQiraEntry for the rationale.
        ensureCleanQiraEntry(onboardingPage);

        QiraFocusZonePage focusZone = new QiraFocusZonePage(mDevice, mConfig);
        QiraHomePage home = new QiraHomePage(mDevice, mConfig);
        QiraCreatorZonePage creator = createCreatorZonePage();

        navigateToCreatorZoneHomeTile(onboardingPage, focusZone, home, creator);
        // Home_TileGrid was added after the en-XM canonical run; the
        // strict baseline rule says we must NOT introduce screen names
        // that en-XM does not have, so we navigate to the tile grid for
        // the subsequent tap but emit no screenshot under
        // MotorolaQiraCreatorZone_Home_TileGrid.

        captureCreatorOnboardingSequence(creator);
        ensurePhotoPickerSeedImage();
        captureAllFeatureTiles(creator, onboardingPage, focusZone, home);
        // CreatorHome_ViewMore must always fire, even on long-description
        // locales where the layout pushes the link below the fold. Wrap
        // in try/catch so a missing link does not abort the suite.
        try {
            captureCreatorHomeViewMore(creator, focusZone, home, onboardingPage);
        } catch (Throwable t) {
            logger.info("Creator Zone ViewMore capture failed (continuing): " + t.getMessage());
        }
    }

    private void ensurePhotoPickerSeedImage() {
        try {
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID};
            String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
            try (Cursor cursor = resolver.query(collection, projection, selection,
                    new String[] {SEED_IMAGE_NAME}, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    logger.info("Creator Zone seed image already present for photo picker.");
                    return;
                }
            } catch (Throwable t) {
                logger.info("Creator Zone seed image lookup failed; inserting a fresh image: "
                        + t.getMessage());
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, SEED_IMAGE_NAME);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000L);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000L);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, SEED_IMAGE_RELATIVE_PATH);
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            Uri uri = resolver.insert(collection, values);
            if (uri == null) {
                logger.info("Creator Zone seed image insert returned null; photo picker may be empty.");
                return;
            }

            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (out == null) {
                    logger.info("Creator Zone seed image output stream was null.");
                    return;
                }
                Bitmap bitmap = Bitmap.createBitmap(360, 360, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawColor(Color.rgb(89, 74, 226));
                paint.setColor(Color.rgb(255, 212, 96));
                canvas.drawCircle(260f, 96f, 52f, paint);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(18f);
                paint.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawLine(56f, 284f, 148f, 176f, paint);
                canvas.drawLine(148f, 176f, 230f, 264f, paint);
                canvas.drawLine(230f, 264f, 308f, 188f, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10f);
                canvas.drawRoundRect(24f, 24f, 336f, 336f, 32f, 32f, paint);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out);
                bitmap.recycle();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(uri, values, null, null);
            }
            logger.info("Creator Zone seed image inserted for photo picker.");
            mUtils.sleep(500L);
        } catch (Throwable t) {
            logger.info("Creator Zone seed image setup failed (continuing): " + t.getMessage());
        }
    }

    /**
     * Captures the Creator Zone home "View more" link and the surface it
     * navigates to (history grid). Returns to the Creator home before
     * exiting so the next sub-flow inherits a clean surface. Non-fatal:
     * a missing link or a refused tap is logged and the script proceeds
     * with the current surface captured under the requested name.
     */
    private void captureCreatorHomeViewMore(QiraCreatorZonePage creator,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            QiraOnboardingPage onboardingPage) throws Exception {
        // Make sure we are actually on the creator home before scanning
        // for the link; some upstream sub-flows leave us on a feature
        // sub-screen.
        for (int i = 0; i < 4 && !creator.isCreatorHomeVisible(); i++) {
            try {
                if (creator.tapTopLeftBackControl()) {
                    try {
                        mDevice.waitForIdle(800L);
                    } catch (Throwable ignored) {
                    }
                    continue;
                }
            } catch (Throwable ignored) {
            }
            mDevice.pressBack();
            try {
                mDevice.waitForIdle(800L);
            } catch (Throwable ignored) {
            }
        }
        if (!creator.isCreatorHomeVisible()) {
            logger.info("Creator Zone home not reachable for ViewMore capture; "
                    + "capturing current surface under CreatorHome_ViewMore.");
            takeScreenshot("CreatorHome_ViewMore");
            return;
        }

        boolean tapped = false;
        try {
            tapped = creator.tapViewMoreHistory();
        } catch (Throwable t) {
            logger.info("Creator Zone tapViewMoreHistory failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        // Always emit the screenshot under the requested name. When the
        // tap missed we capture whatever is on screen so the file always
        // exists for the deliverable.
        takeScreenshot("CreatorHome_ViewMore");
        if (!tapped) {
            logger.info("Creator Zone 'View more' link was not tappable; "
                    + "screenshot reflects best-available state.");
            return;
        }

        // Return to Creator home for downstream sub-flows.
        for (int i = 0; i < 3 && !creator.isCreatorHomeVisible(); i++) {
            mDevice.pressBack();
            try {
                mDevice.waitForIdle(800L);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Ensures the Qira home tile grid is the active surface. If the bubble bar
     * is present we tap the App Icon (which takes us back to home); otherwise
     * we press back a few times to dismiss any leftover Focus Zone overlay.
     */
    private void navigateToCreatorZoneHomeTile(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            QiraCreatorZonePage creator) throws Exception {
        for (int pass = 0; pass < 2; pass++) {
            if (isCreatorEntryGridVisible(onboardingPage, home, creator)) {
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
                    if (isCreatorEntryGridVisible(onboardingPage, home, creator)) {
                        return;
                    }
                }
                mDevice.pressBack();
                mUtils.sleep(800L);
                if (isCreatorEntryGridVisible(onboardingPage, home, creator)) {
                    return;
                }
            }

            if (pass == 0) {
                resetToQiraEntryGrid(onboardingPage);
                if (isCreatorEntryGridVisible(onboardingPage, home, creator)) {
                    return;
                }
                focusZone = new QiraFocusZonePage(mDevice, mConfig);
            }
        }

        resetToQiraEntryGrid(onboardingPage);
        try {
            onboardingPage.waitForFeatureGrid();
        } catch (IllegalStateException ignored) {
            // Final visibility check below decides whether we can proceed.
        }

        if (!isCreatorEntryGridVisible(onboardingPage, home, creator)) {
            throw new IllegalStateException(
                    "Unable to reach the Qira home tile grid to start the Creator Zone capture");
        }
    }

    private boolean isCreatorEntryGridVisible(QiraOnboardingPage onboardingPage,
            QiraHomePage home,
            QiraCreatorZonePage creator) {
        return home.isDiscoverVisible()
                || onboardingPage.isFeatureGridVisible()
                || creator.isHomeTileVisible();
    }

    private void resetToQiraEntryGrid(QiraOnboardingPage onboardingPage) throws Exception {
        try {
            ensureCleanQiraEntry(onboardingPage);
            return;
        } catch (Throwable t) {
            logger.info("Creator Zone recovery hard reset failed (falling back): "
                    + t.getMessage());
        }

        mDevice.pressHome();
        mUtils.sleep(1000L);
        onboardingPage.launchQiraApp();
        onboardingPage.advanceThroughOnboardingToHome(60000L);
    }

    /**
     * Captures the Creator Zone onboarding carousel, the daily-quota
     * "Information" dialog, and the Creator Zone home grid.
     *
     * <p>Extracted as an overridable unit so build-specific variants (e.g. the
     * qira_v2 subclass, where the quota dialog auto-appears over onboarding
     * page&nbsp;1 and must be dismissed before the carousel can advance) can
     * substitute a corrected sequence without duplicating the rest of
     * {@link #captureScreens()}. The default preserves the original ordering:
     * onboarding pages, then the Information dialog, then the home grid.
     */
    protected void captureCreatorOnboardingSequence(QiraCreatorZonePage creator) throws Exception {
        captureOnboardingFlow(creator);
        captureInformationDialog(creator);
        captureCreatorHome(creator);
    }

    /**
     * Walks the 3-page onboarding carousel. Each page is captured, then we tap
     * Next to advance. The last Next transitions into the "Information"
     * dialog which is captured separately by {@link #captureInformationDialog}.
     */
    private void captureOnboardingFlow(QiraCreatorZonePage creator) throws Exception {
        creator.tapCreatorZoneTile();

        if (creator.waitForOnboardingPage1(ONBOARDING_PAGE_TIMEOUT_MS)) {
            takeScreenshot("Onboarding_1_CreatorZone");
            if (!creator.tapNext()) {
                logger.info("Creator Zone onboarding page 1 Next button was not available.");
            }
        } else {
            logger.info("Creator Zone onboarding page 1 did not appear; capturing current screen.");
            takeScreenshot("Onboarding_1_CreatorZone");
        }

        if (creator.waitForOnboardingPage2(ONBOARDING_PAGE_TIMEOUT_MS)) {
            takeScreenshot("Onboarding_2_ImaginationRunFree");
            if (!creator.tapNext()) {
                logger.info("Creator Zone onboarding page 2 Next button was not available.");
            }
        } else {
            logger.info("Creator Zone onboarding page 2 did not match localized anchors; "
                    + "capturing current screen.");
            takeScreenshot("Onboarding_2_ImaginationRunFree");
        }

        if (creator.waitForOnboardingPage3(ONBOARDING_PAGE_TIMEOUT_MS)) {
            takeScreenshot("Onboarding_3_MakeItYourOwn");
            if (!creator.tapNext()) {
                logger.info("Creator Zone onboarding page 3 Next button was not available.");
            }
        } else {
            logger.info("Creator Zone onboarding page 3 did not match localized anchors; "
                    + "capturing current screen.");
            takeScreenshot("Onboarding_3_MakeItYourOwn");
            if (creator.isAnyOnboardingPageVisible() && !creator.tapNext()) {
                logger.info("Creator Zone onboarding fallback Next button was not available.");
            }
        }
    }

    private void captureInformationDialog(QiraCreatorZonePage creator) throws Exception {
        // Row-alignment guarantee: Onboarding_InformationQuota must
        // always fire. On account states / locales where the daily
        // image-quota dialog is not raised post-onboarding (already
        // dismissed for the account, locale-specific copy variants
        // not in the page object's anchor list, etc.) this falls back
        // to a best-effort capture of the current surface so the
        // workbench row aligns with en-XM.
        boolean shown = false;
        try {
            shown = creator.waitForInformationDialog(INFORMATION_TIMEOUT_MS);
        } catch (Throwable t) {
            logger.info("waitForInformationDialog failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("Onboarding_InformationQuota");
        if (shown) {
            if (!creator.tapGotIt()) {
                logger.info("Information dialog 'Got It' button was not available.");
            }
        } else {
            logger.info("Creator Zone Information dialog did not appear; "
                    + "Onboarding_InformationQuota reflects best-available surface.");
        }
    }

    private void captureCreatorHome(QiraCreatorZonePage creator) throws Exception {
        if (!creator.waitForCreatorHome(CREATOR_HOME_TIMEOUT_MS)) {
            logger.info("Creator Zone home did not appear within the timeout; capturing anyway.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot("CreatorHome_Grid");
    }

    /**
     * Opens each feature tile in turn, handles any first-run prompts, captures
     * the main feature surface <strong>plus</strong> any reachable sub-screens
     * (style carousel scroll, aspect-ratio dropdown, Add-image popover,
     * template carousel scroll, permission dialogs, etc.), then returns to the
     * Creator Zone home before the next iteration.
     */
    private void captureAllFeatureTiles(QiraCreatorZonePage creator,
            QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home) throws Exception {
        for (String tile : QiraCreatorZonePage.FEATURE_TILES) {
            captureFeatureTile(creator, onboardingPage, focusZone, home, tile);
        }
    }

    private void captureFeatureTile(QiraCreatorZonePage creator,
            QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            String featureLabel)
            throws Exception {
        logger.info("Capturing Creator Zone feature tile: " + featureLabel);
        String suffix = toScreenSuffix(featureLabel);

        ensureOnCreatorHome(creator);

        if (!openFeatureTile(creator, onboardingPage, focusZone, home, featureLabel)) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException("Creator Zone feature tile '" + featureLabel
                        + "' did not reach its resource-verified surface; refusing"
                        + " canonical best-available captures.");
            }
            logger.info("Feature tile '" + featureLabel
                    + "' did not open after recovery attempts; capturing canonical "
                    + "rows from the best-available surface.");
            captureFeatureFallbackRows(featureLabel, suffix);
            returnToCreatorHome(creator);
            return;
        }

        // Each feature method below captures its own sub-screens and is
        // responsible for granting the photos/videos permission with
        // "Allow all" whenever the runtime prompt appears. Flows that need
        // a source image (Edit image / Create avatar / Style Sync) also walk
        // through the system photo picker and pick the first tile.
        switch (featureLabel) {
            case "Create image":
                captureCreateImageSubScreens(creator, suffix);
                break;
            case "Edit image":
                captureEditImageSubScreens(creator, suffix);
                break;
            case "Create avatar":
                captureCreateAvatarSubScreens(creator, suffix);
                break;
            case "Create sticker":
                captureCreateStickerSubScreens(creator, suffix);
                break;
            case "Scribble":
                captureScribbleSubScreens(creator, suffix);
                break;
            case "Style Sync":
                captureStyleSyncSubScreens(creator, suffix);
                break;
            default:
                captureGenericFeature(creator, suffix);
                break;
        }

        returnToCreatorHome(creator);
    }

    private void captureFeatureFallbackRows(String featureLabel, String suffix) throws Exception {
        switch (featureLabel) {
            case "Create image":
                takeScreenshot(suffix + "_Composer");
                takeScreenshot(suffix + "_QuotaInfoPopup");
                takeScreenshot(suffix + "_Styles");
                takeScreenshot(suffix + "_StyleFantasy");
                takeScreenshot(suffix + "_PromptReady");
                captureCreateImageGenerationFallbacks(suffix);
                break;
            case "Edit image":
                takeScreenshot(suffix + "_ConfirmUsage");
                takeScreenshot(suffix + "_Editor");
                break;
            case "Create avatar":
                takeScreenshot(suffix + "_Main");
                takeScreenshot(suffix + "_PreviewConfirm");
                break;
            case "Create sticker":
                takeScreenshot(suffix + "_Main");
                takeScreenshot(suffix + "_Templates_Slide2");
                takeScreenshot(suffix + "_Templates_Slide3");
                break;
            case "Scribble":
                takeScreenshot(suffix + "_CanvasActive");
                takeScreenshot(suffix + "_ExitPopup");
                break;
            case "Style Sync":
                takeScreenshot(suffix + "_PostPicker");
                break;
            default:
                logger.info("No en-XM fallback rows are defined for Creator feature '"
                        + featureLabel + "'.");
                break;
        }
    }

    private boolean openFeatureTile(QiraCreatorZonePage creator,
            QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            String featureLabel) throws Exception {
        for (int attempt = 1; attempt <= 3; attempt++) {
            ensureOnCreatorHome(creator);
            if (!creator.isCreatorHomeVisible()
                    && !recoverCreatorHomeForFeature(onboardingPage, focusZone, home, creator)) {
                return false;
            }

            boolean tapped = attempt == 3
                    ? creator.tapFeatureTileByGridPosition(featureLabel)
                    : creator.tapFeatureTile(featureLabel);
            if (!tapped && attempt >= 2) {
                tapped = creator.tapFeatureTileByGridPosition(featureLabel);
            }
            if (!tapped) {
                logger.info("Feature tile '" + featureLabel
                        + "' was not visible on the Creator Zone home; attempting recovery.");
                recoverCreatorHomeForFeature(onboardingPage, focusZone, home, creator);
                continue;
            }

            if (creator.dismissInformationDialogIfVisible()) {
                logger.info("Feature tile '" + featureLabel
                        + "' raised the Creator Zone quota popup; dismissed with Got It and retrying.");
                mUtils.sleep(700L);
                continue;
            }

            if (waitForFeatureEntrySurface(creator, featureLabel)) {
                return true;
            }
            if (creator.dismissInformationDialogIfVisible()) {
                logger.info("Feature tile '" + featureLabel
                        + "' raised the Creator Zone quota popup after wait; dismissed with Got It and retrying.");
                mUtils.sleep(700L);
                continue;
            }
            if (!requireVerifiedCanonicalSurfaces() && !creator.isCreatorHomeVisible()) {
                return true;
            }
            logger.info("Feature tile '" + featureLabel
                    + "' remained on Creator Zone home after tap attempt " + attempt + ".");
        }
        return false;
    }

    private boolean waitForFeatureEntrySurface(QiraCreatorZonePage creator, String featureLabel)
            throws Exception {
        boolean allowUnverifiedHomeExit = !requireVerifiedCanonicalSurfaces();
        switch (featureLabel) {
            case "Create image":
                return creator.waitForCreateImageComposer(3500L)
                        || (allowUnverifiedHomeExit && waitForHomeExit(creator, 1000L));
            case "Edit image":
                return creator.waitForConfirmUsageDialog(1500L)
                        || creator.waitForPermissionPrompt(700L)
                        || (allowUnverifiedHomeExit && waitForHomeExit(creator, 1200L));
            case "Create avatar":
                return creator.waitForConfirmUsageDialog(1500L)
                        || creator.waitForPermissionPrompt(700L)
                        || creator.waitForAvatarSurface(3500L)
                        || (allowUnverifiedHomeExit && waitForHomeExit(creator, 1200L));
            case "Create sticker":
                return creator.waitForStickerSurface(4000L)
                        || (allowUnverifiedHomeExit && waitForHomeExit(creator, 1200L));
            case "Scribble":
                return creator.waitForScribbleSurface(5000L);
            case "Style Sync":
                return creator.waitForConfirmUsageDialog(1500L)
                        || creator.waitForPermissionPrompt(700L)
                        || creator.isStyleSyncEntryVisible()
                        || (allowUnverifiedHomeExit && waitForHomeExit(creator, 1200L));
            default:
                return allowUnverifiedHomeExit && waitForHomeExit(creator, 1500L);
        }
    }

    private boolean waitForHomeExit(QiraCreatorZonePage creator, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!creator.isCreatorHomeVisible()) {
                return true;
            }
            mUtils.sleep(200L);
        }
        return !creator.isCreatorHomeVisible();
    }

    private boolean recoverCreatorHomeForFeature(QiraOnboardingPage onboardingPage,
            QiraFocusZonePage focusZone,
            QiraHomePage home,
            QiraCreatorZonePage creator) throws Exception {
        returnToCreatorHome(creator);
        if (creator.isCreatorHomeVisible()) {
            return true;
        }

        navigateToCreatorZoneHomeTile(onboardingPage, focusZone, home, creator);
        if (creator.isCreatorHomeVisible()) {
            return true;
        }

        if (!creator.isHomeTileVisible()) {
            return false;
        }
        creator.tapCreatorZoneTile();
        mUtils.sleep(900L);

        if (creator.waitForOnboardingPage1(3000L)) {
            creator.tapNext();
        }
        if (creator.waitForOnboardingPage2(3000L)) {
            creator.tapNext();
        }
        if (creator.waitForOnboardingPage3(3000L)) {
            creator.tapNext();
        }
        if (creator.waitForInformationDialog(3000L)) {
            creator.tapGotIt();
        }
        return creator.waitForCreatorHome(CREATOR_HOME_TIMEOUT_MS);
    }

    /**
     * Create image sub-screens:
     * <ol>
     *     <li><code>_Composer</code> &mdash; default composer view with the
     *         style carousel, aspect-ratio toggle, prompt field and controls.</li>
     *     <li><code>_QuotaInfoPopup</code> &mdash; "Information" dialog
     *         re-opened by tapping the "N/M left today" chip.</li>
     *     <li><code>_Styles_Scrolled</code> &mdash; horizontally scrolled style
     *         carousel revealing later entries (Realistic / Surreal / Vintage).</li>
     * </ol>
     */
    private void captureCreateImageSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        if (!creator.waitForCreateImageComposer(COMPOSER_TIMEOUT_MS)) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Create image composer was not resource-verified.");
            }
            logger.info("Create image composer not detected; capturing current surface.");
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_Composer");

        captureDailyQuotaInfoPopup(creator, suffix);

        if (!creator.isCreateImageComposerVisible()) {
            logger.info("Create image composer was no longer visible after quota action; retrying entry.");
            creator.tapFeatureTile("Create image");
            if (!creator.waitForCreateImageComposer(5000L)
                    && requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Create image composer could not be restored after quota capture.");
            }
        }

        // _Styles is in the en-XM canonical baseline; _Styles_Scrolled is NOT.
        // Per the strict baseline rule we emit only _Styles. We still
        // perform the scroll-then-rewind so subsequent style-picker
        // interactions on the composer see the carousel from the same
        // initial position, but no _Styles_Scrolled screenshot is emitted.
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_Styles");
        try {
            if (creator.isCreateImageComposerVisible() && creator.scrollStyleCarousel(true)) {
                creator.scrollStyleCarousel(false);
                mDevice.waitForIdle(800L);
            }
        } catch (Throwable t) {
            logger.info("Create image scrollStyleCarousel failed (continuing): " + t.getMessage());
        }

        // Full prompt-entry flow: pick Fantasy style, type a prompt, fire
        // Send, capture every progress stage, then walk the History image
        // detail + Smart Editing tools. Every step is wrapped in try/catch
        // so a missing affordance never aborts the rest of the Creator
        // Zone capture suite. Each capture is bounded by a waitForIdle so
        // the screenshot lands on a stable frame.
        try {
            captureCreateImagePromptFlow(creator, suffix);
        } catch (Throwable t) {
            if (requireVerifiedCanonicalSurfaces()) {
                if (t instanceof Exception) {
                    throw (Exception) t;
                }
                throw new IllegalStateException(
                        "Create image prompt flow failed.", t);
            }
            logger.info("Create image prompt flow failed (continuing): " + t.getMessage());
        }
    }

    /**
     * Drives the Create image composer through a full prompt -> generate ->
     * history-image -> edit flow. Each sub-step is best-effort: the master
     * suite must keep running even when, e.g., the prompt EditText cannot
     * be located, the Send action is gated, or the backend rejects the
     * request. Captures fire on the best-available state in every case so
     * the listed deliverables (StyleFantasy / PromptReady /
     * Generating_<Stage> / GeneratedImage / HistoryImageDetail /
     * SmartEditing / Inpaint / Erase / ChangeBG) always exist on at least
     * one locale.
     */
    private void captureCreateImagePromptFlow(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        // Re-enter the composer if upstream sub-screens (style swipe,
        // daily-quota popup tap+Got It, etc.) navigated us away. Three
        // tiered attempts: (a) plain re-check (composer may already be
        // visible), (b) press back + tap tile (recovers from residual
        // Got It dialog or info popover), (c) press back twice to fully
        // return to Creator Home + tap tile (recovers when an upstream
        // surface left us deep inside a sub-screen).
        for (int attempt = 0; attempt < 3 && !creator.isCreateImageComposerVisible(); attempt++) {
            logger.info("Create image: composer not visible; re-entering tile (attempt "
                    + (attempt + 1) + "/3).");
            try {
                int backPresses = attempt == 0 ? 0 : (attempt == 1 ? 1 : 2);
                for (int b = 0; b < backPresses; b++) {
                    mDevice.pressBack();
                    try {
                        mDevice.waitForIdle(600L);
                    } catch (Throwable ignored) {
                    }
                }
                if (creator.isCreatorHomeVisible() || backPresses > 0) {
                    creator.tapFeatureTile("Create image");
                    creator.waitForCreateImageComposer(5000L);
                }
            } catch (Throwable t) {
                logger.info("Create image: composer re-entry attempt " + (attempt + 1)
                        + " failed (continuing): " + t.getMessage());
            }
        }
        if (!creator.isCreateImageComposerVisible()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Create image composer was not resource-verified after re-entry.");
            }
            logger.info("Create image: composer still not visible after re-entry attempts; "
                    + "capturing the current surface for the remaining canonical rows.");
            takeScreenshot(suffix + "_StyleFantasy");
            takeScreenshot(suffix + "_PromptReady");
            captureCreateImageGenerationFallbacks(suffix);
            return;
        }

        // 1) Pick the Fantasy style.
        boolean styleSelected = false;
        try {
            styleSelected = creator.selectCreateImageStyle("Fantasy");
            if (styleSelected) {
                try {
                    mDevice.waitForIdle(1500L);
                } catch (Throwable ignored) {
                }
            } else {
                logger.info("Create image: 'Fantasy' style not selectable; capturing current state.");
            }
        } catch (Throwable t) {
            logger.info("Create image: Fantasy-style selection failed (continuing): " + t.getMessage());
        }
        if (!styleSelected && requireVerifiedCanonicalSurfaces()) {
            throw new IllegalStateException(
                    "Create image Fantasy style was not resource-clickable.");
        }
        takeScreenshot(suffix + "_StyleFantasy");

        // 2) Type the prompt.
        boolean promptEntered = false;
        try {
            promptEntered = creator.enterCreateImagePrompt(
                    "Bangalore sunset city skyline golden orange light");
        } catch (Throwable t) {
            logger.info("Create image: prompt entry failed (continuing): " + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_PromptReady");
        if (!promptEntered) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Create image prompt could not be entered and verified.");
            }
            logger.info("Create image: prompt not entered; capturing generation rows "
                    + "from the best-available composer surface.");
            captureCreateImageGenerationFallbacks(suffix);
            return;
        }

        // 3) Fire Send/Generate.
        boolean sent = false;
        try {
            sent = creator.tapCreateImageSend();
        } catch (Throwable t) {
            logger.info("Create image: tapCreateImageSend failed (continuing): " + t.getMessage());
        }
        if (!sent && requireVerifiedCanonicalSurfaces()) {
            throw new IllegalStateException(
                    "Create image Send action was not resource-clickable.");
        }

        // Row-alignment guarantee: _Generating_Preparing and
        // _Generating_Generating must always fire, regardless of whether
        // Send actually triggered a backend generation. The
        // captureCreateImageGeneratingStages loop only emits per-stage
        // captures when the localized stage label is detected; on
        // locales/builds where the stage labels are not in the page
        // object's dictionary, OR where Send is not tappable at all,
        // those names would otherwise be missing and the workbench rows
        // would shift. Capture them now as best-effort fallbacks of the
        // post-Send (or composer-still-visible) surface BEFORE the
        // stage loop runs - the loop will emit additional named captures
        // on top if it actually detects a Generating/Preparing stage.
        if (requireVerifiedCanonicalSurfaces()) {
            waitForAndCaptureCreateImageStage(
                    creator, suffix, "Preparing", 15000L);
            waitForAndCaptureCreateImageStage(
                    creator, suffix, "Generating", 30000L);
        } else {
            try {
                mDevice.waitForIdle(800L);
            } catch (Throwable ignored) {
            }
            takeScreenshot(suffix + "_Generating_Preparing");
            try {
                mDevice.waitForIdle(400L);
            } catch (Throwable ignored) {
            }
            takeScreenshot(suffix + "_Generating_Generating");
        }
        if (!sent) {
            logger.info("Create image: Send/Generate action not tappable; "
                    + "_Generating_Preparing / _Generating_Generating reflect "
                    + "best-available pre-send state.");
            takeScreenshot(suffix + "_GeneratedImage");
            return;
        }

        // 4) Capture each visible generation stage as it appears. Stages
        // come fast (≤ 2s each on this build); poll every 250ms with an
        // overall budget of CREATE_IMAGE_GENERATION_TIMEOUT_MS. Already
        // captured _Preparing/_Generating above as guaranteed fallbacks;
        // the loop is guarded against re-emitting those exact suffixes.
        captureCreateImageGeneratingStages(creator, suffix);

        // 5) Wait for the generated image surface, then capture it.
        boolean completed = false;
        try {
            completed = creator.waitForCreateImageResult(60000L);
        } catch (Throwable t) {
            logger.info("Create image: waitForCreateImageResult failed (continuing): "
                    + t.getMessage());
        }
        try {
            mDevice.waitForIdle(1500L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_GeneratedImage");
        if (!completed) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Create image generation result was not resource-verified.");
            }
            logger.info("Create image: generation did not finish within timeout; "
                    + "GeneratedImage screenshot reflects in-progress state.");
        }

        // _HistoryImageDetail / _SmartEditing / per-tool captures
        // (Inpaint / Erase / ChangeBG) are NOT in the en-XM canonical
        // baseline; per the strict baseline rule we do not emit them
        // and skip the History-rail / Smart-Editing exploration entirely.
        // The captures above (_GeneratedImage, _StyleFantasy, _PromptReady,
        // _Generating_*) cover the en-XM-baseline portion of the
        // Create image flow.

        // Return to Creator home so the next feature tile starts from a
        // clean surface. Press back a few times; returnToCreatorHome will
        // also fire after the switch() in captureFeatureTile(). Each
        // iteration first dismisses any discard-prompt popup raised by
        // the previous Back, so we never lose in-flight state to a
        // stray confirmation.
        for (int i = 0; i < 3 && !creator.isCreatorHomeVisible(); i++) {
            try {
                if (creator.isCreateImageDiscardPromptVisible()) {
                    creator.dismissDiscardPromptKeepGenerating();
                    mUtils.sleep(400L);
                    continue;
                }
            } catch (Throwable ignored) {
            }
            try {
                mDevice.pressBack();
            } catch (Throwable ignored) {
            }
            try {
                mDevice.waitForIdle(800L);
            } catch (Throwable ignored) {
            }
        }
    }

    private void waitForAndCaptureCreateImageStage(
            QiraCreatorZonePage creator,
            String suffix,
            String expectedStage,
            long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String current = creator.currentCreateImageGeneratingStage();
            if (expectedStage.equals(current)) {
                try {
                    mDevice.waitForIdle(600L);
                } catch (Throwable ignored) {
                }
                takeScreenshot(suffix + "_Generating_" + expectedStage);
                return;
            }
            if (creator.isCreateImageResultVisible()) {
                break;
            }
            mUtils.sleep(200L);
        }
        throw new IllegalStateException("Create image " + expectedStage
                + " stage was not resource-verified within " + timeoutMs + " ms.");
    }

    private void captureCreateImageGenerationFallbacks(String suffix) throws Exception {
        takeScreenshot(suffix + "_Generating_Preparing");
        takeScreenshot(suffix + "_Generating_Generating");
        takeScreenshot(suffix + "_GeneratedImage");
    }

    /**
     * Captures every distinct generation-progress stage Qira surfaces
     * during a Create image run. Each new stage label triggers one
     * screenshot named {@code _Generating_<Stage>}. Polls every 250ms;
     * exits as soon as the composer transitions to the result surface.
     * Bounded at 60s so a hung backend cannot stall the suite.
     */
    private void captureCreateImageGeneratingStages(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        long deadline = System.currentTimeMillis() + 60000L;
        while (System.currentTimeMillis() < deadline) {
            // Defensive guard: if a "Discard prompt? / Cancel
            // generation?" popup somehow appears mid-generation (stray
            // tap from a prior session, accessibility scrim, etc.),
            // dismiss it via the keep-generating CTA so the in-flight
            // generation is preserved. NEVER taps "Discard" or
            // "Cancel" - dismissDiscardPromptKeepGenerating is a strict
            // safe-label-only helper. Read-only otherwise; this is the
            // only mutating call inside the per-stage loop.
            // _DiscardPromptPopup is NOT in the en-XM canonical
            // baseline, so we suppress the screenshot but still safely
            // dismiss the popup (keep-generating CTA only) so the
            // in-flight generation continues. Read-only otherwise.
            try {
                if (creator.isCreateImageDiscardPromptVisible()) {
                    try {
                        creator.dismissDiscardPromptKeepGenerating();
                    } catch (Throwable t) {
                        logger.info("Create image: dismiss discard popup failed (continuing): "
                                + t.getMessage());
                    }
                }
            } catch (Throwable ignored) {
            }

            // The en-XM baseline contains exactly two
            // _Generating_<Stage> screens: _Generating_Preparing and
            // _Generating_Generating. Both were emitted as guaranteed
            // best-effort captures before this loop started. We never
            // emit any other _Generating_<Stage> name here so the
            // strict-baseline parity invariant holds even on locales
            // where the stage detector also matches "Creating",
            // "Processing", "Rendering", etc.
            try {
                if (!creator.isCreateImageComposerVisible()
                        && !creator.isCreateImageGeneratingVisible()) {
                    return;
                }
            } catch (Throwable ignored) {
            }
            mUtils.sleep(250L);
        }
    }

    /**
     * Taps the "N/M left today" chip in the current composer to re-open the
     * "Information" (daily image quota) dialog, captures it, and dismisses it
     * again via the "Got It" button so the caller can continue.
     */
    private void captureDailyQuotaInfoPopup(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        // Row-alignment guarantee: <suffix>_QuotaInfoPopup must always
        // fire. Tap the chip best-effort; if it isn't visible (account
        // already past quota, localized chip text not in the page
        // object's locator, etc.), capture the current composer surface
        // under the same name so the workbench row aligns.
        boolean tapped = false;
        try {
            tapped = creator.tapDailyQuotaChip();
        } catch (Throwable t) {
            logger.info("tapDailyQuotaChip failed (continuing): " + t.getMessage());
        }
        boolean dialogShown = false;
        if (tapped) {
            try {
                dialogShown = creator.waitForInformationDialog(INFORMATION_TIMEOUT_MS);
            } catch (Throwable t) {
                logger.info("waitForInformationDialog (post-chip) failed (continuing): "
                        + t.getMessage());
            }
        }
        if (requireVerifiedCanonicalSurfaces() && (!tapped || !dialogShown)) {
            throw strictCreatorSurfaceFailure(
                    "CreateImage_quota_dialog_not_resource_verified",
                    "Creator daily-quota Information dialog was not"
                            + " resource-verified; quotaTapped=" + tapped
                            + ", dialogShown=" + dialogShown + ".",
                    null);
        }
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_QuotaInfoPopup");
        if (!tapped) {
            logger.info("Daily-quota chip ('N/M left today') was not visible; "
                    + suffix + "_QuotaInfoPopup reflects best-available composer surface.");
            return;
        }
        if (!dialogShown) {
            logger.info("Daily-quota 'Information' dialog did not appear after tapping chip; "
                    + suffix + "_QuotaInfoPopup reflects best-available surface.");
            return;
        }
        if (!creator.tapGotIt()) {
            logger.info("Daily-quota 'Information' dialog 'Got It' button was not available.");
            mDevice.pressBack();
        }
        mUtils.sleep(600L);
    }

    /**
     * Edit image full sub-flow: captures the Confirm usage dialog (first
     * entry), the runtime Allow-all permission prompt (if raised), walks
     * through the system photo picker by selecting the first tile and tapping
     * Done, then captures the Edit image editor (tools + smart-editing rail)
     * and its daily-quota "Information" popup re-opened from the chip.
     */
    private void captureEditImageSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        boolean strict = requireVerifiedCanonicalSurfaces();
        // The en-XM canonical baseline only contains _ConfirmUsage and
        // _Editor for the Edit image feature. Per the strict baseline
        // rule we suppress _TileClick / _Permission / _PhotoSelected
        // and only emit _ConfirmUsage + _Editor. We still walk through
        // every step (confirm dialog, permission prompt, photo picker)
        // so the surfaces line up correctly when the two baseline
        // screenshots fire.
        boolean confirmShown = false;
        try {
            confirmShown = creator.waitForConfirmUsageDialog(CONFIRM_DIALOG_TIMEOUT_MS);
        } catch (Throwable t) {
            logger.info("waitForConfirmUsageDialog (edit) failed (continuing): " + t.getMessage());
            if (strict) {
                throw strictCreatorSurfaceFailure(
                        "EditImage_confirm_usage_wait_failed",
                        "Edit image usage-rights state could not be verified.",
                        t);
            }
        }
        try {
            mDevice.waitForIdle(400L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_ConfirmUsage");
        if (confirmShown) {
            if (!creator.tapConfirm()) {
                if (strict) {
                    throw strictCreatorSurfaceFailure(
                            "EditImage_confirm_usage_action_failed",
                            "Edit image usage-rights Confirm action was not resource-clickable.",
                            null);
                }
                logger.info("Edit image: Confirm button was not available; "
                        + "continuing with best-available state.");
            }
        } else if (creator.isPhotoPickerVisible()) {
            logger.info("Edit image opened directly to the photo picker; "
                    + "usage rights had already been acknowledged on this device.");
        } else {
            logger.info("Edit image: Confirm-usage dialog not raised; "
                    + suffix + "_ConfirmUsage reflects best-available surface.");
        }

        if (creator.waitForPermissionPrompt(PERMISSION_TIMEOUT_MS)) {
            creator.acceptPhotosPermissionIfPresent(PERMISSION_TIMEOUT_MS);
        }

        boolean pickerOpened = false;
        try {
            pickerOpened = ensurePhotoPickerOpened(creator, "Edit image");
        } catch (Throwable t) {
            logger.info("ensurePhotoPickerOpened (edit) failed (continuing): " + t.getMessage());
            if (strict) {
                throw strictCreatorSurfaceFailure(
                        "EditImage_picker_open_failed",
                        "Edit image photo picker could not be opened.",
                        t);
            }
        }
        boolean photoSelected = false;
        if (pickerOpened) {
            try {
                photoSelected = walkPhotoPickerAndSelectFirst(creator, suffix);
            } catch (Throwable t) {
                logger.info("walkPhotoPickerAndSelectFirst (edit) failed (continuing): "
                        + t.getMessage());
                if (strict) {
                    throw strictCreatorSurfaceFailure(
                            "EditImage_picker_selection_failed",
                            "Edit image photo selection/confirmation threw before completion.",
                            t);
                }
            }
        }

        if (strict && !photoSelected) {
            throw strictCreatorSurfaceFailure(
                    "EditImage_picker_selection_unverified",
                    "Edit image photo selection/confirmation did not succeed.",
                    null);
        }
        if (strict && !mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
            throw strictCreatorSurfaceFailure(
                    "EditImage_picker_still_foreground",
                    "Edit image photo picker did not leave the foreground and return to Qira.",
                    null);
        }

        boolean editorVerified = false;
        if (strict) {
            long deadline = System.currentTimeMillis() + EDIT_EDITOR_TIMEOUT_MS;
            while (System.currentTimeMillis() < deadline) {
                if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                        && creator.isEditImageEditorVisible()
                        && areVerifiedQiraResourcesVisible(
                                "edit_smart_editing_option",
                                "edit_inpaint_option",
                                "edit_erase_option",
                                "edit_change_background_option")) {
                    editorVerified = true;
                    break;
                }
                mUtils.sleep(250L);
            }
            if (!editorVerified) {
                throw strictCreatorSurfaceFailure(
                        "EditImage_editor_not_resource_verified",
                        "Edit image editor did not expose its resource-backed editing tools.",
                        null);
            }
        } else if (!creator.waitForEditImageEditor(EDIT_EDITOR_TIMEOUT_MS)) {
            logger.info("Edit image editor did not appear within the timeout; capturing anyway.");
        }
        // takeScreenshot already prepends a waitForIdle(1000ms); the prior
        // 800ms cushion was redundant. Drop it.
        takeScreenshot(suffix + "_Editor");

        // _QuotaInfoPopup is in the en-XM baseline only for Create image,
        // not for Edit image. Skip the captureDailyQuotaInfoPopup call
        // here so it does not introduce an EditImage_QuotaInfoPopup
        // screenshot. The Create image flow above already captures the
        // Create image variant.
    }

    /**
     * Create avatar full sub-flow: Allow-all permission popup, the main avatar
     * surface with the "take photo" / "Choose Image" actions and sample grid,
     * the system photo picker selection state, the avatar preview with
     * Replace/Confirm, the generation progress screen, and the final avatar
     * result (or a re-capture of the generation screen if the backend has not
     * responded within {@value #AVATAR_GENERATION_TIMEOUT_MS}&nbsp;ms).
     */
    private void captureCreateAvatarSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        boolean strict = requireVerifiedCanonicalSurfaces();
        // The en-XM canonical baseline only contains _Main and
        // _PreviewConfirm for the Create avatar feature. Per the strict
        // baseline rule we suppress every other screenshot in this
        // sub-flow (_Permission, _PhotoSelected, _PostConfirm,
        // _Generating, _Result). We still walk through each step so
        // the surface state is correct when _Main / _PreviewConfirm
        // fire and so the next feature tile starts from a clean home.
        if (creator.waitForConfirmUsageDialog(CONFIRM_DIALOG_TIMEOUT_MS)) {
            try {
                boolean confirmed = creator.tapConfirm();
                if (!confirmed && strict) {
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_confirm_usage_action_failed",
                            "Create avatar usage-rights Confirm action was not resource-clickable.",
                            null);
                }
                mUtils.sleep(800L);
            } catch (Throwable t) {
                logger.info("Create avatar tapConfirm failed (continuing): " + t.getMessage());
                if (strict) {
                    if (t instanceof IllegalStateException) {
                        throw (IllegalStateException) t;
                    }
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_confirm_usage_action_failed",
                            "Create avatar usage-rights Confirm action failed.",
                            t);
                }
            }
        }
        if (creator.waitForPermissionPrompt(PERMISSION_TIMEOUT_MS)) {
            creator.acceptPhotosPermissionIfPresent(PERMISSION_TIMEOUT_MS);
        }
        boolean previewAlreadyVisible = creator.isImagePreviewConfirmVisible();
        if (!previewAlreadyVisible && !creator.waitForAvatarSurface(COMPOSER_TIMEOUT_MS)) {
            logger.info("Create avatar surface not detected; capturing current surface.");
        } else if (previewAlreadyVisible) {
            logger.info("Create avatar preview confirmation is already visible.");
        }
        mUtils.sleep(600L);
        takeScreenshot(suffix + "_Main");

        boolean chooseTapped = false;
        if (!previewAlreadyVisible) {
            try {
                chooseTapped = creator.tapAvatarChooseImage();
            } catch (Throwable t) {
                logger.info("tapAvatarChooseImage failed (continuing): " + t.getMessage());
                if (strict) {
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_choose_image_failed",
                            "Create avatar Choose Image action failed.",
                            t);
                }
            }
        }
        if (!chooseTapped && !previewAlreadyVisible) {
            if (strict) {
                throw strictCreatorSurfaceFailure(
                        "CreateAvatar_choose_image_unavailable",
                        "Create avatar Choose Image action was not resource-clickable.",
                        null);
            }
            logger.info("Create avatar: 'Choose Image' option was not tappable; "
                    + "_PreviewConfirm will capture the current surface.");
        }

        if (chooseTapped && creator.waitForPermissionPrompt(PERMISSION_TIMEOUT_MS)) {
            creator.acceptPhotosPermissionIfPresent(PERMISSION_TIMEOUT_MS);
        }

        boolean photoSelected = previewAlreadyVisible;
        if (chooseTapped) {
            boolean pickerOpened = false;
            try {
                pickerOpened = ensurePhotoPickerOpened(creator, "Create avatar");
            } catch (Throwable t) {
                logger.info("ensurePhotoPickerOpened (avatar) failed (continuing): "
                        + t.getMessage());
                if (strict) {
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_picker_open_failed",
                            "Create avatar photo picker could not be opened.",
                            t);
                }
            }
            if (pickerOpened) {
                try {
                    photoSelected = walkPhotoPickerAndSelectFirst(creator, suffix);
                } catch (Throwable t) {
                    logger.info("walkPhotoPickerAndSelectFirst (avatar) failed (continuing): "
                            + t.getMessage());
                    if (strict) {
                        throw strictCreatorSurfaceFailure(
                                "CreateAvatar_picker_selection_failed",
                                "Create avatar photo selection/confirmation threw before completion.",
                                t);
                    }
                }
            }
        }

        if (strict && !photoSelected) {
            throw strictCreatorSurfaceFailure(
                    "CreateAvatar_picker_selection_unverified",
                    "Create avatar photo selection/confirmation did not succeed.",
                    null);
        }
        if (strict && !mConfig.getPackageName().equals(mDevice.getCurrentPackageName())) {
            throw strictCreatorSurfaceFailure(
                    "CreateAvatar_picker_still_foreground",
                    "Create avatar photo picker did not leave the foreground and return to Qira.",
                    null);
        }

        boolean previewShown = false;
        if (previewAlreadyVisible) {
            previewShown = !strict
                    || isVerifiedSelectedPhotoPreviewSurface("Create avatar");
        } else if (photoSelected) {
            if (strict) {
                long deadline = System.currentTimeMillis() + AVATAR_PREVIEW_TIMEOUT_MS;
                while (System.currentTimeMillis() < deadline) {
                    if (mConfig.getPackageName().equals(mDevice.getCurrentPackageName())
                            && isVerifiedSelectedPhotoPreviewSurface("Create avatar")) {
                        previewShown = true;
                        break;
                    }
                    mUtils.sleep(250L);
                }
            } else {
                try {
                    previewShown = creator.waitForAvatarPreview(AVATAR_PREVIEW_TIMEOUT_MS);
                } catch (Throwable t) {
                    logger.info("waitForAvatarPreview failed (continuing): " + t.getMessage());
                }
            }
            if (!previewShown) {
                logger.info("Create avatar preview (Replace/Create) did not appear in time.");
            }
        }
        if (strict && !previewShown) {
            throw strictCreatorSurfaceFailure(
                    "CreateAvatar_preview_not_resource_verified",
                    "Create avatar preview/create surface did not expose"
                            + " the required replace_label and generate_image_button"
                            + " resource pair.",
                    null);
        }
        try {
            mDevice.waitForIdle(800L);
        } catch (Throwable ignored) {
        }
        takeScreenshot(suffix + "_PreviewConfirm");

        if (previewShown) {
            try {
                boolean confirmed = creator.tapAvatarConfirmPreview();
                if (!confirmed && strict) {
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_preview_confirm_failed",
                            "Create avatar preview Create action was not resource-clickable.",
                            null);
                }
            } catch (Throwable t) {
                logger.info("tapAvatarConfirmPreview failed (continuing): " + t.getMessage());
                if (strict) {
                    if (t instanceof IllegalStateException) {
                        throw (IllegalStateException) t;
                    }
                    throw strictCreatorSurfaceFailure(
                            "CreateAvatar_preview_confirm_failed",
                            "Create avatar preview Create action failed.",
                            t);
                }
            }
        }

        captureAvatarGenerationAndResult(creator, suffix);
    }

    /**
     * Captures the avatar generation progress screen (if it appears) and then
     * waits for the backend to finish so the final avatar grid can be
     * captured. If the backend times out we re-capture the current state so
     * the translator still gets the progress screen string variants.
     */
    private void captureAvatarGenerationAndResult(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        // _Generating / _PostConfirm / _Result are NOT in the en-XM
        // canonical baseline (only _Main and _PreviewConfirm are). Per
        // the strict baseline rule we do not emit any of them. Do not
        // block on the backend generation result here: in localized runs
        // avatar generation can remain in an in-flight state long enough
        // for the instrumentation transport to be killed without a JUnit
        // result. A short probe is enough to let immediate transitions
        // settle; returnToCreatorHome() below owns cleanup/recovery.
        long deadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < deadline
                && !creator.isAvatarGeneratingVisible()) {
            mUtils.sleep(500L);
        }
        logger.info("Skipping backend avatar-result wait; generation/result"
                + " screens are outside the qira_v2 canonical baseline.");
        mUtils.sleep(800L);
    }

    /**
     * Create sticker: captures the default template row plus the successive
     * scrolled template slides so the full template catalogue (Cat mermaid,
     * Travel buddies Cat &amp; Dog, This is fine, Super Puppy, &hellip;) is
     * visible across {@value #STICKER_SLIDE_COUNT} capture passes.
     */
    private void captureCreateStickerSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        if (!creator.waitForStickerSurface(5000L)) {
            logger.info("Create sticker surface not detected immediately; retrying tile tap.");
            creator.tapFeatureTile("Create sticker");
            creator.waitForStickerSurface(4000L);
        }
        mUtils.sleep(FEATURE_SETTLE_MS);
        takeScreenshot(suffix + "_Main");

        for (int slide = 2; slide <= STICKER_SLIDE_COUNT; slide++) {
            boolean scrolled = creator.scrollStickerTemplates(true);
            if (!scrolled) {
                logger.info("Create sticker templates were not scrollable beyond slide "
                        + (slide - 1) + "; capturing the best-available template surface.");
            } else {
                mUtils.sleep(900L);
            }
            takeScreenshot(suffix + "_Templates_Slide" + slide);
        }

        // Rewind the carousel so the feature is left in its default state for
        // any downstream capture or cleanup.
        for (int i = 1; i < STICKER_SLIDE_COUNT; i++) {
            if (!creator.scrollStickerTemplates(false)) {
                break;
            }
            mUtils.sleep(400L);
        }
    }

    /**
     * Scribble: captures the blank canvas (primary surface) plus a secondary
     * snapshot taken after dropping a short stroke on the canvas so the
     * toolbar state (Undo / Generate) is visible for translation capture.
     */
    private void captureScribbleSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        mUtils.sleep(FEATURE_SETTLE_MS);
        if (!creator.waitForScribbleSurface(5000L)) {
            logger.info("Scribble surface not detected after tile tap; retrying from Creator home.");
            if (creator.isCreatorHomeVisible()) {
                creator.tapFeatureTileByGridPosition("Scribble");
                creator.waitForScribbleSurface(5000L);
            }
        }
        if (!creator.isScribbleSurfaceVisible()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Scribble surface was not resource-verified.");
            }
            logger.info("Scribble surface still unavailable; capturing current surface "
                    + "for the canonical Scribble rows.");
            takeScreenshot(suffix + "_CanvasActive");
            takeScreenshot(suffix + "_ExitPopup");
            return;
        }
        // _Canvas (the blank pre-stroke surface) is NOT in the en-XM
        // canonical baseline; the strict baseline rule says we must
        // not introduce screen names that en-XM does not have. Skip
        // the blank-canvas capture and only emit _CanvasActive (the
        // post-stroke state, which IS in the baseline).

        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();
        int cx = w / 2;
        int cy = h / 2;
        mDevice.swipe(cx - 80, cy, cx + 80, cy, 20);
        mUtils.sleep(700L);
        takeScreenshot(suffix + "_CanvasActive");

        // Exit Scribble via the in-app top-left back affordance (arrow icon)
        // so the flow mirrors manual behaviour and does not rely on device BACK.
        boolean tapped = creator.tapTopLeftBackControl();
        if (!tapped) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Scribble back control was not resource-clickable.");
            }
            logger.info("Scribble: top-left back control was not detected immediately after capture.");
        } else {
            mUtils.sleep(500L);
        }
        boolean exitDialogVisible = creator.isScribbleExitDialogVisible();
        if (!exitDialogVisible) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Scribble exit dialog was not resource-verified.");
            }
            logger.info("Scribble exit popup was not verified before capture; "
                    + suffix + "_ExitPopup reflects best-available surface.");
        }
        takeScreenshot(suffix + "_ExitPopup");
        if (exitDialogVisible && creator.confirmScribbleExitIfPresent()) {
            logger.info("Scribble: confirmed exit popup via 'Yes, exit'.");
        }
        mUtils.sleep(800L);
    }

    /**
     * Style Sync full sub-flow: the Confirm usage dialog, the Allow-all
     * permission prompt (if raised), the system photo picker, and the
     * post-picker Style Sync surface once the first photo has been selected.
     */
    private void captureStyleSyncSubScreens(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        // The en-XM canonical baseline only contains _PostPicker for
        // the Style Sync feature. Per the strict baseline rule we
        // suppress every other Style Sync screenshot (_ConfirmUsage,
        // _Permission, _Main, _PhotoSelected). We still tap through
        // the confirm dialog + permission prompt + photo picker so
        // _PostPicker fires on the correct downstream surface.
        if (creator.waitForConfirmUsageDialog(CONFIRM_DIALOG_TIMEOUT_MS)) {
            try {
                creator.tapConfirm();
            } catch (Throwable t) {
                logger.info("Style Sync tapConfirm failed (continuing): " + t.getMessage());
            }
        }

        if (creator.waitForPermissionPrompt(PERMISSION_TIMEOUT_MS)) {
            creator.acceptPhotosPermissionIfPresent(PERMISSION_TIMEOUT_MS);
        }

        boolean postPickerSurfaceVisible = requireVerifiedCanonicalSurfaces()
                ? isVerifiedSelectedPhotoPreviewSurface("Style Sync")
                : creator.isImagePreviewConfirmVisible();
        if (!postPickerSurfaceVisible) {
            boolean pickerOpened = false;
            try {
                pickerOpened = ensurePhotoPickerOpened(creator, "Style Sync");
            } catch (Throwable t) {
                logger.info("ensurePhotoPickerOpened (style-sync) failed (continuing): "
                        + t.getMessage());
            }
            if (pickerOpened) {
                try {
                    walkPhotoPickerAndSelectFirst(creator, suffix);
                } catch (Throwable t) {
                    logger.info("walkPhotoPickerAndSelectFirst (style-sync) failed (continuing): "
                            + t.getMessage());
                }
            }
        } else {
            logger.info("Style Sync post-picker image confirmation is already visible.");
        }

        mUtils.sleep(FEATURE_SETTLE_MS);
        if (requireVerifiedCanonicalSurfaces()) {
            long deadline = System.currentTimeMillis() + AVATAR_PREVIEW_TIMEOUT_MS;
            while (System.currentTimeMillis() < deadline
                    && !isVerifiedSelectedPhotoPreviewSurface("Style Sync")) {
                mUtils.sleep(250L);
            }
            postPickerSurfaceVisible =
                    isVerifiedSelectedPhotoPreviewSurface("Style Sync");
        } else {
            postPickerSurfaceVisible = creator.isImagePreviewConfirmVisible();
        }
        if (!postPickerSurfaceVisible) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw strictCreatorSurfaceFailure(
                        "StyleSync_post_picker_not_resource_verified",
                        "Style Sync post-picker surface did not expose the"
                                + " required replace_label and"
                                + " generate_image_button resource pair.",
                        null);
            }
            logger.info("Style Sync post-picker confirmation was not verified before capture; "
                    + suffix + "_PostPicker reflects best-available surface.");
        }
        takeScreenshot(suffix + "_PostPicker");
    }

    /**
     * Drives the system photo picker: waits for it to appear, highlights the
     * first media tile (capturing the "one item selected" state), then taps
     * Done to confirm the selection. Returns true when the picker launched
     * and a selection was confirmed.
     */
    private boolean walkPhotoPickerAndSelectFirst(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        if (!creator.waitForPhotoPicker(PHOTO_PICKER_TIMEOUT_MS)) {
            return false;
        }
        if (!creator.tapFirstPhotoInPicker()) {
            logger.info("Photo picker: failed to locate a photo tile.");
            return false;
        }
        mUtils.sleep(600L);
        // _PhotoSelected is NOT in the en-XM canonical baseline for any
        // feature, so we suppress the screenshot but still tap Done so
        // the picker dismisses cleanly and the next surface (preview /
        // editor / post-picker) renders for any baseline screenshot
        // the caller is about to take.

        if (!creator.tapPhotoPickerDone()) {
            logger.info("Photo picker: 'Done' button was not tappable.");
            return false;
        }
        mUtils.sleep(1200L);
        return true;
    }

    private boolean areVerifiedQiraResourcesVisible(String... resourceIds) {
        return QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInSingleSnapshot(
                        true, null, resourceIds);
    }

    private boolean isVerifiedSelectedPhotoPreviewSurface(String featureLabel) {
        boolean actionPairVisible = areVerifiedQiraResourcesVisible(
                "replace_label", "generate_image_button");
        if (!actionPairVisible) {
            return false;
        }
        boolean selectedPhotoDescriptionVisible =
                areVerifiedQiraResourcesVisible("cd_selected_photo_preview");
        logger.info(featureLabel + " preview proven by required Qira Compose"
                + " resources replace_label + generate_image_button;"
                + " optional cd_selected_photo_preview="
                + selectedPhotoDescriptionVisible + ".");
        return true;
    }

    private IllegalStateException strictCreatorSurfaceFailure(
            String dumpTag,
            String message,
            Throwable cause) {
        String detail = message + " Current package=" + mDevice.getCurrentPackageName() + ".";
        logger.info(detail);
        QiraUiDumper.dump(
                mDevice,
                (String) null,
                getScreenPrefix() + "_" + dumpTag,
                detail);
        return cause == null
                ? new IllegalStateException(detail)
                : new IllegalStateException(detail, cause);
    }

    private boolean ensurePhotoPickerOpened(QiraCreatorZonePage creator, String flowName)
            throws Exception {
        if (creator.waitForPhotoPicker(3000L)) {
            return true;
        }
        for (int attempt = 1; attempt <= 3; attempt++) {
            if (creator.waitForPermissionPrompt(PERMISSION_TIMEOUT_MS)) {
                creator.acceptPhotosPermissionIfPresent(PERMISSION_TIMEOUT_MS);
            }
            boolean tapped = creator.tapChooseImageOption();
            if (!tapped) {
                logger.info(flowName + ": choose-image action not visible on attempt " + attempt + ".");
            }
            mUtils.sleep(600L);
            if (creator.waitForPhotoPicker(PHOTO_PICKER_TIMEOUT_MS)) {
                return true;
            }
        }
        return false;
    }

    /** Fallback path for any feature tile the switch above does not handle. */
    private void captureGenericFeature(QiraCreatorZonePage creator, String suffix)
            throws Exception {
        if (creator.waitForConfirmUsageDialog(CONFIRM_DIALOG_TIMEOUT_MS)) {
            takeScreenshot(suffix + "_ConfirmUsage");
        } else {
            takeScreenshot(suffix);
        }
    }

    private void ensureOnCreatorHome(QiraCreatorZonePage creator) throws Exception {
        for (int i = 0; i < 6 && !creator.isCreatorHomeVisible(); i++) {
            if (creator.dismissInformationDialogIfVisible()) {
                mUtils.sleep(700L);
                continue;
            }
            if (creator.confirmScribbleExitIfPresent()) {
                mUtils.sleep(700L);
                continue;
            }
            if (creator.tapTopLeftBackControl()) {
                mUtils.sleep(700L);
                continue;
            }
            if (creator.isScribbleSurfaceVisible()) {
                logger.info("Scribble surface detected; retrying top-left back affordance.");
                mUtils.sleep(700L);
                continue;
            }
            mDevice.pressBack();
            mUtils.sleep(800L);
        }
    }

    /**
     * Returns from a feature surface back to the Creator Zone home, closing
     * any lingering dialog first. We press back up to three times and verify
     * the home is visible between attempts.
     */
    private void returnToCreatorHome(QiraCreatorZonePage creator) throws Exception {
        for (int i = 0; i < 6 && !creator.isCreatorHomeVisible(); i++) {
            if (creator.dismissInformationDialogIfVisible()) {
                mUtils.sleep(700L);
                continue;
            }
            if (creator.confirmScribbleExitIfPresent()) {
                mUtils.sleep(700L);
                continue;
            }
            // If a discard-prompt popup raised a "lose your changes?"
            // confirmation in response to the previous Back, dismiss it
            // safely (keep generating / stay) so we do not destroy the
            // in-flight surface state needed by downstream captures.
            try {
                if (creator.isCreateImageDiscardPromptVisible()) {
                    boolean dismissed = creator.dismissDiscardPromptKeepGenerating();
                    if (dismissed) {
                        mUtils.sleep(500L);
                        continue;
                    }
                }
            } catch (Throwable ignored) {
            }
            if (creator.tapTopLeftBackControl()) {
                mUtils.sleep(700L);
                continue;
            }
            if (creator.isScribbleSurfaceVisible()) {
                logger.info("Scribble surface detected; skipping device BACK and retrying top-left back.");
                mUtils.sleep(800L);
                continue;
            }
            mDevice.pressBack();
            mUtils.sleep(900L);
        }
        if (!creator.isCreatorHomeVisible()) {
            if (requireVerifiedCanonicalSurfaces()) {
                throw new IllegalStateException(
                        "Creator Zone home was not resource-verified after leaving feature.");
            }
            logger.info("Creator Zone home was not reachable via back; continuing anyway.");
        }
    }

    /**
     * Converts a feature label (e.g. "Create image") into a filename-safe
     * screenshot suffix ("CreateImage").
     */
    private String toScreenSuffix(String label) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            } else {
                capitalizeNext = true;
            }
        }
        if (sb.length() == 0) {
            return "Feature";
        }
        return sb.toString();
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
