package com.betravelsome.travelpack.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PackingListForWidget implements Parcelable {

    private Integer tripId;
    private String tripName;
    private List<Integer> itemAmount;
    private List<String> itemName;
    public final static Parcelable.Creator<PackingListForWidget> CREATOR = new Creator<PackingListForWidget>() {

        @SuppressWarnings({
                "unchecked"
        })
        public PackingListForWidget createFromParcel(Parcel in) {
            return new PackingListForWidget(in);
        }

        public PackingListForWidget[] newArray(int size) {
            return (new PackingListForWidget[size]);
        }

    };

    protected PackingListForWidget(Parcel in) {
        this.tripId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.tripName = ((String) in.readValue((String.class.getClassLoader())));
        this.itemAmount = new ArrayList<>();
        in.readList(this.itemAmount, (Integer.class.getClassLoader()));
        this.itemName = new ArrayList<>();
        in.readList(this.itemName, (String.class.getClassLoader()));
    }


    /**
     * No args constructor for use in serialization
     */
    public PackingListForWidget() {
    }

    /**
     * Constructs a PackingListForWidget object.
     *
     * @param tripId
     * @param tripName
     * @param itemAmount
     * @param itemName
     */
    public PackingListForWidget(Integer tripId, String tripName, List<Integer> itemAmount, List<String> itemName) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.itemAmount = itemAmount;
        this.itemName = itemName;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public List<Integer> getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(List<Integer> itemAmount) {
        this.itemAmount = itemAmount;
    }

    public List<String> getItemName() {
        return itemName;
    }

    public void setItemName(List<String> itemName) {
        this.itemName = itemName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tripId);
        dest.writeValue(tripName);
        dest.writeValue(itemAmount);
        dest.writeValue(itemName);
    }
}
