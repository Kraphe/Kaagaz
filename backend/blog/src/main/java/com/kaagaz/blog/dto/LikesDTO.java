package com.kaagaz.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikesDTO {
    private String id;

    private String userId ;

    private String blogId;
}
