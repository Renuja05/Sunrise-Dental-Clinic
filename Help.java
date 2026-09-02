package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Step-by-step instructions for new staff, satisfying the Help Section requirement. */
public class Help extends JFrame {

    public Help() {
        super("Help - How to use this system");
        JEditorPane editorPane = new JEditorPane("text/html", HELP_HTML);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 16, 12, 16));
        root.add(new JScrollPane(editorPane), BorderLayout.CENTER);

        JButton close = UIHelper.secondaryButton("Close");
        close.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(close);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(640, 620);
    }

    private static final String HELP_HTML =
            "<html><body style='font-family:sans-serif; font-size:11px; padding:6px;'>" +
            "<h2 style='color:#1F3864;'>Sunrise Dental Clinic System - Staff Guide</h2>" +
            "<p>This guide explains how to use each part of the system, assuming " +
            "you have already logged in successfully.</p>" +

            "<h3 style='color:#1F3864;'>1. Logging In</h3>" +
            "<ol>" +
            "<li>Enter the username and password given to you by your Administrator.</li>" +
            "<li>Click <b>Log In</b>. If the details are wrong, an error message appears " +
            "below the password box - check for typos and try again.</li>" +
            "<li>If your account has been deactivated, contact an Administrator.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>2. Register New Appointment</h3>" +
            "<ol>" +
            "<li>From the Main Menu, click <b>Register New Appointment</b>.</li>" +
            "<li>Fill in the patient's name, address and contact number. If this contact " +
            "number already exists in the system, their saved details are reused/updated " +
            "automatically - you do not need to check first.</li>" +
            "<li>Choose the Dentist and Treatment Type from the drop-down lists.</li>" +
            "<li>Choose the appointment date and a 15-minute time slot between 08:00 and 17:00.</li>" +
            "<li>Click <b>Register Appointment</b>. If the dentist is already booked for that " +
            "exact time, you will be asked to pick a different slot - this is how the system " +
            "prevents double bookings.</li>" +
            "<li>Write down the <b>Appointment Number</b> shown in the confirmation message " +
            "and give it to the patient - they will need it for billing.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>3. Appointment Details / Calculate &amp; Print Bill</h3>" +
            "<ol>" +
            "<li>From the Main Menu, click <b>Appointment Details / Bill</b>.</li>" +
            "<li>Type the appointment number and click <b>Search</b>.</li>" +
            "<li>The patient and appointment details appear on the left.</li>" +
            "<li>If no bill exists yet, optionally tick the returning-patient discount box, " +
            "then click <b>Generate Bill</b>.</li>" +
            "<li>If a bill already exists for that appointment, it is shown automatically.</li>" +
            "<li>Click <b>Print Bill</b> to send the receipt to a printer.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>4. Reports</h3>" +
            "<ol>" +
            "<li>From the Main Menu, click <b>Reports</b>.</li>" +
            "<li><b>Daily Schedule</b>: pick a date and click Run Report to see every " +
            "appointment booked for that day.</li>" +
            "<li><b>Patient History</b>: search for a patient by name or contact number, " +
            "select them from the list, then click Run Report to see all of their past visits.</li>" +
            "<li><b>Revenue Summary</b>: pick a date range to see total income and a " +
            "day-by-day breakdown.</li>" +
            "<li><b>Dentist Workload</b>: pick a date range to see how many appointments " +
            "each dentist has had.</li>" +
            "<li>Use the <b>Print</b> button on any tab to print that table.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>5. Manage Staff Accounts (Administrators only)</h3>" +
            "<ol>" +
            "<li>From the Main Menu, click <b>Manage Staff Accounts</b>.</li>" +
            "<li>Click <b>Add Staff Account</b> to create a new Receptionist or Administrator login.</li>" +
            "<li>Select an account and click <b>Activate / Deactivate Selected</b> to enable " +
            "or block their access without deleting their account.</li>" +
            "<li>Select an account and click <b>Reset Password for Selected</b> if a staff " +
            "member forgets their password.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>6. Logging Out and Exiting</h3>" +
            "<ol>" +
            "<li>Click <b>Log Out</b> on the Main Menu to return to the login screen " +
            "without closing the application.</li>" +
            "<li>Click <b>Exit System</b> to close the application completely.</li>" +
            "</ol>" +

            "<h3 style='color:#1F3864;'>Tips</h3>" +
            "<ul>" +
            "<li>Every screen shows a short error message if something is missing or invalid " +
            "- read it before trying again.</li>" +
            "<li>The appointment number is the fastest way to find a record again later, " +
            "so keep a note of it after registering an appointment.</li>" +
            "</ul>" +
            "</body></html>";
}
