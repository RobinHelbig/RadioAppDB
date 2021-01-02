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