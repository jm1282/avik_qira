package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class SettingsPage extends BasePage{

    // Calibrated against the user's 1920x1200 desktop at 100% scaling.

    private final Point POINT_ACCOUNT = new Point(470, 392);
    private final Point POINT_DEVICES = new Point(459, 438);
    private final Point POINT_REFRESH_DEVICES = new Point(1420, 425);

    private final Point POINT_LANGUAGE = new Point(460, 535);
    private final Point POINT_LANGUAGE_OPTIONS = new Point(860, 460);
    private final Point POINT_LAUNCH_OPTIONS = new Point(492, 582);
    private final Point POINT_VOICE = new Point(450, 628);
    private final Point POINT_SYNC_DATA = new Point(464, 723);
    private final Point POINT_PROCESSING_MODE = new Point(480, 745);

    private final Point POINT_SIDEBAR_SCROLL = new Point(600, 735);
    private final Point POINT_PERSONALIZED_ANSWERS = new Point(500, 595);
    private final Point POINT_CONNECTORS = new Point(500, 640);
    private final Point POINT_UPDATE_ME = new Point(500, 682);
    private final Point POINT_CLOSE_BEHAVIOUR = new Point(496, 665);

    private final Point POINT_ABOUT = new Point(500, 775);
    private final Point POINT_SUPPORT_PAGE = new Point(500, 820);
    private final Point POINT_LEGAL_NOTICES = new Point(500, 860);
    private final Point POINT_FEEDBACK = new Point(500, 910);

    public SettingsPage(Rectangle windowRectangle) {
        super(windowRectangle);
    }

    private void tap(Point point) throws Exception {
        mMouse.mouseLeftClick(point.x, point.y);
        Thread.sleep(1500);
    }

    private void scrollAt(Point point, int wheelSteps) throws Exception {
        mMouse.mouseWheelAt(point.x, point.y, wheelSteps);
        Thread.sleep(1500);
    }

    public SettingsPage tapAccount() throws Exception {
        tap(POINT_ACCOUNT);
        return this;
    }

    public SettingsPage tapDevices() throws Exception {
        tap(POINT_DEVICES);
        return this;
    }

    public SettingsPage tapRefresh() throws Exception {
        tap(POINT_REFRESH_DEVICES);
        return this;
    }

    public SettingsPage tapLanguage() throws Exception {
        tap(POINT_LANGUAGE);
        return this;
    }

    public SettingsPage tapLanguageOptions() throws Exception {
        tap(POINT_LANGUAGE_OPTIONS);
        return this;
    }

    public SettingsPage tapLaunchOptions() throws Exception {
        tap(POINT_LAUNCH_OPTIONS);
        return this;
    }

    public SettingsPage tapVoice() throws Exception {
        tap(POINT_VOICE);
        return this;
    }

    public SettingsPage tapSyncData() throws Exception {
        tap(POINT_SYNC_DATA);
        return this;
    }

    public SettingsPage tapProcessingMode() throws Exception {
        tap(POINT_PROCESSING_MODE);
        return this;
    }

    public SettingsPage scrollSideBar() throws Exception {
        scrollAt(POINT_SIDEBAR_SCROLL, 8);
        return this;
    }

    public SettingsPage tapPersonalizedAnswers() throws Exception {
        tap(POINT_PERSONALIZED_ANSWERS);
        return this;
    }

    public SettingsPage tapConnectors() throws Exception {
        tap(POINT_CONNECTORS);
        return this;
    }

    public SettingsPage tapUpdateMe() throws Exception {
        tap(POINT_UPDATE_ME);
        return this;
    }

    public SettingsPage tapCloseBehaviour() throws Exception {
        tap(POINT_CLOSE_BEHAVIOUR);
        return this;
    }

    public SettingsPage tapAbout() throws Exception {
        tap(POINT_ABOUT);
        return this;
    }

    public SettingsPage tapSupportPage() throws Exception {
        tap(POINT_SUPPORT_PAGE);
        return this;
    }

    public SettingsPage tapLegalNotices() throws Exception {
        tap(POINT_LEGAL_NOTICES);
        return this;
    }

    public SettingsPage tapFeedback() throws Exception {
        tap(POINT_FEEDBACK);
        return this;
    }
}
