package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;

import javax.inject.Inject;

/**
 * Subscribe to this {@link UseCase} to get current {@link ged.mediaplayerremote.domain.model.PlaybackStatus} and all
 * subsequent updates.
 */
public class GetPlaybackStatusUpdates extends UseCase {

    private PlaybackStatusProxy playbackStatusProxy;

    @Inject
    public GetPlaybackStatusUpdates(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                    PlaybackStatusProxy playbackStatusProxy) {
        super(threadExecutor, postExecutionThread);
        this.playbackStatusProxy = playbackStatusProxy;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return playbackStatusProxy.getPlaybackStatusUpdates();
    }
}
