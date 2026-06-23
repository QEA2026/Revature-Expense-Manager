package com.revature.models;

public class Expense {
    private int id;
    private int userId;
    private double amount;
    private String description;
    private String date;
    private String category;

    // When i want just the object first
    public Expense() {}

    // When  i want to just retrieve data
    public Expense(int id, int userId, double amount, String description, String date, String category) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    // When i want to insert data
    public Expense(int userId, double amount, String description, String date, String category) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @java.lang.Override
    public java.lang.String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", category=" + category  + '\'' +
                '}';
    }
}
