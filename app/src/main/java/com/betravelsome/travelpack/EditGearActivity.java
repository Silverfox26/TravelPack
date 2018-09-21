package com.betravelsome.travelpack;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.betravelsome.travelpack.data.ItemDao;
import com.betravelsome.travelpack.data.TravelPackRoomDatabase;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.utilities.AppExecutors;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class EditGearActivity extends AppCompatActivity {

    private static int IMAGE_PICKER_REQUEST = 1;

    private TravelPackViewModel mTravelPackViewModel;

    private EditText mGearName;
    private EditText mGearWeight;
    private ImageView mGearImage;
    private String mImagePath = null;
    private String mName;
    private Float mWeight;
    private Item mGearItem = null;

    private TravelPackRoomDatabase db ;

    private boolean isNewGearItem = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gear);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGearName = findViewById(R.id.editTextGearName);
        mGearWeight = findViewById(R.id.editTextWeight);
        mGearImage = findViewById(R.id.imageViewGear);

        // The ViewModelProvider creates the ViewModel, when the app first starts.
        // When the activity is destroyed and recreated, the Provider returns the existing ViewModel.
        mTravelPackViewModel = ViewModelProviders.of(this).get(TravelPackViewModel.class);

        // Get the intent and receive the itemId;
        Intent intent = getIntent();
        if (intent.hasExtra("GEAR_ITEM_ID_EXTRA")) {

            isNewGearItem = false;
            int itemId = intent.getIntExtra("GEAR_ITEM_ID_EXTRA", -1);

            db = TravelPackRoomDatabase.getDatabase(this);
            new FetchItemByIdTask(this, db).execute(itemId);
        }

    }

    /**
     * This method opens the image picker. So that the user can pick an image for the gear item.
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
        if (requestCode == IMAGE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image Request Ok", Toast.LENGTH_SHORT).show();
                mImagePath = Objects.requireNonNull(data.getData()).toString();
                Glide.with(this).load(data.getData()).into(mGearImage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_gear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_gear) {

            if (mGearName.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter a name for your gear", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mGearWeight.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter a weight for your gear", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mImagePath == null) {
                mImagePath = "android.resource://" + this.getPackageName() + "/" + R.drawable.gear;
            }

            mName = mGearName.getText().toString();
            mWeight = Float.valueOf(mGearWeight.getText().toString());

            if (isNewGearItem) {
                mGearItem = new Item(mName, mWeight, mImagePath);

                AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.insertItem(mGearItem));

                Toast.makeText(this, "Your gear was successfully saved", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            } else {
                mGearItem.setItemName(mName);
                mGearItem.setItemWeight(mWeight);
                mGearItem.setItemImagePath(mImagePath);

                AppExecutors.getInstance().diskIO().execute(() -> this.mTravelPackViewModel.updateGearItem(mGearItem));

                Toast.makeText(this, "Your gear was successfully updated", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
        } else if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "Your changes have been dismissed.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * AsyncTask to fetch an item by id from the travel pack db.
     */
    private static class FetchItemByIdTask extends AsyncTask<Integer, Void, Item> {

        private final WeakReference<EditGearActivity> activityReference;
        private final TravelPackRoomDatabase mDb;

        FetchItemByIdTask(EditGearActivity context,  TravelPackRoomDatabase db) {
            // only retain a weak reference to the activity
            activityReference = new WeakReference<>(context);
            mDb = db;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // get a reference to the activity if it is still there
            EditGearActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
        }

        @Override
        protected Item doInBackground(Integer... ints) {
            // get a reference to the activity if it is still there
            EditGearActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            int itemId = ints[0];
            Item itemResult;

            itemResult = mDb.ItemDao().getItemById(itemId);

            return itemResult;
        }

        @Override
        protected void onPostExecute(Item item) {
            // get a reference to the activity if it is still there
            EditGearActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (item != null) {
                activity.mGearItem = item;

                activity.mGearName.setText(activity.mGearItem.getItemName());
                activity.mGearWeight.setText(String.valueOf(activity.mGearItem.getItemWeight()));

                activity.mImagePath = activity.mGearItem.getItemImagePath();
                Glide.with(activity).load(activity.mImagePath).into(activity.mGearImage);
            } else {
//                activity.showErrorMessage();
            }
        }
    }

}
