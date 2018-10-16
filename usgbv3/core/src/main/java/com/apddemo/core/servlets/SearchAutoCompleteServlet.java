package com.apddemo.core.servlets;


import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.services.StoreLocatorService;
import com.apddemo.core.utils.CountryUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Get store search Store Locator",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/searchAutoComplete"
        })
public class SearchAutoCompleteServlet extends BaseAllMethodsServlet {
    private static Logger LOG = LoggerFactory.getLogger(SearchAutoCompleteServlet.class);

    @Reference
    StoreLocatorService storeLocatorService;
    
    private static String AUTO_COMPLETE_PREFIX = "/config-node/autocomplete";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        

        String pageURL = null;
        
        try {
        	
        	if(request.getHeader("referer") != null){
                try {
                    String refererURI = new URI(request.getHeader("referer")).getPath();
                    if(refererURI != null && refererURI.endsWith(".html")){
                        pageURL = refererURI.replace(".html", "");
                    }
                } catch (URISyntaxException e) {
                    LOG.error("URI SyntaxExvception is :"+e);
                }
            }else{
                RequestParameter pageURLParameter = request.getRequestParameter(ApplicationConstants.PAGE_URL);
                if(pageURLParameter != null){
                    pageURL = pageURLParameter.getString();
                }
            }
        	
//        	pageURL = "/content/demo/en_au";
        	
        	if(!pageURL.isEmpty()) {
        		
				JSONArray autoCompleteList = new JSONArray();
				 
        		Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(request.getResourceResolver(), pageURL);
        		
        		String path = countryInfo.get("sitePath").toString() + AUTO_COMPLETE_PREFIX;
        		Resource autoCompleteResource= request.getResourceResolver().getResource(path);
        		
        		if(autoCompleteResource != null) {
        			
        			Iterable<Resource> children = autoCompleteResource.getChildren();
        			
        			 for(Resource autoComplete : children){
        				 
        				 ValueMap valueMap = autoComplete.getValueMap();
        				 
        				 
        				 if(valueMap.containsKey("keyword")) {
        					 autoCompleteList.put(valueMap.get("keyword", String.class));
        					 
        				 }
        						 
        				 
        			 }
        		}
        		
        		this.setJsonResponseOk(response,autoCompleteList.toString());
        	}
        	
        }catch(Exception e){
			this.setJsonResponse(response, "{\"ERROR\":\"exception"+ e.getMessage() + "\"}", 500);
		}
        
        
        
    }
}
