package com.apddemo.core.models;

import java.util.ArrayList;
import java.util.List;

import com.day.cq.tagging.Tag;

public class SearchContentModel {
	
	private String key;
	private String title;
	private String description;
	private String link;
	private String image;
	private String imgAlt;
	private String type;
	private String category_header;
	private String product_category;
	private List<String> tags;
	private String created_date;
	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImgAlt() {
		return imgAlt;
	}

	public void setImgAlt(String imgAlt) {
		this.imgAlt = imgAlt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		if(this.tags == null) {
			this.tags = new ArrayList<String>();
		}
		this.tags = tags;
	}

	public String getCategory_header() {
		return category_header;
	}

	public void setCategory_header(String category_header) {
		this.category_header = category_header;
	}

	public String getProduct_category() {
		return product_category;
	}

	public void setProduct_category(String product_category) {
		this.product_category = product_category;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	
	
	
	
}