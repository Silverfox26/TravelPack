package com.betravelsome.travelpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.betravelsome.travelpack.adapters.PackingListAdapter;
import com.betravelsome.travelpack.data.TravelPackRoomDatabase;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.PackingListForWidget;
import com.betravelsome.travelpack.model.TripItemJoin;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.betravelsome.travelpack.utilities.RecyclerViewItemTouchHelper;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PackingListActivity extends AppCompatActivity implements PackingListAdapter.PackingListAdapterOnClickHandler,
        PackingListAdapter.PackingListAdapterGetWeightSumOnDataChanged,
        RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener,
        OnMapReadyCallback {

    private static final int GEAR_PICKER_REQUEST = 1;

    private TravelPackViewModel mTravelPackViewModel;
    private PackingListAdapter mPackingListAdapter;
    private TextView gearWeightSumTextView;

    private int mTripId = -1;
    private String mTripName = "";

    private GeoDataClient mGeoDataClient;
    private GoogleMap mMap;
    private TravelPackRoomDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Start Pick Gear List Activity to select a gear item to be added to the packing list
            Intent packingListIntend = new Intent(PackingListActivity.this, GearListActivity.class);
            packingListIntend.putExtra("TRIP_ID_EXTRA", mTripId);
            startActivityForResult(packingListIntend, GEAR_PICKER_REQUEST);
        });

        // initialize view variables
        gearWeightSumTextView = findViewById(R.id.textViewWeightSum);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Get the intent, check its content, and populate the UI with its data
        Intent intent = getIntent();
        if (intent.hasExtra("TRIP_ID_EXTRA") && intent.hasExtra("TRIP_NAME_EXTRA")) {
            mTripId = intent.getIntExtra("TRIP_ID_EXTRA", -1);
            mTripName = intent.getStringExtra("TRIP_NAME_EXTRA");

            // Construct a GeoDataClient.
            mGeoDataClient = Places.getGeoDataClient(this);
            db = TravelPackRoomDatabase.getDatabase(this);
        }

        // Fallback in case the trip name is an empty String
        if (mTripName.equals("")) {
            mTripName = "Your Packing List";
        }

        getSupportActionBar().setTitle(mTripName);

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Configuring the RecyclerView and setting its adapter
        RecyclerView packingListRecyclerView = findViewById(R.id.recyclerViewPackingList);
        packingListRecyclerView.setLayoutManager(layoutManager);
        packingListRecyclerView.setHasFixedSize(true);
        mPackingListAdapter = new PackingListAdapter(this, this, this, mTripId);
        packingListRecyclerView.setAdapter(mPackingListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(packingListRecyclerView);

        // Update the cached copy of the trips in the Adapter.
        Observer<List<ItemPackingList>> mObserver = mPackingListAdapter::setPackingListData;

        mTravelPackViewModel.getAllItemsForTrip(mTripId).observe(this, mObserver);

        // Get the map support fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewPackingList);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v, int clickedPackingListItemId) {
    }

    @Override
    public void onPlusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount) {
        // Increment the item count and update the db
        TripItemJoin item = new TripItemJoin(clickedPackingListTripId, clickedPackingListItemId, clickedItemAmount + 1);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.updateTripItemAmount(item));
    }

    @Override
    public void onMinusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount) {
        // Decrement the item count and update the db

        // Prevent negative values
        if (clickedItemAmount < 2) {
            clickedItemAmount = 2;
        }
        TripItemJoin item = new TripItemJoin(clickedPackingListTripId, clickedPackingListItemId, clickedItemAmount - 1);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.updateTripItemAmount(item));
    }

    /**
     * This method retrieves the results of the Gear Picker activity.
     */
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
        // Delete item from list
        TripItemJoin itemToDelete = mPackingListAdapter.getItemByPosition(position);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.deletePackingListItem(itemToDelete));
    }

    @Override
    public void onSumDataChanged(float weightSum) {
        // Update the textView with the new weight sum
        String weightSumString = String.format(Locale.ENGLISH, "%.2f", weightSum);
        gearWeightSumTextView.setText(weightSumString);
    }

    @Override
    public void onPackingListForWidgetChanged(PackingListForWidget packingList) {
        // Update the Widget
        TravelPackWidgetService.updateWidget(this, packingList, mTripId, mTripName);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        new FetchMapViewBoundsByTripId(this).execute(mTripId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_to_widget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_packing_list_to_widget:
                PackingListForWidget packingList = mPackingListAdapter.getPackingList();
                // Update the widget with the currently selected packing list
                TravelPackWidgetService.updateWidget(this, packingList, mTripId, mTripName);
                Toast.makeText(this, "Added to Widget", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                // Create a back stack if the activity was started by clicking on the widget
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                assert upIntent != null;
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * AsyncTask to fetch a trips PlacesId by its TripId from the travel pack db and update the
     * maps view bounds.
     */
    private static class FetchMapViewBoundsByTripId extends AsyncTask<Integer, Void, String> {

        private final WeakReference<PackingListActivity> activityReference;

        FetchMapViewBoundsByTripId(PackingListActivity context) {
            // only retain a weak reference to the activity
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Integer... ints) {
            // get a reference to the activity if it is still there
            PackingListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            int tripId = ints[0];
            String placeResultString;

            placeResultString = activity.db.travelPackDao().getTripPlaceIdById(tripId);

            return placeResultString;
        }

        @Override
        protected void onPostExecute(String placesId) {
            // get a reference to the activity if it is still there
            PackingListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (placesId != null) {

                activity.mGeoDataClient.getPlaceById(placesId).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place returnedPlace = places.get(0);

                        activity.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(returnedPlace.getViewport(), 0));

                        places.release();
                    } else {
                        Toast.makeText(activity, "The destination could not be found", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(activity, "The destination could not be found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
