package com.revature.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;

public class ConnectionUtil {

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("problem occurred locating driver");
        }

        
        String dbPath = Paths.get("").toAbsolutePath()
                .resolve("../database/expense_manager.db")
                .normalize()
                .toString();

        String url = "jdbc:sqlite:" + dbPath;

        return DriverManager.getConnection(url);
    }
}