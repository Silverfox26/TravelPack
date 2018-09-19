package com.betravelsome.travelpack;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Trip;
import com.facebook.stetho.Stetho;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TripAdapter.TripAdapterOnClickHandler{

    private List<Trip> mTrips = null;
    private TravelPackViewModel mTravelPackViewModel;
    private TripAdapter mTripAdapter;
    private Observer<List<Trip>> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EditTrip Activity
                Intent addTripIntend = new Intent(MainActivity.this, EditTripActivity.class);
                startActivity(addTripIntend);
            }
        });

        // TODO if permission is not granted, use standard image for the trips
        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Setting the span count for the GridLayoutManager based on the device's orientation
        int value = this.getResources().getConfiguration().orientation;
        GridLayoutManager layoutManager;
        if (value == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 4);
        }

        RecyclerView tripRecyclerView = findViewById(R.id.recyclerViewTrips);

        // Configuring the RecyclerView and setting its adapter
        tripRecyclerView.setLayoutManager(layoutManager);
        tripRecyclerView.setHasFixedSize(true);
        mTripAdapter = new TripAdapter(this, this);
        tripRecyclerView.setAdapter(mTripAdapter);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Update the cached copy of the trips in the Adapter.
        mObserver = mTripAdapter::setTripData;

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

        //noinspection SimplifiableIfStatement
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
        startActivity(packingListIntend);
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
