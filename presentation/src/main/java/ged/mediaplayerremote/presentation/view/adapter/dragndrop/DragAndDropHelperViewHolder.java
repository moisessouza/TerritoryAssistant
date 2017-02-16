package ged.mediaplayerremote.presentation.view.adapter.dragndrop;

/**
 * An interface to inform a ViewHolder in a {@link ged.mediaplayerremote.presentation.view.adapter.ButtonAdapter} about
 * drag and drop event.
 */
public interface DragAndDropHelperViewHolder
{
    /**
     * A view has been picked and is being moved now.
     */
    void onItemSelected();

    /**
     * A view has been dropped.
     */
    void onItemClear();
}
