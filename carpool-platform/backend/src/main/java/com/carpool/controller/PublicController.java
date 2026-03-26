package com.carpool.controller;

import com.carpool.service.TripService;
import com.carpool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {
    
    private final UserService userService;
    private final TripService tripService;
    
    public PublicController(UserService userService, TripService tripService) {
        this.userService = userService;
        this.tripService = tripService;
    }
    
    // 供首页使用的统计数据接口，不需要管理员权限
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            // 获取行程数量
            int tripCount = tripService.getTripCount();
            
            // 获取成功匹配的行程数量
            int matchedTripCount = tripService.getMatchedTripCount();
            
            // 获取用户平均评分
            double averageRating = userService.getAverageRating();
            
            // 获取平均等待时间
            int averageWaitTime = tripService.getAverageWaitTime();
            
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("tripCount", tripCount);
            statsData.put("matchedTripCount", matchedTripCount);
            statsData.put("averageRating", averageRating);
            statsData.put("averageWaitTime", averageWaitTime);
            
            return ResponseEntity.ok(statsData);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取统计数据失败");
            return ResponseEntity.status(500).body(error);
        }
    }
}