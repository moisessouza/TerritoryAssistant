package ged.mediaplayerremote.domain.repository;

import ged.mediaplayerremote.domain.model.RemoteDirectory;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.model.MpcServer;
import rx.Observable;

import java.util.Map;

/**
 * Interface that represents a Repository for getting data from MPC-HC server, and sending commands to it.
 */
public interface MpcClientRepository
{
    /**
     * Send a command to the MPC-HC server. Response will be emitted through an {@link Observable}
     *
     * @param server which server should the command be sent to.
     * @param command a {@link Map} collection that should contain one or more {@link String} pairs. Key of the first
     *                pair should equal "wm_command" with desired command code as value. Some commands require
     *                additional parameters such as "position" or "volume".
     */
    Observable<Void> sendCommand(MpcServer server, Map<String, String> command);

    /**
     * Get an {@link Observable} which will emit a {@link PlaybackStatus} object every second. New objects will be
     * emitted until unsubscribed. If an update could not be retrieved, null will be emitted.
     *
     *
     * @param server which server should the status be retrieved from.
     * @return {@link Observable} object which will emit {@link PlaybackStatus} every second when subscribed to.
     */
    Observable<PlaybackStatus> getPlaybackStatusUpdates(MpcServer server);

    /**
     * Get an {@link Observable} which will emit image previews from the server, in the form of byte array. New image
     * will be emitted continuously until unsubscribed. If an image could not be retrieved, null will be emitted. The
     * delay between images depends on user settings, retrieved from {@link UserPreferencesRepository}.
     *
     * @param server which server should the image be retrieved from.
     */
    Observable<byte[]> getSnapshot(MpcServer server);

    /**
     * Get an {@link Observable} which will emit a {@link RemoteDirectory}
     * @param server which server should the directory be retrieved from.
     * @param location location of the directory.
     */
    Observable<RemoteDirectory> getDirectory(MpcServer server, String location);

}
