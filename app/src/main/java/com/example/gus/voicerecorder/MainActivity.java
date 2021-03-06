package com.example.gus.voicerecorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private File root = new File (Environment.getExternalStorageDirectory() + "/VoiceRecorder/");
    MediaPlayer gus = new MediaPlayer();
    Scene mScene1,mScene2;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public int timesFunctionClicked =0;
    private SlidingUpPanelLayout mLayout;

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
                colorRevealShow(findViewById(R.id.reveal_view));

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
                }, 200);

            }

        });


        findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View myView = findViewById(R.id.reveal_view);


                if (timesFunctionClicked > 0) {
                    colorRevealHide(myView);

                    timesFunctionClicked--;
                } else {
                    //colorRevealShow(myView);
                    timesFunctionClicked++;


                }
            }
        });


        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startActivity(new Intent(MainActivity.this,SettingsActivity.class));

            }
        });


        File file = new File(Environment.getExternalStorageDirectory(), "/VoiceRecorder/" );
        file.mkdir();

        final ArrayList<String> filenames = new ArrayList<>();

        Collections.addAll(filenames, root.list());


        mRecyclerView = (RecyclerView) findViewById(R.id.filenames);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new fileListAdapter(filenames);
        mRecyclerView.setAdapter(mAdapter);



    }

    public void colorRevealShow(final View toReveal){
        // get the center for the clipping circle
        int cx = (toReveal.getLeft() + toReveal.getRight()) / 2;
        int cy = toReveal.getHeight();

        // get the final radius for the clipping circle
        int finalRadius = toReveal.getWidth();

        // create and start the animator for this view
        // (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(toReveal, cx, cy, 0, finalRadius*4);
        toReveal.setVisibility(View.VISIBLE);
        anim.setDuration(400);
        anim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.interpolator.accelerate_quad));
        anim.start();
    }
    public void colorRevealHide(final View toReveal){
        // get the center for the clipping circle
        int cx = toReveal.getWidth() / 2;
        int cy = toReveal.getHeight();

        // get the initial radius for the clipping circle
        int initialRadius = toReveal.getWidth();

        // create the animation (the final radius is zero)
        Animator  anim =
                ViewAnimationUtils.createCircularReveal(toReveal, cx, cy, initialRadius*4, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                toReveal.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(400);
        anim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.interpolator.accelerate_quad));

        anim.start();
    }


    @Override
    protected void onResume() {
        super.onResume();

//        ListView list = (ListView) findViewById(R.id.filenames);
  //      list.invalidateViews();
        mRecyclerView.invalidate();
        Intent received = getIntent();


        if(received.getStringExtra("act")!=null && received.getStringExtra("act").equals("recordback")) {
            View fabView = findViewById(R.id.record);
          //  Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_back);
          //  fabView.startAnimation(anim);
        }



    }


}

