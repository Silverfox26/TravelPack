package com.betravelsome.travelpack.utilities;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class RecyclerViewItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final RecyclerViewItemTouchHelperListener mListener;

    /**
     * Creates a Callback for the given drag and swipe allowance.
     */
    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs, RecyclerViewItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.mListener = listener;
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    public interface RecyclerViewItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
