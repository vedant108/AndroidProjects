package com.vedantsuram.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class input_text_activity extends AppCompatActivity {

    EditText titleinput;
    EditText contentinput;
    String titleold = "";
    String contentold = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_text_activity);
        setTitle("Android Notes");
        titleinput = findViewById(R.id.edit_Title_input);
        contentinput = findViewById(R.id.edit_content_input);


        Intent intent = getIntent();
        if (intent.hasExtra("Title")) {
            titleold = intent.getStringExtra("Title");
            titleinput.setText(intent.getStringExtra("Title"));
        }
        if (intent.hasExtra("Content")) {
            contentold = intent.getStringExtra("Content");
            contentinput.setText(intent.getStringExtra("Content"));
        }

        contentinput.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("TITLE", titleinput.getText().toString());
        outState.putString("CONTENT", contentinput.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        titleinput.setText(savedInstanceState.getString("TITLE"));
        contentinput.setText(savedInstanceState.getString("CONTENT"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inputtext_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (titleinput.getText().toString().isEmpty()){
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                alert.setMessage(" Note can not be Saved without Title");
                alert.setTitle("Attention");
                alert.show();
            }
            else{
                SaveNote();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SaveNote() {

        String title = titleinput.getText().toString();
        String content = contentinput.getText().toString();
            if(title.isEmpty() && content.isEmpty()){
                Toast.makeText(this, "Null value passed to Title or Description", Toast.LENGTH_SHORT).show();
            }else if(titleold.equals(title) && contentold.equals(content)){
                Intent intent = new Intent(this, MainActivity.class );
                startActivity(intent);
            } else {
                Notes note  =  new Notes(title,Calendar.getInstance().getTime().toString(),content);
                Intent intent = new Intent(this, MainActivity.class );
                intent.putExtra("notesent", note);
                if(titleold.isEmpty() && contentold.isEmpty()){
                    intent.putExtra("Check", "New" );

                }else {
                    intent.putExtra("Check", "old" );
                }
                setResult(RESULT_OK, intent);
                finish();
            }

    }

    @Override
    public void onBackPressed() {

        if(titleinput.getText().toString().isEmpty() && contentinput.getText().toString().isEmpty()){
            Toast.makeText(this, "NOTE NOT SAVED", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        else if(titleold.equals(titleinput.getText().toString()) && contentold.equals(contentinput.getText().toString())){
            super.onBackPressed();
        }
        else if (!titleinput.getText().toString().isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SaveNote();
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            alert.setMessage(" Want to Save " +"'"+ titleinput.getText().toString()+ "' note"+"?");
            alert.setTitle("Attention");
            alert.show();
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            alert.setMessage(" Note can not be Saved without Title");
            alert.setTitle("Attention");
            alert.show();
        }
    }


}