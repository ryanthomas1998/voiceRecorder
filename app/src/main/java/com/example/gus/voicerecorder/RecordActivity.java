package com.example.gus.voicerecorder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
        mic.setRippleColor(getResources().getColor(R.color.colorAccent));
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                        EditDialog();
                        counter++;
                } else {
                    if(chronometer != null && myAudioRecorder != null) {
                        chronometer.stop();
                        counter--;
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                    }
                    Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                    intent.putExtra("act","record");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            // the context of the activity
                            RecordActivity.this,

                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute

                            new Pair<View, String>(v.findViewById(R.id.record),
                                    "fab"),
                            new Pair<View, String>(bottomBar,
                                    "bottom")
                    );

                    startActivity(intent, options.toBundle());
                }
            }
        });


    }
    public void EditDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View FileDialogView = factory.inflate(
                R.layout.filedialog, null);
        EditText myEditText = (EditText) FileDialogView.findViewById(R.id.filename);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(myEditText, InputMethodManager.SHOW_FORCED);
        final AlertDialog FileDialog = new AlertDialog.Builder(this).create();
        FileDialog.setView(FileDialogView);
        FileDialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + ((TextView) FileDialogView.findViewById(R.id.filename)).getText().toString() + ".mp3");
                try {
                    myAudioRecorder.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myAudioRecorder.start();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                FileDialog.dismiss();
            }
        });
        FileDialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FileDialog.dismiss();

            }
        });

        FileDialog.show();
        counter--;
    }

    @Override
    public void onBackPressed(){


//        Intent intent = new Intent(this, MainActivity.class);
//        startActivityForResult(intent, 100);
        super.onBackPressed();
    }

}
