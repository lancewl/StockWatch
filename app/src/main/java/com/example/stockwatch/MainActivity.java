package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private final List<Stock> stockList = new ArrayList<>();  // Main content is here
    private final HashMap<String, String> symbolMap = new HashMap<String, String>();

    private RecyclerView recyclerView; // Layout's recyclerview
    private StocksAdapter mAdapter; // Data to recyclerview adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.stockRecycler);
        mAdapter = new StocksAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StocksDecoration(20));

        doNameDownload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addBtn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Stock Selection");
            builder.setMessage("Please enter a Stock Symbol:");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String symbol = input.getText().toString();
                    if (symbolMap.containsKey(symbol)) {
                        Stock s = new Stock(symbol, symbolMap.get(symbol), null, null);
                        stockList.add(s);
                        mAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // From OnClickListener
    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        // TODO: open web page
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + s.getSymbol() + "?");


        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    private void doNameDownload() {
        NameDownloadRunnable loaderTaskRunnable = new NameDownloadRunnable(this);
        new Thread(loaderTaskRunnable).start();
    }

    public void updateData(HashMap<String, String> map) {
        symbolMap.putAll(map);
    }
}