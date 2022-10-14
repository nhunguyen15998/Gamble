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
    public int countBlogs(List<DataMapper> conditions){
        try {
            String[] selects = {"blogs.*, blog_cates.*"};
            conditions.add(DataMapper.getInstance("", "blogs.status", "=", this.STATUS_ACTIVATED, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates", DataMapper.getInstance("", "blog_cates.id", "=", "blogs.blog_cate_id", "")));
            return this.getCount(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Map<String, Object>> getBlogs(List<DataMapper> conditions, String orderBy, String[] limits){
        try {
            String[] selects = {"blog_cates.id as cate_id, blogs.id as blog_id, blog_cates.created_at as cate_created_at, blogs.created_at as blog_created_at,"+
                                "blog_cates.url_slug as cate_slug, blogs.url_slug as blog_slug, blog_cates.status as cate_status, blogs.status as blog_status,"+
                                "blog_cates.*, blogs.*"};
            conditions.add(DataMapper.getInstance("", "blog_cates.status", "=", this.STATUS_ACTIVATED, ""));
            conditions.add(DataMapper.getInstance("and", "blogs.status", "=", this.STATUS_ACTIVATED, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates", DataMapper.getInstance("", "blog_cates.id", "=", "blogs.blog_cate_id", "")));
            return this.getAll(selects, conditions, joins, null, orderBy, limits);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getBlog(List<DataMapper> conditions){
        try {
            String[] selects = {"blog_cates.id as cate_id, blogs.id as blog_id, blog_cates.created_at as cate_created_at, blogs.created_at as blog_created_at,"+
                                "blog_cates.url_slug as cate_slug, blogs.url_slug as blog_slug, blog_cates.status as cate_status, blogs.status as blog_status,"+
                                "blog_cates.*, blogs.*"};
            conditions.add(DataMapper.getInstance("", "blog_cates.status", "=", this.STATUS_ACTIVATED, ""));
            conditions.add(DataMapper.getInstance("and", "blogs.status", "=", this.STATUS_ACTIVATED, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates", DataMapper.getInstance("", "blogs.blog_cate_id", "=", "blog_cates.id", "")));
            return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //end crud
    public Map<String, Object> getBlogById(String[] selects, int id){
        try {           
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "blogs.id", "=", String.valueOf(id), ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "blog_cates bc", 
                        DataMapper.getInstance("", "blogs.blog_cate_id", "=", "bc.id", "")));
            joins.add(JoinCondition.getInstance("join", "user_profiles p", 
                        DataMapper.getInstance("", "p.user_id", "=", "blogs.author_id", "")));
               return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
