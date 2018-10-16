package com.apddemo.core.models;

public class FeaturedProduct {
	
	private String Link;
	private String bgImgPath;
	private String description;
	private String descriptionfontcolor;
	private String buttonText;
	private String target;
	private String nodePath;
	public String getLink() {
		return Link;
	}
	public void setLink(String link) {
		if(link != null){
			if(link.startsWith("/content/")){
				link = link+ ".html";
			}
		}
		Link = link; 
	}
	public String getBgImgPath() {
		return bgImgPath;
	}
	public void setBgImgPath(String bgImgPath) {
		this.bgImgPath = bgImgPath;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescriptionfontcolor() {
		return descriptionfontcolor;
	}
	public void setDescriptionfontcolor(String descriptionfontcolor) {
		this.descriptionfontcolor = descriptionfontcolor;
	}
	public String getButtonText() {
		return buttonText;
	}
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getNodePath() {
		return nodePath;
	}
	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
	
	
}