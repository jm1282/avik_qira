package avik.qira.pages;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraScribblePage extends BaseQiraPage {

    private static final String[] SCRIBBLE_ANCHORS = {
            "Scribble",
            "Generate",
            "Realistic",
            "6/6 left today",
            "Watercolor",
            "Sketch Cleanup"
    };

    private static final String[] EXIT_DIALOG_ANCHORS = {
            "Are you sure to exit?",
            "Yes, exit",
            "Stay"
    };

    public QiraScribblePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraScribblePage waitForLoaded() throws Exception {
        UiObject2 anchor = waitForTextOrDescription(5000L, SCRIBBLE_ANCHORS);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the Scribble experience");
        }
        settle();
        return this;
    }

    public boolean openExitDialog() throws Exception {
        mDevice.pressBack();
        settle();
        return waitForTextOrDescription(3000L, EXIT_DIALOG_ANCHORS) != null;
    }

    public QiraCreatorZonePage confirmExit() throws Exception {
        if (!clickByTextOrDescription("Yes, exit")) {
            throw new IllegalStateException("Unable to confirm exit from Scribble");
        }
        return new QiraCreatorZonePage(mDevice, mConfig).waitForLoaded();
    }
}
