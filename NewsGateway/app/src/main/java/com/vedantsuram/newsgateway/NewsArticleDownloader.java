package com.vedantsuram.newsgateway;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsArticleDownloader implements  Runnable {
    NewsService newsService;
    String source;

    NewsArticleDownloader(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }
    private void parseJson(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrSources = jObjMain.getJSONArray("articles");
            for (int i =0; i<jArrSources.length(); i++){
                JSONObject jObjSource = jArrSources.getJSONObject(i);
                String author = jObjSource.getString("author");
                String title = jObjSource.getString("title");
                String description = jObjSource.getString("description");
                String url = jObjSource.getString("url");
                String urlToImage = jObjSource.getString("urlToImage");
                String publishedAt = jObjSource.getString("publishedAt");

                newsService.setArticles(new NewsStoryObject(author, title, description, url, urlToImage, publishedAt, jArrSources.length(),i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleResults(String s){
        parseJson(s);
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String prefix = "https://newsapi.org/v2/everything?sources=";
            String apikey = "&language=en&pageSize=100&apiKey=42dbe0ea4eb0467e8dedb033eb461acf";
            URL url = new URL(prefix +source+ apikey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) stringBuilder.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleResults(stringBuilder.toString());
    }

}
