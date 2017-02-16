package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;

import javax.inject.Inject;

/**
 * When issued from Notification, this {@link UseCase} will attempt to reconnect to server, after connection has been
 * lost while app was in background.
 */
public class Reconnect extends UseCase {

    private PlaybackStatusProxy playbackStatusProxy;

    @Inject
    public Reconnect(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                     PlaybackStatusProxy playbackStatusProxy) {
        super(threadExecutor, postExecutionThread);
        this.playbackStatusProxy = playbackStatusProxy;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        playbackStatusProxy.connectToServer();
        return Observable.empty();
    }
}
