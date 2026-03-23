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
    private Integer likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Comment() {
        this.likes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}