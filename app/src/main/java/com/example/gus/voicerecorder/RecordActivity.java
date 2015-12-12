package com.example.gus.voicerecorder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        getActionBar().hide();

        final Chronometer chronometer = (Chronometer) findViewById(R.id.chrono);

        final MediaRecorder myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + getIntent().getStringExtra("filename") + ".3gp");

        ImageButton mic = (ImageButton) findViewById(R.id.mic);
        mic.setBackground(getDrawable(R.mipmap.microphone));
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
