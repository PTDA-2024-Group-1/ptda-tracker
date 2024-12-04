package com.ptda.tracker.ui.admin.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.renderers.UserRenderer;
import com.ptda.tracker.ui.admin.dialogs.ManageUserDialog;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class ManageUserView extends JPanel {
    private UserService userService;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private MainFrame mainFrame;

    public ManageUserView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        userService = mainFrame.getContext().getBean(UserService.class);
        initComponents();
        setListeners();
        loadUserData();
    }

    private void setListeners() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Long userId = (Long) userTableModel.getValueAt(selectedRow, 0);
                        Optional<User> user = userService.getById(userId);
                        new ManageUserDialog(mainFrame, null, user.orElse(null)).setVisible(true);
                    }
                }
            }
        });
    }

    private void loadUserData() {
        List<User> users = userService.getAllUsers();
        userTableModel.setRowCount(0); // Clear existing data

        for (User user : users) {
            userTableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getUserType(),
                    user.isEmailVerified(),
                    user.isActive()
            });
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        userTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role", "Email Verified", "Active"}, 0);
        userTable = new JTable(userTableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setDefaultEditor(Object.class, null);

        // Set the custom renderer for the table
        UserRenderer renderer = new UserRenderer();
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add Back button
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        leftButtonPanel.add(backButton);
        add(leftButtonPanel, BorderLayout.SOUTH);
    }
}