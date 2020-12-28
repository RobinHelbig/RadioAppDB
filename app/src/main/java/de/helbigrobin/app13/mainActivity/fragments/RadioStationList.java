package de.helbigrobin.app13.mainActivity.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.helbigrobin.app13.R;
import de.helbigrobin.app13.RadioStationActivity;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.mainActivity.MainActivity;

public class RadioStationList extends Fragment {
    List<RadioStation> radioStations;
    ArrayAdapter<RadioStation> arrayAdapter;

    public RadioStationList() {
        super(R.layout.fragment_radiostationlist);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioStations = ((MainActivity) getActivity()).radioStations;
        setupRadioStationList();
    }

    private void setupRadioStationList(){
        String onlyShowFavouritesKey = getString(R.string.sharedPreferences_key_onlyShowFavourites);
        String playLastStationUIdKey = getString(R.string.sharedPreferences_key_playLastStation_uid);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "de.helbigrobin.app13", Context.MODE_PRIVATE);

        boolean onlyShowFavourites = prefs.getBoolean(onlyShowFavouritesKey, false);

        List<RadioStation> shownRadioStations = new ArrayList<>();
        for(RadioStation station : radioStations){
            if(station.favourite || !onlyShowFavourites){ //Wenn Sender Favorit oder Einstellung onlyShowFavourites = false
                shownRadioStations.add(station);
            }
        }

        ListView lv = getView().findViewById(R.id.radio_menu);
        arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                shownRadioStations);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            RadioStation selectedStation = shownRadioStations.get(position);

            //Wenn eine RadioStation geöffnet wird, wird die uid gespeichert, um sie beim Appstart direkt öffnen zu können
            prefs.edit().putLong(playLastStationUIdKey, selectedStation.uid).apply();

            Intent intent = new Intent(getActivity().getApplicationContext(), RadioStationActivity.class);
            intent.putExtra("radioStation", selectedStation);
            startActivity(intent);
        });
        registerForContextMenu(lv);
    }
}
