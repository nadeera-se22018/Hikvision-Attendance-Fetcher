package com.attendance;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MainUI extends JFrame {

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton btnFetch;
    private JButton btnExport;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;

    public MainUI() {
        DatabaseManager.initializeDatabase();

        setTitle("Hikvision Attendance Fetcher");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"User ID", "Date", "In/Out Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        attendanceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        topPanel.add(new JLabel("Start Date:"));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.setPreferredSize(new Dimension(130, 25));
        startDateChooser.setDate(Date.from(LocalDate.now().minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        topPanel.add(startDateChooser);

        topPanel.add(new JLabel("End Date:"));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setPreferredSize(new Dimension(130, 25));
        endDateChooser.setDate(new Date());
        topPanel.add(endDateChooser);

        btnFetch = new JButton("Fetch Attendance");
        btnFetch.addActionListener(e -> fetchData());
        topPanel.add(btnFetch);

        btnExport = new JButton("Export to Excel");
        btnExport.addActionListener(e -> exportData());
        topPanel.add(btnExport);

        add(topPanel, BorderLayout.NORTH);

        loadLocalData();
    }

    private void loadLocalData() {
        tableModel.setRowCount(0);
        List<AttendanceRecord> records = DatabaseManager.getAllRecords();
        for (AttendanceRecord record : records) {
            tableModel.addRow(new Object[]{record.getUserId(), record.getDate(), record.getTime()});
        }
    }

    private void fetchData() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both Start Date and End Date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this, "Start Date cannot be after End Date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnFetch.setEnabled(false);
        btnFetch.setText("Fetching...");

        SwingWorker<List<AttendanceRecord>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AttendanceRecord> doInBackground() throws Exception {
                String jsonResponse = DataFetcher.fetchAttendanceJson(startDate, endDate);
                return AttendanceParser.parse(jsonResponse);
            }

            @Override
            protected void done() {
                try {
                    List<AttendanceRecord> records = get();
                    DatabaseManager.saveRecords(records);
                    loadLocalData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainUI.this,
                            "Network or Authentication Error: " + ex.getMessage(),
                            "Fetch Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnFetch.setEnabled(true);
                    btnFetch.setText("Fetch Attendance");
                }
            }
        };
        worker.execute();
    }

    private void exportData() {
        JOptionPane.showMessageDialog(this,
                "Excel Export functionality will be added in Step 22.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getAttendanceTable() {
        return attendanceTable;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainUI().setVisible(true);
        });
    }
}