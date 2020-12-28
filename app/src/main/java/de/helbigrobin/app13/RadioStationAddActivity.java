package de.helbigrobin.app13;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;

public class RadioStationAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLayout();
    }

    private void setupLayout(){
        setContentView(R.layout.radiostation_form);

        TextView title = findViewById(R.id.title_textview);
        Button confirmButton = findViewById(R.id.confirm_button);
        EditText name = findViewById(R.id.nameEdit);
        EditText stream = findViewById(R.id.streamEdit);
        EditText website = findViewById(R.id.websiteEdit);
        EditText logo = findViewById(R.id.logoEdit);

        title.setText(R.string.titleAdd);
        confirmButton.setText(R.string.saveChanges);

        confirmButton.setOnClickListener((view -> {
            String nameString = name.getText().toString();
            String streamUrl = stream.getText().toString();
            String websiteUrl = website.getText().toString();
            String logoUrl = logo.getText().toString();

            RadioStation newStation = new RadioStation(nameString, streamUrl, websiteUrl, logoUrl);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("radioStation", newStation);
            setResult(Activity.RESULT_OK, returnIntent);

            //Synchron im MainThread UI updaten
            finish();
        }));
    }
}
