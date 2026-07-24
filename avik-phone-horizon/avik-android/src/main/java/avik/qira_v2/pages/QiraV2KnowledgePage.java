package avik.qira_v2.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.List;
import java.util.logging.Logger;

import avik.qira.pages.QiraKnowledgePage;
import avik.qira.utils.QiraConfig;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/**
 * Resource-backed Knowledge page used by qira_v2 captures.
 *
 * <p>The legacy page remains unchanged for qira v1. This subclass removes the
 * geometry and locale-table fallbacks from Workbench-facing qira_v2 flows.</p>
 */
public final class QiraV2KnowledgePage extends QiraKnowledgePage {

    private static final Logger LOGGER = AvikLoggerFactory.INSTANCE.getInstance();

    private static final String[] ONBOARDING_PAGE_1_IDS = {
            "edu_remember_screen1_heading",
            "edu_remember_screen1_suggestion_1",
            "edu_remember_screen1_suggestion_2",
            "edu_remember_screen1_suggestion_3"
    };
    private static final String[] ONBOARDING_PAGE_2_IDS = {
            "edu_remember_screen2_heading_1",
            "enable_personalized",
            "synchronization_title_banner"
    };
    private static final String[] MAIN_IDS = {
            "knowledge_message",
            "memory_manage_bar_subtitle",
            "looking_for",
            "search_hint"
    };
    private static final String[] CATEGORY_OPTION_IDS = {
            "all_",
            "memories",
            "documents",
            "knowledge_category_pay_attention"
    };
    private static final String[] MORE_OPTIONS_MENU_IDS = {
            "memory_manage_dropdown_manage_personalization",
            "memory_manage_dropdown_delete"
    };
    private static final String[] FAB_MENU_IDS = {
            "create_memory_card_title",
            "memory_empty_page_create_memory",
            "upload_files"
    };

    public QiraV2KnowledgePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    @Override
    public boolean isHomeTileVisible() {
        return isAnyComposeResourceVisible("knowledge");
    }

    @Override
    public void tapKnowledgeTile() throws Exception {
        clickRequiredComposeResource("Knowledge tile", "knowledge");
    }

    @Override
    public boolean isOnboardingVisible() {
        return isOnboardingPage1Visible() || isOnboardingPage2Visible();
    }

    @Override
    public boolean isOnboardingPage1Visible() {
        return isAnyComposeResourceVisible(ONBOARDING_PAGE_1_IDS);
    }

    @Override
    public boolean isOnboardingPage2Visible() {
        return isAnyComposeResourceVisible(ONBOARDING_PAGE_2_IDS);
    }

    @Override
    public boolean tapNext() throws Exception {
        return clickFirstComposeResource(
                "next",
                "allow_permissions",
                "i_agree",
                "accept");
    }

    @Override
    public boolean isMainVisible() {
        return isAnyComposeResourceVisible(MAIN_IDS);
    }

    @Override
    public boolean openCategoriesDropdown() throws Exception {
        return clickFirstComposeResource("categories");
    }

    @Override
    public boolean isCategoriesDropdownVisible() {
        return countVisibleComposeResources(CATEGORY_OPTION_IDS) >= 2;
    }

    @Override
    public boolean openTagsDropdown() throws Exception {
        return clickFirstComposeResource("tags");
    }

    @Override
    public boolean isTagsDropdownVisible() {
        return isTagsPopupStructureVisible();
    }

