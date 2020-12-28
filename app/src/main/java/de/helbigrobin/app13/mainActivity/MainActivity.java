package de.helbigrobin.app13.mainActivity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import de.helbigrobin.app13.R;
import de.helbigrobin.app13.RadioStationActivity;
import de.helbigrobin.app13.RadioStationAddActivity;
import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;
import de.helbigrobin.app13.mainActivity.fragments.Configuration;
import de.helbigrobin.app13.mainActivity.fragments.ConfigureRadioStations;
import de.helbigrobin.app13.mainActivity.fragments.RadioStationList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Class<? extends Fragment>  currentlyShownFragmentClass;

    public List<RadioStation> radioStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDatabase();
        setupBurgerMenu();
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
        } else if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //Neue RadioStation
                RadioStation newStation = (RadioStation) intent.getSerializableExtra("radioStation");
                if (newStation != null){
                    AsyncTask.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        RadioStationDao radioStationDao = db.radioStationDao();
                        long newStationUid = radioStationDao.insertRadioStation(newStation);

                        //Ich hole den Eintrag, den ich gerade in die Datenbank geschrieben habe, um sicherzustellen, dass er dort nun auch vorhanden ist
                        RadioStation stationFromDatabase = radioStationDao.getById(newStationUid);
                        radioStations.add(stationFromDatabase);

                        //Derzeit angezeigtes Fragment neu laden
                        runOnUiThread(()->showFragment(currentlyShownFragmentClass));
                    });
                }
            }
        }
    }

    private void checkIfStationNeedsToBeOpened(){
        String playLastStationKey = getString(R.string.sharedPreferences_key_playLastStation);
        String playLastStationUIdKey = getString(R.string.sharedPreferences_key_playLastStation_uid);
        SharedPreferences prefs = getSharedPreferences(
                "de.helbigrobin.app13", Context.MODE_PRIVATE);

        boolean playLastStation = prefs.getBoolean(playLastStationKey, false);

        //Wenn Einstellung zum automatischen Öffnen der letzten Station aktiviert ist
        if (playLastStation){
            long playLastStationUId = prefs.getLong(playLastStationUIdKey, -1);
            //Wenn beim letzten Beenden der App eine Station offen war
            if(playLastStationUId > -1){
                for(RadioStation station : radioStations){
                    if(station.uid == playLastStationUId){
                        //Öffne Station automatisch
                        Intent intent = new Intent(getApplicationContext(), RadioStationActivity.class);
                        intent.putExtra("radioStation", station);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void setupBurgerMenu(){
        drawerLayout = findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.radioStations:
                    showFragment(RadioStationList.class);
                    break;
                case R.id.configureRadioStations:
                    showFragment(ConfigureRadioStations.class);
                    break;
                case R.id.configuration:
                    showFragment(Configuration.class);
                    break;
                case R.id.addRadioStation:
                    Intent intent = new Intent(getApplicationContext(), RadioStationAddActivity.class);
                    startActivityForResult(intent, 1);
                    break;
                default:
                    return true;
            }

            return true;
        });
    }

    private void setupDatabase(){
        //Asynchron Datenbank anfragen
        AsyncTask.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            RadioStationDao radioStationDao = db.radioStationDao();
            radioStations.addAll(radioStationDao.getAll());

            //Synchron im MainThread UI updaten
            runOnUiThread(() -> {
                showFragment(RadioStationList.class);
                checkIfStationNeedsToBeOpened();
            });
        });
    }

    private void showFragment(Class<? extends Fragment> fragmentClass){
        currentlyShownFragmentClass = fragmentClass;
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.mainActivity_fragment, fragmentClass, null)
                .commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
