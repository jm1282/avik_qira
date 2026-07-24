package avik.qira.pages;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraStyleSyncPage extends BaseQiraPage {

    private static final String[] STYLE_SYNC_ANCHORS = {
            "Style Sync",
            "Create a wallpaper inspired by your unique style",
            "Uploading your selfie"
    };

    public QiraStyleSyncPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraStyleSyncPage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(5000L, STYLE_SYNC_ANCHORS);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the Style Sync experience");
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
