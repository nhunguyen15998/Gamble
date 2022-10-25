package com.futech.entertainment.packages.blogs.controllers.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.futech.entertainment.packages.blogs.modelMappers.BlogMapper;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.FileUploadUtil;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class BlogManaController {
    @Autowired
    BlogServiceInterface blogServiceInterface;
    @Autowired
    BlogCateServiceInterface blogCateServiceInterface;
    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.ACTIVATED, "Activated");
        statusList.put(Helpers.DEACTIVATED, "Deactivated");

        return statusList;
    }
   
    public Map<Integer, String> getBlogCateList(int blog_cate_id) {
        String[] selects = {"id, name"};
        List<DataMapper> cond = new ArrayList<DataMapper>();
        cond.add(DataMapper.getInstance("", "status", "=", "1", blog_cate_id!=0?" or id = "+blog_cate_id:""));
        var u = blogCateServiceInterface.getAll(selects, cond, null, null, "id desc", null);
        Map<Integer, String>  blogCateList = new HashMap<Integer, String>();
        if(u.size()>0){
            for (var blog : u) 
                blogCateList.put(Integer.parseInt(blog.get("id").toString()), blog.get("name").toString());
        }
        return  blogCateList;
    }

    @GetMapping("/admin/blogs/all")
    public String ViewUser(Model mdl){
        mdl.addAttribute("blogs",LoadData(1,Helpers.EMPTY, 10) );
        mdl.addAttribute("paging", RowEvent(GetCount(Helpers.EMPTY),10));
        return "blogs/administrator/all-blogs";
    } 

    @GetMapping("/admin/blogs/create")
    public String showCreateForm(Model mdl){
        mdl.addAttribute("blogMapper", new BlogMapper());
        mdl.addAttribute("blogCateList",getBlogCateList(0));
        mdl.addAttribute("formType", 0);
        return "blogs/administrator/create-update";
    }

    @PostMapping("/admin/blogs/create")
    public String CreateBlog(@Valid @ModelAttribute("blogMapper") BlogMapper blogMapper,BindingResult bindingResult,RedirectAttributes redirAttrs,Model model, HttpSession session,@RequestParam("pathImg") MultipartFile multipartFile)  throws IOException {
    
        try {
            if(multipartFile.getOriginalFilename().isEmpty())  bindingResult.addError(new FieldError("blogMapper", "thumbnail", "Thumbnail is required"));
            
            if(bindingResult.hasErrors()){
                model.addAttribute("blogMapper", blogMapper);
                model.addAttribute("formType", 0);
                model.addAttribute("blogCateList", getBlogCateList(0));

                return "blogs/administrator/create-update";
            }
            if(!multipartFile.getOriginalFilename().isEmpty()){
                String fileName = Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                blogMapper.setThumbnail(fileName);
                FileUploadUtil.saveFile("src/main/resources/static/images/blogs/", fileName, multipartFile);
            }
            if(session.getAttribute("user_id")==null) return "redirect:/user/sign-in";
            blogMapper.setAuthor_id(Integer.parseInt(session.getAttribute("user_id").toString()));
            boolean sent = this.blogServiceInterface.createBlog(blogMapper);
            if(sent){
                redirAttrs.addFlashAttribute("successMsg", String.format("Saved successfully!"));
                return "redirect:/admin/blogs/all";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/admin/blogs/all";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/blogs/all";
        }
    }
    @GetMapping("/admin/blogs/update")
    public String showUpdateForm(@RequestParam int id, Model model, RedirectAttributes atts)
    {
        try{
            BlogMapper temp= getBlogMapperByID(id);
            model.addAttribute("blogMapper",temp);
            model.addAttribute("formType", 1);
            model.addAttribute("blogCateList", getBlogCateList(temp.getBlog_cate_id()));
            return "blogs/administrator/create-update";
        } catch(Exception ex){
            atts.addFlashAttribute("error", ex.getMessage());
            return "/admin/blogs/all";

        }
    }
    @PostMapping("/admin/blogs/update")
    public String updateBlog(@Valid @ModelAttribute("blogMapper") BlogMapper blogMapper,Model model, BindingResult bindingResult, RedirectAttributes atts,@RequestParam("pathImg") MultipartFile multipartFile)  throws IOException {
        try {
            
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", blogMapper);
                atts.addAttribute("formType", 0);
                atts.addAttribute("blogCateList", getBlogCateList(blogMapper.getId()));

                return "blogs/administrator/create-update";
            }
            if(!multipartFile.getOriginalFilename().isEmpty()){
                FileUploadUtil.deleteFile(getBlogMapperByID(blogMapper.getId()).getThumbnail());
                String fileName = Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                blogMapper.setThumbnail(fileName);
                FileUploadUtil.saveFile("src/main/resources/static/images/blogs/", fileName, multipartFile);
            }

            boolean updated = this.blogServiceInterface.updateBlog(blogMapper);
            if(updated){
                atts.addFlashAttribute("successMsg", "Successfully update blog");
            } else {
                atts.addFlashAttribute("errorMsg", "Cannot update blog");
            }
        } catch(Exception ex) {
            atts.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/blogs/all";
    }
    public BlogMapper getBlogMapperByID(int id){
        String[] selects = {"blogs.id,title,blogs.url_slug, content,description,blogs.thumbnail, author_id,blog_cate_id, DATE_FORMAT(blogs.created_at,'%d-%m-%Y %H:%i') as created_at, blogs.status,"
        +"bc.name as blogCate,  concat(p.first_name,' ',p.last_name) as author_name"};
            var u = blogServiceInterface.getBlogById(selects,id);
            BlogMapper mapper=new BlogMapper(
               Integer.parseInt(u.get("id").toString()),
               u.get("title").toString(),
                u.get("content").toString(),
                Integer.parseInt(u.get("blog_cate_id").toString()),
                u.get("blogCate").toString(),
                Integer.parseInt(u.get("author_id").toString()),
                u.get("author_name").toString(),
                u.get("url_slug").toString(),
                Integer.parseInt(u.get("status").toString()),
                u.get("thumbnail")==null?"":u.get("thumbnail").toString(),
                u.get("description").toString()
            );
            return mapper;
    }
 //Pagination
 @PostMapping("/admin/blogs/all/LoadDataBlog")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataBlog(int p, String cond, int take)
 {
     List<Map<String,Object>> objs = LoadData(p, cond, take);
     return new ResponseEntity<List<Map<String,Object>>>(objs, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadData(int p, String cond, int take)
 {
        int currentSkip = take * (p - 1);
        //select
        String[] selects = {"blogs.id,title,blogs.url_slug, content,description,blogs.thumbnail, author_id,blog_cate_id, DATE_FORMAT(blogs.created_at,'%d-%m-%Y %H:%i') as created_at, blogs.status,"
        +"bc.name as blogCate,  concat(p.first_name,' ',p.last_name) as author_name"};
       //join
        List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
        lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "author_id", "=", "p.user_id", "")));
        lsJoin.add(JoinCondition.getInstance("left join", "blog_cates bc", DataMapper.getInstance("", "blog_cate_id", "=", "bc.id", "")));
       //limit
        String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance(" title like '%"+cond+"%' or ", "p.first_name", "like", "%"+cond+"%", "or p.last_name like '%"+cond+"%' or bc.name like '%"+cond+"%'"));
        }
        var u = blogServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, lsJoin, null, "blogs.id desc", limit);
        return u;
 }

