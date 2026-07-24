package avik.qira.pages;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraSurfacePage extends BaseQiraPage {

    private static final String[] INTRO_ACTIONS = {
            "Next",
            "Not Now",
            "Try it"
    };

    public QiraSurfacePage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraSurfacePage waitForLoaded(String... preferredLabels) throws Exception {
        UiObject2 anchor = null;
        if (preferredLabels != null && preferredLabels.length > 0) {
            anchor = waitForTextOrDescription(5000L, preferredLabels);
        }

        if (anchor == null
                && waitForTextOrDescription(3000L,
                "Catch me up",
                "Pay Attention",
                "Allow Permissions",
                "No Accessibility Permission",
                "What are you looking for?",
                "Gathering latest notifications",
                "Analyzing contents",
                "Combining themes",
                "Catching you up",
                "Starting transcription",
                "Recording is in progress",
                "Summary not found",
                "Here is the summary",
                "Summary",
                "Transcript",
                "Audio Reco") == null) {
            throw new IllegalStateException("Unable to detect a Qira detail surface");
        }

        settle();
        return this;
    }

    public void scrollDownOnce() throws Exception {
        // Rule-set #2: keep the scrollable lookup scoped to the Qira package
        // so we don't accidentally scroll a system UI surface (e.g. the
        // notification shade) that happens to be scrollable and visible.
        UiObject2 scrollable = mDevice.findObject(
                By.pkg(mConfig.getPackageName()).scrollable(true));
        if (scrollable != null) {
            scrollable.scroll(Direction.DOWN, 0.85f);
        } else {
            int centerX = mDevice.getDisplayWidth() / 2;
            int startY = mDevice.getDisplayHeight() - 300;
            int endY = mDevice.getDisplayHeight() / 3;
            mDevice.swipe(centerX, startY, centerX, endY, 30);
        }
        settle();
    }

    public boolean isIntroVisible(String... labels) {
        return hasTextOrDescription(labels) && hasTextOrDescription(INTRO_ACTIONS);
    }

    public boolean advanceIntroIfPresent() throws Exception {
        if (!hasTextOrDescription(INTRO_ACTIONS)) {
            return false;
        }
        return clickByTextOrDescription("Next", "Try it", "Not Now");
    }

    public boolean hasAnyText(String... labels) {
        return hasTextOrDescription(labels);
    }

    public QiraHomePage goBackHome() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraHomePage(mDevice, mConfig);
    }
}
