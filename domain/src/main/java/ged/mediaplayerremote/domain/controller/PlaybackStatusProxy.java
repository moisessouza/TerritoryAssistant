package ged.mediaplayerremote.domain.controller;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.model.MpcServer;
import ged.mediaplayerremote.domain.model.PlaybackState;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.repository.MpcClientRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * A class that continuously requests current {@link PlaybackStatus} from MPC-HC server, broadcasts it to other
 * components and rebroadcasts all changes to status made locally by one of components to the rest of them.
 */
@Singleton
public class PlaybackStatusProxy implements ServerSettingsChangedListener{

    /**
     * Interface for an Application class to report whether it is running in foreground or background.
     */
    public interface ApplicationStatus {
        boolean isAnyActivityVisible();
    }

    private MpcClientRepository mpcClientRepository;
    private UserPreferencesRepository userPreferencesRepository;
    private BehaviorSubject<PlaybackStatus> playbackStatusPublisher;
    private ThreadExecutor threadExecutor;
    private PostExecutionThread postExecutionThread;
    private ApplicationStatus applicationStatus;
    private PlaybackStatus mPlaybackStatus;
    private boolean localVolumeSynchronized = true;
    private boolean localPositionSynchronized = true;
    private boolean localPlaybackStateSynchronized = true;
    private Subscription playbackStatusSubscription;

    @Inject
    public PlaybackStatusProxy(MpcClientRepository mpcClientRepository,
                               UserPreferencesRepository userPreferencesRepository,
                               ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                               ApplicationStatus applicationStatus) {
        this.mpcClientRepository = mpcClientRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.applicationStatus = applicationStatus;
    }

    /**
     * A method to be called every time a volume change command is issued.
     */
    public void onVolumeChanged(int newVolume) {
        if (mPlaybackStatus != null) mPlaybackStatus.setVolume(newVolume);
        localVolumeSynchronized = false;
        playbackStatusPublisher.onNext(mPlaybackStatus);
    }

    /**
     * A method to be called every time a playback position change command is issued.
     */
    public void onPositionChanged(long newPosition) {
        if (mPlaybackStatus != null) {
            mPlaybackStatus.setPosition(newPosition);
            mPlaybackStatus.setPositionString(positionIntToString((int) newPosition));
        }

        localPositionSynchronized = false;
        playbackStatusPublisher.onNext(mPlaybackStatus);
    }

    /**
     * A method to be called every time playback state change command is issued.
     */
    public void onPlaybackStateChanged(PlaybackState playbackState) {
        if (mPlaybackStatus != null)
            mPlaybackStatus.setPlaybackState(playbackState);
        localPlaybackStateSynchronized = false;
        playbackStatusPublisher.onNext(mPlaybackStatus);
    }

    /**
     * Start downloading current {@link PlaybackStatus} from server every second.
     */
    public void connectToServer() {
        playbackStatusPublisher = BehaviorSubject.create();

        String ip = userPreferencesRepository.getIP();
        if (ip.equals("127.0.0.1")) {
            return;
        }
        MpcServer mpcServer = new MpcServer();
        mpcServer.setIp(ip);
        mpcServer.setPort(userPreferencesRepository.getPort());
        mpcServer.setConnectionTimeout(userPreferencesRepository.getConnectionTimeout());


        playbackStatusSubscription = mpcClientRepository.getPlaybackStatusUpdates(mpcServer)
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(new PlaybackStatusSubscriber());
    }

    @Override
    public void onServerSettingsChanged() {
        if (playbackStatusSubscription != null && !playbackStatusSubscription.isUnsubscribed()) {
            playbackStatusSubscription.unsubscribe();
        }
        connectToServer();
    }

    /**
     * Get current PlaybackStatus
     */
    public PlaybackStatus getPlaybackStatus() {
        return mPlaybackStatus;
    }

    /**
     * Get an {@link Observable} that will emit current {@link PlaybackStatus} and all subsequent updates.
     */
    public Observable<PlaybackStatus> getPlaybackStatusUpdates() {
        return this.playbackStatusPublisher;
    }

    /**
     * Check if the app is currently connected to MPC-HC server, and downloading {@link PlaybackStatus} updates.
     */
    public boolean isDisconnected() {
        return playbackStatusSubscription == null || playbackStatusSubscription.isUnsubscribed();
    }

    private void disconnect() {
        playbackStatusSubscription.unsubscribe();
        playbackStatusPublisher.onCompleted();
    }

    private String positionIntToString(int position) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(position),
                TimeUnit.MILLISECONDS.toMinutes(position) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position)));
    }



    private class PlaybackStatusSubscriber extends DefaultSubscriber<PlaybackStatus> {

        int nullStatusCounter = 0;
        int allActivitiesPausedCounter = 0;

        @Override
        public void onNext(PlaybackStatus playbackStatus) {
            if (playbackStatus == null) {
                nullStatusCounter++;
                playbackStatusPublisher.onNext(null);
            } else {
                nullStatusCounter = 0;
            }
            if (playbackStatus != null) {
                synchroniseWithLocalChanges(playbackStatus);
                PlaybackStatusProxy.this.mPlaybackStatus = playbackStatus;
                playbackStatusPublisher.onNext(playbackStatus);
            }
            checkApplicationStatus();
            if (shouldDisconnect())
                disconnect();
        }

        private boolean shouldDisconnect() {
            boolean widgetDisabled = !userPreferencesRepository.showWidget();
            if (allActivitiesPausedCounter > 5 && widgetDisabled) {
                return true;
            }
            if (!widgetDisabled && allActivitiesPausedCounter > 5 && nullStatusCounter > 10) {
                return true;
            } else {
                return false;
            }
        }

        private void checkApplicationStatus() {
            if (applicationStatus.isAnyActivityVisible()) {
                allActivitiesPausedCounter = 0;
            } else {
                allActivitiesPausedCounter++;
            }
        }

        private void synchroniseWithLocalChanges(PlaybackStatus remotePlaybackStatus) {
            if (!localVolumeSynchronized) {
                remotePlaybackStatus.setVolume(PlaybackStatusProxy.this.mPlaybackStatus.getVolume());
                localVolumeSynchronized = true;
            }
            if (!localPositionSynchronized) {
                remotePlaybackStatus.setPosition(PlaybackStatusProxy.this.mPlaybackStatus.getPosition());
                localPositionSynchronized = true;
            }
            if (!localPlaybackStateSynchronized) {
                remotePlaybackStatus.setPlaybackState(PlaybackStatusProxy.this.mPlaybackStatus.getPlaybackState());
                localPlaybackStateSynchronized = true;
            }
        }
    }
}
