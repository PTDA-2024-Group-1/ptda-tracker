package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.theme.ThemeManager;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.components.renderers.BudgetListRenderer;
import com.ptda.tracker.ui.user.components.renderers.ExpenseListRenderer;
import com.ptda.tracker.ui.user.views.BudgetDetailView;
import com.ptda.tracker.ui.user.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class HomeScreen extends JPanel {
    private final MainFrame mainFrame;
    private final long userId;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final TicketService ticketService;

    public HomeScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.userId = UserSession.getInstance().getUser().getId();

        initUI();
        refreshData();
        setListeners();
    }

    private void setListeners() {
        budgetList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Budget selectedBudget = budgetList.getSelectedValue();
                    if (selectedBudget != null) {
                        mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, selectedBudget));
                        budgetList.clearSelection();
                    }
                }
            }
        });
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
                        expenseList.clearSelection();
                    }
                }
            }
        });
        addPropertyChangeListener("background", evt -> {
            applyThemeSettings(pieChartPanel.getChart());
            applyThemeSettings(barChartPanel.getChart());
        });
    }

    public void refreshData() {
        int budgetCount = budgetService.getCountByUserId(userId);
        int expenseCount = expenseService.getAllByUserId(userId).size();
        int pendingTicketCount = ticketService.getOpenTicketsByUser(UserSession.getInstance().getUser()).size();

        budgetLabel.setText(BUDGETS + ": " + budgetCount);
        expenseLabel.setText(EXPENSES + ": " + expenseCount);
        ticketLabel.setText(PENDING_TICKETS + ": " + pendingTicketCount);

        List<Budget> recentBudgets = budgetService.getRecentByUserId(userId, 5);
        budgetList.setListData(recentBudgets.toArray(new Budget[0]));

        List<Expense> recentExpenses = expenseService.getRecentExpensesByUserId(userId, 5);
        expenseList.setListData(recentExpenses.toArray(new Expense[0]));

        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = expenseService.getExpensesByCategory(userId);
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "", pieDataset, false, true, false
        );
        ChartUtilities.applyCurrentTheme(pieChart);
        applyThemeSettings(pieChart);

        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (Budget budget : recentBudgets) {
            double totalAmount = budgetService.getTotalBudgetAmount(budget.getId());
            barDataset.addValue(totalAmount, BUDGET, budget.getName());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "", BUDGET, TOTAL_AMOUNT, barDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartUtilities.applyCurrentTheme(barChart);
        applyThemeSettings(barChart);

        pieChartPanel.setChart(pieChart);
        barChartPanel.setChart(barChart);

        pieChartPanel.repaint();
        barChartPanel.repaint();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(SUMMARY));
        add(summaryPanel, BorderLayout.NORTH);

        budgetLabel = new JLabel(BUDGETS + ": 0", SwingConstants.CENTER);
        expenseLabel = new JLabel(EXPENSES + ": 0", SwingConstants.CENTER);
        ticketLabel = new JLabel(PENDING_TICKETS + ": 0", SwingConstants.CENTER);

        summaryPanel.add(budgetLabel);
        summaryPanel.add(expenseLabel);
        summaryPanel.add(ticketLabel);

        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        listsPanel.setBorder(BorderFactory.createTitledBorder(RECENT_DATA));
        add(listsPanel, BorderLayout.CENTER);

        budgetList = new JList<>();
        budgetList.setCellRenderer(new BudgetListRenderer());
        JScrollPane budgetScrollPane = new JScrollPane(budgetList);
        budgetScrollPane.setBorder(BorderFactory.createTitledBorder(RECENT_BUDGETS));
        listsPanel.add(budgetScrollPane);

        expenseList = new JList<>();
        expenseList.setCellRenderer(new ExpenseListRenderer());
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);
        expenseScrollPane.setBorder(BorderFactory.createTitledBorder(RECENT_EXPENSES));
        listsPanel.add(expenseScrollPane);

        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartPanel.setBorder(BorderFactory.createTitledBorder(EXPENSES_BY_CATEGORY));
        add(chartPanel, BorderLayout.SOUTH);

        pieChartPanel = new ChartPanel(null);
        barChartPanel = new ChartPanel(null);
        chartPanel.add(pieChartPanel);
        chartPanel.add(barChartPanel);
    }

    private void applyThemeSettings(JFreeChart chart) {
        Color backgroundColor = UIManager.getColor("Panel.background");
        chart.setBackgroundPaint(backgroundColor);
        chart.getPlot().setBackgroundPaint(backgroundColor);

        if (ThemeManager.getInstance().isDark()) {
            chart.getTitle().setPaint(Color.WHITE);
            if (chart.getPlot() instanceof CategoryPlot plot) {
                plot.getDomainAxis().setLabelPaint(Color.WHITE);
                plot.getRangeAxis().setLabelPaint(Color.WHITE);
                plot.getRenderer().setSeriesPaint(0, Color.WHITE);
            }
        }
    }

    private JList<Budget> budgetList;
    private JList<Expense> expenseList;
    private ChartPanel pieChartPanel;
    private ChartPanel barChartPanel;
    private JLabel budgetLabel, expenseLabel, ticketLabel;
    private static final String
            BUDGET = "Budget",
            BUDGETS = "Budgets",
            EXPENSES = "Expenses",
            PENDING_TICKETS = "Pending Tickets",
            SUMMARY = "Summary",
            RECENT_DATA = "Recent Data",
            RECENT_BUDGETS = "Recent Budgets",
            RECENT_EXPENSES = "Recent Expenses",
            EXPENSES_BY_CATEGORY = "Expenses by Category",
            TOTAL_AMOUNT = "Total Amount";
}