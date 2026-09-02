package view;

import dao.UserDAO;
import model.User;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** "Manage Staff Accounts" — Administrator-only screen (Task A assumption). */
public class ManageStaff extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"User ID", "Username", "Full Name", "Role", "Active"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel(" ");

    public ManageStaff() {
        super("Manage Staff Accounts");
        buildUi();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 480);
        loadStaff();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headingPanel.add(new JLabel(UIHelper.loadIcon("icon_staff.png", 32)));
        headingPanel.add(UIHelper.heading("Staff Accounts"));
        root.add(headingPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton add = UIHelper.primaryButton("Add Staff Account");
        add.addActionListener(e -> UIHelper.show(new AddStaff(this::loadStaff)));
        JButton toggleActive = UIHelper.secondaryButton("Activate / Deactivate Selected");
        toggleActive.addActionListener(e -> toggleActiveSelected());
        JButton resetPassword = UIHelper.secondaryButton("Reset Password for Selected");
        resetPassword.addActionListener(e -> resetPasswordSelected());
        JButton refresh = UIHelper.secondaryButton("Refresh");
        refresh.addActionListener(e -> loadStaff());

        buttons.add(add);
        buttons.add(toggleActive);
        buttons.add(resetPassword);
        buttons.add(refresh);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.NORTH);
        status.setFont(UIHelper.FONT_LABEL);
        status.setForeground(UIHelper.DANGER);
        south.add(status, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void loadStaff() {
        try {
            List<User> staff = userDAO.findAll();
            model.setRowCount(0);
            for (User u : staff) {
                model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName(),
                        u.getRole(), u.isActive() ? "Yes" : "No"});
            }
            status.setForeground(UIHelper.NAVY);
            status.setText(staff.size() + " account(s).");
        } catch (SQLException e) {
            status.setForeground(UIHelper.DANGER);
            status.setText("Database error: " + e.getMessage());
        }
    }

    private void toggleActiveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { status.setText("Select an account first."); return; }
        String userId = (String) model.getValueAt(row, 0);
        boolean currentlyActive = "Yes".equals(model.getValueAt(row, 4));
        boolean newActive = !currentlyActive;
        if (!UIHelper.confirm(this, (newActive ? "Activate" : "Deactivate") + " this account?")) return;

        try {
            userDAO.setActive(userId, newActive);
            loadStaff();
        } catch (SQLException e) {
            UIHelper.showError(this, "Could not update the account: " + e.getMessage());
        }
    }

    private void resetPasswordSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { status.setText("Select an account first."); return; }
        String userId = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);

        String newPassword = JOptionPane.showInputDialog(this,
                "New password for " + username + " (minimum 6 characters):", "Reset Password",
                JOptionPane.PLAIN_MESSAGE);
        if (newPassword == null) return; // cancelled

        String err = Validator.password(newPassword);
        if (err != null) {
            UIHelper.showError(this, err);
            return;
        }

        try {
            userDAO.resetPassword(userId, util.PasswordUtil.hash(newPassword));
            UIHelper.showInfo(this, "Password reset for " + username + ".");
        } catch (SQLException e) {
            UIHelper.showError(this, "Could not reset the password: " + e.getMessage());
        }
    }
}
