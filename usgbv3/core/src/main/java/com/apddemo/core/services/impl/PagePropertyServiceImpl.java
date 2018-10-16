package com.apddemo.core.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonJcrNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
//import com.apddemo.core.models.ComparableProperty;
//import com.apddemo.core.models.ResourceEntry;
import com.apddemo.core.services.PagePropertyService;
import com.apddemo.core.utils.NodeUtils;
import com.apddemo.core.utils.StringUtils;

@Component
@Service
public class PagePropertyServiceImpl implements PagePropertyService {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final static String PROPERTY_NODE_PATH = "/jcr:content";
	private final static String PROPERTY_KEY_JCR_TITLE = "jcr:title";
	private final static String TEMPLATE_MASTERTEMPLATE3A = "mastertemplate3a";
	private final static String TEMPLATE_MASTERTEMPLATE3A_APS = "mastertemplate3aaps";
	private final static String TEMPLATE_MASTERTEMPLATE4 = "mastertemplate4";
	private final static String TEMPLATE_MASTERTEMPLATE2A = "mastertemplate2a";
	private final static String TEMPLATE_MASTERTEMPLATE3A_FLEXIBLE = "mastertemplate3aflexible";
	private final static String TEMPLATE_MASTERTEMPLATE3A_APS_FLEXIBLE = "mastertemplate3aapsflexible";
	private final static String TEMPLATE_MASTERTEMPLATE4_FLEXIBLE = "mastertemplate4flexible";
	private final static String TEMPLATE_MASTERTEMPLATE2A_FLEXIBLE = "mastertemplate2aflexible";
	private final static String TAG_PRODUCT_CATEGORY_KEYWORD = "productsCategory:productcategories/";
	
	@Override
	public List<Map<String, Object>> getMultiplePageProperties(ResourceResolver resourceResolver,
			List<String> pathList) {
		
		if(log.isDebugEnabled()){
			log.debug(String.format("getMultiplePageProperties: resourceResolver[%s] pathList[%s]", resourceResolver, pathList));
		}
		
		List<Map<String, Object>> pagePropertyMapList = new ArrayList<Map<String, Object>>();
		
		for(String path : pathList){
			if(StringUtils.isNotBlank(path)){
				Map<String, Object> propertyMap = getPageProperties(resourceResolver, path);
				if(propertyMap != null){
					pagePropertyMapList.add(propertyMap);
				}
			}
		}
		
		if(log.isDebugEnabled()){
			log.debug(String.format("getMultiplePageProperties: returning pagePropertyMapList with size[%s]", pagePropertyMapList.size()));
		}
		
		return pagePropertyMapList;
	}
	
	
	
	@Override
	public Map<String, Object> getPageProperties(ResourceResolver resourceResolver, String path) {
		
		if(log.isDebugEnabled()){
			log.debug(String.format("getPageProperties: resourceResolver[%s] path[%s]", resourceResolver, path));
		}
		
		Map<String, Object> propertyMap = null;
		
		try {
			if(StringUtils.isNotBlank(path)){
				String resourcePath = path+PROPERTY_NODE_PATH;
				Resource pagePropertyResource = resourceResolver.getResource(resourcePath);

				if(pagePropertyResource != null){
					Node pagePropertyNode = pagePropertyResource.adaptTo(Node.class);
					JsonJcrNode pagePropertyJsonJcrNode = new JsonJcrNode(pagePropertyNode);
					String pageTitle = pagePropertyJsonJcrNode.optString(PROPERTY_KEY_JCR_TITLE);
					NodeUtils.removeAppGeneratedPropertiesFromJsonJcrNode(pagePropertyJsonJcrNode);

					propertyMap = new HashMap<String, Object>();
					

					Iterator<String> jsonKeyIterator = pagePropertyJsonJcrNode.keys();
					while(jsonKeyIterator.hasNext()){
						String key = jsonKeyIterator.next();
						propertyMap.put(key, pagePropertyJsonJcrNode.optString(key));
					}
					
					addCustomPageProperties(propertyMap, resourceResolver, path,
							pagePropertyNode, pagePropertyJsonJcrNode, pageTitle);
					
				}else{
					log.warn("getPageProperties: did not found resource: " + resourcePath);
				}
			}
		} catch (JSONException e) {
			log.error("getPageProperties: encountered an Exception", e);
		} catch (RepositoryException e) {
			log.error("getPageProperties: encountered an RepositoryException", e);
		} catch (Exception e) {
			log.error("getPageProperties: encountered an Exception", e);
		}
		
		if(log.isDebugEnabled()){
			log.debug(String.format("getPageProperties: returning propertyMap[%s]", propertyMap));
		}
		
		return propertyMap;
	}
	
