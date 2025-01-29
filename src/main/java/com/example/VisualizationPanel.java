package com.example;

import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class VisualizationPanel {

    private final DatabaseHandler dbHandler;

    // Constructor accepting DatabaseHandler
    public VisualizationPanel(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    // Create the visualization panel (to be used in App.java)
    public Node getVisualizationPanel() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Buttons for refreshing graphs
        Button refreshButton = new Button("Refresh Charts");
        refreshButton.setOnAction(e -> refreshGraphs(root));

        root.getChildren().add(refreshButton);

        refreshGraphs(root);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    private void refreshGraphs(VBox root) {
        root.getChildren().clear();

        // Add a refresh button back to the panel
        Button refreshButton = new Button("Refresh Charts");
        refreshButton.setOnAction(e -> refreshGraphs(root));
        root.getChildren().add(refreshButton);

        // Add charts to the panel
        root.getChildren().addAll(
                createSwingNode(createTotalRevenueChart()),
                createSwingNode(createRevenueByProductChart()),
                createSwingNode(createRevenueByRegionChart()),
                createSwingNode(createForecastingChart()),
                createSwingNode(createSalesByProductChart()),
                createSwingNode(createSalesByRegionChart())
        );
    }

    private SwingNode createSwingNode(JFreeChart chart) {
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(900, 400));
            swingNode.setContent(chartPanel);
        });
        return swingNode;
    }

    /**
     * Chart 1: Total Revenue Over Time
     */
    private JFreeChart createTotalRevenueChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> revenueData = dbHandler.getTotalRevenueOverTime();

        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
        }

        return ChartFactory.createLineChart(
                "Total Revenue Over Time",
                "Time Period",
                "Revenue (TDN)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    /**
     * Chart 2: Revenue by Products Over Time
     */
    private JFreeChart createRevenueByProductChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Map<String, Double>> productRevenueData = dbHandler.getRevenueByProductOverTime();

        for (Map.Entry<String, Map<String, Double>> productEntry : productRevenueData.entrySet()) {
            String product = productEntry.getKey();
            for (Map.Entry<String, Double> timeEntry : productEntry.getValue().entrySet()) {
                dataset.addValue(timeEntry.getValue(), product, timeEntry.getKey());
            }
        }

        return ChartFactory.createLineChart(
                "Revenue by Products Over Time",
                "Time Period",
                "Revenue (TDN)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    /**
     * Chart 3: Revenue by Regions Over Time
     */
    private JFreeChart createRevenueByRegionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Map<String, Double>> regionRevenueData = dbHandler.getRevenueByRegionOverTime();

        for (Map.Entry<String, Map<String, Double>> regionEntry : regionRevenueData.entrySet()) {
            String region = regionEntry.getKey();
            for (Map.Entry<String, Double> timeEntry : regionEntry.getValue().entrySet()) {
                dataset.addValue(timeEntry.getValue(), region, timeEntry.getKey());
            }
        }

        return ChartFactory.createLineChart(
                "Revenue by Regions Over Time",
                "Time Period",
                "Revenue (TDN)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    /**
     * Chart 4: Forecasting Curve
     */
    private JFreeChart createForecastingChart() {
        XYSeries series = new XYSeries("Revenue Forecast");
        List<Double> forecastData = dbHandler.getForecastingData();

        for (int i = 0; i < forecastData.size(); i++) {
            series.add(i + 1, forecastData.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Revenue Forecasting Curve",
                "Future Periods",
                "Revenue (TDN)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        return chart;
    }

    /**
     * Chart 5: Sales Distribution by Product
     */
    private JFreeChart createSalesByProductChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> salesData = dbHandler.getSalesByProduct();

        for (Map.Entry<String, Double> entry : salesData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
                "Sales Distribution by Product",
                dataset,
                true,
                true,
                false
        );
    }

    /**
     * Chart 6: Sales Distribution by Region
     */
    private JFreeChart createSalesByRegionChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> salesData = dbHandler.getSalesByRegion();

        for (Map.Entry<String, Double> entry : salesData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
                "Sales Distribution by Region",
                dataset,
                true,
                true,
                false
        );
    }
}
