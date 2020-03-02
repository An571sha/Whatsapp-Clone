package com.example.whatsappclone.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.whatsappclone.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferencescreen);
    }
}
