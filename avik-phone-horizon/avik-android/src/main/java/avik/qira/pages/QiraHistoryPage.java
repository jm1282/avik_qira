package avik.qira.pages;

import android.graphics.Rect;
import android.widget.ListView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import avik.qira.utils.QiraConfig;

/**
 * Page object for the Motorola Qira "Chat History" surface. Chat History is
 * reached from the Qira home tile grid (the "Chat History" tile) or from the
 * side drawer, and exposes:
 *
 * <ul>
 *     <li>A main list surface with a hero title ("Chat History"), subtitle
 *         ("View and manage all your chats"), a search field ("What are you
 *         looking for?") and a "Manage chats" chip that enters multi-select
 *         mode.</li>
 *     <li>A multi-select manage mode exposing Back / Select all / Delete
 *         controls with per-row checkboxes.</li>
 *     <li>A per-chat detail surface (the same Focus Zone chat surface that
 *         renders Chat conversations) with Back, More options, the shared
 *         "What are you looking for?" composer and a Settings / History /
 *         Feedback overflow menu.</li>
 * </ul>
 *
 * <p>The tile on the home grid has no resource-id, so we match by the label
 * "Chat History" and click the nearest clickable ancestor. List rows are also
 * Compose views without a resource-id, so we pick the top-most wide clickable
 * below the Manage chats header.
 */
public class QiraHistoryPage extends BaseQiraPage {

    private static final long DEFAULT_TIMEOUT_MS = 10000L;

    private static final String TILE_CHAT_HISTORY = "Chat History";

    /** Labels that identify the Chat History tile on the Qira home surface. */
    private static final String[] HOME_TILE_LABELS = {
            "Chat History"
    };

    /**
     * Legacy anchors used by {@link #waitForLoaded()} to detect either the
     * Chat History list surface or any compatible "History" drawer target.
     */
    private static final String[] HISTORY_ANCHORS = {
            "Chat History",
            "History",
            "Recents",
            "Past chats",
            "Delete all",
            "Manage chats"
    };

    /**
     * Labels that identify the main Chat History list surface.
     *
     * <p><b>IMPORTANT</b>: this dictionary deliberately omits the
     * stand-alone hero title ("Chat History" / "Chatverlauf" / ...).
     * The Qira home tile grid has a "Chat History" tile whose label
     * is the EXACT same string as the list-surface hero title; if
     * the title alone is matched, the visibility detector returns
     * true even when the user is still on the tile grid (the bug
     * the en-XM regression exposed). The remaining anchors -
     * subtitle, search placeholder, and the "Manage chats" chip -
     * are all unique to the list surface and never appear on the
     * tile grid, so they are a sound list-vs-tile discriminator.
     *
     * <p>Each anchor is paired with localized variants for every
     * locale the master-suite drives so the detector returns true
     * on every locale, not just English. When a locale's translated
     * string is missing from this list, the
     * {@link #waitForMainOrLocalized(long)} helper falls back to a
     * geometry-based "Manage chats chip in the upper third" probe
     * that works regardless of label translation.
     */
    private static final String[] MAIN_ANCHORS = {
            // English (en-XM / en-US / en-GB / en-IN / en-XA).
            "View and manage all your chats",
            "What are you looking for?",
            "Manage chats",
            // German (de / de-DE).
            "Alle Chats anzeigen und verwalten",
            "Wonach suchst du?",
            "Chats verwalten",
            // Spanish (es / es-ES / es-US).
            "Ver y gestionar todos tus chats",
            "\u00bfQu\u00e9 est\u00e1s buscando?",
            "Administrar chats",
            "Gestionar chats",
            // French (fr / fr-FR).
            "Voir et g\u00e9rer toutes vos conversations",
            "Que recherchez-vous\u00a0?",
            "G\u00e9rer les conversations",
            "G\u00e9rer les chats",
            // Italian (it / it-IT).
            "Visualizza e gestisci tutte le tue chat",
            "Cosa stai cercando?",
            "Gestisci chat",
            // Portuguese (pt-BR).
            "Veja e gerencie todos os seus chats",
            "O que voc\u00ea est\u00e1 procurando?",
            "Gerenciar chats",
            // Polish (pl / pl-PL).
            "Wy\u015bwietl wszystkie czaty i zarz\u0105dzaj nimi",
            "Czego szukasz?",
            "Zarz\u0105dzaj czatami",
            // Romanian (ro / ro-RO).
            "Vizualizeaz\u0103 \u0219i gestioneaz\u0103 toate conversa\u021biile",
            "Ce cau\u021bi?",
            "Gestioneaz\u0103 conversa\u021biile",
            // Japanese (ja-JP).
            "\u3059\u3079\u3066\u306e\u30c1\u30e3\u30c3\u30c8\u3092\u8868\u793a\u3057\u3066\u7ba1\u7406",
            "\u4f55\u3092\u304a\u63a2\u3057\u3067\u3059\u304b\uff1f",        // 何をお探しですか？
            "\u30c1\u30e3\u30c3\u30c8\u3092\u7ba1\u7406",   // チャットを管理
            // Chinese Simplified (zh-CN).
            "\u67e5\u770b\u5e76\u7ba1\u7406\u6240\u6709\u804a\u5929",         // 查看并管理所有聊天
            "\u60a8\u5728\u67e5\u627e\u4ec0\u4e48\uff1f",                     // 您在查找什么？
            "\u7ba1\u7406\u804a\u5929"                                        // 管理聊天
    };

