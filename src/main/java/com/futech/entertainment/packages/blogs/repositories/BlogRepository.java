package com.futech.entertainment.packages.blogs.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.blogs.models.Blog;
import com.futech.entertainment.packages.blogs.repositories.interfaces.BlogRepositoryInterface;
import com.futech.entertainment.packages.core.repositories.BaseRepository;

@Repository
public class BlogRepository extends BaseRepository<Blog> implements BlogRepositoryInterface {
    public BlogRepository() {
        super();
        this.setModel(new Blog());
    }
}
