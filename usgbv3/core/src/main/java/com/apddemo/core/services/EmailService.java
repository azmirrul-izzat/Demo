package com.apddemo.core.services;

import com.apddemo.core.models.EmailSettings;
import org.apache.sling.api.resource.ResourceResolver;

import javax.mail.internet.MimeBodyPart;

public interface EmailService {
    String getMasterEmailTemplate(ResourceResolver resourceResolver, String path);
    EmailSettings getEmailSettingsFromNode(ResourceResolver resourceResolver, String nodePath);
    boolean sendEmail(EmailSettings emailSettings);
    boolean sendEmailWithAttachment(EmailSettings emailSettings, MimeBodyPart bodyPartWithAttachment);
    EmailSettings getEmailSettingsFromPath(ResourceResolver resourceResolver,String path);

}
