package com.futech.entertainment.packages.blogs.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

public interface BlogCateServiceInterface extends BaseServiceInterface<BlogCate>{
    public List<Map<String, Object>> getBlogCategories(List<DataMapper> conditions);
    public Map<String, Object> getBlogCategory(List<DataMapper> conditions);
}
