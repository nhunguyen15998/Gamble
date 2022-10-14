package com.futech.entertainment.packages.blogs.controllers.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.blogs.modelMappers.BlogCateMapper;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.FileUploadUtil;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class BlogCategoriesManaController {
 @Autowired
 BlogCateServiceInterface blogCateServiceInterface;
    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.ACTIVATED, "Activated");
        statusList.put(Helpers.DEACTIVATED, "Deactivated");

        return statusList;
    }

    @GetMapping("/admin/blog-categories/all")
    public String ViewUser(Model mdl){
        mdl.addAttribute("blogCates",LoadData(1,"", 10) );
        mdl.addAttribute("paging", RowEvent(GetCount(""),10));
        return "blogcategories/administrator/all-blog-categories";
    } 

    @GetMapping("/admin/blog-categories/create")
    public String showCreateForm(Model mdl){
        mdl.addAttribute("blogCateMapper", new BlogCateMapper());
        mdl.addAttribute("formType", 0);
        return "blogcategories/administrator/create-update";
    }

    @PostMapping("/admin/blog-categories/create")
    public String CreateUser(@Valid @ModelAttribute("blogCateMapper") BlogCateMapper blogCateMapper,BindingResult bindingResult,RedirectAttributes redirAttrs,Model model)  
    {
        try {
            
            if(bindingResult.hasErrors()){
                model.addAttribute("blogCateMapper", blogCateMapper);
                model.addAttribute("formType", 0);
                return "blogcategories/administrator/create-update";
            }
            
            boolean sent = this.blogCateServiceInterface.createBlog(blogCateMapper);
            if(sent){
                redirAttrs.addFlashAttribute("successMsg", String.format("Saved successfully!"));
                return "redirect:/admin/blog-categories/all";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/admin/blog-categories/all";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/blog-categories/all";
        }
    }
    @GetMapping("/admin/blog-categories/update")
    public String showUpdateForm(@RequestParam int id, Model model, RedirectAttributes atts)
    {
        try{
            model.addAttribute("blogCateMapper", getBlogCateMapperByID(id));
            model.addAttribute("formType", 1);
            return  "blogcategories/administrator/create-update";
        } catch(Exception ex){
            atts.addFlashAttribute("error", ex.getMessage());
            return "/admin/blog-categories/all";

        }
    }
    @PostMapping("/admin/blog-categories/update")
    public String updateUser(@Valid @ModelAttribute("blogCateMapper") BlogCateMapper blogCateMapper, BindingResult bindingResult, RedirectAttributes atts, Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", blogCateMapper);
                model.addAttribute("formType", 0);
                return "blogcategories/administrator/create-update";
            }
            
            boolean updated = blogCateServiceInterface.updateBlogCate(blogCateMapper);
            if(updated){
                atts.addFlashAttribute("successMsg", "Successfully update user");
            } else {
                atts.addFlashAttribute("errorMsg", "Cannot update user");
            }
        } catch(Exception ex) {
            atts.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/blog-categories/all";
    }
    public BlogCateMapper getBlogCateMapperByID(int id){
        String[] selects = {"id, name,url_slug, status"};
            var u = blogCateServiceInterface.getBlogCateById(selects,id);
            BlogCateMapper mapper=new BlogCateMapper(
               Integer.parseInt(u.get("id").toString()),
                u.get("name").toString(),
                u.get("url_slug").toString(),
                Integer.parseInt(u.get("status").toString())
            );
            return mapper;
    }
 //Pagination
 @PostMapping("/admin/blog-caterogies/all/LoadDataBlogCate")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataBlogCate(int p, String cond, int take)
 {
     List<Map<String,Object>> emps = LoadData(p, cond, take);
     return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadData(int p, String cond, int take)
 {
        int currentSkip = take * (p - 1);
        //select
        String[] selects = {"id, name, DATE_FORMAT(created_at,'%m-%d-%Y %H:%i') as created_at,url_slug, status"};
       //limit
        String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance("", "name", "like", "%"+cond+"%", ""));
        }
        var u = blogCateServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "blog_cates.id desc", limit);
        return u;
 }

@PostMapping("/admin/blog-categories/all/GetCount")
 public @ResponseBody int GetCount(String cond)
 {
     //select
     String[] selects = {"bc.id, bc.name, DATE_FORMAT(blog-categories.created_at,'%m-%d-%Y %H:%i') as created_at, bc.status"};
     //condition
     List<DataMapper> lsCond = new ArrayList<DataMapper>();
      if(!cond.isEmpty()&&cond!=null){
          lsCond.add(DataMapper.getInstance("", "bc.name", "like", "%"+cond+"%", ""));
      }
      var u = blogCateServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "bc.id desc", null);
      
      return u!=null?u.size():0;
 }
@PostMapping("/admin/blog-categories/all/RowEvent")
 public @ResponseBody  int RowEvent(int i, int take)
 {
     double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
     double temp = pagi - Math.floor(pagi);
     return (int)(pagi+(temp>0?1:0));
     
 }
 //End pagination

}
