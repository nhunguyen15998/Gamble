package com.futech.entertainment.packages.blogs.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("BlogCate")
public class BlogCate extends BaseModel{
    public BlogCate() {}

    private String table = "blog_cates";
    private String[] columns = {"id", "name", "created_at", "status", "url_slug"};

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String name;
    private String url_slug;
    private LocalDateTime created_at;
    private Integer status;  

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_slug() {
        return url_slug;
    }
    public void setUrl_slug(String url_slug) {
        this.url_slug = url_slug;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
