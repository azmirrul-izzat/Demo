package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.apddemo.core.configuration.CaptchaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaComponent extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(CaptchaComponent.class);

    private String captchaSiteKey;

    public String getCaptchaSiteKey() {
        return captchaSiteKey;
    }


    private CaptchaConfiguration captchaConfiguration;


    @Override
    public void activate() throws Exception {
        this.captchaConfiguration = ((CaptchaConfiguration) getSlingScriptHelper().getService(CaptchaConfiguration.class));
        this.captchaSiteKey = captchaConfiguration.getClientSiteKey();
    }

}
