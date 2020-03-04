package com.brtvsk.noter;


import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;



public class SettingsFragment extends PreferenceFragmentCompat   {

    public static final String
            KEY_PREF_EXAMPLE_SWITCH = "example_switch";

    public static final String KEY_PREF_USERNAME = "username_preference";

    public static final String KEY_PREF_DATEFORMAT = "dateformat_preference";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_layout, rootKey);
    }
}