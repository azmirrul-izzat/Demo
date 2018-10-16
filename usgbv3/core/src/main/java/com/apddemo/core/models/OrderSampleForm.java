package com.apddemo.core.models;

public class OrderSampleForm {

	private String name;
	private String email;
	private String address;
	private String suburb;
	private String state;
	private String postcode;
	private String phone;
	private String segment;
	private String product;
	private String comments;
	private Boolean informNews;
	
	public OrderSampleForm(){
		
	}
	
	public OrderSampleForm(String name, String email, String address,
			String suburb, String state, String postcode, String phone,
			String segment, String product, String comments, Boolean informNews) {
		super();
		this.name = name;
		this.email = email;
		this.address = address;
		this.suburb = suburb;
		this.state = state;
		this.postcode = postcode;
		this.phone = phone;
		this.segment = segment;
		this.product = product;
		this.comments = comments;
		this.informNews = informNews;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSuburb() {
		return suburb;
	}
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Boolean getInformNews() {
		return informNews;
	}
	public void setInformNews(Boolean informNews) {
		this.informNews = informNews;
	}
}
