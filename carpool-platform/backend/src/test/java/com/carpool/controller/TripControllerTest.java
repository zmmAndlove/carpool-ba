package com.carpool.controller;

import com.carpool.dto.TripDTO;
import com.carpool.entity.Trip;
import com.carpool.entity.User;
import com.carpool.service.JwtService;
import com.carpool.service.TripService;
import com.carpool.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testCreateTripSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(1L);
        trip.setTripType("offer");
        trip.setDepartureProvince("北京市");
        trip.setDepartureCity("北京市");
        trip.setDestinationProvince("上海市");
        trip.setDestinationCity("上海市");
        trip.setDepartureTime(LocalDateTime.now());
        trip.setSeatsTotal(4);
        trip.setSeatsAvailable(4);
        trip.setPricePerPerson(200.0);

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.createTrip(anyLong(), any(TripDTO.class))).thenReturn(trip);

        mockMvc.perform(post("/trips")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tripType\":\"offer\",\"departureProvince\":\"北京市\",\"departureCity\":\"北京市\",\"destinationProvince\":\"上海市\",\"destinationCity\":\"上海市\",\"departureTime\":\"2026-04-01T08:00:00\",\"seatsTotal\":4,\"pricePerPerson\":200}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("行程发布成功"))
                .andExpect(jsonPath("$.trip.id").value(1))
                .andExpect(jsonPath("$.trip.tripType").value("offer"));
    }

    @Test
    public void testCreateTripFailure() throws Exception {
        // 模拟服务异常
        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.createTrip(anyLong(), any(TripDTO.class))).thenThrow(new RuntimeException("创建行程失败"));

        mockMvc.perform(post("/trips")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tripType\":\"offer\",\"departureProvince\":\"北京市\",\"departureCity\":\"北京市\",\"destinationProvince\":\"上海市\",\"destinationCity\":\"上海市\",\"departureTime\":\"2026-04-01T08:00:00\",\"seatsTotal\":4,\"pricePerPerson\":200}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("创建行程失败"));
    }

    @Test
    public void testGetTripSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(1L);
        trip.setTripType("offer");
        trip.setDepartureProvince("北京市");
        trip.setDepartureCity("北京市");
        trip.setDestinationProvince("上海市");
        trip.setDestinationCity("上海市");

        User user = new User();
        user.setId(1L);
        user.setRealName("Test User");
        user.setPhone("13800138000");

        when(tripService.getTripById(anyLong())).thenReturn(Optional.of(trip));
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/trips/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trip.id").value(1))
                .andExpect(jsonPath("$.trip.tripType").value("offer"))
                .andExpect(jsonPath("$.publisher.realName").value("Test User"));
    }

    @Test
    public void testGetTripNotFound() throws Exception {
        // 模拟行程不存在
        when(tripService.getTripById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/trips/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("行程不存在"));
    }

    @Test
    public void testGetMyTrips() throws Exception {
        // 模拟JWT服务
        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);

        // 模拟用户服务
        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");
        when(userService.getUserByPhone("13800138000")).thenReturn(Optional.of(user));

        // 模拟行程服务
        List<Trip> trips = new ArrayList<>();
        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setUserId(1L);
        trip1.setTripType("offer");
        trips.add(trip1);

        when(tripService.getUserTrips(1L, null)).thenReturn(trips);

        mockMvc.perform(get("/trips/my")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trips.length()").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    public void testSearchTrips() throws Exception {
        // 模拟服务调用
        List<Trip> trips = new ArrayList<>();
        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setUserId(1L);
        trip1.setTripType("offer");
        trip1.setDepartureProvince("北京市");
        trip1.setDepartureCity("北京市");
        trip1.setDestinationProvince("上海市");
        trip1.setDestinationCity("上海市");
        trip1.setDepartureTime(LocalDateTime.now());
        trip1.setSeatsAvailable(4);
        trip1.setSeatsTotal(4);
        trip1.setStatus("pending");
        trip1.setCreatedAt(LocalDateTime.now());
        trip1.setUpdatedAt(LocalDateTime.now());
        trips.add(trip1);

        // 使用正确的参数值，确保模拟能够正确生效
        when(tripService.searchTrips("北京市", null, "上海市", null, null, null, null, 1, 20)).thenReturn(trips);
        when(tripService.countTrips("北京市", null, "上海市", null, null, null, null)).thenReturn(1);

        mockMvc.perform(get("/trips/search")
                .param("departureProvince", "北京市")
                .param("destinationProvince", "上海市"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trips.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    public void testUpdateTripSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(1L);
        trip.setTripType("offer");
        trip.setDepartureProvince("北京市");
        trip.setDepartureCity("北京市");
        trip.setDestinationProvince("上海市");
        trip.setDestinationCity("上海市");

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.updateTrip(anyLong(), anyLong(), any(TripDTO.class))).thenReturn(trip);

        mockMvc.perform(put("/trips/1")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tripType\":\"offer\",\"departureProvince\":\"北京市\",\"departureCity\":\"北京市\",\"destinationProvince\":\"上海市\",\"destinationCity\":\"上海市\",\"departureTime\":\"2026-04-01T08:00:00\",\"seatsTotal\":4,\"pricePerPerson\":250}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("行程更新成功"))
                .andExpect(jsonPath("$.trip.id").value(1));
    }

    @Test
    public void testDeleteTripSuccess() throws Exception {
        // 模拟服务调用
        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(tripService).deleteTrip(anyLong(), anyLong());

        mockMvc.perform(delete("/trips/1")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("行程删除成功"));
    }

    @Test
    public void testJoinTripSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(2L);
        trip.setSeatsAvailable(3);

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.joinTrip(anyLong(), anyLong())).thenReturn(trip);

        mockMvc.perform(post("/trips/1/join")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("加入行程成功"))
                .andExpect(jsonPath("$.trip.id").value(1))
                .andExpect(jsonPath("$.trip.seatsAvailable").value(3));
    }

    @Test
    public void testCancelJoinSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(2L);
        trip.setSeatsAvailable(4);

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.cancelJoin(anyLong(), anyLong())).thenReturn(trip);

        mockMvc.perform(post("/trips/1/cancel")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("取消加入成功"))
                .andExpect(jsonPath("$.trip.id").value(1))
                .andExpect(jsonPath("$.trip.seatsAvailable").value(4));
    }

    @Test
    public void testCompleteTripSuccess() throws Exception {
        // 模拟服务调用
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setUserId(1L);
        trip.setStatus("completed");

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");

        when(jwtService.extractUsername(anyString())).thenReturn("13800138000");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.isTokenValid(anyString(), any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn(true);
        when(userService.getUserByPhone(anyString())).thenReturn(Optional.of(user));
        when(tripService.completeTrip(anyLong(), anyLong())).thenReturn(trip);

        mockMvc.perform(post("/trips/1/complete")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("行程完成"))
                .andExpect(jsonPath("$.trip.id").value(1))
                .andExpect(jsonPath("$.trip.status").value("completed"));
    }
}
