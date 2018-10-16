package com.apddemo.core.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.apddemo.core.daos.ContactUsDao;
import com.apddemo.core.models.Contact;
import com.apddemo.core.models.ContactDetails;
import com.apddemo.core.models.ContactUsForm;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.ContactService;
import com.apddemo.core.services.ContactUSFormService;
import com.apddemo.core.services.EmailService;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = ContactUSFormService.class
        , configurationPid = "com.apddemo.core.services.impl.ContactUSFormServiceImpl")
public class ContactUSFormServiceImpl implements ContactUSFormService{

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final String DELIMETER = ",";

    @Reference
    ContactUsDao contactUsDao;

    @Reference
    private EmailService emailService;

    @Reference
    private ContactService contactService;

    @Override
    public String submitContactUSBasicForm(JsonObject inputData) {
        String jsonResponse = "";
        Gson gson = new Gson();

        // reading parameters
        //LOG.info("parameterMap:"+parameterMap);
        ContactUsForm contactUsForm = null;
        String insertResponse = null;
        if(inputData != null){
            contactUsForm = new ContactUsForm();
            if(inputData.has("dropdownselect")){
                contactUsForm.setInquiryType(inputData.get("dropdownselect").getAsString());
            }
            if(inputData.has("name")){
                contactUsForm.setName(inputData.get("name").getAsString());
            }
            if(inputData.has("email")){
                contactUsForm.setEmail(inputData.get("email").getAsString());
            }
            if(inputData.has("message")){
                contactUsForm.setMessage(inputData.get("message").getAsString());
            }
            if(inputData.has("agree") ){
                if("on".equals(inputData.get("agree").getAsString())){
                    contactUsForm.setInformNews("1");
                }else{
                    contactUsForm.setInformNews("0");
                }

            }
            LOG.info("inputData to string :"+inputData.toString());
            contactUsForm.setForm1Payload(inputData.toString());
            LOG.info("contactUsDao:"+contactUsDao);
            if(contactUsDao != null){
                LOG.info("contactUsForm:"+contactUsForm);
                insertResponse = contactUsDao.insertForm1Data(contactUsForm);
            }

        }
        JsonObject response = new JsonObject();
        response.addProperty("id", insertResponse);
        jsonResponse =gson.toJson(response);
        return jsonResponse;
    }

    @Override
    public String submitContactUSAdvancedForm(String data, SlingHttpServletRequest request) {
        String jsonData =null;
        boolean status = false;
        Gson gson = new Gson();
        ContactUsForm contactUsForm= null;

        JsonParser parser = new JsonParser();
        JsonObject inputData = parser.parse(data).getAsJsonObject();
        LOG.info("inputData:"+inputData);
        if(inputData != null){
            contactUsForm = new ContactUsForm();
            if(inputData.has("dropdownselect")){
                contactUsForm.setInquiryType(inputData.get("dropdownselect").getAsString());
            }
            if(inputData.has("name")){
                contactUsForm.setName(inputData.get("name").getAsString());
            }
            if(inputData.has("email")){
                contactUsForm.setEmail(inputData.get("email").getAsString());
            }
            if(inputData.has("message")){
                contactUsForm.setMessage(inputData.get("message").getAsString());
            }
            if(inputData.has("agree")){
                String agreeNews = inputData.get("agree").getAsString();
                if("on".equals(agreeNews)){
                    contactUsForm.setInformNews("1");
                }else{
                    contactUsForm.setInformNews("0");
                }
            }
            if(inputData.has("dbid")){
                contactUsForm.setId(inputData.get("dbid").getAsString());
            }
            if(inputData.has("contact")){
                contactUsForm.setPhone(inputData.get("contact").getAsString());
            }
            if(inputData.has("occupation")){
                contactUsForm.setOccupation(inputData.get("occupation").getAsString());
            }
            // these two are array
            if(inputData.has("i_work_with")){
                JsonArray i_work_withJsonArray = inputData.get("i_work_with").getAsJsonArray();
                StringBuffer workWithSB = new StringBuffer("");
                for(int i=0; i<i_work_withJsonArray.size(); i++){
                    workWithSB.append(i_work_withJsonArray.get(i).getAsString()).append(",");
                }
                if(workWithSB.length()>0){
                    contactUsForm.setWorkWith(workWithSB.substring(0, workWithSB.length()-1));
                }
            }
            if(inputData.has("i_am_interested")){
                JsonArray interestedJsonArray = inputData.get("i_am_interested").getAsJsonArray();
                StringBuffer interestedSB = new StringBuffer("");
                for(int i=0; i<interestedJsonArray.size(); i++){
                    interestedSB.append(interestedJsonArray.get(i).getAsString()).append(",");
                }
                if(interestedSB.length()>0){
                    contactUsForm.setProductInterest(interestedSB.substring(0, interestedSB.length()-1));
                }

            }
            contactUsForm.setForm2Payload(inputData.toString());
            // set status to 1
            contactUsForm.setStatus("1");
            LOG.info("contactUsDao:"+contactUsDao);
            if(contactUsDao != null){
                LOG.info("contactUsForm:"+contactUsForm);
                status = contactUsDao.insertForm2Data(contactUsForm);
            }

        }
        LOG.info("status of db is :"+status);

        String homePagePath = null;
        try {
            String referrerURIfromRequest = StringUtils.getReferrerURIfromRequest(request);
            homePagePath = StringUtils.getRootSitePath(referrerURIfromRequest);
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while getting referrer url:"+e);
        }
        LOG.info("homePagePath:"+homePagePath);
        if(status && homePagePath!= null){
            String productInterest = contactUsForm.getProductInterest();
            productInterest = productInterest.replace("_", " ");

            // trigger email
            status = sendContactUSForm2Email(request.getResourceResolver(), homePagePath
                    , "contacts",
                    contactUsForm.getEmail(), contactUsForm.getProductInterest(), contactUsForm.getInquiryType(),
                    contactUsForm.getName(), contactUsForm.getEmail(), contactUsForm.getOccupation(),
                    contactUsForm.getWorkWith(), contactUsForm.getPhone(), contactUsForm.getInquiryType()
                    , productInterest, contactUsForm.getInformNews(), contactUsForm.getMessage());

        }

        JsonObject response = new JsonObject();
        response.addProperty("status", status);
        jsonData =gson.toJson(response);
        return jsonData;
    }

