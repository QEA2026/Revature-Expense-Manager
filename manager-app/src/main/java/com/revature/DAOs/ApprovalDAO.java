package com.revature.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.revature.models.Approval;
import com.revature.utils.ConnectionUtil;

public class ApprovalDAO implements ApprovalDAOInterface{

    @Override
    public Approval getApprovalByExpenseId(int expenseId){
        String sql = "select * from approvals where expense_id = ?;";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, expenseId);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Approval a = new Approval(
                            rs.getInt("id"),
                            rs.getInt("expense_id"),
                            rs.getString("status"),
                            rs.getInt("reviewer"),
                            rs.getString("comment"),
                            rs.getString("review_date")
                    );
                    return a;
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateApproval(int expenseId, String status, int reviewerId, String comment){
        String sql = "UPDATE approvals SET status = ?, reviewer = ?, comment = ?, review_date = ? WHERE expense_id = ?;";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            String reviewDate = LocalDate.now().toString();

            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setString(3, comment);
            ps.setString(4, reviewDate);
            ps.setInt(5, expenseId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }



}