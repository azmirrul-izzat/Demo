
package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.day.cq.wcm.api.Page;
import com.apddemo.core.models.PopUpIconTileModel;


public class PopUpIconTileComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(PopUpIconTileComponent.class);
	
	private PopUpIconTileModel popupIcon = new PopUpIconTileModel();
	private String error;
	
	
    
	private String test;
	
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	
	public PopUpIconTileModel getPopupIcon() {
		return popupIcon;
	}

	public void setPopupIcon(PopUpIconTileModel popupIcon) {
		this.popupIcon = popupIcon;
	}

	@Override
	public void activate() throws Exception {		

		popupIcon = new PopUpIconTileModel();
		Node currentNode = getResource().adaptTo(Node.class);
		
		
		test = "hello world";
		
		if(currentNode.hasProperty("bgcolor")) {
			
			String bgcolor = currentNode.getProperty("bgcolor").getString();
			popupIcon.setBgcolor(bgcolor);
			
		}
		if(currentNode.hasProperty("image")) {
			String image = currentNode.getProperty("image").getString();
			popupIcon.setImage(image);
			
			
			Resource res = getResourceResolver().getResource(image);
			Asset asset = res.adaptTo(Asset.class);
			popupIcon.setAlt(asset.getMetadataValue("dc:title"));
		}
					
		if(currentNode.hasProperty("alt") ) {
			String alt = currentNode.getProperty("alt").getString();
			popupIcon.setAlt(alt);
		}
		

		if(currentNode.hasProperty("title")) {
			String title = currentNode.getProperty("title").getString();
			popupIcon.setTitle(title);
		}
		

		if(currentNode.hasProperty("shortDesc")) {
			String shortDesc = currentNode.getProperty("shortDesc").getString();
			popupIcon.setShortDesc(shortDesc);
		}

		if(currentNode.hasProperty("description")) {
			String description = currentNode.getProperty("description").getString();
			popupIcon.setDescription(description);
		}
		
		String uuid = UUID.randomUUID().toString().replace("-", "");
		popupIcon.setUuid(uuid);
		
		LOG.info(popupIcon.getTitle());
		LOG.info(popupIcon.getImage());
		LOG.info(popupIcon.getAlt());
		LOG.info(popupIcon.getUuid());
	    
	    
	}
	
	
}
    


