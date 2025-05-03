package org.example.Practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCRunner {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost:5433/";
    private static final String DATABASE_NAME = "sea_cruises";
    public static final String USER_NAME = "daniil";
    public static final String DATABASE_PASS = "daniil";
    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;

    public JDBCRunner() {
        checkDriver();
        checkDB();
    }

    public static void checkDriver() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту.", e);
        }
    }

    public static void checkDB() {
        try (Connection _ = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            // Проверка подключения
        } catch (SQLException e) {
            throw new RuntimeException("Нет базы данных! Проверьте имя базы или разверните локально резервную копию.",
                    e);
        }
    }
}