	private void addCustomPageProperties(Map<String, Object> propertyMap,
			ResourceResolver resourceResolver, String path, Node pagePropertyNode,
			JsonJcrNode pagePropertyJsonJcrNode, String pageTitle)
					throws PathNotFoundException, RepositoryException, Exception{
		
		propertyMap.put(KEY_PAGE_TITLE, pageTitle);
		propertyMap.put(KEY_URI, path);
		
//		if(pagePropertyNode.hasNode("resourcelist")){
//			List<ResourceEntry> pageResourceList = new ArrayList<ResourceEntry>();
//			
//			Node resourceListComponentNode = pagePropertyNode.getNode("resourcelist");
//			if(resourceListComponentNode.hasNode("resourcelist")){
//				Node resourceListParentNode = resourceListComponentNode.getNode("resourcelist");
//				NodeIterator resourceNodeIterator = resourceListParentNode.getNodes();
//				
//				while(resourceNodeIterator.hasNext()){
//					Node resourceNode = resourceNodeIterator.nextNode();
//					ResourceEntry resourceListBean = new ResourceEntry();
//					NodeUtils.parsePropertyIntoBean(resourceListBean, resourceNode, "title", "docSrc");
//					if(StringUtils.isNotBlank(resourceListBean.getTitle()) || StringUtils.isNotBlank(resourceListBean.getDocSrc())){
//						pageResourceList.add(resourceListBean);
//					}
//				}
//			}
//			
//			propertyMap.put(KEY_RESOURCE_LIST, pageResourceList);
//		}
//		
//		addComparableProperies(propertyMap, pagePropertyNode);
//		
//		Resource pageResource = resourceResolver.getResource(path);
//		Page pageFromPath = null;
//		if(pageResource != null){
//			pageFromPath = pageResource.adaptTo(Page.class);
//		}else{
//			if(log.isDebugEnabled()){
//				log.debug("getPageProperties: no Resource for path:" + path);
//			}
//		}
//		
//		if(pageFromPath != null){
//			String pageTemplateStr = getPageTemplate(pageFromPath);
//			
//			boolean pageMightUnderProductCategory = TEMPLATE_MASTERTEMPLATE4.equalsIgnoreCase(pageTemplateStr) ||
//					TEMPLATE_MASTERTEMPLATE3A.equalsIgnoreCase(pageTemplateStr) ||
//					TEMPLATE_MASTERTEMPLATE3A_APS.equalsIgnoreCase(pageTemplateStr) || TEMPLATE_MASTERTEMPLATE4_FLEXIBLE.equalsIgnoreCase(pageTemplateStr) ||
//					TEMPLATE_MASTERTEMPLATE3A_FLEXIBLE.equalsIgnoreCase(pageTemplateStr) ||
//					TEMPLATE_MASTERTEMPLATE3A_APS_FLEXIBLE.equalsIgnoreCase(pageTemplateStr);
//			
//			if(pageMightUnderProductCategory){
//				Page categoryParentPage = getCategoryPage(pageFromPath, 1);
//				
//				if(categoryParentPage != null){
//					Node parentPageNode = categoryParentPage.getContentResource().adaptTo(Node.class);
//					
//					if(parentPageNode.hasProperty(KEY_CQ_TAGS)){
//						addCategoryName(propertyMap, parentPageNode);
//						propertyMap.put(KEY_CATEGORY_PAGE_URI, categoryParentPage.getPath());
//						addComparableProperies(propertyMap, parentPageNode);
//					}
//				}else if((TEMPLATE_MASTERTEMPLATE3A.equalsIgnoreCase(pageTemplateStr) || TEMPLATE_MASTERTEMPLATE3A_FLEXIBLE.equalsIgnoreCase(pageTemplateStr)) &&
//						pagePropertyNode.hasProperty(KEY_CQ_TAGS)){
//					addCategoryName(propertyMap, pagePropertyNode);
//				}
//			}else if((TEMPLATE_MASTERTEMPLATE2A.equalsIgnoreCase(pageTemplateStr) || TEMPLATE_MASTERTEMPLATE2A_FLEXIBLE.equalsIgnoreCase(pageTemplateStr) ) &&
//					pagePropertyNode.hasProperty(KEY_CQ_TAGS)){
//				addCategoryName(propertyMap, pagePropertyNode);
//			}
//			
//			String orderSampleCategory = pageFromPath.getParent().getProperties().get(KEY_ORDER_SAMPLE_CATEGORY, "");
//			if(StringUtils.isNotBlank(orderSampleCategory)){
//				propertyMap.put(KEY_ORDER_SAMPLE_CATEGORY, orderSampleCategory);
//			}
//		}else{
//			if(log.isDebugEnabled()){
//				log.debug("getPageProperties: Resource adapt to Page.class resulted to null for the path:" + path);
//			}
//		}
	}
	
