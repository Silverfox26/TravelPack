package com.betravelsome.travelpack.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.betravelsome.travelpack.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * from item_table ORDER BY item_name")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * from item_table WHERE id = :id")
    Item getItemById(int id);

    @Update()
    void update(Item item);

    @Insert()
    void insertItem(Item item);

    @Delete
    void deleteItem(Item item);
}
