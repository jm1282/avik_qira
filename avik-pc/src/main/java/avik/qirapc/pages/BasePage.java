package avik.qirapc.pages;

import com.motorola.g11n.tools.avik.client.win.action.KeyAction;
import com.motorola.g11n.tools.avik.client.win.action.MouseAction;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;

import java.awt.Point;
import java.awt.Rectangle;


public class BasePage {

    private static final double REFERENCE_WINDOW_WIDTH = 1200.0;
    private static final double REFERENCE_WINDOW_HEIGHT = 800.0;

    protected Rectangle mRect;
    protected KeyAction mKey;
    protected MouseAction mMouse;
    protected AvikLogger mLogger;

    public BasePage(Rectangle windowRectangle) {
        this.mRect = windowRectangle;
        this.mKey = new KeyAction();
        this.mMouse = new MouseAction(windowRectangle);
        this.mLogger = AvikLogger.INSTANCE;
    }

    protected Point scaledPoint(double referenceX, double referenceY) {
        int x = (int) Math.round(referenceX * mRect.width / REFERENCE_WINDOW_WIDTH);
        int y = (int) Math.round(referenceY * mRect.height / REFERENCE_WINDOW_HEIGHT);

        x = Math.max(1, Math.min(x, Math.max(1, mRect.width - 2)));
        y = Math.max(1, Math.min(y, Math.max(1, mRect.height - 2)));

        return new Point(x, y);
    }

    protected Rectangle scaledRectangle(double referenceX, double referenceY, double referenceWidth, double referenceHeight) {
        Point origin = scaledPoint(referenceX, referenceY);
        int width = Math.max(1, (int) Math.round(referenceWidth * mRect.width / REFERENCE_WINDOW_WIDTH));
        int height = Math.max(1, (int) Math.round(referenceHeight * mRect.height / REFERENCE_WINDOW_HEIGHT));

        if (origin.x + width > mRect.width) {
            width = Math.max(1, mRect.width - origin.x);
        }

        if (origin.y + height > mRect.height) {
            height = Math.max(1, mRect.height - origin.y);
        }

        return new Rectangle(origin.x, origin.y, width, height);
    }
}
