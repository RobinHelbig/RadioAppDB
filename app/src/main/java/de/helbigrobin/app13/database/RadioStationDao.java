package de.helbigrobin.app13.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RadioStationDao {
    @Query("SELECT uid, name, stream_url, website_url, logo_url, favourite FROM radiostation")
    List<RadioStation> getAll();

    @Query("SELECT uid, name, stream_url, website_url, logo_url, favourite FROM radiostation WHERE uid = :uid")
    RadioStation getById(Integer uid);

    @Insert
    void insertRadioStation(RadioStation radioStation);

    @Update
    void updateRadioStation(RadioStation radioStation);

    @Delete
    void deleteRadioStation(RadioStation radioStation);
}
