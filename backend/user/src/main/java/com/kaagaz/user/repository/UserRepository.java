package com.kaagaz.user.repository;


import com.kaagaz.user.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUserName(String name);
    Optional<User> findByEmail(String email);
}
