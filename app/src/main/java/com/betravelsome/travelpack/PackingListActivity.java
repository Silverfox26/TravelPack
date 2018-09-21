package com.betravelsome.travelpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.betravelsome.travelpack.adapters.PackingListAdapter;
import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.data.TravelPackRoomDatabase;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.model.TripItemJoin;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.betravelsome.travelpack.utilities.RecyclerViewItemTouchHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class PackingListActivity extends AppCompatActivity implements PackingListAdapter.PackingListAdapterOnClickHandler,
        PackingListAdapter.PackingListAdapterGetWeightSumOnDataChanged,
        RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener,
        OnMapReadyCallback {

    private static final String TAG = "CLICK";

    private static int GEAR_PICKER_REQUEST = 1;

    private List<Item> mItems = null;
    private TravelPackViewModel mTravelPackViewModel;
    private PackingListAdapter mPackingListAdapter;
    private Observer<List<ItemPackingList>> mObserver;
    private RecyclerView packingListRecyclerView;
    private TextView gearWeightSumTextView;

    private int mTripId = -1;

    protected GeoDataClient mGeoDataClient;
    private GoogleMap mMap;
    private TravelPackRoomDatabase db;
    private LatLngBounds mViewport;
    private Intent mIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIntent = getIntent();

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

        gearWeightSumTextView = findViewById(R.id.textViewWeightSum);

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        packingListRecyclerView = findViewById(R.id.recyclerViewPackingList);

        // Configuring the RecyclerView and setting its adapter
        packingListRecyclerView.setLayoutManager(layoutManager);
        packingListRecyclerView.setHasFixedSize(true);
        mPackingListAdapter = new PackingListAdapter(this, this, this);
        packingListRecyclerView.setAdapter(mPackingListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(packingListRecyclerView);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the trips in the Adapter.
        mObserver = mPackingListAdapter::setPackingListData;

        // Get the intent, check its content, and populate the UI with its data
        if (mIntent.hasExtra("TRIP_ID_EXTRA")) {
            mTripId = mIntent.getIntExtra("TRIP_ID_EXTRA", -1);

            mTravelPackViewModel.getAllItemsForTrip(mTripId).observe(this, mObserver);

            // Construct a GeoDataClient.
            mGeoDataClient = Places.getGeoDataClient(this);
            db = TravelPackRoomDatabase.getDatabase(this);

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewPackingList);
        mapFragment.getMapAsync(this);
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
        TripItemJoin itemToDelete = mPackingListAdapter.getItemByPosition(position);
        AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.deletePackingListItem(itemToDelete));
    }

    @Override
    public void onSumDataChanged(float weightSum) {
        gearWeightSumTextView.setText(String.valueOf(weightSum));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        new FetchMapViewBoundsByTripId(this).execute(mTripId);
    }

    /**
     * AsyncTask to fetch a trips PlacesId by its TripId from the travel pack db.
     */
    private static class FetchMapViewBoundsByTripId extends AsyncTask<Integer, Void, String> {

        private final WeakReference<PackingListActivity> activityReference;

        FetchMapViewBoundsByTripId(PackingListActivity context) {
            // only retain a weak reference to the activity
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // get a reference to the activity if it is still there
            PackingListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
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

                activity.mGeoDataClient.getPlaceById(placesId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            Place returnedPlace = places.get(0);

                            Log.d(TAG, "onComplete: " + returnedPlace.getId() + returnedPlace.getName());
                            activity.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(returnedPlace.getViewport(), 0));

                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
            } else {
//                activity.showErrorMessage();
            }
        }
    }
}
