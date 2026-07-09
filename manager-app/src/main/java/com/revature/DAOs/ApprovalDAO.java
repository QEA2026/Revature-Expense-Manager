package com.revature.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.revature.models.Approval;
import com.revature.utils.ConnectionUtil;
import com.revature.exceptions.ResourceNotFoundException;

public class ApprovalDAO implements ApprovalDAOInterface {
    private static final Logger logger = LoggerFactory.getLogger(ApprovalDAO.class);

    @Override
    public Approval getApprovalByExpenseId(int expenseId) {
        String sql = "select * from approvals where expense_id = ?;";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expenseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Approval a = new Approval(
                            rs.getInt("id"),
                            rs.getInt("expense_id"),
                            rs.getString("status"),
                            rs.getInt("reviewer"),
                            rs.getString("comment"),
                            rs.getString("review_date")
                    );
                    logger.info("Successfully retrieved approval for expense id: {}", expenseId);
                    return a;
                } else {
                    logger.warn("No approval found for expense id: {}", expenseId);
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error retrieving approval for expense id {} : {}", expenseId, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateApproval(int expenseId, String status, int reviewerId, String comment) {
        String sql = "UPDATE approvals SET status = ?, reviewer = ?, comment = ?, review_date = ? WHERE expense_id = ?;";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String reviewDate = LocalDate.now().toString();

            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setString(3, comment);
            ps.setString(4, reviewDate);
            ps.setInt(5, expenseId);

            int rowsAffected = ps.executeUpdate();

            // if no rows affected we know the id is incorrect
            if (rowsAffected == 0) {
                logger.warn("No approval found for expense id: {} - update failed", expenseId);
                throw new ResourceNotFoundException("No approval found for expense id: " + expenseId);
            }
            logger.info("Successfully updated approval for expense id: {} to status: {}", expenseId, status);
            return true;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (SQLException e) {
            logger.error("Database error retrieving approval for expense id {} : {}", expenseId, e.getMessage());
        }
        return false;
    }

}