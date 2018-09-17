package com.betravelsome.travelpack.data;

import android.app.Application;

public class TravelPackRepository {

    // Member variable declarations
    private final TravelPackDao mTravelPackDao;

    public TravelPackRepository(Application application) {
        TravelPackRoomDatabase db = TravelPackRoomDatabase.getDatabase(application);
        mTravelPackDao = db.travelPackDao();
    }
}
