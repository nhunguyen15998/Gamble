package com.futech.entertainment.packages.blogs.controllers.admin;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.futech.entertainment.packages.blogs.modelMappers.BlogCateMapper;
import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;

@Controller
public class BlogCategoriesManaController {
 @Autowired
 BlogCateServiceInterface blogCateServiceInterface;
 @Autowired
 BlogServiceInterface blogServiceInterface;
    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.ACTIVATED, "Activated");
        statusList.put(Helpers.DEACTIVATED, "Deactivated");

        return statusList;
    }

    @GetMapping("/admin/blog-categories/all")
    public String ViewBlog(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Blog categories");
        mdl.addAttribute("blogCates",LoadData(1,"", 10) );
        mdl.addAttribute("paging", RowEvent(GetCount(""),10));
        return "blogcategories/administrator/all-blog-categories";
    } 

    @GetMapping("/admin/blog-categories/create")
    public String showCreateForm(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "Create | Blog categories");

        mdl.addAttribute("blogCateMapper", new BlogCateMapper());
        mdl.addAttribute("formType", 0);
        return "blogcategories/administrator/create-update";
    }

    @PostMapping("/admin/blog-categories/create")
    public String CreateUser(@Valid @ModelAttribute("blogCateMapper") BlogCateMapper blogCateMapper,BindingResult bindingResult,RedirectAttributes redirAttrs,Model model)  
    {
        try {
            if(!blogCateMapper.getUrl_slug().isBlank()&&!blogCateServiceInterface.checkUrlSlug(blogCateMapper.getUrl_slug(), 0)) bindingResult.addError(new FieldError("blogMapper", "url_slug", "The blog category with this url slug already exists."));
            
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
    public String showUpdateForm(@RequestParam int id, Model model, RedirectAttributes atts, HttpSession session)
    {
        try{
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
            session.setAttribute("title", "Update | Blog categories");

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
            if(!blogCateMapper.getUrl_slug().isBlank()&&!blogCateServiceInterface.checkUrlSlug(blogCateMapper.getUrl_slug(), blogCateMapper.getId())) bindingResult.addError(new FieldError("blogMapper", "url_slug", "The blog category with this url slug already exists."));

            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", blogCateMapper);
                model.addAttribute("formType", 0);
                return "blogcategories/administrator/create-update";
            }
            
            boolean updated = blogCateServiceInterface.updateBlogCate(blogCateMapper);
            if(updated){
                atts.addFlashAttribute("successMsg", "Successfully update blog");
            } else {
                atts.addFlashAttribute("errorMsg", "Cannot update blog");
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
    @PostMapping("/admin/blog-categories/delete")
    public @ResponseBody int Delete(@RequestParam int id,HttpSession session){
        try {
            if(id>0) {
                List<DataMapper> lsCond = new ArrayList<DataMapper>();
                lsCond.add(DataMapper.getInstance("", "blog_cate_id", "=", ""+id, ""));
                int count = blogServiceInterface.getAll(null, lsCond, null, null, null, null).size();
                if(count == 0) blogCateServiceInterface.delete(id);
                return count > 0 ?  0 : 1;
            }
          return 0;
        } catch (Exception e) {
          return 0;
        }
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
        String[] selects = {"id, name, DATE_FORMAT(created_at,'%d-%m-%Y %H:%i') as created_at,url_slug, status"};
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
     String[] selects = {"id, name, DATE_FORMAT(created_at,'%d-%m-%Y %H:%i') as created_at, status"};
     //condition
     List<DataMapper> lsCond = new ArrayList<DataMapper>();
      if(!cond.isEmpty()&&cond!=null){
          lsCond.add(DataMapper.getInstance("", "name", "like", "%"+cond+"%", ""));
      }
      var u = blogCateServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "id desc", null);
      
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
