package com.futech.entertainment.packages.blogs.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.blogs.modelMappers.BlogCateMapper;
import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.blogs.services.interfaces.BlogCateServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;

@Service
public class BlogCateService extends BaseService<BlogCate> implements BlogCateServiceInterface {
    public String STATUS_DEACTIVATED = "0";
    public String STATUS_ACTIVATED = "1";

    // crud
    public List<Map<String, Object>> getBlogCategories(){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
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
    public boolean createBlog(BlogCateMapper blogCateMapper){
        try {
            BlogCate newBlogCate = new BlogCate();
            newBlogCate.setName(blogCateMapper.getName());
            newBlogCate.setStatus(blogCateMapper.getStatus());
            newBlogCate.setUrl_slug(blogCateMapper.getUrl_slug());
            newBlogCate.setCreated_at(LocalDateTime.now());
            this.create(newBlogCate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateBlogCate(BlogCateMapper blogCateMapper){
        try {
            BlogCate upBlogCate = new BlogCate();
            upBlogCate.setId(blogCateMapper.getId());
            upBlogCate.setName(blogCateMapper.getName());
            upBlogCate.setUrl_slug(blogCateMapper.getUrl_slug());
            upBlogCate.setStatus(blogCateMapper.getStatus());
            var updated = this.update(upBlogCate);

            if(updated){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkUrlSlug(String url, int bID){
        List<DataMapper> conditions = new ArrayList<DataMapper>();
        if(bID>0)
        conditions.add(DataMapper.getInstance("", "blog_cates.id", "<>", String.valueOf(bID), " and"));
        conditions.add(DataMapper.getInstance("", "url_slug", "=", url.replaceAll(" ", ""), ""));
        if(this.getAll(null, conditions, null, null, null, null).size()>0) return false;
        return true;
    }
    //end crud
    public Map<String, Object> getBlogCateById(String[] selects, int id){
        try {           
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "id", "=", String.valueOf(id), ""));
            return this.getFirstBy(selects, conditions, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

}
