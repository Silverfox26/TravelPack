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
import com.betravelsome.travelpack.model.Item;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class GearItemAdapter extends RecyclerView.Adapter<GearItemAdapter.GearItemViewHolder> {

    // Cached copy of Gear Items
    private List<Item> mItemData;

    // On-click handler to make it easy for an Activity to interface with the RecyclerView
    private final GearItemAdapter.GearItemAdapterOnClickHandler mClickHandler;

    private final Context mContext;

    /**
     * Creates a GearItemAdapter.
     */
    public GearItemAdapter(GearItemAdapter.GearItemAdapterOnClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    /**
     * OnCLickHandler interface
     */
    public interface GearItemAdapterOnClickHandler {
        void onClick(View v, int clickedItemId);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull GearItemAdapter.GearItemViewHolder holder, int position) {

        // Declare and initialize variables with the gear data
        String gearItemName = mItemData.get(position).getItemName();
        String gearItemImagePath = mItemData.get(position).getItemImagePath();
        Float gearItemWeight = mItemData.get(position).getItemWeight();

        // Set the data to the corresponding views
        holder.mGearItemName.setText(gearItemName);

        String gearItemWeightString = String.format(Locale.ENGLISH, "%.2f", gearItemWeight);
        holder.mGearItemWeight.setText(gearItemWeightString);

        // Create the image URI and display it using Glide
        Uri uri;
        uri = Uri.parse(gearItemImagePath);
        Glide.with(mContext).load(uri).into(holder.mGearItemImage);
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
    public GearItemAdapter.GearItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_gear_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new GearItemAdapter.GearItemViewHolder(view);
    }

    /**
     * Cache of the children views for a trip list item.
     */
    public class GearItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mGearItemName;
        final ImageView mGearItemImage;
        final TextView mGearItemWeight;

        GearItemViewHolder(View itemView) {
            super(itemView);
            mGearItemName = itemView.findViewById(R.id.textViewGearName);
            mGearItemImage = itemView.findViewById(R.id.imageViewGear);
            mGearItemWeight = itemView.findViewById(R.id.textViewGearItemWeight);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int id = mItemData.get(adapterPosition).getId();
            mClickHandler.onClick(v, id);

        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mItemData == null) return 0;
        return mItemData.size();
    }

    /**
     * Method is used to set the gear data on a GearItemAdapter if one is already created.
     *
     * @param itemData The new trip data to be displayed.
     */
    public void setGearItemData(List<Item> itemData) {
        mItemData = itemData;
        notifyDataSetChanged();
    }

    /**
     * Method returns a gear item specified by its id
     *
     * @param position The gear items position
     * @return The requested Item
     */
    public Item getItemByPosition(int position) {
        return mItemData.get(position);
    }
}