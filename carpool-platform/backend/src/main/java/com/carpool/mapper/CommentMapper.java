package com.carpool.mapper;

import com.carpool.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    @Insert("INSERT INTO comments (user_id, username, avatar, content, rating, likes, parent_id, created_at, updated_at) " +
            "VALUES (#{userId}, #{username}, #{avatar}, #{content}, #{rating}, #{likes}, #{parentId}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    @Select("SELECT * FROM comments ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Comment> findAll(int limit, int offset);
    
    @Select("SELECT COUNT(*) FROM comments")
    int count();
    
    @Update("UPDATE comments SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(Long id);
    
    @Select("SELECT * FROM comments WHERE id = #{id}")
    Comment findById(Long id);
    
    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(Long id);
}