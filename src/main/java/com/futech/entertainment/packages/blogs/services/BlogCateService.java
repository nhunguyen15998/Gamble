package com.futech.entertainment.packages.blogs.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Service
public class BlogCateService extends BaseService<BlogCate> implements BlogCateServiceInterface {
    public String STATUS_DEACTIVATED = "0";
    public String STATUS_ACTIVATED = "1";

    // crud
    public List<Map<String, Object>> getBlogCategories(List<DataMapper> conditions){
        try {
            conditions.add(DataMapper.getInstance("", "status", "=", this.STATUS_ACTIVATED, ""));
            return this.getAll(null, conditions, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Map<String, Object> getBlogCategory(List<DataMapper> conditions){
        try {
            conditions.add(DataMapper.getInstance("", "status", "=", this.STATUS_ACTIVATED, ""));
            return this.getFirstBy(null, conditions, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //end crud

}
