package com.betravelsome.travelpack.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.betravelsome.travelpack.model.Trip;

import java.util.List;

@Dao
public interface TravelPackDao {

    @Query("SELECT * from trip_table ORDER BY trip_name")
    LiveData<List<Trip>> getAllTrips();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertTrip(Trip trip);

}
