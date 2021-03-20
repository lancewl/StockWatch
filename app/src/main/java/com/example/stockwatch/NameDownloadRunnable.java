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
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class NameDownloadRunnable implements Runnable {

    private static final String TAG = "NameDownloadRunnable";

    private final MainActivity mainActivity;

    private static final String nameURL = "https://api.iextrading.com/1.0/ref-data/symbols";

    NameDownloadRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(nameURL).buildUpon();

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
        final HashMap<String, String> map = parseJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.updateData(map));
    }

    private HashMap<String, String> parseJSON(String s) {
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            JSONArray stockList = new JSONArray(s);

            for(int i=0; i<stockList.length(); i++) {
                JSONObject stockObject = stockList.getJSONObject(i);

                String symbol = stockObject.getString("symbol");
                String name = stockObject.getString("name");

                map.put(symbol, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
