package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

/**
 * Page object for the Motorola Qira "Knowledge" surface. Knowledge is reached
 * from the Qira home tile grid and exposes:
 *
 * <ul>
 *     <li>A 2-page first-run onboarding carousel
 *         (Knowledge introduction &rarr; Turn on all permissions below).</li>
 *     <li>A main list view with a hero title ("All your memories &amp; files in
 *         one place"), a search field ("What are you looking for?"), Categories
 *         / Tags dropdowns and a scrollable list of saved memories (PDFs).</li>
 *     <li>A top-right More options menu with "Manage Settings" and
 *         "Delete Everything".</li>
 *     <li>A per-item detail screen with Summary / Transcript / Audio Recording
 *         tabs (re-using the same layout as Pay Attention summaries).</li>
 * </ul>
 *
 * <p>All matching is done via Compose-friendly text / content-description
 * lookups from {@link BaseQiraPage}. The tile on the home grid has no
 * resource-id, so we match by the label "Knowledge" and click the nearest
 * clickable ancestor.
 */
public class QiraKnowledgePage extends BaseQiraPage {

    private static final long DEFAULT_TIMEOUT_MS = 10000L;

    private static final String TILE_KNOWLEDGE = "Knowledge";

    /** Hero tile labels shown on the Qira home surface. */
    private static final String[] HOME_KNOWLEDGE_TILE_LABELS = {
            "Knowledge"
    };

    /**
     * Anchors that uniquely identify the first onboarding page. The first page
     * advertises Knowledge with the "Motorola Qira remembers and recalls…"
     * headline plus a list of bullet points.
     */
    private static final String[] ONBOARDING_PAGE_1_LABELS = {
            "Motorola Qira remembers and recalls for you across all your devices",
            "Save everything from meeting notes and documents to birthdays",
            "Get answers powered by your personal knowledge",
            "Easily recall details when you need them most",
            "Motorola Qira merkt sich die Daten und ruft sie f\u00fcr dich auf all deinen Ger\u00e4ten ab.",
            "Du kannst alles speichern, von Besprechungsnotizen und Dokumenten bis hin zu Geburtstagen",
            "Erhalte Antworten, die auf dein pers\u00f6nliches Wissen zur\u00fcckgreifen.",
            "Greife auf Detailinformationen zu, wenn du sie am dringendsten brauchst"
    };

    /**
     * Anchors for the second onboarding page. The second page asks the user
     * to enable "Personalized Answers" and "Sync data across your devices".
     */
    private static final String[] ONBOARDING_PAGE_2_LABELS = {
            "Turn on all permissions below",
            "Enable Personalized Answers",
            "Allow Motorola Qira to use saved memories and files for more tailored responses",
            "Sync data across your devices",
            "Keep everything in sync across your PC and mobile devices"
    };

    /** Labels that identify the main Knowledge list surface (post-onboarding). */
    private static final String[] MAIN_ANCHORS = {
            "All your memories & files in one place",
            "What are you looking for?",
            "Alle deine Erinnerungen und Dateien an einem Ort",
            "Wonach suchst du?"
    };

    private static final String[] ONBOARDING_PRIMARY_ACTION_LABELS = {
            "Next",
            "Continue",
            "I agree",
            "Agree",
            "Accept",
            "Weiter",
            "Suivant",
            "Continuer",
            "Autoriser",
            "Permitir",
            "Avan\u00e7ar",
            "Siguiente"
    };

    private static final String CATEGORIES_LABEL = "Categories";
    private static final String TAGS_LABEL = "Tags";

    /** Options exposed by the "Categories" dropdown. */
    private static final String[] CATEGORY_OPTION_LABELS = {
            "All", "Memories", "Documents", "Pay Attention",
            "Alle", "Erinnerungen", "Dokumente", "Achtgeben"
    };

    /** Options exposed by the "Tags" dropdown. */
    private static final String[] TAG_OPTION_LABELS = {
            "Identity", "Contact", "Education", "Work"
    };

    /** Options exposed by the top-right More options overflow menu. */
    private static final String[] MORE_OPTIONS_LABELS = {
            "Manage Settings", "Delete Everything"
    };
    private static final String[] MANAGE_SETTINGS_DIALOG_LABELS = {
            "Manage Settings",
            "Allow personalized answers",
            "Sync data across your devices",
            "Save"
    };

    private static final String[] MANAGE_SETTINGS_CANCEL_LABELS = {
            "Cancel",
            "Close"
    };

