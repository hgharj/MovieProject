package com.example.android.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class MoviePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private int mDefaultPrefIndex = 0;
        private int mCurrentPrefIndex = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                mCurrentPrefIndex = listPreference.findIndexOfValue(stringValue);
                if (mCurrentPrefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[mCurrentPrefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }

            if (mCurrentPrefIndex != mDefaultPrefIndex) {
                Intent intent = new Intent(this.getActivity(), MainActivity.class);
                startActivity(intent);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                mDefaultPrefIndex = listPreference.findIndexOfValue(preferenceString);
            }
            onPreferenceChange(preference, preferenceString);
        }


    }
}
