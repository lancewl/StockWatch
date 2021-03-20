package com.example.stockwatch;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class StocksDecoration extends RecyclerView.ItemDecoration {

    private final int decorationHeight;

    public StocksDecoration(int height) {
        this.decorationHeight = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = decorationHeight;
    }
}
