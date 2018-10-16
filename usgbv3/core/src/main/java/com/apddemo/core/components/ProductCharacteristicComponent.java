package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.apddemo.core.models.ProductModel;


public class ProductCharacteristicComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(ProductCharacteristicComponent.class);
	
	private List<ProductModel> productList;
	private String error;

	public List<ProductModel> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductModel> productList) {
		this.productList = productList;
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
	    productList = new ArrayList<ProductModel>();
	    
	    while (ni.hasNext()) {
	        Node child = (Node)ni.nextNode(); 
	         
	        
	        NodeIterator ni2 =  child.getNodes() ; 
	        while (ni2.hasNext()) {
		        Node child2 = (Node)ni2.nextNode(); 
		        
		        productList.add(setProductInfo(child2));
	        }
	    }
	    
	}
	
	public ProductModel setProductInfo(Node rawNode) {
		
		ProductModel product = new ProductModel();
		
		try {
			
			if(rawNode.hasProperty("title")){
				
				product.setTitle(rawNode.getProperty("title").getString());
			}
			
			if(rawNode.hasProperty("description")){
				
				product.setDescription(rawNode.getProperty("description").getString());
			}
			
			if(rawNode.hasProperty("icon")){
				
				product.setIcon(rawNode.getProperty("icon").getString());
			}
			
			if(rawNode.hasProperty("linkType")) {
				
				String linkType = rawNode.getProperty("linkType").getString();
				product.setLinkType(linkType);
				
				if("asset".equals(linkType) && rawNode.hasProperty("imgPath")) {
					
					product.setLink(rawNode.getProperty("imgPath").getString());
					
				}else if("page".equals(linkType) && rawNode.hasProperty("linkPath")) {

					product.setLink(rawNode.getProperty("linkPath").getString() + ".html");
					
				}else if("externalURL".equals(linkType) && rawNode.hasProperty("externalUrl")) {
					String external = rawNode.getProperty("externalUrl").getString();
					if(!external.startsWith("http")) {
						external = "http://" + external;
					}
					product.setLink(external);
					
				}else {

					product.setLink("");
				}
				
			}
			
		}catch (Exception e) {
			error = e.getMessage();
		}
		
		
		
		return product;
		
	}
    

}
