package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class GalleryPage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;

    private final Point POINT_GALLERY;
    private final Point POINT_BACK;
    private final Point POINT_SYNC;
    private final Point POINT_SYNC_ALT;
    private final Point POINT_RECREATE;
    private final Point POINT_CLOSE;


    public GalleryPage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_GALLERY = scaledPoint(1128, 266);
        POINT_BACK = scaledPoint(128, 38);
        POINT_SYNC = scaledPoint(1110, 128);
        POINT_SYNC_ALT = scaledPoint(1088, 128);
        POINT_RECREATE = scaledPoint(373, 190);
        POINT_CLOSE = scaledPoint(965, 208);
    }

    public GalleryPage tapGallery() throws Exception {
        mMouse.mouseLeftClick(POINT_GALLERY.x, POINT_GALLERY.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public GalleryPage tapBack() throws Exception {
        mMouse.mouseLeftClick(POINT_BACK.x, POINT_BACK.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public GalleryPage hoverOverSync() throws Exception {
        mMouse.mouseMove(POINT_SYNC.x, POINT_SYNC.y);
        Thread.sleep(800);
        mMouse.mouseMove(POINT_SYNC_ALT.x, POINT_SYNC_ALT.y);
        Thread.sleep(2200);
        return this;
    }

    public GalleryPage hoverOverRecreate() throws Exception {
        mMouse.mouseMove(POINT_RECREATE.x, POINT_RECREATE.y);
        Thread.sleep(3000);
        return this;
    }

    public GalleryPage tapRecreate() throws Exception {
        mMouse.mouseLeftClick(POINT_RECREATE.x, POINT_RECREATE.y);
        Thread.sleep(1000);
        return this;
    }

    public GalleryPage tapClose() throws Exception {
        mMouse.mouseLeftClick(POINT_CLOSE.x, POINT_CLOSE.y);
        Thread.sleep(1000);
        return this;
    }
}
