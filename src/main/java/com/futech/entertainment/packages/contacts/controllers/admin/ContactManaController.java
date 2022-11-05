package com.futech.entertainment.packages.contacts.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.contacts.modelMappers.ContactSentMapper;
import com.futech.entertainment.packages.contacts.services.ContactService;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Controller
public class ContactManaController {
    @Autowired
    ContactService contactService;


       @GetMapping("/admin/contacts/all")
       public String ViewContact(Model mdl){
           mdl.addAttribute("contacts",LoadData(1,"", 10) );
           mdl.addAttribute("paging", RowEvent(GetCount(""),10));
           return "contacts/administrator/all-contact";
       } 

       @GetMapping("/admin/contacts/update")
       public String showUpdateForm(@RequestParam String caseNumber, Model model, RedirectAttributes atts)
       {
           try{
               var contact=contactService.getContactByCaseNumber(caseNumber);
               model.addAttribute("contactMapper",contact );
               if(contact.getStatus()==contactService.CONTACT_PENDING){contact.setStatus(contactService.CONTACT_READ); contactService.updateContact(contact);}
               model.addAttribute("formType", 1);
               return  "contacts/administrator/update";
           } catch(Exception ex){
               atts.addFlashAttribute("error", ex.getMessage());
               return "/admin/contacts/all";
   
           }
       }
       @PostMapping("/admin/contacts/update")
       public String updateContact(@Valid @ModelAttribute("contactMapper") ContactSentMapper contactMapper, BindingResult bindingResult, RedirectAttributes atts, Model model) {
           try {
                if(contactMapper.getReply().isBlank()){
                    bindingResult.addError(new FieldError("contactMapper","reply","Rreply content is required"));
                }
               if(bindingResult.hasErrors()){
                   model.addAttribute("contactMapper", contactMapper);
                   model.addAttribute("formType", 0);
                   return "contacts/administrator/update";
               }

               contactMapper.setStatus(contactService.CONTACT_REPLIED);
               boolean updated = contactService.updateContact(contactMapper);
               if(updated){
                   atts.addFlashAttribute("successMsg", "Email sent successfully");
               } else {
                   atts.addFlashAttribute("errorMsg", "Something went wrong");
               }
           } catch(Exception ex) {
               atts.addFlashAttribute("error", ex.getMessage());
           }
           return "redirect:/admin/contacts/all";
       }

       
    //Pagination
    @PostMapping("/admin/contacts/all/LoadDataContact")
    public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataContact(int p, String cond, int take)
    {
        List<Map<String,Object>> emps = LoadData(p, cond, take);
        return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
    }
   
    public List<Map<String,Object>>  LoadData(int p, String cond, int take)
    {
           int currentSkip = take * (p - 1);
           //select
           String[] selects = {"id", "name", "email", "message", "reply", "DATE_FORMAT(created_at,'%d-%m-%Y %H:%i') as created_at", "status","case_number"};
          //limit
           String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
          //condition
          List<DataMapper> lsCond = new ArrayList<DataMapper>();
           if(!cond.isEmpty()&&cond!=null){
               lsCond.add(DataMapper.getInstance("email like '%"+cond+"%' or", "name", "like", "%"+cond+"%", "or case_number like '%"+cond+"%'"));
           }
           var u = contactService.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "id desc", limit);
           return u;
    }
   
   @PostMapping("/admin/contacts/all/GetCount")
    public @ResponseBody int GetCount(String cond)
    {
        //select
        String[] selects = {"id", "name", "email", "message", "reply", "created_at", "status","case_number"};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance("email like '%"+cond+"%' or", "name", "like", "%"+cond+"%", "or case_number like '%"+cond+"%'"));
        }
        var u = contactService.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "id desc", null);
    
         return u!=null?u.size():0;
    }
   @PostMapping("/admin/contacts/all/RowEvent")
    public @ResponseBody  int RowEvent(int i, int take)
    {
        double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
        double temp = pagi - Math.floor(pagi);
        return (int)(pagi+(temp>0?1:0));
        
    }
    //End pagination
      
}
