package com.apddemo.core.daos.impl;

import com.apddemo.core.daos.BaseDao;
import com.apddemo.core.daos.ContactUsDao;
import com.apddemo.core.models.ContactUsForm;
import com.apddemo.core.models.QueryDataResult;
import com.apddemo.core.models.ShareData;
import com.apddemo.core.utils.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component(immediate = true, service = ContactUsDao.class
		, configurationPid = "com.apddemo.core.daos.impl.ContactUsDaoImpl")
public class ContactUsDaoImpl implements ContactUsDao {
	
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Reference
	BaseDao baseDao;

	private final static String INSERT_DATA_STATEMENT = "INSERT INTO ContactUs (name, email, message , inquiryType " +
			",  informNews,  status ,  inquiryTime, form1payload )" +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	private final static String UPDATE_SECOND_FORM ="UPDATE ContactUs SET  phone  = ?,  " +
			"occupation  = ?,  productInterest  = ?,  workWith  = ?,  status  = ?, form2payload = ? WHERE  id  = ?";

	private final static String GET_CONTACT_US_FORM1 = "select * from ContactUs where status=0;";

	private final static String UPDATE_SCHEDULER_STATUS = "UPDATE ContactUs SET  status  = ? WHERE  id  = ?";

	private SimpleDateFormat submitTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String insertForm1Data(ContactUsForm contactUsForm) {

		if(contactUsForm != null && contactUsForm.getInquiryType() != null && contactUsForm.getName() != null
				&& contactUsForm.getEmail() != null && contactUsForm.getMessage() != null){
			LOG.info("baseDao:"+baseDao);
			int responseId = baseDao.insertDataReturnId(INSERT_DATA_STATEMENT,
					contactUsForm.getName(),
					contactUsForm.getEmail(),
					contactUsForm.getMessage(),
					contactUsForm.getInquiryType(),
					contactUsForm.getInformNews(),
					0,
					submitTime.format(new Date()),
					contactUsForm.getForm1Payload());
			LOG.info("responseId:"+responseId);
			return Integer.toString(responseId);
		}
		return null;
	}

	@Override
	public boolean insertForm2Data(ContactUsForm contactUsForm) {
		if(contactUsForm != null && contactUsForm.getId() != null && contactUsForm.getOccupation()!= null
				&& contactUsForm.getPhone() != null){
			LOG.info("baseDao:"+baseDao);
			boolean status = baseDao.removeUpdateData(UPDATE_SECOND_FORM,
					contactUsForm.getPhone(), contactUsForm.getOccupation(), contactUsForm.getProductInterest(),
					contactUsForm.getWorkWith(), contactUsForm.getStatus(), contactUsForm.getForm2Payload()
					, contactUsForm.getId());
			return status;
		}
		return false;
	}

	@Override
	public List<ContactUsForm> getContactUSForm1SubmitDetail() {
		List<ContactUsForm> contactUsFormList = new ArrayList<>();
		ContactUsForm contactUsForm = null;
		ResultSet resultSet = null;
		QueryDataResult queryDataResult = null;
		try {
			queryDataResult = baseDao.queryData(GET_CONTACT_US_FORM1, null);
			resultSet = queryDataResult != null ? queryDataResult.getResultSet() : null;
			if (resultSet != null){
				while(resultSet.next()){
					contactUsForm = new ContactUsForm();
					contactUsForm.setId(Integer.toString(resultSet.getInt("id")));
					//LOG.info("id is:"+Integer.toString(resultSet.getInt("id")));
					contactUsForm.setName(resultSet.getString("name"));
					contactUsForm.setEmail(resultSet.getString("email"));
					contactUsForm.setMessage(resultSet.getString("message"));
					contactUsForm.setInquiryType(resultSet.getString("inquiryType"));
					contactUsForm.setInformNews(Boolean.toString(resultSet.getBoolean("informNews")));
					//LOG.info("inform news is :"+Boolean.toString(resultSet.getBoolean("informNews")));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
					contactUsForm.setInquiryTime(sdf.format(resultSet.getTimestamp("inquiryTime")));
					contactUsFormList.add(contactUsForm);
				}
			}else{
				LOG.debug("getShareData: resultSet:" + resultSet);
			}
		} catch (SQLException e) {
			LOG.error("Exception e:"+e);
		}finally {
			baseDao.closeQueryDataResult(queryDataResult);
		}
		return contactUsFormList;
	}

	@Override
	public Boolean updateContactUSForm1SubmitDetails(List<ContactUsForm> contactUsFormList) {
		Boolean status = false;

		for(ContactUsForm contactUsForm: contactUsFormList){
			if(contactUsForm.getId() != null){
				status = baseDao.removeUpdateData(UPDATE_SCHEDULER_STATUS, 1, contactUsForm.getId());
				if(!status){
					LOG.info("error for id:"+contactUsForm.getId());
				}
			}
		}
		return status;
	}


}
