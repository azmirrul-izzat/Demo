package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.Gson;
import com.apddemo.core.services.StoreLocatorService;
import com.apddemo.core.utils.CountryUtils;


public class WhereToBuyTileComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(WhereToBuyTileComponent.class);
	
	private String placeholder;
	private Map<String, String> countryInfo;
	private List<String> stateList;
	private String error;
	
	
    StoreLocatorService storeLocatorService;


	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public Map<String, String> getCountryInfo() {
		return countryInfo;
	}

	public void setCountryInfo(Map<String, String> countryInfo) {
		this.countryInfo = countryInfo;
	}

	public List<String> getStateList() {
		return stateList;
	}

	public void setStateList(List<String> stateList) {
		this.stateList = stateList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		try {
			error = "";
			countryInfo = CountryUtils.retrieveDemoCountrybyPath(getResourceResolver(), getCurrentPage().getPath());
			String wtbUrl = countryInfo.get("wtbUrl").toString();
			String countryCode = countryInfo.get("countryCode").toString();
			
			SearchResult searchResult = getWhereToBuyComponent(wtbUrl);
			
			if(searchResult != null && searchResult.getTotalMatches() > 0 ) {
				
				Hit hit = searchResult.getHits().get(0);
				Node hitNode = hit.getNode();
				
				if(hitNode.hasProperty("matchField")) {
					placeholder = hitNode.getProperty("matchField").getValue().getString();
				}
			}
			
			storeLocatorService = ((StoreLocatorService) getSlingScriptHelper().getService(StoreLocatorService.class));
			String storeList = storeLocatorService.getAllStates(countryCode, getResourceResolver());
			
			Gson googleJson = new Gson();
			stateList = googleJson.fromJson(storeList, ArrayList.class);

			
		}catch (Exception e) {
			error = error + "ERROR : " + e.getMessage();
		}
		
		
		
		
	}

	public SearchResult getWhereToBuyComponent(String contentPage) {
		
		SearchResult result = null;
		
		try {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("path", contentPage);
			map.put("type", "nt:unstructured");
		    map.put("1_property", "sling:resourceType");
		    map.put("1_property.value", "apddemo/components/content/where-to-buy-component");
		    map.put("p.limit", "-1"); 
			
		    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
		    Session session = getResourceResolver().adaptTo(Session.class);
		    
		    Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
		    
		    result = query.getResult();
		    
		}catch (Exception e) {
			error = "Exception : " + e.getMessage() + "<br/>";
		}
		
	    
		return result;
	}
	
	
    

}
