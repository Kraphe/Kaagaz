package com.kaagaz.blog.service;


import com.kaagaz.blog.dto.BlogDTO;
import com.kaagaz.blog.dto.CommentDTO;
import com.kaagaz.blog.entity.Blog;
import com.kaagaz.blog.entity.Comment;
import com.kaagaz.blog.repository.BlogRepository;
import com.kaagaz.blog.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    WebClient.Builder wc;

    @Autowired
    private RedisService redisService;


    private static final ModelMapper modelMapper = new ModelMapper();


    public ResponseEntity<?> authenticate(HttpHeaders headers){
        String userServiceUrl = "http://localhost:8080/user/authenticate";
        try {
            return wc.build()
                    .post()
                    .uri(userServiceUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (Exception e) {
            log.error("User authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Authorized");
        }
    }


    public ResponseEntity<?> saveNewBlog(BlogDTO blogdto, HttpHeaders headers){
        String userServiceUrl = "http://localhost:8080/user/blog/add";
        blogdto.setPublishedDate(LocalDateTime.now());
        try {
            Blog blog= modelMapper.map(blogdto,Blog.class);
            blog=blogRepository.save(blog);
            blogdto = modelMapper.map(blog, BlogDTO.class);
            System.out.println(blogdto);
            ResponseEntity<BlogDTO> response=wc.build()
                    .post()
                    .uri(userServiceUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(blogdto)
                    .retrieve()
                    .toEntity(BlogDTO.class)
                    .block();
            blogdto = response.getBody();
            blog = modelMapper.map(blogdto, Blog.class);
            blogRepository.save(blog);
            System.out.println(blog);
            return new ResponseEntity<>(blogdto,HttpStatus.CREATED);
        }catch (Exception e) {
            log.error("some thing went wrong  while saving Commentdto", e);
            return  new ResponseEntity<>("some thing went wrong  while saving Comment",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<?> saveNewComment(CommentDTO commentdto, HttpHeaders headers) {
        String userServiceUrl = "http://localhost:8080/user/comment/add";
        try {
            commentdto.setPublishedDate(LocalDateTime.now());
            Optional<Blog> blog=blogRepository.findById(commentdto.getBlogId());
            if(blog.isPresent())
                commentdto.setBlogAuthorName(blog.get().getAuthorName());
            System.out.println(commentdto);
            Comment comment = modelMapper.map(commentdto, Comment.class);

            comment = commentRepository.save(comment);
            commentdto.setId(comment.getId());
            String commentId = commentdto.getId() ;
            System.out.println(commentdto);
            log.info("Generated Comment ID: " + commentdto.getId());
            log.info("Updated CommentDTO with generated ID: "   + commentdto);

            // Send updated CommentDTO to User service
            ResponseEntity<CommentDTO> response = wc.build()
                    .post()
                    .uri(userServiceUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(commentdto)
                    .retrieve()
                    .toEntity(CommentDTO.class)
                    .block();
            commentdto=response.getBody();
            System.out.println(commentdto);
            log.info("Response from User service: " + response.getStatusCode());
            log.info("Updated CommentDTO with generated ID: " + commentdto);

            // Save comment reference in Blog after User service call
            Comment finalComment = modelMapper.map(commentdto,Comment.class);
                Optional<Comment> updateComment= commentRepository.findById(commentdto.getId());
                updateComment.get().setAuthorName(commentdto.getBlogAuthorName());
                commentRepository.save(updateComment.get());
                blog.get().getComments().put(commentId, finalComment);
                blogRepository.save(blog.get());
                log.info("Comment added to Blog with Blog ID: " + blog.get().getId());

//            messagingTemplate.convertAndSend("/topic/notifications", commentdto);

            return new ResponseEntity<>(commentdto, HttpStatus.OK);
        }  catch (Exception e) {
            log.error("An error occurred while saving CommentDTO: " + e);
            return new ResponseEntity<>("Error saving Comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteBlog(String id){
        blogRepository.findById(id).ifPresent(blog -> {
             blog.getComments().values()
                    .forEach(comment -> commentRepository.deleteById(comment.getId()));
             blog.getComments().clear();
             blogRepository.deleteById(id);
        });
    }

    public void deleteComment(String id){
        Optional<Comment> comment = commentRepository.findById(id);

        comment.ifPresent(value -> {
            Optional<Blog> blog = blogRepository.findById(value.getBlogId());
            blog.ifPresent(blogEntity -> {
                blogEntity.getComments().remove(comment.get().getId());
                blogRepository.save(blogEntity); // Save the updated blog
            });
        });
        commentRepository.deleteById(id);
    }


    public List<Blog> getAllBlogs(){
        List<Blog> blogList = new ArrayList<>();
        return blogList = blogRepository.findAll();
    }

    public Blog getBlog(String id){
        Blog reddisBlog= redisService.get(id, Blog.class);
        if(reddisBlog!=null)
            return reddisBlog;
        Optional<Blog> blog = blogRepository.findById(id);
//        return blog.get();
        if(blog.isPresent())
            redisService.set(id,blog, Long.valueOf(3001));
        return blog.orElse(null);
    }

    public List<CommentDTO> getComments(String blogId){
        Optional<Blog> blog = blogRepository.findById(blogId);

        if (blog.isPresent()) {
            return blog.get().getComments().values().stream()
                    .map(comment -> modelMapper.map(comment, CommentDTO.class))
                    .collect(Collectors.toList());
        }
        else
            return List.of();
    }
}
