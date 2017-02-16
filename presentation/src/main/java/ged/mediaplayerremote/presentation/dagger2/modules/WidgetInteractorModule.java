package ged.mediaplayerremote.presentation.dagger2.modules;

import dagger.Module;
import dagger.Provides;
import ged.mediaplayerremote.domain.controller.CommandDispatcher;
import ged.mediaplayerremote.domain.controller.PlaybackStatusProxy;
import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import ged.mediaplayerremote.domain.interactor.GetPlaybackStatusUpdates;
import ged.mediaplayerremote.domain.interactor.Reconnect;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.interactor.command.SendFastForwardCommand;
import ged.mediaplayerremote.domain.interactor.command.SendPlayPauseCommand;
import ged.mediaplayerremote.domain.interactor.command.SendRewindCommand;
import ged.mediaplayerremote.domain.interactor.command.SendVolumeDownCommand;
import ged.mediaplayerremote.domain.interactor.command.SendVolumeUpCommand;

import javax.inject.Named;

/**
 * Dagger module that provides Use Cases for widget presenter.
 */

@Module
public class WidgetInteractorModule {

    public WidgetInteractorModule() {
    }

    @Provides
    @Named("playbackStatus")
    UseCase provideGetPlaybackStatusUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                            PlaybackStatusProxy playbackStatusProxy) {
        return new GetPlaybackStatusUpdates(threadExecutor, postExecutionThread, playbackStatusProxy);
    }

    @Provides
    @Named("playPause")
    UseCase providePlayPauseUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                    CommandDispatcher commandDispatcher) {
        return new SendPlayPauseCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("rewind")
    UseCase provideRewindUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                 CommandDispatcher commandDispatcher) {
        return new SendRewindCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("fastForward")
    UseCase provideFastForwardUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                      CommandDispatcher commandDispatcher) {
        return new SendFastForwardCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("VolumeDown")
    UseCase provideVolumeDownUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                     CommandDispatcher commandDispatcher) {
        return new SendVolumeDownCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("VolumeUp")
    UseCase provideVolumeUpUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                   CommandDispatcher commandDispatcher) {
        return new SendVolumeUpCommand(threadExecutor, postExecutionThread, commandDispatcher);
    }

    @Provides
    @Named("reconnect")
    UseCase provideReconnectUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                    PlaybackStatusProxy playbackStatusProxy) {
        return new Reconnect(threadExecutor, postExecutionThread, playbackStatusProxy);
    }
}
