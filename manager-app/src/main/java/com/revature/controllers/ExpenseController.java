package com.revature.controllers;
import com.revature.DAOs.ExpenseDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import com.revature.DAOs.UserDAO;
import com.revature.models.User;
import com.revature.exceptions.ResourceNotFoundException;

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

    ExpenseDAO expenseDAO = new ExpenseDAO();
    UserDAO userDAO = new UserDAO();

    // Returns every expense currently awaiting manager review.
    public Handler getPendingExpensesHandler = (ctx) -> {
        try {
            var pendingExpenses = expenseDAO.getPendingExpenses();
            ctx.json(pendingExpenses);
            ctx.status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };

    // {userId} comes from the URL itself, e.g. /reports/employee/3
    public Handler getExpensesByEmployeeHandler = (ctx) -> {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));

            // Check if the user exists first
            User user = userDAO.getUserById(userId);
            if(user == null) {
                throw new ResourceNotFoundException("No user found with id:" + userId);
            }
            // if the user exists, get their expenses (could be an empty list which is valid)
            var expenses = expenseDAO.getExpensesByEmployee(userId);
            ctx.json(expenses);
            ctx.status(HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };

    // e.g. /reports/category/travel
    public Handler getExpensesByCategoryHandler = (ctx) -> {
        try {
            String category = ctx.pathParam("category");
            ctx.json(expenseDAO.getExpensesByCategory(category));
            ctx.status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };

    public Handler getExpenseByDateHandler = (ctx) -> {
        try {
            String date = ctx.pathParam("date");
            ctx.json(expenseDAO.getExpenseByDate(date));
            ctx.status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };

    // we need this before approving or denying it.
    public Handler getExpenseByIdHandler = (ctx) -> {
        try {
            int id = Integer.parseInt(ctx.pathParam("expenseId"));

            var expense = expenseDAO.getExpenseById(id);
            if (expense == null) {
                throw new ResourceNotFoundException("No expense found with id:" + id);
            }

            ctx.json(expense);
            ctx.status(HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };


}
