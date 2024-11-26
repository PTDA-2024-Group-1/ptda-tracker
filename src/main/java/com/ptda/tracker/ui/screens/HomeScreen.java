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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class HomeScreen extends JPanel {
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final TicketService ticketService;
    private final long userId;
    private JList<Budget> budgetList;
    private JList<Expense> expenseList;
    private JLabel budgetLabel;
    private JLabel expenseLabel;
    private JLabel ticketLabel;
    private ChartPanel pieChartPanel;

    public HomeScreen(MainFrame mainFrame) {
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.userId = UserSession.getInstance().getUser().getId();

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
                        mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense));
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
        chartPanel.add(pieChartPanel, BorderLayout.CENTER);

        // Initial data load
        refreshData();
    }

    public void refreshData() {
        int budgetCount = budgetService.getAllByUserId(userId).size();
        int expenseCount = expenseService.getAllByUserId(userId).size();
        int pendingTicketCount = ticketService.getOpenTicketsByUser(UserSession.getInstance().getUser()).size();

        budgetLabel.setText("Budgets: " + budgetCount);
        expenseLabel.setText("Expenses: " + expenseCount);
        ticketLabel.setText("Pending Tickets: " + pendingTicketCount);

        List<Budget> recentBudgets = budgetService.getAllByUserId(userId).stream().limit(5).toList();
        budgetList.setListData(recentBudgets.toArray(new Budget[0]));

        List<Expense> recentExpenses = expenseService.getAllByUserId(userId).stream().limit(5).toList();
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