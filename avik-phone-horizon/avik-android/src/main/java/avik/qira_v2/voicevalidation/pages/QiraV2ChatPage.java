package avik.qira_v2.voicevalidation.pages;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import com.motorola.g11n.tools.avik.client.android.log.AvikLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import avik.qira.pages.BaseQiraPage;
import avik.qira.utils.QiraConfig;
import avik.qira.utils.QiraStrings;

/**
 * Drives the Qira chat surface for Voice Validation: starts a fresh chat, enters
 * the exact localized prompt (via clipboard paste, since the Compose composer
 * exposes no editable node that {@code UiObject2.setText} can drive and
 * {@code input text} corrupts non-ASCII), sends it, waits for the response,
 * handles a counter-question, and taps the response's Play control.
 *
 * <p>Controls are matched through localized English anchors ({@code cd_new_chat},
 * {@code cd_send}, {@code cd_play}, {@code cd_more_options}) resolved by the
 * qira_v2 Compose resolver, so the page works with Qira's UI in any locale.
 */
public final class QiraV2ChatPage extends BaseQiraPage {

    /** Placeholder labels that identify the composer entry field. */
    private static final String[] COMPOSER_ENTRY_LABELS = {
            "What are you looking for?",
            "Ask about anything",
            "Ask Qira",
            "Ask anything",
            "Message Qira",
            "Type a message",
            "Type or speak"
    };

    private static final String[] ERROR_ANCHORS = {
            "Something went wrong",
            "try again later or restart"
    };

    /** Response is "done" once its text is unchanged this long (rides out streaming). */
    private static final long RESPONSE_STABLE_MS = 6000L;
    /** Below this length a stable response is treated as a possible interim placeholder. */
    private static final int SHORT_RESPONSE_LEN = 55;
    /** Max extra time to wait for the final answer to replace a short interim message. */
    private static final long INTERIM_GRACE_MS = 25000L;

    private final Logger logger = AvikLoggerFactory.INSTANCE.getInstance();

    public enum ResponseState { COMPLETE, ERROR, TIMEOUT }

    public QiraV2ChatPage(UiDevice device, QiraConfig config) throws Exception {
        super(device, config);
    }

    /** Opens the chat surface and guarantees a brand-new (empty) chat. */
    public void openChatAndStartNew() throws Exception {
        goToHomeGrid();
        // Bubble-bar Chat entry; exact desc avoids the "Chat History" tile.
        if (!clickByExactTextOrDescription("Chat")) {
            clickByTextOrDescription("Chat");
        }
        settle();
        // A chat that already holds a conversation exposes the overflow ("More
        // options"); start a fresh chat from it. An empty composer is already a
        // new chat, so nothing more is needed.
        if (findByTextOrDescription("More options") != null) {
            startNewChatViaOverflow();
        }
        if (waitForComposer(8000L) == null) {
            throw new IllegalStateException(
                    "VoiceValidation: composer did not appear for a new chat.");
        }
        settle();
    }

    private void startNewChatViaOverflow() throws Exception {
        if (!clickByTextOrDescription("More options")) {
            return;
        }
        settle();
        if (!clickByTextOrDescription("New Chat") && !clickByTextOrDescription("New chat")) {
            throw new IllegalStateException("VoiceValidation: 'New Chat' menu item not found.");
        }
        settle();
    }

    /** Navigates to the Qira home grid (the surface that exposes the drawer). */
    private void goToHomeGrid() throws Exception {
        for (int i = 0; i < 8; i++) {
            if (isHomeGridVisible()) {
                return;
            }
            UiObject2 appIcon = lowestByDescription("App Icon");
            if (appIcon != null) {
                clickObject(appIcon);
            } else if (findByTextOrDescription("Minimize") != null) {
                clickByTextOrDescription("Minimize");
            } else {
                mDevice.pressBack();
            }
            settle();
        }
        if (!isHomeGridVisible()) {
            throw new IllegalStateException(
                    "VoiceValidation: could not reach the Qira home grid.");
        }
    }

    private boolean isHomeGridVisible() {
        if (findByTextOrDescription("Menu", "Open navigation drawer") != null) {
            return true;
        }
        int tiles = 0;
        for (String tile : new String[] {"Creations", "Knowledge", "Chat History", "Help & Support"}) {
            if (findByTextOrDescription(tile) != null) {
                tiles++;
            }
        }
        return tiles >= 2;
    }

