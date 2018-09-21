package com.betravelsome.travelpack;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.betravelsome.travelpack.adapters.TripAdapter;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TripAdapter.TripAdapterOnClickHandler{

    private List<Trip> mTrips = null;
    private TravelPackViewModel mTravelPackViewModel;
    private TripAdapter mTripAdapter;
    private Observer<List<Trip>> mObserver;

    private AdView mAdView;

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

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Setting the span count for the GridLayoutManager based on the device's screen width
        int posterWidth = 500;
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, calculateBestSpanCount(posterWidth));

        RecyclerView tripRecyclerView = findViewById(R.id.recyclerViewTrips);

        // Configuring the RecyclerView and setting its adapter
        tripRecyclerView.setLayoutManager(gridLayoutManager);
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

    @Override
    public boolean onLongClick(View v, int clickedTripId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to delete this trip?")
                .setTitle("Delete?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Trip tripToDelete = mTripAdapter.getTripByPosition(clickedTripId);
                AppExecutors.getInstance().diskIO().execute(() -> MainActivity.this.mTravelPackViewModel.deleteTrip(tripToDelete));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

        return true;
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

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }
}
