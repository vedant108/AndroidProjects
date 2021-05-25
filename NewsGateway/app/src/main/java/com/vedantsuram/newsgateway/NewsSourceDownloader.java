package com.vedantsuram.newsgateway;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsSourceDownloader implements Runnable {
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;


    public NewsSourceDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private ArrayList<NewsSourceObject> parseJSON(String s) {
        ArrayList<NewsSourceObject> countryList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrSources = jObjMain.getJSONArray("sources");

            for (int i =0; i<jArrSources.length(); i++){
                JSONObject jObjSource = jArrSources.getJSONObject(i);
                String id = jObjSource.getString("id");
                String name = jObjSource.getString("name");
                String url = jObjSource.getString("url");
                String category = jObjSource.getString("category");
                countryList.add(new NewsSourceObject(id,name,url,category));
            }
            return countryList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleResults(String s){
        final ArrayList<NewsSourceObject> newsSourceObjects = parseJSON(s);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(newsSourceObjects != null){
                    mainActivity.setSources(newsSourceObjects);
                }
            }
        });
    }

    @Override
    public void run() {

        StringBuilder sb = new StringBuilder();

        try {
            String key = "&apiKey=42dbe0ea4eb0467e8dedb033eb461acf";
            String prefix = "https://newsapi.org/v2/sources?language=en&country=us&category=";
            URL url = new URL(prefix + key);


            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");



        } catch (IOException e) {
            e.printStackTrace();
        }
        handleResults(sb.toString());

    }

}
