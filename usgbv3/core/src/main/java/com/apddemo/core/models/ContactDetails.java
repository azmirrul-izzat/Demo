package com.apddemo.core.models;

import java.util.ArrayList;
import java.util.List;

public class ContactDetails {

	private String inquiryTopic;
	private String productInterest;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String phone;
	private String contact;
	private String email;

	
	public String getInquiryTopic() {
		return inquiryTopic;
	}
	public void setInquiryTopic(String inquiryTopic) {
		this.inquiryTopic = inquiryTopic;
	}
	public String getProductInterest() {
		return productInterest;
	}
	public void setProductInterest(String productInterest) {
		this.productInterest = productInterest;
	}
	
}