    private UiObject2 lowestByDescription(String desc) {
        List<UiObject2> nodes = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(desc)));
        UiObject2 best = null;
        int maxY = -1;
        for (UiObject2 n : nodes) {
            try {
                Rect b = n.getVisibleBounds();
                if (b != null && b.centerY() > maxY) {
                    maxY = b.centerY();
                    best = n;
                }
            } catch (Throwable ignored) {
            }
        }
        return best;
    }

    /** Focuses the composer, pastes the exact prompt, and taps Send. */
    public void enterQueryAndSend(String text) throws Exception {
        boolean focused = false;
        for (int attempt = 0; attempt < 3 && !focused; attempt++) {
            try {
                UiObject2 entry = waitForComposer(8000L);
                if (entry == null) {
                    throw new IllegalStateException(
                            "VoiceValidation: composer entry field not found.");
                }
                clickObject(entry);
                focused = true;
            } catch (IllegalStateException e) {
                throw e;
            } catch (Throwable stale) {
                mUtils.sleep(400L);
            }
        }
        if (!focused) {
            throw new IllegalStateException("VoiceValidation: could not focus the composer.");
        }
        settle();
        setClipboard(text);
        mDevice.pressKeyCode(KeyEvent.KEYCODE_PASTE);
        settle();
        boolean sent = false;
        for (int attempt = 0; attempt < 3 && !sent; attempt++) {
            try {
                sent = clickByExactTextOrDescription("Send") || clickByTextOrDescription("Send");
                if (!sent) {
                    break;
                }
            } catch (Throwable stale) {
                mUtils.sleep(400L);
            }
        }
        if (!sent) {
            throw new IllegalStateException("VoiceValidation: Send button not found.");
        }
        settle();
    }

    /**
     * Waits until the latest response is complete (its Play action appears), or
     * an error card is shown, or the timeout elapses.
     */
    public ResponseState waitForResponseComplete(long timeoutMs) throws Exception {
        // Complete = the latest response has a Play control AND its text has
        // stopped changing for RESPONSE_STABLE_MS. Text-stability (rather than a
        // fixed settle) rides out token streaming and interim "please wait..."
        // bubbles that an async tool call (e.g. Weather) replaces with the final
        // answer a few seconds later.
        long deadline = System.currentTimeMillis() + timeoutMs;
        String lastText = null;
        long stableSince = System.currentTimeMillis();
        long firstPlayAt = 0L;
        boolean sawPlay = false;
        while (System.currentTimeMillis() < deadline) {
            if (findByText(ERROR_ANCHORS) != null) {
                logger.info("VoiceValidation: response returned an error card.");
                return ResponseState.ERROR;
            }
            scrollChatToBottom();
            UiObject2 play = findLowestPlay();
            if (play != null) {
                sawPlay = true;
                if (firstPlayAt == 0L) {
                    firstPlayAt = System.currentTimeMillis();
                }
                String current = getLatestResponseText();
                if (current != null && current.equals(lastText)) {
                    boolean stable = System.currentTimeMillis() - stableSince >= RESPONSE_STABLE_MS;
                    // A short, stable answer may be an interim "please wait..."
                    // placeholder an async tool call (e.g. Weather) will replace;
                    // give the final answer a grace window to arrive.
                    boolean longEnough = current.length() >= SHORT_RESPONSE_LEN;
                    boolean graceOver =
                            System.currentTimeMillis() - firstPlayAt >= INTERIM_GRACE_MS;
                    if (stable && (longEnough || graceOver)) {
                        return ResponseState.COMPLETE;
                    }
                } else {
                    lastText = current;
                    stableSince = System.currentTimeMillis();
                }
            }
            mUtils.sleep(1500L);
        }
        return sawPlay ? ResponseState.COMPLETE : ResponseState.TIMEOUT;
    }

    /** Taps the "Scroll to bottom" FAB until it disappears (view at conversation end). */
    private void scrollChatToBottom() throws Exception {
        for (int i = 0; i < 5; i++) {
            UiObject2 fab = findByTextOrDescription("Scroll to bottom");
            if (fab == null) {
                return;
            }
            try {
                clickObject(fab);
            } catch (Throwable ignored) {
            }
            settle();
        }
    }

    /** Heuristic: the latest response is a clarifying question. */
    public boolean isCounterQuestion() {
        String text = getLatestResponseText();
        if (text == null) {
            return false;
        }
        String t = QiraStrings.stripBidiControls(text).trim();
        if (t.isEmpty()) {
            return false;
        }
        // A question mark anywhere (Latin '?', full-width '？', Arabic '؟').
        if (t.indexOf('?') >= 0 || t.indexOf('\uFF1F') >= 0 || t.indexOf('\u061F') >= 0) {
            return true;
        }
        // A numbered multiple-choice clarification, e.g. "1) ... 2) ...".
        return t.contains("1)") && t.contains("2)");
    }

    /** Best-effort text of the latest assistant response (just above its Play row). */
    public String getLatestResponseText() {
        UiObject2 play = findLowestPlay();
        Rect pb = null;
        if (play != null) {
            try {
                pb = play.getVisibleBounds();
            } catch (Throwable ignored) {
            }
        }
        List<UiObject2> texts = mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"));
        String best = null;
        int bestBottom = -1;
        for (UiObject2 t : texts) {
            try {
                String s = t.getText();
                if (s == null || s.trim().isEmpty() || isChrome(s)) {
                    continue;
                }
                Rect b = t.getVisibleBounds();
                if (b == null) {
                    continue;
                }
                // Response text sits above its own action row.
                if (pb != null && b.top >= pb.top) {
                    continue;
                }
                if (b.bottom > bestBottom) {
                    bestBottom = b.bottom;
                    best = s;
                }
            } catch (Throwable ignored) {
            }
        }
        return best;
    }

    /**
     * Full visible on-screen response text (every non-chrome text node above the
     * Play action row, top-ordered). This is the "screenshot of chat response"
     * the host validates against the SSML output text.
     */
    public String getResponseBlob() {
        UiObject2 play = findLowestPlay();
        Rect pb = null;
        if (play != null) {
            try {
                pb = play.getVisibleBounds();
            } catch (Throwable ignored) {
            }
        }
        final List<String> texts = new ArrayList<>();
        final List<Integer> tops = new ArrayList<>();
        for (UiObject2 t : mDevice.findObjects(
                By.pkg(mConfig.getPackageName()).clazz("android.widget.TextView"))) {
            try {
                String s = t.getText();
                if (s == null || s.trim().isEmpty() || isChrome(s)) {
                    continue;
                }
                Rect b = t.getVisibleBounds();
                if (b == null) {
                    continue;
                }
                if (pb != null && b.top >= pb.top) {
                    continue;
                }
                tops.add(b.top);
                texts.add(s);
            } catch (Throwable ignored) {
            }
        }
        Integer[] idx = new Integer[texts.size()];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = i;
        }
        java.util.Arrays.sort(idx, new java.util.Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(tops.get(a), tops.get(b));
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i : idx) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(texts.get(i));
        }
        return sb.toString();
    }

    /**
     * True when a node with exactly {@code text} is on screen. Used to verify the
     * prompt was entered byte-for-byte (special characters / symbols included),
     * since a corrupted paste would not render the exact string.
     */
    public boolean isTextDisplayed(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            return mDevice.findObject(By.pkg(mConfig.getPackageName()).text(text)) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public void replyAndSend(String affirmative) throws Exception {
        enterQueryAndSend(affirmative);
    }

    /** Taps the Play control of the latest (lowest on screen) response. */
    public boolean tapPlayLatest() throws Exception {
        scrollChatToBottom();
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                UiObject2 play = findLowestPlay();
                if (play == null) {
                    return false;
                }
                clickObject(play);
                settle();
                return true;
            } catch (Throwable stale) {
                mUtils.sleep(400L);
            }
        }
        return false;
    }

    private UiObject2 waitForComposer(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            UiObject2 entry = findByTextOrDescription(COMPOSER_ENTRY_LABELS);
            if (entry != null) {
                return entry;
            }
            // Fall back to the field left of the Send button when the hint text
            // is not exposed as an accessibility node.
            UiObject2 send = findByExactTextOrDescription("Send");
            if (send != null) {
                UiObject2 field = findComposerLeftOf(send);
                if (field != null) {
                    return field;
                }
            }
            mUtils.sleep(300L);
        }
        return null;
    }

    private UiObject2 findComposerLeftOf(UiObject2 send) {
        try {
            Rect sb = send.getVisibleBounds();
            if (sb == null) {
                return null;
            }
            List<UiObject2> clickables = mDevice.findObjects(
                    By.pkg(mConfig.getPackageName()).clickable(true));
            UiObject2 best = null;
            int bestRight = -1;
            for (UiObject2 c : clickables) {
                Rect b = c.getVisibleBounds();
                if (b == null) {
                    continue;
                }
                // Same row as Send, wider than an icon, to its left.
                if (b.centerY() >= sb.top - 20 && b.centerY() <= sb.bottom + 20
                        && b.left < sb.left && b.width() > sb.width()) {
                    if (b.right > bestRight) {
                        bestRight = b.right;
                        best = c;
                    }
                }
            }
            return best;
        } catch (Throwable t) {
            return null;
        }
    }

    private UiObject2 findLowestPlay() {
        List<UiObject2> plays = findAllPlay();
        UiObject2 lowest = null;
        int maxY = -1;
        for (UiObject2 p : plays) {
            try {
                Rect b = p.getVisibleBounds();
                if (b == null) {
                    continue;
                }
                if (b.centerY() > maxY) {
                    maxY = b.centerY();
                    lowest = p;
                }
            } catch (Throwable ignored) {
            }
        }
        return lowest;
    }

    private List<UiObject2> findAllPlay() {
        List<UiObject2> out = new ArrayList<>();
        String[] labels = QiraStrings.expandAll("Play");
        for (String label : labels) {
            try {
                List<UiObject2> found = mDevice.findObjects(
                        By.pkg(mConfig.getPackageName()).desc(exactPatternForLabel(label)));
                if (found != null) {
                    out.addAll(found);
                }
            } catch (Throwable ignored) {
            }
        }
        return out;
    }

    private boolean isChrome(String s) {
        String t = s.trim();
        if (t.length() > 400) {
            return false;
        }
        if (t.contains("AI can make mistakes")) {
            return true;
        }
        for (String hint : COMPOSER_ENTRY_LABELS) {
            if (t.equalsIgnoreCase(hint)) {
                return true;
            }
        }
        return false;
    }

    private void setClipboard(final String text) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
                ClipboardManager cm =
                        (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText("vv", text));
                }
            }
        });
    }
}
