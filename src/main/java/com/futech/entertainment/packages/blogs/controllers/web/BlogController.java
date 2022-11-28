package com.futech.entertainment.packages.blogs.controllers.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class BlogController {
    
    @Autowired
    private BlogCateServiceInterface blogCateServiceInterface;
    @Autowired
    private BlogServiceInterface blogServiceInterface;

    private int itemPerPage = 3;
    private String startPoint = "0";
    private String[] limits = {startPoint, String.valueOf(itemPerPage)};

    @GetMapping("/articles/{cate}")
    public String blog(HttpServletRequest request, Model model, @PathVariable String cate, @RequestParam String page) throws IOException {
        try {
            var cateList = this.blogCateServiceInterface.getBlogCategories();
            cateList.sort(Comparator.comparing(c -> String.valueOf(c.get("name"))));
            model.addAttribute("cates", cateList);

            String orderBy = "blogs.created_at desc";
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "blog_cates.url_slug", "=", cate, "and"));
            String[] limit = {String.valueOf(Integer.parseInt(page)*itemPerPage-itemPerPage), String.valueOf(itemPerPage)};
            var blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, Integer.parseInt(page) == 1 ? limits : limit);
            
            conditions.clear();
            conditions.add(DataMapper.getInstance("", "blog_cates.url_slug", "=", cate, "and"));
            var allBlogs = (double)this.blogServiceInterface.countBlogs(conditions);
            System.out.println("allBlogs: "+allBlogs);
            if(blogs.size() > 0){
                model.addAttribute("blogs", blogs);
                model.addAttribute("itemPerPage", itemPerPage);
                model.addAttribute("totalPages", (int)Math.ceil(allBlogs/itemPerPage));
            }
            model.addAttribute("currentCate", cate);
            model.addAttribute("articleURL", request.getRequestURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "blogs/blogs";
    }

    @GetMapping("/articles")
    public ResponseEntity<List<Map<String, Object>>> blogPagination(@RequestParam String data) {
        List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            System.out.println(data);
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            var cateId = json.get("cateId").getAsString();
            String[] limit = {json.get("offset").getAsString(), String.valueOf(itemPerPage)};

            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "id", "=", cateId, "and"));
            var cate = this.blogCateServiceInterface.getBlogCategory(conditions);
            conditions.clear();
            conditions.add(DataMapper.getInstance("", "blog_cates.id", "=", cateId, "and"));
            String orderBy = "blogs.created_at desc";
            blogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limit);

            conditions.clear();
            conditions.add(DataMapper.getInstance("", "blog_cates.id", "=", cateId, "and"));
            var allBlogs = (double)this.blogServiceInterface.countBlogs(conditions);
            if(blogs.size() == 0 || allBlogs == 0){
                obj.put("code", 404);
                obj.put("message", "Not found");
                obj.put("cateSlug", cate.get("url_slug"));
                blogs.add(obj);
            }
            obj.put("total", allBlogs);
            obj.put("totalPages", (int)Math.ceil(allBlogs/itemPerPage));
            blogs.add(0, obj);
            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            blogs.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(blogs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/articles/detail/{cate}/{url}-{id}")
    public String blogDetail(@PathVariable String cate, @PathVariable String url, @PathVariable String id, Model model, HttpServletRequest request){
        try {
            System.out.println("cate: "+cate);
            System.out.println("url: "+url);
            System.out.println("id: "+id);
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "blogs.id", "=", id, "and"));
            var blog = this.blogServiceInterface.getBlog(conditions);
            conditions.clear();
            //conditions.add(DataMapper.getInstance("", "blogs.is_featured", "=", "1", "and"));
            String orderBy = "blogs.created_at desc";
            String[] limits = {"1"};
            var featuredBlogs = this.blogServiceInterface.getBlogs(conditions, orderBy, limits);
            model.addAttribute("blog", blog);
            model.addAttribute("featuredBlogs", featuredBlogs);
            model.addAttribute("articleURL", request.getRequestURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "blogs/blog_detail";
    }

}
