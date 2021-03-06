package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private final List<Stock> stockList = new ArrayList<>();  // Main content is here
    private final HashMap<String, String> symbolMap = new HashMap<String, String>();

    private RecyclerView recyclerView; // Layout's recyclerview
    private StocksAdapter mAdapter; // Data to recyclerview adapter
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    private DatabaseHandler databaseHandler;

    private static final String marketwatchURL = "http://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.stockRecycler);
        mAdapter = new StocksAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StocksDecoration(20));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        doNameDownload();

        databaseHandler = new DatabaseHandler(this);
        ArrayList<String[]> saveStocks = databaseHandler.loadStocks();

        if (checkNetwork()) {
            for (int i = 0; i < saveStocks.size(); i++) {
                doStockDownload(saveStocks.get(i)[0], true);
            }
        } else {
            for (int i = 0; i < saveStocks.size(); i++) {
                Stock s = new Stock(saveStocks.get(i)[0], saveStocks.get(i)[1], 0.0, 0.0, 0.0);
                initStock(s);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Loaded Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
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
            if (checkNetwork()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Stock Selection");
                builder.setMessage("Please enter a Stock Symbol:");
                final EditText input = new EditText(this);
                input.setGravity(Gravity.CENTER);
                input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String inputText = input.getText().toString();
                        List<String> matchItems = new ArrayList<String>();
                        for(HashMap.Entry item  :  symbolMap.entrySet()) {
                            String k = item.getKey().toString();
                            String v = item.getValue().toString();
                            if (k.contains(inputText) || v.contains(inputText)) {
                                matchItems.add(k + "-" + v);
                            }
                        }

                        matchItems.sort( Comparator.comparing( String::toString ) );

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
                            builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { }
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
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

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
        String symbol = s.getSymbol();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketwatchURL + symbol));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(android.R.drawable.ic_menu_delete);
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

    private Boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void doRefresh() {
        databaseHandler = new DatabaseHandler(this);
        ArrayList<String[]> saveStocks = databaseHandler.loadStocks();

        if (checkNetwork()) {
            stockList.clear();
            mAdapter.notifyDataSetChanged();
            for (int i = 0; i < saveStocks.size(); i++) {
                doStockDownload(saveStocks.get(i)[0], true);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        swiper.setRefreshing(false);
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
            builder.setIcon(android.R.drawable.ic_dialog_alert);
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