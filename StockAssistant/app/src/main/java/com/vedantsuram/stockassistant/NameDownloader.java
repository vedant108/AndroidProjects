package com.vedantsuram.stockassistant;

import android.net.Uri;

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
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class NameDownloader implements Runnable {

    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    public NameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);

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

        HashMap<String, String> symbols = parseJSON(sb.toString());

        mainActivity.runOnUiThread(() -> {
            mainActivity.getsymbols(symbols);
        });

    }


    private HashMap<String, String> parseJSON(String s){

        HashMap<String, String> making = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(s);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                making.put(jsonObject.getString("symbol") , jsonObject.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return making;
    }
}
