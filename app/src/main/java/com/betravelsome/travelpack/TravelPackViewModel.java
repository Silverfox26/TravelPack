package com.betravelsome.travelpack;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.betravelsome.travelpack.data.TravelPackRepository;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.model.TripItemJoin;

import java.util.List;

public class TravelPackViewModel extends AndroidViewModel {

    // Member variable declarations
    private final TravelPackRepository mRepository;
    private LiveData<List<Trip>> mTrips;
    private LiveData<List<Item>> mItems;
    private LiveData<List<ItemPackingList>> mTripItems = null;

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

    public LiveData<List<ItemPackingList>> getAllItemsForTrip(int tripId) {
        mTripItems = mRepository.getAllItemsForTrip(tripId);
        return mTripItems;
    }

    public void insertTripItemJoin(int tripId, int itemId) {
        mRepository.insertTripItemJoin(tripId, itemId);
    }

    public Item getGearItemById(int itemId) {
        return mRepository.getGearItemById(itemId);
    }

    public void updateGearItem(Item item) {
        mRepository.updateGearItem(item);
    }

    public void updateTripItemAmount(TripItemJoin item) {
        mRepository.updateTripItemAmount(item);
    }

    public void deletePackingListItem(TripItemJoin item) {
        mRepository.deletePackingListItem(item);
    }

    public void deleteGearItem(Item item) {
        mRepository.deleteGearItem(item);
    }

    public void deleteTrip(Trip trip) {
        mRepository.deleteTrip(trip);
    }
}
