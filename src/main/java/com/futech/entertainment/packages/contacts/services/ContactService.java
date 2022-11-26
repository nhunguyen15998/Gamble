package com.futech.entertainment.packages.contacts.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.contacts.modelMappers.ContactMapper;
import com.futech.entertainment.packages.contacts.modelMappers.ContactSentMapper;
import com.futech.entertainment.packages.contacts.models.Contact;
import com.futech.entertainment.packages.contacts.services.interfaces.ContactServiceInterface;
import com.futech.entertainment.packages.core.models.EmailDetails;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.services.interfaces.EmailServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;

@Service
public class ContactService extends BaseService<Contact> implements ContactServiceInterface {
    @Autowired 
    private EmailServiceInterface emailServiceInterface;
    public int CONTACT_PENDING = 0;
    public int CONTACT_READ = 1;
    public int CONTACT_REPLIED = 2;

    public Contact clientCreateContact(ContactMapper contactMapper){
        try {
            Contact contact = new Contact();
            contact.setName(contactMapper.getName());
            contact.setEmail(contactMapper.getEmail());
            contact.setMessage(contactMapper.getMessage());
            contact.setCreated_at(LocalDateTime.now());
            contact.setCase_number(Helpers.randomStringDate());
            contact.setStatus(CONTACT_PENDING);
            return this.create(contact);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateContact(ContactSentMapper contact) {
        try {
            Contact upContact = new Contact();
            upContact.setId(contact.getId());
            upContact.setReply(contact.getReply());
            upContact.setStatus(contact.getStatus());
            var updated = this.update(upContact);
            int sent =-1;
            if(contact.getStatus()==2){
             sent = this.emailServiceInterface.sendMailWithAttachment(new EmailDetails(
                contact.getEmail(),
                "<p>Dear our beloved user,</p>"+
                "<br/>"+
                "<p>Thank you for contacting us when you have questions about this issue: "+ contact.getMessage()+ "</p>"+
                "<br/>"+ contact.getReply(),
                "Reply contact for case number #"+contact.getCase_number()
            ), true)==true?1:0;
            }
            if(updated && (sent==-1||sent==1)?true:false){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getCountPending(){
        List<DataMapper> conditions = new ArrayList<DataMapper>();
        conditions.add(DataMapper.getInstance("", "status", "=", "0", ""));
        return this.getCount(null, conditions, null);
           
    }
    public ContactSentMapper getContactByCaseNumber(String caseNumber){
        try {           
            String[] selects = {"id", "name", "email", "message", "reply", "created_at", "status","case_number"};
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "case_number", "=", caseNumber, ""));
            var e=  this.getFirstBy(selects, conditions, null);
            var c= new ContactSentMapper(Integer.parseInt(e.get("id").toString()), e.get("name").toString(), e.get("email").toString(),e.get("case_number").toString(), e.get("message").toString(), e.get("reply")==null?"":e.get("reply").toString(), LocalDateTime.parse(e.get("created_at").toString()), Integer.parseInt(e.get("status").toString()));
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
