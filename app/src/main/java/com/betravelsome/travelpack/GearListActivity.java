package com.betravelsome.travelpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.betravelsome.travelpack.adapters.GearItemAdapter;
import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.Trip;

import java.util.List;

public class GearListActivity extends AppCompatActivity implements GearItemAdapter.GearItemAdapterOnClickHandler {

    private List<Item> mItems = null;
    private TravelPackViewModel mTravelPackViewModel;
    private GearItemAdapter mItemAdapter;
    private Observer<List<Item>> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EditTrip Activity
                Intent addItemIntend = new Intent(GearListActivity.this, EditGearActivity.class);
                startActivity(addItemIntend);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView itemRecyclerView = findViewById(R.id.recyclerViewGearItems);

        // Configuring the RecyclerView and setting its adapter
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerView.setHasFixedSize(true);
        mItemAdapter = new GearItemAdapter(this, this);
        itemRecyclerView.setAdapter(mItemAdapter);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the items in the Adapter.
        mObserver = mItemAdapter::setGearItemData;

        mTravelPackViewModel.getAllItems().observe(this, mObserver);
    }

    @Override
    public void onClick(View v, int clickedItemId) {

    }
}
