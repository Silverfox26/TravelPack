package com.betravelsome.travelpack.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.model.TripItemJoin;

import java.util.List;

public class TravelPackRepository {

    // Member variable declarations
    private final TripDao mTripDao;
    private final ItemDao mItemDao;
    private final TripItemJoinDao mTripItemJoinDao;
    private final LiveData<List<Trip>> mTrips;
    private final LiveData<List<Item>> mItems;
    private LiveData<List<ItemPackingList>> mTripItems;

    public TravelPackRepository(Application application) {
        TravelPackRoomDatabase db = TravelPackRoomDatabase.getDatabase(application);
        mTripDao = db.travelPackDao();
        mItemDao = db.ItemDao();
        mTripItemJoinDao = db.tripItemJoinDao();

        mTrips = mTripDao.getAllTrips();
        mItems = mItemDao.getAllItems();
        mTripItems = null;
    }

    // Wrapper to get the list of all trips.
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Trip>> getAllTrips() {
        return mTrips;
    }

    // Wrapper to insert a trip into the db
    public void insertTrip(Trip trip) {
        mTripDao.insertTrip(trip);
    }

    // Wrapper to get the list of all items.
    public LiveData<List<Item>> getAllItems() {
        return mItems;
    }

    // Wrapper to insert an item into the db
    public void insertItem(Item item) {
        mItemDao.insertItem(item);
    }

    // Wrapper to get the list of all items for a trip.
    public LiveData<List<ItemPackingList>> getAllItemsForTrip(int tripId) {
        mTripItems = mTripItemJoinDao.getItemsForTrip(tripId);
        return mTripItems;
    }

    // Wrapper to insert a trip item join into the db
    public void insertTripItemJoin(int tripId, int itemId) {
        mTripItemJoinDao.insert(new TripItemJoin(tripId, itemId));
    }

    // Wrapper to update a gear item
    public void updateGearItem(Item item) {
        mItemDao.update(item);
    }

    // Wrapper to update a gear item
    public void updateTripItemAmount(TripItemJoin item) {
        mTripItemJoinDao.update(item);
    }

    // Wrapper to delete a gear item from a packing list
    public void deletePackingListItem(TripItemJoin item) {
        mTripItemJoinDao.deletePackingListItem(item);
    }

    // Wrapper to delete an item from the gear list
    public void deleteGearItem(Item item) {
        mItemDao.deleteItem(item);
    }

    // Wrapper to delete a trip from the trip list
    public void deleteTrip(Trip trip) {
        mTripDao.deleteTrip(trip);
    }
}
