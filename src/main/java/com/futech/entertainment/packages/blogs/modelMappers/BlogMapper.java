package com.futech.entertainment.packages.blogs.modelMappers;

import javax.validation.constraints.NotBlank;

public class BlogMapper {
    
    
    private Integer id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Content is required")
    private String content;
    private Integer blog_cate_id;
    private String blogCate;
    private Integer author_id;
    private String url_slug;
    private String author_name;
    private Integer status;

    
    public BlogMapper() {
    }

    public BlogMapper(Integer id, String title, String content, Integer blog_cate_id, String blogCate,
            Integer author_id, String author_name, String url, Integer status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.blog_cate_id = blog_cate_id;
        this.blogCate = blogCate;
        this.author_id = author_id;
        this.author_name = author_name;
        this.status = status;
        this.url_slug=url;
    }

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


    public Integer getstatus() {
        return status;
    }

    public void setstatus(Integer status) {
        this.status = status;
    }
    public String getBlogCate() {
        return blogCate;
    }

    public void setBlogCate(String blogCate) {
        this.blogCate = blogCate;
    } 
    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl_slug() {
        return url_slug;
    }

    public void setUrl_slug(String url_slug) {
        this.url_slug = url_slug;
    }
}