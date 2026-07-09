package com.revature.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Approval {
    private int id;
    private int expenseId;
    private String status;
    private int reviewer;
    private String comment;
    private String reviewDate;

    // Create an object approvals and add the methods later
    public Approval() {}

    // Read an approval and manipulate
    public Approval(int id, int expenseId, String status, int reviewer, String comment, String reviewDate) {
        this.id = id;
        this.expenseId = expenseId;
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }
    // Insert an approval
    public Approval(int expenseId, String status, int reviewer, String comment, String reviewDate) {
        this.expenseId = expenseId;
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReviewer() {
        return reviewer;
    }

    public void setReviewer(int reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Approval{" +
                "id=" + id +
                ", expenseId=" + expenseId +
                ", status='" + status + '\'' +
                ", reviewer=" + reviewer +
                ", comment='" + comment + '\'' +
                ", reviewDate='" + reviewDate + '\'' +
                '}';
    }
}
