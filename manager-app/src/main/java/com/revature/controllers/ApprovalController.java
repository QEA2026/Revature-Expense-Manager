package com.revature.controllers;

import com.revature.DAOs.ApprovalDAO;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Approval;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);
    ApprovalDAO approvalDAO = new ApprovalDAO();

    // PUT /expenses/{id}/review
    // Body: { "status": "approved", "reviewer": 3, "comment": "Looks good" }
    public Handler reviewExpenseHandler = (ctx) -> {
        int expenseId = 0;
        try {
            expenseId = Integer.parseInt(ctx.pathParam("id"));

            Approval reviewRequest = ctx.bodyAsClass(Approval.class);

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
            System.out.println("ERROR IN REVIEW HANDLER: " + e.getMessage());
            e.printStackTrace();
            logger.error("Unexpected error reviewing expense {} : {}", expenseId, e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred.");
        }
    };
}
