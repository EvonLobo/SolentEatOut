package com.example.solenteatout;
import android.preference.PreferenceActivity;
import android.os.Bundle;

import com.example.solenteatout.R;

public class PrefsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
