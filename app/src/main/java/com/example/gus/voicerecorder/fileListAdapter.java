package com.example.gus.voicerecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ryan on 12/20/15.
 */

    public class fileListAdapter extends RecyclerView.Adapter<fileListAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    MediaPlayer gus = new MediaPlayer();

    private File root = new File (Environment.getExternalStorageDirectory() + "/VoiceRecorder/");


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView recordingName, recordingLength;
        public ImageButton myImageButton;

        public ViewHolder(View v) {
            super(v);
            recordingName = (TextView) v.findViewById(R.id.recording_name);
            recordingLength = (TextView) v.findViewById(R.id.duration);
            myImageButton = (ImageButton) v.findViewById(R.id.play);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = mDataset.get(position);
        holder.recordingName.setText(name);

        holder.myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String path = name;
                    gus.setDataSource(root + "/" + path);
                    gus.prepare();
                    gus.start();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public fileListAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }
}