    /** Labels that identify the Manage chats multi-select mode. */
    private static final String[] MANAGE_MODE_ANCHORS = {
            "Select all",
            "Delete"
    };

    /**
     * Localized labels for the "Manage chats" chip on the Chat History
     * top bar. Multi-locale aliases ("Verwalten", "G\u00e9rer", ...)
     * cover the en-XM / de-DE / fr-FR / es / it / pt / pl / ro / ja / zh
     * master-suite locales. Geometry-based fallback in
     * {@link #findManageChatsChipByGeometry()} catches builds that
     * reword the label entirely.
     */
    private static final String[] MANAGE_CHATS_LABELS = {
            "Manage chats",
            "Manage Chats",
            "Manage",
            "Verwalten",
            "Chats verwalten",
            "G\u00e9rer",
            "G\u00e9rer les conversations",
            "G\u00e9rer les chats",
            "Administrar",
            "Administrar chats",
            "Gestionar",
            "Gestionar chats",
            "Gestisci",
            "Gestisci chat",
            "Gerenciar",
            "Gerenciar chats",
            "Zarz\u0105dzaj",
            "Zarz\u0105dzaj czatami",
            "Gestioneaz\u0103",
            "Gestioneaz\u0103 conversa\u021biile",
            "\u7ba1\u7406",         // ja: 管理
            "\u7ba1\u7406\u3059\u308b", // ja: 管理する
            "\u30c1\u30e3\u30c3\u30c8\u3092\u7ba1\u7406", // ja: チャットを管理
            "\u7ba1\u7406\u804a\u5929"   // zh: 管理聊天
    };

    /**
     * Localized labels for the "Delete" action that appears on the
     * Manage chats action bar after one or more rows are selected.
     * "Delete" is also the per-row trash glyph desc, so the geometry
     * fallback prefers a wider hit-target near the bottom-right.
     */
    private static final String[] DELETE_ACTION_LABELS = {
            "Delete",
            "L\u00f6schen",
            "Supprimer",
            "Eliminar",
            "Borrar",
            "Elimina",
            "Excluir",
            "Apagar",
            "Usu\u0144",
            "\u015aterge",
            "\u524a\u9664",  // ja: 削除
            "\u5220\u9664"   // zh: 删除
    };

    /**
     * Localized labels for the in-list "Back" affordance that exits
     * Manage chats mode. The top-bar Back icon is intentionally NOT
     * tapped (it can leave Qira entirely on this build); we look for
     * the in-list label first and fall back to geometry only after.
     */
    private static final String[] BACK_ACTION_LABELS = {
            "Back",
            "Zur\u00fcck",
            "Retour",
            "Atr\u00e1s",
            "Volver",
            "Indietro",
            "Voltar",
            "Wstecz",
            "\u00cenapoi",
            "\u623b\u308b",  // ja: 戻る
            "\u8fd4\u56de"   // zh: 返回
    };

    /**
     * Localized labels for the "Cancel" button on the per-chat Delete
     * confirmation dialog. Used by {@link #cancelDeleteChatDialog()} so
     * downstream sub-flows inherit the same chat row.
     */
    private static final String[] CANCEL_LABELS = {
            "Cancel",
            "Abbrechen",
            "Annuler",
            "Cancelar",
            "Annulla",
            "Anuluj",
            "Anuleaz\u0103",
            "\u30ad\u30e3\u30f3\u30bb\u30eb",  // ja: キャンセル
            "\u53d6\u6d88"                       // zh: 取消
    };

