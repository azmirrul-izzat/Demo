package com.apddemo.core.models;

import java.util.List;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

public class AssetsPagesCategoryModel {
	
	private Page category;
	private Tag tag;
	private List<ProductModel> pages;
	
	public Page getCategory() {
		return category;
	}
	
	public void setCategory(Page category) {
		this.category = category;
	}
	
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public List<ProductModel> getPages() {
		return pages;
	}
	
	public void setPages(List<ProductModel> pages) {
		this.pages = pages;
	}
	
	
	
	
	
	
	
	
}