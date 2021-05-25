package com.vedantsuram.civiladvocacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Official> officialList = new ArrayList<>();
    private CivilAdaptor civilAdaptor;
    private RecyclerView recyclerView;
    DataDownloader dataDownloader;
    private TextView banner;
    private String loc;
    private static final int LOCATION_REQUEST = 111;
    private FusedLocationProviderClient mFusedLocationClient;
    private String Current;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.LocBanner);
        recyclerView = findViewById(R.id.recycler);
        civilAdaptor = new CivilAdaptor(this,  officialList);
        recyclerView.setAdapter(civilAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4B2851")));


        if(doNetCheck()){
            Log.d(TAG, "onCreate: my loc variable " + loc);
            if(loc == null ){
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                findoutlocation();
            }else{
                DataDownloader dataDownloader = new DataDownloader(MainActivity.this, loc);
                new Thread(dataDownloader).start();
            }
        }else{
            banner.setText("No Data For Location");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitems, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search){
                AlertDialog.Builder addressInput = new AlertDialog.Builder(this);
                View customLayout = getLayoutInflater().inflate(R.layout.dailog, null);
                EditText text = customLayout.findViewById(R.id.edit);
                addressInput.setView(customLayout);
                addressInput.setTitle("Enter Address");
                addressInput.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String add = text.getText().toString();
                        loc = add;
                        if(doNetCheck()){
                            if(add.isEmpty()){
                            Toast.makeText(MainActivity.this, "Null value passed", Toast.LENGTH_SHORT).show();
                        } else {
                            DataDownloader dataDownloader = new DataDownloader(MainActivity.this, add);
                            new Thread(dataDownloader).start();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
                            builder.setTitle("No Network Connection");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dial = addressInput.create();
                dial.show();
        } else if(item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean doNetCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

     public void SettingList(ArrayList<Object> ret){

        if (ret == null) {
             officialList.clear();
        } else {
             loc = (String) ret.get(0);
             banner.setText(loc);
             officialList.clear();
             officialList.addAll((List<Official>) ret.get(1));
        }

         civilAdaptor.notifyDataSetChanged();
     }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("remember", loc);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loc = savedInstanceState.getString("remember");
        DataDownloader dataDownloader = new DataDownloader(MainActivity.this, loc);
        new Thread(dataDownloader).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int  position = recyclerView.getChildAdapterPosition(view);
        Official official = officialList.get(position);

        Intent intent_Detail = new Intent(MainActivity.this, OfficialDetail.class);
        intent_Detail.putExtra("Location", loc);
        intent_Detail.putExtra("Officail", official);
        startActivity(intent_Detail);
    }

    private  void findoutlocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Current = String.format(Locale.getDefault(),
                                    "%.5f, %.5f", location.getLatitude(), location.getLongitude());

                            dataDownloader = new DataDownloader(MainActivity.this, Current);
                            new Thread(dataDownloader).start();
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }

    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findoutlocation();
                } else {
                    Toast.makeText(MainActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();;
                }
            }
        }
    }



}