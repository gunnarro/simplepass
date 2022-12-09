package com.gunnarro.android.simplepass.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.ui.MainActivity;

import java.util.Objects;

import javax.inject.Inject;

public class PreferencesFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Inject
    public PreferencesFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.setPreferencesFromResource(R.xml.preferences, rootKey);
        requireActivity().setTitle(R.string.title_settings);
        Preference button = getPreferenceManager().findPreference("settings_back_button");
        if (button != null) {
            button.setOnPreferenceClickListener(arg0 -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction("view_credential_list");
                startActivity(intent);
                return true;
            });
        }

        setOnPreferenceChangeListener(getResources().getString(R.string.pref_automatic_logout_time));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_length_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_uppercase_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_least_lowercase_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_least_digit_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_least_symbol_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_repeat_character_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_illegal_sequence_alphabetical_rule));
        setOnPreferenceChangeListener(getResources().getString(R.string.pref_password_illegal_sequence_numerical_rule));
        setOnPreferenceChangeListener(Objects.requireNonNull(getResources().getString(R.string.pref_password_illegal_sequence_us_qwerty_rule)));
    }

    private void setOnPreferenceChangeListener(String preferenceResource) {
        Preference preference = getPreferenceManager().findPreference(preferenceResource);
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("PreferencesFragment", String.format("onPreferenceChange, preference key=%s, new value=%s", preference.getKey(), newValue));
        // update value
        return true;
    }
}
