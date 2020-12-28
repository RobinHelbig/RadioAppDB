package de.helbigrobin.app13.mainActivity.fragments;

import android.content.Intent;
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
        ListView lv = getView().findViewById(R.id.radio_menu);
        arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                radioStations);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), RadioStationActivity.class);
            intent.putExtra("radioStation", radioStations.get(position));
            startActivity(intent);
        });
        registerForContextMenu(lv);
    }
}
