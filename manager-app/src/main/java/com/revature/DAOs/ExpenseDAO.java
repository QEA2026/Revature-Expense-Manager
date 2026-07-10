package com.revature.DAOs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.revature.models.Expense;
import com.revature.utils.ConnectionUtil;
import com.revature.exceptions.ResourceNotFoundException;


public class ExpenseDAO implements ExpenseDAOInterface{
    private static final Logger logger = LoggerFactory.getLogger(ExpenseDAO.class);

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
                logger.info("Successfully retrieved {} pending expense(s)", expenseList.size());
                return expenseList;
            }
        } catch (SQLException e){
            logger.error("Database error retrieving pending expenses: {}", e.getMessage());
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
                logger.info("Successfully retrieved {} expense(s) for employee id: {}", expenseList.size(), userId);
                return expenseList;
            }
        } catch (SQLException e){
            logger.error("Database error retrieving expenses for employee id {} : {}", userId, e.getMessage());
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
                logger.info("Successfully retrieved {} expense(s) for category: {}", expenseList.size(), category);
                return expenseList;
            }

        } catch (SQLException e){
            logger.error("Database error retrieving expenses for category {} : {}", category, e.getMessage());
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
                logger.info("Successfully retrieved {} expense(s) for date: {}", expenseList.size(), date);
                return expenseList;
            }

        } catch (SQLException e){
            logger.error("Database error retrieving expenses for date {} : {}", date, e.getMessage());
        }
        return null;
    }

    // Should return only one specific expense by its unique primary key
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
                    logger.info("Successfully retrieved expense with id: {}", expenseId);
                    return e;
                }

            }

        } catch (SQLException a){
            logger.error("Database error retrieving expense by id {} : {}", expenseId, a.getMessage());
        }
        logger.warn("No expense found with id: {}", expenseId);
        throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
    }
}