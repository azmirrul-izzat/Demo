package com.apddemo.core.configuration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = SolrServerConfiguration.class, immediate = true)
@Designate(ocd = SolrServerConfiguration.Config.class)
public class SolrServerConfiguration {

    @ObjectClassDefinition(name="Solr Server Configuration",
            description = "Solr Server Configuration which will be used across the entire website")
    public @interface Config {

    	@AttributeDefinition(name = "Protocol", defaultValue ="http", description = "Configuration value")
	    String protocolValue();
	     
	    @AttributeDefinition(name = "Solr Server Name", defaultValue ="localhost", description = "Server name or IP address")
	    String serverName();
	     
	    @AttributeDefinition(name = "Solr Server Port", defaultValue ="8983", description = "Server port")
	    String serverPort();
	     
	    @AttributeDefinition(name = "Solr Core Name", defaultValue ="collection", description = "Core name in solr server")
	    String serverCollection();
	     
	    @AttributeDefinition(name = "Content page path", defaultValue ="/content/we-retail", description = "Content page path from where solr has to index the pages")
	    String serverPath();
    	
    	
//    	
//        @AttributeDefinition(name = "Client Secret Key")
//        String getClientSecretKey() default "6Lfv8hkTAAAAANdHMX2H4KFHjhqVW2BWnS8eHrtI";
//
//        @AttributeDefinition(name = "Client Site Key",
//                description = "Client Site key that was generated")
//        String getClientSiteKey() default "6Lfv8hkTAAAAANyFtPRDZRX879T7dvuHLPIx__CQ";
    }

	    private String solrProtocol;
	    
	    private String solrServerName ;
	 
	    private String solrServerPort ;
	 
	    private String solrCoreName ;
	     
	    private String contentPagePath ;
    
	    public String getSolrProtocol() {
	        return this.solrProtocol;
	    }
	     
	    public String getSolrServerName() {
	        return this.solrServerName;
	    }
	 
	    public String getSolrServerPort() {
	        return this.solrServerPort;
	    }
	 
	    public String getSolrCoreName() {
	        return this.solrCoreName;
	    }
	     
	    public String getContentPagePath() {
	        return this.contentPagePath;
	    }
	    
//    public String getClientSecretKey() {
//        return clientSecretKey;
//    }
//
//    String clientSecretKey;
//
//    public String getClientSiteKey() {
//        return clientSiteKey;
//    }
//
//    String clientSiteKey;

    @Activate
    @Modified
    protected void activate(final SolrServerConfiguration.Config config) {
    	
    	//Populate the solrProtocol data member
        this.solrProtocol = config.protocolValue();
        this.solrServerName = config.serverName();
        this.solrServerPort = config.serverPort(); 
        this.solrCoreName = config.serverCollection(); 
        this.contentPagePath = config.serverPath(); 
//        clientSiteKey = config.getClientSiteKey();
//        clientSecretKey = config.getClientSecretKey();
    }
}
