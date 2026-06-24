package com.ordertracker;

// This is the Order object - represents one order in our system
public class Order {

    private String id;
    private String product;
    private String status;
    private double amount;

    // Constructor - creates an Order with all fields
    public Order(String id, String product, String status, double amount) {
        this.id = id;
        this.product = product;
        this.status = status;
        this.amount = amount;
    }

    // Getters - allow other classes to read the values
    public String getId()      { return id; }
    public String getProduct() { return product; }
    public String getStatus()  { return status; }
    public double getAmount()  { return amount; }
}
