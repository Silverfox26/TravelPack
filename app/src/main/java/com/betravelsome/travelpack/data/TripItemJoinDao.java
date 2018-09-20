package com.betravelsome.travelpack.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.TripItemJoin;

import java.util.List;

@Dao
public interface TripItemJoinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TripItemJoin tripItemJoin);

    @Query("SELECT * FROM item_table INNER JOIN trip_item_join_table ON " +
            "item_table.id=trip_item_join_table.item_id WHERE " +
            "trip_item_join_table.trip_id=:tripId")
    LiveData<List<Item>> getItemsForTrip(final int tripId);
}
