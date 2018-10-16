package com.apddemo.core.daos;

import com.apddemo.core.models.ContactUsForm;

import java.util.List;


public interface ContactUsDao {

	String insertForm1Data(ContactUsForm contactUsForm);

    boolean insertForm2Data(ContactUsForm contactUsForm);

    List<ContactUsForm> getContactUSForm1SubmitDetail();

    Boolean updateContactUSForm1SubmitDetails(List<ContactUsForm> contactUsFormList);
}
