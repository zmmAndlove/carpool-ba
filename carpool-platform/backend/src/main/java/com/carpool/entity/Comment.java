package com.carpool.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Integer rating;
    private int likes;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Comment() {
        this.likes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 回复构造函数
    public Comment(Long userId, String username, String avatar, String content, Integer rating, Long parentId) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.content = content;
        this.rating = rating;
        this.likes = 0;
        this.parentId = parentId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}