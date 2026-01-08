package com.sa1mone.service;

import com.sa1mone.entity.Order;
import com.sa1mone.entity.OrderItem;
import com.sa1mone.enums.OrderStatus;
import com.sa1mone.repo.OrderRepository;
import com.sa1mone.response.ProductResponse;
import com.sa1mone.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        orderService = new OrderServiceImpl(orderRepository, restTemplateBuilder);
    }

    @Test
    void getOrderById_shouldReturnOrder_whenExists() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setId(id);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(id);
        assertEquals(id, result.getId());
    }

    @Test
    void getOrderById_shouldThrowNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(id));
    }

    @Test
    void checkStock_shouldReturnTrue_whenEnoughQuantity() {
        UUID productId = UUID.randomUUID();
        ProductResponse product = new ProductResponse("Test", "Desc", 10.0, 5);
        Map<String, ProductResponse> responses = Map.of(productId.toString(), product);

        boolean result = orderService.checkStock(productId, 3, responses);
        assertTrue(result);
    }

    @Test
    void checkStock_shouldThrow_whenProductMissing() {
        UUID productId = UUID.randomUUID();
        Map<String, ProductResponse> responses = new HashMap<>();

        assertThrows(IllegalStateException.class,
                () -> orderService.checkStock(productId, 1, responses));
    }

    @Test
    void checkOrderStatus_shouldReturnStatus_whenOrderBelongsToUser() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(OrderStatus.PROCESSING);

        when(restTemplate.getForEntity(anyString(), eq(UserResponse.class)))
                .thenReturn(ResponseEntity.ok(userResponse));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatus status = orderService.checkOrderStatus(email, orderId);
        assertEquals(OrderStatus.PROCESSING, status);
    }

    @Test
    void checkOrderStatus_shouldThrowNotFound_whenOrderNotBelongsToUser() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(UUID.randomUUID()); // інший користувач

        when(restTemplate.getForEntity(anyString(), eq(UserResponse.class)))
                .thenReturn(ResponseEntity.ok(userResponse));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(EntityNotFoundException.class,
                () -> orderService.checkOrderStatus(email, orderId));
    }

    @Test
    void cancelOrder_shouldUpdateStatusAndRestoreStock() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        OrderItem item = new OrderItem();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(2);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(OrderStatus.PROCESSING);
        order.setItems(List.of(item));

        when(restTemplate.getForEntity(anyString(), eq(UserResponse.class)))
                .thenReturn(ResponseEntity.ok(userResponse));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderStatus result = orderService.cancelOrder(email, orderId);
        assertEquals(OrderStatus.CANCELLED, result);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_shouldThrow_whenAlreadyCancelled() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(OrderStatus.CANCELLED);

        when(restTemplate.getForEntity(anyString(), eq(UserResponse.class)))
                .thenReturn(ResponseEntity.ok(userResponse));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.cancelOrder(email, orderId));
    }
}