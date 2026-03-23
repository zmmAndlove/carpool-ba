package com.carpool.mapper;

import com.carpool.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    
    @Insert("INSERT INTO users (username, phone, password, real_name, avatar, gender, age, id_card, " +
            "hometown_province, hometown_city, current_province, current_city, university, interests, " +
            "rating, trip_count, completed_trips, status, created_at, updated_at) " +
            "VALUES (#{username}, #{phone}, #{password}, #{realName}, #{avatar}, #{gender}, #{age}, #{idCard}, " +
            "#{hometownProvince}, #{hometownCity}, #{currentProvince}, #{currentCity}, #{university}, #{interests}, " +
            "#{rating}, #{tripCount}, #{completedTrips}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET " +
            "username = #{username}, " +
            "real_name = #{realName}, " +
            "avatar = #{avatar}, " +
            "gender = #{gender}, " +
            "age = #{age}, " +
            "id_card = #{idCard}, " +
            "hometown_province = #{hometownProvince}, " +
            "hometown_city = #{hometownCity}, " +
            "current_province = #{currentProvince}, " +
            "current_city = #{currentCity}, " +
            "university = #{university}, " +
            "interests = #{interests}, " +
            "rating = #{rating}, " +
            "trip_count = #{tripCount}, " +
            "completed_trips = #{completedTrips}, " +
            "status = #{status}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(User user);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = "userResult", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "password", column = "password"),
        @Result(property = "realName", column = "real_name"),
        @Result(property = "avatar", column = "avatar"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "age", column = "age"),
        @Result(property = "idCard", column = "id_card"),
        @Result(property = "hometownProvince", column = "hometown_province"),
        @Result(property = "hometownCity", column = "hometown_city"),
        @Result(property = "currentProvince", column = "current_province"),
        @Result(property = "currentCity", column = "current_city"),
        @Result(property = "university", column = "university"),
        @Result(property = "interests", column = "interests"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "tripCount", column = "trip_count"),
        @Result(property = "completedTrips", column = "completed_trips"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Optional<User> findById(Long id);
    
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    @ResultMap("userResult")
    Optional<User> findByPhone(String phone);
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    @ResultMap("userResult")
    Optional<User> findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE status = 'ACTIVE' " +
            "AND hometown_province = #{province} AND hometown_city = #{city} " +
            "ORDER BY rating DESC LIMIT #{limit}")
    @ResultMap("userResult")
    List<User> findUsersByHometown(String province, String city, int limit);
    
    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone}")
    int countByPhone(String phone);
    
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
    
    @Update("UPDATE users SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(Long id, String password);
    
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(Long id, String status);
}