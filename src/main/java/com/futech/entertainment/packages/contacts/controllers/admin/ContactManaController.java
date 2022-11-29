package com.futech.entertainment.packages.contacts.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
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
import com.futech.entertainment.packages.contacts.services.interfaces.ContactServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Controller
public class ContactManaController {
    @Autowired
    ContactService contactService;
    @Autowired 
    ContactServiceInterface contactServiceInterface;


       @GetMapping("/admin/contacts/all")
       public String ViewContact(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Contacts");

           mdl.addAttribute("contacts",LoadData(1,"", 10,-1) );
           mdl.addAttribute("paging", RowEvent(GetCount("",-1),10));
           return "contacts/administrator/all-contact";
       } 

       @GetMapping("/admin/contacts/update")
       public String showUpdateForm(@RequestParam String caseNumber, Model model, RedirectAttributes atts, HttpSession session)
       {
           try{
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
            session.setAttribute("title", "Update | Contacts"); 

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
                    bindingResult.addError(new FieldError("contactMapper","reply","Reply content is required"));
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
@PostMapping("/admin/contacts/getCountPending")
public @ResponseBody int GetCountPendingContact(){
    return contactServiceInterface.getCountPending();
}
       
    //Pagination
    @PostMapping("/admin/contacts/all/LoadDataContact")
    public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataContact(int p, String cond, int take, int status)
    {
        List<Map<String,Object>> emps = LoadData(p, cond, take, status);
        return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
    }
   
    public List<Map<String,Object>>  LoadData(int p, String cond, int take, int status)
    {
           int currentSkip = take * (p - 1);
           //select
           String[] selects = {"id", "name", "email", "message", "reply", "DATE_FORMAT(created_at,'%d-%m-%Y %H:%i') as created_at", "status","case_number"};
          //limit
           String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
          //condition
          List<DataMapper> lsCond = new ArrayList<DataMapper>();
          if(status!=-1) lsCond.add(DataMapper.getInstance("", "status", "=", String.valueOf(status),""));

           if(!cond.isEmpty()&&cond!=null){
               lsCond.add(DataMapper.getInstance((status!=-1?"and":"")+" email like '%"+cond+"%' or", "name", "like", "%"+cond+"%", "or case_number like '%"+cond+"%'"));
           }
           var u = contactService.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "id desc", limit);
           return u;
    }
   
   @PostMapping("/admin/contacts/all/GetCount")
    public @ResponseBody int GetCount(String cond, int status)
    {
        //select
        String[] selects = {"id", "name", "email", "message", "reply", "created_at", "status","case_number"};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
       if(status!=-1) lsCond.add(DataMapper.getInstance("", "status", "=", String.valueOf(status),""));

        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance((status!=-1?"and":"")+" email like '%"+cond+"%' or", "name", "like", "%"+cond+"%", "or case_number like '%"+cond+"%'"));
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
