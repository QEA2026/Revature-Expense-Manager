package com.revature.expensemanager;

import java.io.Console;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public final class ManagerApp {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService = new AuthService();
    private final ExpenseRepository expenseRepository = new ExpenseRepository();

    public static void main(String[] args) {
        ManagerApp app = new ManagerApp();
        app.run();
    }

    private void run() {
        try {
            Database.initializeDatabase();
        } catch (SQLException error) {
            System.out.println("Unable to initialize the shared database: " + error.getMessage());
            return;
        }

        while (true) {
            System.out.println("\nExpense Manager - Manager");
            System.out.println("1. Log in");
            System.out.println("2. Exit");
            String choice = prompt("Choose 1 or 2: ");

            if ("1".equals(choice)) {
                ManagerUser manager = login();
                if (manager != null) {
                    managerMenu(manager);
                }
            } else if ("2".equals(choice)) {
                System.out.println("Goodbye.");
                return;
            } else {
                System.out.println("Please choose 1 or 2.");
            }
        }
    }

    private ManagerUser login() {
        String username = prompt("Username: ");
        String password = readPassword();
        try {
            ManagerUser manager = authService.authenticateManager(username, password);
            System.out.println("\nWelcome, " + manager.username() + ".");
            return manager;
        } catch (IllegalArgumentException | SQLException error) {
            System.out.println("Login failed: " + error.getMessage());
            return null;
        }
    }

    private void managerMenu(ManagerUser manager) {
        while (true) {
            System.out.println("\nManager Menu - " + manager.username());
            System.out.println("1. See pending expenses");
            System.out.println("2. Review an expense");
            System.out.println("3. See all expenses");
            System.out.println("4. Employee summary");
            System.out.println("5. Category summary");
            System.out.println("6. Date range report");
            System.out.println("7. Log out");
            String choice = prompt("Choose 1 to 7: ");

            try {
                switch (choice) {
                    case "1" -> displayExpenses("Pending expenses", expenseRepository.listPendingExpenses());
                    case "2" -> reviewExpense(manager);
                    case "3" -> displayExpenses("All expenses", expenseRepository.listAllExpenses());
                    case "4" -> displaySummary("Employee summary", expenseRepository.summarizeByEmployee());
                    case "5" -> displaySummary("Category summary", expenseRepository.summarizeByCategory());
                    case "6" -> reportByDateRange();
                    case "7" -> {
                        System.out.println("Logging out.");
                        return;
                    }
                    default -> System.out.println("Please choose a menu option from 1 to 7.");
                }
            } catch (IllegalArgumentException | SQLException error) {
                System.out.println("Action failed: " + error.getMessage());
            }
        }
    }

    private void reviewExpense(ManagerUser manager) throws SQLException {
        List<ExpenseRecord> pendingExpenses = expenseRepository.listPendingExpenses();
        displayExpenses("Pending expenses", pendingExpenses);
        if (pendingExpenses.isEmpty()) {
            return;
        }

        int expenseId = parsePositiveInt(prompt("Type the expense ID to review: "), "Expense ID");
        ExpenseRecord expense = expenseRepository.findPendingExpenseById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Pending expense not found."));

        String decision = prompt("Type approved or denied: ").trim().toLowerCase();
        if (!"approved".equals(decision) && !"denied".equals(decision)) {
            throw new IllegalArgumentException("Decision must be approved or denied.");
        }

        String comment = prompt("Comment: ");
        expenseRepository.reviewExpense(expense.id(), manager, decision, comment, LocalDate.now());
        System.out.println("Expense " + expense.id() + " is now " + decision + ".");
    }

    private void reportByDateRange() throws SQLException {
        LocalDate startDate = parseDate(prompt("Start date (YYYY-MM-DD): "));
        LocalDate endDate = parseDate(prompt("End date (YYYY-MM-DD): "));
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after the start date.");
        }

        displayExpenses(
                "Expenses from " + startDate + " to " + endDate,
                expenseRepository.findByDateRange(startDate, endDate)
        );
    }

    private void displayExpenses(String title, List<ExpenseRecord> expenses) {
        System.out.println("\n" + title);
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        String divider = "-".repeat(82);
        System.out.println(divider);
        for (ExpenseRecord expense : expenses) {
            System.out.printf(
                    "[%d] %s | %s | $%.2f | %s | employee=%s%n",
                    expense.id(),
                    expense.expenseDate(),
                    expense.category(),
                    expense.amount(),
                    expense.status(),
                    expense.employeeUsername()
            );
            System.out.println("Description: " + expense.description());
            System.out.println("Reviewer: " + (expense.reviewerUsername().isBlank() ? "Not reviewed yet" : expense.reviewerUsername()));
            System.out.println("Comment: " + (expense.comment().isBlank() ? "-" : expense.comment()));
            if (!expense.reviewDate().isBlank()) {
                System.out.println("Review date: " + expense.reviewDate());
            }
            System.out.println(divider);
        }
    }

    private void displaySummary(String title, List<SummaryRecord> rows) {
        System.out.println("\n" + title);
        if (rows.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        System.out.printf(
                "%-16s %8s %12s %10s %10s %10s%n",
                "Label",
                "Count",
                "Total",
                "Approved",
                "Denied",
                "Pending"
        );
        System.out.println("-".repeat(74));
        for (SummaryRecord row : rows) {
            System.out.printf(
                    "%-16s %8d %12.2f %10d %10d %10d%n",
                    row.label(),
                    row.expenseCount(),
                    row.totalAmount(),
                    row.approvedCount(),
                    row.deniedCount(),
                    row.pendingCount()
            );
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private String readPassword() {
        Console console = System.console();
        if (console != null) {
            char[] password = console.readPassword("Password: ");
            if (password != null) {
                return new String(password);
            }
        }

        System.out.print("Password: ");
        return scanner.nextLine();
    }

    private int parsePositiveInt(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than zero.");
            }
            return parsed;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Dates must use the YYYY-MM-DD format.");
        }
    }
}
