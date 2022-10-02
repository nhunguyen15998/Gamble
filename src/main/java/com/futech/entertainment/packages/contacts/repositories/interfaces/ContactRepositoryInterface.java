package com.futech.entertainment.packages.contacts.repositories.interfaces;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.contacts.models.Contact;
import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;

@Component
public interface ContactRepositoryInterface extends BaseRepositoryInterface<Contact> {
    
}
