package com.carpool.service;

import com.carpool.dto.UserDTO;
import com.carpool.entity.User;
import com.carpool.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User register(UserDTO userDTO) {
        // 检查手机号是否已注册
        if (userMapper.countByPhone(userDTO.getPhone()) > 0) {
            throw new RuntimeException("该手机号已注册");
        }
        
        // 创建用户实体
        User user = new User();
        user.setPhone(userDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setAvatar(userDTO.getAvatar());
        user.setGender(userDTO.getGender());
        user.setAge(userDTO.getAge());
        user.setIdCard(userDTO.getIdCard());
        user.setHometownProvince(userDTO.getHometownProvince());
        user.setHometownCity(userDTO.getHometownCity());
        user.setCurrentProvince(userDTO.getCurrentProvince());
        user.setCurrentCity(userDTO.getCurrentCity());
        user.setUniversity(userDTO.getUniversity());
        user.setInterests(userDTO.getInterests());
        
        // 保存用户
        userMapper.insert(user);
        return user;
    }
    
    public Optional<User> login(String phone, String password) {
        Optional<User> userOpt = userMapper.findByPhone(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    public Optional<User> getUserById(Long id) {
        return userMapper.findById(id);
    }
    
    public Optional<User> getUserByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }
    
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        user.setRealName(userDTO.getRealName());
        user.setAvatar(userDTO.getAvatar());
        user.setGender(userDTO.getGender());
        user.setAge(userDTO.getAge());
        user.setIdCard(userDTO.getIdCard());
        user.setHometownProvince(userDTO.getHometownProvince());
        user.setHometownCity(userDTO.getHometownCity());
        user.setCurrentProvince(userDTO.getCurrentProvince());
        user.setCurrentCity(userDTO.getCurrentCity());
        user.setUniversity(userDTO.getUniversity());
        user.setInterests(userDTO.getInterests());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        
        userMapper.update(user);
        return user;
    }
    
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }
    
    @Transactional
    public void updateRating(Long id, Double newRating) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 简单平均计算新评分
            double currentRating = user.getRating();
            int tripCount = user.getTripCount();
            double updatedRating = (currentRating * tripCount + newRating) / (tripCount + 1);
            
            user.setRating(updatedRating);
            user.setTripCount(tripCount + 1);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            
            userMapper.update(user);
        }
    }
    
    @Transactional
    public void incrementCompletedTrips(Long id) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCompletedTrips(user.getCompletedTrips() + 1);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userMapper.update(user);
        }
    }
}