    /**
     * Labels exposed by the Knowledge FAB menu. Tapping the "Menu" icon in the
     * bottom-right of the Knowledge surface pops up this menu with entries for
     * creating a new memory or uploading files.
     */
    private static final String[] FAB_MENU_ANCHORS = {
            "Create a memory",
            "Upload Files"
    };

    /**
     * Labels that identify the "Create a memory" composer dialog reached from
     * the FAB menu. The dialog contains a title, a placeholder "For example,
     * …" prompt, a "Remember this" confirm button and a "Cancel" button.
     */
    private static final String[] CREATE_MEMORY_ANCHORS = {
            "Create a memory",
            "For example",
            "Remember this",
            "Cancel"
    };

    /**
     * Pattern matched against the content-description of saved Knowledge list
     * rows. The app renders each row with a description like
     * "Some Title_04-17-2026 0227PM.pdf, Apr 17, 2026, Local".
     */
    private static final Pattern LIST_ITEM_DESC =
            Pattern.compile("(?is).+\\.[a-z0-9]{2,5},\\s*.+$");

    /** Detail screen tabs shown after tapping a list item. */
    public static final String[] DETAIL_TAB_LABELS = {
            "Summary", "Transcript", "Audio Recording"
    };

    public QiraKnowledgePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    // ---------------------------------------------------------------------
    // Home tile navigation
    // ---------------------------------------------------------------------

    /** Returns true when the Qira home tile grid is currently showing the Knowledge tile. */
    public boolean isHomeTileVisible() {
        return hasTextOrDescription(HOME_KNOWLEDGE_TILE_LABELS)
                || findKnowledgeTileByGeometry() != null;
    }

    /**
     * Taps the Knowledge tile on the Qira home surface. We intentionally click
     * the deepest clickable ancestor so the whole card is activated (the inner
     * label TextView is not itself clickable).
     */
    public void tapKnowledgeTile() throws Exception {
        UiObject2 label = findByExactTextOrDescription(TILE_KNOWLEDGE);
        if (label == null) {
            UiObject2 fallback = findKnowledgeTileByGeometry();
            if (fallback == null) {
                throw new IllegalStateException(
                        "Unable to locate the Knowledge tile on the Qira home");
            }
            clickObject(fallback);
            return;
        }
        UiObject2 clickable = findClickableAncestor(label);
        if (clickable == null) {
            clickable = findNearestClickableTile(label);
        }
        clickObject(clickable != null ? clickable : label);
    }

    // ---------------------------------------------------------------------
    // Onboarding carousel (Next / Back)
    // ---------------------------------------------------------------------

    public boolean isOnboardingVisible() {
        return isKnowledgeOnboardingDialogVisibleByGeometry()
                || isOnboardingPage1Visible()
                || isOnboardingPage2Visible();
    }

    public boolean isOnboardingPage1Visible() {
        if (hasTextOrDescription(ONBOARDING_PAGE_1_LABELS)) {
            return true;
        }
        return isKnowledgeOnboardingDialogVisibleByGeometry()
                && countKnowledgeOnboardingToggleCandidatesByGeometry() == 0;
    }

    public boolean isOnboardingPage2Visible() {
        if (isOnboardingPage2VisibleByGeometry()) {
            return true;
        }
        return hasTextOrDescription(ONBOARDING_PAGE_2_LABELS);
    }

