package ged.mediaplayerremote.domain.controller;

import ged.mediaplayerremote.domain.exception.ServerNotAvailableException;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.model.PlaybackState;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class that sends commands to MPC-HC server, and notifies {@link PlaybackStatusProxy} every time a command has been
 * issued that may have altered a {@link PlaybackStatus} of the player, such as change volume or set position commands.
 **/
@Singleton
public class CommandDispatcher {

    private MpcClientRepository mpcClientRepository;
    private UserPreferencesRepository userPreferencesRepository;
    private PlaybackStatusProxy playbackStatusProxy;

    @Inject
    public CommandDispatcher(MpcClientRepository mpcClientRepository, PlaybackStatusProxy playbackStatusProxy,
                             UserPreferencesRepository userPreferencesRepository) {

        this.mpcClientRepository = mpcClientRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.playbackStatusProxy = playbackStatusProxy;

    }

    /**
     * Send a play or pause command, depending on current playback state.
     *
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable playPause() {
        PlaybackStatus playbackStatus = playbackStatusProxy.getPlaybackStatus();
        if (playbackStatus == null) {
            return Observable.error(new ServerNotAvailableException());
        }

        PlaybackState playbackState = playbackStatus.getPlaybackState();

        Map<String, String> command = new HashMap<>(1);

        if (playbackState == PlaybackState.NO_FILE_SELECTED) {
            command.put("wm_command", "" + 887);
            playbackStatusProxy.onPlaybackStateChanged(PlaybackState.PLAYING);

        } else {
            if (playbackState == PlaybackState.PLAYING) {
                command.put("wm_command", "" + 888);
                playbackStatusProxy.onPlaybackStateChanged(PlaybackState.PAUSED);

            } else {
                command.put("wm_command", "" + 887);
                playbackStatusProxy.onPlaybackStateChanged(PlaybackState.PLAYING);


            }
        }

        return mpcClientRepository.sendCommand(getServer(), command);
    }

    /**
     * Send a command that will send a fast forward command. The jump interval depends on user preference, and is taken
     * from {@link UserPreferencesRepository}
     *
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable fastForward() {


        PlaybackStatus playbackStatus = playbackStatusProxy.getPlaybackStatus();
        if (playbackStatus == null) {
            return Observable.error(new ServerNotAvailableException());
        }

        long currentPosition = playbackStatus.getPosition();
        long newPosition = currentPosition + userPreferencesRepository.getTimeJumpValue() * 1000;

        long currentDuration = playbackStatus.getDuration();

        if (newPosition > currentDuration) {
            newPosition = currentDuration;
        }

        playbackStatusProxy.onPositionChanged(newPosition);


        return mpcClientRepository.sendCommand(getServer(), getJumpToTimeCommand(newPosition));
    }

    /**
     * Send a command that will send a rewind command. The jump interval depends on user preference, and is taken
     * from {@link UserPreferencesRepository}.
     *
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable rewind() {

        PlaybackStatus playbackStatus = playbackStatusProxy.getPlaybackStatus();
        if (playbackStatus == null) {
            return Observable.error(new ServerNotAvailableException());
        }

        long currentPosition = playbackStatus.getPosition();
        long newPosition = currentPosition - userPreferencesRepository.getTimeJumpValue() * 1000;

        if (newPosition < 0) {
            newPosition = 0;
        }

        playbackStatusProxy.onPositionChanged(newPosition);

        return mpcClientRepository.sendCommand(getServer(), getJumpToTimeCommand(newPosition));
    }

    /**
     * Send a command that will raise the volume. The new value depends on user preference and is taken from
     * {@link UserPreferencesRepository}.
     *
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable raiseVolume() {
        PlaybackStatus playbackStatus = playbackStatusProxy.getPlaybackStatus();
        if (playbackStatus == null) {
            return Observable.error(new ServerNotAvailableException());
        }
        int currentVolume = playbackStatus.getVolume();

        int newVolume = currentVolume + userPreferencesRepository.getVolumeJumpValue();
        if (newVolume > 100) {
            newVolume = 100;
        }
        playbackStatusProxy.onVolumeChanged(newVolume);
        return mpcClientRepository.sendCommand(getServer(), getSetVolumeCommand(newVolume));
    }

    /**
     * Send a command that will lower the volume. The new value depends on user preference and is taken from
     * {@link UserPreferencesRepository}.
     *
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable lowerVolume() {

        PlaybackStatus playbackStatus = playbackStatusProxy.getPlaybackStatus();
        if (playbackStatus == null) {
            return Observable.error(new ServerNotAvailableException());
        }
        int currentVolume = playbackStatus.getVolume();

        int newVolume = currentVolume - userPreferencesRepository.getVolumeJumpValue();
        if (newVolume < 0) {
            newVolume = 0;
        }
        playbackStatusProxy.onVolumeChanged(newVolume);
        return mpcClientRepository.sendCommand(getServer(), getSetVolumeCommand(newVolume));
    }

    /**
     * Send a command that will change volume to a specified value.
     *
     * @param volume The new value to be set.
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable setVolume(int volume) {
        playbackStatusProxy.onVolumeChanged(volume);
        return mpcClientRepository.sendCommand(getServer(), getSetVolumeCommand(volume));
    }

    /**
     * Send a command that will set the playback to new position.
     *
     * @param position The new position in milliseconds.
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable setPosition(long position) {
        playbackStatusProxy.onPositionChanged(position);
        return mpcClientRepository.sendCommand(getServer(), getJumpToTimeCommand(position));
    }

    /**
     * Send a command to the MPC-HC server.
     *
     * @param command A command to be sent.
     * @return {@link Observable} that will emit result of the command.
     */
    public Observable miscCommand(Map<String, String> command) {
        return mpcClientRepository.sendCommand(getServer(), command);
    }

    private MpcServer getServer() {
        MpcServer server = new MpcServer();
        server.setIp(userPreferencesRepository.getIP());
        server.setPort(userPreferencesRepository.getPort());
        server.setConnectionTimeout(userPreferencesRepository.getConnectionTimeout());

        return server;
    }

    private Map<String, String> getJumpToTimeCommand(long position) {
        String posStr = getPositionString(position);

        Map<String, String> command = new HashMap<>(2);
        command.put("wm_command", "-1");
        command.put("position", posStr);

        return command;
    }

    @SuppressWarnings("DefaultLocale")
    private String getPositionString(long position) {

        return String.format("%d:%d:%d:%d",
                TimeUnit.MILLISECONDS.toHours(position),
                TimeUnit.MILLISECONDS.toMinutes(position) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position)),
                TimeUnit.MILLISECONDS.toMillis(position) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(position)
                ));
    }

    private Map<String, String> getSetVolumeCommand(int volume) {

        Map<String, String> command = new HashMap<>(2);
        command.put("wm_command", "-2");
        command.put("volume", "" + volume);

        return command;
    }


}
