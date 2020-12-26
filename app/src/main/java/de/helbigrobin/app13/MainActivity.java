package de.helbigrobin.app13;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;

public class MainActivity extends AppCompatActivity {

    List<RadioStation> radioStations = new ArrayList<>();

    ArrayAdapter<RadioStation> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMenus();
        setupDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.finishOption){
            finish();
        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }

    private void setupMenus(){
        ListView lv = findViewById(R.id.radio_menu);
        arrayAdapter = new ArrayAdapter<RadioStation>(this,
                android.R.layout.simple_list_item_1,
                radioStations);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, RadioStationActivity.class);
            intent.putExtra("radioStationUid", radioStations.get(position).uid);
            startActivity(intent);
        });
        registerForContextMenu(lv);
    }

    private void setupDatabase(){
        //Asynchron Datenbank anfragen
        AsyncTask.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            RadioStationDao radioStationDao = db.radioStationDao();
            radioStations.addAll(radioStationDao.getAll());

            //Synchron im MainThread UI updaten
            runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
        });
    }
}