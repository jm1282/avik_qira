package avik.qirapc.pages;

import java.awt.Point;
import java.awt.Rectangle;

public class CustomImagePage extends BasePage{

    private static final long SHORT_WAIT_MS = 1000L;
    private static final long MEDIUM_WAIT_MS = 2000L;

    private final Point POINT_CUSTOM_IMAGE;
    private final Point POINT_REFERENCE_IMAGE_LABEL;
    private final Point POINT_REFERENCE_IMAGE_CARD;
    private final Point POINT_IMAGE_WEIGHT_DROPDOWN;
    private final Point POINT_BOTTOM_IMAGE_ICON;
    private final Point POINT_BOTTOM_STYLE_ICON;
    private final Point POINT_BACK_ARROW;

    public CustomImagePage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_CUSTOM_IMAGE = scaledPoint(530, 266);
        POINT_REFERENCE_IMAGE_LABEL = scaledPoint(228, 108);
        POINT_REFERENCE_IMAGE_CARD = scaledPoint(334, 221);
        POINT_IMAGE_WEIGHT_DROPDOWN = scaledPoint(334, 322);
        POINT_BOTTOM_IMAGE_ICON = scaledPoint(273, 665);
        POINT_BOTTOM_STYLE_ICON = scaledPoint(326, 665);
        POINT_BACK_ARROW = scaledPoint(128, 38);
    }

    public CustomImagePage tapCustomImage() throws Exception {
        mMouse.mouseLeftClick(POINT_CUSTOM_IMAGE.x, POINT_CUSTOM_IMAGE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CustomImagePage hoverOverReferenceImage() throws Exception {
        mMouse.mouseMove(POINT_REFERENCE_IMAGE_LABEL.x, POINT_REFERENCE_IMAGE_LABEL.y);
        Thread.sleep(800L);
        mMouse.mouseMove(POINT_REFERENCE_IMAGE_CARD.x, POINT_REFERENCE_IMAGE_CARD.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CustomImagePage tapReferenceImageCard() throws Exception {
        mMouse.mouseLeftClick(POINT_REFERENCE_IMAGE_CARD.x, POINT_REFERENCE_IMAGE_CARD.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CustomImagePage tapImageWeightDropdown() throws Exception {
        mMouse.mouseLeftClick(POINT_IMAGE_WEIGHT_DROPDOWN.x, POINT_IMAGE_WEIGHT_DROPDOWN.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CustomImagePage tapBottomImageIcon() throws Exception {
        mMouse.mouseLeftClick(POINT_BOTTOM_IMAGE_ICON.x, POINT_BOTTOM_IMAGE_ICON.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CustomImagePage tapBottomStyleIcon() throws Exception {
        mMouse.mouseLeftClick(POINT_BOTTOM_STYLE_ICON.x, POINT_BOTTOM_STYLE_ICON.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CustomImagePage tapBack() throws Exception {
        mMouse.mouseLeftClick(POINT_BACK_ARROW.x, POINT_BACK_ARROW.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    // Backward-compatible wrappers
    public CustomImagePage tapResolutions() throws Exception {
        return tapImageWeightDropdown();
    }

    public CustomImagePage tapStyles() throws Exception {
        return tapBottomStyleIcon();
    }
}
