package com.apddemo.core.schedulers;

import com.day.commons.datasource.poolservice.DataSourcePool;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 *
 * clear the sharedata table before 30 days
 * 
 */
@Designate(ocd=ClearShareDataScheduledTask.Config.class)
@Component(service=Runnable.class)
public class ClearShareDataScheduledTask implements Runnable {

	@ObjectClassDefinition(name="Clear ShareData Task",
			description = "Clear ShareData Table before 30days")
	public static @interface Config {

		@AttributeDefinition(name = "Cron-job expression")
		String scheduler_expression() default "0 30  * * ?";

		@AttributeDefinition(name = "Concurrent task",
				description = "Whether or not to schedule this task concurrently")
		boolean scheduler_concurrent() default false;
	}

	public static final String GET_STMT = "delete from ShareData WHERE timeAdded < GETDATE()- 30";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String DATA_SOURCE_NAME_PUB = "usgbsqldbprod.database.windows.net";
	private static final String DATA_SOURCE_NAME_UAT = "usgbsqldbuat.database.windows.net";
	private static final String DATA_SOURCE_NAME_LOCAL ="local";
	
	@Reference
	private DataSourcePool source;

    @Override
    public void run() {
        logger.debug("Clear ShareData Task is running");
        clearShareData();
    }
    
    private void clearShareData(){
    	
    	Connection connection = getConnection();
		try {
			if(connection != null){
				PreparedStatement pStatement = connection.prepareStatement(GET_STMT);
				pStatement.executeUpdate();
				
			}else{
				logger.warn("queryData: connection is null");
			}
		} catch (SQLException e) {
			logger.error("queryData: encountered SQLException", e);
		}finally {
			try {
				if (connection != null) connection.close();
			} catch (SQLException e) {
				logger.error("deleteData: encountered SQLException while closing connection", e);
			}
		}
    	
    }
    
	protected Connection getConnection() {
		DataSource dataSource = null;
		Connection connection = null;
		
		try{
			dataSource = (DataSource) source.getDataSource(DATA_SOURCE_NAME_UAT);
			connection = dataSource.getConnection();
		} catch (Exception e) {
			logger.error("getConnection: encountered Exception", e);
		}
		
		return connection; 
	}

	@Activate
	protected void activate(final ClearShareDataScheduledTask.Config config) {

	}
}
