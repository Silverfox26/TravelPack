package com.betravelsome.travelpack.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.betravelsome.travelpack.R;
import com.betravelsome.travelpack.model.Item;
import com.betravelsome.travelpack.model.ItemPackingList;
import com.betravelsome.travelpack.model.Trip;
import com.betravelsome.travelpack.model.TripItemJoin;
import com.bumptech.glide.Glide;

import java.util.List;

public class PackingListAdapter extends RecyclerView.Adapter<PackingListAdapter.PackingListViewHolder> {

    private final static String TAG = "CLICK";

    // Cached copy of Packing List Items
    private List<ItemPackingList> mPackingListData;

    // On-click handler to make it easy for an Activity to interface with the RecyclerView
    private final PackingListAdapter.PackingListAdapterOnClickHandler mClickHandler;

    private final Context mContext;

    /**
     * Creates a PackingListAdapter.
     */
    public PackingListAdapter(PackingListAdapter.PackingListAdapterOnClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    public interface PackingListAdapterOnClickHandler {
        void onClick(View v, int clickedPackingListItemId);

        void onPlusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount);

        void onMinusClicked(View v, int clickedPackingListTripId, int clickedPackingListItemId, int clickedItemAmount);
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

        String gearItemName = mPackingListData.get(position).getItemName();
        Float gearItemWeight = mPackingListData.get(position).getItemWeight();
        String gearItemImagePath = mPackingListData.get(position).getItemImagePath();
        Integer gearItemAmount = mPackingListData.get(position).getItemAmount();

        holder.mGearItemName.setText(gearItemName);
        holder.mGearItemWeight.setText(gearItemWeight.toString());
        holder.mGearItemAmount.setText(String.valueOf(gearItemAmount));

        Float gearWeightSum = gearItemAmount * gearItemWeight;
        holder.mGearWeightSum.setText(gearWeightSum.toString());

        holder.mGearItemPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Plus Clicked");
                int adapterPosition = holder.getAdapterPosition();
                int tripId = mPackingListData.get(adapterPosition).getTripId();
                int itemId = mPackingListData.get(adapterPosition).getItemId();
                int itemAmount = mPackingListData.get(adapterPosition).getItemAmount();
                mClickHandler.onPlusClicked(v, tripId, itemId, itemAmount);
            }
        });

        holder.mGearItemMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Minus Clicked");
                int adapterPosition = holder.getAdapterPosition();
                int tripId = mPackingListData.get(adapterPosition).getTripId();
                int itemId = mPackingListData.get(adapterPosition).getItemId();
                int itemAmount = mPackingListData.get(adapterPosition).getItemAmount();
                mClickHandler.onMinusClicked(v, tripId, itemId, itemAmount);
            }
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
            mGearItemAmount = itemView.findViewById(R.id.textViewAmmount);
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
            Log.d("AAA_ADAPTER", "onClick: " + id + v);
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
     * This way new data can be loaded from the web and displayed without the need for
     * a new MovieAdapter.
     *
     * @param itemData The new packing list data to be displayed.
     */
    public void setPackingListData(List<ItemPackingList> itemData) {
        mPackingListData = itemData;
        notifyDataSetChanged();
    }

    public void increaseItemCount(int position) {
        int itemAmount = mPackingListData.get(position).getItemAmount();
        mPackingListData.get(position).setItemAmount(itemAmount + 1);
    }

    public TripItemJoin removeItem(int position) {
        int tripId = mPackingListData.get(position).getTripId();
        int itemId = mPackingListData.get(position).getItemId();
        TripItemJoin item = new TripItemJoin(tripId, itemId);

        return item;
    }
}
