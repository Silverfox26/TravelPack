package com.betravelsome.travelpack.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.betravelsome.travelpack.model.Trip;

import java.util.List;

public class TravelPackRepository {

    // Member variable declarations
    private final TravelPackDao mTravelPackDao;
    private final LiveData<List<Trip>> mTrips;

    public TravelPackRepository(Application application) {
        TravelPackRoomDatabase db = TravelPackRoomDatabase.getDatabase(application);
        mTravelPackDao = db.travelPackDao();
        mTrips = mTravelPackDao.getAllTrips();
    }

    // Wrapper to get the list of all trips.
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Trip>> getAllTrips() {
        return mTrips;
    }

    // Wrapper to insert a trip into the db
    public void insertTrip(Trip trip) {
        mTravelPackDao.insertTrip(trip);
    }
}
