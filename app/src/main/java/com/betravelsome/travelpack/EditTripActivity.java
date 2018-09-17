package com.betravelsome.travelpack;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class EditTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int PLACE_PICKER_REQUEST = 1;
    private static int IMAGE_PICKER_REQUEST = 2;

    private GoogleMap mMap;

    private TravelPackViewModel mTravelPackViewModel;

    private EditText mTripName;
    private String mImagePath = null;
    private Place mPlace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTripName = findViewById(R.id.editTextTripName);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);
    }

    /**
     * This method opens the PlacePicker, so the user can choose the destination for the trip.
     *
     * @param view
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
     *
     * @param view
     */
    public void onPickImageButtonClick(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMAGE_PICKER_REQUEST);
    }

    /**
     * This method retrieves the results of the PlacePicker or ImagePicker activity.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("IMAGE_LOAD", "onActivityResult: " + resultCode + " " + requestCode);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                EditText destinationEditText = findViewById(R.id.editTextDestination);

                mPlace = PlacePicker.getPlace(this, data);
                destinationEditText.setText(mPlace.getName());

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mPlace.getViewport(), 0));
            }
        } else if (requestCode == IMAGE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image Request Ok", Toast.LENGTH_SHORT).show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_trip) {
            String tripName = mTripName.getText().toString();

            Trip trip = new Trip(tripName, mPlace, mImagePath);

            AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.insertTrip(trip));

            // TODO Show confirmation that Trip was saved. And then exit to main activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
