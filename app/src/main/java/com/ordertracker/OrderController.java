package com.ordertracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// @RestController = this class handles HTTP requests and returns JSON responses
// @RequestMapping = all URLs in this class start with /api/orders
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    // Spring automatically injects the OrderService here
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders/{orderId}
    // Example: GET /api/orders/ORD001
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        log.info("GET request received for order: {}", orderId);
        return orderService.getOrder(orderId);
    }

    // POST /api/orders
    // Body: { "product": "Laptop", "amount": 75000 }
    @PostMapping
    public Order createOrder(@RequestBody Map<String, Object> body) {
        String product = (String) body.get("product");
        double amount  = Double.parseDouble(body.get("amount").toString());

        log.info("POST request to create order - product: {}, amount: {}", product, amount);
        return orderService.createOrder(product, amount);
    }

    // GET /api/orders/health
    // Simple health check endpoint
    @GetMapping("/health")
    public Map<String, String> health() {
        log.info("Health check called");
        return Map.of("status", "UP", "service", "order-tracker");
    }
}
