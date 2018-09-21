package com.betravelsome.travelpack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.betravelsome.travelpack.model.PackingListForWidget;

/**
 * Implementation of App Widget functionality.
 */
public class TravelPackWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        PackingListForWidget packingList = Preferences.loadPackingListForWidget(context);
        int tripID = Preferences.loadTripIdForWidget(context);
        String tripName = Preferences.loadTripNameForWidget(context);

        if (packingList != null) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.travel_pack_widget);

            views.setTextViewText(R.id.packing_list_name_widget_text_view, tripName);

            // Create an Intent to launch PackingListActivity when clicked
            Intent intent = new Intent(context, PackingListActivity.class);
            intent.putExtra("TRIP_ID_EXTRA", tripID);
            intent.putExtra("TRIP_NAME_EXTRA", tripName);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Widgets allow click handlers to only launch pending intents
            views.setOnClickPendingIntent(R.id.packing_list_name_widget_text_view, pendingIntent);
            views.setTextViewText(R.id.packing_list_name_widget_text_view, tripName);

            // Initialize the list view
            Intent serviceIntent = new Intent(context, TravelPackWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // Bind the remote adapter
            views.setRemoteAdapter(R.id.packing_list_widget_list_view, serviceIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.packing_list_widget_list_view);
        }
    }

    public static void updateTravelPackWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

