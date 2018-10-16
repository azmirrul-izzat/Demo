package com.apddemo.core.components;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;


public class RetrieveALT extends WCMUsePojo {

	private String path;
	
	@Override  
    public void activate() {  
	    
		String assetPath = get("imgPath", String.class);
	
		Resource res = getResourceResolver().getResource(assetPath);
		Asset asset = res.adaptTo(Asset.class);
		
		String dcTitle = asset.getMetadataValue("dc:title");
		
		if(dcTitle != null && !dcTitle.isEmpty()) {
			path = dcTitle;
		}else {
			path = asset.getName();
		}
		
	}
	
	public String getPath() {
		
		return path;
		
	}
  
}  