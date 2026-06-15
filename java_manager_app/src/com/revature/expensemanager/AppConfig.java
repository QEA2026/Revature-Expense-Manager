package com.revature.expensemanager;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {
    public static final Path PROJECT_ROOT = Paths.get("").toAbsolutePath().normalize();
    public static final Path DB_PATH = PROJECT_ROOT.resolve("data").resolve("expense_manager.db");
    public static final Path SCHEMA_PATH = PROJECT_ROOT.resolve("shared").resolve("schema.sql");

    private AppConfig() {
    }
}
