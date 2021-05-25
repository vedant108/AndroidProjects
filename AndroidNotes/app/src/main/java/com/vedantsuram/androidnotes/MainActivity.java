package com.vedantsuram.androidnotes;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "ABc";
    private ArrayList<Notes> notesList = new ArrayList<Notes>();
    private RecyclerView recyclerView;
    private Notes_Adapter notesAdapter;
    private int position;
    private int REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new Notes_Adapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Android Notes(" + notesList.size() + ")");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a237e")));
        notesList.addAll(populatelist());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add){
            Intent intent = new Intent(this, input_text_activity.class );
            startActivityForResult(intent , REQUEST_CODE);
        } else if(item.getItemId() == R.id.about){
            Intent intent = new Intent(this, About.class );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        position = recyclerView.getChildLayoutPosition(view);
        Intent intent = new Intent(this, input_text_activity.class);
        intent.putExtra("Title", notesList.get(position).getTitle());
        intent.putExtra("Content", notesList.get(position).getContent());
        startActivityForResult(intent, REQUEST_CODE);
    }

    public ArrayList<Notes> populatelist() {
        ArrayList<Notes> List = new ArrayList<Notes>();
        try {

            InputStream in = getApplicationContext().openFileInput("Data.json");
            BufferedReader read = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = read.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonarray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);
                String title = jsonObject.getString("Title");
                String date = jsonObject.getString("Date");
                String content = jsonObject.getString("Content");
                Notes notes = new Notes(title, date, content);
                List.add(notes);
                Log.d("see", "populatelist: " + title + content);
            }

        } catch (IOException e) {
            Log.d(" also", "populatelist: " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return List;
    }


    @Override
    protected void onPause() {
        Savenote();
        super.onPause();

    }

    @Override
    protected void onResume() {
       super.onResume();
       notesAdapter.notifyDataSetChanged();
    }

    public void Savenote() {
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput("Data.json", Context.MODE_PRIVATE);

            PrintWriter printWriter  = new PrintWriter(fos);
            printWriter.print(notesList);
            printWriter.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("etrace", "Savenote: "+ e);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        position = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notesList.remove(position);
                notesAdapter.notifyDataSetChanged();
                position = -1;
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                position = -1;
            }
        });
        dialog.setMessage("Are you sure to DELETE this?");
        dialog.setTitle("Attention");
        dialog.show();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE == requestCode && resultCode == RESULT_OK){
            assert data != null;
            Notes new_note = (Notes) Objects.requireNonNull(data.getExtras().getSerializable("notesent"));
                String check = data.getStringExtra("Check");
                if( check.equals("New")) {
                    notesList.add(0, new_note);
                }
                else{
                    notesList.remove(position);
                    notesList.add(0, new_note);
            }
        }

    }
}