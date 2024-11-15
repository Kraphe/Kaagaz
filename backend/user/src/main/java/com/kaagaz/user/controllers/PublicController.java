package com.kaagaz.user.controllers;


import com.kaagaz.user.dto.UserDTO;
import com.kaagaz.user.entity.User;
import com.kaagaz.user.service.UserDetailsServiceImpl;
import com.kaagaz.user.service.UserService;
import com.kaagaz.user.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/account")
@CrossOrigin
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public ResponseEntity<?> getHealth(){
        return new ResponseEntity<>("Health check", HttpStatus.OK);
    }

    @PostMapping("/email/exist/{email}")
    public ResponseEntity<?> isEmailExist(@PathVariable String email){
        System.out.println(email);
        return userService.isEmailExist(email);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userdto){
        try{
            userdto = userService.saveNewUser(userdto);
            UserDetails userDetails= userDetailsService.loadUserByUsername(userdto.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            System.out.println(userdto);

            return new ResponseEntity<>(jwt,HttpStatus.OK);
        }catch(Exception e){
            System.out.print("error while signup " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody UserDTO userdto){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userdto.getUserName(),userdto.getPassword()));
            UserDetails userDetails= userDetailsService.loadUserByUsername(userdto.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            System.out.println(userdto);

            return new ResponseEntity<>(jwt,HttpStatus.OK);
        }catch(Exception e){
            System.out.println(userdto);
            System.out.println(e);
            return new ResponseEntity<>(e,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> get(){
        List<User> userList =userService.getAllUser();
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }
}
