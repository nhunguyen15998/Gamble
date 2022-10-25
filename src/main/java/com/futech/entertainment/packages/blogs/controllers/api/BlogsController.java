package com.futech.entertainment.packages.blogs.controllers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

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
    @GetMapping("/article/cates")
    public ResponseEntity<List<Map<String, Object>>> getBlogCates(){
        List<Map<String, Object>> cates = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            cates = this.blogCateServiceInterface.getBlogCategories();
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            cates.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //latest blogs
    @GetMapping("/article/latest-blogs")
    public ResponseEntity<List<Map<String, Object>>> getLatestBlogs(@RequestParam Optional<String[]> limit){
        List<Map<String, Object>> cates = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            String orderBy = "blogs.created_at";
            cates = this.blogServiceInterface.getBlogs(conditions, orderBy, limit.get().length > 0 ? limit.get() : new String[]{"0","3"});
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            cates.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(cates, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //blogs by cates 
    @GetMapping("/articles")
    public ResponseEntity<List<Map<String, Object>>> getBlogs(@RequestParam Optional<String> cateId, @RequestParam Optional<String[]> limit) {
        List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            System.out.println(cateId.get());
            if(cateId.get() != ""){
                conditions.add(DataMapper.getInstance("", "blog_cates.id", "=", cateId.get(), "and"));
            }
            String orderBy = "blogs.created_at desc";
            blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limit.get().length > 0 ? limit.get() : null);

            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            blogs.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
