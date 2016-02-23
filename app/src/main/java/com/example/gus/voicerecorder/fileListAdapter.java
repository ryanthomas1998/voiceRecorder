package com.example.gus.voicerecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ryan on 12/20/15.
 */

    public class fileListAdapter extends RecyclerView.Adapter<fileListAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    MediaPlayer gus = new MediaPlayer();
    public boolean isPlaying=false;

    private File root = new File (Environment.getExternalStorageDirectory() + "/VoiceRecorder/");


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView recordingName, recordingLength;
        public ImageButton myImageButton;
        public RelativeLayout relLay;

        public ViewHolder(View v) {
            super(v);
            recordingName = (TextView) v.findViewById(R.id.recording_name);
            recordingLength = (TextView) v.findViewById(R.id.duration);
            myImageButton = (ImageButton) v.findViewById(R.id.play);
            relLay = (RelativeLayout) v.findViewById(R.id.item_layout);
        }
    }

    public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
    public fileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = mDataset.get(position);

        //Removes the filetype tag and sets the textview for the name to the filename

        holder.recordingName.setText(name.replaceAll(".mp3", ""));



        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(root + "/"+name);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        
        String dateFormatted = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(Long.parseLong(durationStr)),
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(durationStr)) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Long.parseLong(durationStr))),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(durationStr)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(durationStr))));
        
        holder.recordingLength.setText(dateFormatted);


        //Grabs the mp3 and sets the duration value to the duration in the metadata.


        holder.myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(holder,name);
            }
        });
        holder.relLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(holder,name);
            }
        });


        holder.myImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final int pos = mDataset.indexOf(name);
                remove(name);


                final Snackbar snackbar = Snackbar
                        .make(holder.myImageButton, name + " is deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar = Snackbar.make(view.getRootView(), name + " is restored", Snackbar.LENGTH_SHORT);
                                snackbar.show();

                            }

                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                switch (event) {
                                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                        add(pos, name);

                                        break;
                                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        Log.d("filename", name);
                                        File delFile = new File(root + "/" + name);
                                        delFile.delete();
                                        break;
                                }
                            }
                        });

                snackbar.show();

                return false;
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void playSong(ViewHolder holder, String name){
        if(isPlaying==false) {
            try {

                String path = name;
                gus.setDataSource(root + "/" + path);
                gus.prepare();
                gus.start();
                Log.d("is playing", "true");
                isPlaying = true;
                holder.myImageButton.setBackgroundResource(R.drawable.pausebtn);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{

            gus.stop();
            gus.reset();
            isPlaying=false;
            holder.myImageButton.setBackgroundResource(R.drawable.play);

        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public fileListAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }
}



