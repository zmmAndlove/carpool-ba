package com.carpool.service;

import com.carpool.entity.User;
import com.carpool.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserMapper userMapper;
    
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userMapper.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        User user = userOpt.get();
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}