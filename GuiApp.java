//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class GuiApp extends JFrame {
    private CustomerStore store;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Phone", "Name", "Address", "Email"}, 0) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table;
    private final JTextArea log;
    private final JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> (new GuiApp()).setVisible(true));
    }

    public GuiApp() {
        super("Maintenance Shop");
        this.table = new JTable(this.tableModel);
        this.log = new JTextArea(5, 80);
        this.searchField = new JTextField(18);
        this.setDefaultCloseOperation(0);
        this.setSize(1000, 700);
        this.setLocationRelativeTo((Component)null);
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select SQLite Database (.db)");
        if (fc.showOpenDialog(this) != 0) {
            JOptionPane.showMessageDialog(this, "No database selected. Exiting.");
            System.exit(0);
        }

        String selectedDb = fc.getSelectedFile().getAbsolutePath();
        ConnectionManager.setDatabasePath(selectedDb);
        this.store = new CustomerStore();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                GuiApp.this.doExit();
            }
        });
        this.setLayout(new BorderLayout(5, 5));
        this.buildToolbar();
        this.add(this.buildMainPanel(), "Center");
        this.log.setEditable(false);
        this.log.setLineWrap(true);
        this.log.setWrapStyleWord(true);
        this.log.getDocument().addDocumentListener(new DocumentListener() {
            private void scroll() {
                GuiApp.this.log.setCaretPosition(GuiApp.this.log.getDocument().getLength());
            }

            public void insertUpdate(DocumentEvent e) {
                this.scroll();
            }

            public void removeUpdate(DocumentEvent e) {
                this.scroll();
            }

            public void changedUpdate(DocumentEvent e) {
                this.scroll();
            }
        });
        this.logMsg("[DB] " + selectedDb);
        this.refreshTable();
    }

    private void info(String m) {
        JOptionPane.showMessageDialog(this, m);
    }

    private void warn(String m) {
        JOptionPane.showMessageDialog(this, m, "Warning", 2);
    }

    private void logMsg(String m) {
        this.log.append(m + "\n");
    }

    private Component buildMainPanel() {
        this.table.setFillsViewportHeight(true);
        this.table.setRowHeight(22);
        this.sorter = new TableRowSorter(this.tableModel);
        this.table.setRowSorter(this.sorter);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        p.add(new JScrollPane(this.table), "Center");
        p.add(new JScrollPane(this.log), "South");
        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                GuiApp.this.filter();
            }

            public void removeUpdate(DocumentEvent e) {
                GuiApp.this.filter();
            }

            public void changedUpdate(DocumentEvent e) {
                GuiApp.this.filter();
            }
        });
        return p;
    }

    private void buildToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(this.btn("Load CSV", (e) -> this.onLoadCsv()));
        tb.add(this.btn("Refresh", (e) -> this.refreshTable()));
        tb.addSeparator();
        tb.add(this.btn("Add", (e) -> this.onAdd()));
        tb.add(this.btn("Update", (e) -> this.onUpdate()));
        tb.add(this.btn("Delete", (e) -> this.onDelete()));
        tb.add(this.btn("Export All", (e) -> this.onExportCsv()));
        tb.addSeparator();
        tb.add(this.btn("Clear Log", (e) -> this.log.setText("")));
        tb.add(this.btn("Exit", (e) -> this.doExit()));
        tb.add(Box.createHorizontalGlue());
        tb.add(new JLabel("Search: "));
        tb.add(this.searchField);
        JButton clear = new JButton("✕");
        clear.addActionListener((e) -> this.searchField.setText(""));
        tb.add(clear);
        this.add(tb, "North");
    }

    private JButton btn(String t, ActionListener a) {
        JButton b = new JButton(t);
        b.addActionListener(a);
        return b;
    }

    private void onLoadCsv() {
        JFileChooser c = new JFileChooser();
        if (c.showOpenDialog(this) == 0) {
            String msg = this.store.loadFromCsv(c.getSelectedFile().getAbsolutePath());
            this.info(msg);
            this.logMsg("[CSV] " + msg);
            this.refreshTable();
        }
    }

    private void onExportCsv() {
        JFileChooser c = new JFileChooser();
        c.setSelectedFile(new File("backup.csv"));
        if (c.showSaveDialog(this) == 0) {
            boolean ok = this.store.saveToCsv(c.getSelectedFile().getAbsolutePath());
            if (ok) {
                String path = c.getSelectedFile().getAbsolutePath();
                this.info("Export OK\nPath: " + path);
                this.logMsg("[CSV] Exported: " + path);

                try {
                    Desktop.getDesktop().open(c.getSelectedFile());
                } catch (Exception var5) {
                }
            } else {
                this.warn("Export failed");
                this.logMsg("[CSV] Export failed");
            }

        }
    }

    private JPanel makeForm(JTextField ph, JTextField nm, JTextField ad, JTextField em) {
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        p.add(new JLabel("Phone (digits only):"));
        p.add(ph);
        p.add(new JLabel("Name:"));
        p.add(nm);
        p.add(new JLabel("Address:"));
        p.add(ad);
        p.add(new JLabel("Email (optional):"));
        p.add(em);
        return p;
    }

    private void onAdd() {
        JTextField ph = new JTextField();
        JTextField nm = new JTextField();
        JTextField ad = new JTextField();
        JTextField em = new JTextField();
        JPanel form = this.makeForm(ph, nm, ad, em);
        if (JOptionPane.showConfirmDialog(this, form, "Add Customer", 2) == 0) {
            String phone = CustomerStore.normalizePhone(ph.getText());
            String name = nm.getText().trim();
            String addr = ad.getText().trim();
            String email = em.getText().trim();
            if (CustomerStore.isValidPhone(phone) && !name.isEmpty() && !addr.isEmpty()) {
                String emailErr = CustomerStore.emailError(email);
                if (emailErr != null) {
                    this.warn(emailErr);
                } else if (this.store.getByPhone(phone) != null) {
                    this.warn("Phone already exists.");
                } else if (!this.store.insert(new Customer(phone, name, addr, email))) {
                    this.warn("Insert failed.");
                } else {
                    this.logMsg("[ADD] " + phone + " | " + name);
                    this.refreshTable();
                }
            } else {
                this.warn("Phone must be 7–11 digits. Name and Address required.");
            }
        }
    }

    private void onUpdate() {
        int r = this.table.getSelectedRow();
        if (r < 0) {
            this.warn("Select row");
        } else {
            int m = this.table.convertRowIndexToModel(r);
            String phone = this.tableModel.getValueAt(m, 0).toString();
            String name = this.tableModel.getValueAt(m, 1).toString();
            String addr = this.tableModel.getValueAt(m, 2).toString();
            String email = this.tableModel.getValueAt(m, 3) == null ? "" : this.tableModel.getValueAt(m, 3).toString();
            JTextField ph = new JTextField(phone);
            ph.setEditable(false);
            JTextField nm = new JTextField(name);
            JTextField ad = new JTextField(addr);
            JTextField em = new JTextField(email);
            JPanel form = this.makeForm(ph, nm, ad, em);
            if (JOptionPane.showConfirmDialog(this, form, "Edit Customer", 2) == 0) {
                String newName = nm.getText().trim();
                String newAddr = ad.getText().trim();
                String newEmail = em.getText().trim();
                if (!newName.isEmpty() && !newAddr.isEmpty()) {
                    String emailErr = CustomerStore.emailError(newEmail);
                    if (emailErr != null) {
                        this.warn(emailErr);
                    } else if (!this.store.update(new Customer(phone, newName, newAddr, newEmail))) {
                        this.warn("Update failed.");
                    } else {
                        this.logMsg("[UPDATE] " + phone + " | " + newName);
                        this.refreshTable();
                    }
                } else {
                    this.warn("Name and Address required.");
                }
            }
        }
    }

    private void onDelete() {
        int r = this.table.getSelectedRow();
        if (r < 0) {
            this.warn("Select row");
        } else {
            int m = this.table.convertRowIndexToModel(r);
            String ph = CustomerStore.normalizePhone(this.tableModel.getValueAt(m, 0).toString());
            if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", 0) == 0) {
                if (this.store.delete(ph)) {
                    this.logMsg("[DELETE] " + ph);
                } else {
                    this.logMsg("[DELETE] failed " + ph);
                }

                this.refreshTable();
            }

        }
    }

    private void refreshTable() {
        List<Customer> rows = this.store.listAll();
        this.tableModel.setRowCount(0);

        for(Customer c : rows) {
            this.tableModel.addRow(new Object[]{c.getPhoneNumber(), c.getName(), c.getAddress(), c.getEmail()});
        }

        this.logMsg("[REFRESH] rows=" + rows.size());
    }

    private void doExit() {
        if (JOptionPane.showConfirmDialog(this, "Exit?", "Confirm", 0) == 0) {
            System.exit(0);
        }

    }

    private void filter() {
        String t = this.searchField.getText().trim();
        if (t.isEmpty()) {
            this.sorter.setRowFilter((RowFilter)null);
            this.logMsg("[SEARCH] cleared");
        } else {
            this.sorter.setRowFilter(RowFilter.regexFilter("(?i)" + t, new int[0]));
            this.logMsg("[SEARCH] '" + t + "'");
        }

    }
}
