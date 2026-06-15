package com.revature.expensemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ExpenseRepository {
    private static final String EXPENSE_SELECT = """
            SELECT
                e.id,
                employee.username AS employee_username,
                e.amount,
                e.category,
                e.description,
                e.date AS expense_date,
                a.status,
                COALESCE(a.comment, '') AS comment,
                COALESCE(a.review_date, '') AS review_date,
                COALESCE(reviewer.username, '') AS reviewer_username
            FROM expenses e
            INNER JOIN users employee ON employee.id = e.user_id
            INNER JOIN approvals a ON a.expense_id = e.id
            LEFT JOIN users reviewer ON reviewer.id = a.reviewer
            """;

    public List<ExpenseRecord> listPendingExpenses() throws SQLException {
        return fetchExpenses(
                EXPENSE_SELECT + " WHERE a.status = 'pending' ORDER BY e.date DESC, e.id DESC",
                statement -> {
                }
        );
    }

    public List<ExpenseRecord> listAllExpenses() throws SQLException {
        return fetchExpenses(
                EXPENSE_SELECT + " ORDER BY e.date DESC, e.id DESC",
                statement -> {
                }
        );
    }

    public Optional<ExpenseRecord> findPendingExpenseById(int expenseId) throws SQLException {
        List<ExpenseRecord> expenses = fetchExpenses(
                EXPENSE_SELECT + " WHERE e.id = ? AND a.status = 'pending'",
                statement -> statement.setInt(1, expenseId)
        );
        if (expenses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(expenses.getFirst());
    }

    public void reviewExpense(int expenseId, ManagerUser manager, String status, String comment, LocalDate reviewDate)
            throws SQLException {
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(
                     """
                     UPDATE approvals
                     SET status = ?, reviewer = ?, comment = ?, review_date = ?
                     WHERE expense_id = ?
                     """
             )) {
            statement.setString(1, status);
            statement.setInt(2, manager.id());
            statement.setString(3, comment == null || comment.isBlank() ? null : comment.trim());
            statement.setString(4, reviewDate.toString());
            statement.setInt(5, expenseId);
            statement.executeUpdate();
        }
    }

    public List<SummaryRecord> summarizeByEmployee() throws SQLException {
        return fetchSummary(
                """
                SELECT
                    employee.username AS label,
                    COUNT(*) AS expense_count,
                    ROUND(SUM(e.amount), 2) AS total_amount,
                    SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) AS approved_count,
                    SUM(CASE WHEN a.status = 'denied' THEN 1 ELSE 0 END) AS denied_count,
                    SUM(CASE WHEN a.status = 'pending' THEN 1 ELSE 0 END) AS pending_count
                FROM expenses e
                INNER JOIN users employee ON employee.id = e.user_id
                INNER JOIN approvals a ON a.expense_id = e.id
                GROUP BY employee.username
                ORDER BY employee.username
                """,
                statement -> {
                }
        );
    }

    public List<SummaryRecord> summarizeByCategory() throws SQLException {
        return fetchSummary(
                """
                SELECT
                    e.category AS label,
                    COUNT(*) AS expense_count,
                    ROUND(SUM(e.amount), 2) AS total_amount,
                    SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) AS approved_count,
                    SUM(CASE WHEN a.status = 'denied' THEN 1 ELSE 0 END) AS denied_count,
                    SUM(CASE WHEN a.status = 'pending' THEN 1 ELSE 0 END) AS pending_count
                FROM expenses e
                INNER JOIN approvals a ON a.expense_id = e.id
                GROUP BY e.category
                ORDER BY e.category
                """,
                statement -> {
                }
        );
    }

    public List<ExpenseRecord> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        return fetchExpenses(
                EXPENSE_SELECT + " WHERE DATE(e.date) BETWEEN DATE(?) AND DATE(?) ORDER BY e.date DESC, e.id DESC",
                statement -> {
                    statement.setString(1, startDate.toString());
                    statement.setString(2, endDate.toString());
                }
        );
    }

    private List<ExpenseRecord> fetchExpenses(String sql, SqlBinder binder) throws SQLException {
        List<ExpenseRecord> records = new ArrayList<>();
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(
                            new ExpenseRecord(
                                    resultSet.getInt("id"),
                                    resultSet.getString("employee_username"),
                                    resultSet.getDouble("amount"),
                                    resultSet.getString("category"),
                                    resultSet.getString("description"),
                                    resultSet.getString("expense_date"),
                                    resultSet.getString("status"),
                                    resultSet.getString("comment"),
                                    resultSet.getString("review_date"),
                                    resultSet.getString("reviewer_username")
                            )
                    );
                }
            }
        }
        return records;
    }

    private List<SummaryRecord> fetchSummary(String sql, SqlBinder binder) throws SQLException {
        List<SummaryRecord> rows = new ArrayList<>();
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(
                            new SummaryRecord(
                                    resultSet.getString("label"),
                                    resultSet.getInt("expense_count"),
                                    resultSet.getDouble("total_amount"),
                                    resultSet.getInt("approved_count"),
                                    resultSet.getInt("denied_count"),
                                    resultSet.getInt("pending_count")
                            )
                    );
                }
            }
        }
        return rows;
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
