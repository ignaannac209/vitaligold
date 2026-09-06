package main.java.com.ingsoft.vitaligold.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre y guarda una sola conexión a la base de datos para reutilizarla.
 */
public class DataBaseConnection {

    private static Connection connection;

    private DataBaseConnection() {
    }

    public static Connection getConnectionDataBase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(Credentials.URL_DB, Credentials.USER_DB, Credentials.PASS_DB);
        }
        return connection;
    }
}
