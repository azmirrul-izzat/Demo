package com.apddemo.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class OrderInfo {

	@Column(name="order_id")
	private String order_id;
	
	@Column(name="user_id")
	private String user_id;

	@Column(name="order_country")	
	private String order_country;

	@Column(name="status")
	private String status;
	
	@Column(name="ship_id")
	private String ship_id;

	@Column(name="created_date")
	private Date created_date;

	@Column(name="modified_date")
	private Date modified_date;

	@Column(name="modified_by")
	private String modified_by;

	private List<ProductInfo> productList;
	
	private ShippingDetail shippingDetail;
	
	private UserInfo userInfo;
	
	private int ageOfSamplesOrder;
	
	private int ageOfLastModified;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getOrder_country() {
		return order_country;
	}

	public void setOrder_country(String order_country) {
		this.order_country = order_country;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShip_id() {
		return ship_id;
	}

	public void setShip_id(String ship_id) {
		this.ship_id = ship_id;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getModified_date() {
		return modified_date;
	}

	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}

	public String getModified_by() {
		return modified_by;
	}

	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}

	public List<ProductInfo> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductInfo> productList) {
		if(productList == null){
			this.productList = new ArrayList<ProductInfo>();
		}
		this.productList = productList;
	}

	public ShippingDetail getShipingDetail() {
		return shippingDetail;
	}

	public void setShippingDetail(ShippingDetail shippingDetail) {
		this.shippingDetail = shippingDetail;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	

	public int getAgeOfSamplesOrder() {
		return ageOfSamplesOrder;
	}

	public void setAgeOfSamplesOrder(int ageOfSamplesOrder) {
		this.ageOfSamplesOrder = ageOfSamplesOrder;
	}

	public int getAgeOfLastModified() {
		return ageOfLastModified;
	}

	public void setAgeOfLastModified(int ageOfLastModified) {
		this.ageOfLastModified = ageOfLastModified;
	}

	@Override
	public String toString() {
		return "OrderInfo [order_id=" + order_id + ", user_id=" + user_id
				+ ", order_country=" + order_country + ", status=" + status
				+ ", ship_id=" + ship_id + ", created_date=" + created_date
				+ ", modified_date=" + modified_date + ", modified_by="
				+ modified_by + "]";
	}
	
	
	
}
