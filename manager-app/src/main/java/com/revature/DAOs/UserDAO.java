package com.revature.DAOs;
import com.revature.models.User;
import com.revature.utils.ConnectionUtil;
import java.sql.*;

// The DAO just talks to the database so no validation
public class UserDAO implements UserDAOInterface{

    @Override
    public User getUserByUsername(String username) {
        String sql = "select * from users where username = ?;";

        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, username);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    User u = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    return u;
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}