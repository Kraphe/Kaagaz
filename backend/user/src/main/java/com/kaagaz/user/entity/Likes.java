package com.kaagaz.user.entity;


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


//
//model Like {
//id     Int  @id @default(autoincrement())
//userId Int
//postId Int
//user   User @relation(fields: [userId], references: [id])
//post   Post @relation(fields: [postId], references: [id])
//}
