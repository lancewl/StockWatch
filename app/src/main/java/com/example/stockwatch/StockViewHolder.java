package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder  extends RecyclerView.ViewHolder {
    public TextView symbol;
    TextView company;
    TextView price;
    TextView change;

    StockViewHolder(View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        company = view.findViewById(R.id.company);
        price = view.findViewById(R.id.price);
        change = view.findViewById(R.id.change);
    }
}
