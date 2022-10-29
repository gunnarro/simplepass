package com.gunnarro.android.simplepass.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gunnarro.android.simplepass.ui.MainActivity;

import javax.inject.Inject;
import com.gunnarro.android.simplepass.R;

import java.util.Objects;

public class PreferencesFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Inject
    public PreferencesFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.setPreferencesFromResource(R.xml.preferences, rootKey);
        Preference button = getPreferenceManager().findPreference("settings_back_button");
        if (button != null) {
            button.setOnPreferenceClickListener(arg0 -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction("view_credential_list");
                startActivity(intent);
                return true;
            });
        }

        // FIXME
    //    getPreferenceManager().findPreference(Objects.requireNonNull(getResources().getString(R.string.pref_automatic_logout_time))).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("PreferencesFragment", String.format("onPreferenceChange, preference key=%s, new value=%s", preference.getKey(), newValue));
        // update value
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        Log.d("PreferencesFragment", "onCreateOptionsMenu. hasVisible=" + menu.hasVisibleItems());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // hide current options menu
        menu.getItem(0).setVisible(false);
    }
}
