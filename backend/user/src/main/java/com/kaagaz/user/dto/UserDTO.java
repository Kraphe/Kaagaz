package com.kaagaz.user.dto;

import com.kaagaz.user.entity.Blog;
import com.kaagaz.user.entity.Comment;
import com.kaagaz.user.entity.Likes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;

    private String email;

    private String userName;

    private String password;

    private Map<String, Blog> blogs = new TreeMap<>();

    @DBRef
    private Map<String, Comment> comments = new TreeMap<>();
    @DBRef
    private Map<String, Likes> likes = new TreeMap<>();

    private List<String> roles;

}
