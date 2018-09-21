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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.betravelsome.travelpack.adapters.GearItemAdapter;
import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.betravelsome.travelpack.utilities.RecyclerViewItemTouchHelper;

import java.util.List;

public class GearListActivity extends AppCompatActivity implements GearItemAdapter.GearItemAdapterOnClickHandler, RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener {

    private List<Item> mItems = null;
    private TravelPackViewModel mTravelPackViewModel;
    private GearItemAdapter mItemAdapter;
    private Observer<List<Item>> mObserver;

    private boolean isItemPickActivity = false;
    private int mTripId = -1;
    private int mClickedItemId = -1;

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

        // Get the intent, receive the tripId and set the list to pickable;
        Intent intent = getIntent();
        if (intent.hasExtra("TRIP_ID_EXTRA")) {
            mTripId = intent.getIntExtra("TRIP_ID_EXTRA", -1);

            isItemPickActivity = true;
        }

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView itemRecyclerView = findViewById(R.id.recyclerViewGearItems);

        // Configuring the RecyclerView and setting its adapter
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerView.setHasFixedSize(true);
        mItemAdapter = new GearItemAdapter(this, this);
        itemRecyclerView.setAdapter(mItemAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(itemRecyclerView);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the items in the Adapter.
        mObserver = mItemAdapter::setGearItemData;

        mTravelPackViewModel.getAllItems().observe(this, mObserver);
    }

    @Override
    public void onClick(View v, int clickedItemId) {
        if (isItemPickActivity) {
            mClickedItemId = clickedItemId;
            finish();
        } else {
            // Start Edit Gear Activity for the selected Trip
            Intent editGearItemIntend = new Intent(GearListActivity.this, EditGearActivity.class);
            editGearItemIntend.putExtra("GEAR_ITEM_ID_EXTRA", clickedItemId);
            startActivity(editGearItemIntend);
        }
    }

    @Override
    public void finish() {
        if (mTripId != -1 && mClickedItemId != -1) {
            // Prepare data intent
            Intent data = new Intent();
            data.putExtra("TRIP_ID_EXTRA", mTripId);
            data.putExtra("ITEM_ID_EXTRA", mClickedItemId);
            // Activity finished ok, return the data
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Item itemToDelete = mItemAdapter.getItemByPosition(position);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.deleteGearItem(itemToDelete));
    }
}
