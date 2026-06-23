package com.revature.DAOs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.revature.models.Expense;
import com.revature.utils.ConnectionUtil;


public class ExpenseDAO implements ExpenseDAOInterface{

    @Override
    public ArrayList<Expense> getPendingExpenses() {
        String sql = "Select expenses.* from expenses " +
                "JOIN approvals ON approvals.expense_id = expenses.id " +
                "WHERE approvals.status = 'pending';";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ArrayList<Expense> expenseList = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("date"),
                            rs.getString("category")
                    );
                    expenseList.add(e);
                }
                return expenseList;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Expense> getExpensesByEmployee(int userId){
        String sql = "Select * from expenses WHERE user_id = ?;";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ArrayList<Expense> expenseList = new ArrayList<>();

            ps.setInt(1,userId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("date"),
                            rs.getString("category")
                    );
                    expenseList.add(e);
                }
                return expenseList;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Expense> getExpensesByCategory(String category){
        String sql = "Select * from expenses where category = ?;";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, category);

            ArrayList<Expense> expenseList = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("date"),
                            rs.getString("category")
                    );
                    expenseList.add(e);
                }
                return expenseList;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public ArrayList<Expense> getExpenseByDate(String date){
        String sql = "Select * from expenses where date = ?;";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1,date);

            ArrayList<Expense> expenseList = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("date"),
                            rs.getString("category")
                    );
                    expenseList.add(e);
                }
                return expenseList;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Expense getExpenseById(int expenseId){
        String sql = "Select * from expenses where id = ?;";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1,expenseId);


            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("date"),
                            rs.getString("category")
                    );
                    return e;
                }

            }

        } catch (SQLException a){
            a.printStackTrace();
        }
        return null;
    }
}