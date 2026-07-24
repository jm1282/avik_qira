package avik.qirapc.pages;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class ExplorerPage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;
    private static final long HERO_CHANGE_TIMEOUT_MS = 12000;
    private static final long HERO_POLL_INTERVAL_MS = 1000;

    private final Point POINT_RETURN_TO_BASE_PAGE;
    private final Point POINT_SETTINGS_BUTTON;
    private final Point POINT_SLIDE;
    private final Point POINT_NEXT;
    private final Point POINT_CHAT_HISTORY_CARD;
    private final Rectangle RECT_HERO_SIGNATURE_SAMPLE;
    private final boolean mUsesWindowRelativeCoordinates;

    public ExplorerPage(Rectangle windowRectangle) {
        super(windowRectangle);
        mUsesWindowRelativeCoordinates = windowRectangle.width <= 1400 && windowRectangle.height <= 900;

        POINT_RETURN_TO_BASE_PAGE = resolvePoint(459, 217, 100, 40);
        POINT_SETTINGS_BUTTON = resolvePoint(410, 442, 53, 265);
        POINT_SLIDE = resolvePoint(955, 462, 598, 286);
        POINT_NEXT = resolvePoint(1530, 462, 1168, 286);
        POINT_CHAT_HISTORY_CARD = resolvePoint(1170, 742, 811, 566);
        RECT_HERO_SIGNATURE_SAMPLE = scaledRectangle(470, 170, 450, 220);
    }

    public ExplorerPage tapSettings() throws Exception {
        mMouse.mouseLeftClick(POINT_SETTINGS_BUTTON.x, POINT_SETTINGS_BUTTON.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public ExplorerPage tapReturnToBasePage() throws Exception {
        mMouse.mouseLeftClick(POINT_RETURN_TO_BASE_PAGE.x, POINT_RETURN_TO_BASE_PAGE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ExplorerPage tapSlide() throws Exception {
        mMouse.mouseLeftClick(POINT_SLIDE.x, POINT_SLIDE.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public ExplorerPage tapNext() throws Exception {
        mMouse.mouseLeftClick(POINT_NEXT.x, POINT_NEXT.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public ExplorerPage tapChatHistoryCard() throws Exception {
        mMouse.mouseLeftClick(POINT_CHAT_HISTORY_CARD.x, POINT_CHAT_HISTORY_CARD.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public long captureHeroSignature() throws Exception {
        return computeImageSignature(captureSample(RECT_HERO_SIGNATURE_SAMPLE));
    }

    public ExplorerPage waitForHeroCardChange(long previousSignature) throws Exception {
        long deadline = System.currentTimeMillis() + HERO_CHANGE_TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {
            Thread.sleep(HERO_POLL_INTERVAL_MS);

            if (captureHeroSignature() != previousSignature) {
                Thread.sleep(MEDIUM_WAIT_MS);
                return this;
            }
        }

        tapNext();
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    private Point resolvePoint(int desktopX, int desktopY, double windowReferenceX, double windowReferenceY) {
        if (mUsesWindowRelativeCoordinates) {
            return scaledPoint(windowReferenceX, windowReferenceY);
        }

        return new Point(desktopX, desktopY);
    }

    private BufferedImage captureSample(Rectangle sampleArea) throws Exception {
        Rectangle absoluteSample = new Rectangle(
                mRect.x + sampleArea.x,
                mRect.y + sampleArea.y,
                sampleArea.width,
                sampleArea.height
        );

        return new Robot().createScreenCapture(absoluteSample);
    }

    private long computeImageSignature(BufferedImage image) {
        long signature = 17L;

        for (int y = 0; y < image.getHeight(); y += 12) {
            for (int x = 0; x < image.getWidth(); x += 12) {
                Color color = new Color(image.getRGB(x, y));
                int bucket = ((color.getRed() / 16) << 8)
                        | ((color.getGreen() / 16) << 4)
                        | (color.getBlue() / 16);
                signature = signature * 31L + bucket;
            }
        }

        return signature;
    }
}
