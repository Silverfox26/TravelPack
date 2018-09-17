package com.betravelsome.travelpack;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EditTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int PLACE_PICKER_REQUEST = 1;
    private static int IMAGE_PICKER_REQUEST = 2;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
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

                Place place = PlacePicker.getPlace(this, data);
                destinationEditText.setText(place.getName());

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
            }
        } else if (requestCode == IMAGE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image Request Ok", Toast.LENGTH_SHORT).show();
                ImageView imageView = findViewById(R.id.imageViewTrip);
                Glide.with(this).load(data.getData()).into(imageView);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }
}
