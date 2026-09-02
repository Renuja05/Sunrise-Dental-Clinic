package view;

import dao.UserDAO;
import model.User;
import util.CurrentSession;
import util.PasswordUtil;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** First screen shown: staff must log in before anything else is reachable. */
public class Login extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private final JTextField usernameField = UIHelper.textField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel statusLabel = new JLabel(" ");

    public Login() {
        super("Sunrise Dental Clinic - Login");
        buildUi();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 420);
        setResizable(false);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(24, 40, 24, 40));

        JLabel logo = new JLabel(UIHelper.loadIcon("logo.png", 90));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel title = new JLabel("Sunrise Dental Clinic", SwingConstants.CENTER);
        title.setFont(UIHelper.FONT_TITLE);
        title.setForeground(UIHelper.NAVY);

        JLabel subtitle = new JLabel("Appointment & Patient Management System", SwingConstants.CENTER);
        subtitle.setFont(UIHelper.FONT_LABEL);
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(logo);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(Box.createVerticalStrut(10), gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        form.add(UIHelper.label("Username:"), gbc);
        gbc.gridx = 1;
        usernameField.setColumns(14);
        form.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        form.add(UIHelper.label("Password:"), gbc);
        gbc.gridx = 1;
        passwordField.setFont(UIHelper.FONT_LABEL);
        form.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        statusLabel.setForeground(UIHelper.DANGER);
        statusLabel.setFont(UIHelper.FONT_LABEL);
        form.add(statusLabel, gbc);

        gbc.gridy = 4;
        JButton loginButton = UIHelper.primaryButton("Log In");
        loginButton.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginButton);
        form.add(loginButton, gbc);

        gbc.gridy = 5;
        JLabel hint = new JLabel("<html><center>Default admin account: <b>admin</b> / password123<br/>" +
                "Default receptionist: <b>reception1</b> / password123</center></html>");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        form.add(hint, gbc);

        root.add(titlePanel, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String err = Validator.requiredText(username, "Username");
        if (err == null) err = Validator.requiredText(password, "Password");
        if (err != null) {
            statusLabel.setText(err);
            return;
        }

        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
                statusLabel.setText("Invalid username or password.");
                return;
            }
            if (!user.isActive()) {
                statusLabel.setText("This account has been deactivated. Contact an administrator.");
                return;
            }
            CurrentSession.getInstance().login(user);
            UIHelper.show(new MenuForm());
            dispose();
        } catch (SQLException e) {
            statusLabel.setText("Could not reach the database: " + e.getMessage());
        }
    }
}
