package com.futech.entertainment.packages.blogs.controllers.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Controller
public class BlogController {
    
    @Autowired
    private BlogCateServiceInterface blogCateServiceInterface;
    @Autowired
    private BlogServiceInterface blogServiceInterface;

    @GetMapping("/articles")
    public String blog(HttpServletRequest request, Model model) throws IOException {
        try {
            String[] selects = {"id, name, status"};
            var cates = this.blogCateServiceInterface.getBlogCategories(selects);
            model.addAttribute("cates", cates);
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            String orderBy = "blogs.created_at desc";
            var blogs = this.blogServiceInterface.getBlogs(conditions, orderBy);
            model.addAttribute("blogs", blogs);
            model.addAttribute("articleURL", request.getRequestURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "blogs/blogs";
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
            model.addAttribute("blog", blog);
            model.addAttribute("articleURL", request.getRequestURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "blogs/blog_detail";
    }

}
