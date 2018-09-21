package com.betravelsome.travelpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.betravelsome.travelpack.adapters.GearItemAdapter;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.betravelsome.travelpack.utilities.RecyclerViewItemTouchHelper;

import java.util.List;
import java.util.Objects;

public class GearListActivity extends AppCompatActivity implements GearItemAdapter.GearItemAdapterOnClickHandler, RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener {

    private TravelPackViewModel mTravelPackViewModel;
    private GearItemAdapter mItemAdapter;

    private boolean isItemPickActivity = false;
    private int mTripId = -1;
    private int mClickedItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Start EditTrip Activity
            Intent addItemIntend = new Intent(GearListActivity.this, EditGearActivity.class);
            startActivity(addItemIntend);
        });

        // Get the intent that started the activity, receive the tripId and set the list to pickable;
        Intent intent = getIntent();
        if (intent.hasExtra("TRIP_ID_EXTRA")) {
            mTripId = intent.getIntExtra("TRIP_ID_EXTRA", -1);

            isItemPickActivity = true;
        }

        // Create LayoutManager
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Configuring the RecyclerView and setting its adapter
        RecyclerView itemRecyclerView = findViewById(R.id.recyclerViewGearItems);
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
        Observer<List<Item>> mObserver = mItemAdapter::setGearItemData;

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
        // if the activity was called from the packing lsit activity, return the selected item
        // by using an intent
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Deleting this item will remove it from all your trips too!")
                .setTitle("Delete?");

        builder.setPositiveButton("DELETE", (dialog, id) -> {
            Item itemToDelete = mItemAdapter.getItemByPosition(position);
            AppExecutors.getInstance().diskIO().execute(() -> GearListActivity.this.mTravelPackViewModel.deleteGearItem(itemToDelete));
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> GearListActivity.this.mItemAdapter.notifyDataSetChanged());

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
