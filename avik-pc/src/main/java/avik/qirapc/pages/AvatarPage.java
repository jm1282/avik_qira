package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class AvatarPage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;

    private final Point POINT_AVATAR = new Point(690, 266);
    private final Point POINT_UPLOAD_AN_IMAGE = new Point(683, 687);
    private final Point POINT_BACK = new Point(128, 38);

    public AvatarPage(Rectangle windowRectangle) {
        super(windowRectangle);
    }

    public AvatarPage tapAvatar() throws Exception {
        mMouse.mouseLeftClick(POINT_AVATAR.x, POINT_AVATAR.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public AvatarPage tapUpload() throws Exception {
        mMouse.mouseLeftClick(POINT_UPLOAD_AN_IMAGE.x, POINT_UPLOAD_AN_IMAGE.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public AvatarPage tapBack() throws Exception {
        mMouse.mouseLeftClick(POINT_BACK.x, POINT_BACK.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }
}
