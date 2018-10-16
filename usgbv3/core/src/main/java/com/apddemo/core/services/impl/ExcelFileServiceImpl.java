package com.apddemo.core.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.apddemo.core.services.ExcelFileService;
import com.apddemo.core.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Iterator;

@Component(immediate = true, service = ExcelFileService.class
		, configurationPid = "com.apddemo.core.services.impl.ExcelFileServiceImpl")
public class ExcelFileServiceImpl implements ExcelFileService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@Override
	public boolean parseExcelToNodes(String excelPath, String targetPath,
			String parentName, String childName, ResourceResolver resourceResolver) {
		return parseExcelToNodes(excelPath, targetPath, parentName, childName, resourceResolver, false);
	}
	
	@Override
	public boolean parseExcelToNodes(String excelPath, String targetPath,
			String parentName, String childName, ResourceResolver resourceResolver, boolean dontLogoutSession) {
		
		if(log.isDebugEnabled()){
			log.debug(String.format("parseExcelToNodes: excelPath[%s] targetPath[%s] parentName[%s] childName[%s] resourceResolver[%s] ",
					excelPath, targetPath, parentName, childName, resourceResolver));
		}
		
		boolean success = true;
		
		if(StringUtils.isNotBlank(excelPath) && StringUtils.isNotBlank(targetPath) && 
				StringUtils.isNotBlank(parentName) && StringUtils.isNotBlank(childName)
				&& resourceResolver != null){

			try {
				Resource excelResource = resourceResolver.getResource(excelPath);
				if(log.isDebugEnabled()){
					log.debug(String.format("parseExcelToNodes: excelResource[%s]", excelResource));
				}
				
				Rendition excelRendition = excelResource.adaptTo(Asset.class).getOriginal();
				
				Workbook workbook = null;
				if (excelRendition != null) {
					workbook = WorkbookFactory.create(excelRendition.getStream());
				}else{
					if(log.isDebugEnabled()){
						log.debug("parseExcelToNodes: excelRendition is NULL ");
					}
				}
				
				if(workbook != null){
					if(log.isDebugEnabled()){
						log.debug("parseExcelToNodes: starting workbook read and parse... ");
					}
					Sheet sheet = workbook.getSheetAt(0);
					int propCount = sheet.getRow(0).getLastCellNum();
					String[] headingprops = new String[propCount];
					
					int loopCounter = 0;
					for (Iterator<Cell> headerCellIterator = sheet.getRow(0).cellIterator(); headerCellIterator.hasNext(); loopCounter++) {
						Cell cell = headerCellIterator.next();
						headingprops[loopCounter] = cell.getStringCellValue().trim();
					}

					Session session = resourceResolver.adaptTo(Session.class);
					Node root = session.getRootNode();
					//Get the content node in the JCR
					Node targetNode = root.getNode(targetPath.substring(1));
					
					if(targetNode.hasNode(parentName)){
						if(log.isDebugEnabled()){
							log.debug(String.format("parseExcelToNodes: parentNode[%s] found. Removing node...", parentName));
						}
						targetNode.getNode(parentName).remove();
						if(log.isDebugEnabled()){
							log.debug("parseExcelToNodes: remove parentNode success");
						}
					}
					
					Node parentNode = targetNode.addNode(parentName, "nt:unstructured");
					parentNode.setProperty(PROPERTY_GENERATED_KEY, PROPERTY_GENERATED_VALUE);
					//log.info("headingprops " + headingprops.length + " --> " + headingprops.toString());
					loopCounter = 0;
					for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); loopCounter++) {
						if(loopCounter == 0){ //skip header row
							rowIterator.next();
							continue;
						}
						
						Row row = rowIterator.next();
						Node childNode = parentNode.addNode(childName + "_" + loopCounter, "nt:unstructured");
						
						int cellCounter = 0;
						for (Iterator<Cell> dataCellIterator = row.cellIterator(); dataCellIterator.hasNext(); cellCounter++) {
							
							if(cellCounter < headingprops.length ){
								Cell dataCell = dataCellIterator.next();
								String cellVal = "";
								if(dataCell.getCellType() == Cell.CELL_TYPE_STRING || dataCell.getCellType() == Cell.CELL_TYPE_FORMULA){
									cellVal = dataCell.getStringCellValue();
								}else if(dataCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
									cellVal = dataCell.getNumericCellValue()+"";
								}
								//log.info("cellCounter : " + cellCounter + " --> " + cellVal);
								childNode.setProperty(headingprops[cellCounter], cellVal);
							}else{
								break;
							}
						}
					}
					
					session.save();
					if(log.isDebugEnabled()){
						log.debug("parseExcelToNodes: parse and save nodes success");
					}
					if(!dontLogoutSession){
						if(log.isDebugEnabled()){
							log.debug("parseExcelToNodes: logging out session");
						}
						session.logout();
					}
					
				}else{
					if(log.isDebugEnabled()){
						log.debug("parseExcelToNodes: could not create workbook from excelRendition stream");
					}
					success = false;
				}
				
				
			} catch (Throwable e) {
				log.error("parseExcelToNodes: encountered an Throwable", e);
				success = false;
			}
		}else{
			success = false;
		}
		return success;
	}

}