package com.betravelsome.travelpack;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.betravelsome.travelpack.data.TravelPackRepository;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.Trip;

import java.util.List;

public class TravelPackViewModel extends AndroidViewModel {

    // Member variable declarations
    private final TravelPackRepository mRepository;
    private LiveData<List<Trip>> mTrips;
    private LiveData<List<Item>> mItems;
    private LiveData<List<Item>> mTripItems = null;

    public TravelPackViewModel(Application application) {
        super(application);
        mRepository = new TravelPackRepository(application);
    }

//    public TravelPackViewModel(Application application, int tripId) {
//        super(application);
//        mRepository = new TravelPackRepository(application, tripId);
//    }

    public LiveData<List<Trip>> getAllTrips() {
        mTrips = mRepository.getAllTrips();
        return mTrips;
    }

    public void insertTrip(Trip trip) {
        mRepository.insertTrip(trip);
    }

    public LiveData<List<Item>> getAllItems() {
        mItems = mRepository.getAllItems();
        return mItems;
    }

    public void insertItem(Item item) {
        mRepository.insertItem(item);
    }

    public LiveData<List<Item>> getAllItemsForTrip(int tripId) {
        mItems = mRepository.getAllItemsForTrip(tripId);
        return mItems;
    }

    public void insertTripItemJoin(int tripId, int itemId) {
        mRepository.insertTripItemJoin(tripId, itemId);
    }

}
