package com.futech.entertainment.packages.blogs.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.blogs.models.BlogCate;
import com.futech.entertainment.packages.blogs.repositories.interfaces.BlogCateRepositoryInterface;
import com.futech.entertainment.packages.core.repositories.BaseRepository;

@Repository
public class BlogCateRepository extends BaseRepository<BlogCate> implements BlogCateRepositoryInterface {
    public BlogCateRepository() {
        super();
        this.setModel(new BlogCate());
    }
}
