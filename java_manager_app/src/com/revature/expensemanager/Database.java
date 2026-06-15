package com.revature.expensemanager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private Database() {
    }

    public static Connection connect() throws SQLException {
        try {
            Files.createDirectories(AppConfig.DB_PATH.getParent());
            Class.forName("org.sqlite.JDBC");
        } catch (IOException | ClassNotFoundException error) {
            throw new SQLException("Unable to prepare the SQLite connection.", error);
        }

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + AppConfig.DB_PATH);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = connect()) {
            if (tableExists(connection, "users")) {
                return;
            }

            String schemaSql;
            try {
                schemaSql = Files.readString(AppConfig.SCHEMA_PATH, StandardCharsets.UTF_8);
            } catch (IOException error) {
                throw new SQLException("Unable to read schema.sql.", error);
            }

            try (Statement statement = connection.createStatement()) {
                for (String command : schemaSql.split(";\\s*(?:\\R|$)")) {
                    String sql = command.trim();
                    if (!sql.isEmpty()) {
                        statement.executeUpdate(sql);
                    }
                }
            }
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }
}
