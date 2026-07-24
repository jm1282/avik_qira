package avik.qirapc.scripts;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.awt.Rectangle;

import avik.qirapc.pages.ActionBarPage;
import avik.qirapc.utils.Qira;


/**
 * Sample of Avik Windows script in Java
 */
@AvikWinScript
public class TestActionBarPage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_ActionBarPage_";

    private static Application mApp;
    private static Rectangle mRect;
    protected AvikLogger mLogger = AvikLogger.INSTANCE;

    @BeforeAll
    public static void setup() throws Exception {
        mApp = Application.getInstance();
        mRect = Utils.INSTANCE.getDesktopRect();
        Qira.restartQira(mApp, mRect);
        Thread.sleep(7000);
    }

    @AfterAll
    public static void tearDown() throws Exception {
    }

    @AvikWinMain
    public void executeCapture() throws Exception {
        try {
            main();
        } catch (Exception e) {
            StackTraceElement[] error = e.getStackTrace();
            for (StackTraceElement stackTraceElement : error) {
                mLogger.warning(stackTraceElement.toString());
            }
            throw e;
        }
    }

    public void main() throws Exception {
        ActionBarPage actionBarPage = new ActionBarPage(mRect);

        actionBarPage.tapIcon();
        capture("bar");

        actionBarPage.hoverOverFocusZone();
        capture("focus_zone");

        actionBarPage.hoverOverChat();
        capture("chat_overlay");
        actionBarPage.tapChat();
        capture("chat_open");
        actionBarPage.tapOutsideActionBar();

        actionBarPage.hoverOverLive();
        capture("live_overlay");
        actionBarPage.tapLive();
        capture("live_open");
        actionBarPage.tapLive();

        actionBarPage.hoverOverUpdateMe();
        capture("update_me_overlay");
        actionBarPage.tapUpdateMe();
        capture("update_me_open");
        actionBarPage.tapUpdateMeMinimize();
        capture("update_me_minimized");
        actionBarPage.tapIcon();

        actionBarPage.hoverOverTakeNotes();
        capture("take_notes_overlay");
        actionBarPage.tapTakeNotes();
        capture("take_notes_recording");
        actionBarPage.tapTakeNotesStop();
        capture("take_notes_summary");

        actionBarPage.tapSummaryTab();
        capture("take_notes_summary_tab");

        actionBarPage.tapTranscriptTab();
        capture("take_notes_transcript_tab");

        actionBarPage.tapAudioRecordingTab();
        capture("take_notes_audio_recording_tab");
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }
}
