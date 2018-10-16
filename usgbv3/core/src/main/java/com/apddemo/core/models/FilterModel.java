package com.apddemo.core.models;

import java.util.ArrayList;
import java.util.List;

import com.day.cq.tagging.Tag;

public class FilterModel {
	
	private Tag tagCategory;
	private List<Tag> tagValues;
	

	public Tag getTagCategory() {
		return tagCategory;
	}

	public void setTagCategory(Tag tagCategory) {
		this.tagCategory = tagCategory;
	}

	public List<Tag> getTagValues() {
		return tagValues;
	}

	public void setTagValues(List<Tag> tagValues) {
		if(tagValues == null) {
			tagValues = new ArrayList<Tag>();
		}
		
		this.tagValues = tagValues;
	}
	
	
	
	
	
}