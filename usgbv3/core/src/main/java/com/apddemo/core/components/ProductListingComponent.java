package com.apddemo.core.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.apddemo.core.models.FilterModel;
import com.apddemo.core.models.ProductModel;
import com.apddemo.core.utils.CountryUtils;


public class ProductListingComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(ProductListingComponent.class);
	
	private List<ProductModel> parentListingList;
	private List<ProductModel> productListingList;
	private List<FilterModel> filterListingList;
	private String productListingJson;
	private String error;

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

	
	public List<ProductModel> getParentListingList() {
		return parentListingList;
	}

	public void setParentListingList(List<ProductModel> parentListingList) {
		this.parentListingList = parentListingList;
	}

	public List<ProductModel> getProductListingList() {
		return productListingList;
	}

	public void setProductList(List<ProductModel> productListingList) {
		this.productListingList = productListingList;
	}

	public List<FilterModel> getFilterListingList() {
		return filterListingList;
	}

	public void setFilterListingList(List<FilterModel> filterListingList) {
		this.filterListingList = filterListingList;
	}

	public String getProductListingJson() {
		return productListingJson;
	}

	public void setProductListingJson(String productListingJson) {
		this.productListingJson = productListingJson;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		
		parentListingList = new ArrayList<ProductModel>();
	    productListingList = new ArrayList<ProductModel>();
	    filterListingList = new ArrayList<FilterModel>();
	    List<Tag> tagListingList = new ArrayList<Tag>();
	    
	    try {
	    	
	    	List<Page> siblings =  Lists.newArrayList(getCurrentPage().getParent().listChildren()); 
			List<Page> childrens =  Lists.newArrayList(getCurrentPage().listChildren()); 
	    	
			for(Page sibling : siblings) {
		    	
		    	ProductModel siblingProduct = setProductInfo(sibling);
		    	parentListingList.add(siblingProduct);
		    	
		    }

		    for(Page child : childrens) {
		    	
		    	ProductModel childProduct = setProductInfo(child);
		    	productListingList.add(childProduct);
		    	tagListingList.addAll(childProduct.getTagList());
		    	
		    }
		    
		    //remove filter
		    filterListingList = groupingFilter(tagListingList);
		    
		    productListingJson = convertToJsonString();
		    
		    //String json = new Gson().toJson(productListingList );
		    
//		    error = new Gson().toJson(productListingList.get(0) );
	    	
	    }catch (Exception e) {
			error = error + e.getMessage();
		}
	    
	    
	}
	
	public ProductModel setProductInfo(Page page) {
		
		ProductModel product = new ProductModel();
		ValueMap pageProperties = page.getProperties();
		
		try {
			
			product.setName(page.getName());
			product.setTitle(page.getTitle());
			product.setLink(page.getPath());
			product.setDescription(page.getDescription());
			
			if(page.getTags() != null) {
				product.setTagList(Arrays.asList( page.getTags()));
			}
			
			if(pageProperties.get("pageImage") != null && !(pageProperties.get("pageImage").toString().isEmpty())){
				
				product.setImage(pageProperties.get("pageImage").toString());
			}			
			

			Calendar calendar = pageProperties.get("cq:lastModified", Calendar.class);
			Date date = (Date) calendar.getTime();
			product.setCreatedDate(formatter.format(date));
			
		}catch (Exception e) {
			error = e.getMessage();
		}
		
		
		
		return product;
		
	}
	
	public List<Tag> removeDuplicate(List<Tag> tags){
		
		List<Tag> uniqueTags = new ArrayList<Tag>();
		// remove duplicate
	    HashSet<Tag> hashSet = new HashSet(tags);
	    uniqueTags = new ArrayList<Tag>(hashSet);
	    
	    return uniqueTags;
	}
	
	public List<FilterModel> groupingFilter(List<Tag> tags){
		
		List<FilterModel> filterGroups = new ArrayList<FilterModel>();
		HashSet<Tag> categoryList = new HashSet<Tag>();
		
		List<Tag> uniqueTags = removeDuplicate(tags);
		for(Tag singleTag : uniqueTags) {
			categoryList.add(singleTag.getParent());
		}
		
		for(Tag singleTag : categoryList) {
			
			FilterModel filter = new FilterModel();
			List<Tag> valuesTag = new ArrayList<Tag>();
			
			filter.setTagCategory(singleTag);
			
			for(Tag uniqueTag : uniqueTags) {
				
				if((singleTag.getName()).equalsIgnoreCase(uniqueTag.getParent().getName())) {
					valuesTag.add(uniqueTag);
				}
			}
			filter.setTagValues(valuesTag);
			filterGroups.add(filter);
		}
		
		
		
		return filterGroups;
	}
	
	public String convertToJsonString() {
		
		String productListing = "";
		JSONObject basicInfoObject = new JSONObject();
		
		try {

			Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(getResourceResolver(), getCurrentPage().getPath());
			
			JSONObject categoryJson = getPageCategory(getResourceResolver(), countryInfo.get("sitePath")+"/products", getCurrentPage().getPath());
			basicInfoObject.put("category_key", categoryJson.get("key"));
			
			basicInfoObject.put("country_key", countryInfo.get("countryCode"));
			
			basicInfoObject.put("product_listing_filter", convertFilterToJson());
			
			if(getProperties().containsKey("enableShoutout") && "true".equalsIgnoreCase((String) getProperties().get("enableShoutout"))) {
				basicInfoObject.put("shoutout", true);
			}else {
				basicInfoObject.put("shoutout", false);
			}
			
			
			basicInfoObject.put("product_result", convertProductListingListToJson());
			productListing = basicInfoObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return productListing;
	}
	
	public JSONArray convertFilterToJson() {
		
		JSONArray filterJson = new JSONArray();
		JSONObject filterInfoJson = new JSONObject();
		
		try {
			filterInfoJson.put("catergory_key", "refine_search");
			filterInfoJson.put("category_title", "Refine Your Search");
			
			if(filterListingList != null) {

				JSONArray filterListingListJson = new JSONArray();
				for(FilterModel filterListing : filterListingList) {

					JSONObject filterListingJson = new JSONObject();
					filterListingJson.put("key", filterListing.getTagCategory().getName());
					filterListingJson.put("value", filterListing.getTagCategory().getTitle());
					
					
					if(filterListing.getTagValues() != null) {
						JSONArray secondFilterListingListJson = new JSONArray();
						for(Tag filterListingTag : filterListing.getTagValues()) {
							
							JSONObject secondFilterListingJson = new JSONObject();
							secondFilterListingJson.put("key", filterListingTag.getName());
							secondFilterListingJson.put("value", filterListingTag.getTitle());
							secondFilterListingListJson.put(secondFilterListingJson);
						}

						filterListingJson.put("child", secondFilterListingListJson);
						
					}
					
					filterListingListJson.put(filterListingJson);
				}
			
				filterInfoJson.put("category_child", filterListingListJson);
			}
			filterJson.put(filterInfoJson);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return filterJson;
		
	}
	
	public JSONArray convertProductListingListToJson() {
		
		JSONArray productJson = new JSONArray();
		
		try {
			
			if(productListingList != null) {
				int count = 1;
				for(ProductModel product : productListingList) {

					JSONObject productInfoJson = new JSONObject();
					productInfoJson.put("num", count);
					productInfoJson.put("img", product.getImage());
					productInfoJson.put("title", product.getTitle());
					
					for(Tag tag : product.getTagList()) {
						
						productInfoJson.put(tag.getParent().getName(), tag.getName());
					}
					
					productInfoJson.put("link", product.getLink() + ".html");
					productInfoJson.put("created_date", product.getCreatedDate());
					
					productJson.put(productInfoJson);
					count++;
				}
				
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		return productJson;
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
    

}
