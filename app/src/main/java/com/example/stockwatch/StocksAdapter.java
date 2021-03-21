package com.example.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "EmployeesAdapter";
    private final List<Stock> stockList;
    private final MainActivity mainAct;

    StocksAdapter(List<Stock> sList, MainActivity ma) {
        this.stockList = sList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Employee " + position);

        Stock stock = stockList.get(position);

        String symbol = stock.getSymbol();
        String company = stock.getCompany();
        Double price = stock.getPrice();
        Double change = stock.getChange();
        Double changePercent = stock.getChangePercent();
        String sign = "";

        if (change >= 0) {
            sign = "▲ ";
            holder.symbol.setTextColor(Color.GREEN);
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
        } else {
            sign = "▼ ";
            holder.symbol.setTextColor(Color.RED);
            holder.company.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
        }


        holder.symbol.setText(symbol);
        holder.company.setText(company);
        holder.price.setText(price + "");
        holder.change.setText(sign + change + " (" + String.format("%.2f", changePercent) + "%)");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
