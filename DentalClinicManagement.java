package dentalclinic.management;

import db.DBConnection;
import view.Login;
import view.UIHelper;

import javax.swing.*;

/**
 * Entry point for the whole application — run this file (right-click →
 * Run File) to start the system. It checks the database is reachable
 * (through the {@code DBConnection} Singleton) and then opens the
 * Login screen.
 */
public class DentalClinicManagement {

    public static void main(String[] args) {
        UIHelper.applySystemLookAndFeel();

        boolean dbOk = DBConnection.getInstance().testConnection();
        if (!dbOk) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n" +
                    "Please check that:\n" +
                    "1. MySQL is running.\n" +
                    "2. You ran sql/schema.sql to create the sunrise_dental database.\n" +
                    "3. The URL/username/password in db/DBConnection.java are correct.\n" +
                    "4. mysql-connector-j-x.x.x.jar has been added to the project Libraries.\n\n" +
                    "The login screen will still open, but nothing will work until this is fixed.",
                    "Database Connection Problem",
                    JOptionPane.WARNING_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> UIHelper.show(new Login()));
    }
}
