package com.example;

public class SalesRecord {
    private String productName;
    private int quantitySold;
    private double pricePerUnit;
    private String buyerName;
    private String region;
    private String gender;
    private String productType;
    private int saleMonth;
    private int saleYear;
    private double revenue;

    // Constructor
    public SalesRecord(String productName, int quantitySold, double pricePerUnit, String buyerName,
                       String region, String gender, String productType, int saleMonth, int saleYear) {
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.revenue = quantitySold * pricePerUnit; // Automatically calculates revenue
        this.buyerName = buyerName;
        this.region = region;
        this.gender = gender; // No need for validation as UI enforces valid input
        this.productType = productType; // No need for validation as UI enforces valid input
        validateSaleDate(saleMonth, saleYear); // Validate sale date
        this.saleMonth = saleMonth;
        this.saleYear = saleYear;
    }

    // Validate sale month and year
    private void validateSaleDate(int saleMonth, int saleYear) {
        if (saleMonth < 1 || saleMonth > 12) {
            throw new IllegalArgumentException("Invalid sale month! Must be between 1 and 12.");
        }
        if (saleYear < 2000 || saleYear > 2100) {
            throw new IllegalArgumentException("Invalid sale year! Must be between 2000 and 2100.");
        }
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
        updateRevenue(); // Recalculate revenue when quantity changes
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        updateRevenue(); // Recalculate revenue when price changes
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getSaleMonth() {
        return saleMonth;
    }

    public void setSaleMonth(int saleMonth) {
        validateSaleDate(saleMonth, this.saleYear);
        this.saleMonth = saleMonth;
    }

    public int getSaleYear() {
        return saleYear;
    }

    public void setSaleYear(int saleYear) {
        validateSaleDate(this.saleMonth, saleYear);
        this.saleYear = saleYear;
    }

    public double getRevenue() {
        return revenue;
    }

    public String getSaleDate() {
        return String.format("%02d/%d", saleMonth, saleYear);
    }

    public void updateRevenue() {
        this.revenue = calculateRevenue(); // Automatically updates revenue
    }

    // Calculate total revenue for the record
    public double calculateRevenue() {
        return quantitySold * pricePerUnit;
    }

    @Override
    public String toString() {
        return "Product: " + productName +
                ", Quantity Sold: " + quantitySold +
                ", Price: " + pricePerUnit +
                ", Revenue: " + revenue +
                ", Buyer: " + buyerName +
                ", Region: " + region +
                ", Gender: " + gender +
                ", Product Type: " + productType +
                ", Sale Date: " + getSaleDate();
    }
}
