package com.ptda.tracker.ui.screens;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.LoginForm;
import com.ptda.tracker.ui.views.ProfileView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class NavigationMenu extends JPanel {
    private final MainFrame mainFrame;
    private String currentScreen;

    private static final String HOME_SCREEN = ScreenNames.HOME_SCREEN;
    private static final String BUDGETS_SCREEN = ScreenNames.BUDGETS_SCREEN;
    private static final String EXPENSES_SCREEN = ScreenNames.EXPENSES_SCREEN;
    private static final String TICKETS_SCREEN = ScreenNames.TICKETS_SCREEN;
    private static final String PROFILE_SCREEN = ScreenNames.PROFILE_SCREEN;
    private static final String NAVIGATION_SCREEN = ScreenNames.NAVIGATION_SCREEN;
    private static final String LOGIN_SCREEN = ScreenNames.LOGIN_FORM;

    public NavigationMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentScreen = "";

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(56, 56, 56)); // Cor de fundo #383838

        // Painel superior para os botões de navegação principais
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(56, 56, 56)); // Cor de fundo #383838
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os botões

        // Adiciona os botões principais (Home e Budgets) no painel superior
        addButtonToPanel(topPanel, "Home", HOME_SCREEN, gbc, 0);
        addButtonToPanel(topPanel, "Budgets", BUDGETS_SCREEN, gbc, 1);
        addButtonToPanel(topPanel, "Expenses", EXPENSES_SCREEN, gbc, 2);
        addButtonToPanel(topPanel, "Tickets", TICKETS_SCREEN, gbc, 3);

        // Painel inferior para "Profile" e "Logout"
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(56, 56, 56)); // Mesma cor do fundo
        gbc.gridy = 0; // Reinicia o índice para o painel inferior

        // Adiciona os botões "Profile" e "Logout" ao painel inferior
        addButtonToPanel(bottomPanel, "Profile", PROFILE_SCREEN, gbc, 0);
        addButtonToPanel(bottomPanel, "Logout", null, gbc, 1);

        // Adiciona os painéis superior e inferior ao layout principal
        add(topPanel, BorderLayout.CENTER); // Botões principais ficam no meio
        add(bottomPanel, BorderLayout.SOUTH); // Botões inferiores ficam embaixo
    }

    private void addButtonToPanel(JPanel panel, String label, String screenName, GridBagConstraints gbc, int row) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Cor de fundo #383838
        button.setForeground(Color.WHITE); // Texto em branco
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(56, 56, 56), 2, true)); // Borda arredondada
        button.setActionCommand(screenName); // Ação do botão, para verificar se é o botão ativo
        button.addActionListener(e -> {
            if (screenName != null) {
                navigateToScreen(screenName);
            } else {
                logout();
            }
        });

        // Configurações para o layout
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(button, gbc);
    }

    private JPanel getScreenInstance(String screenName) {
        switch (screenName) {
            case HOME_SCREEN: return new HomeScreen(mainFrame);
            case BUDGETS_SCREEN: return new BudgetsScreen(mainFrame);
            case EXPENSES_SCREEN: return new ExpensesScreen(mainFrame);
            case TICKETS_SCREEN: return new TicketsScreen(mainFrame);
            case PROFILE_SCREEN: return new ProfileView(mainFrame);
            default: return new JPanel(); // Retorna um painel vazio para telas não implementadas
        }
    }

    private void navigateToScreen(String screenName) {
        NavigationScreen navigationScreen = (NavigationScreen) mainFrame.getScreen(NAVIGATION_SCREEN);
        navigationScreen.setContent(screenName, () -> getScreenInstance(screenName));
        mainFrame.showScreen(NAVIGATION_SCREEN);
        updateActiveScreen(screenName);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
            preferences.remove("email");
            preferences.remove("password");
            UserSession.getInstance().clear();
            mainFrame.registerScreen(LOGIN_SCREEN, new LoginForm(mainFrame));
            mainFrame.showScreen(LOGIN_SCREEN);
        }
    }

    public void updateActiveScreen(String activeScreen) {
        this.currentScreen = activeScreen;

        // Alterar estilo para o botão ativo
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                for (Component innerComponent : ((JPanel) component).getComponents()) {
                    if (innerComponent instanceof JButton button) {
                        if (button.getActionCommand().equals(currentScreen)) {
                            button.setBackground(new Color(0, 0, 0)); // Cor de fundo para o botão ativo
                            button.setFont(new Font("Arial", Font.BOLD, 16)); // Aumenta o tamanho da fonte do botão ativo
                        } else {
                            button.setBackground(new Color(56, 56, 56)); // Cor de fundo normal
                            button.setFont(new Font("Arial", Font.BOLD, 14)); // Fonte normal
                        }
                    }
                }
            }
        }
    }
}
