package com.ptda.tracker.ui.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.ui.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HomeScreen extends JPanel {
    private final MainFrame mainFrame;
    private final long userId;
    private JList<Budget> budgetList;
    private JList<Expense> expenseList;
    private JLabel budgetLabel, expenseLabel, ticketLabel;
    private ChartPanel pieChartPanel;

    public HomeScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userId = UserSession.getInstance().getUser().getId();

        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with summaries
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        add(summaryPanel, BorderLayout.NORTH);

        budgetLabel = new JLabel("Budgets: 0", SwingConstants.CENTER);
        expenseLabel = new JLabel("Expenses: 0", SwingConstants.CENTER);
        ticketLabel = new JLabel("Pending Tickets: 0", SwingConstants.CENTER);

        budgetLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ticketLabel.setFont(new Font("Arial", Font.BOLD, 14));

        summaryPanel.add(budgetLabel);
        summaryPanel.add(expenseLabel);
        summaryPanel.add(ticketLabel);

        // Central panel with budget and expense lists
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        listsPanel.setBorder(BorderFactory.createTitledBorder("Recent Data"));
        add(listsPanel, BorderLayout.CENTER);

        budgetList = new JList<>();
        budgetList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Budget selectedBudget = budgetList.getSelectedValue();
                    if (selectedBudget != null) {
                        mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, selectedBudget));
                        budgetList.clearSelection(); // Limpar seleção após abrir detalhes
                    }
                }
            }
        });
        JScrollPane budgetScrollPane = new JScrollPane(budgetList);
        budgetScrollPane.setBorder(BorderFactory.createTitledBorder("Recent Budgets"));
        listsPanel.add(budgetScrollPane);

        expenseList = new JList<>();
        expenseList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Expense selectedExpense = expenseList.getSelectedValue();
                    if (selectedExpense != null) {
                        mainFrame.registerAndShowScreen(
                                ScreenNames.EXPENSE_DETAIL_VIEW,
                                new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), HomeScreen.this::refreshData)
                        );
                        expenseList.clearSelection(); // Limpar seleção após abrir detalhes
                    }
                }
            }
        });
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);
        expenseScrollPane.setBorder(BorderFactory.createTitledBorder("Recent Expenses"));
        listsPanel.add(expenseScrollPane);

        // Bottom panel with chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Expenses by Category"));
        add(chartPanel, BorderLayout.SOUTH);

        pieChartPanel = new ChartPanel(null);
        pieChartPanel.setBackground(getBackground());
        chartPanel.add(pieChartPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        BudgetService budgetService = mainFrame.getContext().getBean(BudgetService.class);
        ExpenseService expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        TicketService ticketService = mainFrame.getContext().getBean(TicketService.class);

        int budgetCount = budgetService.getAllByUserId(userId).size();
        int expenseCount = expenseService.getAllByUserId(userId).size();
        int pendingTicketCount = ticketService.getOpenTicketsByUser(UserSession.getInstance().getUser()).size();

        budgetLabel.setText("Budgets: " + budgetCount);
        expenseLabel.setText("Expenses: " + expenseCount);
        ticketLabel.setText("Pending Tickets: " + pendingTicketCount);

        List<Budget> recentBudgets = budgetService.getAllByUserId(userId).stream()
                .sorted((b1, b2) -> Long.compare(b2.getCreatedAt(), b1.getCreatedAt()))
                .limit(5)
                .toList();
        budgetList.setListData(recentBudgets.toArray(new Budget[0]));

        List<Expense> recentExpenses = expenseService.getAllByUserId(userId).stream()
                .sorted((e1, e2) -> Long.compare(e2.getCreatedAt(), e1.getCreatedAt()))
                .limit(5)
                .toList();
        expenseList.setListData(recentExpenses.toArray(new Expense[0]));

        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = expenseService.getExpensesByCategory(userId);
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart("Expenses by Category", dataset, true, true, false);
        pieChartPanel.setChart(pieChart);
    }
}