package com.revature.expensemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AuthService {
    public ManagerUser authenticateManager(String username, String password) throws SQLException {
        String cleanUsername = username == null ? "" : username.trim();
        if (cleanUsername.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, username, password, role FROM users WHERE username = ?"
             )) {
            statement.setString(1, cleanUsername);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || !PasswordHasher.verifyPassword(password, resultSet.getString("password"))) {
                    throw new IllegalArgumentException("Invalid username or password.");
                }

                if (!"manager".equals(resultSet.getString("role"))) {
                    throw new IllegalArgumentException("This account belongs to an employee. Use the Python employee app.");
                }

                return new ManagerUser(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role")
                );
            }
        }
    }
}
