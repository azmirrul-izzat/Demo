package com.apddemo.core.utils;

import com.apddemo.core.services.ExcelFileService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.jcr.JsonJcrNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(NodeUtils.class);
	
	private static final String PROPERTY_NAME_DELIMETER = "\\."; //need to escape the dot(.)
	public static final String JCR_PROPERTY_PREFIX = "jcr:";
	public static final String CQ_PROPERTY_PREFIX = "cq:";
	public static final String SLING_PROPERTY_PREFIX = "sling:";

	
	/**
	 * Parses the node property into a bean using Java Reflection.
	 * Property name should be in the format of EL (expression language)
	 * 
	 * eg.
	 * ExampleBean1
	 *   contains String nameProperty
	 *   and ExampleBean2 propertyBean2
	 * And this is ExampleBean2 structure
	 * ExampleBean2
	 *   contains String titleProperty
	 * 
	 * to set ExampleBean1's nameProperty value, the node property name should be
	 * "p.nameProperty"
	 * 
	 * to set ExampleBean1's propertyBean2's titleProperty value, the node property name should be
	 * "propertyBean2.titleProperty"
	 * 
	 * if propertyBean2 is null it will automatically create new instance of it
	 *  using default constructor (eg. new ExampleBean2())
	 *  
	 *  NOTE: THIS ONLY WORKS FOR STRING TYPE PROPERTIES
	 * 
	 * @param objectInstance an instance of the bean
	 * @param node the node where the property would be read
	 * @param propertyNames node property names that should also follow the guideline
	 * @throws Exception for any exception encountered in the process
	 */
	public static void parsePropertyIntoBean(Object objectInstance,
			Node node, String... propertyNames) throws Exception{
		
		if(objectInstance != null && propertyNames != null && propertyNames.length > 0){
			for(String propName : propertyNames){
				
				if(node.hasProperty(propName) && propName != null &&
						!propName.isEmpty()){
					
					String[] properties = propName.split(PROPERTY_NAME_DELIMETER);
					Object objInstance = objectInstance;
					Class<?> instanceClass = objectInstance.getClass();
					
					for(int i=0; i < properties.length; i++){
						
						//checks if property is another object bean if it is not yet the last one on the properties array
						if((i+1) < properties.length){
							Method getterMethod = new PropertyDescriptor(properties[i], instanceClass).getReadMethod();
							Object propObject = getterMethod.invoke(objInstance);
							
							//if the property is an object and it is null then instantiate new of that using default constructor
							if(propObject == null){
								Method setterMethod = new PropertyDescriptor(properties[i], instanceClass).getWriteMethod();
								instanceClass = getterMethod.getReturnType();
								Object newInstance = instanceClass.getConstructor().newInstance();
								setterMethod.invoke(objInstance, newInstance);
								objInstance = newInstance;
							}else{
								objInstance = propObject;
								instanceClass = getterMethod.getReturnType();
							}
							
						}else{
							Method setterMethod = new PropertyDescriptor(properties[i], instanceClass).getWriteMethod();
							Property prop = node.getProperty(propName);
							if(prop.isMultiple()){
								Value[] values = prop.getValues();
								
								String[] valueStrArray = new String[values.length];
								for(int arrIndex=0; arrIndex < valueStrArray.length; arrIndex++){
									valueStrArray[arrIndex] = values[arrIndex].getString();
								}
								
								setterMethod.invoke(objInstance, new Object[] {valueStrArray});
							}else{
								setterMethod.invoke(objInstance, prop.getValue().getString());
							}
							
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * Removes all jcr, cq, and sling properties in a JsonJcrNode.
	 * If a property key starts with "jcr:" then it is considered JCR property.
	 * If a property key starts with "cq:" then it is considered CQ property.
	 * If a property key starts with "sling:" then it is considered Sling property.
	 * Useful when you don't want to expose application generated properties in your JSON
	 * 
	 * @param jsonJcrNode
	 */
	public static void removeAppGeneratedPropertiesFromJsonJcrNode(JsonJcrNode jsonJcrNode){
		if(jsonJcrNode != null){
			List<String> keyList = new ArrayList<String>();
			
			Iterator<String> keyIterator = jsonJcrNode.keys();
			while(keyIterator.hasNext()){
				String key = keyIterator.next();
				if(key != null &&
						(key.startsWith(JCR_PROPERTY_PREFIX) ||
								key.startsWith(CQ_PROPERTY_PREFIX) ||
								key.startsWith(SLING_PROPERTY_PREFIX))){
					keyList.add(key);
				}
			}
			
			for(String key : keyList){
				jsonJcrNode.remove(key);
			}
		}
	}

	/**
	 * Gets the node iterator for all child nodes of a given node path.
	 * This method also checks if the parent node is generated via exceltonode service
	 *
	 * @param nodePath the node path
	 * @param resourceResolver
	 * @return
	 * @throws ValueFormatException
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 */
	public static NodeIterator getAndvalidateParentNodeAndGetChildNodeIterator(String nodePath, ResourceResolver resourceResolver)
			throws ValueFormatException, PathNotFoundException, RepositoryException {

		NodeIterator childNodeIterator = null;

		// /content/demo/contacts
		if(log.isDebugEnabled()){
			log.debug(String.format("getAndvalidateParentNodeAndGetChildNodeIterator: nodePath[%s]", nodePath));
		}
		if(StringUtils.isNotBlank(nodePath)){

			Resource contactResource = resourceResolver.getResource(nodePath);
			if(contactResource != null){
				Node contactsNode = contactResource.adaptTo(Node.class);

				//checks node if generated by exceltonode service to avoid access to different nodes
				if(contactsNode.hasProperty(ExcelFileService.PROPERTY_GENERATED_KEY) &&
						ExcelFileService.PROPERTY_GENERATED_VALUE.equals(
								contactsNode.getProperty(ExcelFileService.PROPERTY_GENERATED_KEY).getString())){
					childNodeIterator = contactsNode.getNodes();
				}else{
					if(log.isDebugEnabled()){
						log.debug("getAndvalidateParentNodeAndGetChildNodeIterator: parent node might not be exceltonode generated");
					}
				}
			}else{
				if(log.isDebugEnabled()){
					log.debug("getAndvalidateParentNodeAndGetChildNodeIterator: contactResource based on nPath, not found");
				}
			}
		}

		return childNodeIterator;
	}
}
