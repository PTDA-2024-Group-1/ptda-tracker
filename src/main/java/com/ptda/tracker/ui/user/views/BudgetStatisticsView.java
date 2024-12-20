package com.ptda.tracker.ui.user.views;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
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
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetStatisticsView extends JPanel {
    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private final ExpenseDivisionService expenseDivisionService;
    private final Budget budget;

    public BudgetStatisticsView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.budget = budget;
        initComponents();
    }

    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> userExpenses = expenseService.getAllByBudgetId(budget.getId()).stream()
                .flatMap(expense -> expenseDivisionService.getAllByExpenseId(expense.getId()).stream())
                .collect(Collectors.groupingBy(expenseDivision -> expenseDivision.getUser().getName(), Collectors.summingDouble(ExpenseDivision::getAmount)));

        userExpenses.forEach((user, amount) -> dataset.addValue(amount, "Amount", user));

        JFreeChart chart = ChartFactory.createBarChart(
                EXPENSES_BY_USER_TITLE,
                "User",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<ExpenseCategory, Double> categoryExpenses = expenseService.getAllByBudgetId(budget.getId()).stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        categoryExpenses.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                EXPENSES_BY_CATEGORY_TITLE,
                dataset,
                true, true, false);

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId()).forEach(expense -> {
            dataset.addValue(expense.getAmount(), "Expenses", expense.getDate());
        });

        JFreeChart chart = ChartFactory.createLineChart(
                TRENDS_OVER_TIME_TITLE,
                "Date",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createStackedBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId()).forEach(expense -> {
            dataset.addValue(expense.getAmount(), expense.getCategory().toString(), expense.getCreatedBy().getName());
        });

        JFreeChart chart = ChartFactory.createStackedBarChart(
                CATEGORY_BREAKDOWN_TITLE,
                "User",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        applyThemeSettings(chart);
        return chart;
    }

    private JFreeChart createAreaChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        expenseService.getAllByBudgetId(budget.getId()).forEach(expense -> {
            dataset.addValue(expense.getAmount(), "Cumulative Expenses", expense.getDate());
        });

        JFreeChart chart = ChartFactory.createAreaChart(
                CUMULATIVE_TRENDS_TITLE,
                "Date",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        applyThemeSettings(chart);
        return chart;
    }

    private void generatePdf(Map<String, Double> userExpenses) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(SAVE_PDF_DIALOG_TITLE);
        fileChooser.setSelectedFile(new File(PDF_DEFAULT_NAME));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String outputPath = fileToSave.getAbsolutePath();

            try {
                // Criar o documento PDF
                PdfWriter writer = new PdfWriter(outputPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Adicionar título
                document.add(new com.itextpdf.layout.element.Paragraph(PDF_TITLE)
                        .setBold().setFontSize(16).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                // Adicionar gráficos
                addChartToPdf(document, createBarChart(), EXPENSES_BY_USER_TITLE);
                addChartToPdf(document, createPieChart(), EXPENSES_BY_CATEGORY_TITLE);
                addChartToPdf(document, createLineChart(), TRENDS_OVER_TIME_TITLE);
                addChartToPdf(document, createStackedBarChart(), CATEGORY_BREAKDOWN_TITLE);
                addChartToPdf(document, createAreaChart(), CUMULATIVE_TRENDS_TITLE);

                // Adicionar participantes
                document.add(new com.itextpdf.layout.element.Paragraph(PARTICIPANTS_AND_AMOUNTS_TITLE)
                        .setBold().setFontSize(12).setMarginTop(20));
                userExpenses.forEach((user, amount) -> {
                    document.add(new com.itextpdf.layout.element.Paragraph(user + ": " + String.format("%.2f €", amount)));
                });

                // Fechar o documento
                document.close();
                JOptionPane.showMessageDialog(this, PDF_SUCCESS_MESSAGE + outputPath);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, PDF_ERROR_MESSAGE + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addChartToPdf(Document document, JFreeChart chart, String chartTitle) throws IOException {
        // Definir a cor de fundo clara para o gráfico
        Color lightBackground = Color.WHITE;
        chart.setBackgroundPaint(lightBackground);
        chart.getPlot().setBackgroundPaint(lightBackground);

        // Exportar o gráfico para um ByteArrayOutputStream
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOutputStream, chart, 500, 400);
        byte[] chartBytes = chartOutputStream.toByteArray();

        // Adicionar o título do gráfico e a imagem ao PDF
        document.add(new com.itextpdf.layout.element.Paragraph(chartTitle)
                .setBold().setFontSize(14).setMarginTop(20));
        Image chartImage = new Image(ImageDataFactory.create(chartBytes));
        document.add(chartImage.setAutoScale(true));
    }

    private void applyThemeSettings(JFreeChart chart) {
        Color backgroundColor = UIManager.getColor("Panel.background");
        chart.setBackgroundPaint(backgroundColor);
        chart.getPlot().setBackgroundPaint(backgroundColor);

        if (isDarkTheme(backgroundColor)) {
            chart.getTitle().setPaint(Color.WHITE);
            if (chart.getPlot() instanceof CategoryPlot) {
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                plot.getDomainAxis().setLabelPaint(Color.WHITE);
                plot.getRangeAxis().setLabelPaint(Color.WHITE);
            } else if (chart.getPlot() instanceof PiePlot) {
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setLabelPaint(Color.WHITE);
            }
        }
    }

    private boolean isDarkTheme(Color backgroundColor) {
        int brightness = (int) Math.sqrt(
                backgroundColor.getRed() * backgroundColor.getRed() * 0.241 +
                        backgroundColor.getGreen() * backgroundColor.getGreen() * 0.691 +
                        backgroundColor.getBlue() * backgroundColor.getBlue() * 0.068);
        return brightness < 130;
    }



    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel de Participantes
        JPanel participantsPanel = new JPanel();
        participantsPanel.setLayout(new BoxLayout(participantsPanel, BoxLayout.Y_AXIS));
        participantsPanel.setBorder(BorderFactory.createTitledBorder(PARTICIPANTS_TITLE));

        Map<String, Double> userExpenses = expenseService.getAllByBudgetId(budget.getId()).stream()
                .flatMap(expense -> expenseDivisionService.getAllByExpenseId(expense.getId()).stream())
                .collect(Collectors.groupingBy(expenseDivision -> expenseDivision.getUser().getName(), Collectors.summingDouble(ExpenseDivision::getAmount)));

        userExpenses.forEach((user, amount) -> {
            JLabel participantLabel = new JLabel(user + ": " + String.format("%.2f €", amount));
            participantsPanel.add(participantLabel);
        });

        // Painel de Tabs para Gráficos
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel expensesByUserPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab(EXPENSES_BY_USER_TITLE, expensesByUserPanel);
        tabbedPane.addTab(EXPENSES_BY_CATEGORY_TITLE, new JPanel(new BorderLayout()));
        tabbedPane.addTab(TRENDS_OVER_TIME_TITLE, new JPanel(new BorderLayout()));
        tabbedPane.addTab(CATEGORY_BREAKDOWN_TITLE, new JPanel(new BorderLayout()));
        tabbedPane.addTab(CUMULATIVE_TRENDS_TITLE, new JPanel(new BorderLayout()));

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(selectedIndex);
            JPanel selectedPanel = (JPanel) tabbedPane.getComponentAt(selectedIndex);
            if (selectedPanel.getComponentCount() == 0) {
                switch (title) {
                    case EXPENSES_BY_USER_TITLE:
                        selectedPanel.add(new ChartPanel(createBarChart()), BorderLayout.CENTER);
                        break;
                    case EXPENSES_BY_CATEGORY_TITLE:
                        selectedPanel.add(new ChartPanel(createPieChart()), BorderLayout.CENTER);
                        break;
                    case TRENDS_OVER_TIME_TITLE:
                        selectedPanel.add(new ChartPanel(createLineChart()), BorderLayout.CENTER);
                        break;
                    case CATEGORY_BREAKDOWN_TITLE:
                        selectedPanel.add(new ChartPanel(createStackedBarChart()), BorderLayout.CENTER);
                        break;
                    case CUMULATIVE_TRENDS_TITLE:
                        selectedPanel.add(new ChartPanel(createAreaChart()), BorderLayout.CENTER);
                        break;
                }
                selectedPanel.revalidate();
            }
        });

        // Carregar o gráfico da primeira aba
        expensesByUserPanel.add(new ChartPanel(createBarChart()), BorderLayout.CENTER);
        expensesByUserPanel.revalidate();

        // ScrollPane para permitir rolagem
        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Painel Inferior para os botões
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Botão de Back (alinhado à esquerda)
        JButton backButton = new JButton(BACK_BUTTON_TEXT);
        backButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget)));
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.add(backButton);
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);

        // Botão de Generate PDF (alinhado à direita)
        JButton generatePdfButton = new JButton(GENERATE_PDF_BUTTON_TEXT);
        generatePdfButton.addActionListener(e -> generatePdf(userExpenses));
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.add(generatePdfButton);
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

        // Adiciona os componentes principais ao painel
        add(participantsPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private ChartUtilities ChartUtils;
    private static final String PARTICIPANTS_TITLE = "Participants";
    private static final String EXPENSES_BY_USER_TITLE = "Expenses by User";
    private static final String EXPENSES_BY_CATEGORY_TITLE = "Expenses by Category";
    private static final String TRENDS_OVER_TIME_TITLE = "Trends Over Time";
    private static final String CATEGORY_BREAKDOWN_TITLE = "Category Breakdown";
    private static final String CUMULATIVE_TRENDS_TITLE = "Cumulative Trends";
    private static final String BACK_BUTTON_TEXT = "Back";
    private static final String GENERATE_PDF_BUTTON_TEXT = "Generate PDF";
    private static final String SAVE_PDF_DIALOG_TITLE = "Save PDF";
    private static final String PDF_DEFAULT_NAME = "relatorio.pdf";
    private static final String PDF_TITLE = "Budget Statistics";
    private static final String PARTICIPANTS_AND_AMOUNTS_TITLE = "Participants and Amounts:";
    private static final String PDF_SUCCESS_MESSAGE = "PDF generated successfully: ";
    private static final String PDF_ERROR_MESSAGE = "Error generating PDF: ";

}