package com.futech.entertainment.packages.contacts.controllers.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.contacts.modelMappers.ContactMapper;
import com.futech.entertainment.packages.contacts.services.interfaces.ContactServiceInterface;

@Controller
public class ContactController {
    
    @Autowired
    private ContactServiceInterface contactServiceInterface;

    @GetMapping("/contact-us")
    public String contact(HttpServletRequest request, Model model) throws IOException {
        model.addAttribute("contactMapper", new ContactMapper());
        model.addAttribute("contactURL", request.getRequestURL());
        return "contacts/contact";
    }
    
    @PostMapping("/contact/send-message")
    public String sendContact(@Valid @ModelAttribute("contactMapper") ContactMapper contactMapper, BindingResult bindingResult, 
                              RedirectAttributes redirAttrs, Model model, HttpServletRequest request){
        try {
            model.addAttribute("contactURL", request.getRequestURL());
            if(bindingResult.hasErrors()){
                model.addAttribute("contactMapper", contactMapper);
                return "contacts/contact";
            }
            var contact = this.contactServiceInterface.clientCreateContact(contactMapper);
            if(contact != null){
                redirAttrs.addFlashAttribute("successMsg", "Your message has been sent");
                return "redirect:/contact-us";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/contact-us";
        } catch (Exception e) {
            e.printStackTrace();
            return "contacts/contact";
        }
    }
    
}
