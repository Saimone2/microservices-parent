package com.sa1mone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1mone.entity.Order;
import com.sa1mone.enums.OrderStatus;
import com.sa1mone.request.OrderRequest;
import com.sa1mone.response.OrderResponse;
import com.sa1mone.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration.class
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        String email = "user@example.com";

        OrderRequest request = new OrderRequest();
        request.setDeliveryAddress("Kyiv, Ukraine");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(2);

        request.setItems(List.of(item));

        Order order = new Order();
        order.setId(UUID.randomUUID());

        when(orderService.createOrder(eq(email), any(OrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/order/create")
                        .header("X-User-Email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }


    @Test
    void getAuthenticatedUserOrders_shouldReturnList() throws Exception {
        String email = "user@example.com";
        OrderResponse response1 = new OrderResponse();
        response1.setId(UUID.randomUUID());
        OrderResponse response2 = new OrderResponse();
        response2.setId(UUID.randomUUID());

        when(orderService.getAuthenticatedUserOrders(email)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/order/me")
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(response2.getId().toString()));
    }

    @Test
    void checkOrderStatus_shouldReturnStatus() throws Exception {
        String email = "user@example.com";
        UUID orderId = UUID.randomUUID();

        when(orderService.checkOrderStatus(email, orderId)).thenReturn(OrderStatus.PROCESSING);

        mockMvc.perform(get("/order/{orderId}/status", orderId)
                .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void cancelOrder_shouldReturnCancelledStatus() throws Exception {
        String email = "user@example.com";
        UUID orderId = UUID.randomUUID();

        when(orderService.cancelOrder(email, orderId)).thenReturn(OrderStatus.CANCELLED);

        mockMvc.perform(delete("/order/{orderId}/cancel", orderId)
                        .header("X-User-Email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void createOrder_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        String email = "user@example.com";

        OrderRequest invalid = new OrderRequest();
        invalid.setDeliveryAddress("   ");
        invalid.setItems(List.of());

        mockMvc.perform(post("/order/create")
                        .header("X-User-Email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}