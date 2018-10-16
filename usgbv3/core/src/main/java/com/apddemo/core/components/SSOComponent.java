package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.apddemo.core.services.SSOConfigurationService;

import java.util.Map;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSOComponent extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(SSOComponent.class);
    
	private SSOConfigurationService ssoConfig;
    
    private String clientId;
    private String clientDomain;

    public String getClientId() {
		return clientId;
    }
    
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientDomain() {
		return clientDomain;
	}
	
	public void setClientDomain(String clientDomain) {
		this.clientDomain = clientDomain;
	}

	@Override
    public void activate() throws Exception {
		
		ssoConfig = ((SSOConfigurationService) getSlingScriptHelper().getService(SSOConfigurationService.class));
		Map<String, String> config = ssoConfig.getConfig(getRequest().getServerName());
    	
		LOG.info("SSOComponent :: get sso configuration");
		
		clientId = ""; clientDomain = "";
    	if(config != null) {
    		clientId = config.get("clientId");
    		clientDomain = config.get("clientDomain");
    	}
    }

}
