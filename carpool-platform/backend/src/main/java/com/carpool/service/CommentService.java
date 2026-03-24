package com.carpool.service;

import com.carpool.entity.Comment;
import com.carpool.mapper.CommentMapper;
import com.carpool.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    
    private final CommentMapper commentMapper;
    
    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }
    
    @Transactional
    public Comment createComment(User user, String content, int rating) {
        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setAvatar(user.getAvatar());
        comment.setContent(content);
        comment.setRating(rating);
        
        commentMapper.insert(comment);
        return comment;
    }
    
    @Transactional
    public Comment replyToComment(User user, String content, Long parentId) {
        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setAvatar(user.getAvatar());
        comment.setContent(content);
        comment.setRating(5); // 回复设置默认评分为 5
        // 不设置 parentId 字段，因为数据库表中没有这个字段
        
        commentMapper.insert(comment);
        return comment;
    }
    
    public List<Comment> getComments(int page, int size) {
        int offset = (page - 1) * size;
        return commentMapper.findAll(size, offset);
    }
    
    public int getCommentsCount() {
        return commentMapper.count();
    }
    
    @Transactional
    public void likeComment(Long commentId) {
        commentMapper.incrementLikes(commentId);
    }
    
    public Comment getCommentById(Long commentId) {
        return commentMapper.findById(commentId);
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        commentMapper.deleteById(commentId);
    }
}