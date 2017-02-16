package ged.mediaplayerremote.presentation.presenter;

import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.model.PlaybackState;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.presentation.model.PlaybackStatusModel;
import ged.mediaplayerremote.presentation.model.mapper.PlaybackStatusModelMapper;
import ged.mediaplayerremote.presentation.view.WidgetView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * {@link Presenter} that controls communication with {@link WidgetView}
 */
@Singleton
public class WidgetPresenter implements Presenter {

    private WidgetView widgetView;
    private PlaybackStatusModelMapper playbackStatusModelMapper;
    private UseCase playPauseUseCase;
    private UseCase lowerVolumeUseCase;
    private UseCase raiseVolumeUseCase;
    private UseCase fastForwardUseCase;
    private UseCase rewindUseCase;
    private UseCase getPlaybackStatusUpdatesUseCase;
    private UseCase reconnectUseCase;

    private int connectionRetryCount = 0;
    private boolean activityPaused;
    private boolean isConnecting = false;

    @Inject
    public WidgetPresenter(
            PlaybackStatusModelMapper playbackStatusModelMapper,
            @Named("playPause") UseCase playPauseUseCase,
            @Named("fastForward") UseCase fastForwardUseCase,
            @Named("rewind") UseCase rewindUseCase,
            @Named("VolumeDown") UseCase lowerVolumeUseCase,
            @Named("VolumeUp") UseCase raiseVolumeUseCase,
            @Named("playbackStatus") UseCase getPlaybackStatusUpdatesUseCase,
            @Named("reconnect") UseCase reconnectUseCase) {

        this.playbackStatusModelMapper = playbackStatusModelMapper;

        this.playPauseUseCase = playPauseUseCase;
        this.fastForwardUseCase = fastForwardUseCase;
        this.rewindUseCase = rewindUseCase;
        this.lowerVolumeUseCase = lowerVolumeUseCase;
        this.raiseVolumeUseCase = raiseVolumeUseCase;
        this.getPlaybackStatusUpdatesUseCase = getPlaybackStatusUpdatesUseCase;
        this.reconnectUseCase = reconnectUseCase;
    }

    public void initialize() {
        getPlaybackStatusUpdates();
        widgetView.showPlayButton();
    }

    @Override
    public void resume() {
        activityPaused = false;
    }

    @Override
    public void pause() {
        activityPaused = true;
    }

    @Override
    public void destroy() {
        widgetView = null;
        getPlaybackStatusUpdatesUseCase.unsubscribe();
    }

    public void setView(WidgetView widgetView) {
        this.widgetView = widgetView;
    }

    public void onMainButtonClicked() {
        playPauseUseCase.execute(new DefaultSubscriber());
    }

    public void onVolumeUpClicked() {
        raiseVolumeUseCase.execute(new DefaultSubscriber());
    }

    public void onVolumeDownClicked() {
        lowerVolumeUseCase.execute(new DefaultSubscriber());
    }

    public void onFastForwardClicked() {
        fastForwardUseCase.execute(new DefaultSubscriber());
    }

    public void onRewindClicked() {
        rewindUseCase.execute(new DefaultSubscriber());
    }

    public void onReconnectClicked() {
        reconnectUseCase.execute(new DefaultSubscriber());
        connectionRetryCount = 0;
        widgetView.showViewConnecting(connectionRetryCount);

        isConnecting = true;
        getPlaybackStatusUpdates();
    }

    private void getPlaybackStatusUpdates() {
        if (getPlaybackStatusUpdatesUseCase.isSubscribed()) {
            getPlaybackStatusUpdatesUseCase.unsubscribe();
        }
        getPlaybackStatusUpdatesUseCase.execute(new PlaybackStatusSubscriber());
    }

    private class PlaybackStatusSubscriber extends DefaultSubscriber<PlaybackStatus> {
        @Override
        public void onNext(PlaybackStatus playbackStatus) {

            PlaybackStatusModel playbackStatusModel = playbackStatusModelMapper.transform(playbackStatus);
            if (playbackStatusModel == null && isConnecting && activityPaused) {
                widgetView.showViewConnecting(connectionRetryCount);
                connectionRetryCount++;
                return;
            }
            if (playbackStatusModel != null) {
                isConnecting = false;
            }
            if (playbackStatusModel != null && playbackStatusModel.getPlaybackState() == PlaybackState.PLAYING) {
                widgetView.showPauseButton();
            } else {
                widgetView.showPlayButton();
            }

        }

        @Override
        public void onCompleted() {
            widgetView.showViewDisconnected();
        }
    }
}
