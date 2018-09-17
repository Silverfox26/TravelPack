package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.betravelsome.travelpack.data.Converters;
import com.google.android.gms.location.places.Place;

@Entity(tableName = "trip_table")
@TypeConverters({Converters.class})
public class Trip {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "trip_name")
    private String tripName;
    @ColumnInfo(name = "trip_place")
    private Place tripPlace;
    @ColumnInfo(name = "image_path")
    private String imagePath;

    /**
     * Trip constructor
     *
     * @param id
     * @param tripName
     * @param tripPlace
     * @param imagePath
     */
    public Trip(int id, String tripName, Place tripPlace, String imagePath) {
        this.id = id;
        this.tripName = tripName;
        this.tripPlace = tripPlace;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Place getTripPlace() {
        return tripPlace;
    }

    public void setTripPlace(Place tripPlace) {
        this.tripPlace = tripPlace;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
