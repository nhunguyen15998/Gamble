package com.futech.entertainment.packages.contacts.services.interfaces;

import com.futech.entertainment.packages.contacts.modelMappers.ContactMapper;
import com.futech.entertainment.packages.contacts.modelMappers.ContactSentMapper;
import com.futech.entertainment.packages.contacts.models.Contact;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;

public interface ContactServiceInterface extends BaseServiceInterface<Contact>{
    public Contact clientCreateContact(ContactMapper contactMapper);
    public boolean updateContact( ContactSentMapper contact);
    public ContactSentMapper getContactByCaseNumber(String caseNumber);
    public int getCountPending();
}
