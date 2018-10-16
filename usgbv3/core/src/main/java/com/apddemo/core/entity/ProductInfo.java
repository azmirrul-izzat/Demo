package com.apddemo.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ProductInfo {
	
	@Column(name="product_id")
	private String product_id;

	@Column(name="product_name")	
	private String product_name;

	@Column(name="product_path")
	private String product_path;

	@Column(name="product_url")
	private String product_url;

	@Column(name="product_quantity")
	private int product_quantity;


	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_path() {
		return product_path;
	}

	public void setProduct_path(String product_path) {
		this.product_path = product_path;
	}

	public String getProduct_url() {
		return product_url;
	}

	public void setProduct_url(String product_url) {
		this.product_url = product_url;
	}

	public int getProduct_quantity() {
		return product_quantity;
	}

	public void setProduct_quantity(int product_quantity) {
		this.product_quantity = product_quantity;
	}

	
	
}
