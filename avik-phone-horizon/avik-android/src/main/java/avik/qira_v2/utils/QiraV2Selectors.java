package avik.qira_v2.utils;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import avik.qira.utils.QiraStrings;

public final class QiraV2Selectors {

    private QiraV2Selectors() {
    }

    public static UiObject2 findByResourceId(
            UiDevice device,
            String packageName,
            String... resourceIds) {
        if (device == null || resourceIds == null) {
            return null;
        }
        for (String resourceId : resourceIds) {
            if (resourceId == null || resourceId.trim().isEmpty()) {
                continue;
            }
            UiObject2 object = safeFind(device, By.res(toFullResourceId(packageName, resourceId)));
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    public static UiObject2 findByQiraStringIdsForEnglish(
            UiDevice device,
            String packageName,
            Logger logger,
            String... englishAnchors) {
        QiraStrings.ResolvedQiraStringId[] ids =
                QiraStrings.getInstance().resolveQiraStringIdsForEnglish(englishAnchors);
        List<String> resourceNames = new ArrayList<>();
        for (QiraStrings.ResolvedQiraStringId id : ids) {
            if (id.getEntryName() != null && !id.getEntryName().isEmpty()) {
                resourceNames.add(id.getEntryName());
                if (logger != null) {
                    logger.info("QiraV2 string selector candidate: " + id.toLogString());
                }
            }
        }
        return findByQiraResourceNames(
                device,
                packageName,
                resourceNames.toArray(new String[0]));
    }

    public static UiObject2 findByQiraResourceNames(
            UiDevice device,
            String packageName,
            String... qiraStringResourceNames) {
        if (device == null || qiraStringResourceNames == null
                || qiraStringResourceNames.length == 0) {
            return null;
        }
        String[] resolved =
                QiraStrings.getInstance().resolveQiraResourceNames(qiraStringResourceNames);
        for (String value : resolved) {
            UiObject2 object = findByResolvedString(device, packageName, value);
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    public static boolean clickIfPresent(UiObject2 object) {
        if (object == null) {
            return false;
        }
        object.click();
        return true;
    }

    private static UiObject2 findByResolvedString(
            UiDevice device,
            String packageName,
            String resolvedValue) {
        if (resolvedValue == null || resolvedValue.isEmpty()) {
            return null;
        }
        UiObject2 exactText = safeFind(device, By.pkg(packageName).text(resolvedValue));
        if (exactText != null) {
            return exactText;
        }
        UiObject2 exactDesc = safeFind(device, By.pkg(packageName).desc(resolvedValue));
        if (exactDesc != null) {
            return exactDesc;
        }

        String stripped = QiraStrings.stripBidiControls(resolvedValue);
        if (stripped == null || stripped.isEmpty()) {
            return null;
        }
        Pattern contains = Pattern.compile("(?s).*" + Pattern.quote(stripped) + ".*");
        UiObject2 text = safeFind(device, By.pkg(packageName).text(contains));
        if (text != null) {
            return text;
        }
        return safeFind(device, By.pkg(packageName).desc(contains));
    }

    private static UiObject2 safeFind(UiDevice device, androidx.test.uiautomator.BySelector by) {
        try {
            return device.findObject(by);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String toFullResourceId(String packageName, String resourceId) {
        String id = resourceId.trim();
        if (id.contains(":id/") || id.startsWith("android:id/")) {
            return id;
        }
        if (id.contains("/")) {
            return id;
        }
        return String.format(Locale.US, "%s:id/%s", packageName, id);
    }
}
