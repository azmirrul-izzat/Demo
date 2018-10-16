package com.apddemo.core.servlets;


import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.services.CaptchaService;
import com.apddemo.core.services.ContactUSFormService;
import com.apddemo.core.utils.CountryUtils;
import com.apddemo.core.utils.StringUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Retrieve Page Properties",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/usgb/v3/pageproperty",
        "sling.servlet.resourceTypes="+ "cq:Page",
        "sling.servlet.selectors="+ "properties_v3",
        "sling.servlet.extensions="+ "json"
})
public class PagePropertyServlet  extends BaseAllMethodsServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8811236444436438274L;

	private static final Logger LOG = LoggerFactory.getLogger(PagePropertyServlet.class);

	private static final String JSON_KEY_PAGE_PROPERTIES_lIST = "pagePropertiesList";
	private static final String MASTERTEMPLATE3 = "mastertemplate3";
	private static final String PRODUCT_PREFIX = "/products";
	
    @Reference
    ContactUSFormService contactUSFormService;

    @Reference
    CaptchaService captchaService;

	@Reference
    private QueryBuilder builder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

    	response.setCharacterEncoding("utf-8");
    	JSONObject pagePropertiesList = new JSONObject();
    	
    	 try {
    		 
    		 String pageURL = null;
	        if(request.getRequestURI() != null){
	            try {
	                String refererURI = request.getRequestURI();
	                if(refererURI != null && refererURI.endsWith(".properties_v3.json")){
	                    pageURL = refererURI.replace(".properties_v3.json", "");
	                }
	            } catch (Exception e) {
	                LOG.error("URI SyntaxExvception is :"+e);
	            }
	        }else{
	            RequestParameter pageURLParameter = request.getRequestParameter(ApplicationConstants.PAGE_URL);
	            if(pageURLParameter != null){
	                pageURL = pageURLParameter.getString();
	            }else {
	            	String pathListJsonArrayStr = request.getParameter("paths");
	    			
	    	        if(StringUtils.isBlank(pathListJsonArrayStr)){
	    				String resourcePath = request.getResource().getPath();
	    				pathListJsonArrayStr = StringUtils.isNotBlank(resourcePath) ?
	    						String.format("[\"%s\"]", resourcePath) : null;
	    	        }
	    						
	    	        ResourceResolver resourceResolver = request.getResourceResolver();
	    			List<String> pathList = new ArrayList<String>();
	    			JSONArray pathJsonArray = new JSONArray(pathListJsonArrayStr);
	    			
	    			for(int i=0; i < pathJsonArray.length(); i++){
	    				String path = pathJsonArray.optString(i);
	    				if(StringUtils.isNotBlank(path)){
	    					pathList.add(path);
	    				}
	    			}
	    			
	    			pageURL = pathList.get(0);
	            }
	        }
	        
	        ResourceResolver resourceResolver = request.getResourceResolver();
	        Page contentPage = resourceResolver.resolve(pageURL).adaptTo(Page.class); 
	        
	        if(contentPage != null) {
	        	
	        	JSONArray pagePropertiesListArray = new JSONArray();
	    		LOG.info("getBasicInfo START " );
	        	pagePropertiesListArray.put(getBasicInfo(contentPage));
	    		LOG.info("getAdvanceInfo START " );
	        	pagePropertiesListArray.put(getAdvanceInfo(resourceResolver, contentPage));
				
	        	pagePropertiesListArray = mergeAll(pagePropertiesListArray);
				pagePropertiesList.put(JSON_KEY_PAGE_PROPERTIES_lIST, pagePropertiesListArray);
				
				
	        }
			
//	        JSONObject jsonObj = new JSONObject();
//			String jsonData = "testtttaa";
//			jsonObj.put("test", jsonData);
//			jsonObj.put("uri", pageURL);
//			jsonObj.put("ParameterNames", request.getRequestURI());
//			jsonObj.put("ParameterNrefererames", pageURL);
//			
//			JSONArray pagePropertiesListArray = new JSONArray();
//			pagePropertiesListArray.put(jsonObj);
//			
//			
//			pagePropertiesList.put(JSON_KEY_PAGE_PROPERTIES_lIST, pagePropertiesListArray);
			
			setJsonResponseOk(response, pagePropertiesList.toString());
			
			
			
	        
			
			
	        
    	 }catch (Exception e) {
			// TODO: handle exception
		}
    	

    }
    
    public JSONObject getBasicInfo(Page contentPage) {
    	
    	JSONObject basicInfoObject = new JSONObject();
    	
    	try {
    		ValueMap pageProperties = contentPage.getProperties();
    		
			basicInfoObject.put("pageTitle", contentPage.getTitle());
			basicInfoObject.put("pageDescription", contentPage.getDescription());			
			basicInfoObject.put("uri", contentPage.getPath());
			
			if(pageProperties.containsKey("pageImage")) {
    			basicInfoObject.put("pageImage", pageProperties.get("pageImage").toString());
    		}
			
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return basicInfoObject;
    	
    }
    
    public JSONObject getAdvanceInfo(ResourceResolver resourceResolver, Page contentPage) {
    	
    	JSONObject basicInfoObject = new JSONObject();
    	
    	try {
    		LOG.info("getAdvanceInfo START " );
    		Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(resourceResolver, contentPage.getPath());
//    		LOG.info("countryInfo = " + countryInfo.toString());
    		if(countryInfo != null) {
    			basicInfoObject.put("countryCode", countryInfo.get("countryCode"));
    			
    			JSONObject pageCategory = getPageCategory(resourceResolver, countryInfo.get("sitePath"), contentPage.getPath());
    			basicInfoObject.put("pageCategoryKey", pageCategory.get("key").toString());
        		basicInfoObject.put("pageCategoryName", pageCategory.get("name").toString());
    		}

    		LOG.info("getPageTemplate = " + getPageTemplate(contentPage));
        	if(getPageTemplate(contentPage).equalsIgnoreCase(MASTERTEMPLATE3)) {
        		
        		ValueMap pageProperties = contentPage.getProperties();
        		
        		basicInfoObject.put("categoryName", MASTERTEMPLATE3);
        		
        		JSONObject productCategory = getPageCategory(resourceResolver, countryInfo.get("sitePath") + PRODUCT_PREFIX, contentPage.getPath());
        		
        		basicInfoObject.put("categoryKey", productCategory.get("key").toString());
        		basicInfoObject.put("categoryName", productCategory.get("name").toString());
        		
        		JSONArray comparisonPropertiesList = new JSONArray();
        		JSONObject comparisonProperties = new JSONObject();
        		if(pageProperties.containsKey("physicalProperties")) {
        			comparisonProperties.put("physicalProperties", pageProperties.get("physicalProperties").toString());
        		}
        		
        		if(pageProperties.containsKey("featuresAndBenefits")) {
        			comparisonProperties.put("featuresAndBenefits", pageProperties.get("featuresAndBenefits").toString());
        		}
        		
        		if(pageProperties.containsKey("applications")) {
        			comparisonProperties.put("applications", pageProperties.get("applications").toString());
        		}
        		
        		if(pageProperties.containsKey("sustainability")) {
        			comparisonProperties.put("sustainability", pageProperties.get("sustainability").toString());
        		}
        		
        		comparisonPropertiesList.put(comparisonProperties);
        		basicInfoObject.put("comparisonProperties", comparisonPropertiesList);
        		
        		JSONArray resourceList = new JSONArray();
        		resourceList = getResourceList(resourceResolver, contentPage.getPath());
        		if(resourceList.length() > 0) {
        			basicInfoObject.put("resourceList", resourceList);
        		}
        	}
			
		} catch (JSONException e) {
			LOG.info("getAdvanceInfo JSONException = " + e.getMessage());
			e.printStackTrace();
		}
    	
    	
    	
    	return basicInfoObject;
    }
    
    private String getPageTemplate(Page page){
		String pageTemplateStr = page.getProperties().get("cq:template", String.class);
		
		String[] teplatePathElements = pageTemplateStr.split("/");
		if(teplatePathElements != null && teplatePathElements.length > 0){
			pageTemplateStr = teplatePathElements[teplatePathElements.length - 1];
		}
		
		return pageTemplateStr;
	}
	
	public JSONObject getPageCategory(ResourceResolver resourceResolver, String homePage, String pagePath) {
		JSONObject category = new JSONObject();

		LOG.info("getPageCategory Start = " + homePage + "  |  " + pagePath);
		
		if(pagePath.contains("jcr:content")) {
			pagePath = pagePath.substring(0,pagePath.indexOf("jcr:content"));
		}
		Page contentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class); 
		
		
		try {
			
			
			LOG.info("getPageCategory pagePath = " + pagePath);
			
			if(pagePath.indexOf(homePage) > -1) {
//				LOG.info("getPageCategory indexOf > -1 = " + pagePath);
				
				if(homePage.equals(pagePath)) {

//					LOG.info("getPageCategory equals = " + pagePath);
					Page parentPage = contentPage.getParent(); 
					
					category.put("key", "homepage");
					category.put("name", "Home Page");
					
				}else {
					
					if(!(homePage.equals(contentPage.getParent().getPath()))) {

//						LOG.info("getPageCategory if = " + pagePath);
						category = getPageCategory(resourceResolver, homePage, contentPage.getParent().getPath());
						
					}else {

//						LOG.info("getPageCategory else = " + pagePath);
						
						category.put("key", contentPage.getName());
						category.put("name", contentPage.getTitle());
					}
				}
				
			}else {

				LOG.info("getPageCategory indexOf not = " + pagePath);
				category.put("key", "others");
				category.put("name", "Others");
			}
			
				
			
			
		}catch (Exception e) {
			LOG.info("getPageCategory ERROR = " + e.getMessage());
		}
		
		
		
		
		return category;
	}
	
	public JSONArray mergeAll(JSONArray jsonArray) {
		
		JSONArray mergeArray = new JSONArray();
		JSONObject combined = new JSONObject();
		
		try {
			for(int i = 0; i < jsonArray.length(); i++) {
				
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				Iterator it = jsonObj.keys();
			    while (it.hasNext()) {
			        String key = (String)it.next();
			        combined.put(key, jsonObj.get(key));
			    }
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mergeArray.put(combined);
		
		return mergeArray;
		
	}
	
	public JSONArray getResourceList(ResourceResolver resourceResolver, String currentPath) {
		
		JSONArray resourceList = new JSONArray();
		
		try {
			
			SearchResult resultPages = queryResourceList(resourceResolver, currentPath);
			int totalMatchforPage = (int) resultPages.getTotalMatches();
			
			if(totalMatchforPage > 0){
				
				for(Hit hit : resultPages.getHits()){
					
					Node hitProperties = hit.getNode();
					NodeIterator ni =  hitProperties.getNodes() ; 
					while (ni.hasNext()) {
				        Node child = (Node)ni.nextNode(); 
				         
				        
				        NodeIterator ni2 =  child.getNodes() ; 
				        while (ni2.hasNext()) {
					        Node child2 = (Node)ni2.nextNode(); 
					        
					        if(child2.hasProperty("docPath")) {

						        JSONObject resource = new JSONObject();
						        
						        Resource res = resourceResolver.getResource(child2.getProperty("docPath").getString());
								Asset asset = res.adaptTo(Asset.class);
								
								resource.put("title", asset.getMetadataValue("dc:title"));
						        resource.put("docSrc", child2.getProperty("docPath").getString());
						        resourceList.put(resource);
					        }
					        
					        
				        }
				    }
				}
			}
		}catch (Exception e) {
			LOG.info("getResourceList Exception : " + e.getMessage());
		}
		
		
		return resourceList;
	}
	
	public SearchResult queryResourceList(ResourceResolver resourceResolver, String path) {

		Session session = resourceResolver.adaptTo(Session.class);
		Map<String, String> queryMap = new HashMap<String, String>();

		queryMap.put("path", path);
		queryMap.put("1_property", "sling:resourceType");
		queryMap.put("1_property.value", "apddemo/components/content/resources-component");
		queryMap.put("orderby", "@jcr:score");
		queryMap.put("orderby.sort", "desc");
		LOG.info("builder = " + builder);
		
		Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
		SearchResult result = query.getResult();
		
		return result;
	}
    	
    	
    	
}
