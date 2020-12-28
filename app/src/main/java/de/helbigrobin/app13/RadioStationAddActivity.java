package de.helbigrobin.app13;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RadioStationAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        int radioStationUid = intent.getIntExtra("radioStationUid", -1);
        if (radioStationUid >= 0){

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }
}