    @Override
    public Boolean sendContactUSBatchEmail(String configurationBasePath, List<ContactUsForm> contactUsFormList
            , ResourceResolver adminResourceResolver) {
        Boolean status = false;
        if(configurationBasePath != null && contactUsFormList != null && contactUsFormList.size()>0){
           EmailSettings emailSettings = emailService.getEmailSettingsFromNode(adminResourceResolver, configurationBasePath);
           if(emailSettings != null){

               String[] emailTemplateVarArgs = null;
               for(ContactUsForm contactUsForm :contactUsFormList){
                   //LOG.info("contactUsForm.getEmail():"+contactUsForm.getEmail());
                   emailSettings.setFromAddress(contactUsForm.getEmail());
                   emailTemplateVarArgs = new String[7];
                   emailTemplateVarArgs[0]= contactUsForm.getId();
                   emailTemplateVarArgs[1]= contactUsForm.getName();
                   emailTemplateVarArgs[2]= contactUsForm.getEmail();
                   emailTemplateVarArgs[3]= contactUsForm.getMessage();
                   emailTemplateVarArgs[4]= contactUsForm.getInquiryType();
                   emailTemplateVarArgs[5]= contactUsForm.getInformNews();
                   emailTemplateVarArgs[6]= contactUsForm.getInquiryTime();
                   emailSettings.setTemplateParametersByVarArgs(emailTemplateVarArgs);
                   /*LOG.info("to addrss:"+emailSettings.getToaddress());
                   LOG.info("tempalte is :"+emailSettings.getTemplate());
                   */status = emailService.sendEmail(emailSettings);
               }
           }
        }
        return status;
    }

    private boolean sendContactUSForm2Email(ResourceResolver resourceResolver, String nodePath, String contactsNodeName
            , String email, String productInterest, String inquiryType, String... emailTemplateVarArgs) {
        LOG.info("In sendEmail"+emailService);
        EmailSettings emailSettings = emailService.getEmailSettingsFromNode(resourceResolver, nodePath);
        emailSettings.setFromAddress(email);
        LOG.info("emailSettings.....:"+emailSettings.getFromAddress());

        //add more toAddress based on user selection of inquiry type and product Interest
        List<String> toAddressList = new ArrayList<String>();

        String[] productInterestArray = productInterest.split(DELIMETER);
        String contactNodePath = nodePath + "/" + contactsNodeName;

        LOG.info("contactNodePath....:"+contactNodePath);

        String productInt = null;

        if(productInterestArray.length > 1){
            productInt = ContactService.KEYWORD_MULTIPLE_PRODUCTS;
        }else if(productInterestArray.length > 0){
            productInt = productInterestArray[0];
        }
        LOG.info("@@@@@@@@@Before getContactDetails");
        List<ContactDetails> contactDetailsList = null;
        if(contactService != null){
            LOG.info("inside the if");
            contactDetailsList = contactService.getContactDetails
                    (resourceResolver, contactNodePath, inquiryType, productInt);
        }
        LOG.info("contactDetailsList:"+contactDetailsList);
        LOG.info("contactDetailsList  size :"+contactDetailsList.size());

        if(contactDetailsList != null && contactDetailsList.size() > 0){
            LOG.info("inside if");
            for(ContactDetails contactDetails : contactDetailsList){
                LOG.info("contactDetails:"+contactDetails.getPhone());
                LOG.info("contactDetails:"+contactDetails.getEmail());
                if(contactDetails.getEmail() != null){
                    toAddressList.add(contactDetails.getEmail());
                    LOG.info("contact email :"+contactDetails.getEmail());
                }
            }
        }
        LOG.info("toAddressList:"+toAddressList);
        if(emailSettings.getToaddress() != null && emailSettings.getToaddress().length > 0){
            toAddressList.addAll(Arrays.asList(emailSettings.getToaddress()));
        }
        emailSettings.setToaddress(toAddressList.toArray(new String[0]));
        emailSettings.setTemplateParametersByVarArgs(emailTemplateVarArgs);
        LOG.info("emailTemplateVarArgs:"+emailTemplateVarArgs);

        boolean success = false;
        if(toAddressList != null && toAddressList.size()>0){
            success = emailService.sendEmail(emailSettings);
        }
        return success;
    }

}
