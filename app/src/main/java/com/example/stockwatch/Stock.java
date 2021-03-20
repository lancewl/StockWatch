package com.example.stockwatch;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Stock implements Serializable {

    private String symbol;
    private String company;
    private Double price;
    private Double change;
    private Double changePercent;

    Stock(String symbol, String company, Double price, Double change, Double changePercent) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompany() { return company; }

    public Double getPrice() {
        return price;
    }

    public Double getChange() {
        return change;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    @NonNull
    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("symbol").value(getSymbol());
            jsonWriter.name("company").value(getCompany());
            jsonWriter.name("price").value(getPrice());
            jsonWriter.name("change").value(getChange());
            jsonWriter.name("changePercent").value(getChangePercent());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
