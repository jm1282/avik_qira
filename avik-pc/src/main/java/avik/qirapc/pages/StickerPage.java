package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class StickerPage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;

    private final Point POINT_STICKER;
    private final Point POINT_DAILY_LIMIT_BADGE;
    private final Point POINT_DAILY_LIMIT_BADGE_ALT;
    private final Point POINT_BACK;

    public StickerPage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_STICKER = scaledPoint(846, 266);
        POINT_DAILY_LIMIT_BADGE = scaledPoint(404, 409);
        POINT_DAILY_LIMIT_BADGE_ALT = scaledPoint(432, 409);
        POINT_BACK = scaledPoint(128, 38);
    }

    public StickerPage tapSticker() throws Exception {
        mMouse.mouseLeftClick(POINT_STICKER.x, POINT_STICKER.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public StickerPage hoverOverDailyLimitBadge() throws Exception {
        mMouse.mouseMove(POINT_DAILY_LIMIT_BADGE.x, POINT_DAILY_LIMIT_BADGE.y);
        Thread.sleep(800);
        mMouse.mouseMove(POINT_DAILY_LIMIT_BADGE_ALT.x, POINT_DAILY_LIMIT_BADGE_ALT.y);
        Thread.sleep(2200);
        return this;
    }

    public StickerPage tapBack() throws Exception {
        mMouse.mouseLeftClick(POINT_BACK.x, POINT_BACK.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }
}
