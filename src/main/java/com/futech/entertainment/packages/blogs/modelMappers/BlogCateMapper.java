package com.futech.entertainment.packages.blogs.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.IsExisted;

public class BlogCateMapper {
    private Integer id;
    @NotBlank(message = "Name is required")
    private String name;
    @IsExisted(message = "Slug already exists")
    @NotBlank(message = "Url slug is required")
    private String url_slug;
    private Integer status;  
    
    public BlogCateMapper(){};
    
    public BlogCateMapper(Integer id,String name,String url_slug, Integer status) {
        this.id = id;
        this.name = name;
        this.url_slug = url_slug;
        this.status = status;
    }
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

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
