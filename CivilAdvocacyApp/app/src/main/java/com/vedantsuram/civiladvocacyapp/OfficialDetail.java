package com.vedantsuram.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class OfficialDetail extends AppCompatActivity {
    private Official official;
    private TextView name;
    private TextView office;
    private TextView party;
    private TextView phone;
    private TextView email;
    private TextView website;
    private TextView address;
    private TextView location;
    private TextView tx1;
    private TextView tx2;
    private TextView tx3;
    private TextView tx4;
    private ImageView Image;
    private ImageView PartyImg;
    private ImageView fb;
    private ImageView twitter;
    private ImageView youtube;
    String locText;
    String twit;
    String face;
    String yout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_detail);

        name= findViewById(R.id.postName);
        office = findViewById(R.id.post);
        party = findViewById(R.id.party);
        location = findViewById(R.id.Location);
        website = findViewById(R.id.website);
        email  = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        tx1 = findViewById(R.id.tx1);
        tx2 = findViewById(R.id.tx2);
        tx3 = findViewById(R.id.tx3);
        tx4 = findViewById(R.id.tx4);
        address = findViewById(R.id.address);
        Image = findViewById(R.id.imageperson);
        PartyImg = findViewById(R.id.partylogo);
        fb = findViewById(R.id.fb);
        twitter = findViewById(R.id.twit);
        youtube = findViewById(R.id.youtube);

        Intent intent = getIntent();

        if (intent.hasExtra("Location")) {
            locText = intent.getStringExtra("Location");
            location.setText(locText);
        } else {
            location.setText("");
        }
        if (intent.hasExtra("Officail")) {
            official = (Official) intent.getSerializableExtra("Officail");
            if (official != null) {
                ViewManager(official);
            } else {
                Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to receive data.", Toast.LENGTH_SHORT).show();
        }
    }


    public void ViewManager(Official official){
        office.setText(official.getOffice());
        name.setText(official.getName());


        if (official.getParty().equals("Republican Party") ) {
            String name = "(" + official.getParty() + ")";
            party.setText(name);
            findViewById(R.id.detail_layout).setBackgroundColor(Color.RED);
            PartyImg.setImageResource(R.drawable.rep_logo);
        } else if (official.getParty().equals("Democratic Party") ) {
            String name = "(" + official.getParty() + ")";
            party.setText(name);
            findViewById(R.id.detail_layout).setBackgroundColor(Color.BLUE);
            PartyImg.setImageResource(R.drawable.dem_logo);
        } else if (official.getParty().equals("Nonpartisan")) {
            findViewById(R.id.detail_layout).setBackgroundColor(Color.BLACK);
            party.setVisibility(View.INVISIBLE);
            PartyImg.setVisibility(View.INVISIBLE);
        }

        if(official.getAddress().equals("")) {
            address.setVisibility(View.GONE);
            tx1.setVisibility(View.GONE);
        }else {
            address.setText(official.getAddress());
            Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        }

        if(official.getEmail().equals("")) {
            email.setVisibility(View.GONE);
            tx3.setVisibility(View.GONE);
        }else {
            email.setText(official.getEmail());
            if (!official.getEmail().equals("")) {
                Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            }
        }

        if(official.getPhoneNumber().equals("")) {
            phone.setVisibility(View.GONE);
            tx2.setVisibility(View.GONE);
        }else {
            phone.setText(official.getPhoneNumber());
            if (!official.getPhoneNumber().equals("")) {
                Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            }
        }

        if(official.getWebsite().equals("")) {
            website.setVisibility(View.GONE);
            tx4.setVisibility(View.GONE);
        }else {
            website.setText(official.getWebsite());
            if (!official.getWebsite().equals("")) {
                Linkify.addLinks(website, Linkify.WEB_URLS);
            }
        }

        if (official.getSocialMedia() != null) {
            Map<String, String> channels = official.getSocialMedia();

            if (channels.containsKey("Facebook")) {
                face = channels.get("Facebook");
                fb.setVisibility(View.VISIBLE);
            } else {
                fb.setVisibility(View.INVISIBLE);
            }

            if (channels.containsKey("Twitter")) {
                twit = channels.get("Twitter");
                twitter.setVisibility(View.VISIBLE);
            } else {
                twitter.setVisibility(View.INVISIBLE);
            }

            if (channels.containsKey("YouTube")) {
                yout = channels.get("YouTube");
                youtube.setVisibility(View.VISIBLE);

            } else {
                youtube.setVisibility(View.INVISIBLE);
            }
        }
        Linkify.addLinks(address, Linkify.ALL);
        DowwnloadNow();
    }

    public void ClickedOnPhoto(View v) {
        if (official.getPhoto() == null) {
            Toast.makeText(this, "Image Unavailable", Toast.LENGTH_LONG).show();
        } else {
            Intent intent_official = new Intent(this, photoView.class);
            intent_official.putExtra("Location", locText);
            intent_official.putExtra("Official", official);
            startActivity(intent_official);
        }
    }

    public void DowwnloadNow() {

        final String photoUrl = official.getPhoto();
        if (photoUrl != null) {
            final String changedUrl = photoUrl.replace("http:", "https:");
            Picasso.get().load(changedUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(Image);
        } else {
            if(doNetCheck()) {
                Picasso.get().load(photoUrl)
                        .fit()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missing)
                        .into(Image);
            }else{
                Picasso.get().load(photoUrl)
                        .fit()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.brokenimage)
                        .into(Image);
            }

        }
    }



    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + face;

        Intent intent = null;
        String urlToUse;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);

            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + face;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        startActivity(intent);

    }

    public void twitterClicked(View v) {

        String twitterAppUrl = "twitter://user?screen_name=" + twit;
        String twitterWebUrl = "https://twitter.com/" + twit;

        Intent intent = null;
        try {
            //Get the twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            //No twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void youtubeClicked(View v) {
        String youtubeWebUrl = "https://www.youtube.com/" + yout;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(youtubeWebUrl));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeWebUrl)));
        }
    }

    public void LogoClicked(View v) {
        String togoLink = "";
        Intent intent = null;
        if (official.getParty().equals("Republican Party")) {
            togoLink = "https://www.gop.com";
        } else if (official.getParty().equals("Democratic Party")) {
            togoLink = "https://democrats.org";
        }

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(togoLink));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(togoLink)));
        }
    }

    private boolean doNetCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}