package com.revature.controllers;
import com.revature.DAOs.ExpenseDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

/*
 * Handles all HTTP requests related to viewing expenses for the
 * Manager App. This is the "web" layer that sits between Postman
 * (or any future frontend) and the database.It doesn't contain
 * any database logic itself, it just receives requests, calls the
 * DAO to get data, and sends a JSON response back.
 *
 * Covers these manager user stories:
 *  - View all pending expenses awaiting review
 *  - Generate reports by employee, category, or date
 */

public class ExpenseController {

    // You need to create an instance in order to call the methods
    ExpenseDAO expenseDAO = new ExpenseDAO();

    // Returns every expense currently awaiting manager review.
    public Handler getPendingExpensesHandler = (ctx) -> {
        var pendingExpenses = expenseDAO.getPendingExpenses();
        ctx.json(pendingExpenses);
        ctx.status(HttpStatus.OK);
    };

    // {userId} comes from the URL itself, e.g. /reports/employee/3
    public Handler getExpensesByEmployeeHandler = (ctx) -> {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        var expenses = expenseDAO.getExpensesByEmployee(userId);
        ctx.json(expenses);
        ctx.status(HttpStatus.OK);
    };
    // e.g. /reports/category/travel
    public Handler getExpensesByCategoryHandler = (ctx) -> {
        String category = ctx.pathParam("category");
        ctx.json(expenseDAO.getExpensesByCategory(category));
        ctx.status(HttpStatus.OK);
    };

    public Handler getExpenseByDateHandler = (ctx) -> {
        String date = ctx.pathParam("date");
        ctx.json(expenseDAO.getExpenseByDate(date));
        ctx.status(HttpStatus.OK);
    };
    // we need this before approving or denying it.
    public Handler getExpenseByIdHandler = (ctx) -> {
        int id = Integer.parseInt(ctx.pathParam("expenseId"));
        ctx.json(expenseDAO.getExpenseById(id));
        ctx.status(HttpStatus.OK);
    };


}
