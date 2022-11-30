package com.futech.entertainment.packages.blogs.controllers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.middlewares.auth.interfaces.Authentication;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Validated
@RestController
@RequestMapping(path = "/api")
public class BlogsController {
    @Autowired
    private BlogCateServiceInterface blogCateServiceInterface;
    @Autowired
    private BlogServiceInterface blogServiceInterface;

    private int itemPerPage = 3;
    private String startPoint = "0";
    private String[] limits = {startPoint, String.valueOf(itemPerPage)};

    //blog cates
    @CrossOrigin
    @GetMapping("/article/cates/blogs")
    public ResponseEntity<List<Map<String, Object>>> getBlogCates(){//@RequestHeader("limit") Optional<String[]> limit){
        List<Map<String, Object>> cateBlogs = new ArrayList<Map<String, Object>>();
        try {
            var cates = this.blogCateServiceInterface.getBlogCategories();
            for (var cate : cates) {
                var cateId = cate.get("id").toString();
                var cateName = cate.get("name").toString();
                List<DataMapper> conditions = new ArrayList<DataMapper>();
                conditions.add(DataMapper.getInstance("", "blog_cates.id", "=", cateId, "and"));
                String orderBy = "blogs.created_at desc";
                var blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limits);//.isPresent() ? limit.get() : null);

                if(blogs.size() > 0){
                    Map<String, Object> list = new HashMap<String, Object>();
                    list.put("cateId", cateId);
                    list.put("cateName", cateName);
                    list.put("blogs", blogs);
                    cateBlogs.add(list);
                }
            }
            return new ResponseEntity<List<Map<String, Object>>>(cateBlogs, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> obj = new HashMap<String, Object>();
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            cateBlogs.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(cateBlogs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //latest blogs
    @CrossOrigin
    @GetMapping("/article/latest-blogs")
    public ResponseEntity<List<Map<String, Object>>> getLatestBlogs(){//@RequestHeader("limit") Optional<String[]> limit){
        List<Map<String, Object>> cates = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            String orderBy = "blogs.created_at";
            cates = this.blogServiceInterface.getBlogs(conditions, orderBy, limits);//.get().length > 0 ? limit.get() : new String[]{"0","3"});
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            cates.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //blogs by cates 
    @CrossOrigin
    @GetMapping("/articles")
    public ResponseEntity<List<Map<String, Object>>> getBlogs(@RequestParam Optional<String> cateId, @RequestParam String page){
        System.out.println("page:"+page);
        List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            System.out.println(cateId.get());
            if(cateId.get() != ""){
                conditions.add(DataMapper.getInstance("", "blog_cates.id", "=", cateId.get(), "and"));
            }
            String orderBy = "blogs.created_at desc";
            itemPerPage = 4;
            String[] limit = {String.valueOf(Integer.parseInt(page)*itemPerPage-itemPerPage), String.valueOf(itemPerPage)};
            blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limit);

            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            blogs.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //blog by id
    @CrossOrigin
    @GetMapping("/articles/detail")
    public ResponseEntity<Map<String, Object>> getBlog(@RequestParam String blogId){
        Map<String, Object> blog = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "blogs.id", "=", blogId, "and"));
            blog = this.blogServiceInterface.getBlog(conditions);

            return new ResponseEntity<Map<String, Object>>(blog, HttpStatus.OK);
        } catch (Exception e) {
            blog.put("code", 500);
            blog.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //search
    @CrossOrigin
    @GetMapping("/articles/search-results")
    public ResponseEntity<List<Map<String, Object>>> searchBlog(@Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers, @RequestParam String search, @RequestParam String page){
        List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("(", "blogs.title", "like", "%"+search.trim()+"%", "or"));
            conditions.add(DataMapper.getInstance("", "blog_cates.name", "like", "%"+search.trim()+"%", ") and"));
            String orderBy = "blogs.created_at desc";
            itemPerPage = 4;
            String[] limit = {String.valueOf(Integer.parseInt(page)*itemPerPage-itemPerPage), String.valueOf(itemPerPage)};
            blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limit);

            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 400);
            obj.put("message", e.getMessage());
            blogs.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> onValidationError(ConstraintViolationException e) {
        Map<String, Object> errors = new HashMap<String,Object>();
        errors.put("code", 400);
        errors.put("message", e.getMessage().split(": ")[1]);
        return new ResponseEntity<Map<String, Object>>(errors, HttpStatus.BAD_REQUEST);
    }

}
