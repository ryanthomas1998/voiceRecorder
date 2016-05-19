package com.example.gus.voicerecorder;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.gus.voicerecorder.R;

public class SettingsActivity extends PreferenceActivity {


    public static final String KEY_FILE_FORMAT = "file_format";
    public static final String KEY_CODEC = "codec";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}