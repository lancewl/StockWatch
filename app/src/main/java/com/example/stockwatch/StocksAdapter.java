package com.example.stockwatch;

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

        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());
        holder.price.setText(stock.getPrice());
        holder.change.setText(stock.getChange() + stock.getChangePercent());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
