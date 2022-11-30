package com.futech.entertainment.packages.blogs.controllers.admin;

import java.io.IOException;
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
    public String ViewBlogs(Model mdl, HttpSession session, RedirectAttributes attr){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Blogs");

        mdl.addAttribute("blogs",LoadData(1,Helpers.EMPTY, 10) );
        mdl.addAttribute("paging", RowEvent(GetCount(Helpers.EMPTY),10));
        return "blogs/administrator/all-blogs";
    } 

    @GetMapping("/admin/blogs/create")
    public String showCreateForm(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
        session.setAttribute("title", "Create | Blogs");

        mdl.addAttribute("blogMapper", new BlogMapper());
        mdl.addAttribute("blogCateList",getBlogCateList(0));
        mdl.addAttribute("formType", 0);
        return "blogs/administrator/create-update";
    }

    @PostMapping("/admin/blogs/create")
    public String CreateBlog(@Valid @ModelAttribute("blogMapper") BlogMapper blogMapper,BindingResult bindingResult,RedirectAttributes redirAttrs,Model model, HttpSession session,@RequestParam("pathImg") MultipartFile multipartFile,@RequestParam String imgViewer)  throws IOException {
    
        try {
            
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
            blogMapper.setAuthor_id(Integer.parseInt(session.getAttribute("user_id").toString()));
            if(imgViewer.isEmpty())  bindingResult.addError(new FieldError("blogMapper", "thumbnail", "Thumbnail is required"));
            if(!blogMapper.getUrl_slug().isBlank()&&!blogServiceInterface.checkUrlSlug(blogMapper.getUrl_slug(), 0)) bindingResult.addError(new FieldError("blogMapper", "url_slug", "The blog with this url slug already exists."));
            if(bindingResult.hasErrors()){
                if(!imgViewer.isBlank()) blogMapper.setThumbnail(imgViewer);
                model.addAttribute("blogMapper", blogMapper);
                model.addAttribute("formType", 0);
                model.addAttribute("blogCateList", getBlogCateList(0));

                return "blogs/administrator/create-update";
            }
            if(!multipartFile.getOriginalFilename().isEmpty()){
                String fileName = Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                blogMapper.setThumbnail("/images/blogs/" +fileName);
                FileUploadUtil.saveFile("src/main/resources/static/images/blogs/", fileName, multipartFile);
            }
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
    public String showUpdateForm(@RequestParam int id, Model model, RedirectAttributes atts, HttpSession session)
    {
        try{
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
            session.setAttribute("title", "Update | Blogs");

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
    public String updateBlog(@Valid @ModelAttribute("blogMapper") BlogMapper blogMapper,BindingResult bindingResult,RedirectAttributes redirAttrs,Model model, HttpSession session,@RequestParam("pathImg") MultipartFile multipartFile,@RequestParam String imgViewer)  throws IOException {
        try {
            
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Boolean.parseBoolean(session.getAttribute("is_admin").toString())==false)) return "redirect:/user/sign-in";
            if(imgViewer.isEmpty())  bindingResult.addError(new FieldError("blogMapper", "thumbnail", "Thumbnail is required"));
            if(!blogMapper.getUrl_slug().isBlank()&&!blogServiceInterface.checkUrlSlug(blogMapper.getUrl_slug(), blogMapper.getId())) bindingResult.addError(new FieldError("blogMapper", "url_slug", "The blog with this url slug already exists."));
           
            if(bindingResult.hasErrors()){
                if(!imgViewer.isBlank()) blogMapper.setThumbnail(imgViewer);
                model.addAttribute("blogMapper", blogMapper);
                redirAttrs.addAttribute("formType", 1);
                model.addAttribute("blogCateList", getBlogCateList(blogMapper.getId()));

                return "blogs/administrator/create-update";
            }
            if(!multipartFile.getOriginalFilename().isEmpty()){
                FileUploadUtil.deleteFile(getBlogMapperByID(blogMapper.getId()).getThumbnail());
                String fileName = Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                blogMapper.setThumbnail("/images/blogs/"+fileName);
                FileUploadUtil.saveFile("src/main/resources/static/images/blogs/", fileName, multipartFile);
            }

            boolean updated = this.blogServiceInterface.updateBlog(blogMapper);
            if(updated){
                redirAttrs.addFlashAttribute("successMsg", "Successfully update blog");
            } else {
                redirAttrs.addFlashAttribute("errorMsg", "Cannot update blog");
            }
        } catch(Exception ex) {
            redirAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/blogs/all";
    }
    @PostMapping("/admin/blogs/delete")
    public @ResponseBody int Delete(@RequestParam int id,HttpSession session){
        try {
            BlogMapper blog = getBlogMapperByID(id);
            if(blog!=null) {
                if(getBlogMapperByID(blog.getId()).getThumbnail()!=null) FileUploadUtil.deleteFile(getBlogMapperByID(blog.getId()).getThumbnail());
                blogServiceInterface.delete(id);
            }
           return 1;

        } catch (Exception e) {
          return 0;
        }
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
