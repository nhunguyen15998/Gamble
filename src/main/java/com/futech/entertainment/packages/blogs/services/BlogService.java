package com.futech.entertainment.packages.blogs.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.blogs.models.Blog;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;

@Service
public class BlogService extends BaseService<Blog> implements BlogServiceInterface {
    public String STATUS_DEACTIVATED = "0";
    public String STATUS_ACTIVATED = "1";

    //crud
    public List<Map<String, Object>> getBlogs(List<DataMapper> conditions, String orderBy){
        try {
            String[] selects = {"blog_cates.*, blogs.*"};
            conditions.add(DataMapper.getInstance("", "blog_cates.status", "=", this.STATUS_ACTIVATED, ""));
            conditions.add(DataMapper.getInstance("and", "blogs.status", "=", this.STATUS_ACTIVATED, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates", DataMapper.getInstance("", "blog_cates.id", "=", "blogs.blog_cate_id", "")));
            return this.getAll(selects, conditions, joins, null, orderBy, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getBlog(List<DataMapper> conditions){
        try {
            String[] selects = {"blog_cates.*, blogs.*"};
            conditions.add(DataMapper.getInstance("", "blog_cates.status", "=", this.STATUS_ACTIVATED, ""));
            conditions.add(DataMapper.getInstance("and", "blogs.status", "=", this.STATUS_ACTIVATED, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates", DataMapper.getInstance("", "blog_cates.id", "=", "blogs.blog_cate_id", "")));
            return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //end crud
}
