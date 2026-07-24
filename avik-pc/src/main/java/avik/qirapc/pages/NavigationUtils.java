package avik.qirapc.pages;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class NavigationUtils extends BasePage {

    public NavigationUtils(Rectangle rect) {
        super(rect);
    }

    private void focusContentArea() throws Exception {
        // Safe central area inside content panel
        mMouse.mouseLeftClick(600, 400);
        Thread.sleep(300);
    }

    public void tap(int x, int y) throws Exception {
        mMouse.mouseLeftClick(x, y);
        Thread.sleep(500);
    }

    public void scrollDown(int taps) throws Exception {
        focusContentArea();
        for (int i = 0; i < taps; i++) {
            mKey.keyPress(KeyEvent.VK_DOWN);
            Thread.sleep(200);
        }
    }

    public void scrollUp(int taps) throws Exception {
        focusContentArea();
        for (int i = 0; i < taps; i++) {
            mKey.keyPress(KeyEvent.VK_UP);
            Thread.sleep(200);
        }
    }

    public void scrollDown(int x, int y, int taps) throws Exception {
        tap(x, y);
        for (int i = 0; i < taps; i++) {
            mKey.keyPress(KeyEvent.VK_DOWN);
            Thread.sleep(200);
        }
    }

    public void scrollUp(int x, int y, int taps) throws Exception {
        tap(x, y);
        for (int i = 0; i < taps; i++) {
            mKey.keyPress(KeyEvent.VK_UP);
            Thread.sleep(200);
        }
    }
}