	private void addCategoryName(Map<String, Object> propertyMap, Node pageNode)
			throws ValueFormatException, IllegalStateException, RepositoryException{
		
		Property categoryNameProp = pageNode.getProperty(KEY_CQ_TAGS);
		String categoryNameStr = null;
		if(categoryNameProp.isMultiple()){
			for(Value val : categoryNameProp.getValues()){
				categoryNameStr = val.getString();
				break;
			}
		}else{
			categoryNameStr = categoryNameProp.getString();
		}
		
		if(StringUtils.isNotBlank(categoryNameStr)){
			categoryNameStr = categoryNameStr.replace(TAG_PRODUCT_CATEGORY_KEYWORD, "");
			propertyMap.put(KEY_CATEGORY_NAME, categoryNameStr);
		}
	}
	
//	private void addComparableProperies(Map<String, Object> propertyMap, Node pagePropertyNode)
//			throws PathNotFoundException, RepositoryException, Exception{
//		
//		if(pagePropertyNode != null && pagePropertyNode.hasNode("comparableProperties")){
//			List<ComparableProperty> comparablePropList = new ArrayList<ComparableProperty>();
//			
//			Node comparablePropertiesNode = pagePropertyNode.getNode("comparableProperties");
//			NodeIterator comparablePropNodeIterator = comparablePropertiesNode.getNodes();
//			while(comparablePropNodeIterator.hasNext()){
//				Node comparablePropertyNode = comparablePropNodeIterator.nextNode();
//				ComparableProperty comparableProp = new ComparableProperty();
//				NodeUtils.parsePropertyIntoBean(comparableProp, comparablePropertyNode,
//						"propertyKey", "propertyLabel");
//				
//				comparablePropList.add(comparableProp);
//			}
//			
//			propertyMap.put(KEY_COMPARABLE_PROP_LIST, comparablePropList);
//		}
//	}
	
	private String getPageTemplate(Page page){
		String pageTemplateStr = page.getProperties().get("cq:template", String.class);
		
		String[] teplatePathElements = pageTemplateStr.split("/");
		if(teplatePathElements != null && teplatePathElements.length > 0){
			pageTemplateStr = teplatePathElements[teplatePathElements.length - 1];
		}
		
		return pageTemplateStr;
	}
	
	private Page getCategoryPage(Page page, int parentLevel){
		try{
			
			if((getPageTemplate(page).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE2A) ||
					getPageTemplate(page).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE3A) ||
					getPageTemplate(page).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE3A_FLEXIBLE)||
					getPageTemplate(page).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE2A_FLEXIBLE)) &&
					StringUtils.isNotBlank(page.getProperties().get(KEY_CQ_TAGS, String.class))){
				return page;
			
			}else{
				Page parentPage = page.getParent(parentLevel);
				if(parentLevel >= 5){ //recursion limit
					return null;
				}else if(parentPage == null){
					return null;
				}else if((getPageTemplate(parentPage).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE2A) ||
						getPageTemplate(parentPage).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE3A)||
						getPageTemplate(parentPage).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE2A_FLEXIBLE)||
						getPageTemplate(parentPage).equalsIgnoreCase(TEMPLATE_MASTERTEMPLATE3A_FLEXIBLE)) &&
						StringUtils.isNotBlank(parentPage.getProperties().get(KEY_CQ_TAGS, String.class))){
						
					return parentPage;
				}else{
					return getCategoryPage(parentPage, parentLevel++);
				}
				
			}
			
			
		}catch(Exception e){
			log.error("getCategoryPage: encountered exception", e);
			return null;
		}
	}

}