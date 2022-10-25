package com.futech.entertainment.packages.blogs.services.interfaces;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.futech.entertainment.packages.blogs.modelMappers.BlogCateMapper;
import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

public interface BlogCateServiceInterface extends BaseServiceInterface<BlogCate>{
    public List<Map<String, Object>> getBlogCategories();
    public Map<String, Object> getBlogCategory(List<DataMapper> conditions);
    public boolean createBlog(BlogCateMapper blogCateMapper);
    public  Map<String, Object> getBlogCateById(String[] selects, int id);
    public boolean updateBlogCate(BlogCateMapper blogCateMapper);
}
