package com.apddemo.core.models;

import java.util.List;

public class ShareData {
	private String uuid; 
	private List<String> paths;
	private String concatenatedPaths;
	
	public void setConcatenatedPaths(String newConcatenatedPaths){
		this.concatenatedPaths = newConcatenatedPaths;
	}
	
	public String getConcatenatedPaths(){
		return this.concatenatedPaths;
	}
	
	
	public void setUuid (String newUuid){
		this.uuid = newUuid;
	}
	public String getUuid (){
		return this.uuid;
	}
	
	public void setPaths(List <String> newPaths){
		this.paths = newPaths;
	}
	
	public List <String> getPaths(){
		return this.paths;
	}
}
