package ged.mediaplayerremote.presentation.view.adapter.dragndrop;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import ged.mediaplayerremote.presentation.view.ButtonGridView;


/**
 * {@link android.support.v7.widget.helper.ItemTouchHelper.Callback} implementation that allows buttons in a
 * {@link ButtonGridView} to be moved using drag and drop technique.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback
{
    private final ItemMovedListener itemMovedListener;

    public SimpleItemTouchHelperCallback(ItemMovedListener itemMovedListener) {
        this.itemMovedListener = itemMovedListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        itemMovedListener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof DragAndDropHelperViewHolder) {
                DragAndDropHelperViewHolder itemViewHolder = (DragAndDropHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof DragAndDropHelperViewHolder) {
            DragAndDropHelperViewHolder itemViewHolder = (DragAndDropHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}
