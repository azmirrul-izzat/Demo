package com.usgbv3.core.daos.impl;


import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.usgbv3.core.daos.DocumentCollatorDao;
import com.usgbv3.core.entity.DocumentCollator;
import com.usgbv3.core.models.QueryDataResult;
import com.usgbv3.core.daos.ContactUsDao;

@Component(immediate = true, service = DocumentCollatorDao.class
, configurationPid = "com.usgbv3.core.daos.impl.DocumentCollatorDaoImpl")
public class DocumentCollatorDaoImpl extends BaseDaoImpl implements DocumentCollatorDao {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat submitTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public int insertData(DocumentCollator data) {

		log.info("data : " + data.toString());
		String query = "INSERT INTO DC_Document (doc_aem_id,doc_name,doc_path,doc_url,created_date,user_id) VALUES (?,?,?,?,?,?);";
		int insertedId = insertDataReturnId(query,
				data.getDoc_aem_id(),
				data.getDoc_name(),
				data.getDoc_path(),
				data.getDoc_url(),
				submitTime.format(new Date()),
				data.getUser_id());
		
		return insertedId;
	}

	@Override
	public boolean removeData(String userId, List<String> removeList) {
		
//		log.info("removeData getUser : " + userId);
		String query = "DELETE FROM DC_Document WHERE user_id = ? AND doc_id = ?";
		boolean success = false;
		for(String removeId : removeList){
			
			success = removeUpdateData(query,
					userId,
					removeId);
			
			if(!success){
				break;
			}
		}
		
		
		
		return success;
	}

	@Override
	public List<DocumentCollator> getDocumentListbySsoId(String sso_id) {
		
//		log.info("getDocumentListbySsoId : " + sso_id);
		String query = "SELECT dc.* FROM DC_Document dc, SSO_User usr WHERE dc.user_id = usr.user_id AND usr.user_sso_id = ?";
		List<DocumentCollator> dcList = new ArrayList<DocumentCollator>();
		
//		ResultSetMapper resultSetMapper = new ResultSetMapper();
//		ResultSet resultSet = null;
//		QueryDataResult queryDataResult = null;
		
		try{
			List<Map<String, Object>> newQuery = retrieveData(query, sso_id);
			
			for(Map<String, Object> queryMap : newQuery){

				DocumentCollator documentCollator = new DocumentCollator();

				documentCollator.setDoc_id((String) queryMap.get("doc_id"));
				documentCollator.setDoc_aem_id((String) queryMap.get("doc_aem_id"));
	            documentCollator.setDoc_name((String) queryMap.get("doc_name"));
	            documentCollator.setDoc_url((String) queryMap.get("doc_url"));
	            documentCollator.setDoc_path((String) queryMap.get("doc_path"));
	            documentCollator.setUser_id((String) queryMap.get("user_id"));
	            documentCollator.setCreated_date((Date) queryMap.get("created_date"));
	            dcList.add(documentCollator);
				
			}
//			queryDataResult = queryData(query, sso_id);
//			resultSet = queryDataResult != null ? queryDataResult.getResultSet() : null;
//			
//			log.info("resultSet Size : " + resultSet.getFetchSize());
//			if (resultSet != null && resultSet.next()){
//				DocumentCollator documentCollator = new DocumentCollator();
//				log.info("UserInfo resultSet 2 : " + resultSet.getString("doc_id"));
//
//				documentCollator.setDoc_id(resultSet.getString("doc_id"));
//				documentCollator.setDoc_aem_id(resultSet.getString("doc_aem_id"));
//	            documentCollator.setDoc_name(resultSet.getString("doc_name"));
//	            documentCollator.setDoc_url(resultSet.getString("doc_path"));
//	            documentCollator.setDoc_path(resultSet.getString("doc_url"));
//	            documentCollator.setUser_id(resultSet.getString("user_id"));
//	            documentCollator.setCreated_date(resultSet.getDate("created_date"));
//	            dcList.add(documentCollator);
//			}
			
//			log.info("DocumentCollator resultSet : " + resultSet.getNString("user_id"));
//			dcList = resultSetMapper.mapRersultSetToObject(resultSet, DocumentCollator.class);
//			//userInfo = resultSet;
//			log.info("DocumentCollatorDaoImpl getDocumentListbySsoId pojoList : " + dcList.toString());
		}catch(Exception ex){
			log.error("DocumentCollatorDaoImpl : " + ex.getMessage());
		}
		
		
		return dcList;
	}
	

}