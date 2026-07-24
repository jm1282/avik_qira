package avik.qira_v2.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import avik.qira.pages.QiraCreatorZonePage;
import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/** Resource-backed Creator Zone page for qira_v2 captures. */
public final class QiraV2CreatorZonePage extends QiraCreatorZonePage {

    private static final Logger LOGGER = AvikLoggerFactory.INSTANCE.getInstance();
    private static final String[] FEATURE_RESOURCE_IDS = {
            "create_image_label",
            "edit_image_feature_name",
            "photo_to_avatar_feature_name",
            "text_to_sticker_feature_name",
            "scribble_feature_name",
            "style_sync_feature_name"
    };
    public QiraV2CreatorZonePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config, false);
    }

    @Override
    public boolean isInformationDialogVisible() {
        return QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInSingleSnapshot(
                        true, null, "information_title", "got_it")
                || QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInAccessibilitySnapshot(
                        mConfig.getPackageName(),
                        null,
                        "information_title",
                        "got_it");
    }

    @Override
    public boolean tapGotIt() throws Exception {
        return QiraV2SlapTextDump
                .performAccessibilityClickByResolvedQiraStringResource(
                        "got_it", LOGGER)
                || QiraV2SlapTextDump.shellClickByResolvedQiraStringResource(
                        mDevice, "got_it", true, LOGGER)
                || clickResource("got_it");
    }

    @Override
    public boolean isOnboardingPage1Visible() {
        return !isCreatorHomeVisible()
                && isComposeResourceVisible("edu_create_zone_screen1_suggestion_1");
    }

    @Override
    public boolean isOnboardingPage2Visible() {
        return !isCreatorHomeVisible()
                && isComposeResourceVisible("edu_create_zone_screen_heading");
    }

    @Override
    public boolean isOnboardingPage3Visible() {
        return !isCreatorHomeVisible()
                && isComposeResourceVisible("edu_create_zone_screen2_heading")
                && isComposeResourceVisible("edu_create_zone_screen3_suggestion_1");
    }

    @Override
    public boolean tapNext() throws Exception {
        if (clickResource("onboarding_agree_button")) {
            return true;
        }
        return clickResource("onboarding_next_button");
    }

    @Override
    public boolean isCreatorHomeVisible() {
        boolean informationDialog = isInformationDialogVisible();
        boolean featureOverlay = hasFeatureOverlayEvidence();
        int featureCount = countVisibleResources(FEATURE_RESOURCE_IDS);
        return !informationDialog && !featureOverlay && featureCount >= 4;
    }

    @Override
    public boolean tapFeatureTile(String featureLabel) throws Exception {
        String resourceId = featureResourceId(featureLabel);
        return resourceId != null && clickFeatureTileResource(resourceId);
    }

    @Override
    public boolean tapFeatureTileByGridPosition(String featureLabel) throws Exception {
        return tapFeatureTile(featureLabel);
    }

    @Override
    public boolean tapViewMoreHistory() throws Exception {
        return clickResource("predefined_view_more_button");
    }

    @Override
    public boolean tapTopLeftBackControl() throws Exception {
        if (isCreatorHomeVisible()) {
            return false;
        }
        return clickResource("back_arrow_content_desc", "cd_back");
    }

    @Override
    public boolean tapDailyQuotaChip() throws Exception {
        // quota_badge_info is the formatted "N/M left today" text, not the
        // clickable info icon. The icon and the resulting dialog title share
        // the stable information_title resource; before the dialog opens the
        // icon is the only visible match.
        return QiraV2SlapTextDump
                .performAccessibilityClickByResolvedQiraStringResource(
                        "information_title", LOGGER)
                || QiraV2SlapTextDump.shellClickByResolvedQiraStringResource(
                        mDevice, "information_title", true, LOGGER)
                || clickResource("information_title");
    }

    @Override
    public boolean isConfirmUsageDialogVisible() {
        return isResourceVisible("image_usage_rights_dialog_title");
    }

    @Override
    public boolean tapConfirm() throws Exception {
        return clickResource("confirm_button", "confirm_label");
    }

    @Override
    public boolean isCreateImageComposerVisible() {
        return isResourceVisible("input_hint");
    }

    @Override
    public boolean selectCreateImageStyle(String style) throws Exception {
        if ("Fantasy".equalsIgnoreCase(style)) {
            for (int pass = 0; pass < 6; pass++) {
                if (QiraV2SlapTextDump
                        .performAccessibilityClickByResolvedQiraStringResource(
                                "fantasy_description", LOGGER)
                        || clickResource("fantasy_style")) {
                    return true;
                }
                if (!super.scrollStyleCarousel(
                        QiraStrings.getInstance().isCurrentLocaleRtl())) {
                    break;
                }
            }
            return false;
        }
        return super.selectCreateImageStyle(style);
    }

    @Override
    public boolean tapCreateImageSend() throws Exception {
        return clickResource("input_send_desc");
    }

    @Override
    public boolean isCreateImageGeneratingVisible() {
        return isResourceVisible("chat_preparing_step")
                || isResourceVisible("chat_generating_step");
    }

    @Override
    public String currentCreateImageGeneratingStage() {
        if (isResourceVisible("chat_preparing_step")) {
            return "Preparing";
        }
        if (isResourceVisible("chat_generating_step")) {
            return "Generating";
        }
        return null;
    }

    @Override
    public boolean isCreateImageResultVisible() {
        return isResourceVisible("generated_image_preview")
                || isResourceVisible("cd_generated_image_chat")
                || isResourceVisible("cd_generated_image_search_bar");
    }

    @Override
    public boolean isPhotoPickerVisible() {
        return foregroundPickerPackage() != null;
    }

    @Override
    public boolean waitForPhotoPicker(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isPhotoPickerVisible()) {
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    @Override
    public boolean tapChooseImageOption() throws Exception {
        return QiraV2SlapTextDump.clickClickableAncestorByResolvedQiraStringResource(
                mDevice, "open_gallery", true, LOGGER);
    }

    @Override
    public boolean tapFirstPhotoInPicker() throws Exception {
        String pickerPackage = foregroundPickerPackage();
        if (pickerPackage == null) {
            LOGGER.info("QiraV2 photo picker selection: foreground package is not a picker; package="
                    + mDevice.getCurrentPackageName());
            return false;
        }

        long interactionDeadline = System.currentTimeMillis() + 4000L;
        int staleScans = 0;
        boolean clicked = false;
        while (System.currentTimeMillis() < interactionDeadline && !clicked) {
            try {
                Rect firstAssetBounds = findFirstOrderedMediaAssetBounds(pickerPackage);
                if (firstAssetBounds == null) {
                    return false;
                }
                clicked = clickReacquiredFirstOrderedMediaAsset(
                        pickerPackage, firstAssetBounds);
            } catch (StaleObjectException stale) {
                staleScans++;
                LOGGER.info("QiraV2 photo picker media scan became stale; reacquiring scan "
                        + staleScans + ".");
            }
            if (!clicked && System.currentTimeMillis() < interactionDeadline) {
                mUtils.sleep(100L);
            }
        }
        if (!clicked) {
            LOGGER.info("QiraV2 photo picker selection failed after bounded stale-node retries.");
            return false;
        }
        return waitForUniquePhotoPickerActionBounds(
                pickerPackage, interactionDeadline) != null;
    }

    @Override
    public boolean tapPhotoPickerDone() throws Exception {
        String pickerPackage = foregroundPickerPackage();
        if (pickerPackage == null) {
            LOGGER.info("QiraV2 photo picker confirmation: picker is not foreground; package="
                    + mDevice.getCurrentPackageName());
            return false;
        }
        long actionDeadline = System.currentTimeMillis() + 4000L;
        Rect expectedActionBounds =
                waitForUniquePhotoPickerActionBounds(pickerPackage, actionDeadline);
        if (expectedActionBounds == null) {
            return false;
        }
        boolean clicked = false;
        int staleScans = 0;
        while (System.currentTimeMillis() < actionDeadline && !clicked) {
            try {
                clicked = clickReacquiredUniquePhotoPickerAction(
                        pickerPackage, expectedActionBounds);
                if (!clicked) {
                    expectedActionBounds =
                            waitForUniquePhotoPickerActionBounds(
                                    pickerPackage, actionDeadline);
                    if (expectedActionBounds == null) {
                        return false;
                    }
                }
            } catch (StaleObjectException stale) {
                staleScans++;
                LOGGER.info("QiraV2 photo picker action became stale before click;"
                        + " reacquiring scan " + staleScans + ".");
            }
            if (!clicked && System.currentTimeMillis() < actionDeadline) {
                mUtils.sleep(100L);
            }
        }
        if (!clicked) {
            LOGGER.info("QiraV2 photo picker confirmation action could not be"
                    + " reacquired within the existing deadline.");
            return false;
        }

        long deadline = System.currentTimeMillis() + 8000L;
        while (System.currentTimeMillis() < deadline) {
            String currentPackage = mDevice.getCurrentPackageName();
            if (mConfig.getPackageName().equals(currentPackage)) {
                LOGGER.info("QiraV2 photo picker confirmation returned to Qira; package="
                        + currentPackage + ".");
                return true;
            }
            mUtils.sleep(200L);
        }
        LOGGER.info("QiraV2 photo picker confirmation did not return to Qira; pickerPackage="
                + pickerPackage + ", currentPackage=" + mDevice.getCurrentPackageName() + ".");
        return false;
    }

    @Override
    public boolean isScribbleSurfaceVisible() {
        return (isResourceVisible("generate_scribble_header")
                || isResourceVisible("scribble_feature_name"))
                && isResourceVisible("generate_image_button");
    }

    @Override
    public boolean isScribbleExitDialogVisible() {
        return isResourceVisible("discard_sketch_dialog_title");
    }

    @Override
    public boolean confirmScribbleExitIfPresent() throws Exception {
        return isScribbleExitDialogVisible()
                && clickResource("discard_sketch_dialog_secondary_button_text");
    }

    @Override
    public boolean isStickerSurfaceVisible() {
        return isResourceVisible("templates_title")
                || isResourceVisible("sticker_example_one_title")
                || isResourceVisible("sticker_example_two_title")
                || isResourceVisible("sticker_example_three_title");
    }

    @Override
    public boolean waitForAvatarSurface(long timeoutMs) throws Exception {
        return waitForResource(
                timeoutMs,
                "avatar_title",
                "avatar_description",
                "image_usage_rights_dialog_title");
    }

    @Override
    public boolean isStyleSyncEntryVisible() {
        return QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInSingleSnapshot(
                        true, null, "style_sync_title", "style_sync_description");
    }

    @Override
    public boolean isImagePreviewConfirmVisible() {
        return QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInSingleSnapshot(
                        true,
                        null,
                        "cd_selected_photo_preview",
                        "replace_label",
                        "generate_image_button");
    }

    @Override
    public boolean tapAvatarConfirmPreview() throws Exception {
        return QiraV2SlapTextDump.clickClickableAncestorByResolvedQiraStringResource(
                mDevice, "generate_image_button", true, LOGGER);
    }

    private boolean clickResource(String... resourceIds) {
        for (String resourceId : resourceIds) {
            if (QiraV2SlapTextDump.clickClickableAncestorByResolvedQiraStringResource(
                    mDevice, resourceId, true, null)
                    || QiraV2SlapTextDump
                    .performAccessibilityClickByResolvedQiraStringResource(
                            resourceId, LOGGER)
                    || QiraV2SlapTextDump.shellClickByResolvedQiraStringResource(
                            mDevice, resourceId, true, LOGGER)
                    || QiraV2SlapTextDump.clickByResolvedQiraStringResource(
                            mDevice, resourceId, true, null)) {
                return true;
            }
        }
        return false;
    }

    private boolean clickFeatureTileResource(String resourceId) {
        if ("style_sync_feature_name".equals(resourceId)) {
            return QiraV2SlapTextDump
                    .clickApprovedUpperRegionByResolvedQiraStringResource(
                            mDevice, resourceId, LOGGER);
        }
        return QiraV2SlapTextDump
                .performAccessibilityClickByResolvedQiraStringResource(
                        resourceId, LOGGER);
    }

    private boolean isResourceVisible(String resourceId) {
        return QiraV2SlapTextDump.findByResolvedQiraStringResource(
                resourceId, true, null) != null
                || QiraV2SlapTextDump
                .areResolvedQiraStringResourcesVisibleInAccessibilitySnapshot(
                        mConfig.getPackageName(), null, resourceId);
    }

    private boolean isComposeResourceVisible(String resourceId) {
        return QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                resourceId, true, null) != null;
    }

    private int countVisibleResources(String... resourceIds) {
        int count = 0;
        for (String resourceId : resourceIds) {
            if (isResourceVisible(resourceId)) {
                count++;
            }
        }
        return count;
    }

    private boolean hasFeatureOverlayEvidence() {
        return isResourceVisible("input_hint")
                || isResourceVisible("edit_image_input_hint")
                || isResourceVisible("image_usage_rights_dialog_title")
                || isResourceVisible("templates_title")
                || isResourceVisible("generate_scribble_desc_1")
                || isResourceVisible("generate_scribble_desc_2")
                || isScribbleSurfaceVisible()
                || isResourceVisible("avatar_title")
                || isResourceVisible("avatar_description")
                || isResourceVisible("style_sync_title")
                || isResourceVisible("cd_selected_photo_preview");
    }

    private String foregroundPickerPackage() {
        String packageName = mDevice.getCurrentPackageName();
        if (packageName == null || packageName.isEmpty()) {
            return null;
        }
        return packageName.contains("photopicker")
                || packageName.contains("providers.media")
                ? packageName
                : null;
    }

    private boolean hasNonEmptyDescribedDirectChild(UiObject2 parent) {
        for (UiObject2 child : parent.getChildren()) {
            String description = child.getContentDescription();
            if (description != null && !description.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Rect findFirstOrderedMediaAssetBounds(String pickerPackage) {
        List<UiObject2> scrollables =
                mDevice.findObjects(By.pkg(pickerPackage).scrollable(true));
        Rect firstAssetBounds = null;
        int assetCount = 0;
        int qualifyingScrollableCount = 0;
        for (UiObject2 scrollable : scrollables) {
            Rect firstInScrollable = null;
            for (UiObject2 child : scrollable.getChildren()) {
                if (!child.isClickable()
                        || !child.isEnabled()
                        || child.isScrollable()
                        || !hasNonEmptyDescribedDirectChild(child)) {
                    continue;
                }
                Rect bounds = copyVisibleBounds(child);
                if (bounds == null) {
                    continue;
                }
                assetCount++;
                if (firstInScrollable == null) {
                    firstInScrollable = bounds;
                }
            }
            if (firstInScrollable != null) {
                qualifyingScrollableCount++;
                if (firstAssetBounds == null) {
                    firstAssetBounds = firstInScrollable;
                }
            }
        }
        LOGGER.info("QiraV2 photo picker selection candidates: package=" + pickerPackage
                + ", scrollables=" + scrollables.size()
                + ", qualifyingScrollables=" + qualifyingScrollableCount
                + ", orderedAssets=" + assetCount + ".");
        if (qualifyingScrollableCount != 1 || firstAssetBounds == null) {
            LOGGER.info("QiraV2 photo picker selection failed: expected one scrollable media grid"
                    + " with at least one described clickable direct child.");
            return null;
        }
        return new Rect(firstAssetBounds);
    }

    private boolean clickReacquiredFirstOrderedMediaAsset(
            String pickerPackage,
            Rect expectedBounds) {
        UiObject2 target = null;
        int qualifyingScrollableCount = 0;
        for (UiObject2 scrollable : mDevice.findObjects(
                By.pkg(pickerPackage).scrollable(true))) {
            UiObject2 firstInScrollable = null;
            Rect firstBounds = null;
            for (UiObject2 child : scrollable.getChildren()) {
                if (!child.isClickable()
                        || !child.isEnabled()
                        || child.isScrollable()
                        || !hasNonEmptyDescribedDirectChild(child)) {
                    continue;
                }
                firstBounds = copyVisibleBounds(child);
                if (firstBounds != null) {
                    firstInScrollable = child;
                    break;
                }
            }
            if (firstInScrollable != null) {
                qualifyingScrollableCount++;
                if (expectedBounds.equals(firstBounds)) {
                    target = firstInScrollable;
                }
            }
        }
        if (qualifyingScrollableCount != 1 || target == null) {
            return false;
        }
        LOGGER.info("QiraV2 photo picker selecting reacquired first ordered media asset"
                + " via UiObject2.click; bounds=" + expectedBounds + ".");
        target.click();
        return true;
    }

    private Rect waitForUniquePhotoPickerActionBounds(
            String pickerPackage,
            long deadline) throws Exception {
        int lastCount = -1;
        int staleScans = 0;
        while (System.currentTimeMillis() < deadline) {
            try {
                List<Rect> candidates =
                        findPhotoPickerActionCandidateBounds(pickerPackage);
                if (candidates.size() != lastCount) {
                    LOGGER.info("QiraV2 photo picker TextView-backed logical-final"
                            + " action candidates=" + candidates.size() + ".");
                    lastCount = candidates.size();
                }
                if (candidates.size() == 1) {
                    return new Rect(candidates.get(0));
                }
            } catch (StaleObjectException stale) {
                staleScans++;
                LOGGER.info("QiraV2 photo picker action scan became stale;"
                        + " reacquiring scan " + staleScans + ".");
            }
            mUtils.sleep(200L);
        }
        LOGGER.info("QiraV2 photo picker confirmation action failed: "
                + (lastCount > 1 ? "ambiguous candidates=" + lastCount : "candidate count=0")
                + ".");
        return null;
    }

    private List<Rect> findPhotoPickerActionCandidateBounds(String pickerPackage) {
        List<Rect> candidates = new ArrayList<>();
        for (UiObject2 row : mDevice.findObjects(By.pkg(pickerPackage))) {
            UiObject2 logicalFinalAction = logicalFinalTextAction(row);
            if (logicalFinalAction == null) {
                continue;
            }
            Rect bounds = copyVisibleBounds(logicalFinalAction);
            if (bounds != null && !containsBounds(candidates, bounds)) {
                candidates.add(bounds);
            }
        }
        return candidates;
    }

    private boolean clickReacquiredUniquePhotoPickerAction(
            String pickerPackage,
            Rect expectedBounds) {
        List<Rect> candidateBounds = new ArrayList<>();
        UiObject2 target = null;
        for (UiObject2 row : mDevice.findObjects(By.pkg(pickerPackage))) {
            UiObject2 action = logicalFinalTextAction(row);
            if (action == null) {
                continue;
            }
            Rect bounds = copyVisibleBounds(action);
            if (bounds == null || containsBounds(candidateBounds, bounds)) {
                continue;
            }
            candidateBounds.add(bounds);
            if (expectedBounds.equals(bounds)) {
                target = action;
            }
        }
        if (candidateBounds.size() != 1 || target == null) {
            LOGGER.info("QiraV2 photo picker action reacquire expected one candidate;"
                    + " candidates=" + candidateBounds.size() + ".");
            return false;
        }
        LOGGER.info("QiraV2 photo picker clicking reacquired unique logical-final"
                + " TextView-backed action via UiObject2.click; bounds="
                + expectedBounds + ".");
        target.click();
        return true;
    }

    private UiObject2 logicalFinalTextAction(UiObject2 row) {
        UiObject2 logicalFinalAction = null;
        int textActionCount = 0;
        for (UiObject2 child : row.getChildren()) {
            if (child.isClickable()
                    && child.isEnabled()
                    && !child.isScrollable()
                    && hasNonEmptyDirectTextViewRole(child)) {
                textActionCount++;
                logicalFinalAction = child;
            }
        }
        return textActionCount >= 2 ? logicalFinalAction : null;
    }

    private boolean hasNonEmptyDirectTextViewRole(UiObject2 parent) {
        for (UiObject2 child : parent.getChildren()) {
            if (!"android.widget.TextView".equals(child.getClassName())) {
                continue;
            }
            String text = child.getText();
            String description = child.getContentDescription();
            if ((text != null && !text.trim().isEmpty())
                    || (description != null && !description.trim().isEmpty())) {
                return true;
            }
        }
        return false;
    }

    private Rect copyVisibleBounds(UiObject2 object) {
        Rect bounds = object.getVisibleBounds();
        return bounds == null || bounds.isEmpty() ? null : new Rect(bounds);
    }

    private boolean containsBounds(List<Rect> candidates, Rect expected) {
        for (Rect candidate : candidates) {
            if (expected.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean waitForResource(
            long timeoutMs,
            String... resourceIds) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            for (String resourceId : resourceIds) {
                if (isResourceVisible(resourceId) && !isCreatorHomeVisible()) {
                    return true;
                }
            }
            mUtils.sleep(200L);
        }
        return false;
    }

    private static String featureResourceId(String featureLabel) {
        if (featureLabel == null) {
            return null;
        }
        for (int index = 0; index < FEATURE_TILES.length; index++) {
            if (FEATURE_TILES[index].equals(featureLabel)) {
                return FEATURE_RESOURCE_IDS[index];
            }
        }
        return null;
    }
}
