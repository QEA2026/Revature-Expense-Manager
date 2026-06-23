package com.revature.DAOs;

import com.revature.models.Approval;


/*
* As a manager, I want to approve or deny submitted expenses so that I can
  manage reimbursements appropriately.
    * We need to pass in the parameter expenseId because its a foreign key to
      expenses
    * We also need the status, reviewerId, and comment.
                                                 ^
                                                 |
* As a manager, I want to add comments to expense decisions so that employees
  understand the reasoning behind approvals or denials.

 */
public interface ApprovalDAOInterface {

    // Before I can approve/deny an expense I need to get the approval record for that specific expense
    Approval getApprovalByExpenseId(int expenseId);

    // Approve/Deny an expense with a comment
    boolean updateApproval(int expenseId, String status, int reviewer, String comment);


}