package com.ptda.tracker.ui.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.renderers.BudgetListRenderer;
import com.ptda.tracker.ui.renderers.ExpenseListRenderer;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.ui.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class HomeScreen extends JPanel {
    private final MainFrame mainFrame;
    private final long userId;
    private JList<Budget> budgetList;
    private JList<Expense> expenseList;

    public HomeScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userId = UserSession.getInstance().getUser().getId();

        initUI();
        refreshData();
    }

    public void refreshData() {
        BudgetService budgetService = mainFrame.getContext().getBean(BudgetService.class);
        ExpenseService expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        TicketService ticketService = mainFrame.getContext().getBean(TicketService.class);

        int budgetCount = budgetService.getAllByUserId(userId).size();
        int expenseCount = expenseService.getAllByUserId(userId).size();
        int pendingTicketCount = ticketService.getOpenTicketsByUser(UserSession.getInstance().getUser()).size();

        budgetLabel.setText(BUDGETS + budgetCount);
        expenseLabel.setText(EXPENSES + expenseCount);
        ticketLabel.setText(PENDING_TICKETS + pendingTicketCount);

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

        JFreeChart pieChart = ChartFactory.createPieChart(EXPENSES_BY_CATEGORY, dataset, true, true, false);
        pieChartPanel.setChart(pieChart);
        pieChart.setBackgroundPaint(new Color(0, 0, 0, 0));

        // Set the title color to the system default label color
        TextTitle chartTitle = pieChart.getTitle();
        chartTitle.setPaint(UIManager.getColor("Label.foreground"));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with summaries
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(SUMMARY));
        add(summaryPanel, BorderLayout.NORTH);

        budgetLabel = new JLabel(BUDGETS + "0", SwingConstants.CENTER);
        expenseLabel = new JLabel(EXPENSES + "0", SwingConstants.CENTER);
        ticketLabel = new JLabel(PENDING_TICKETS + "0", SwingConstants.CENTER);

        budgetLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ticketLabel.setFont(new Font("Arial", Font.BOLD, 14));

        summaryPanel.add(budgetLabel);
        summaryPanel.add(expenseLabel);
        summaryPanel.add(ticketLabel);

        // Central panel with budget and expense lists
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        listsPanel.setBorder(BorderFactory.createTitledBorder(RECENT_DATA));
        add(listsPanel, BorderLayout.CENTER);

        budgetList = new JList<>();
        budgetList.setCellRenderer(new BudgetListRenderer()); // Set the cell renderer for budgetList
        budgetList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Budget selectedBudget = budgetList.getSelectedValue();
                    if (selectedBudget != null) {
                        mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, selectedBudget));
                        budgetList.clearSelection(); // Clear selection after opening details
                    }
                }
            }
        });
        JScrollPane budgetScrollPane = new JScrollPane(budgetList);
        budgetScrollPane.setBorder(BorderFactory.createTitledBorder(RECENT_BUDGETS));
        listsPanel.add(budgetScrollPane);

        expenseList = new JList<>();
        expenseList.setCellRenderer(new ExpenseListRenderer()); // Set the cell renderer for expenseList
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
                        expenseList.clearSelection(); // Clear selection after opening details
                    }
                }
            }
        });
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);
        expenseScrollPane.setBorder(BorderFactory.createTitledBorder(RECENT_EXPENSES));
        listsPanel.add(expenseScrollPane);

        // Bottom panel with chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(EXPENSES_BY_CATEGORY));
        add(chartPanel, BorderLayout.SOUTH);

        pieChartPanel = new ChartPanel(null);
        chartPanel.add(pieChartPanel, BorderLayout.CENTER);

        // Add a property change listener for background color changes
        addPropertyChangeListener("background", evt -> {
            Color newColor = (Color) evt.getNewValue();
            pieChartPanel.getChart().setBackgroundPaint(newColor);
        });
    }

    private JLabel budgetLabel, expenseLabel, ticketLabel;
    private ChartPanel pieChartPanel;
    private static final String
            BUDGETS = "Budgets: ",
            EXPENSES = "Expenses: ",
            PENDING_TICKETS = "Pending Tickets: ",
            SUMMARY = "Summary",
            RECENT_DATA = "Recent Data",
            RECENT_BUDGETS = "Recent Budgets",
            RECENT_EXPENSES = "Recent Expenses",
            EXPENSES_BY_CATEGORY = "Expenses by Category";
}