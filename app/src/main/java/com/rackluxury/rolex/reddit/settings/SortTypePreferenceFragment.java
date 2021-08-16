package com.rackluxury.rolex.reddit.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.rackluxury.rolex.R;

public class SortTypePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sort_type_preferences, rootKey);
    }
}