package com.betravelsome.travelpack;

import android.content.Context;
import android.widget.Adapter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.betravelsome.travelpack.model.PackingListForWidget;

public class WidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private PackingListForWidget mPackingList;

    WidgetListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    /**
     * Called when the factory is first constructed. The same factory may be shared across
     * multiple RemoteViewAdapters depending on the intent passed.
     */
    @Override
    public void onCreate() {

    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
     * RemoteViewsFactory to respond to data changes by updating any internal references.
     */
    @Override
    public void onDataSetChanged() {
        mPackingList = Preferences.loadPackingListForWidget(mContext);
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is
     * unbound.
     */
    @Override
    public void onDestroy() {

    }

    /**
     * Returns the size of the Packing List data
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mPackingList.getItemName().size();
    }

    /**
     * This method sets the data to the list view items
     *
     * @param position The position of the item within the Factory's data set of the item whose
     *                 view we want.
     * @return A RemoteViews object corresponding to the data at the specified position.
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listItem = new RemoteViews(mContext.getPackageName(), R.layout.travel_pack_widget_item);

        listItem.setTextViewText(R.id.item_quantity_text_view, mPackingList.getItemAmount().get(position).toString());
        listItem.setTextViewText(R.id.item_name_text_view, mPackingList.getItemName().get(position));

        return listItem;
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that
     * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
     * view will be used.
     *
     * @return The RemoteViews representing the desired loading view.
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * Returns the number of view types this factory returns.
     *
     * @return The number of types of Views that will be returned by this factory.
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * Gets the item id of an item at the specified position.
     *
     * @param position The position of the item within the data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * See {@link Adapter#hasStableIds()}.
     *
     * @return True if the same id always refers to the same object.
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}
