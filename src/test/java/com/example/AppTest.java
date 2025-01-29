package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private DatabaseHandler dbHandler;

    @BeforeEach
    void setUp() {
        dbHandler = new DatabaseHandler();
        dbHandler.initializeDatabase();
        dbHandler.clearDatabase(); // Clear existing data before each test

        // Add sample data with valid months (1-12), years, and products
        dbHandler.saveSalesData(new SalesRecord("Gel", 5, 15.0, "Mohamed", "Ariana", "Male", "Prevention", 1, 2025));
        dbHandler.saveSalesData(new SalesRecord("Cream", 10, 35.0, "Salim", "Béja", "Male", "Treatment", 1, 2025));
        dbHandler.saveSalesData(new SalesRecord("Cream", 8, 25.0, "Malika", "Tunis", "Female", "Prevention", 2, 2025));
    }

    @Test
    void testRetrieveSalesData() {
        List<SalesRecord> records = dbHandler.retrieveSalesData();
        assertEquals(3, records.size(), "Expected 3 records in the database");

        // Verify first record (Gel)
        SalesRecord firstRecord = records.get(0);
        assertEquals("Gel", firstRecord.getProductName());
        assertEquals(5, firstRecord.getQuantitySold());
        assertEquals(15.0, firstRecord.getPricePerUnit());
        assertEquals(75.0, firstRecord.getRevenue());
        assertEquals("Mohamed", firstRecord.getBuyerName());
        assertEquals("Ariana", firstRecord.getRegion());
        assertEquals("Male", firstRecord.getGender());
        assertEquals("Prevention", firstRecord.getProductType());
        assertEquals(1, firstRecord.getSaleMonth());
        assertEquals(2025, firstRecord.getSaleYear());
    }

    @Test
    void testCalculateTotalRevenue() {
        double totalRevenue = dbHandler.calculateTotalRevenue();
        assertEquals(625.0, totalRevenue, 0.01, "Total revenue calculation failed");
    }


    @Test
    void testCalculateRevenueByProduct() {
        double gelRevenue = dbHandler.calculateRevenueByProduct("Gel");
        assertEquals(75.0, gelRevenue, 0.01, "Revenue for Gel is incorrect");

        double creamRevenue = dbHandler.calculateRevenueByProduct("Cream");
        assertEquals(550.0, creamRevenue, 0.01, "Revenue for Cream is incorrect");
    }

    @Test
    void testCalculateRevenueByRegion() {
        double arianaRevenue = dbHandler.calculateRevenueByRegion("Ariana");
        assertEquals(75.0, arianaRevenue, 0.01, "Revenue for Ariana is incorrect");

        double bejaRevenue = dbHandler.calculateRevenueByRegion("Béja");
        assertEquals(350.0, bejaRevenue, 0.01, "Revenue for Béja is incorrect");

        double tunisRevenue = dbHandler.calculateRevenueByRegion("Tunis");
        assertEquals(200.0, tunisRevenue, 0.01, "Revenue for Tunis is incorrect");
    }

    @Test
    void testForecastRevenueByProduct() {
        double gelForecast = dbHandler.forecastRevenueByProduct("Gel", 3, 2025);
        assertTrue(gelForecast > 0, "Forecasted revenue for Gel should be greater than 0");

        double creamForecast = dbHandler.forecastRevenueByProduct("Cream", 3, 2025);
        assertTrue(creamForecast > 0, "Forecasted revenue for Cream should be greater than 0");
    }

    @Test
    void testForecastRevenueWithNoData() {
        double noDataForecast = dbHandler.forecastRevenueByProduct("NonExistentProduct", 3, 2025);
        assertEquals(0, noDataForecast, "Forecasted revenue for a non-existent product should be 0");
    }

    @Test
    void testClearDatabase() {
        dbHandler.clearDatabase();
        List<SalesRecord> records = dbHandler.retrieveSalesData();
        assertTrue(records.isEmpty(), "Database should be empty after clearing");
    }

    @Test
    void testVisualizationDataIntegrity() {
        // Ensure data consistency for visualization (Total Revenue Over Time)
        Map<String, Double> totalRevenueOverTime = dbHandler.getTotalRevenueOverTime();
        assertNotNull(totalRevenueOverTime, "Total revenue data should not be null");
        assertTrue(totalRevenueOverTime.size() > 0, "Total revenue data should contain entries");

        // Ensure data consistency for Revenue by Products
        Map<String, Map<String, Double>> revenueByProducts = dbHandler.getRevenueByProductOverTime();
        assertNotNull(revenueByProducts, "Revenue by product data should not be null");
        assertTrue(revenueByProducts.containsKey("Gel"), "Revenue data for 'Gel' should exist");

        // Ensure data consistency for Revenue by Regions
        Map<String, Map<String, Double>> revenueByRegions = dbHandler.getRevenueByRegionOverTime();
        assertNotNull(revenueByRegions, "Revenue by region data should not be null");
        assertTrue(revenueByRegions.containsKey("Ariana"), "Revenue data for 'Ariana' should exist");
    }

    @Test
    void testGetSalesByProduct() {
        Map<String, Double> salesByProduct = dbHandler.getSalesByProduct();
        assertNotNull(salesByProduct, "Sales by product data should not be null");
        assertEquals(75.0, salesByProduct.get("Gel"), 0.01, "Sales revenue for Gel is incorrect");
    }

    @Test
    void testGetSalesByRegion() {
        Map<String, Double> salesByRegion = dbHandler.getSalesByRegion();
        assertNotNull(salesByRegion, "Sales by region data should not be null");
        assertEquals(75.0, salesByRegion.get("Ariana"), 0.01, "Sales revenue for Ariana is incorrect");
    }
}
