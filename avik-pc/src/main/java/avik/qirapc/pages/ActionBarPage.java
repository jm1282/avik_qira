package avik.qirapc.pages;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class ActionBarPage extends BasePage {

    private static final int ACTION_RETRY_COUNT = 6;
    private static final int SIGNATURE_COLUMNS = 12;
    private static final int SIGNATURE_ROWS = 8;
    private static final int STABLE_CHANGE_POLLS = 2;
    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 4500;
    private static final long LONG_WAIT_MS = 7000;
    private static final long POST_CLICK_SETTLE_MS = 1200;
    private static final long VERIFY_POLL_INTERVAL_MS = 300;
    private static final double CONTENT_CHANGE_THRESHOLD = 14.0;
    private static final double TAKE_NOTES_STOP_THRESHOLD = 10.0;
    private static final double REFERENCE_DESKTOP_WIDTH = 1920.0;
    private static final double REFERENCE_DESKTOP_HEIGHT = 1200.0;

    private final Point POINT_ICON;
    private final Point POINT_RESET;
    private final Point POINT_OUTSIDE_ACTION_BAR;
    private final Point POINT_FOCUS_ZONE;
    private final Point POINT_CHAT;
    private final Point POINT_LIVE;
    private final Point POINT_UPDATE_ME;
    private final Point POINT_TAKE_NOTES;
    private final Point POINT_TAKE_NOTES_STOP;
    private final Point POINT_UPDATE_ME_MINIMIZE;
    private final Point POINT_SUMMARY_TAB;
    private final Point POINT_TRANSCRIPT_TAB;
    private final Point POINT_AUDIO_RECORDING_TAB;
    private final Point[] POINTS_UPDATE_ME_CANDIDATES;
    private final Point[] POINTS_UPDATE_ME_MINIMIZE_CANDIDATES;
    private final Point[] POINTS_TAKE_NOTES_CANDIDATES;
    private final Point[] POINTS_TAKE_NOTES_STOP_CANDIDATES;
    private final Rectangle REGION_CONTENT;
    private final Rectangle REGION_TAKE_NOTES_TABS;
    private Robot mRobot;

    public ActionBarPage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_ICON = desktopScaledPoint(960, 55);
        POINT_RESET = desktopScaledPoint(960, 100);
        POINT_OUTSIDE_ACTION_BAR = desktopScaledPoint(960, 180);
        POINT_FOCUS_ZONE = desktopScaledPoint(832, 55);
        POINT_CHAT = desktopScaledPoint(899, 55);
        POINT_LIVE = desktopScaledPoint(959, 55);
        POINT_UPDATE_ME = desktopScaledPoint(1025, 55);
        POINT_TAKE_NOTES = desktopScaledPoint(1089, 55);
        POINT_TAKE_NOTES_STOP = desktopScaledPoint(1111, 59);
        POINT_UPDATE_ME_MINIMIZE = desktopScaledPoint(1328, 105);
        POINT_SUMMARY_TAB = desktopScaledPoint(723, 176);
        POINT_TRANSCRIPT_TAB = desktopScaledPoint(960, 176);
        POINT_AUDIO_RECORDING_TAB = desktopScaledPoint(1191, 176);
        POINTS_UPDATE_ME_CANDIDATES = desktopScaledPoints(new double[][]{
                {1025, 55},
                {1031, 55},
                {1019, 55},
                {1035, 58},
                {1024, 60},
                {1017, 59}
        });
        POINTS_UPDATE_ME_MINIMIZE_CANDIDATES = desktopScaledPoints(new double[][]{
                {1328, 105},
                {1321, 105},
                {1335, 105},
                {1328, 98},
                {1328, 112},
                {1322, 99}
        });
        POINTS_TAKE_NOTES_CANDIDATES = desktopScaledPoints(new double[][]{
                {1089, 55},
                {1095, 55},
                {1083, 55},
                {1098, 58},
                {1088, 60},
                {1081, 59}
        });
        POINTS_TAKE_NOTES_STOP_CANDIDATES = desktopScaledPoints(new double[][]{
                {1111, 59},
                {1104, 59},
                {1118, 59},
                {1111, 53},
                {1111, 65},
                {1106, 64}
        });
        REGION_CONTENT = desktopScaledRectangle(650, 120, 720, 380);
        REGION_TAKE_NOTES_TABS = desktopScaledRectangle(680, 145, 600, 100);

    }

    public ActionBarPage tapIcon() throws Exception {
        moveOffBar();
        Thread.sleep(SHORT_WAIT_MS);
        mMouse.mouseMove(POINT_ICON.x, POINT_ICON.y + 12);
        Thread.sleep(SHORT_WAIT_MS);
        mMouse.mouseMove(POINT_ICON.x, POINT_ICON.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ActionBarPage tapFocusZone() throws Exception {
        mMouse.mouseLeftClick(POINT_FOCUS_ZONE.x, POINT_FOCUS_ZONE.y);
        Thread.sleep(LONG_WAIT_MS);
        return this;
    }

    public ActionBarPage hoverOverFocusZone() throws Exception {
        hoverIcon(POINT_FOCUS_ZONE);
        return this;
    }

    public ActionBarPage hoverOverChat() throws Exception {
        hoverIcon(POINT_CHAT);
        return this;
    }

    public ActionBarPage hoverOverLive() throws Exception {
        hoverIcon(POINT_LIVE);
        return this;
    }

    public ActionBarPage hoverOverCatchMeUp() throws Exception {
        hoverIcon(POINT_UPDATE_ME);
        return this;
    }

    public ActionBarPage hoverOverPayAttention() throws Exception {
        hoverIcon(POINT_TAKE_NOTES);
        return this;
    }

    public ActionBarPage hoverOverUpdateMe() throws Exception {
        return hoverOverCatchMeUp();
    }

    public ActionBarPage hoverOverTakeNotes() throws Exception {
        return hoverOverPayAttention();
    }

    public ActionBarPage tapChat() throws Exception {
        tapIcon(POINT_CHAT, LONG_WAIT_MS);
        return this;
    }

    public ActionBarPage tapOutsideActionBar() throws Exception {
        mMouse.mouseLeftClick(POINT_OUTSIDE_ACTION_BAR.x, POINT_OUTSIDE_ACTION_BAR.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ActionBarPage tapLive() throws Exception {
        tapIcon(POINT_LIVE, LONG_WAIT_MS);
        return this;
    }

    public ActionBarPage tapUpdateMe() throws Exception {
        tapVerifiedActionBarTarget(
                "Update me",
                POINT_UPDATE_ME,
                POINTS_UPDATE_ME_CANDIDATES,
                REGION_CONTENT,
                CONTENT_CHANGE_THRESHOLD,
                LONG_WAIT_MS
        );
        return this;
    }

    public ActionBarPage tapTakeNotes() throws Exception {
        tapVerifiedActionBarTarget(
                "Take notes",
                POINT_TAKE_NOTES,
                POINTS_TAKE_NOTES_CANDIDATES,
                REGION_CONTENT,
                CONTENT_CHANGE_THRESHOLD,
                LONG_WAIT_MS
        );
        return this;
    }

    public ActionBarPage tapTakeNotesStop() throws Exception {
        tapVerifiedActionBarTarget(
                "Take notes stop",
                POINT_TAKE_NOTES_STOP,
                POINTS_TAKE_NOTES_STOP_CANDIDATES,
                REGION_TAKE_NOTES_TABS,
                TAKE_NOTES_STOP_THRESHOLD,
                LONG_WAIT_MS
        );
        return this;
    }

    public ActionBarPage tapUpdateMeMinimize() throws Exception {
        tapVerifiedPanelTarget(
                "Update me minimize",
                POINTS_UPDATE_ME_MINIMIZE_CANDIDATES,
                REGION_CONTENT,
                CONTENT_CHANGE_THRESHOLD,
                MEDIUM_WAIT_MS
        );
        return this;
    }

    public ActionBarPage tapSummaryTab() throws Exception {
        mMouse.mouseLeftClick(POINT_SUMMARY_TAB.x, POINT_SUMMARY_TAB.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ActionBarPage tapTranscriptTab() throws Exception {
        mMouse.mouseLeftClick(POINT_TRANSCRIPT_TAB.x, POINT_TRANSCRIPT_TAB.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ActionBarPage tapAudioRecordingTab() throws Exception {
        mMouse.mouseLeftClick(POINT_AUDIO_RECORDING_TAB.x, POINT_AUDIO_RECORDING_TAB.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    private void hoverIcon(Point point) throws Exception {
        moveOffBar();
        Thread.sleep(SHORT_WAIT_MS);
        mMouse.mouseMove(point.x, point.y + 12);
        Thread.sleep(SHORT_WAIT_MS);
        mMouse.mouseMove(point.x, point.y);
        Thread.sleep(MEDIUM_WAIT_MS);
    }

    private void moveOffBar() throws Exception {
        mMouse.mouseMove(POINT_RESET.x, POINT_RESET.y);
    }

    private void tapIcon(Point point, long waitMs) throws Exception {
        moveOffBar();
        Thread.sleep(SHORT_WAIT_MS);
        mMouse.mouseLeftClick(point.x, point.y);
        Thread.sleep(waitMs);
    }

    private void tapHoveredIcon(Point point, long waitMs) throws Exception {
        mMouse.mouseLeftClick(point.x, point.y);
        Thread.sleep(waitMs);
    }

    private void tapVerifiedPanelTarget(
            String actionName,
            Point[] candidatePoints,
            Rectangle verificationRegion,
            double threshold,
            long timeoutMs
    ) throws Exception {
        int[] baselineSignature = captureRegionSignature(verificationRegion);
        double bestDifference = 0.0;

        for (int attempt = 0; attempt < Math.min(ACTION_RETRY_COUNT, candidatePoints.length); attempt++) {
            Point candidate = candidatePoints[attempt];
            mMouse.mouseLeftClick(candidate.x, candidate.y);

            double observedDifference = waitForRegionChange(verificationRegion, baselineSignature, threshold, timeoutMs);
            bestDifference = Math.max(bestDifference, observedDifference);

            if (observedDifference >= threshold) {
                Thread.sleep(POST_CLICK_SETTLE_MS);
                return;
            }

            Thread.sleep(SHORT_WAIT_MS);
        }

        throw new IllegalStateException(
                actionName + " click did not reach the expected UI state. Max observed region change="
                        + String.format("%.2f", bestDifference)
        );
    }

    private void tapVerifiedActionBarTarget(
            String actionName,
            Point hoverPoint,
            Point[] candidatePoints,
            Rectangle verificationRegion,
            double threshold,
            long timeoutMs
    ) throws Exception {
        int[] baselineSignature = captureRegionSignature(verificationRegion);
        double bestDifference = 0.0;

        for (int attempt = 0; attempt < Math.min(ACTION_RETRY_COUNT, candidatePoints.length); attempt++) {
            hoverIcon(hoverPoint);
            Point candidate = candidatePoints[attempt];
            mMouse.mouseLeftClick(candidate.x, candidate.y);

            double observedDifference = waitForRegionChange(verificationRegion, baselineSignature, threshold, timeoutMs);
            bestDifference = Math.max(bestDifference, observedDifference);

            if (observedDifference >= threshold) {
                Thread.sleep(POST_CLICK_SETTLE_MS);
                return;
            }

            Thread.sleep(SHORT_WAIT_MS);
        }

        throw new IllegalStateException(
                actionName + " click did not reach the expected UI state. Max observed region change="
                        + String.format("%.2f", bestDifference)
        );
    }

    private double waitForRegionChange(
            Rectangle verificationRegion,
            int[] baselineSignature,
            double threshold,
            long timeoutMs
    ) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int stableHits = 0;
        double maxDifference = 0.0;

        while (System.currentTimeMillis() < deadline) {
            Thread.sleep(VERIFY_POLL_INTERVAL_MS);

            double difference = calculateSignatureDifference(
                    baselineSignature,
                    captureRegionSignature(verificationRegion)
            );
            maxDifference = Math.max(maxDifference, difference);

            if (difference >= threshold) {
                stableHits++;
                if (stableHits >= STABLE_CHANGE_POLLS) {
                    return maxDifference;
                }
            } else {
                stableHits = 0;
            }
        }

        return maxDifference;
    }

    private int[] captureRegionSignature(Rectangle region) {
        BufferedImage image = getRobot().createScreenCapture(region);
        int[] signature = new int[SIGNATURE_COLUMNS * SIGNATURE_ROWS * 3];
        int index = 0;

        for (int row = 0; row < SIGNATURE_ROWS; row++) {
            int y = Math.min(image.getHeight() - 1, ((row * 2) + 1) * image.getHeight() / (SIGNATURE_ROWS * 2));

            for (int column = 0; column < SIGNATURE_COLUMNS; column++) {
                int x = Math.min(image.getWidth() - 1, ((column * 2) + 1) * image.getWidth() / (SIGNATURE_COLUMNS * 2));
                int rgb = image.getRGB(x, y);

                signature[index++] = (rgb >> 16) & 0xFF;
                signature[index++] = (rgb >> 8) & 0xFF;
                signature[index++] = rgb & 0xFF;
            }
        }

        return signature;
    }

    private double calculateSignatureDifference(int[] baselineSignature, int[] currentSignature) {
        long totalDifference = 0;

        for (int i = 0; i < baselineSignature.length; i++) {
            totalDifference += Math.abs(baselineSignature[i] - currentSignature[i]);
        }

        return totalDifference / (double) baselineSignature.length;
    }

    private Robot getRobot() {
        if (mRobot == null) {
            try {
                mRobot = new Robot();
            } catch (AWTException e) {
                throw new IllegalStateException("Unable to create desktop Robot for Action Bar verification.", e);
            }
        }

        return mRobot;
    }

    private Point desktopScaledPoint(double referenceX, double referenceY) {
        int x = (int) Math.round(referenceX * mRect.width / REFERENCE_DESKTOP_WIDTH);
        int y = (int) Math.round(referenceY * mRect.height / REFERENCE_DESKTOP_HEIGHT);

        x = Math.max(1, Math.min(x, Math.max(1, mRect.width - 2)));
        y = Math.max(1, Math.min(y, Math.max(1, mRect.height - 2)));

        return new Point(x, y);
    }

    private Rectangle desktopScaledRectangle(
            double referenceX,
            double referenceY,
            double referenceWidth,
            double referenceHeight
    ) {
        Point origin = desktopScaledPoint(referenceX, referenceY);
        int width = Math.max(1, (int) Math.round(referenceWidth * mRect.width / REFERENCE_DESKTOP_WIDTH));
        int height = Math.max(1, (int) Math.round(referenceHeight * mRect.height / REFERENCE_DESKTOP_HEIGHT));

        if (origin.x + width > mRect.width) {
            width = Math.max(1, mRect.width - origin.x);
        }

        if (origin.y + height > mRect.height) {
            height = Math.max(1, mRect.height - origin.y);
        }

        return new Rectangle(origin.x, origin.y, width, height);
    }

    private Point[] desktopScaledPoints(double[][] referencePoints) {
        Point[] points = new Point[referencePoints.length];

        for (int i = 0; i < referencePoints.length; i++) {
            points[i] = desktopScaledPoint(referencePoints[i][0], referencePoints[i][1]);
        }

        return points;
    }
}
