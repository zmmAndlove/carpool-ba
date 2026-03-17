package com.carpool.controller;

import com.carpool.dto.LoginDTO;
import com.carpool.dto.UserDTO;
import com.carpool.entity.User;
import com.carpool.service.JwtService;
import com.carpool.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.register(userDTO);
            
            // 生成JWT令牌
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                java.util.Collections.emptyList()
            );
            String token = jwtService.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功");
            response.put("user", convertToResponse(user));
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getPhone(),
                    loginDTO.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            Optional<User> userOpt = userService.getUserByPhone(loginDTO.getPhone());
            if (userOpt.isEmpty()) {
                throw new RuntimeException("用户不存在");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("user", convertToResponse(userOpt.get()));
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "手机号或密码错误");
            return ResponseEntity.status(401).body(error);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String phone = jwtService.extractUsername(token);
            
            Optional<User> userOpt = userService.getUserByPhone(phone);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("用户不存在");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", convertToResponse(userOpt.get()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取用户信息失败");
            return ResponseEntity.status(401).body(error);
        }
    }
    
    private Map<String, Object> convertToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("phone", user.getPhone());
        response.put("realName", user.getRealName());
        response.put("avatar", user.getAvatar());
        response.put("gender", user.getGender());
        response.put("age", user.getAge());
        response.put("hometownProvince", user.getHometownProvince());
        response.put("hometownCity", user.getHometownCity());
        response.put("currentProvince", user.getCurrentProvince());
        response.put("currentCity", user.getCurrentCity());
        response.put("university", user.getUniversity());
        response.put("interests", user.getInterests());
        response.put("rating", user.getRating());
        response.put("tripCount", user.getTripCount());
        response.put("completedTrips", user.getCompletedTrips());
        response.put("createdAt", user.getCreatedAt());
        return response;
    }
}