package com.carpool.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Trip {
    private Long id;
    private Long userId;
    private String tripType; // car_owner: 车找人, passenger: 人找车
    private String departureProvince;
    private String departureCity;
    private String departureDistrict;
    private String departureDetail;
    private String destinationProvince;
    private String destinationCity;
    private String destinationDistrict;
    private String destinationDetail;
    private LocalDateTime departureTime;
    private Integer seatsAvailable;
    private Integer seatsTotal;
    private Double pricePerPerson;
    private String description;
    private String carModel;
    private String carPlate;
    private String status; // pending, matched, completed, cancelled
    private Integer viewCount;
    private Integer matchCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Trip() {
        this.seatsAvailable = 1;
        this.seatsTotal = 4;
        this.status = "pending";
        this.viewCount = 0;
        this.matchCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}