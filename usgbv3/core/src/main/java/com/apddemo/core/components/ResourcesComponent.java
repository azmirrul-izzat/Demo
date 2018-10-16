package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.models.AssetsPagesCategoryModel;
import com.apddemo.core.models.FilterModel;
import com.apddemo.core.models.MixMediaModel;
import com.apddemo.core.models.ProductModel;


public class ResourcesComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(ResourcesComponent.class);
	
	private List<AssetsPagesCategoryModel> resourceList;
	private List<Tag> filterResourceList;
	private String error;


	public List<AssetsPagesCategoryModel> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<AssetsPagesCategoryModel> resourceList) {
		this.resourceList = resourceList;
	}

	public List<Tag> getFilterResourceList() {
		return filterResourceList;
	}

	public void setFilterResourceList(List<Tag> filterResourceList) {
		this.filterResourceList = filterResourceList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		List<ProductModel> assetList = new ArrayList<ProductModel>();
		filterResourceList = new ArrayList<Tag>();
		resourceList = new ArrayList<AssetsPagesCategoryModel>();
	    error = "";
	    
	    Node currentNode = getResource().adaptTo(Node.class);
	    NodeIterator ni =  currentNode.getNodes() ; 
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes() ; 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        assetList.add(setProductInfo(child2));
	        }
	    }
	    
	    List<Tag> tagListingList = new ArrayList<Tag>();
	    
	    for(ProductModel resource : assetList) {
	    	
	    	tagListingList.add(resource.getTag());
	    }
	    
	    
	    //remove filter
	    filterResourceList = groupingFilter(tagListingList);
	    
	    for(Tag tag : filterResourceList) {
	    	
	    	AssetsPagesCategoryModel assetResource = new AssetsPagesCategoryModel();
	    	assetResource.setTag(tag);
	    	
	    	List<ProductModel> productList = new ArrayList<ProductModel>();
	    	
	    	for(ProductModel product : assetList) {
	    		
	    		if(tag.getName().equals(product.getTag().getName())) {
	    			productList.add(product);
	    		}
	    	}
	    	
	    	assetResource.setPages(productList);
	    	resourceList.add(assetResource);
	    }
	    
	    
	}
	
	public ProductModel setProductInfo(Node rawNode) {
		
		ProductModel product = new ProductModel();

		try {
			
			if(rawNode.hasProperty("docPath") && !(rawNode.getProperty("docPath").getString().isEmpty())){

				String docPath = rawNode.getProperty("docPath").getString();
				product.setLink(docPath);
				
				TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
				Resource res = getResourceResolver().getResource(docPath);
				Asset asset = res.adaptTo(Asset.class);
				
				product.setTitle(asset.getMetadataValue("dc:title"));
				product.setDescription(asset.getMetadataValue("dc:description"));
				
				Object[] tags = (Object[]) asset.getMetadata("cq:tags");
				
				for (Object obj : tags) {
				     Tag tag = tagManager.resolve(obj.toString());
				     
				     //check if tags is under asset category or not
				     if("category".equals(tag.getParent().getName())) {
				    	 product.setTag(tag);
					     //error = error + "taglist : " + tag.getName() + "<br/>";
				     }   
				}
				
				
			}
			
			if(rawNode.hasProperty("thumbnail") && !(rawNode.getProperty("thumbnail").getString().isEmpty())){

				String thumbnail = rawNode.getProperty("thumbnail").getString();
				product.setImage(thumbnail);
				
			}
			
		}catch (Exception e) {
			error = error + "<br/> Exception : " + e.getMessage();
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
	
	public List<Tag> groupingFilter(List<Tag> tags){
		
		List<FilterModel> filterGroups = new ArrayList<FilterModel>();
		HashSet<Tag> categoryList = new HashSet<Tag>();
		
		List<Tag> uniqueTags = removeDuplicate(tags);
		
		
		
		
		return uniqueTags;
	}
    

}
