package com.vedantsuram.stockassistant;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.view.View;
import android.widget.TextView;

public class Stock_ViewHolder extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView name;
    TextView price;
    TextView changepercent;
    TextView changevalue;

    public Stock_ViewHolder(@NonNull View itemView) {
        super(itemView);

        symbol = itemView.findViewById(R.id.symbolview);
        name   = itemView.findViewById(R.id.companynameview);
        changevalue  = itemView.findViewById(R.id.changeprice);
        price  = itemView.findViewById(R.id.priceview);
        changepercent = itemView.findViewById(R.id.changepercentview);
    }

}