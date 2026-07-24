package avik.qira_v2.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.List;
import java.util.Locale;

import avik.qira.pages.QiraSettingsDetailPage;
import avik.qira.pages.QiraSettingsPage;
import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;
import avik.qira_v2.utils.QiraV2ComposeStrings;
import avik.qira_v2.utils.QiraV2SlapTextDump;

/**
 * Stable-resource Settings navigation for Workbench-facing qira_v2 captures.
 */
public final class QiraV2SettingsPage extends QiraSettingsPage {

    private static final String[] OPTION_RESOURCE_IDS = {
            "accounts_check",
            "title_devices",
            "smart_Connect",
            "language",
            "launch_options",
            "voice",
            "lock_screen",
            "title_sync",
            "personalized_answers",
            "CMU",
            "connectors",
            "about",
            "about_support_page",
            "title_legal",
            "feedback"
    };

    public QiraV2SettingsPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    @Override
    public boolean selectOption(String... labels) throws Exception {
        String resourceId = optionResourceId(labels);
        if (resourceId != null
                && QiraV2SlapTextDump
                .clickClickableAncestorByResolvedQiraComposeStringResource(
                        mDevice, resourceId, true, null)) {
            return true;
        }
        String localized = resolvePlainComposeValue(resourceId);
        if (localized == null || localized.isEmpty()) {
            return false;
        }
        return super.selectOption(localized);
    }

    @Override
    public QiraSettingsDetailPage openOptionDetailByPosition(
            int optionIndex,
            String[] optionLabels,
            String... detailAnchors) throws Exception {
        if (!selectOption(optionLabels)) {
            return null;
        }
        settle();
        return new QiraSettingsDetailPage(mDevice, mConfig);
    }

    @Override
    public BodyTitleCheck verifyBodyTitleAlignment(String[] expectedAliases) {
        boolean masterDetail = isMasterDetailLayout();
        boolean optionListVisible;
        try {
            optionListVisible = waitForOptionList(1L);
        } catch (Exception ignored) {
            optionListVisible = false;
        }
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (String resourceId : detailResourceIds(expectedAliases)) {
            String localized = resolvePlainComposeValue(resourceId);
            if (localized == null || localized.isEmpty()) {
                continue;
            }
            for (UiObject2 hit : findLocalizedHits(localized)) {
                try {
                    Rect bounds = hit.getVisibleBounds();
                    if (bounds == null || bounds.isEmpty()) {
                        continue;
                    }
                    if (masterDetail && bounds.centerX() > width / 2) {
                        return BodyTitleCheck.MATCH;
                    }
                    if (!masterDetail
                            && !optionListVisible
                            && bounds.top < (height * 35) / 100) {
                        return BodyTitleCheck.MATCH;
                    }
                } catch (StaleObjectException ignored) {
                }
            }
        }
        return BodyTitleCheck.MISMATCH;
    }

    @Override
    public boolean returnToSettingsListIfNeeded(long timeoutMs) throws Exception {
        if (isResourceBackedOptionListVisible()) {
            return true;
        }
        if (!clickFirstComposeResource("cd_settings_back")) {
            return false;
        }
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isResourceBackedOptionListVisible()) {
                return true;
            }
            mUtils.sleep(200L);
        }
        return isResourceBackedOptionListVisible();
    }

    @Override
    public boolean scrollCatchMeUpDetailToTop() throws Exception {
        if (isCatchMeUpTopResourceVisible()) {
            return true;
        }
        super.scrollCatchMeUpDetailToTop();
        return isCatchMeUpTopResourceVisible();
    }

    private List<UiObject2> findLocalizedHits(String localized) {
        java.util.ArrayList<UiObject2> hits = new java.util.ArrayList<>();
        hits.addAll(mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).text(
                        exactPatternForLabel(localized))));
        hits.addAll(mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).desc(
                        exactPatternForLabel(localized))));
        return hits;
    }

    private boolean clickFirstComposeResource(String... resourceIds) {
        for (String resourceId : resourceIds) {
            if (QiraV2SlapTextDump
                    .clickClickableAncestorByResolvedQiraComposeStringResource(
                            mDevice, resourceId, true, null)) {
                return true;
            }
        }
        return false;
    }

    private boolean isResourceBackedOptionListVisible() {
        int visible = 0;
        for (String resourceId : OPTION_RESOURCE_IDS) {
            if (QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                    resourceId, true, null) != null) {
                visible++;
            }
        }
        return visible >= 3;
    }

    private boolean isCatchMeUpTopResourceVisible() {
        boolean title = QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                "CMU", true, null) != null
                || QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                "catch_me_up", true, null) != null;
        boolean clear = QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                "clear_cmu_toggle", true, null) != null
                || QiraV2SlapTextDump.findByResolvedQiraComposeStringResource(
                "clear_summarized_notifications", true, null) != null;
        return title && clear;
    }

    private String resolvePlainComposeValue(String resourceId) {
        if (resourceId == null) {
            return null;
        }
        String value = QiraV2ComposeStrings.resolve(
                mConfig.getPackageName(), resourceId, null);
        if (value == null || value.isEmpty()) {
            value = QiraV2ComposeStrings.resolveDefault(
                    mConfig.getPackageName(), resourceId, null);
        }
        return QiraStrings.stripBidiControls(value);
    }

    private static String optionResourceId(String[] labels) {
        if (labels == null || labels.length == 0 || labels[0] == null) {
            return null;
        }
        String label = labels[0].replace('\u00A0', ' ')
                .trim()
                .toLowerCase(Locale.ROOT);
        switch (label) {
            case "account":
                return "accounts_check";
            case "devices":
                return "title_devices";
            case "smart connect":
            case "smartconnect":
                return "smart_Connect";
            case "language":
                return "language";
            case "launch options":
                return "launch_options";
            case "voice":
                return "voice";
            case "lock-screen display":
            case "lock screen display":
                return "lock_screen";
            case "sync data":
                return "sync_data_label";
            case "personalized answers":
            case "personalised answers":
                return "personalized_answers";
            case "catch me up":
                return "CMU";
            case "connectors":
                return "connectors";
            case "about":
                return "about";
            case "support page":
                return "about_support_page";
            case "legal notices":
                return "title_legal";
            case "feedback":
                return "feedback";
            default:
                return null;
        }
    }

    private static String[] detailResourceIds(String[] labels) {
        String optionId = optionResourceId(labels);
        if (labels == null || labels.length == 0 || labels[0] == null) {
            return new String[0];
        }
        String label = labels[0].replace('\u00A0', ' ')
                .trim()
                .toLowerCase(Locale.ROOT);
        switch (label) {
            case "lock-screen display":
            case "lock screen display":
                return new String[] {"title_lock_screen", "use_on_lock_screen", optionId};
            case "smart connect":
            case "smartconnect":
                return new String[] {"smart_connect_title", optionId};
            case "about":
                return new String[] {"title_about", optionId};
            case "feedback":
                return new String[] {"title_feedback", optionId};
            default:
                return optionId == null
                        ? new String[0]
                        : new String[] {optionId};
        }
    }
}
