package com.apddemo.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.apddemo.core.models.CountryLanguageModel;
import com.apddemo.core.models.CountryModel;
import com.apddemo.core.services.ExcelFileService;
import com.apddemo.core.utils.CountryUtils;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.List;



@Component(immediate = true, service = WorkflowProcess.class
		, configurationPid = "com.apddemo.core.workflow.ExcelContentUploader", name = "Converts Excel content to JCR "
		, property = {
		"process.label=USGB Excel content to JCR - Workflow Process"
})
public class ExcelContentUploader implements WorkflowProcess {

	@Reference
	ExcelFileService excelfileService;

	@Reference
	Replicator replicator;

	@Reference
	ResourceResolverFactory resourceResolverFactory;
	//protected final static String ROOT_PATH = "/content/demo/";

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(WorkItem workItem, WorkflowSession wfSession,
                        MetaDataMap args) throws WorkflowException {

		LOG.info("In Execute ExcelContentUploader...##########################");

		Session session = null;
		ResourceResolver resourceResolver = null;
		List<CountryModel> demoCountryList = null;
		
		String excelPath = "";
		String targetPath = "";
		String parentName = "";
		String childName = "";
		String payloadPath = "";

		// getting the pay load - excel path
		if(workItem != null && workItem.getWorkflowData() != null &&
				workItem.getWorkflowData().getPayload() != null){
			payloadPath = workItem.getWorkflowData().getPayload().toString();
			excelPath = payloadPath;
		}else{
			LOG.warn("execute: some are null in the workItem/workflow data");
		}		
		LOG.info("payloadPath : " + payloadPath);
		if(StringUtils.isNotBlank(payloadPath)){
			
			String[] payloadPathElements = payloadPath.split("/");
			String fileName = payloadPathElements.length > 0 ?
					payloadPathElements[payloadPathElements.length -1].toLowerCase() : "";
//			LOG.info("fileName 0/-1 : " + fileName);
//			LOG.info("payloadPathElements.length : " + payloadPathElements.length);
			if(payloadPathElements.length >= 3 && StringUtils.isNotBlank(fileName) && fileName.endsWith(".xlsx")){
				

				try {
					resourceResolver = getResourceResolver(wfSession.adaptTo(Session.class));
					LOG.info("resourceResolver:"+resourceResolver);
					// get the country list
					demoCountryList = CountryUtils.getCountryModelList(resourceResolver);
					LOG.info("demoCountryList:"+demoCountryList);
					
				} catch (Exception e) {
					LOG.info("ERROR resourceResolver : " + e.getMessage());
					//e.getStackTrace();
				}
				

				if((payloadPath.toLowerCase()).contains("excelcontent") && demoCountryList != null){
					List<CountryLanguageModel> languageList = null;
					for(CountryModel countryModel : demoCountryList){
						LOG.info("file:"+fileName.toLowerCase() +" dam path :"+countryModel.getDamPath().toLowerCase());
						if((excelPath.toLowerCase()).indexOf(countryModel.getDamPath().toLowerCase()) != -1){
							LOG.info("true");
							languageList = countryModel.getLanguageList();
							if(languageList != null && languageList.size()>0){
								for(CountryLanguageModel countryLanguageModel: languageList){
									targetPath = countryLanguageModel.getLanguagePath();
									break;
								}
							}

						}
						if(targetPath.length()>0){

							break;
						}
					}
					LOG.info("targetPath : " + targetPath);
					if (fileName.contains("store")) {
						parentName = "storelocations";
						childName = "location";
					} else if (fileName.contains("contact")) {
						parentName = "contacts";
						childName = "contact";
					} else if (fileName.contains("state")) {
						parentName = "states";
						childName = "state";
					} else if (fileName.contains("occupation")) {
						parentName = "occupations";
						childName = "occupation";
					} else if (fileName.contains("workingwith")) {
						parentName = "workingwiths";
						childName = "workingwith";
					} else if (fileName.contains("proximity")) {
						parentName = "proximitylocations";
						childName = "proximity";
					} else if (fileName.contains("country")) {
						parentName = "countries";
						childName = "country";
					} else if(fileName.contains("hrform")){
						parentName = "hrlocations";
						childName = "hr";
					}
					
					
					LOG.info("Excel Service..:excelPath..:"+excelPath +"targetPath..:"+targetPath +"parentName..:"+ parentName+"childName..:"+
							childName);
					boolean isSuccess = excelfileService.parseExcelToNodes(excelPath, targetPath, parentName,
							childName, resourceResolver, true);
					LOG.info("Excel Service..After parseExcelToNodes.:"+isSuccess);
					
					session = resourceResolver.adaptTo(Session.class);
					if(isSuccess){
						
						try {
							replicator.replicate(session, ReplicationActionType.ACTIVATE,
									targetPath + "/" + parentName);
							LOG.info("Excel Service..After Replication.:"+targetPath + "/" + parentName);
						} catch (ReplicationException e) {
							LOG.info("Exception in replication..:" + e);
						} finally {
							resourceResolver.close();
						}
					}else{
						resourceResolver.close();

					}
				}else{
					LOG.debug("execute: file's parent folder is not named excelContent");
				}
			}else{
				LOG.debug("execute: payload path elements does not match requirements or filetype is not .xlsx");
			}
		}else{
			LOG.debug("execute: payload path is blank or null");
		}
	}

	private ResourceResolver getResourceResolver(Session session)
			throws LoginException {
		return resourceResolverFactory.getResourceResolver(Collections
				.<String, Object> singletonMap(
						JcrResourceConstants.AUTHENTICATION_INFO_SESSION,
						session));
	}

}