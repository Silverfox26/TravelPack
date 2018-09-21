package com.betravelsome.travelpack;

import android.content.Context;
import android.content.SharedPreferences;

import com.betravelsome.travelpack.model.PackingListForWidget;
import com.google.gson.Gson;

public class Preferences {
    private static final String PREFERENCES_NAME = "preferences";

    /**
     * This method saves a passed in PackingListForWidget as a JSON String in sharedPreferences
     *
     * @param context App Context
     * @param packingListForWidget  PackingListForWidget object that should be saved
     */
    public static void savePackingListForWidget(Context context, PackingListForWidget packingListForWidget) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();

        Gson gson = new Gson();
        prefs.putString("widget_packing_list_key", gson.toJson(packingListForWidget));

        prefs.apply();
    }

    /**
     * This method loads the saved PackingListForWidget JSON String from the sharedPreferences and converts
     * it back to a PackingListForWidget object.
     *
     * @param context App context.
     * @return loaded PackingListForWidget object.
     */
    public static PackingListForWidget loadPackingListForWidget(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String packingListForWidgetJSON = prefs.getString("widget_packing_list_key", "");

        Gson gson = new Gson();

        return "".equals(packingListForWidgetJSON) ? null : gson.fromJson(packingListForWidgetJSON, PackingListForWidget.class);
    }

    public static void saveTripIdForWidget (Context context, Integer tripId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();

        prefs.putInt("widget_packing_list_trip_id", tripId);

        prefs.apply();
    }

    public static Integer loadTripIdForWidget (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        return prefs.getInt("widget_packing_list_trip_id", -1);
    }

    public static void saveTripNameForWidget (Context context, String tripName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();

        prefs.putString("widget_packing_list_trip_name", tripName);

        prefs.apply();
    }

    public static String loadTripNameForWidget (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        return prefs.getString("widget_packing_list_trip_name", "");
    }

}
