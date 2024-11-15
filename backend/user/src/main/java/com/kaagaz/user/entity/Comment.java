package com.kaagaz.user.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @NonNull
    private String id;
    @NonNull
    private String blogId;

    private String blogAuthorName;;

    private String authorName;

    private String content;

    private LocalDateTime publishedDate;


}


//model Comment {
//id        Int      @id @default(autoincrement())
//userId    Int
//postId    Int
//content   String
//createdAt DateTime @default(now())
//user      User     @relation(fields: [userId], references: [id])
//post      Post     @relation(fields: [postId], references: [id])
//}