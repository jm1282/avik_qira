package avik.qira.pages;

import android.widget.EditText;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import avik.qira.utils.QiraConfig;

public class QiraComposerPage extends BaseQiraPage {

    private static final String[] COMPOSER_ANCHORS = {
            "Ask Qira",
            "Ask anything",
            "Type a message",
            "Type or speak",
            "Message Qira",
            "Chat",
            "Try it",
            "Not Now",
            "Find inspiration, get things done, and stay organized",
            "Motorola Qira learns from you and can reference past conversations",
            "What are you looking for?",
            "What is the storage limit?",
            "How to create new images?",
            "Find inspiration",
            "No Accessibility Permission",
            "Allow Permissions"
    };

    private static final String[] INTRO_ANCHORS = {
            "Find inspiration, get things done, and stay organized",
            "Try it",
            "Not Now"
    };

    private static final String[] RESPONSE_ANCHORS = {
            "What are you looking for?",
            "What is the storage limit?",
            "How to create new images?",
            "Summary not found"
    };

    public QiraComposerPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    public QiraComposerPage waitForLoaded() throws Exception {
        UiObject2 editor = waitForClass(EditText.class.getName(), 5000L);
        if (editor == null && waitForTextOrDescription(3000L, COMPOSER_ANCHORS) == null) {
            throw new IllegalStateException("Unable to detect the Qira composer");
        }
        settle();
        return this;
    }

    public boolean isIntroVisible() {
        return hasTextOrDescription(INTRO_ANCHORS);
    }

    public boolean advanceIntroIfPresent() throws Exception {
        if (!isIntroVisible()) {
            return false;
        }
        if (clickByTextOrDescription("Try it", "Next")) {
            return true;
        }
        return clickByTextOrDescription("Not Now");
    }

    public boolean stagePrompt(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            return false;
        }

        UiObject2 editor = waitForClass(EditText.class.getName(), 3000L);
        if (editor == null) {
            if (clickByTextOrDescription("Try it")) {
                editor = waitForClass(EditText.class.getName(), 3000L);
            }
        }

        if (editor == null) {
            if (clickByTextOrDescription("Not Now")) {
                editor = waitForClass(EditText.class.getName(), 3000L);
            }
        }

        if (editor == null) {
            return false;
        }

        editor.click();
        settle();
        editor.setText(prompt);
        settle();
        return true;
    }

    public boolean waitForResponseState(long timeoutMs) throws Exception {
        return waitForTextOrDescription(timeoutMs, RESPONSE_ANCHORS) != null;
    }

    public QiraHomePage closeComposer() throws Exception {
        mDevice.pressBack();
        settle();
        return new QiraHomePage(mDevice, mConfig);
    }
}
