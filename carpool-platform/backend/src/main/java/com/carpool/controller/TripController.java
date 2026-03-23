package com.carpool.controller;

import com.carpool.dto.TripDTO;
import com.carpool.entity.Trip;
import com.carpool.entity.User;
import com.carpool.service.JwtService;
import com.carpool.service.TripService;
import com.carpool.service.UserService;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trips")
public class TripController {
    
    private final TripService tripService;
    private final UserService userService;
    private final JwtService jwtService;
    
    public TripController(TripService tripService, UserService userService, JwtService jwtService) {
        this.tripService = tripService;
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    @PostMapping
    public ResponseEntity<?> createTrip(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TripDTO tripDTO
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            Trip trip = tripService.createTrip(userId, tripDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "行程发布成功");
            response.put("trip", convertToResponse(trip));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrip(@PathVariable Long id) {
        try {
            Optional<Trip> tripOpt = tripService.getTripById(id);
            if (!tripOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "行程不存在");
                return ResponseEntity.status(404).body(error);
            }
            
            Trip trip = tripOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("trip", convertToResponse(trip));
            
            // 获取发布者信息
            Optional<User> userOpt = userService.getUserById(trip.getUserId());
            userOpt.ifPresent(user -> response.put("publisher", convertUserToResponse(user)));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取行程失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyTrips(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            List<Trip> trips = tripService.getUserTrips(userId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("trips", trips.stream().map(this::convertToResponse).collect(Collectors.toList()));
            response.put("total", trips.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取行程列表失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchTrips(
            @RequestParam(required = false) String departureProvince,
            @RequestParam(required = false) String departureCity,
            @RequestParam(required = false) String destinationProvince,
            @RequestParam(required = false) String destinationCity,
            @RequestParam(required = false) String tripType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            List<Trip> trips = tripService.searchTrips(
                departureProvince,
                departureCity,
                destinationProvince,
                destinationCity,
                tripType,
                startTime,
                endTime,
                page,
                size
            );
            
            // 计算总记录数，使用与搜索相同的条件
            int total = tripService.countTrips(
                departureProvince,
                departureCity,
                destinationProvince,
                destinationCity,
                tripType,
                startTime,
                endTime
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("trips", trips.stream().map(this::convertToResponse).collect(Collectors.toList()));
            response.put("page", page);
            response.put("size", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "搜索行程失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrip(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody TripDTO tripDTO
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            Trip trip = tripService.updateTrip(id, userId, tripDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "行程更新成功");
            response.put("trip", convertToResponse(trip));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            tripService.deleteTrip(id, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "行程删除成功");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinTrip(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            Trip trip = tripService.joinTrip(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "加入行程成功");
            response.put("trip", convertToResponse(trip));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelJoin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            Trip trip = tripService.cancelJoin(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "取消加入成功");
            response.put("trip", convertToResponse(trip));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeTrip(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            Trip trip = tripService.completeTrip(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "行程完成");
            response.put("trip", convertToResponse(trip));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String phone = jwtService.extractUsername(token);
        Optional<User> userOpt = userService.getUserByPhone(phone);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        return userOpt.get().getId();
    }
    
    private Map<String, Object> convertToResponse(Trip trip) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", trip.getId());
        response.put("userId", trip.getUserId());
        response.put("tripType", trip.getTripType());
        response.put("departureProvince", trip.getDepartureProvince());
        response.put("departureCity", trip.getDepartureCity());
        response.put("departureDistrict", trip.getDepartureDistrict());
        response.put("departureDetail", trip.getDepartureDetail());
        response.put("destinationProvince", trip.getDestinationProvince());
        response.put("destinationCity", trip.getDestinationCity());
        response.put("destinationDistrict", trip.getDestinationDistrict());
        response.put("destinationDetail", trip.getDestinationDetail());
        response.put("departureTime", trip.getDepartureTime());
        response.put("seatsAvailable", trip.getSeatsAvailable());
        response.put("seatsTotal", trip.getSeatsTotal());
        response.put("pricePerPerson", trip.getPricePerPerson());
        response.put("description", trip.getDescription());
        response.put("carModel", trip.getCarModel());
        response.put("carPlate", trip.getCarPlate());
        response.put("status", trip.getStatus());
        response.put("viewCount", trip.getViewCount());
        response.put("matchCount", trip.getMatchCount());
        response.put("createdAt", trip.getCreatedAt());
        response.put("updatedAt", trip.getUpdatedAt());
        return response;
    }
    
    private Map<String, Object> convertUserToResponse(User user) {
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
        return response;
    }
}