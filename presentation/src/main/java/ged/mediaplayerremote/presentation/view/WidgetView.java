package ged.mediaplayerremote.presentation.view;

/**
 * Interface representing a View in a model view presenter pattern.
 * This view represents a notification widget_control, that is used to control the player when the app is not in the foreground.
 */
public interface WidgetView {

    /**
     * Switch the graphic of the middle button to a pause symbol.
     */
    void showPauseButton();

    /**
     * Switch the graphic of the middle button to a play symbol.
     */
    void showPlayButton();

    /**
     * Switch the notification to a "Disconnected" mode with a reconnect button on the side.
     */
    void showViewDisconnected();

    /**
     * Switch the notification to a "Reconnecting" mode with a reconnect tries counter.
     *
     * @param retries How many pings from the server have timed out already.
     */
    void showViewConnecting(int retries);

}
