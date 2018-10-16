package com.apddemo.core.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamUtils {
	
	private final static Logger LOG = LoggerFactory.getLogger(DamUtils.class);
	
	public static String saveBase64Image(ResourceResolver resourceResolver, String imageData, String fileName, javax.jcr.Session session, String damLocation) throws Exception {
		try {
			           
			// check if file already exist in the target node
			if(resourceResolver.getResource(damLocation + "/" + fileName)  != null ){
				throw new Exception("File already exist in DAM location " + damLocation);
			}
			
			InputStream is = convertBase64ToImage(imageData);
			
			javax.jcr.Node node = session.getNode(damLocation);
			javax.jcr.ValueFactory valueFactory = session.getValueFactory();
			javax.jcr.Binary contentValue = valueFactory.createBinary(is);
						
			// File Node
			javax.jcr.Node fileNode = node.addNode(fileName, "dam:Asset");
			fileNode.addMixin("mix:referenceable");
			fileNode.addMixin("mix:versionable");
						
			// Content node
			javax.jcr.Node contentNode = fileNode.addNode("jcr:content", "dam:AssetContent");
			Calendar lastModified = Calendar.getInstance();
			lastModified.setTimeInMillis(lastModified.getTimeInMillis());
			contentNode.setProperty("jcr:lastModified", lastModified);
			
			// metadata node
			javax.jcr.Node metaDataNode = contentNode.addNode("metadata",
					"nt:unstructured");
			metaDataNode.addMixin("cq:Taggable");            
			metaDataNode.setProperty("jcr:mimeType", getImageType(imageData));
			metaDataNode.setProperty("dam:Fileformat", getImageType(imageData).split("/")[1].toUpperCase());

            // renditions node
			javax.jcr.Node renditionsNode = contentNode.addNode("renditions", "nt:folder");
			// renditions original image node
			javax.jcr.Node originalFileNode = renditionsNode.addNode("original", "nt:file");
			javax.jcr.Node originalFileContentNode = originalFileNode.addNode("jcr:content", "nt:resource");
			originalFileContentNode.setProperty("jcr:mimeType", getImageType(imageData));
			originalFileContentNode.setProperty("jcr:data", contentValue);
			
			session.save();
			

			// Return the path to the document that was stored in CQ.                      
			return fileNode.getPath();
			
		} catch (Exception ex) {
			LOG.error("saveBase64Image " + ex);
			
			throw ex;
		}

    }
	
	private static InputStream convertBase64ToImage(String sourceData) throws Exception{
		// tokenize the data
		String parts[] = sourceData.split(",");
		String imageString = parts[1];

		byte[] imageByte = Base64.getDecoder().decode(imageString);		
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

		return bis;
	}
	
	private static String getImageType(String base64SourceData){
		// tokenize the data
		
		String parts[] = base64SourceData.split(",");
		return parts[0].split(";")[0].split(":")[1];
	}
}
