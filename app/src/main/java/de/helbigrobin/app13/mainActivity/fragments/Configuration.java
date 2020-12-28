package de.helbigrobin.app13.mainActivity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import de.helbigrobin.app13.R;

public class Configuration extends Fragment {
    public Configuration() {
        super(R.layout.fragment_configuration);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupLayout();
    }

    private void setupLayout(){
        String playLastStationKey = getString(R.string.sharedPreferences_key_playLastStation);
        String onlyShowFavouritesKey = getString(R.string.sharedPreferences_key_onlyShowFavourites);
        SwitchCompat playLastStationSwitch = getView().findViewById(R.id.playLastStationSwitch);
        SwitchCompat onlyShowFavouritesSwitch = getView().findViewById(R.id.onlyShowFavouritesSwitch);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "de.helbigrobin.app13", Context.MODE_PRIVATE);

        boolean playLastStation = prefs.getBoolean(playLastStationKey, false);
        boolean onlyShowFavourites = prefs.getBoolean(onlyShowFavouritesKey, false);

        playLastStationSwitch.setChecked(playLastStation);
        onlyShowFavouritesSwitch.setChecked(onlyShowFavourites);

        playLastStationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(playLastStationKey, isChecked).apply();
        });

        onlyShowFavouritesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(onlyShowFavouritesKey, isChecked).apply();
        });
    }
}