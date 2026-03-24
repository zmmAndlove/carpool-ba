package com.carpool.service;

import com.carpool.dto.UserDTO;
import com.carpool.entity.User;
import com.carpool.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User register(UserDTO userDTO) {
        try {
            // 检查用户名是否已注册
            int count = userMapper.countByUsername(userDTO.getUsername());
            System.out.println("Username count: " + count);
            if (count > 0) {
                throw new RuntimeException("该用户名已注册");
            }
            
            // 创建用户实体
            User user = new User();
            user.setUsername(userDTO.getUsername());
            // 如果没有提供phone，使用默认值
            user.setPhone(userDTO.getPhone() != null ? userDTO.getPhone() : "");
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRealName(userDTO.getRealName());
            user.setAvatar(userDTO.getAvatar());
            user.setGender(userDTO.getGender());
            user.setAge(userDTO.getAge());
            // user.setIdCard(userDTO.getIdCard()); // 数据库中没有这个字段
            user.setHometownProvince(userDTO.getHometownProvince());
            user.setHometownCity(userDTO.getHometownCity());
            user.setCurrentProvince(userDTO.getCurrentProvince());
            user.setCurrentCity(userDTO.getCurrentCity());
            user.setUniversity(userDTO.getUniversity());
            user.setInterests(userDTO.getInterests());
            
            // 保存用户
            System.out.println("Inserting user: " + user.getUsername());
            int result = userMapper.insert(user);
            System.out.println("Insert result: " + result);
            return user;
        } catch (Exception e) {
            System.out.println("Register error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }
    
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userMapper.findByUsername(username);
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
    
    public Optional<User> getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOpt = userMapper.findById(id);
        if (!userOpt.isPresent()) {
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
        if (!userOpt.isPresent()) {
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
    
    public int getUserCount() {
        return userMapper.count();
    }
    
    public List<User> getUsers(int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.findAll(size, offset);
    }
    
    public double getAverageRating() {
        return userMapper.getAverageRating();
    }
}