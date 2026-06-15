package com.revature.expensemanager;

public record ExpenseRecord(
        int id,
        String employeeUsername,
        double amount,
        String category,
        String description,
        String expenseDate,
        String status,
        String comment,
        String reviewDate,
        String reviewerUsername
) {
}
