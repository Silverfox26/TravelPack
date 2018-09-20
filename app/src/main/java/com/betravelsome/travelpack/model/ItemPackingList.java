package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

public class ItemPackingList {

    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "trip_id")
    private int tripId;
    @ColumnInfo(name = "item_id")
    private int itemId;


    @ColumnInfo(name = "item_name")
    private String itemName;
    @ColumnInfo(name = "item_weight")
    private float itemWeight;
    @ColumnInfo(name = "item_image_path")
    private String itemImagePath;

    @ColumnInfo(name = "item_amount")
    private int itemAmount;

    public ItemPackingList(int id, int tripId, int itemId, String itemName, float itemWeight, String itemImagePath, int itemAmount) {
        this.id = id;
        this.tripId = tripId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemImagePath = itemImagePath;
        this.itemAmount = itemAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(float itemWeight) {
        this.itemWeight = itemWeight;
    }

    public String getItemImagePath() {
        return itemImagePath;
    }

    public void setItemImagePath(String itemImagePath) {
        this.itemImagePath = itemImagePath;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }
}
