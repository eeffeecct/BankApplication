package org.example.util;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    private static final TransactionManager INSTANCE = new TransactionManager();

    private TransactionManager() {
        // Приватный конструктор для предотвращения создания новых экземпляров
    }

    public static TransactionManager getInstance() {
        return INSTANCE;
    }

    public static void begin(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection cannot be null");
        }
        connection.setAutoCommit(false);
    }

    public static void commit(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection cannot be null");
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    public static void rollback(Connection connection) {
        if (connection == null) return;

        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Rollback failed: " + e.getMessage());
        }
    }

}
