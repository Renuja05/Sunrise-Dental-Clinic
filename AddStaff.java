package view;

import dao.UserDAO;
import util.PasswordUtil;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Small form for creating a new Receptionist or Administrator account. */
public class AddStaff extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private final JTextField usernameField = UIHelper.textField();
    private final JTextField fullNameField = UIHelper.textField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JRadioButton receptionistRadio = new JRadioButton("Receptionist", true);
    private final JRadioButton administratorRadio = new JRadioButton("Administrator");
    private final JLabel status = new JLabel(" ");

    private final Runnable onSaved;

    public AddStaff(Runnable onSaved) {
        super("Add Staff Account");
        this.onSaved = onSaved;
        buildUi();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 340);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 24, 20, 24));
        root.add(UIHelper.heading("New Staff Account"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.weightx = 1;

        int row = 0;
        gbc.gridy = row++; gbc.gridx = 0; form.add(UIHelper.label("Full Name:"), gbc);
        gbc.gridx = 1; form.add(fullNameField, gbc);

        gbc.gridy = row++; gbc.gridx = 0; form.add(UIHelper.label("Username:"), gbc);
        gbc.gridx = 1; form.add(usernameField, gbc);

        gbc.gridy = row++; gbc.gridx = 0; form.add(UIHelper.label("Password:"), gbc);
        gbc.gridx = 1; passwordField.setFont(UIHelper.FONT_LABEL); form.add(passwordField, gbc);

        ButtonGroup group = new ButtonGroup();
        group.add(receptionistRadio);
        group.add(administratorRadio);
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rolePanel.add(receptionistRadio);
        rolePanel.add(administratorRadio);
        gbc.gridy = row++; gbc.gridx = 0; form.add(UIHelper.label("Role:"), gbc);
        gbc.gridx = 1; form.add(rolePanel, gbc);

        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        status.setForeground(UIHelper.DANGER);
        status.setFont(UIHelper.FONT_LABEL);
        form.add(status, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton save = UIHelper.primaryButton("Create Account");
        save.addActionListener(e -> save());
        JButton cancel = UIHelper.secondaryButton("Cancel");
        cancel.addActionListener(e -> dispose());
        buttons.add(save);
        buttons.add(cancel);
        gbc.gridy = row;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void save() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = administratorRadio.isSelected() ? "ADMINISTRATOR" : "RECEPTIONIST";

        String err = Validator.fullName(fullName);
        if (err == null) err = Validator.username(username);
        if (err == null) err = Validator.password(password);
        if (err != null) {
            status.setText(err);
            return;
        }

        try {
            userDAO.insert(username.trim(), PasswordUtil.hash(password), fullName.trim(), role);
            UIHelper.showInfo(this, "Account created for " + username.trim() + ".");
            onSaved.run();
            dispose();
        } catch (SQLException e) {
            status.setText("Could not create the account: " + e.getMessage());
        }
    }
}
