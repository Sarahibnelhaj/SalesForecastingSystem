package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    private final DatabaseHandler dbHandler = new DatabaseHandler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the database (without clearing records)
        dbHandler.initializeDatabase();

        // Create a TabPane for clean UI
        TabPane tabPane = new TabPane();

        // Tabs for Data Entry and Visualization
        Tab dataEntryTab = new Tab("Data Entry", createDataEntryPanel());
        Tab visualizationTab = new Tab("Visualization", new VisualizationPanel(dbHandler).getVisualizationPanel());

        // Disable tab closing
        dataEntryTab.setClosable(false);
        visualizationTab.setClosable(false);

        // Add tabs to the TabPane
        tabPane.getTabs().addAll(dataEntryTab, visualizationTab);

        // Set up the primary stage
        primaryStage.setScene(new Scene(tabPane, 1000, 700));
        primaryStage.setTitle("Sales Forecasting System");
        primaryStage.show();
    }

    private VBox createDataEntryPanel() {
        // Product Name Dropdown
        ComboBox<String> productNameComboBox = new ComboBox<>();
        productNameComboBox.getItems().addAll(dbHandler.getAvailableProducts());
        productNameComboBox.setPromptText("Select Product Name");

        // Quantity and Price Fields
        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter Quantity Sold");

        TextField priceField = new TextField();
        priceField.setPromptText("Enter Price per Unit");

        // Buyer Name
        TextField buyerNameField = new TextField();
        buyerNameField.setPromptText("Enter Buyer Name");

        // Region Dropdown
        ComboBox<String> regionComboBox = new ComboBox<>();
        regionComboBox.getItems().addAll(
                "Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa",
                "Jendouba", "Kairouan", "Kasserine", "Kebili", "Le Kef", "Mahdia",
                "Manouba", "Médenine", "Monastir", "Nabeul", "Sfax", "Sidi Bou Zid",
                "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan"
        );
        regionComboBox.setPromptText("Select Region");

        // Gender Dropdown
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select Gender");

        // Product Type Dropdown
        ComboBox<String> productTypeComboBox = new ComboBox<>();
        productTypeComboBox.getItems().addAll("Prevention", "Treatment");
        productTypeComboBox.setPromptText("Select Product Type");

        // Sale Month & Year Fields
        TextField saleMonthField = new TextField();
        saleMonthField.setPromptText("Enter Sale Month (1-12)");

        TextField saleYearField = new TextField();
        saleYearField.setPromptText("Enter Sale Year (e.g., 2025)");

        // Buttons for Actions
        Button saveButton = new Button("Save Record");
        Button fetchButton = new Button("Fetch Records");
        Button totalRevenueButton = new Button("Calculate Total Revenue");
        Button productRevenueButton = new Button("Calculate Revenue for Product");
        Button regionRevenueButton = new Button("Calculate Revenue for Region");
        Button clearDatabaseButton = new Button("Clear Database");
        Button displayAllRecordsButton = new Button("Display All Records");

        // Button Actions
        saveButton.setOnAction(e -> {
            try {
                String productName = productNameComboBox.getValue();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                String buyerName = buyerNameField.getText();
                String region = regionComboBox.getValue();
                String gender = genderComboBox.getValue();
                String productType = productTypeComboBox.getValue();
                int saleMonth = Integer.parseInt(saleMonthField.getText());
                int saleYear = Integer.parseInt(saleYearField.getText());

                if (productName == null || region == null || gender == null || productType == null) {
                    showAlert(Alert.AlertType.ERROR, "Please fill all dropdown fields.");
                    return;
                }

                SalesRecord record = new SalesRecord(productName, quantity, price, buyerName, region, gender, productType, saleMonth, saleYear);
                dbHandler.saveSalesData(record);

                // Clear input fields
                productNameComboBox.setValue(null);
                quantityField.clear();
                priceField.clear();
                buyerNameField.clear();
                regionComboBox.setValue(null);
                genderComboBox.setValue(null);
                productTypeComboBox.setValue(null);
                saleMonthField.clear();
                saleYearField.clear();

                showAlert(Alert.AlertType.INFORMATION, "Record Saved Successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error Saving Record: " + ex.getMessage());
            }
        });

        fetchButton.setOnAction(e -> {
            List<SalesRecord> records = dbHandler.retrieveSalesData();
            showAlert(Alert.AlertType.INFORMATION, "Fetched " + records.size() + " record(s). Check console for details.");
            records.forEach(System.out::println);
        });

        totalRevenueButton.setOnAction(e -> {
            double totalRevenue = dbHandler.calculateTotalRevenue();
            showAlert(Alert.AlertType.INFORMATION, "Total Revenue: " + totalRevenue);
        });

        productRevenueButton.setOnAction(e -> {
            String productName = productNameComboBox.getValue();
            if (productName == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a product.");
                return;
            }
            double revenue = dbHandler.calculateRevenueByProduct(productName);
            showAlert(Alert.AlertType.INFORMATION, "Revenue for Product " + productName + ": " + revenue);
        });

        regionRevenueButton.setOnAction(e -> {
            String region = regionComboBox.getValue();
            if (region == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a region.");
                return;
            }
            double revenue = dbHandler.calculateRevenueByRegion(region);
            showAlert(Alert.AlertType.INFORMATION, "Revenue for Region " + region + ": " + revenue);
        });

        clearDatabaseButton.setOnAction(e -> {
            dbHandler.clearDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Database Cleared Successfully!");
        });

        displayAllRecordsButton.setOnAction(e -> {
            List<SalesRecord> records = dbHandler.retrieveSalesData();
            if (records.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No records found in the database.");
            } else {
                StringBuilder recordsDisplay = new StringBuilder("All Records:\n");
                for (SalesRecord record : records) {
                    recordsDisplay.append(record.toString()).append("\n");
                }
                showAlert(Alert.AlertType.INFORMATION, recordsDisplay.toString());
            }
        });

        // Layout Structure
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        GridPane formLayout = new GridPane();
        formLayout.setPadding(new Insets(10));
        formLayout.setHgap(10);
        formLayout.setVgap(5);
        formLayout.addRow(0, productNameComboBox, quantityField, priceField);
        formLayout.addRow(1, buyerNameField, regionComboBox, genderComboBox);
        formLayout.addRow(2, productTypeComboBox, saleMonthField, saleYearField);

        HBox buttonRow1 = new HBox(10, saveButton, fetchButton);
        HBox buttonRow2 = new HBox(10, totalRevenueButton, productRevenueButton, regionRevenueButton);
        HBox buttonRow3 = new HBox(10, clearDatabaseButton, displayAllRecordsButton);
        buttonRow1.setAlignment(Pos.CENTER);
        buttonRow2.setAlignment(Pos.CENTER);
        buttonRow3.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(formLayout, buttonRow1, buttonRow2, buttonRow3);

        return layout;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
