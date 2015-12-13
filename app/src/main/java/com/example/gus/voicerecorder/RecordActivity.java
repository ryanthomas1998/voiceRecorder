package com.example.gus.voicerecorder;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);


        final Chronometer chronometer = (Chronometer) findViewById(R.id.chrono);

        final MediaRecorder myAudioRecorder = new MediaRecorder();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        0);
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        0);
//
//        }
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + getIntent().getStringExtra("filename") + ".3gp");

        FloatingActionButton mic = (FloatingActionButton) findViewById(R.id.record);
        mic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                        counter++;
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    chronometer.stop();
                    counter--;
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });


    }
}
