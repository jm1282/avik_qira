package avik.demomode.pages;

import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import avik.demomode.dialogs.BaseDialog;
import avik.demomode.utils.CommonMethods;

public class BasePage {

    protected UiDevice mDevice;
    protected AvikUtility mUtils;
    protected ObjectUtils mObjectUtils;
    protected CommonMethods mCommonMethods;

    public BasePage(UiDevice device) throws Exception {
        this.mDevice = device;
        this.mUtils = AvikUtility.getInstance();
        this.mObjectUtils = new ObjectUtils();
        this.mCommonMethods = new CommonMethods();
    }

    public <T extends BaseDialog> T openDialog() throws Exception {
        mCommonMethods.clickButton();
        mUtils.sleep(Constants.ONE_SECOND);
        return (T) new BaseDialog();
    }

    public void nextExperienceCard() throws Exception {
        mCommonMethods.nextExperienceCard();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public void previousExperienceCard() throws Exception {
        mCommonMethods.previousExperienceCard();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public <T extends BasePage> T nextExperience(Class<T> pageType) throws Exception {
        mCommonMethods.nextExperience();
        return pageType.getDeclaredConstructor(UiDevice.class).newInstance(mDevice);
    }

    public void openMenu() throws Exception {
        mCommonMethods.openMenu();
        mUtils.sleep(Constants.THREE_SECONDS);
    }

    public <T extends BasePage> T goBack() throws Exception {
        mCommonMethods.goBack();
        return (T) new BasePage(mDevice);
    }
}