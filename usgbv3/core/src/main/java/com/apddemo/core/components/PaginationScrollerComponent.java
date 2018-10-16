package com.apddemo.core.components;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.models.ArticleModel;
import com.apddemo.core.models.ProductModel;


public class PaginationScrollerComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(PaginationScrollerComponent.class);
	
	private List<ArticleModel> articleList;
	private ArticleModel prevArticle;
	private ArticleModel nextArticle;
	private String error;


	public List<ArticleModel> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<ArticleModel> articleList) {
		this.articleList = articleList;
	}


	public ArticleModel getPrevArticle() {
		return prevArticle;
	}

	public void setPrevArticle(ArticleModel prevArticle) {
		this.prevArticle = prevArticle;
	}

	public ArticleModel getNextArticle() {
		return nextArticle;
	}

	public void setNextArticle(ArticleModel nextArticle) {
		this.nextArticle = nextArticle;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		
		
		prevArticle = new ArticleModel();
		nextArticle = new ArticleModel();
		List<ArticleModel> rawArticleList = new ArrayList<ArticleModel>();
		List<Page> siblings =  Lists.newArrayList(getCurrentPage().getParent().listChildren());
		
		error = "";
		//error = error + "siblings = " + siblings.size() + "<br/>";
		try {
			
			if(siblings.size() > 1) {
				
				for(Page sibling : siblings) {
			    	
					ArticleModel articleModel = new ArticleModel();
					articleModel.setTitle(sibling.getTitle());
					articleModel.setDescription(sibling.getDescription());
					articleModel.setLink(sibling.getPath());
					
					ValueMap pageProperties = sibling.getProperties();
					
					if(pageProperties.containsKey("pagedate")) {
						
						Calendar calendar = (Calendar) pageProperties.get("pagedate");
						Date date = (Date) calendar.getTime();
						articleModel.setArticleDate(date);
						
						
					}else {
						
						Calendar calendar = sibling.getLastModified();
						Date date = (Date) calendar.getTime();
						articleModel.setArticleDate(date);
						
					}
					
					rawArticleList.add(articleModel);
			    	
			    }
				
				articleList = sortArticle(rawArticleList);
				
				int articleNo = 0;
				for(ArticleModel article : articleList) {
					
					if(article.getLink().equals(getCurrentPage().getPath())) {
						//error = error + "[" + articleNo + " ] ";
						if(articleNo != 0) {
							prevArticle = articleList.get(articleNo - 1);
						}
						
						if(articleNo < articleList.size()) {
							nextArticle = articleList.get(articleNo + 1);
						}
					}
					articleNo++;
				}
			}
			
			
			
			
			
		}catch(Exception ex) {
			error = error + ex.getMessage();
		}
		
		
	}
	
	public List<ArticleModel> sortArticle(List<ArticleModel> rawArticleList){
		
		Collections.sort(rawArticleList, new Comparator<ArticleModel>() {
		  public int compare(ArticleModel o1, ArticleModel o2) {
		      return o1.getArticleDate().compareTo(o2.getArticleDate());
		  }
		});
		
		return rawArticleList;
	}
	
	
	
	
    

}
