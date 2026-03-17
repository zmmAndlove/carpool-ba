package com.carpool.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String password;
    private String realName;
    private String avatar;
    private String gender;
    private Integer age;
    private String idCard;
    private String hometownProvince;
    private String hometownCity;
    private String currentProvince;
    private String currentCity;
    private String university;
    private String interests;
    private Double rating;
    private Integer tripCount;
    private Integer completedTrips;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public User() {
        this.rating = 5.0;
        this.tripCount = 0;
        this.completedTrips = 0;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}