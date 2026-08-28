package com.attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainUI extends JFrame {

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton btnFetch;

    public MainUI() {
        DatabaseManager.initializeDatabase();

        setTitle("Hikvision Attendance Fetcher");
        setSize(700, 500);
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

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetch = new JButton("Fetch Attendance Data");
        btnFetch.addActionListener(e -> fetchData());
        topPanel.add(btnFetch);
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
        btnFetch.setEnabled(false);
        btnFetch.setText("Fetching Data...");

        SwingWorker<List<AttendanceRecord>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AttendanceRecord> doInBackground() throws Exception {
                String jsonResponse = DataFetcher.fetchAttendanceJson();
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
                    btnFetch.setText("Fetch Attendance Data");
                }
            }
        };
        worker.execute();
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
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