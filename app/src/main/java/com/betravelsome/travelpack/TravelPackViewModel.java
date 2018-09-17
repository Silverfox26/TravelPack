package com.betravelsome.travelpack;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.betravelsome.travelpack.data.TravelPackRepository;
import com.betravelsome.travelpack.model.Trip;

import java.util.List;

public class TravelPackViewModel extends AndroidViewModel {

    // Member variable declarations
    private final TravelPackRepository mRepository;
    private LiveData<List<Trip>> mTrips;

    public TravelPackViewModel(Application application) {
        super(application);
        mRepository = new TravelPackRepository(application);
    }

    public LiveData<List<Trip>> getAllTrips() {
        mTrips = mRepository.getAllTrips();
        return mTrips;
    }

    public void insertTrip(Trip trip) {
        mRepository.insertTrip(trip);
    }
}
