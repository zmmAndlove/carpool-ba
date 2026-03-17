package com.carpool.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TripDTO {
    
    @NotBlank(message = "行程类型不能为空")
    private String tripType; // car_owner 或 passenger
    
    @NotBlank(message = "出发省份不能为空")
    private String departureProvince;
    
    @NotBlank(message = "出发城市不能为空")
    private String departureCity;
    
    private String departureDistrict;
    private String departureDetail;
    
    @NotBlank(message = "目的省份不能为空")
    private String destinationProvince;
    
    @NotBlank(message = "目的城市不能为空")
    private String destinationCity;
    
    private String destinationDistrict;
    private String destinationDetail;
    
    @NotNull(message = "出发时间不能为空")
    private LocalDateTime departureTime;
    
    @NotNull(message = "可用座位数不能为空")
    private Integer seatsAvailable;
    
    private Integer seatsTotal;
    private Double pricePerPerson;
    private String description;
    private String carModel;
    private String carPlate;
    
    // 构造函数设置默认值
    public TripDTO() {
        this.seatsAvailable = 1;
        this.seatsTotal = 4;
    }
}