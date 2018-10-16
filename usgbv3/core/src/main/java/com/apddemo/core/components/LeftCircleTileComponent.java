package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.models.ProductModel;
import com.apddemo.core.models.ArticleModel;
import com.apddemo.core.models.AssetsPagesCategoryModel;


public class LeftCircleTileComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(LeftCircleTileComponent.class);
	
	private String error;
	private String image;
	private String title;
	private String description;
	private List<ArticleModel> articleList;

	
	public List<ArticleModel> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<ArticleModel> articleList) {
		this.articleList = articleList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	


	@Override
	public void activate() throws Exception {		
		
		Node currentNode = getResource().adaptTo(Node.class);
	    NodeIterator ni =  currentNode.getNodes() ; 
	    articleList = new ArrayList<ArticleModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes() ; 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        articleList.add(setArticle(child2));
	        }
	    }
	}
	
	public ArticleModel setArticle(Node rawNode) {
		
		ArticleModel article = new ArticleModel();
		try {
			
			String link = rawNode.getProperty("link").getString();
			String style = rawNode.getProperty("linkStyle").getString();
			
			article.setLink(link);
			article.setImgAlt(style);
			
			Page parentPage = getPageManager().getPage(link);
			
			if(parentPage.getProperties().containsKey("pageTitle")){
				article.setTitle((String)parentPage.getProperties().get("pageTitle"));
			}
			
			if(parentPage.getProperties().containsKey("pageDescription")){
				article.setDescription((String)parentPage.getProperties().get("pageDescription"));
			}
			
			if(parentPage.getProperties().containsKey("pageImage")){
				article.setImage((String)parentPage.getProperties().get("pageImage"));
			}
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return article;
	}

}
