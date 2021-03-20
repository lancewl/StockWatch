package com.example.stockwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class StockDownloadRunnable implements Runnable {

    private static final String TAG = "StockDownloadRunnable";

    private final MainActivity mainActivity;
    private final String symbol;

    private static final String stockURL = "https://cloud.iexapis.com/stable/stock/";
    private static final String yourAPIKey = "pk_27e8a19db093496db1398b7a363d0b00";

    StockDownloadRunnable(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
    }


    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(stockURL).buildUpon();

        buildURL.appendPath(symbol);
        buildURL.appendPath("quote");
        buildURL.appendQueryParameter("token", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
        final Stock s = parseJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.updateStock(s));
    }

    private Stock parseJSON(String s) {
        try {
            JSONObject stock = new JSONObject(s);
            String symbol = stock.getString("symbol");
            String company = stock.getString("companyName");
            Double price = stock.getDouble("latestPrice");
            Double change  = stock.getDouble("change");
            Double changePercent  = stock.getDouble("changePercent");

            return new Stock(symbol, company, price, change, changePercent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
