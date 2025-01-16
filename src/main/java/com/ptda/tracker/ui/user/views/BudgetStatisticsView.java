package com.ptda.tracker.ui.user.views;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.models.tracker.*;
import com.ptda.tracker.services.tracker.*;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetStatisticsView extends JPanel {

    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private final ExpenseDivisionService expenseDivisionService;
    private final BudgetSplitService budgetSplitService;
    private final Budget budget;
    private JTabbedPane chartTabbedPane;
    private Map<String, Double> userExpenses;

    public BudgetStatisticsView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.budgetSplitService = mainFrame.getContext().getBean(BudgetSplitService.class);
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.budget = budget;

        calculateUserExpenses();
        initializeUI();
    }

    private void createChartTabbedPane() {
        chartTabbedPane = new JTabbedPane();

        addChartTab(EXPENSES_BY_CATEGORY, this::createPieChart);
        addChartTab(TRENDS_OVER_TIME, this::createLineChart);
        addChartTab(CATEGORY_BREAKDOWN, this::createStackedBarChart);
        addChartTab(CUMULATIVE_TRENDS, this::createAreaChart);

        // Load first tab
        loadChartForTab(0);
    }

    private void addChartTab(String title, ChartCreator chartCreator) {
        JPanel panel = new JPanel(new BorderLayout());
        chartTabbedPane.addTab(title, panel);
        chartTabbedPane.addChangeListener(e -> handleTabChange());
    }

    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(chartTabbedPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Back button
        JButton backButton = createButton(BACK_BUTTON,
                e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW,
                        new BudgetDetailView(mainFrame, budget)));
        bottomPanel.add(createButtonPanel(backButton, FlowLayout.LEFT), BorderLayout.WEST);

        // Generate PDF button
        JButton pdfButton = createButton(GENERATE_PDF,
                e -> generatePdf());
        bottomPanel.add(createButtonPanel(pdfButton, FlowLayout.RIGHT), BorderLayout.EAST);

        return bottomPanel;
    }

    // Chart Creation Methods
    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<ExpenseCategory, Double> categoryExpenses = getCategoryExpenses();
        categoryExpenses.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                EXPENSES_BY_CATEGORY,
                dataset,
                true, true, false
        );

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId())
                .forEach(expense -> dataset.addValue(
                        expense.getAmount(),
                        EXPENSES,
                        expense.getDate()
                ));

        JFreeChart chart = ChartFactory.createLineChart(
                TRENDS_OVER_TIME,
                DATE, AMOUNT,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createStackedBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId())
                .forEach(expense -> dataset.addValue(
                        expense.getAmount(),
                        expense.getCategory().toString(),
                        expense.getCreatedBy().getName()
                ));

        JFreeChart chart = ChartFactory.createStackedBarChart(
                CATEGORY_BREAKDOWN,
                USER, AMOUNT,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createAreaChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId())
                .forEach(expense -> dataset.addValue(
                        expense.getAmount(),
                        CUMULATIVE_EXPENSES,
                        expense.getDate()
                ));

        JFreeChart chart = ChartFactory.createAreaChart(
                CUMULATIVE_TRENDS,
                DATE, AMOUNT,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        applyThemeSettings(chart);
        return chart;
    }

    // PDF Generation Methods
    private void generatePdf() {
        File fileToSave = showSaveDialog();
        if (fileToSave == null) return;

        try (PdfWriter writer = new PdfWriter(fileToSave);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Set document margins
            document.setMargins(50, 50, 50, 50);

            addPdfHeader(document);
            addBudgetSummary(document);
            addChartsToPdf(document);

            JOptionPane.showMessageDialog(this, PDF_SUCCESS + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            handlePdfError(e);
        }
    }

    private void addPdfHeader(Document document) throws MalformedURLException {
        //document.add(new Image(ImageDataFactory.create(AppConfig.LOGO_PATH)).setWidth(100));

        // Add title
        document.add(new Paragraph(PDF_TITLE + budget.getName())
                .setBold()
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Add date and report info
        document.add(new Paragraph(GENERATED_ON + java.time.LocalDate.now().toString())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(30));
    }

    private void addBudgetSummary(Document document) {
        // Add budget overview
        document.add(new Paragraph(BUDGET_OVERVIEW)
                .setBold()
                .setFontSize(18)
                .setMarginBottom(10));

        // Calculate total expenses
        double totalExpenses = getCategoryExpenses().values().stream().mapToDouble(Double::doubleValue).sum();

        // Add summary table
        Table summaryTable = new Table(new float[]{1, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(30);

        // Add table headers
        summaryTable.addHeaderCell(createCell(METRIC, true));
        summaryTable.addHeaderCell(createCell(VALUE, true));

        // Add table rows
        summaryTable.addCell(createCell(TOTAL_EXPENSES));
        summaryTable.addCell(createCell(String.format("$%.2f", totalExpenses)));

        summaryTable.addCell(createCell(NUMBER_OF_CATEGORIES));
        summaryTable.addCell(createCell(String.valueOf(getCategoryExpenses().size())));

        summaryTable.addCell(createCell(NUMBER_OF_USERS));
        summaryTable.addCell(createCell(String.valueOf(userExpenses.size())));

        document.add(summaryTable);
    }

    private void addChartsToPdf(Document document) throws IOException {
        document.add(new Paragraph(EXPENSE_ANALYSIS)
                .setBold()
                .setFontSize(18)
                .setMarginTop(20)
                .setMarginBottom(20));

        // Create a 2x2 grid for charts
        Table chartGrid = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        // Add charts to grid
        addChartToGrid(chartGrid, createPieChart(), EXPENSES_BY_CATEGORY);
        addChartToGrid(chartGrid, createLineChart(), TRENDS_OVER_TIME);
        addChartToGrid(chartGrid, createStackedBarChart(), CATEGORY_BREAKDOWN);
        addChartToGrid(chartGrid, createAreaChart(), CUMULATIVE_TRENDS);

        document.add(chartGrid);
    }

    private void addChartToGrid(Table grid, JFreeChart chart, String title) throws IOException {
        Cell cell = new Cell();

        // Add chart title
        cell.add(new Paragraph(title)
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Prepare and add chart
        prepareChartForPdf(chart);
        byte[] chartBytes = generateChartBytes(chart);
        Image chartImage = new Image(ImageDataFactory.create(chartBytes))
                .setAutoScale(true)
                .setWidth(UnitValue.createPercentValue(100));

        cell.add(chartImage);
        cell.setPadding(10);
        grid.addCell(cell);
    }

    private Cell createCell(String content, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
        }
        return cell;
    }

    private Cell createCell(String content) {
        return createCell(content, false);
    }

    // Utility Methods
    private void calculateUserExpenses() {
        userExpenses = budgetSplitService.getAllByBudgetId(budget.getId()).stream()
                .collect(Collectors.groupingBy(
                        budgetSplit -> budgetSplit.getUser().getName(),
                        Collectors.summingDouble(BudgetSplit::getAmount)
                ));
    }

    private Map<ExpenseCategory, Double> getCategoryExpenses() {
        return expenseService.getAllByBudgetId(budget.getId()).stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    private void handleTabChange() {
        int selectedIndex = chartTabbedPane.getSelectedIndex();
        loadChartForTab(selectedIndex);
    }

    private void loadChartForTab(int index) {
        JPanel panel = (JPanel) chartTabbedPane.getComponentAt(index);
        if (panel.getComponentCount() == 0) {
            String title = chartTabbedPane.getTitleAt(index);
            JFreeChart chart = createChartForTitle(title);
            panel.add(new ChartPanel(chart), BorderLayout.CENTER);
            panel.revalidate();
        }
    }

    private JFreeChart createChartForTitle(String title) {
        if (EXPENSES_BY_CATEGORY.equals(title)) {
            return createPieChart();
        } else if (TRENDS_OVER_TIME.equals(title)) {
            return createLineChart();
        } else if (CATEGORY_BREAKDOWN.equals(title)) {
            return createStackedBarChart();
        } else if (CUMULATIVE_TRENDS.equals(title)) {
            return createAreaChart();
        } else {
            throw new IllegalArgumentException("Unknown chart type: " + title);
        }
    }

    private void addChartToPdf(Document document, JFreeChart chart, String title) throws IOException {
        prepareChartForPdf(chart);

        byte[] chartBytes = generateChartBytes(chart);
        document.add(new Paragraph(title).setBold().setFontSize(14).setMarginTop(20));
        document.add(new Image(ImageDataFactory.create(chartBytes)).setAutoScale(true));
    }

    private void prepareChartForPdf(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
    }

    private byte[] generateChartBytes(JFreeChart chart) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(output, chart, 500, 400);
        return output.toByteArray();
    }

    private File showSaveDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(SAVE_PDF_DIALOG);
        fileChooser.setSelectedFile(new File(PDF_DEFAULT_NAME));

        return fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
                ? fileChooser.getSelectedFile()
                : null;
    }

    private void handlePdfError(Exception e) {
        JOptionPane.showMessageDialog(this, PDF_ERROR + e.getMessage());
        e.printStackTrace();
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private JPanel createButtonPanel(JButton button, int alignment) {
        JPanel panel = new JPanel(new FlowLayout(alignment));
        panel.add(button);
        return panel;
    }

    // Theme-related Methods
    private void applyThemeSettings(JFreeChart chart) {
        Color backgroundColor = UIManager.getColor("Panel.background");
        chart.setBackgroundPaint(backgroundColor);
        chart.getPlot().setBackgroundPaint(backgroundColor);

        if (isDarkTheme(backgroundColor)) {
            applyDarkTheme(chart);
        }
    }

    private void applyDarkTheme(JFreeChart chart) {
        chart.getTitle().setPaint(Color.WHITE);

        if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.getDomainAxis().setLabelPaint(Color.WHITE);
            plot.getRangeAxis().setLabelPaint(Color.WHITE);
        }
    }

    private boolean isDarkTheme(Color backgroundColor) {
        double brightness = Math.sqrt(
                backgroundColor.getRed() * backgroundColor.getRed() * 0.241 +
                        backgroundColor.getGreen() * backgroundColor.getGreen() * 0.691 +
                        backgroundColor.getBlue() * backgroundColor.getBlue() * 0.068
        );
        return brightness < 130;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        createChartTabbedPane();
        JScrollPane scrollPane = createScrollPane();
        JPanel bottomPanel = createBottomPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Functional interfaces
    @FunctionalInterface
    private interface ChartCreator {
        JFreeChart create();
    }


    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            EXPENSES = localeManager.getTranslation("expenses"),
            DATE = localeManager.getTranslation("date"),
            AMOUNT = localeManager.getTranslation("amount"),
            USER = localeManager.getTranslation("user"),
            CUMULATIVE_EXPENSES = localeManager.getTranslation("cumulative_expenses"),
            GENERATED_ON = localeManager.getTranslation("generated_on"),
            BUDGET_OVERVIEW = localeManager.getTranslation("budget_overview"),
            METRIC = localeManager.getTranslation("metric"),
            VALUE = localeManager.getTranslation("value"),
            TOTAL_EXPENSES = localeManager.getTranslation("total_expenses"),
            NUMBER_OF_CATEGORIES = localeManager.getTranslation("number_categories"),
            NUMBER_OF_USERS = localeManager.getTranslation("number_users"),
            EXPENSE_ANALYSIS = localeManager.getTranslation("expense_analysis"),
            END_OF_REPORT = localeManager.getTranslation("end_of_report"),
            EXPENSES_BY_CATEGORY = localeManager.getTranslation("expenses_by_category"),
            TRENDS_OVER_TIME = localeManager.getTranslation("trends_over_time"),
            CATEGORY_BREAKDOWN = localeManager.getTranslation("category_breakdown"),
            CUMULATIVE_TRENDS = localeManager.getTranslation("cumulative_trends"),
            BACK_BUTTON = localeManager.getTranslation("back"),
            GENERATE_PDF = localeManager.getTranslation("generate_pdf"),
            SAVE_PDF_DIALOG = localeManager.getTranslation("save_pdf_dialog"),
            PDF_DEFAULT_NAME = localeManager.getTranslation("pdf_default_name"),
            PDF_TITLE = localeManager.getTranslation("pdf_title"),
            PDF_SUCCESS = localeManager.getTranslation("pdf_success"),
            PDF_ERROR = localeManager.getTranslation("pdf_error");
}