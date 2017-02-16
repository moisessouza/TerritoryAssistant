package ged.mediaplayerremote.presentation.view.adapter.dragndrop;

/**
 * An interface to notify {@link ged.mediaplayerremote.presentation.presenter.ButtonGridPresenter} of drag and drop
 * events.
 */
public interface ItemMovedListener
{
    /**
     * Method to call every time a button has been dragged to a new position.
     */
    void onItemMove(int fromPosition, int toPosition);

}
