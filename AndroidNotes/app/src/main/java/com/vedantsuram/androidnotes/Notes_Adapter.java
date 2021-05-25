package com.vedantsuram.androidnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notes_Adapter extends RecyclerView.Adapter<Notes_ViewHolder> {

    private ArrayList<Notes> notesList;
    private MainActivity mainActivity;

    public Notes_Adapter(ArrayList<Notes> notesList, MainActivity mainActivity) {
        this.notesList = notesList;
        this.mainActivity = mainActivity;
    }



    @NonNull
    @Override
    public Notes_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_holder, parent ,false);

        itemView.setOnLongClickListener(mainActivity);
        return new Notes_ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Notes_ViewHolder holder, int position) {
            Notes n = notesList.get(position);
            holder.title.setText(n.getTitle());
            holder.date.setText(n.getDate());
            if(n.getContent().length() > 80){
                String eighty = n.getContent().substring(0, 79);
                eighty = eighty.concat("...");
                holder.content.setText(eighty);
            }
            else {
                holder.content.setText(n.getContent());
            }


    }

    @Override
    public int getItemCount() {
        mainActivity.getSupportActionBar().setTitle("Android Notes " + "(" +notesList.size() + ")");
        return notesList.size();
    }


}
