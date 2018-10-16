package com.apddemo.core.daos.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.UserInfo;
import com.apddemo.core.models.QueryDataResult;
import com.apddemo.core.models.UrlRedirection;
import com.apddemo.core.utils.StringUtils;
import com.apddemo.core.daos.BaseDao;
import com.apddemo.core.daos.OrderSampleDao;

@Component(immediate = true, service = UserInfoDao.class
, configurationPid = "com.apddemo.core.daos.impl.UserInfoDaoImpl")
public class UserInfoDaoImpl implements UserInfoDao {
	
	@Reference
	BaseDao baseDao;
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat submitTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static String INSERT_DATA_STATEMENT = "INSERT INTO OrderSamplesForm (name,email,address,suburb,state,postcode,phone,segment,product,comments,informNews,timeAdded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
	
	@Override
	public boolean insertData(UserInfo data) {
		
		boolean success = baseDao.insertData(INSERT_DATA_STATEMENT,
//				data.getName(),
//				data.getEmail(),
//				data.getAddress(),
//				data.getSuburb(),
//				data.getState(),
//				data.getPostcode(),
//				data.getPhone(),
//				data.getSegment(),
//				data.getProduct(),
//				data.getComments(),
//				data.getInformNews(),
				new Date());
		
		return success;
	}

	@Override
	public UserInfo getUser(String sso_id) {
		
		log.info("UserInfo getUser : " + sso_id);
		String query = "SELECT * FROM SSO_User WHERE user_sso_id = ?";
		UserInfo userInfo = new UserInfo();
		
		ResultSet resultSet = null;
		QueryDataResult queryDataResult = null;
		
		try{
			queryDataResult = baseDao.queryData(query, sso_id);
			resultSet = queryDataResult != null ? queryDataResult.getResultSet() : null;
//			log.info("UserInfo resultthen Set 1 : " + resultSet.getString("user_id"));
			if (resultSet != null && resultSet.next()){
				log.info("UserInfo resultSet 2 : " + resultSet.getString("user_id"));
//				UserInfo pojoList = (UserInfo) resultSetMapper.mapRersultSetToObject(resultSet, UserInfo.class);
//				log.info("UserInfo getUser pojoList : " + pojoList.toString());

			}else{
				if(log.isDebugEnabled()){
					log.debug("getUrlRedirectionForOldUrl: resultSet:" + resultSet);
				}
			}
			
		}catch(Exception ex){
			log.info("UserInfoDao : " + ex.getMessage());
			log.error("UserInfoDao : " + ex.getMessage());
		}finally {
			baseDao.closeResultSet(resultSet);
			baseDao.closeQueryDataResult(queryDataResult);
		}
		
		
		return userInfo;
	}

	@Override
	public String getUserId(UserInfo userInfo) {
		
		String userid = "";
		log.info("UserInfo getUser : " + userInfo.getUser_sso_id());
		String selectQuery = "SELECT * FROM SSO_User WHERE user_sso_id = ?";
		
		
		List<Map<String, Object>> retrieveUser = new ArrayList<Map<String,Object>>();
		
		try{
			retrieveUser = baseDao.retrieveData(selectQuery, userInfo.getUser_sso_id());
			
			if(retrieveUser.size() > 0){
				for(Map<String, Object> queryMap : retrieveUser){
					if(queryMap.get("user_id") != null && !((String) queryMap.get("user_id")).isEmpty()){
						userid = (String) queryMap.get("user_id");
					}
				}
			}
			log.info("getUserId userid: " + userid);
			if(userid.isEmpty()){
				int insertId;
				
				if(userInfo != null && StringUtils.isNotBlank(userInfo.getUser_sso_id()) ){
					
					insertId = baseDao.insertDataReturnId("INSERT INTO SSO_User (user_sso_id, display_name, user_first_name, user_last_name, email, created_date, last_login_date) VALUES (?,?,?,?,?,?,?)",
							userInfo.getUser_sso_id(),
							userInfo.getDisplay_name(),
							userInfo.getUser_first_name(),
							userInfo.getUser_last_name(),
							userInfo.getEmail(),
							submitTime.format(new Date()),
							submitTime.format(new Date()));
					
					userid = "SSO" + String.format("%08d", insertId);
				}
				
			}
			

		}catch(Exception ex){
			log.info("UserInfoDao : " + ex.getMessage());
			log.error("UserInfoDao : " + ex.getMessage());
		}
		
		
		return userid;
	}
	
	

}
