package com.futech.entertainment.packages.core.services.interfaces;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.models.EmailDetails;

@Service
public interface EmailServiceInterface {
	boolean sendSimpleMail(EmailDetails details);
	boolean sendMailWithAttachment(EmailDetails details, boolean isHtml);
}
