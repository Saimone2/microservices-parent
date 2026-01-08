package com.sa1mone.controller;

import com.sa1mone.entity.Order;
import com.sa1mone.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderAdminController.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration.class
})
class OrderAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void getUserOrdersById_shouldReturnOrders_whenAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        Order order1 = new Order(); order1.setId(UUID.randomUUID());
        Order order2 = new Order(); order2.setId(UUID.randomUUID());

        when(orderService.getOrdersByUserId(userId)).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/order/from-user")
                        .header("X-Roles", "admin")
                        .param("id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(order2.getId().toString()));
    }

    @Test
    void getUserOrdersById_shouldReturnForbidden_whenNotAuthorized() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/order/from-user")
                        .header("X-Roles", "user")
                        .param("id", userId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderById_shouldReturnOrder_whenProductManager() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(); order.setId(orderId);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        mockMvc.perform(get("/order/{orderId}", orderId)
                        .header("X-Roles", "product_manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void getOrderById_shouldReturnForbidden_whenNotAuthorized() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(get("/order/{orderId}", orderId)
                        .header("X-Roles", "customer"))
                .andExpect(status().isForbidden());
    }
}