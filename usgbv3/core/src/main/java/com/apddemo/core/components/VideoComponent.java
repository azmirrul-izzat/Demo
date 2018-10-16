package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.apddemo.core.models.FeaturedProduct;


public class VideoComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(VideoComponent.class);
	
	final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    
	private String thumbnail;
	private String videoMP4;
	private String videoOGG;
	private String videoOri;
	private String componentId;
	private String youtubeID;
	private String error;

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public String getVideoMP4() {
		return videoMP4;
	}

	public void setVideoMP4(String videoMP4) {
		this.videoMP4 = videoMP4;
	}

	public String getVideoOGG() {
		return videoOGG;
	}

	public void setVideoOGG(String videoOGG) {
		this.videoOGG = videoOGG;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getYoutubeID() {
		return youtubeID;
	}

	public void setYoutubeID(String youtubeID) {
		this.youtubeID = youtubeID;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		
		
		//LOG.info("defaultInherit:");
		String videoType = (String) getProperties().get("assetType");
		String damThumb = (String) getProperties().get("thumbnail");
		componentId = "vid_" + String.valueOf(getProperties().hashCode());
		
		if(damThumb != null && !damThumb.isEmpty()) {
			thumbnail = damThumb;
			
		}else {
		}

		if("youtube".equalsIgnoreCase(videoType)) {
			
			String youtubeUrl = (String) getProperties().get("youtube");
			youtubeID = extractVideoIdFromUrl(youtubeUrl);
			
			if(damThumb != null && !damThumb.isEmpty()) {
				thumbnail = damThumb;
				
			}else {
				thumbnail = "http://img.youtube.com/vi/" + youtubeID + "/maxresdefault.jpg";
			}
			
			
		}else {
			try {
				String videoDam = (String) getProperties().get("videoDam");
				if(videoDam != null && !videoDam.isEmpty()) {

					Resource res = getResourceResolver().getResource(videoDam);
					Asset asset = res.adaptTo(Asset.class);
					
					//get the video thumbnail
					if(damThumb != null && !damThumb.isEmpty()) {
						thumbnail = damThumb;
						
					}else {
						Rendition renditionThumnail = asset.getRendition("cq5dam.thumbnail.319.319.png");
						thumbnail = renditionThumnail.getPath();
					}
					
					
					//get the MP4 video
//					Rendition renditionMP4 = asset.getRendition("cq5dam.video.iehq.mp4");
//					videoMP4 = renditionMP4.getPath();
					
					//get the OGG video
//					Rendition renditionOGG = asset.getRendition("cq5dam.video.firefoxhq.ogg");
//					videoOGG = renditionOGG.getPath();
					
					videoOri = videoDam;
					
				}else {
					thumbnail = "";
					videoMP4 = "";
					videoOGG = "";
					videoOri = "";
				}
				
			}catch (Exception e) {
				error = e.getMessage();
			}
			
			
		}
			
		
		
		
		

	}
	
	public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){
                return matcher.group(1);
            }
        }

        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }

	public String getVideoOri() {
		return videoOri;
	}

	public void setVideoOri(String videoOri) {
		this.videoOri = videoOri;
	}

    

}
