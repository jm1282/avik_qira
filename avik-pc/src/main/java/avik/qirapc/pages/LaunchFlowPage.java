package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class LaunchFlowPage extends BasePage {

    // Calibrated against the user's 1920x1200 desktop at 100% scale.
    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 3000;
    private static final long LONG_WAIT_MS = 5000;

    private final Point POINT_FOCUS_ZONE = new Point(832, 55);
    private final Point POINT_CHAT = new Point(899, 55);
    private final Point POINT_LIVE = new Point(959, 55);
    private final Point POINT_BUBBLE = new Point(960, 55);
    private final Point POINT_UPDATE_ME = new Point(1025, 55);
    private final Point POINT_CAMERA = new Point(1068, 55);
    private final Point POINT_NEXT = new Point(1295, 635);
    private final Point POINT_AGREE = new Point(1288, 560);

    public LaunchFlowPage(Rectangle windowRectangle) {
        super(windowRectangle);
    }

    public LaunchFlowPage waitForLauncherOverlay() throws Exception {
        Thread.sleep(12000);
        return this;
    }

    public LaunchFlowPage waitForBubble() throws Exception {
        Thread.sleep(28000);
        return this;
    }

    public LaunchFlowPage waitForAttachedState() throws Exception {
        Thread.sleep(8000);
        return this;
    }

    public LaunchFlowPage waitForThinkingState() throws Exception {
        Thread.sleep(LONG_WAIT_MS);
        return this;
    }

    public LaunchFlowPage waitForTransition() throws Exception {
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public LaunchFlowPage hoverOverFocusZone() throws Exception {
        return hover(POINT_FOCUS_ZONE, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage hoverOverChat() throws Exception {
        return hover(POINT_CHAT, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapChat() throws Exception {
        return tap(POINT_CHAT, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage hoverOverLive() throws Exception {
        return hover(POINT_LIVE, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapLive() throws Exception {
        return tap(POINT_LIVE, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapCamera() throws Exception {
        return tap(POINT_CAMERA, LONG_WAIT_MS);
    }

    public LaunchFlowPage hoverOverUpdateMe() throws Exception {
        return hover(POINT_UPDATE_ME, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapUpdateMe() throws Exception {
        return tap(POINT_UPDATE_ME, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapBubble() throws Exception {
        return tap(POINT_BUBBLE, MEDIUM_WAIT_MS);
    }

    public LaunchFlowPage tapNext() throws Exception {
        return tap(POINT_NEXT, SHORT_WAIT_MS);
    }

    public LaunchFlowPage tapAgree() throws Exception {
        return tap(POINT_AGREE, LONG_WAIT_MS);
    }

    public LaunchFlowPage dismissOverlay() throws Exception {
        mKey.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    private LaunchFlowPage tap(Point point, long waitMs) throws Exception {
        mMouse.mouseLeftClick(point.x, point.y);
        Thread.sleep(waitMs);
        return this;
    }

    private LaunchFlowPage hover(Point point, long waitMs) throws Exception {
        mMouse.mouseMove(point.x, point.y);
        Thread.sleep(waitMs);
        return this;
    }
}
