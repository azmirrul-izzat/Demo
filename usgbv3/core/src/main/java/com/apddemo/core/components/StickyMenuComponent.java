package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.apddemo.core.models.ContainerFluidModel;


public class StickyMenuComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(StickyMenuComponent.class);
	
	private List<ContainerFluidModel> containerFluidList;
	private String containerFluidTemplate = "apddemo/components/content/pd-container-fluid-placeholder";
	private String firstNode;
	private String lastNode;
	private String error;

	public List<ContainerFluidModel> getContainerFluidList() {
		return containerFluidList;
	}

	public void setContainerFluidList(List<ContainerFluidModel> containerFluidList) {
		this.containerFluidList = containerFluidList;
	}

	public String getFirstNode() {
		return firstNode;
	}

	public void setFirstNode(String firstNode) {
		this.firstNode = firstNode;
	}

	public String getLastNode() {
		return lastNode;
	}

	public void setLastNode(String lastNode) {
		this.lastNode = lastNode;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		Node currentNode = getResource().adaptTo(Node.class);
	    NodeIterator ni =  currentNode.getNodes() ; 
	    containerFluidList = new ArrayList<ContainerFluidModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes() ; 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        if(containerFluidTemplate.equalsIgnoreCase(child2.getProperty("sling:resourceType").getString())) {
		        	containerFluidList.add(setProductInfo(child2));
		        }
		        
	        }
	    }
	    
	    firstNode = containerFluidList.get(0).getNodeName();
	    lastNode = containerFluidList.get(containerFluidList.size() - 1).getNodeName();
	    
	}
	
	public ContainerFluidModel setProductInfo(Node rawNode) {
		
		ContainerFluidModel fluid = new ContainerFluidModel();
		
		try {
				
			if(rawNode.getProperty("title") != null && !(rawNode.getProperty("title").getString().isEmpty())){
				
				fluid.setTitle(rawNode.getProperty("title").getString());
				fluid.setNodeName(rawNode.getName());
				
				if(rawNode.getProperty("bgcolor") != null && !(rawNode.getProperty("bgcolor").getString().isEmpty())){
					
					fluid.setBgColor(rawNode.getProperty("bgcolor").getString());
				}
			}
						
		}catch (Exception e) {
			error = e.getMessage();
		}
		
		return fluid;
		
	}
    

}
