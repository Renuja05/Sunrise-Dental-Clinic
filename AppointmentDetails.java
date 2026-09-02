package view;

import dao.AppointmentDAO;
import dao.BillDAO;
import model.Appointment;
import model.Bill;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Combines "Display Appointment Details" and "Calculate and Print Bill"
 * in one screen (Task A, Figure 5): printing a bill always requires an
 * appointment to have been looked up first. The consultation fee is a
 * fixed clinic charge; the optional 10% loyalty discount is a simple
 * checkbox instead of a full Strategy-pattern hierarchy.
 */
public class AppointmentDetails extends JFrame {

    private static final double CONSULTATION_FEE = 1000.00;
    private static final double LOYALTY_DISCOUNT_RATE = 0.10;
    private static final int LOYALTY_MIN_VISITS = 3;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();

    private final JTextField apptNoField = UIHelper.textField();
    private final JLabel statusLabel = new JLabel(" ");

    private final JLabel patientNameValue = valueLabel();
    private final JLabel addressValue = valueLabel();
    private final JLabel contactValue = valueLabel();
    private final JLabel dentistValue = valueLabel();
    private final JLabel treatmentValue = valueLabel();
    private final JLabel dateTimeValue = valueLabel();
    private final JLabel statusValue = valueLabel();

    private final JCheckBox discountCheck = new JCheckBox("Apply returning-patient discount (10% off treatment, from 4th visit)");
    private final JButton generateBillButton = UIHelper.primaryButton("Generate Bill");
    private final JButton printButton = UIHelper.secondaryButton("Print Bill");
    private final JTextArea receiptArea = new JTextArea(14, 34);

    private Appointment currentAppointment;

