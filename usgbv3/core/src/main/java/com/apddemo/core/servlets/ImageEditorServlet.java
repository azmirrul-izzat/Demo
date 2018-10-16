package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.apddemo.core.utils.DamUtils;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Asset Tools - Image Cropper" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/usgb/v3/imageEditor"
})
public class ImageEditorServlet extends BaseAllMethodsServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1382304157686275614L;
	
	private final static Logger LOG = LoggerFactory.getLogger(ImageEditorServlet.class);
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {
		ImageEditorResponse respObj = null;
		try {
			LOG.info("ImageEditorServlet START");

			response.setCharacterEncoding("utf-8");
			request.setCharacterEncoding("utf-8");

			String imageData = request.getParameter("imageData");
			String targetPath = request.getParameter("targetPath");
						
 		    String fileName = targetPath.substring(targetPath.lastIndexOf("/") + 1, targetPath.length());
			String damLocation = targetPath.substring(0, targetPath.lastIndexOf("/"));
			
			String damImagePath = DamUtils.saveBase64Image(request.getResourceResolver(), imageData, fileName, request.getResourceResolver().adaptTo(Session.class), damLocation);
						
			if(damImagePath != null){
				respObj = new ImageEditorResponse("0", "Saved successfully");
			}

		}catch (Exception e) {
			LOG.error("doPost: encountered Exception", e);
			respObj = new ImageEditorResponse("1", e.getMessage());
		}catch (Throwable e) {
			LOG.error("doPost: encountered Throwable", e);
			respObj = new ImageEditorResponse("1", e.getMessage());
		}

		setJsonResponseOk(response, new Gson().toJson(respObj));
		
		LOG.info("ImageEditorServlet END");
	}
		
	/**
	 * @author User
	 *
	 */
	public class ImageEditorResponse{
		
		private String msgCode;
		private String msgDesc;
		
		public ImageEditorResponse() {
		}

		public ImageEditorResponse(String msgCode, String msgDesc) {
			this.msgCode = msgCode;
			this.msgDesc = msgDesc;
		}
		
		public String getMsgCode() {
			return msgCode;
		}
		public void setMsgCode(String msgCode) {
			this.msgCode = msgCode;
		}
		public String getMsgDesc() {
			return msgDesc;
		}
		public void setMsgDesc(String msgDesc) {
			this.msgDesc = msgDesc;
		}		
	}

}
