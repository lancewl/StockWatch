package com.example.stockwatch;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Stock implements Serializable {

    private String symbol;
    private String company;
    private String price;
    private String change;

    Stock(String symbol, String company, String price, String change) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompany() { return company; }

    public String getPrice() {
        return price;
    }

    public String getChange() {
        return change;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setChange(String change) {
        this.change = change;
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
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
