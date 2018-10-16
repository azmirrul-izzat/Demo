package com.apddemo.core.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ShippingDetail {

	
	@Column(name="ship_id")
	private String ship_id;

	@Column(name="ship_name")
	private String ship_name;

	@Column(name="ship_phone_no")
	private String ship_phone_no;

	@Column(name="ship_email")
	private String ship_email;

	@Column(name="ship_address1")
	private String ship_address1;

	@Column(name="ship_address2")
	private String ship_address2;

	@Column(name="ship_address3")
	private String ship_address3;

	@Column(name="ship_postcode")
	private String ship_postcode;

	@Column(name="ship_city")
	private String ship_city;

	@Column(name="ship_country")
	private String ship_country;

	@Column(name="created_date")
	private Date created_date;

	@Column(name="remark")
	private String remark;

	public String getShip_id() {
		return ship_id;
	}

	public void setShip_id(String ship_id) {
		this.ship_id = ship_id;
	}

	public String getShip_name() {
		return ship_name;
	}

	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}

	public String getShip_phone_no() {
		return ship_phone_no;
	}

	public void setShip_phone_no(String ship_phone_no) {
		this.ship_phone_no = ship_phone_no;
	}

	public String getShip_email() {
		return ship_email;
	}

	public void setShip_email(String ship_email) {
		this.ship_email = ship_email;
	}

	public String getShip_address1() {
		return ship_address1;
	}

	public void setShip_address1(String ship_address1) {
		this.ship_address1 = ship_address1;
	}

	public String getShip_address2() {
		return ship_address2;
	}

	public void setShip_address2(String ship_address2) {
		this.ship_address2 = ship_address2;
	}

	public String getShip_address3() {
		return ship_address3;
	}

	public void setShip_address3(String ship_address3) {
		this.ship_address3 = ship_address3;
	}

	public String getShip_postcode() {
		return ship_postcode;
	}

	public void setShip_postcode(String ship_postcode) {
		this.ship_postcode = ship_postcode;
	}

	public String getShip_city() {
		return ship_city;
	}

	public void setShip_city(String ship_city) {
		this.ship_city = ship_city;
	}

	public String getShip_country() {
		return ship_country;
	}

	public void setShip_country(String ship_country) {
		this.ship_country = ship_country;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "ShipingDetail [ship_id=" + ship_id 
				+ ", ship_name=" + ship_name
				+ ", ship_phone_no=" + ship_phone_no 
				+ ", ship_email=" + ship_email 
				+ ", ship_address1=" + ship_address1
				+ ", ship_address2=" + ship_address2 
				+ ", ship_address3=" + ship_address3 
				+ ", ship_postcode=" + ship_postcode
				+ ", ship_city=" + ship_city 
				+ ", ship_country=" + ship_country
				+ ", remark=" + remark
				+ ", created_date=" + created_date + "]";
	}

	
	

	
}
