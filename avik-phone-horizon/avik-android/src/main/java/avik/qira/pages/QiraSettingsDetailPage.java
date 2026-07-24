package avik.qira.pages;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraSettingsDetailPage extends BaseQiraPage {

    public QiraSettingsDetailPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraSettingsDetailPage waitForLoaded(String... anchors) throws Exception {
        return waitForLoaded(5000L, anchors);
    }

    public QiraSettingsDetailPage waitForLoaded(long timeoutMs, String... anchors) throws Exception {
        UiObject2 anchor = waitForTextOrDescription(timeoutMs, anchors);
        if (anchor == null) {
            throw new IllegalStateException("Unable to detect the requested Qira settings detail");
        }
        settle();
        return this;
    }

    public QiraSettingsDetailPage openNested(String[] entryLabels, String... anchors)
            throws Exception {
        if (!clickByTextOrDescriptionWithScroll(entryLabels)) {
            return null;
        }
        return new QiraSettingsDetailPage(mDevice, mConfig).waitForLoaded(anchors);
    }

    public QiraSettingsDetailPage goBackToDetail(String... anchors) throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraSettingsDetailPage(mDevice, mConfig).waitForLoaded(anchors);
    }

    public QiraSettingsPage goBackToSettings() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraSettingsPage(mDevice, mConfig).waitForLoaded();
    }
}
