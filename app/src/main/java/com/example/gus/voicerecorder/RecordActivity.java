package com.example.gus.voicerecorder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private int counter = 0;
    public Chronometer chronometer;
    public MediaRecorder myAudioRecorder ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        chronometer = (Chronometer) findViewById(R.id.chrono);
        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


        FloatingActionButton mic = (FloatingActionButton) findViewById(R.id.record);
        mic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                        EditDialog();
                        counter++;
                } else {

                    chronometer.stop();
                    counter--;
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });


    }
    public void EditDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecordActivity.this);
        alertDialog.setTitle("Filename");
        alertDialog.setMessage("Enter Filename");

        final EditText input = new EditText(RecordActivity.this);
        input.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Input", input.getText().toString());
                        myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + input.getText().toString() + ".mp3");
                        try {
                            myAudioRecorder.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myAudioRecorder.start();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        counter--;
                    }
                });

        alertDialog.show();
    }

}
