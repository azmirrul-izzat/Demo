package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.apddemo.core.models.FeaturedProduct;


public class FeatureCardsCarousel extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(FeatureCardsCarousel.class);
	
	private static String BACKGROUD_IMAGE_PATH="bgImgPath";
	private static String BUTTON_TEXT="buttonText";
	private static String DESCRIPTION="description";
	private static String FONT_COLOR_DESCRIPTION="fontColorDescription";
	private static String LINK="link";
	private static String TARGET="target";
	
	private boolean cancelInherit;
	private List<FeaturedProduct> featuredProductList;
	private String customCode;
	
	public boolean isCancelInherit() {
		return cancelInherit;
	}
	public void setCancelInherit(boolean cancelInherit) {
		this.cancelInherit = cancelInherit;
	}
	public List<FeaturedProduct> getFeaturedProductList() {
		return featuredProductList;
	}
	public void setFeaturedProductList(List<FeaturedProduct> featuredProductList) {
		this.featuredProductList = featuredProductList;
	}
	public String getCustomCode() {
		return customCode;
	}
	public void setCustomCode(String customCode) {
		this.customCode = customCode;
	}
	

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 * This method would be called on activate and call of this component.
	 */
	@Override
	public void activate() throws Exception {		
		
		//LOG.info("defaultInherit:");
		ValueMap valueMap = getProperties();
		if(valueMap != null && valueMap.containsKey("inherit")){
			String defaultInherit = (String) valueMap.get("inherit");
			//LOG.info("defaultInherit:"+defaultInherit);
			List<FeaturedProduct> tempFeaturedProductList = null;
			if(defaultInherit != null && "true".equals(defaultInherit)){
				// here we are supposed to get the details from the parent and display the featured products
				Page parentPage = getCurrentPage().getParent();
				//LOG.info("tempalte name"+parentPage.getProperties().get("cq:template"));
				// looping and checking whether to inherit or display the properties
				while(parentPage != null){
					tempFeaturedProductList = getPageFeaturedProduct(parentPage);
					//LOG.info("tempFeaturedProductList:"+tempFeaturedProductList);
					if(tempFeaturedProductList == null){
						if("/apps/usgb/templates/mastertemplate2flexible".equals(parentPage.getProperties().get("cq:template"))){
							parentPage = null;
						}else{
							parentPage =  parentPage.getParent();
						}
					}else{
						featuredProductList = tempFeaturedProductList;
						break;
					} 
				}
				
			}
		}else{
			
			// we are supposed to get the properties value and display it
			//featuredProductList = getPageFeaturedProduct(getCurrentPage());
			Node featuredProductNode = getResource().adaptTo(Node.class);
			LOG.info("currentNode :"+featuredProductNode.getPath());
			if(featuredProductNode != null && featuredProductNode.hasNode("fp")){
				Node childFeaturedProductNode = featuredProductNode.getNode("fp");
					if(childFeaturedProductNode != null){
						NodeIterator featureproductChildNodes = childFeaturedProductNode.getNodes();
						if(featureproductChildNodes!= null){
							featuredProductList = new ArrayList<FeaturedProduct>();
							FeaturedProduct fp = null;
							while (featureproductChildNodes.hasNext()) {
								  fp = new FeaturedProduct();
							      Node node = featureproductChildNodes.nextNode();
							      if(node.hasProperty(FeatureCardsCarousel.BACKGROUD_IMAGE_PATH)){
							    	  fp.setBgImgPath(node.getProperty(FeatureCardsCarousel.BACKGROUD_IMAGE_PATH).getValue().getString());
							      }
							      if(node.hasProperty(FeatureCardsCarousel.BUTTON_TEXT)){
							    	  fp.setButtonText(node.getProperty(FeatureCardsCarousel.BUTTON_TEXT).getValue().getString());
							      }
							      if(node.hasProperty(FeatureCardsCarousel.DESCRIPTION)){
							    	  fp.setDescription(node.getProperty(FeatureCardsCarousel.DESCRIPTION).getValue().getString());
							      }
							      if(node.hasProperty(FeatureCardsCarousel.FONT_COLOR_DESCRIPTION)){
							    	  fp.setDescriptionfontcolor(node.getProperty(FeatureCardsCarousel.FONT_COLOR_DESCRIPTION).getValue().getString());
							      }
							      if(node.hasProperty(FeatureCardsCarousel.LINK)){
							    	  fp.setLink(node.getProperty(FeatureCardsCarousel.LINK).getValue().getString());
							      }
							      if(node.hasProperty(FeatureCardsCarousel.TARGET)){
							    	  fp.setTarget(node.getProperty(FeatureCardsCarousel.TARGET).getValue().getString());
							      }
							      fp.setNodePath(node.getPath()); 
							      //LOG.info("fp: "+fp);
							      featuredProductList.add(fp);
							     // LOG.info("featuredProductList: "+featuredProductList);
							 }
							
					}
							
				}
			}
			
		}
		if(valueMap != null && valueMap.containsKey("inherit")){
			cancelInherit = true;
		}
		if(valueMap != null && valueMap.containsKey("customcode")){
			customCode= (String) valueMap.get("customcode");
		}
		
		

	}

	/* 
	 * This method would return null if the featured product was asked to inherit from parent page
	 * or will return the featured product list of the page
	 */
	private  List<FeaturedProduct> getPageFeaturedProduct(Page page) {
		List<FeaturedProduct> featuredProductList = null;
		if(page != null){
			String pagePath = page.getPath();
			Node pageNode = getResourceResolver().resolve(pagePath).adaptTo(Node.class);
			FeaturedProduct fp = null;
			
			try {
				if(pageNode != null && pageNode.hasNode("jcr:content")){
					Node jcrNode = pageNode.getNode("jcr:content");
					//LOG.info("has featured product:"+jcrNode.hasNode("featuredproduct"));
					if(jcrNode != null && jcrNode.hasNode("featuredproduct")){
						//LOG.info("node is :"+jcrNode.getNode("featuredproduct"));
						Node featuredProductNode = jcrNode.getNode("featuredproduct");
						// if the inherit is selected for the featured product then we would return null
						if(featuredProductNode != null && featuredProductNode.hasProperty("inherit")){
							return null;
						}else{
							// we are getting the properties of the featured product 
							if(featuredProductNode != null && featuredProductNode.hasNode("fp")){
								Node childFeaturedProductNode = featuredProductNode.getNode("fp");
									if(childFeaturedProductNode != null){
										NodeIterator featureproductChildNodes = childFeaturedProductNode.getNodes();
										if(featureproductChildNodes!= null){
											featuredProductList = new ArrayList<FeaturedProduct>();
											while (featureproductChildNodes.hasNext()) {
												  fp = new FeaturedProduct();
											      Node node = featureproductChildNodes.nextNode();
											      if(node.hasProperty(FeatureCardsCarousel.BACKGROUD_IMAGE_PATH)){
											    	  fp.setBgImgPath(node.getProperty(FeatureCardsCarousel.BACKGROUD_IMAGE_PATH).getValue().getString());
											      }
											      if(node.hasProperty(FeatureCardsCarousel.BUTTON_TEXT)){
											    	  fp.setButtonText(node.getProperty(FeatureCardsCarousel.BUTTON_TEXT).getValue().getString());
											      }
											      if(node.hasProperty(FeatureCardsCarousel.DESCRIPTION)){
											    	  fp.setDescription(node.getProperty(FeatureCardsCarousel.DESCRIPTION).getValue().getString());
											      }
											      if(node.hasProperty(FeatureCardsCarousel.FONT_COLOR_DESCRIPTION)){
											    	  fp.setDescriptionfontcolor(node.getProperty(FeatureCardsCarousel.FONT_COLOR_DESCRIPTION).getValue().getString());
											      }
											      if(node.hasProperty(FeatureCardsCarousel.LINK)){
											    	  fp.setLink(node.getProperty(FeatureCardsCarousel.LINK).getValue().getString());
											      }
											      if(node.hasProperty(FeatureCardsCarousel.TARGET)){
											    	  fp.setTarget(node.getProperty(FeatureCardsCarousel.TARGET).getValue().getString());
											      }
											      fp.setNodePath(node.getPath());
											      //LOG.info("fp: "+fp);
											      featuredProductList.add(fp);
											     // LOG.info("featuredProductList: "+featuredProductList);
											 }
											
									}
											
								}
							}
						}
						
						
						
					}
				}
			} catch (RepositoryException e) {
				LOG.error("Exception in getParentPageFeaturedProduct in FeaturedProducts "+e);
			}
		}
		
		return featuredProductList;
	}

}
