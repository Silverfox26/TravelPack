package com.betravelsome.travelpack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.betravelsome.travelpack.model.PackingListForWidget;

public class TravelPackWidgetService extends RemoteViewsService{

    public static void updateWidget(Context context, PackingListForWidget packingList, int tripId, String tripName) {
        // Save the packingList, tripID, and trip name to sharedPreferences
        Preferences.savePackingListForWidget(context, packingList);
        Preferences.saveTripIdForWidget(context, tripId);
        Preferences.saveTripNameForWidget(context, tripName);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TravelPackWidget.class));
        TravelPackWidget.updateTravelPackWidgets(context, appWidgetManager, appWidgetIds);
    }

    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListRemoteViewsFactory(this.getApplicationContext());
    }
}
