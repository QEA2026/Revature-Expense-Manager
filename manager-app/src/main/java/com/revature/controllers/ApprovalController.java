package com.revature.controllers;

import com.revature.DAOs.ApprovalDAO;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Approval;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * ApprovalController
 *
 * Handles all HTTP requests related to reviewing expenses.
 * Managers use this controller to approve or deny pending
 * expense reports submitted by employees.
 *
 * Covers this manager user story:
 *  - As a manager, I want to approve or deny submitted expenses
 *    so that I can manage reimbursements appropriately.
 *  - As a manager, I want to add comments to expense decisions
 *    so that employees understand the reasoning behind approvals or denials.
 */

public class ApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);
    ApprovalDAO approvalDAO = new ApprovalDAO();

    // PUT /expenses/{id}/review
    // Body: { "status": "approved", "reviewer": 3, "comment": "Looks good" }
    public Handler reviewExpenseHandler = (ctx) -> {
        int expenseId = 0;
        try {
            // get the expense id from the url path
            expenseId = Integer.parseInt(ctx.pathParam("id"));

            // then deserialize the JSOM request body into a Approval object
            Approval reviewRequest = ctx.bodyAsClass(Approval.class);

            //call the DAO to update the approvals table and return true if successful
            boolean success = approvalDAO.updateApproval(
                    expenseId,
                    reviewRequest.getStatus(),
                    reviewRequest.getReviewer(),
                    reviewRequest.getComment()
            );

            if (success) {
                logger.info("Expense {} successfully updated to status: {}", expenseId, reviewRequest.getStatus());
                ctx.status(HttpStatus.OK);
                ctx.result("Expense " + reviewRequest.getStatus() + " successfully.");
            } else {
                logger.warn("Failed to update expense {}", expenseId);
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.result("Could not update expense.");
            }

        } catch (ResourceNotFoundException e) {
            logger.warn("No approval found for expense id: {}", expenseId);
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error reviewing expense {} : {}", expenseId, e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };
}
