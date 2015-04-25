package info.sarihh.antiinferencehub;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
/*
 * Author: Sari Haj Hussein
 */
public class DatabaseConnection {

    public static final Connection getDatabaseConnection(String driverName, String url, String user, String password) {
        if (driverName.equals("MySQL Driver")) {
            conn = getConnection("com.mysql.jdbc.Driver", url, user, password);
        } else if (driverName.equals("Oracle Thin Driver")) {
            conn = getConnection("oracle.jdbc.driver.OracleDriver", url, user, password);
        } else if (driverName.equals("PostgreSQL Driver")) {
            conn = getConnection("org.postgresql.Driver", url, user, password);
        }
        return conn;
    }

    private static final Connection getConnection(String className, String url, String user, String password) {
        try {
            Class.forName(className);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException cnfe) {
            return null;
        } catch (SQLException se) {
            return null;
        }
    }
    private static Connection conn;
}
