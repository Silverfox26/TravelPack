package com.betravelsome.travelpack.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "item_table")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "item_name")
    private String itemName;
    @ColumnInfo(name = "item_weight")
    private float itemWeight;
    @ColumnInfo(name = "item_image_path")
    private String itemImagePath;

    /**
     * Item Constructor
     */
    public Item(int id, String itemName, float itemWeight, String itemImagePath) {
        this.id = id;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemImagePath = itemImagePath;
    }

    /**
     * Item Constructor
     */
    @Ignore
    public Item(String itemName, float itemWeight, String itemImagePath) {
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemImagePath = itemImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
