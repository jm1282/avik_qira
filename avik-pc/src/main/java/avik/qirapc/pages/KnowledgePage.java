package avik.qirapc.pages;

import com.motorola.g11n.tools.avik.client.win.action.keyboards.AbstractKeyboard;
import com.motorola.g11n.tools.avik.client.win.action.keyboards.KeyboardFactory;

import java.awt.Point;
import java.awt.Rectangle;

public class KnowledgePage extends BasePage {

    private static final long SHORT_WAIT_MS = 1000;
    private static final long MEDIUM_WAIT_MS = 2000;

    private final Point POINT_KNOWLEDGE_NAV;
    private final Point POINT_CATEGORY;
    private final Point POINT_TAGS;
    private final Point POINT_TAGS_SCROLL_AREA;
    private final Point POINT_NEW;
    private final Point POINT_CREATE_MEMORY;
    private final Point POINT_MODAL_TEXT_FIELD;
    private final Point POINT_REMEMBER_THIS;
    private final Point POINT_MEMORY_CANCEL;
    private final Point POINT_ROW_DELETE;
    private final Point POINT_DELETE_CANCEL;
    private final Point POINT_DELETE_CONFIRM;
    private final Point POINT_NEUTRAL_AREA;

    public KnowledgePage(Rectangle windowRectangle) {
        super(windowRectangle);
        POINT_KNOWLEDGE_NAV = scaledPoint(112, 219);
        POINT_CATEGORY = scaledPoint(355, 287);
        POINT_TAGS = scaledPoint(476, 287);
        POINT_TAGS_SCROLL_AREA = scaledPoint(1245, 819);
        POINT_NEW = scaledPoint(1158, 218);
        POINT_CREATE_MEMORY = scaledPoint(1124, 289);
        POINT_MODAL_TEXT_FIELD = scaledPoint(662, 392);
        POINT_REMEMBER_THIS = scaledPoint(821, 486);
        POINT_MEMORY_CANCEL = scaledPoint(741, 486);
        POINT_ROW_DELETE = scaledPoint(1192, 415);
        POINT_DELETE_CANCEL = scaledPoint(737, 443);
        POINT_DELETE_CONFIRM = scaledPoint(851, 443);
        POINT_NEUTRAL_AREA = scaledPoint(612, 188);
    }

    public KnowledgePage tapKnowledgeNav() throws Exception {
        mMouse.mouseLeftClick(POINT_KNOWLEDGE_NAV.x, POINT_KNOWLEDGE_NAV.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public KnowledgePage tapKnowledgeCard() throws Exception {
        return tapKnowledgeNav();
    }

    public KnowledgePage tapNeutralArea() throws Exception {
        mMouse.mouseLeftClick(POINT_NEUTRAL_AREA.x, POINT_NEUTRAL_AREA.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage tapCategory() throws Exception {
        mMouse.mouseLeftClick(POINT_CATEGORY.x, POINT_CATEGORY.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage tapTags() throws Exception {
        mMouse.mouseLeftClick(POINT_TAGS.x, POINT_TAGS.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage scrollTagsDown(int wheelSteps) throws Exception {
        mMouse.mouseMove(POINT_TAGS_SCROLL_AREA.x, POINT_TAGS_SCROLL_AREA.y);
        Thread.sleep(300);
        mMouse.mouseWheelAt(POINT_TAGS_SCROLL_AREA.x, POINT_TAGS_SCROLL_AREA.y, wheelSteps);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public KnowledgePage tapNew() throws Exception {
        mMouse.mouseLeftClick(POINT_NEW.x, POINT_NEW.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage tapCreateMemory() throws Exception {
        mMouse.mouseLeftClick(POINT_CREATE_MEMORY.x, POINT_CREATE_MEMORY.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public KnowledgePage createMemory(String input_memory) throws Exception {
        mMouse.mouseLeftClick(POINT_MODAL_TEXT_FIELD.x, POINT_MODAL_TEXT_FIELD.y);
        Thread.sleep(SHORT_WAIT_MS);
        AbstractKeyboard keyboard = KeyboardFactory.INSTANCE.getSystemKeyboard();
        for (int i = 0; i < input_memory.length(); i++) {
            char c = input_memory.charAt(i);
            keyboard.type(c);
        }
        return this;
    }

    public KnowledgePage tapRememberThis() throws Exception {
        mMouse.mouseLeftClick(POINT_REMEMBER_THIS.x, POINT_REMEMBER_THIS.y);
        Thread.sleep(5000);
        return this;
    }

    public KnowledgePage tapMemoryCancel() throws Exception {
        mMouse.mouseLeftClick(POINT_MEMORY_CANCEL.x, POINT_MEMORY_CANCEL.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage tapDelete() throws Exception {
        mMouse.mouseLeftClick(POINT_ROW_DELETE.x, POINT_ROW_DELETE.y);
        Thread.sleep(MEDIUM_WAIT_MS);
        return this;
    }

    public KnowledgePage tapDeleteCancel() throws Exception {
        mMouse.mouseLeftClick(POINT_DELETE_CANCEL.x, POINT_DELETE_CANCEL.y);
        Thread.sleep(SHORT_WAIT_MS);
        return this;
    }

    public KnowledgePage tapDeleteItem() throws Exception {
        mMouse.mouseLeftClick(POINT_DELETE_CONFIRM.x, POINT_DELETE_CONFIRM.y);
        Thread.sleep(5000);
        return this;
    }
}
