package com.futech.entertainment.packages.contacts.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.contacts.modelMappers.ContactMapper;
import com.futech.entertainment.packages.contacts.models.Contact;
import com.futech.entertainment.packages.contacts.services.interfaces.ContactServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;

@Service
public class ContactService extends BaseService<Contact> implements ContactServiceInterface {
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
            contact.setStatus(CONTACT_PENDING);
            return this.create(contact);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
