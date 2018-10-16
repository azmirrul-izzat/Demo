package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.apddemo.core.models.SPProductResourcesModel;


public class SPProductResourcesComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(SPProductResourcesComponent.class);
	
	private List<SPProductResourcesModel> resourcesList;
	private String error;
	
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<SPProductResourcesModel> getResourcesList() {
		return resourcesList;
	}

	public void setResourcesList(List<SPProductResourcesModel> resourcesList) {
		this.resourcesList = resourcesList;
	}

	@Override
	public void activate() throws Exception {		

		Node currentNode = getResource().adaptTo(Node.class);
	    NodeIterator ni =  currentNode.getNodes() ; 
	    resourcesList = new ArrayList<SPProductResourcesModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        NodeIterator ni2 =  child.getNodes(); 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        resourcesList.add(setProductResources(child2));
	        }
	    }
	    
	}
	
	public SPProductResourcesModel setProductResources(Node n) {
		
		SPProductResourcesModel resourcesList = new SPProductResourcesModel();
		

		try {
			
			if(n.getProperty("title") != null && !(n.getProperty("title").getString().isEmpty())) {
					String title = n.getProperty("title").getString();
					resourcesList.setTitle(title);
				
			}
			if(n.getProperty("docPath") != null && !(n.getProperty("docPath").getString().isEmpty())) {
				String docPath = n.getProperty("docPath").getString();
				resourcesList.setDocPath(docPath);
			}
			
			if(n.getProperty("iconPath") != null && !(n.getProperty("iconPath").getString().isEmpty())) {
					
				String iconPath = n.getProperty("iconPath").getString();
				resourcesList.setIcon(iconPath);
				
				
				Resource res = getResourceResolver().getResource(iconPath);
				Asset asset = res.adaptTo(Asset.class);
				resourcesList.setAlt(asset.getMetadataValue("dc:title"));
			}
			
			if(n.getProperty("alt") != null && !(n.getProperty("alt").getString().isEmpty())) {
				String alt = n.getProperty("alt").getString();
				resourcesList.setAlt(alt);
			}
			
			LOG.info(resourcesList.getTitle());
			LOG.info(resourcesList.getDocPath());
			LOG.info(resourcesList.getIcon());
			LOG.info(resourcesList.getAlt());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			error = e.getMessage();
		}
		
		
	
		
		return resourcesList;
		
	}
}
    


