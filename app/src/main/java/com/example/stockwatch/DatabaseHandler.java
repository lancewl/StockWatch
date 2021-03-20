package com.example.stockwatch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockAssistantTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + "TEXT not null unique," +
                    COMPANY + "TEXT not null)";

    private final SQLiteDatabase database;
    private final MainActivity mainActivity;

    DatabaseHandler(MainActivity context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mainActivity = context;
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding " + stock.getSymbol());
        ContentValues values = new ContentValues(); values.put(SYMBOL, stock.getSymbol()); values.put(COMPANY, stock.getCompany());
        database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: Add Complete");
    }

    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(
                TABLE_NAME, "SYMBOL = ?", new String[] { symbol });
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public ArrayList<String[]> loadStocks() {
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{SYMBOL, COMPANY}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) { cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) { String symbol = cursor.getString(0); String company = cursor.getString(1); stocks.add(new String[]{symbol, company}); cursor.moveToNext();
            }
            cursor.close(); }
        return stocks;
    }

    void shutDown() {
        database.close();
    }
}
