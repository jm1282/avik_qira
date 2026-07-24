package avik.qira.pages;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraGalleryPage extends BaseQiraPage {

    private static final String[] GALLERY_ANCHORS = {
            "Gallery",
            "Gallery sync enabled",
            "images synced successfully",
            "Create image",
            "Redo",
            "Edit"
    };

    public QiraGalleryPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraGalleryPage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(5000L, GALLERY_ANCHORS);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the Creator Zone gallery");
        }
        settle();
        return this;
    }

    public QiraCreatorZonePage goBackToCreatorZone() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraCreatorZonePage(mDevice, mConfig).waitForLoaded();
    }
}
