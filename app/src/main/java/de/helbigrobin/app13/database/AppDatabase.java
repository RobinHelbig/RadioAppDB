package de.helbigrobin.app13.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {RadioStation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract RadioStationDao radioStationDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "radioAppDB").createFromAsset("databases/radioAppDB").build();
        }
        return instance;
    }
}



//                        RadioStationDao radioStationDao = instance.radioStationDao();
//                        RadioStation live1 = new RadioStation(context.getString(R.string.name_1live), context.getString(R.string.radio_1live), context.getString(R.string.website_1live), context.getString(R.string.logo_1live));
//                        RadioStation live1Diggi = new RadioStation(context.getString(R.string.name_1livediggi), context.getString(R.string.radio_1livediggi), context.getString(R.string.website_1livediggi), context.getString(R.string.logo_1livediggi));
//                        RadioStation wdr2 = new RadioStation(context.getString(R.string.name_wdr2), context.getString(R.string.radio_wdr2), context.getString(R.string.website_wdr2), context.getString(R.string.logo_wdr2));
//                        RadioStation wdr4 = new RadioStation(context.getString(R.string.name_wdr4), context.getString(R.string.radio_wdr4), context.getString(R.string.website_wdr4), context.getString(R.string.logo_wdr4));
//                        radioStationDao.insertRadioStation(live1);
//                        radioStationDao.insertRadioStation(live1Diggi);
//                        radioStationDao.insertRadioStation(wdr2);
//                        radioStationDao.insertRadioStation(wdr4);