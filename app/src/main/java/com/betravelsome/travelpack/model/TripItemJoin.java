package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;

@Entity(tableName = "trip_item_join_table",
        primaryKeys = {"trip_id", "item_id"},
        foreignKeys = {
            @ForeignKey(entity = Trip.class,
                        parentColumns = "id",
                        childColumns = "trip_id"),
            @ForeignKey(entity = Item.class,
                        parentColumns = "id",
                        childColumns = "item_id")
        },
        indices = {@Index("item_id")})
public class TripItemJoin {

    @ColumnInfo(name = "trip_id")
    private final int tripId;
    @ColumnInfo(name = "item_id")
    private final int itemId;
    @ColumnInfo(name = "item_amount")
    private int itemAmount;

    public TripItemJoin(final int tripId, final int itemId) {
        this.tripId = tripId;
        this.itemId = itemId;
        this.itemAmount = 1;
    }

    @Ignore
    public TripItemJoin(final int tripId, final int itemId, final int amount) {
        this.tripId = tripId;
        this.itemId = itemId;
        this.itemAmount = amount;
    }

    public int getTripId() {
        return tripId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }
}
