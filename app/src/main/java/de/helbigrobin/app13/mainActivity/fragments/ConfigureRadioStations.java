package de.helbigrobin.app13.mainActivity.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import de.helbigrobin.app13.R;
import de.helbigrobin.app13.RadioStationActivity;
import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;
import de.helbigrobin.app13.mainActivity.MainActivity;

public class ConfigureRadioStations extends Fragment {
    List<RadioStation> radioStations;
    ArrayAdapter<RadioStation> arrayAdapter;

    public ConfigureRadioStations() {
        super(R.layout.fragment_configureradiostations);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioStations = ((MainActivity) getActivity()).radioStations;
        setupRadioStationList();
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        RadioStation station = radioStations.get(position);

        if(item.getItemId() == R.id.edit) {

        } else if(item.getItemId() == R.id.delete){

        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }

    private void setupRadioStationList(){
        ListView lv = getView().findViewById(R.id.radioEdit_menu);
        arrayAdapter = new RadioConfigAdapter(getActivity().getApplicationContext(), R.layout.listviewelement_radioedit,
                radioStations);
        lv.setAdapter(arrayAdapter);
        registerForContextMenu(lv);
    }

    static class RadioConfigAdapter extends ArrayAdapter<RadioStation> {
        private final List<RadioStation> items;
        private final Context context;

        public RadioConfigAdapter(Context context, int layoutResourceId, List<RadioStation> items) {
            super(context, layoutResourceId, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.listviewelement_radioedit,parent,false);

            RadioStation currentStation = items.get(position);

            TextView textView = (TextView) listItem.findViewById(R.id.radioEditTextView);
            textView.setText(currentStation.name);

            ImageView imageView = (ImageView) listItem.findViewById(R.id.radioEditImageView);
            if(currentStation.favourite){
                imageView.setImageResource(R.mipmap.favorite_yes);
            } else {
                imageView.setImageResource(R.mipmap.favorite_no);
            }

            imageView.setOnClickListener(view -> {
                //Favoritenstatus togglen
                currentStation.favourite = !currentStation.favourite;

                AsyncTask.execute(() -> {
                    //DB Update
                    AppDatabase db = AppDatabase.getInstance(getContext());
                    RadioStationDao radioStationDao = db.radioStationDao();
                    radioStationDao.updateRadioStation(currentStation);

                    //Synchron im MainThread UI updaten
                    new Handler(Looper.getMainLooper()).post(() -> {
                        //Neuen Favoritenstatus in der UI setzen
                        if(currentStation.favourite){
                            imageView.setImageResource(R.mipmap.favorite_yes);
                        } else {
                            imageView.setImageResource(R.mipmap.favorite_no);
                        }
                    });
                });
            });

            return listItem;
        }
    }
}