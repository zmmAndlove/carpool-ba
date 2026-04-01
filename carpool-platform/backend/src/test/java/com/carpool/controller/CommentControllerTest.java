package com.carpool.controller;

import com.carpool.entity.Comment;
import com.carpool.entity.User;
import com.carpool.dto.CommentDTO;
import com.carpool.service.CommentService;
import com.carpool.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    @MockBean
    private com.carpool.service.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testCreateCommentSuccess() throws Exception {
        // 模拟用户服务
        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setUsername("testuser");
        user.setAvatar("test-avatar.jpg");
        when(userService.getUserByUsername("testuser")).thenReturn(java.util.Optional.of(user));

        // 模拟评论服务
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("这是一条测试评论，长度足够长");
        comment.setRating(5);
        when(commentService.createComment(any(User.class), anyString(), anyInt())).thenReturn(comment);

        // 执行测试
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"这是一条测试评论，长度足够长\",\"rating\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("建议提交成功"))
                .andExpect(jsonPath("$.comment.id").value(1))
                .andExpect(jsonPath("$.comment.content").value("这是一条测试评论，长度足够长"))
                .andExpect(jsonPath("$.comment.rating").value(5));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testCreateCommentFailure() throws Exception {
        // 模拟用户服务
        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setUsername("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(java.util.Optional.of(user));

        // 模拟评论服务异常
        when(commentService.createComment(any(User.class), anyString(), anyInt())).thenThrow(new RuntimeException("评论失败"));

        // 执行测试
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"这是一条测试评论，长度足够长\",\"rating\":5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("评论失败"));
    }

    @Test
    public void testGetComments() throws Exception {
        // 模拟服务调用
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setUserId(1L);
        comment1.setContent("评论1");
        comment1.setRating(5);
        comments.add(comment1);

        when(commentService.getComments(anyInt(), anyInt())).thenReturn(comments);
        when(commentService.getCommentsCount()).thenReturn(1);

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }


}