package com.apddemo.core.schedulers;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.apddemo.core.daos.ContactUsDao;
import com.apddemo.core.models.ContactUsForm;
import com.apddemo.core.services.ContactUSFormService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
import java.util.List;


/**
 *
 * clear the sharedata table before 30 days
 * 
 */
@Designate(ocd=ContactUSMailScheduler.Config.class)
@Component(service=Runnable.class)
public class ContactUSMailScheduler implements Runnable {

	@ObjectClassDefinition(name="Contact US Email Scheduler",
			description = "This scheduler will run after 1 days and populate the form1 alone submitted data")
	public static @interface Config {

		@AttributeDefinition(name = "Cron-job expression")//""
		String scheduler_expression() default "0 */30 * ? * *";

		@AttributeDefinition(name = "Concurrent task",
				description = "Whether or not to schedule this task concurrently")
		boolean scheduler_concurrent() default false;
	}
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Reference
	private DataSourcePool source;

	@Reference
	private ContactUsDao contactUsDao;

	@Reference
	private ContactUSFormService contactUSFormService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void run() {
		LOG.debug("Contact US Mail Scheduler is running");
		mailContactUsData();
    }

    private void mailContactUsData(){
		ResourceResolver adminResourceResolver = null;
		try {
			adminResourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			LOG.info("adminResourceResolver:"+adminResourceResolver);
			List<ContactUsForm> contactUsFormList = contactUsDao.getContactUSForm1SubmitDetail();
			LOG.info("contactUsFormList:"+contactUsFormList);
			Boolean emailStatus = false, dbUpdateStatus= false;
			if(contactUsFormList != null && contactUsFormList.size()>0){
				emailStatus = contactUSFormService.sendContactUSBatchEmail("/content/demo/global"
                        ,contactUsFormList, adminResourceResolver);
				LOG.info("emailStatus:"+emailStatus);
			}
			if(emailStatus){
				dbUpdateStatus = contactUsDao.updateContactUSForm1SubmitDetails(contactUsFormList);
			}
			LOG.info("dbUpdateStatus:"+dbUpdateStatus);
		} catch (LoginException e) {
			LOG.error("LoginException:"+e);
		}finally {
			// ALWAYS close resolvers you open
			if (adminResourceResolver != null) {
				adminResourceResolver.close();
			}
		}
    }

	@Activate
	protected void activate(final ContactUSMailScheduler.Config config) {

	}
}
