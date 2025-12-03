//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static String dbPath = null;

    public ConnectionManager() {
    }

    public static void setDatabasePath(String path) {
        dbPath = path;
    }

    public static Connection getConnection() throws SQLException {
        if (dbPath != null && !dbPath.isBlank()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException var1) {
            }

            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } else {
            throw new SQLException("Database path not set");
        }
    }
}
