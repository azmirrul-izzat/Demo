package com.apddemo.core.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class UserInfo {
	
	@Column(name="user_id")
	private String user_id;

	@Column(name="user_sso_id")	
	private String user_sso_id;

	@Column(name="display_name")
	private String display_name;

	@Column(name="user_first_name")
	private String user_first_name;

	@Column(name="user_last_name")
	private String user_last_name;

	@Column(name="email")
	private String email;
	
	@Column(name="created_date")
	private Date created_date;

	@Column(name="last_login_date")
	private Date last_login_date;

	
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_sso_id() {
		return user_sso_id;
	}
	
	public void setUser_sso_id(String user_sso_id) {
		this.user_sso_id = user_sso_id;
	}
	
	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getUser_first_name() {
		return user_first_name;
	}
	
	public void setUser_first_name(String user_first_name) {
		this.user_first_name = user_first_name;
	}
	
	public String getUser_last_name() {
		return user_last_name;
	}
	
	public void setUser_last_name(String user_last_name) {
		this.user_last_name = user_last_name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated_date() {
		return created_date;
	}
	
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	
	public Date getLast_login_date() {
		return last_login_date;
	}
	
	public void setLast_login_date(Date last_login_date) {
		this.last_login_date = last_login_date;
	}

	@Override
	public String toString() {
		return "UserInfo [user_id=" + user_id + ", user_sso_id="
				+ user_sso_id + ", user_first_name=" + user_first_name
				+ ", user_last_name=" + user_last_name + ", created_date="
				+ created_date + ", last_login_date=" + last_login_date + "]";
	}

	
}
