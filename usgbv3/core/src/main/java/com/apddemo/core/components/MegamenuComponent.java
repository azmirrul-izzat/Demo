package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.apddemo.core.models.ArticleModel;
import com.apddemo.core.models.MegamenuChildModel;
import com.apddemo.core.models.MegamenuModel;


public class MegamenuComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(MegamenuComponent.class);
	
	private List<MegamenuModel> megamenuList;
	private String error;
	
	
	public List<MegamenuModel> getMegamenuList() {
		return megamenuList;
	}

	public void setMegamenuList(List<MegamenuModel> megamenuList) {
		this.megamenuList = megamenuList;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		

		megamenuList = new ArrayList<MegamenuModel>();
		error = "Start";
		
		try {
			
			for(int i = 1; i <= 8; i++) {
				
				MegamenuModel megamenu = retrieveMegamenubyTabNo(i);
				
				if(megamenu != null ) {
					megamenuList.add(megamenu);
				}
			}
			
		}catch (Exception e) {
			error = error + " Exception " + e.getMessage();
		}
		
	    
	}
	
	public MegamenuModel retrieveMegamenubyTabNo(int tabNo) {
		
		MegamenuModel tabMegamenu = new MegamenuModel();
		error = error + " retrieveMegamenubyTabNo = " + tabNo;
		
		if(getProperties().containsKey("pagePath" + tabNo)) {
			
			String path = (String) getProperties().get("pagePath" + tabNo);
			String styleType = (String) getProperties().get("styleType" + tabNo);
			
			error = error + " path = " + path;
			Page parentPage = getPageManager().getPage(path);
			
			ValueMap parentPageProperties = parentPage.getProperties();
			
			if(parentPageProperties.containsKey("excludeHeader")) {
				return null;
			}
			
			tabMegamenu.setPagePath(path);
			tabMegamenu.setLink(path);
			tabMegamenu.setTitle(parentPage.getTitle());
			tabMegamenu.setStyleType(styleType);
			
			if( ("style2".equalsIgnoreCase(styleType)) || ("style3".equalsIgnoreCase(styleType)) ) {
				
				List<MegamenuChildModel> megamenuChild = setSubMegamenu(parentPage, styleType);
				
				if(megamenuChild != null) {
					tabMegamenu.setChild(megamenuChild);
				}
				
				if("style2".equalsIgnoreCase(styleType)) {
					
					if(getProperties().containsKey("style2noLanding" + tabNo)) {
						tabMegamenu.setNoLandingPage(true);
					}
				}
				if("style3".equalsIgnoreCase(styleType)) {

					Node currentNode = getResource().adaptTo(Node.class);
					
					try {
						
						if(getProperties().containsKey("style3noLanding" + tabNo)) {
							tabMegamenu.setNoLandingPage(true);
						}
						
						NodeIterator ni =  currentNode.getNodes() ; 
						
						while (ni.hasNext()) {
					        Node child = (Node)ni.nextNode(); 
					         LOG.info("bottomSection = " + child.getName() + " | " + "bottomSection" + tabNo);
					         if(child.getName().equals("bottomSection" + tabNo)) {
					        	 	if(getProperties().containsKey("bottomSectionTitle" + tabNo)) {
					        	 		
					        	 		tabMegamenu.setBottomSectionTitle((String) getProperties().get("bottomSectionTitle" + tabNo));
					        	 	}

									List<ArticleModel> bottomPanel = new ArrayList<ArticleModel>();
									NodeIterator ni2 =  child.getNodes() ; 
						        	 LOG.info("bottomSection Size = " + child.getNodes().getSize());
									
							        while (ni2.hasNext()) {
							        	ArticleModel bottomSection = new ArticleModel();
								        Node child2 = (Node)ni2.nextNode(); 
								        
								        LOG.info("bottomTitle = " + child2.getProperty("bottomTitle" + tabNo).getString());
								        LOG.info("bottomLink = " + child2.getProperty("bottomLink" + tabNo).getString());
								        LOG.info("bottomDescription = " + child2.getProperty("bottomDescription" + tabNo).getString());
								        LOG.info("bottomAlt = " + child2.getProperty("bottomAlt" + tabNo).getString());
								        LOG.info("bottomImgPath = " + child2.getProperty("bottomImgPath" + tabNo).getString());
								        
								        if(child2.getProperty("bottomTitle" + tabNo) != null) {
								        	bottomSection.setTitle(child2.getProperty("bottomTitle" + tabNo).getString());
								        }
								        
								        if(child2.getProperty("bottomLink" + tabNo) != null) {
								        	bottomSection.setLink(child2.getProperty("bottomLink" + tabNo).getString());
								        }

								        if(child2.getProperty("bottomDescription" + tabNo) != null) {
								        	bottomSection.setDescription(child2.getProperty("bottomDescription" + tabNo).getString());
								        }

								        if(child2.getProperty("bottomAlt" + tabNo) != null) {
								        	bottomSection.setImgAlt(child2.getProperty("bottomAlt" + tabNo).getString());
								        }

								        if(child2.getProperty("bottomImgPath" + tabNo) != null) {
								        	bottomSection.setImage(child2.getProperty("bottomImgPath" + tabNo).getString());
								        }
								        
								        bottomPanel.add(bottomSection);
							        }
					        	 
						        tabMegamenu.setBottomPanel(bottomPanel);
					         }
					         
					         if(child.getName().equals("rightSection" + tabNo)) {
					        	 	
					        	 	if(getProperties().containsKey("rightSectionTitle" + tabNo)) {
					        	 		
					        	 		tabMegamenu.setRightSectionTitle((String) getProperties().get("rightSectionTitle" + tabNo));
					        	 	}

									List<ArticleModel> sidePanel = new ArrayList<ArticleModel>();
									NodeIterator ni2 =  child.getNodes() ; 
									
							        while (ni2.hasNext()) {
							        	ArticleModel rightSection = new ArticleModel();
								        Node child2 = (Node)ni2.nextNode(); 


								        if(child2.getProperty("rightTitle" + tabNo) != null) {
								        	rightSection.setTitle(child2.getProperty("rightTitle" + tabNo).getString());
								        }

								        if(child2.getProperty("rightLink" + tabNo) != null) {
								        	rightSection.setLink(child2.getProperty("rightLink" + tabNo).getString());
								        }

								        sidePanel.add(rightSection);
							        }
					        	 
							        tabMegamenu.setSidePanel(sidePanel);
					         }
					        
					    }
						
					}catch (Exception e) {
						error = error + " Exception " + e.getMessage();
					}
				    
				    
					
//					getProperties().
				}
			}
		}else {
			return null;
		}
		
		
		return tabMegamenu;
	}
	
	
	public List<MegamenuChildModel> setSubMegamenu(Page menuPage, String styleType){
		
		List<MegamenuChildModel> subMegamenu = new ArrayList<MegamenuChildModel>();
		
		List<Page> listOfPages =  Lists.newArrayList(menuPage.listChildren());
		
		for(Page subPage : listOfPages) {
			
			ValueMap subPageProperties = subPage.getProperties();
			
			//Skip if excludeHeader at pageproperties is ticked
			if(subPageProperties.containsKey("excludeHeader")) {
				continue;
			}
			
			MegamenuChildModel megasubPage = new MegamenuChildModel();
			megasubPage.setTitle(subPage.getTitle());
			megasubPage.setLink(subPage.getPath());
			megasubPage.setDescription(subPage.getDescription());
			
			if("style2".equalsIgnoreCase(styleType)) {
				
				List<MegamenuChildModel> superSubMegamenu = new ArrayList<MegamenuChildModel>();
				superSubMegamenu = setSubMegamenu(subPage, "none");
				megasubPage.setChild(superSubMegamenu);
			}
			
			subMegamenu.add(megasubPage);
		}
		
		
		return subMegamenu;
	}
    

}
