package com.revature.DAOs;

import java.util.ArrayList;

import com.revature.models.Expense;

//here we will lay out functionalities that UserDAO will implement
public interface ExpenseDAOInterface {

    // view all pending expenses ( joins with approvals to filter by status)
    ArrayList<Expense> getPendingExpenses();

    // Generate report by employee
    ArrayList<Expense> getExpensesByEmployee(int userId);

    // Generate report by category? There is no field for this in the DB should i add it?
    ArrayList<Expense> getExpensesByCategory(String category);

    //generate report by date
    ArrayList<Expense> getExpenseByDate(String date);

    // Get a single expense by its id -> for approving and denying
    Expense getExpenseById(int expenseId);

}