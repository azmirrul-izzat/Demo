package com.apddemo.core.models;

import java.util.ArrayList;
import java.util.List;

public class MegamenuModel {
	
	private String pagePath;
	private String styleType;
	private String link;
	private String title;
	private String name;
	private boolean isNoLandingPage;
	private String rightSectionTitle;
	private String bottomSectionTitle;
	private List<ArticleModel> sidePanel;
	private List<ArticleModel> bottomPanel;
	private List<MegamenuChildModel> child;
	
	
	public String getPagePath() {
		return pagePath;
	}
	
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	
	public String getStyleType() {
		return styleType;
	}
	
	public void setStyleType(String styleType) {
		this.styleType = styleType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNoLandingPage() {
		return isNoLandingPage;
	}

	public void setNoLandingPage(boolean isNoLandingPage) {
		this.isNoLandingPage = isNoLandingPage;
	}

	public String getRightSectionTitle() {
		return rightSectionTitle;
	}

	public void setRightSectionTitle(String rightSectionTitle) {
		this.rightSectionTitle = rightSectionTitle;
	}

	public String getBottomSectionTitle() {
		return bottomSectionTitle;
	}

	public void setBottomSectionTitle(String bottomSectionTitle) {
		this.bottomSectionTitle = bottomSectionTitle;
	}

	public List<ArticleModel> getSidePanel() {
		if(sidePanel == null) {
			sidePanel = new ArrayList<ArticleModel>();
		}
				
		return sidePanel;
	}

	public void setSidePanel(List<ArticleModel> sidePanel) {
		this.sidePanel = sidePanel;
	}

	public List<ArticleModel> getBottomPanel() {
		if(bottomPanel == null) {
			bottomPanel = new ArrayList<ArticleModel>();
		}
		
		return bottomPanel;
	}

	public void setBottomPanel(List<ArticleModel> bottomPanel) {
		this.bottomPanel = bottomPanel;
	}

	public List<MegamenuChildModel> getChild() {
		
		if(child == null) {
			child = new ArrayList<MegamenuChildModel>();
		}
		return child;
	}

	public void setChild(List<MegamenuChildModel> child) {
		this.child = child;
	}
	
	

	
	
	
	
}