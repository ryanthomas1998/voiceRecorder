package com.example.gus.voicerecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


        File file = new File(Environment.getExternalStorageDirectory(), "/VoiceRecorder/" );
        file.mkdir();

        final ArrayList<String> filenames = new ArrayList<>();

        ArrayAdapter<String> FilenameAdapter = new ArrayAdapter<>(this, R.layout.list_item, filenames);

        final ListView listView = (ListView) findViewById(R.id.filenames);
        listView.setAdapter(FilenameAdapter);
        if(filenames.size() > 0 ) {
            Collections.addAll(filenames, root.list());
            ListView list = (ListView) findViewById(R.id.filenames);
            list.invalidateViews();
        }
        Button button = (Button) findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RecordActivity.class).putExtra("filename", ((EditText) findViewById(R.id.edit_txt)).getText().toString().replaceAll(" ", "")));
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
                        String dFile = filenames.get(position);
                        Log.d("filename", dFile);
                        File delFile = new File(root + "/" + dFile);
                        delFile.delete();
                        Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                        filenames.remove(position);
                        ((ListView) findViewById(R.id.filenames)).invalidateViews();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setTitle("Delete?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;

            }
        });

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


    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView list = (ListView) findViewById(R.id.filenames);
        list.invalidateViews();
    }
}

