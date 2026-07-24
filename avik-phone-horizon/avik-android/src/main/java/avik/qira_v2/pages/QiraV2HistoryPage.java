package avik.qira_v2.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.List;

import avik.qira.pages.QiraHistoryPage;
import avik.qira.utils.QiraConfig;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/** Resource-backed Chat History page for qira_v2 captures. */
public final class QiraV2HistoryPage extends QiraHistoryPage {

    private static final String[] MAIN_IDS = {
            "view_manage_chats",
            "manage_chats"
    };
    private static final String[] MANAGE_IDS = {
            "select_all",
            "cd_select_all",
            "delete",
            "cd_delete"
    };
    private static final String[] DELETE_DIALOG_IDS = {
            "delete_chat_prompt",
            "delete_chat_message",
            "title_delete_alert_dialog",
            "title_delete_alert_dialog_single"
    };
    private static final String[] CHAT_MORE_OPTIONS_IDS = {
            "settings",
            "history",
            "feedback"
    };

    public QiraV2HistoryPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    @Override
    public boolean isHomeTileVisible() {
        return isAnyComposeResourceVisible("chat_hist");
    }

    @Override
    public void tapChatHistoryTile() throws Exception {
        if (!clickFirstComposeResource("chat_hist")) {
            throw new IllegalStateException(
                    "QiraV2 Chat History tile was not resource-clickable.");
        }
    }

    @Override
    public boolean isMainVisible() {
        return isAnyComposeResourceVisible(MAIN_IDS);
    }

    @Override
    public boolean waitForMain(long timeoutMs) throws Exception {
        return waitForAnyComposeResource(timeoutMs, MAIN_IDS);
    }

    @Override
    public boolean waitForMainOrLocalized(long timeoutMs) throws Exception {
        return waitForMain(timeoutMs);
    }

    @Override
    public boolean openManageChats() throws Exception {
        if (!clickFirstComposeResource("manage_chats")) {
            return false;
        }
        return waitForAnyComposeResource(8000L, "select_all", "cd_select_all");
    }

    @Override
    public boolean isManageModeVisible() {
        return countVisibleComposeResources(MANAGE_IDS) >= 2;
    }

    @Override
    public boolean selectFirstManageChat() throws Exception {
        int height = mDevice.getDisplayHeight();
        UiObject2 target = null;
        int top = Integer.MAX_VALUE;
        List<UiObject2> checkables = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true).clickable(true));
        for (UiObject2 checkable : checkables) {
            try {
                Rect bounds = checkable.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()
                        || bounds.top < (height * 18) / 100
                        || bounds.bottom > (height * 88) / 100
                        || checkable.isChecked()) {
                    continue;
                }
                if (bounds.top < top) {
                    top = bounds.top;
                    target = checkable;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        if (target == null) {
            return false;
        }
        target.click();
        settle();
        try {
            return target.isChecked();
        } catch (StaleObjectException stale) {
            return mDevice.hasObject(
                    By.pkg(mConfig.getPackageName()).checkable(true).checked(true));
        }
    }

    @Override
    public boolean tapManageChatsDeleteAction() throws Exception {
        return clickFirstComposeResource("cd_delete", "delete");
    }

    @Override
    public boolean waitForDeleteChatDialog(long timeoutMs) throws Exception {
        return waitForAnyComposeResource(timeoutMs, DELETE_DIALOG_IDS);
    }

    @Override
    public boolean cancelDeleteChatDialog() throws Exception {
        return clickFirstComposeResource("cancel", "cancel_button");
    }

    @Override
    public boolean exitManageChats() throws Exception {
        return clickFirstComposeResource(
                "back_arrow_contentDescription",
                "cd_back",
                "back");
    }

    @Override
    public boolean openChatMoreOptions() throws Exception {
        if (!clickFirstComposeResource("cd_more_options")) {
            return false;
        }
        return waitForAnyComposeResource(5000L, CHAT_MORE_OPTIONS_IDS);
    }

    @Override
    public boolean isChatMoreOptionsVisible() {
        return countVisibleComposeResources(CHAT_MORE_OPTIONS_IDS) >= 2;
    }

    private boolean clickFirstComposeResource(String... resourceIds) {
        for (String resourceId : resourceIds) {
            if (QiraV2SlapTextDump
                    .clickClickableAncestorByResolvedQiraComposeStringResource(
                            mDevice, resourceId, true, null)
                    || QiraV2SlapTextDump.clickByResolvedQiraComposeStringResource(
                            mDevice, resourceId, true, null)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyComposeResourceVisible(String... resourceIds) {
        for (String resourceId : resourceIds) {
            if (QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                    resourceId, true, null) != null) {
                return true;
            }
        }
        return false;
    }

    private int countVisibleComposeResources(String... resourceIds) {
        int count = 0;
        for (String resourceId : resourceIds) {
            if (isAnyComposeResourceVisible(resourceId)) {
                count++;
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
