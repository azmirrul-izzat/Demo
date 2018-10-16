package com.apddemo.core.services.impl;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.EmailService;
import com.apddemo.core.utils.NodeUtils;
import com.apddemo.core.utils.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = EmailService.class
        , configurationPid = "com.apddemo.core.services.impl.EmailServiceImpl")
public class EmailServiceImpl implements EmailService{
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String EMAIL_SETTINGS_NODE_RELATIVE_PATH = "/settings/contactusemailsettings";

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    public EmailSettings getEmailSettingsFromNode(
            ResourceResolver resourceResolver, String nodePath) {

        EmailSettings emailSettings = null;

        try {

            String settingsPath = nodePath + EMAIL_SETTINGS_NODE_RELATIVE_PATH;
            Resource emailSettingsResource = resourceResolver
                    .getResource(settingsPath);

            if (emailSettingsResource != null) {

                Node emailSettingsNode = emailSettingsResource
                        .adaptTo(Node.class);
                if (emailSettingsNode != null) {

                    emailSettings = new EmailSettings();

                    NodeUtils.parsePropertyIntoBean(emailSettings,
                            emailSettingsNode, "fromAddress", "toaddress",
                            "ccaddress", "subject", "template");

                    String emailTemplate = emailSettings.getTemplate();

                    if (emailTemplate != null
                            && (emailTemplate.endsWith(".html") || emailTemplate
                            .endsWith(".txt"))) {

                        Resource templateResource = resourceResolver
                                .getResource(emailTemplate);
                        if (templateResource != null) {

                            InputStream templateIs = templateResource
                                    .adaptTo(InputStream.class);
                            String templateContents = StringUtils
                                    .getStringFromInputStream(templateIs);
                            emailSettings.setTemplate(templateContents);
                        } else {
                            log.warn("getEmailSettingsFromNode: email template resource is null for path:"
                                    + emailTemplate);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("getEmailSettingsFromNode: encountered an Exception", e);
        }

        return emailSettings;
    }



    @Override
    public EmailSettings getEmailSettingsFromPath(ResourceResolver resourceResolver, String path) {

        EmailSettings emailSettings = null;
        try {
            String settingsPath = path;
            Resource emailSettingsResource = resourceResolver
                    .getResource(settingsPath);

            if (emailSettingsResource != null) {

                Node emailSettingsNode = emailSettingsResource
                        .adaptTo(Node.class);
                if (emailSettingsNode != null) {

                    emailSettings = new EmailSettings();

                    NodeUtils.parsePropertyIntoBean(emailSettings,
                            emailSettingsNode, "fromAddress", "toaddress", "ccaddress", "subject", "template");

                    String emailTemplate = emailSettings.getTemplate();

                    if (emailTemplate != null
                            && (emailTemplate.endsWith(".html") || emailTemplate
                            .endsWith(".txt"))) {

                        Resource templateResource = resourceResolver
                                .getResource(emailTemplate);
                        if (templateResource != null) {

                            InputStream templateIs = templateResource
                                    .adaptTo(InputStream.class);
                            String templateContents = StringUtils
                                    .getStringFromInputStream(templateIs);
                            emailSettings.setTemplate(templateContents);
                        } else {
                            log.warn("getEmailSettingsFromNode: email template resource is null for path:" + emailTemplate);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("getEmailSettingsFromNode: encountered an Exception", e);
        }

        return emailSettings;
    }

    @Override
    public boolean sendEmail(EmailSettings emailSettings) {

        //log.info("Final sendEmail.....:" + emailSettings.getToaddress()[0]);
        //log.info("Final sendEmail template.....:" + emailSettings.getTemplate());
        boolean sendSuccess = false;

        try {
            if (emailSettings != null) {

                Email email = new HtmlEmail();
                email.setCharset("UTF-8");
                String message = StringUtils.formatString(
                        emailSettings.getTemplate(),
                        emailSettings.getTemplateParameters());
                // email.setMsg(message);
                email.setContent(message, "text/html; charset=utf-8");

                List<InternetAddress> toAddressList = new ArrayList<InternetAddress>();
                for (String toAddress : emailSettings.getToaddress()) {
                    if (StringUtils.isNotBlank(toAddress)) {
                        toAddressList.add(new InternetAddress(toAddress));
                    }
                }
                log.info("toAddressList:"+toAddressList);
                email.setTo(toAddressList);
                email.setFrom(emailSettings.getFromAddress());

                if (emailSettings.getCcaddress() != null
                        && emailSettings.getCcaddress().length > 0) {
                    List<InternetAddress> ccAddressList = new ArrayList<InternetAddress>();
                    for (String ccAddress : emailSettings.getCcaddress()) {
                        if (StringUtils.isNotBlank(ccAddress)) {
                            ccAddressList.add(new InternetAddress(ccAddress));
                        }
                    }
                    email.setCc(ccAddressList);
                }

                email.setSubject(emailSettings.getSubject());
                MessageGateway<Email> messageGateway = messageGatewayService
                        .getGateway(email.getClass());
                messageGateway.send(email);
                sendSuccess = true;
            } else {
                log.warn("sendEmail: emailSettings is null");
            }

        } catch (EmailException e) {
            log.error("sendEmail: encountered EmailException", e);
        } catch (AddressException e) {
            log.error("sendEmail: encountered AddressException", e);
        } catch (Throwable e) {
            log.error("sendEmail: encountered Throwable", e);
        }

        return sendSuccess;
    }

    @Override
    public boolean sendEmailWithAttachment(EmailSettings emailSettings,
                                           MimeBodyPart bodyPartWithAttachment) {
        log.info("Final send email with Attchment.....:"
                + emailSettings.getToaddress()[0]);
        boolean sendSuccess = false;

        try {
            if (emailSettings != null) {

                MultiPartEmail email = new MultiPartEmail();
                email.setCharset("UTF-8");
                String message = StringUtils.formatStringUTF8(
                        emailSettings.getTemplate(),
                        emailSettings.getTemplateParameters());
                Multipart multipart = new MimeMultipart();
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                //  String htmlText = ("<div style=\"color:red;\">BRIDGEYE</div>");
                message = message.replaceAll("\"", "\\\"");
                messageBodyPart.setContent(message, "text/html");
				/*bodyPartWithAttachment.set
				multipart.addBodyPart(bodyPartWithAttachment);*/
                //multipart.addBodyPart(messageBodyPart);
                // set email body with attachment here
                //	email.setContent((MimeMultipart) multipart);
                //

                multipart.addBodyPart(bodyPartWithAttachment); //3
                // Create the HTML Part
                BodyPart htmlBodyPart = new MimeBodyPart(); //4
                htmlBodyPart.setContent(message , "text/html"); //5
                multipart.addBodyPart(htmlBodyPart); // 6
                // Set the Multipart's to be the email's content
                //message.setContent(multipart); //7

                email.setContent((MimeMultipart) multipart);




                List<InternetAddress> toAddressList = new ArrayList<InternetAddress>();
                for (String toAddress : emailSettings.getToaddress()) {
                    if (StringUtils.isNotBlank(toAddress)) {
                        toAddressList.add(new InternetAddress(toAddress));
                    }
                }
                email.setTo(toAddressList);
                email.setFrom(emailSettings.getFromAddress());
                if (emailSettings.getCcaddress() != null
                        && emailSettings.getCcaddress().length > 0) {
                    List<InternetAddress> ccAddressList = new ArrayList<InternetAddress>();
                    for (String ccAddress : emailSettings.getCcaddress()) {
                        if (StringUtils.isNotBlank(ccAddress)) {
                            ccAddressList.add(new InternetAddress(ccAddress));
                        }
                    }
                    email.setCc(ccAddressList);
                }

                email.setSubject(emailSettings.getSubject());
                MessageGateway<Email> messageGateway = messageGatewayService
                        .getGateway(HtmlEmail.class);
                messageGateway.send(email);
                sendSuccess = true;
            } else {
                log.warn("sendEmail: emailSettings is null");
            }

        } catch (EmailException e) {
            log.error("sendEmail: encountered EmailException", e);
        } catch (AddressException e) {
            log.error("sendEmail: encountered AddressException", e);
        } catch (Throwable e) {
            log.error("sendEmail: encountered Throwable", e);
        }

        return sendSuccess;

    }

    @Override
    public String getMasterEmailTemplate(ResourceResolver resourceResolver,
                                         String emailTemplatePath) {
        if (emailTemplatePath != null
                && (emailTemplatePath.endsWith(".html") || emailTemplatePath
                .endsWith(".txt"))) {
            Resource templateResource = resourceResolver
                    .getResource(emailTemplatePath);
            if (templateResource != null) {
                InputStream templateIs = templateResource
                        .adaptTo(InputStream.class);
                return StringUtils.getStringFromInputStream(templateIs);
            } else {
                log.warn("getEmailSettingsFromNode: email template resource is null for path:"
                        + emailTemplatePath);
            }
        }
        return null;
    }
}
