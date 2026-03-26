package com.carpool.controller;

import com.carpool.entity.Comment;
import com.carpool.entity.Trip;
import com.carpool.entity.User;
import com.carpool.service.CommentService;
import com.carpool.service.TripService;
import com.carpool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final TripService tripService;
    private final CommentService commentService;
    
    public AdminController(UserService userService, TripService tripService, CommentService commentService) {
        this.userService = userService;
        this.tripService = tripService;
        this.commentService = commentService;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            // 获取注册用户数量
            int userCount = userService.getUserCount();
            
            // 获取行程数量
            int tripCount = tripService.getTripCount();
            
            // 获取评论数量
            int commentCount = commentService.getCommentsCount();
            
            // 获取成功匹配的行程数量
            int matchedTripCount = tripService.getMatchedTripCount();
            
            // 获取用户平均评分
            double averageRating = userService.getAverageRating();
            
            // 获取平均等待时间
            int averageWaitTime = tripService.getAverageWaitTime();
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("userCount", userCount);
            dashboardData.put("tripCount", tripCount);
            dashboardData.put("commentCount", commentCount);
            dashboardData.put("matchedTripCount", matchedTripCount);
            dashboardData.put("averageRating", averageRating);
            dashboardData.put("averageWaitTime", averageWaitTime);
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取仪表盘数据失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<User> users = userService.getUsers(page, size);
            int total = userService.getUserCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("page", page);
            response.put("size", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取用户列表失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trips")
    public ResponseEntity<?> getTrips(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Trip> trips = tripService.getTrips(page, size);
            int total = tripService.getTripCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("trips", trips);
            response.put("page", page);
            response.put("size", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取行程列表失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/comments")
    public ResponseEntity<?> getComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Comment> comments = commentService.getComments(page, size);
            int total = commentService.getCommentsCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", comments);
            response.put("page", page);
            response.put("size", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取评论列表失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "评论删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "删除评论失败");
            return ResponseEntity.status(500).body(error);
        }
    }
}
