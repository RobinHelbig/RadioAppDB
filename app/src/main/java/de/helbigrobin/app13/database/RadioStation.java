package de.helbigrobin.app13.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class RadioStation implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "stream_url")
    public String streamUrl;

    @ColumnInfo(name = "website_url")
    public String websiteUrl;

    @ColumnInfo(name = "logo_url")
    public String logoUrl;

    @ColumnInfo(name = "favourite")
    public Boolean favourite;

    public RadioStation(String name, String streamUrl, String websiteUrl, String logoUrl){
        this.name = name;
        this.streamUrl = streamUrl;
        this.websiteUrl = websiteUrl;
        this.logoUrl = logoUrl;
        this.favourite = false;
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }
}
