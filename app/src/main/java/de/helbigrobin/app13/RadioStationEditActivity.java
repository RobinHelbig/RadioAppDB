package de.helbigrobin.app13;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;
import de.helbigrobin.app13.mainActivity.fragments.RadioStationList;

public class RadioStationEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        RadioStation radioStation = (RadioStation) intent.getSerializableExtra("radioStation");
        if (radioStation != null){
            setupLayout(radioStation);
        }
    }

    private void setupLayout(RadioStation station){
        setContentView(R.layout.radiostation_form);

        TextView title = findViewById(R.id.title_textview);
        Button confirmButton = findViewById(R.id.confirm_button);
        EditText name = findViewById(R.id.nameEdit);
        EditText stream = findViewById(R.id.streamEdit);
        EditText website = findViewById(R.id.websiteEdit);
        EditText logo = findViewById(R.id.logoEdit);

        title.setText(getString(R.string.titleEdit).replace("#id#", ""+station.uid));
        confirmButton.setText(R.string.saveChanges);
        name.setText(station.name);
        stream.setText(station.streamUrl);
        website.setText(station.websiteUrl);
        logo.setText(station.logoUrl);

        confirmButton.setOnClickListener((view -> {
            station.name = name.getText().toString();
            station.streamUrl = stream.getText().toString();
            station.websiteUrl = website.getText().toString();
            station.logoUrl = logo.getText().toString();

            AsyncTask.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                RadioStationDao radioStationDao = db.radioStationDao();
                radioStationDao.updateRadioStation(station);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("radioStation", station);
                setResult(Activity.RESULT_OK, returnIntent);

                //Synchron im MainThread UI updaten
                runOnUiThread(this::finish);
            });
        }));
    }
}
