package com.kaagaz.user.controllers;

import com.kaagaz.user.dto.BlogDTO;
import com.kaagaz.user.dto.CommentDTO;
import com.kaagaz.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins="http://localhost:5173", allowedHeaders="*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate() {
        return ResponseEntity.ok("Authenticated successfully");
    }


    @PostMapping("/blog/add")
    public ResponseEntity<?> addBlog(@RequestBody BlogDTO blogdto){
        System.out.println("bnnnmn m m m m m ");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        System.out.println(blogdto);
        try{
            blogdto=userService.saveNewBlog(blogdto,username);
            return new ResponseEntity<>(blogdto, HttpStatus.OK);

        }catch(Exception e){
            log.error("Error occur while adding new post", e);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    @PostMapping("/comment/add")
    public ResponseEntity<?> addComment( @RequestBody CommentDTO commentdto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();

        try{
            commentdto=userService.saveNewComment(commentdto,username);
            return new ResponseEntity<>(commentdto, HttpStatus.OK);

        }catch(Exception e){
            log.error("Error occur while adding new comment", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/comment/{Id}")
    public ResponseEntity<?> deleteComment(@PathVariable String Id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try{
            return userService.deleteComment(Id,username);
        }
        catch(Exception e){
            return new ResponseEntity<>("Internal server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/blog/{Id}")
    public ResponseEntity<?> deleteBlog(@PathVariable String Id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try{
            return userService.deleteBlog(Id,username);
        }
        catch(Exception e){
            return new ResponseEntity<>("Internal server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/isSubscribed")
    public ResponseEntity<?> isSubscribed(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try{
            return userService.isSubscribed(username);
        }
        catch(Exception e){
            return new ResponseEntity<>("Internal server Error",HttpStatus.UNAUTHORIZED);
        }
    }

}
