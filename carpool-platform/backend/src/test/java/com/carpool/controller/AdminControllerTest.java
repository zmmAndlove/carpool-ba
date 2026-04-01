package com.carpool.controller;

import com.carpool.entity.User;
import com.carpool.entity.Trip;
import com.carpool.service.JwtService;
import com.carpool.service.UserService;
import com.carpool.service.TripService;
import com.carpool.service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TripService tripService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private CommentService commentService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllUsers() throws Exception {
        // 模拟服务调用
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setRealName("Test User");
        users.add(user1);

        when(userService.getUsers(anyInt(), anyInt())).thenReturn(users);
        when(userService.getUserCount()).thenReturn(1);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllTrips() throws Exception {
        // 模拟服务调用
        List<Trip> trips = new ArrayList<>();
        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setTripType("offer");
        trip1.setDepartureProvince("北京市");
        trip1.setDestinationProvince("上海市");
        trips.add(trip1);

        when(tripService.getTrips(anyInt(), anyInt())).thenReturn(trips);
        when(tripService.getTripCount()).thenReturn(1);

        mockMvc.perform(get("/admin/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trips.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }
}
