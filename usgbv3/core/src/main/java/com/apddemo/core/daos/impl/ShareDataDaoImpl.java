package com.apddemo.core.daos.impl;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.apddemo.core.daos.BaseDao;
import com.apddemo.core.daos.ShareDataDao;
import com.apddemo.core.models.QueryDataResult;
import com.apddemo.core.models.ShareData;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component(immediate = true, service = ShareDataDao.class
		, configurationPid = "com.apddemo.core.daos.impl.ShareDataDaoImpl")
public class ShareDataDaoImpl implements ShareDataDao {

	public static final String INSERT_STMT = "insert into ShareData values (?,?,?)";
	public static final String GET_STMT = "select * from ShareData where ID = ?";
	private SimpleDateFormat submitTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	BaseDao baseDao;

	@Override
	public boolean insertShareData(ShareData data) {

		boolean insertSuccess = baseDao.insertData(INSERT_STMT, data.getUuid(),
				data.getConcatenatedPaths(),submitTime.format(new Date()));
		return insertSuccess;
	}

	@Override
	public ShareData getShareData(String key) {
		ShareData result = null;
		ResultSet resultSet = null;
		QueryDataResult queryDataResult = null;
		try {
			queryDataResult = baseDao.queryData(GET_STMT, key);
			
			resultSet = queryDataResult != null ? queryDataResult.getResultSet() : null;

			if (resultSet != null && resultSet.next() && resultSet.getString("paths") != null){
				result = new ShareData();
				result.setUuid(key);
				result.setConcatenatedPaths(resultSet.getString("paths"));
			}else{
				log.debug("getShareData: resultSet:" + resultSet);
				return null;
			}
		} catch (SQLException e) {
			log.error("queryData: encountered SQLException", e);
		} finally {
			baseDao.closeQueryDataResult(queryDataResult);
		}

		return result;
	}
}
