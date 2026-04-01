package com.carpool.controller;

import com.carpool.service.JwtService;
import com.carpool.service.TripService;
import com.carpool.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicController.class)
public class PublicControllerTest {

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

    @Test
    @WithMockUser
    public void testGetStatsSuccess() throws Exception {
        // 模拟服务调用
        when(tripService.getTripCount()).thenReturn(100);
        when(tripService.getMatchedTripCount()).thenReturn(80);
        when(userService.getAverageRating()).thenReturn(4.5);
        when(tripService.getAverageWaitTime()).thenReturn(15);

        mockMvc.perform(get("/public/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripCount").value(100))
                .andExpect(jsonPath("$.matchedTripCount").value(80))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.averageWaitTime").value(15));
    }

    @Test
    @WithMockUser
    public void testGetStatsFailure() throws Exception {
        // 模拟服务异常
        when(tripService.getTripCount()).thenThrow(new RuntimeException("服务异常"));

        mockMvc.perform(get("/public/stats"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("获取统计数据失败"));
    }
}
