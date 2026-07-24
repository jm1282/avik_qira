package avik.motohealth.nightlyauto;

import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.motorola.g11n.avik.uiautomatoradapter.AvikUiDevice;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;
import com.motorola.g11n.tools.avik.client.android.AvikHandler;
import com.motorola.g11n.tools.avik.screenshot.delta.DeltaMethod;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import avik.motohealth.utils.TestUtils;


@RunWith(AndroidJUnit4.class)
public class WatchMusic {
    @Rule
    public final AvikHandler avikHandler = AvikHandler.getInstance();
    public AvikUiDevice mAvikDevice=AvikUiDevice.getInstance();
    public UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public AvikUtility mUtility= AvikUtility.getInstance();
    TestUtils mUtil = new TestUtils();

    @Test
    @DeltaMethod
    public void captureOfWatchMusic() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uiDevice.executeShellCommand(
                    "am start com.motorola.watch/com.motorola.watch.ui.activity.MainPageActivity");
        }
        mUtil.clickByText("Watch","com.motorola.watch","watch");
        mUtil.getListView().scrollToEnd(100);
        mUtil.clickByText("Music","com.motorola.watch","btn_music_manager");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddSongs");
        mUtil.clickByObj("Add song","com.motorola.watch:id/songs_add_btn",0);
        mUtil.clickByText("Songs","com.motorola.watch","tab_songs");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Songs");
        mUtil.clickByText("Select all","com.motorola.watch","select_all");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_DeselectAll");
        mUtil.clickByText("Deselect all","com.motorola.watch","deselect_all");
        mUtil.clickByObj("View selected","com.motorola.watch:id/view_selected_btn",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_WatchMusic_ViewSelectedNone_Toast");
        mUtil.waitFor10S(5,"Waiting for the toast disappear");
        mUtil.clickByText("Albums","com.motorola.watch","tab_albums");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Albums");
        mUtil.clickByObj("avik data","com.motorola.watch:id/category_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Albums_Data");
        mUtil.pressBack(1);
        mUtil.clickByText("Artists","com.motorola.watch","tab_artists");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Artist");
        mUtil.clickByObj("avik data","com.motorola.watch:id/category_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Artist_Data");
        mUtil.pressBack(1);
        mUtil.clickByText("Folders","com.motorola.watch","tab_folders");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Folders");
        mUtil.clickByObj("avik data","com.motorola.watch:id/category_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Folders_Data");
        mUtil.pressBack(1);
        mUtil.clickByText("Songs","com.motorola.watch","tab_songs");
        mUtil.clickByObj("","com.motorola.watch:id/song_checkbox",1);
        mUtil.clickByObj("View selected","com.motorola.watch:id/view_selected_btn",0);
        mUtil.clickByObj("Add to watch","com.motorola.watch:id/add_to_watch_btn",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_Transferring");
        mUtil.clickByObj("Close Btn","com.motorola.watch:id/close_btn",0);
        mUtil.isExistClass("com.motorola.watch:id/cancel_transfer_dialog_title");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_StopTransferInProgress_Dialog");
        mUtil.clickByObj("Continue transfer","com.motorola.watch:id/continue_transfer_btn",0);
        mUtil.isExistResuorceId("om.motorola.watch:id/song_name");
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_TransferringSuccessfully");
        mUtil.clickByObj("Close Btn","com.motorola.watch:id/close_btn",0);
        mUtil.clickByObj("","com.motorola.watch:id/song_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_SongsRightMenu");
        mUtil.clickByObj("Add to playlist","com.motorola.watch:id/add_to_play_list",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_Add1SongsToPlayList");
        mUtil.clickByObj("Cancel","com.motorola.watch:id/playlist_cancel_btn",0);
        mUtil.clickByObj("","com.motorola.watch:id/song_name",0);
        mUtil.clickByObj("Delete","com.motorola.watch:id/delete",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_DeleteSongs_Dialog");
        uiDevice.findObject(By.res("com.motorola.watch:id/dialog_confirm_button")).click();
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_DeleteSongs_Toast");
        mUtil.clickByText("Playlists","com.motorola.watch","tab_title_playlist");
        mUtil.clickByObj("New playlist","com.motorola.watch:id/new_playlist_btn",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_WatchMusic_AddToWatch_CreatePlayList_Dialog");
        mUtil.getObj("","com.motorola.watch:id/text_input_edit_text",0).setText("avikPlayList");
        mUtil.clickByObj("","com.motorola.watch:id/dialog_confirm_button",0);
        mUtil.takeAvikScreenshotToast("MotoHealth_WatchMusic_AddToWatch_NewPlayList");
        mUtil.clickByObj("","com.motorola.watch:id/playlist_name",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_NewPlayListAddSongs");
        mUtil.clickByObj("edit icon","com.motorola.watch:id/playlist_edit",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_RenewPlayList_Dialog");
        mUtil.clickByObj("Cancel btn","com.motorola.watch:id/dialog_cancel_button",0);
        mUtil.clickByObj("","com.motorola.watch:id/playlist_add_song_btn",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_AddSongToPlaylist");
        mUtil.pressBack(1);
        mUtil.clickByObj("Delete","com.motorola.watch:id/playlist_delete",0);
        mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddToWatch_DeletePlayList_Dialog");
        mUtil.clickByObj("Delete btn","com.motorola.watch:id/dialog_confirm_button",0);
    }
    @Before
    public void setup() throws Exception{
        mUtil.writeLog("setup");
//        mUtil.pressBack(5);
        mUtil.writeLog("=====locale: " + mAvikDevice.getLocale());
    }
    @After
    public void tearDown() throws InterruptedException {
        mUtil.pressBack(5);
        mUtil.writeLog("tearDown");
    }
    @Test
    public void testMain() throws Exception {
            mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_Permission_MusicAndAudio");
            mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_AddMusicToPlayer_Toast");
            mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_RemoveMusicFromPlayer");
            mUtil.takeAvikScreenshot("MotoHealth_WatchMusic_RemoveMusicFromPlayer_Toast");
            captureOfWatchMusic();

    }
}
