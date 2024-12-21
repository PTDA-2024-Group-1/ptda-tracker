package com.ptda.tracker.ui.admin.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.dialogs.ManageUserDialog;
import com.ptda.tracker.ui.admin.screens.AdministrationOptionsScreen;
import com.ptda.tracker.ui.admin.renderers.UserRenderer;
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
    private AdministrationOptionsScreen adminOptionsScreen;

    public ManageUserView(MainFrame mainFrame, AdministrationOptionsScreen adminOptionsScreen) {
        this.mainFrame = mainFrame;
        this.adminOptionsScreen = adminOptionsScreen;
        userService = mainFrame.getContext().getBean(UserService.class);
        initComponents();
        setListeners();
        loadUserData();
    }

    private void setListeners() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Long userId = (Long) userTableModel.getValueAt(selectedRow, 0);
                        Optional<User> user = userService.getById(userId);
                        new ManageUserDialog(mainFrame, () -> {
                            loadUserData();
                            adminOptionsScreen.refreshData();
                        }, user.orElse(null), adminOptionsScreen).setVisible(true);
                    }
                }
            }
        });
    }

    private void loadUserData() {
        List<User> users = userService.getAllUsers();
        userTableModel.setRowCount(0);

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
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST; // Align to the left
        c.insets = new Insets(0, 0, 10, 0); // Add some bottom padding
        add(titleLabel, c);

        // Table
        userTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role", "Email Verified", "Active"}, 0);
        userTable = new JTable(userTableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setDefaultEditor(Object.class, null);
        UserRenderer renderer = new UserRenderer();
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JScrollPane scrollPane = new JScrollPane(userTable);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 10, 0); // Add some bottom padding
        add(scrollPane, c);

        // Back Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Use a JPanel for better layout
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            adminOptionsScreen.refreshData();
            mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
        });
        buttonPanel.add(backButton);
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL; // Make the button panel fill the width
        add(buttonPanel, c);
    }
}