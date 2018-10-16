package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ValueMap;
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
import com.apddemo.core.models.ProductModel;
import com.apddemo.core.models.SystemPageCategoryModel;


public class SystemContentComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(SystemContentComponent.class);
	
	private List<SystemPageCategoryModel> productList;
	private List<String> filterList;
	private String error;
	

	public List<SystemPageCategoryModel> getProductList() {
		return productList;
	}

	public void setProductList(List<SystemPageCategoryModel> productList) {
		this.productList = productList;
	}

	public List<String> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<String> filterList) {
		this.filterList = filterList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		productList = new ArrayList<SystemPageCategoryModel>();
		filterList = new ArrayList<String>();
		List<String> rawCategoryList = new ArrayList<String>();
		error = "";
		try {
			
			String parentPath = (String) getProperties().get("contentPath");
//			error = parentPath;
			SearchResult hotspotList = getSystemPages(parentPath);
			error = error + " hotspotList : " + hotspotList.getTotalMatches() + "<br/>";
			int hitNo = 0;
			
			if(hotspotList != null && hotspotList.getTotalMatches() > 0) {
				for (Hit hit : hotspotList.getHits()) {
					
					Node hitNode = hit.getNode();
					SystemPageCategoryModel systemPageModel = new SystemPageCategoryModel();
					
					if(hitNode.hasProperty("category")) {
						error = error + " category ade : " + hitNode.getProperty("category").getValue().getString() + "<br/>";
						systemPageModel.setCategory(hitNode.getProperty("category").getValue().getString());
						systemPageModel.setPageNo(hitNo);
						rawCategoryList.add(hitNode.getProperty("category").getValue().getString());
					}
					
					Node pageNode = getPagebyNode(hitNode);
					Page systemPage = getPageManager().getPage(pageNode.getPath());
					systemPageModel.setPage(systemPage);
					productList.add(systemPageModel);
//					Page parentPage = getPageManager().getPage(path);
					hitNo++;
					
				}
				filterList = new ArrayList<String>(new HashSet<String>(rawCategoryList));
			}
			
			
		}catch(Exception ex) {
			error = error + "Exception activate : " + ex.getMessage();
		}
		
//		error = hotspotList.toString();
		
//		Page parentPage = getPageManager().getPage(parentPath);
//		List<Page> categoryPages =  Lists.newArrayList(parentPage.listChildren());
//		
//		for(Page category : categoryPages) {
//			
//			PagesCategoryModel systemPages = new PagesCategoryModel();
//			List<ProductModel> products = setProduct(category);
//			systemPages.setCategory(category);
//			systemPages.setPages(products);
//			
//			productList.add(systemPages);
//		}
		
	}
	
	public Node getPagebyNode(Node node) {
		
		Node result = null;
		try {

			if("cq:Page".equalsIgnoreCase(node.getProperty("jcr:primaryType").getValue().getString())) {
				error = error + "node cq:page : " + node.getPath() + "<br/>";
				result = node;
			}else {
				error = error + "node else: " + node.getPath() + "<br/>";
				result = getPagebyNode(node.getParent());
			}
			
		}catch (Exception e) {
			error = error + "Exception getPagebyNode : " + e.getMessage();
		}
		
		return result;
		
	}
	
	public SearchResult getSystemPages(String contentPage) {
		
		SearchResult result = null;
		
		try {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("path", contentPage);
			map.put("type", "nt:unstructured");
		    map.put("1_property", "sling:resourceType");
		    map.put("1_property.value", "apddemo/components/content/hotspots-component");
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
