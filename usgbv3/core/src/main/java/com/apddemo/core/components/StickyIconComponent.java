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
import com.apddemo.core.models.IconModel;
import com.apddemo.core.models.MixMediaModel;


public class StickyIconComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(StickyIconComponent.class);
	
	private List<IconModel> iconList;
	private List<IconModel> firstIconList;
	private List<IconModel> secondIconList;
	private String hideView;
	private String error;
	
	public List<IconModel> getIconList() {
		return iconList;
	}

	public void setIconList(List<IconModel> iconList) {
		this.iconList = iconList;
	}

	public List<IconModel> getFirstIconList() {
		return firstIconList;
	}

	public void setFirstIconList(List<IconModel> firstIconList) {
		this.firstIconList = firstIconList;
	}

	public List<IconModel> getSecondIconList() {
		return secondIconList;
	}

	public void setSecondIconList(List<IconModel> secondIconList) {
		this.secondIconList = secondIconList;
	}

	public String getHideView() {
		return hideView;
	}

	public void setHideView(String hideView) {
		this.hideView = hideView;
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
			
			Node currentNode = getResource().adaptTo(Node.class);
		    NodeIterator ni =  currentNode.getNodes() ; 
		    iconList = new ArrayList<IconModel>();
		    firstIconList = new ArrayList<IconModel>();
		    secondIconList = new ArrayList<IconModel>();
		    hideView = "";
		    
		    while (ni.hasNext()) {
		        Node child = (Node)ni.nextNode(); 
		         
		        
		        NodeIterator ni2 =  child.getNodes() ; 
		        while (ni2.hasNext()) {
			        Node child2 = (Node)ni2.nextNode(); 
			        
			        iconList.add(setIcon(child2));
		        }
		    }
		    
		    int size = iconList.size();
		    
		    if (size > 0) {
		    	for (int i = 0; i < size; i++) {
			    	
			    	if ( i < (size + 1) / 2) {
			    		
			    		firstIconList.add(iconList.get(i));
			    	}else {
			    		
			    		secondIconList.add(iconList.get(i));
			    	}
			    }
		    }
		    
		    hideView = setHidden();
		    
		    
		}catch (Exception e) {
			error = e.getMessage();
		}
	    
	}
	
	public IconModel setIcon(Node rawNode) {
		
		IconModel iconModel = new IconModel();
		
		try {
			
			
			if(rawNode.getProperty("title") != null && !(rawNode.getProperty("title").getString().isEmpty())) {
				iconModel.setTitle(rawNode.getProperty("title").getString());
			}
			
			if(rawNode.getProperty("link") != null && !(rawNode.getProperty("link").getString().isEmpty())) {
				iconModel.setLink(rawNode.getProperty("link").getString());
			}
			
			if(rawNode.getProperty("icon") != null && !(rawNode.getProperty("icon").getString().isEmpty())) {
				iconModel.setIcon(rawNode.getProperty("icon").getString());
			}
			
		}catch (Exception e) {
			error = e.getMessage();
		}
		
		
		return iconModel;
		
	}
	
	public String setHidden(){
		
		String hidden = "";
		String hideDestop = "";
		String hideMobile = "";
		
		if(getProperties().get("hideDestop") != null && !(getProperties().get("hideDestop").toString().isEmpty())) {
			hideDestop = getProperties().get("hideDestop").toString();
		}
		
		if(getProperties().get("hideMobile") != null && !(getProperties().get("hideMobile").toString().isEmpty())) {
			hideMobile = getProperties().get("hideMobile").toString();
		}
		
		if(("true".equalsIgnoreCase(hideDestop) || "yes".equalsIgnoreCase(hideDestop)) && ("true".equalsIgnoreCase(hideMobile) || "yes".equalsIgnoreCase(hideMobile))) {
			
			hidden = "hidden";
		}else if ("true".equalsIgnoreCase(hideDestop) || "yes".equalsIgnoreCase(hideDestop)) {
			
			hidden = "hidden-sm hidden-md hidden-lg";
		}else if ("true".equalsIgnoreCase(hideMobile) || "yes".equalsIgnoreCase(hideMobile)) {
			
			hidden = "hidden-xs";
		}
		
		return hidden;
	}
    

}
