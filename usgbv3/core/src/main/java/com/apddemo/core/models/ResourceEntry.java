package com.apddemo.core.models;

public class ResourceEntry {

	private String title;
	private String docSrc;
	private String thumbnailPath;
	public void setTitle( String newTitle){
		this.title = newTitle;
	}
	
	public String getTitle (){
		return this.title;
	}
		
	public void setDocSrc(String newDocSrc){
		this.docSrc = newDocSrc;
	}
	
	public String getDocSrc (){
		return this.docSrc;
	}
	
	public void setThumbnailPath(String newThumbnailPath){
		this.thumbnailPath = newThumbnailPath;
	}
	public String getThumbnailPath(){
		return this.thumbnailPath;
	}
}
