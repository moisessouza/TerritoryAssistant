package ged.mediaplayerremote.domain.controller;

/**
 * Interface for listening to any server related settings made by the user.
 */
public interface ServerSettingsChangedListener {

    /**
     * A method to be called whenever user has selected a new server.
     */
    void onServerSettingsChanged();
}
