package com.kaagaz.user.service;

import com.kaagaz.user.entity.User;
import com.kaagaz.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("load by user name");
        User user = userRepository.findByUserName(username);
            if(user!=null){
                UserDetails userDetails=org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUserName())
                        .password(user.getPassword())
                        .roles(user.getRoles().toArray(new String[0]))
                        .build();
                return userDetails;
            }
            throw new UsernameNotFoundException("userNot Found with this name: " + username);
    }

}
