package com.sa1mone.service;

import com.sa1mone.request.DeliveryRequest;
import com.sa1mone.request.ReserveStockRequest;
import com.sa1mone.response.OrderResponse;
import com.sa1mone.response.ProductResponse;
import com.sa1mone.response.UserResponse;
import com.sa1mone.entity.Order;
import com.sa1mone.entity.OrderItem;
import com.sa1mone.enums.OrderStatus;
import com.sa1mone.repo.OrderRepository;
import com.sa1mone.request.OrderRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RestTemplateBuilder restTemplateBuilder) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("The user has not made any orders yet"));
    }

    @Override
    public Order createOrder(String email, OrderRequest orderRequest) {
        LocalDateTime now = LocalDateTime.now();
        UserResponse userResponse = fetchUserInfoByEmail(email);

        String deliveryAddress = orderRequest.getDeliveryAddress() != null && !orderRequest.getDeliveryAddress().isEmpty()
                ? orderRequest.getDeliveryAddress()
                : userResponse.getAddress();

        Order order = new Order();
        order.setUserId(userResponse.getId());
        order.setDeliveryAddress(deliveryAddress);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Set<UUID> productIds = orderRequest.getItems().stream()
                .map(OrderRequest.OrderItemRequest::getProductId)
                .collect(Collectors.toSet());

        Map<String, ProductResponse> productResponses = fetchProductInfoBatch(productIds);

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    boolean isAvailable = checkStock(item.getProductId(), item.getQuantity(), productResponses);
                    System.out.println(isAvailable);
                    if (!isAvailable) {
                        throw new IllegalArgumentException("Not enough stock for product: " + item.getProductId());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());

                    ProductResponse productResponse = productResponses.get(item.getProductId().toString());
                    if (productResponse == null) {
                        throw new IllegalStateException("Product info not found for ID: " + item.getProductId());
                    }
                    orderItem.setPrice(productResponse.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        order.setTotalPrice(totalPrice);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        orderItems.forEach(item -> reserveStock(item.getProductId(), item.getQuantity()));
        createDeliveryForOrder(savedOrder);
        return savedOrder;
    }

    public boolean checkStock(UUID productId, int quantity, Map<String, ProductResponse> productResponses) {
        ProductResponse product = productResponses.get(productId.toString());
        if (product == null) {
            throw new IllegalStateException("Product info not found for ID: " + productId);
        }
        return product.getQuantity() >= quantity;
    }

    private Map<String, ProductResponse> fetchProductInfoBatch(Set<UUID> productIds) {
        String catalogServiceUrl = "http://catalog-service:8082/management/product/batch-full";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<String> productIdsList = productIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(productIdsList, headers);

        try {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);
            RestTemplate restTemplateWithTimeout = new RestTemplate(factory);

            ResponseEntity<Map> response = restTemplateWithTimeout.postForEntity(
                    catalogServiceUrl, requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, ProductResponse> result = new HashMap<>();
                Map<String, Map> body = response.getBody();
                if (body != null) {
                    for (Map.Entry<String, Map> entry : body.entrySet()) {
                        String id = entry.getKey();
                        Map data = entry.getValue();
                        if (data != null) {
                            ProductResponse productResponse = new ProductResponse();
                            productResponse.setName((String) data.get("name"));
                            productResponse.setDescription((String) data.get("description"));
                            productResponse.setPrice((Double) data.get("price"));
                            productResponse.setQuantity((Integer) data.get("quantity"));
                            result.put(id, productResponse);
                        }
                    }
                }

                for (UUID id : productIds) {
                    result.putIfAbsent(id.toString(), new ProductResponse("Unknown", "Not found", 0.0, 0));
                }
                return result;
            } else {
                throw new ServiceUnavailableException("Catalog Service returned error: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Catalog Service is unavailable: " + e.getMessage());
        }
    }

    public void reserveStock(UUID productId, int quantity) {
        String inventoryServiceUrl = "http://inventory-service:8085/management/inventory/reserve";

        ReserveStockRequest request = new ReserveStockRequest(productId, quantity);

        try {
            restTemplate.postForEntity(inventoryServiceUrl, request, Void.class);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Inventory Service is unavailable: " + e.getMessage());
        }
    }

    private void createDeliveryForOrder(Order order) {
        String deliveryServiceUrl = "http://delivery-service:8086/management/delivery/create";

        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setOrderId(order.getId());
        deliveryRequest.setUserId(order.getUserId());
        deliveryRequest.setDeliveryAddress(order.getDeliveryAddress());

        try {
            restTemplate.postForEntity(deliveryServiceUrl, deliveryRequest, Void.class);
        } catch (RestClientException ex) {
            throw new RuntimeException("Error contacting Delivery Service");
        }
    }

    @Override
    public List<OrderResponse> getAuthenticatedUserOrders(String email) {
        UserResponse userResponse = fetchUserInfoByEmail(email);

        List<Order> orders = getOrdersByUserId(userResponse.getId());
        return orders.stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderStatus checkOrderStatus(String email, UUID orderId) {
        UserResponse userResponse = fetchUserInfoByEmail(email);
        UUID userId = userResponse.getId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("This order was not found for user"));

        if (!order.getUserId().equals(userId)) {
            throw new EntityNotFoundException("This order was not found for user");
        }
        return order.getStatus();
    }

    @Override
    public OrderStatus cancelOrder(String email, UUID orderId) {
        UserResponse userResponse = fetchUserInfoByEmail(email);
        UUID userId = userResponse.getId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("This order was not found for user"));

        if (!order.getUserId().equals(userId)) {
            throw new EntityNotFoundException("This order was not found for user");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already canceled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            restoreStock(item.getProductId(), item.getQuantity());
        }

        cancelDelivery(order.getId());
        return order.getStatus();
    }

    private void cancelDelivery(UUID orderId) {
        String url = "http://delivery-service:8086/management/delivery/" + orderId + "/cancel";

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Failed to cancel delivery: " + e.getMessage());
        }
    }

    public void restoreStock(UUID productId, int quantity) {
        String inventoryServiceUrl = "http://inventory-service:8085/management/inventory/restore";

        ReserveStockRequest request = new ReserveStockRequest(productId, quantity);

        try {
            restTemplate.postForEntity(inventoryServiceUrl, request, Void.class);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Inventory Service is unavailable: " + e.getMessage());
        }
    }

    private OrderResponse mapOrderToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(this::mapOrderItemToResponse)
                .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }

    private OrderResponse.OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();

        ProductResponse product = fetchProductInfoById(item.getProductId());
        itemResponse.setProductName(product.getName());

        itemResponse.setQuantity(item.getQuantity());
        itemResponse.setPrice(item.getPrice());

        return itemResponse;
    }

    private UserResponse fetchUserInfoByEmail(String email) {
        String userServiceUrl = "http://user-service:8081/management/user/find-by-email?email=" + email;

        try {
            ResponseEntity<UserResponse> response = restTemplate.getForEntity(userServiceUrl, UserResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new EntityNotFoundException("User not found");
            }
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("User Service is unavailable: " + e.getMessage());
        }
    }

    private ProductResponse fetchProductInfoById(UUID productId) {
        String productServiceUrl = "http://catalog-service:8082/management/product/find-product-by-id?id=" + productId;

        try {
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(productServiceUrl, ProductResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new EntityNotFoundException("Product not found");
            }
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Catalog Service is unavailable: " + e.getMessage());
        }
    }
}