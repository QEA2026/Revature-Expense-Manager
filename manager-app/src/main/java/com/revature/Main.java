package com.revature;

import io.javalin.Javalin;
import com.revature.controllers.AuthController;
import com.revature.controllers.ExpenseController;
import com.revature.controllers.ApprovalController;
import com.revature.ui.ManagerConsole;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Manager App ===");
        System.out.println("1. Run terminal manager menu");
        System.out.println("2. Start REST API");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            new ManagerConsole().run();
            return;
        }

        AuthController authController = new AuthController();
        ExpenseController expenseController = new ExpenseController();
        ApprovalController approvalController = new ApprovalController();

        Javalin app = Javalin.create(config -> {
            // Keep manager-side route setup together so the API entry points stay easy to find.
            config.routes.post("/login", authController.loginHandler);
            config.routes.get("/expenses/pending", expenseController.getPendingExpensesHandler);
            config.routes.put("/expenses/{id}/review", approvalController.reviewExpenseHandler); // to approve
            config.routes.get("/reports/employee/{userId}", expenseController.getExpensesByEmployeeHandler);
            config.routes.get("/reports/category/{category}", expenseController.getExpensesByCategoryHandler);
            config.routes.get("/reports/date/{date}", expenseController.getExpenseByDateHandler);
            config.routes.get("/reports/expense/{expenseId}", expenseController.getExpenseByIdHandler);
        });

        System.out.println("Server starting on http://localhost:8080");
        app.start(8080);
    }
}
