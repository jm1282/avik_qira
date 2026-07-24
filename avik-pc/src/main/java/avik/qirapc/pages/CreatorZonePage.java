package avik.qirapc.pages;

import java.awt.Color;
import com.motorola.g11n.tools.avik.client.win.action.keyboards.AbstractKeyboard;
import com.motorola.g11n.tools.avik.client.win.action.keyboards.KeyboardFactory;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class CreatorZonePage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;
    private static final int DARK_PIXEL_THRESHOLD = 105;
    private static final int BRIGHT_PIXEL_THRESHOLD = 200;
    private static final double TOOLTIP_DARK_RATIO_THRESHOLD = 0.70;
    private static final double TOOLTIP_CLOSE_BRIGHT_RATIO_THRESHOLD = 0.03;
    private static final double TOOLTIP_CLOSE_CONTRAST_THRESHOLD = 120.0;

    private final Point POINT_CREATOR_ZONE;
    private final Point POINT_EDIT_IMAGE;
    private final Point POINT_CLICK_TO_TRY;
    private final Point POINT_SMART_EDIT;
    private final Point POINT_SMART_EDIT_CANCEL;
    private final Point POINT_RESIZE;
    private final Point POINT_DONE;
    private final Point POINT_INPAINT;
    private final Point POINT_ERASE;
    private final Point POINT_CHANGE_BACKGROUND;
    private final Point POINT_TEXT_FIELD;
    private final Point POINT_CONFIRM;
    private final Point POINT_BACK_ARROW;
    private final Point POINT_YES_EXIT;
    private final Point POINT_TOOLTIP_ADVANCE;
    private final Rectangle RECT_TOOLTIP_BODY_SAMPLE;
    private final Rectangle RECT_TOOLTIP_CLOSE_SAMPLE;


    public CreatorZonePage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_CREATOR_ZONE = scaledPoint(78, 170);
        POINT_EDIT_IMAGE = scaledPoint(370, 266);
        POINT_CLICK_TO_TRY = scaledPoint(340, 643);
        POINT_SMART_EDIT = scaledPoint(593, 302);
        POINT_SMART_EDIT_CANCEL = scaledPoint(780, 415);
        POINT_RESIZE = scaledPoint(591, 671);
        POINT_DONE = scaledPoint(1053, 669);
        POINT_INPAINT = scaledPoint(618, 116);
        POINT_ERASE = scaledPoint(1090, 575);
        POINT_CHANGE_BACKGROUND = scaledPoint(800, 575);
        POINT_TEXT_FIELD = scaledPoint(666, 673);
        POINT_CONFIRM = scaledPoint(977, 669);
        POINT_BACK_ARROW = scaledPoint(128, 38);
        POINT_YES_EXIT = scaledPoint(857, 430);
        POINT_TOOLTIP_ADVANCE = scaledPoint(993, 564);
        RECT_TOOLTIP_BODY_SAMPLE = scaledRectangle(630, 220, 280, 220);
        RECT_TOOLTIP_CLOSE_SAMPLE = scaledRectangle(1000, 180, 40, 40);
    }

    public CreatorZonePage tapCreatorZone() throws Exception {
        mMouse.mouseLeftClick(POINT_CREATOR_ZONE.x, POINT_CREATOR_ZONE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapEditImage() throws Exception {
        mMouse.mouseLeftClick(POINT_EDIT_IMAGE.x, POINT_EDIT_IMAGE.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapClickToTry() throws Exception {
        mMouse.mouseLeftClick(POINT_CLICK_TO_TRY.x, POINT_CLICK_TO_TRY.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage hoverOverSmartEdit() throws Exception {
        mMouse.mouseMove(POINT_SMART_EDIT.x, POINT_SMART_EDIT.y);
        Thread.sleep(3000);
        return this;
    }

    public CreatorZonePage tapSmartEdit() throws Exception {
        mMouse.mouseLeftClick(POINT_SMART_EDIT.x, POINT_SMART_EDIT.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapSmartEditCancel() throws Exception {
        mMouse.mouseLeftClick(POINT_SMART_EDIT_CANCEL.x, POINT_SMART_EDIT_CANCEL.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapResize() throws Exception {
        mMouse.mouseLeftClick(POINT_RESIZE.x, POINT_RESIZE.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapDone() throws Exception {
        mMouse.mouseLeftClick(POINT_DONE.x, POINT_DONE.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapInpaint() throws Exception {
        mMouse.mouseLeftClick(POINT_INPAINT.x, POINT_INPAINT.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapErase() throws Exception {
        mMouse.mouseLeftClick(POINT_ERASE.x, POINT_ERASE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapChangeBackground() throws Exception {
        mMouse.mouseLeftClick(POINT_CHANGE_BACKGROUND.x, POINT_CHANGE_BACKGROUND.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage describeYourIdea(String prompt) throws Exception {
        mMouse.mouseLeftClick(POINT_TEXT_FIELD.x, POINT_TEXT_FIELD.y);
        Thread.sleep(SHORT_WAIT_MS);
        AbstractKeyboard keyboard = KeyboardFactory.INSTANCE.getSystemKeyboard();
        for (int i = 0; i < prompt.length(); i++) {
            char c = prompt.charAt(i);
            keyboard.type(c);
        }
        mMouse.mouseLeftClick(POINT_CONFIRM.x, POINT_CONFIRM.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapBackArrow() throws Exception {
        mMouse.mouseLeftClick(POINT_BACK_ARROW.x, POINT_BACK_ARROW.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public CreatorZonePage tapYesExit() throws Exception {
        mMouse.mouseLeftClick(POINT_YES_EXIT.x, POINT_YES_EXIT.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public boolean isOnboardingTooltipVisible() throws Exception {
        return matchesTooltip();
    }

    public CreatorZonePage tapTooltipAdvance() throws Exception {
        mMouse.mouseLeftClick(POINT_TOOLTIP_ADVANCE.x, POINT_TOOLTIP_ADVANCE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    private boolean matchesTooltip() throws Exception {
        BufferedImage tooltipBody = captureSample(RECT_TOOLTIP_BODY_SAMPLE);
        BufferedImage tooltipClose = captureSample(RECT_TOOLTIP_CLOSE_SAMPLE);

        double darkRatio = calculateDarkPixelRatio(tooltipBody);
        double brightRatio = calculateBrightPixelRatio(tooltipClose);
        double contrast = calculateBrightnessContrast(tooltipClose);

        return darkRatio >= TOOLTIP_DARK_RATIO_THRESHOLD
                && brightRatio >= TOOLTIP_CLOSE_BRIGHT_RATIO_THRESHOLD
                && contrast >= TOOLTIP_CLOSE_CONTRAST_THRESHOLD;
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

    private double calculateDarkPixelRatio(BufferedImage image) {
        int darkPixels = 0;
        int totalPixels = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int brightness = color.getRed() + color.getGreen() + color.getBlue();
                if (brightness <= DARK_PIXEL_THRESHOLD * 3) {
                    darkPixels++;
                }
            }
        }

        return totalPixels == 0 ? 0.0 : (double) darkPixels / totalPixels;
    }

    private double calculateBrightPixelRatio(BufferedImage image) {
        int brightPixels = 0;
        int totalPixels = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int brightness = color.getRed() + color.getGreen() + color.getBlue();
                if (brightness >= BRIGHT_PIXEL_THRESHOLD * 3) {
                    brightPixels++;
                }
            }
        }

        return totalPixels == 0 ? 0.0 : (double) brightPixels / totalPixels;
    }

    private double calculateBrightnessContrast(BufferedImage image) {
        int minBrightness = Integer.MAX_VALUE;
        int maxBrightness = Integer.MIN_VALUE;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int brightness = color.getRed() + color.getGreen() + color.getBlue();
                minBrightness = Math.min(minBrightness, brightness);
                maxBrightness = Math.max(maxBrightness, brightness);
            }
        }

        if (minBrightness == Integer.MAX_VALUE || maxBrightness == Integer.MIN_VALUE) {
            return 0.0;
        }

        return (maxBrightness - minBrightness) / 3.0;
    }
}
