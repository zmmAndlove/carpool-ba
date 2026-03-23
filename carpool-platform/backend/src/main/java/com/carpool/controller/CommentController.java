package com.carpool.controller;

import com.carpool.dto.CommentDTO;
import com.carpool.entity.Comment;
import com.carpool.service.CommentService;
import com.carpool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    private final CommentService commentService;
    private final UserService userService;
    
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            java.util.Optional<com.carpool.entity.User> userOpt = userService.getUserByUsername(userDetails.getUsername());
            com.carpool.entity.User user = userOpt.orElseThrow(() -> new RuntimeException("用户不存在"));
            
            Comment comment = commentService.createComment(
                    user,
                    commentDTO.getContent(),
                    commentDTO.getRating()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "建议提交成功");
            response.put("comment", convertToResponse(comment));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Comment> comments = commentService.getComments(page, size);
            int total = commentService.getCommentsCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", comments.stream().map(this::convertToResponse).collect(Collectors.toList()));
            response.put("page", page);
            response.put("size", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取建议失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id) {
        try {
            commentService.likeComment(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "点赞成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "点赞失败");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    private Map<String, Object> convertToResponse(Comment comment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", comment.getId());
        response.put("username", comment.getUsername());
        response.put("avatar", comment.getAvatar());
        response.put("content", comment.getContent());
        response.put("rating", comment.getRating());
        response.put("likes", comment.getLikes());
        response.put("createdAt", comment.getCreatedAt());
        return response;
    }
}