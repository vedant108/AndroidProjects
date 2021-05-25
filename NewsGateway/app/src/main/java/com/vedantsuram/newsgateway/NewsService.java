package com.vedantsuram.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class NewsService extends Service {

    private ArrayList<NewsStoryObject> storylist;
    private ServiceReceiver serviceReceiver;
    private boolean running = true;
    private NewsService newsService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public NewsService() {
        newsService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        storylist = new ArrayList<NewsStoryObject>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceReceiver = new ServiceReceiver();
                registerReceiver(serviceReceiver, new IntentFilter(MainActivity.REQUEST_STORIES));

                while (running){
                    if (storylist.size() == 0 || storylist.size() != storylist.get(0).getTotal()){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent responseIntent = new Intent();
                        responseIntent.setAction(MainActivity.RESPONSE_STORIES);
                        responseIntent.putExtra("articles", storylist);
                        sendBroadcast(responseIntent);
                        storylist.clear();
                    }
                }
            }
        }).start();
        return START_STICKY;
    }

    public void setArticles(NewsStoryObject newsStoryObject){
        storylist.add(newsStoryObject);
    }


    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NewsArticleDownloader newsArticleDownloader = new NewsArticleDownloader(newsService, intent.getStringExtra("source"));
            new Thread(newsArticleDownloader).start();
        }
    }




}
