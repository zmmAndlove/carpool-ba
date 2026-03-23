package com.carpool.service;

import com.carpool.dto.TripDTO;
import com.carpool.entity.Trip;
import com.carpool.entity.User;
import com.carpool.mapper.TripMapper;
import com.carpool.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    
    @Autowired
    private TripMapper tripMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Transactional
    public Trip createTrip(Long userId, TripDTO tripDTO) {
        // 验证用户是否存在
        Optional<User> userOpt = userMapper.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建行程实体
        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setTripType(tripDTO.getTripType());
        trip.setDepartureProvince(tripDTO.getDepartureProvince());
        trip.setDepartureCity(tripDTO.getDepartureCity());
        trip.setDepartureDistrict(tripDTO.getDepartureDistrict());
        trip.setDepartureDetail(tripDTO.getDepartureDetail());
        trip.setDestinationProvince(tripDTO.getDestinationProvince());
        trip.setDestinationCity(tripDTO.getDestinationCity());
        trip.setDestinationDistrict(tripDTO.getDestinationDistrict());
        trip.setDestinationDetail(tripDTO.getDestinationDetail());
        trip.setDepartureTime(tripDTO.getDepartureTime());
        trip.setSeatsAvailable(tripDTO.getSeatsAvailable());
        trip.setSeatsTotal(tripDTO.getSeatsTotal() != null ? tripDTO.getSeatsTotal() : 4);
        trip.setPricePerPerson(tripDTO.getPricePerPerson());
        trip.setDescription(tripDTO.getDescription());
        trip.setCarModel(tripDTO.getCarModel());
        trip.setCarPlate(tripDTO.getCarPlate());
        trip.setStatus("pending");
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());
        
        // 保存行程
        tripMapper.insert(trip);
        return trip;
    }
    
    public Optional<Trip> getTripById(Long id) {
        Optional<Trip> tripOpt = tripMapper.findById(id);
        if (tripOpt.isPresent()) {
            // 增加查看次数
            tripMapper.incrementViewCount(id);
        }
        return tripOpt;
    }
    
    public List<Trip> getUserTrips(Long userId, String status) {
        return tripMapper.findByUserIdAndStatus(userId, status);
    }
    
    public List<Trip> searchTrips(
            String departureProvince,
            String departureCity,
            String destinationProvince,
            String destinationCity,
            String tripType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size
    ) {
        int offset = (page - 1) * size;
        return tripMapper.searchTrips(
            departureProvince,
            departureCity,
            destinationProvince,
            destinationCity,
            tripType,
            startTime,
            endTime,
            size,
            offset
        );
    }
    
    public int countTripsByRoute(
            String departureProvince,
            String departureCity,
            String destinationProvince,
            String destinationCity
    ) {
        return tripMapper.countByRoute(
            departureProvince,
            departureCity,
            destinationProvince,
            destinationCity
        );
    }
    
    public int countTrips(
            String departureProvince,
            String departureCity,
            String destinationProvince,
            String destinationCity,
            String tripType,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return tripMapper.countTrips(
            departureProvince,
            departureCity,
            destinationProvince,
            destinationCity,
            tripType,
            startTime,
            endTime
        );
    }
    
    @Transactional
    public Trip updateTrip(Long tripId, Long userId, TripDTO tripDTO) {
        Optional<Trip> tripOpt = tripMapper.findById(tripId);
        if (tripOpt.isEmpty()) {
            throw new RuntimeException("行程不存在");
        }
        
        Trip trip = tripOpt.get();
        if (!trip.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此行程");
        }
        
        if (!"pending".equals(trip.getStatus())) {
            throw new RuntimeException("只能修改待匹配状态的行程");
        }
        
        // 更新行程信息
        trip.setTripType(tripDTO.getTripType());
        trip.setDepartureProvince(tripDTO.getDepartureProvince());
        trip.setDepartureCity(tripDTO.getDepartureCity());
        trip.setDepartureDistrict(tripDTO.getDepartureDistrict());
        trip.setDepartureDetail(tripDTO.getDepartureDetail());
        trip.setDestinationProvince(tripDTO.getDestinationProvince());
        trip.setDestinationCity(tripDTO.getDestinationCity());
        trip.setDestinationDistrict(tripDTO.getDestinationDistrict());
        trip.setDestinationDetail(tripDTO.getDestinationDetail());
        trip.setDepartureTime(tripDTO.getDepartureTime());
        trip.setSeatsAvailable(tripDTO.getSeatsAvailable());
        trip.setSeatsTotal(tripDTO.getSeatsTotal() != null ? tripDTO.getSeatsTotal() : 4);
        trip.setPricePerPerson(tripDTO.getPricePerPerson());
        trip.setDescription(tripDTO.getDescription());
        trip.setCarModel(tripDTO.getCarModel());
        trip.setCarPlate(tripDTO.getCarPlate());
        trip.setUpdatedAt(LocalDateTime.now());
        
        tripMapper.update(trip);
        return trip;
    }
    
    @Transactional
    public void deleteTrip(Long tripId, Long userId) {
        Optional<Trip> tripOpt = tripMapper.findById(tripId);
        if (tripOpt.isEmpty()) {
            throw new RuntimeException("行程不存在");
        }
        
        Trip trip = tripOpt.get();
        if (!trip.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此行程");
        }
        
        if (!"pending".equals(trip.getStatus())) {
            throw new RuntimeException("只能删除待匹配状态的行程");
        }
        
        int deleted = tripMapper.deleteByIdAndUserId(tripId, userId);
        if (deleted == 0) {
            throw new RuntimeException("删除失败");
        }
    }
    
    @Transactional
    public Trip joinTrip(Long tripId, Long userId) {
        Optional<Trip> tripOpt = tripMapper.findById(tripId);
        if (tripOpt.isEmpty()) {
            throw new RuntimeException("行程不存在");
        }
        
        Trip trip = tripOpt.get();
        if (!"pending".equals(trip.getStatus())) {
            throw new RuntimeException("行程不可加入");
        }
        
        if (trip.getSeatsAvailable() <= 0) {
            throw new RuntimeException("座位已满");
        }
        
        if (trip.getUserId().equals(userId)) {
            throw new RuntimeException("不能加入自己发布的行程");
        }
        
        // 减少可用座位数
        int updated = tripMapper.decreaseSeats(tripId);
        if (updated == 0) {
            throw new RuntimeException("加入失败，座位可能已被占用");
        }
        
        // 增加匹配次数
        tripMapper.incrementMatchCount(tripId);
        
        // 更新行程状态（如果座位已满）
        if (trip.getSeatsAvailable() - 1 <= 0) {
            tripMapper.updateStatus(tripId, "matched");
        }
        
        // 返回更新后的行程
        return tripMapper.findById(tripId).orElse(trip);
    }
    
    @Transactional
    public Trip cancelJoin(Long tripId, Long userId) {
        Optional<Trip> tripOpt = tripMapper.findById(tripId);
        if (tripOpt.isEmpty()) {
            throw new RuntimeException("行程不存在");
        }
        
        Trip trip = tripOpt.get();
        if (!"matched".equals(trip.getStatus()) && !"pending".equals(trip.getStatus())) {
            throw new RuntimeException("行程不可取消加入");
        }
        
        // 增加可用座位数
        tripMapper.increaseSeats(tripId);
        
        // 如果之前是已匹配状态，改回待匹配
        if ("matched".equals(trip.getStatus())) {
            tripMapper.updateStatus(tripId, "pending");
        }
        
        // 返回更新后的行程
        return tripMapper.findById(tripId).orElse(trip);
    }
    
    @Transactional
    public Trip completeTrip(Long tripId, Long userId) {
        Optional<Trip> tripOpt = tripMapper.findById(tripId);
        if (tripOpt.isEmpty()) {
            throw new RuntimeException("行程不存在");
        }
        
        Trip trip = tripOpt.get();
        if (!trip.getUserId().equals(userId)) {
            throw new RuntimeException("只有发布者可以完成行程");
        }
        
        if (!"matched".equals(trip.getStatus())) {
            throw new RuntimeException("只有已匹配的行程可以完成");
        }
        
        tripMapper.updateStatus(tripId, "completed");
        
        // 更新用户的完成行程数
        Optional<User> userOpt = userMapper.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCompletedTrips(user.getCompletedTrips() + 1);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.update(user);
        }
        
        return tripMapper.findById(tripId).orElse(trip);
    }
}