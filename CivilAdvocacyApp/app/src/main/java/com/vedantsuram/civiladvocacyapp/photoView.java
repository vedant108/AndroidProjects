package com.vedantsuram.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class photoView extends AppCompatActivity {
    private ImageView Image;
    private ImageView party;
    private TextView name;
    private TextView location;
    private TextView post;
    private String loc;
    private Official official;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Image = findViewById(R.id.Image);
        party = findViewById(R.id.logo);
        name = findViewById(R.id.Name);
        location = findViewById(R.id.location);
        post = findViewById(R.id.post);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            loc = intent.getStringExtra("Location");
            location.setText(loc);
        } else {
            location.setText("");
        }
        if (intent.hasExtra("Official")) {
            official = (Official) intent.getSerializableExtra("Official");
            if (official != null) {
                ViewManager(official);
            }
        }
    }


    public void ViewManager(Official official) {

        if (official.getOffice().isEmpty()) {
            post.setText("");
        } else {
            post.setText(official.getOffice());
        }

        if (official.getName().isEmpty()) {
            name.setText("");
        } else {
            name.setText(official.getName());
        }

        if (official.getParty().isEmpty()) {
            findViewById(R.id.photoview).setBackgroundColor(Color.WHITE);
        } else {
            if (official.getParty().equals("Republican Party")) {
                findViewById(R.id.photoview).setBackgroundColor(Color.RED);
                party.setImageResource(R.drawable.rep_logo);
            } else if (official.getParty().equals("Democratic Party")) {
                findViewById(R.id.photoview).setBackgroundColor(Color.BLUE);
                party.setImageResource(R.drawable.dem_logo);
            } else {
                findViewById(R.id.photoview).setBackgroundColor(Color.BLACK);
                party.setVisibility(View.INVISIBLE);
            }
        }

        DownloadNow(official.getPhoto());
    }


    public void DownloadNow(final String photoUrl) {

        if (photoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = photoUrl.replace("http:", "https:");

                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(Image);

                }
            }).build();

            picasso.load(photoUrl)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(Image);

        }
    }

}