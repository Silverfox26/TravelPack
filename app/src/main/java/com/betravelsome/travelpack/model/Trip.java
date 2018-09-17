package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trip_table")
public class Trip {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "trip_name")
    private String tripName;
    @ColumnInfo(name = "destination_id")
    private String destinationId;
    @ColumnInfo(name = "image_path")
    private String imagePath;

    /**
     * Trip constructor.
     *
     * @param id            Unique database Id of the trip.
     * @param tripName      Name of the trip.
     * @param destinationId Google Places Id for the destination of the trip.
     * @param imagePath     Path to the trip's image.
     */
    public Trip(int id, String tripName, String destinationId, String imagePath) {
        this.id = id;
        this.tripName = tripName;
        this.destinationId = destinationId;
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

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
