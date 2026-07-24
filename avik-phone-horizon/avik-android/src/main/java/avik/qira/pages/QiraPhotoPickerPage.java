package avik.qira.pages;

import android.graphics.Rect;
import android.widget.ImageView;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.List;

import avik.qira.utils.QiraConfig;

public class QiraPhotoPickerPage extends BaseQiraPage {

    private static final String[] PICKER_ANCHORS = {
            "Search Google Photos",
            "Photos",
            "Collections",
            "Gallery"
    };

    public QiraPhotoPickerPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraPhotoPickerPage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(5000L, PICKER_ANCHORS);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the photo picker");
        }
        settle();
        return this;
    }

    public boolean selectFirstAsset() throws Exception {
        // Rule-set #2: combine pkg + clazz. The photo picker is a separate
        // process from Qira (it can be `com.google.android.photopicker`,
        // `com.google.android.providers.media.module`, or an OEM gallery),
        // so we scope the scan to whichever package currently owns the
        // foreground — that's the "stable parent" for this geometric
        // sweep. Using the raw `By.clazz(ImageView)` on the whole device
        // would otherwise also match decorative ImageViews in the status
        // bar or navigation bar.
        String foreground = mDevice.getCurrentPackageName();
        List<UiObject2> imageCandidates;
        if (foreground != null && !foreground.isEmpty()) {
            imageCandidates = mDevice.findObjects(
                    By.pkg(foreground).clazz(ImageView.class.getName()));
        } else {
            // Cross-package exception (rule-set #2): when we can't resolve
            // the foreground package we fall back to a device-wide sweep
            // so the automation can still make progress on the picker.
            imageCandidates = mDevice.findObjects(By.clazz(ImageView.class.getName()));
        }
        UiObject2 bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        int minY = mDevice.getDisplayHeight() / 4;

        for (UiObject2 candidate : imageCandidates) {
            Rect bounds = candidate.getVisibleBounds();
            if (bounds == null || bounds.isEmpty()) {
                continue;
            }
            if (bounds.centerY() < minY || bounds.width() < 100 || bounds.height() < 100) {
                continue;
            }

            int score = bounds.top + bounds.left;
            if (score < bestScore) {
                bestScore = score;
                bestMatch = candidate;
            }
        }

        if (bestMatch == null) {
            return false;
        }

        bestMatch.click();
        settle();
        return true;
    }

    public QiraCreatorZonePage goBackToCreatorZone() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraCreatorZonePage(mDevice, mConfig).waitForLoaded();
    }
}
