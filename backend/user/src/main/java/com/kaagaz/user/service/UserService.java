package com.kaagaz.user.service;

import com.kaagaz.user.dto.BlogDTO;
import com.kaagaz.user.dto.CommentDTO;
import com.kaagaz.user.dto.UserDTO;
import com.kaagaz.user.entity.Blog;
import com.kaagaz.user.entity.Comment;
import com.kaagaz.user.entity.Subscription;
import com.kaagaz.user.entity.User;
import com.kaagaz.user.repository.SubscriptionRepository;
import com.kaagaz.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private WebClient.Builder wc;

    private static final ModelMapper modelMapper = new ModelMapper();


    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> getUser(String id){
        return  userRepository.findById(id);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByName(String name){
        return userRepository.findByUserName(name);
    }

    public void updateUser(User user){
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user.getId()));

        userRepository.save(existingUser);
    }

    public ResponseEntity<?> isEmailExist(String email){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isPresent())
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public UserDTO saveNewUser(UserDTO userdto){
        User user = modelMapper.map(userdto, User.class);
           try{
               user.setPassword(passwordEncoder.encode(user.getPassword()));
               user.setRoles(Arrays.asList("USER"));
               userRepository.save(user);
               user=userRepository.findByUserName(user.getUserName());
           }
           catch(Exception e){
               log.error("Error occur while saving new User", e);
           }
        return userdto=modelMapper.map(user,UserDTO.class);
    }

    public User saveUser(User user){
        userRepository.save(user);
        user=userRepository.findByUserName(user.getUserName());
        return user;
    }

    public ResponseEntity<?> deleteBlog(String blogId, String userName){
        User user = userRepository.findByUserName(userName);
        if(!user.getBlogs().containsKey(blogId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String userServiceUrl = "http://localhost:8081/blog/delete/blog/"+ blogId;
        try{
            ResponseEntity<String> response=wc.build()
                    .delete()
                    .uri(userServiceUrl)
                    .retrieve()
                    .toEntity(String.class)
                    .block();



            Map<String, Comment> updatedComments = user.getComments().entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().getBlogId().equals(blogId))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            user.setComments(updatedComments);
            user.getBlogs().remove(blogId);
            userRepository.save(user);

            return new  ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Error while performing delete blog" + e);
            return new ResponseEntity<>("Error while performing delete blog",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteComment(String commentId, String userName){
        String userServiceUrl = "http://localhost:8081/blog/delete/comment/"+ commentId;
        User user =userRepository.findByUserName(userName);
        if(!user.getComments().containsKey(commentId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try{
            ResponseEntity<String> response=wc.build()
                    .delete()
                    .uri(userServiceUrl)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            user.getComments().remove(commentId);
            userRepository.save(user);
            return new  ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Error while performing delete blog" + e);
            return new ResponseEntity<>("Error while performing delete blog",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BlogDTO saveNewBlog(BlogDTO blogdto, String userName){
        blogdto.setAuthorName(userName);

        Blog blog = modelMapper.map(blogdto,Blog.class);
        try{
            User user =userRepository.findByUserName(userName);
            blogdto.setAuthorId(user.getId());
            user.getBlogs().put(blog.getId(),blog);
            userRepository.save(user);
        }
        catch(Exception e){
            log.error(blog + ""+ e);
        }
        return blogdto;
    }

    public CommentDTO saveNewComment(CommentDTO commentdto, String userName){
        System.out.println("comment in user " + commentdto);
        commentdto.setAuthorName(userName);
        User user = userRepository.findByUserName(userName);
        Comment comment = modelMapper.map(commentdto,Comment.class);
        try{

            String commentId= comment.getId();
            user.getComments().put(commentId,comment);
            userRepository.save(user);
        }
        catch(Exception e){
            log.error("Some thing bad occur" + e);
        }
        return commentdto;
    }

    public ResponseEntity<?> isSubscribed(String userName){
        User user = userRepository.findByUserName(userName);
        String userServiceUrl = "http://localhost:8082/subscription/isSubscribed/email/"+ user.getEmail();
        try{
            ResponseEntity<String> response=wc.build()
                    .post()
                    .uri(userServiceUrl)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            if(response==null||response.getStatusCode()!=HttpStatus.OK)
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch( Exception e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }



    public void availSubscription(String userName, int monthsToAdd){
        User user = userRepository.findByUserName(userName);
        Optional<Subscription> subs = Optional.ofNullable(subscriptionRepository.findByUserId(user.getId()));

        LocalDate currentDate = LocalDate.now();

        if (subs.isPresent()) {
            // Update existing subscription
            Subscription subscription = subs.get();
            LocalDate endDate = subscription.getEndDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            // Extend the end date by the specified number of months
            subscription.setEndDate(Date.from(endDate.plusMonths(monthsToAdd)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            subscriptionRepository.save(subscription);

        } else {
            // Create a new subscription
            Subscription subscription = new Subscription();
            subscription.setUserId(user.getId());
            subscription.setStartDate(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subscription.setEndDate(Date.from(currentDate.plusMonths(monthsToAdd)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subscription.setActive(true);
            subscriptionRepository.save(subscription);
        }


    }

}
