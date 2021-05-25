package com.vedantsuram.civiladvocacyapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CivilViewHolder  extends RecyclerView.ViewHolder {

    public TextView CivilChair;
    public TextView CivilChairName;
    public TextView party;


    public CivilViewHolder(@NonNull View itemView) {
        super(itemView);
        CivilChair = itemView.findViewById(R.id.chair);
        CivilChairName = itemView.findViewById(R.id.chairname);
        party = itemView.findViewById(R.id.party);
    }
}
