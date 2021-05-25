package com.vedantsuram.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private NewsReceiver newsReceiver;
    private ArrayList<NewsSourceObject> ListObj = new ArrayList<>();
    private HashMap<String, ArrayList<NewsSourceObject>> Data = new HashMap<>();
    private Menu menu;
    private ArrayList<NewsSourceObject> source = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView listView;
    private List<androidx.fragment.app.Fragment> fragments;
    private PageAdapter pageAdapter;
    private ViewPager viewPager;
    static final String REQUEST_STORIES = "Request_Stories";
    static final String RESPONSE_STORIES = "Response_Stories";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, new IntentFilter(RESPONSE_STORIES));
        registerReceiver(newsReceiver, new IntentFilter(REQUEST_STORIES));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NewsSourceDownloader newsSourceDownloader = new NewsSourceDownloader(MainActivity.this);
        new Thread(newsSourceDownloader).start();

        ArrayAdapter<NewsSourceObject> newsSourceArrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, ListObj);
        listView.setAdapter(newsSourceArrayAdapter);

//       CustomAdapter customAdapter = new CustomAdapter(this, R.layout.drawer_list_item, ListObj);
//       listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nowClickedSource(i);
            }
        });

        fragments = new ArrayList<androidx.fragment.app.Fragment>();
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);

        if (savedInstanceState != null){
            fragments = (List<androidx.fragment.app.Fragment>) savedInstanceState.getSerializable("fragments");
            setTitle(savedInstanceState.getString("title"));
            pageAdapter.notifyDataSetChanged();
            for (int i = 0; i< pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);
        }
    }

    private class PageAdapter extends FragmentPagerAdapter {

        private long baseId = 0;
        public PageAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId+position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }

    public void setSources(ArrayList<NewsSourceObject> Input) {

        for (NewsSourceObject newsSourceObject: Input) {

            if (!Data.containsKey(newsSourceObject.getCategory())) {
                Data.put(newsSourceObject.getCategory(), new ArrayList<NewsSourceObject>());
            }
            ArrayList<NewsSourceObject> dataList = Data.get(newsSourceObject.getCategory());
            if (dataList != null) {
                dataList.add(newsSourceObject);
            }
        }
        Data.put("All", Input);
        ArrayList<String> List = new ArrayList<>(Data.keySet());
        Collections.sort(List);
        for (String s  : List) {
            menu.add(s);
        }

        ListObj.addAll(Input);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, ListObj));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("fragments", (Serializable) fragments);
        outState.putString("title",getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        unregisterReceiver(newsReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu main_menu) {
        getMenuInflater().inflate(R.menu.menu_main, main_menu);
        menu = main_menu;
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        ListObj.clear();
        ArrayList<NewsSourceObject> datalist = Data.get(item.getTitle().toString());
        if (datalist != null) {
            ListObj.addAll(datalist);
        }

        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RESPONSE_STORIES.equals(intent.getAction())) {
                ArrayList<NewsStoryObject> newsStoryObjects = (ArrayList<NewsStoryObject>) intent.getSerializableExtra("articles");
                fragments.clear();
                assert newsStoryObjects != null;
                int x = newsStoryObjects.size() - 90;
                for (int i = 0; i < x; i++) {
                    fragments.add(Fragment.newInstance(newsStoryObjects.get(i)));
                    pageAdapter.notifyChangeInPosition(i);
                }
                pageAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0);
            }
        }
    }

    private void nowClickedSource(int position) {
        setTitle(ListObj.get(position).getName());
        Intent requestIntent = new Intent();
        requestIntent.setAction(MainActivity.REQUEST_STORIES);
        requestIntent.putExtra("source", ListObj.get(position).getId());
        sendBroadcast(requestIntent);
        drawerLayout.closeDrawer(listView);
    }

    @Override
    protected void onResume() {
        registerReceiver(newsReceiver, new IntentFilter(RESPONSE_STORIES));
        registerReceiver(newsReceiver, new IntentFilter(MainActivity.REQUEST_STORIES));
        super.onResume();
    }

}
