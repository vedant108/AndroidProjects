package com.vedantsuram.stockassistant;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class StockDownloader implements Runnable {

    private MainActivity mainActivity;
    private String symbolFromMain;

    private static final String DATA_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String TAIL_URL = "/quote?token=pk_65147df7c096426897ae3ebd0ac8fbdb";

    public StockDownloader(MainActivity mainActivity, String symbolFromMain) {
        this.mainActivity = mainActivity;
        this.symbolFromMain = symbolFromMain;
    }

    @Override
    public void run() {

        String URL = DATA_URL + symbolFromMain + TAIL_URL;
        Uri dataUri = Uri.parse(URL);
        URL url  = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(dataUri.toString());
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stock stock = parseJSON(sb.toString());

        mainActivity.runOnUiThread(() -> {
            mainActivity.AddStockFromStDownloader(stock);
        });
    }


    private Stock parseJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            String symbol = jsonObject.getString("symbol");
            String name = jsonObject.getString("companyName");
            double price = jsonObject.getDouble("latestPrice");
            double change = jsonObject.getDouble("change");
            double changePercent = jsonObject.getDouble("changePercent");
            return new Stock(symbol, name, price, change, changePercent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
