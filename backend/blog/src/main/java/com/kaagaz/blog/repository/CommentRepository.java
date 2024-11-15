package com.kaagaz.blog.repository;

import com.kaagaz.blog.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
