package com.betravelsome.travelpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.betravelsome.travelpack.adapters.PackingListAdapter;
import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.model.TripItemJoin;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.betravelsome.travelpack.utilities.RecyclerViewItemTouchHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.List;
import java.util.Objects;

public class PackingListActivity extends AppCompatActivity implements PackingListAdapter.PackingListAdapterOnClickHandler, RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener {

    private static final String TAG = "CLICK";

    private static int GEAR_PICKER_REQUEST = 1;

    private List<Item> mItems = null;
    private TravelPackViewModel mTravelPackViewModel;
    private PackingListAdapter mPackingListAdapter;
    private Observer<List<ItemPackingList>> mObserver;
    private RecyclerView packingListRecyclerView;

    private int mTripId = -1;

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
                // Start Pick Gear List Activity to select a gear item to be added to the packing list
                Intent packingListIntend = new Intent(PackingListActivity.this, GearListActivity.class);
                packingListIntend.putExtra("TRIP_ID_EXTRA", mTripId);
                startActivityForResult(packingListIntend, GEAR_PICKER_REQUEST);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        packingListRecyclerView = findViewById(R.id.recyclerViewPackingList);

        // Configuring the RecyclerView and setting its adapter
        packingListRecyclerView.setLayoutManager(layoutManager);
        packingListRecyclerView.setHasFixedSize(true);
        mPackingListAdapter = new PackingListAdapter(this, this);
        packingListRecyclerView.setAdapter(mPackingListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(packingListRecyclerView);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the trips in the Adapter.
        mObserver = mPackingListAdapter::setPackingListData;

        // Get the intent, check its content, and populate the UI with its data
        Intent intent = getIntent();
        if (intent.hasExtra("TRIP_ID_EXTRA")) {
            mTripId = intent.getIntExtra("TRIP_ID_EXTRA", -1);

            mTravelPackViewModel.getAllItemsForTrip(mTripId).observe(this, mObserver);
        }
    }

    @Override
    public void onClick(View v, int clickedPackingListItemId) {
    }

    @Override
    public void onPlusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount) {
        TripItemJoin item = new TripItemJoin(clickedPackingListTripId, clickedPackingListItemId, clickedItemAmount + 1 );
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.updateTripItemAmount(item));
    }

    @Override
    public void onMinusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount) {
        TripItemJoin item = new TripItemJoin(clickedPackingListTripId, clickedPackingListItemId, clickedItemAmount - 1 );
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.updateTripItemAmount(item));
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GEAR_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                int itemId = data.getIntExtra("ITEM_ID_EXTRA", -1);
                mTripId = data.getIntExtra("TRIP_ID_EXTRA", -1);

                AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.insertTripItemJoin(mTripId, itemId));
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        TripItemJoin itemToDelete = mPackingListAdapter.removeItem(position);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.deletePackingListItem(itemToDelete));
    }

}