    private boolean isTagsPopupStructureVisible() {
        for (UiObject2 content : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).res("android:id/content"))) {
            try {
                for (UiObject2 scrollView : content.findObjects(
                        By.clazz("android.widget.ScrollView"))) {
                    List<UiObject2> rows = scrollView.getChildren();
                    if (rows.size() < 4 || rows.size() > 6) {
                        continue;
                    }
                    int clickableRows = 0;
                    int labeledRows = 0;
                    for (UiObject2 row : rows) {
                        Rect bounds = row.getVisibleBounds();
                        if (bounds == null || bounds.isEmpty()) {
                            continue;
                        }
                        if (row.isClickable()) {
                            clickableRows++;
                        }
                        if (!row.findObjects(
                                By.clazz("android.widget.TextView")).isEmpty()) {
                            labeledRows++;
                        }
                    }
                    if (clickableRows >= 4 && labeledRows >= 4) {
                        return true;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return false;
    }

    @Override
    public boolean openMoreOptionsMenu() throws Exception {
        if (!clickFirstComposeResource("cd_more_options")) {
            return false;
        }
        return waitForAnyComposeResource(5000L, MORE_OPTIONS_MENU_IDS);
    }

    @Override
    public boolean isMoreOptionsMenuVisible() {
        return isAnyComposeResourceVisible(MORE_OPTIONS_MENU_IDS);
    }

    @Override
    public boolean openManageSettingsDialogFromMenu() throws Exception {
        if (!isMoreOptionsMenuVisible()
                || !clickFirstComposeResource(
                "memory_manage_dropdown_manage_personalization")) {
            return false;
        }
        return waitForAnyComposeResource(5000L, "manage_settings_title_card");
    }

    @Override
    public boolean isManageSettingsDialogVisible() {
        return isAnyComposeResourceVisible("manage_settings_title_card")
                && isAnyComposeResourceVisible("save");
    }

    @Override
    public boolean dismissManageSettingsDialog() throws Exception {
        return clickFirstComposeResource("cancel", "cancel_button");
    }

    @Override
    public boolean openFabMenu() throws Exception {
        if (isFabMenuVisible()) {
            return true;
        }
        if (!clickFirstComposeResource("cd_menu")) {
            return false;
        }
        return waitForAnyComposeResource(5000L, FAB_MENU_IDS);
    }

    @Override
    public boolean isFabMenuVisible() {
        return isAnyComposeResourceVisible("upload_files")
                && isAnyComposeResourceVisible(
                "create_memory_card_title",
                "memory_empty_page_create_memory");
    }

    @Override
    public boolean openCreateMemoryDialog() throws Exception {
        if (!isFabMenuVisible() && !openFabMenu()) {
            return false;
        }
        if (!clickFirstComposeResource(
                "create_memory_card_title",
                "memory_empty_page_create_memory")) {
            return false;
        }
        return waitForAnyComposeResource(
                5000L, "create_memory_card_primary_button", "cd_remember_this");
    }

    @Override
    public boolean isCreateMemoryDialogVisible() {
        return isAnyComposeResourceVisible("create_memory_card_title")
                && isAnyComposeResourceVisible(
                "create_memory_card_primary_button",
                "cd_remember_this");
    }

    @Override
    public boolean cancelCreateMemoryDialog() throws Exception {
        return clickFirstComposeResource("cancel", "cancel_button");
    }

    private void clickRequiredComposeResource(
            String label,
            String... resourceIds) {
        if (!clickFirstComposeResource(resourceIds)) {
            throw new IllegalStateException(
                    "QiraV2 Knowledge could not activate resource-backed " + label + ".");
        }
    }

    private boolean clickFirstComposeResource(String... resourceIds) {
        if (resourceIds == null) {
            return false;
        }
        for (String resourceId : resourceIds) {
            if (resourceId == null) {
                continue;
            }
            if (QiraV2SlapTextDump
                    .clickClickableAncestorByResolvedQiraComposeStringResource(
                            mDevice, resourceId, true, LOGGER)
                    || QiraV2SlapTextDump.clickByResolvedQiraComposeStringResource(
                            mDevice, resourceId, true, LOGGER)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyComposeResourceVisible(String... resourceIds) {
        if (resourceIds == null) {
            return false;
        }
        for (String resourceId : resourceIds) {
            if (resourceId != null
                    && QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                            resourceId, true, null) != null) {
                return true;
            }
        }
        return false;
    }

    private int countVisibleComposeResources(String... resourceIds) {
        int count = 0;
        if (resourceIds != null) {
            for (String resourceId : resourceIds) {
                if (isAnyComposeResourceVisible(resourceId)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean waitForAnyComposeResource(
            long timeoutMs,
            String... resourceIds) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isAnyComposeResourceVisible(resourceIds)) {
                return true;
            }
            mUtils.sleep(200L);
        }
        return isAnyComposeResourceVisible(resourceIds);
    }
}
