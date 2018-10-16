package com.apddemo.core.models;

public class UrlRedirection {
	private String id; 
	private String oldUrl;
	private String newUrl;
	
	@Override
	public String toString() {
		return String.format("id[%s] oldUrl[%s] newUrl[%s]", id, oldUrl, newUrl);
	}
	
	public void setId (String id){
		this.id = id;
	}
	public String getId (){
		return this.id;
	}
	
	public void setOldUrl(String oldUrl){
		this.oldUrl = oldUrl;
	}
	
	public String getOldUrl(){
		return this.oldUrl;
	}
	
	public void setNewUrl(String newUrl){
		this.newUrl = newUrl;
	}
	
	public String getNewUrl(){
		return this.newUrl;
	}
}
