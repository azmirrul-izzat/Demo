package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.models.FilterModel;
import com.apddemo.core.models.MixMediaModel;
import com.apddemo.core.models.ProductModel;
import com.apddemo.core.models.SPProductPagesModel;

public class SPProductPagesComponent extends WCMUsePojo {

	private static final Logger LOG = LoggerFactory.getLogger(SPProductPagesComponent.class);
	
	// global variable specific to class
	private List<SPProductPagesModel> pageList ;
	

	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<SPProductPagesModel> getPageList() {
		return pageList;
	}

	public void setPageList(List<SPProductPagesModel> pageList) {
		this.pageList = pageList;
	}
	
	@Override
	public void activate() throws Exception {
		
		Node currentNode = getResource().adaptTo(Node.class);
	    NodeIterator ni =  currentNode.getNodes() ; 
	    pageList = new ArrayList<SPProductPagesModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes(); 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        pageList.add(setSideNode(child2));
		        
	        }
	    }
	    
	}

	public SPProductPagesModel setSideNode(Node rawNode) {
		
		SPProductPagesModel pageList = new SPProductPagesModel();
		
		try {
			

		
			if(rawNode.getProperty("path") != null && !(rawNode.getProperty("path").getString().isEmpty())) {
				
				String path = rawNode.getProperty("path").getString();
				
				Page page = getPageManager().getPage(path);
				
				if (page.getTitle() != null && !(page.getTitle().isEmpty())) {
					
					ValueMap pageProperties = page.getProperties();
					
					pageList.setTitle(page.getTitle().toString());
					pageList.setPath(page.getPath().toString());
					if (pageProperties.get("pageImage") != null && !(pageProperties.get("pageImage").toString().isEmpty())) {
						
						
						String image = pageProperties.get("pageImage").toString();
						pageList.setPageImage(image);
						
						
						Resource res = getResourceResolver().getResource(image);
						Asset asset = res.adaptTo(Asset.class);
						pageList.setAlt(asset.getMetadataValue("dc:title"));
						
						
					}
					
					LOG.info(pageList.getTitle());
					LOG.info(pageList.getPath());
				}
			}
			

			
			if(rawNode.getProperty("subtitle") != null && !(rawNode.getProperty("subtitle").getString().isEmpty())) {
				String subtitle = rawNode.getProperty("subtitle").getString();
				pageList.setSubtitle(subtitle);
			
			}
			
			
		}catch (Exception e) {
			error = e.getMessage();
		}
		return pageList;
		
	}
	
	
	
}
		
		
		
		
		