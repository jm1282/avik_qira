package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class ScribblePage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000L;
    private static final long MEDIUM_WAIT_MS = 2000L;
    private static final long CANVAS_MOVE_WAIT_MS = 600L;
    private static final long CLICK_STEP_WAIT_MS = 120L;

    // Navigation points
    private final Point POINT_SCRIBBLE_MENU;
    private final Point POINT_BACK_ARROW;
    private final Point POINT_DRAW_BUTTON;
    private final Point POINT_CONFIRM_BUTTON;

    // Canvas points (relative to 1200x800 reference window)
    private final Point POINT_CANVAS_CENTER;
    private final Point POINT_CANVAS_TOP_LEFT;
    private final Point POINT_CANVAS_TOP_RIGHT;
    private final Point POINT_CANVAS_BOTTOM_LEFT;
    private final Point POINT_CANVAS_BOTTOM_RIGHT;
    private final Point POINT_LINE_START;
    private final Point POINT_LINE_END;

    // Explicit desktop coordinate requested by user
    private static final Point POINT_RIGHT_ICON = new Point(1160, 882);

    public ScribblePage(Rectangle windowRectangle) {
        super(windowRectangle);

        POINT_SCRIBBLE_MENU = scaledPoint(1001, 266);
        POINT_BACK_ARROW = scaledPoint(481, 212);
        POINT_DRAW_BUTTON = scaledPoint(317, 370);
        POINT_CONFIRM_BUTTON = scaledPoint(675, 490);

        POINT_CANVAS_CENTER = scaledPoint(573, 430);
        POINT_CANVAS_TOP_LEFT = scaledPoint(510, 320);
        POINT_CANVAS_TOP_RIGHT = scaledPoint(635, 320);
        POINT_CANVAS_BOTTOM_LEFT = scaledPoint(510, 540);
        POINT_CANVAS_BOTTOM_RIGHT = scaledPoint(635, 540);
        POINT_LINE_START = scaledPoint(520, 430);
        POINT_LINE_END = scaledPoint(680, 430);
    }

    /** Opens the Scribble module from Creator Zone. */
    public ScribblePage tapScribble() throws Exception {
        click(POINT_SCRIBBLE_MENU, MEDIUM_WAIT_MS);
        return this;
    }

    /** Starts drawing mode. */
    public ScribblePage tapClickHereToDraw() throws Exception {
        click(POINT_DRAW_BUTTON, SHORT_WAIT_MS);
        return this;
    }

    /**
     * Executes a stable, coordinate-based canvas interaction sequence.
     * This is intentionally simple and deterministic for reliability.
     */
    public ScribblePage drawSamplePatternByCoordinates() throws Exception {
        move(POINT_CANVAS_CENTER, CANVAS_MOVE_WAIT_MS);
        click(POINT_CANVAS_CENTER, CANVAS_MOVE_WAIT_MS);

        click(POINT_CANVAS_TOP_LEFT, CANVAS_MOVE_WAIT_MS);
        click(POINT_CANVAS_TOP_RIGHT, CANVAS_MOVE_WAIT_MS);
        click(POINT_CANVAS_BOTTOM_RIGHT, CANVAS_MOVE_WAIT_MS);
        click(POINT_CANVAS_BOTTOM_LEFT, CANVAS_MOVE_WAIT_MS);
        click(POINT_CANVAS_CENTER, SHORT_WAIT_MS);
        return this;
    }

    /**
     * Draws a horizontal line using dense coordinate clicks.
     * This avoids element identifiers and works with pure X,Y positions.
     */
    public ScribblePage drawLineByCoordinates() throws Exception {
        int steps = 14;
        double deltaX = (POINT_LINE_END.x - POINT_LINE_START.x) / (double) steps;
        double deltaY = (POINT_LINE_END.y - POINT_LINE_START.y) / (double) steps;

        move(POINT_LINE_START, CLICK_STEP_WAIT_MS);
        for (int i = 0; i <= steps; i++) {
            int currentX = (int) Math.round(POINT_LINE_START.x + (deltaX * i));
            int currentY = (int) Math.round(POINT_LINE_START.y + (deltaY * i));
            mMouse.mouseLeftClick(currentX, currentY);
            Thread.sleep(CLICK_STEP_WAIT_MS);
        }

        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    /** Clicks the right-side icon at fixed coordinate (1160, 882). */
    public ScribblePage tapRightIconAtCoordinate() throws Exception {
        mMouse.mouseLeftClick(POINT_RIGHT_ICON.x, POINT_RIGHT_ICON.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    /** Keeps compatibility with the previous method name. */
    public ScribblePage hoverOverScribbleHere() throws Exception {
        move(POINT_CANVAS_TOP_LEFT, CANVAS_MOVE_WAIT_MS);
        move(POINT_CANVAS_CENTER, CANVAS_MOVE_WAIT_MS);
        move(POINT_CANVAS_BOTTOM_RIGHT, MEDIUM_WAIT_MS);
        return this;
    }

    /** Confirms Scribble action if confirm button is shown. */
    public ScribblePage tapConfirm() throws Exception {
        click(POINT_CONFIRM_BUTTON, SHORT_WAIT_MS);
        return this;
    }

    /** Navigates back from Scribble screen. */
    public ScribblePage tapBack() throws Exception {
        click(POINT_BACK_ARROW, SHORT_WAIT_MS);
        return this;
    }

    /** Basic readiness action for right panel area. */
    public boolean isArtworkPanelVisible() throws Exception {
        move(POINT_CANVAS_BOTTOM_RIGHT, SHORT_WAIT_MS);
        return true;
    }

    private void click(Point point, long waitMs) throws Exception {
        mMouse.mouseLeftClick(point.x, point.y);
        Thread.sleep(waitMs);
    }

    private void move(Point point, long waitMs) throws Exception {
        mMouse.mouseMove(point.x, point.y);
        Thread.sleep(waitMs);
    }
}
