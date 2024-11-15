package com.kaagaz.blog.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Likes {

    @Id
    private String id;

    private String userId ;

    private String blogId;

}

