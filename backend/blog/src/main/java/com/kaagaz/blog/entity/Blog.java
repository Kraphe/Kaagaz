package com.kaagaz.blog.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@Document(collection = "blogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
    @Id
    private String id ;

    private String authorName;

    private String authorId;

    private String title;

    private String imgUrl;

    private String Content;

    private Boolean published=false;

    private LocalDateTime  publishedDate;

    private Map<String, Comment> comments = new TreeMap<>();

    private Map<String, Likes> likes = new TreeMap<>();


}


