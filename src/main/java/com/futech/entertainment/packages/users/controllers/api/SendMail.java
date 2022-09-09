package com.futech.entertainment.packages.users.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.core.models.EmailDetails;
import com.futech.entertainment.packages.core.services.EmailService;
import com.futech.entertainment.packages.core.utils.Helpers;

@RestController
// Class
public class SendMail {
 
    @Autowired 
    private EmailService emailService;
 
    // Plain Email
    @PostMapping("/sendMail")
    public String sendMail(@RequestBody EmailDetails details){
        boolean status = emailService.sendSimpleMail(details);
        if(status){
            return "Successfully sent";
        }
        return "Failed to send";
    }
 
    // HTML Email with attachment
    @PostMapping("/sendMailWithAttachment")
    public String sendMailWithAttachment(@RequestBody EmailDetails details){
        boolean status = emailService.sendMailWithAttachment(details, true);
        if(status){
            return "Successfully sent";
        }
        return "Failed to send";
    }

}
