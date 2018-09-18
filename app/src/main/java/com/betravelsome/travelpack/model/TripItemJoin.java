package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = "trip_item_join_table",
        primaryKeys = {"trip_Id", "itemId"},
        foreignKeys = {
            @ForeignKey(entity = Trip.class,
                        parentColumns = "id",
                        childColumns = "tripId"),
            @ForeignKey(entity = Item.class,
                        parentColumns = "id",
                        childColumns = "itemId")
        })
public class TripItemJoin {

    @ColumnInfo(name = "trip_id")
    private final int tripId;
    @ColumnInfo(name = "item_id")
    private final int itemId;

    public TripItemJoin(final int tripId, final int itemId) {
        this.tripId = tripId;
        this.itemId = itemId;
    }
}
