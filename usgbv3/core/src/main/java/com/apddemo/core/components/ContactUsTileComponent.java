package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
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


public class ContactUsTileComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(ContactUsTileComponent.class);
	
	private Map<String, String> countryInfo;
	private List<String> inquiryTypeList;
	private String error;


	public Map<String, String> getCountryInfo() {
		return countryInfo;
	}

	public void setCountryInfo(Map<String, String> countryInfo) {
		this.countryInfo = countryInfo;
	}

	public List<String> getInquiryTypeList() {
		return inquiryTypeList;
	}

	public void setInquiryTypeList(List<String> inquiryTypeList) {
		this.inquiryTypeList = inquiryTypeList;
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
			
			Set<String> rawInquiryTypeList = new HashSet<>();
			inquiryTypeList = new ArrayList<String>();
			
			countryInfo = CountryUtils.retrieveDemoCountrybyPath(getResourceResolver(), getCurrentPage().getPath());
			String sitePath = countryInfo.get("sitePath").toString();
			
			String  contactsNodePath = sitePath+"/contacts";
			
			
			Resource contactNodeResource = getResourceResolver().resolve(contactsNodePath);
			
			if(contactNodeResource != null){
				
				Iterator<Resource> contactNodeIterator = contactNodeResource.listChildren();
				
				while(contactNodeIterator.hasNext()){
					
					Resource contact = contactNodeIterator.next();
	                ValueMap contactValueMap = contact.getValueMap();
	                if(contactValueMap.containsKey("inquiryTopic")){
	                	rawInquiryTypeList.add(contactValueMap.get("inquiryTopic").toString());
	                }
					
				}
				
				inquiryTypeList.addAll(rawInquiryTypeList);
				
			}

			
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
