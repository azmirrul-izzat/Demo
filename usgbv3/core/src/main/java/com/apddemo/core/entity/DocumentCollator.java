package com.apddemo.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class DocumentCollator {
	
	@Column(name="doc_id")
	private String doc_id;

	@Column(name="doc_aem_id")	
	private String doc_aem_id;
	
	@Column(name="doc_name")
	private String doc_name;

	@Column(name="doc_path")	
	private String doc_path;

	@Column(name="doc_url")	
	private String doc_url;

	@Column(name="created_date")
	private Date created_date;

	@Column(name="user_id")
	private String user_id;
	

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String getDoc_aem_id() {
		return doc_aem_id;
	}

	public void setDoc_aem_id(String doc_aem_id) {
		this.doc_aem_id = doc_aem_id;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public String getDoc_path() {
		return doc_path;
	}

	public void setDoc_path(String doc_path) {
		this.doc_path = doc_path;
	}

	public String getDoc_url() {
		return doc_url;
	}

	public void setDoc_url(String doc_url) {
		this.doc_url = doc_url;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String toString() {
		return "DocumentCollator [doc_id=" + doc_id + ", doc_aem_id="
				+ doc_aem_id + ", doc_name=" + doc_name + ", doc_path="
				+ doc_path + ", doc_url=" + doc_url + ", created_date="
				+ created_date + ", user_id=" + user_id + "]";
	}
	
	
	
	
	
}
