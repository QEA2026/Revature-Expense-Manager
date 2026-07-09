package com.revature.ui;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.revature.DAOs.ApprovalDAO;
import com.revature.DAOs.ExpenseDAO;
import com.revature.DAOs.UserDAO;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Approval;
import com.revature.models.Expense;
import com.revature.models.User;

import java.util.ArrayList;
import java.util.Scanner;

public class ManagerConsole {

    private final Scanner scanner = new Scanner(System.in);
    private final UserDAO userDAO = new UserDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final ApprovalDAO approvalDAO = new ApprovalDAO();

    public void run() {
        System.out.println("\n=== Manager Console ===");
        User loggedInManager = loginMenu();

        if (loggedInManager == null) {
            System.out.println("Too many failed attempts. Goodbye.");
            return;
        }

        managerMenu(loggedInManager);
    }

    private User loginMenu() {
        int attempts = 0;

        while (attempts < 3) {
            System.out.print("Manager username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Manager password: ");
            String password = scanner.nextLine().trim();

            try {
                User foundUser = userDAO.getUserByUsername(username);

                BCrypt.Result result = BCrypt.verifyer().verify(
                        password.toCharArray(),
                        foundUser.getPassword()
                );

                if (result.verified && "manager".equals(foundUser.getRole())) {
                    System.out.println("\nLogin successful. Welcome, " + foundUser.getUsername() + ".");
                    return foundUser;
                }

                System.out.println("Invalid manager credentials.");
            } catch (ResourceNotFoundException e) {
                System.out.println("Invalid manager credentials.");
            } catch (Exception e) {
                System.out.println("There was a problem during login.");
            }

            attempts++;
        }

        return null;
    }

    private void managerMenu(User manager) {
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. View pending expenses");
            System.out.println("2. Approve an expense");
            System.out.println("3. Deny an expense");
            System.out.println("4. Report by employee");
            System.out.println("5. Report by category");
            System.out.println("6. Report by date");
            System.out.println("7. Find expense by id");
            System.out.println("8. View approval by expense id");
            System.out.println("9. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewPendingExpenses();
                    break;
                case "2":
                    reviewExpense(manager, "approved");
                    break;
                case "3":
                    reviewExpense(manager, "denied");
                    break;
                case "4":
                    reportByEmployee();
                    break;
                case "5":
                    reportByCategory();
                    break;
                case "6":
                    reportByDate();
                    break;
                case "7":
                    findExpenseById();
                    break;
                case "8":
                    findApprovalByExpenseId();
                    break;
                case "9":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void viewPendingExpenses() {
        ArrayList<Expense> pendingExpenses = expenseDAO.getPendingExpenses();
        printExpenseList("Pending Expenses", pendingExpenses);
    }

    private void reviewExpense(User manager, String status) {
        viewPendingExpenses();

        System.out.print("Enter the expense id to " + status + ": ");
        String expenseIdInput = scanner.nextLine().trim();

        if (!expenseIdInput.matches("\\d+")) {
            System.out.println("Please enter a numeric expense id.");
            return;
        }

        int expenseId = Integer.parseInt(expenseIdInput);

        try {
            Expense expense = expenseDAO.getExpenseById(expenseId);
            printSingleExpense(expense);

            System.out.print("Enter a comment: ");
            String comment = scanner.nextLine().trim();

            boolean updated = approvalDAO.updateApproval(
                    expenseId,
                    status,
                    manager.getId(),
                    comment
            );

            if (updated) {
                System.out.println("Expense " + expenseId + " was " + status + ".");
            } else {
                System.out.println("Could not update the expense.");
            }
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("There was a problem updating the expense.");
        }
    }

    private void reportByEmployee() {
        System.out.print("Enter employee id: ");
        String userIdInput = scanner.nextLine().trim();

        if (!userIdInput.matches("\\d+")) {
            System.out.println("Please enter a numeric employee id.");
            return;
        }

        ArrayList<Expense> expenses = expenseDAO.getExpensesByEmployee(Integer.parseInt(userIdInput));
        printExpenseList("Expenses For Employee " + userIdInput, expenses);
    }

    private void reportByCategory() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        ArrayList<Expense> expenses = expenseDAO.getExpensesByCategory(category);
        printExpenseList("Expenses In Category " + category, expenses);
    }

    private void reportByDate() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        ArrayList<Expense> expenses = expenseDAO.getExpenseByDate(date);
        printExpenseList("Expenses On " + date, expenses);
    }

    private void findExpenseById() {
        System.out.print("Enter expense id: ");
        String expenseIdInput = scanner.nextLine().trim();

        if (!expenseIdInput.matches("\\d+")) {
            System.out.println("Please enter a numeric expense id.");
            return;
        }

        try {
            Expense expense = expenseDAO.getExpenseById(Integer.parseInt(expenseIdInput));
            printSingleExpense(expense);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findApprovalByExpenseId() {
        System.out.print("Enter expense id: ");
        String expenseIdInput = scanner.nextLine().trim();

        if (!expenseIdInput.matches("\\d+")) {
            System.out.println("Please enter a numeric expense id.");
            return;
        }

        Approval approval = approvalDAO.getApprovalByExpenseId(Integer.parseInt(expenseIdInput));

        if (approval == null) {
            System.out.println("No approval found for that expense id.");
            return;
        }

        System.out.println("\nApproval Record");
        System.out.println("Approval ID: " + approval.getId());
        System.out.println("Expense ID: " + approval.getExpenseId());
        System.out.println("Status: " + approval.getStatus());
        System.out.println("Reviewer ID: " + approval.getReviewer());
        System.out.println("Comment: " + approval.getComment());
        System.out.println("Review Date: " + approval.getReviewDate());
    }

    private void printExpenseList(String heading, ArrayList<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("\n--- " + heading + " ---");
        for (Expense expense : expenses) {
            printSingleExpense(expense);
        }
    }

    private void printSingleExpense(Expense expense) {
        System.out.println(
                "ID: " + expense.getId() +
                        " | User ID: " + expense.getUserId() +
                        " | Amount: $" + expense.getAmount() +
                        " | Description: " + expense.getDescription() +
                        " | Date: " + expense.getDate() +
                        " | Category: " + expense.getCategory()
        );
    }
}
