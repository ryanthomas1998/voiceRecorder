package com.example.gus.voicerecorder;

import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RecordActivity extends AppCompatActivity {

    private int counter = 0;
    public Chronometer chronometer;
    public MediaRecorder myAudioRecorder ;
    public boolean recording;
    public boolean paused=false;
    public Timer timer;
    public String fileName="";
    private GraphicalView mChart;
    private boolean drawDisplayed;

    private XYSeries visitsSeries ;
    private XYSeries secondSeries;

    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesDataset Sdataset;

    XYMultipleSeriesDataset multiDateSet;

    private XYSeriesRenderer visitsRenderer;
    private XYMultipleSeriesRenderer multiRenderer;
    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        chronometer = (Chronometer) findViewById(R.id.chrono);

        // Setting up chart
        setupTopChart();

        final String TAG = "test";
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setTouchEnabled(false);
        mLayout.setMinFlingVelocity(100);


        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_WB);
        myAudioRecorder.setAudioEncodingBitRate(100000);


        FloatingActionButton mic = (FloatingActionButton) findViewById(R.id.record);
        mic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        mic.setRippleColor(getResources().getColor(R.color.colorAccent));

        (findViewById(R.id.pause_record)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawDisplayed) {
                    mLayout.setPanelHeight(800);
                    (findViewById(R.id.pause_record)).animate().rotationBy(180).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    drawDisplayed=true;
                }else{

                    mLayout.setPanelHeight(0);
                    (findViewById(R.id.pause_record)).animate().rotationBy(180).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400).start();
                    drawDisplayed=false;
                }

            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.fileTypeSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filetypes, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        (findViewById(R.id.stop_record)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {stopRecording(v);}
        });
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(v);

            }
        });


    }
    public void stopRecording(View v){
        if (counter == 0) {
            EditDialog();
            counter++;
        } else if (chronometer != null && myAudioRecorder != null && recording) {
                recording = false;
                // timer.cancel();
                chronometer.stop();
                counter--;
                myAudioRecorder.stop();
                myAudioRecorder.release();

            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            intent.putExtra("act", "record");

//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    // the context of the activity
//                    RecordActivity.this,
//
//                    // For each shared element, add to this method a new Pair item,
//                    // which contains the reference of the view we are transitioning *from*,
//                    // and the value of the transitionName attribute
//
//                    new Pair<View, String>(v.findViewById(R.id.record),
//                            "fab"),
//                    new Pair<View, String>(v.findViewById(R.id.bottomBar),
//                            "bottom")
//            );




            startActivity(intent);
        }

    }
    private void setupTopChart(){

        // Creating an  XYSeries for Sound
        visitsSeries = new XYSeries("");
        secondSeries = new XYSeries("");

        // Creating a dataset to hold each series
        dataset = new XYMultipleSeriesDataset();
        Sdataset = new XYMultipleSeriesDataset();


        // Adding Visits Series to the dataset
        dataset.addSeries(visitsSeries);
        Sdataset.addSeries(secondSeries);

        //Adding datasets together
        multiDateSet = new XYMultipleSeriesDataset();
        multiDateSet.addSeries(visitsSeries);
        multiDateSet.addSeries(secondSeries);

        // Creating XYSeriesRenderer to customize visitsSeries
        visitsRenderer = new XYSeriesRenderer();
        visitsRenderer.setColor(getResources().getColor(R.color.colorAccent));
        visitsRenderer.setPointStyle(PointStyle.CIRCLE);
        visitsRenderer.setFillPoints(true);
        visitsRenderer.setDisplayChartValues(false);
        visitsRenderer.setLineWidth(10);



        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer = new XYMultipleSeriesRenderer();


        multiRenderer.setMargins(new int[]{0, 0, 0, 0});
        multiRenderer.setPanEnabled(false);
        multiRenderer.setShowLegend(false);
        multiRenderer.setChartTitle("");
        //multiRenderer.setFitLegend(true);

        //Eliminate the Shite
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setXTitle("");
        multiRenderer.setShowLabels(false);
        multiRenderer.setShowAxes(false);

        multiRenderer.setMarginsColor(Color.TRANSPARENT);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        multiRenderer.setXLabelsColor(Color.TRANSPARENT);
        multiRenderer.setYLabelsColor(0, Color.TRANSPARENT);
        multiRenderer.setGridColor(Color.TRANSPARENT);
        multiRenderer.setXLabelsPadding(0);
        multiRenderer.setYLabelsPadding(0);
        multiRenderer.setBarWidth(100);

        //Establish Beginning Dimensions
        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(10);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(0);


        //Make the bar graphs touching
        multiRenderer.setBarSpacing(0);


        // Adding visitsRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(visitsRenderer);
        multiRenderer.addSeriesRenderer(visitsRenderer);

        // Getting a reference to LinearLayout of the RecordActivity Layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chartTop);


        mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(), multiDateSet, multiRenderer, org.achartengine.chart.BarChart.Type.DEFAULT);

        // Adding the Line Chart to the LinearLayout
        chartContainer.addView(mChart);
    }

    private class ChartTask extends AsyncTask<Void, String, Void> {

        // Generates dummy data in a non-ui thread
        @Override
        protected Void doInBackground(Void... params) {
            int i = 0,yMax=0,yMin=0;
            try{
                do{
                    String [] values = new String[2];

                    int maxAmp = myAudioRecorder.getMaxAmplitude();
                    if(maxAmp>yMax){
                        yMax =maxAmp;
                        multiRenderer.setYAxisMax(yMax);
                    }
                    if(maxAmp*-1<yMin){
                        yMin=maxAmp*-1;
                        multiRenderer.setYAxisMin(yMin);
                    }
                    if(i>10){
                        multiRenderer.setXAxisMin(multiRenderer.getXAxisMin()+1);
                        multiRenderer.setXAxisMax(multiRenderer.getXAxisMax()+1);
                    }

                    Log.d("record",Integer.toString(maxAmp));
                    values[0] = Integer.toString(i);
                    values[1] = Integer.toString(maxAmp);

                    publishProgress(values);
                    Thread.sleep(50);

                    i++;
                }while(recording);
            }catch(Exception e){ }
            return null;
        }

        // Plotting generated data in the graph
        @Override
        protected void onProgressUpdate(String... values) {
            visitsSeries.add(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
            secondSeries.add(Integer.parseInt(values[0]) - 1, Integer.parseInt(values[1]) * -1);
            Log.d("test", values[1]);
            mChart.repaint();

        }
    }


    public void EditDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View FileDialogView = factory.inflate(
                R.layout.filedialog, null);
        EditText myEditText = (EditText) FileDialogView.findViewById(R.id.filename);

        final AlertDialog FileDialog = new AlertDialog.Builder(this).create();
        FileDialog.setView(FileDialogView);
        FileDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        FileDialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fileName=((TextView) FileDialogView.findViewById(R.id.filename)).getText().toString() + ".mp3";
                myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + ((TextView) FileDialogView.findViewById(R.id.filename)).getText().toString() + ".mp3");
                try {
                    myAudioRecorder.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myAudioRecorder.start();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                recording=true;
                // Start plotting chart
                new ChartTask().execute();

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

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(RecordActivity.this,MainActivity.class);
        setIntent.putExtra("act","recordback");
        startActivity(setIntent);
    }

}
