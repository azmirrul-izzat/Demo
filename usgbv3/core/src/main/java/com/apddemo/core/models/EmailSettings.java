package com.apddemo.core.models;

public class EmailSettings {

	private String fromAddress;
	private String[] toaddress;
	private String[] ccaddress;
	private String subject;
	private String template;
	private String[] templateParameters;
	
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String[] getToaddress() {
		return toaddress;
	}
	public void setToaddress(String[] toaddress) {
		this.toaddress = toaddress;
	}
	public String[] getCcaddress() {
		return ccaddress;
	}
	public void setCcaddress(String[] ccaddress) {
		this.ccaddress = ccaddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String[] getTemplateParameters() {
		return templateParameters;
	}
	public void setTemplateParameters(String[] templateParameters) {
		this.templateParameters = templateParameters;
	}
	public void setTemplateParametersByVarArgs(String... templateParameters) {
		this.templateParameters = templateParameters;
	}
}
