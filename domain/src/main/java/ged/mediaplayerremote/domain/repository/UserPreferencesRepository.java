package ged.mediaplayerremote.domain.repository;


/**
 * Interface that represents a repository for getting user settings.
 */
public interface UserPreferencesRepository {

    /**
     * Get an IP of currently chosen MPC-HC server.
     */
    String getIP();

    /**
     * Get a port which should be used when connecting to server.
     */
    String getPort();

    /**
     * Get a connection timeout that should be used while sending requests to server.
     */
    int getConnectionTimeout();

    /**
     * Get value (in percent) that should be used to calculate new volume, in increase/decrease volume commands.
     */
    int getVolumeJumpValue();

    /**
     * Get a value [in seconds] that should be used to calculate new position in fast forward and rewind commands.
     */
    int getTimeJumpValue();

    /**
     * Get a boolean value that indicates whether physical volume button should be used to change volume in MPC-HC
     * client, or left with default Android behaviour.
     */
    boolean usePhysicalVolumeButtons();

    /**
     * Get a boolean value that indicates if the app should prevent Android device from going to sleep.
     */
    boolean keepScreenOn();

    /**
     * Get a boolean value that indicates if the screen should be dimmed when app is on.
     */
    boolean adjustScreenBrightness();

    /**
     * Get a boolean value that indicates whether to enable or disable notification widget with control buttons.
     */
    boolean showWidget();

    /**
     * Get a boolean value that indicates whether MPC-HC preview image should be downloaded from the server or not.
     */
    boolean showSnapshot();

    /**
     * Get a value [in seconds] that indicates interval at which preview image should be downloaded from MPC-HC server.
     */
    int getSnapshotUpdateInterval();
}
