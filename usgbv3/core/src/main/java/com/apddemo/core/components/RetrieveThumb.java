package com.apddemo.core.components;

import org.apache.sling.api.resource.Resource;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;


public class RetrieveThumb extends WCMUsePojo {

	private String path;
	
	@Override  
    public void activate() {  
	    
		String assetVideo = get("video", String.class);
		String assetPdf = get("pdf", String.class);
		
		if (assetVideo != null && !assetVideo.isEmpty()) {
		
			path = assetVideo;
		} else {
			
			Resource res = getResourceResolver().getResource(assetVideo);
			Asset asset = res.adaptTo(Asset.class);
			
			Rendition renditionThumbnail = asset.getRendition("cq5dam.thumbnail.140.100.png");
			
			path = renditionThumbnail.getPath();
			
		}
		if (assetPdf != null && !assetPdf.isEmpty()) {
			path = assetPdf;
		} else {
			
			String image = "/content/dam/demo/global/website/image/logo/pdf-icon.png";
			path = image;
		}
}
	
	public String getPath() {
		
		return path;
		
	}
	    
  
}  