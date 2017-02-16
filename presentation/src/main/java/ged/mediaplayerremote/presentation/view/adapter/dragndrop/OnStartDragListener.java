package ged.mediaplayerremote.presentation.view.adapter.dragndrop;

import android.support.v7.widget.RecyclerView;

/**
 * An interface to inform {@link ged.mediaplayerremote.presentation.view.fragment.ButtonGridFragment} that a button drag
 * event has started.
 */
public interface OnStartDragListener {

    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
