package com.betravelsome.travelpack;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TripAdapter.TripAdapterOnClickHandler {

    private TravelPackViewModel mTravelPackViewModel;
    private TripAdapter mTripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Start EditTrip Activity
            Intent addTripIntend = new Intent(MainActivity.this, EditTripActivity.class);
            startActivity(addTripIntend);
        });

        // Check and request permissions for reading external storage
        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize and show test ads
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Setting the span count for the GridLayoutManager based on the device's screen width
        int itemWidth = 500;
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, calculateBestSpanCount(itemWidth));

        // Configuring the RecyclerView and setting its adapter
        RecyclerView tripRecyclerView = findViewById(R.id.recyclerViewTrips);

        tripRecyclerView.setLayoutManager(gridLayoutManager);
        tripRecyclerView.setHasFixedSize(true);
        mTripAdapter = new TripAdapter(this, this);
        tripRecyclerView.setAdapter(mTripAdapter);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the trips in the Adapter.
        Observer<List<Trip>> mObserver = mTripAdapter::setTripData;

        mTravelPackViewModel.getAllTrips().observe(this, mObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_gear_items) {
            // Start GearList Activity
            Intent gearListIntend = new Intent(MainActivity.this, GearListActivity.class);
            startActivity(gearListIntend);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v, int clickedTripId) {
        // Start Packing List Activity for the selected Trip
        Intent packingListIntend = new Intent(MainActivity.this, PackingListActivity.class);
        packingListIntend.putExtra("TRIP_ID_EXTRA", clickedTripId);
        TextView tripName = v.findViewById(R.id.textViewTripName);
        packingListIntend.putExtra("TRIP_NAME_EXTRA", tripName.getText());
        startActivity(packingListIntend);
    }

    @Override
    public boolean onLongClick(View v, int clickedTripId) {

        // Create a dialog warning the user about the deletion of the trip
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to delete this trip?")
                .setTitle("Delete?");

        builder.setPositiveButton("OK", (dialog, id) -> {
            Trip tripToDelete = mTripAdapter.getTripByPosition(clickedTripId);
            AppExecutors.getInstance().diskIO().execute(() -> MainActivity.this.mTravelPackViewModel.deleteTrip(tripToDelete));
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {
            // User cancelled the dialog
        });

        AlertDialog dialog = builder.create();

        dialog.show();

        return true;
    }

    private boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Method calculates the best span count for a grid view based on the screen width.
     *
     * @param itemWidth the width of the items
     * @return calculated span count
     */
    private int calculateBestSpanCount(int itemWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / itemWidth);
    }
}
