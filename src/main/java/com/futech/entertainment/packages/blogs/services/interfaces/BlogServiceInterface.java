package com.futech.entertainment.packages.blogs.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.blogs.models.Blog;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

public interface BlogServiceInterface extends BaseServiceInterface<Blog> {
    public List<Map<String, Object>> getBlogs(List<DataMapper> conditions, String orderBy);
    public Map<String, Object> getBlog(List<DataMapper> conditions);
}
