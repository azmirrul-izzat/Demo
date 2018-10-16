package com.apddemo.core.daos.impl;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.apddemo.core.daos.BaseDao;
import com.apddemo.core.models.QueryDataResult;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = BaseDao.class,
		configurationPid = "com.apddemo.core.daos.impl.BaseDaoImpl")
public class BaseDaoImpl implements BaseDao{

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String DATA_SOURCE_NAME_PUB = "usgbsqldbprod.database.windows.net";
	private static final String DATA_SOURCE_NAME_UAT = "usgbsqldbuat.database.windows.net";
	private static final String DATA_SOURCE_NAME_LOCAL ="local";

	/**
	 * Injecting the data source pool
	 */
	@Reference
	DataSourcePool dataSourcePool;
	
	protected Connection getConnection() {
		DataSource dataSource = null;
		Connection connection = null;
		
		try{
			dataSource = (DataSource) dataSourcePool.getDataSource(DATA_SOURCE_NAME_UAT);
			connection = dataSource.getConnection();
		} catch (Exception e) {
			log.error("getConnection: encountered Exception", e);
		}
		
		return connection; 
	}
	
	public void closeQueryDataResult(QueryDataResult queryDataResult){
		try{
			if(queryDataResult != null){
				if(queryDataResult.getResultSet() != null){
					queryDataResult.getResultSet().close();
				}
				if(queryDataResult.getpStatement() != null){
					queryDataResult.getpStatement().close();
				}
				if(queryDataResult.getConnection() != null){
					queryDataResult.getConnection().close();
				}
			}
		}catch(SQLException e){
			log.error("closeQueryDataResult: encountered SQLException while closing QueryDataResult", e);
		}
	}

	public QueryDataResult queryData(String queryString, Object... data){
		
		Connection connection = getConnection();
		QueryDataResult queryDataResult = null;
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		try {
			if(connection != null){
				pStatement = connection.prepareStatement(queryString);
				if(data != null){
					for(int i=0; i < data.length; i++){
						pStatement.setObject(i+1, data[i]);
					}
				}
				resultSet = pStatement.executeQuery();
				queryDataResult = new QueryDataResult(connection, resultSet, pStatement);
			}else{
				log.warn("queryData: connection is null");
			}
		} catch (SQLException e) {
			log.error("queryData: encountered SQLException", e);
		} finally {
			/*try {
				if (pStatement != null) pStatement.close();
				//if (connection != null && !connection.isClosed()) connection.close();
			} catch (SQLException e) {
				log.error("queryData: encountered SQLException while closing connection", e);
			}*/
		}
		
		return queryDataResult;
	}

	public boolean insertData(String statement, Object... data) {
		
		boolean success = false;
		Connection connection = null;
		PreparedStatement pStatement = null;
		try {
			connection = getConnection();
			
			if(connection != null){
				pStatement = connection.prepareStatement(statement);
				
				for(int i=0; i < data.length; i++){
					pStatement.setObject(i+1, data[i]);
				}
				pStatement.execute();
				success = true;
			}else{
				log.warn("insertData: connection is null");
				log.info("insertData: connection is null");
			}
		} catch (Exception e) {
			log.error("insertData: encountered SQLException", e);
			log.info("insertData: encountered SQLException", e);
		} finally {
			try {
				//20160413 - iSkandar - Close Statement and connection DTU Issue
				if (pStatement != null) pStatement.close();
				//veeru added and logic
				if (connection != null && !connection.isClosed()) connection.close();
				//20160413 - iSkandar - Close Statement and connection DTU Issue
			} catch (SQLException e) {
				log.error("insertData: encountered SQLException while closing connection", e);
			}
		}
		
		return success; 
	}

	public List<Map<String, Object>> retrieveData(String queryString, Object... data){
		
		log.info("statement: " +queryString);
		Connection connection = getConnection();
		QueryDataResult queryDataResult = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> finalResult = null;
		try {
			if(connection != null){
				PreparedStatement pStatement = connection.prepareStatement(queryString);
				for(int i=0; i < data.length; i++){
					pStatement.setObject(i+1, data[i]);
				}
				resultSet = pStatement.executeQuery();
				finalResult = resultSetToList(resultSet);
				log.info("finalResult : " + finalResult.toString());
			}else{
				log.warn("queryData: connection is null");
			}
		} catch (SQLException e) {
			log.error("queryData: encountered SQLException", e);
		}finally{
			
			try {
				connection.close();
				closeQueryDataResult(queryDataResult);
				
				if (resultSet != null){
					resultSet.close();
				}
			} catch (SQLException e) {
				log.error("BaseDao : queryDataNew :: " + e.getMessage());
			}
		}
		
		return finalResult;
	}

	public int insertDataReturnId(String statement, Object... data) {
		
		int insertId = 0;
		Connection connection = null;
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			if(connection != null){
				pStatement = connection.prepareStatement(statement,Statement.RETURN_GENERATED_KEYS);
				
				for(int i=0; i < data.length; i++){
					pStatement.setObject(i+1, data[i]);
				}
				pStatement.executeUpdate();
				
				rs = pStatement.getGeneratedKeys();

				if (rs.next()) {
					insertId = (int) rs.getLong(1);
				}
				
			}else{
				log.warn("insertDataReturnId: connection is null");
			}
		} catch (Exception e) {
			if(e.getMessage().contains("Violation of UNIQUE KEY")){
				insertId = -999;
			}
			log.error("insertDataReturnId: encountered SQLException : " + e.getMessage());
		} finally {
			try {
				if (pStatement != null) pStatement.close();
				if (connection != null) connection.close();

				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				log.error("insertDataReturnId: encountered SQLException while closing connection", e);
			}
		}
		
		return insertId; 
	}

	public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
	    while (rs.next()){
	        Map<String, Object> row = new HashMap<String, Object>(columns);
	        for(int i = 1; i <= columns; ++i){
	            row.put(md.getColumnName(i), rs.getObject(i));
	        }
	        rows.add(row);
	    }
	    return rows;
	}
	
	public boolean removeUpdateData(String statement, Object... data) {

//		log.info("statement: " +statement);
		boolean success = false;
		Connection connection = null;
		PreparedStatement pStatement = null;
		try {
			connection = getConnection();
			if(connection != null){
				pStatement = connection.prepareStatement(statement);
				
				for(int i=0; i < data.length; i++){
					pStatement.setObject(i+1, data[i]);
				}
				pStatement.execute();
				success = true;
			}else{
				log.warn("removeData: connection is null");
				log.info("removeData: connection is null");
			}
		} catch (Exception e) {
			log.error("removeData: encountered SQLException", e);
			log.info("removeData: encountered SQLException", e);
		} finally {
			try {
				//20160413 - iSkandar - Close Statement and connection DTU Issue
				if (pStatement != null) pStatement.close();
				if (connection != null) connection.close();
				//20160413 - iSkandar - Close Statement and connection DTU Issue
			} catch (SQLException e) {
				log.error("removeData: encountered SQLException while closing connection", e);
			}
		}
		
		return success; 
	}
	
	public void closeResultSet(ResultSet resultSet){
		try{
			if(resultSet != null){
				resultSet.close();
			}
		}catch(SQLException e){
			log.error("closeResultSet: encountered SQLException while closing ResultSet", e);
		}
	}
}
