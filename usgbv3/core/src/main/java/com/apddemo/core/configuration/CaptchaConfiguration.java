package com.apddemo.core.configuration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = CaptchaConfiguration.class, immediate = true)
@Designate(ocd = CaptchaConfiguration.Config.class)
public class CaptchaConfiguration {

    @ObjectClassDefinition(name="Captcha Configuration",
            description = "Captcha configuration which will be used across the entire website")
    public @interface Config {

        @AttributeDefinition(name = "Client Secret Key")
        String getClientSecretKey() default "6Lfv8hkTAAAAANdHMX2H4KFHjhqVW2BWnS8eHrtI";

        @AttributeDefinition(name = "Client Site Key",
                description = "Client Site key that was generated")
        String getClientSiteKey() default "6Lfv8hkTAAAAANyFtPRDZRX879T7dvuHLPIx__CQ";
    }

    public String getClientSecretKey() {
        return clientSecretKey;
    }

    String clientSecretKey;

    public String getClientSiteKey() {
        return clientSiteKey;
    }

    String clientSiteKey;

    @Activate
    @Modified
    protected void activate(final CaptchaConfiguration.Config config) {
        clientSiteKey = config.getClientSiteKey();
        clientSecretKey = config.getClientSecretKey();
    }
}
