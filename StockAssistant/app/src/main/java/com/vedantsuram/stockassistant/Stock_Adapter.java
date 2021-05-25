package com.vedantsuram.stockassistant;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import static androidx.recyclerview.widget.RecyclerView.*;

public class Stock_Adapter extends RecyclerView.Adapter<Stock_ViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Stock> stockArrayList;


    public Stock_Adapter(MainActivity mainActivity, ArrayList<Stock> stockArrayList) {
        this.mainActivity = mainActivity;
        this.stockArrayList = stockArrayList;
    }

    @NonNull
    @Override
    public Stock_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_stock__view_holder, parent ,false);

        itemView.setOnLongClickListener(mainActivity);
        itemView.setOnClickListener(mainActivity);
        return new Stock_ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Stock_ViewHolder holder, int position) {
        Stock stock = stockArrayList.get(position);

        String arrow;


        if(stock.getChangePercent()>= 0.0){
            arrow ="▲ ";
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.changevalue.setTextColor(Color.GREEN);
            holder.changepercent.setTextColor(Color.GREEN);
        }else{
            arrow = "▼ ";
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.changevalue.setTextColor(Color.RED);
            holder.changepercent.setTextColor(Color.RED);
        }


        holder.symbol.setText(stock.getStockSymbol());
        holder.name.setText(stock.getCompanyName());
        holder.price.setText(stock.getPrice() + "");
        holder.changevalue.setText(arrow + String.format(Locale.US, "%.2f", stock.getPriceChange()) + "");
        holder.changepercent.setText("(" + String.format(Locale.US, "%.2f%%", stock.getChangePercent()*100)+ ")") ;
    }

    @Override
    public int getItemCount() {
        return stockArrayList.size();
    }
}
