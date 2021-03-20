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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private final List<Stock> stockList = new ArrayList<>();  // Main content is here
    private final HashMap<String, String> symbolMap = new HashMap<String, String>();

    private RecyclerView recyclerView; // Layout's recyclerview
    private StocksAdapter mAdapter; // Data to recyclerview adapter

    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.stockRecycler);
        mAdapter = new StocksAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StocksDecoration(20));

        databaseHandler = new DatabaseHandler(this);
        ArrayList<String[]> saveStocks = databaseHandler.loadStocks();

        doNameDownload();

        for (int i = 0; i < saveStocks.size(); i++) {
            doStockDownload(saveStocks.get(i)[0], true);
        }
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
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
                    String inputText = input.getText().toString();
                    List<String> matchItems = new ArrayList<String>();
                    for(HashMap.Entry item  :  symbolMap.entrySet()) {
                        String k = item.getKey().toString();
                        String v = item.getValue().toString();
                        if (k.contains(inputText)) {
                            matchItems.add(k + "-" + v);
                        }
                    }

                    if (matchItems.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Symbol Not Found: " + inputText);
                        builder.setMessage("Data for stock symbol");
                        AlertDialog err_dialog = builder.create();
                        err_dialog.show();
                    } else if (matchItems.size() == 1) {
                        String str = matchItems.get(0);
                        String[] arrOfStr = str.split("-", 2);
                        doStockDownload(arrOfStr[0], false);
                    } else {
                        final CharSequence[] sArray = matchItems.toArray(new CharSequence[matchItems.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Make a selection");
                        builder.setItems(sArray, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String[] arrOfStr = sArray[which].toString().split("-", 2);
                                doStockDownload(arrOfStr[0], false);
                            }
                        });
                        AlertDialog select_dialog = builder.create();
                        select_dialog.show();
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
                deleteStock(pos);
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

    private void doStockDownload(String symbol, Boolean init) {
        StockDownloadRunnable loaderTaskRunnable = new StockDownloadRunnable(this, symbol, init);
        new Thread(loaderTaskRunnable).start();
    }

    public void initStock(Stock s) {
        stockList.add(s);
        stockList.sort(Comparator.comparing(Stock::getSymbol));
        mAdapter.notifyDataSetChanged();
    }

    public void addStock(Stock s) {
        if (!stockList.contains(s)) {
            stockList.add(s);
            stockList.sort(Comparator.comparing(Stock::getSymbol));
            databaseHandler.addStock(s);
            mAdapter.notifyDataSetChanged();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock Symbol " + s.getSymbol() + " is already displayed");
            AlertDialog err_dialog = builder.create();
            err_dialog.show();
        }
    }

    public void deleteStock(int pos) {
        Stock s = stockList.get(pos);
        databaseHandler.deleteStock(s.getSymbol());
        stockList.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    public void updateSymbolList(HashMap<String, String> map) {
        symbolMap.putAll(map);
    }
}