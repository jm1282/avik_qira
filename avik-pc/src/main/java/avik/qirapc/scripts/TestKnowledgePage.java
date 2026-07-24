package avik.qirapc.scripts;

import com.motorola.g11n.tools.avik.client.win.Application;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinMain;
import com.motorola.g11n.tools.avik.client.win.annotation.AvikWinScript;
import com.motorola.g11n.tools.avik.client.win.log.AvikLogger;
import com.motorola.g11n.tools.avik.client.win.util.Utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.awt.Rectangle;

import avik.qirapc.pages.ExplorerPage;
import avik.qirapc.pages.KnowledgePage;
import avik.qirapc.utils.Qira;


/**
 * Sample of Avik Windows script in Java
 */
@AvikWinScript
public class TestKnowledgePage {

    private static final String SCREENSHOT_PREFIX = "LenovoQiraPC_KnowledgePage_";

    private static Application mApp;
    private static Rectangle mRect;
    private static boolean mQiraLaunched;
    protected AvikLogger mLogger = AvikLogger.INSTANCE;

    @BeforeAll
    public static void setup() throws Exception {
        mApp = Application.getInstance();
        Rectangle desktopRect = Utils.INSTANCE.getDesktopRect();
        mQiraLaunched = false;
        Qira.restartQira(mApp, desktopRect);
        mRect = Qira.getMainWindowRectangle(mApp);
        mQiraLaunched = true;
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (mRect != null && mQiraLaunched) {
            try {
                new ExplorerPage(mRect).tapReturnToBasePage();
            } catch (Exception ignored) {
                // Avoid a stray desktop click if launch/navigation fails before the main window is ready.
            }
        }
    }

    @AvikWinMain
    public void executeCapture() throws Exception {
        try {
            main();
        } catch (Exception e) {
            StackTraceElement[] error = e.getStackTrace();
            for (StackTraceElement stackTraceElement : error) {
                mLogger.warning(stackTraceElement.toString());
            }
            throw e;
        }
    }

    public void main() throws Exception {
        Thread.sleep(5000);

        ExplorerPage explorerPage = new ExplorerPage(mRect);
        KnowledgePage knowledgePage = new KnowledgePage(mRect);

        explorerPage.tapReturnToBasePage();
        knowledgePage.tapKnowledgeNav();
        capture("knowledge");

        knowledgePage.tapCategory();
        capture("category");
        knowledgePage.tapNeutralArea();

        knowledgePage.tapTags();
        capture("tags_1");
        knowledgePage.scrollTagsDown(4);
        capture("tags_2");
        knowledgePage.scrollTagsDown(4);
        capture("tags_3");
        knowledgePage.scrollTagsDown(4);
        capture("tags_4");
        knowledgePage.tapNeutralArea();

        knowledgePage.tapNew();
        capture("new");

        knowledgePage.tapCreateMemory();
        capture("create_memory");
        knowledgePage.tapMemoryCancel();

        knowledgePage.tapDelete();
        capture("delete_warning");
        knowledgePage.tapDeleteCancel();
    }

    private void capture(String screenName) throws Exception {
        mApp.takeDesktopScreenshot(SCREENSHOT_PREFIX + screenName);
    }
}
