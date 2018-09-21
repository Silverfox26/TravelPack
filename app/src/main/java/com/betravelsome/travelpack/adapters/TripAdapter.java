package com.betravelsome.travelpack.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.betravelsome.travelpack.R;
import com.betravelsome.travelpack.model.Trip;
import com.bumptech.glide.Glide;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    // Cached copy of Trips
    private List<Trip> mTripData;

    // On-click handler to make it easy for an Activity to interface with the RecyclerView
    private final TripAdapterOnClickHandler mClickHandler;

    private final Context mContext;

    /**
     * Creates a TripAdapter.
     */
    public TripAdapter(TripAdapterOnClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    /**
     * OnClickHandler to interface with clicks on the item, as well as long clicks
     */
    public interface TripAdapterOnClickHandler {
        void onClick(View v, int clickedTripId);

        boolean onLongClick(View v, int clickedTripId);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {

        // Declare and initialize variables with the trip data
        String tripName = mTripData.get(position).getTripName();
        String tripImagePath = mTripData.get(position).getTripImagePath();

        // Set the data to the corresponding views
        holder.mTripName.setText(tripName);

        // Create the image URI and display it using Glide
        Uri uri;
        uri = Uri.parse(tripImagePath);
        Glide.with(mContext).load(uri).into(holder.mTripImage);
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_main_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TripViewHolder(view);
    }

    /**
     * Cache of the children views for a trip list item.
     */
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView mTripName;
        final ImageView mTripImage;

        TripViewHolder(View itemView) {
            super(itemView);
            mTripName = itemView.findViewById(R.id.textViewTripName);
            mTripImage = itemView.findViewById(R.id.imageViewTrip);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int id = mTripData.get(adapterPosition).getId();
            mClickHandler.onClick(v, id);

        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            int id = mTripData.get(adapterPosition).getId();
            return mClickHandler.onLongClick(v, id);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mTripData == null) return 0;
        return mTripData.size();
    }

    /**
     * Method returns a Trip object by its position
     */
    public Trip getTripByPosition(int position) {
        return mTripData.get(position);
    }

    /**
     * Method is used to set the trip data on a TripAdapter if one is already created.
     *
     * @param tripData The new trip data to be displayed.
     */
    public void setTripData(List<Trip> tripData) {
        mTripData = tripData;
        notifyDataSetChanged();
    }
}
