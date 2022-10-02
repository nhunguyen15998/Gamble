package com.futech.entertainment.packages.blogs.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Blog")
public class Blog extends BaseModel{
    public Blog() {}

    private String table = "blogs";
    private String[] columns = {"id", "title", "content", "blog_cate_id", "author_id", "created_at", "status",
                                "thumbnail", "description" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String title;
    private String content;
    private Integer blog_cate_id;  
    private Integer author_id;
    private String thumbnail;
    private String description;  
    private LocalDateTime created_at;
    private Integer status;  

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getBlog_cate_id() {
        return blog_cate_id;
    }
    public void setBlog_cate_id(Integer blog_cate_id) {
        this.blog_cate_id = blog_cate_id;
    }

    public Integer getAuthor_id() {
        return author_id;
    }
    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

