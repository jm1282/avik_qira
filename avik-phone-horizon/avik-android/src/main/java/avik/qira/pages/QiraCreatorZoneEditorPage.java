package avik.qira.pages;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraCreatorZoneEditorPage extends BaseQiraPage {

    private static final String[] EDITOR_ANCHORS = {
            "Edit",
            "How do you want to edit this image",
            "Smart Editing",
            "Inpaint",
            "Erase",
            "Change BG",
            "Reply",
            "Replace",
            "Confirm"
    };

    public QiraCreatorZoneEditorPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraCreatorZoneEditorPage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(5000L, EDITOR_ANCHORS);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the Creator Zone editor");
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
