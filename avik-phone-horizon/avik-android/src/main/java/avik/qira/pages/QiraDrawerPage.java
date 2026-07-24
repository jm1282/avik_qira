package avik.qira.pages;

import android.graphics.Rect;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.StaleObjectException;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.List;

import avik.qira.utils.QiraConfig;

public class QiraDrawerPage extends BaseQiraPage {

    /**
     * Anchors that only appear on the opened side-drawer and nowhere else
     * in Qira (in particular: <em>not</em> on the Qira home tile grid, so
     * they cannot false-positive when {@code openDrawer()} failed to
     * actually open the drawer). "Creator Zone" and "Knowledge" are
     * intentionally excluded because they are also top-level home tiles.
     */
    private static final String[] DRAWER_ANCHORS = {
            "Discover",
            "Help & Support",
            "No chats found",
            "All Chats",
            "Recent chats",
            "Manage chats"
    };

    /**
     * Localized labels for the "Settings" drawer entry. The QiraStrings
     * runtime catalog is supposed to expand the English anchor to the
     * locale-specific label, but on some locales (notably es-US, which
     * uses "Ajustes" rather than "Configuración") the expansion misses
     * the drawer-entry-specific resource id, so we fall back to this
     * static alias list before bailing out.
     */
    private static final String[] SETTINGS_DRAWER_LABELS = {
            "Settings",
            "Ajustes",
            "Configuración",
            "Configuracion",
            "Configurações",
            "Configuracoes",
            "Einstellungen",
            "Paramètres",
            "Parametres",
            "Impostazioni",
            "Ustawienia",
            "Setări",
            "Setari",
            "設定",
            "设置"
    };

    public QiraDrawerPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraDrawerPage waitForLoaded() throws Exception {
        long deadline = System.currentTimeMillis() + 5000L;
        while (System.currentTimeMillis() < deadline) {
            UiObject2 anchor = findByTextOrDescription(DRAWER_ANCHORS);
            if (anchor != null || isDrawerVisibleByGeometry()) {
                settle();
                return this;
            }
            mUtils.sleep(250L);
        }
        throw new IllegalStateException("Unable to detect the Qira navigation drawer");
    }

    public QiraSettingsPage openSettings() throws Exception {
        // The drawer is anchored to the left edge of the display. Restrict
        // the "Settings" search to the left half of the screen so we never
        // pick up a "Settings" label rendered by an unrelated app or by a
        // home-tile that's still visible behind a half-open drawer.
        UiObject2 target = findSettingsInDrawer();
        if (target != null) {
            clickObject(target);
        } else {
            // Fall back to the generic text/desc click against every known
            // localized form of "Settings" before giving up.
            if (!clickByTextOrDescription(SETTINGS_DRAWER_LABELS)) {
                UiObject2 fallback = findBottomDrawerEntryByGeometry();
                if (fallback == null) {
                    return null;
                }
                clickObject(fallback);
            }
        }
        settle();
        return new QiraSettingsPage(mDevice, mConfig).waitForLoaded();
    }

    /**
     * Returns the "Settings" drawer entry restricted to the left half of the
     * display (where the drawer lives), or {@code null} if no such element
     * is visible. Matching iterates over every alias (English + translated
     * forms) returned by {@link #localizeLabels(String...)}, so this works
     * unchanged in non-English locales as long as "Settings" is present in
     * the catalog or reachable by the runtime resource scan.
     */
    private UiObject2 findSettingsInDrawer() {
        int maxX = mDevice.getDisplayWidth() / 2;
        for (String anchor : SETTINGS_DRAWER_LABELS) {
            UiObject2 stableIcon = findStableDescriptionInLeftHalf(anchor, maxX);
            if (stableIcon != null) {
                return stableIcon;
            }
        }
        for (String label : localizeLabels(SETTINGS_DRAWER_LABELS)) {
            UiObject2 hit = findSettingsInLeftHalf(label, maxX);
            if (hit != null) {
                return hit;
            }
        }
        return null;
    }

    private UiObject2 findStableDescriptionInLeftHalf(String description, int maxX) {
        List<UiObject2> candidates = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(description)));
        for (UiObject2 obj : candidates) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) continue;
                if (bounds.centerX() <= maxX) return obj;
            } catch (StaleObjectException ignored) {
            }
        }
        return null;
    }

    private UiObject2 findSettingsInLeftHalf(String label, int maxX) {
        List<UiObject2> candidates = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).text(exactPatternForLabel(label)));
        for (UiObject2 obj : candidates) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) continue;
                if (bounds.centerX() <= maxX) return obj;
            } catch (StaleObjectException ignored) {
            }
        }
        List<UiObject2> descCandidates = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(label)));
        for (UiObject2 obj : descCandidates) {
            try {
                Rect bounds = obj.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) continue;
                if (bounds.centerX() <= maxX) return obj;
            } catch (StaleObjectException ignored) {
            }
        }
        return null;
    }

    /**
     * Last-resort fallback used when the localized drawer label for Settings
     * cannot be matched via text/description. Settings is the lowest left-pane
     * navigation row in the drawer list on current builds.
     */
    private UiObject2 findBottomDrawerEntryByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        UiObject2 best = null;
        int bestBottom = Integer.MIN_VALUE;
        for (UiObject2 object : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true))) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.centerX() > (width * 48) / 100) {
                    continue;
                }
                if (bounds.top < (height * 22) / 100 || bounds.bottom > (height * 90) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 14) / 100 || bounds.width() > (width * 48) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                if (bounds.bottom > bestBottom) {
                    bestBottom = bounds.bottom;
                    best = object;
                }
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return best;
    }

    private boolean isDrawerVisibleByGeometry() {
        int width = mDevice.getDisplayWidth();
        int height = mDevice.getDisplayHeight();
        int leftEntries = 0;
        List<UiObject2> clickables = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clickable(true));
        for (UiObject2 object : clickables) {
            try {
                Rect bounds = object.getVisibleBounds();
                if (bounds == null || bounds.isEmpty()) {
                    continue;
                }
                if (bounds.centerX() > (width * 45) / 100) {
                    continue;
                }
                if (bounds.top < (height * 10) / 100 || bounds.bottom > (height * 72) / 100) {
                    continue;
                }
                if (bounds.width() < (width * 15) / 100 || bounds.width() > (width * 45) / 100) {
                    continue;
                }
                if (bounds.height() < (height * 3) / 100 || bounds.height() > (height * 12) / 100) {
                    continue;
                }
                leftEntries++;
            } catch (StaleObjectException ignored) {
                // Node recycled while scanning.
            }
        }
        return leftEntries >= 3;
    }

    public QiraHomePage closeDrawer() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraHomePage(mDevice, mConfig).waitForLoaded();
    }
}
