package com.kaagaz.user.dto;

import com.kaagaz.user.entity.Comment;
import com.kaagaz.user.entity.Likes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDTO {

    private String id;

    private String authorName;

    private String authorId; // Keep as String for String representation

    private String title;

    private String imgUrl;

    private String content; // Changed to lowercase for consistency

    private Boolean published = false;

    private LocalDateTime publishedDate;

    private Map<String, Comment> comments = new TreeMap<>();

    private Map<String, Likes> likes = new TreeMap<>();

}
