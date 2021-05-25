package com.vedantsuram.stockassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private Stock_Adapter stockAdapter;
    private final ArrayList<Stock> StockdataList = new ArrayList<>();
    public HashMap<String, String> symbols;
    private Databasehandler databasehandler;
    private SwipeRefreshLayout swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        recyclerView = findViewById(R.id.recycle);
        stockAdapter = new Stock_Adapter(this , StockdataList);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databasehandler = new Databasehandler(this);
        swiper = findViewById(R.id.swipe);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(doNetCheck()) LoadOnRefresh();
                else  {
                    swiper.setRefreshing(false);
                    CreateDialog("", "Add/Refresh");
                }
            }
        });


        NameDownloader nd = new NameDownloader(this);
        new Thread(nd).start();

        ArrayList<Stock> stockArrayList = databasehandler.loadStockList();
        if(doNetCheck()) {
            for (int i = 0; i < stockArrayList.size(); i++) {
                String symbol = stockArrayList.get(i).getStockSymbol();
                StockDownloader stockDownloader = new StockDownloader(this, symbol);
                new Thread(stockDownloader).start();
            }
        }else{
            StockdataList.addAll(stockArrayList);
            Comparator<Stock> CompareBySymbol =  (Stock StockOne, Stock StockTwo) ->  StockOne.getStockSymbol().compareTo(StockTwo.getStockSymbol()) ;
            Collections.sort(StockdataList , CompareBySymbol);
            stockAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        stockAdapter.notifyDataSetChanged();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addnew){
            if (doNetCheck()) ActionAddOption();
            else CreateDialog("", "Add/Refresh");
        }
        return super.onOptionsItemSelected(item);
    }

    public void getsymbols(HashMap<String, String> input ){
        this.symbols = input;
   }

    public void AddStockFromStDownloader(Stock stock){
        if(stock != null) {
            int idx = -1;
            String sym = stock.getStockSymbol();
            for(int i=0; i< StockdataList.size(); i++){
                if(StockdataList.get(i).getStockSymbol().equals(sym)) idx = i;
            }
            if (idx != -1) StockdataList.remove(idx);
            StockdataList.add(stock);
            Comparator<Stock> CompareBySymbol = (Stock StockOne, Stock StockTwo) -> StockOne.getStockSymbol().compareTo(StockTwo.getStockSymbol());
            Collections.sort(StockdataList, CompareBySymbol);
            stockAdapter.notifyDataSetChanged();
        }
    }

    public void LoadOnRefresh(){
        swiper.setRefreshing(false);
        ArrayList<Stock> stockArrayList = databasehandler.loadStockList();
        for(int i=0; i<stockArrayList.size(); i++){
            String symbol = stockArrayList.get(i).getStockSymbol();
            StockDownloader stockDownloader = new StockDownloader(this, symbol);
            new Thread(stockDownloader).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databasehandler.shutDown();
    }

    private boolean doNetCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void ActionAddOption(){
        if (symbols == null) {
            NameDownloader symbolNameDownload = new NameDownloader(this);
            new Thread(symbolNameDownload).start();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText StockSymbol = customLayout.findViewById(R.id.editstock);
        StockSymbol.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        builder.setView(customLayout);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                String symbol = StockSymbol.getText().toString();
                if(symbol.trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Input Empty.", Toast.LENGTH_LONG).show();
                }
                else if (doNetCheck()){
                    if(symbols.size() == 0){
                        NameDownloader nd = new NameDownloader(MainActivity.this);
                        new Thread(nd).start();
                    }
                    ArrayList<String> matches = Allmatches(symbol.trim());
                    if (!matches.isEmpty()) {
                        if (matches.size() == 1) {
                            if (IsDuplicate(matches.get(0)))
                                CreateDialog(symbol,"Duplicate");
                            else
                                AddingNew(matches.get(0));
                        } else
                            MultipleOptions(symbol, matches);
                    } else
                        CreateDialog(symbol, "NotFound");
                }
                else {
                    CreateDialog(symbol, "Add/Refresh");
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private ArrayList Allmatches(String str){
        ArrayList<String> ret = new ArrayList<>();

         for (String symbol : symbols.keySet()) {
                String name = symbols.get(symbol);
                if (symbol.toUpperCase().startsWith(str.toUpperCase()))
                    ret.add(symbol + " - " + name);
                else {
                    if (name == null) throw new AssertionError();
                    if (name.toUpperCase().startsWith(str.toUpperCase()))
                        ret.add(symbol + " - " + name);
                }
            }
        return ret;
    }

    private void CreateDialog(String symbol , String Action){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (Action) {
            case "Duplicate":
                builder.setTitle("Duplicate Stock");
                builder.setIcon(R.drawable.baseline_warning_18);
                builder.setMessage("Stock Symbol " + symbol + " is already displayed");
                break;
            case "NotFound":
                builder.setTitle("Symbol Not Found: " + symbol);
                builder.setMessage("Data for stock symbol");
                break;
            case "Start":
                builder.setMessage("Stocks App needs internet to get latest prices");
                break;
            case "Add/Refresh":
                builder.setTitle("No Network Connection");
                builder.setMessage("Stocks Cannot be Updated/Added Without A Network Connection");
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean IsDuplicate(String s) {
        String sym = s.split("-")[0].trim();
        for(Stock in : StockdataList){
            if(in.getStockSymbol().equals(sym)) return true;
        }
        return false;
    }

    private void AddingNew(String str) {
        String symbol = str.split("-")[0].trim();
        StockDownloader stockDataDownload = new StockDownloader(this, symbol);
        new Thread(stockDataDownload).start();
        databasehandler.AddnewDB(symbol, symbols.get(symbol));
    }


    private void MultipleOptions( String str, List<String> Options) {
        String[] Option = new String[Options.size()];
        for (int i = 0; i < Option.length; i++)  Option[i] = Options.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Selection");
        builder.setItems(Option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (IsDuplicate(Option[selected])) CreateDialog(str, "Duplicate");
                else AddingNew(Option[selected]);
            }
        });
        builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected){
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        String marketWatchURL =  "http://www.marketwatch.com/investing/stock/" + StockdataList.get(position).getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketWatchURL));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        TextView symbol = view.findViewById(R.id.symbolview);
        String text = symbol.getText().toString().trim();
        final int position = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_action_name);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + text + "?");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                databasehandler.deleteStock(StockdataList.get(position).getStockSymbol());
                StockdataList.remove(position);
                stockAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

}