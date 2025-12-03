//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerStore {
    public CustomerStore() {
        try {
            CustomerDao.ensureTable();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure Customer table: " + e.getMessage(), e);
        }
    }

    public static String normalizePhone(String s) {
        return s == null ? "" : s.replaceAll("[^0-9]", "");
    }

    public boolean insert(Customer c) {
        try {
            String phone = normalizePhone(c.getPhoneNumber());
            String name = safe(c.getName()).trim();
            String addr = safe(c.getAddress()).trim();
            String email = safe(c.getEmail()).trim();
            if (isValidPhone(phone) && isValidName(name) && isValidAddress(addr)) {
                return emailError(email) != null ? false : CustomerDao.insert(new Customer(phone, name, addr, email));
            } else {
                return false;
            }
        } catch (SQLException var6) {
            return false;
        }
    }

    public boolean update(Customer c) {
        try {
            String phone = normalizePhone(c.getPhoneNumber());
            String name = safe(c.getName()).trim();
            String addr = safe(c.getAddress()).trim();
            String email = safe(c.getEmail()).trim();
            if (isValidPhone(phone) && isValidName(name) && isValidAddress(addr)) {
                return emailError(email) != null ? false : CustomerDao.update(new Customer(phone, name, addr, email));
            } else {
                return false;
            }
        } catch (SQLException var6) {
            return false;
        }
    }

    public boolean delete(String phoneRaw) {
        try {
            return CustomerDao.delete(normalizePhone(phoneRaw));
        } catch (SQLException var3) {
            return false;
        }
    }

    public Customer getByPhone(String phoneRaw) {
        try {
            return CustomerDao.find(normalizePhone(phoneRaw));
        } catch (SQLException var3) {
            return null;
        }
    }

    public List<Customer> listAll() {
        try {
            return CustomerDao.listAll();
        } catch (SQLException var2) {
            return List.of();
        }
    }

    public String loadFromCsv(String path) {
        return this.importCsv(Path.of(path));
    }

    public String importCsv(Path csvPath) {
        int added = 0;
        int updated = 0;
        int skipped = 0;
        int total = 0;

        String line;
        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            while((line = br.readLine()) != null) {
                ++total;
                if (line.isBlank()) {
                    ++skipped;
                } else {
                    List<String> cols = parseCsvLine(line);
                    if (cols.size() != 4) {
                        ++skipped;
                    } else {
                        String phone = normalizePhone(safe((String)cols.get(0)).trim());
                        String name = safe((String)cols.get(1)).trim();
                        String addr = safe((String)cols.get(2)).trim();
                        String email = safe((String)cols.get(3)).trim();
                        if (isValidPhone(phone) && isValidName(name) && isValidAddress(addr) && emailError(email) == null) {
                            Customer c = new Customer(phone, name, addr, email);
                            if (CustomerDao.insert(c)) {
                                ++added;
                            } else if (CustomerDao.update(c)) {
                                ++updated;
                            } else {
                                ++skipped;
                            }
                        } else {
                            ++skipped;
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            return "Import error: " + ((Exception)e).getMessage();
        }

        return "Total: " + total + " | Added: " + added + " | Updated: " + updated + " | Skipped: " + skipped;
    }

    public boolean saveToCsv(String path) {
        try {
            List<Customer> list = CustomerDao.listAll();

            try (BufferedWriter bw = Files.newBufferedWriter(Path.of(path), StandardCharsets.UTF_8)) {
                bw.write("Phone,Name,Address,Email\n");

                for(Customer c : list) {
                    String var10001 = csv(c.getPhoneNumber());
                    bw.write(var10001 + "," + csv(c.getName()) + "," + csv(c.getAddress()) + "," + csv(c.getEmail()) + "\n");
                }
            }

            return true;
        } catch (Exception var8) {
            return false;
        }
    }

    public static boolean isValidEmail(String s) {
        return s != null && !s.isBlank() ? s.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") : true;
    }

    public static String emailError(String s) {
        if (s != null && !s.isBlank()) {
            return !isValidEmail(s) ? "Invalid email. Use format name@example.com." : null;
        } else {
            return null;
        }
    }

    public static boolean isValidPhone(String p) {
        return p != null && p.length() >= 7 && p.length() <= 11;
    }

    public static boolean isValidName(String s) {
        return s != null && !s.isBlank();
    }

    public static boolean isValidAddress(String s) {
        return s != null && !s.isBlank();
    }

    public static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String csv(String s) {
        return s == null ? "" : (s.contains(",") ? "\"" + s.replace("\"", "\"\"") + "\"" : s);
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean q = false;

        for(char c : line.toCharArray()) {
            if (c == '"') {
                q = !q;
            } else if (c == ',' && !q) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        result.add(sb.toString());
        return result;
    }
}
