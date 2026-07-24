package avik.demomodelight.pages;

import androidx.test.uiautomator.UiDevice;

import com.motorola.frevoutils.code.utils.Constants;
import com.motorola.frevoutils.code.utils.ObjectUtils;
import com.motorola.g11n.avik.uiautomatoradapter.AvikUtility;

import avik.demomodelight.utils.CommonMethods;


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

    public void previousExperienceCard() throws Exception {
        mCommonMethods.previousExperienceCard();
        mUtils.sleep(Constants.ONE_SECOND);
    }

    public <T extends BasePage> T goBack() throws Exception {
        mCommonMethods.goBack();
        return (T) new BasePage(mDevice);
    }
}