package com.ordertracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// @Service tells Spring: this class contains business logic
@Service
public class OrderService {

    // Logger - this is the "pen" that writes log lines
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // In-memory database - stores orders in a Map (no real DB needed)
    // Key = orderId, Value = Order object
    private static final Map<String, Order> orders = new HashMap<>();

    // Pre-load some sample orders when the app starts
    static {
        orders.put("ORD001", new Order("ORD001", "Laptop",    "DELIVERED", 75000.00));
        orders.put("ORD002", new Order("ORD002", "Phone",     "SHIPPED",   45000.00));
        orders.put("ORD003", new Order("ORD003", "Headphones","PENDING",   3500.00));
    }

    // Find an order by ID
    public Order getOrder(String orderId) {
        log.info("Looking up order: {}", orderId);

        Order order = orders.get(orderId);

        if (order == null) {
            // This log line will appear as ERROR in Grafana/Loki
            log.error("Order not found: {}", orderId);
            throw new OrderNotFoundException("Order not found: " + orderId);
        }

        log.info("Order found: {} - Status: {}", orderId, order.getStatus());
        return order;
    }

    // Create a new order
    public Order createOrder(String product, double amount) {
        // Generate a simple order ID
        String orderId = "ORD" + String.format("%03d", orders.size() + 1);

        Order order = new Order(orderId, product, "PENDING", amount);
        orders.put(orderId, order);

        log.info("New order created: {} for product: {} amount: {}", orderId, product, amount);
        return order;
    }
}
