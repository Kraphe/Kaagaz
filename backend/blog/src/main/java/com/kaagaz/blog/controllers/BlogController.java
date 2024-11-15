package com.kaagaz.blog.controllers;


import com.kaagaz.blog.dto.BlogDTO;
import com.kaagaz.blog.dto.CommentDTO;
import com.kaagaz.blog.entity.Blog;
import com.kaagaz.blog.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blog")
@CrossOrigin("*")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    WebClient.Builder wc;



    @GetMapping("/health-check")
    public ResponseEntity<?> getHealth(HttpServletRequest request){
        System.out.println(request);
        return new ResponseEntity<>("Health check", HttpStatus.OK);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable String id){
        List<CommentDTO> comments= blogService.getComments(id);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @PostMapping("/create/new-blog")
    public ResponseEntity<?> createPost(@RequestBody BlogDTO blogdto, @RequestHeader HttpHeaders headers) {
        try{
            return blogService.saveNewBlog(blogdto,headers);
        }
        catch( Exception e){
            log.error("Something went wrong");
            return new ResponseEntity<>("Somethins went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("create/new-comment/blogId/{id}")
    public ResponseEntity<?> createComment(@PathVariable String id, @RequestBody CommentDTO commentdto, @RequestHeader HttpHeaders headers) {
        commentdto.setBlogId(id);
        System.out.println(commentdto);
        try{
            return blogService.saveNewComment(commentdto, headers);
        }
        catch( Exception e){
            log.error("Something went wrong while saving comment");
            return new ResponseEntity<>("Somethins went wrong while saving comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/comment/{Id}")
    public ResponseEntity<?> deleteComment(@PathVariable String Id){
       try{
           blogService.deleteComment(Id);
           return new ResponseEntity<>("Success in deleting comment",HttpStatus.OK);
       }
       catch(Exception e){
           return new ResponseEntity<>("Internal server Error",HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @DeleteMapping("/delete/blog/{Id}")
    public ResponseEntity<?> deleteBlog(@PathVariable String Id){
        try{
            blogService.deleteBlog(Id);
            return new ResponseEntity<>("Success in deleting comment",HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("Internal server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return new ResponseEntity<>(blogs, HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Blog> getBlog(@PathVariable String id) {
        Blog blog = blogService.getBlog(id);
        if (blog != null) {
            return new ResponseEntity<>(blog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
