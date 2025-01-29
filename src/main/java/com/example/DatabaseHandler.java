package com.example;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:sales.db";

    // Initialize the database and create the "sales" table if it doesn't exist
    public void initializeDatabase() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS sales (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price_per_unit REAL NOT NULL,
                    revenue REAL NOT NULL,
                    buyer_name TEXT,
                    region TEXT,
                    gender TEXT CHECK (gender IN ('Male', 'Female', 'Other')),
                    product_type TEXT CHECK (product_type IN ('Prevention', 'Treatment')),
                    sale_month INTEGER NOT NULL,
                    sale_year INTEGER NOT NULL
                );
                """;
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // Save a sales record to the database
    public void saveSalesData(SalesRecord record) {
        String insertQuery = """
                INSERT INTO sales (product_name, quantity, price_per_unit, revenue, buyer_name, region, gender, product_type, sale_month, sale_year)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, record.getProductName());
            preparedStatement.setInt(2, record.getQuantitySold());
            preparedStatement.setDouble(3, record.getPricePerUnit());
            preparedStatement.setDouble(4, record.getRevenue());
            preparedStatement.setString(5, record.getBuyerName());
            preparedStatement.setString(6, record.getRegion());
            preparedStatement.setString(7, record.getGender());
            preparedStatement.setString(8, record.getProductType());
            preparedStatement.setInt(9, record.getSaleMonth());
            preparedStatement.setInt(10, record.getSaleYear());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Retrieve all sales records from the database
    public List<SalesRecord> retrieveSalesData() {
        List<SalesRecord> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM sales;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                SalesRecord record = new SalesRecord(
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price_per_unit"),
                        resultSet.getString("buyer_name"),
                        resultSet.getString("region"),
                        resultSet.getString("gender"),
                        resultSet.getString("product_type"),
                        resultSet.getInt("sale_month"),
                        resultSet.getInt("sale_year")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return records;
    }

    // Calculate total revenue for all sales records
    public double calculateTotalRevenue() {
        String query = "SELECT SUM(revenue) AS total_revenue FROM sales;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.getDouble("total_revenue");
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
        }
        return 0;
    }

    // Calculate total revenue for a specific product
    public double calculateRevenueByProduct(String productName) {
        String query = "SELECT SUM(revenue) AS product_revenue FROM sales WHERE product_name = ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("product_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating revenue by product: " + e.getMessage());
        }
        return 0;
    }

    // Calculate total revenue for a specific region
    public double calculateRevenueByRegion(String region) {
        String query = "SELECT SUM(revenue) AS region_revenue FROM sales WHERE region = ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, region);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("region_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating revenue by region: " + e.getMessage());
        }
        return 0;
    }

    // Forecast revenue for a specific product
    public double forecastRevenueByProduct(String productName, int futureMonth, int futureYear) {
        String query = "SELECT revenue, sale_month, sale_year FROM sales WHERE product_name = ? ORDER BY sale_year, sale_month;";
        List<Double> revenues = new ArrayList<>();
        List<Integer> months = new ArrayList<>();
        List<Integer> years = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                revenues.add(resultSet.getDouble("revenue"));
                months.add(resultSet.getInt("sale_month"));
                years.add(resultSet.getInt("sale_year"));
            }

            if (revenues.isEmpty()) {
                return 0; // No data for the product
            }

            double avgGrowthRate = calculateAverageGrowthRate(revenues);

            int currentIndex = (years.get(years.size() - 1) - 2024) * 12 + months.get(months.size() - 1);
            int futureIndex = (futureYear - 2024) * 12 + futureMonth;

            double forecastedRevenue = revenues.get(revenues.size() - 1);
            for (int i = 0; i < (futureIndex - currentIndex); i++) {
                forecastedRevenue *= (1 + avgGrowthRate);
            }

            return Math.max(forecastedRevenue, 0); // Ensure non-negative forecast

        } catch (SQLException e) {
            System.err.println("Error forecasting revenue by product: " + e.getMessage());
        }
        return 0;
    }

    // Helper function: Fetch data for single-key maps
    private Map<String, Double> fetchDataMap(String query) {
        Map<String, Double> dataMap = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                dataMap.put(resultSet.getString(1), resultSet.getDouble(2));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
        return dataMap;
    }

    // Helper function: Calculate average growth rate from revenue data
    private double calculateAverageGrowthRate(List<Double> revenues) {
        if (revenues.size() < 2) return 0;
        double totalGrowth = 0;
        for (int i = 1; i < revenues.size(); i++) {
            double growthRate = (revenues.get(i) - revenues.get(i - 1)) / revenues.get(i - 1);
            totalGrowth += growthRate;
        }
        return totalGrowth / (revenues.size() - 1);
    }

    // Clear all records from the database
    public void clearDatabase() {
        String deleteQuery = "DELETE FROM sales;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            System.err.println("Error clearing database: " + e.getMessage());
        }
    }

    // Get sales distribution by product
    public Map<String, Double> getSalesByProduct() {
        String query = "SELECT product_name, SUM(revenue) AS total_revenue FROM sales GROUP BY product_name;";
        return fetchDataMap(query);
    }

    // Get sales distribution by region
    public Map<String, Double> getSalesByRegion() {
        String query = "SELECT region, SUM(revenue) AS total_revenue FROM sales GROUP BY region;";
        return fetchDataMap(query);
    }

    // Get total revenue over time
    public Map<String, Double> getTotalRevenueOverTime() {
        String query = "SELECT sale_year || '-' || sale_month AS period, SUM(revenue) AS total FROM sales GROUP BY period ORDER BY sale_year, sale_month;";
        return fetchDataMap(query);
    }

    // Get revenue by region over time
    public Map<String, Map<String, Double>> getRevenueByRegionOverTime() {
        String query = "SELECT region, sale_year || '-' || sale_month AS period, SUM(revenue) AS total "
                     + "FROM sales GROUP BY region, period ORDER BY sale_year, sale_month;";
        return fetchMultiKeyDataMap(query);
    }

    // Forecast revenue data for visualization
    public List<Double> getForecastingData() {
        List<Double> forecastedData = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            forecastedData.add(Math.random() * 1000); // Replace with actual forecasting logic
        }
        return forecastedData;
    }

    // Get available product names from the database
    public List<String> getAvailableProducts() {
        List<String> products = new ArrayList<>();
        String query = "SELECT DISTINCT product_name FROM sales;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                products.add(resultSet.getString("product_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
        }
        return products;
    }

    // Helper function: Fetch multi-key data maps
    private Map<String, Map<String, Double>> fetchMultiKeyDataMap(String query) {
        Map<String, Map<String, Double>> dataMap = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String key1 = resultSet.getString(1);
                String key2 = resultSet.getString(2);
                double value = resultSet.getDouble(3);
                dataMap.putIfAbsent(key1, new LinkedHashMap<>());
                dataMap.get(key1).put(key2, value);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching multi-key data: " + e.getMessage());
        }
        return dataMap;
    }

    public Map<String, Map<String, Double>> getRevenueByProductOverTime() {
        String query = """
            SELECT product_name, 
                   sale_year || '-' || sale_month AS period, 
                   SUM(revenue) AS total_revenue
            FROM sales
            GROUP BY product_name, period
            ORDER BY product_name, sale_year, sale_month;
        """;
    
        return fetchMultiKeyDataMap(query);
    }
    
}
