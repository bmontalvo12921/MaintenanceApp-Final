//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    public CustomerDao() {
    }

    public static void ensureTable() throws SQLException {
        try (
                Connection c = ConnectionManager.getConnection();
                Statement st = c.createStatement();
        ) {
            st.executeUpdate("    CREATE TABLE IF NOT EXISTS customers (\n      phone TEXT PRIMARY KEY,\n      name TEXT NOT NULL,\n      address TEXT NOT NULL,\n      email TEXT\n    )\n");
        }

    }

    public static boolean insert(Customer c) throws SQLException {
        String sql = "INSERT OR IGNORE INTO customers(phone,name,address,email) VALUES(?,?,?,?)";

        boolean var4;
        try (
                Connection cn = ConnectionManager.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
        ) {
            ps.setString(1, c.getPhoneNumber());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getEmail());
            var4 = ps.executeUpdate() > 0;
        }

        return var4;
    }

    public static boolean update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, address=?, email=? WHERE phone=?";

        boolean var4;
        try (
                Connection cn = ConnectionManager.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
        ) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhoneNumber());
            var4 = ps.executeUpdate() > 0;
        }

        return var4;
    }

    public static boolean delete(String phone) throws SQLException {
        String sql = "DELETE FROM customers WHERE phone=?";

        boolean var4;
        try (
                Connection cn = ConnectionManager.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
        ) {
            ps.setString(1, phone);
            var4 = ps.executeUpdate() > 0;
        }

        return var4;
    }

    public static Customer find(String phone) throws SQLException {
        String sql = "SELECT phone,name,address,email FROM customers WHERE phone=?";

        Customer var5;
        try (
                Connection cn = ConnectionManager.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
        ) {
            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    var5 = new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                    return var5;
                }

                var5 = null;
            }
        }

        return var5;
    }

    public static List<Customer> listAll() throws SQLException {
        String sql = "SELECT phone,name,address,email FROM customers ORDER BY name";
        List<Customer> out = new ArrayList();

        try (
                Connection cn = ConnectionManager.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()) {
                out.add(new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        }

        return out;
    }
}
