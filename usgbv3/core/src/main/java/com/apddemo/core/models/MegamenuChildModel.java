package com.apddemo.core.models;

import java.util.ArrayList;
import java.util.List;

public class MegamenuChildModel {
	
	private String title;
	private String name;
	private String description;
	private String link;
	private List<MegamenuChildModel> child;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
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