@PostMapping("/admin/blogs/all/GetCount")
 public @ResponseBody int GetCount(String cond)
 {
    String[] selects = {"blogs.id,title,blogs.url_slug, content,description,blogs.thumbnail, author_id,blog_cate_id, DATE_FORMAT(blogs.created_at,'%d-%m-%Y %H:%i') as created_at, blogs.status,"
    +"bc.name as blogCate,  concat(p.first_name,' ',p.last_name) as author_name"};
    //join
    List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
    lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "author_id", "=", "p.user_id", "")));
    lsJoin.add(JoinCondition.getInstance("left join", "blog_cates bc", DataMapper.getInstance("", "blog_cate_id", "=", "bc.id", "")));
   
    //condition
    List<DataMapper> lsCond = new ArrayList<DataMapper>();
    if(!cond.isEmpty()&&cond!=null){
    lsCond.add(DataMapper.getInstance(" title like '%"+cond+"%' or ", "p.first_name", "like", "%"+cond+"%", "or p.last_name like '%"+cond+"%' or bc.name like '%"+cond+"%'"));
    }
    var u = blogServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, lsJoin, null, null, null);
    return u==null?0:u.size();
 }

@PostMapping("/admin/blogs/all/RowEvent")
 public @ResponseBody  int RowEvent(int i, int take)
 {
     double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
     double temp = pagi - Math.floor(pagi);
     return (int)(pagi+(temp>0?1:0));
     
 }
 //End pagination

}
