package de.helbigrobin.app13;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.helbigrobin.app13.database.RadioStation;

public class RadioStationAddActivity extends AppCompatActivity {
    EditText name, stream, website, logo;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLayout();
    }

    private void setupLayout(){
        setContentView(R.layout.radiostation_form);

        TextView title = findViewById(R.id.title_textview);
        confirmButton = findViewById(R.id.confirm_button);
        name = findViewById(R.id.nameEdit);
        stream = findViewById(R.id.streamEdit);
        website = findViewById(R.id.websiteEdit);
        logo = findViewById(R.id.logoEdit);

        title.setText(R.string.titleAdd);
        confirmButton.setText(R.string.addStation);
        //Button zuerst disabled, weil alle Felder noch leer sind
        confirmButton.setEnabled(false);

        TextChangeListener textChangeListener = new TextChangeListener();
        name.addTextChangedListener(textChangeListener);
        stream.addTextChangedListener(textChangeListener);
        website.addTextChangedListener(textChangeListener);
        logo.addTextChangedListener(textChangeListener);

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

    private class TextChangeListener implements TextWatcher{
        public void afterTextChanged(Editable s) {
            //Jedes Feld der RadioStation muss einen Wert haben, um sie abspeichern zu können
            confirmButton.setEnabled(!name.getText().toString().isEmpty() &&
                    !stream.getText().toString().isEmpty() &&
                    !website.getText().toString().isEmpty() &&
                    !logo.getText().toString().isEmpty());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
