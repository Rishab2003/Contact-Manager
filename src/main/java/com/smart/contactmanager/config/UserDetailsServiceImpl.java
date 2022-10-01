package com.smart.contactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        //fetching user from database
        User user=userRepository.getUserByName(username);

        if(user==null){
            throw new UsernameNotFoundException("Could not found user!!");
        }

        CustomUserDetails customUserDetails=new CustomUserDetails(user);


        return customUserDetails;
    }
    
}
