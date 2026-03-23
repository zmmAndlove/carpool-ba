package com.carpool.mapper;

import com.carpool.entity.Trip;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TripMapper {
    
    @Insert("INSERT INTO trips (user_id, trip_type, departure_province, departure_city, departure_district, departure_detail, " +
            "destination_province, destination_city, destination_district, destination_detail, departure_time, " +
            "seats_available, seats_total, price_per_person, description, car_model, car_plate, " +
            "status, view_count, match_count, created_at, updated_at) " +
            "VALUES (#{userId}, #{tripType}, #{departureProvince}, #{departureCity}, #{departureDistrict}, #{departureDetail}, " +
            "#{destinationProvince}, #{destinationCity}, #{destinationDistrict}, #{destinationDetail}, #{departureTime}, " +
            "#{seatsAvailable}, #{seatsTotal}, #{pricePerPerson}, #{description}, #{carModel}, #{carPlate}, " +
            "#{status}, #{viewCount}, #{matchCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Trip trip);
    
    @Update("UPDATE trips SET " +
            "departure_province = #{departureProvince}, " +
            "departure_city = #{departureCity}, " +
            "departure_district = #{departureDistrict}, " +
            "departure_detail = #{departureDetail}, " +
            "destination_province = #{destinationProvince}, " +
            "destination_city = #{destinationCity}, " +
            "destination_district = #{destinationDistrict}, " +
            "destination_detail = #{destinationDetail}, " +
            "departure_time = #{departureTime}, " +
            "seats_available = #{seatsAvailable}, " +
            "seats_total = #{seatsTotal}, " +
            "price_per_person = #{pricePerPerson}, " +
            "description = #{description}, " +
            "car_model = #{carModel}, " +
            "car_plate = #{carPlate}, " +
            "status = #{status}, " +
            "view_count = #{viewCount}, " +
            "match_count = #{matchCount}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(Trip trip);
    
    @Select("SELECT * FROM trips WHERE id = #{id}")
    @Results(id = "tripResult", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "tripType", column = "trip_type"),
        @Result(property = "departureProvince", column = "departure_province"),
        @Result(property = "departureCity", column = "departure_city"),
        @Result(property = "departureDistrict", column = "departure_district"),
        @Result(property = "departureDetail", column = "departure_detail"),
        @Result(property = "destinationProvince", column = "destination_province"),
        @Result(property = "destinationCity", column = "destination_city"),
        @Result(property = "destinationDistrict", column = "destination_district"),
        @Result(property = "destinationDetail", column = "destination_detail"),
        @Result(property = "departureTime", column = "departure_time"),
        @Result(property = "seatsAvailable", column = "seats_available"),
        @Result(property = "seatsTotal", column = "seats_total"),
        @Result(property = "pricePerPerson", column = "price_per_person"),
        @Result(property = "description", column = "description"),
        @Result(property = "carModel", column = "car_model"),
        @Result(property = "carPlate", column = "car_plate"),
        @Result(property = "status", column = "status"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "matchCount", column = "match_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Optional<Trip> findById(Long id);
    
    @Select("SELECT * FROM trips WHERE user_id = #{userId} AND status = #{status} ORDER BY departure_time DESC")
    @ResultMap("tripResult")
    List<Trip> findByUserIdAndStatus(Long userId, String status);
    
    @Select("<script>" +
            "SELECT * FROM trips WHERE status = 'pending' " +
            "<if test='departureProvince != null'>AND departure_province = #{departureProvince}</if>" +
            "<if test='departureCity != null'>AND departure_city = #{departureCity}</if>" +
            "<if test='destinationProvince != null'>AND destination_province = #{destinationProvince}</if>" +
            "<if test='destinationCity != null'>AND destination_city = #{destinationCity}</if>" +
            "<if test='tripType != null'>AND trip_type = #{tripType}</if>" +
            "<if test='startTime != null'>AND departure_time &gt;= #{startTime}</if>" +
            "<if test='endTime != null'>AND departure_time &lt;= #{endTime}</if>" +
            "ORDER BY departure_time ASC LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    @ResultMap("tripResult")
    List<Trip> searchTrips(String departureProvince, String departureCity,
                          String destinationProvince, String destinationCity,
                          String tripType, LocalDateTime startTime, LocalDateTime endTime,
                          int limit, int offset);
    
    @Select("SELECT COUNT(*) FROM trips WHERE status = 'pending' " +
            "AND departure_province = #{departureProvince} AND departure_city = #{departureCity} " +
            "AND destination_province = #{destinationProvince} AND destination_city = #{destinationCity}")
    int countByRoute(String departureProvince, String departureCity,
                     String destinationProvince, String destinationCity);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM trips WHERE status = 'pending' " +
            "<if test='departureProvince != null'>AND departure_province = #{departureProvince}</if>" +
            "<if test='departureCity != null'>AND departure_city = #{departureCity}</if>" +
            "<if test='destinationProvince != null'>AND destination_province = #{destinationProvince}</if>" +
            "<if test='destinationCity != null'>AND destination_city = #{destinationCity}</if>" +
            "<if test='tripType != null'>AND trip_type = #{tripType}</if>" +
            "<if test='startTime != null'>AND departure_time &gt;= #{startTime}</if>" +
            "<if test='endTime != null'>AND departure_time &lt;= #{endTime}</if>" +
            "</script>")
    int countTrips(String departureProvince, String departureCity,
                   String destinationProvince, String destinationCity,
                   String tripType, LocalDateTime startTime, LocalDateTime endTime);
    
    @Update("UPDATE trips SET seats_available = seats_available - 1, updated_at = NOW() WHERE id = #{id} AND seats_available > 0")
    int decreaseSeats(Long id);
    
    @Update("UPDATE trips SET seats_available = seats_available + 1, updated_at = NOW() WHERE id = #{id}")
    int increaseSeats(Long id);
    
    @Update("UPDATE trips SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(Long id, String status);
    
    @Update("UPDATE trips SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
    
    @Update("UPDATE trips SET match_count = match_count + 1 WHERE id = #{id}")
    int incrementMatchCount(Long id);
    
    @Delete("DELETE FROM trips WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(Long id, Long userId);
}