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
import com.apddemo.core.models.MixMediaModel;


public class MixMediaComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(MixMediaComponent.class);
	
	private List<MixMediaModel> mixMediaList;
	private String error;
	
	public List<MixMediaModel> getMixMediaList() {
		return mixMediaList;
	}

	public void setMixMediaList(List<MixMediaModel> mixMediaList) {
		this.mixMediaList = mixMediaList;
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
	    mixMediaList = new ArrayList<MixMediaModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes() ; 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        mixMediaList.add(setMixMedia(child2));
	        }
	    }
	    
	}
	
	public MixMediaModel setMixMedia(Node rawNode) {
		
		MixMediaModel mixmedia = new MixMediaModel();
		
		boolean isVideo = false;
		try {
			
			if(rawNode.getProperty("enableVideo") != null && 
					("yes".equalsIgnoreCase(rawNode.getProperty("enableVideo").getString()) || "true".equalsIgnoreCase(rawNode.getProperty("enableVideo").getString()))) {
				isVideo = true;
				mixmedia.setVideo(true);
			}
			
			if(isVideo) {
				
				
				if(rawNode.getProperty("videoDam") != null && !(rawNode.getProperty("videoDam").getString().isEmpty())){

					String videoDam = rawNode.getProperty("videoDam").getString();
							
					Resource res = getResourceResolver().getResource(videoDam);
					Asset asset = res.adaptTo(Asset.class);
					Rendition renditionThumnail = asset.getRendition("cq5dam.thumbnail.319.319.png");
					
					mixmedia.setLink(videoDam);
					mixmedia.setThumbnail(renditionThumnail.getPath());
				}
			}else {
				if(rawNode.getProperty("imgPath") != null && !(rawNode.getProperty("imgPath").getString().isEmpty())){
					
					String imgPath = rawNode.getProperty("imgPath").getString();
					mixmedia.setLink(imgPath);
					mixmedia.setThumbnail(imgPath);
					
					Resource res = getResourceResolver().getResource(imgPath);
					Asset asset = res.adaptTo(Asset.class);
					mixmedia.setAlt(asset.getMetadataValue("dc:title"));
				}
			}
			
			if(rawNode.getProperty("thumbnail") != null && !(rawNode.getProperty("thumbnail").getString().isEmpty())) {
				String thumbnail = rawNode.getProperty("thumbnail").getString();
				mixmedia.setThumbnail(thumbnail);
			}
			
			if(rawNode.getProperty("alt") != null && !(rawNode.getProperty("alt").getString().isEmpty())) {
				String alt = rawNode.getProperty("alt").getString();
				mixmedia.setAlt(alt);
			}
			
		}catch (Exception e) {
			error = e.getMessage();
		}
		
		
		return mixmedia;
		
	}
    

}
