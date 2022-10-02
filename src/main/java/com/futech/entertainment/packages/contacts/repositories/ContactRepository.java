package com.futech.entertainment.packages.contacts.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.contacts.models.Contact;
import com.futech.entertainment.packages.contacts.repositories.interfaces.ContactRepositoryInterface;
import com.futech.entertainment.packages.core.repositories.BaseRepository;

@Repository
public class ContactRepository extends BaseRepository<Contact> implements ContactRepositoryInterface {
    public ContactRepository(){
        super();
        this.setModel(new Contact());
    }
}
