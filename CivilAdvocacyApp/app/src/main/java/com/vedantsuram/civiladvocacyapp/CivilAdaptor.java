package com.vedantsuram.civiladvocacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CivilAdaptor extends RecyclerView.Adapter<CivilViewHolder> {
    MainActivity mainActivity;
    private List<Official> officialList;

    public CivilAdaptor(MainActivity mainActivity, List<Official> officialList) {
        this.mainActivity = mainActivity;
        this.officialList = officialList;
    }

    @NonNull
    @Override
    public CivilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.civil_holder, parent, false);
        view.setOnClickListener((View.OnClickListener) mainActivity);
        return new CivilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CivilViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.CivilChair.setText(official.getOffice());
        holder.CivilChairName.setText(official.getName());
        holder.party.setText("(" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
