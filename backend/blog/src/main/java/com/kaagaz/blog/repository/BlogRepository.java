package com.kaagaz.blog.repository;


import com.kaagaz.blog.entity.Blog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {
    Blog findByTitle(String name);
}
