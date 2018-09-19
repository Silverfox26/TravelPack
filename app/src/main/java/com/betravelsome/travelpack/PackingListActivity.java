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

import com.betravelsome.travelpack.adapters.PackingListAdapter;
import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;

import java.util.List;

public class PackingListActivity extends AppCompatActivity implements PackingListAdapter.PackingListAdapterOnClickHandler {

    private List<Item> mItems = null;
    private TravelPackViewModel mTravelPackViewModel;
    private PackingListAdapter mPackingListAdapter;
    private Observer<List<Item>> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView packingListRecyclerView = findViewById(R.id.recyclerViewPackingList);

        // Configuring the RecyclerView and setting its adapter
        packingListRecyclerView.setLayoutManager(layoutManager);
        packingListRecyclerView.setHasFixedSize(true);
        mPackingListAdapter = new PackingListAdapter(this, this);
        packingListRecyclerView.setAdapter(mPackingListAdapter);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the trips in the Adapter.
        mObserver = mPackingListAdapter::setPackingListData;

        // Get the intent, check its content, and populate the UI with its data
        Intent intent = getIntent();
        if (intent.hasExtra("TRIP_ID_EXTRA")) {
            int tripId = intent.getIntExtra("TRIP_ID_EXTRA", -1);

            mTravelPackViewModel.getAllItemsForTrip(tripId).observe(this, mObserver);
        }
    }

    @Override
    public void onClick(View v, int clickedPackingListItemId) {

    }
}
