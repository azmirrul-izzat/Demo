package com.apddemo.core.services.impl;

import com.apddemo.core.models.Contact;
import com.apddemo.core.models.ContactDetails;
import com.apddemo.core.services.ContactService;
import com.apddemo.core.utils.NodeUtils;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = ContactService.class,
        configurationPid = "com.apddemo.core.daos.impl.ContactServiceImpl")
public class ContactServiceImpl implements ContactService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final static String KEY_INQUIRY_TOPIC = "inquiryTopic";
    protected final static String KEY_PRODUCT_INTEREST = "productInterest";


    @Override
    public List<ContactDetails> getContactDetails(ResourceResolver resourceResolver,
                                            String nodePath, String enquiryType, String product) {
        List<ContactDetails> contactDetailsList = new ArrayList<>();
        ContactDetails contactDetails =  null;
        log.info("enquiryType"+ enquiryType);
        log.info("product"+ product);
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("getContactDetails: enquiryType[%s] product[%s] state[%s]", enquiryType, product));
            }

            NodeIterator childNodeIterator = NodeUtils.getAndvalidateParentNodeAndGetChildNodeIterator(
                    nodePath, resourceResolver);
            if (childNodeIterator != null) {
                while(childNodeIterator.hasNext()){
                    Node childNode = childNodeIterator.nextNode();
                    if(childNode.hasProperty("cluster") && "specific".equals(childNode.getProperty("cluster").getValue().getString())){
                        if(childNode.hasProperty("productInterest") && childNode.getProperty("productInterest") != null){
                            String propertyInterest = childNode.getProperty("productInterest").getValue().getString();
                            if(!ContactService.KEYWORD_MULTIPLE_PRODUCTS.equals(propertyInterest)){
                                propertyInterest = StringUtils.replaceSpecialCharacters(propertyInterest.toLowerCase());
                            }
                           // log.info("propertyInterest:"+propertyInterest);
                            if(propertyInterest.equals(product)){
                                if(childNode.hasProperty("inquiryTopic") && childNode.getProperty("inquiryTopic")!= null){
                                    String inquiryTopic = childNode.getProperty("inquiryTopic").getValue().getString();
                                    inquiryTopic = StringUtils.replaceSpecialCharacters(inquiryTopic.toLowerCase());
                                    //log.info("inquiryTopic:"+inquiryTopic);
                                    if(inquiryTopic.equals(enquiryType)){
                                        contactDetails = new ContactDetails();
                                        //log.info("childNode matched is :"+childNode.getPath());
                                        if(childNode.hasProperty("contact") && childNode.getProperty("contact") != null){
                                            contactDetails.setContact(childNode.getProperty("contact").getValue().getString());
                                        }
                                        if(childNode.hasProperty("Phone") && childNode.getProperty("Phone") != null){
                                            contactDetails.setPhone(childNode.getProperty("Phone").getValue().getString());
                                        }
                                        if(childNode.hasProperty("email") && childNode.getProperty("email") != null){
                                            //log.info("emails is :"+childNode.getProperty("email").getValue().getString());
                                            contactDetails.setEmail(childNode.getProperty("email").getValue().getString());
                                        }
                                        if(childNode.hasProperty("inquiryTopic") && childNode.getProperty("inquiryTopic") != null){
                                            contactDetails.setInquiryTopic(childNode.getProperty("inquiryTopic").getValue().getString());
                                        }
                                        if(childNode.hasProperty("productInterest") && childNode.getProperty("productInterest") != null){
                                            contactDetails.setProductInterest(childNode.getProperty("productInterest").getValue().getString());
                                        }
                                        contactDetailsList.add(contactDetails);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("getContactDetails: childNodeIterator is null");
                }
            }
        } catch (ValueFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("getContactDetails: encountered ValueFormatException:" + e.getMessage());
            }
        } catch (PathNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("getContactDetails: encountered PathNotFoundException:" + e.getMessage());
            }
        } catch (RepositoryException e) {
            if (log.isDebugEnabled()) {
                log.debug("getContactDetails: encountered RepositoryException:" + e.getMessage());
            }
        } catch (Throwable e) {
            log.error("getContactDetails: encountered Throwable", e);
        }

        return contactDetailsList;
    }

    /*private void iterateNodeAndGetDetails(NodeIterator childNodeIterator, ContactDetails contactDetails,
                                          String enquiryType, String product, String state) throws Exception {
        log.info(" In iterateNodeAndGetDetails.....::::");

        log.info("product.....:" + product);
        log.info("enquiryType.....:" + enquiryType);
        log.info("state.....:" + state);
        if (enquiryType != null) {
            enquiryType = enquiryType.replace("_", " ");
        }
        while (childNodeIterator.hasNext()) {
            Node childNode = childNodeIterator.nextNode();
            if ((StringUtils.isNotBlank(product) && childNode.hasProperty(KEY_PRODUCT_INTEREST) &&
                    product.equalsIgnoreCase(childNode.getProperty(KEY_PRODUCT_INTEREST).getString())) || StringUtils.isBlank(product)) {

                if ((StringUtils.isNotBlank(enquiryType) && childNode.hasProperty(KEY_INQUIRY_TOPIC) &&
                        enquiryType.equalsIgnoreCase(childNode.getProperty(KEY_INQUIRY_TOPIC).getString())) || StringUtils.isBlank(enquiryType)) {

                    NodeUtils.parsePropertyIntoBean(contactDetails, childNode, "inquiryTopic", "productInterest");

                    Contact contact1 = new Contact();
                    contact1.setName(childNode.hasProperty("contact1") ? childNode.getProperty("contact1").getString() : null);
                    contact1.setPhone(childNode.hasProperty("contact1PhoneNumber") ? childNode.getProperty("contact1PhoneNumber").getString() : null);
                    contact1.setEmail(childNode.hasProperty("contact1Email") ? childNode.getProperty("contact1Email").getString() : null);
                    if (StringUtils.isNotBlank(contact1.getName()) ||
                            StringUtils.isNotBlank(contact1.getPhone()) ||
                            StringUtils.isNotBlank(contact1.getEmail())) {
                        contactDetails.addContact(contact1);
                    }

                    Contact contact2 = new Contact();
                    contact2.setName(childNode.hasProperty("contact2") ? childNode.getProperty("contact2").getString() : null);
                    contact2.setPhone(childNode.hasProperty("contact2PhoneNumber") ? childNode.getProperty("contact2PhoneNumber").getString() : null);
                    contact2.setEmail(childNode.hasProperty("contact2Email") ? childNode.getProperty("contact2Email").getString() : null);

                    if (StringUtils.isNotBlank(contact2.getName()) ||
                            StringUtils.isNotBlank(contact2.getPhone()) ||
                            StringUtils.isNotBlank(contact2.getEmail())) {
                        contactDetails.addContact(contact2);
                    }

                    break;
                }
            }
        }
    }*/
}
