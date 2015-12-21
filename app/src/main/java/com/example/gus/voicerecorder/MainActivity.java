package com.example.gus.voicerecorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private File root = new File (Environment.getExternalStorageDirectory() + "/VoiceRecorder/");
    MediaPlayer gus = new MediaPlayer();
    Scene mScene1,mScene2;
    public int timesFunctionClicked =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);


        final View fabView = findViewById(R.id.record);
        final FloatingActionButton toRecord = (FloatingActionButton) findViewById(R.id.record);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        toRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        toRecord.setRippleColor(getResources().getColor(R.color.colorAccent));

        toRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_forward);
                fabView.startAnimation(anim);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                }, 300);


            }

        });

        findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final View myView = findViewById(R.id.toFill);
                View bottomBar = findViewById(R.id.scene_root);

                if(timesFunctionClicked>0){

                    // get the center for the clipping circle
                    int cx = myView.getWidth() / 2;
                    int cy = myView.getHeight();

                    // get the initial radius for the clipping circle
                    int initialRadius = myView.getWidth();

                    // create the animation (the final radius is zero)
                    Animator  anim =
                            ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

                    // make the view invisible when the animation is done
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            myView.setVisibility(View.INVISIBLE);
                        }
                    });

                    // start the animation
                    anim.start();
                    timesFunctionClicked--;
                }else {
                    timesFunctionClicked++;


                    // get the center for the clipping circle
                    int cx = (myView.getLeft() + myView.getRight()) / 2;
                    int cy = myView.getHeight();

                    // get the final radius for the clipping circle
                    int finalRadius = myView.getWidth();

                    // create and start the animator for this view
                    // (the start radius is zero)
                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                    myView.setVisibility(View.VISIBLE);
                    anim.start();
                }
            }
        });


        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int cx = v.getRight() - 30;
//                int cy = v.getBottom() - 60;
//                int finalRadius = Math.max(v.getWidth(), v.getHeight());
//                Animator animator = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
//                v.startAnimation(anims);

                final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_back);
                fabView.startAnimation(anim);

            }
        });


        File file = new File(Environment.getExternalStorageDirectory(), "/VoiceRecorder/" );
        file.mkdir();

        final ArrayList<String> filenames = new ArrayList<>();

        ArrayAdapter<String> FilenameAdapter = new ArrayAdapter<>(this, R.layout.list_item, filenames);

        final ListView listView = (ListView) findViewById(R.id.filenames);
        listView.setAdapter(FilenameAdapter);

            Collections.addAll(filenames, root.list());
            ListView list = (ListView) findViewById(R.id.filenames);
            list.invalidateViews();



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


    @Override
    protected void onResume() {
        super.onResume();

        ListView list = (ListView) findViewById(R.id.filenames);
        list.invalidateViews();
        Intent received = getIntent();

        if(received.getStringExtra("act")!=null && received.getStringExtra("act").equals("record")){
            View fabView = findViewById(R.id.record);
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_back);
            fabView.startAnimation(anim);
        }



    }
}