    public AppointmentDetails() {
        super("Appointment Details / Calculate & Print Bill");
        buildUi();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 580);
    }

    private static JLabel valueLabel() {
        JLabel l = new JLabel("-");
        l.setFont(UIHelper.FONT_LABEL);
        return l;
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.add(new JLabel(UIHelper.loadIcon("icon_search.png", 28)));
        searchBar.add(UIHelper.label("Appointment Number:"));
        apptNoField.setColumns(16);
        searchBar.add(apptNoField);
        JButton searchButton = UIHelper.primaryButton("Search");
        searchButton.addActionListener(e -> search());
        getRootPane().setDefaultButton(searchButton);
        searchBar.add(searchButton);

        JPanel details = new JPanel(new GridBagLayout());
        details.setBorder(BorderFactory.createTitledBorder(null, "Appointment Details",
                TitledBorder.LEADING, TitledBorder.TOP, UIHelper.FONT_HEADING, UIHelper.NAVY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 12);
        gbc.weightx = 1;
        String[] labels = {"Patient Name:", "Address:", "Contact Number:", "Dentist:", "Treatment:", "Date / Time:", "Status:"};
        JLabel[] values = {patientNameValue, addressValue, contactValue, dentistValue, treatmentValue, dateTimeValue, statusValue};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            details.add(UIHelper.label(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            details.add(values[i], gbc);
        }

        JPanel billing = new JPanel(new BorderLayout(8, 8));
        billing.setBorder(BorderFactory.createTitledBorder(null, "Billing",
                TitledBorder.LEADING, TitledBorder.TOP, UIHelper.FONT_HEADING, UIHelper.NAVY));

        JPanel billingControls = new JPanel();
        billingControls.setLayout(new BoxLayout(billingControls, BoxLayout.Y_AXIS));
        discountCheck.setFont(UIHelper.FONT_LABEL);
        JPanel discountRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        discountRow.add(discountCheck);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        generateBillButton.addActionListener(e -> generateBill());
        printButton.addActionListener(e -> printBill());
        printButton.setEnabled(false);
        buttonRow.add(generateBillButton);
        buttonRow.add(printButton);
        billingControls.add(discountRow);
        billingControls.add(buttonRow);

        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receiptArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        billing.add(billingControls, BorderLayout.NORTH);
        billing.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        setBillingEnabled(false);

        JPanel centre = new JPanel(new GridLayout(1, 2, 16, 0));
        centre.add(details);
        centre.add(billing);

        statusLabel.setFont(UIHelper.FONT_LABEL);
        statusLabel.setForeground(UIHelper.DANGER);

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchBar, BorderLayout.NORTH);
        top.add(statusLabel, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(centre, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void setBillingEnabled(boolean enabled) {
        discountCheck.setEnabled(enabled);
        generateBillButton.setEnabled(enabled);
    }

    private void search() {
        String apptNo = apptNoField.getText();
        String err = Validator.appointmentNumber(apptNo);
        if (err != null) {
            statusLabel.setText(err);
            return;
        }

        clearDetails();
        setBillingEnabled(false);
        printButton.setEnabled(false);
        receiptArea.setText("");

        try {
            Appointment appointment = appointmentDAO.findByNumber(apptNo.trim().toUpperCase());
            if (appointment == null) {
                statusLabel.setText("No appointment found with number " + apptNo + ".");
                return;
            }
            currentAppointment = appointment;
            patientNameValue.setText(str(appointment.getPatientName()));
            addressValue.setText(str(appointment.getPatientAddress()));
            contactValue.setText(str(appointment.getPatientContact()));
            dentistValue.setText(str(appointment.getDentistName()));
            treatmentValue.setText(str(appointment.getTreatmentName()));
            dateTimeValue.setText(appointment.getAppointmentDate() + "  " + appointment.getAppointmentTime());
            statusValue.setText(appointment.getStatus());
            statusLabel.setText(" ");

            Bill existingBill = billDAO.findByAppointmentNumber(appointment.getAppointmentNumber());
            if (existingBill != null) {
                renderReceipt(existingBill);
                setBillingEnabled(false);
                printButton.setEnabled(true);
            } else {
                receiptArea.setText("No bill has been generated for this appointment yet.");
                setBillingEnabled(true);
                printButton.setEnabled(false);
            }
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void generateBill() {
        if (currentAppointment == null) return;
        try {
            int visitCount = appointmentDAO.findByPatientId(currentAppointment.getPatientId()).size();
            double treatmentCost = currentAppointment.getTreatmentCost();
            double discount = 0;
            if (discountCheck.isSelected() && visitCount >= LOYALTY_MIN_VISITS) {
                discount = treatmentCost * LOYALTY_DISCOUNT_RATE;
            }

            Bill bill = billDAO.generateBill(currentAppointment.getAppointmentNumber(),
                    CONSULTATION_FEE, treatmentCost, discount);
            if (bill == null) {
                statusLabel.setText("A bill already exists for this appointment.");
                return;
            }
            bill.setPatientName(currentAppointment.getPatientName());
            bill.setDentistName(currentAppointment.getDentistName());
            bill.setTreatmentName(currentAppointment.getTreatmentName());

            renderReceipt(bill);
            statusLabel.setText(" ");
            statusValue.setText("COMPLETED");
            setBillingEnabled(false);
            printButton.setEnabled(true);
        } catch (SQLException e) {
            statusLabel.setText("Could not generate the bill: " + e.getMessage());
        }
    }

    private void renderReceipt(Bill bill) {
        NumberFormat currency = NumberFormat.getNumberInstance(Locale.US);
        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);

        StringBuilder sb = new StringBuilder();
        sb.append("        SUNRISE DENTAL CLINIC\n");
        sb.append("     Appointment & Billing Receipt\n");
        sb.append("----------------------------------------\n");
        sb.append("Bill No.        : ").append(str(bill.getBillNumber())).append('\n');
        sb.append("Appointment No. : ").append(str(bill.getAppointmentNumber())).append('\n');
        sb.append("Patient         : ").append(str(bill.getPatientName())).append('\n');
        sb.append("Dentist         : ").append(str(bill.getDentistName())).append('\n');
        sb.append("Treatment       : ").append(str(bill.getTreatmentName())).append('\n');
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s Rs. %10s%n", "Consultation Fee", currency.format(bill.getConsultationFee())));
        sb.append(String.format("%-20s Rs. %10s%n", "Treatment Cost", currency.format(bill.getTreatmentCost())));
        sb.append(String.format("%-20s Rs. %10s%n", "Discount", currency.format(bill.getDiscount())));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s Rs. %10s%n", "TOTAL DUE", currency.format(bill.getTotalAmount())));
        sb.append("----------------------------------------\n");
        sb.append("Thank you for visiting Sunrise Dental Clinic.\n");
        receiptArea.setText(sb.toString());
        receiptArea.setCaretPosition(0);
    }

    private void printBill() {
        try {
            boolean printed = receiptArea.print();
            if (!printed) {
                statusLabel.setText("Printing was cancelled.");
            }
        } catch (PrinterException e) {
            UIHelper.showError(this, "Could not print the bill: " + e.getMessage());
        }
    }

    private void clearDetails() {
        for (JLabel l : new JLabel[]{patientNameValue, addressValue, contactValue, dentistValue,
                treatmentValue, dateTimeValue, statusValue}) {
            l.setText("-");
        }
        currentAppointment = null;
    }

    private String str(Object o) {
        return o == null ? "-" : o.toString();
    }
}
