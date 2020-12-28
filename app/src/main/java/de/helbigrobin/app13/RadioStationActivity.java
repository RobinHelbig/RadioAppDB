package de.helbigrobin.app13;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.helbigrobin.app13.database.AppDatabase;
import de.helbigrobin.app13.database.RadioStation;
import de.helbigrobin.app13.database.RadioStationDao;

public class RadioStationActivity extends AppCompatActivity {
    public IRadioService radioService;

    Button startButton, stopButton;
    WebView webView;
    RadioServiceConnection con;
    RadioStateUpdateReceiver radioStateUpdateReceiver;
    WifiStateUpdateReceiver wifiStateUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        int radioStationUid = intent.getIntExtra("radioStationUid", -1);
        if (radioStationUid >= 0){
            Intent serviceIntent = new Intent(this, RadioService.class);
            serviceIntent.putExtra("radioStationUid", radioStationUid);
            startService(serviceIntent);
            con = new RadioServiceConnection();
            bindService(serviceIntent, con, Context.BIND_AUTO_CREATE);
            setContentView(R.layout.activity_radio);

            //BroadcastReceiver registrieren
            radioStateUpdateReceiver = new RadioStateUpdateReceiver();
            registerReceiver(radioStateUpdateReceiver, new IntentFilter("RADIO_STATE_UPDATE"));

            wifiStateUpdateReceiver = new WifiStateUpdateReceiver();
            registerReceiver(wifiStateUpdateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            startButton = findViewById(R.id.startButton);
            stopButton = findViewById(R.id.stopButton);
            webView = findViewById(R.id.webview);

            showRadioWebsite(radioStationUid);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //Broadcast Receiver freigeben
        unregisterReceiver(radioStateUpdateReceiver);
        unregisterReceiver(wifiStateUpdateReceiver);

        if (radioService != null){
            Intent serviceIntent = new Intent(this, RadioService.class);
            stopService(serviceIntent);
            unbindService(con);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) { //Wenn geht in Webview zurück navigieren
            webView.goBack();
        } else {
            super.onBackPressed(); //Ansonsten Action verlassen
        }
    }

    public void onStartButtonClick(View view){
        if(radioService != null){
            if(radioService.getState() == RadioService.RadioState.Stopped){
                radioService.startRadio();
            } else if (radioService.getState() == RadioService.RadioState.Paused){
                radioService.continueRadio();
            } else if(radioService.getState() == RadioService.RadioState.Playing){
                radioService.pauseRadio();
            }
        }
    }

    public void onStopButtonClick(View view){
        if(radioService != null){
            //Im State Stopped kann der Stop Button nicht geklickt werden. Deshalb wird dieser Fall hier nicht betrachtet

            if(radioService.getState() == RadioService.RadioState.Paused || radioService.getState() == RadioService.RadioState.Playing){
                radioService.stopRadio();
            }
        }
    }


    private void showRadioWebsite(int radioStationUid){
        Log.i("statusInfo", "Website " + radioStationUid);

        //Asynchron RadioStation aus Datenbank abfragen
        AsyncTask.execute(() -> {
            RadioStation radioStation = AppDatabase.getInstance(this).radioStationDao().getById(radioStationUid);

            //Synchron im MainThread weiterarbeiten
            runOnUiThread(() -> {
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(radioStation.websiteUrl);
            });
        });
    }

    class RadioServiceConnection implements ServiceConnection {
         boolean bound = false;
         @Override
         public void onServiceConnected (ComponentName arg0, IBinder arg1) {
             radioService  = ((RadioService.RadioBinder) arg1).getRadioService();
             bound = true;
         }

        @Override
         public void onServiceDisconnected(ComponentName arg0) {
             bound = false;
         }
    }

    public class RadioStateUpdateReceiver extends BroadcastReceiver {

        /*
        Der RadioStateUpdateReceiver wird informiert, wenn der Status des RadioService verändert wird (Stopped, Playing, Paused).
        Das muss passieren, da das Pausieren/Weiterspielen und Stoppen über die ForegroundService Notification direkt den Service ansteuert. Ohne den anschließenden Broadcast würde die RadioStationActivity das nicht mitbekommen und ihre Buttons nicht anpassen.
         */

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("RADIO_STATE_UPDATE"))
            {
                int state = intent.getIntExtra("state",-1);
                if(state >= 0 && state <= 2){
                    RadioService.RadioState radioState = RadioService.RadioState.values()[state];
                    if(radioState == RadioService.RadioState.Stopped){
                        stopButton.setEnabled(false);
                        startButton.setText(getString(R.string.start));
                    } else if(radioState == RadioService.RadioState.Paused){
                        stopButton.setEnabled(true);
                        startButton.setText(getString(R.string.weiterspielen));
                    } else if(radioState == RadioService.RadioState.Playing){
                        stopButton.setEnabled(true);
                        startButton.setText(getString(R.string.pause));
                    }
                }
            }
        }
    }

    public class WifiStateUpdateReceiver extends BroadcastReceiver {

        /*
        Der WifiStateUpdateReceiver wird informiert wenn sich der Wifi Status aktualisiert.
         */

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if(noConnectivity){ //Wenn keine Verbindung, dann Activity beenden (Radio Stop in onDestroy)
                    finish();
                }
            }
        }
    }
}