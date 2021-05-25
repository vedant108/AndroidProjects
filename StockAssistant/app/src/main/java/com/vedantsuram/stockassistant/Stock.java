package com.vedantsuram.stockassistant;

import java.io.Serializable;
import java.util.Objects;

public class Stock implements Serializable {

    String StockSymbol;
    String CompanyName;
    double Price;
    double PriceChange;
    double ChangePercent;

    public Stock(String stockSymbol, String companyName, double price, double priceChange, double changePercent) {
        StockSymbol = stockSymbol;
        CompanyName = companyName;
        Price = price;
        PriceChange = priceChange;
        ChangePercent = changePercent;
    }

    public Stock() {

    }

    public String getStockSymbol() {
        return StockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        StockSymbol = stockSymbol;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getPriceChange() {
        return PriceChange;
    }

    public void setPriceChange(double priceChange) {
        PriceChange = priceChange;
    }

    public double getChangePercent() {
        return ChangePercent;
    }

    public void setChangePercent(double changePercent) {
        ChangePercent = changePercent;
    }

}
