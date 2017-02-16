package ged.mediaplayerremote.presentation.view;

import ged.mediaplayerremote.presentation.model.ServerModel;

/**
 * Interface representing a View in a model view presenter pattern.
 * This view represents server finder, that is used to scan local Wi-Fi network to find available MPC-HC servers.
 */
public interface ServerFinderView extends BaseView {

    /**
     * Show a view with a progress bar indicating a loading process.
     *
     * @param maxProgress A maximum value of the progress bar.
     */
    void showViewLoading(int maxProgress);

    /**
     * Hide a loading view.
     */
    void hideViewLoading();

    /**
     * Add a new item to the list of displayed {@link ServerModel}.
     *
     * @param serverModel A server to be shown.
     */
    void addServer(ServerModel serverModel);

    /**
     * Clear the list of displayed servers.
     */
    void clearServers();

    /**
     * Select a server to be used by the rest of application.
     *
     * @param ip A server to be used.
     */
    void selectServer(String ip);

    /**
     * Increase the progress of the loading bar.
     */
    void addProgress();

    /**
     * Reset the progress on the loading bar.
     */
    void clearProgress();
}
