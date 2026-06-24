package com.ordertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = this is the starting point of the entire app
// When you run the JAR, Java looks for this class first
@SpringBootApplication
public class OrderTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderTrackerApplication.class, args);
    }
}
