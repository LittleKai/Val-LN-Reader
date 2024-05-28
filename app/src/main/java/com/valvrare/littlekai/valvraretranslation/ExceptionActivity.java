package com.valvrare.littlekai.valvraretranslation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;

public class ExceptionActivity extends AppCompatActivity {

    private TextView exceptionTextView;
    private Toolbar toolbar;
    String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);

        exceptionTextView = (TextView) findViewById(R.id.exceptionTextView);

        toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        error = intent.getStringExtra("error");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Jump Manga has encountered an error and needed to stop.");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        Button quit = (Button)findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button restart = (Button)findViewById(R.id.send);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText =
                        "mailto:aduc5525@gmail.com" +
                                "?subject=" + Uri.encode("Valvrare Translation error report") +
                                "&body=" + Uri.encode(error);
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));
            }
        });

        exceptionTextView.setText(error);

        String filename = "logcat.txt";

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(error.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
