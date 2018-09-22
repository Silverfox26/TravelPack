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

import com.betravelsome.travelpack.Preferences;
import com.betravelsome.travelpack.R;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.PackingListForWidget;
import com.betravelsome.travelpack.model.TripItemJoin;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PackingListAdapter extends RecyclerView.Adapter<PackingListAdapter.PackingListViewHolder> {

    // Cached copy of Packing List Items
    private List<ItemPackingList> mPackingListData;

    // On-click handler to make it easy for an Activity to interface with the RecyclerView
    private final PackingListAdapter.PackingListAdapterOnClickHandler mClickHandler;

    // Handler that makes it easy for an Activity to react to changes in the overall weight of the items
    private final PackingListAdapterGetWeightSumOnDataChanged mDataChangeHandler;

    private final Context mContext;
    private final int mTripId;

    /**
     * Creates a PackingListAdapter.
     */
    public PackingListAdapter(PackingListAdapter.PackingListAdapterOnClickHandler clickHandler, PackingListAdapterGetWeightSumOnDataChanged dataChangeHandler, Context context, int tripId) {
        this.mClickHandler = clickHandler;
        this.mDataChangeHandler = dataChangeHandler;
        this.mContext = context;
        this.mTripId = tripId;
    }

    /**
     * OnClickHandler to interface with clicks on the item, as well as its plus and minus buttons
     */
    public interface PackingListAdapterOnClickHandler {
        void onClick(View v, int clickedPackingListItemId);

        void onPlusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount);

        void onMinusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount);
    }

    /**
     * Handler the gets called, when the overall item weight changes
     */
    public interface PackingListAdapterGetWeightSumOnDataChanged {
        void onSumDataChanged(float weightSum);

        void onPackingListForWidgetChanged(PackingListForWidget packingList);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PackingListAdapter.PackingListViewHolder holder, int position) {

        // Declare and initialize variables with the packing list data
        String gearItemName = mPackingListData.get(position).getItemName();
        Float gearItemWeight = mPackingListData.get(position).getItemWeight();
        String gearItemImagePath = mPackingListData.get(position).getItemImagePath();
        Integer gearItemAmount = mPackingListData.get(position).getItemAmount();

        // Set the data to the corresponding views
        holder.mGearItemName.setText(gearItemName);

        String gearItemWeightString = String.format(Locale.ENGLISH, "%.2f", gearItemWeight);
        holder.mGearItemWeight.setText(gearItemWeightString);
        holder.mGearItemAmount.setText(String.valueOf(gearItemAmount));

        Float gearWeightSum = gearItemAmount * gearItemWeight;
        String gearItemWeightSumString = String.format(Locale.ENGLISH, "%.2f", gearWeightSum);
        holder.mGearWeightSum.setText(gearItemWeightSumString);

        holder.mGearItemPlus.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            int tripId = mPackingListData.get(adapterPosition).getTripId();
            int itemId = mPackingListData.get(adapterPosition).getItemId();
            int itemAmount = mPackingListData.get(adapterPosition).getItemAmount();
            mClickHandler.onPlusClicked(v, tripId, itemId, itemAmount);
        });

        holder.mGearItemMinus.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            int tripId = mPackingListData.get(adapterPosition).getTripId();
            int itemId = mPackingListData.get(adapterPosition).getItemId();
            int itemAmount = mPackingListData.get(adapterPosition).getItemAmount();
            mClickHandler.onMinusClicked(v, tripId, itemId, itemAmount);
        });

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
    public PackingListAdapter.PackingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_packing_list_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new PackingListAdapter.PackingListViewHolder(view);
    }

    /**
     * Cache of the children views for a packing list item.
     */
    public class PackingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mGearItemName;
        final TextView mGearItemWeight;
        final ImageView mGearItemImage;
        final TextView mGearItemAmount;
        final TextView mGearWeightSum;
        final TextView mGearItemPlus;
        final TextView mGearItemMinus;

        PackingListViewHolder(View itemView) {
            super(itemView);
            mGearItemName = itemView.findViewById(R.id.textViewGearName);
            mGearItemWeight = itemView.findViewById(R.id.textViewGearWeight);
            mGearItemImage = itemView.findViewById(R.id.imageViewGear);
            mGearItemAmount = itemView.findViewById(R.id.textViewAmount);
            mGearWeightSum = itemView.findViewById(R.id.textViewSumGearItemWeight);
            mGearItemPlus = itemView.findViewById(R.id.textViewPlus);
            mGearItemMinus = itemView.findViewById(R.id.textViewMinus);
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
            int id = mPackingListData.get(adapterPosition).getId();
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
        if (mPackingListData == null) return 0;
        return mPackingListData.size();
    }

    /**
     * Method is used to set the packing list data on a PackingListAdapter if one is already created.
     *
     * @param itemData The new packing list data to be displayed.
     */
    public void setPackingListData(List<ItemPackingList> itemData) {
        mPackingListData = itemData;
        getGearWeightSum();
        getPackingListForWidget();
        notifyDataSetChanged();
    }

    /**
     * Returns a TripItemJoin object that holds a database relation for a trip and
     * an item
     */
    public TripItemJoin getItemByPosition(int position) {
        int tripId = mPackingListData.get(position).getTripId();
        int itemId = mPackingListData.get(position).getItemId();

        return new TripItemJoin(tripId, itemId);
    }

    /**
     * Method returns the sum of all item weights of the packing list
     */
    private void getGearWeightSum() {
        Float weightSum = 0.0f;

        if (mPackingListData != null) {

            for (ItemPackingList item : mPackingListData) {
                int itemAmount = item.getItemAmount();
                float itemWeight = item.getItemWeight();
                float itemWeightSum = itemAmount * itemWeight;
                weightSum = weightSum + itemWeightSum;
            }
        }

        mDataChangeHandler.onSumDataChanged(weightSum);
    }

    /**
     * Method to refresh the packing list data for the apps widget whenever the data changes
     */
    private void getPackingListForWidget() {

        if (mTripId == Preferences.loadTripIdForWidget(mContext)) {
            List<Integer> itemAmount = new ArrayList<>();
            List<String> itemName = new ArrayList<>();

            if (null != mPackingListData) {

                for (ItemPackingList item : mPackingListData) {
                    itemAmount.add(item.getItemAmount());
                    itemName.add(item.getItemName());
                }
            }
            PackingListForWidget packingList = new PackingListForWidget(mTripId, itemAmount, itemName);

            mDataChangeHandler.onPackingListForWidgetChanged(packingList);
        }

    }

    /**
     * Method to return the whole packing list. Use when user adds a packing list to the
     * widget for the first time.
     *
     * @return the packing list data
     */
    public PackingListForWidget getPackingList() {
        List<Integer> itemAmount = new ArrayList<>();
        List<String> itemName = new ArrayList<>();

        if (null != mPackingListData) {

            for (ItemPackingList item : mPackingListData) {
                itemAmount.add(item.getItemAmount());
                itemName.add(item.getItemName());
            }
        }
        return new PackingListForWidget(mTripId, itemAmount, itemName);
    }
}