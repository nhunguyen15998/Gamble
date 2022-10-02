package com.futech.entertainment.packages.contacts.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Email;

public class ContactMapper {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Message is required")
    private String message;

    public ContactMapper() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
}