    /**
     * Labels that identify the per-chat detail surface. Chat detail re-uses the
     * Focus Zone chat composer, so we look for the composer placeholder and its
     * bottom-row affordances together with the Back / More options header.
     */
    private static final String[] CHAT_DETAIL_ANCHORS = {
            "What are you looking for?",
            "Send",
            "Microphone",
            "Attach"
    };

    /** Options exposed by the chat detail More options popup. */
    private static final String[] CHAT_MORE_OPTIONS_LABELS = {
            "Settings",
            "History",
            "Feedback"
    };

    public QiraHistoryPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    /**
     * Legacy entry point retained for drawer-based navigation to Chat
     * History. New capture scripts should prefer {@link #waitForMain(long)}
     * which matches the exact list surface reached from the home tile.
     */
    public QiraHistoryPage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(DEFAULT_TIMEOUT_MS, HISTORY_ANCHORS);
        if (anchor == null
                && waitForClass("androidx.recyclerview.widget.RecyclerView", 3000L) == null
                && waitForClass(ListView.class.getName(), 3000L) == null) {
            throw new IllegalStateException("Unable to detect the Qira history view");
        }
        settle();
        return this;
    }

    public QiraHomePage goBackHome() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraHomePage(mDevice, mConfig);
    }

    // ---------------------------------------------------------------------
    // Home tile navigation
    // ---------------------------------------------------------------------

    public boolean isHomeTileVisible() {
        return hasTextOrDescription(HOME_TILE_LABELS)
                || findChatHistoryTileByGeometry() != null;
    }

    /**
     * Taps the Chat History tile on the Qira home surface. The inner label is a
     * non-clickable {@code TextView}; we walk up to the nearest clickable
     * ancestor (or the closest clickable tile) so the whole card is activated.
     */
    public void tapChatHistoryTile() throws Exception {
        UiObject2 label = findByExactTextOrDescription(TILE_CHAT_HISTORY);
        if (label == null) {
            UiObject2 fallback = findChatHistoryTileByGeometry();
            if (fallback == null) {
                throw new IllegalStateException(
                        "Unable to locate the Chat History tile on the Qira home");
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
    // Main Chat History list surface
    // ---------------------------------------------------------------------

    public boolean isMainVisible() {
        return hasTextOrDescription(MAIN_ANCHORS);
    }

    public boolean waitForMain(long timeoutMs) throws Exception {
        return waitForTextOrDescription(timeoutMs, MAIN_ANCHORS) != null;
    }

    /**
     * Same as {@link #waitForMain(long)} plus a geometry-based fallback
     * for builds where every text anchor in {@link #MAIN_ANCHORS} is
     * either localized in a way the dictionary does not yet cover or
     * rendered as a non-text Compose node. The fallback returns true
     * when ALL of the following are true:
     *
     * <ol>
     *   <li>Qira is the foreground package.</li>
     *   <li>{@link #findManageChatsChipByGeometry()} matches a chip
     *       in the upper third of the display - this is the
     *       "Manage chats" pill that is visually distinctive on
     *       every locale (icon + text inside a rounded pill).</li>
     * </ol>
     *
     * <p>The combination is intentionally strict: a foreground bubble
     * bar dialog also has clickable rounded buttons in the upper third,
     * but those live inside a different Compose root that does not
     * include the Qira list-search field, so the chip search will miss
     * them. That keeps the false-positive rate low while letting locale
     * runs whose dictionary entry happens to be missing still detect
     * the right surface.
     */
    public boolean waitForMainOrLocalized(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + Math.max(timeoutMs, 100L);
        while (System.currentTimeMillis() < deadline) {
            if (isMainVisible()) {
                return true;
            }
            try {
                String pkg = mDevice.getCurrentPackageName();
                if (mConfig.getPackageName().equals(pkg)
                        && findManageChatsChipByGeometry() != null) {
                    return true;
                }
            } catch (Throwable ignored) {
                // Stale Compose nodes can throw transiently; retry.
            }
            mUtils.sleep(250L);
        }
        return isMainVisible();
    }

    // ---------------------------------------------------------------------
    // Manage chats (multi-select) mode
    // ---------------------------------------------------------------------

    /**
     * Opens the "Manage chats" multi-select mode by tapping the chip at the
     * top of the list. Returns false if the chip is not found.
     */
    public boolean openManageChats() throws Exception {
        UiObject2 chip = findByExactTextOrDescription(MANAGE_CHATS_LABELS);
        if (chip == null) {
            chip = findManageChatsChipByGeometry();
        }
        if (chip == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(chip);
        if (clickable == null) {
            clickable = findNearestClickableTile(chip);
        }
        clickObject(clickable != null ? clickable : chip);
        return waitForTextOrDescription(DEFAULT_TIMEOUT_MS, MANAGE_MODE_ANCHORS) != null;
    }

    public boolean isManageModeVisible() {
        return hasTextOrDescription(MANAGE_MODE_ANCHORS);
    }

    public boolean selectFirstManageChat() throws Exception {
        Rect row = findFirstManageChatRowBounds();
        if (row == null) {
            UiObject2 checkbox = findFirstManageChatCheckboxBelowHeader();
            return checkbox != null && clickObjectSafely(checkbox);
        }

        UiObject2 checkbox = findManageChatCheckboxInRow(row);
        if (checkbox != null && clickObjectSafely(checkbox)) {
            return true;
        }

        tap(row.left + Math.max(24, row.width() / 20), row.centerY());
        return true;
    }

    public boolean tapFirstManageChatsCheckbox() throws Exception {
        return selectFirstManageChat();
    }

    public boolean tapManageChatsDeleteAction() throws Exception {
        UiObject2 delete = findByExactTextOrDescription(DELETE_ACTION_LABELS);
        if (delete != null) {
            UiObject2 clickable = findClickableAncestor(delete);
            if (clickObjectSafely(clickable != null ? clickable : delete)) {
                return true;
            }
        }

        UiObject2 action = findDeleteActionByGeometry();
        if (action != null) {
            return clickObjectSafely(action);
        }
        return false;
    }

    public boolean waitForDeleteChatDialog(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isDeleteChatDialogVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(250L);
        }
        return isDeleteChatDialogVisible();
    }

    public boolean cancelDeleteChatDialog() throws Exception {
        // Try every localized "Cancel" label (multi-locale CANCEL_LABELS).
        // Compose builds expose the label both as text and as content-desc;
        // clickByExactTextOrDescription covers both routes.
        if (clickByExactTextOrDescription(CANCEL_LABELS)) {
            return true;
        }
        if (clickByTextOrDescription(CANCEL_LABELS)) {
            return true;
        }
        UiObject2 action = findDeleteDialogSecondaryActionByGeometry();
        if (action == null) {
            return false;
        }
        return clickObjectSafely(action);
    }

    /**
     * Exits Manage chats mode by tapping the in-list "Back" label (the top-bar
     * Back icon on this surface can accidentally leave Qira entirely).
     * Returns false if the label is not present.
     */
    public boolean exitManageChats() throws Exception {
        UiObject2 back = waitForExactTextOrDescription(DEFAULT_TIMEOUT_MS / 2, BACK_ACTION_LABELS);
        if (back == null) {
            back = findManageChatsBackActionByGeometry();
        }
        if (back == null) {
            return false;
        }
        try {
            UiObject2 clickable = findClickableAncestor(back);
            return clickObjectSafely(clickable != null ? clickable : back);
        } catch (StaleObjectException stale) {
            back = findByExactTextOrDescription(BACK_ACTION_LABELS);
            if (back == null) {
                back = findManageChatsBackActionByGeometry();
            }
            if (back == null) {
                return false;
            }
            UiObject2 clickable = findClickableAncestor(back);
            return clickObjectSafely(clickable != null ? clickable : back);
        }
    }

    // ---------------------------------------------------------------------
    // Chat list items / detail surface
    // ---------------------------------------------------------------------

    /**
     * Locates the first chat list row on the main Chat History surface. Rows
     * are Compose views without a resource-id; we filter all wide clickables
     * located below the Manage chats header and pick the top-most one.
     */
    public UiObject2 findFirstChatItem() {
        int minY = resolveListTopBound();
        List<UiObject2> candidates =
                mDevice.findObjects(By.pkg(mConfig.getPackageName()).clickable(true));
        UiObject2 best = null;
        int bestY = Integer.MAX_VALUE;
        int minWidth = mDevice.getDisplayWidth() / 2;
        int maxHeight = mDevice.getDisplayHeight();
        for (UiObject2 obj : candidates) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.width() < minWidth) {
                    continue;
                }
                // Ignore the full-screen root clickable that covers the whole
                // surface (its height matches the display).
                if (bounds.height() >= maxHeight - 10) {
                    continue;
                }
                if (bounds.top < minY) {
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

    public boolean tapFirstChatItem() throws Exception {
        UiObject2 item = findFirstChatItem();
        if (item == null) {
            return false;
        }
        clickObject(item);
        return true;
    }

    public boolean hasAnyChatItems() {
        return findFirstChatItem() != null;
    }

    public boolean isChatDetailVisible() {
        int hits = 0;
        for (String label : CHAT_DETAIL_ANCHORS) {
            if (findByExactTextOrDescription(label) != null) {
                hits++;
            }
        }
        // Require at least two anchors to avoid false positives with the main
        // Chat History list (which also renders "What are you looking for?").
        return hits >= 2 || isChatDetailChromeVisibleByGeometry();
    }

    public boolean waitForChatDetail(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isChatDetailVisible()) {
                settle();
                return true;
            }
            mUtils.sleep(300L);
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // Chat detail More options overflow (Settings / History / Feedback)
    // ---------------------------------------------------------------------

    public boolean openChatMoreOptions() throws Exception {
        UiObject2 moreOptions = findByStableDescription("More options");
        if (moreOptions == null) {
            return false;
        }
        UiObject2 clickable = findClickableAncestor(moreOptions);
        clickObject(clickable != null ? clickable : moreOptions);
        return waitForTextOrDescription(DEFAULT_TIMEOUT_MS, CHAT_MORE_OPTIONS_LABELS) != null;
    }

    public boolean isChatMoreOptionsVisible() {
        return hasTextOrDescription(CHAT_MORE_OPTIONS_LABELS);
    }

    // ---------------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------------

    /**
     * Computes the y-coordinate below which chat list rows live. Prefers the
     * bottom edge of the "Manage chats" chip if present; otherwise falls back
     * to ~40% of the display height.
     */
    private int resolveListTopBound() {
        UiObject2 manage = findByExactTextOrDescription(MANAGE_CHATS_LABELS);
        if (manage == null) {
            manage = findManageChatsChipByGeometry();
        }
        if (manage != null) {
            try {
                Rect bounds = manage.getVisibleBounds();
                if (bounds != null && !bounds.isEmpty()) {
                    return bounds.bottom + 20;
                }
            } catch (StaleObjectException ignored) {
                // Fall through to the default.
            }
        }
        return (int) (mDevice.getDisplayHeight() * 0.4);
    }

    private Rect findFirstManageChatRowBounds() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int minTop = findManageListTopBound();
        Rect best = null;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < minTop || bounds.bottom > (height * 98) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 65) / 100
                        || bounds.height() < (height * 5) / 100
                        || bounds.height() > (height * 16) / 100) {
                    continue;
                }
                if (best == null || bounds.top < best.top) {
                    best = bounds;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findFirstManageChatCheckboxBelowHeader() {
        int minTop = findManageListTopBound();
        UiObject2 best = null;
        int bestY = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < minTop) {
                    continue;
                }
                if (bounds.top < bestY) {
                    bestY = bounds.top;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private int findManageListTopBound() {
        int height = mDevice.getDisplayHeight();
        int bestBottom = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top > (height * 35) / 100
                        && bounds.top < (height * 60) / 100) {
                    bestBottom = Math.min(bestBottom, bounds.bottom);
                }
            } catch (StaleObjectException ignored) {
            }
        }
        if (bestBottom < Integer.MAX_VALUE) {
            return bestBottom + Math.max(12, height / 200);
        }
        return (height * 52) / 100;
    }

    private UiObject2 findManageChatCheckboxInRow(Rect rowBounds) {
        UiObject2 best = null;
        int bestDistance = Integer.MAX_VALUE;
        int width = mDevice.getDisplayWidth();
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).checkable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.centerY() < rowBounds.top || bounds.centerY() > rowBounds.bottom) {
                    continue;
                }
                if (bounds.centerX() > rowBounds.left + (width * 18) / 100) {
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

    private UiObject2 findDeleteActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestRight = Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 35) / 100
                        || bounds.bottom > (height * 58) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 68) / 100
                        || bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 28) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (bounds.right > bestRight) {
                    bestRight = bounds.right;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private boolean isDeleteChatDialogVisible() {
        return countDeleteDialogTextRowsByGeometry() >= 4
                && findDeleteDialogSecondaryActionByGeometry() != null;
    }

    private UiObject2 findManageChatsChipByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestX = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 30) / 100
                        || bounds.bottom > (height * 50) / 100) {
                    continue;
                }
                if (bounds.centerX() < (width * 55) / 100
                        || bounds.centerX() > (width * 90) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 12) / 100
                        || bounds.width() > (width * 32) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100
                        || bounds.height() > (height * 10) / 100) {
                    continue;
                }
                if (bounds.left < bestX) {
                    bestX = bounds.left;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findManageChatsBackActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestLeft = Integer.MAX_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 35) / 100
                        || bounds.bottom > (height * 58) / 100) {
                    continue;
                }
                if (bounds.centerX() > (width * 36) / 100
                        || bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 28) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                if (bounds.left < bestLeft) {
                    bestLeft = bounds.left;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private UiObject2 findDeleteDialogSecondaryActionByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestCenterX = Integer.MAX_VALUE;
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
                if (bounds.top < (height * 50) / 100
                        || bounds.bottom > (height * 64) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100
                        || bounds.width() > (width * 24) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 1) / 100
                        || bounds.height() > (height * 5) / 100) {
                    continue;
                }
                if (bounds.centerX() < bestCenterX) {
                    bestCenterX = bounds.centerX();
                    best = object;
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private int countDeleteDialogTextRowsByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int count = 0;
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
                if (bounds.top < (height * 30) / 100
                        || bounds.bottom > (height * 68) / 100) {
                    continue;
                }
                if (bounds.left < (width * 14) / 100
                        || bounds.right > (width * 86) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 8) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 1) / 100
                        || bounds.height() > (height * 8) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private boolean isChatDetailChromeVisibleByGeometry() {
        return findChatDetailBackByGeometry() != null
                && findChatDetailMoreOptionsByGeometry() != null;
    }

    private UiObject2 findChatDetailBackByGeometry() {
        return findChatDetailHeaderIconByGeometry(true);
    }

    private UiObject2 findChatDetailMoreOptionsByGeometry() {
        return findChatDetailHeaderIconByGeometry(false);
    }

    private UiObject2 findChatDetailHeaderIconByGeometry(boolean left) {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestEdge = left ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(By.pkg(mConfig.getPackageName()))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.top < (height * 46) / 100
                        || bounds.bottom > (height * 54) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 4) / 100
                        || bounds.width() > (width * 10) / 100
                        || bounds.height() < (height * 2) / 100
                        || bounds.height() > (height * 5) / 100) {
                    continue;
                }
                if (left) {
                    if (bounds.centerX() > (width * 20) / 100) {
                        continue;
                    }
                    if (bounds.left < bestEdge) {
                        bestEdge = bounds.left;
                        best = object;
                    }
                } else {
                    if (bounds.centerX() < (width * 80) / 100) {
                        continue;
                    }
                    if (bounds.right > bestEdge) {
                        bestEdge = bounds.right;
                        best = object;
                    }
                }
            } catch (StaleObjectException ignored) {
            }
        }
        return best;
    }

    private int countChatDetailTitleRowsByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int count = 0;
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
                if (bounds.left < (width * 45) / 100
                        || bounds.right > (width * 95) / 100) {
                    continue;
                }
                count++;
            } catch (StaleObjectException ignored) {
            }
        }
        return count;
    }

    private boolean clickObjectSafely(UiObject2 object) throws Exception {
        if (object == null) {
            return false;
        }
        try {
            Rect bounds = object.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                return false;
            }
            mDevice.click(bounds.centerX(), bounds.centerY());
            settle();
            return true;
        } catch (StaleObjectException stale) {
            return false;
        }
    }

    private UiObject2 findClickableAncestor(UiObject2 object) {
        UiObject2 current = object;
        try {
            for (int depth = 0; current != null && depth < 6; depth++) {
                if (current.isClickable()) {
                    return current;
                }
                current = current.getParent();
            }
        } catch (StaleObjectException ignored) {
            return null;
        }
        return null;
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
     * Fallback locator for Discover layouts where "Chat History" is localized
     * and no stable English text/description is exposed. Picks the left-column
     * card closest to the lower content band (historically Chat History / Chat).
     */
    private UiObject2 findChatHistoryTileByGeometry() {
        List<UiObject2> cards = findDiscoverCardsByGeometry();
        if (cards.isEmpty()) {
            return null;
        }

        int width = mDevice.getDisplayWidth();
        int targetY = (mDevice.getDisplayHeight() * 72) / 100;
        UiObject2 best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (UiObject2 card : cards) {
            try {
                Rect bounds = card.getVisibleBounds();
                if (bounds.centerX() >= (width * 45) / 100) {
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
