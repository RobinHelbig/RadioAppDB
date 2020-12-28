package de.helbigrobin.app13.mainActivity;

import android.content.Intent;
import android.os.AsyncTask;
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
//                    showFragment(AddRadioStation.class);
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
            });
        });
    }

    private void showFragment(Class<? extends Fragment> fragmentClass){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.mainActivity_fragment, fragmentClass, null)
                .commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
