package com.revature.expensemanager;

public record SummaryRecord(
        String label,
        int expenseCount,
        double totalAmount,
        int approvedCount,
        int deniedCount,
        int pendingCount
) {
}
