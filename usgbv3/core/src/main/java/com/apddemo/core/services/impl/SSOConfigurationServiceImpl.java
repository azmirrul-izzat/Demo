package com.apddemo.core.services.impl;


import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;

import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.services.SSOConfigurationService;

/*
 * SSOConfigurationDetails Class
 * 
 * This class is the component which after deployment would be activated and will get the SSO configuration details
 *  and will get the hostname specific properties avaliable to the system
 *  
 *  Dependencies: com.usgb.core.services.impl.SSOConfigurationServiceImpl
 *                com.usgb.core.services.SSOConfigurationService
 *                
 */
@Component(immediate = true, service = SSOConfigurationService.class, configurationPid = "com.apddemo.core.services.SSOConfigurationDetails")
@Designate(ocd=SSOConfigurationServiceImpl.ConfigurationDetails.class, factory=true)
public class SSOConfigurationServiceImpl implements SSOConfigurationService{
	final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	// ssoConfiguration contains all the domain and its property mapping in a hashmap
	private Map<String, Map<String, String>> ssoConfiguration = new HashMap<String, Map<String, String>>();
	
	// injecting ConfigurationAdmin to get all the properties of the system
	@Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
	private ConfigurationAdmin configAdmin;
	
	@ObjectClassDefinition(name = "apddemo SSO Configuration")
    public @interface ConfigurationDetails {
		
		 @AttributeDefinition(name = "Client ID", defaultValue = "clientId")
		  String clientId();

		  @AttributeDefinition(name = "Client Secret", defaultValue = "clientSecret")
		  String clientSecret();

		  @AttributeDefinition(name = "Client Domain", defaultValue = "clientDomain")
		  String clientDomain();
		  
		  @AttributeDefinition(name = "Public Domain", defaultValue = "publicDomain")
		  String publicDomain();
    }
	
	final static String SSO_PID ="com.apddemo.core.services.SSOConfigurationDetails";
	/*
	 * This method would run on activate and would loop through the configuration for getting the SSO configuration
	 * This would also have the SSOconfiguration map ready for getting the properties based on the hostname.
	 * 
	 * -- removing this part of code as we need downtime - moving the code to getConfig
	 */
    @Activate
    @Modified
    public void activate(final ComponentContext componentContext) {
    	/*try {
			// reading all the list of configurations  set up in the system
			Configuration[] listConfigurations = configAdmin.listConfigurations(null);
			Map<String, String>  configMap = null;
			
			//looping the configurations
	        for (Configuration configuration : listConfigurations) {
	        	configMap = new HashMap<String, String>();
	        	
	        	// if the configuration is a SSO configutation Service
	        	if(configuration.getPid().startsWith("com.usgb.core.services.impl.SSOConfigurationServiceImpl")){
	        		LOG.debug("the pid's arre :"+configuration.getPid());
	        		// getting its properties
	        		Dictionary<String, Object> props = configuration.getProperties();
	        		configMap.put("clientId", PropertiesUtil.toString(props.get("clientId"), "defaultValue"));
	        		configMap.put("clientSecret", PropertiesUtil.toString(props.get("clientSecret"), "defaultValue"));
	        		configMap.put("clientDomain", PropertiesUtil.toString(props.get("clientDomain"), "defaultValue"));
	        		ssoConfiguration.put(PropertiesUtil.toString(props.get("publicDomain"), "defaultValue"), configMap);
	        	}
	        }
			
		} catch (IOException | InvalidSyntaxException e) {
			LOG.error("exception :"+e);
		}
    	LOG.info("the configuration set in the syste are :"+ssoConfiguration);*/
    }
    
    /*
     * This method would get the Configuration details of the hostname send to it.
     * It would return null if the configuration details are not set  for the hostname sent.
     * 
     * Getting the SSO Config details and storing in a map and sending the appropriate value
     */
    public Map<String, String> getConfig(String hostname){
    	LOG.info("input host name :"+hostname);
    	
    	try {
    		
    		String filter = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "=" + SSO_PID + ")";
			Configuration[] listConfigurations = configAdmin.listConfigurations(filter);
			LOG.info("listConfigurations :"+listConfigurations); 
			Dictionary<String, Object> props =null;
			Map<String, String>  configMap= null;
			for(Configuration config: listConfigurations){
				props = config.getProperties(); 
				//LOG.debug("props :"+props); 
				configMap  = new HashMap<String, String>();
				configMap.put("clientId", PropertiesUtil.toString(props.get("clientId"), "defaultValue"));
        		configMap.put("clientSecret", PropertiesUtil.toString(props.get("clientSecret"), "defaultValue"));
        		configMap.put("clientDomain", PropertiesUtil.toString(props.get("clientDomain"), "defaultValue"));
        		ssoConfiguration.put(PropertiesUtil.toString(props.get("publicDomain"), "defaultValue"), configMap);
			}
			LOG.info("the configuration set in the system are :"+ssoConfiguration);
			
		} catch (IOException e) {
			LOG.info("IOException in getConfig :"+e);
		} catch (InvalidSyntaxException e) {
			LOG.info("InvalidSyntaxException in getConfig :"+e);
		}
    	if(ssoConfiguration.containsKey(hostname)){
    		return ssoConfiguration.get(hostname);
    	}	
    	return null;
    }
}