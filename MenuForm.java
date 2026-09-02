package view;

import util.CurrentSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The hub every staff member lands on after logging in. Which buttons
 * are shown depends on the logged-in role — Manage Staff Accounts is
 * only visible to an Administrator, using the polymorphic
 * {@code canManageStaff()} method from the User class hierarchy rather
 * than checking a role string directly.
 */
public class MenuForm extends JFrame {

    public MenuForm() {
        super("Sunrise Dental Clinic - Main Menu");
        buildUi();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitSystem();
            }
        });
        setSize(560, 560);
        setResizable(false);
    }

    private void buildUi() {
        var user = CurrentSession.getInstance().getCurrentUser();

        JPanel header = new JPanel();
        header.setBackground(UIHelper.NAVY);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel dashboardTitle = new JLabel(user.getDashboardTitle());
        dashboardTitle.setFont(UIHelper.FONT_TITLE);
        dashboardTitle.setForeground(Color.WHITE);

        JLabel welcome = new JLabel("Signed in as " + user.getFullName() + " (" + user.getUsername() + ")");
        welcome.setFont(UIHelper.FONT_LABEL);
        welcome.setForeground(UIHelper.LIGHT_BLUE);

        header.add(dashboardTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(welcome);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 12));
        buttons.setBackground(UIHelper.BACKGROUND);
        buttons.setBorder(new EmptyBorder(24, 50, 24, 50));

        JButton registerBtn = UIHelper.menuButton("1. Register New Appointment", "icon_appointment.png");
        registerBtn.addActionListener(e -> UIHelper.show(new RegisterAppointment()));
        buttons.add(registerBtn);

        JButton detailsBtn = UIHelper.menuButton("2. Appointment Details / Bill", "icon_search.png");
        detailsBtn.addActionListener(e -> UIHelper.show(new AppointmentDetails()));
        buttons.add(detailsBtn);

        JButton reportsBtn = UIHelper.menuButton("3. Reports", "icon_reports.png");
        reportsBtn.addActionListener(e -> UIHelper.show(new Reports()));
        buttons.add(reportsBtn);

        if (user.canManageStaff()) {
            JButton staffBtn = UIHelper.menuButton("4. Manage Staff Accounts", "icon_staff.png");
            staffBtn.addActionListener(e -> UIHelper.show(new ManageStaff()));
            buttons.add(staffBtn);
        }

        JButton helpBtn = UIHelper.menuButton("5. Help", "icon_help.png");
        helpBtn.addActionListener(e -> UIHelper.show(new Help()));
        buttons.add(helpBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        bottom.setBackground(UIHelper.BACKGROUND);
        JButton logout = UIHelper.secondaryButton("Log Out");
        logout.addActionListener(e -> logout());
        JButton exit = UIHelper.secondaryButton("6. Exit System");
        exit.setIcon(UIHelper.loadIcon("icon_exit.png", 20));
        exit.setForeground(UIHelper.DANGER);
        exit.addActionListener(e -> exitSystem());
        bottom.add(logout);
        bottom.add(exit);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(buttons, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void logout() {
        if (!UIHelper.confirm(this, "Log out of the current session?")) return;
        CurrentSession.getInstance().logout();
        UIHelper.show(new Login());
        dispose();
    }

    private void exitSystem() {
        if (!UIHelper.confirm(this, "Are you sure you want to exit the system?")) return;
        System.exit(0);
    }
}
