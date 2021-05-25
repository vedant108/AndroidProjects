package com.vedantsuram.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Notes_ViewHolder extends RecyclerView.ViewHolder {


    TextView title;
    TextView date;
    TextView content;
    public Notes_ViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.titile_notes);
        date = itemView.findViewById(R.id.date_view);
        content = itemView.findViewById(R.id.content_view);
    }
}
