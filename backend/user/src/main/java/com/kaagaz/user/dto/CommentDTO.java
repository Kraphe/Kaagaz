package com.kaagaz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private String id;

    private String blogAuthorName;

    private String authorName;

    private String blogId;

    private String content;

    private LocalDateTime publishedDate;
}