    public boolean waitForOnboardingPage1(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isOnboardingPage1Visible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean waitForOnboardingPage2(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isOnboardingPage2Visible()) {
                settle();
                return true;
            }
            // If onboarding has already been dismissed and main content is up,
            // stop waiting early instead of burning the full timeout.
            if (isMainVisible() && !isOnboardingVisible()) {
                return false;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    /** Advances the onboarding carousel. Returns false if no Next control is visible. */
    public boolean tapNext() throws Exception {
        enableVisibleKnowledgePermissionTogglesByGeometry();

        if (clickByExactTextOrDescription(ONBOARDING_PRIMARY_ACTION_LABELS)) {
            return true;
        }
        UiObject2 primary = findKnowledgeOnboardingPrimaryActionButtonByGeometry();
        if (primary == null) {
            return false;
        }
        clickObject(primary);
        return true;
    }

    /**
     * Legacy alias kept for compatibility with older scripts. Prefer {@link #tapNext()}.
     */
    public boolean tapNextIfPresent() throws Exception {
        return tapNext();
    }

    // ---------------------------------------------------------------------
    // Main Knowledge surface
    // ---------------------------------------------------------------------

    public boolean isMainVisible() {
        if (isKnowledgeOnboardingDialogVisibleByGeometry()) {
            return false;
        }
        return hasTextOrDescription(MAIN_ANCHORS)
                || isKnowledgeMainSurfaceVisibleByGeometry();
    }

    public boolean waitForMain(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isMainVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public QiraKnowledgePage waitForLoaded() throws Exception {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isOnboardingVisible()
                    || isMainVisible()
                    || isFabMenuVisible()
                    || isCreateMemoryDialogVisible()) {
                settle();
                return this;
            }
            mUtils.sleep(250L);
        }
        throw new IllegalStateException("Unable to detect the Knowledge experience");
    }

    // ---------------------------------------------------------------------
    // Categories / Tags dropdowns
    // ---------------------------------------------------------------------

    public boolean openCategoriesDropdown() throws Exception {
        return openDropdownByLabel(CATEGORIES_LABEL, 0);
    }

    public boolean isCategoriesDropdownVisible() {
        // The dropdown menu renders its options as a vertical list; requiring
        // at least two known entries avoids false positives with the main list.
        int hits = 0;
        for (String label : CATEGORY_OPTION_LABELS) {
            if (findByExactTextOrDescription(label) != null) {
                hits++;
            }
        }
        return hits >= 2 || isKnowledgeDropdownPopupVisibleByGeometry();
    }

    public boolean openTagsDropdown() throws Exception {
        return openDropdownByLabel(TAGS_LABEL, 1);
    }

    public boolean isTagsDropdownVisible() {
        int hits = 0;
        for (String label : TAG_OPTION_LABELS) {
            if (findByExactTextOrDescription(label) != null) {
                hits++;
            }
        }
        return hits >= 2 || isKnowledgeDropdownPopupVisibleByGeometry();
    }

    /**
     * Opens the dropdown triggered by the given Knowledge header label
     * ("Categories" or "Tags"). The header is a label + Dropdown glyph pair;
     * tapping the label (or its clickable ancestor) exposes the options list.
     */
    private boolean openDropdownByLabel(String label, int fallbackIndex) throws Exception {
        // Rule-set #1: desc before text. Knowledge dropdown headers
        // ("Categories" / "Tags") are labeled by a Compose TextField whose
        // content-desc carries the English label on localized builds.
        UiObject2 trigger = findByExactTextOrDescription(label);
        if (trigger == null) {
            trigger = findKnowledgeHeaderDropdownByIndex(fallbackIndex);
        }
        if (trigger == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(trigger);
        if (clickable == null) {
            clickable = findNearestClickableTile(trigger);
        }
        clickObject(clickable != null ? clickable : trigger);
        return true;
    }

    // ---------------------------------------------------------------------
    // More options overflow menu (top-right "More options" icon)
    // ---------------------------------------------------------------------

    public boolean openMoreOptionsMenu() throws Exception {
        UiObject2 moreOptions = findByStableDescription("More options");
        if (moreOptions == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(moreOptions);
        clickObject(clickable != null ? clickable : moreOptions);
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isMoreOptionsMenuVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isMoreOptionsMenuVisible() {
        return hasTextOrDescription(MORE_OPTIONS_LABELS)
                || isKnowledgeMoreOptionsPopupVisibleByGeometry();
    }
    public boolean openManageSettingsDialogFromMenu() throws Exception {
        if (!isMoreOptionsMenuVisible()) {
            return false;
        }
        UiObject2 option = findByExactTextOrDescription("Manage Settings");
        if (option == null) {
            option = findByTextOrDescription("Manage Settings");
        }
        if (option == null) {
            option = findKnowledgeMoreOptionsFirstItemByGeometry();
        }
        if (option == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(option);
        clickObject(clickable != null ? clickable : option);
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isManageSettingsDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return isManageSettingsDialogVisible();
    }

    public boolean isManageSettingsDialogVisible() {
        return hasTextOrDescription(MANAGE_SETTINGS_DIALOG_LABELS)
                && findManageSettingsDialogActionByGeometry() != null;
    }

    public boolean dismissManageSettingsDialog() throws Exception {
        if (!isManageSettingsDialogVisible()) {
            return false;
        }
        if (clickByExactTextOrDescription(MANAGE_SETTINGS_CANCEL_LABELS)) {
            return true;
        }
        UiObject2 action = findManageSettingsDialogCancelByGeometry();
        if (action == null) {
            return false;
        }
        clickObject(action);
        return true;
    }

    // ---------------------------------------------------------------------
    // List items / detail surface
    // ---------------------------------------------------------------------

    /**
     * Locates the first Knowledge list row. Rows are Compose views whose
     * {@code content-desc} encodes the title, date and sync state (Local /
     * Synced). We filter out the "Delete" glyphs and pick the top-most row.
     */
    public UiObject2 findFirstListItem() {
        UiObject2 best = null;
        int bestY = Integer.MAX_VALUE;
        for (UiObject2 obj : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                String desc = obj.getContentDescription();
                if (!looksLikeKnowledgeListItemDescription(desc)) {
                    continue;
                }
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                // Ignore the tiny per-row "Delete" glyph (it has the same
                // parent content-desc but a much smaller width).
                if (bounds.width() < mDevice.getDisplayWidth() / 2) {
                    continue;
                }
                if (bounds.top < bestY) {
                    bestY = bounds.top;
                    best = obj;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while we scan; skip.
            }
        }
        return best;
    }

    public boolean tapFirstListItem() throws Exception {
        UiObject2 item = findFirstListItem();
        if (item == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(item);
        if (clickable == null) {
            clickable = findNearestClickableTile(item);
        }
        clickObject(clickable != null ? clickable : item);
        return true;
    }

    public boolean tapFirstListItemDeleteIcon() throws Exception {
        UiObject2 item = findFirstListItem();
        if (item == null) {
            return false;
        }
        Rect rowBounds;
        try {
            rowBounds = item.getVisibleBounds();
        } catch (StaleObjectException stale) {
            return false;
        }
        if (rowBounds == null || rowBounds.isEmpty()) {
            return false;
        }
        UiObject2 delete = findDeleteIconInListRow(rowBounds);
        if (delete == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(delete);
        clickObject(clickable != null ? clickable : delete);
        return true;
    }

    public boolean hasAnyListItems() {
        return findFirstListItem() != null;
    }

    public boolean waitForDeleteItemDialog(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isDeleteItemDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return isDeleteItemDialogVisible();
    }

    public boolean cancelDeleteItemDialog() throws Exception {
        if (clickByTextOrDescription("Cancel")) {
            return true;
        }
        UiObject2 secondary = findKnowledgeDeleteDialogSecondaryActionByGeometry();
        if (secondary == null) {
            return false;
        }
        clickObject(secondary);
        return true;
    }

    public boolean areDetailTabsVisible() {
        int tabs = 0;
        for (String label : DETAIL_TAB_LABELS) {
            if (findByExactTextOrDescription(label) != null) {
                tabs++;
            }
        }
        return tabs >= 2;
    }

    public boolean waitForDetailTabs(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (areDetailTabsVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(300L);
        }
        return false;
    }

    public boolean selectDetailTab(String tab) throws Exception {
        // Rule-set #1: desc before text. Detail tabs expose the English
        // label in the tab's content-desc; localized builds translate the
        // visible text but not the desc, so desc lookup first keeps tab
        // navigation multilingual.
        UiObject2 target = findByExactDescription(tab);
        if (target == null) {
            target = findByExactText(tab);
        }
        if (target == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(target);
        clickObject(clickable != null ? clickable : target);
        return true;
    }

    // ---------------------------------------------------------------------
    // FAB / Create memory composer (best-effort; renders only when the FAB
    // button is present on the surface). Preserved for backward compatibility.
    // ---------------------------------------------------------------------

    /**
     * Opens the Knowledge FAB popup menu by tapping the "Menu" icon in the
     * bottom-right corner. The icon has {@code content-desc="Menu"} but lives
     * inside a non-clickable Compose view; we click the nearest clickable
     * ancestor so the whole affordance is activated.
     */
    public boolean openFabMenu() throws Exception {
        if (hasTextOrDescription(FAB_MENU_ANCHORS)) {
            return true;
        }
        UiObject2 menu = findByStableDescription("Menu");
        if (menu != null) {
            UiObject2 clickable = findClickableAncestor(menu);
            clickObject(clickable != null ? clickable : menu);
        } else if (!clickBottomRightClickable()) {
            return false;
        }
        long deadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < deadline) {
            if (isFabMenuVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isFabMenuVisible() {
        return hasTextOrDescription(FAB_MENU_ANCHORS)
                || isKnowledgeFabMenuVisibleByGeometry();
    }

    public boolean openCreateMemoryDialog() throws Exception {
        if (!isFabMenuVisible() && !openFabMenu()) {
            return false;
        }
        UiObject2 option = findByExactTextOrDescription("Create a memory");
        if (option == null) {
            option = findKnowledgeFabMenuOptionByIndex(0);
        }
        if (option == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(option);
        if (clickable == null) {
            clickable = findNearestClickableTile(option);
        }
        clickObject(clickable != null ? clickable : option);
        long deadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < deadline) {
            if (isCreateMemoryDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return false;
    }

    public boolean isCreateMemoryDialogVisible() {
        return hasTextOrDescription(CREATE_MEMORY_ANCHORS)
                || isKnowledgeCreateMemoryDialogVisibleByGeometry();
    }

    public boolean cancelCreateMemoryDialog() throws Exception {
        if (clickByTextOrDescription("Cancel")) {
            return true;
        }
        UiObject2 secondary = findKnowledgeCreateMemorySecondaryActionByGeometry();
        if (secondary == null) {
            return false;
        }
        clickObject(secondary);
        return true;
    }

    // ---------------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------------

    private UiObject2 findKnowledgeMoreOptionsFirstItemByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.left < (width * 45) / 100
                        || bounds.right > width - 8
                        || bounds.top < (height * 4) / 100
                        || bounds.bottom > (height * 32) / 100) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findManageSettingsDialogActionByGeometry() {
        UiObject2 best = null;
        int bestArea = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.left < (width * 10) / 100
                        || bounds.right > (width * 90) / 100
                        || bounds.top < (height * 48) / 100
                        || bounds.bottom > (height * 86) / 100) {
                    continue;
                }
                int area = bounds.width() * bounds.height();
                if (area > bestArea) {
                    bestArea = area;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findManageSettingsDialogCancelByGeometry() {
        UiObject2 best = null;
        int bestTop = Integer.MIN_VALUE;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.left < (width * 18) / 100
                        || bounds.right > (width * 82) / 100
                        || bounds.top < (height * 58) / 100
                        || bounds.top > (height * 82) / 100) {
                    continue;
                }
                if (bounds.top > bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }
    private UiObject2 findClickableAncestor(UiObject2 object) {
        UiObject2 current = object;
        for (int depth = 0; current != null && depth < 6; depth++) {
            if (current.isClickable()) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    private UiObject2 findDeleteIconInListRow(Rect rowBounds) {
        UiObject2 best = null;
        int bestDistance = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.centerY() < rowBounds.top || bounds.centerY() > rowBounds.bottom) {
                    continue;
                }
                if (bounds.centerX() < rowBounds.right - (width * 18) / 100) {
                    continue;
                }
                if (bounds.width() > (width * 16) / 100) {
                    continue;
                }
                int distance = Math.abs(bounds.centerY() - rowBounds.centerY());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isDeleteItemDialogVisible() {
        return countPopupTextRowsByGeometry(14, 86, 38, 64) >= 2
                && findKnowledgeDeleteDialogSecondaryActionByGeometry() != null;
    }

    private UiObject2 findKnowledgeDeleteDialogSecondaryActionByGeometry() {
        return findKnowledgeDeleteDialogActionByGeometry(false);
    }

    private UiObject2 findKnowledgeDeleteDialogActionByGeometry(boolean primary) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        boolean rtl = QiraStrings.getInstance().isCurrentLocaleRtl();
        UiObject2 best = null;
        int bestCenterX = primary == rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 52) / 100
                        || bounds.bottom > (height * 60) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 6) / 100
                        || bounds.width() > (width * 20) / 100) {
                    continue;
                }
                int centerX = bounds.centerX();
                if (primary == rtl) {
                    if (centerX < bestCenterX) {
                        bestCenterX = centerX;
                        best = object;
                    }
                } else if (centerX > bestCenterX) {
                    bestCenterX = centerX;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private boolean isKnowledgeOnboardingDialogVisibleByGeometry() {
        return findKnowledgeOnboardingTitleByGeometry() != null
                && countKnowledgeOnboardingActionButtonsByGeometry() >= 1;
    }

    private boolean isOnboardingPage2VisibleByGeometry() {
        if (countKnowledgeOnboardingToggleCandidatesByGeometry() < 1) {
            return false;
        }
        return findKnowledgeOnboardingPrimaryActionButtonByGeometry() != null
                || countKnowledgeOnboardingActionButtonsByGeometry() >= 1;
    }

    private boolean isKnowledgeMainSurfaceVisibleByGeometry() {
        if (findByStableDescription("More options") == null
                || findByStableDescription("Menu") == null
                || findByStableDescription("Search") == null) {
            return false;
        }
        return findKnowledgeHeaderDropdownByIndex(0) != null
                || findFirstListItem() != null;
    }

    private boolean isKnowledgeDropdownPopupVisibleByGeometry() {
        return countPopupTextRowsByGeometry(0, 30, 35, 60) >= 2;
    }

    private boolean isKnowledgeMoreOptionsPopupVisibleByGeometry() {
        return countPopupTextRowsByGeometry(65, 98, 5, 22) >= 2;
    }

    private boolean isKnowledgeFabMenuVisibleByGeometry() {
        return findKnowledgeFabMenuOptionByIndex(1) != null;
    }

    private UiObject2 findKnowledgeFabMenuOptionByIndex(int index) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> candidates = new ArrayList<>();
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                String desc = object.getContentDescription();
                String text = object.getText();
                if ((desc == null || desc.trim().isEmpty())
                        && (text == null || text.trim().isEmpty())) {
                    continue;
                }
                if (bounds.left < (width * 72) / 100
                        || bounds.right > (width * 98) / 100) {
                    continue;
                }
                if (bounds.top < (height * 72) / 100
                        || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 12) / 100
                        || bounds.width() > (width * 28) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                candidates.add(object);
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        if (candidates.size() <= index) {
            return null;
        }
        Collections.sort(candidates, new Comparator<UiObject2>() {
            @Override
            public int compare(UiObject2 a, UiObject2 b) {
                Rect ab = a.getVisibleBounds();
                Rect bb = b.getVisibleBounds();
                int dy = ab.top - bb.top;
                if (Math.abs(dy) > 16) {
                    return dy;
                }
                return ab.left - bb.left;
            }
        });
        return candidates.get(index);
    }

    private boolean isKnowledgeCreateMemoryDialogVisibleByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        boolean hasTitle = false;
        boolean hasPrompt = false;
        int actionLabels = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (!hasTitle
                        && bounds.left < (width * 25) / 100
                        && bounds.top >= (height * 32) / 100
                        && bounds.bottom <= (height * 40) / 100
                        && bounds.width() >= (width * 8) / 100
                        && bounds.width() <= (width * 25) / 100) {
                    hasTitle = true;
                    continue;
                }
                if (!hasPrompt
                        && bounds.left < (width * 15) / 100
                        && bounds.top >= (height * 46) / 100
                        && bounds.bottom <= (height * 54) / 100
                        && bounds.width() >= (width * 40) / 100) {
                    hasPrompt = true;
                    continue;
                }
                if (bounds.centerX() >= (width * 35) / 100
                        && bounds.centerX() <= (width * 65) / 100
                        && bounds.top >= (height * 58) / 100
                        && bounds.bottom <= (height * 70) / 100) {
                    actionLabels++;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return hasTitle && hasPrompt && actionLabels >= 2;
    }

    private UiObject2 findKnowledgeCreateMemorySecondaryActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        boolean rtl = QiraStrings.getInstance().isCurrentLocaleRtl();
        UiObject2 best = null;
        int bestCenterX = rtl ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 69) / 100
                        || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 12) / 100
                        || bounds.width() > (width * 36) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                int centerX = bounds.centerX();
                if (rtl) {
                    if (centerX > bestCenterX) {
                        bestCenterX = centerX;
                        best = object;
                    }
                } else if (centerX < bestCenterX) {
                    bestCenterX = centerX;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private boolean looksLikeKnowledgeListItemDescription(String desc) {
        if (desc == null) {
            return false;
        }
        String clean = desc.trim();
        if (clean.isEmpty()) {
            return false;
        }
        if (LIST_ITEM_DESC.matcher(clean).matches()) {
            return true;
        }
        String lower = clean.toLowerCase(Locale.ROOT);
        if (lower.equals("delete")
                || lower.equals("l\u00f6schen")
                || lower.equals("more options")
                || lower.equals("menu")
                || lower.equals("back")) {
            return false;
        }
        return lower.contains(".pdf")
                || lower.contains(".doc")
                || lower.contains(".txt")
                || lower.contains(".jpg")
                || lower.contains(".jpeg")
                || lower.contains(".png")
                || lower.contains(".mp3")
                || lower.contains(".wav")
                || lower.contains(".m4a");
    }

    private int countPopupTextRowsByGeometry(int minLeftPercent,
            int maxRightPercent,
            int minTopPercent,
            int maxBottomPercent) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int matches = 0;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.left < (width * minLeftPercent) / 100
                        || bounds.right > (width * maxRightPercent) / 100) {
                    continue;
                }
                if (bounds.top < (height * minTopPercent) / 100
                        || bounds.bottom > (height * maxBottomPercent) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 2) / 100
                        || bounds.width() > (width * 30) / 100) {
                    continue;
                }
                matches++;
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return matches;
    }

    private UiObject2 findKnowledgeHeaderDropdownByIndex(int index) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> candidates = new ArrayList<>();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 26) / 100
                        || bounds.bottom > (height * 40) / 100) {
                    continue;
                }
                if (bounds.left < (width * 2) / 100
                        || bounds.right > (width * 40) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 20) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (!hasDescendantContentDescription(object, "Dropdown", 2)) {
                    continue;
                }
                candidates.add(object);
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        if (candidates.size() <= index) {
            return null;
        }
        Collections.sort(candidates, new Comparator<UiObject2>() {
            @Override
            public int compare(UiObject2 a, UiObject2 b) {
                Rect ab = a.getVisibleBounds();
                Rect bb = b.getVisibleBounds();
                int dy = ab.top - bb.top;
                if (Math.abs(dy) > 24) {
                    return dy;
                }
                return ab.left - bb.left;
            }
        });
        return candidates.get(index);
    }

    private boolean hasDescendantContentDescription(UiObject2 object,
            String contentDescription,
            int maxDepth) {
        if (object == null || maxDepth <= 0 || contentDescription == null) {
            return false;
        }
        try {
            for (UiObject2 child : object.getChildren()) {
                String desc = child.getContentDescription();
                // Pseudo-locales (en-XM / en-XA / ar-XB) wrap every
                // content-desc with bidi isolate markers, so compare
                // against the stripped form to stay locale-safe.
                if (contentDescription.equals(QiraStrings.stripBidiControls(desc))) {
                    return true;
                }
                if (hasDescendantContentDescription(child, contentDescription, maxDepth - 1)) {
                    return true;
                }
            }
        } catch (StaleObjectException ignored) {
            // Node recycled while scanning descendants.
        }
        return false;
    }

    private UiObject2 findKnowledgeOnboardingTitleByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String text = object.getText();
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 36) / 100
                        || bounds.bottom > (height * 50) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100
                        || bounds.centerX() > (width * 65) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 10) / 100
                        || bounds.width() > (width * 50) / 100) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private int countKnowledgeOnboardingActionButtonsByGeometry() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 74) / 100
                        || bounds.bottom > (height * 92) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 28) / 100
                        || bounds.centerX() > (width * 72) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 55) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return count;
    }

    private int countKnowledgeOnboardingToggleCandidatesByGeometry() {
        int count = 0;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 42) / 100
                        || bounds.bottom > (height * 76) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 45) / 100
                        || bounds.centerX() > (width * 90) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 4) / 100
                        || bounds.width() > (width * 16) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 9) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return count;
    }

    private boolean enableVisibleKnowledgePermissionTogglesByGeometry() throws Exception {
        boolean changed = false;
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        for (UiObject2 toggle : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = toggle.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 12) / 100
                        || bounds.bottom > (height * 78) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 52) / 100
                        || bounds.centerX() > (width * 92) / 100) {
                    continue;
                }
                if (!toggle.isEnabled() || toggle.isChecked()) {
                    continue;
                }
                clickObject(toggle);
                changed = true;
                mUtils.sleep(120L);
            } catch (StaleObjectException ignored) {
                // Toggle list re-laid out while we were enabling permissions.
            }
        }
        return changed;
    }

    private UiObject2 findKnowledgeOnboardingPrimaryActionButtonByGeometry() {
        UiObject2 horizontalPrimary = findKnowledgePermissionPrimaryActionByGeometry();
        if (horizontalPrimary != null) {
            return horizontalPrimary;
        }

        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestTop = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 74) / 100
                        || bounds.bottom > (height * 86) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 35) / 100
                        || bounds.centerX() > (width * 65) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 22) / 100
                        || bounds.width() > (width * 45) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (bounds.top < bestTop) {
                    bestTop = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private UiObject2 findKnowledgePermissionPrimaryActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> candidates = new ArrayList<>();
        boolean rtl = avik.qira.utils.QiraStrings.getInstance().isCurrentLocaleRtl();

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 74) / 100
                        || bounds.bottom > (height * 88) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 18) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 4) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 45) / 100
                        || bounds.centerX() > (width * 80) / 100) {
                    continue;
                }
                candidates.add(object);
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }

        if (candidates.size() < 2) {
            return null;
        }

        UiObject2 best = null;
        int bestCenterX = rtl ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int yTolerance = (height * 3) / 100;
        for (UiObject2 candidate : candidates) {
            Rect cb;
            try {
                cb = candidate.getVisibleBounds();
            } catch (StaleObjectException stale) {
                continue;
            }
            if (cb == null || cb.isEmpty()) {
                continue;
            }
            boolean hasSibling = false;
            for (UiObject2 other : candidates) {
                if (other == candidate) {
                    continue;
                }
                Rect ob;
                try {
                    ob = other.getVisibleBounds();
                } catch (StaleObjectException stale) {
                    continue;
                }
                if (ob == null || ob.isEmpty()) {
                    continue;
                }
                if (Math.abs(ob.top - cb.top) <= yTolerance) {
                    hasSibling = true;
                    break;
                }
            }
            if (!hasSibling) {
                continue;
            }

            int centerX = cb.centerX();
            if (rtl) {
                if (centerX < bestCenterX) {
                    bestCenterX = centerX;
                    best = candidate;
                }
            } else if (centerX > bestCenterX) {
                bestCenterX = centerX;
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Fallback tile locator: scans all clickable Compose nodes in the Qira
     * package and returns the smallest one whose bounds contain the given
     * label's center point. Useful when none of the label's direct ancestors
     * are marked clickable (common with Compose surfaces).
     */
    private UiObject2 findNearestClickableTile(UiObject2 label) {
        Rect labelBounds;
        try {
            labelBounds = label.getVisibleBounds();
        } catch (StaleObjectException stale) {
            return null;
        }
        if (labelBounds == null || labelBounds.isEmpty()) {
            return null;
        }
        List<UiObject2> clickables =
                mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true));
        UiObject2 best = null;
        int bestArea = Integer.MAX_VALUE;
        for (UiObject2 obj : clickables) {
            try {
                Rect b = obj.getVisibleBounds();
                if (b == null || b.isEmpty()) {
                    continue;
                }
                if (!b.contains(labelBounds.centerX(), labelBounds.centerY())) {
                    continue;
                }
                int area = b.width() * b.height();
                if (area > 0 && area < bestArea) {
                    bestArea = area;
                    best = obj;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled; skip.
            }
        }
        return best;
    }

    /**
     * Fallback locator for the newer Discover layout where card labels are
     * localized text-only and no longer expose stable English descriptions.
     * Knowledge is the right-column card closest to vertical mid-screen.
     */
    private UiObject2 findKnowledgeTileByGeometry() {
        List<UiObject2> cards = findDiscoverCardsByGeometry();
        if (cards.isEmpty()) {
            return null;
        }

        int width = mDevice.getDisplayWidth();
        int targetY = (mDevice.getDisplayHeight() * 50) / 100;
        UiObject2 best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (UiObject2 card : cards) {
            try {
                Rect bounds = card.getVisibleBounds();
                if (bounds.centerX() <= (width * 55) / 100) {
                    continue;
                }
                int distance = Math.abs(bounds.centerY() - targetY);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = card;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while evaluating candidates.
            }
        }
        return best;
    }

    private List<UiObject2> findDiscoverCardsByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        List<UiObject2> cards = new ArrayList<>();

        int leftColumn = 0;
        int rightColumn = 0;
        int middleColumn = 0;

        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 10) / 100 || bounds.bottom > (height * 93) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 18) / 100 || bounds.width() > (width * 56) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 9) / 100 || bounds.height() > (height * 34) / 100) {
                    continue;
                }
                cards.add(object);
                int cx = bounds.centerX();
                if (cx < (width * 45) / 100) {
                    leftColumn++;
                } else if (cx > (width * 55) / 100) {
                    rightColumn++;
                } else {
                    middleColumn++;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }

        if (cards.size() < 4 || leftColumn < 2 || rightColumn < 2 || middleColumn > 1) {
            return new ArrayList<>();
        }

        Collections.sort(cards, new Comparator<UiObject2>() {
            @Override
            public int compare(UiObject2 a, UiObject2 b) {
                Rect ab = a.getVisibleBounds();
                Rect bb = b.getVisibleBounds();
                int dy = ab.top - bb.top;
                if (Math.abs(dy) > 40) {
                    return dy;
                }
                return ab.left - bb.left;
            }
        });
        return cards;
    }
}
