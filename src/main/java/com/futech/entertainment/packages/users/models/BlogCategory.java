package com.futech.entertainment.packages.users.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.blogs.repositories.BlogCateRepository;
import com.futech.entertainment.packages.core.models.BaseModel;
@Qualifier("BlogCategory")
public class BlogCategory extends BaseModel {
    public BlogCategory() {}

    private String table = "blog_cates";

    private String[] columns = { "id", "name","created_at","status" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }
    
    private Integer id;
    private String name;
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
