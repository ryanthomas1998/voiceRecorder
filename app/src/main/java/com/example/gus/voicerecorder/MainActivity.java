package com.example.gus.voicerecorder;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private File root = new File (Environment.getExternalStorageDirectory() + "/VoiceRecorder/");
    MediaPlayer gus = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);


        final View fabView = findViewById(R.id.record);
        FloatingActionButton toRecord = (FloatingActionButton) findViewById(R.id.record);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        toRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        toRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        // the context of the activity
                        MainActivity.this,

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

        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = v.getRight() - 30;
                int cy = v.getBottom() - 60;
                int finalRadius = Math.max(v.getWidth(), v.getHeight());
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                v.setVisibility(View.VISIBLE);
                anim.start();

            }
        });


        File file = new File(Environment.getExternalStorageDirectory(), "/VoiceRecorder/" );
        file.mkdir();

        final ArrayList<String> filenames = new ArrayList<>();

        ArrayAdapter<String> FilenameAdapter = new ArrayAdapter<>(this, R.layout.list_item, filenames);

        final ListView listView = (ListView) findViewById(R.id.filenames);
        listView.setAdapter(FilenameAdapter);
        //if(filenames.size() > 0 ) {
            Collections.addAll(filenames, root.list());
            ListView list = (ListView) findViewById(R.id.filenames);
            list.invalidateViews();
        //}


        ListView rows = (ListView) findViewById(R.id.filenames);
        rows.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                try {
                    final int position = p;
                    String path = filenames.get(position);
                    gus.setDataSource(root + "/" + path);
                    gus.prepare();
                    gus.start();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gus.stop();
                        gus.release();

                    }
                });

                builder.setTitle("Playing Recording");
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        ((ListView) findViewById(R.id.filenames)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int p, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final int position = p;
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String dFile = filenames.get(position);
                        Log.d("filename", dFile);
                        File delFile = new File(root + "/" + dFile);
                        delFile.delete();

                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorLayout), dFile + " is deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), dFile + " is restored", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                });

                        snackbar.show();
                        filenames.remove(position);
                        ((ListView) findViewById(R.id.filenames)).invalidateViews();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setTitle("Delete?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
        });



    }
    public void animate(){}


    @Override
    protected void onResume() {
        super.onResume();

        ListView list = (ListView) findViewById(R.id.filenames);
        list.invalidateViews();
    }
}

