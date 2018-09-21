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

    public TravelPackViewModel(Application application) {
        super(application);
        mRepository = new TravelPackRepository(application);
    }

    public LiveData<List<Trip>> getAllTrips() {
        return mRepository.getAllTrips();
    }

    public void insertTrip(Trip trip) {
        mRepository.insertTrip(trip);
    }

    public LiveData<List<Item>> getAllItems() {
        return mRepository.getAllItems();
    }

    public void insertItem(Item item) {
        mRepository.insertItem(item);
    }

    public LiveData<List<ItemPackingList>> getAllItemsForTrip(int tripId) {
        return mRepository.getAllItemsForTrip(tripId);
    }

    public void insertTripItemJoin(int tripId, int itemId) {
        mRepository.insertTripItemJoin(tripId, itemId);
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
