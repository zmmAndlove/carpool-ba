package com.carpool.controller;

import com.carpool.dto.LoginDTO;
import com.carpool.dto.UserDTO;
import com.carpool.entity.User;
import com.carpool.service.JwtService;
import com.carpool.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testRegisterSuccess() throws Exception {
        // 模拟请求数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138000");
        userDTO.setRealName("Test User");

        // 模拟服务调用
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setPhone("13800138000");
        user.setRealName("Test User");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.register(any(UserDTO.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-token");
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"phone\":\"13800138000\",\"realName\":\"Test User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    public void testRegisterFailure() throws Exception {
        // 模拟服务异常
        when(userService.register(any(UserDTO.class))).thenThrow(new RuntimeException("用户名已存在"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"phone\":\"13800138000\",\"realName\":\"Test User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("用户名已存在"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // 模拟请求数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        // 模拟服务调用
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPhone("13800138000");
        user.setRealName("Test User");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-token");
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        // 模拟认证失败
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("认证失败"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("用户名或密码错误"));
    }

    @Test
    public void testGetCurrentUserSuccess() throws Exception {
        // 模拟服务调用
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPhone("13800138000");
        user.setRealName("Test User");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    public void testGetCurrentUserFailure() throws Exception {
        // 模拟服务异常
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("获取用户信息失败"));
    }
}
