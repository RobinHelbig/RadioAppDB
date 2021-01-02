package de.helbigrobin.app13;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import de.helbigrobin.app13.database.RadioStation;

public class RadioService extends Service implements IRadioService {
    RadioStation radioStation = null;
    MediaPlayer mediaPlayer;
    RadioBinder binder = new RadioBinder();
    RadioState state = RadioState.Playing;
    RemoteViews notificationContentView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action != null){
            //Hier werden die Button OnClicks der ForegroundService Notification verarbeitet

            if(action.equals("start")){
                if(getState() == RadioState.Stopped){
                    startRadio();
                } else if (getState() == RadioState.Paused){
                    continueRadio();
                } else if(getState() == RadioState.Playing){
                    pauseRadio();
                }
            } else if (action.equals("stop")) {
                if(getState() == RadioState.Paused || getState() == RadioState.Playing){
                    stopRadio();
                }
            }
        }
       return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        RadioStation radioStation = (RadioStation) intent.getSerializableExtra("radioStation");
        if (radioStation != null) {
            this.radioStation = radioStation;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            String source = this.radioStation.streamUrl;

            try {
                mediaPlayer.setDataSource(source);
            } catch(Exception e){
                Log.i("statusInfo", "Error: " + e.getMessage());
            }

            startRadio();
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.radioStation = null;
        setState(RadioState.Stopped);
        mediaPlayer.release();
        stopForeground(true);
        return true;
    }

    @Override
    public RadioState getState() {
        return state;
    }

    public void setState(RadioState state){
        this.state = state;

        //Beim Pausieren und Weiterspielen des Streams den Pause/Weiterspielen Text im Button der ForegroundService Notification passend setzen
        if(notificationContentView != null && (getState() == RadioState.Paused || getState() == RadioState.Playing)){
            if (getState() == RadioState.Paused){
                notificationContentView.setTextViewText(R.id.notifcationStartButton, getString(R.string.weiterspielen));
            } else if(getState() == RadioState.Playing){
                notificationContentView.setTextViewText(R.id.notifcationStartButton, getString(R.string.pause));
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setContent(notificationContentView)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(1, builder.build()); //Buttons der Notification Updaten
        }

        //RadioStationActivity informieren über neuen Zustand
        Intent sendLevel = new Intent();
        sendLevel.setAction("RADIO_STATE_UPDATE");
        sendLevel.putExtra( "state", getState().ordinal());
        sendBroadcast(sendLevel);
    }

    @Override
    public void startRadio() {
        Log.i("statusInfo", "Start " + radioStation.uid);

        setState(RadioState.Playing);

        displayForegroundMessage();

        try{
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
        } catch(Exception e){
            Log.i("statusInfo", "Error: " + e.getMessage());
        }
    }

    @Override
    public void stopRadio() {
        Log.i("statusInfo", "Stop");
        stopForeground(true);
        setState(RadioState.Stopped);
        mediaPlayer.stop();
    }

    @Override
    public void pauseRadio() {
        Log.i("statusInfo", "Pause");
        setState(RadioState.Paused);
        mediaPlayer.pause();
    }

    @Override
    public void continueRadio() {
        Log.i("statusInfo", "Continue");
        setState(RadioState.Playing);
        mediaPlayer.start();
    }

    void displayForegroundMessage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Für neue Android Versionen muss ein Channel erstellt werden
            CharSequence name = "RadioChannel";
            String description = "Für Radio Messages";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        //Remote View erstellen und mit Infos füllen
        notificationContentView = new RemoteViews(getPackageName(), R.layout.notification);
        notificationContentView.setTextViewText(R.id.notifcationStartButton, getString(R.string.pause));
        notificationContentView.setTextViewText(R.id.notifcationStopButton, getString(R.string.stop));

        //Intent zum Weiterspielen/Pausieren des Streams (ruft den Service mit Action auf)
        Intent startIntent = new Intent(getApplicationContext(), RadioService.class);
        startIntent.setAction("start");
        PendingIntent startPendingIntent = PendingIntent.getService(getApplicationContext(), 0, startIntent, 0);
        notificationContentView.setOnClickPendingIntent(R.id.notifcationStartButton, startPendingIntent);

        //Intent zum Stoppen des Streams (ruft den Service mit Action auf)
        Intent stopIntent = new Intent(getApplicationContext(), RadioService.class);
        stopIntent.setAction("stop");
        PendingIntent stopPendingIntent = PendingIntent.getService(getApplicationContext(), 0, stopIntent, 0);
        notificationContentView.setOnClickPendingIntent(R.id.notifcationStopButton, stopPendingIntent);

        //Intent zum Öffnen der RadioStationActivity (wenn neben die Buttons geklickt wird)
        Intent openIntent = new Intent(getApplicationContext(), RadioStationActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openIntent, 0);
        notificationContentView.setOnClickPendingIntent(R.id.notificationMain, openPendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setContent(notificationContentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        //Bild der Station setzen
        Picasso.get().load(radioStation.logoUrl).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(notificationContentView, R.id.notifcationImageView, 1, notification);

        startForeground(1, notification);
    }

    public class RadioBinder extends Binder {
        public IRadioService getRadioService(){
            return RadioService.this;
        }
    }

    public enum RadioState{
        Stopped, Playing, Paused
    }
}

