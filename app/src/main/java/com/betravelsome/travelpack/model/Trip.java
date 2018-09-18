package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trip_table")
public class Trip {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "trip_name")
    private String tripName;
    @ColumnInfo(name = "trip_place_id")
    private String tripPlaceId;
    @ColumnInfo(name = "trip_image_path")
    private String tripImagePath;

    /**
     * Trip constructor
     *
     * @param id
     * @param tripName
     * @param tripPlaceId
     * @param tripImagePath
     */
    public Trip(int id, String tripName, String tripPlaceId, String tripImagePath) {
        this.id = id;
        this.tripName = tripName;
        this.tripPlaceId = tripPlaceId;
        this.tripImagePath = tripImagePath;
    }

    /**
     * Trip constructor
     *
     * @param tripName
     * @param tripPlaceId
     * @param tripImagePath
     */
    @Ignore
    public Trip(String tripName, String tripPlaceId, String tripImagePath) {
        this.tripName = tripName;
        this.tripPlaceId = tripPlaceId;
        this.tripImagePath = tripImagePath;
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

    public String getTripPlaceId() {
        return tripPlaceId;
    }

    public void setTripPlace(String tripPlaceId) {
        this.tripPlaceId = tripPlaceId;
    }

    public String getTripImagePath() {
        return tripImagePath;
    }

    public void setTripImagePath(String tripImagePath) {
        this.tripImagePath = tripImagePath;
    }
}
