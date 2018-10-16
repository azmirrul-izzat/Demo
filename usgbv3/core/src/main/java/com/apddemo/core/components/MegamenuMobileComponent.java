package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.models.ArticleModel;
import com.apddemo.core.models.MegamenuChildModel;
import com.apddemo.core.models.MegamenuModel;
import com.apddemo.core.utils.CountryUtils;


public class MegamenuMobileComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(MegamenuMobileComponent.class);
	
	private List<MegamenuModel> megamenuList;
	private String error;
	
	
	public List<MegamenuModel> getMegamenuList() {
		return megamenuList;
	}

	public void setMegamenuList(List<MegamenuModel> megamenuList) {
		this.megamenuList = megamenuList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		megamenuList = new ArrayList<MegamenuModel>();
		error = "Start";
		
		try {
			
			Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(getResourceResolver(), getCurrentPage().getPath());
			
			SearchResult resultPages = getMegamenuComponent(countryInfo.get("sitePath").toString());
			
			Hit hit = resultPages.getHits().get(0);
			
			ValueMap hitProperties = hit.getProperties();
			for(int i = 1; i <= 8; i++) {
				
				MegamenuModel megamenu = retrieveMegamenubyTabNo(hitProperties, i);
				
				if(megamenu != null ) {
					megamenuList.add(megamenu);
				}
			}
			
			
		}catch (Exception e) {
			error = error + " Exception " + e.getMessage();
		}
		
	    
	}
	
	public SearchResult getMegamenuComponent(String homepage) {
			
		SearchResult result = null;
		
		try {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("path", homepage);
			map.put("type", "nt:unstructured");
		    map.put("1_property", "sling:resourceType");
		    map.put("1_property.value", "apddemo/components/content/header-megamenu");
		    map.put("p.limit", "1"); 
			
		    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
		    Session session = getResourceResolver().adaptTo(Session.class);
		    
		    Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
		    
		    result = query.getResult();
		    
		}catch (Exception e) {
			error = "Exception : " + e.getMessage() + "<br/>";
		}
		
	    
		return result;
	}
	
	public MegamenuModel retrieveMegamenubyTabNo(ValueMap hitProperties, int tabNo) {
		
		MegamenuModel tabMegamenu = new MegamenuModel();
		error = error + " retrieveMegamenubyTabNo = " + tabNo;
		
		if(hitProperties.containsKey("pagePath" + tabNo)) {
			
			
			String path = (String) hitProperties.get("pagePath" + tabNo);
			String styleType = (String) hitProperties.get("styleType" + tabNo);
			
			error = error + " path = " + path;
			Page parentPage = getPageManager().getPage(path);
			
			ValueMap pageProperties = parentPage.getProperties();
			
			//Skip if excludeHeader at pageproperties is ticked
			if(pageProperties.containsKey("excludeHeader")) {
				return null;
			}
			
			tabMegamenu.setPagePath(path);
			tabMegamenu.setLink(path);
			tabMegamenu.setTitle(parentPage.getTitle());
			tabMegamenu.setName(parentPage.getName());
			tabMegamenu.setStyleType(styleType);
			
			if("style2".equalsIgnoreCase(styleType)) {
				if(hitProperties.containsKey("style2noLanding" + tabNo)) {
					tabMegamenu.setNoLandingPage(true);
				}
			}
			if("style3".equalsIgnoreCase(styleType)) {
				if(hitProperties.containsKey("style3noLanding" + tabNo)) {
					tabMegamenu.setNoLandingPage(true);
				}
			}
				
			List<MegamenuChildModel> megamenuChild = setSubMegamenu(parentPage, styleType);
			
			if(megamenuChild != null) {
				tabMegamenu.setChild(megamenuChild);
			}
			
		}else {
			return null;
		}
		
		
		return tabMegamenu;
	}
	
	
	public List<MegamenuChildModel> setSubMegamenu(Page menuPage, String styleType){
		
		List<MegamenuChildModel> subMegamenu = new ArrayList<MegamenuChildModel>();
		
		List<Page> listOfPages =  Lists.newArrayList(menuPage.listChildren());
		
		for(Page subPage : listOfPages) {
			
			ValueMap pageProperties = subPage.getProperties();
			
			if(pageProperties.containsKey("excludeHeader")) {
				continue;
			}
			
			MegamenuChildModel megasubPage = new MegamenuChildModel();
			megasubPage.setName(subPage.getName());
			megasubPage.setTitle(subPage.getTitle());
			megasubPage.setLink(subPage.getPath());
			megasubPage.setDescription(subPage.getDescription());
			
			if("style2".equalsIgnoreCase(styleType)) {
				
				List<MegamenuChildModel> superSubMegamenu = new ArrayList<MegamenuChildModel>();
				superSubMegamenu = setSubMegamenu(subPage, "none");
				megasubPage.setChild(superSubMegamenu);
			}
			
			subMegamenu.add(megasubPage);
		}
		
		
		return subMegamenu;
	}
	    

}
