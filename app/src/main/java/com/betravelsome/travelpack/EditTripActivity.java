package com.betravelsome.travelpack;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Objects;

public class EditTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int PLACE_PICKER_REQUEST = 1;
    private static int IMAGE_PICKER_REQUEST = 2;

    // Key values to save states
    private static final String TRIP_NAME_KEY = "TRIP_NAME_KEY";
    private static final String TRIP_PLACE_ID_KEY = "TRIP_PLACE_ID_KEY";
    private static final String TRIP_IMAGE_KEY = "TRIP_IMAGE_KEY";

    private GoogleMap mMap;

    private TravelPackViewModel mTravelPackViewModel;

    private EditText mTripName;
    private String mImagePath = null;
    private String mPlaceId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize view variables
        mTripName = findViewById(R.id.editTextTripName);
        ImageView mTripImage = findViewById(R.id.imageViewTrip);

        // Check if saved instance state contains data and set it to the corresponding variables.
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRIP_NAME_KEY)) {
                mTripName.setText(savedInstanceState.getString(TRIP_NAME_KEY));
            }

            if (savedInstanceState.containsKey(TRIP_PLACE_ID_KEY)) {
                mPlaceId = savedInstanceState.getString(TRIP_PLACE_ID_KEY);
            }

            if (savedInstanceState.containsKey(TRIP_IMAGE_KEY)) {
                mImagePath = savedInstanceState.getString(TRIP_IMAGE_KEY);
                Glide.with(this).load(mImagePath).into(mTripImage);
            }
        }

        // get the map support fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TRIP_NAME_KEY, mTripName.getText().toString());
        if (mPlaceId != null) {
            outState.putString(TRIP_PLACE_ID_KEY, mPlaceId);
        }
        if (mImagePath != null) {
            outState.putString(TRIP_IMAGE_KEY, mImagePath);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * This method opens the PlacePicker, so the user can choose the destination for the trip.
     */
    public void onPickDestinationButtonClick(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method opens the image picker. So that the user can pick an image for the trip.
     */
    public void onPickImageButtonClick(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMAGE_PICKER_REQUEST);
    }

    /**
     * This method retrieves the results of the PlacePicker and ImagePicker activity.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                EditText destinationEditText = findViewById(R.id.editTextDestination);

                Place place = PlacePicker.getPlace(this, data);
                mPlaceId = place.getId();
                destinationEditText.setText(place.getName());

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
            }
        } else if (requestCode == IMAGE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                ImageView imageView = findViewById(R.id.imageViewTrip);
                mImagePath = Objects.requireNonNull(data.getData()).toString();
                Glide.with(this).load(data.getData()).into(imageView);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_trip) {
            if (mTripName.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter a name for your trip", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mPlaceId == null) {
                Toast.makeText(this, "Please pick a destination for your trip", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mImagePath == null) {
                mImagePath = "android.resource://" + this.getPackageName() + "/" + R.drawable.trip;
            }

            String tripName = mTripName.getText().toString();

            Trip trip = new Trip(tripName, mPlaceId, mImagePath);

            AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.insertTrip(trip));

            Toast.makeText(this, "Your trip was successfully saved", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "Your changes have been dismissed.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
