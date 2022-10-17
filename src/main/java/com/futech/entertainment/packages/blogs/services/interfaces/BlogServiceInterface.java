package com.futech.entertainment.packages.blogs.services.interfaces;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.futech.entertainment.packages.blogs.modelMappers.BlogMapper;
import com.futech.entertainment.packages.blogs.models.Blog;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

public interface BlogServiceInterface extends BaseServiceInterface<Blog> {
    public int countBlogs(List<DataMapper> conditions);
    public List<Map<String, Object>> getBlogs(List<DataMapper> conditions, String orderBy, String[] limits);
    public Map<String, Object> getBlog(List<DataMapper> conditions);
    public Map<String, Object> getBlogById(String[] selects, int id);
    public boolean createBlog(BlogMapper blogMapper);
    public boolean updateBlog(BlogMapper blogMapper);
}
